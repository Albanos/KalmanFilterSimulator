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

    // Segment A = {12078, 12700}
    // Segment B = {12700, 12694}
    // Segment C = {12694, 12700}
    // Segment D = {12700, 12078} --> kaputt!!
    private static final String[] segment = {"12700", "12694"};

    public static void main(String[] args) {
        readDataOfFileAndCalculateCartesianPoints(pathToNexus6File);

        makeCompleteFilterSimulationClearAllDataAndReadFileAgain(pathToNexus6File);

        // Extrahiere die Filter-Konfi, wo lati und longi-RMSE minimal sind
        FilterConfiguration configurationWithMinimalLatiAndLongiRmse = FilterConfiguration.findConfigurationWithMinimalLatiAndLongiRmse();

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        clearAllDataAndReadFileAgain(pathToNexus6File);

        // Simuliere noch einmal in der Ausgangs-Konfi und exportiere
        simulateFilerWithSpecificParametersWithExcelAndVikExport(8f, 0.5);

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        clearAllDataAndReadFileAgain(pathToNexus6File);

        // Nehme die Konfiguration von configurationWithMinimalLatiAndLongiRmse, simuliere erneut und exportiere
        simulateFilerWithSpecificParametersWithExcelAndVikExport(
                configurationWithMinimalLatiAndLongiRmse.getSigmaAccel(),
                configurationWithMinimalLatiAndLongiRmse.getSigmaGnssSpeed());

        System.out.println("Optimale Filter-Konfi:\nsigmaAccel:  "
                + configurationWithMinimalLatiAndLongiRmse.getSigmaAccel() + " und sigmaSpeed:  "
                + configurationWithMinimalLatiAndLongiRmse.getSigmaGnssSpeed());

//        EstimationFilter2 filter2 = new EstimationFilter2();
//        filter2.makeEstimation();
//
//        // Berechne die RMSE-Werte und speichere sie im Service
//        Service2.calculateRMSEFor10Hearts();
//
//        ExcelFileCreator2 excelFileCreator2 = new ExcelFileCreator2();
//        excelFileCreator2.writeDataToFile();
//
//        // Schreibe den Inhalt in ein viking-file
//        Service2.writeAllDataToVikingFile();

        System.out.println("Hi");
    }

    private static void clearAllDataAndReadFileAgain(String fileToReadAgain) {
        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        Service2.getListOfAllData().clear();
        Service2.setRmseLatiEstGt(0);
        Service2.setRmseLongiEstGt(0);
        Service2.setRmseLatiGnssGt(0);
        Service2.setRmseLongiGnssGt(0);

        Service2.setOldDt(0);
        Service2.setDt(0);

        readDataOfFileAndCalculateCartesianPoints(fileToReadAgain);
    }

    private static void simulateFilerWithSpecificParametersWithExcelAndVikExport(float sigmaAccel, double sigmaSpeed) {
        Constants.setSigmaAccel(sigmaAccel);
        Constants.setSigmaGnssSpeed(sigmaSpeed);
        EstimationFilter2 filter = new EstimationFilter2();
        filter.makeEstimation();
        Service2.calculateRMSEFor10Hearts();
        ExcelFileCreator2 excelFileCreator2 = new ExcelFileCreator2();
        excelFileCreator2.writeDataToFile();
        Service2.writeAllDataToVikingFile();
    }

    private static void makeCompleteFilterSimulationClearAllDataAndReadFileAgain(String readAgainFile) {
        FilterConfiguration configuration = new FilterConfiguration();
        configuration.filterSimulation_01_to_50_1_in_01_onlySigmaAccel();

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        clearAllDataAndReadFileAgain(readAgainFile);

        configuration.filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed();

        // Leere die Liste mit Daten und lies erneut ein (neuer Versuch, da statische Liste)
        clearAllDataAndReadFileAgain(readAgainFile);

        configuration.filterSimulation_01_to_50_1_in_01_forSigmaAccel_and_01_to_15_1_in_01_forSigmaGnssSpeed();
    }

    private static void readDataOfFileAndCalculateCartesianPoints(String fileToRead) {
        CsvReader reader = new CsvReader();
        reader.readAllFromCsvFile(fileToRead, segment);

        // Berechne für jede WGS-Position die cartesische Position,
        // mit der WGS-Beschleunigung
        Service2.calculateCartesianPointAndWgsAccelForData();
    }
}
