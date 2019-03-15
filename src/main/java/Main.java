import java.util.List;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
    private static final Constants constants = Constants.getInstance();
    private static final Service2 service = Service2.getInstance();
    private static final CsvReader csvReader = CsvReader.getInstance();
    private static final FilterConfiguration filterConfiguration = FilterConfiguration.getInstance();

    // mit und ohne GT um etwa 4m schlechter als GNSS
    private static final String pathToNexus6File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\nexus6_ownFOrmat_withGT_11_11_38.csv";

    // Um 8cm schlechter als GNSS, aber mit GT klein wenig besser als GNSS
    private static final String pathToNexus6File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\nexus6_ownFormat_withGT_11_19_10.csv";

    // mit und ohne GT besser als GNSS
    private static final String pathToNexus6File3 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-22_2017-06-01_10-03-52.0_formatted_addedColumns.csv";

    // mit und ohne GT schlechter als GNSS, aber: zwischendurch (also während GO) keine Positionen vorhanden.
    // MERKE: SOGAR LÜCKEN IN DER GT!!!
    private static final String pathToNexus6File4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-23_2017-06-01_10-12-31.0_formatted_addedColumns.csv";

    // mit und ohne GT schlechter als GNSS, aber: zwischendurch (also während GO) keine Positionen vorhanden
    private static final String pathToNexus6File5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-25_2017-06-01_10-33-05.0_formatted_addedColumns.csv";

    // mit und ohne GT schlechter als GNSS, aber: zwischendurch (also während GO) keine Positionen vorhanden
    private static final String pathToNexus6File6 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-27_2017-06-01_10-49-31.0_formatted_addedColumns.csv";

    // mit und ohne GT schlechter als GNSS, aber: zwischendurch (also während GO) keine Positionen vorhanden
    private static final String pathToNexus6File7 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_Nexus-6-Messung-1_2017-05-23_14-47-59.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File8 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-28_2017-06-01_10-57-37.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File9 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_Nexus-6-Messung-3_2017-05-23_15-14-30.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File10 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_Nexus-6-Messung-4_2017-05-23_15-27-39.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File11 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_Nexus-6-Messung-5_2017-05-23_15-37-59.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File12 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_Nexus-6-Messung-6_2017-05-23_15-45-56.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File13 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-21_2017-06-01_09-54-26.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File14 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-24_2017-06-01_10-22-00.0_formatted_addedColumns.csv";

    private static final String pathToNexus6File15 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus6-messung-26_2017-06-01_10-40-53.0_formatted_addedColumns.csv";

    private static final String pathToS7File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\S7Edge_ownFormat_withGT_11_51_42.csv";

    private static final String pathToS7File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_s7edge-messung-1_2017-05-23_16-07-01.0_formatted_addedColumns.csv";

    private static final String pathToS7File3 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_s7edge-messung-24_2017-06-01_11-59-27.0_formatted_addedColumns.csv";

    private static final String pathToNexus5File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus5-messung-21_2017-06-01_09-56-40.0_formatted_addedColumns.csv";

    private static final String pathToNexus5File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus5-messung-210_2017-06-01_11-21-25.0_formatted_addedColumns.csv";

    private static final String pathToNexus5File3 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_Nexus5-Messung-4_2017-05-23_15-31-03.0_formatted_addedColumns.csv";

    private static final String pathToNexus5File4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_Nexus5-Messung-5_2017-05-23_15-39-30.0_formatted_addedColumns.csv";

    private static final String pathToNexus5File5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Car2X-ADN_nexus5-messung-22_2017-06-01_10-06-06.0_formatted_addedColumns.csv";

    // ===================================================================NEUE DATEN, MIT STEP_DETECTOR
    private static final String nexus6_luan1 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_luan-1_2019-03-08_15-34-32.0_formatted_addedColumns.csv";

    private static final String nexus6_luan2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_luan-2_2019-03-08_15-48-04.0_formatted_addedColumns.csv";

    private static final String nexus6_luan3 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_luan-3_2019-03-08_15-55-25.0_formatted_addedColumns.csv";

    private static final String nexus6_luan4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_luan-4_2019-03-08_16-05-01.0_formatted_addedColumns.csv";

    private static final String nexus6_luan5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_luan-5_2019-03-08_16-13-41.0_formatted_addedColumns.csv";

    private static final String nexus6_rovi1 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_rovena-1_2019-03-08_16-25-31.0_formatted_addedColumns.csv";

    private static final String nexus6_rovi2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_rovena-2_2019-03-08_16-41-17.0_formatted_addedColumns.csv";

    private static final String nexus6_rovi3 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_rovena-3_2019-03-08_16-49-35.0_formatted_addedColumns.csv";

    private static final String nexus6_rovi4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_rovena-4_2019-03-08_16-57-30.0_formatted_addedColumns.csv";

    private static final String nexus6_rovi5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetection\\Car2X-ADN_rovena-5_2019-03-08_17-05-06.0_formatted_addedColumns.csv";

    public static void main(String[] args) {
        readAllSegmentsFromCsv(nexus6_luan1);
        double sumOfAllRmseValuesEstGt = 0;
        double sumOfAllRmseValuesGnssGt = 0;
        // Segment A
        constants.setCurrentSegment(constants.getSegmentA());
        service.setListOfAllDataByGlobalSegment();

        FilterConfiguration startConf
                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                5.0,
                3.0,
                4.5,
                4.5,
                3.5,
                0.5, false);
        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();

        ExcelFileCreator2 creator = new ExcelFileCreator2();
        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
        service.writeAllDataToVikingFile(constants.getCurrentSegment());

        // Segment B
        constants.setCurrentSegment(constants.getSegmentB());
        service.setListOfAllDataByGlobalSegment();

        startConf
                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                5.0,
                3.0,
                4.5,
                4.5,
                3.5,
                0.5, false);
        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();

        creator = new ExcelFileCreator2();
        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
        service.writeAllDataToVikingFile(constants.getCurrentSegment());

        // Segment C
        constants.setCurrentSegment(constants.getSegmentC());
        service.setListOfAllDataByGlobalSegment();

        startConf
                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                5.0,
                3.0,
                4.5,
                4.5,
                3.5,
                0.5, false);
        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();

        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
        service.writeAllDataToVikingFile(constants.getCurrentSegment());

        // Segment D
        constants.setCurrentSegment(constants.getSegmentD());
        service.setListOfAllDataByGlobalSegment();

        startConf
                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                5.0,
                3.0,
                4.5,
                4.5,
                3.5,
                0.5, false);
        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();

        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
        service.writeAllDataToVikingFile(constants.getCurrentSegment());
        System.out.println("Summe der RMSE-Werte, Est_GT:  " + sumOfAllRmseValuesEstGt);
        System.out.println("Summe der RMSE-Werte, GNSS_GT:  " + sumOfAllRmseValuesGnssGt);



//        FilterConfiguration bestConfi = simulateFilterForAllSegmentsAndFindBestConfi(false);
//        System.out.println("Beste Kofi:\n" +
//                "sigmaGnssSpeed:  " + bestConfi.getSigmaGnssSpeed() + "\n"
//                + "sigmaPosAccuracy:  " + bestConfi.getSigmaPosAccuracy() + "\n"
//                + "G1:  " + bestConfi.getG1() + "\n"
//                + "G2:  " + bestConfi.getG2() + "\n"
//                + "G3:  " + bestConfi.getG3() + "\n"
//                + "G4:  " + bestConfi.getG4()
//        );


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
