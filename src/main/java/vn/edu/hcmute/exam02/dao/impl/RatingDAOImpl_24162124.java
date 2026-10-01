package vn.edu.hcmute.exam02.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.exam02.config.DBConnection_24162124;
import vn.edu.hcmute.exam02.dao.RatingDAO_24162124;
import vn.edu.hcmute.exam02.model.Rating_24162124;

public class RatingDAOImpl_24162124 implements RatingDAO_24162124 {
    private static final String FIND_BY_BOOK_ID_SQL =
            "SELECT r.userid, r.bookid, r.rating, r.review_text, "
                    + "COALESCE(NULLIF(LTRIM(RTRIM(u.fullname)), ''), u.email) AS reviewer_name "
                    + "FROM rating r "
                    + "INNER JOIN users u ON u.id = r.userid "
                    + "WHERE r.bookid = ? "
                    + "ORDER BY r.userid";

    private static final String EXISTS_SQL =
            "SELECT 1 FROM rating WHERE userid = ? AND bookid = ?";

    private static final String INSERT_SQL =
            "INSERT INTO rating (userid, bookid, rating, review_text) VALUES (?, ?, ?, ?)";

    @Override
    public List<Rating_24162124> findByBookId(int bookId) throws SQLException {
        List<Rating_24162124> reviews = new ArrayList<>();
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_BOOK_ID_SQL)) {
            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reviews.add(mapReview(resultSet));
                }
            }
        }
        return reviews;
    }

    @Override
    public boolean existsByUserIdAndBookId(int userId, int bookId) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_SQL)) {
            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int insert(Rating_24162124 rating) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setInt(1, rating.getUserId());
            statement.setInt(2, rating.getBookId());
            statement.setShort(3, rating.getRating());
            statement.setString(4, rating.getReviewText());
            return statement.executeUpdate();
        }
    }

    private Rating_24162124 mapReview(ResultSet resultSet) throws SQLException {
        Rating_24162124 review = new Rating_24162124();
        review.setUserId(resultSet.getInt("userid"));
        review.setBookId(resultSet.getInt("bookid"));
        review.setRating(resultSet.getShort("rating"));
        review.setReviewText(resultSet.getString("review_text"));
        review.setReviewerName(resultSet.getString("reviewer_name"));
        return review;
    }
}
