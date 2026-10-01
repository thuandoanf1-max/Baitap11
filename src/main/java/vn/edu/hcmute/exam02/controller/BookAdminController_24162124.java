package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.edu.hcmute.exam02.model.Book_24162124;
import vn.edu.hcmute.exam02.service.BookService_24162124;
import vn.edu.hcmute.exam02.service.impl.BookServiceImpl_24162124;

public class BookAdminController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 5;
    private static final String LIST_VIEW = "/WEB-INF/views/admin/book/list.jsp";
    private static final String ADD_VIEW = "/WEB-INF/views/admin/book/add.jsp";
    private static final String EDIT_VIEW = "/WEB-INF/views/admin/book/edit.jsp";

    private final BookService_24162124 bookService = new BookServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isBlank() || "list".equals(action)) {
            showList(request, response);
            return;
        }
        if ("add".equals(action)) {
            request.setAttribute("book", new Book_24162124());
            request.setAttribute("returnPage", parsePage(request.getParameter("page")));
            request.getRequestDispatcher(ADD_VIEW).forward(request, response);
            return;
        }
        if ("edit".equals(action)) {
            showEditForm(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            createBook(request, response);
        } else if ("update".equals(action)) {
            updateBook(request, response);
        } else if ("delete".equals(action)) {
            deleteBook(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ.");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int currentPage = parsePage(request.getParameter("page"));
            int totalPages = bookService.getTotalPages(PAGE_SIZE);
            currentPage = Math.min(currentPage, totalPages);

            request.setAttribute(
                    "books", bookService.getBooksForAdminPage(currentPage, PAGE_SIZE));
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalBooks", bookService.countBooks());
        } catch (SQLException exception) {
            request.setAttribute("databaseError", "Không tải được danh sách sách từ SQL Server.");
            getServletContext().log("Khong the tai danh sach sach admin", exception);
        }
        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int returnPage = parsePage(request.getParameter("page"));
        Integer bookId;
        try {
            bookId = parsePositiveInteger(request.getParameter("id"), "Book ID");
        } catch (IllegalArgumentException exception) {
            redirectToList(request, response, returnPage, "notFound");
            return;
        }
        try {
            Optional<Book_24162124> book = bookService.getBookById(bookId);
            if (book.isEmpty()) {
                redirectToList(request, response, returnPage, "notFound");
                return;
            }
            request.setAttribute("book", book.get());
            request.setAttribute("returnPage", returnPage);
            request.getRequestDispatcher(EDIT_VIEW).forward(request, response);
        } catch (SQLException exception) {
            getServletContext().log("Khong the tai form cap nhat sach", exception);
            redirectToList(request, response, returnPage, "databaseError");
        }
    }

    private void createBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Book_24162124 book = new Book_24162124();
        request.setAttribute("returnPage", parsePage(request.getParameter("page")));
        try {
            populateBook(request, book, false);
            int generatedId = bookService.createBook(book);
            redirectToList(request, response, 1, "created", generatedId);
        } catch (IllegalArgumentException exception) {
            forwardFormWithError(request, response, book, ADD_VIEW, exception.getMessage());
        } catch (SQLException exception) {
            getServletContext().log("Khong the them sach", exception);
            forwardFormWithError(
                    request, response, book, ADD_VIEW,
                    "Không thể thêm sách vào SQL Server.");
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Book_24162124 book = new Book_24162124();
        int returnPage = parsePage(request.getParameter("page"));
        try {
            populateBook(request, book, true);
            if (!bookService.updateBook(book)) {
                redirectToList(request, response, returnPage, "notFound");
                return;
            }
            redirectToList(request, response, returnPage, "updated");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("returnPage", returnPage);
            forwardFormWithError(request, response, book, EDIT_VIEW, exception.getMessage());
        } catch (SQLException exception) {
            getServletContext().log("Khong the cap nhat sach", exception);
            request.setAttribute("returnPage", returnPage);
            forwardFormWithError(
                    request, response, book, EDIT_VIEW,
                    "Không thể cập nhật sách trong SQL Server.");
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int returnPage = parsePage(request.getParameter("page"));
        try {
            int bookId = parsePositiveInteger(request.getParameter("id"), "Book ID");
            String status = bookService.deleteBook(bookId) ? "deleted" : "notFound";
            redirectToList(request, response, returnPage, status);
        } catch (IllegalArgumentException exception) {
            redirectToList(request, response, returnPage, "notFound");
        } catch (SQLException exception) {
            getServletContext().log("Khong the xoa sach", exception);
            redirectToList(request, response, returnPage, "deleteError");
        }
    }

    private void populateBook(HttpServletRequest request, Book_24162124 book,
                              boolean requireId) {
        if (requireId) {
            book.setBookId(parsePositiveInteger(request.getParameter("id"), "Book ID"));
        }
        book.setIsbn(parsePositiveInteger(request.getParameter("isbn"), "ISBN"));
        book.setTitle(request.getParameter("title"));
        book.setPublisher(request.getParameter("publisher"));
        book.setPrice(parsePrice(request.getParameter("price")));
        book.setDescription(request.getParameter("description"));
        book.setPublishDate(parseDate(request.getParameter("publishDate")));
        book.setCoverImage(request.getParameter("coverImage"));
        book.setQuantity(parseNonNegativeInteger(request.getParameter("quantity"), "Quantity"));
    }

    private Integer parsePositiveInteger(String value, String fieldName) {
        try {
            int parsedValue = Integer.parseInt(value);
            if (parsedValue < 1) {
                throw new IllegalArgumentException(fieldName + " phải là số nguyên dương.");
            }
            return parsedValue;
        } catch (NumberFormatException | NullPointerException exception) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên dương.");
        }
    }

    private Integer parseNonNegativeInteger(String value, String fieldName) {
        try {
            int parsedValue = Integer.parseInt(value);
            if (parsedValue < 0) {
                throw new IllegalArgumentException(fieldName + " phải là số nguyên không âm.");
            }
            return parsedValue;
        } catch (NumberFormatException | NullPointerException exception) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên không âm.");
        }
    }

    private BigDecimal parsePrice(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException | NullPointerException exception) {
            throw new IllegalArgumentException("Giá sách không hợp lệ.");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new IllegalArgumentException("Ngày xuất bản không hợp lệ.");
        }
    }

    private int parsePage(String value) {
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException | NullPointerException exception) {
            return 1;
        }
    }

    private void forwardFormWithError(HttpServletRequest request, HttpServletResponse response,
                                      Book_24162124 book, String view, String error)
            throws ServletException, IOException {
        request.setAttribute("book", book);
        request.setAttribute("formError", error);
        request.getRequestDispatcher(view).forward(request, response);
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response,
                                int page, String status) throws IOException {
        redirectToList(request, response, page, status, null);
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response,
                                int page, String status, Integer bookId) throws IOException {
        String location = request.getContextPath()
                + "/admin/books?page=" + page + "&status=" + status;
        if (bookId != null) {
            location += "&id=" + bookId;
        }
        response.sendRedirect(location);
    }
}
