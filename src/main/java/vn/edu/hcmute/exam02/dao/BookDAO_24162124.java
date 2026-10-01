package vn.edu.hcmute.exam02.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import vn.edu.hcmute.exam02.model.Book_24162124;

public interface BookDAO_24162124 {
    List<Book_24162124> findAll() throws SQLException;

    List<Book_24162124> findPage(int offset, int pageSize) throws SQLException;

    List<Book_24162124> findAdminPage(int offset, int pageSize) throws SQLException;

    Optional<Book_24162124> findById(int bookId) throws SQLException;

    int insert(Book_24162124 book) throws SQLException;

    boolean update(Book_24162124 book) throws SQLException;

    boolean deleteById(int bookId) throws SQLException;

    int countAll() throws SQLException;
}
