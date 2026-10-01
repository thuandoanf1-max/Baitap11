package vn.edu.hcmute.exam02.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import vn.edu.hcmute.exam02.config.DBConnection_24162124;
import vn.edu.hcmute.exam02.dao.BookDAO_24162124;
import vn.edu.hcmute.exam02.model.Book_24162124;

public class BookDAOImpl_24162124 implements BookDAO_24162124 {
    private static final String BOOK_SELECT_SQL =
            "SELECT b.bookid, b.isbn, b.title, b.publisher, b.price, b.description, "
                    + "b.publish_date, b.cover_image, b.quantity, "
                    + "author_data.author_names, rating_data.average_rating, "
                    + "COALESCE(rating_data.review_count, 0) AS review_count "
                    + "FROM books b "
                    + "LEFT JOIN ("
                    + "    SELECT ba.bookid, STRING_AGG(a.author_name, ', ') AS author_names, "
                    + "           MIN(a.author_name) AS sort_author "
                    + "    FROM book_author ba "
                    + "    INNER JOIN author a ON a.author_id = ba.author_id "
                    + "    GROUP BY ba.bookid"
                    + ") author_data ON author_data.bookid = b.bookid "
                    + "LEFT JOIN ("
                    + "    SELECT r.bookid, AVG(CAST(r.rating AS DECIMAL(4,2))) AS average_rating, "
                    + "           COUNT(*) AS review_count "
                    + "    FROM rating r "
                    + "    GROUP BY r.bookid"
                    + ") rating_data ON rating_data.bookid = b.bookid ";

    private static final String FIND_ALL_SQL = BOOK_SELECT_SQL + "ORDER BY b.bookid";

    private static final String FIND_PAGE_SQL = BOOK_SELECT_SQL
            + "ORDER BY COALESCE(author_data.sort_author, ''), b.title, b.bookid "
            + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    private static final String FIND_ADMIN_PAGE_SQL = BOOK_SELECT_SQL
            + "ORDER BY b.bookid DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    private static final String FIND_BY_ID_SQL = BOOK_SELECT_SQL + "WHERE b.bookid = ?";

    private static final String COUNT_ALL_SQL = "SELECT COUNT(*) FROM books";

    private static final String INSERT_SQL =
            "INSERT INTO books (isbn, title, publisher, price, description, "
                    + "publish_date, cover_image, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE books SET isbn = ?, title = ?, publisher = ?, price = ?, "
                    + "description = ?, publish_date = ?, cover_image = ?, quantity = ? "
                    + "WHERE bookid = ?";

    private static final String DELETE_RATINGS_SQL = "DELETE FROM rating WHERE bookid = ?";
    private static final String DELETE_BOOK_AUTHORS_SQL =
            "DELETE FROM book_author WHERE bookid = ?";
    private static final String DELETE_BOOK_SQL = "DELETE FROM books WHERE bookid = ?";

    @Override
    public List<Book_24162124> findAll() throws SQLException {
        List<Book_24162124> books = new ArrayList<>();
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                books.add(mapBook(resultSet));
            }
        }
        return books;
    }

    @Override
    public List<Book_24162124> findPage(int offset, int pageSize) throws SQLException {
        List<Book_24162124> books = new ArrayList<>();
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_PAGE_SQL)) {
            statement.setInt(1, offset);
            statement.setInt(2, pageSize);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(mapBook(resultSet));
                }
            }
        }
        return books;
    }

    @Override
    public List<Book_24162124> findAdminPage(int offset, int pageSize) throws SQLException {
        List<Book_24162124> books = new ArrayList<>();
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ADMIN_PAGE_SQL)) {
            statement.setInt(1, offset);
            statement.setInt(2, pageSize);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(mapBook(resultSet));
                }
            }
        }
        return books;
    }

    @Override
    public Optional<Book_24162124> findById(int bookId) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapBook(resultSet))
                        : Optional.empty();
            }
        }
    }

    @Override
    public int insert(Book_24162124 book) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setBookParameters(statement, book);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Khong the them sach vao database.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new SQLException("Khong lay duoc bookid sau khi them sach.");
        }
    }

    @Override
    public boolean update(Book_24162124 book) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            setBookParameters(statement, book);
            statement.setInt(9, book.getBookId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(int bookId) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection()) {
            connection.setAutoCommit(false);
            try {
                executeDeleteByBookId(connection, DELETE_RATINGS_SQL, bookId);
                executeDeleteByBookId(connection, DELETE_BOOK_AUTHORS_SQL, bookId);
                int deletedBooks = executeDeleteByBookId(connection, DELETE_BOOK_SQL, bookId);
                connection.commit();
                return deletedBooks == 1;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    @Override
    public int countAll() throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    private void setBookParameters(PreparedStatement statement, Book_24162124 book)
            throws SQLException {
        statement.setObject(1, book.getIsbn(), java.sql.Types.INTEGER);
        statement.setString(2, book.getTitle());
        statement.setString(3, book.getPublisher());
        statement.setBigDecimal(4, book.getPrice());
        statement.setString(5, book.getDescription());
        statement.setObject(6, book.getPublishDate(), java.sql.Types.DATE);
        statement.setString(7, book.getCoverImage());
        statement.setObject(8, book.getQuantity(), java.sql.Types.INTEGER);
    }

    private int executeDeleteByBookId(Connection connection, String sql, int bookId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            return statement.executeUpdate();
        }
    }

    private Book_24162124 mapBook(ResultSet resultSet) throws SQLException {
        Book_24162124 book = new Book_24162124();
        book.setBookId(resultSet.getInt("bookid"));
        book.setIsbn((Integer) resultSet.getObject("isbn"));
        book.setTitle(resultSet.getString("title"));
        book.setPublisher(resultSet.getString("publisher"));
        book.setPrice(resultSet.getBigDecimal("price"));
        book.setDescription(resultSet.getString("description"));
        if (resultSet.getDate("publish_date") != null) {
            book.setPublishDate(resultSet.getDate("publish_date").toLocalDate());
        }
        book.setCoverImage(resultSet.getString("cover_image"));
        book.setQuantity((Integer) resultSet.getObject("quantity"));
        book.setAuthorNames(resultSet.getString("author_names"));
        Object averageRating = resultSet.getObject("average_rating");
        book.setAverageRating(averageRating == null ? null : resultSet.getDouble("average_rating"));
        book.setReviewCount(resultSet.getInt("review_count"));
        return book;
    }
}
