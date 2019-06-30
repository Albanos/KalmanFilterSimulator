import geodesy.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Luan Hajzeraj on 12/1/2018.
 */
class Service2 {
    private static Service2 instance = null;

    private Service2() {
    }

    static Service2 getInstance() {
        if (instance == null) {
            instance = new Service2();
        }
        return instance;
    }

    private final Constants constants = Constants.getInstance();
    private final CsvReader csvReader = CsvReader.getInstance();

    private LinkedList<Data> listOfAllData = new LinkedList<>();
    private GeodeticCalculator calculator = new GeodeticCalculator();
    private GlobalPosition firstGlobalPosition = null;
    private double dt;
    private double oldDt;

    //==================================================

    void setListOfAllDataByGlobalSegment() {
        // Hole aus der Map diejenige Liste, mit der gearbeitet werden soll (je nach Segment)
        final String keyForMap = constants.getCurrentSegment()[0].concat("_").concat(constants.getCurrentSegment()[1]);
        final List<Data> dataOfSegment = csvReader.getOriginalLinesBySegments().get(keyForMap);
        // Clear die listOfAllData und setze Sie neu
        getListOfAllData().clear();
        getListOfAllData().addAll(dataOfSegment);
        // Aktualisiere die erste Position
        Data firstData = getListOfAllData().getFirst();
        setFirstGlobalPosition(new GlobalPosition(
                firstData.getLatitude_wgs(),
                firstData.getLongitude_wgs(),
                firstData.getAltitude_wgs()
        ));
        // Setze den StepCounter-Zähler zurück (In jeder Datei beginnt StepDetectorCounter wieder mit 1)
        CsvReader.setOldStepDetectorCount(0);
    }

    void setListOfAllData(LinkedList<Data> listOfAllData) {
        this.listOfAllData = listOfAllData;
    }

    double getDt() {
        return dt;
    }

    void setDt(double dt) {
        this.dt = dt;
    }

    double getOldDt() {
        return oldDt;
    }

    void setOldDt(double oldDt) {
        this.oldDt = oldDt;
    }

    public GlobalPosition getFirstGlobalPosition() {
        return firstGlobalPosition;
    }

    public void setFirstGlobalPosition(GlobalPosition firstGlobalPosition) {
        this.firstGlobalPosition = firstGlobalPosition;
    }

    LinkedList<Data> getListOfAllData() {
        return listOfAllData;
    }

    void calculateCartesianPointAndWgsAccelForData(final String[] segment) {
        final String keyForMap = segment[0] + "_" + segment[1];
        final List<Data> dataOfCurrentSegment = csvReader.getOriginalLinesBySegments().get(keyForMap);
        final GlobalPosition firstGlobalPosition;
        if(!dataOfCurrentSegment.isEmpty()) {
            final Data firstDataRow = dataOfCurrentSegment.get(0);
//            Wir verschieben den Ursprung des Koordinatensystems in den ersten GT-Punkt
            firstGlobalPosition = new GlobalPosition(
                        firstDataRow.getLatitude_wgs(),
                        firstDataRow.getLongitude_wgs(),
                        firstDataRow.getAltitude_wgs()
                );
//            firstGlobalPosition = new GlobalPosition(
//                    firstDataRow.getLatitude_gt(),
//                    firstDataRow.getLongitude_gt(),
//                    firstDataRow.getAltitude_wgs()
//            );
        } else {
            firstGlobalPosition = null;
        }

        for (Data row : dataOfCurrentSegment) {
            calculateCartesianPointsByDataRow(firstGlobalPosition, row);

            // Aktualisiere dt
            setDt(getOldDt() == 0 ? 0.1 : (getDt() - getOldDt()) / 1000.0f);
            setOldDt(getDt());
            float[] earthAcc = calculateWgsAccel(row);
            row.setAccel_x_wgs(earthAcc[0]);
            row.setAccel_y_wgs(earthAcc[1]);
        }
    }

    public void calculateCartesianPointsByDataRow(GlobalPosition firstGlobalPosition, Data row) {
        // Berechne nur einen kartesischen Punkt, wenn lat_wgs und lon_wgs != NaN
        if(!Double.isNaN(row.getLatitude_wgs()) && !Double.isNaN(row.getLongitude_wgs())) {
            // Berechne die kartesischen Punkte für die "normalen" Positionen
            final GlobalPosition globalPosition = row.getGlobalPosition();
            final double distance = coordinateDistanceBetweenTwoPoints(firstGlobalPosition, globalPosition);
            final double angle = coordinateAngleBetweenTwoPoints(firstGlobalPosition, globalPosition);

            //if (distance != 0.0 && angle != 0.0) {
            if (distance != 0.0 && !Double.isNaN(angle)) {
                row.setCartesian_x(distance * Math.sin(Math.toRadians(angle)));
                row.setCartesian_y(distance * Math.cos(Math.toRadians(angle)));
            }
        }
        // Berechne die kartesischen Punkte für die GT-Positionen
        final GlobalPosition globalPositionsGt = row.getGlobalPositionsGt();
        final double distanceGt = coordinateDistanceBetweenTwoPoints(firstGlobalPosition, globalPositionsGt);
        final double angleGt = coordinateAngleBetweenTwoPoints(firstGlobalPosition, globalPositionsGt);

        //if (distanceGt != 0.0 && angleGt != 0.0) {
        if (distanceGt != 0.0 && !Double.isNaN(angleGt)) {
            row.setCartesian_x_gt(distanceGt * Math.sin(Math.toRadians(angleGt)));
            row.setCartesian_y_gt(distanceGt * Math.cos(Math.toRadians(angleGt)));
        }
    }

    public HashMap<String, Double> calculateCartesianPointByLatLon(double lat, double lon, GlobalPosition firstGlobalPosition) {
        final GlobalPosition gp = new GlobalPosition(lat,lon,0);
        final double dist = coordinateDistanceBetweenTwoPoints(firstGlobalPosition, gp);
        final double ang = coordinateAngleBetweenTwoPoints(firstGlobalPosition, gp);

        HashMap<String, Double> returnMap = new HashMap<>();
        returnMap.put("x", dist * Math.sin(Math.toRadians(ang)));
        returnMap.put("y", dist * Math.cos(Math.toRadians(ang)));
        return returnMap;
    }

    private float[] calculateWgsAccel(Data row) {
        float[] gravityValues = new float[3];
        float[] magneticValues = new float[3];

        gravityValues[0] = (float) row.getGravitiy_x_imu();
        gravityValues[1] = (float) row.getGravitiy_y_imu();
        gravityValues[2] = (float) row.getGravitiy_z_imu();

        magneticValues[0] = (float) row.getMagnetic_x_imu();
        magneticValues[1] = (float) row.getMagnetic_y_imu();
        magneticValues[2] = (float) row.getMagnetic_z_imu();

        float[] deviceRelativeAcceleration = new float[4];
        deviceRelativeAcceleration[0] = (float) row.getAccel_x_imu();
        deviceRelativeAcceleration[1] = (float) row.getAccel_y_imu();
        deviceRelativeAcceleration[2] = (float) row.getAccel_z_imu();
        deviceRelativeAcceleration[3] = 0;

        float[] R = new float[16], I = new float[16], earthAcc = new float[16];
        getRotationMatrix(R, I, gravityValues, magneticValues);

        float[] inv = new float[16];
        invertM(inv, 0, R, 0);
        RealVector realVector = multiplyMV(inv, deviceRelativeAcceleration);
        earthAcc[0] = (float) realVector.getEntry(0);
        earthAcc[1] = (float) realVector.getEntry(1);
        earthAcc[2] = (float) realVector.getEntry(2);
        return earthAcc;
    }

    /**
     * Berechnet die Distanz zwischen zwei globalPositions
     *
     * @param g1 -
     * @param g2 -
     * @return -
     */
    public double coordinateDistanceBetweenTwoPoints(GlobalPosition g1, GlobalPosition g2) {
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
     * @param g1 -
     * @param g2 -
     * @return -
     */
    public double coordinateAngleBetweenTwoPoints(GlobalPosition g1, GlobalPosition g2) {
        if (g1 != null && g2 != null) {
            GeodeticMeasurement gm = calculator
                    .calculateGeodeticMeasurement(Ellipsoid.WGS84, g1, g2);

            return gm.getAzimuth();
        }
        return 0;
    }

    /**
     * Implementierung der Rotations-Matrix aus Android
     *
     * @param R           -
     * @param I           -
     * @param gravity     -
     * @param geomagnetic -
     * @return -
     */
    boolean getRotationMatrix(float[] R, float[] I,
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
     * @param mInv       -
     * @param mInvOffset -
     * @param m          -
     * @param mOffset    -
     * @return -
     */
    boolean invertM(float[] mInv, int mInvOffset, float[] m,
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

    RealVector multiplyMV(float[] lhsMat,
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

    void makeDownSampling(double dt) {
        int j = 1;
        int i = 0;
        double oldDt = 0;
        double currentDt;
        LinkedList<Data> temp = new LinkedList<>();
        //for(int i =0; i < Service.getListOfAllImuValues().size() ; i++) {
        for (Data d : getListOfAllData()) {
            if (j < getListOfAllData().size()) {
                Data iData = getListOfAllData().get(i);
                Data jData = getListOfAllData().get(j);

                double differenceInSek = (jData.getTimestamp() - iData.getTimestamp()) / 1000.0f;

                //if ( differenceInSek >= 0.020894866) {
                // Wert stammt statisch von Simulation des Nexus 6 in Android-Studio
                if (differenceInSek >= dt) {
                    // 0.1 -> 10 Hz
                    //if(differenceInSek >= 0.1) {
                    i = j;
                    temp.add(d);
                }
                j++;
            }
        }
        // Die in temp gespeicherten Einträge sind die übrigen, mit
        // denen wir weiter arbeiten wollen. Deshalb setze wir die
        // statische/globale Liste neu
        setListOfAllData(temp);

        // Aktualisiere dt, entsprechend dem letzte und vorletztem timestamp
        long last = getListOfAllData().get(getListOfAllData().size() - 1).getTimestamp();
        long lastLast = getListOfAllData().get(getListOfAllData().size() - 2).getTimestamp();
        setDt((last - lastLast) / 1000.0f);
    }

    void calculateAngleAndDistanceAndWgsPositionByDataPoint(Data data) {
        if(data.getEstimatedLat() != 0.0 && data.getEstimatedLon() != 0.0) {
            return;
        }

        double estimated_x = data.getEstimatedPoint_x();
        double estimated_y = data.getEstimatedPoint_y();

        // Berechne Abstand des Geschätzten und des ersten kartesischen Punktes
//        double x = 0 - Math.abs(estimated_x);
//        double y = 0 - Math.abs(estimated_y);
        double x = 0 - estimated_x;
        double y = 0 - estimated_y;

//        double x = firstCartesian_x - estimated_x;
//        double y = firstCartesian_y - estimated_y;

        // Bestimme zunächst die Distanz zum ersten Punkt, mithilfe von Pythagoras
        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        // Bestimme den Winkel über Atctan, da An- & Gegenkathete über Punkt bekannt
        double angle = 0;
        if (y == 0 && x > 0) {
            angle = Math.toDegrees(Math.PI / 2);
        } else if (y == 0 && x < 0) {
            angle = Math.toDegrees((Math.PI / 2) * -1);
        } else if (y > 0) {
            angle = Math.toDegrees(Math.atan(x / y) + Math.PI);
        } else if (y < 0) {
            angle = Math.toDegrees(Math.atan(x / y));
        }
        // Rücksprache mit Eric (Winkelrichtung im Uhrzeigersinn!!!)
//        if(y > 0 && x >= 0) {
//            angle = Math.toDegrees(Math.atan(x / y));
//        }
//        else if(y < 0 && x >= 0) {
//            angle = Math.toDegrees(Math.atan(x / y)) + 90;
//        }
//        else if(y < 0 && x <= 0) {
//            angle = Math.toDegrees(Math.atan(x / y)) + 180;
//        }
//        else if(y > 0 && x <= 0) {
//            angle = Math.toDegrees(Math.atan(x / y)) + 270;
//        }
//        else if(y == 0 && x > 0) {
//            angle = 90;
//        }
//        else if(y == 0 && x < 0) {
//            angle = 270;
//        }

        // Berechne nun die WGS-koordinaten
        calculateWGSCoordinateByDataPoint(data, angle, distance);
    }

    void calculateWGSCoordinateByDataPoint(Data data, double angle, double distance) {
//        // Ermittle Winkel und Distanz des Daten-Punktes anhand des übergebenen Datenpunktes
//        LinkedList<Double> angleAndDistanceOfDataPoint = getAngleDistanceDataMap().get(data);
//        Double angleOfDataPoint = angleAndDistanceOfDataPoint.get(0);
//        Double distanceOfDataPoint = angleAndDistanceOfDataPoint.get(1);

        // Berechne neue Koordinaten mittels Geodesy
        GlobalCoordinates globalCoordinates = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84,
                getFirstGlobalPosition(), angle, distance);

        // Setze entsprechende End-koordinaten im Datenpunkt selbst
        data.setEstimatedLat(globalCoordinates.getLatitude());
        data.setEstimatedLon(globalCoordinates.getLongitude());
    }

    /**
     * Lateraler und longitudinaler Abstand zwischen geschätzten- und GT-Punkten im WGS-System
     *
     * @param data
     */
    void calculateDistanceBetweenEstimatedAndGTPosition(Data data) {
        double latitude_est = data.getEstimatedLat();
        double longitude_est = data.getEstimatedLon();
        double longitude_gt = data.getLongitude_gt();
        double latitude_gt = data.getLatitude_gt();

        // Berechne zunächste die longitudinale Distanz
        GlobalCoordinates gc1 = new GlobalCoordinates(latitude_est, longitude_est);
        GlobalCoordinates gc2 = new GlobalCoordinates(latitude_est, longitude_gt);
        GeodeticCurve geodeticCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gc1, gc2);
        double distanceLon = geodeticCurve.getEllipsoidalDistance();
        if (longitude_est - longitude_gt < 0) {
            distanceLon = -distanceLon;
        }


        // Anschließend die laterale Distanz
        GlobalCoordinates gc3 = new GlobalCoordinates(latitude_est, longitude_est);
        GlobalCoordinates gc4 = new GlobalCoordinates(latitude_gt, longitude_est);
        GeodeticCurve geodeticCurve1 = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gc3, gc4);
        double distanceLat = geodeticCurve1.getEllipsoidalDistance();
        if (latitude_est - latitude_gt < 0) {
            distanceLat = -distanceLat;
        }
        // Berücksichtige die Richtung des Nutzers (Abstand in und um Bewegungsrichtung) und setze diesen Abstand im Daten-punkt
        HashMap<String, Double> latiLongiDistancesEstToGt =
                rotateOnGtDirection(distanceLat, distanceLon, (data.getGtDirection() * (Math.PI / 180)));

        // setze die berechneten lati/longi-Abstände im Datenpunkt
        data.setLatiDistanceEstToGtWithDirection(latiLongiDistancesEstToGt.get("latiDistance"));
        data.setLongiDistanceEstToGtWithDirection(latiLongiDistancesEstToGt.get("longiDistance"));

        // Berechne im selben Zug den Abstand zwischen GNSS- & GT-Position
        // Allerdings nur, wenn die jeweilige Position NICHT NaN ist
        if( !Double.isNaN(data.getLatitude_wgs()) && !Double.isNaN(data.getLongitude_wgs())) {
            calculateDistanceBetweenGNSSAndGTPosition(data);
        }
    }

    /**
     * Lateraler und longitudinaler Abstand zwischen Smartphone-GNSS- und GT-Punkten im WGS-System
     *
     * @param d
     */
    void calculateDistanceBetweenGNSSAndGTPosition(Data d) {
        double latitude_wgs = d.getLatitude_wgs();
        double longitude_wgs = d.getLongitude_wgs();
        double latitude_gt = d.getLatitude_gt();
        double longitude_gt = d.getLongitude_gt();

        // Berechne den longitudinalen Abstand
        GlobalCoordinates gc1 = new GlobalCoordinates(latitude_wgs, longitude_wgs);
        GlobalCoordinates gc2 = new GlobalCoordinates(latitude_wgs, longitude_gt);
        GeodeticCurve geodeticCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gc1, gc2);
        double distanceLon = geodeticCurve.getEllipsoidalDistance();
        if (longitude_wgs - longitude_gt < 0) {
            distanceLon = -distanceLon;
        }

        // Berechne den lateralen Abstand
        GlobalCoordinates gc3 = new GlobalCoordinates(latitude_wgs, longitude_wgs);
        GlobalCoordinates gc4 = new GlobalCoordinates(latitude_gt, longitude_wgs);
        GeodeticCurve geodeticCurve1 = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gc3, gc4);
        double distanceLat = geodeticCurve1.getEllipsoidalDistance();
        if (latitude_wgs - latitude_gt < 0) {
            distanceLat = -distanceLat;
        }

        HashMap<String, Double> latiLongiDistancesGnssToGt =
                rotateOnGtDirection(distanceLat, distanceLon, (d.getGtDirection() * (Math.PI / 180)));

        d.setLatiDistanceGnssToGtWithDirection(latiLongiDistancesGnssToGt.get("latiDistance"));
        d.setLongiDistanceGnssToGtWithDirection(latiLongiDistancesGnssToGt.get("longiDistance"));
    }

    void calculateAbsoluteDistanceBetweenEstAndGtPoint(Data d) {
        double estimatedLon = d.getEstimatedLon();
        double estimatedLat = d.getEstimatedLat();
        double longitude_gt = d.getLongitude_gt();
        double latitude_gt = d.getLatitude_gt();

        GlobalCoordinates gcEst = new GlobalCoordinates(estimatedLat, estimatedLon);
        GlobalCoordinates gcGt = new GlobalCoordinates(latitude_gt, longitude_gt);
        GeodeticCurve geodeticCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gcEst, gcGt);

        d.setAbsoluteDistanceEstGt(geodeticCurve.getEllipsoidalDistance());

        // Berechne im selben Zug den absoluten Abstand zwischen GT und Smartphone-GNSS
        calculateAbsoluteDistanceBetweenGnssAndGtPoint(d);
    }

    private void calculateAbsoluteDistanceBetweenGnssAndGtPoint(Data d) {
        double longitude_wgs = d.getLongitude_wgs();
        double latitude_wgs = d.getLatitude_wgs();
        double longitude_gt = d.getLongitude_gt();
        double latitude_gt = d.getLatitude_gt();

        GlobalCoordinates gcGnss = new GlobalCoordinates(latitude_wgs, longitude_wgs);
        GlobalCoordinates gcGt = new GlobalCoordinates(latitude_gt, longitude_gt);
        GeodeticCurve geodeticCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, gcGnss, gcGt);

        d.setAbsoluteDistanceGnssGt(geodeticCurve.getEllipsoidalDistance());
    }

    /**
     * Implementierung von Michel: berücksichtigt einfach die jeweillige Richtung und berechnet
     * somit den Abstand in und links/rechts zur Bewegungsrichtung des Nutzers
     */
    HashMap<String, Double> rotateOnGtDirection(double distanceLat, double distanceLon, double rad) {
        double latiDistance = distanceLon * Math.cos(rad) - distanceLat * Math.sin(rad);
        double longiDistance = distanceLat * Math.cos(rad) + distanceLon * Math.sin(rad);

        HashMap<String, Double> returnMap = new HashMap<>();
        returnMap.put("latiDistance", latiDistance);
        returnMap.put("longiDistance", longiDistance);

        return returnMap;
    }

    void writeAllDataToVikingFile(String runFileName, String[] currentSegment) {
        StringBuffer output = new StringBuffer();
        double firstPosition_lat = 0;
        double firstPosition_lon = 0;
        double lastPosition_lat = 0;
        double lastPosition_lon = 0;
        boolean firstPointSet = false;
        String präfixOfFile = "#VIKING GPS Data file http://viking.sf.net/\n" +
                "FILE_VERSION=1\n" +
                "\n" +
                "xmpp=0,125000\n" +
                "ympp=0,125000\n" +
                "lat=51,339021\n" +
                "lon=9,449298\n" +
                "mode=mercator\n" +
                "color=#cccccc\n" +
                "highlightcolor=#eea500\n" +
                "drawscale=t\n" +
                "drawcentermark=t\n" +
                "drawhighlight=t\n" +
                "\n" +
                "~Layer Map\n" +
                "name=Standard-Karte\n" +
                "mode=13\n" +
                "directory=\n" +
                "cache_type=1\n" +
                "mapfile=\n" +
                "alpha=255\n" +
                "autodownload=t\n" +
                "adlonlymissing=f\n" +
                "mapzoom=0\n" +
                "~EndLayer\n" +
                "\n" +
                "\n" +
                "~Layer TrackWaypoint\n" +
                "name=Importierte Datei\n" +
                "tracks_visible=t\n" +
                "waypoints_visible=t\n" +
                "routes_visible=t\n" +
                "trackdrawlabels=t\n" +
                "trackfontsize=3\n" +
                "drawmode=0\n" +
                "trackcolor=#000000\n" +
                "drawlines=t\n" +
                "line_thickness=1\n" +
                "drawdirections=f\n" +
                "trkdirectionsize=5\n" +
                "drawpoints=t\n" +
                "trkpointsize=2\n" +
                "drawelevation=f\n" +
                "elevation_factor=30\n" +
                "drawstops=f\n" +
                "stop_length=60\n" +
                "bg_line_thickness=0\n" +
                "trackbgcolor=#ffffff\n" +
                "speed_factor=30,000000\n" +
                "tracksortorder=0\n" +
                "drawlabels=t\n" +
                "wpfontsize=3\n" +
                "wpcolor=#000000\n" +
                "wptextcolor=#ffffff\n" +
                "wpbgcolor=#8383c4\n" +
                "wpbgand=f\n" +
                "wpsymbol=0\n" +
                "wpsize=4\n" +
                "wpsyms=t\n" +
                "wpsortorder=0\n" +
                "drawimages=t\n" +
                "image_size=64\n" +
                "image_alpha=255\n" +
                "image_cache_size=300\n" +
                "metadatadesc=\n" +
                "metadataauthor=\n" +
                "metadatatime=2017-04-11T15:30:19.307Z\n" +
                "metadatakeywords=\n" +
                "~LayerData\n" +
                "type=\"waypointlist\"\n" +
                "type=\"waypointlistend\"\n" +
                "type=\"track\" name=\"GT-Points\" color=#ff0000 visible=\"y\"\n";

        // Füge das Präfix dem Ouput hinzu
        output.append(präfixOfFile);

        // Beginne mit dem schreiben der GT-Positionen (rot)
        double latitude_gt = 0;
        double longitude_gt = 0;
        for (Data d : getListOfAllData()) {
            latitude_gt = d.getLatitude_gt();
            longitude_gt = d.getLongitude_gt();

            // Setze die erste Position für viking-markierung (erster Punkt A)
            if (!firstPointSet) {
                firstPosition_lat = latitude_gt;
                firstPosition_lon = longitude_gt;
                firstPointSet = true;
            }

            String row = "type=\"trackpoint\" latitude=\"" + latitude_gt + "\" longitude=\"" + longitude_gt + "\"\n";

            output.append(row);
        }

        // Setze die finale Position für viking-markierung (Punktmarkierung B)
        lastPosition_lat = latitude_gt;
        lastPosition_lon = longitude_gt;

        // schliesse die track ab
        output.append("type=\"trackend\"\n");

        // Hänge jede GNSS-Position an (grün)
        output.append("type=\"route\" name=\"GNSS-Points\" color=#00ff00\n");
        double oldLatitude = 0;
        double oldLongitude = 0;
        for (Data d : getListOfAllData()) {
            double latitude_wgs = d.getLatitude_wgs();
            double longitude_wgs = d.getLongitude_wgs();
            double altitude_wgs = d.getAltitude_wgs();

            // Zeiche jede Position nur einmal: wegen aktueller Struktur zeichnen wir Daten mehrmals
            if ((Double.isNaN(latitude_wgs) && Double.isNaN(longitude_wgs)) || latitude_wgs == oldLatitude || longitude_wgs == oldLongitude) {
                continue;
            }
            oldLatitude = latitude_wgs;
            oldLongitude = longitude_wgs;

            String row = "type=\"routepoint\" latitude=\""
                    + latitude_wgs + "\" longitude=\""
                    + longitude_wgs + "\" altitude=\"" + altitude_wgs + "\"\n";

            output.append(row);
        }

        // schliesse die route ab
        output.append("type=\"routeend\"\n");

        // schreibe die geschätzten Punkte (blau)
        output.append("type=\"route\" name=\"estimatedPoints\" color=#0000ff\n");
        for (Data d : getListOfAllData()) {
            double estimatedLat = d.getEstimatedLat();
            double estimatedLon = d.getEstimatedLon();

            if (estimatedLat == 0 || estimatedLon == 0) {
                continue;
            }

            String row = "type=\"routepoint\" latitude=\"" + estimatedLat + "\" longitude=\"" + estimatedLon + "\"\n";

            output.append(row);
            // FIXME: OLD, wird weiter unten, in der WaypointList gehandelt
//            // Wenn die Position eine Stufe ist, dann setzte zusätzlich noch einen Waypoint mit name-label auf Position
//            if(d.isCurbPosition()) {
//                row = "type=\"waypointlist\"\n" +
//                        "type=\"waypoint\" latitude=\"" + estimatedLat + "\" longitude=\"" + estimatedLon + "\" name=\"CurbPosition\"\n" +
//                        "type=\"waypointlistend\"\n";
//                output.append(row);
//            }
        }

        // schliesse die route ab
        output.append("type=\"routeend\"\n");

        // Füge statisch die Eckpunkte hinzu
        String suffix = "~LayerData\n" +
                "type=\"waypointlist\"\n" +
                "type=\"waypoint\" latitude=\"" + firstPosition_lat + "\" longitude=\"" + firstPosition_lon + "\" name=\"A\"\n" +
                "type=\"waypoint\" latitude=\"" + lastPosition_lat + "\" longitude=\"" + lastPosition_lon + "\" name=\"B\"\n" +
                "type=\"waypoint\" latitude=\"51.339037340000004\" longitude=\"9.4473964999999964\" name=\"C\" unixtime=\"1491926179\"\n" +
                "type=\"waypointlistend\"\n" +
                "~EndLayerData\n" +
                "~EndLayer";
        // Wenn eine CurbPosition existiert, dann füge sie der WayPointList hinzu, indem du variable suffix überschreibst
        Data curbPosition = getListOfAllData().stream().filter(d -> d.isCurbPosition()).findFirst().orElse(null);
        if(curbPosition != null) {
            suffix = "~LayerData\n" +
                    "type=\"waypointlist\"\n" +
                    "type=\"waypoint\" latitude=\"" + firstPosition_lat + "\" longitude=\"" + firstPosition_lon + "\" name=\"A\"\n" +
                    "type=\"waypoint\" latitude=\"" + lastPosition_lat + "\" longitude=\"" + lastPosition_lon + "\" name=\"B\"\n" +
                    "type=\"waypoint\" latitude=\"51.339037340000004\" longitude=\"9.4473964999999964\" name=\"C\" unixtime=\"1491926179\"\n" +
                    "type=\"waypoint\" latitude=\"" + curbPosition.getEstimatedLat() + "\" longitude=\"" + curbPosition.getEstimatedLon() + "\" name=\"CurbPosition\"\n" +
                    "type=\"waypointlistend\"\n" +
                    "~EndLayerData\n" +
                    "~EndLayer";
        }
        output.append(suffix);

        String outputAsString = output.toString();

        // schreibe alles in eine Datei
        OutputStream os = null;
        try {
            // Ermittle das aktuelle Segment und baue Suffix für Dateiname
            String segmentSuffix = "";
            switch(String.join(",",constants.getCurrentSegment())) {
                //case "12078,12700": segmentSuffix = "SegA"; break;
                // MERKE: Bei zweiter Messung anderen Startpunkt gewählt!
                case "12079,12700": segmentSuffix = "SegA"; break;
                case "12700_First,12694": segmentSuffix = "SegB"; break;
                case "12694,12700": segmentSuffix = "SegC"; break;
                //case "12700_Second,12078": segmentSuffix = "SegD"; break;
                // Ebenso wie oben...
                case "12700_Second,12079": segmentSuffix = "SegD"; break;
            }

            os = new FileOutputStream(new File("vikingExport_" + runFileName
//                    new Timestamp(System.currentTimeMillis()).toString()
//                            .replaceAll("\\s", "_")
//                            .replaceAll(":", "-")
//                            .replaceAll("\\.", "-")
                            .concat("_" + segmentSuffix)
                            .concat(".vik")));
            os.write(outputAsString.getBytes(), 0, outputAsString.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Double> calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts() {
        double sumOfLongDistancesEstGT = 0;
        double sumOfLatDistancesEstGT = 0;
        double sumOfLonDistancesGnssGT = 0;
        double sumOfLatDistancesGnssGT = 0;
        double sumOfAbsoluteDistanceEstGt = 0;
        double sumOfAbsoluteDistanceGnssGt = 0;
        int countOfIterations = 0;
        Map<String, Double> rmseMap = new LinkedHashMap<>();

        // Berechne die Quadrate der Differenzen
        for (Data d : getListOfAllData()) {
            double estimatedPoint_x = d.getEstimatedPoint_x();
            double estimatedPoint_y = d.getEstimatedPoint_y();

            // Schreibe nur diejenigen Datensätze, die auch geschätzte Punkte haben
            // Wir überspringen Punkte, wegen definierter Schätzfrequenz
            if (estimatedPoint_x == 0.0 || estimatedPoint_y == 0.0) {
                continue;
            }

            countOfIterations++;
            double longiDistanceEstToGt = d.getLongiDistanceEstToGtWithDirection();
            double latiDistanceEstToGt = d.getLatiDistanceEstToGtWithDirection();

            sumOfLongDistancesEstGT += Math.pow(longiDistanceEstToGt, 2);
            sumOfLatDistancesEstGT += Math.pow(latiDistanceEstToGt, 2);

            double longiDistanceGNSSToGt = d.getLongiDistanceGnssToGtWithDirection();
            double latiDistanceGNSSToGt = d.getLatiDistanceGnssToGtWithDirection();

            sumOfLonDistancesGnssGT += Math.pow(longiDistanceGNSSToGt, 2);
            sumOfLatDistancesGnssGT += Math.pow(latiDistanceGNSSToGt, 2);

            double absoluteDistanceEstGt = d.getAbsoluteDistanceEstGt();
            sumOfAbsoluteDistanceEstGt += Math.pow(absoluteDistanceEstGt, 2);

            double absoluteDistanceGnssGt = d.getAbsoluteDistanceGnssGt();
            sumOfAbsoluteDistanceGnssGt += Math.pow(absoluteDistanceGnssGt, 2);
        }

        // Setze nun den RMSE
        rmseMap.put("latiEstGt", Math.sqrt((sumOfLatDistancesEstGT / countOfIterations)));
        //setRmseLatiEstGt(Math.sqrt((sumOfLatDistancesEstGT / countOfIterations)));
        rmseMap.put("longiEstGt", Math.sqrt(sumOfLongDistancesEstGT / countOfIterations));
        //setRmseLongiEstGt(Math.sqrt(sumOfLongDistancesEstGT / countOfIterations));
        rmseMap.put("latiGnssGt", Math.sqrt(sumOfLatDistancesGnssGT / countOfIterations));
        //setRmseLatiGnssGt(Math.sqrt(sumOfLatDistancesGnssGT / countOfIterations));
        rmseMap.put("longiGnssGt", Math.sqrt(sumOfLonDistancesGnssGT / countOfIterations));
        //setRmseLongiGnssGt(Math.sqrt(sumOfLonDistancesGnssGT / countOfIterations));
        rmseMap.put("absEstGt", Math.sqrt(sumOfAbsoluteDistanceEstGt / countOfIterations));
        rmseMap.put("absGnssGt", Math.sqrt(sumOfAbsoluteDistanceGnssGt / countOfIterations));

        return rmseMap;
    }
}
