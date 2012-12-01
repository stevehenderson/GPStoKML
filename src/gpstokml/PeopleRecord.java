/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gpstokml;

/**
 * This is an object to track people exiting or entering
 * 
 * @author labman
 */
public class PeopleRecord {

    int millis;

    ///EXIT or ENTRANCE
    String type = "unset";

    String dateString;

    String timeString;

    int secondsSinceMidnight;

    /*
     * The amount of time shift applied by the application.
     * This is a use configurable value in the main file....
     */
    double timeShiftSeconds;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public int getMillis() {
        return millis;
    }

    public void setMillis(int millis) {
        this.millis = millis;
    }

    public int getSecondsSinceMidnight() {
        return secondsSinceMidnight;
    }

    public void setSecondsSinceMidnight(int secondsSinceMidnight) {
        this.secondsSinceMidnight = secondsSinceMidnight;
    }

    public double getTimeShiftSeconds() {
        return timeShiftSeconds;
    }

    public void setTimeShiftSeconds(double timeShiftSeconds) {
        this.timeShiftSeconds = timeShiftSeconds;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }

    public PeopleRecord(String[] s)  {
        this.millis = Integer.parseInt(s[0]);
        this.dateString = s[2];
        this.timeString = s[4];
        this.secondsSinceMidnight = Integer.parseInt(s[8]);
        int exitTest = Integer.parseInt(s[11]);
        if(exitTest >0) {
            this.type = "EXIT";            
        } else {
            this.type = "ENTER";
        }

    }

}
