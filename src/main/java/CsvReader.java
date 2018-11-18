import com.opencsv.CSVReader;
import geodesy.GlobalPosition;

import java.io.FileReader;
import java.io.IOException;

/**
 * Algemeine Erklärung:
 * Die Daten werden nur nach einem bestimmten Muster/nach einer bestimmten Reihenfolge eingelesen. Die Positionsangaben
 * werden getrennt von den IMU-Daten gespeichert
 */
public class CsvReader {
    private static CSVReader reader = null;

    public void readCsvDataAndSaveInPojo(String pathToFile) {
        boolean firstIteration = true;
        try {
            reader = new CSVReader(new FileReader(pathToFile));
            String[] line;
            while ((line = reader.readNext()) != null) {

                // Überspringe den Header und die Zeilen, wo keine Position vorliegt
                if (firstIteration || line[1].contains("NaN")) {
                    firstIteration = false;
                    continue;
                }

                // Berücksichtige nur den ersten Teststrecken-Abschnitt, also nur von A -> B.
                // Lesen wir das Label STOP_.... sind wir fertig
                if (line[16].contains("STOP")) {
                    break;
                }

                Coordinates c = new Coordinates();

                //m.setTimestamp(Double.valueOf(line[0]));
                c.setTimestamp(Double.valueOf(line[0]));

                // set lat/lon and lat_gt/lon_gt --> FILE 2
                c.setLongitude(Double.valueOf(line[1]));
                c.setLatitude(Double.valueOf(line[2]));
                c.setAltitude(Double.valueOf(line[3]));

                // set firstGlobalPosition (for geodesy)
                if (Service.getFirstPosition() == null) {
                    Service.setFirstPosition(new GlobalPosition(
                            c.getLatitude(),
                            c.getLongitude(),
                            c.getAltitude()
                    ));
                }

                // set gnss-accuracy --> FILE 2
                c.setAccuracy(Double.valueOf(line[4]));

                //Service.getListOfAllMeasurements().add(m);
                // Handle den Fall, dass im Daten-Type2 die selbe Location öfter vorkommt
                if (Service.getListOfAllWGSPositions().isEmpty()) {
                    Service.getListOfAllWGSPositions().add(c);

                    // generate globalPositions for all positions
                    Service.getListOfAllGlobalPositions().add(new GlobalPosition(
                            c.getLatitude(),
                            c.getLongitude(),
                            c.getAltitude()
                    ));

                } else {
                    Coordinates lastWGSPosition = Service.getListOfAllWGSPositions().getLast();
                    if (lastWGSPosition.getLatitude() != c.getLatitude()) {
                        Service.getListOfAllWGSPositions().add(c);

                        // generate globalPositions for all positions
                        Service.getListOfAllGlobalPositions().add(new GlobalPosition(
                                c.getLatitude(),
                                c.getLongitude(),
                                c.getAltitude()
                        ));

                    }
                }


                // Speiechere die IMU-Werte in einer separate Liste
                saveIMUValuesInPojo(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setze nun auch für jeden Accel-Value den WGS-Accel-Value
        Service.calculateWgsAccelOfAllImuValues();
    }

    private void saveIMUValuesInPojo(String[] line) {
        //Measure m = new Measure();
        ImuValues data = new ImuValues();
        // set IMU-values --> FILE 2
        data.setAccel_x(Double.valueOf(line[5]));
        data.setAccel_y(Double.valueOf(line[6]));
        data.setAccel_z(Double.valueOf(line[7]));
        data.setMagnitude_x(Double.valueOf(line[8]));
        data.setMagnitude_y(Double.valueOf(line[9]));
        data.setMagnitude_z(Double.valueOf(line[10]));
        data.setGravity_x(Double.valueOf(line[11]));
        data.setGravity_y(Double.valueOf(line[12]));
        data.setGravity_z(Double.valueOf(line[13]));

        // set GNSS-Speed (amount of speed), GNSS-Bearing and speed in x- and y-orientation --> FILE 2
        data.setAmountGnss(Double.valueOf(line[14]));
        data.setBearingGnss(Double.valueOf(line[15]));
        data.setSpeed_x_wgs(data.getAmountGnss() * Math.sin(Math.toRadians(data.getBearingGnss())));
        data.setSpeed_y_wgs(data.getAmountGnss() * Math.cos(Math.toRadians(data.getBearingGnss())));

        data.setTimestamp(line[0]);

        //Service.getOnlyIMUValues().add(m);
        Service.getListOfAllImuValues().add(data);
    }
}
