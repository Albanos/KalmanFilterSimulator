import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
    private static final String pathToNexus6File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Nexus6_ownFormat_withGT_15_27_39.csv";

    private static final String pathToNexus6File2 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\nexus6_ownFormat_withGT_11_19_10.csv";

    public static void main(String[] args) {
        // Lese Daten von File
        CsvReader reader = new CsvReader();
        reader.readCsvDataAndSaveInPojo(pathToNexus6File2);

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

        LinkedHashMap<Coordinates, Double> estimatedWgsPositionGtDistanceMap = Service.getEstimatedWgsPositionGtDistanceMap();
        LinkedHashMap<CartesianPoint, Coordinates> pointToWGSMap = Service.getPointToWGSMap();

        // Schreibe Daten in ein file
        ExcelFileCreator excelFileCreator = new ExcelFileCreator();
        excelFileCreator.writeCartesianPointsToFile();

        System.out.println("Hi");
    }
}
