import com.opencsv.CSVReader;
import geodesy.GlobalPosition;

import java.io.FileReader;
import java.io.IOException;

public class CsvReader {
    private static CSVReader reader = null;
    private static long timeWithoutPoint;
    private boolean readFirstMark = false;

    public void readAllSegmentsFromfile(String pathToFile) {
        String[] line;
        try {
            reader = new CSVReader(new FileReader(pathToFile));
            reader.skip(1);

            while ((line = reader.readNext()) != null) {
                // Überspringe die Datensätze, die mit stop gelabelt sind
                if (line[1].startsWith("NaN") || line[18].startsWith("STOP")) {
                    continue;
                }

                generateDataObjectAndSaveAllData(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readAllFromCsvFile(String pathToFile, String[] segment) {
        String[] line;
        try {
            reader = new CSVReader(new FileReader(pathToFile));
            reader.skip(1);

            while ((line = reader.readNext()) != null) {
                // ignoriere die rows, wo keine Position vorliegt
                //if(line[1].startsWith("NaN") || !(line[18].startsWith("GO_" + segment[0])) && !(line[18].startsWith("STOP_" + segment[1]))) {
                if (line[1].startsWith("NaN") ||
                        !((line[18].startsWith("GO_" + segment[0])))) {
                    continue;
                }

                // höre auf zu lesen, wenn du das erste STOP liest = erstes Segment
                //if(line[18].startsWith("STOP_" + segment[1])) {
                else if (line[18].startsWith("STOP_" + segment[1])) {
                    break;
                }

                generateDataObjectAndSaveAllData(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateDataObjectAndSaveAllData(String[] line) {
        Data d = new Data();

        // Entferne Punkt aus timestamp
        String timestampWithoutPoint = line[0].replaceAll("\\.", "");
        timeWithoutPoint = Long.valueOf(timestampWithoutPoint);
        d.setTimestamp(timeWithoutPoint);

        d.setLongitude_wgs(Double.valueOf(line[1]));
        d.setLatitude_wgs(Double.valueOf(line[2]));
        d.setAltitude_wgs(Double.valueOf(line[3]));

        // setze erste GlobalPosition (for geodesy)
        if (Service2.getFirstGlobalPosition() == null) {
            Service2.setFirstGlobalPosition(new GlobalPosition(
                    d.getLatitude_wgs(),
                    d.getLongitude_wgs(),
                    d.getAltitude_wgs()
            ));
        }

        // setze ebenso eine globale Position für lat/lon/alt
        d.setGlobalPosition(
                new GlobalPosition(
                        d.getLatitude_wgs(),
                        d.getLongitude_wgs(),
                        d.getAltitude_wgs()
                )
        );

        d.setBearing_wgs(Double.valueOf(line[4]));
        d.setAmountSpeed_wgs(Double.valueOf(line[5]));

        // Berechne auf Basis des Winkels und dem Betrag d. Geschw. den x- & y-Speed
        d.setSpeed_x_wgs(d.getAmountSpeed_wgs() * Math.sin(Math.toRadians(d.getBearing_wgs())));
        d.setSpeed_y_wgs(d.getAmountSpeed_wgs() * Math.cos(Math.toRadians(d.getBearing_wgs())));

        d.setLongitude_gt(Double.valueOf(line[6]));
        d.setLatitude_gt(Double.valueOf(line[7]));
        d.setAccuracy_gnss(Double.valueOf(line[8]));
        d.setAccel_x_imu(Double.valueOf(line[9]));
        d.setAccel_y_imu(Double.valueOf(line[10]));
        d.setAccel_z_imu(Double.valueOf(line[11]));
        d.setMagnetic_x_imu(Double.valueOf(line[12]));
        d.setMagnetic_y_imu(Double.valueOf(line[13]));
        d.setMagnetic_z_imu(Double.valueOf(line[14]));
        d.setGravitiy_x_imu(Double.valueOf(line[15]));
        d.setGravitiy_y_imu(Double.valueOf(line[16]));
        d.setGravitiy_z_imu(Double.valueOf(line[17]));

        // Setze auch die GT-direction für spätere Abstandsberechnung in und um Bewegungsrichtung
        d.setGtDirection(Double.valueOf(line[19]));

        Service2.getListOfAllData().add(d);
    }
}
