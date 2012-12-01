/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gpstokml;

import java.util.Iterator;
import java.util.Vector;
import org.boehn.kmlframework.kml.AltitudeModeEnum;
import org.boehn.kmlframework.kml.LookAt;
import org.boehn.kmlframework.kml.Placemark;
import org.boehn.kmlframework.kml.Point;

/**
 *
 * @author labman
 */
public class GeoRecord {


    double LOOKAT_RANGE =  6000.0;
    double LOOKAT_HEADING = 0.0;
    double LOOKAT_TILT = 45.0;

    int index;

    double lati;

    double longi;

    int tripId;
    
    String direction;

    double altitude;

    double speed;

    double heading;

    int secondsSinceMidnight;

    String dateString;

    String timeString;

    Vector<PeopleRecord> associatedPeople;

    public double getAltitude() {
        return altitude;
    }

    public Vector<PeopleRecord> getAssociatedPeople() {
        return associatedPeople;
    }

    public String getDateString() {
        return dateString;
    }

    public String getDirection() {
        return direction;
    }

    public double getHeading() {
        return heading;
    }

    public int getIndex() {
        return index;
    }

    public double getLati() {
        return lati;
    }

    public double getLongi() {
        return longi;
    }

    public int getSecondsSinceMidnight() {
        return secondsSinceMidnight;
    }

    public double getSpeed() {
        return speed;
    }

    public String getTimeString() {
        return timeString;
    }

    public int getTripId() {
        return tripId;
    }
    

    public void addPeopleRecord(PeopleRecord pr) {
        associatedPeople.add(pr);
    }

    private String cdata(String s) {
        return "<![CDATA[" + s + "]]>";
    }

    private String getBasicText() {
        String nl = "<br>";
        StringBuilder sb = new StringBuilder();
        sb.append("Time: " + this.timeString);
        sb.append(nl);
        sb.append("Date: " + this.dateString);
        sb.append(nl);
        sb.append("Trip: " + tripId);
        sb.append(nl);
        sb.append("Direction: " + this.direction);
        sb.append(nl);
        sb.append("Speed: " + this.speed);
        sb.append(nl);
        return sb.toString();
    }

    private String getPeopleText() {
        String nl = "<br>";
        StringBuilder sb = new StringBuilder();
        sb.append(getBasicText());
        sb.append(nl);
        int exits =0;
        int enters =0;
        Iterator<PeopleRecord> it = this.associatedPeople.iterator();
        while(it.hasNext()) {
            PeopleRecord pr = it.next();
            if(pr.getType().equals("EXIT")) {
                exits++;
            } else {
                enters++;
            }
        }
        sb.append(enters + " PAX ENTERED bus");
        sb.append(nl);
        sb.append(exits + " PAX EXITED bus");
        return sb.toString();
    }


    public String toCSV() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(this.index);
        sb.append(",");
        sb.append(this.dateString);
        sb.append(",");
        sb.append(this.timeString);
        sb.append(",");
        sb.append(this.secondsSinceMidnight);
        sb.append(",");
        sb.append(this.tripId);
        sb.append(",");
        sb.append(this.direction);
        sb.append(",");
        sb.append(this.speed);
        sb.append(",");
        sb.append(this.lati);
        sb.append(",");
        sb.append(this.longi);
        sb.append(",");
        sb.append(this.altitude);
        sb.append(",");
        int exits =0;
        int enters =0;
        Iterator<PeopleRecord> it = this.associatedPeople.iterator();
        while(it.hasNext()) {
            PeopleRecord pr = it.next();
            if(pr.getType().equals("EXIT")) {
                exits++;
            } else {
                enters++;
            }
        }
        sb.append(exits);
        sb.append(",");
        sb.append(enters);
        sb.append(",");

        return sb.toString();

    }

     /**
     * Returns the GeoRecord as a properly formatted KML entry
     * @return
     */
    public Placemark toKMLWithPeople() {
        Placemark ifi = new Placemark("" + this.index);
        ifi.setLocation(this.longi, this.lati);


        Point p = new Point(this.longi, this.lati, this.altitude);
        LookAt look = new LookAt(this.longi, this.lati, this.altitude, this.LOOKAT_HEADING, this.LOOKAT_TILT, AltitudeModeEnum.relativeToGround, this.LOOKAT_RANGE);

        if(this.associatedPeople.size() ==0){
            if(this.direction.equals("N")) {
                ifi.setStyleUrl("wpIconNorth");
            } else {
                ifi.setStyleUrl("wpIconSouth");
            }
            ifi.setDescription(cdata(getBasicText()));
        } else {
            ifi.setStyleUrl("peopleIcon");
            ifi.setDescription(cdata(getPeopleText()));
        }
        ifi.setAbstractView(look);
        ifi.setGeometry(p);
        return ifi;
    }

    /**
     * Returns the GeoRecord as a properly formatted KML entry
     * @return
     */
    public Placemark toKML() {
        Placemark ifi = new Placemark("" + this.index);
        ifi.setLocation(this.longi, this.lati);
        

        Point p = new Point(this.longi, this.lati, this.altitude);
        LookAt look = new LookAt(this.longi, this.lati, this.altitude, this.LOOKAT_HEADING, this.LOOKAT_TILT, AltitudeModeEnum.relativeToGround, this.LOOKAT_RANGE);
        
        if(this.direction.equals("N")) {
            ifi.setStyleUrl("wpIconNorth");
        } else {
            ifi.setStyleUrl("wpIconSouth");
        }
        ifi.setDescription(cdata(getBasicText()));

        ifi.setAbstractView(look);
        ifi.setGeometry(p);
        return ifi;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GeoRecord{" + "index=" + index + "lati=" + lati + "longi=" + longi + "tripId=" + tripId + "direction=" + direction + "altitude=" + altitude + "speed=" + speed + "heading=" + heading + "secondsSinceMidnight=" + secondsSinceMidnight + "dateString=" + dateString + "timeString=" + timeString + "associatedPeople=" + associatedPeople + '}');
        if(this.associatedPeople.size() >0) {
            Iterator<PeopleRecord> it = this.associatedPeople.iterator();
            while(it.hasNext()) {
                sb.append("\n");
                PeopleRecord pr = it.next();
                sb.append(pr);
                
            }
        }
        return sb.toString();
    }


    /*
     * Creates a Georecord from a CSV line
     */
    public GeoRecord( String[] s) {

        associatedPeople = new Vector<PeopleRecord>();
        this.index = Integer.parseInt(s[0]);
        this.tripId  = Integer.parseInt(s[1]);
        this.direction = s[2];
        this.dateString = s[6];
        int h = Integer.parseInt(s[8]);
        int m = Integer.parseInt(s[9]);
        int sec = Integer.parseInt(s[10]);
        this.timeString = h + ":" + m + ":" + sec;
        this.secondsSinceMidnight = h*60*60+m*60+sec;
        this.longi = -1*Double.parseDouble(s[14]);
        this.lati = Double.parseDouble(s[12]);
        this.altitude =Double.parseDouble(s[16]);
        this.speed =Double.parseDouble(s[17]);

    }
    
}
