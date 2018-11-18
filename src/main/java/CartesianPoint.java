/**
 * @author Luan Hajzeraj on 14.11.2018.
 */
public class CartesianPoint {
    private double x;
    private double y;
    private double accel_x_wgs;
    private double accel_y_wgs;
    private double speed_x;
    private double speed_y;
    private double accuracy;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getSpeed_x() {
        return speed_x;
    }

    public void setSpeed_x(double speed_x) {
        this.speed_x = speed_x;
    }

    public double getSpeed_y() {
        return speed_y;
    }

    public void setSpeed_y(double speed_y) {
        this.speed_y = speed_y;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public CartesianPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getAccel_x_wgs() {
        return accel_x_wgs;
    }

    public void setAccel_x_wgs(double accel_x_wgs) {
        this.accel_x_wgs = accel_x_wgs;
    }

    public double getAccel_y_wgs() {
        return accel_y_wgs;
    }

    public void setAccel_y_wgs(double accel_y_wgs) {
        this.accel_y_wgs = accel_y_wgs;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
