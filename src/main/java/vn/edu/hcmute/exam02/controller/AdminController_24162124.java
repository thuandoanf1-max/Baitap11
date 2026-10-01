package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.edu.hcmute.exam02.service.BookService_24162124;
import vn.edu.hcmute.exam02.service.impl.BookServiceImpl_24162124;

public class AdminController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookService_24162124 bookService = new BookServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("totalBooks", bookService.countBooks());
        } catch (SQLException exception) {
            request.setAttribute("databaseError", "Khong doc duoc thong ke tu SQL Server.");
            getServletContext().log("Khong the tai thong ke admin", exception);
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
