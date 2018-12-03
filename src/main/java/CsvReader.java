import com.opencsv.CSVReader;
import geodesy.GlobalPosition;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Algemeine Erklärung:
 * Die Daten werden nur nach einem bestimmten Muster/nach einer bestimmten Reihenfolge eingelesen. Die Positionsangaben
 * werden getrennt von den IMU-Daten gespeichert
 */
public class CsvReader {
    private static CSVReader reader = null;
    private static long timeWithoutPoint;

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
                timeWithoutPoint = Long.valueOf(timestampWithoutPoint);
                c.setTimestamp(timeWithoutPoint);


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
                // set gnss-accuracy --> FILE 2
                c.setAccuracy(Double.valueOf(line[8]));

                saveWgsPositionsAndGlobalsPositions(c);

                // Speiechere die GT-Positionen in einer separaten Liste
                saveGTPositions(line);

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
        c.setLongitude_GT(Double.valueOf(line[6]));
        c.setLatitude_GT(Double.valueOf(line[7]));
        c.setTimestamp(timeWithoutPoint);
        // Handle ebenso die GT-Positionen -> in separater Liste abspeichern
        // ANNAHME HIER: Benuter bewegt sich, also keine Position zweimal/mehrmals
        Service.getListOfAllGTWgsPositions().add(c);
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
        //String timestampWithoutPoint = line[0].replaceAll("\\.", "");
        data.setTimestamp(timeWithoutPoint);

        //Service.getOnlyIMUValues().add(m);
        Service.getListOfAllImuValues().add(data);
    }

    //================================================================
    public void readAllFromCsvFile(String pathToFile) {
        String[] line;
        try {
            reader = new CSVReader(new FileReader(pathToFile));
            reader.skip(1);

            while ((line = reader.readNext()) != null) {
                // ignoriere die rows, wo keine Position vorliegt
                if(line[1].startsWith("NaN")) {
                    continue;
                }

                // höre auf zu lesen, wenn du das erste STOP liest = erstes Segment
                if(line[18].startsWith("STOP")) {
                    break;
                }

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

                Service2.getListOfAllData().add(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
