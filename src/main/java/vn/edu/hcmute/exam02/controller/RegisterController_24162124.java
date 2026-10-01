package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.hcmute.exam02.model.OtpChallenge_24162124;
import vn.edu.hcmute.exam02.model.PendingRegistration_24162124;
import vn.edu.hcmute.exam02.service.MailService_24162124;
import vn.edu.hcmute.exam02.service.OtpService_24162124;
import vn.edu.hcmute.exam02.service.UserService_24162124;
import vn.edu.hcmute.exam02.service.impl.MailServiceImpl_24162124;
import vn.edu.hcmute.exam02.service.impl.OtpServiceImpl_24162124;
import vn.edu.hcmute.exam02.service.impl.UserServiceImpl_24162124;

public class RegisterController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService_24162124 userService = new UserServiceImpl_24162124();
    private final OtpService_24162124 otpService = new OtpServiceImpl_24162124();
    private final MailService_24162124 mailService = new MailServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession(false) != null
                && request.getSession(false).getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String fullname = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            PendingRegistration_24162124 pendingRegistration =
                    userService.prepareRegistration(
                            email, fullname, phone, password, confirmPassword);
            OtpChallenge_24162124 otpChallenge = otpService.createChallenge();
            mailService.sendRegistrationOtp(
                    pendingRegistration.getEmail(), otpChallenge.getCode());

            HttpSession session = request.getSession(true);
            session.setAttribute("pendingRegistration", pendingRegistration);
            session.setAttribute("registrationOtp", otpChallenge);
            session.setMaxInactiveInterval(30 * 60);
            response.sendRedirect(request.getContextPath() + "/verify-otp");
            return;
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        } catch (SQLException exception) {
            request.setAttribute("error", "Không thể kiểm tra tài khoản trên SQL Server.");
            getServletContext().log("Đăng ký thất bại do lỗi database", exception);
        } catch (MessagingException | IllegalStateException exception) {
            request.setAttribute("error", "Không gửi được OTP. Vui lòng kiểm tra cấu hình SMTP.");
            getServletContext().log("Không gửi được email OTP", exception);
        }

        request.setAttribute("email", email);
        request.setAttribute("fullname", fullname);
        request.setAttribute("phone", phone);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp")
                .forward(request, response);
    }
}
