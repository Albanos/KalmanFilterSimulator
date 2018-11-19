/**
 * @author Luan Hajzeraj on 16.11.2018.
 */
public class Coordinates {
    private double latitude;
    private double longitude;
    private double altitude;
    private double latitude_GT;
    private double longitude_GT;
    private double timestamp;
    private double accuracy;

    public Coordinates() {
    }

    public Coordinates(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = System.currentTimeMillis();
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

    public double getTimestamp() {
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

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }
}
