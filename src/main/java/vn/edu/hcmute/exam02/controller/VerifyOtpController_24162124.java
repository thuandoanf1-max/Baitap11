package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.hcmute.exam02.model.OtpChallenge_24162124;
import vn.edu.hcmute.exam02.model.OtpVerificationResult_24162124;
import vn.edu.hcmute.exam02.model.PendingRegistration_24162124;
import vn.edu.hcmute.exam02.service.OtpService_24162124;
import vn.edu.hcmute.exam02.service.UserService_24162124;
import vn.edu.hcmute.exam02.service.impl.OtpServiceImpl_24162124;
import vn.edu.hcmute.exam02.service.impl.UserServiceImpl_24162124;

public class VerifyOtpController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService_24162124 userService = new UserServiceImpl_24162124();
    private final OtpService_24162124 otpService = new OtpServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        PendingRegistration_24162124 pendingRegistration = getPendingRegistration(session);
        if (pendingRegistration == null || getOtpChallenge(session) == null) {
            response.sendRedirect(request.getContextPath() + "/register?missing=1");
            return;
        }
        request.setAttribute("pendingEmail", pendingRegistration.getEmail());
        request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        PendingRegistration_24162124 pendingRegistration = getPendingRegistration(session);
        OtpChallenge_24162124 otpChallenge = getOtpChallenge(session);
        if (pendingRegistration == null || otpChallenge == null) {
            response.sendRedirect(request.getContextPath() + "/register?missing=1");
            return;
        }

        request.setAttribute("pendingEmail", pendingRegistration.getEmail());
        OtpVerificationResult_24162124 result =
                otpService.verify(otpChallenge, request.getParameter("otp"));
        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            if (result.isTerminal()) {
                clearRegistrationSession(session);
                request.setAttribute("registrationClosed", true);
            }
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        try {
            userService.completeRegistration(pendingRegistration);
            clearRegistrationSession(session);
            response.sendRedirect(request.getContextPath() + "/login?registered=1");
        } catch (IllegalArgumentException exception) {
            clearRegistrationSession(session);
            request.setAttribute("error", exception.getMessage());
            request.setAttribute("registrationClosed", true);
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute("error", "Không thể hoàn tất đăng ký trên SQL Server.");
            getServletContext().log("Không thể tạo user sau khi xác nhận OTP", exception);
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp")
                    .forward(request, response);
        }
    }

    private PendingRegistration_24162124 getPendingRegistration(HttpSession session) {
        return session == null
                ? null
                : (PendingRegistration_24162124) session.getAttribute("pendingRegistration");
    }

    private OtpChallenge_24162124 getOtpChallenge(HttpSession session) {
        return session == null
                ? null
                : (OtpChallenge_24162124) session.getAttribute("registrationOtp");
    }

    private void clearRegistrationSession(HttpSession session) {
        if (session != null) {
            session.removeAttribute("pendingRegistration");
            session.removeAttribute("registrationOtp");
        }
    }
}
