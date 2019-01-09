/**
 * @author Luan Hajzeraj on 12.12.2018.
 */
public class Constants {
    private static Constants instance = null;
    private Constants(){}
    static Constants getInstance() {
        if(instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    private float SIGMA_ACCEL = 8f;
    private int POSITION_OF_FIRST_POINT_WITH_CARTESIAN_COORDINATES = 197;
    private double SIGMA_GNSS_SPEED = 0.5;

    // Einzelne Segment
    // MERKE: ACHTE DARAUF, DASS DIE DATENSÄTZE IMMER DIE ENTSPR. LABEL-MARK. HABEN!!!
    // Segment A = {12078, 12700}
    // Segment B = {12700_First, 12694}
    // Segment C = {12694, 12700}
    // Segment D = {12700_Second, 12078}
    private String[] currentSegment;

    // statische Werte für Segmente
    private final String[] segmentA = new String[]{"12078", "12700"};
    private final String[] segmentB = new String[]{"12700_First", "12694"};
    private final String[] segmentC = new String[]{"12694", "12700"};
    private final String[] segmentD = new String[]{"12700_Second", "12078"};

    float getSigmaAccel() {
        return SIGMA_ACCEL;
    }

    void setSigmaAccel(float sigmaAccel) {
        SIGMA_ACCEL = sigmaAccel;
    }

    int getPositionOfFirstPointWithCartesianCoordinates() {
        return POSITION_OF_FIRST_POINT_WITH_CARTESIAN_COORDINATES;
    }

    public void setPositionOfFirstPointWithCartesianCoordinates(int positionOfFirstPointWithCartesianCoordinates) {
        POSITION_OF_FIRST_POINT_WITH_CARTESIAN_COORDINATES = positionOfFirstPointWithCartesianCoordinates;
    }

    double getSigmaGnssSpeed() {
        return SIGMA_GNSS_SPEED;
    }

    void setSigmaGnssSpeed(double sigmaGnssSpeed) {
        SIGMA_GNSS_SPEED = sigmaGnssSpeed;
    }

    String[] getCurrentSegment() {
        return currentSegment;
    }

    void setCurrentSegment(String[] currentSegment) {
        this.currentSegment = currentSegment;
    }

    String[] getSegmentA() {
        return segmentA;
    }

    String[] getSegmentB() {
        return segmentB;
    }

    String[] getSegmentC() {
        return segmentC;
    }

    String[] getSegmentD() {
        return segmentD;
    }
}
