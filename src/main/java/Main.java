import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
    private static final String pathToNexus6File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\Nexus6_ownFOrmat_withGT_10_48_21.csv";

    private static final String pathToS7File =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\S7Edge_ownFormat_withGT_16_52_36.csv";

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
