/**
 * Created on Tue Aug 27 13:02:25 ICT 2019
 * HeartCore Robo Desktop v5.0.3 (Build No. 5.0.3-20190618.1)
 **/
package scripts;
import com.tplan.robot.scripting.*;
import com.tplan.robot.*;
import java.awt.*;

import java.sql.*;
import java.sql.Timestamp;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.Date;

import java.lang.Object;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;



public class ExportDataFromSQLite extends DefaultJavaTestScript  {
    
    public String list_url = "";
    public String list_key = "";
    public void test() {
        try {
            String db_name = "crawlservice.db";
            String table_name = "urlandkeyword";
            String table_company = "company";
            String file_output_name = "input.csv";
            
            exportDataToCSV(db_name, table_name, file_output_name);
            getInformationOfCompany(db_name, table_company);
            //connectDB(db_name);
        } catch (StopRequestException ex) {
            throw ex;
        }
    }
    
    private Connection  connectDB(String db_name){
        Connection conn = null;
        String test = "";
        try {
            //Get directory project
            String path = getContext().getVariableAsString("_PROJECT_DIR");
            String fullPathDir = URLDecoder.decode(path, "UTF-8");
            fullPathDir = fullPathDir.replace("\\", "/");
            
            fullPathDir =  fullPathDir.replace("bat/ana", "database/" + db_name);
            
            String path_db = "jdbc:sqlite:" + fullPathDir;
            //String path_db = "jdbc:sqlite:C:/sqlite/db/test/" + db_name;
            Class.forName("org.sqlite.JDBC");
            test = path_db;
            // c = DriverManager.getConnection("jdbc:sqlite:test.db");
            conn = DriverManager.getConnection(path_db);
        } catch ( Exception e ) {
            getContext().setVariable("error", e.getClass().getName() + ": " + e.getMessage() + ", url: " + test);
        }
        getContext().setVariable("result", "Opened database successfully");
        return conn;
    }
    
    public void exportDataToCSV(String db_name, String table_name, String file_name){
        
        String sql = "SELECT * FROM " + table_name;
        String sql_del = "DELETE FROM " + table_name;

        try (Connection conn = this.connectDB(db_name);
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            
            //Get directory project
            String path = getContext().getVariableAsString("_PROJECT_DIR");
            String fullPathDir = URLDecoder.decode(path, "UTF-8");
            fullPathDir = fullPathDir.replace("\\", "/");

            String path_file = fullPathDir + "/result/" + file_name;
            this.deleteCreateNewFile(path_file);
            int idx = 0;
            while (rs.next()) {
                //str_query = str_query +  rs.getString("name") + "," + rs.getString("corporateNumber") + "," + rs.getString("postCode") + "/n";
                idx++;
                getContext().setVariable("url_csv"+idx, rs.getString("url"));
                // getContext().setVariable("keyword"+idx, rs.getString("keyword"));
                list_url = list_url + rs.getString("url") + ",";
                
                String arr_keywords[] = rs.getString("keyword").split(",");
                for(int i = 0; i < arr_keywords.length; i++){
                    String keyword = arr_keywords[i];
                    if(list_key.indexOf(keyword) < 0){
                        list_key = list_key + keyword + ",";
                    }
                    getContext().setVariable("keyword"+idx, keyword);
                    writeContentToCSVFile(path_file, rs.getString("url"), keyword);
                }
                getContext().setVariable("total_key", Integer.toString(idx));
            }
            
            stmt.executeUpdate(sql_del);
            
            if(list_url.lastIndexOf(",") > 0){
                list_url = list_url.substring(0, list_url.lastIndexOf(","));
            }
            if(list_key.lastIndexOf(",") > 0){
                list_key = list_key.substring(0, list_key.lastIndexOf(","));
            }
            
            
            getContext().setVariable("list_key", list_key);
            getContext().setVariable("list_url", list_url);
            getContext().setVariable("total_url", Integer.toString(idx));
            getContext().setVariable("check_status", "Export database to csv file successful.");
        } catch (SQLException e) {
            getContext().setVariable("error", "Export database to csv file. Error: " + e.getMessage());
        }  catch (Exception e) {
            getContext().setVariable("error", "Export database to csv file. Error: " + e.getMessage());
        }
        
    }
    
    public void getInformationOfCompany(String db_name, String table_name){
         String sql = "SELECT * FROM " + table_name + " WHERE URL = '" + list_url + "' and keywords = '" + list_key  + "' LIMIT 1";
        try (Connection conn = this.connectDB(db_name);
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
                while (rs.next()) {
                    getContext().setVariable("com_email", rs.getString("email"));
                    getContext().setVariable("com_name", rs.getString("name"));
                }
                getContext().setVariable("check_status", "getInformationOfCompany success");
        } catch (SQLException e) {
            getContext().setVariable("error", "getInformationOfCompany. Error: " + e.getMessage() + ", sql: " + sql);
        }  catch (Exception e) {
            getContext().setVariable("error", "getInformationOfCompany. Error: " + e.getMessage() + ", sql: " + sql);
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
            getContext().setVariable("check_status", "GetHref.java. Create new file csv error. " + e.toString());
        }
    }
    
    public void writeContentToCSVFile(String path_file, String url, String keyword){
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
            
            out.print(url);
            out.print(",");
            out.print('"' + keyword.replace("\"", "") + '"');
            out.print("\n");
            
            out.flush();
            out.close();
            getContext().setVariable("check_status", "writeContentToCSVFile. Successful");
        }catch(IOException e){
            getContext().setVariable("check_status", "writeContentToCSVFile. IOException: " + e.toString());
        }
    }
   
    public static void main(String args[]) {
        ExportDataFromSQLite script = new ExportDataFromSQLite();
        ApplicationSupport robot = new ApplicationSupport();
        AutomatedRunnable runnable = robot.createAutomatedRunnable(script, "ExportDataFromSQLite@" + Integer.toHexString(script.hashCode()), args, System.out, false);
        new Thread(runnable).start();
    }
}
