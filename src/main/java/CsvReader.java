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
                //if (line[18].contains("GO_12700_0")) {
                if(line[18].contains("STOP")) {
                    break;
                }

                Coordinates c = new Coordinates();

                // Entferne aus dem Timestamp den Punkt
                String timestampWithoutPoint = line[0].replaceAll("\\.", "");
                c.setTimestamp(Long.valueOf(timestampWithoutPoint));


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

                // Set GNSS-Bearing, amount of GNSS-speed and x- & y-GNSS-speed
                c.setBearing_gnss(Double.valueOf(line[4]));
                c.setAmountSpeedGnss(Double.valueOf(line[5]));
                c.setSpeed_x_gnss(c.getAmountSpeedGnss() * Math.sin(Math.toRadians(c.getBearing_gnss())));
                c.setSpeed_y_gnss(c.getAmountSpeedGnss() * Math.cos(Math.toRadians(c.getBearing_gnss())));

                saveWgsPositionsAndGlobalsPositions(c);

                // Speiechere die GT-Positionen in einer separaten Liste
                saveGTPositions(line);

                // set gnss-accuracy --> FILE 2
                c.setAccuracy(Double.valueOf(line[8]));

                // Speiechere die IMU-Werte in einer separate Liste
                saveIMUValuesInPojo(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setze nun auch für jeden Accel-Value den WGS-Accel-Value
        Service.calculateWgsAccelOfAllImuValues();
    }

    private void saveWgsPositionsAndGlobalsPositions(Coordinates c) {
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
            if (lastWGSPosition.getLatitude() != c.getLatitude() && lastWGSPosition.getLongitude() != c.getLongitude()) {
                Service.getListOfAllWGSPositions().add(c);

                // generate globalPositions for all positions
                Service.getListOfAllGlobalPositions().add(new GlobalPosition(
                        c.getLatitude(),
                        c.getLongitude(),
                        c.getAltitude()
                ));

            }
        }
    }

    private void saveGTPositions(String[] line) {
        Coordinates c = new Coordinates();
        // Handle ebenso die GT-Positionen -> in separater Liste abspeichern
        // ANNAHME HIER: Benuter bewegt sich, also keine Position zweimal/mehrmals
        if (Service.getListOfAllGTWgsPositions().isEmpty()) {
            c.setLongitude_GT(Double.valueOf(line[6]));
            c.setLatitude_GT(Double.valueOf(line[7]));

            Service.getListOfAllGTWgsPositions().add(c);
        }
        else {
            Coordinates lastGtPosition = Service.getListOfAllGTWgsPositions().getLast();
            if(lastGtPosition.getLatitude_GT() != c.getLatitude_GT()) {
                c.setLongitude_GT(Double.valueOf(line[6]));
                c.setLatitude_GT(Double.valueOf(line[7]));

                Service.getListOfAllGTWgsPositions().add(c);
            }
        }
    }

    private void saveIMUValuesInPojo(String[] line) {
        //Measure m = new Measure();
        ImuValues data = new ImuValues();
        // set IMU-values --> FILE 2
        data.setAccel_x(Double.valueOf(line[9]));
        data.setAccel_y(Double.valueOf(line[10]));
        data.setAccel_z(Double.valueOf(line[11]));
        data.setMagnitude_x(Double.valueOf(line[12]));
        data.setMagnitude_y(Double.valueOf(line[13]));
        data.setMagnitude_z(Double.valueOf(line[14]));
        data.setGravity_x(Double.valueOf(line[15]));
        data.setGravity_y(Double.valueOf(line[16]));
        data.setGravity_z(Double.valueOf(line[17]));

        // Entferne Punkt aus Timestamp
        String timestampWithoutPoint = line[0].replaceAll("\\.", "");
        data.setTimestamp(Long.valueOf(timestampWithoutPoint));

        //Service.getOnlyIMUValues().add(m);
        Service.getListOfAllImuValues().add(data);
    }
}
