package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.server.Utils.Configuration;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 01/09/2014.
 */
public class MailContent {

    static public enum EMAIL_TYPE {
        ADMIN, ORDER, CONSUMER, NONE
    };

    static public HashMap<EMAIL_TYPE, String> emailTemplates = new HashMap<EMAIL_TYPE, String>();
    static {
        emailTemplates.put(EMAIL_TYPE.CONSUMER, getTemplate("consumerTemplate.html"));
        emailTemplates.put(EMAIL_TYPE.ADMIN, getTemplate("adminTemplate.html"));
        emailTemplates.put(EMAIL_TYPE.ORDER, getTemplate("supplierTemplate.html"));
        emailTemplates.put(EMAIL_TYPE.NONE, "$container$");
    }
    static String getTemplate(String templateName) {
        String template = null;
        try {
            template = IOUtils.toString(new FileReader(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.email_templatesPath) + templateName));
        } catch (Exception e) {
            try {
                template = IOUtils.toString(Mailing.class.getResourceAsStream("/" + templateName));
            } catch (IOException e1) {
                // TODO - fallback to default template
            }
        }
        return template != null ? template : "$container$";
    }

    private EMAIL_TYPE template;
    private StringBuffer htmlContent;
    private StringBuffer textContent;

    public MailContent(EMAIL_TYPE template) {
        this.template = template;
        htmlContent = new StringBuffer();
        textContent = new StringBuffer();
    }

    public void addTitle(String title) {
        htmlContent.append("<h1 style='font: 1.2em; color: #333;'>" + title + "</h1>");
        textContent.append(title + "\r\n");
    }

    public void sendEmail(String recipients, String subject, boolean bcc) throws Exception {
        String template = emailTemplates.get(this.template);
        Mailing.sendEmail(template, recipients, subject, htmlContent.toString(), textContent.toString(), bcc);
    }

    public void addLine(String text) {
        htmlContent.append("<p style='font-size: 1em; color: #333; padding: 10px 5px; margin: 5px 0px;'>" + text + "</p>");
    }

    public static String getButtonCode(String text, String tooltip, String url) {
        return "<a title='" + tooltip + "' style='margin: 2px 0; text-align: center; border: 1px solid #333333; border-radius: 3px; font-size: 1em;" +
                "font-weight: bold; line-height: normal; padding: 8px 15px; position: relative; cursor: pointer; text-shadow: 0 1px 0 rgba(0, 0, 0, 0.7);" +
                "text-decoration: none; color: #FFFFFF; background: #e89216; background: linear-gradient(#e89216, #e25e12) repeat scroll 0 0 transparent;' " +
                "href='" + url + "'>" + text + "</a>";
    }

    public void addAction(String text, String tooltip, String url) {
        htmlContent.append("<p style='font-size: 1em; color: #333; padding: 10px 5px; margin: 5px 0px;'>" + getButtonCode("Click to " + text, tooltip, url) + "</p>");
        textContent.append("Copy paste this link '" + url + "' to " + text + "\r\n");
    }

    public void addTable(List<List<String>> rowsAndColumns) {
        // ordered by rows first and then columns
        htmlContent.append("<table cellpadding='10' style='margin: 10px 0px;'>");
        for(List<String> rowContent : rowsAndColumns) {
            htmlContent.append("<tr>");
            for(String columnContent : rowContent) {
                htmlContent.append("<td>" + columnContent + "</td>");
                textContent.append(rowContent + " ");
            }
            htmlContent.append("</tr>");
            textContent.append("\r\n");
        }
        htmlContent.append("</table>");
    }

    public void sendEmail(List<User> users, String subject, boolean bcc) throws Exception {
        sendEmail(ListUtil.toString(users, new ListUtil.GetLabel<User>() {
            @Override
            public String getLabel(User value) {
                return value.getEmail();
            }
        }, ","), subject, bcc);
    }

}
