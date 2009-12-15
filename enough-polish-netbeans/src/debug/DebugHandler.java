/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import org.openide.util.Exceptions;

/**
 *
 * @author marcels
 */
public class DebugHandler {
    public static final boolean enable = true;
    public static final String PATH_AND_FILENAME = "c:\\SE\\debug.txt";
    
    public static void save(String s) {
        if(enable){
            FileWriter out = null;
            StringBuilder contents = new StringBuilder();

            try {
                File outputFile = new File(PATH_AND_FILENAME);
                if(outputFile.exists()){
                    BufferedReader input =  new BufferedReader(new FileReader(outputFile));
                      try {
                        String line = null; 
                        while (( line = input.readLine()) != null){
                          contents.append(line);
                          contents.append(System.getProperty("line.separator"));
                        }
                      }
                      finally {
                        input.close();
                      }

                }
                out = new FileWriter(outputFile);
                Date d = new Date();
                String time = String.valueOf(d.getTime());
                contents.append(time + " " + s);
                contents.append(System.getProperty("line.separator"));
                out.write(contents.toString());
                out.close();

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        }
    }


}
