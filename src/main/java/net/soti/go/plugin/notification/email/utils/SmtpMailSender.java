package net.soti.go.plugin.notification.email.utils;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.thoughtworks.go.plugin.api.logging.Logger;

import static javax.mail.Message.RecipientType.TO;


/**
 * User: wsim
 * Date: 2018-04-04
 */
public class SmtpMailSender {
    private static final Logger LOG = Logger.getLoggerFor(SmtpMailSender.class);

    private SmtpMailSender() {

    }

    public static void sendEmail(String title, String messageBody, List<String> recepients, String sender) {
        final String host = "internalmail.corp.soti.net";
        final int port = 25;

        Properties props = new Properties();

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.timeout", 30 * 1000);

        Session session = Session.getInstance(props);

        List<InternetAddress> receivers = new ArrayList<>();

        for (String recepient : recepients) {
            try {
                InternetAddress address = new InternetAddress(recepient);
                receivers.add(address);
            } catch (AddressException e) {
                LOG.error(String.format("Illegal mail address format '%s'.", recepient));
            }
        }

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(TO, (Address[]) receivers.toArray());
            message.setSubject(title);
            message.setContent(message, "text/html");
            message.setSentDate(new Date());
            message.setText(messageBody);
            message.setSender(new InternetAddress(sender));
            message.setReplyTo(InternetAddress.parse(sender));

            Transport transport = session.getTransport();
            transport.connect(host, port, null, null);
            transport.sendMessage(message, message.getRecipients(TO));
        } catch (AddressException e) {
            LOG.error("Failed to send mail, Failed to parse address", e);
        } catch (NoSuchProviderException e) {
            LOG.error("Failed to send mail, no such provider", e);
        } catch (MessagingException me) {
            LOG.error("Failed to send mail", me);
        }
    }
}
