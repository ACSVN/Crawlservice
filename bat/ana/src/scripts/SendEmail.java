/**
 * Created on Wed Aug 28 04:48:44 ICT 2019
 * HeartCore Robo Desktop v5.0.3 (Build No. 5.0.3-20190618.1)
 **/
package scripts;
import com.tplan.robot.scripting.*;
import com.tplan.robot.*;
import java.awt.*;

import java.net.URLDecoder;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmail extends DefaultJavaTestScript  {

    public void test() {
        
        //Ennable: https://myaccount.google.com/lesssecureapps?pli=1
        
        try {
            String web_name = getContext().getVariableAsString("web_name");
            String com_name = getContext().getVariableAsString("com_name");
            String com_email = getContext().getVariableAsString("com_email");
            
            //Get directory project
            String path = getContext().getVariableAsString("_PROJECT_DIR");
            String fullPathDir = URLDecoder.decode(path, "UTF-8");
            fullPathDir = fullPathDir.replace("\\", "/");

            String pathFileName = fullPathDir + "/result/" + web_name + ".csv";    
            String fileName =    web_name + ".csv";
            String username = "quoc.nguyen@addix.vn";
            String password = "addixvn007";
            
            String mailFrom = "quoc.nguyen@addix.vn";
            String mailTo = com_email;
            String mailCC = "nhan.lam@addix.vn";
            String mailBCC = "ly.hua@addix.vn";

            String subject = "Auto Send Email";
            String companyName = com_name;
            
            //Email myEmail = new Email();
            String content = this.setContent(companyName);
            
            this.sendEmail(mailFrom, mailTo, mailCC, mailBCC, username, password, subject, content, pathFileName, fileName);
            getContext().setVariable("check_detail", "SendEmail successful" );
        } catch (StopRequestException ex) {
            getContext().setVariable("check_detail", "SendEmail error:  " + ex.toString());
            throw ex;
        }catch(Exception ex){
            getContext().setVariable("check_detail", "SendEmail error:  " + ex.toString());
        } 
    }
    
    public String setContent(String companyName){
        StringBuffer body = new StringBuffer("Dear " + companyName + " company, <br><br>");        
        body.append("Thank you for using our service. <br>");
        body.append("It was finished to get data and send you as a csv file.<br>");
        body.append("Please confirm the contents.<br><br>");
        body.append("If you have some problems, please do not hesitate to contact us.<br><br>");
        body.append("Best regards, <br>");
        body.append("ADDIX, Inc <br>");
        return  body.toString();
    }
    public void sendEmail(String fromMail, String toMail, String ccMail, String bccMail,
            String username, String password, String subject, String content, String pathFile, String nameFile)
            throws AddressException, MessagingException{
        // Sender's email ID needs to be mentioned
        String mailFrom = fromMail;
        String mailTo = toMail;
        String mailCC = ccMail;
        String mailBCC = bccMail;
        
        Properties mailServerProperties;
        Session getMailSession;
        MimeMessage mailMessage;
        // Step1: setup Mail Server
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        // Step2: get Mail Session
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        mailMessage = new MimeMessage(getMailSession);
        
        mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo)); 
        if(mailBCC != ""){
            mailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(mailBCC)); 
        }
        if(mailCC != ""){
            mailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(mailCC)); 
        }    
        
        mailMessage.setSubject(subject);   
        
        //Part one
        BodyPart mailMessageBodyPart = new MimeBodyPart();
        mailMessageBodyPart.setContent(content, "text/html");
        // Create a multipar message
        Multipart multipart = new MimeMultipart();
        // Set text message part
        multipart.addBodyPart(mailMessageBodyPart);
        
        // Part two is attachment
        mailMessageBodyPart = new MimeBodyPart();
        String filename = pathFile;
        DataSource source = new FileDataSource(filename);
        mailMessageBodyPart.setDataHandler(new DataHandler(source));
        mailMessageBodyPart.setFileName(nameFile);
        multipart.addBodyPart(mailMessageBodyPart);
        
        // Send the complete message parts
        mailMessage.setContent(multipart);
        
        // Step3: Send mail
        Transport transport = getMailSession.getTransport("smtp");

        // Using admin mail + password to send email
        transport.connect("smtp.gmail.com", username, password); 
        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        transport.close();
    }
   
    public static void main(String args[]) {
        SendEmail script = new SendEmail();
        ApplicationSupport robot = new ApplicationSupport();
        AutomatedRunnable runnable = robot.createAutomatedRunnable(script, "SendEmail@" + Integer.toHexString(script.hashCode()), args, System.out, false);
        new Thread(runnable).start();
    }
}
