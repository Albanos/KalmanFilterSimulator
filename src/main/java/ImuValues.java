/**
 * @author Luan Hajzeraj on 18.11.2018.
 */
public class ImuValues {
    private double accel_x;
    private double accel_y;
    private double accel_z;

    private double magnitude_x;
    private double magnitude_y;
    private double magnitude_z;

    private double gravity_x;
    private double gravity_y;
    private double gravity_z;

    private long timestamp;

    private double bearingGnss;
    private double amountGnss;

    private double speed_x_wgs;
    private double speed_y_wgs;

    private double accel_x_wgs;
    private double accel_y_wgs;

    private double dt;

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
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

    public double getAccel_x() {
        return accel_x;
    }

    public void setAccel_x(double accel_x) {
        this.accel_x = accel_x;
    }

    public double getAccel_y() {
        return accel_y;
    }

    public void setAccel_y(double accel_y) {
        this.accel_y = accel_y;
    }

    public double getAccel_z() {
        return accel_z;
    }

    public void setAccel_z(double accel_z) {
        this.accel_z = accel_z;
    }

    public double getMagnitude_x() {
        return magnitude_x;
    }

    public void setMagnitude_x(double magnitude_x) {
        this.magnitude_x = magnitude_x;
    }

    public double getMagnitude_y() {
        return magnitude_y;
    }

    public void setMagnitude_y(double magnitude_y) {
        this.magnitude_y = magnitude_y;
    }

    public double getMagnitude_z() {
        return magnitude_z;
    }

    public void setMagnitude_z(double magnitude_z) {
        this.magnitude_z = magnitude_z;
    }

    public double getGravity_x() {
        return gravity_x;
    }

    public void setGravity_x(double gravity_x) {
        this.gravity_x = gravity_x;
    }

    public double getGravity_y() {
        return gravity_y;
    }

    public void setGravity_y(double gravity_y) {
        this.gravity_y = gravity_y;
    }

    public double getGravity_z() {
        return gravity_z;
    }

    public void setGravity_z(double gravity_z) {
        this.gravity_z = gravity_z;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getBearingGnss() {
        return bearingGnss;
    }

    public void setBearingGnss(double bearingGnss) {
        this.bearingGnss = bearingGnss;
    }

    public double getAmountGnss() {
        return amountGnss;
    }

    public void setAmountGnss(double amountGnss) {
        this.amountGnss = amountGnss;
    }

    public double getSpeed_x_wgs() {
        return speed_x_wgs;
    }

    public void setSpeed_x_wgs(double speed_x_wgs) {
        this.speed_x_wgs = speed_x_wgs;
    }

    public double getSpeed_y_wgs() {
        return speed_y_wgs;
    }

    public void setSpeed_y_wgs(double speed_y_wgs) {
        this.speed_y_wgs = speed_y_wgs;
    }
}
