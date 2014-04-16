/**
 * 
 */
package cis526;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * write a string to a file
 *
 */
public class WriteFile {

	/**
	 * 
	 * @param fileName
	 * @param writeStr
	 */
    public static void writeFile(String fileName, String writeStr) {
        BufferedWriter writer = null;
        try {
            //create a temporary file
            File logFile = new File("./output/" + fileName);

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(writeStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens
                writer.close();
            } catch (Exception e) {
            }
            System.out.println("Write Complete >> ./output/" + fileName);
        }
    }
    
    /**
	 * 
	 * @param fileName
	 * @param writeStr
	 */
    public static void writeFile(String fileName, ArrayList<String> writeList) {
        BufferedWriter writer = null;
        
        String writeStr = "";
        Iterator<String> iter = writeList.iterator();
        while(iter.hasNext()) {
        	writeStr = writeStr + iter.next() + '\n';
        }
        writeStr.trim();
        
        try {
            //create a temporary file
            File logFile = new File("./output/" + fileName);

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(writeStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens
                writer.close();
            } catch (Exception e) {
            }
            System.out.println("Write Complete >> ./output/" + fileName);
        }
    }
}