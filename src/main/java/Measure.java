import geodesy.GlobalPosition;

import java.sql.Timestamp;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Measure {
    private double latitude;
    private double longitude;
    private double altitude;

    private double latitude_gt;
    private double longitude_gt;

    private double accel_x;
    private double accel_y;
    private double accel_z;

    private double magnitude_x;
    private double magnitude_y;
    private double magnitude_z;

    private double gravity_x;
    private double gravity_y;
    private double gravity_z;

    private String timestamp;

    private double bearingGnss;
    private double amountGnss;

    private double speed_x;
    private double speed_y;

    private double gnssAccuracy;

    public double getGnssAccuracy() {
        return gnssAccuracy;
    }

    public void setGnssAccuracy(double gnssAccuracy) {
        this.gnssAccuracy = gnssAccuracy;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude_gt() {
        return latitude_gt;
    }

    public void setLatitude_gt(double latitude_gt) {
        this.latitude_gt = latitude_gt;
    }

    public double getLongitude_gt() {
        return longitude_gt;
    }

    public void setLongitude_gt(double longitude_gt) {
        this.longitude_gt = longitude_gt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
}
