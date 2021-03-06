import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    // ================================ Dateien mit Step-detector, an klarem Tag (sonnig)
    private static final String nexus6b_johann3 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-03-nexus6b_2019-03-29_15-26-02.0_formatted_addedColumns.csv";
    private static final String nexus6w_johann3 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-03-nexus6w_2019-03-29_15-26-03.0_formatted_addedColumns.csv";
    private static final String nexus6b_johann4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-04-nexus6b_2019-03-29_15-36-03.0_formatted_addedColumns.csv";
    private static final String nexus6w_johann4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-04-nexus6w_2019-03-29_15-36-03.0_formatted_addedColumns.csv";
    private static final String nexus6b_johann5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-05-nexus6b_2019-03-29_15-45-48.0_formatted_addedColumns.csv";
    private static final String nexus6w_johann5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-05-nexus6w_2019-03-29_15-45-48.0_formatted_addedColumns.csv";
    private static final String nexus6b_johann6 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-06-nexus6b_2019-03-29_15-55-35.0_formatted_addedColumns.csv";
    private static final String nexus6w_johann6 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-06-nexus6w_2019-03-29_15-55-36.0_formatted_addedColumns.csv";
    private static final String nexus6b_johann7 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-07-nexus6b_2019-03-29_16-06-41.0_formatted_addedColumns.csv";
    private static final String nexus6w_johann7 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-07-nexus6w_2019-03-29_16-06-41.0_formatted_addedColumns.csv";
    private static final String nexus6b_johann8 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-08-nexus6b_2019-03-29_16-18-52.0_formatted_addedColumns.csv";
    private static final String nexus6w_johann8 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-08-nexus6w_2019-03-29_16-18-52.0_formatted_addedColumns.csv";
    private static final String nexus6b_luan1 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-01-nexus6b_2019-03-29_17-27-06.0_formatted_addedColumns.csv";
    private static final String nexus6w_luan1 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-01-nexus6w_2019-03-29_17-27-05.0_formatted_addedColumns.csv";
    private static final String nexus6b_luan4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-04-nexus6b_2019-03-29_18-13-07.0_formatted_addedColumns.csv";
    private static final String nexus6w_luan4 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-04-nexus6w_2019-03-29_18-13-07.0_formatted_addedColumns.csv";
    private static final String nexus6b_luan5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-05-nexus6b_2019-03-29_18-22-08.0_formatted_addedColumns.csv";
    private static final String nexus6w_luan5 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-05-nexus6w_2019-03-29_18-22-07.0_formatted_addedColumns.csv";
    private static final String nexus6b_luan6 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-06-nexus6b_2019-03-29_18-31-35.0_formatted_addedColumns.csv";
    private static final String nexus6w_luan6 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-06-nexus6w_2019-03-29_18-31-35.0_formatted_addedColumns.csv";
    private static final String nexus6b_luan7 =
            "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-07-nexus6b_2019-03-29_18-40-15.0_formatted_addedColumns.csv";

    private static Map<String, String> files = new HashMap<>();

    static {
        files.put("nexus6b_johann3", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-03-nexus6b_2019-03-29_15-26-02.0_formatted_addedColumns.csv");
        files.put("nexus6w_johann3", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-03-nexus6w_2019-03-29_15-26-03.0_formatted_addedColumns.csv");
        files.put("nexus6b_johann4", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-04-nexus6b_2019-03-29_15-36-03.0_formatted_addedColumns.csv");
        files.put("nexus6w_johann4", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-04-nexus6w_2019-03-29_15-36-03.0_formatted_addedColumns.csv");
        files.put("nexus6b_johann5", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-05-nexus6b_2019-03-29_15-45-48.0_formatted_addedColumns.csv");
        files.put("nexus6w_johann5", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-05-nexus6w_2019-03-29_15-45-48.0_formatted_addedColumns.csv");
        files.put("nexus6b_johann6", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-06-nexus6b_2019-03-29_15-55-35.0_formatted_addedColumns.csv");
        files.put("nexus6w_johann6", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-06-nexus6w_2019-03-29_15-55-36.0_formatted_addedColumns.csv");
        files.put("nexus6b_johann7", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-07-nexus6b_2019-03-29_16-06-41.0_formatted_addedColumns.csv");
        files.put("nexus6w_johann7", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-07-nexus6w_2019-03-29_16-06-41.0_formatted_addedColumns.csv");
        files.put("nexus6b_johann8", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-08-nexus6b_2019-03-29_16-18-52.0_formatted_addedColumns.csv");
        files.put("nexus6w_johann8", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_johann-08-nexus6w_2019-03-29_16-18-52.0_formatted_addedColumns.csv");
        files.put("nexus6b_luan1", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-01-nexus6b_2019-03-29_17-27-06.0_formatted_addedColumns.csv");
        files.put("nexus6w_luan1", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-01-nexus6w_2019-03-29_17-27-05.0_formatted_addedColumns.csv");
        files.put("nexus6b_luan4", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-04-nexus6b_2019-03-29_18-13-07.0_formatted_addedColumns.csv");
        files.put("nexus6w_luan4", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-04-nexus6w_2019-03-29_18-13-07.0_formatted_addedColumns.csv");
        files.put("nexus6b_luan5", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-05-nexus6b_2019-03-29_18-22-08.0_formatted_addedColumns.csv");
        files.put("nexus6w_luan5", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-05-nexus6w_2019-03-29_18-22-07.0_formatted_addedColumns.csv");
        files.put("nexus6b_luan6", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-06-nexus6b_2019-03-29_18-31-35.0_formatted_addedColumns.csv");
        files.put("nexus6w_luan6", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-06-nexus6w_2019-03-29_18-31-35.0_formatted_addedColumns.csv");
        files.put("nexus6b_luan7", "D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\dataWithStepDetectionOnClearDay\\Car2X-ADN_luan-07-nexus6b_2019-03-29_18-40-15.0_formatted_addedColumns.csv");
    }

    public static void main(String[] args) {
        for (Map.Entry<String, String> entry : files.entrySet()) {
            try {
                Workbook excel = readExcelTemplate();
                //readAllSegmentsFromCsv(entry.getValue(), false, false);
                for (String segment : Arrays.asList("A", "B")) {
                    for (Boolean withCurb : Arrays.asList(Boolean.FALSE, Boolean.TRUE)) {
                        for (Boolean withStep : Arrays.asList(Boolean.FALSE, Boolean.TRUE)) {
                            setAndClearSpecificThings();
                            doStuff(entry.getValue(), entry.getKey(), withCurb, withStep, segment);
                            writeToExcel(excel, withCurb, withStep, segment);
                        }
                    }
                }
                saveExcel(excel, entry.getKey());
                System.out.println("=============================Simulation von " + entry.getKey() + " abgeschlossen");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void readAllSegmentsFromCsv(String pathToFile, String fileName, boolean only10Meters, boolean only20Meters) {
        if (only10Meters && only20Meters) {
            try {
                throw new Exception("Entweder nur 10 meter oder nur 20 meter, nicht beide");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        // Befülle die globale map mit allen segmenten einmalig
        csvReader.readSegmentFromCsv(pathToFile, fileName, constants.getSegmentA(), only10Meters, only20Meters);
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentA());
        service.last10m(constants.getSegmentA());
        //service.labelLast10And5Meters(constants.getSegmentA());

        csvReader.readSegmentFromCsv(pathToFile, fileName, constants.getSegmentB(), only10Meters, only20Meters);
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentB());
        service.last10m(constants.getSegmentB());
        //service.labelLast10And5Meters(constants.getSegmentB());

        csvReader.readSegmentFromCsv(pathToFile, fileName, constants.getSegmentC(), only10Meters, only20Meters);
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentC());

        csvReader.readSegmentFromCsv(pathToFile, fileName, constants.getSegmentD(), only10Meters, only20Meters);
        service.calculateCartesianPointAndWgsAccelForData(constants.getSegmentD());
    }

    private static FilterConfiguration simulateFilterForAllSegmentsAndFindBestConfi(boolean withGtAsFakeMeasurement, boolean withVelocityFromStepDetection) {
        // Simuliere alle Konfigurationen:
        //filterConfiguration.filterSimulation_overAllSegments_to_20_1_in_001_for_Accel_to_15_1_in_001_for_Speed(withGtAsFakeMeasurement);
        //filterConfiguration.filterSimulation_01_to_15_1_in_01_onlySigmaGnssSpeed(withGtAsFakeMeasurement);
        filterConfiguration.filterSimulation_overAllSegments_simulateAllValues_withoutAccel_by_01_steps(withGtAsFakeMeasurement, withVelocityFromStepDetection);
        //filterConfiguration.filterSimulation_overAllSegments_simulateOnlyVectorG_withoutAccel(withGtAsFakeMeasurement);

        // Gebe beste Konfiguration zurück
        return filterConfiguration.findBestConfigurationBySumOfAbsRmse();
    }

    private static void doStuff(String filePath, String fileName, boolean withCurb, boolean withStep, String segment) {
        //String fileName = filePath.split("_")[2] + "_" + args[0];
        readAllSegmentsFromCsv(filePath, fileName, false, false);

        // Segment A
        switch (segment) {
            case "A":
                constants.setCurrentSegment(constants.getSegmentA());
                service.setListOfAllDataByGlobalSegment();
                FilterConfiguration startConf
                        = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                        5.0,
                        3.0,
                        4.5,
                        4.5,
                        3.5,
                        0.5,
                        withCurb, withStep);
            System.out.println("RMSE, Est -> GT, Segment A:  " + startConf.getRmseAbsDistanceEstGt());
            System.out.println("RMSE, GNSS -> GT, Segment A:  " + startConf.getRmseAbsDistanceGnssGt() + "\n\n");
            System.out.println("RMSE, Est -> GT, Segment A, LATI:  " + startConf.getRmseLatiDistanceEstGt());
            System.out.println("RMSE, Est -> GT, Segment A, LONGI:  " + startConf.getRmseLongiDistanceEstGt() + "\n");
            System.out.println("RMSE, GNSS -> GT, Segment A, LATI:  " + startConf.getRmseLatiDistanceGnssGt());
            System.out.println("RMSE, GNSS -> GT, Segment A, LONGI:  " + startConf.getRmseLongiDistanceGnssGt() + "\n");

//        ExcelFileCreator2 creator = new ExcelFileCreator2();
//        creator.writeDataToFile(fileName, service.getListOfAllData(),startConf, constants.getCurrentSegment());
                //service.writeAllDataToVikingFile(fileName, constants.getCurrentSegment());
                break;

            // Segment B
            case "B":
                constants.setCurrentSegment(constants.getSegmentB());
                service.setListOfAllDataByGlobalSegment();
                startConf
                        = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
                        5.0,
                        3.0,
                        4.5,
                        4.5,
                        3.5,
                        0.5,
                        withCurb, withStep);
            System.out.println("RMSE, Est -> GT, Segment B:  " + startConf.getRmseAbsDistanceEstGt());
            System.out.println("RMSE, GNSS -> GT, Segment B:  " + startConf.getRmseAbsDistanceGnssGt() + "\n\n");
            System.out.println("RMSE, Est -> GT, Segment B, LATI:  " + startConf.getRmseLatiDistanceEstGt());
            System.out.println("RMSE, Est -> GT, Segment b, LONGI:  " + startConf.getRmseLongiDistanceEstGt() + "\n");
            System.out.println("RMSE, GNSS -> GT, Segment B, LATI:  " + startConf.getRmseLatiDistanceGnssGt());
            System.out.println("RMSE, GNSS -> GT, Segment B, LONGI:  " + startConf.getRmseLongiDistanceGnssGt() + "\n");


//        creator = new ExcelFileCreator2();
//        creator.writeDataToFile(fileName, service.getListOfAllData(),startConf, constants.getCurrentSegment());
                //service.writeAllDataToVikingFile(fileName, constants.getCurrentSegment());
                break;
        }

        // FIXME: Vorerst nur Betrachtung von Segment A und Segment B, für 10m-Evaluation
        // Segment C
//        constants.setCurrentSegment(constants.getSegmentC());
//        service.setListOfAllDataByGlobalSegment();
//
//        startConf
//                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
//                5.0,
//                3.0,
//                4.5,
//                4.5,
//                3.5,
//                0.5, true, true);
//        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
//        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();
//
//        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
//        service.writeAllDataToVikingFile(constants.getCurrentSegment());
//
//        // Segment D
//        constants.setCurrentSegment(constants.getSegmentD());
//        service.setListOfAllDataByGlobalSegment();
//
//        startConf
//                = filterConfiguration.simulateEstimationWithAllParametersGenerateConfigurationAndReturnThem(
//                5.0,
//                3.0,
//                4.5,
//                4.5,
//                3.5,
//                0.5, true, true);
//
//        sumOfAllRmseValuesEstGt += startConf.getRmseAbsDistanceEstGt();
//        sumOfAllRmseValuesGnssGt += startConf.getRmseAbsDistanceGnssGt();
//
//        creator.writeDataToFile(service.getListOfAllData(),startConf, constants.getCurrentSegment());
//        service.writeAllDataToVikingFile(constants.getCurrentSegment());
//        System.out.println("Summe der RMSE-Werte, Est_GT:  " + sumOfAllRmseValuesEstGt / 2);
//        System.out.println("Summe der RMSE-Werte, GNSS_GT:  " + sumOfAllRmseValuesGnssGt / 2);


//        FilterConfiguration bestConfi = simulateFilterForAllSegmentsAndFindBestConfi(false);
//        System.out.println("Beste Kofi:\n" +
//                "sigmaGnssSpeed:  " + bestConfi.getSigmaGnssSpeed() + "\n"
//                + "sigmaPosAccuracy:  " + bestConfi.getSigmaPosAccuracy() + "\n"
//                + "G1:  " + bestConfi.getG1() + "\n"
//                + "G2:  " + bestConfi.getG2() + "\n"
//                + "G3:  " + bestConfi.getG3() + "\n"
//                + "G4:  " + bestConfi.getG4()
//        );
    }

    private static void setAndClearSpecificThings() {
        csvReader.getOriginalLinesBySegments().clear();
        CsvReader.setFirstIteration(true);
    }

    private static Workbook readExcelTemplate() throws IOException {
        return WorkbookFactory.create(new File("D:\\Workspace_IntelliJ\\FilterSimulator\\src\\main\\ressources\\template.xlsx"));
    }

    private static void writeToExcel(Workbook excel, boolean withCurb, boolean withStep, String segment) {
        String sheetName = "A".equals(segment) ? "Segment1" : "Segment2";
        if (!withCurb && !withStep) {
            writeFromColumn(sheetName, 1, excel);
        } else if (!withCurb) {
            writeFromColumn(sheetName, 6, excel);
        } else if (!withStep) {
            writeFromColumn(sheetName, 11, excel);
        } else {
            writeFromColumn(sheetName, 16, excel);
        }
    }

    private static void writeFromColumn(String sheetName, int column, Workbook excel) {
        Sheet sheet = excel.getSheet(sheetName);
        Map<Integer, Data> dataMap = service
                .getListOfAllData()
                .stream()
                .filter(d -> d.getDistanceFromStart() != null)
                .collect(Collectors.toMap(Data::getDistanceFromStart, d -> d));

        double summOfDistanceLati = 0;
        double summOfDistanceLongi = 0;
        double summOfDistanceAbs = 0;

        for (int meter = 0; meter <= 10; meter++) {
            Data data = dataMap.get(meter);
            int rowNumber = meter == 0 ? 5 : ((meter+1) * 6) - 1;
            //int rowNumber = (meter * 6) - 1;
            Row currentRow = sheet.getRow(rowNumber);

            // All Est-values
            // Est -> latiDist
            Cell currentCell = currentRow.getCell(column);
            currentCell.setCellValue(data.getLatiDistanceEstToGtWithDirection());
            summOfDistanceLati += data.getLatiDistanceEstToGtWithDirection();
            // Est --> longiDist
            currentCell = currentRow.getCell(column + 1);
            currentCell.setCellValue(data.getLongiDistanceEstToGtWithDirection());
            summOfDistanceLongi += data.getLongiDistanceEstToGtWithDirection();
            // Est -> AbstEst
            currentCell = currentRow.getCell(column + 2);
            currentCell.setCellValue(data.getAbsoluteDistanceEstGt());
            summOfDistanceAbs += data.getAbsoluteDistanceEstGt();

            // All GNSS-Values
            currentRow = sheet.getRow(rowNumber + 1);
            // GNSS -> latiDist
            currentCell = currentRow.getCell(column);
            currentCell.setCellValue(data.getLatiDistanceGnssToGtWithDirection());
            summOfDistanceLati += data.getLatiDistanceGnssToGtWithDirection();
            // GNSS --> longiDist
            currentCell = currentRow.getCell(column + 1);
            currentCell.setCellValue(data.getLongiDistanceGnssToGtWithDirection());
            summOfDistanceLongi += data.getLongiDistanceGnssToGtWithDirection();
            // GNSS -> AbstEst
            currentCell = currentRow.getCell(column + 2);
            currentCell.setCellValue(data.getAbsoluteDistanceGnssGt());
            summOfDistanceAbs += data.getAbsoluteDistanceGnssGt();
        }
        System.out.println();
    }

    private static void saveExcel(Workbook excel, String fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(fileName + ".xlsx"));
        excel.write(out);
        out.close();
        excel.close();
    }
}
