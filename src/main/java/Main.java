import java.util.LinkedList;

/**
 * @author Luan Hajzeraj on 12.11.2018.
 */
public class Main {
//    private static final String pathToFile1 =
//            "D:\\Workspace_IntelliJ\\" +
//            "FilterSimulator\\src\\main\\resources\\" +
//            "Car2X-ADN_Nexus-6-Messung-3_2017-05-23_15-14-30.0_formatted_addedColumns.csv";

    private static final String pathToFile2 =
            "D:\\Workspace_IntelliJ\\" +
                    "FilterSimulator\\src\\main\\resources\\" +
                    "Car2X-ADN_x5-messung-3-1_2017-06-13_09-50-44.0_formatted_addedColumns.csv";

    private static final String pathToFile2Nexus6 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\resources\\Car2X-ADN_s7edge-messung-3-1_2017-06-13_09-51-35.0_formatted_addedColumns.csv";

    private static final String pathToFileS7OwnFormat =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\resources\\s7_edge_eigenesFormat_09_51_35.csv";

    public static void main(String[] args){
//        ===========================================================================Daten-Typ1
// Daten einlesen und in List von Service speichern
//        CsvReader reader = new CsvReader();
//        reader.readCsvDataOfFileType2(pathToFile);
//        LinkedList<Measure> allMeasurements = Service.getListOfAllMeasurements();
//
//        // Berechne alle kartesischen Punkte auf Basis der Liste aller globalen Positionen in Service
//        Service.calculateAllCartesianPointsOfDataType1();
//
//        // Setze alle anderen Größen der kartesischen Punkte
//        Service.setAllParametersOfAllCartesianPoints();
//        LinkedList<CartesianPoint> allCartesianPoins = Service.getListOfAllCartesianPoints();
//
//        // Berechne die WGS-Beschleunigung in x- und y-Richtung
        //Service.calculateWgsAccelOfDataType1();
//
//        // Erzeuge ein Kalman-Filter-Objekt
//        EstimationFilter filter = new EstimationFilter();
//        filter.makeEstimation();
//        LinkedList<CartesianPoint> estimatedCartesianPoints = Service.getListOfAllEstimatedCartesianPoints();
//
//        System.out.println("Anzahl, kartesische Punkte:  " + Service.getListOfAllCartesianPoints().size());
//        System.out.println("Anzahl, geschätzte Punkte:  " + Service.getListOfAllEstimatedCartesianPoints().size());
//
//        LinkedHashMap<CartesianPoint, Coordinates> pointToWGSMap = Service.getPointToWGSMap();
//
//        // Schreibe Daten in ein file
//        ExcelFileCreator excelFileCreator = new ExcelFileCreator();
//        excelFileCreator.writeCartesianPointsToFile();

        //        ===========================================================================Daten-Typ2
        // Lese Daten von File
        CsvReader reader = new CsvReader();
        //reader.readCsvDataOfFileType2(pathToFile2Nexus6);
        reader.readCsvDataOfOwnFormat(pathToFileS7OwnFormat);

        LinkedList<Coordinates> onlyWGSPositions = Service.getListOfAllWGSPositions();
        LinkedList<ImuValues> onlyIMUValues = Service.getListOfAllImuValues();

        // Berechne kartesische Punkte aus Lat/Lon
        Service.calculateAllCartesianPointsOfDataType2();
        Service.setAllParametersOfAllCartesianPointsOwnFormat();

        LinkedList<CartesianPoint> onlyCartesianPoints = Service.getOnlyCartesianPoints();

        // Filter ausführen
        EstimationFilter filter = new EstimationFilter();
        filter.makeEstimation();

        // Schreibe Daten in ein file
        ExcelFileCreator excelFileCreator = new ExcelFileCreator();
        excelFileCreator.writeCartesianPointsToFileForDataType2();

        System.out.println("Hi");
    }
}
