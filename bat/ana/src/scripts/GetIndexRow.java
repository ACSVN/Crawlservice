/**
 * Created on Tue Aug 27 16:53:48 ICT 2019
 * HeartCore Robo Desktop v5.0.3 (Build No. 5.0.3-20190618.1)
 **/
package scripts;
import com.tplan.robot.scripting.*;
import com.tplan.robot.*;
import java.awt.*;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetIndexRow extends DefaultJavaTestScript  {

    public void test() {
        try {
            String url_csv_item = getContext().getVariableAsString("url_csv_item");
            
            //Get directory project
            String path = getContext().getVariableAsString("_PROJECT_DIR");
            String fullPathDir = URLDecoder.decode(path, "UTF-8");
            fullPathDir = fullPathDir.replace("\\", "/");
            
            String path_file_input_main = fullPathDir + "/input_main.csv";
            
            int ind = readDataFromCSV(path_file_input_main, url_csv_item);
        } catch (StopRequestException ex) {
            getContext().setVariable("index_row", -1);
            throw ex;
        } catch (Exception e) {
            getContext().setVariable("index_row", -1);
        }
    }
    
    public int readDataFromCSV(String path_file_name, String url_csv){
        
        BufferedReader fileReader = null;
        //Create a new list of href to be filled by CSV file data 
        ArrayList<String> list_variable = new ArrayList<String>();
        int index_row = 1;
        boolean isExists = false;
        String name_script = "";
        try {
             
            String line = "";
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(path_file_name));
            //Read the CSV file header to skip it
            fileReader.readLine();
            
            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {
                index_row++;
                
                String[] infor = line.split(",");
                name_script = infor[15];
                if(infor[1].compareTo(url_csv) == 0){
                    isExists = true;
                    break;
                }
            }
            if(isExists){
                getContext().setVariable("index_row", index_row );
                getContext().setVariable("name_script", name_script );
            }else{
                getContext().setVariable("index_row", -1 );
                getContext().setVariable("name_script", "not_exists" );
            }
        } 
        catch (Exception e) {
            getContext().setVariable("index_row", -1);
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                getContext().setVariable("index_row", -1);
                e.printStackTrace();
            }
        }
        return index_row;
    }
   
    public static void main(String args[]) {
        GetIndexRow script = new GetIndexRow();
        ApplicationSupport robot = new ApplicationSupport();
        AutomatedRunnable runnable = robot.createAutomatedRunnable(script, "GetIndexRow@" + Integer.toHexString(script.hashCode()), args, System.out, false);
        new Thread(runnable).start();
    }
}
