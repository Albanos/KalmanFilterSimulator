import com.opencsv.CSVReader;
import geodesy.GlobalPosition;

import java.io.FileReader;
import java.io.IOException;

/**
 * Alggmeiner Erklärung:
 * Die erste Datei (FILE 1) ist nach der Position gefiltert. Diese enthählt somit nur einen Datensatz pro sekunde. Die
 * zweite Datei (FILE 2) ist nach IMU gefiltert: entält sehr viel mehr rows aber weniger Spalten!!!
 */
public class CsvReader {
    private static CSVReader reader = null;

    /**
     * Liest Daten vom Typ2 (mehr IMU-Daten) ein. Handelt auch den Case, dass die Lat/Lon oefter vorkommt. Speichert
     * diese dann nur einmal ab!
     *
     * @param pathToFile
     */
//    public void readCsvDataOfFileType2(String pathToFile) {
//        boolean firstIteration = true;
//        try {
//            reader = new CSVReader(new FileReader(pathToFile));
//            String[] line;
//            while ((line = reader.readNext()) != null) {
//
//                // Überspringe den Header und die Zeilen, wo keine Position vorliegt
//                if (firstIteration || line[50].contains("NaN")) {
//                    firstIteration = false;
//                    continue;
//                }
//
//                // Berücksichtige nur den ersten Teststrecken-Abschnitt, also nur von A -> B.
//                // Lesen wir das Label STOP_.... sind wir fertig
//                if (line[1].contains("STOP")) {
//                    break;
//                }
//
//                Coordinates c = new Coordinates();
//
//                //m.setTimestamp(Double.valueOf(line[0]));
//                c.setTimestamp(Double.valueOf(line[0]));
//
//                // set lat/lon and lat_gt/lon_gt --> FILE 2
//                c.setLongitude(Double.valueOf(line[50]));
//                c.setLatitude(Double.valueOf(line[51]));
//                c.setAltitude(Double.valueOf(line[52]));
//
//                // set firstGlobalPosition (for geodesy)
//                if (Service.getFirstPosition() == null) {
//                    Service.setFirstPosition(new GlobalPosition(
//                            c.getLatitude(),
//                            c.getLongitude(),
//                            c.getAltitude()
//                    ));
//                }
//
//                // set gnss-accuracy --> FILE 2
//                c.setAccuracy(Double.valueOf(line[62]));
//
//                //Service.getListOfAllMeasurements().add(m);
//                // Handle den Fall, dass im Daten-Type2 die selbe Location öfter vorkommt
//                if (Service.getListOfAllWGSPositions().isEmpty()) {
//                    Service.getListOfAllWGSPositions().add(c);
//
//                    // generate globalPositions for all positions
//                    Service.getListOfAllGlobalPositions().add(new GlobalPosition(
//                            c.getLatitude(),
//                            c.getLongitude(),
//                            c.getAltitude()
//                    ));
//
//                } else {
//                    Coordinates lastWGSPosition = Service.getListOfAllWGSPositions().getLast();
//                    if (lastWGSPosition.getLatitude() != c.getLatitude()) {
//                        Service.getListOfAllWGSPositions().add(c);
//
//                        // generate globalPositions for all positions
//                        Service.getListOfAllGlobalPositions().add(new GlobalPosition(
//                                c.getLatitude(),
//                                c.getLongitude(),
//                                c.getAltitude()
//                        ));
//
//                    }
//                }
//
//
//                // Speiechere die IMU-Werte in einer separate Liste
//                saveIMUSeparate(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void readCsvDataOfOwnFormat(String pathToFile) {
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
                saveIMUSeparateOwnFormat(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveIMUSeparateOwnFormat(String[] line) {
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
        data.setSpeed_x(data.getAmountGnss() * Math.sin(Math.toRadians(data.getBearingGnss())));
        data.setSpeed_y(data.getAmountGnss() * Math.cos(Math.toRadians(data.getBearingGnss())));

        data.setTimestamp(line[0]);

        //Service.getOnlyIMUValues().add(m);
        Service.getListOfAllImuValues().add(data);
    }

//    private void saveIMUSeparate(String[] line) {
//        Measure m = new Measure();
//        // set IMU-values --> FILE 2
//        m.setAccel_x(Double.valueOf(line[12]));
//        m.setAccel_y(Double.valueOf(line[13]));
//        m.setAccel_z(Double.valueOf(line[14]));
//        m.setMagnitude_x(Double.valueOf(line[3]));
//        m.setMagnitude_y(Double.valueOf(line[4]));
//        m.setMagnitude_z(Double.valueOf(line[5]));
//        m.setGravity_x(Double.valueOf(line[30]));
//        m.setGravity_y(Double.valueOf(line[31]));
//        m.setGravity_z(Double.valueOf(line[32]));
//
//        // set GNSS-Speed (amount of speed), GNSS-Bearing and speed in x- and y-orientation --> FILE 2
//        m.setAmountGnss(Double.valueOf(line[58]));
//        m.setBearingGnss(Double.valueOf(line[53]));
//        m.setSpeed_x(m.getAmountGnss() * Math.sin(Math.toRadians(m.getBearingGnss())));
//        m.setSpeed_y(m.getAmountGnss() * Math.cos(Math.toRadians(m.getBearingGnss())));
//
//        m.setTimestamp(line[0]);
//
//        Service.getOnlyIMUValues().add(m);
//
//    }

//    public void readCsvDataOfFilteType1(String pathToFile) {
//        boolean firstIteration = true;
//        try {
//            reader = new CSVReader(new FileReader(pathToFile));
//            String[] line;
//            while ((line = reader.readNext()) != null) {
//
//                // Überspringe den Header und die Zeilen, wo keine Position vorliegt
//                if (firstIteration) {
//                    firstIteration = false;
//                    continue;
//                }
//
//                // Berücksichtige nur den ersten Teststrecken-Abschnitt, also nur von A -> B.
//                // Lesen wir das Label STOP_.... sind wir fertig
//                if (line[1].contains("STOP")) {
//                    break;
//                }
//
//                Measure m = new Measure();
//
//                m.setTimestamp(line[0]);
//
//                // set lat/lon and lat_gt/lon_gt --> FILE 1
//                m.setLongitude(Double.valueOf(line[51]));
//                m.setLatitude(Double.valueOf(line[53]));
//                m.setAltitude(Double.valueOf(line[55]));
//
//                // set firstGlobalPosition (for geodesy)
//                if (Service.getFirstPosition() == null) {
//                    Service.setFirstPosition(new GlobalPosition(
//                            m.getLatitude(),
//                            m.getLongitude(),
//                            m.getAltitude()
//                    ));
//                }
//
//                // generate globalPositions for all positions
//                Service.getListOfAllGlobalPositions().add(new GlobalPosition(
//                        m.getLatitude(),
//                        m.getLongitude(),
//                        m.getAltitude()
//                ));
//
//
//                // FILE 1
//                m.setLongitude_gt(Double.valueOf(line[52]));
//                m.setLatitude_gt(Double.valueOf(line[54]));
//
//                // set IMU-values --> FILE 1
//                m.setAccel_x(Double.valueOf(line[2]));
//                m.setAccel_y(Double.valueOf(line[3]));
//                m.setAccel_z(Double.valueOf(line[4]));
//                m.setMagnitude_x(Double.valueOf(line[29]));
//                m.setMagnitude_y(Double.valueOf(line[30]));
//                m.setMagnitude_z(Double.valueOf(line[31]));
//                m.setGravity_x(Double.valueOf(line[8]));
//                m.setGravity_y(Double.valueOf(line[9]));
//                m.setGravity_z(Double.valueOf(line[10]));
//
//                // set GNSS-Speed (amount of speed), GNSS-Bearing and speed in x- and y-orientation --> FILE 1
//                m.setAmountGnss(Double.valueOf(line[68]));
//                m.setBearingGnss(Double.valueOf(line[63]));
//                m.setSpeed_x(m.getAmountGnss() * Math.sin(Math.toRadians(m.getBearingGnss())));
//                m.setSpeed_y(m.getAmountGnss() * Math.cos(Math.toRadians(m.getBearingGnss())));
//
//                // set gnss-accuracy --> FILE 1
//                m.setGnssAccuracy(Double.valueOf(line[72]));
//
//                Service.getListOfAllMeasurements().add(m);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
