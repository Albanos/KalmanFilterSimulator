import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author Luan Hajzeraj on 12/3/2018.
 */
public class ExcelFileCreator2 {
    public void writeDataToFile() {
        // ======================Grundstruktur für den Excel-Export erzeugen
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet firstSheet = workbook.createSheet("Original");
        HSSFSheet secondSheet = workbook.createSheet("Estimated");
        HSSFSheet thirdSheet = workbook.createSheet("Original_WGS");

        // Die echten Punkte in sheet1 --> Header erzeugen
        HSSFRow sheet1_row1 = firstSheet.createRow(0);
        HSSFCell cell_1A = sheet1_row1.createCell(0);
        cell_1A.setCellValue(new HSSFRichTextString("Original"));

        HSSFRow sheet1_row2 = firstSheet.createRow(1);
        HSSFCell cell_2A = sheet1_row2.createCell(0);
        cell_2A.setCellValue(new HSSFRichTextString("Timestamp"));

        HSSFCell cell_2B = sheet1_row2.createCell(1);
        cell_2B.setCellValue(new HSSFRichTextString("X"));

        HSSFCell cell_2C = sheet1_row2.createCell(2);
        cell_2C.setCellValue(new HSSFRichTextString("Y"));

        // Die geschätzten Punkte in sheet2 --> Header erzeugen
        HSSFRow sheet2_row1 = secondSheet.createRow(0);
        HSSFCell cell_1A_sheet2 = sheet2_row1.createCell(0);
        cell_1A_sheet2.setCellValue(new HSSFRichTextString("Estimated"));

        HSSFRow sheet2_row2 = secondSheet.createRow(1);
        HSSFCell cell_2A_sheet2 = sheet2_row2.createCell(0);
        cell_2A_sheet2.setCellValue(new HSSFRichTextString("Timestamp"));

        HSSFCell cell_2B_sheet2 = sheet2_row2.createCell(1);
        cell_2B_sheet2.setCellValue(new HSSFRichTextString("X"));

        HSSFCell cell_2C_sheet2 = sheet2_row2.createCell(2);
        cell_2C_sheet2.setCellValue(new HSSFRichTextString("Y"));

//        HSSFCell cell_2D_sheet2 = sheet2_row2.createCell(3);
//        cell_2D_sheet2.setCellValue(new HSSFRichTextString("VectorU_x"));
//
//        HSSFCell cell_2E_sheet2 = sheet2_row2.createCell(4);
//        cell_2E_sheet2.setCellValue(new HSSFRichTextString("VectorU_y"));
//
//        HSSFCell cell_2F_sheet2 = sheet2_row2.createCell(5);
//        cell_2F_sheet2.setCellValue(new HSSFRichTextString("Velocity_X"));
//
//        HSSFCell cell_2G_sheet2 = sheet2_row2.createCell(6);
//        cell_2G_sheet2.setCellValue(new HSSFRichTextString("Velocity_Y"));

        HSSFCell cell_2D_sheet2 = sheet2_row2.createCell(3);
        cell_2D_sheet2.setCellValue(new HSSFRichTextString("Latitude"));

        HSSFCell cell_2E_sheet2 = sheet2_row2.createCell(4);
        cell_2E_sheet2.setCellValue(new HSSFRichTextString("Longitude"));

        HSSFCell cell_2F_sheet2 = sheet2_row2.createCell(5);
        cell_2F_sheet2.setCellValue(new HSSFRichTextString("Longi_Distance_Est_GT_[m]"));

        HSSFCell cell_2G_sheet2 = sheet2_row2.createCell(6);
        cell_2G_sheet2.setCellValue(new HSSFRichTextString("Lati_Distance_Est_GT_[m]"));

        HSSFCell cell_2H_sheet2 = sheet2_row2.createCell(7);
        cell_2H_sheet2.setCellValue(new HSSFRichTextString("Longi_Distance_GNSS_GT_[m]"));

        HSSFCell cell_2I_sheet2 = sheet2_row2.createCell(8);
        cell_2I_sheet2.setCellValue(new HSSFRichTextString("Lati_Distance_GNSS_GT_[m]"));

        HSSFCell cell_2J_sheet2 = sheet2_row2.createCell(9);
        cell_2J_sheet2.setCellValue("Latitude_GT");

        HSSFCell cell_2K_sheet2 = sheet2_row2.createCell(10);
        cell_2K_sheet2.setCellValue("Longitude_GT");

        HSSFCell cell_2L_sheet2 = sheet2_row2.createCell(11);
        cell_2L_sheet2.setCellValue("Absolute_Distance_Est_GT_[m]");

        HSSFCell cell_2M_sheet2 = sheet2_row2.createCell(12);
        cell_2M_sheet2.setCellValue("Absolute_Distance_GNSS_GT_[m]");

        // Originale Punkte (im WGS-Format) in Sheet 3
        HSSFRow sheet3_row1 = thirdSheet.createRow(0);
        HSSFCell cell_1A_sheet3 = sheet3_row1.createCell(0);
        cell_1A_sheet3.setCellValue(new HSSFRichTextString("Original_WGS"));

        HSSFRow sheet3_row2 = thirdSheet.createRow(1);
        HSSFCell cell_2A_sheet3 = sheet3_row2.createCell(0);
        cell_2A_sheet3.setCellValue(new HSSFRichTextString("Timestamp"));

        HSSFCell cell_2B_sheet3 = sheet3_row2.createCell(1);
        cell_2B_sheet3.setCellValue(new HSSFRichTextString("Latitude"));

        HSSFCell cell_2C_sheet3 = sheet3_row2.createCell(2);
        cell_2C_sheet3.setCellValue(new HSSFRichTextString("Longitude"));

        HSSFCell cell_2D_sheet3 = sheet3_row2.createCell(3);
        cell_2D_sheet3.setCellValue(new HSSFRichTextString("Altitude"));

        double oldCartesian_x =0;
        double oldCartesian_y = 0;

        int i =2;
        // ===================================== Originale kartesische Punkte
        for(Data d : Service2.getListOfAllData()) {
            long currentTimestamp = d.getTimestamp();

            double cartesian_x = d.getCartesian_x();
            double cartesian_y = d.getCartesian_y();

            // Zeiche jede Position nur einmal: wegen aktueller Struktur zeichnen wir Daten mehrmals
            if(cartesian_x == oldCartesian_x || cartesian_y == oldCartesian_y) {
                continue;
            }
            oldCartesian_x = cartesian_x;
            oldCartesian_y = cartesian_y;

            HSSFRow currentRow = firstSheet.createRow(i);
            HSSFCell originalTimestamp = currentRow.createCell(0);
            HSSFCell originalX = currentRow.createCell(1);
            HSSFCell originalY = currentRow.createCell(2);

            originalTimestamp.setCellValue(currentTimestamp);
            originalX.setCellValue(cartesian_x);
            originalY.setCellValue(cartesian_y);

            i++;
        }

        // ===================================== Zeichne die geschätzten Punkte
        i = 2;
        int j = 0;
        for(Data d : Service2.getListOfAllData()) {
            long currentTimestamp = d.getTimestamp();
            double estimatedPoint_x = d.getEstimatedPoint_x();
            double estimatedPoint_y = d.getEstimatedPoint_y();

            // Schreibe nur diejenigen Datensätze, die auch geschätzte Punkte haben
            // Wir überspringen Punkte, wegen definierter Schätzfrequenz
            if(estimatedPoint_x == 0 || estimatedPoint_y == 0) {
                continue;
            }

            // Schreibe ausserdem die geschätzten WGS-Koordinaten
            double estimatedLat = d.getEstimatedLat();
            double estimatedLon = d.getEstimatedLon();

            // Schreibe auch die longitudinale und laterale Distanz zur GT-Position
            double latiDistanceEstToGt = d.getLatiDistanceEstToGtWithDirection();
            double longiDistanceEstToGt = d.getLongiDistanceEstToGtWithDirection();
            double latiDistanceGNSSToGt = d.getLatiDistanceGnssToGtWithDirection();
            double longiDistanceGNSSToGt = d.getLongiDistanceGnssToGtWithDirection();
            double latitude_gt = d.getLatitude_gt();
            double longitude_gt = d.getLongitude_gt();
            // Schreibe auch den absoluten Abstand zwischen Est <--> GT und GNSS <--> GT
            double absoluteDistanceEstGtValue = d.getAbsoluteDistanceEstGt();
            double absoluteDistanceGnssGtValue = d.getAbsoluteDistanceGnssGt();

            HSSFRow currentRow = secondSheet.createRow(i);
            HSSFCell estimatedTimestamp = currentRow.createCell(0);
            HSSFCell estimatedX = currentRow.createCell(1);
            HSSFCell estimatedY = currentRow.createCell(2);
//            HSSFCell vectorUX = currentRow.createCell(3);
//            HSSFCell vectorUY = currentRow.createCell(4);
//            HSSFCell velocityX = currentRow.createCell(5);
//            HSSFCell velocityY = currentRow.createCell(6);
            HSSFCell latitudeOfPoint = currentRow.createCell(3);
            HSSFCell longitudeOfPoint = currentRow.createCell(4);
            HSSFCell lonDistance_Est_GT = currentRow.createCell(5);
            HSSFCell latDistance_Est_GT = currentRow.createCell(6);
            HSSFCell lonDistance_GNSS_GT = currentRow.createCell(7);
            HSSFCell latDistance_GNSS_GT = currentRow.createCell(8);
            HSSFCell latitude_GT = currentRow.createCell(9);
            HSSFCell longitude_GT = currentRow.createCell(10);
            HSSFCell absoluteDistanceEstGt = currentRow.createCell(11);
            HSSFCell absoluteDistanceGnssGt = currentRow.createCell(12);

            estimatedTimestamp.setCellValue(currentTimestamp);
            estimatedX.setCellValue(estimatedPoint_x);
            estimatedY.setCellValue(estimatedPoint_y);
            latitudeOfPoint.setCellValue(estimatedLat);
            longitudeOfPoint.setCellValue(estimatedLon);
            lonDistance_Est_GT.setCellValue(longiDistanceEstToGt);
            latDistance_Est_GT.setCellValue(latiDistanceEstToGt);
            lonDistance_GNSS_GT.setCellValue(longiDistanceGNSSToGt);
            latDistance_GNSS_GT.setCellValue(latiDistanceGNSSToGt);
            latitude_GT.setCellValue(latitude_gt);
            longitude_GT.setCellValue(longitude_gt);
            absoluteDistanceEstGt.setCellValue(absoluteDistanceEstGtValue);
            absoluteDistanceGnssGt.setCellValue(absoluteDistanceGnssGtValue);

            i++;
        }

        // Schreibe die berechneten RMSE-Werte in sheet2, ganz ans Ende
        HSSFRow lastRowAfterEstPoints = secondSheet.createRow(i);
        lastRowAfterEstPoints.createCell(4).setCellValue("RMSE-Werte");
        HSSFCell rmseLonEstGt = lastRowAfterEstPoints.createCell(5);
        HSSFCell rmseLatEstGt = lastRowAfterEstPoints.createCell(6);
        HSSFCell rmseLonGnssGt = lastRowAfterEstPoints.createCell(7);
        HSSFCell rmseLatGnssGt = lastRowAfterEstPoints.createCell(8);
        HSSFCell rmseAbsDistanceEstGt = lastRowAfterEstPoints.createCell(11); // Spalte 9 & 10 = la/lon-GT
        HSSFCell rmseAbsDistanceGnssGt = lastRowAfterEstPoints.createCell(12);

        rmseLonEstGt.setCellValue(Service2.getRmseLongiEstGt());
        rmseLatEstGt.setCellValue(Service2.getRmseLatiEstGt());
        rmseLonGnssGt.setCellValue(Service2.getRmseLongiGnssGt());
        rmseLatGnssGt.setCellValue(Service2.getRmseLatiGnssGt());
        rmseAbsDistanceEstGt.setCellValue(Service2.getRmseAbsoluteDistanceEstGt());
        rmseAbsDistanceGnssGt.setCellValue(Service2.getRmseAbsoluteDistanceGnssGt());

        i = 2;
        double oldLatitude =0;
        double oldLongitude =0;
        // ===================================== Schreibe die originalen WGS-Punkte
        for(Data d : Service2.getListOfAllData()) {
            long currentTimestamp = d.getTimestamp();
            double latitude_wgs = d.getLatitude_wgs();
            double longitude_wgs = d.getLongitude_wgs();
            double altitude_wgs = d.getAltitude_wgs();

            // Zeiche jede Position nur einmal: wegen aktueller Struktur zeichnen wir Daten mehrmals
            if(latitude_wgs == oldLatitude || longitude_wgs == oldLongitude) {
                continue;
            }

            oldLatitude = latitude_wgs;
            oldLongitude = longitude_wgs;

            HSSFRow currentRow = thirdSheet.createRow(i);
            HSSFCell originalWGS_timestamp = currentRow.createCell(0);
            HSSFCell originalWGS_latitude = currentRow.createCell(1);
            HSSFCell originalWGS_longitude = currentRow.createCell(2);
            HSSFCell originalWGS_altitude = currentRow.createCell(3);

            originalWGS_timestamp.setCellValue(currentTimestamp);
            originalWGS_latitude.setCellValue(latitude_wgs);
            originalWGS_longitude.setCellValue(longitude_wgs);
            originalWGS_altitude.setCellValue(altitude_wgs);

            i++;
        }

        // ======================Schreibe alles in ein file
        try {
            String fileName = "export_" +
                    new Timestamp(System.currentTimeMillis()).toString()
                            .replaceAll("\\s", "_")
                            .replaceAll(":", "-")
                            .replaceAll("\\.", "-").concat(".xls");
            FileOutputStream fos = new FileOutputStream(fileName);
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
