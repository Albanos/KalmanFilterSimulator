import geodesy.GlobalPosition;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luan Hajzeraj on 11/23/2018.
 */
public class FirstTest {
    Service2 service = Service2.getInstance();

    @Test
    public void translateGlobalCoordinateToCartesianCoordinateTest() {
        GlobalPosition startPosition = new GlobalPosition(51.196110, 9.728793,0);
        GlobalPosition finish = new GlobalPosition(51.196155, 9.728793,0);

        // Berechne Abstand und Winkel zwischen start und Ziel
        double distance = service.coordinateDistanceBetweenTwoPoints(startPosition, finish);
        double angle = service.coordinateAngleBetweenTwoPoints(startPosition, finish);

        // Berechne kartesischen Punkt
        double x = distance * Math.sin(Math.toRadians(angle));
        int y = (int) (distance * Math.cos(Math.toRadians(angle)));

        Assert.assertTrue(x == 0);
        Assert.assertTrue(y == 5);

        // weiteres Szenario:
        // finish liegt (im Uhrzeigersinn) um 180 grad gedreht, auf 10m Entfernung
        finish.setLatitude(51.19602);
        finish.setLongitude(9.728793);

        // Berechne Abstand und Winkel zwischen start und Ziel
        distance = service.coordinateDistanceBetweenTwoPoints(startPosition, finish);
        angle = service.coordinateAngleBetweenTwoPoints(startPosition, finish);

        // Berechne kartesischen Punkt
        x = (int) (distance * Math.sin(Math.toRadians(angle)));
        y = (int) (distance * Math.cos(Math.toRadians(angle)));

        Assert.assertTrue(x == 0);
        Assert.assertTrue(y == -10);

        // weiteres Szenario:
        // finish liegt (im Uhrzeigersinn) um 45 grad gedreht, auf 10m Entfernung
        finish.setLatitude(51.196174);
        finish.setLongitude(9.728895);

        distance = service.coordinateDistanceBetweenTwoPoints(startPosition, finish);
        angle = service.coordinateAngleBetweenTwoPoints(startPosition, finish);

        // Berechne kartesischen Punkt
        double x2 =  (distance * Math.sin(Math.toRadians(angle)));
        double y2 =  (distance * Math.cos(Math.toRadians(angle)));

        Assert.assertTrue(x2 == 7.129927214979018);
        Assert.assertTrue(y2 == 7.120134871775175);
    }

    @Test
    public void translateCartesianCoordinatesInGlobalCoordinatesTest() {
        // Setze Startposition fest (auch setzen)
        GlobalPosition startPosition = new GlobalPosition(51.196110, 9.728793,0);
        service.setFirstGlobalPosition(startPosition);

        // Definiere kartesischen Punkt
        Data d = new Data();
        d.setEstimatedPoint_x(0);
        d.setEstimatedPoint_y(5);
        service.calculateAngleAndDistanceAndWgsPositionByDataPoint(d);

        double lat = d.getEstimatedLat();
        double lon = d.getEstimatedLon();

//        Assert.assertTrue(lat == 51.196155);
//        Assert.assertTrue(lon == 9.728793);

        // Anderes Szenario:
        // Kartesischer Punkt liegt (im Uhrzeigersinn) um 90 Grad unten, bei 10m Entfernung
        d.setEstimatedPoint_x(-10);
        d.setEstimatedPoint_y(0);
        service.calculateAngleAndDistanceAndWgsPositionByDataPoint(d);

        lat = d.getEstimatedLat();
        lon = d.getEstimatedLon();

//        Assert.assertTrue(lat == 51.19602);
//        Assert.assertTrue(lon == 9.728793);

        // Anderes Szenario:
        // Kartesischer Punkt liegt (im Uhrzeigersinn) um 90 Grad unten, bei 10m Entfernung
        d.setEstimatedPoint_x(5*Math.sqrt(2));
        d.setEstimatedPoint_y(5*Math.sqrt(2));
        service.calculateAngleAndDistanceAndWgsPositionByDataPoint(d);

        lat = d.getEstimatedLat();
        lon = d.getEstimatedLon();

        Assert.assertTrue(lat == 51.196174);
        Assert.assertTrue(lon == 9.728895);
    }
}
