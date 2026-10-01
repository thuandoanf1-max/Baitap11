package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.edu.hcmute.exam02.service.BookService_24162124;
import vn.edu.hcmute.exam02.service.impl.BookServiceImpl_24162124;

public class HomeController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 3;
    private final BookService_24162124 bookService = new BookServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int currentPage = parsePage(request.getParameter("page"));
            int totalPages = bookService.getTotalPages(PAGE_SIZE);
            currentPage = Math.min(currentPage, totalPages);

            request.setAttribute(
                    "booksByAuthor",
                    bookService.getBooksByAuthor(currentPage, PAGE_SIZE));
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", PAGE_SIZE);
        } catch (SQLException exception) {
            request.setAttribute("booksByAuthor", Collections.emptyMap());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute(
                    "databaseError",
                    "Không tải được sách từ SQL Server. Hãy kiểm tra database.properties.");
            getServletContext().log("Khong the tai sach tren trang chu", exception);
        }
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }

    private int parsePage(String pageParameter) {
        if (pageParameter == null || pageParameter.trim().isEmpty()) {
            return 1;
        }
        try {
            return Math.max(1, Integer.parseInt(pageParameter));
        } catch (NumberFormatException exception) {
            return 1;
        }
    }
}
