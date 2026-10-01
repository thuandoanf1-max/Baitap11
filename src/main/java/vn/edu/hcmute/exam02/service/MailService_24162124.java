package vn.edu.hcmute.exam02.service;

import jakarta.mail.MessagingException;

public interface MailService_24162124 {
    void sendRegistrationOtp(String recipientEmail, String otp) throws MessagingException;
}
