/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gpstokml;

/**
 * This application takes GPS data recorded from the bus GPS and combines it with the
 * people EXIT/ENTRANCE logger.
 *
 * This requires synching the time so that they are as close as possible, then
 * matching the EXIT/ENTRANCE data to the closest GPS hack
 *
 * NOTE:  Pass the following args to the JVM to avoid stack overflow:   -Xss2048k
 *
 * @author henderso
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Processor p= new Processor();
        p.doIt();
        System.exit(0);
        
    }



}
