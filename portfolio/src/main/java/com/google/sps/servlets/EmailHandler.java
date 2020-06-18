package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMessage;  

// Handles image data on the '/images' page.
@WebServlet("/email")
public final class EmailHandler extends HttpServlet {

	/**
   * doGet handles HTTP requests to '/images' and prints URL in json format
   * to '/images' servlet page. Is compatible with edit requests and generic
   * comment post requests.
   * 
   * @param request HttpServletRequest used to make GET request.
   * @param response HttpServletResponse used in GET requests.
   * @throws IOException
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String message = request.getParameter("message");
    String recipient = "neelbagora@gmail.com";
    String subject = "New message from your website!";
    if (message == null) {
      message = "Test message";
    }
    send(recipient, subject, message);
    response.sendRedirect("/index.html");
  } 

  public static void send(String to, String subject, String msg) {  

    // Sender's email ID needs to be mentioned
    String from = "neelbagora@gmail.com";

    // Assuming you are sending email from localhost
    String host = "localhost";

    // Get system properties
    Properties properties = System.getProperties();

    // Setup mail server
    properties.setProperty("mail.smtp.host", host);

    // Get the default Session object.
    Session session = Session.getDefaultInstance(properties);
    
    try {
      // Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(session);
      
      // Set From: header field of the header.
      message.setFrom(new InternetAddress(from));
      
      // Set To: header field of the header.
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      
      // Set Subject: header field
      message.setSubject("This is the Subject Line!");
      
      // Now set the actual message
      message.setText("This is actual message");
      
      // Send message
      Transport.send(message);
    } catch (MessagingException mex) {
      mex.printStackTrace();
    }        
  } 
}