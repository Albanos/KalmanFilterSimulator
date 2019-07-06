import com.opencsv.CSVReader;
import geodesy.GlobalPosition;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class CsvReader {
    private static CsvReader instance = null;

    // statische variablen für die jeweilligen Spaltenpositionen innerhalb eines bestimmten files
    private static boolean firstIteration = true;
    private static int timestampPosition = -1;
    private static int latitudePosition = -1;
    private static int longitudePosition = -1;
    private static int altitudePosition = -1;
    private static int bearingPosition = -1;
    private static int gnssSpeedPosition = -1;
    private static int longitudeGtPosition = -1;
    private static int latitudeGtPosition = -1;
    private static int gpsAccuracyPosition = -1;
    private static int accelXPosition = -1;
    private static int accelYPosition = -1;
    private static int accelZPosition = -1;
    private static int magneticXPosition = -1;
    private static int magneticYPosition = -1;
    private static int magneticZPosition = -1;
    private static int gravityXPosition = -1;
    private static int gravityYPosition = -1;
    private static int gravityZPosition = -1;
    private static int gt_directionPosition = -1;
    private static int labelPosition = -1;
    private static int typeOrientation_x = -1;
    private static int typeStepDetectorCounter = -1;

    private static long oldTimeFromStepdetector = 0;
    private static int oldStepDetectorCount = 0;
    private static double oldAmoundOfVelocity_stepDetector = 0;
    private static double oldVel_x_StepDetector = 0;
    private static double oldVel_y_StepDetector = 0;


    private CsvReader() {
    }

    static CsvReader getInstance() {
        if (instance == null) {
            instance = new CsvReader();
        }
        return instance;
    }

    private Map<String, List<Data>> originalLinesBySegments = new LinkedHashMap<>();
    // Anzahl an GNSS-Positionen, pro Segment (für GT-Pos als Messung in Filter)
    private Map<String, Integer> gnssCounterBySegments = new LinkedHashMap<>();

//     FIXME: OLD
//    void readAllSegmentsFromfile(String pathToFile) {
//        String[] line;
//        try {
//            CSVReader reader = new CSVReader(new FileReader(pathToFile));
//            reader.skip(1);
//
//            while ((line = reader.readNext()) != null) {
//                // Überspringe die Datensätze, die mit stop gelabelt sind
//                if (line[1].startsWith("NaN") || line[18].startsWith("STOP")) {
//                    continue;
//                }
//
//                generateDataObjectAndSaveAllData(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    void readSegmentFromCsv(final String pathToFile, final String[] segment, boolean only10Meters, boolean only20Meters) {
        String[] line;
        // Wir haben eine kartesische Position weniger als WGS-Positionen!
        int positionCounter = -1;
        Double oldLongitude = null;
        Double oldLatitude = null;
        final List<Data> dataObjects = new LinkedList<>();
        try {
            final CSVReader reader = new CSVReader(new FileReader(pathToFile));
            reader.skip(1);
            // Ermittle die Spaltennummern der einzulesenden Datei genau einmal und speichere sie statisch
            if(firstIteration) {
                setColumnPositionValuesOfFile(pathToFile);
                firstIteration = false;
            }
            while ((line = reader.readNext()) != null) {
                // ignoriere die rows, wo keine Position vorliegt
                if ((line[longitudePosition].startsWith("NaN")) ||
                        !((line[labelPosition].startsWith("GO_" + segment[0])))) {
                    continue;
                }

                // Wenn nur 10m simuliert werden sollen (Car2P-Szenario), betrachten wir das Label "genauer": Schaue,
                // ob das label mit 10m beginnt, denn:
                // 0.....5......10 --> entspricht den 10m
                if(only10Meters) {
                    if (line[labelPosition].startsWith("GO_" + segment[0] + "_1")) {
                        break;
                    }
                }
                else if(only20Meters) {
                    if (line[labelPosition].startsWith("GO_" + segment[0] + "_2")) {
                        break;
                    }
                }
                else {
                    // höre auf zu lesen, wenn du das erste STOP liest = erstes Segment
                    //if(line[18].startsWith("STOP_" + segment[1])) {
                    if (line[labelPosition].startsWith("STOP_" + segment[1])) {
                        break;
                    }
                }

                dataObjects.add(generateDataObjectAndSaveAllData(line, pathToFile));
                // Zähle die GNSS-Positionen und speichere für akt. Segment (für GT-Auswertung im Filter)
                // Nur, wenn es auch eine gültige, also KEINE NaN-Position ist
                if(!Double.isNaN( ((LinkedList<Data>) dataObjects).getLast().getLongitude_wgs())
                        && !Double.isNaN( ((LinkedList<Data>) dataObjects).getLast().getLatitude_wgs())) {
                    Double currentLongitude = Double.valueOf(line[longitudePosition]);
                    Double currentLatitude = Double.valueOf(line[latitudePosition]);
                    if (!currentLongitude.equals(oldLongitude) && !currentLatitude.equals(oldLatitude)) {
                        positionCounter++;
                        oldLongitude = currentLongitude;
                        oldLatitude = currentLatitude;
                    }
                }
            }
            final String key = segment[0].concat("_").concat(segment[1]);
            if (!originalLinesBySegments.containsKey(key)) {
                originalLinesBySegments.put(key, dataObjects);
            }
            getGnssCounterBySegments().put(key, positionCounter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int findSpecificColumnByName(String nameOfColumn, String pathToFile) {
        try {
            final CSVReader reader = new CSVReader(new FileReader(pathToFile));
            String[] firstLineOfFile = reader.readNext();
            for(int i =0; i < firstLineOfFile.length; i++) {
                if(firstLineOfFile[i].equals(nameOfColumn)) {
                    return i;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Data generateDataObjectAndSaveAllData(final String[] line, String pathToFile) {
        final Data d = new Data();

        // Entferne Punkt aus timestamp
        d.setTimestamp(Long.valueOf(line[timestampPosition].replaceAll("\\.", "")));

        d.setLabel(line[labelPosition]);

        d.setLongitude_wgs(Double.valueOf(line[longitudePosition]));
        d.setLatitude_wgs(Double.valueOf(line[latitudePosition]));
        d.setAltitude_wgs(Double.valueOf(line[altitudePosition]));
//FIXME: gehört hier nicht hin
//        // setze erste GlobalPosition (for geodesy)
//        if (Service2.getFirstGlobalPosition() == null) {
//            Service2.setFirstGlobalPosition(new GlobalPosition(
//                    d.getLatitude_wgs(),
//                    d.getLongitude_wgs(),
//                    d.getAltitude_wgs()
//            ));
//        }
        // Berechne nur, wenn Location vorhanden ist. Für die Datensätze, wo keine Location vorhanden ist
        // benötigen wir auch keine Geschwindigkeit, weil dies eh keine gültige Messung ist (eben keine location). Wir
        // wollen aber gern darüber iterieren
        if(!Double.isNaN(d.getLongitude_wgs()) && !Double.isNaN(d.getLatitude_wgs()) && !Double.isNaN(d.getAltitude_wgs())) {
            // setze ebenso eine globale Position für lat/lon/alt
            d.setGlobalPosition(
                    new GlobalPosition(
                            d.getLatitude_wgs(),
                            d.getLongitude_wgs(),
                            d.getAltitude_wgs()
                    )
            );


            d.setBearing_wgs(Double.valueOf(line[bearingPosition]));
            //d.setBearing_wgs(Double.valueOf(line[typeOrientation_x]));
            d.setAmountSpeed_wgs(Double.valueOf(line[gnssSpeedPosition]));
            // Berechne auf Basis des Winkels und dem Betrag d. Geschw. den x- & y-Speed
            d.setSpeed_x_wgs(d.getAmountSpeed_wgs() * Math.sin(Math.toRadians(d.getBearing_wgs())));
            d.setSpeed_y_wgs(d.getAmountSpeed_wgs() * Math.cos(Math.toRadians(d.getBearing_wgs())));

            // Berechne die Geschwindigkeit (Betrag und in x- & y-Richtung)
            // aus Schritterkennng und speichere in Data-Objekt
            calculateCompleteVelocityByStepDetectorAndSetInDataObject(line[typeOrientation_x], line[typeStepDetectorCounter], d);

        }
        d.setLongitude_gt(Double.valueOf(line[longitudeGtPosition]));
        d.setLatitude_gt(Double.valueOf(line[latitudeGtPosition]));
        d.setAccuracy_gnss(Double.valueOf(line[gpsAccuracyPosition]));
        d.setAccel_x_imu(Double.valueOf(line[accelXPosition]));
        d.setAccel_y_imu(Double.valueOf(line[accelYPosition]));
        d.setAccel_z_imu(Double.valueOf(line[accelZPosition]));
        d.setMagnetic_x_imu(Double.valueOf(line[magneticXPosition]));
        d.setMagnetic_y_imu(Double.valueOf(line[magneticYPosition]));
        d.setMagnetic_z_imu(Double.valueOf(line[magneticZPosition]));
        d.setGravitiy_x_imu(Double.valueOf(line[gravityXPosition]));
        d.setGravitiy_y_imu(Double.valueOf(line[gravityYPosition]));
        d.setGravitiy_z_imu(Double.valueOf(line[gravityZPosition]));

        // setze auch die Global-Position für GT-Position (um in kartesischen Punkt umrechnen zu können)
        d.setGlobalPositionsGt(
                new GlobalPosition(
                        d.getLatitude_gt(),
                        d.getLongitude_gt(),
                        0.0
                )
        );

        // Setze auch die GT-direction für spätere Abstandsberechnung in und um Bewegungsrichtung
        d.setGtDirection(Double.valueOf(line[gt_directionPosition]));
        return d;
    }

    private void calculateCompleteVelocityByStepDetectorAndSetInDataObject(String currentOrientation,String currentStepDetectorCounter, Data d) {

        // Berechne die Geschwindigkeit nur, wenn sie einen von NaN verschiedenen Wert hat
        // (kommt bei manchen Datensätzen am Anfang für kurze Zeit vor). Wenn NaN, nehme GNSS-Geschwindigkeit.
        // Ebenso überspringen wir Schritt 1, da wir dann noch kein dt errechnen können
        if(!currentStepDetectorCounter.equals("NaN")) {
            int currentStepDetectorCount = Integer.valueOf(currentStepDetectorCounter);
            if(currentStepDetectorCount != 1) {
                if (currentStepDetectorCount > oldStepDetectorCount) {
                    // Aktualisiere alten wert
                    oldStepDetectorCount = currentStepDetectorCount;

                    // Berechne dt
                    //double dt = oldTimeFromStepdetector == 0 ? 0.1 : (d.getTimestamp() - oldTimeFromStepdetector) / 1000000000.0f;
                    float dt = oldTimeFromStepdetector == 0 ? 0.1f : (d.getTimestamp() - oldTimeFromStepdetector) / 1000.0f;
                    oldTimeFromStepdetector = d.getTimestamp();

                    // Schrittlängen:
                    // Luan: 0.8278146
                    // Rovena: 0.8038585
                    // Johann: 0.769230
                    double stepLength = 0.8278146;
                    double stepFrequency = 1 / dt;
                    double walkVelocity = stepLength * stepFrequency;
                    d.setAmountSpeed_stepDetector(walkVelocity);

                    // Als Winkel nutzen wir den Orientation-Wert
                    double angleOrientation = Double.valueOf(currentOrientation);
                    double vel_x_stepDetector = d.getAmountSpeed_stepDetector() * Math.sin(Math.toRadians(d.getBearing_wgs()));
                    double vel_y_stepDetector = d.getAmountSpeed_stepDetector() * Math.cos(Math.toRadians(d.getBearing_wgs()));
                    d.setSpeed_x_stepDetector(vel_x_stepDetector);
                    d.setSpeed_y_stepDetector(vel_y_stepDetector);

                    // Merke dir die zuletzt berechnete Geschwindigkeit um sie setzen zu können, wenn kein
                    // neuer Schritt erkannt wurde
                    oldAmoundOfVelocity_stepDetector = walkVelocity;
                    oldVel_x_StepDetector = vel_x_stepDetector;
                    oldVel_y_StepDetector = vel_y_stepDetector;
                } else {
                    // Setze die zuletzt berechnete Geschwindigkeit, da kein neuer Schritt erkannt wurde
                    d.setAmountSpeed_stepDetector(oldAmoundOfVelocity_stepDetector);
                    d.setSpeed_x_stepDetector(oldVel_x_StepDetector);
                    d.setSpeed_y_stepDetector(oldVel_y_StepDetector);
                }
            }
            else {
                // Wenn du bei Schritt 1 bist,
                // setze die zuvor berechneten GNSS-Geschwindigkeitswerte auch hier
                d.setAmountSpeed_stepDetector(d.getAmountSpeed_wgs());
                d.setSpeed_x_stepDetector(d.getSpeed_x_wgs());
                d.setSpeed_y_stepDetector(d.getSpeed_x_wgs());
            }
        }
        // Wenn du eine NaN Geschwindigkeit findest,
        // setze die zuvor berechneten GNSS-Geschwindigkeitswerte auch hier
        else {
            d.setAmountSpeed_stepDetector(d.getAmountSpeed_wgs());
            d.setSpeed_x_stepDetector(d.getSpeed_x_wgs());
            d.setSpeed_y_stepDetector(d.getSpeed_x_wgs());
        }
    }

    private void setColumnPositionValuesOfFile(String pathToFile) {
        timestampPosition = findSpecificColumnByName("Timestamp", pathToFile);
        longitudePosition = findSpecificColumnByName("TYPE_GPS-Longitude", pathToFile);
        latitudePosition = findSpecificColumnByName("TYPE_GPS-Latitude", pathToFile);
        altitudePosition = findSpecificColumnByName("TYPE_GPS-Altitude", pathToFile);
        bearingPosition = findSpecificColumnByName("TYPE_GPS-Bearing", pathToFile);
        gnssSpeedPosition = findSpecificColumnByName("TYPE_GPS-Speed", pathToFile);
        longitudeGtPosition = findSpecificColumnByName("TYPE_GPS-Longitude_GT", pathToFile);
        latitudeGtPosition = findSpecificColumnByName("TYPE_GPS-Latitude_GT", pathToFile);
        gpsAccuracyPosition = findSpecificColumnByName("TYPE_GPS-Accuracy", pathToFile);
        accelXPosition = findSpecificColumnByName("TYPE_ACCELEROMETER-X", pathToFile);
        accelYPosition = findSpecificColumnByName("TYPE_ACCELEROMETER-Y", pathToFile);
        accelZPosition = findSpecificColumnByName("TYPE_ACCELEROMETER-Z", pathToFile);
        magneticXPosition = findSpecificColumnByName("TYPE_MAGNETIC_FIELD-X", pathToFile);
        magneticYPosition = findSpecificColumnByName("TYPE_MAGNETIC_FIELD-Y", pathToFile);
        magneticZPosition = findSpecificColumnByName("TYPE_MAGNETIC_FIELD-Z", pathToFile);
        gravityXPosition = findSpecificColumnByName("TYPE_GRAVITY-X", pathToFile);
        gravityYPosition = findSpecificColumnByName("TYPE_GRAVITY-Y", pathToFile);
        gravityZPosition = findSpecificColumnByName("TYPE_GRAVITY-Z", pathToFile);
        labelPosition = findSpecificColumnByName("label", pathToFile);
        gt_directionPosition = findSpecificColumnByName("GT_Direction", pathToFile);
        typeOrientation_x = findSpecificColumnByName("TYPE_ORIENTATION-X",pathToFile);
        typeStepDetectorCounter = findSpecificColumnByName("TYPE_STEP_DETECTOR_COUNTER-X", pathToFile);
    }

    Map<String, List<Data>> getOriginalLinesBySegments() {
        return originalLinesBySegments;
    }
    public Map<String, Integer> getGnssCounterBySegments() {
        return gnssCounterBySegments;
    }

    public static int getOldStepDetectorCount() {
        return oldStepDetectorCount;
    }

    public static void setOldStepDetectorCount(int oldStepDetectorCount) {
        CsvReader.oldStepDetectorCount = oldStepDetectorCount;
    }
}
