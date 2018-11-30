import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author Luan Hajzeraj on 16.11.2018.
 */
public class ExcelFileCreator {

    public void writeCartesianPointsToFile() {
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

        HSSFCell cell_2D_sheet2 = sheet2_row2.createCell(3);
        cell_2D_sheet2.setCellValue(new HSSFRichTextString("VectorU_x"));

        HSSFCell cell_2E_sheet2 = sheet2_row2.createCell(4);
        cell_2E_sheet2.setCellValue(new HSSFRichTextString("VectorU_y"));

        HSSFCell cell_2F_sheet2 = sheet2_row2.createCell(5);
        cell_2F_sheet2.setCellValue(new HSSFRichTextString("Velocity_X"));

        HSSFCell cell_2G_sheet2 = sheet2_row2.createCell(6);
        cell_2G_sheet2.setCellValue(new HSSFRichTextString("Velocity_Y"));

        HSSFCell cell_2H_sheet2 = sheet2_row2.createCell(7);
        cell_2H_sheet2.setCellValue(new HSSFRichTextString("Latitude"));

        HSSFCell cell_2I_sheet2 = sheet2_row2.createCell(8);
        cell_2I_sheet2.setCellValue(new HSSFRichTextString("Longitude"));

        HSSFCell cell_2J_sheet2 = sheet2_row2.createCell(9);
        cell_2J_sheet2.setCellValue(new HSSFRichTextString("Long_Distance_Est_GT_[m]"));

        HSSFCell cell_2K_sheet2 = sheet2_row2.createCell(10);
        cell_2K_sheet2.setCellValue(new HSSFRichTextString("Lat_Distance_Est_GT_[m]"));

        HSSFCell cell_2L_sheet2 = sheet2_row2.createCell(11);
        cell_2L_sheet2.setCellValue(new HSSFRichTextString("Timestamp_GT_Position"));

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

        // Zeichne die originalen kartesischen Punkte
        int i = 2;
        for (CartesianPoint p : Service.getListOfAllCartesianPoints()) {
            String timestamp = Long.toString(p.getTimestamp());

            Double x = p.getX();
            Double y = p.getY();

            HSSFRow currentRow = firstSheet.createRow(i);
            HSSFCell originalTimestamp = currentRow.createCell(0);
            HSSFCell originalX = currentRow.createCell(1);
            HSSFCell originalY = currentRow.createCell(2);

            originalTimestamp.setCellValue(timestamp);
            originalX.setCellValue(x);
            originalY.setCellValue(y);
            i++;
        }

        // Zeichne die geschätzten Punkte
        i = 2;
        int j = 0;
        for (CartesianPoint p : Service.getListOfAllEstimatedCartesianPoints()) {
            //String timestamp = Long.toString(p.getTimestamp());
            long timestamp = p.getTimestamp();
            Double x = p.getX();
            Double y = p.getY();

            // Zeichne ausserdem noch die Globalen Koordinaten des jeweils geschätzten Punktes
            // Existiert für diesen Punkt kein Eintrag in der Liste ist es der erste Punkt (für ihn werden keine WGS-Koordinaten berechnet)
            double latitudeOfP = Service.getPointToWGSMap().get(p) == null ? 0 : Service.getPointToWGSMap().get(p).getLatitude();
            double longitudeOfP = Service.getPointToWGSMap().get(p) == null ? 0 : Service.getPointToWGSMap().get(p).getLongitude();

            // Zeichne ausserdem den Abstand zwischen geschätzter- & GT-Position
            double latDistanceEstPstGtPst =0;
            double lonDistanceEstPstGtPst = 0;
            long time_GT_position = 0;
            Coordinates wgsPositionOfCurrentEstimatedPoint = Service.getPointToWGSMap().get(p);
            time_GT_position = wgsPositionOfCurrentEstimatedPoint.getTimestamp();

            if(Service.getEstimatedWgsPositionGtLongitudinalDistanceMap().get(wgsPositionOfCurrentEstimatedPoint) != 0) {
                lonDistanceEstPstGtPst = Service.getEstimatedWgsPositionGtLongitudinalDistanceMap().get(wgsPositionOfCurrentEstimatedPoint);
            }

            if(Service.getEstimatedWgsPositionGtLateralDistanceMap().get(wgsPositionOfCurrentEstimatedPoint) != 0) {
                latDistanceEstPstGtPst = Service.getEstimatedWgsPositionGtLateralDistanceMap().get(wgsPositionOfCurrentEstimatedPoint);
            }
//            if (Service.getEstimatedWgsPositionGtDistanceMap().get(wgsPositionOfCurrentEstimatedPoint) != 0) {
//                distanceEstPstGtPst = Service.getEstimatedWgsPositionGtDistanceMap().get(wgsPositionOfCurrentEstimatedPoint);
//            }

            HSSFRow currentRow = secondSheet.createRow(i);
            HSSFCell estimatedTimestamp = currentRow.createCell(0);
            HSSFCell estimatedX = currentRow.createCell(1);
            HSSFCell estimatedY = currentRow.createCell(2);
            HSSFCell vectorUX = currentRow.createCell(3);
            HSSFCell vectorUY = currentRow.createCell(4);
            HSSFCell velocityX = currentRow.createCell(5);
            HSSFCell velocityY = currentRow.createCell(6);
            HSSFCell latitudeOfPoint = currentRow.createCell(7);
            HSSFCell longitudeOfPoint = currentRow.createCell(8);
            //HSSFCell distanceEstGt = currentRow.createCell(9);
            HSSFCell lonDistanceEstWgs_GT = currentRow.createCell(9);
            HSSFCell latDistanceEstWgs_GT = currentRow.createCell(10);
            HSSFCell timestamp_GT_Position = currentRow.createCell(11);

            estimatedTimestamp.setCellValue(timestamp);
            estimatedX.setCellValue(x);
            estimatedY.setCellValue(y);
            latitudeOfPoint.setCellValue(latitudeOfP);
            longitudeOfPoint.setCellValue(longitudeOfP);
            //distanceEstGt.setCellValue(distanceEstPstGtPst);
            lonDistanceEstWgs_GT.setCellValue(lonDistanceEstPstGtPst);
            latDistanceEstWgs_GT.setCellValue(latDistanceEstPstGtPst);
            timestamp_GT_Position.setCellValue(time_GT_position);

            i++;
            j++;
        }

        i = 2;
        // Zeichne die originalen WGS Punkte
        for (Coordinates c : Service.getListOfAllWGSPositions()) {
            double latitude = c.getLatitude();
            double longitude = c.getLongitude();
            double altitude = c.getAltitude();
            String timestamp = Long.toString(c.getTimestamp());

            HSSFRow currentRow = thirdSheet.createRow(i);
            HSSFCell originalWGS_timestamp = currentRow.createCell(0);
            HSSFCell originalWGS_latitude = currentRow.createCell(1);
            HSSFCell originalWGS_longitude = currentRow.createCell(2);
            HSSFCell originalWGS_altitude = currentRow.createCell(3);

            originalWGS_timestamp.setCellValue(timestamp);
            originalWGS_latitude.setCellValue(latitude);
            originalWGS_longitude.setCellValue(longitude);
            originalWGS_altitude.setCellValue(altitude);

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
