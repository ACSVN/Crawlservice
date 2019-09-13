/**
 * Created on Fri Aug 30 15:00:46 ICT 2019
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

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DeleteFileExist extends DefaultJavaTestScript  {

    public void test() {
        try {
            String filename = getContext().getVariableAsString("file_html");
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = URLDecoder.decode(path, "UTF-8");
			
			if(fullPath.indexOf("classes") >= 0){
                fullPath = fullPath.replace("classes/",  "html_file");
            }
            if(fullPath.indexOf("src") >= 0){
                fullPath = fullPath.replace("src/",  "html_file");
            }
			
			File folder = new File(fullPath);
            File[] listOfFiles = folder.listFiles();
            String content = "";
            for (int i = 0; i < listOfFiles.length; i++) {
                
                File file = listOfFiles[i];
                if (file.isFile() && file.getName().endsWith(".html")) {
                    boolean success = file.delete();
                }
            }

            String path_file_html =  fullPath + "/" + filename;
			
			getContext().setVariable("fullPathHtml", path_file_html.substring(1, path_file_html.length()).replace("/", "\\"));
			
        } catch (StopRequestException ex) {
            throw ex;
        } catch (Exception e) {
            getContext().setVariable("check_status", "DeleteFileExist.java. Deleted html file!!!. Exception: " + e.toString());
            System.err.println("Usage: java PageSaver url local_file");
        }
    }
   
    public static void main(String args[]) {
        DeleteFileExist script = new DeleteFileExist();
        ApplicationSupport robot = new ApplicationSupport();
        AutomatedRunnable runnable = robot.createAutomatedRunnable(script, "DeleteFileExist@" + Integer.toHexString(script.hashCode()), args, System.out, false);
        new Thread(runnable).start();
    }
}
