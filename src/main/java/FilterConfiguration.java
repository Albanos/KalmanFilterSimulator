import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Luan Hajzeraj on 15.12.2018.
 */
public class FilterConfiguration {
    private static FilterConfiguration instance = null;

    private FilterConfiguration() {
    }

    static FilterConfiguration getInstance() {
        if (instance == null) {
            instance = new FilterConfiguration();
        }
        return instance;
    }

    private final Constants constants = Constants.getInstance();
    private final Service2 service = Service2.getInstance();

    private float sigmaAccel;
    private double sigmaGnssSpeed;
    private double sigmaPosAccuracy;
    // Werte für den Vektor des Prozessrauschens G (Verantwortlich für Prozessrauschkovarianz Q)
    private double g1;
    private double g2;
    private double g3;
    private double g4;
    private double rmseLongiDistanceEstGt;
    private double rmseLatiDistanceEstGt;
    private double rmseLongiDistanceGnssGt;
    private double rmseLatiDistanceGnssGt;

    // rmseWert für absoluten Abstand
    private double rmseAbsDistanceEstGt;
    private double rmseAbsDistanceGnssGt;

    // statische Liste für alle möglichen Filter-Konfigurationen
    private LinkedList<FilterConfiguration> allFilterConfigurations = new LinkedList<>();

    // Map mit FilterKonfigurationen als Key und den Simulationsergebnissen in einer List
    private LinkedHashMap<String, ArrayList<FilterConfiguration>> allSimulationsOfOneConfiMap = new LinkedHashMap<>();

    // ============================================================


    public double getSigmaPosAccuracy() {
        return sigmaPosAccuracy;
    }

    public void setSigmaPosAccuracy(double sigmaPosAccuracy) {
        this.sigmaPosAccuracy = sigmaPosAccuracy;
    }

    public double getG1() {
        return g1;
    }

    public void setG1(double g1) {
        this.g1 = g1;
    }

    public double getG2() {
        return g2;
    }

    public void setG2(double g2) {
        this.g2 = g2;
    }

    public double getG3() {
        return g3;
    }

    public void setG3(double g3) {
        this.g3 = g3;
    }

    public double getG4() {
        return g4;
    }

    public void setG4(double g4) {
        this.g4 = g4;
    }

    public LinkedHashMap<String, ArrayList<FilterConfiguration>> getAllSimulationsOfOneConfiMap() {
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

    public LinkedList<FilterConfiguration> getAllFilterConfigurations() {
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
    // FIXME: OLD
//    public void filterSimulation_01_to_50_1_in_01_onlySigmaAccel() {
//        //for (float i = 0.01f; i <= 20.1; i = i + 0.01f) {
//        for (float i = 0.1f; i <= 20.1; i = i + 0.1f) {
//            FilterConfiguration currentConfiguration = new FilterConfiguration();
//            currentConfiguration.setSigmaAccel(i);
//            currentConfiguration.setSigmaGnssSpeed(constants.getSigmaGnssSpeed());
//
//            constants.setSigmaAccel(i);
//            simulateEstimationAndSaveResultInGlobalList(currentConfiguration);
//        }
//    }
    // FIXME: OLD
//    public void filterSimulation_overAllSegments_001_to_20_1_in_001_onlySigmaAccel(String pathToFile) {
//        for(float i = 0.01f; i <= 20.1; i = i + 0.01f) {
//            // ===============================================Lese Segment A ein
//            constants.setCurrentSegment(constants.getSegmentA());
//            //Main.readDataOfFileAndCalculateCartesianPoints(pathToFile);
//            service.setListOfAllDataByGlobalSegment();
//
//            FilterConfiguration conf1 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, constants.getSigmaGnssSpeed());
//
//            // Speichere das Filter-Ergebnis in temp-Liste der Konfis
//            ArrayList<FilterConfiguration> tempListOfCurrentConfi = new ArrayList<>();
//            tempListOfCurrentConfi.add(conf1);
//
//            // ===============================================Lese Segment B ein
//            constants.setCurrentSegment(constants.getSegmentB());
//            //Main.clearAllDataAndReadFileAgain(pathToFile);
//            service.setListOfAllDataByGlobalSegment();
//
//            FilterConfiguration conf2 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, constants.getSigmaGnssSpeed());
//            tempListOfCurrentConfi.add(conf2);
//
//            // ===============================================Lese Segment C ein
//            constants.setCurrentSegment(constants.getSegmentC());
//            //Main.clearAllDataAndReadFileAgain(pathToFile);
//            service.setListOfAllDataByGlobalSegment();
//
//            FilterConfiguration conf3 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, constants.getSigmaGnssSpeed());
//            tempListOfCurrentConfi.add(conf3);
//
//            // ===============================================Lese Segment D ein
//            constants.setCurrentSegment(constants.getSegmentD());
//            //Main.clearAllDataAndReadFileAgain(pathToFile);
//            service.setListOfAllDataByGlobalSegment();
//
//            FilterConfiguration conf4 = simulateEstimationAndGenerateConfigurationAndreturnThem(i, constants.getSigmaGnssSpeed());
//            tempListOfCurrentConfi.add(conf4);
//
//            // Speichere alle Konfis in globaler map
//            allSimulationsOfOneConfiMap.put(
//                    String.valueOf(i).concat("_").concat(String.valueOf(constants.getSigmaGnssSpeed())),
//                    tempListOfCurrentConfi
//            );
//        }
//    }

    // FIXME: Old
//    public void filterSimulation_overAllSegments_001_to_15_1_in_001_onlySigmaSpeed(String pathToFile) {
//        for(double i = 0.1; i <= 15.1; i = i + 0.1) {
//            // ===============================================Lese Segment A ein
//            constants.setCurrentSegment(constants.getSegmentA());
//            Main.readDataOfFileAndCalculateCartesianPoints(pathToFile);
//
//            FilterConfiguration conf1
//                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i);
//
//            // Speichere das Filter-Ergebnis in temp-Liste der Konfis
//            ArrayList<FilterConfiguration> tempListOfCurrentConfi = new ArrayList<>();
//            tempListOfCurrentConfi.add(conf1);
//
//            // ===============================================Lese Segment B ein
//            constants.setCurrentSegment(constants.getSegmentB());
//            //Main.clearAllDataAndReadFileAgain(pathToFile);
//            service.setListOfAllDataByGlobalSegment();
//
//            FilterConfiguration conf2
//                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i);
//            tempListOfCurrentConfi.add(conf2);
//
//            // ===============================================Lese Segment C ein
//            constants.setCurrentSegment(constants.getSegmentC());
//            //Main.clearAllDataAndReadFileAgain(pathToFile);
//            service.setListOfAllDataByGlobalSegment();
//
//            FilterConfiguration conf3
//                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i);
//            tempListOfCurrentConfi.add(conf3);
//
//            // ===============================================Lese Segment D ein
//            constants.setCurrentSegment(constants.getSegmentD());
//            //Main.clearAllDataAndReadFileAgain(pathToFile);
//            service.setListOfAllDataByGlobalSegment();
//
//            FilterConfiguration conf4
//                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i);
//            tempListOfCurrentConfi.add(conf4);
//
//            // Speichere alle Konfis in globaler map
//            allSimulationsOfOneConfiMap.put(
//                    String.valueOf(i).concat("_").concat(String.valueOf(constants.getSigmaGnssSpeed())),
//                    tempListOfCurrentConfi
//            );
//        }
//    }
    public void filterSimulation_overAllSegments_to_20_1_in_001_for_Accel_to_15_1_in_001_for_Speed(boolean withGtAsFakeMeasurement, boolean withVelocityfromStepDetection) {
        for (float i = 0.1f; i <= 20.1; i = i + 0.1f) {
            for (double j = 0.1; j <= 15.1; j = j + 0.1) {
                // Speichere Filter-Ergebnise für jedes Segment in temp-Liste
                ArrayList<FilterConfiguration> allSegmentResultsForCurrentConfi = new ArrayList<>();

                // ===============================================Lese Segment A ein
                constants.setCurrentSegment(constants.getSegmentA());
//                Main.readDataOfFileAndCalculateCartesianPoints(pathToFile);
                service.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf1
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j, withGtAsFakeMeasurement, withVelocityfromStepDetection);

                allSegmentResultsForCurrentConfi.add(conf1);

                // ===============================================Lese Segment B ein
                constants.setCurrentSegment(constants.getSegmentB());
                //Main.clearAllDataAndReadFileAgain(pathToFile);
                service.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf2
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j, withGtAsFakeMeasurement, withVelocityfromStepDetection);
                allSegmentResultsForCurrentConfi.add(conf2);

                // ===============================================Lese Segment C ein
                constants.setCurrentSegment(constants.getSegmentC());
                //Main.clearAllDataAndReadFileAgain(pathToFile);
                service.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf3
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j, withGtAsFakeMeasurement, withVelocityfromStepDetection);
                allSegmentResultsForCurrentConfi.add(conf3);

                // ===============================================Lese Segment D ein
                constants.setCurrentSegment(constants.getSegmentD());
                //Main.clearAllDataAndReadFileAgain(pathToFile);
                service.setListOfAllDataByGlobalSegment();

                FilterConfiguration conf4
                        = simulateEstimationAndGenerateConfigurationAndreturnThem(i, j, withGtAsFakeMeasurement, withVelocityfromStepDetection);
                allSegmentResultsForCurrentConfi.add(conf4);

                // Speichere alle Konfis in globaler map
                allSimulationsOfOneConfiMap.put(
                        String.valueOf(i).concat("_").concat(String.valueOf(constants.getSigmaGnssSpeed())),
                        allSegmentResultsForCurrentConfi
                );
            }
        }
    }

    public void filterSimulation_overAllSegments_simulateAllValues_withoutAccel_by_01_steps(boolean withGtAsFakeMeasurement, boolean withVelocityFromStepDetection) {
        // Für Positions-Genauigkeit (=sigmaPosAcc)
        for (double i = 0.5; i <= 8.5; i = i + 0.5) {
            // Für GNSS-Speed (sigmaGnssSpeed)
            for (double j = 0.5; j <= 5.5; j = j + 0.5) {
                // Für G1 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
                for (double k = 0.5; k <= 4.5; k = k + 0.5) {
                    // Für G2 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
                    for (double l = 0.5; l <= 4.5; l = l + 0.5) {
                        // Für G3 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
                        for (double m = 0.5; m <= 3.5; m = m + 0.5) {
                            // Für G4 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
                            for (double n = 0.5; n <= 3.5; n = n + 0.5) {
                                // Speichere Filter-Ergebnise für jedes Segment in temp-Liste
                                ArrayList<FilterConfiguration> allSegmentResultsForCurrentConfi = new ArrayList<>();

                                // ===============================================Setze Segment A
                                constants.setCurrentSegment(constants.getSegmentA());
                                service.setListOfAllDataByGlobalSegment();

                                FilterConfiguration conf1
                                        = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                                i, j, k, l, m, n, withGtAsFakeMeasurement, withVelocityFromStepDetection
                                );
                                allSegmentResultsForCurrentConfi.add(conf1);

                                // ===============================================Setze Segment B
                                constants.setCurrentSegment(constants.getSegmentB());
                                service.setListOfAllDataByGlobalSegment();

                                FilterConfiguration conf2
                                        = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                        i, j, k, l, m, n, withGtAsFakeMeasurement, withVelocityFromStepDetection
                                );
                                allSegmentResultsForCurrentConfi.add(conf2);

                                // ===============================================Setze Segment C
                                constants.setCurrentSegment(constants.getSegmentC());
                                service.setListOfAllDataByGlobalSegment();

                                FilterConfiguration conf3
                                        = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                        i, j, k, l, m, n, withGtAsFakeMeasurement, withVelocityFromStepDetection
                                );
                                allSegmentResultsForCurrentConfi.add(conf3);

                                // ===============================================Setze Segment D
                                constants.setCurrentSegment(constants.getSegmentD());
                                service.setListOfAllDataByGlobalSegment();

                                FilterConfiguration conf4
                                        = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                        i, j, k, l, m, n, withGtAsFakeMeasurement, withVelocityFromStepDetection
                                );
                                allSegmentResultsForCurrentConfi.add(conf4);

                                // Speichere alle Konfis in globaler map
                                String key = String.valueOf(i).concat("_")
                                        .concat(String.valueOf(j)).concat("_")
                                        .concat(String.valueOf(k)).concat("_")
                                        .concat(String.valueOf(l)).concat("_")
                                        .concat(String.valueOf(m)).concat("_")
                                        .concat(String.valueOf(n));
                                allSimulationsOfOneConfiMap.put(key, allSegmentResultsForCurrentConfi);
                            }
                        }
                    }
                }
            }
        }
    }

    public void filterSimulation_overAllSegments_simulateOnlyVectorG_withoutAccel(boolean withGtAsFakeMeasurement, boolean withVelocityFromStepDetection) {
        // Für G1 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
        for (double i = 22.5; i <= 30.5; i = i + 1) {
            // Für G2 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
            for (double j = 22.5; j <= 30.5; j = j + 1) {
                // Für G3 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
                for (double k = 3.5; k <= 5.5; k = k + 1) {
                    // Für G4 des Prozessrausch-Vektors G (bildet Prozessrauschkovarianz Q)
                    for (double l = 0.5; l <= 2.5; l = l + 1) {
                        // Speichere Filter-Ergebnise für jedes Segment in temp-Liste
                        ArrayList<FilterConfiguration> allSegmentResultsForCurrentConfi = new ArrayList<>();

                        // ===============================================Setze Segment A
                        constants.setCurrentSegment(constants.getSegmentA());
                        service.setListOfAllDataByGlobalSegment();

                        FilterConfiguration conf1
                                = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                constants.getSIGMA_POSITION_ACC(),
                                constants.getSigmaGnssSpeed(),
                                i,
                                j,
                                k,
                                l,
                                withGtAsFakeMeasurement, withVelocityFromStepDetection
                        );
                        allSegmentResultsForCurrentConfi.add(conf1);

                        // ===============================================Setze Segment B
                        constants.setCurrentSegment(constants.getSegmentB());
                        service.setListOfAllDataByGlobalSegment();

                        FilterConfiguration conf2
                                = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                constants.getSIGMA_POSITION_ACC(),
                                constants.getSigmaGnssSpeed(),
                                i,
                                j,
                                k,
                                l,
                                withGtAsFakeMeasurement, withVelocityFromStepDetection
                        );
                        allSegmentResultsForCurrentConfi.add(conf2);

                        // ===============================================Setze Segment C
                        constants.setCurrentSegment(constants.getSegmentC());
                        service.setListOfAllDataByGlobalSegment();

                        FilterConfiguration conf3
                                = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                constants.getSIGMA_POSITION_ACC(),
                                constants.getSigmaGnssSpeed(),
                                i,
                                j,
                                k,
                                l,
                                withGtAsFakeMeasurement, withVelocityFromStepDetection
                        );
                        allSegmentResultsForCurrentConfi.add(conf3);

                        // ===============================================Setze Segment D
                        constants.setCurrentSegment(constants.getSegmentD());
                        service.setListOfAllDataByGlobalSegment();

                        FilterConfiguration conf4
                                = simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                                constants.getSIGMA_POSITION_ACC(),
                                constants.getSigmaGnssSpeed(),
                                i,
                                j,
                                k,
                                l,
                                withGtAsFakeMeasurement, withVelocityFromStepDetection
                        );
                        allSegmentResultsForCurrentConfi.add(conf4);

                        // Speichere alle Konfis in globaler map
                        String key = String.valueOf(constants.getSIGMA_POSITION_ACC()).concat("_")
                                .concat(String.valueOf(constants.getSigmaGnssSpeed())).concat("_")
                                .concat(String.valueOf(i)).concat("_")
                                .concat(String.valueOf(j)).concat("_")
                                .concat(String.valueOf(k)).concat("_")
                                .concat(String.valueOf(l));
                        allSimulationsOfOneConfiMap.put(key, allSegmentResultsForCurrentConfi);
                    }
                }
            }
        }
    }

    public FilterConfiguration findBestConfigurationBySumOfAbsRmse() {
        FilterConfiguration returnConfi = null;
        double oldRmse = 1000000;
        // Iteriere über gesamte Map, anhand der listen (=values)
        for (ArrayList<FilterConfiguration> list : allSimulationsOfOneConfiMap.values()) {
            // Extrahiere alle vier Ergebnisse (immer exakt 4, für 4 Segmente)
            FilterConfiguration confSegA = list.get(0);
            FilterConfiguration confSegB = list.get(1);
            FilterConfiguration confSegC = list.get(2);
            FilterConfiguration confSegD = list.get(3);

            double sumOfAllAbsrmseValues =
                    confSegA.getRmseAbsDistanceEstGt()
                            + confSegB.getRmseAbsDistanceEstGt()
                            + confSegC.getRmseAbsDistanceEstGt()
                            + confSegD.getRmseAbsDistanceEstGt();

            // Wenn Summe kleiner als vorherige Konfi ist diese besser...
            if (sumOfAllAbsrmseValues < oldRmse) {
                // Alle Konfis sind gleich, deshalb egal, ob conf1, conf2,...
                returnConfi = confSegA;
                oldRmse = sumOfAllAbsrmseValues;
            }
        }
        return returnConfi;
    }

    /**
     * Filter-Simulation mit gnssSpeed = 0.1 bis gnssSpeed = 5.1 in 0.1-Schritten
     */
    public void filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed(boolean withGtAsFakeMeasurement, boolean withVelocityfromStepDetection) {
        for (double i = 0.1; i < 15.1; i = i + 0.1) {
            // Speichere Filter-Ergebnise für jedes Segment in temp-Liste
            ArrayList<FilterConfiguration> allSegmentResultsForCurrentConfi = new ArrayList<>();

            // ===============================================Lese Segment A ein
            constants.setCurrentSegment(constants.getSegmentA());
//                Main.readDataOfFileAndCalculateCartesianPoints(pathToFile);
            service.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf1
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i, withGtAsFakeMeasurement, withVelocityfromStepDetection);

            allSegmentResultsForCurrentConfi.add(conf1);

            // ===============================================Lese Segment B ein
            constants.setCurrentSegment(constants.getSegmentB());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            service.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf2
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i, withGtAsFakeMeasurement, withVelocityfromStepDetection);
            allSegmentResultsForCurrentConfi.add(conf2);

            // ===============================================Lese Segment C ein
            constants.setCurrentSegment(constants.getSegmentC());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            service.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf3
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i, withGtAsFakeMeasurement, withVelocityfromStepDetection);
            allSegmentResultsForCurrentConfi.add(conf3);

            // ===============================================Lese Segment D ein
            constants.setCurrentSegment(constants.getSegmentD());
            //Main.clearAllDataAndReadFileAgain(pathToFile);
            service.setListOfAllDataByGlobalSegment();

            FilterConfiguration conf4
                    = simulateEstimationAndGenerateConfigurationAndreturnThem(constants.getSigmaAccel(), i, withGtAsFakeMeasurement, withVelocityfromStepDetection);
            allSegmentResultsForCurrentConfi.add(conf4);

            // Speichere alle Konfis in globaler map
            allSimulationsOfOneConfiMap.put(
                    String.valueOf(i).concat("_").concat(String.valueOf(constants.getSigmaGnssSpeed())),
                    allSegmentResultsForCurrentConfi
            );
        }
    }
    // FIXME: old
//    public void filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed() {
//        //for (double i = 0.01; i <= 15.1; i = i + 0.01) {
//        for (double i = 0.001; i <= 15.1; i = i + 0.001) {
//            FilterConfiguration currentConfiguration = new FilterConfiguration();
//            currentConfiguration.setSigmaAccel(constants.getSigmaAccel());
//            currentConfiguration.setSigmaGnssSpeed(i);
//
//            constants.setSigmaGnssSpeed(i);
//            simulateEstimationAndSaveResultInGlobalList(currentConfiguration);
//        }
//    }

    /**
     * Filter-Simulation mit sigmaAccel = 0.1 bis sigmaAccel = 30.1 in 0.1-Schritten und gnssSpeed = 0.1 bis
     * gnssSpeed = 5.1 in 0.1-Schritten als jeweilige Kombinationen
     */
    // FIXME: OLD
//    public void filterSimulation_01_to_50_1_in_01_forSigmaAccel_and_01_to_15_1_in_01_forSigmaGnssSpeed() {
//        //for (float i = 0.01f; i <= 20.1; i = i + 0.01f) {
//        for (float i = 0.1f; i <= 20.1; i = i + 0.1f) {
//            //for (double j = 0.01; j <= 15.1; j = j + 0.01) {
//            for (double j = 0.1; j <= 15.1; j = j + 0.1) {
//                FilterConfiguration currentConfiguration = new FilterConfiguration();
//                currentConfiguration.setSigmaAccel(i);
//                currentConfiguration.setSigmaGnssSpeed(j);
//
//                constants.setSigmaAccel(i);
//                constants.setSigmaGnssSpeed(j);
//                simulateEstimationAndSaveResultInGlobalList(currentConfiguration, true);
//            }
//        }
//    }
    private void simulateEstimationAndSaveResultInGlobalList(FilterConfiguration currentConfiguration, boolean withGtAsFakeMeasurement, boolean withVelocityfromStepDetection) {
        System.out.println("=====================Beginn, simulation mit sigmaAccel:  "
                + currentConfiguration.getSigmaAccel() + " & sigmaSpeed:  "
                + currentConfiguration.getSigmaGnssSpeed());

        EstimationFilter2 filter = new EstimationFilter2();
        filter.makeEstimation(withGtAsFakeMeasurement, withVelocityfromStepDetection);

        Map<String, Double> allRmseValues = service.calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts();

        currentConfiguration.setRmseLatiDistanceEstGt(allRmseValues.get("latiEstGt"));
        currentConfiguration.setRmseLongiDistanceEstGt(allRmseValues.get("longiEstGt"));
        currentConfiguration.setRmseLatiDistanceGnssGt(allRmseValues.get("latiGnssGt"));
        currentConfiguration.setRmseLongiDistanceGnssGt(allRmseValues.get("longiGnssGt"));
        currentConfiguration.setRmseAbsDistanceEstGt(allRmseValues.get("absEstGt"));
        currentConfiguration.setRmseAbsDistanceGnssGt(allRmseValues.get("absGnssGt"));

        allFilterConfigurations.add(currentConfiguration);
    }

    public FilterConfiguration simulateEstimationAndGenerateConfigurationAndreturnThem(float sigmaAccel, double sigmaSpeed, boolean withGtAsFakeMeasurement, boolean withVelocityfromStepDetection) {
        FilterConfiguration currentConfiguration = new FilterConfiguration();
        currentConfiguration.setSigmaAccel(sigmaAccel);
        currentConfiguration.setSigmaGnssSpeed(sigmaSpeed);

        constants.setSigmaAccel(sigmaAccel);
        constants.setSigmaGnssSpeed(sigmaSpeed);
        // Führe Simulation durch
        System.out.println("=====================Beginn, simulation mit sigmaAccel:  "
                + sigmaAccel + " & sigmaSpeed:  " + sigmaSpeed);

        EstimationFilter2 filter = new EstimationFilter2();
        filter.makeEstimation(withGtAsFakeMeasurement, withVelocityfromStepDetection);

        Map<String, Double> allRmseValues = service.calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts();

        currentConfiguration.setRmseLatiDistanceEstGt(allRmseValues.get("latiEstGt"));
        currentConfiguration.setRmseLongiDistanceEstGt(allRmseValues.get("longiEstGt"));
        currentConfiguration.setRmseLatiDistanceGnssGt(allRmseValues.get("latiGnssGt"));
        currentConfiguration.setRmseLongiDistanceGnssGt(allRmseValues.get("longiGnssGt"));
        currentConfiguration.setRmseAbsDistanceEstGt(allRmseValues.get("absEstGt"));
        currentConfiguration.setRmseAbsDistanceGnssGt(allRmseValues.get("absGnssGt"));

        return currentConfiguration;
    }

    public FilterConfiguration simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
            double sigmaPosAccuracy,
            double sigmaGnssSpeed,
            double g1,
            double g2,
            double g3,
            double g4,
            boolean withGtAsFakeMeasurement,
            boolean withVelocityfromStepDetection
    ) {
        FilterConfiguration currentConfiguration = new FilterConfiguration();
        currentConfiguration.setSigmaGnssSpeed(sigmaGnssSpeed);
        currentConfiguration.setSigmaPosAccuracy(sigmaPosAccuracy);
        currentConfiguration.setG1(g1);
        currentConfiguration.setG2(g2);
        currentConfiguration.setG3(g3);
        currentConfiguration.setG4(g4);

        constants.setSigmaGnssSpeed(sigmaGnssSpeed);
        constants.setSIGMA_POSITION_ACC(sigmaPosAccuracy);
        constants.setG1(g1);
        constants.setG2(g2);
        constants.setG3(g3);
        constants.setG4(g4);

        // Führe Simulation durch
        System.out.println("=====================Beginn, simulation mit\n " +
                "sigmaGnssSpeed:  " + sigmaGnssSpeed + "\n"
                + "sigmaPosAccuracy:  " + sigmaPosAccuracy + "\n"
                + "G1:  " + g1 + "\n"
                + "G2:  " + g2 + "\n"
                + "G3:  " + g3 + "\n"
                + "G4:  " + g4 + "\n");

        EstimationFilter2 currentFilter = new EstimationFilter2();
        currentFilter.makeEstimation(withGtAsFakeMeasurement, withVelocityfromStepDetection);

        Map<String, Double> allRmseValues = service.calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts();

        currentConfiguration.setRmseLatiDistanceEstGt(allRmseValues.get("latiEstGt"));
        currentConfiguration.setRmseLongiDistanceEstGt(allRmseValues.get("longiEstGt"));
        currentConfiguration.setRmseLatiDistanceGnssGt(allRmseValues.get("latiGnssGt"));
        currentConfiguration.setRmseLongiDistanceGnssGt(allRmseValues.get("longiGnssGt"));
        currentConfiguration.setRmseAbsDistanceEstGt(allRmseValues.get("absEstGt"));
        currentConfiguration.setRmseAbsDistanceGnssGt(allRmseValues.get("absGnssGt"));

        return currentConfiguration;
    }

    public FilterConfiguration findConfigurationWithMinimalLatiRmseOfEstPoints() {
        // Wir müssen natürlich nur die Est-Rmse-Werte betrachten, da Gnss gleich bleiben
        double latiRmse = 1000;

        FilterConfiguration optConfiguration = null;
        for (FilterConfiguration fc : allFilterConfigurations) {
            if (fc.getRmseLatiDistanceEstGt() < latiRmse) {
                optConfiguration = fc;
                latiRmse = fc.getRmseLatiDistanceEstGt();
            }
        }
        return optConfiguration;
    }

    public FilterConfiguration findConfigurationWithMinimalLongiRmseOfEstPoints() {
        // Wir müssen natürlich nur die Est-Rmse-Werte betrachten, da Gnss gleich bleiben
        double longiRmse = 1000;

        FilterConfiguration optConfiguration = null;
        for (FilterConfiguration fc : allFilterConfigurations) {
            if (fc.getRmseLongiDistanceEstGt() < longiRmse) {
                optConfiguration = fc;
                longiRmse = fc.getRmseLongiDistanceEstGt();
            }
        }
        return optConfiguration;
    }

    public FilterConfiguration findConfigurationWithMinimalLatiAndLongiRmseOfEstPoints() {
        // Wir müssen natürlich nur die Est-Rmse-Werte betrachten, da Gnss gleich bleiben
        double latirmse = 1000;
        double longiRmse = 1000;

        FilterConfiguration optConfiguration = null;
        for (FilterConfiguration fc : allFilterConfigurations) {
            if (fc.getRmseLongiDistanceEstGt() < longiRmse && fc.getRmseLatiDistanceEstGt() < latirmse) {
                optConfiguration = fc;
                longiRmse = fc.getRmseLongiDistanceEstGt();
                latirmse = fc.getRmseLatiDistanceEstGt();
            }
        }
        return optConfiguration;
    }
}
