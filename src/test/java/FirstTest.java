import geodesy.GlobalPosition;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luan Hajzeraj on 11/23/2018.
 */
public class FirstTest {
    // Komponente scheint nun korrekt zu rechnen
    @Test
    public void wgsAccelTest() {
        float[] gravityValues = new float[3];
        float[] magneticValues = new float[3];

        gravityValues[0] = -0.106965736f;
        gravityValues[1] = -0.03656099f;
        gravityValues[2] = 9.805999f;

        magneticValues[0] = -7.62f;
        magneticValues[1] = -15.9f;
        magneticValues[2] = -48.96f;

        float[] deviceRelativeAcceleration = new float[4];
        deviceRelativeAcceleration[0] = 0.105348095f;
        deviceRelativeAcceleration[1] = -0.0071828244f;
        deviceRelativeAcceleration[2] = 9.79019f;
        deviceRelativeAcceleration[3] = 0.0f;

        float[] R = new float[16], I = new float[16], earthAcc = new float[16];

        Service.getRotationMatrix(R, I, gravityValues, magneticValues);

        float[] inv = new float[16];
        Service.invertM(inv, 0, R, 0);
        RealVector realVector = Service.multiplyMV(inv, deviceRelativeAcceleration);
        earthAcc[0] = (float) realVector.getEntry(0);
        earthAcc[1] = (float) realVector.getEntry(1);
        earthAcc[2] = (float) realVector.getEntry(2);

        float[] exp = {
                -0.17594706f,
                -0.12206694f,
                9.788417f,0,0,0,0,0,0,0,0,0,0,0,0,0
        };

        Assert.assertArrayEquals(exp, earthAcc, 0.2f);
    }

    // Scheint ebenfalls zu funktionieren
    @Test
    public void cartesianPointTest() {
        GlobalPosition g = new GlobalPosition(51.195922402218514, 9.729042075412195, 443.44000585766383);
        GlobalPosition firstPosition = new GlobalPosition(51.195904161464625, 9.729047495159477, 443.50095560170564);

        double distance = Service.coordinateDistanceBetweenTwoPoints(firstPosition, g);
        double angle = Service.coordinateAngleBetweenTwoPoints(firstPosition, g);

        // handle the first point: distance and angle between firstPoint and firstPoint is 0
        if (distance != 0.0 && angle != 0.0) {
            CartesianPoint cartesianPoint = new CartesianPoint(
                    distance * Math.sin(Math.toRadians(angle)),
                    distance * Math.cos(Math.toRadians(angle))
            );

            Assert.assertEquals(-0.37887556800336547, cartesianPoint.getX(), 0);
            Assert.assertEquals(2.0294623017182998, cartesianPoint.getY(),0);
        }
    }
}
