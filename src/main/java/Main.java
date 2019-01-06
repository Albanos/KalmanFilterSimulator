import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
    private static final String pathToNexus6File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\nexus6_ownFOrmat_withGT_11_11_38.csv";

    private static final String pathToNexus6File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\nexus6_ownFormat_withGT_11_19_10.csv";

    private static final String pathToS7File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\S7Edge_ownFormat_withGT_11_51_42.csv";

    public static void main(String[] args) {
        writeOneTimeAllSegmentsInMap(pathToNexus6File2);
        Constants.setListOfAllDataByGlobalSegment();

        FilterConfiguration bestConfi = simulateFilterBySegmentsAndFindBestConfi(pathToNexus6File2);
        System.out.println("Beste Kofi:\nsigmaAccel:  "
                + bestConfi.getSigmaAccel()
                + "\nsigmaSpeed:  " + bestConfi.getSigmaGnssSpeed());

//        Constants.setCurrentSegment(Constants.getSegmentA());
//        Constants.setListOfAllDataByGlobalSegment();
//        makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(8f, 0.5);
//
//        Constants.setCurrentSegment(Constants.getSegmentB());
//        Constants.setListOfAllDataByGlobalSegment();
//        makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(8f, 0.5);
//
//        Constants.setCurrentSegment(Constants.getSegmentC());
//        Constants.setListOfAllDataByGlobalSegment();
//        makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(8f, 0.5);
//
//        Constants.setCurrentSegment(Constants.getSegmentD());
//        Constants.setListOfAllDataByGlobalSegment();
//        makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(8f, 0.5);

        //        FilterConfiguration bestConfi = simulateFilterBySegmentsAndFindBestConfi(pathToNexus6File2);
//        System.out.println("Beste Kofi:\nsigmaAccel:  "
//                + bestConfi.getSigmaAccel()
//                + "\nsigmaSpeed:  " + bestConfi.getSigmaGnssSpeed());
        //readDataOfFileAndCalculateCartesianPoints(pathToNexus6File2);
        //readCompleteFileAndCalculateCartesianPoints(pathToNexus6File2);

        //makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(20f, 0.001);

//        makeCompleteFilterSimulationFindBestKonfisOnLatiLongiRmseSimulateWithThemAndWriteInFile(pathToNexus6File2);
//        makeCompleteFilterSimulationWithoutAccelFindBestKonfiOnLatiLongiRmseSimulateWithThemAndWriteInFile();

        System.out.println("Hi");
    }

    private static void writeOneTimeAllSegmentsInMap(String pathToFile) {
        // Befülle die globale map mit allen segmenten einmalig
        Constants.setCurrentSegment(Constants.getSegmentA());
        CsvReader reader = new CsvReader();

        reader.readAllFromCsvFile(pathToFile, Constants.getCurrentSegment());
        Service2.calculateCartesianPointAndWgsAccelForData(Constants.getCurrentSegment());

        Constants.setCurrentSegment(Constants.getSegmentB());
        reader.readAllFromCsvFile(pathToFile, Constants.getCurrentSegment());
        Service2.calculateCartesianPointAndWgsAccelForData(Constants.getCurrentSegment());

        Constants.setCurrentSegment(Constants.getSegmentC());
        reader.readAllFromCsvFile(pathToFile, Constants.getCurrentSegment());
        Service2.calculateCartesianPointAndWgsAccelForData(Constants.getCurrentSegment());

        Constants.setCurrentSegment(Constants.getSegmentD());
        reader.readAllFromCsvFile(pathToFile, Constants.getCurrentSegment());
        Service2.calculateCartesianPointAndWgsAccelForData(Constants.getCurrentSegment());
    }

    private static FilterConfiguration simulateFilterBySegmentsAndFindBestConfi(String pathToFile) {
        // Simuliere alle Konfigurationen:
        // erhöhe nur Accel, nur Speed oder beides
        FilterConfiguration currentConfi = new FilterConfiguration();
//        currentConfi.filterSimulation_overAllSegments_001_to_20_1_in_001_onlySigmaAccel(pathToFile);
        //currentConfi.filterSimulation_overAllSegments_001_to_15_1_in_001_onlySigmaSpeed(pathToFile);
        currentConfi.filterSimulation_overAllSegments_to_20_1_in_001_for_Accel_to_15_1_in_001_for_Speed(pathToFile);

        // Gebe beste Konfiguration zurück
        return currentConfi.findBestConfigurationBySumOfAbsRmse();
    }

    /**
     * Simuliert Filter ohne Accel zu variieren und sucht dann beste Konfi anahnd von lati/longi-
     * Rmse. Daten werden in eine File geschrieben
     */
    private static void makeCompleteFilterSimulationWithoutAccelFindBestKonfiOnLatiLongiRmseSimulateWithThemAndWriteInFile() {
        makeCompleteFilterSimulationWithoutAccelClearAllDataAndReadFileAgain();

        // Extrahiere die Filter-Konfi, wo lati und longi-RMSE minimal sind
        FilterConfiguration configurationWithMinimalLatiAndLongiRmse = FilterConfiguration.findConfigurationWithMinimalLatiAndLongiRmseOfEstPoints();
        FilterConfiguration configurationWithMinimalLongiRmseOfEstPoints = FilterConfiguration.findConfigurationWithMinimalLongiRmseOfEstPoints();
        FilterConfiguration configurationWithMinimalLatiRmseOfEstPoints = FilterConfiguration.findConfigurationWithMinimalLatiRmseOfEstPoints();

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        //clearAllDataAndReadFileAgain(pathToNexus6File2);

        // Simuliere noch einmal in der Ausgangs-Konfi und exportiere
        //simulateFilerWithSpecificParametersWithExcelAndVikExport(8f, 0.5);

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        //clearAllDataAndReadFileAgain(pathToNexus6File2);

        // simuliere mit minimalem Longi-Abstand
        simulateFilerWithSpecificParametersWithExcelAndVikExport(
                configurationWithMinimalLongiRmseOfEstPoints.getSigmaAccel(),
                configurationWithMinimalLongiRmseOfEstPoints.getSigmaGnssSpeed()
        );

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        //clearAllDataAndReadFileAgain(pathToNexus6File2);

        // simuliere mit minimalem lati-Abstand
        simulateFilerWithSpecificParametersWithExcelAndVikExport(
                configurationWithMinimalLatiRmseOfEstPoints.getSigmaAccel(),
                configurationWithMinimalLatiRmseOfEstPoints.getSigmaGnssSpeed()
        );

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        //clearAllDataAndReadFileAgain(pathToNexus6File2);

        // Nehme die Konfiguration von configurationWithMinimalLatiAndLongiRmse, simuliere erneut und exportiere
        simulateFilerWithSpecificParametersWithExcelAndVikExport(
                configurationWithMinimalLatiAndLongiRmse.getSigmaAccel(),
                configurationWithMinimalLatiAndLongiRmse.getSigmaGnssSpeed());

        System.out.println("Filter-Konfi mit minimalem Longi:\nsigmaAccel:  "
                + configurationWithMinimalLongiRmseOfEstPoints.getSigmaAccel() + " und sigmaSpeed:  "
                + configurationWithMinimalLongiRmseOfEstPoints.getSigmaGnssSpeed());

        System.out.println("Filter-Konfi mit minimalem Lati:\nsigmaAccel:  "
                + configurationWithMinimalLatiRmseOfEstPoints.getSigmaAccel() + " und sigmaSpeed:  "
                + configurationWithMinimalLatiRmseOfEstPoints.getSigmaGnssSpeed());

        System.out.println("Optimale Filter-Konfi:\nsigmaAccel:  "
                + configurationWithMinimalLatiAndLongiRmse.getSigmaAccel() + " und sigmaSpeed:  "
                + configurationWithMinimalLatiAndLongiRmse.getSigmaGnssSpeed());

        ExcelFileCreator2 creator2 = new ExcelFileCreator2();
        creator2.writeDataToFile();

        Service2.writeAllDataToVikingFile();
    }

    private static void makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(float sigmaAccel, double sigmaSpeed) {
        Constants.setSigmaAccel(sigmaAccel);
        Constants.setSigmaGnssSpeed(sigmaSpeed);
        EstimationFilter2 filter = new EstimationFilter2();
        filter.makeEstimation();

        Service2.calculateRMSEOfLatiLongiDistancesAndAbsDistanceFor10Hearts();

        ExcelFileCreator2 creator = new ExcelFileCreator2();
        creator.writeDataToFile();
        Service2.writeAllDataToVikingFile();
    }

    private static void readCompleteFileAndCalculateCartesianPoints(String fileToRead) {
        CsvReader reader = new CsvReader();
        reader.readAllSegmentsFromfile(fileToRead);

        // Berechne für jede WGS-Position die cartesische Position,
        // mit der WGS-Beschleunigung
        Service2.calculateCartesianPointAndWgsAccelForData(Constants.getCurrentSegment());
    }

    public static void clearAllDataAndReadFileAgain(String fileToReadAgain) {
        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        Service2.getListOfAllData().clear();
        Service2.setRmseLatiEstGt(0);
        Service2.setRmseLongiEstGt(0);
        Service2.setRmseLatiGnssGt(0);
        Service2.setRmseLongiGnssGt(0);

        Service2.setOldDt(0);
        Service2.setDt(0);

        readDataOfFileAndCalculateCartesianPoints(fileToReadAgain);
        //readCompleteFileAndCalculateCartesianPoints(fileToReadAgain);
    }

    private static void simulateFilerWithSpecificParametersWithExcelAndVikExport(float sigmaAccel, double sigmaSpeed) {
        makeSimulationWithSpecificSigmaAccelAndSpecificSigmaSpeed(sigmaAccel, sigmaSpeed);
    }

    private static void makeCompleteFilterSimulationWithoutAccelClearAllDataAndReadFileAgain() {
        FilterConfiguration confi = new FilterConfiguration();
        confi.filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed();
    }

    /**
     * Lese alle Daten für das jeweillige Segment ein und Berechne kartesische Punkte, sowie WGS-Accel
     *
     * @param fileToRead
     */
    public static void readDataOfFileAndCalculateCartesianPoints(String fileToRead) {
        CsvReader reader = new CsvReader();
        reader.readAllFromCsvFile(fileToRead, Constants.getCurrentSegment());

        // Berechne für jede WGS-Position die cartesische Position,
        // mit der WGS-Beschleunigung
        Service2.calculateCartesianPointAndWgsAccelForData(Constants.getCurrentSegment());
    }

    public static String getPathToNexus6File() {
        return pathToNexus6File;
    }

    public static String getPathToNexus6File2() {
        return pathToNexus6File2;
    }

    public static String getPathToS7File() {
        return pathToS7File;
    }
}
