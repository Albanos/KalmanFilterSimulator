import geodesy.GlobalPosition;

/**
 * @author Luan Hajzeraj on 12/1/2018.
 */
public class Data {
    // aus .csv-File
    private long timestamp;
    private double latitude_wgs;
    private double longitude_wgs;
    private double altitude_wgs;
    private double bearing_wgs;
    private double amountSpeed_wgs;
    private double latitude_gt;
    private double longitude_gt;
    private double accuracy_gnss;
    private double accel_x_imu;
    private double accel_y_imu;
    private double accel_z_imu;
    private double magnetic_x_imu;
    private double magnetic_y_imu;
    private double magnetic_z_imu;
    private double gravitiy_x_imu;
    private double gravitiy_y_imu;
    private double gravitiy_z_imu;

    // für kartesischen Punkt
    private double cartesian_x;
    private double cartesian_y;
    private double accel_x_wgs;
    private double accel_y_wgs;
    private double speed_x_wgs;
    private double speed_y_wgs;

    // für geodesy-Rechnungen
    private GlobalPosition globalPosition;

    // für die schätzungen
    private double estimatedPoint_x;
    private double estimatedPoint_y;
    private double estimatedLat;
    private double estimatedLon;

    // Abstände der geschätzten WGS-Positionen zur WGS-GT-Position mit Berücksichtigung der Richtung (in und um Bewegungsrichtung)
    private double latiDistanceEstToGtWithDirection;
    private double longiDistanceEstToGtWithDirection;

    // Abstände der GNSS-Positionen zur WGS-GT-Position mit Berücksichtigung der Richtung (in und um Bewegungsrichtung)
    private double latiDistanceGnssToGtWithDirection;
    private double longiDistanceGnssToGtWithDirection;

    // Absoluter Abstand (keine lat/lon-Betrachtung), Est <--> GT
    private double absoluteDistanceEstGt;

    // Absoluter Abstand (keine lat/lon-Betrachtung), GNSS <--> GT
    private double absoluteDistanceGnssGt;

    // GT-direction für die Rotation der Punkte (für Abstandsberechnung in und um Bewegungsrichtung)
    private double gtDirection;

    //===============================================

    public double getAbsoluteDistanceEstGt() {
        return absoluteDistanceEstGt;
    }

    public void setAbsoluteDistanceEstGt(double absoluteDistanceEstGt) {
        this.absoluteDistanceEstGt = absoluteDistanceEstGt;
    }

    public double getAbsoluteDistanceGnssGt() {
        return absoluteDistanceGnssGt;
    }

    public void setAbsoluteDistanceGnssGt(double absoluteDistanceGnssGt) {
        this.absoluteDistanceGnssGt = absoluteDistanceGnssGt;
    }

    public double getLatiDistanceEstToGtWithDirection() {
        return latiDistanceEstToGtWithDirection;
    }

    public void setLatiDistanceEstToGtWithDirection(double latiDistanceEstToGtWithDirection) {
        this.latiDistanceEstToGtWithDirection = latiDistanceEstToGtWithDirection;
    }

    public double getLongiDistanceEstToGtWithDirection() {
        return longiDistanceEstToGtWithDirection;
    }

    public void setLongiDistanceEstToGtWithDirection(double longiDistanceEstToGtWithDirection) {
        this.longiDistanceEstToGtWithDirection = longiDistanceEstToGtWithDirection;
    }

    public double getLatiDistanceGnssToGtWithDirection() {
        return latiDistanceGnssToGtWithDirection;
    }

    public void setLatiDistanceGnssToGtWithDirection(double latiDistanceGnssToGtWithDirection) {
        this.latiDistanceGnssToGtWithDirection = latiDistanceGnssToGtWithDirection;
    }

    public double getLongiDistanceGnssToGtWithDirection() {
        return longiDistanceGnssToGtWithDirection;
    }

    public void setLongiDistanceGnssToGtWithDirection(double longiDistanceGnssToGtWithDirection) {
        this.longiDistanceGnssToGtWithDirection = longiDistanceGnssToGtWithDirection;
    }

    public double getGtDirection() {
        return gtDirection;
    }

    public void setGtDirection(double gtDirection) {
        this.gtDirection = gtDirection;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude_wgs() {
        return latitude_wgs;
    }

    public void setLatitude_wgs(double latitude_wgs) {
        this.latitude_wgs = latitude_wgs;
    }

    public double getLongitude_wgs() {
        return longitude_wgs;
    }

    public void setLongitude_wgs(double longitude_wgs) {
        this.longitude_wgs = longitude_wgs;
    }

    public double getAltitude_wgs() {
        return altitude_wgs;
    }

    public void setAltitude_wgs(double altitude_wgs) {
        this.altitude_wgs = altitude_wgs;
    }

    public double getBearing_wgs() {
        return bearing_wgs;
    }

    public void setBearing_wgs(double bearing_wgs) {
        this.bearing_wgs = bearing_wgs;
    }

    public double getAmountSpeed_wgs() {
        return amountSpeed_wgs;
    }

    public void setAmountSpeed_wgs(double amountSpeed_wgs) {
        this.amountSpeed_wgs = amountSpeed_wgs;
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

    public double getAccuracy_gnss() {
        return accuracy_gnss;
    }

    public void setAccuracy_gnss(double accuracy_gnss) {
        this.accuracy_gnss = accuracy_gnss;
    }

    public double getAccel_x_imu() {
        return accel_x_imu;
    }

    public void setAccel_x_imu(double accel_x_imu) {
        this.accel_x_imu = accel_x_imu;
    }

    public double getAccel_y_imu() {
        return accel_y_imu;
    }

    public void setAccel_y_imu(double accel_y_imu) {
        this.accel_y_imu = accel_y_imu;
    }

    public double getAccel_z_imu() {
        return accel_z_imu;
    }

    public void setAccel_z_imu(double accel_z_imu) {
        this.accel_z_imu = accel_z_imu;
    }

    public double getMagnetic_x_imu() {
        return magnetic_x_imu;
    }

    public void setMagnetic_x_imu(double magnetic_x_imu) {
        this.magnetic_x_imu = magnetic_x_imu;
    }

    public double getMagnetic_y_imu() {
        return magnetic_y_imu;
    }

    public void setMagnetic_y_imu(double magnetic_y_imu) {
        this.magnetic_y_imu = magnetic_y_imu;
    }

    public double getMagnetic_z_imu() {
        return magnetic_z_imu;
    }

    public void setMagnetic_z_imu(double magnetic_z_imu) {
        this.magnetic_z_imu = magnetic_z_imu;
    }

    public double getGravitiy_x_imu() {
        return gravitiy_x_imu;
    }

    public void setGravitiy_x_imu(double gravitiy_x_imu) {
        this.gravitiy_x_imu = gravitiy_x_imu;
    }

    public double getGravitiy_y_imu() {
        return gravitiy_y_imu;
    }

    public void setGravitiy_y_imu(double gravitiy_y_imu) {
        this.gravitiy_y_imu = gravitiy_y_imu;
    }

    public double getGravitiy_z_imu() {
        return gravitiy_z_imu;
    }

    public void setGravitiy_z_imu(double gravitiy_z_imu) {
        this.gravitiy_z_imu = gravitiy_z_imu;
    }

    public double getCartesian_x() {
        return cartesian_x;
    }

    public void setCartesian_x(double cartesian_x) {
        this.cartesian_x = cartesian_x;
    }

    public double getCartesian_y() {
        return cartesian_y;
    }

    public void setCartesian_y(double cartesian_y) {
        this.cartesian_y = cartesian_y;
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

    public GlobalPosition getGlobalPosition() {
        return globalPosition;
    }

    public void setGlobalPosition(GlobalPosition globalPosition) {
        this.globalPosition = globalPosition;
    }

    public double getEstimatedPoint_x() {
        return estimatedPoint_x;
    }

    public void setEstimatedPoint_x(double estimatedPoint_x) {
        this.estimatedPoint_x = estimatedPoint_x;
    }

    public double getEstimatedPoint_y() {
        return estimatedPoint_y;
    }

    public void setEstimatedPoint_y(double estimatedPoint_y) {
        this.estimatedPoint_y = estimatedPoint_y;
    }

    public double getEstimatedLat() {
        return estimatedLat;
    }

    public void setEstimatedLat(double estimatedLat) {
        this.estimatedLat = estimatedLat;
    }

    public double getEstimatedLon() {
        return estimatedLon;
    }

    public void setEstimatedLon(double estimatedLon) {
        this.estimatedLon = estimatedLon;
    }
}
