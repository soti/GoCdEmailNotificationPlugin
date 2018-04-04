package net.soti.go.plugin.notification.email.utils;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.thoughtworks.go.plugin.api.logging.Logger;

import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.TO;


/**
 * User: wsim
 * Date: 2018-04-04
 */
public class SmtpMailSender {
    private static final Logger LOG = Logger.getLoggerFor(SmtpMailSender.class);
    private final String smtpHost;
    private final int port;
    private final String sender;
    private final Properties props = new Properties();

    public SmtpMailSender(String smtpHost, int port, boolean enableSsl, String sender) {
        this.smtpHost = smtpHost;
        this.port = port;
        this.sender = sender;

        props.put("mail.smtp.starttls.enable", String.valueOf(enableSsl).toLowerCase());
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.timeout", 30 * 1000);
    }

    public void sendEmail(final String title, final String messageBody, final List<String> recepients, final String cc, final String bcc) {
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
            message.setRecipients(TO, receivers.toArray(new InternetAddress[0]));
            message.setSubject(title);
            message.setContent(messageBody, "text/html; charset=utf-8");
            message.setSentDate(new Date());
            message.setSender(new InternetAddress(sender));
            message.setReplyTo(InternetAddress.parse(sender));
            if (cc != null && cc.length() > 0) {
                message.setRecipients(BCC, InternetAddress.parse(cc));
            }
            if (bcc != null && bcc.length() > 0) {
                message.setRecipients(BCC, InternetAddress.parse(bcc));
            }

            Transport transport = session.getTransport();
            transport.connect(smtpHost, port, null, null);
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
