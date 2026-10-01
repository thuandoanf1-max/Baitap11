package vn.edu.hcmute.exam02.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OtpChallenge_24162124 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String code;
    private final LocalDateTime expiresAt;
    private int failedAttempts;

    public OtpChallenge_24162124(String code, LocalDateTime expiresAt) {
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void increaseFailedAttempts() {
        failedAttempts++;
    }
}
