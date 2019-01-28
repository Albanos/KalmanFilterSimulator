/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
    private static final Constants constants = Constants.getInstance();
    private static final Service2 service = Service2.getInstance();
    private static final CsvReader csvReader = CsvReader.getInstance();
    private static final FilterConfiguration filterConfiguration = FilterConfiguration.getInstance();

    private static final String pathToNexus6File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\nexus6_ownFOrmat_withGT_11_11_38.csv";

    private static final String pathToNexus6File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\nexus6_ownFormat_withGT_11_19_10.csv";

    private static final String pathToS7File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\S7Edge_ownFormat_withGT_11_51_42.csv";

    private static final String pathToNexus5File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus5-messung-21_2017-06-01_09-56-40.0_formatted_addedColumns.csv";

    private static final String pathToNexus5File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus5-messung-210_2017-06-01_11-21-25.0_formatted_addedColumns.csv";

    public static void main(String[] args) {
        readAllSegmentsFromCsv(pathToNexus5File2);
//        double sumOfAllRmseValuesEstGt = 0;
//        double sumOfAllRmseValuesGnssGt = 0;
//        // Segment A
//        constants.setCurrentSegment(constants.getSegmentA());
//        service.setListOfAllDataByGlobalSegment();
//
//        FilterConfiguration startConf
//                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
//                5.0,
//                3.0,
//                4.5,
//                4.5,
//                3.5,
//                0.5, true);
//        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
//        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();
//
//        ExcelFileCreator2 creator = new ExcelFileCreator2();
//        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
//        service.writeAllDataToVikingFile(constants.getCurrentSegment());
//
//        // Segment B
//        constants.setCurrentSegment(constants.getSegmentB());
//        service.setListOfAllDataByGlobalSegment();
//
//        startConf
//                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
//                5.0,
//                3.0,
//                4.5,
//                4.5,
//                3.5,
//                0.5, true);
//        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
//        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();
//
//        creator = new ExcelFileCreator2();
//        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
//        service.writeAllDataToVikingFile(constants.getCurrentSegment());
//
//        // Segment C
//        constants.setCurrentSegment(constants.getSegmentC());
//        service.setListOfAllDataByGlobalSegment();
//
//        startConf
//                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
//                5.0,
//                3.0,
//                4.5,
//                4.5,
//                3.5,
//                0.5, true);
//        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
//        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();
//
//        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
//        service.writeAllDataToVikingFile(constants.getCurrentSegment());
//
//        // Segment D
//        constants.setCurrentSegment(constants.getSegmentD());
//        service.setListOfAllDataByGlobalSegment();
//
//        startConf
//                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
//                5.0,
//                3.0,
//                4.5,
//                4.5,
//                3.5,
//                0.5, true);
//        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
//        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();
//
//        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
//        service.writeAllDataToVikingFile(constants.getCurrentSegment());
//        System.out.println("Summe der RMSE-Werte, Est_GT:  " + sumOfAllRmseValuesEstGt);
//        System.out.println("Summe der RMSE-Werte, GNSS_GT:  " + sumOfAllRmseValuesGnssGt);



        FilterConfiguration bestConfi = simulateFilterForAllSegmentsAndFindBestConfi(false);
        System.out.println("Beste Kofi:\n" +
                "sigmaGnssSpeed:  " + bestConfi.getSigmaGnssSpeed() + "\n"
                + "sigmaPosAccuracy:  " + bestConfi.getSigmaPosAccuracy() + "\n"
                + "G1:  " + bestConfi.getG1() + "\n"
                + "G2:  " + bestConfi.getG2() + "\n"
                + "G3:  " + bestConfi.getG3() + "\n"
                + "G4:  " + bestConfi.getG4()
        );


        System.out.println("Hi");
    }

    private static void readAllSegmentsFromCsv(String pathToFile) {
        // Befülle die globale map mit allen segmenten einmalig
        csvReader.readSegmentFromCsv(pathToFile, constants.getSegmentA());
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentA());

        csvReader.readSegmentFromCsv(pathToFile, constants.getSegmentB());
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentB());

        csvReader.readSegmentFromCsv(pathToFile, constants.getSegmentC());
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentC());

        csvReader.readSegmentFromCsv(pathToFile, constants.getSegmentD());
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentD());
    }

    private static FilterConfiguration simulateFilterForAllSegmentsAndFindBestConfi(boolean withGtAsFakeMeasurement) {
        // Simuliere alle Konfigurationen:
        //filterConfiguration.filterSimulation_overAllSegments_to_20_1_in_001_for_Accel_to_15_1_in_001_for_Speed(withGtAsFakeMeasurement);
        //filterConfiguration.filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed(withGtAsFakeMeasurement);
        filterConfiguration.filterSimulation_overAllSegments_simulateAllValues_withoutAccel_by_01_steps(withGtAsFakeMeasurement);
        //filterConfiguration.filterSimulation_overAllSegments_simulateOnlyVectorG_withoutAccel(withGtAsFakeMeasurement);

        // Gebe beste Konfiguration zurück
        return filterConfiguration.findBestConfigurationBySumOfAbsRmse();
    }

    /**
     * Simuliert Filter ohne Accel zu variieren und sucht dann beste Konfi anahnd von lati/longi-
     * Rmse. Daten werden in eine File geschrieben
     */
    // FIXME: old
//    private static void makeCompleteFilterSimulationWithoutAccelFindBestKonfiOnLatiLongiRmseSimulateWithThemAndWriteInFile() {
//        makeCompleteFilterSimulationWithoutAccelClearAllDataAndReadFileAgain();
//
//        // Extrahiere die Filter-Konfi, wo lati und longi-RMSE minimal sind
//        FilterConfiguration configurationWithMinimalLatiAndLongiRmse = filterConfiguration.findConfigurationWithMinimalLatiAndLongiRmseOfEstPoints();
//        FilterConfiguration configurationWithMinimalLongiRmseOfEstPoints = filterConfiguration.findConfigurationWithMinimalLongiRmseOfEstPoints();
//        FilterConfiguration configurationWithMinimalLatiRmseOfEstPoints = filterConfiguration.findConfigurationWithMinimalLatiRmseOfEstPoints();
//
//        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
//        //clearAllDataAndReadFileAgain(pathToNexus6File2);
//
//        // Simuliere noch einmal in der Ausgangs-Konfi und exportiere
//        //simulateFilerWithSpecificParametersWithExcelAndVikExport(8f, 0.5);
//
//        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
//        //clearAllDataAndReadFileAgain(pathToNexus6File2);
//
//        // simuliere mit minimalem Longi-Abstand
//        simulateFilerWithSpecificParametersWithExcelAndVikExport(
//                configurationWithMinimalLongiRmseOfEstPoints.getSigmaAccel(),
//                configurationWithMinimalLongiRmseOfEstPoints.getSigmaGnssSpeed()
//        );
//
//        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
//        //clearAllDataAndReadFileAgain(pathToNexus6File2);
//
//        // simuliere mit minimalem lati-Abstand
//        simulateFilerWithSpecificParametersWithExcelAndVikExport(
//                configurationWithMinimalLatiRmseOfEstPoints.getSigmaAccel(),
//                configurationWithMinimalLatiRmseOfEstPoints.getSigmaGnssSpeed()
//        );
//
//        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
//        //clearAllDataAndReadFileAgain(pathToNexus6File2);
//
//        // Nehme die Konfiguration von configurationWithMinimalLatiAndLongiRmse, simuliere erneut und exportiere
//        simulateFilerWithSpecificParametersWithExcelAndVikExport(
//                configurationWithMinimalLatiAndLongiRmse.getSigmaAccel(),
//                configurationWithMinimalLatiAndLongiRmse.getSigmaGnssSpeed());
//
//        System.out.println("Filter-Konfi mit minimalem Longi:\nsigmaAccel:  "
//                + configurationWithMinimalLongiRmseOfEstPoints.getSigmaAccel() + " und sigmaSpeed:  "
//                + configurationWithMinimalLongiRmseOfEstPoints.getSigmaGnssSpeed());
//
//        System.out.println("Filter-Konfi mit minimalem Lati:\nsigmaAccel:  "
//                + configurationWithMinimalLatiRmseOfEstPoints.getSigmaAccel() + " und sigmaSpeed:  "
//                + configurationWithMinimalLatiRmseOfEstPoints.getSigmaGnssSpeed());
//
//        System.out.println("Optimale Filter-Konfi:\nsigmaAccel:  "
//                + configurationWithMinimalLatiAndLongiRmse.getSigmaAccel() + " und sigmaSpeed:  "
//                + configurationWithMinimalLatiAndLongiRmse.getSigmaGnssSpeed());
//
//        ExcelFileCreator2 creator2 = new ExcelFileCreator2();
//        creator2.writeDataToFile();
//
//        service.writeAllDataToVikingFile();
//    }

    // FIXME: old
//    private static void makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(float sigmaAccel, double sigmaSpeed) {
//        constants.setSigmaAccel(sigmaAccel);
//        constants.setSigmaGnssSpeed(sigmaSpeed);
//        EstimationFilter2 filter = new EstimationFilter2();
//        filter.makeEstimation();
//
//        service.calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts();
//
//        ExcelFileCreator2 creator = new ExcelFileCreator2();
//        creator.writeDataToFile(service.getListOfAllData(),);
//        service.writeAllDataToVikingFile();
//    }

    // FIXME: Old
//    private static void readCompleteFileAndCalculateCartesianPoints(String fileToRead) {
//        csvReader.readAllSegmentsFromfile(fileToRead);
//
//        // Berechne für jede WGS-Position die cartesische Position,
//        // mit der WGS-Beschleunigung
//        service.calculateCartesianPointAndWgsAccelForData(constants.getCurrentSegment());
//    }
    // FIXME: old
//    public static void clearAllDataAndReadFileAgain(String fileToReadAgain) {
//        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
//        service.getListOfAllData().clear();
//
//        service.setOldDt(0);
//        service.setDt(0);
//
//        readDataOfFileAndCalculateCartesianPoints(fileToReadAgain);
//        //readCompleteFileAndCalculateCartesianPoints(fileToReadAgain);
//    }
//
//    private static void simulateFilerWithSpecificParametersWithExcelAndVikExport(float sigmaAccel, double sigmaSpeed) {
//        makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(sigmaAccel, sigmaSpeed);
//    }
//
//    private static void makeCompleteFilterSimulationWithoutAccelClearAllDataAndReadFileAgain() {
//        filterConfiguration.filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed();
//    }

    /**
     * Lese alle Daten für das jeweillige Segment ein und Berechne kartesische Punkte, sowie WGS-Accel
     *
     * @param fileToRead
     */
    public static void readDataOfFileAndCalculateCartesianPoints(String fileToRead) {
        csvReader.readSegmentFromCsv(fileToRead, constants.getCurrentSegment());

        // Berechne für jede WGS-Position die cartesische Position,
        // mit der WGS-Beschleunigung
        service.calculateCartesianPointAndWgsAccelForData(constants.getCurrentSegment());
    }
}
