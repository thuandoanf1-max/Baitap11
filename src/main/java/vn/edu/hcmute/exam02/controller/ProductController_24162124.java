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

public class ProductController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookService_24162124 bookService = new BookServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("books", bookService.getAllBooks());
        } catch (SQLException exception) {
            request.setAttribute("books", Collections.emptyList());
            request.setAttribute("databaseError", "Khong tai duoc danh sach sach tu SQL Server.");
            getServletContext().log("Khong the tai danh sach sach", exception);
        }
        request.getRequestDispatcher("/WEB-INF/views/book/list.jsp").forward(request, response);
    }
}
