package vn.edu.hcmute.exam02.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import vn.edu.hcmute.exam02.dao.BookDAO_24162124;
import vn.edu.hcmute.exam02.dao.impl.BookDAOImpl_24162124;
import vn.edu.hcmute.exam02.model.Book_24162124;
import vn.edu.hcmute.exam02.service.BookService_24162124;

public class BookServiceImpl_24162124 implements BookService_24162124 {
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999.99");
    private final BookDAO_24162124 bookDAO;

    public BookServiceImpl_24162124() {
        this(new BookDAOImpl_24162124());
    }

    public BookServiceImpl_24162124(BookDAO_24162124 bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Override
    public List<Book_24162124> getAllBooks() throws SQLException {
        return bookDAO.findAll();
    }

    @Override
    public Map<String, List<Book_24162124>> getBooksByAuthor(int page, int pageSize)
            throws SQLException {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page va pageSize phai lon hon 0");
        }

        int offset = (page - 1) * pageSize;
        List<Book_24162124> books = bookDAO.findPage(offset, pageSize);
        Map<String, List<Book_24162124>> booksByAuthor = new LinkedHashMap<>();
        for (Book_24162124 book : books) {
            String authorNames = book.getAuthorNames();
            String groupName = authorNames == null || authorNames.trim().isEmpty()
                    ? "Chưa xác định"
                    : authorNames.trim();
            booksByAuthor.computeIfAbsent(groupName, key -> new ArrayList<>())
                    .add(book);
        }
        return booksByAuthor;
    }

    @Override
    public List<Book_24162124> getBooksForAdminPage(int page, int pageSize)
            throws SQLException {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page và pageSize phải lớn hơn 0.");
        }
        return bookDAO.findAdminPage((page - 1) * pageSize, pageSize);
    }

    @Override
    public Optional<Book_24162124> getBookById(int bookId) throws SQLException {
        if (bookId < 1) {
            return Optional.empty();
        }
        return bookDAO.findById(bookId);
    }

    @Override
    public int createBook(Book_24162124 book) throws SQLException {
        validateBook(book, false);
        return bookDAO.insert(book);
    }

    @Override
    public boolean updateBook(Book_24162124 book) throws SQLException {
        validateBook(book, true);
        return bookDAO.update(book);
    }

    @Override
    public boolean deleteBook(int bookId) throws SQLException {
        if (bookId < 1) {
            throw new IllegalArgumentException("Book ID không hợp lệ.");
        }
        return bookDAO.deleteById(bookId);
    }

    @Override
    public int countBooks() throws SQLException {
        return bookDAO.countAll();
    }

    @Override
    public int getTotalPages(int pageSize) throws SQLException {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize phai lon hon 0");
        }
        int totalBooks = bookDAO.countAll();
        return Math.max(1, (totalBooks + pageSize - 1) / pageSize);
    }

    private void validateBook(Book_24162124 book, boolean requireId) {
        if (book == null) {
            throw new IllegalArgumentException("Thông tin sách không được để trống.");
        }
        if (requireId && book.getBookId() < 1) {
            throw new IllegalArgumentException("Book ID không hợp lệ.");
        }
        if (book.getIsbn() == null || book.getIsbn() < 1) {
            throw new IllegalArgumentException("ISBN phải là số nguyên dương.");
        }

        book.setTitle(normalizeRequired(book.getTitle(), "Tiêu đề", 200));
        book.setPublisher(normalizeRequired(book.getPublisher(), "Publisher", 100));

        if (book.getPrice() == null
                || book.getPrice().compareTo(BigDecimal.ZERO) < 0
                || book.getPrice().compareTo(MAX_PRICE) > 0
                || book.getPrice().scale() > 2) {
            throw new IllegalArgumentException(
                    "Giá phải từ 0 đến 9999.99 và có tối đa 2 chữ số thập phân.");
        }
        if (book.getPublishDate() == null) {
            throw new IllegalArgumentException("Ngày xuất bản không được để trống.");
        }
        if (book.getQuantity() == null || book.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity phải là số nguyên không âm.");
        }

        book.setDescription(normalizeOptional(book.getDescription(), "Mô tả", 5000));
        book.setCoverImage(normalizeOptional(book.getCoverImage(), "Cover image", 100));
    }

    private String normalizeRequired(String value, String fieldName, int maxLength) {
        String normalizedValue = value == null ? "" : value.trim();
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        if (normalizedValue.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " không được vượt quá " + maxLength + " ký tự.");
        }
        return normalizedValue;
    }

    private String normalizeOptional(String value, String fieldName, int maxLength) {
        String normalizedValue = value == null ? "" : value.trim();
        if (normalizedValue.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " không được vượt quá " + maxLength + " ký tự.");
        }
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }
}
