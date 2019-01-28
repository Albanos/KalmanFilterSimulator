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
    //private double SIGMA_GNSS_SPEED = 0.5;
    private double SIGMA_GNSS_SPEED = 3.0;
    // Festgelegter Wert, der auf den meisten Abschnitten zutrifft
    //private double SIGMA_POSITION_ACC = 3;
    private double SIGMA_POSITION_ACC = 5.0;

    // Einzelne Werte für die Prozessrausch-Matrix G, die die Prozessrauschkovarianzmatrix Q bilden
    // Standardwerte sind diejenigen, die für die "typische" Matrix Q sorgen
    private double G1 = 0.5;
    private double G2 = 0.5;
    private double G3 = 1.0;
    private double G4 = 1.0;

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

    public double getSIGMA_POSITION_ACC() {
        return SIGMA_POSITION_ACC;
    }

    public void setSIGMA_POSITION_ACC(double SIGMA_POSITION_ACC) {
        this.SIGMA_POSITION_ACC = SIGMA_POSITION_ACC;
    }

    public double getG1() {
        return G1;
    }

    public void setG1(double g1) {
        G1 = g1;
    }

    public double getG2() {
        return G2;
    }

    public void setG2(double g2) {
        G2 = g2;
    }

    public double getG3() {
        return G3;
    }

    public void setG3(double g3) {
        G3 = g3;
    }

    public double getG4() {
        return G4;
    }

    public void setG4(double g4) {
        G4 = g4;
    }

    float getSigmaAccel() {
        return SIGMA_ACCEL;
    }

    void setSigmaAccel(float sigmaAccel) {
        SIGMA_ACCEL = sigmaAccel;
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
