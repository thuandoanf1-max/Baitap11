package vn.edu.hcmute.exam02.service;

import vn.edu.hcmute.exam02.model.OtpChallenge_24162124;
import vn.edu.hcmute.exam02.model.OtpVerificationResult_24162124;

public interface OtpService_24162124 {
    OtpChallenge_24162124 createChallenge();

    OtpVerificationResult_24162124 verify(
            OtpChallenge_24162124 challenge, String submittedOtp);
}
