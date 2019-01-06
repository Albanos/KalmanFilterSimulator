import geodesy.GlobalPosition;

import java.util.List;

/**
 * @author Luan Hajzeraj on 12.12.2018.
 */
public class Constants {
    public static float SIGMA_ACCEL = 8f;
    public static int POSITION_OF_FIRST_POINT_WITH_CARTESIAN_COORDINATES = 197;
    public static double SIGMA_GNSS_SPEED = 0.5;

    // Einzelne Segment
    // MERKE: ACHTE DARAUF, DASS DIE DATENSÄTZE IMMER DIE ENTSPR. LABEL-MARK. HABEN!!!
    // Segment A = {12078, 12700}
    // Segment B = {12700_First, 12694}
    // Segment C = {12694, 12700}
    // Segment D = {12700_Second, 12078}
    private static String[] currentSegment;

    // statische Werte für Segmente
    private static final String[] segmentA = new String[]{"12078", "12700"};
    private static final String[] segmentB = new String[]{"12700_First", "12694"};
    private static final String[] segmentC = new String[]{"12694", "12700"};
    private static final String[] segmentD = new String[]{"12700_Second", "12078"};

    public static float getSigmaAccel() {
        return SIGMA_ACCEL;
    }

    public static void setSigmaAccel(float sigmaAccel) {
        SIGMA_ACCEL = sigmaAccel;
    }

    public static int getPositionOfFirstPointWithCartesianCoordinates() {
        return POSITION_OF_FIRST_POINT_WITH_CARTESIAN_COORDINATES;
    }

    public static void setPositionOfFirstPointWithCartesianCoordinates(int positionOfFirstPointWithCartesianCoordinates) {
        POSITION_OF_FIRST_POINT_WITH_CARTESIAN_COORDINATES = positionOfFirstPointWithCartesianCoordinates;
    }

    public static double getSigmaGnssSpeed() {
        return SIGMA_GNSS_SPEED;
    }

    public static void setSigmaGnssSpeed(double sigmaGnssSpeed) {
        SIGMA_GNSS_SPEED = sigmaGnssSpeed;
    }

    public static String[] getCurrentSegment() {
        return currentSegment;
    }

    public static void setCurrentSegment(String[] currentSegment) {
        Constants.currentSegment = currentSegment;
    }

    public static void setListOfAllDataByGlobalSegment() {
        // Hole aus der Map diejenige Liste, mit der gearbeitet werden soll (je nach Segment)
        String keyForMap = Constants.getCurrentSegment()[0].concat("_").concat(Constants.getCurrentSegment()[1]);
        List<Data> dataOfSegment = CsvReader.getOriginalLinesBySegments().get(keyForMap);
        // Resete alles vom Service ------------------TEST
        Service2.getListOfAllData().clear();
        Service2.getAngleDistanceDataMap().clear();
        Service2.setRmseAbsoluteDistanceGnssGt(0);
        Service2.setRmseAbsoluteDistanceEstGt(0);
        Service2.setDt(0);
        Service2.setOldDt(0);
        Service2.setRmseLongiGnssGt(0);
        Service2.setRmseLatiGnssGt(0);
        Service2.setRmseLongiEstGt(0);
        Service2.setRmseLatiEstGt(0);
        Service2.getListOfAllData().addAll(dataOfSegment);
        // Aktualisiere die erste Position
        Service2.setFirstGlobalPosition(new GlobalPosition(
                Service2.getListOfAllData().getFirst().getLatitude_wgs(),
                Service2.getListOfAllData().getFirst().getLongitude_wgs(),
                Service2.getListOfAllData().getFirst().getAltitude_wgs()
        ));
    }

    public static String[] getSegmentA() {
        return segmentA;
    }

    public static String[] getSegmentB() {
        return segmentB;
    }

    public static String[] getSegmentC() {
        return segmentC;
    }

    public static String[] getSegmentD() {
        return segmentD;
    }
}
