package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.shared.entities.ApplicationSettings;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;

public class Mailing {

    static HashSet<String> smtpPorts = new HashSet<String>(Arrays.asList("25", "2525", "587", "465", "587", "2526"));

    static public void sendEmail(String template, String recipients, String subject, String message, String plainText, boolean asBcc) throws Exception {

        ApplicationSettings settings = ServerUtil.getSettings();
        String from = settings.getEmailFrom();
        String host = settings.getEmailServer();
        Integer port = settings.getEmailPort();
        boolean isSMTPS = settings.isSmtps();
        boolean enableTLS = settings.isEnableTLS();
        // check port
        final String account = settings.getEmailAccount();
        final String password = settings.getEmailPassword();

        // Create propertiesEditor for the Session
        Properties props = new Properties();

        // If using static Transport.send(),
        // need to specify the mail server here
        if(isSMTPS) {
            props.put("mail.smtps.host", host);
            props.put("mail.smtps.port", port);
            props.put("mail.smtps.auth", "true");
            props.put("mail.smtps.starttls.enable", enableTLS);
        } else {
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", enableTLS);
        }
        // To see what is going on behind the scene
        props.put("mail.debug", "true"); 

        // create authenticator
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        };

        // Get a session
        Session session = Session.getInstance(props, authenticator);

        try {
            // Get a Transport object to send e-mail
            Transport bus = session.getTransport(isSMTPS ? "smtps" : "smtp");

            // Connect only once here
            // Transport.send() disconnects after each send
            // Usually, no username and password is required for SMTP
            bus.connect();

            // Instantiate a message
            Message msg = new MimeMessage(session);

            // Set message attributes
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address;
            if(asBcc) {
                address = InternetAddress.parse(from);
	            msg.setRecipients(Message.RecipientType.TO, address);
	            // Parse comma/space-separated list. Cut some slack.
	            msg.setRecipients(Message.RecipientType.BCC,
	                                InternetAddress.parse(recipients, false));
            } else {
            	address = InternetAddress.parse(recipients);
	            msg.setRecipients(Message.RecipientType.TO, address);
            }

            msg.setSubject(subject);
            msg.setSentDate(new Date());
            // Set message content
            // set plain text and html content
            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(decorateText(plainText), "text/plain; charset=UTF-8;");
            // HTML version
            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(decorateMessage(template, message), "text/html; charset=UTF-8;");
            // Create the Multipart.  Add BodyParts to it.
            final Multipart mp = new MimeMultipart("alternative");
            mp.addBodyPart(textPart);
            mp.addBodyPart(htmlPart);
            // Set Multipart as the message's content
            msg.setContent(mp);

            // now send the mail message
            msg.saveChanges();
            bus.sendMessage(msg, address);

            bus.close();

        } catch (MessagingException mex) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
            // How to access nested exceptions
            while (mex.getNextException() != null) {
                // Get next exception in chain
                Exception ex = mex.getNextException();
                ex.printStackTrace();
                if (!(ex instanceof MessagingException)) break;
                else mex = (MessagingException)ex;
            }
            throw new Exception("Error sending email");
        }
    }

    private static String decorateMessage(String template, String content) {
		return template.replace("$container$", content);
	}

    private static String decorateText(String message) {
		return message;
	}

    // A simple multipart/mixed e-mail. Both body parts are text/plain.
    public static void setMultipartContent(Message msg) throws MessagingException {
        // Create and fill first part
        MimeBodyPart p1 = new MimeBodyPart();
        p1.setText("This is part one of a test multipart e-mail.");

        // Create and fill second part
        MimeBodyPart p2 = new MimeBodyPart();
        // Here is how to set a charset on textual content
        p2.setText("This is the second part", "us-ascii");

        // Create the Multipart.  Add BodyParts to it.
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(p1);
        mp.addBodyPart(p2);

        // Set Multipart as the message's content
        msg.setContent(mp);
    }



    // Set a file as an attachment.  Uses JAF FileDataSource.
    public static void setFileAsAttachment(Message msg, String filename)
             throws MessagingException {

        // Create and fill first part
        MimeBodyPart p1 = new MimeBodyPart();
        p1.setText("This is part one of a test multipart e-mail." +
                    "The second part is file as an attachment");

        // Create second part
        MimeBodyPart p2 = new MimeBodyPart();

        // Put a file in the second part
        FileDataSource fds = new FileDataSource(filename);
        p2.setDataHandler(new DataHandler(fds));
        p2.setFileName(fds.getName());

        // Create the Multipart.  Add BodyParts to it.
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(p1);
        mp.addBodyPart(p2);

        // Set Multipart as the message's content
        msg.setContent(mp);
    }

}
