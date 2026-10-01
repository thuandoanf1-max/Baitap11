package vn.edu.hcmute.exam02.model;

public class OtpVerificationResult_24162124 {
    private final boolean success;
    private final boolean terminal;
    private final String message;

    public OtpVerificationResult_24162124(boolean success, boolean terminal, String message) {
        this.success = success;
        this.terminal = terminal;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public String getMessage() {
        return message;
    }
}
