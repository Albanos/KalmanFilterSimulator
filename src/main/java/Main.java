import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
    private static final String pathToFileS7OwnFormat =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\" +
                    "src\\main\\resources\\s7_edge_eigenesFormat_09_51_35.csv";

    public static void main(String[] args) {

        // Lese Daten von File
        CsvReader reader = new CsvReader();
        //reader.readCsvDataOfFileType2(pathToFile2Nexus6);
        reader.readCsvDataAndSaveInPojo(pathToFileS7OwnFormat);

        LinkedList<Coordinates> allWGSPositions = Service.getListOfAllWGSPositions();
        LinkedList<ImuValues> allIMUValues = Service.getListOfAllImuValues();

        // Berechne kartesische Punkte aus Lat/Lon
        Service.calculateAllCartesianPoints();

        // Setze evtl noch andere Groessen
        Service.setAllOtheParametersOfAllCartesianPoints();

        LinkedList<CartesianPoint> allCartesianPoints = Service.getOnlyCartesianPoints();

        // Filter ausführen
        EstimationFilter filter = new EstimationFilter();
        filter.makeEstimation();

        // Schreibe Daten in ein file
        ExcelFileCreator excelFileCreator = new ExcelFileCreator();
        excelFileCreator.writeCartesianPointsToFile();

        System.out.println("Hi");
    }
}
