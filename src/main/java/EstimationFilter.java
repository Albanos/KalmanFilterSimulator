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

    //final double dt = 0.1;
    //final double dt = 0.057312012;
    final double dt = Service.getDt();
    //final double dt = 0.005;
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
//    private static long timestamp2;
//    private static long timestamp = timestamp2 = System.currentTimeMillis();

    private static LinkedList<CartesianPoint> copyListOfAllCartesianPoints = new LinkedList<>();
    private static long timestamp2;
    private static long timestamp = timestamp2 = Service.getResampledListOfAllImuValues().getFirst().getTimestamp();

    public EstimationFilter() {
        // Kopiere alle kartesischen Punkte, damit diese für Schleife gelöscht werden können
        copyListOfAllCartesianPoints.addAll(Service.getListOfAllCartesianPoints());

        CartesianPoint firstCartesianPoint = Service.getListOfAllCartesianPoints().getFirst();
        float locationAccurancy = (float) firstCartesianPoint.getAccuracy_gnss();

        // Standardabweichung der Beschleunigung (statisch festgelegt), für Prozessrauschen
        final float sigmaAccel = 8f;

        double coordinate_x = firstCartesianPoint.getX();
        double coordinate_y = firstCartesianPoint.getY();
        double speed_x = firstCartesianPoint.getSpeed_x_wgs();
        double speed_y = firstCartesianPoint.getSpeed_y_wgs();
        // Wir löschen die init-Position, damit sie nicht als Messung genutzt wird
        copyListOfAllCartesianPoints.remove(copyListOfAllCartesianPoints.getFirst());

//        ImuValues firstImuValue = Service.getListOfAllImuValues().getFirst();
        ImuValues firstImuValue = Service.getResampledListOfAllImuValues().getFirst();
        double accel_x = firstImuValue.getAccel_x_wgs();
        double accel_y = firstImuValue.getAccel_y_wgs();
        // Dementsprechend muss auch der jeweillige IMU-Wert passend zur Position gelöscht werden
        Service.getResampledListOfAllImuValues().remove(firstImuValue);

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

//        currentMeasurment = new ArrayRealVector(new double[]{
//                copyListOfAllCartesianPoints.getFirst().getX(),
//                copyListOfAllCartesianPoints.getFirst().getY(),
//                copyListOfAllCartesianPoints.getFirst().getSpeed_x_wgs(),
//                copyListOfAllCartesianPoints.getFirst().getSpeed_y_wgs()
//        });
        z = new ArrayRealVector(new double[]{
                copyListOfAllCartesianPoints.getFirst().getX(),
                copyListOfAllCartesianPoints.getFirst().getY(),
                copyListOfAllCartesianPoints.getFirst().getSpeed_x_wgs(),
                copyListOfAllCartesianPoints.getFirst().getSpeed_y_wgs()
        });

        currentMeasurment = z;

        pm = new DefaultProcessModel(A, B, Q, x, P);
        mm = new DefaultMeasurementModel(H, R);
        filter = new KalmanFilter(pm, mm);

    }

    public void makeEstimation() {

        // Iteriere über Zahl der Positionen und fünfmal weiter
        for (int i = 0; i < Service.getResampledListOfAllImuValues().size(); i++) {
            ImuValues currentImu = Service.getResampledListOfAllImuValues().get(i);

            if ((currentImu.getTimestamp() - timestamp) < 100) {
                continue;
            }
            // LAZY-WAITING:
            // Prüfe, ob TIME_TO_SLEEP vergangen ist oder nicht. Wenn nicht, springe zur nächsten Iteration
//            if ((System.currentTimeMillis() - timestamp) < TIME_TO_SLEEP) {
//                continue;
//            }

            //timestamp = System.currentTimeMillis();
            timestamp = currentImu.getTimestamp();

            //System.out.println("Anzahl Punkte in Kopie:  " + copyListOfAllCartesianPoints.size());
            System.out.println("Iteration, Nr.:  " + i);

            // Aktualisiere u
//            u.setEntry(0, (float)Service.getListOfAllImuValues().get(i).getAccel_x_wgs());
//            u.setEntry(1, (float)Service.getListOfAllImuValues().get(i).getAccel_y_wgs());
            u.setEntry(0, Service.getResampledListOfAllImuValues().get(i).getAccel_x_wgs());
            u.setEntry(1, Service.getResampledListOfAllImuValues().get(i).getAccel_y_wgs());

            filter.predict(u);

            // Prüfe ob 1s vergangen ist. Wenn ja, hole nächste bekannte Position und den Speed
            //if ((System.currentTimeMillis() - timestamp2) >= 1000) {
            if ((currentImu.getTimestamp() - timestamp2) >= 1000) {
                //timestamp2 = System.currentTimeMillis();
                timestamp2 = currentImu.getTimestamp();

                System.out.println("===================================================================================Aktuelle Position, Zeit:  " + new Timestamp(currentImu.getTimestamp()));
                //CartesianPoint currentPosition = copyListOfAllCartesianPoints.get(j);
                CartesianPoint currentPosition = copyListOfAllCartesianPoints.getFirst();
                System.out.println("===================================================================================Aktueller kart. Punkt:  " + currentPosition.getX() + " ; " + currentPosition.getY());
                System.out.println("Time, cartesianPoint:  " + currentPosition.getTimestamp() + " ; IMU-TimeSTamp:  " + currentImu.getTimestamp());

                currentMeasurment = new ArrayRealVector(new double[]{
                        currentPosition.getX(),
                        currentPosition.getY(),
                        currentPosition.getSpeed_x_wgs(),
                        currentPosition.getSpeed_y_wgs()
                });

                filter.correct(currentMeasurment);


                // Entferne die soeben genutzte Position damit Schleife irgendwann terminiert
                //copyListOfAllCartesianPoints.remove(j);
                copyListOfAllCartesianPoints.remove(currentPosition);

            }

            double estimatedPosition_x = filter.getStateEstimation()[0];
            double estimatedPosition_y = filter.getStateEstimation()[1];

            System.out.println("Geschätzter Punkt:  " + estimatedPosition_x + " ; " + estimatedPosition_y + " Zur Zeit (jetzt):  " + new Timestamp(System.currentTimeMillis()) + "\n");

            CartesianPoint estimatedPoint = new CartesianPoint(estimatedPosition_x, estimatedPosition_y);
            //estimatedPoint.setTimestamp(System.currentTimeMillis());
            estimatedPoint.setTimestamp(Service.getResampledListOfAllImuValues().get(i).getTimestamp());
            //estimatedPoint.setTimestamp(Service.getListOfAllImuValues().get(i).getTimestamp());

            Service.getListOfAllEstimatedCartesianPoints().add(estimatedPoint);

            // Rechne die geschätzten Punkte wieder in das WGS-System um
            Service.calculateAngleAndDistanceByPoint(estimatedPoint);
            Service.calculateWGSCoordinateByCartesianPoint(estimatedPoint);

            // Berechne den Abstand zwischen geschätztem Punkt und GT-Punkt
            Service.calculateDistanceBetweenEstimatedAndGTPosition(estimatedPoint, i);

            // Erhöhe counter
            //i++;
        }
    }
}
