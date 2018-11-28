import geodesy.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Luan Hajzeraj on 13.11.2018.
 */
class Service {
    private static GeodeticCalculator calculator = new GeodeticCalculator();
    private static double dt;
    private static double oldDt = 0;
    private static LinkedList<Double> allDtValues = new LinkedList<>();
    private static GlobalPosition firstPosition = null;
    private static LinkedList<CartesianPoint> listOfAllCartesianPoints = new LinkedList<CartesianPoint>();
    private static LinkedList<CartesianPoint> listOfAllEstimatedCartesianPoints = new LinkedList<>();

    // fuer jede Position auch eine GlobalPosition
    private static LinkedList<GlobalPosition> listOfAllGlobalPositions = new LinkedList<GlobalPosition>();

    private static HashMap<CartesianPoint, List<Double>> angleDistanceCartesianPointMap = new HashMap<>();
    private static LinkedHashMap<CartesianPoint, Coordinates> pointToWGSMap = new LinkedHashMap<>();

    private static LinkedList<Coordinates> listOfAllWGSPositions = new LinkedList<>();
    private static LinkedList<ImuValues> listOfAllImuValues = new LinkedList<>();
    private static LinkedList<Coordinates> listOfAllGTWgsPositions = new LinkedList<>();

    private static LinkedHashMap<Coordinates, Double> estimatedWgsPositionGtLateralDistanceMap = new LinkedHashMap<>();
    private static LinkedHashMap<Coordinates, Double> estimatedWgsPositionGtLongitudinalDistanceMap = new LinkedHashMap<>();

    private static LinkedList<ImuValues> resampledListOfAllImuValues = new LinkedList<>();
    private static LinkedList<Coordinates> resampledListOfAllGtPositions = new LinkedList<>();

    // ====================================================


    public static LinkedList<Coordinates> getResampledListOfAllGtPositions() {
        return resampledListOfAllGtPositions;
    }

    public static LinkedHashMap<Coordinates, Double> getEstimatedWgsPositionGtLateralDistanceMap() {
        return estimatedWgsPositionGtLateralDistanceMap;
    }

    public static LinkedHashMap<Coordinates, Double> getEstimatedWgsPositionGtLongitudinalDistanceMap() {
        return estimatedWgsPositionGtLongitudinalDistanceMap;
    }

    public static LinkedList<Double> getAllDtValues() {
        return allDtValues;
    }

    public static LinkedList<ImuValues> getResampledListOfAllImuValues() {
        return resampledListOfAllImuValues;
    }

    public static LinkedList<Coordinates> getListOfAllGTWgsPositions() {
        return listOfAllGTWgsPositions;
    }

    public static LinkedList<ImuValues> getListOfAllImuValues() {
        return listOfAllImuValues;
    }

    public static LinkedList<Coordinates> getListOfAllWGSPositions() {
        return listOfAllWGSPositions;
    }

    public static LinkedHashMap<CartesianPoint, Coordinates> getPointToWGSMap() {
        return pointToWGSMap;
    }

    public static HashMap<CartesianPoint, List<Double>> getAngleDistanceCartesianPointMap() {
        return angleDistanceCartesianPointMap;
    }

    public static LinkedList<CartesianPoint> getListOfAllEstimatedCartesianPoints() {
        return listOfAllEstimatedCartesianPoints;
    }

    public static LinkedList<CartesianPoint> getListOfAllCartesianPoints() {
        return listOfAllCartesianPoints;
    }

    public static LinkedList<GlobalPosition> getListOfAllGlobalPositions() {
        return listOfAllGlobalPositions;
    }

    public static GlobalPosition getFirstPosition() {
        return firstPosition;
    }

    public static void setFirstPosition(GlobalPosition firstPosition) {
        Service.firstPosition = firstPosition;
    }

    public static double getDt() {
        return dt;
    }

    public static void setDt(double dt) {
        Service.dt = dt;
    }

    public static double getOldDt() {
        return oldDt;
    }

    public static void setOldDt(double oldDt) {
        Service.oldDt = oldDt;
    }

    /**
     * Implementierung der Rotations-Matrix aus Android
     *
     * @param R
     * @param I
     * @param gravity
     * @param geomagnetic
     * @return
     */
    public static boolean getRotationMatrix(float[] R, float[] I,
                                            float[] gravity, float[] geomagnetic) {
        // TODO: move this to native code for efficiency
        float Ax = gravity[0];
        float Ay = gravity[1];
        float Az = gravity[2];

        final float normsqA = (Ax * Ax + Ay * Ay + Az * Az);
        final float g = 9.81f;
        final float freeFallGravitySquared = 0.01f * g * g;
        if (normsqA < freeFallGravitySquared) {
            // gravity less than 10% of normal value
            return false;
        }

        final float Ex = geomagnetic[0];
        final float Ey = geomagnetic[1];
        final float Ez = geomagnetic[2];
        float Hx = Ey * Az - Ez * Ay;
        float Hy = Ez * Ax - Ex * Az;
        float Hz = Ex * Ay - Ey * Ax;
        final float normH = (float) Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);

        if (normH < 0.1f) {
            // device is close to free fall (or in space?), or close to
            // magnetic north pole. Typical values are  > 100.
            return false;
        }
        final float invH = 1.0f / normH;
        Hx *= invH;
        Hy *= invH;
        Hz *= invH;
        final float invA = 1.0f / (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
        Ax *= invA;
        Ay *= invA;
        Az *= invA;
        final float Mx = Ay * Hz - Az * Hy;
        final float My = Az * Hx - Ax * Hz;
        final float Mz = Ax * Hy - Ay * Hx;
        if (R != null) {
            if (R.length == 9) {
                R[0] = Hx;
                R[1] = Hy;
                R[2] = Hz;
                R[3] = Mx;
                R[4] = My;
                R[5] = Mz;
                R[6] = Ax;
                R[7] = Ay;
                R[8] = Az;
            } else if (R.length == 16) {
                R[0] = Hx;
                R[1] = Hy;
                R[2] = Hz;
                R[3] = 0;
                R[4] = Mx;
                R[5] = My;
                R[6] = Mz;
                R[7] = 0;
                R[8] = Ax;
                R[9] = Ay;
                R[10] = Az;
                R[11] = 0;
                R[12] = 0;
                R[13] = 0;
                R[14] = 0;
                R[15] = 1;
            }
        }
        if (I != null) {
            // compute the inclination matrix by projecting the geomagnetic
            // vector onto the Z (gravity) and X (horizontal component
            // of geomagnetic vector) axes.
            final float invE = 1.0f / (float) Math.sqrt(Ex * Ex + Ey * Ey + Ez * Ez);
            final float c = (Ex * Mx + Ey * My + Ez * Mz) * invE;
            final float s = (Ex * Ax + Ey * Ay + Ez * Az) * invE;
            if (I.length == 9) {
                I[0] = 1;
                I[1] = 0;
                I[2] = 0;
                I[3] = 0;
                I[4] = c;
                I[5] = s;
                I[6] = 0;
                I[7] = -s;
                I[8] = c;
            } else if (I.length == 16) {
                I[0] = 1;
                I[1] = 0;
                I[2] = 0;
                I[4] = 0;
                I[5] = c;
                I[6] = s;
                I[8] = 0;
                I[9] = -s;
                I[10] = c;
                I[3] = I[7] = I[11] = I[12] = I[13] = I[14] = 0;
                I[15] = 1;
            }
        }
        return true;
    }

    /**
     * Invertieren einer Matrix aus Android-Bibliothek
     *
     * @param mInv
     * @param mInvOffset
     * @param m
     * @param mOffset
     * @return
     */
    public static boolean invertM(float[] mInv, int mInvOffset, float[] m,
                                  int mOffset) {
        // Invert a 4 x 4 matrix using Cramer's Rule

        // transpose matrix
        final float src0 = m[mOffset + 0];
        final float src4 = m[mOffset + 1];
        final float src8 = m[mOffset + 2];
        final float src12 = m[mOffset + 3];

        final float src1 = m[mOffset + 4];
        final float src5 = m[mOffset + 5];
        final float src9 = m[mOffset + 6];
        final float src13 = m[mOffset + 7];

        final float src2 = m[mOffset + 8];
        final float src6 = m[mOffset + 9];
        final float src10 = m[mOffset + 10];
        final float src14 = m[mOffset + 11];

        final float src3 = m[mOffset + 12];
        final float src7 = m[mOffset + 13];
        final float src11 = m[mOffset + 14];
        final float src15 = m[mOffset + 15];

        // calculate pairs for first 8 elements (cofactors)
        final float atmp0 = src10 * src15;
        final float atmp1 = src11 * src14;
        final float atmp2 = src9 * src15;
        final float atmp3 = src11 * src13;
        final float atmp4 = src9 * src14;
        final float atmp5 = src10 * src13;
        final float atmp6 = src8 * src15;
        final float atmp7 = src11 * src12;
        final float atmp8 = src8 * src14;
        final float atmp9 = src10 * src12;
        final float atmp10 = src8 * src13;
        final float atmp11 = src9 * src12;

        // calculate first 8 elements (cofactors)
        final float dst0 = (atmp0 * src5 + atmp3 * src6 + atmp4 * src7)
                - (atmp1 * src5 + atmp2 * src6 + atmp5 * src7);
        final float dst1 = (atmp1 * src4 + atmp6 * src6 + atmp9 * src7)
                - (atmp0 * src4 + atmp7 * src6 + atmp8 * src7);
        final float dst2 = (atmp2 * src4 + atmp7 * src5 + atmp10 * src7)
                - (atmp3 * src4 + atmp6 * src5 + atmp11 * src7);
        final float dst3 = (atmp5 * src4 + atmp8 * src5 + atmp11 * src6)
                - (atmp4 * src4 + atmp9 * src5 + atmp10 * src6);
        final float dst4 = (atmp1 * src1 + atmp2 * src2 + atmp5 * src3)
                - (atmp0 * src1 + atmp3 * src2 + atmp4 * src3);
        final float dst5 = (atmp0 * src0 + atmp7 * src2 + atmp8 * src3)
                - (atmp1 * src0 + atmp6 * src2 + atmp9 * src3);
        final float dst6 = (atmp3 * src0 + atmp6 * src1 + atmp11 * src3)
                - (atmp2 * src0 + atmp7 * src1 + atmp10 * src3);
        final float dst7 = (atmp4 * src0 + atmp9 * src1 + atmp10 * src2)
                - (atmp5 * src0 + atmp8 * src1 + atmp11 * src2);

        // calculate pairs for second 8 elements (cofactors)
        final float btmp0 = src2 * src7;
        final float btmp1 = src3 * src6;
        final float btmp2 = src1 * src7;
        final float btmp3 = src3 * src5;
        final float btmp4 = src1 * src6;
        final float btmp5 = src2 * src5;
        final float btmp6 = src0 * src7;
        final float btmp7 = src3 * src4;
        final float btmp8 = src0 * src6;
        final float btmp9 = src2 * src4;
        final float btmp10 = src0 * src5;
        final float btmp11 = src1 * src4;

        // calculate second 8 elements (cofactors)
        final float dst8 = (btmp0 * src13 + btmp3 * src14 + btmp4 * src15)
                - (btmp1 * src13 + btmp2 * src14 + btmp5 * src15);
        final float dst9 = (btmp1 * src12 + btmp6 * src14 + btmp9 * src15)
                - (btmp0 * src12 + btmp7 * src14 + btmp8 * src15);
        final float dst10 = (btmp2 * src12 + btmp7 * src13 + btmp10 * src15)
                - (btmp3 * src12 + btmp6 * src13 + btmp11 * src15);
        final float dst11 = (btmp5 * src12 + btmp8 * src13 + btmp11 * src14)
                - (btmp4 * src12 + btmp9 * src13 + btmp10 * src14);
        final float dst12 = (btmp2 * src10 + btmp5 * src11 + btmp1 * src9)
                - (btmp4 * src11 + btmp0 * src9 + btmp3 * src10);
        final float dst13 = (btmp8 * src11 + btmp0 * src8 + btmp7 * src10)
                - (btmp6 * src10 + btmp9 * src11 + btmp1 * src8);
        final float dst14 = (btmp6 * src9 + btmp11 * src11 + btmp3 * src8)
                - (btmp10 * src11 + btmp2 * src8 + btmp7 * src9);
        final float dst15 = (btmp10 * src10 + btmp4 * src8 + btmp9 * src9)
                - (btmp8 * src9 + btmp11 * src10 + btmp5 * src8);

        // calculate determinant
        final float det =
                src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3;

        if (det == 0.0f) {
            return false;
        }

        // calculate matrix inverse
        final float invdet = 1.0f / det;
        mInv[mInvOffset] = dst0 * invdet;
        mInv[1 + mInvOffset] = dst1 * invdet;
        mInv[2 + mInvOffset] = dst2 * invdet;
        mInv[3 + mInvOffset] = dst3 * invdet;

        mInv[4 + mInvOffset] = dst4 * invdet;
        mInv[5 + mInvOffset] = dst5 * invdet;
        mInv[6 + mInvOffset] = dst6 * invdet;
        mInv[7 + mInvOffset] = dst7 * invdet;

        mInv[8 + mInvOffset] = dst8 * invdet;
        mInv[9 + mInvOffset] = dst9 * invdet;
        mInv[10 + mInvOffset] = dst10 * invdet;
        mInv[11 + mInvOffset] = dst11 * invdet;

        mInv[12 + mInvOffset] = dst12 * invdet;
        mInv[13 + mInvOffset] = dst13 * invdet;
        mInv[14 + mInvOffset] = dst14 * invdet;
        mInv[15 + mInvOffset] = dst15 * invdet;

        return true;
    }

    public static RealVector multiplyMV(float[] lhsMat,
                                        float[] rhsVec) {
        RealMatrix lhsM = new Array2DRowRealMatrix(4, 4);
        RealVector rhsV = new ArrayRealVector(4, 0);

        lhsM.setEntry(0, 0, lhsMat[0]);
        lhsM.setEntry(0, 1, lhsMat[1]);
        lhsM.setEntry(0, 2, lhsMat[2]);
        lhsM.setEntry(0, 3, lhsMat[3]);
        lhsM.setEntry(1, 0, lhsMat[4]);
        lhsM.setEntry(1, 1, lhsMat[5]);
        lhsM.setEntry(1, 2, lhsMat[6]);
        lhsM.setEntry(1, 3, lhsMat[7]);
        lhsM.setEntry(2, 0, lhsMat[8]);
        lhsM.setEntry(2, 1, lhsMat[9]);
        lhsM.setEntry(2, 2, lhsMat[10]);
        lhsM.setEntry(2, 3, lhsMat[11]);
        lhsM.setEntry(3, 0, lhsMat[12]);
        lhsM.setEntry(3, 1, lhsMat[13]);
        lhsM.setEntry(3, 2, lhsMat[14]);
        lhsM.setEntry(3, 3, lhsMat[15]);

        rhsV.setEntry(0, rhsVec[0]);
        rhsV.setEntry(1, rhsVec[1]);
        rhsV.setEntry(2, rhsVec[2]);
        rhsV.setEntry(3, rhsVec[3]);

        RealVector result = lhsM.preMultiply(rhsV);
        return result;
    }

    public static void calculateWgsAccelOfAllImuValues() {
        float[] gravityValues = new float[3];
        float[] magneticValues = new float[3];


        for (int i = 0; i < Service.getListOfAllImuValues().size(); i++) {
            ImuValues currentImu = Service.getListOfAllImuValues().get(i);
            //ImuValues currentImuValue = Service.getListOfAllImuValues().get(i);

            // Setze dt im Service und greife im Filter darauf zu. WICHTIG: timestamps der Messungen sind in ms, nicht in nano wie android
            Service.setDt(Service.getOldDt() == 0 ? 0.1f : (currentImu.getTimestamp() - Service.getOldDt()) / 1000.0f);
            Service.setOldDt(currentImu.getTimestamp());
            double dt = Service.getDt();
            Service.getAllDtValues().add(dt);

            gravityValues[0] = (float) currentImu.getGravity_x();
            gravityValues[1] = (float) currentImu.getGravity_y();
            gravityValues[2] = (float) currentImu.getGravity_z();

            magneticValues[0] = (float) currentImu.getMagnitude_x();
            magneticValues[1] = (float) currentImu.getMagnitude_y();
            magneticValues[2] = (float) currentImu.getMagnitude_z();

            float[] deviceRelativeAcceleration = new float[4];
            deviceRelativeAcceleration[0] = (float) currentImu.getAccel_x();
            deviceRelativeAcceleration[1] = (float) currentImu.getAccel_y();
            deviceRelativeAcceleration[2] = (float) currentImu.getAccel_z();
            deviceRelativeAcceleration[3] = 0;

            float[] R = new float[16], I = new float[16], earthAcc = new float[16];

            Service.getRotationMatrix(R, I, gravityValues, magneticValues);

            float[] inv = new float[16];
            Service.invertM(inv, 0, R, 0);
            RealVector realVector = Service.multiplyMV(inv, deviceRelativeAcceleration);
            earthAcc[0] = (float) realVector.getEntry(0);
            earthAcc[1] = (float) realVector.getEntry(1);
            earthAcc[2] = (float) realVector.getEntry(2);

            currentImu.setAccel_x_wgs(earthAcc[0]);
            currentImu.setAccel_y_wgs(earthAcc[1]);

        }
    }

    public static void calculateAllCartesianPoints() {
        if (Service.getListOfAllGlobalPositions().size() >= 2) {
            for (GlobalPosition g : Service.getListOfAllGlobalPositions()) {
                double distance = coordinateDistanceBetweenTwoPoints(Service.getFirstPosition(), g);
                double angle = coordinateAngleBetweenTwoPoints(Service.getFirstPosition(), g);

                // handle the first point: distance and angle between firstPoint and firstPoint is 0
                if (distance != 0.0 && angle != 0.0) {

                    Service.getListOfAllCartesianPoints().add(
                            new CartesianPoint(
                                    distance * Math.sin(Math.toRadians(angle)),
                                    distance * Math.cos(Math.toRadians(angle))
                            )
                    );
                }
            }
        }
    }

    /**
     * Berechnet die Distanz zwischen zwei globalPositions
     *
     * @param g1
     * @param g2
     * @return
     */
    static double coordinateDistanceBetweenTwoPoints(GlobalPosition g1, GlobalPosition g2) {
        if (g1 != null && g2 != null) {
            GeodeticMeasurement gm = calculator
                    .calculateGeodeticMeasurement(Ellipsoid.WGS84, g1, g2);

            return gm.getEllipsoidalDistance();
        }
        return 0;
    }

    /**
     * Berechnet den Winkel zwischen zwei globalPositions im Gradmaß (Winkel ist am Noden
     * ausgerichtet ; Winkelrichtung ist im Uhrzeigersinn)
     *
     * @param g1
     * @param g2
     * @return
     */
    static double coordinateAngleBetweenTwoPoints(GlobalPosition g1, GlobalPosition g2) {
        if (g1 != null && g2 != null) {
            GeodeticMeasurement gm = calculator
                    .calculateGeodeticMeasurement(Ellipsoid.WGS84, g1, g2);

            return gm.getAzimuth();
        }
        return 0;
    }

    /**
     * Setzt aktuell nur zusätzlich die GNSS-accuracy und den timestamp
     */
    public static void setAllOtheParametersOfAllCartesianPoints() {
        for (int i = 0; i < Service.getListOfAllWGSPositions().size() - 1; i++) {
            Coordinates wgsPosition = Service.getListOfAllWGSPositions().get(i);
            CartesianPoint cartesianPoint = Service.getListOfAllCartesianPoints().get(i);

            cartesianPoint.setAccuracy_gnss(wgsPosition.getAccuracy());
            cartesianPoint.setTimestamp(wgsPosition.getTimestamp());

            cartesianPoint.setSpeed_x_wgs(
                    wgsPosition.getAmountSpeedGnss() * Math.sin(Math.toRadians(wgsPosition.getBearing_gnss()))
            );

            cartesianPoint.setSpeed_y_wgs(
                    wgsPosition.getAmountSpeedGnss() * Math.cos(Math.toRadians(wgsPosition.getBearing_gnss()))
            );
        }
    }

    static void calculateAngleAndDistanceByPoint(CartesianPoint point) {
        double pointX = point.getX();
        double pointY = point.getY();

        // Ermittle die Differenz zum ersten Punkt
        double firstPointX = Service.getListOfAllCartesianPoints().getFirst().getX();
        double firstPointY = Service.getListOfAllCartesianPoints().getFirst().getY();

        double x = firstPointX - pointX;
        double y = firstPointY - pointY;


        // Bestimme zunächst die Distanz zum ersten Punkt, mithilfe von Pythagoras
        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        // Bestimme den Winkel über Atctan, da An- & Gegenkathete über Punkt bekannt
        double angle = 0;
        if (y == 0 && x > 0) {
            angle = Math.toDegrees(Math.PI / 2);
        } else if (y == 0 && x < 0) {
            angle = Math.toDegrees((Math.PI / 2) * -1);
        } else if (y > 0) {
            //angle = Math.toDegrees( Math.atan(x / y));
            angle = Math.toDegrees(Math.atan(x / y) + Math.PI);
        } else if (y < 0) {
            angle = Math.toDegrees(Math.atan(x / y));
            //angle = Math.toDegrees( Math.atan( x / y ) + Math.PI );
        }


        // Füge die Ergebnise der map hinzu
        LinkedList<Double> foo = new LinkedList<Double>();
        foo.add(angle);
        foo.add(distance);
        Service.getAngleDistanceCartesianPointMap().put(point, foo);
    }

    static void calculateWGSCoordinateByCartesianPoint(CartesianPoint point) {
        // Ermittle Winkel und Distanz, passend zum point
        List<Double> valuesForPoint = Service.getAngleDistanceCartesianPointMap().get(point);
        Double angleOfPoint = valuesForPoint.get(0);
        Double distanceOfPoint = valuesForPoint.get(1);

        // Bestimme über Geodesy-framework die neue Position
        GlobalCoordinates globalCoordinates = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84,
                Service.getFirstPosition(), angleOfPoint, distanceOfPoint);

        // Speichere die Werte in Map: erst lat, dann lon
        Service.getPointToWGSMap().put(point, new Coordinates(globalCoordinates.getLatitude(), globalCoordinates.getLongitude()));
    }

    /**
     * Berechnet die Distanz zwischen geschätztem Punkt (im WGS-System) und GT-Punkt (im WGS-System), auf lat/lon-Ebene
     * und speichert diesen in einer globalen map
     */
    static void calculateDistanceBetweenEstimatedAndGTPosition(CartesianPoint currentEsttimatedPoint, int iterationCounter) {
        Coordinates currentGTPosition = Service.getResampledListOfAllGtPositions().get(iterationCounter);
        //Coordinates currentGTPosition = Service.getListOfAllGTWgsPositions().get(iterationCounter);
        Coordinates estimatedWgsPosition = Service.getPointToWGSMap().get(currentEsttimatedPoint);

        calculateWgsGtLateralDistanceAndPushToGlobalMap(currentGTPosition, estimatedWgsPosition);
        calculateWgsGtLongitudinalDistanceAndPushToGlobalMap(currentGTPosition, estimatedWgsPosition);

//        GlobalPosition start = new GlobalPosition(estimatedWgsPosition.getLatitude(), estimatedWgsPosition.getLongitude(), 0);
//        GlobalPosition end = new GlobalPosition(currentGTPosition.getLatitude_GT(), currentGTPosition.getLongitude_GT(), 0);
//
//        GeodeticCurve geodeticCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
//        Service.getEstimatedWgsPositionGtDistanceMap().put(estimatedWgsPosition, geodeticCurve.getEllipsoidalDistance());
    }

    /**
     * Berechnet die laterale Distanz zwischen übergebenem geschätzten- und GT-Punkt. Laterale Distanz
     * wird in globaler Hash-Map gespeichert. Man beachte die Unterscheidung zur lateralen Distanz bei der
     * Definition der globalen Koordinaten
     *
     * @param currentGTPosition
     * @param estimatedWgsPosition
     */
    private static void calculateWgsGtLongitudinalDistanceAndPushToGlobalMap(Coordinates currentGTPosition, Coordinates estimatedWgsPosition) {
        double longitudeOfGt = currentGTPosition.getLongitude_GT();
        double latitudeOfWgs = estimatedWgsPosition.getLatitude();
        double longitudeOfWgs = estimatedWgsPosition.getLongitude();

        GlobalCoordinates gc1 = new GlobalCoordinates(latitudeOfWgs,longitudeOfWgs);
        GlobalCoordinates gc2 = new GlobalCoordinates(latitudeOfWgs,longitudeOfGt);

        GeodeticCurve geodeticCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gc1, gc2);
        Service.getEstimatedWgsPositionGtLongitudinalDistanceMap().put(estimatedWgsPosition,geodeticCurve.getEllipsoidalDistance());
    }

    /**
     * Berechnet die laterale Distanz zwischen übergebenem geschätzten- und GT-Punkt. Laterale Distanz
     * wird in globaler Hash-Map gespeichert. Man beachte die Unterscheidung zur longitudinalen Distanz bei der
     * Definition der globalen Koordinaten
     *
     * @param currentGTPosition
     * @param estimatedWgsPosition
     */
    private static void calculateWgsGtLateralDistanceAndPushToGlobalMap(Coordinates currentGTPosition, Coordinates estimatedWgsPosition) {
        double latitudeOfGt = currentGTPosition.getLatitude_GT();
        double latitudeOfWgs = estimatedWgsPosition.getLatitude();
        double longitudeOfWgs = estimatedWgsPosition.getLongitude();

        GlobalCoordinates gc1 = new GlobalCoordinates(latitudeOfWgs,longitudeOfWgs);
        GlobalCoordinates gc2 = new GlobalCoordinates(latitudeOfGt,longitudeOfWgs);

        GeodeticCurve geodeticCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gc1, gc2);
        Service.getEstimatedWgsPositionGtLateralDistanceMap().put(estimatedWgsPosition,geodeticCurve.getEllipsoidalDistance());
    }


    public static void makeDownSamplingOfImu() {
        int j = 1;
        int i = 0;
        double oldDt = 0;
        double currentDt;
        //for(int i =0; i < Service.getListOfAllImuValues().size() ; i++) {
        resampledListOfAllImuValues.add(Service.getListOfAllImuValues().getFirst());
        for (ImuValues imu : Service.getListOfAllImuValues()) {
            if(j < Service.getListOfAllImuValues().size()) {
                ImuValues iIMU = Service.getListOfAllImuValues().get(i);
                ImuValues jIMU = Service.getListOfAllImuValues().get(j);

                double differenceInSek = (jIMU.getTimestamp() - iIMU.getTimestamp()) / 1000.0f;

                //if ( differenceInSek >= 0.020894866) {
                // Wert stammt statisch von Simulation des Nexus 6 in Android-Studio
                if(differenceInSek >= 0.057312011) {
                // 0.1 -> 10 Hz
                //if(differenceInSek >= 0.1) {
                    resampledListOfAllImuValues.add(jIMU);
                    i = j;
                    currentDt = oldDt == 0 ? 0.1f : (jIMU.getTimestamp() - oldDt) / 1000.0f;
                    oldDt = jIMU.getTimestamp();
                    Service.getAllDtValues().add(currentDt);

                }
                j = j + 1;
            }
        }
        LinkedList<ImuValues> foo = Service.getListOfAllImuValues();
        LinkedList<ImuValues> foo2 = Service.getResampledListOfAllImuValues();
        long timestampOfLast = foo2.get(foo2.size() - 1).getTimestamp();
        long timestampOfLastLast = foo2.get(foo2.size() - 2).getTimestamp();
        double x = (timestampOfLast - timestampOfLastLast) / 1000.0f;
        Service.setDt(x);

        makeDownSamplingOfGtPositions();
    }

    private static void makeDownSamplingOfGtPositions() {
        int j = 1;
        int i = 0;
        double oldDt = 0;
        double currentDt;
        resampledListOfAllGtPositions.add(Service.getListOfAllGTWgsPositions().getFirst());
        for (Coordinates c : Service.getListOfAllGTWgsPositions()) {
            if(j < Service.getListOfAllGTWgsPositions().size()) {
                Coordinates iCoord = Service.getListOfAllGTWgsPositions().get(i);
                Coordinates jCoord = Service.getListOfAllGTWgsPositions().get(j);

                double differenceInSek = (jCoord.getTimestamp() - iCoord.getTimestamp()) / 1000.0f;

                //if ( differenceInSek >= 0.020894866) {
                // Wert stammt statisch von Simulation des Nexus 6 in Android-Studio
                //if(differenceInSek >= 0.057312011) {
                // 0.1 -> 10 Hz
                if(differenceInSek >= 0.1) {
                    resampledListOfAllGtPositions.add(jCoord);
                    i = j;

                }
                j = j + 1;
            }
        }
    }

    public static double calculateAverage(LinkedList<Double> values) {
        double sum = 0;
        if(!values.isEmpty()) {
            for (Double mark : values) {
                sum = sum + mark;
            }
            return sum / values.size();
        }
        return sum;
    }
}
