/**
 * @author Luan Hajzeraj on 16.11.2018.
 */
public class Coordinates {
    private double latitude;
    private double longitude;
    private double altitude;
    private double latitude_GT;
    private double longitude_GT;
    private long timestamp;
    private double accuracy;
    private double bearing_gnss;
    private double amountSpeedGnss;
    private double speed_x_gnss;
    private double speed_y_gnss;

    public Coordinates() {
    }

    public Coordinates(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = System.currentTimeMillis();
    }

    public double getBearing_gnss() {
        return bearing_gnss;
    }

    public void setBearing_gnss(double bearing_gnss) {
        this.bearing_gnss = bearing_gnss;
    }

    public double getAmountSpeedGnss() {
        return amountSpeedGnss;
    }

    public void setAmountSpeedGnss(double amountSpeedGnss) {
        this.amountSpeedGnss = amountSpeedGnss;
    }

    public double getSpeed_x_gnss() {
        return speed_x_gnss;
    }

    public void setSpeed_x_gnss(double speed_x_gnss) {
        this.speed_x_gnss = speed_x_gnss;
    }

    public double getSpeed_y_gnss() {
        return speed_y_gnss;
    }

    public void setSpeed_y_gnss(double speed_y_gnss) {
        this.speed_y_gnss = speed_y_gnss;
    }

    public double getLatitude_GT() {
        return latitude_GT;
    }

    public void setLatitude_GT(double latitude_GT) {
        this.latitude_GT = latitude_GT;
    }

    public double getLongitude_GT() {
        return longitude_GT;
    }

    public void setLongitude_GT(double longitude_GT) {
        this.longitude_GT = longitude_GT;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
