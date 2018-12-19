import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 15.12.2018.
 */
public class FilterConfiguration {
    private float sigmaAccel;
    private double sigmaGnssSpeed;
    private double rmseLongiDistanceEstGt;
    private double rmseLatiDistanceEstGt;
    private double rmseLongiDistanceGnssGt;
    private double rmseLatiDistanceGnssGt;

    // statische Liste für alle möglichen Filter-Konfigurationen
    private static LinkedList<FilterConfiguration> allFilterConfigurations = new LinkedList<>();

    // ============================================================


    public static LinkedList<FilterConfiguration> getAllFilterConfigurations() {
        return allFilterConfigurations;
    }

    public float getSigmaAccel() {
        return sigmaAccel;
    }

    public void setSigmaAccel(float sigmaAccel) {
        this.sigmaAccel = sigmaAccel;
    }

    public double getSigmaGnssSpeed() {
        return sigmaGnssSpeed;
    }

    public void setSigmaGnssSpeed(double sigmaGnssSpeed) {
        this.sigmaGnssSpeed = sigmaGnssSpeed;
    }

    public double getRmseLongiDistanceEstGt() {
        return rmseLongiDistanceEstGt;
    }

    public void setRmseLongiDistanceEstGt(double rmseLongiDistanceEstGt) {
        this.rmseLongiDistanceEstGt = rmseLongiDistanceEstGt;
    }

    public double getRmseLatiDistanceEstGt() {
        return rmseLatiDistanceEstGt;
    }

    public void setRmseLatiDistanceEstGt(double rmseLatiDistanceEstGt) {
        this.rmseLatiDistanceEstGt = rmseLatiDistanceEstGt;
    }

    public double getRmseLongiDistanceGnssGt() {
        return rmseLongiDistanceGnssGt;
    }

    public void setRmseLongiDistanceGnssGt(double rmseLongiDistanceGnssGt) {
        this.rmseLongiDistanceGnssGt = rmseLongiDistanceGnssGt;
    }

    public double getRmseLatiDistanceGnssGt() {
        return rmseLatiDistanceGnssGt;
    }

    public void setRmseLatiDistanceGnssGt(double rmseLatiDistanceGnssGt) {
        this.rmseLatiDistanceGnssGt = rmseLatiDistanceGnssGt;
    }

    /**
     * Filter-Simulation mit sigmaAccel = 0.1 bis sigmaAccel = 50.1 in 0.1-Schritten
     */
    public void filterSimulation_01_to_50_1_in_01_onlySigmaAccel() {
        //for (float i = 0.01f; i <= 50.1; i = i + 0.01f) {
        for (float i = 0.1f; i <= 20.1; i = i + 0.1f) {
            FilterConfiguration currentConfiguration = new FilterConfiguration();
            currentConfiguration.setSigmaAccel(i);
            currentConfiguration.setSigmaGnssSpeed(Constants.getSigmaGnssSpeed());

            Constants.setSigmaAccel(i);
            simulateEstimationAndSaveResultInGlobalList(currentConfiguration);
        }
    }

    /**
     * Filter-Simulation mit gnssSpeed = 0.1 bis gnssSpeed = 5.1 in 0.1-Schritten
     */
    public void filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed() {
        //for (double i = 0.01; i <= 15.1; i = i + 0.01) {
        for (double i = 0.1; i <= 15.1; i = i + 0.1) {
            FilterConfiguration currentConfiguration = new FilterConfiguration();
            currentConfiguration.setSigmaAccel(Constants.getSigmaAccel());
            currentConfiguration.setSigmaGnssSpeed(i);

            Constants.setSigmaGnssSpeed(i);
            simulateEstimationAndSaveResultInGlobalList(currentConfiguration);
        }
    }

    /**
     * Filter-Simulation mit sigmaAccel = 0.1 bis sigmaAccel = 30.1 in 0.1-Schritten und gnssSpeed = 0.1 bis
     * gnssSpeed = 5.1 in 0.1-Schritten als jeweilige Kombinationen
     */
    public void filterSimulation_01_to_50_1_in_01_forSigmaAccel_and_01_to_15_1_in_01_forSigmaGnssSpeed() {
        //for (float i = 0.01f; i <= 50.1; i = i + 0.01f) {
        for (float i = 0.1f; i <= 20.1; i = i + 0.1f) {
            //for (double j = 0.01; j <= 15.1; j = j + 0.01) {
            for (double j = 0.1; j <= 15.1; j = j + 0.1) {
                FilterConfiguration currentConfiguration = new FilterConfiguration();
                currentConfiguration.setSigmaAccel(i);
                currentConfiguration.setSigmaGnssSpeed(j);

                Constants.setSigmaAccel(i);
                Constants.setSigmaGnssSpeed(j);
                simulateEstimationAndSaveResultInGlobalList(currentConfiguration);
            }
        }
    }

    private void simulateEstimationAndSaveResultInGlobalList(FilterConfiguration currentConfiguration) {
        System.out.println("=====================Beginn, simulation mit sigmaAccel:  "
                + currentConfiguration.getSigmaAccel() + " & sigmaSpeed:  "
                + currentConfiguration.getSigmaGnssSpeed());

        EstimationFilter2 filter = new EstimationFilter2();
        filter.makeEstimation();

        Service2.calculateRMSEFor10Hearts();

        currentConfiguration.setRmseLatiDistanceEstGt(Service2.getRmseLatiEstGt());
        currentConfiguration.setRmseLongiDistanceEstGt(Service2.getRmseLongiEstGt());
        currentConfiguration.setRmseLatiDistanceGnssGt(Service2.getRmseLatiGnssGt());
        currentConfiguration.setRmseLongiDistanceGnssGt(Service2.getRmseLongiGnssGt());

        FilterConfiguration.getAllFilterConfigurations().add(currentConfiguration);
    }

    public static FilterConfiguration findConfigurationWithMinimalLatiRmse() {
        // Wir müssen natürlich nur die Est-Rmse-Werte betrachten, da Gnss gleich bleiben
        double latiRmse = 1000;

        FilterConfiguration optConfiguration = null;
        for (FilterConfiguration fc : FilterConfiguration.getAllFilterConfigurations()) {
            if (fc.getRmseLatiDistanceEstGt() < latiRmse) {
                optConfiguration = fc;
                latiRmse = fc.getRmseLatiDistanceEstGt();
            }
        }
        return optConfiguration;
    }

    public static FilterConfiguration findConfigurationWithMinimalLongiRmse() {
        // Wir müssen natürlich nur die Est-Rmse-Werte betrachten, da Gnss gleich bleiben
        double longiRmse = 1000;

        FilterConfiguration optConfiguration = null;
        for (FilterConfiguration fc : FilterConfiguration.getAllFilterConfigurations()) {
            if (fc.getRmseLongiDistanceEstGt() < longiRmse) {
                optConfiguration = fc;
                longiRmse = fc.getRmseLongiDistanceEstGt();
            }
        }
        return optConfiguration;
    }

    public static FilterConfiguration findConfigurationWithMinimalLatiAndLongiRmse() {
        // Wir müssen natürlich nur die Est-Rmse-Werte betrachten, da Gnss gleich bleiben
        double latirmse = 1000;
        double longiRmse = 1000;

        FilterConfiguration optConfiguration = null;
        for(FilterConfiguration fc : FilterConfiguration.getAllFilterConfigurations()) {
            if(fc.getRmseLongiDistanceEstGt() < longiRmse && fc.getRmseLatiDistanceEstGt() < latirmse) {
                optConfiguration = fc;
                longiRmse = fc.getRmseLongiDistanceEstGt();
                latirmse = fc.getRmseLatiDistanceEstGt();
            }
        }
        return optConfiguration;
    }
}
