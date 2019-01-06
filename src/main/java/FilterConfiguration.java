import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    // rmseWert für absoluten Abstand
    private double rmseAbsDistanceEstGt;
    private double rmseAbsDistanceGnssGt;

    // statische Liste für alle möglichen Filter-Konfigurationen
    private static LinkedList<FilterConfiguration> allFilterConfigurations = new LinkedList<>();

    // Map mit FilterKonfigurationen als Key und den Simulationsergebnissen in einer List
    private static LinkedHashMap<String, ArrayList<FilterConfiguration>> allSimulationsOfOneConfiMap = new LinkedHashMap<>();

    // ============================================================


    public static LinkedHashMap<String, ArrayList<FilterConfiguration>> getAllSimulationsOfOneConfiMap() {
        return allSimulationsOfOneConfiMap;
    }

    public double getRmseAbsDistanceEstGt() {
        return rmseAbsDistanceEstGt;
    }

    public void setRmseAbsDistanceEstGt(double rmseAbsDistanceEstGt) {
        this.rmseAbsDistanceEstGt = rmseAbsDistanceEstGt;
    }

    public double getRmseAbsDistanceGnssGt() {
        return rmseAbsDistanceGnssGt;
    }

    public void setRmseAbsDistanceGnssGt(double rmseAbsDistanceGnssGt) {
        this.rmseAbsDistanceGnssGt = rmseAbsDistanceGnssGt;
    }

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
        //for (float i = 0.01f; i <= 20.1; i = i + 0.01f) {
        for (float i = 0.1f; i <= 20.1; i = i + 0.1f) {
            FilterConfiguration currentConfiguration = new FilterConfiguration();
            currentConfiguration.setSigmaAccel(i);
            currentConfiguration.setSigmaGnssSpeed(Constants.getSigmaGnssSpeed());

            Constants.setSigmaAccel(i);
            simulateEstimationAndSaveResultInGlobalList(currentConfiguration);
        }
    }

    public void filterSimulation_overAllSegments_001_to_20_1_in_001_onlySigmaAccel(String pathToFile) {
        for(float i = 0.01f; i <= 20.1; i = i + 0.01f) {
            // ===============================================Lese Segment A ein
            Constants.setCurrentSegment(Constants.getSegmentA());
            //Main.readDataOfFileAndCalculateCartesianPoints(pathToFile);
            Constants.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf1 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, Constants.getSigmaGnssSpeed());

            // Speichere das Filter-Ergebnis in temp-Liste der Konfis
            ArrayList<FilterConfiguration> tempListOfCurrentConfi = new ArrayList<>();
            tempListOfCurrentConfi.add(conf1);

            // ===============================================Lese Segment B ein
            Constants.setCurrentSegment(Constants.getSegmentB());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            Constants.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf2 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, Constants.getSigmaGnssSpeed());
            tempListOfCurrentConfi.add(conf2);

            // ===============================================Lese Segment C ein
            Constants.setCurrentSegment(Constants.getSegmentC());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            Constants.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf3 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, Constants.getSigmaGnssSpeed());
            tempListOfCurrentConfi.add(conf3);

            // ===============================================Lese Segment D ein
            Constants.setCurrentSegment(Constants.getSegmentD());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            Constants.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf4 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, Constants.getSigmaGnssSpeed());
            tempListOfCurrentConfi.add(conf4);

            // Speichere alle Konfis in globaler map
            allSimulationsOfOneConfiMap.put(
                    String.valueOf(i).concat("_").concat(String.valueOf(Constants.getSigmaGnssSpeed())),
                    tempListOfCurrentConfi
            );
        }
    }

    public void filterSimulation_overAllSegments_001_to_15_1_in_001_onlySigmaSpeed(String pathToFile) {
        for(double i = 0.1; i <= 15.1; i = i + 0.1) {
            // ===============================================Lese Segment A ein
            Constants.setCurrentSegment(Constants.getSegmentA());
            Main.readDataOfFileAndCalculateCartesianPoints(pathToFile);

            FilterConfiguration conf1
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(Constants.getSigmaAccel(), i);

            // Speichere das Filter-Ergebnis in temp-Liste der Konfis
            ArrayList<FilterConfiguration> tempListOfCurrentConfi = new ArrayList<>();
            tempListOfCurrentConfi.add(conf1);

            // ===============================================Lese Segment B ein
            Constants.setCurrentSegment(Constants.getSegmentB());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            Constants.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf2
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(Constants.getSigmaAccel(), i);
            tempListOfCurrentConfi.add(conf2);

            // ===============================================Lese Segment C ein
            Constants.setCurrentSegment(Constants.getSegmentC());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            Constants.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf3
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(Constants.getSigmaAccel(), i);
            tempListOfCurrentConfi.add(conf3);

            // ===============================================Lese Segment D ein
            Constants.setCurrentSegment(Constants.getSegmentD());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            Constants.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf4
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(Constants.getSigmaAccel(), i);
            tempListOfCurrentConfi.add(conf4);

            // Speichere alle Konfis in globaler map
            allSimulationsOfOneConfiMap.put(
                    String.valueOf(i).concat("_").concat(String.valueOf(Constants.getSigmaGnssSpeed())),
                    tempListOfCurrentConfi
            );
        }
    }

    public void filterSimulation_overAllSegments_to_20_1_in_001_for_Accel_to_15_1_in_001_for_Speed(String pathToFile) {
        for(float i = 0.1f; i <= 20.1; i = i + 0.1f) {
            for(double j = 0.1; j <= 15.1; j = j + 0.1) {
                // ===============================================Lese Segment A ein
                Constants.setCurrentSegment(Constants.getSegmentA());
//                Main.readDataOfFileAndCalculateCartesianPoints(pathToFile);
                Constants.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf1
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j);

                // Speichere das Filter-Ergebnis in temp-Liste der Konfis
                ArrayList<FilterConfiguration> tempListOfCurrentConfi = new ArrayList<>();
                tempListOfCurrentConfi.add(conf1);

                // ===============================================Lese Segment B ein
                Constants.setCurrentSegment(Constants.getSegmentB());
                //Main.clearAllDataAndReadFileAgain(pathToFile);
                Constants.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf2
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j);
                tempListOfCurrentConfi.add(conf2);

                // ===============================================Lese Segment C ein
                Constants.setCurrentSegment(Constants.getSegmentC());
                //Main.clearAllDataAndReadFileAgain(pathToFile);
                Constants.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf3
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j);
                tempListOfCurrentConfi.add(conf3);

                // ===============================================Lese Segment D ein
                Constants.setCurrentSegment(Constants.getSegmentD());
                //Main.clearAllDataAndReadFileAgain(pathToFile);
                Constants.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf4
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j);
                tempListOfCurrentConfi.add(conf4);

                // Speichere alle Konfis in globaler map
                allSimulationsOfOneConfiMap.put(
                        String.valueOf(i).concat("_").concat(String.valueOf(Constants.getSigmaGnssSpeed())),
                        tempListOfCurrentConfi
                );
            }
        }
    }

    public FilterConfiguration findBestConfigurationBySumOfAbsRmse() {
        FilterConfiguration returnConfi = null;
        double oldRmse = 1000000;
        // Iteriere über gesamte Map, anhand der listen (=values)
        for(ArrayList<FilterConfiguration> list : FilterConfiguration.allSimulationsOfOneConfiMap.values()) {
            // Extrahiere alle vier Ergebnisse (immer exakt 4, für 4 Segmente)
            FilterConfiguration conf1 = list.get(0);
            FilterConfiguration conf2 = list.get(1);
            FilterConfiguration conf3 = list.get(2);
            FilterConfiguration conf4 = list.get(3);

            double sumOfAllAbsrmseValues =
                    conf1.getRmseAbsDistanceEstGt()
                            + conf2.getRmseAbsDistanceEstGt()
                            + conf3.getRmseAbsDistanceEstGt()
                            + conf4.getRmseAbsDistanceEstGt();

            // Wenn Summe kleiner als vorherige Konfi ist diese besser...
            if(sumOfAllAbsrmseValues < oldRmse) {
                // Alle Konfis sind gleich, deshalb egal, ob conf1, conf2,...
                returnConfi = conf1;
                oldRmse = sumOfAllAbsrmseValues;
            }
        }
        return returnConfi;
    }

    /**
     * Filter-Simulation mit gnssSpeed = 0.1 bis gnssSpeed = 5.1 in 0.1-Schritten
     */
    public void filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed() {
        //for (double i = 0.01; i <= 15.1; i = i + 0.01) {
        for (double i = 0.001; i <= 15.1; i = i + 0.001) {
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
        //for (float i = 0.01f; i <= 20.1; i = i + 0.01f) {
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

        Service2.calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts();

        currentConfiguration.setRmseLatiDistanceEstGt(Service2.getRmseLatiEstGt());
        currentConfiguration.setRmseLongiDistanceEstGt(Service2.getRmseLongiEstGt());
        currentConfiguration.setRmseLatiDistanceGnssGt(Service2.getRmseLatiGnssGt());
        currentConfiguration.setRmseLongiDistanceGnssGt(Service2.getRmseLongiGnssGt());

        FilterConfiguration.getAllFilterConfigurations().add(currentConfiguration);
    }

    private FilterConfiguration simulateEstimationAndGenerateConfigurationAndreturnThem(float sigmaAccel, double sigmaSpeed) {
        FilterConfiguration currentConfiguration = new FilterConfiguration();
        currentConfiguration.setSigmaAccel(sigmaAccel);
        currentConfiguration.setSigmaGnssSpeed(sigmaSpeed);

        Constants.setSigmaAccel(sigmaAccel);
        // Führe Simulation durch
        System.out.println("=====================Beginn, simulation mit sigmaAccel:  "
                + currentConfiguration.getSigmaAccel() + " & sigmaSpeed:  "
                + currentConfiguration.getSigmaGnssSpeed());

        EstimationFilter2 filter = new EstimationFilter2();
        filter.makeEstimation();

        Service2.calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts();

        currentConfiguration.setRmseLatiDistanceEstGt(Service2.getRmseLatiEstGt());
        currentConfiguration.setRmseLongiDistanceEstGt(Service2.getRmseLongiEstGt());
        currentConfiguration.setRmseLatiDistanceGnssGt(Service2.getRmseLatiGnssGt());
        currentConfiguration.setRmseLongiDistanceGnssGt(Service2.getRmseLongiGnssGt());
        currentConfiguration.setRmseAbsDistanceEstGt(Service2.getRmseAbsoluteDistanceEstGt());
        currentConfiguration.setRmseAbsDistanceGnssGt(Service2.getRmseAbsoluteDistanceGnssGt());

        return currentConfiguration;
    }

    public static FilterConfiguration findConfigurationWithMinimalLatiRmseOfEstPoints() {
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

    public static FilterConfiguration findConfigurationWithMinimalLongiRmseOfEstPoints() {
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

    public static FilterConfiguration findConfigurationWithMinimalLatiAndLongiRmseOfEstPoints() {
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
