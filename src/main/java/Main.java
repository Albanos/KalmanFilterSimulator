import java.sql.Timestamp;
import java.util.LinkedHashMap;
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

    private static final String pathToS7File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\s7Edge_ownFormat_withGT_11_43_43.csv";

    public static void main(String[] args) {
        // Lese Daten von File
        CsvReader reader = new CsvReader();
        reader.readCsvDataAndSaveInPojo(pathToS7File);

        LinkedList<Coordinates> allWGSPositions = Service.getListOfAllWGSPositions();
        LinkedList<ImuValues> allIMUValues = Service.getListOfAllImuValues();
        LinkedList<Coordinates> allGTWgsPositions = Service.getListOfAllGTWgsPositions();

        // Berechne kartesische Punkte aus Lat/Lon
        Service.calculateAllCartesianPoints();

        // Setze evtl noch andere Groessen
        Service.setAllOtheParametersOfAllCartesianPoints();

        LinkedList<CartesianPoint> allCartesianPoints = Service.getListOfAllCartesianPoints();
        LinkedList<ImuValues> before = Service.getListOfAllImuValues();

        Service.makeDownSamplingOfImu();

        LinkedList<ImuValues> after = Service.getResampledListOfAllImuValues();

        // Filter ausführen
        EstimationFilter filter = new EstimationFilter();
        filter.makeEstimation();

        // Berechne den durchschnittlichen dt-Wert
        Service.setDt(Service.calculateAverage(Service.getAllDtValues()));

        // Schreibe Daten in ein file
        ExcelFileCreator excelFileCreator = new ExcelFileCreator();
        excelFileCreator.writeCartesianPointsToFile();

        System.out.println("Hi");
    }
}
