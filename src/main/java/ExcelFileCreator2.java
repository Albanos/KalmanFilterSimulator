import org.apache.poi.hssf.usermodel.*;

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

        HSSFCell cell_2M_sheet2 = sheet2_row2.createCell(12);
        cell_2M_sheet2.setCellValue(new HSSFRichTextString(("Lat_GT")));

        HSSFCell cell_2N_sheet2 = sheet2_row2.createCell(13);
        cell_2N_sheet2.setCellValue(new HSSFRichTextString("Lon_GT"));

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

        int i =2;
        // ===================================== Originale kartesische Punkte
        for(Data d : Service2.getListOfAllData()) {
            long currentTimestamp = d.getTimestamp();

            double cartesian_x = d.getCartesian_x();
            double cartesian_y = d.getCartesian_y();

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

        }
    }
}