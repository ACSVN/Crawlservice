/**
 * Created on Tue Aug 27 16:38:18 ICT 2019
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputFileCreate extends DefaultJavaTestScript  {

    public void test() {
        try {
            String web_name = getContext().getVariableAsString("web_name");
        
            //Get directory project
            String path = getContext().getVariableAsString("_PROJECT_DIR");
            String fullPathDir = URLDecoder.decode(path, "UTF-8");
            fullPathDir = fullPathDir.replace("\\", "/");
            
            //create new output file data
            String path_file_data_result = fullPathDir + "/result/" + web_name + ".csv";
            this.deleteCreateNewFile(path_file_data_result);
            
            ArrayList<String> title_result = new ArrayList<String>() {{
                add("重要度高/ rank");
                add("web site name");
                add("URL (list tab)");
                add("search key word");
                add("URL　/ URL(article)");
                add("タイトル /tile");
                add("掲載日/post date");
                add("掲載媒体/source");
                add("概要 /all text");
            }};
            this.writeTemplateFile(path_file_data_result, title_result);
            
            //// create new data storage if not exists
            //String path_file_data_storage = fullPathDir + "/result/data_storage_" + web_name + ".csv";
            //this.createNewFile(path_file_data_storage);
            
        } catch (IOException ex) {
            getContext().setVariable("check_status", "deleteCreateNewFile. java function test error. " + ex.toString());
        }
    }
    
    public void createNewFile(String dir_path){
        try{
            File file = new File(dir_path);
            if(!file.exists()){
                file.createNewFile();
            }
            
            getContext().setVariable("path_href_file", dir_path);
        }catch(IOException e){
            getContext().setVariable("check_status", "GetHref.java. Create new file csv error. " + e.toString());
        }
    }
    
    public void deleteCreateNewFile(String dir_path){
        try{
            File file = new File(dir_path);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            getContext().setVariable("path_href_file", dir_path);
        }catch(IOException e){
            getContext().setVariable("check_status", "deleteCreateNewFile.java. deleteCreateNewFile Create new file csv error. " + e.toString());
        }
    }
    
    public void writeTemplateFile(String path_file, ArrayList<String> title_header){
        try{
            File in = new File(path_file);
            OutputStream os = null;
            PrintWriter out = null;
            if ( in.exists() && !in.isDirectory() ) {
                os = new FileOutputStream(new File(path_file), true);
            }
            else {
                os = new FileOutputStream(new File(path_file));
            }
            os.write(239);
            os.write(187);
            os.write(191);
            
            out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
            
            for(int i = 0; i < title_header.size(); i++){
                out.print('"' + title_header.get(i).replace("\"", "") + '"');
                if(i == title_header.size()-1){
                    out.print("\n");
                }else{
                    out.print(",");
                }
                
            }
            out.flush();
            out.close();
            getContext().setVariable("check_status", "OutputFileCreate.java function writeTemplateFile success!!!!");
        }catch(IOException e){
            getContext().setVariable("check_status", "OutputFileCreate.java function writeTemplateFile error. IOException: " + e.toString());
        }
    }
   
    public static void main(String args[]) {
        OutputFileCreate script = new OutputFileCreate();
        ApplicationSupport robot = new ApplicationSupport();
        AutomatedRunnable runnable = robot.createAutomatedRunnable(script, "OutputFileCreate@" + Integer.toHexString(script.hashCode()), args, System.out, false);
        new Thread(runnable).start();
    }
}
