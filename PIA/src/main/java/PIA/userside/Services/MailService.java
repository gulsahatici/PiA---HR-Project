package PIA.userside.Services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailService {
    private static final String FROM_EMAIL = "talentpathgroup1@gmail.com";
    private static final String PASSWORD = "eubv rtas bjwt fela";

    public static void sendDecisionMail(String toEmail, String decision) throws MessagingException {
        String subject;
        String body;

        if ("accept".equalsIgnoreCase(decision)) {
            subject = "🎉 Congratulations!";
            body = "Tebrikler! İşe kabul edildiniz. En kısa sürede sizinle iletişime geçeceğiz.";
        } else if ("reject".equalsIgnoreCase(decision)) {
            subject = "Bilgilendirme";
            body = "Değerlendirmeniz sonucu maalesef işe alım sürecine devam edemiyoruz. İlginiz için teşekkür ederiz.";
        } else {
            throw new IllegalArgumentException("Invalid decision value");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
    }
}



