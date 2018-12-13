/**
 * @author Luan Hajzeraj on 12.12.2018.
 */
public class Constants {
    public static float SIGMA_ACCEL = 8f;
    public static int POSITION_OF_FIRST_POINT_WITH_CARTESIAN_COORDINATES = 197;
    public static double SIGMA_GNSS_SPEED = 3;

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
}
