package vn.edu.hcmute.exam02.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import vn.edu.hcmute.exam02.service.MailService_24162124;

public class MailServiceImpl_24162124 implements MailService_24162124 {
    private static final String USERNAME_PLACEHOLDER = "YOUR_SMTP_EMAIL";
    private static final String PASSWORD_PLACEHOLDER = "YOUR_SMTP_APP_PASSWORD";
    private final Properties configuration = new Properties();

    public MailServiceImpl_24162124() {
        try (InputStream input = MailServiceImpl_24162124.class.getClassLoader()
                .getResourceAsStream("smtp.properties")) {
            if (input == null) {
                throw new IllegalStateException("Không tìm thấy smtp.properties");
            }
            configuration.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Không đọc được smtp.properties", exception);
        }
    }

    @Override
    public void sendRegistrationOtp(String recipientEmail, String otp) throws MessagingException {
        String host = getConfig("SMTP_HOST", "smtp.host");
        String port = getConfig("SMTP_PORT", "smtp.port");
        String username = getConfig("SMTP_USERNAME", "smtp.username");
        String password = getConfig("SMTP_PASSWORD", "smtp.password");
        String from = getConfig("SMTP_FROM", "smtp.from");
        String auth = getConfig("SMTP_AUTH", "smtp.auth");
        String startTls = getConfig("SMTP_STARTTLS_ENABLE", "smtp.starttls.enable");

        if (USERNAME_PLACEHOLDER.equals(username)
                || PASSWORD_PLACEHOLDER.equals(password)
                || USERNAME_PLACEHOLDER.equals(from)) {
            throw new MessagingException(
                    "Chưa cấu hình tài khoản SMTP trong smtp.properties hoặc environment variables");
        }

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", startTls);
        mailProperties.put("mail.smtp.connectiontimeout", "10000");
        mailProperties.put("mail.smtp.timeout", "10000");
        mailProperties.put("mail.smtp.writetimeout", "10000");

        Session mailSession = Session.getInstance(
                mailProperties, new SmtpAuthenticator_24162124(username, password));

        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Mã OTP đăng ký BookStore 24162124", "UTF-8");
        message.setText(
                "Mã OTP xác nhận đăng ký của bạn là: " + otp
                        + "\nMã có hiệu lực trong 5 phút."
                        + "\nNếu bạn không thực hiện đăng ký, hãy bỏ qua email này.",
                "UTF-8");
        Transport.send(message);
    }

    private String getConfig(String environmentName, String propertyName) throws MessagingException {
        String environmentValue = System.getenv(environmentName);
        String value = environmentValue == null || environmentValue.trim().isEmpty()
                ? configuration.getProperty(propertyName)
                : environmentValue;
        if (value == null || value.trim().isEmpty()) {
            throw new MessagingException("Thiếu cấu hình SMTP: " + propertyName);
        }
        return value.trim();
    }

    private static final class SmtpAuthenticator_24162124 extends Authenticator {
        private final String username;
        private final String password;

        private SmtpAuthenticator_24162124(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
