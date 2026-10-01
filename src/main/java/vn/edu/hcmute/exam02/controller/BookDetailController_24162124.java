package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.hcmute.exam02.model.Book_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;
import vn.edu.hcmute.exam02.service.BookService_24162124;
import vn.edu.hcmute.exam02.service.RatingService_24162124;
import vn.edu.hcmute.exam02.service.impl.BookServiceImpl_24162124;
import vn.edu.hcmute.exam02.service.impl.RatingServiceImpl_24162124;

public class BookDetailController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DETAIL_VIEW = "/WEB-INF/views/book/detail.jsp";
    private static final String FLASH_SUCCESS = "reviewFlashSuccess";
    private static final String FLASH_ERROR = "reviewFlashError";

    private final BookService_24162124 bookService = new BookServiceImpl_24162124();
    private final RatingService_24162124 ratingService = new RatingServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer bookId = parsePositiveInteger(request.getParameter("id"));
        if (bookId == null) {
            forwardError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "Mã sách không hợp lệ.");
            return;
        }

        try {
            Optional<Book_24162124> book = bookService.getBookById(bookId);
            if (book.isEmpty()) {
                forwardError(request, response, HttpServletResponse.SC_NOT_FOUND,
                        "Không tìm thấy sách có mã " + bookId + ".");
                return;
            }

            request.setAttribute("book", book.get());
            request.setAttribute("reviews", ratingService.getReviewsByBookId(bookId));
            moveFlashMessagesToRequest(request);
            request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
        } catch (SQLException exception) {
            getServletContext().log("Khong the tai chi tiet sach", exception);
            forwardError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Không tải được chi tiết sách từ SQL Server.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer bookId = parsePositiveInteger(request.getParameter("bookId"));
        if (bookId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã sách không hợp lệ.");
            return;
        }

        HttpSession session = request.getSession(false);
        User_24162124 currentUser = session == null
                ? null
                : (User_24162124) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?required=review");
            return;
        }

        try {
            if (bookService.getBookById(bookId).isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy sách.");
                return;
            }

            short rating = parseRating(request.getParameter("rating"));
            ratingService.addReview(
                    currentUser.getId(), bookId, rating, request.getParameter("reviewText"));
            session.setAttribute(FLASH_SUCCESS, "Thêm review thành công.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            session.setAttribute(FLASH_ERROR, exception.getMessage());
        } catch (SQLException exception) {
            session.setAttribute(FLASH_ERROR, "Không thể lưu review vào SQL Server.");
            getServletContext().log("Khong the them review", exception);
        }

        response.sendRedirect(request.getContextPath() + "/book/detail?id=" + bookId);
    }

    private Integer parsePositiveInteger(String value) {
        try {
            int parsedValue = Integer.parseInt(value);
            return parsedValue > 0 ? parsedValue : null;
        } catch (NumberFormatException | NullPointerException exception) {
            return null;
        }
    }

    private short parseRating(String value) {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException | NullPointerException exception) {
            throw new IllegalArgumentException("Điểm đánh giá không hợp lệ.");
        }
    }

    private void moveFlashMessagesToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        moveFlashAttribute(session, request, FLASH_SUCCESS, "reviewSuccess");
        moveFlashAttribute(session, request, FLASH_ERROR, "reviewError");
    }

    private void moveFlashAttribute(HttpSession session, HttpServletRequest request,
                                    String sessionName, String requestName) {
        Object value = session.getAttribute(sessionName);
        if (value != null) {
            request.setAttribute(requestName, value);
            session.removeAttribute(sessionName);
        }
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              int status, String message)
            throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("detailError", message);
        request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
    }
}
