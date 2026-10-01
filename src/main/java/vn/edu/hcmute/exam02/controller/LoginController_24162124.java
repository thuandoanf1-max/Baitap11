package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.hcmute.exam02.model.User_24162124;
import vn.edu.hcmute.exam02.service.UserService_24162124;
import vn.edu.hcmute.exam02.service.impl.UserServiceImpl_24162124;

public class LoginController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService_24162124 userService = new UserServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Optional<User_24162124> authenticatedUser = userService.login(email, password);
            if (authenticatedUser.isPresent()) {
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession(true);
                session.setAttribute("currentUser", authenticatedUser.get());
                session.setMaxInactiveInterval(30 * 60);
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            request.setAttribute("error", "Email hoac mat khau khong dung.");
        } catch (SQLException exception) {
            request.setAttribute("error", "Khong the ket noi SQL Server.");
            getServletContext().log("Dang nhap that bai do loi database", exception);
        }

        request.setAttribute("email", email);
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }
}
