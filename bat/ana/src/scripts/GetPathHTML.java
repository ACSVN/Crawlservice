/**
 * Created on Fri Aug 30 15:38:34 ICT 2019
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

public class GetPathHTML extends DefaultJavaTestScript  {

	public void test() {
		try {
			String filename = getContext().getVariableAsString("file_html");
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = URLDecoder.decode(path, "UTF-8");
            //fullPath = fullPath.replace("classes/",  "html_file/" + filename);
            if(fullPath.indexOf("classes") >= 0){
                fullPath = fullPath.replace("classes/",  "html_file/" + filename);
            }
            if(fullPath.indexOf("src") >= 0){
                fullPath = fullPath.replace("src/",  "html_file/" + filename);
            }

            File file = new File(fullPath);
            if(file.exists()){
                boolean success = file.delete();
            }

            getContext().setVariable("fullPathHtml", fullPath.substring(1, fullPath.length()).replace("/", "\\"));
			
		} catch (StopRequestException ex) {
            throw ex;
        } catch (Exception e) {
            System.err.println("Usage: java PageSaver url local_file");
        }
	}
   
	public static void main(String args[]) {
		GetPathHTML script = new GetPathHTML();
		ApplicationSupport robot = new ApplicationSupport();
		AutomatedRunnable runnable = robot.createAutomatedRunnable(script, "GetPathHTML@" + Integer.toHexString(script.hashCode()), args, System.out, false);
		new Thread(runnable).start();
	}
}
