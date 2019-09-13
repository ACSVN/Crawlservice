/**
 * Created on Wed Aug 28 01:38:47 ICT 2019
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

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Header; 
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GetHrefsTop20 extends DefaultJavaTestScript  {
    
    public ArrayList<String> arr_hrefs = new ArrayList<String>();
    public ArrayList<String> arr_name_com = new ArrayList<String>();
    public ArrayList<String> arr_title = new ArrayList<String>();

    public void test() {
        try {
            
            String name_script = getContext().getVariableAsString("name_script");
            if(name_script.compareTo("ana1.tpr") == 0){
                this.GetHrefsAndContentWithoutSavePageSource();
            }
            if(name_script.compareTo("ana2.tpr") == 0){
                this.GetHrefsAndContentFromHtml();
            }
            
        } catch (StopRequestException ex) {
            throw ex;
        }
    }
    
    //get data without save page source
    public void GetHrefsAndContentWithoutSavePageSource(){
        try {
            String str_url = getContext().getVariableAsString("url_result_keyword");
            String url_web = getContext().getVariableAsString("url_web");
            String web_name = getContext().getVariableAsString("web_name");
            String class_href = getContext().getVariableAsString("class_href");
            String class_title = getContext().getVariableAsString("class_title");
            String domain = getContext().getVariableAsString("domain");
            String replace_nxt_p = getContext().getVariableAsString("replace_nxt_p");
            String class_next_page = getContext().getVariableAsString("class_next_page");
            String case_display = getContext().getVariableAsString("case_display");
            String ele_page = getContext().getVariableAsString("class_gsc_cursor_page");
            String keyword = getContext().getVariableAsString("keyword");
            
            int p=0;
            int ipage = 1;
            String url_item = str_url;
            
            String fileNameDS = "data_storge/" + web_name.toLowerCase() + ".xlsx";
            ArrayList<String> href_storge = this.loadDataToArrayList(keyword, fileNameDS, 0);
            
            // get total page of result for keyword
            Document docpage = Jsoup.connect(url_item).get();
            Elements page = docpage.select(ele_page);
            if(page.size() > 0){
                p=page.size();
            }
            
            //loop all page and get hrefs (request get top 20 => count loop is two
            if(class_next_page.indexOf("null") < 0 ){
                while( ipage < 3 && (ipage == 1 ||  p > 1)){
                    String url_page = "";
                    if(ipage < 2){
                        url_page = url_item;
                    }else{
                        if(web_name.indexOf("ainow") >= 0 || web_name.indexOf("moguravr") >= 0 || web_name.indexOf("wirelesswire") >= 0 || web_name.indexOf("techable") >= 0){
                            String str_rp =replace_nxt_p+keyword;
                            url_page = url_item.replace(str_rp, "") + class_next_page + ipage +  "/" + str_rp;
                        }else if(web_name.indexOf("newsmynavi") >= 0){
                            url_page = url_item.replace(replace_nxt_p, "") + class_next_page + ipage +  "&" + replace_nxt_p;
                        }else if(web_name.indexOf("gigazine") >= 0){
                            int offset = Integer.parseInt(case_display);
                            url_page = url_item.replace(replace_nxt_p, "") + class_next_page + (ipage-1)*offset;
                        }else if(web_name.indexOf("robotstart") >= 0){
                            url_page = url_item.replace(replace_nxt_p + keyword, "") + class_next_page + ipage + "&s="+keyword;
                        }else{
                            url_page = url_item.replace(replace_nxt_p, "") + class_next_page + ipage;
                        }
                    }
                    
                    this.getListHrefsItem(url_page, class_href, domain, class_title, href_storge);
                    getContext().setVariable("check_detail", " ipage: " + ipage + ", "+ url_page);
                    ipage++;
                }
            }else{
                String url_page = url_item;
                this.getListHrefsItem(url_page, class_href, domain, class_title, href_storge);
                getContext().setVariable("check_detail", " Website only one page");
            }
            
            ////write list href to data storage
            //this.writeDataToStorge(keyword, fileNameDS, this.arr_hrefs);
            
            //get top 20
            this.arr_hrefs = this.getTop20(this.arr_hrefs);
            this.arr_title = this.getTop20(this.arr_title);
            
            //write list href to data storage
            this.writeDataToStorge(keyword, fileNameDS, this.arr_hrefs);
            
            //get data content top 20 href
            this.getDataContentHref(url_web, web_name, keyword, this.arr_hrefs, this.arr_title);
            
        }catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex);
        }
    }
    
    //get data with save page source (get list href from html)
    public void GetHrefsAndContentFromHtml(){
        String url_web = getContext().getVariableAsString("url_web");
        String web_name = getContext().getVariableAsString("web_name");
        String class_href = getContext().getVariableAsString("class_href");
        String class_title = getContext().getVariableAsString("class_title");
        String domain = getContext().getVariableAsString("domain");
        String replace_nxt_p = getContext().getVariableAsString("replace_nxt_p");
        String class_next_page = getContext().getVariableAsString("class_next_page");
        String case_display = getContext().getVariableAsString("case_display");
        String ele_page = getContext().getVariableAsString("class_gsc_cursor_page");
        String keyword = getContext().getVariableAsString("keyword");
        
        String fileNameDS = "data_storge/" + web_name.toLowerCase() + ".xlsx";
        ArrayList<String> href_storge = this.loadDataToArrayList(keyword, fileNameDS, 0);
            
        this.getHrefFromHTML(class_href,  class_title, domain, href_storge);
        
        //write list href to data storage
        this.writeDataToStorge(keyword, fileNameDS, this.arr_hrefs);

        //get top 20
        this.arr_hrefs = this.getTop20(this.arr_hrefs);
        this.arr_title = this.getTop20(this.arr_title);
        
        //get data content top 20 href
        this.getDataContentHref(url_web, web_name, keyword, this.arr_hrefs, this.arr_title);
    }
    
    //get href from html
    public void getHrefFromHTML(String class_href, String class_title, String domain, ArrayList<String> href_storge){
        try {
            String web_name = getContext().getVariableAsString("web_name");
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
                    String path_file_html = fullPath + "/" + file.getName();
                    content = Jsoup.parse(new File(path_file_html), "UTF-8").toString();
                    getContext().setVariable("check_detail", "content: " + content);
                    Document doc = Jsoup.parse(content);

                    String arr_class_href[] = class_href.split(" !@! ");
                    String arr_class_title[] = class_title.split(" !@! ");

                    for(int cls = 0; cls < arr_class_href.length; cls++) {
                        Elements hrefs = doc.select(arr_class_href[cls]);
                        Elements titles = doc.select(arr_class_title[cls]);

                        for(int j = 0; j <  hrefs.size(); j++){
                            String str_url_tmp = "";
                            String title_tmp = titles.get(j).text();
                                                 
                            if(web_name.indexOf("itmedia") >= 0){
                                str_url_tmp = hrefs.get(j).attr("href");
                            }else{
                                if(hrefs.get(j).attr("href").indexOf(domain) < 0 && hrefs.get(j).attr("href").indexOf("tech.ascii.jp") < 0 && hrefs.get(j).attr("href").indexOf("special.nikkeibp.co.jp") < 0 && hrefs.get(j).attr("href").indexOf("https://active.nikkeibp.co.jp") < 0 ){
                                    str_url_tmp = domain + hrefs.get(j).attr("href");
                                }else{
                                    str_url_tmp = hrefs.get(j).attr("href");
                                }
                            }

                            if(!href_storge.contains(str_url_tmp) && !this.arr_hrefs.contains(str_url_tmp) && (str_url_tmp.indexOf("/tag/") < 0) && (str_url_tmp.indexOf("page=") < 0)){
                                this.arr_hrefs.add(str_url_tmp);
                                this.arr_title.add(title_tmp);
                            }
                        }
                    }
                }
            }
        } catch (StopRequestException ex) {
            getContext().setVariable("check_detail", "Error StopRequestException: " + ex.toString());
            throw ex;
        } catch(IOException e){
            getContext().setVariable("check_detail", "Error IOException: " + e.toString());
            throw new RuntimeException(e);
        }
    }
    
    //funtion create enough top 20 when  result not enough 20 record
    public ArrayList<String> getTop20(ArrayList<String> arr_str){
        ArrayList<String> arr_top20 = new ArrayList<String>();
        int loop = 20;
        for(int i = 0; (i < arr_str.size()) && (i < loop); i++){
            arr_top20.add(arr_str.get(i));
        }
        int count = arr_top20.size();
        while(count < 20){
            arr_top20.add("null");
            count++;
        }
        return arr_top20;
    }
    
    //get list href from website after enter keywords
    public void getListHrefsItem(String url_item, String class_href, String domain, String class_title, ArrayList<String> href_storge){
        
        try {
            URL u;
            Scanner s;
            String content = "";
            u = new URL(url_item);
            Document doc = Jsoup.connect(url_item).get();
            String charset = doc.charset().toString();
            getContext().setVariable("charset", charset);
            getContext().setVariable("check_detail", "url_item: " + url_item);

            String arr_class_href[] = class_href.split(",");
            String arr_class_title[] = class_title.split(",");
            
            for(int cls = 0; cls < arr_class_href.length; cls++) {       
                Elements hrefs = doc.select(arr_class_href[cls]);
                Elements titles = doc.select(arr_class_title[cls]);
                if(titles.size() < hrefs.size()){
                    titles = doc.select(arr_class_href[cls]);
                }
                
                for(int i = 0; i <  hrefs.size(); i++){
                    String str_url_tmp = "";
                    String title_tmp = titles.get(i).text();
                    if(hrefs.get(i).attr("href").indexOf(domain) < 0){
                        str_url_tmp = domain + hrefs.get(i).attr("href");
                        //arr_temp.add(domain + href.attr("href"));
                    }else{
                        str_url_tmp = hrefs.get(i).attr("href");
                        //arr_temp.add(href.attr("href"));
                    }
                    if(str_url_tmp.indexOf("/msg/?ty") > 0){
                        str_url_tmp = str_url_tmp.substring(0, str_url_tmp.indexOf("/msg/?ty"));
                    }else if(str_url_tmp.indexOf("/?ty") > 0){
                        str_url_tmp = str_url_tmp.substring(0, str_url_tmp.indexOf("/?ty"));
                    }else if(str_url_tmp.indexOf("_message/") > 0){
                        str_url_tmp = str_url_tmp.replace("_message/", "_detail/");
                    }else if(str_url_tmp.indexOf("/-tab__pr/") > 0){
                        str_url_tmp = str_url_tmp.replace("/-tab__pr/", "/-tab__jd/");
                    }else if(str_url_tmp.indexOf("nx1_") > 0){
                        str_url_tmp = str_url_tmp.replace("nx1_", "nx2_");
                        str_url_tmp = str_url_tmp.replace("&list_disp_no=1", "");
                        str_url_tmp = str_url_tmp.replace("n_ichiran_cst_n5_ttl", "ngen_tab-top_info");
                    }else{
                        str_url_tmp = str_url_tmp.replace(",", "");
                    }
                    
                    if(!href_storge.contains(str_url_tmp) && !this.arr_hrefs.contains(str_url_tmp) && (str_url_tmp.indexOf("/tag/") < 0)){
                        getContext().setVariable("check_detail", "this.arr_hrefs: " + this.arr_hrefs.size());
                        //this.map_item.put(name_com, str_url_tmp);
                        this.arr_hrefs.add(str_url_tmp);
                        this.arr_title.add(title_tmp);
                        href_storge.add(str_url_tmp);
                    }
                    getContext().setVariable("check_detail", "titles.get(3).text(): " + titles.size());
                }
            }
            
        } catch (StopRequestException ex) {
            getContext().setVariable("check_detail", "Error StopRequestException: " + ex.toString());
            throw ex;
        } catch(IOException e){
            getContext().setVariable("check_detail", "Error IOException getLinkItem: " + url_item);
            throw new RuntimeException(e);
        }
    }
    
    //load data storage
    public ArrayList<String> loadDataToArrayList(String sheetName, String fileName, int num_col){
        try{
            ArrayList<String> arr_data = new ArrayList<String>();

            //String basePath = new File(fileName).getAbsolutePath();
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = URLDecoder.decode(path, "UTF-8");
            //fullPath = fullPath.replace("classes/", fileName);
            if(fullPath.indexOf("classes") >= 0){
                fullPath = fullPath.replace("classes/", fileName);
            }
            
            if(fullPath.indexOf("src") >= 0){
                fullPath = fullPath.replace("src/", fileName);
            }
            
            File excelFile = new File(fullPath);
            FileInputStream fis = new FileInputStream(excelFile);

            // we create an XSSF Workbook object for our XLSX Excel File
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            // choose sheet name
            XSSFSheet sheet = workbook.getSheet(sheetName);
            getContext().setVariable("check_detail", "Load data storge success. sheetName: " + sheetName);
            //we iterate on rows
            Iterator<Row> rowIt = sheet.iterator();

            while(rowIt.hasNext()) {
                Row row = rowIt.next();

                //get value of first column 0
                Cell cell = row.getCell(num_col);

                //add cell value to array string (storge data)
                arr_data.add(cell.getStringCellValue());
            }
            getContext().setVariable("check_detail", "Load data storge success.");
            fis.close();
            return arr_data;
        } catch(IOException e){
            getContext().setVariable("check_detail", "Load data storge error. " + e.toString());
            throw new RuntimeException(e);
        }
    }
    
    //write to data storage data href 
    public void writeDataToStorge(String sheetName, String fileName, ArrayList<String> arr_data){
        try{
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = URLDecoder.decode(path, "UTF-8");
            
            if(fullPath.indexOf("classes") >= 0){
                fullPath = fullPath.replace("classes/", fileName);
            }

            if(fullPath.indexOf("src") >= 0){
                fullPath = fullPath.replace("src/", fileName);
            }

            File excelFile = new File(fullPath);
            FileInputStream fis = new FileInputStream(excelFile);

            // we create an XSSF Workbook object for our XLSX Excel File
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            // choose sheet name
            XSSFSheet sheet = workbook.getSheet(sheetName);
            int lastRow=sheet.getLastRowNum();

            int length = arr_data.size();
            for(String href : arr_data){
                if(href != "null"){
                    Row row = sheet.createRow(++lastRow);
                    row.createCell(0).setCellValue(href);
                }
            }
            fis.close();
            FileOutputStream output_file =new FileOutputStream(new File(fullPath));
            //write changes
            workbook.write(output_file);
            output_file.close();
            getContext().setVariable("check_detail", "write data storge success");

        }catch(Exception e){
            getContext().setVariable("check_detail", "write data storge error. " + e.toString());
        }
    }

    //get content each href item
    public void getDataContentHref(String url_web, String web_name, String keyword, ArrayList<String> arr_urls, ArrayList<String> arr_title){
        try{
            //Get directory project
            String path = getContext().getVariableAsString("_PROJECT_DIR");
            String fullPathDir = URLDecoder.decode(path, "UTF-8");
            fullPathDir = fullPathDir.replace("\\", "/");
            fullPathDir = fullPathDir + "/result/" + web_name + ".csv";
            for (int i = 0; i < arr_urls.size(); i++) {
                String str_url = arr_urls.get(i);
                String str_title = arr_title.get(i);
                if(str_url.indexOf("null") < 0){
                    this.getDataContentItemHref(i+1, fullPathDir, str_url, str_title, web_name, url_web, keyword);
                }else{
                    this.writeDataToFile(i+1, fullPathDir, "Sheet1", web_name, url_web, keyword, "", "", "", "", "");
                }
            }
        }catch(Exception e){
            getContext().setVariable("check_detail", "write data storge error. " + e.toString());
        }
    }
    
    //get data content href item
    public void getDataContentItemHref(int ind, String path_write, String str_url, String title, String web_name, String url_web, String keyword){
        
        //String web_name = getContext().getVariableAsString("web_name");
        String class_date_post = getContext().getVariableAsString("class_date_post");
        String class_source = getContext().getVariableAsString("class_source");
        String class_content = getContext().getVariableAsString("class_content");
        String class_tr_dl = getContext().getVariableAsString("class_tr_dl");
        //String keyword = getContext().getVariableAsString("keyword");
        //String url_web = getContext().getVariableAsString("url_web");
        
        try {
            URL u;
            Scanner s;
            //String content = "";
            u = new URL(str_url);

            Document doc_charset = Jsoup.connect(str_url).get();
            String charset = doc_charset.charset().toString();

            String post_date = doc_charset.select(class_date_post).text();
            String source = doc_charset.select(class_source).text();
            Elements contents = doc_charset.select(class_content);
            
            String content = "";
            for(Element ele : contents){
                if(content.length() == 0){
                    content = content + ele.text();
                }else{
                    content = content + "\n\r" + ele.text();
                }
            }

            String sheeName = "Sheet1";
            this.writeDataToFile(ind, path_write, sheeName, web_name, url_web, keyword, str_url, title, post_date, source, content);
            getContext().setVariable("check_detail", "writeDataToFile completed!!!!!!");

        } catch (StopRequestException ex) {
            getContext().setVariable("check_detail", "error: " + str_url);
            throw ex;
        } catch(IOException e){
            getContext().setVariable("check_detail", "error: " + str_url);
            throw new RuntimeException(e);
        }
    }
    
    // write to csv file 
    public void writeDataToFile(int ind, String path_out_file, String sheeName, String web_name, String url_web, String keyword, String url_item, String title, String date_posts, String sources, String contents){
        try{

            File in = new File(path_out_file);
            PrintWriter out = null;
            OutputStream os = null;
            if ( in.exists() && !in.isDirectory() ) {
                os = new FileOutputStream(path_out_file, true);
            }
            else {
                os = new FileOutputStream(path_out_file, false);
            }
            
            //set format file
            os.write(239);
            os.write(187);
            os.write(191);
            
            //create output file
            out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
            
            out.print(ind);
            out.print(",");
            out.print('"' + web_name.replace("\"", "") + '"');
            out.print(",");
            out.print('"' + url_web.replace("\"", "") + '"');
            out.print(",");
            out.print('"' + keyword.replace("\"", "") + '"');
            out.print(",");
            out.print('"' + url_item.replace("\"", "") + '"');
            out.print(",");
            out.print('"' + title.replace("\"", "") + '"');
            out.print(",");
            out.print('"' + date_posts.replace("\"", "") + '"');
            out.print(",");
            out.print('"' + sources.replace("\"", "") + '"');
            out.print(",");
            out.print('"' + contents.replace("\"", "") + '"');
            out.print("\n");
            
            out.close();
            getContext().setVariable("check_status", "GetHref.java function writeContentToCSVFile successe!!!!!.");
            
        }catch(IOException e){
            getContext().setVariable("check_status", "GetHref.java function writeContentToCSVFile. IOException: " + e.toString());
        }
    }
  
    public static void main(String args[]) {
        GetHrefsTop20 script = new GetHrefsTop20();
        ApplicationSupport robot = new ApplicationSupport();
        AutomatedRunnable runnable = robot.createAutomatedRunnable(script, "GetHrefsTop20@" + Integer.toHexString(script.hashCode()), args, System.out, false);
        new Thread(runnable).start();
    }
}
