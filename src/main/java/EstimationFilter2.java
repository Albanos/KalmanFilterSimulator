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
    private KalmanFilter filter;
    final double dt = 0.1;
    //final double dt = Service2.getDt();
    //final double dt = 0.057312011;
    //final double dt = 0.06;
    //final double dt = 0.020894866;
    //final double dt = 0.02;

    // Statische Variablen, von der doku-page
    // position measurement noise (meter)
    double measurementNoise = 10d;
    // acceleration noise (meter/sec^2)
    double accelNoise = 0.2d;

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
    private RealMatrix R;
    private RealMatrix P;
    private RealMatrix H;
    private RealVector z;
    private RealVector currentMeasurment;

    private ProcessModel pm;
    private MeasurementModel mm;

    // Variable zum steuern der Erzeugung von Punkten, pro sekunde (in ms).
    // Bsp.: TIME_TO_SLEEP=100 --> 10 Punkte/sek --> 10 Hz
    private static final int TIME_TO_SLEEP = 100;

    private LinkedList<Data> copyListOfAllData = new LinkedList<>();
    private static long timestamp2;
    private static long timestamp;

    public EstimationFilter2() {
        //timestamp = timestamp2 = Service2.getListOfAllData().getFirst().getTimestamp();

        // Kopiere alle Daten, damit diese für Schleife gelöscht werden können
        copyListOfAllData.addAll(Service2.getListOfAllData());

        // Wir nutzen kein downsampling mehr, frquenz der daten ist etwa 200 Hz,
        // deshalb erster kartesischer Punkt erst hier vorhanden. Wir wollen nicht mit (0/0) initialisieren
        Data firstDataPoint = copyListOfAllData.get(Constants.getPositionOfFirstPointWithCartesianCoordinates());
        timestamp = timestamp2 = firstDataPoint.getTimestamp();

        float locationAccurancy = (float) firstDataPoint.getAccuracy_gnss();

        // Standardabweichung der Beschleunigung (statisch festgelegt), für Prozessrauschen
        final float sigmaAccel = Constants.getSigmaAccel();

        double coordinate_x = firstDataPoint.getCartesian_x();
        double coordinate_y = firstDataPoint.getCartesian_y();
        double speed_x = firstDataPoint.getSpeed_x_wgs();
        double speed_y = firstDataPoint.getSpeed_y_wgs();
        // Wir löschen die init-Position, damit sie nicht als Messung genutzt wird
        //copyListOfAllData.remove(firstDataPoint);

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
        Q = new Array2DRowRealMatrix(new double[][]{
                {1 / 4 * Math.pow(this.dt, 4), 1 / 4 * Math.pow(this.dt, 4), 1 / 2 * Math.pow(this.dt, 3), 1 / 2 * Math.pow(this.dt, 3)},
                {1 / 4 * Math.pow(this.dt, 4), 1 / 4 * Math.pow(this.dt, 4), 1 / 2 * Math.pow(this.dt, 3), 1 / 2 * Math.pow(this.dt, 3)},
                {1 / 2 * Math.pow(this.dt, 3), 1 / 2 * Math.pow(this.dt, 3), Math.pow(this.dt, 2) * Math.pow(sigmaAccel, 2), Math.pow(this.dt, 2)},
                {1 / 2 * Math.pow(this.dt, 3), 1 / 2 * Math.pow(this.dt, 3), Math.pow(this.dt, 2), Math.pow(this.dt, 2) * Math.pow(sigmaAccel, 2)}
        });

        double locationVarianz = Math.pow(locationAccurancy, 2);
        double speedVarianz = Math.pow(Constants.getSigmaGnssSpeed(), 2); // speedVarianz wird statisch festgelegt, da Geschw.-Genauigkeit nicht verfügbar
        R = new Array2DRowRealMatrix(new double[][]{
                {locationVarianz, 0, 0, 0},
                {0, locationVarianz, 0, 0},
                {0, 0, speedVarianz, 0},
                {0, 0, 0, speedVarianz}
        });


        P = new Array2DRowRealMatrix(new double[][]{
                {10, 0, 0, 0},
                {0, 10, 0, 0},
                {0, 0, 10, 0},
                {0, 0, 0, 10}
        });

        z = new ArrayRealVector(new double[]{
                firstDataPoint.getCartesian_x(),
                firstDataPoint.getCartesian_y(),
                firstDataPoint.getSpeed_x_wgs(),
                firstDataPoint.getSpeed_y_wgs()
        });

        currentMeasurment = z;

        // Lösche nun den ersten Punkt aus der Listen-Kopie,
        // damit dieser Datensatz nicht als Messung zum Einsatz kommt
        copyListOfAllData.remove(firstDataPoint);

        pm = new DefaultProcessModel(A, B, Q, x, P);
        mm = new DefaultMeasurementModel(H, R);
        filter = new KalmanFilter(pm, mm);
    }

    public void makeEstimation() {
        for(Data d : copyListOfAllData) {
            long currentTimestamp = d.getTimestamp();
            double currentAccelXWgs = d.getAccel_x_wgs();
            double currentAccelYWgs = d.getAccel_y_wgs();

            // Wir nutzen eine Frquenz von 10 Hz
            if((currentTimestamp - timestamp) < 100) {
                continue;
            }
            timestamp = currentTimestamp;

            double cartesian_x = d.getCartesian_x();
            double cartesian_y = d.getCartesian_y();

            // Aktualisiere Vektor u
            u.setEntry(0, currentAccelXWgs);
            u.setEntry(1, currentAccelYWgs);

            // mache Vorhersageschritt
            filter.predict(u);

            // Prüfe ob 1s vergangen ist. Wenn ja: Werte kartesische
            // Position aus
            if((currentTimestamp - timestamp2) >= 1000) {
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

                // Vergleiche currentMesasurement mit z. Wenn ungleich
                // liegt eine neue Messung vor, die wir auswerten
                if( !currentMeasurment.equals(z) ) {
                    filter.correct(currentMeasurment);
                    z = currentMeasurment;
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
            Service2.calculateAngleAndDistanceAndWgsPositionByDataPoint(d);

            // Berechne auch die longitudinale und laterale Distanz zur GT-Position
            Service2.calculateDistanceBetweenEstimatedAndGTPosition(d);

            // Berechne ausserdem den absoluten Abstand zwischen Est <--> GT (& zwischen GNSS <--> GT)
            Service2.calculateAbsoluteDistanceBetweenEstAndGtPoint(d);
        }
        System.out.println("=======================Schätzungen abgeschlossen\n");
    }
}
