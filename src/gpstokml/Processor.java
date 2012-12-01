/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import net.sf.javaml.core.kdtree.KDTree;
import org.boehn.kmlframework.kml.*;

/**
 * The Main class for doing the normaliztion
 * @author labman
 */
public class Processor {

    /**
     * The time offset (s) between the PEOPLE clock and the GEO clock
     *
     * You'll need to set this via trial and error using GoogleMap to plot results...
     */
    double TIME_SHIFT = -20;

    double firstTime = 1000000;
    double lastTime = -1000000;

    int tripCounter = 0;

    /**
     * A KD Tree (one-dimensional) for GeoRecord
     *
     * Keyed off of seconds from midnight
     */
    KDTree geoRecords;
    /***
     * The GEO CSV..the raw excel from the GPS logger plus some other stuff...here's
     * an extract:
     * INDEX	TRIP	DIR	VALID	UTC DATE	UTC TIME	LOCAL DATE	LOCAL TIME	HOUR	MIN	SEC	MS	LATITUDE	N/S	LONGITUDE	E/W	ALTITUDE	SPEED	HEADING	G-X	G-Y	G-Z
     *   1	1	N	SPS	10/25/2010	10:21:25	10/25/2010	5:21:25	6	21	25	0	41.392919	N	73.954107	W	147.411774	25.62009	24.570396	0	0	0
     *
     */
    final static String GEOFILE = "normalized_bus25oct.csv";
    /***
     * The Passenger EXIT/ENTRANCE CSV
     * millis	stamp	DATE	HOUR	MIN	S	SECOND_FROM_MIDNIGHT	MILLIS_LAPSE	LAPSE	ENTER	EXIT
     *  6349	 2010/10/25:6:17:54EXIT	 2010/10/25	6	17	54	22674			0	1
     *  8396	 2010/10/25:6:17:56EXIT	 2010/10/25	6	17	56	22676	2047	2	0	1
     *  9675	 2010/10/25:6:17:57EXIT	 2010/10/25	6	17	57	22677	826	1	0	1     
     */
    final static String PAXFILE = "clean_0620_1220_25oct_mahan_start.csv";

    /***
     * Loads the pax file and does a nearest neighbor search to match the nearest geo record in time
     */
    public void loadPax() {
        //Open pax file
        //for each line
        //create a PeopleRecord
        //match it to a Geo record

         try {
            CSVReader reader = new CSVReader(new FileReader(PAXFILE));
            String[] nextLine;
            int lineCounter =0;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if(lineCounter > 0) {
                    int secSinceMidnight = Integer.parseInt(nextLine[8]);
                    double[] key = new double[1];
                    key[0] = TIME_SHIFT + 1.0 * secSinceMidnight;
                    GeoRecord matchingGeo = (GeoRecord)geoRecords.nearest(key);
                    if(matchingGeo !=null) {
                        PeopleRecord pr = new PeopleRecord(nextLine);
                        pr.setTimeShiftSeconds(TIME_SHIFT);
                        matchingGeo.addPeopleRecord(pr);
                    } else {
                        System.err.println("Null Geo Returned!!");
                    }                    
                }
                lineCounter++;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void loadGeo() {

        try {
            CSVReader reader = new CSVReader(new FileReader(GEOFILE));
            String[] nextLine;
            int lineCounter =0;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if(lineCounter > 0) {
                    GeoRecord newRecord = new GeoRecord(nextLine);
                    double[] key = new double[1];

                    //Keep track of trps
                    int tripNumber = newRecord.getTripId();
                    if(tripNumber > tripCounter) tripCounter = tripNumber;
                    key[0] = newRecord.getSecondsSinceMidnight();

                    //Key the time range
                    if(key[0] < firstTime) firstTime = key[0];
                    if(key[0] > lastTime) lastTime = key[0];
                    
                    System.err.println("trying sec " + newRecord.getSecondsSinceMidnight());
                    geoRecords.insert(key, newRecord);
                }
                lineCounter++;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }



    /**
     * Dumps the results as a series of KML's..one per trip
     */
    public void writeKMLandCSV() {

        String csv_header = "INDEX,DATE,TIME,SECONDS_SINCE_MIDNIGHT,TRIP_ID,DIR,SPEED,LAT,LONG,ALT,EXITS,ENTRANCES";

        try {
            CSVWriter writer = new CSVWriter(new FileWriter("bus_data.csv"), ',');

            //Write the header
            String[] entries = csv_header.split(",");
            writer.writeNext(entries);            


            //Setup some style
            LineStyle lineStyle = new LineStyle();
            lineStyle.setColor("ff005555");
            lineStyle.setWidth(5.0);

            IconStyle peopleIconStyle = new IconStyle();
            //Icon peopleIcon = new Icon();
            //peopleIcon.setHref("http://maps.google.com/mapfiles/kml/man.png");
            peopleIconStyle.setIconHref("http://maps.google.com/mapfiles/kml/shapes/man.png");
            peopleIconStyle.setScale(1.50);

            IconStyle wpIconStyleNorth = new IconStyle();
            //wpIcon.setHref("http://maps.google.com/mapfiles/kml/paddle/red-stars.png");
            //wpIconStyleNorth.setIconHref("http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png");
            wpIconStyleNorth.setIconHref("http://maps.google.com/mapfiles/ms/micons/green-dot.png");
            wpIconStyleNorth.setScale(0.50);

            IconStyle wpIconStyleSouth = new IconStyle();
            //wpIcon.setHref("http://maps.google.com/mapfiles/kml/paddle/red-stars.png");
            wpIconStyleSouth.setIconHref("http://maps.google.com/mapfiles/ms/micons/yellow-dot.png");
            wpIconStyleSouth.setScale(0.50);


            Style style_lineStyle = new Style();
            style_lineStyle.setLineStyle(lineStyle);

            Style style_peopleIconStyle = new Style();
            style_peopleIconStyle.setId("peopleIcon");
            style_peopleIconStyle.setIconStyle(peopleIconStyle);

            Style style_wpIconStyleNorth = new Style();
            style_wpIconStyleNorth.setId("wpIconNorth");
            style_wpIconStyleNorth.setIconStyle(wpIconStyleNorth);

            Style style_wpIconStyleSouth = new Style();
            style_wpIconStyleSouth.setId("wpIconSouth");
            style_wpIconStyleSouth.setIconStyle(wpIconStyleSouth);



            Kml[] kml = new Kml[tripCounter];
            Document[] document = new Document[tripCounter];
            Folder[] northFolders = new Folder[tripCounter];
            Folder[] southFolders = new Folder[tripCounter];
            Folder[] peopleFolders = new Folder[tripCounter];
           // Folder[] tracksFolders = new Folder[tripCounter];

            for(int i=0; i < tripCounter; i++) {
                kml[i] = new Kml();
                kml[i].setXmlIndent(true);
                document[i] = new Document();
                document[i].addStyleSelector(style_lineStyle);
                document[i].addStyleSelector(style_peopleIconStyle);
                document[i].addStyleSelector(style_wpIconStyleNorth);
                document[i].addStyleSelector(style_wpIconStyleSouth);
                kml[i].setFeature(document[i]);

                northFolders[i] = new Folder();
                northFolders[i].setName("North Route");

                southFolders[i] = new Folder();
                southFolders[i].setName("South Route");

                peopleFolders[i] = new Folder();
                peopleFolders[i].setName("People");

                //tracksFolders[i] = new Folder();
                //tracksFolders[i].setName("Tracks");

                document[i].addFeature(northFolders[i]);
                document[i].addFeature(southFolders[i]);
                document[i].addFeature(peopleFolders[i]);
                //document[i].addFeature(tracksFolders[i]);

            }

            double[] lowerKey = new double[1];
            double[] upperKey = new double[1];

            lowerKey[0] = firstTime;
            upperKey[0] = lastTime;

            Object[] foo = geoRecords.range(lowerKey, upperKey);
            for(int i=0; i < foo.length; i++) {
                GeoRecord nextRecord = (GeoRecord) foo[i];
                int tripNumber = nextRecord.getTripId();
                //System.out.println(foo[i]);
                if(nextRecord.getDirection().equals("N")) {
                    northFolders[tripNumber-1].addFeature(nextRecord.toKML());
                } else {
                    southFolders[tripNumber-1].addFeature(nextRecord.toKML());
                }
                if(nextRecord.getAssociatedPeople().size() >0) {
                    peopleFolders[tripNumber-1].addFeature(nextRecord.toKMLWithPeople());
                }

                //Write CSV entry
                String csvLine = nextRecord.toCSV();
                String[] entries2 = csvLine.split(",");
                writer.writeNext(entries2);

            }

            //Write the KML files
            for(int i=0; i < tripCounter; i++) {
                kml[i].createKml("bus_route" + i + ".kml");
            }
            
            //Close the csv writer
            writer.close();

        } catch(Exception e) {
                System.err.println(e);
       }
    }


    public void doIt() {

        //Load geo data
        loadGeo();

        //Load pax data
        loadPax();

        writeKMLandCSV();
    }


    public Processor() {

        //Initialize structures
        geoRecords = new KDTree(1);

        



    }
}
