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
        CsvReader reader = new CsvReader();
        reader.readAllFromCsvFile(pathToNexus6File2);

        // Berechne für jede WGS-Position die cartesische Position,
        // mit der WGS-Beschleunigung
        Service2.calculateCartesianPointAndWgsAccelForData();

        LinkedList<Data> listOfAllData1 = Service2.getListOfAllData();

        // Entferne überschüssige Einträge (downSampling)
        //Service2.makeDownSampling(0.057312011);
        LinkedList<Data> listOfAllData = Service2.getListOfAllData();

        EstimationFilter2 filter2 = new EstimationFilter2();
        filter2.makeEstimation();

        ExcelFileCreator2 excelFileCreator2 = new ExcelFileCreator2();
        excelFileCreator2.writeDataToFile();

        // Schreibe den Inhalt in ein viking-file
        Service2.writeAllDataToVikingFile();

        System.out.println("Hi");
    }
}
