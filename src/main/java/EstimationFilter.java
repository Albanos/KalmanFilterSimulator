import org.apache.commons.math3.filter.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.sql.Timestamp;
import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 14.11.2018.
 */
public class EstimationFilter {
    private static EstimationFilter instance;

    private KalmanFilter filter;

    //final double dt = Service.getDt();
    final double dt = Service.getListOfAllImuValues().getFirst().getDt();

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
    private static long timestamp2;
    private static long timestamp = timestamp2= System.currentTimeMillis();
    private static LinkedList<CartesianPoint> copyListOfAllCartesianPoints = new LinkedList<>();

    public EstimationFilter() {
        // Kopiere alle kartesischen Punkte, damit diese für Schleife gelöscht werden können
        copyListOfAllCartesianPoints.addAll(Service.getListOfAllCartesianPoints());

        CartesianPoint firstCartesianPoint = Service.getListOfAllCartesianPoints().getFirst();
        float locationAccurancy = (float) firstCartesianPoint.getAccuracy_gnss();

        // Standardabweichung der Beschleunigung (statisch festgelegt), für Prozessrauschen
        final float sigmaAccel = 8f;

        double coordinate_x = firstCartesianPoint.getX();
        double coordinate_y = firstCartesianPoint.getY();

        ImuValues firstImuValue = Service.getListOfAllImuValues().getFirst();
        double speed_x = firstImuValue.getSpeed_x_wgs();
        double speed_y = firstImuValue.getSpeed_y_wgs();
        double accel_x = firstImuValue.getAccel_x_wgs();
        double accel_y = firstImuValue.getAccel_y_wgs();

        x = new ArrayRealVector(new double[]{coordinate_x, coordinate_y, speed_x, speed_y});
        u = new ArrayRealVector(new double[]{accel_x, accel_y});
        A = new Array2DRowRealMatrix(new double[][]{
                {1, 0, dt, 0},
                {0, 1, 0, dt},
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
                {dt, 0},
                {0, dt}
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
                {1 / 4 * Math.pow(dt, 4), 1 / 4 * Math.pow(dt, 4), 1 / 2 * Math.pow(dt, 3), 1 / 2 * Math.pow(dt, 3)},
                {1 / 4 * Math.pow(dt, 4), 1 / 4 * Math.pow(dt, 4), 1 / 2 * Math.pow(dt, 3), 1 / 2 * Math.pow(dt, 3)},
                {1 / 2 * Math.pow(dt, 3), 1 / 2 * Math.pow(dt, 3), Math.pow(dt, 2) * Math.pow(sigmaAccel, 2), Math.pow(dt, 2)},
                {1 / 2 * Math.pow(dt, 3), 1 / 2 * Math.pow(dt, 3), Math.pow(dt, 2), Math.pow(dt, 2) * Math.pow(sigmaAccel, 2)}
        });

        double locationVarianz = Math.pow(locationAccurancy, 2);
        double speedVarianz = Math.pow(0.5, 2); // speedVarianz wird statisch festgelegt, da Geschw.-Genauigkeit nicht verfügbar
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

//        z = new ArrayRealVector(new double[]{Service.getListOfPoints().getLast().getX(),
//                Service.getListOfPoints().getLast().getY(),
//                Service.getSpeed_x_wgs(), Service.getSpeed_y_wgs()});

        //currentMeasurment = z;

        pm = new DefaultProcessModel(A, B, Q, x, P);
        mm = new DefaultMeasurementModel(H, R);
        filter = new KalmanFilter(pm, mm);

    }

    public void makeEstimation() {
        int i = 0;
        int j = 0;
        while (!copyListOfAllCartesianPoints.isEmpty()) {
            // LAZY-WAITING:
            // Prüfe, ob TIME_TO_SLEEP vergangen ist oder nicht. Wenn nicht, springe zur nächsten Iteration
            if ((System.currentTimeMillis() - timestamp) < TIME_TO_SLEEP) {
                continue;
            }
            timestamp = System.currentTimeMillis();

            System.out.println("Anzahl Punkte in Kopie:  " + copyListOfAllCartesianPoints.size());


            // Aktualisiere u
            u.setEntry(0, Service.getListOfAllImuValues().get(i).getAccel_x_wgs());
            u.setEntry(1, Service.getListOfAllImuValues().get(i).getAccel_y_wgs());

            filter.predict(u);

            // Prüfe ob 1s vergangen ist. Wenn ja, hole nächste bekannte Position und den Speed
            if ((System.currentTimeMillis() - timestamp2) >= 1000) {
                timestamp2 = System.currentTimeMillis();

                //System.out.println("Korrektur, zur Zeit:  " + new Timestamp(System.currentTimeMillis()));
                System.out.println("===================================================================================DRIN!!!, Zeit:  " + new Timestamp(System.currentTimeMillis()));
                currentMeasurment = new ArrayRealVector(new double[]{
                        copyListOfAllCartesianPoints.get(j).getX(),
                        copyListOfAllCartesianPoints.get(j).getY(),
                        Service.getListOfAllImuValues().get(i).getSpeed_x_wgs(),
                        Service.getListOfAllImuValues().get(i).getSpeed_y_wgs()
                });

                filter.correct(currentMeasurment);

                // Entferne die soeben genutzte Position damit Schleife irgendwann terminiert
                copyListOfAllCartesianPoints.remove(j);
            }

            double estimatedPosition_x = filter.getStateEstimation()[0];
            double estimatedPosition_y = filter.getStateEstimation()[1];

            System.out.println("Geschätzter Punkt:  " + estimatedPosition_x + " ; " + estimatedPosition_y + " Zur Zeit (jetzt):  " + new Timestamp(System.currentTimeMillis()));
            System.out.println("Echter Punkt:  " + Service.getListOfAllCartesianPoints().get(j).getX() + " ; " + Service.getListOfAllCartesianPoints().get(j).getY() + "\n");

            CartesianPoint estimatedPoint = new CartesianPoint(estimatedPosition_x, estimatedPosition_y);
            estimatedPoint.setTimestamp(Long.toString(System.currentTimeMillis()));
            Service.getListOfAllEstimatedCartesianPoints().add(estimatedPoint);

            // Rechne die geschätzten Punkte wieder in das WGS-System um
            Service.calculateAngleAndDistanceByPoint(estimatedPoint);
            Service.calculateWGSCoordinateByCartesianPoint(estimatedPoint);

            // Berechne den Abstand zwischen geschätztem Punkt und GT-Punkt
            Service.calculateDistanceBetweenEstimatedAndGTPosition(estimatedPoint, i);

            // Erhöhe counter
            i = i + 1;
        }
    }
}
