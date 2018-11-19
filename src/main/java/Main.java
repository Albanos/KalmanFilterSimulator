import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
    private static final String pathToS7File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\s7_edge_eigenesFormat_09_51_35.csv";

    private static final String pathToNexus6File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Nexus6_15_37_59.csv";

    private static final String pathToS7FileWithFakeGTPosition =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\s7_edge_eigenesFormat_09_51_35_mitFakeGTPosition.csv";

    public static void main(String[] args) {

        // Lese Daten von File
        CsvReader reader = new CsvReader();
        //reader.readCsvDataOfFileType2(pathToFile2Nexus6);
        reader.readCsvDataAndSaveInPojo(pathToS7FileWithFakeGTPosition);

        LinkedList<Coordinates> allWGSPositions = Service.getListOfAllWGSPositions();
        LinkedList<ImuValues> allIMUValues = Service.getListOfAllImuValues();
        LinkedList<Coordinates> allGTWgsPositions = Service.getListOfAllGTWgsPositions();

        // Berechne kartesische Punkte aus Lat/Lon
        Service.calculateAllCartesianPoints();

        // Setze evtl noch andere Groessen
        Service.setAllOtheParametersOfAllCartesianPoints();

        LinkedList<CartesianPoint> allCartesianPoints = Service.getListOfAllCartesianPoints();

        // Filter ausführen
        EstimationFilter filter = new EstimationFilter();
        filter.makeEstimation();

        // Schreibe Daten in ein file
        ExcelFileCreator excelFileCreator = new ExcelFileCreator();
        excelFileCreator.writeCartesianPointsToFile();

        System.out.println("Hi");
    }
}
