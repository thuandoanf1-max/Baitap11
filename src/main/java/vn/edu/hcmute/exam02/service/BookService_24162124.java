package vn.edu.hcmute.exam02.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import vn.edu.hcmute.exam02.model.Book_24162124;

public interface BookService_24162124 {
    List<Book_24162124> getAllBooks() throws SQLException;

    Map<String, List<Book_24162124>> getBooksByAuthor(int page, int pageSize)
            throws SQLException;

    List<Book_24162124> getBooksForAdminPage(int page, int pageSize)
            throws SQLException;

    Optional<Book_24162124> getBookById(int bookId) throws SQLException;

    int createBook(Book_24162124 book) throws SQLException;

    boolean updateBook(Book_24162124 book) throws SQLException;

    boolean deleteBook(int bookId) throws SQLException;

    int countBooks() throws SQLException;

    int getTotalPages(int pageSize) throws SQLException;
}
