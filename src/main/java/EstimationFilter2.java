import org.apache.commons.math3.filter.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 12/1/2018.
 */
public class EstimationFilter2 {
    private final Constants constants = Constants.getInstance();
    private final Service2 service = Service2.getInstance();
    private final CsvReader csvReader = CsvReader.getInstance();

    private KalmanFilter filter;
    final double dt = 0.1;
    //final double dt = service.getDt();
    //final double dt = 0.057312011;
    //final double dt = 0.06;
    //final double dt = 0.020894866;
    //final double dt = 0.02;

    // Vektoren und Matrizen (nach Notation von Apache Math):
    // x: Zustandsvektor
    // u: Eingabevektor
    // A: Transitionsmatrix
    // B: Eingabematrix
    // Q: Prozessrauschkovarianz
    // R: Messrauschkovarianz
    // P: Kovarianz
    // H: Messmatrix
    // z: Messvektor
    private RealVector x;
    private RealVector u;
    private RealMatrix A;
    private RealMatrix B;
    private RealMatrix Q;
    private RealMatrix G;
    private RealMatrix R;
    private RealMatrix R_temp;
    private RealMatrix P;
    private RealMatrix H;
    private RealVector z;
    private RealVector currentMeasurment;

    private ProcessModel pm;
    private MeasurementModel mm;

    private LinkedList<Data> copyListOfAllData = new LinkedList<>();
    private static long timestamp2;
    private static long timestamp;

    public EstimationFilter2() {
        // Kopiere alle Daten, damit diese für Schleife gelöscht werden können
        copyListOfAllData.addAll(service.getListOfAllData());

        // Wir nutzen kein downsampling mehr, frquenz der daten ist etwa 200 Hz,
        // deshalb erster kartesischer Punkt erst hier vorhanden. Wir wollen nicht mit (0/0) initialisieren
        Data firstDataPoint = copyListOfAllData.getFirst();
//        Data firstDataPoint = copyListOfAllData.stream()
//                .filter(d -> d.getCartesian_x() != 0.0)
//                .findFirst().orElse(new Data());
        timestamp = timestamp2 = firstDataPoint.getTimestamp();

        float locationAccurancy = (float) firstDataPoint.getAccuracy_gnss();

        // Standardabweichung der Beschleunigung (statisch festgelegt), für Prozessrauschen
        final float sigmaAccel = constants.getSigmaAccel();

        double coordinate_x = firstDataPoint.getCartesian_x_gt();
        double coordinate_y = firstDataPoint.getCartesian_y_gt();
        double speed_x = firstDataPoint.getSpeed_x_wgs();
        double speed_y = firstDataPoint.getSpeed_y_wgs();

        double accel_x = firstDataPoint.getAccel_x_wgs();
        double accel_y = firstDataPoint.getAccel_y_wgs();

        x = new ArrayRealVector(new double[]{coordinate_x, coordinate_y, speed_x, speed_y});
        u = new ArrayRealVector(new double[]{accel_x, accel_y});
        A = new Array2DRowRealMatrix(new double[][]{
                {1, 0, this.dt, 0},
                {0, 1, 0, this.dt},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });

//        B = new Array2DRowRealMatrix(new double[][]{
//                {(Math.pow(dt, 2) / 2), 0},
//                {0, (Math.pow(dt, 2) / 2)},
//                {dt, 0},
//                {0, dt}
//        });
        B = new Array2DRowRealMatrix(new double[][]{
                {0, 0},
                {0, 0},
                {this.dt, 0},
                {0, this.dt}
        });

        H = new Array2DRowRealMatrix(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });

        // Standardabweichung für Beschleunigung ist statisch 1, deshalb ignoriert
        // Standardabweichung soll bei zusätzlicher Messung von Geschwindigkeit mitberücksichtigt,
        // deshalb wird sigma nachobengesetzt
        G = new Array2DRowRealMatrix(new double[]{
                constants.getG1() * Math.pow(this.dt, 2),
                constants.getG2() * Math.pow(this.dt, 2),
                constants.getG3() * this.dt,
                constants.getG4() * this.dt});
        RealMatrix Q2 = G.multiply(G.transpose());
        Q2.setEntry(2, 2, Math.pow(this.dt, 2) * Math.pow(sigmaAccel, 2));
        Q2.setEntry(3, 3, Math.pow(this.dt, 2) * Math.pow(sigmaAccel, 2));

        // Bemerkung: Die Abweichung zwischen Q und Q2 ergeben sich in der fünfzehnten nachkommastelle
        Q = new Array2DRowRealMatrix(new double[][]{
                {0.25 * Math.pow(this.dt, 4), 0.25 * Math.pow(this.dt, 4), 0.5 * Math.pow(this.dt, 3), 0.5 * Math.pow(this.dt, 3)},
                {0.25 * Math.pow(this.dt, 4), 0.25 * Math.pow(this.dt, 4), 0.5 * Math.pow(this.dt, 3), 0.5 * Math.pow(this.dt, 3)},
                {0.5 * Math.pow(this.dt, 3), 0.5 * Math.pow(this.dt, 3), Math.pow(this.dt, 2) * Math.pow(sigmaAccel, 2), Math.pow(this.dt, 2)},
                {0.5 * Math.pow(this.dt, 3), 0.5 * Math.pow(this.dt, 3), Math.pow(this.dt, 2), Math.pow(this.dt, 2) * Math.pow(sigmaAccel, 2)}
        });

        Q = Q2;

        double locationVarianz = Math.pow(constants.getSIGMA_POSITION_ACC(), 2);
        double speedVarianz = Math.pow(constants.getSigmaGnssSpeed(), 2); // speedVarianz wird statisch festgelegt, da Geschw.-Genauigkeit nicht verfügbar
        R = new Array2DRowRealMatrix(new double[][]{
                {locationVarianz, 0, 0, 0},
                {0, locationVarianz, 0, 0},
                {0, 0, speedVarianz, 0},
                {0, 0, 0, speedVarianz}
        });
        R_temp = R.copy();

        P = new Array2DRowRealMatrix(new double[][]{
                {10, 0, 0, 0},
                {0, 10, 0, 0},
                {0, 0, 10, 0},
                {0, 0, 0, 10}
        });

        // Lösche nun den ersten Punkt aus der Listen-Kopie,
        // damit dieser Datensatz nicht als Messung zum Einsatz kommt
        copyListOfAllData.remove(firstDataPoint);

        pm = new DefaultProcessModel(A, B, Q, x, P);
        mm = new DefaultMeasurementModel(H, R);
        filter = new KalmanFilter(pm, mm);
    }

    public void makeEstimation(boolean withGtAsMeasurement, boolean withVelocityFromStepDetection) {
        int iterationCounter = 0;
        z = new ArrayRealVector(new double[] {0,0,0,0}); // initialisiere z mit nullen
        currentMeasurment = new ArrayRealVector(new double[] {0,0,0,0});
        for(Data d : copyListOfAllData) {
            long currentTimestamp = d.getTimestamp();
            double currentAccelXWgs = d.getAccel_x_wgs();
            double currentAccelYWgs = d.getAccel_y_wgs();

            // Wir nutzen eine Frquenz von 10 Hz
            if((currentTimestamp - timestamp) < 100) {
                continue;
            }
            timestamp = currentTimestamp;

            // Aktualisiere Vektor u
            u.setEntry(0, currentAccelXWgs);
            u.setEntry(1, currentAccelYWgs);

            // mache Vorhersageschritt
            //filter.predict(u);
            filter.predict();

            // Prüfe ob 1s vergangen ist. Wenn ja: Werte kartesische
            // Position aus
            if((currentTimestamp - timestamp2) >= 1000) {
                // Für den Fall der "normalen" Messung muss location existieren
                if(!Double.isNaN(d.getLatitude_wgs()) && !Double.isNaN(d.getLongitude_wgs())) {
                    timestamp2 = currentTimestamp;

                    System.out.println("Aktuelle Position, Zeit:  " + currentTimestamp);
                    System.out.println("Aktueller kartesischer Punkt:  " + d.getCartesian_x() + " ; " + d.getCartesian_y() + "\n");
                    // Extrahiere aus aktuellem Datensatz Position und jew. speed
                    currentMeasurment = new ArrayRealVector(new double[]{
                            d.getCartesian_x(),
                            d.getCartesian_y(),
                            d.getSpeed_x_wgs(),
                            d.getSpeed_y_wgs()
                    });
                    if(withVelocityFromStepDetection) {
                        currentMeasurment = new ArrayRealVector(new double[]{
                                d.getCartesian_x(),
                                d.getCartesian_y(),
                                d.getSpeed_x_stepDetector(),
                                d.getSpeed_y_stepDetector()
                        });
                    }

//                    // Vergleiche currentMesasurement mit z. Wenn ungleich
//                    // liegt eine neue Messung vor, die wir auswerten
//                    if (!currentMeasurment.equals(z)) {
//                        filter.correct(currentMeasurment);
//                        z = currentMeasurment;
//                    }
                }

                // Für den Fall der GT-Auswertung (wenn gewünscht) ist keine Location erforderlich, denn:
                // wir nutzen ja GT
                // Nutze GT-Position auf halber Strecke, wenn gewünscht
                if (withGtAsMeasurement) {
                    iterationCounter++;
                    String key = String.join("_", constants.getCurrentSegment());
                    switch (key) {
                        //case "12078_12700":
                        // MERKE: neue Markierungsnummer im Messdurchlauf mit Step-erfassung
                        case "12079_12700":
                            if (iterationCounter == csvReader.getGnssCounterBySegments().get(key) / 2) {
                                currentMeasurment = new ArrayRealVector(new double[]{
                                        d.getCartesian_x_gt(),
                                        d.getCartesian_y_gt(),
                                        d.getSpeed_x_wgs(),
                                        d.getSpeed_y_wgs()
                                });
                                if(withVelocityFromStepDetection) {
                                    currentMeasurment = new ArrayRealVector(new double[]{
                                            d.getCartesian_x_gt(),
                                            d.getCartesian_y_gt(),
                                            d.getSpeed_x_stepDetector(),
                                            d.getSpeed_y_stepDetector()
                                    });
                                }
                                System.out.println("====================GT-Position für SegmentA genutzt!");
                                System.out.println("Betrag der GNSS-Geschwindigkeit:  " + d.getAmountSpeed_wgs());
                                // Speichere aktuellen Wert von Matrix R temporär und setze kleineres Positions-Messrauschen
//                                saveCurrentMatrixRAndSetLowerPositionsVarianz();
                            }
                            System.out.println("Iteration:  " + iterationCounter);
                            break;
                        case "12700_First_12694":
                            if (iterationCounter == csvReader.getGnssCounterBySegments().get(key) / 2) {
                                currentMeasurment = new ArrayRealVector(new double[]{
                                        d.getCartesian_x_gt(),
                                        d.getCartesian_y_gt(),
                                        d.getSpeed_x_wgs(),
                                        d.getSpeed_y_wgs()
                                });
                                if(withVelocityFromStepDetection) {
                                    currentMeasurment = new ArrayRealVector(new double[]{
                                            d.getCartesian_x_gt(),
                                            d.getCartesian_y_gt(),
                                            d.getSpeed_x_stepDetector(),
                                            d.getSpeed_y_stepDetector()
                                    });
                                }
                                System.out.println("====================GT-Position für SegmentB genutzt!");
                                System.out.println("Betrag der GNSS-Geschwindigkeit:  " + d.getAmountSpeed_wgs());
                                // Speichere aktuellen Wert von Matrix R temporär und setze kleineres Positions-Messrauschen
//                                saveCurrentMatrixRAndSetLowerPositionsVarianz();
                            }
                            System.out.println("Iteration:  " + iterationCounter);
                            break;

                        case "12694_12700":
                            if (iterationCounter == csvReader.getGnssCounterBySegments().get(key) / 2) {
                                currentMeasurment = new ArrayRealVector(new double[]{
                                        d.getCartesian_x_gt(),
                                        d.getCartesian_y_gt(),
                                        d.getSpeed_x_wgs(),
                                        d.getSpeed_y_wgs()
                                });
                                if(withVelocityFromStepDetection) {
                                    currentMeasurment = new ArrayRealVector(new double[]{
                                            d.getCartesian_x_gt(),
                                            d.getCartesian_y_gt(),
                                            d.getSpeed_x_stepDetector(),
                                            d.getSpeed_y_stepDetector()
                                    });
                                }
                                System.out.println("====================GT-Position für SegmentC genutzt!");
                                System.out.println("Betrag der GNSS-Geschwindigkeit:  " + d.getAmountSpeed_wgs());
                                // Speichere aktuellen Wert von Matrix R temporär und setze kleineres Positions-Messrauschen
//                                saveCurrentMatrixRAndSetLowerPositionsVarianz();
                            }
                            System.out.println("Iteration:  " + iterationCounter);
                            break;

                        //case "12700_Second_12078":
                        // MERKE: neue Markierungsnummer im Messdurchlauf mit Step-erfassung
                        case "12700_Second_12079":
                            if (iterationCounter == csvReader.getGnssCounterBySegments().get(key) / 2) {
                                currentMeasurment = new ArrayRealVector(new double[]{
                                        d.getCartesian_x_gt(),
                                        d.getCartesian_y_gt(),
                                        d.getSpeed_x_wgs(),
                                        d.getSpeed_y_wgs()
                                });
                                if(withVelocityFromStepDetection) {
                                    currentMeasurment = new ArrayRealVector(new double[]{
                                            d.getCartesian_x_gt(),
                                            d.getCartesian_y_gt(),
                                            d.getSpeed_x_stepDetector(),
                                            d.getSpeed_y_stepDetector()
                                    });
                                }
                                System.out.println("====================GT-Position für SegmentD genutzt!");
                                System.out.println("Betrag der GNSS-Geschwindigkeit:  " + d.getAmountSpeed_wgs());
                                // Speichere aktuellen Wert von Matrix R temporär und setze kleineres Positions-Messrauschen
//                                saveCurrentMatrixRAndSetLowerPositionsVarianz();
                            }
                            System.out.println("Iteration:  " + iterationCounter);
                            break;
                    }
                }

                // Vergleiche currentMesasurement mit z. Wenn ungleich
                // liegt eine neue Messung vor, die wir auswerten
                if (!currentMeasurment.equals(z)) {
                    String key = String.join("_", constants.getCurrentSegment());
                    // Prüfe für Segment A und D ob die Messungen z und currentMeasure dem Betrage nach in Y-ACHSE größer sind
                    if(key.equals("12079_12700") || key.equals("12700_Second_12079")) {
                        if(Math.abs(currentMeasurment.getEntry(1)) > Math.abs(z.getEntry(1))) {
                            filter.correct(currentMeasurment);
                            z = currentMeasurment;
                        }
                    }
                    // Prüfe für Segment B und C ob die Messungen z und currentMeasure dem Betrage nach in X-ACHSE größer sind
                    else {
                        if(Math.abs(currentMeasurment.getEntry(0)) > Math.abs(z.getEntry(0))) {
                            filter.correct(currentMeasurment);
                            z = currentMeasurment;
                        }
                    }
//                    if(currentMeasurment.getEntry(3) == 0.0) {
//                        System.out.println("Geschwindigkeit ist NULL!!!");
//                    }
//                    filter.correct(currentMeasurment);
//                    z = currentMeasurment;
//                    R = R_temp.copy();
                }
            }

            double estX = filter.getStateEstimation()[0];
            double estY = filter.getStateEstimation()[1];

            System.out.println("Aktuell geschätzter Punkt:  " + estX + " ; " + estY + "\n");

            // setze den geschätzten kartesischen Punkt in Datensatz
            d.setEstimatedPoint_x(estX);
            d.setEstimatedPoint_y(estY);

            // Rechne geschätzte Kartesische Punkte in WGS-Format um:
            // erst Abstand und Winkel zum ersten kartesischen Punkt, dann die WGS-Koordinaten
            service.calculateAngleAndDistanceAndWgsPositionByDataPoint(d);

            // Berechne auch die longitudinale und laterale Distanz zur GT-Position
            service.calculateDistanceBetweenEstimatedAndGTPosition(d);

            // Berechne ausserdem den absoluten Abstand zwischen Est <--> GT (& zwischen GNSS <--> GT)
            service.calculateAbsoluteDistanceBetweenEstAndGtPoint(d);
        }
        System.out.println("=======================Schätzungen abgeschlossen\n");
    }

    private void saveCurrentMatrixRAndSetLowerPositionsVarianz() {
        R_temp = R.copy();
        R = new Array2DRowRealMatrix(new double[][]{
                {0.1, 0, 0, 0},
                {0, 0.1, 0, 0},
                {0, 0, 9, 0},
                {0, 0, 0, 9}
        });
    }
}
