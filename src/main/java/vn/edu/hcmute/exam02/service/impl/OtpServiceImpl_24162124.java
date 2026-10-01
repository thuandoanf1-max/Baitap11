package vn.edu.hcmute.exam02.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import vn.edu.hcmute.exam02.model.OtpChallenge_24162124;
import vn.edu.hcmute.exam02.model.OtpVerificationResult_24162124;
import vn.edu.hcmute.exam02.service.OtpService_24162124;

public class OtpServiceImpl_24162124 implements OtpService_24162124 {
    private static final int OTP_BOUND = 1_000_000;
    private static final int OTP_EXPIRE_MINUTES = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public OtpChallenge_24162124 createChallenge() {
        String code = String.format("%06d", secureRandom.nextInt(OTP_BOUND));
        return new OtpChallenge_24162124(
                code, LocalDateTime.now().plusMinutes(OTP_EXPIRE_MINUTES));
    }

    @Override
    public OtpVerificationResult_24162124 verify(
            OtpChallenge_24162124 challenge, String submittedOtp) {
        if (challenge == null) {
            return new OtpVerificationResult_24162124(
                    false, true, "Phiên xác nhận OTP không tồn tại.");
        }
        if (LocalDateTime.now().isAfter(challenge.getExpiresAt())) {
            return new OtpVerificationResult_24162124(
                    false, true, "Mã OTP đã hết hạn. Vui lòng đăng ký lại.");
        }
        if (challenge.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            return new OtpVerificationResult_24162124(
                    false, true, "Bạn đã nhập sai OTP quá số lần cho phép.");
        }

        String normalizedOtp = submittedOtp == null ? "" : submittedOtp.trim();
        boolean matched = MessageDigest.isEqual(
                challenge.getCode().getBytes(StandardCharsets.UTF_8),
                normalizedOtp.getBytes(StandardCharsets.UTF_8));
        if (matched) {
            return new OtpVerificationResult_24162124(
                    true, true, "Xác nhận OTP thành công.");
        }

        challenge.increaseFailedAttempts();
        int remainingAttempts = MAX_FAILED_ATTEMPTS - challenge.getFailedAttempts();
        boolean terminal = remainingAttempts <= 0;
        String message = terminal
                ? "Bạn đã nhập sai OTP quá số lần cho phép."
                : "Mã OTP không đúng. Còn " + remainingAttempts + " lần thử.";
        return new OtpVerificationResult_24162124(false, terminal, message);
    }
}
