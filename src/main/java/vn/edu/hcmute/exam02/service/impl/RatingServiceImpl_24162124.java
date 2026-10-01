package vn.edu.hcmute.exam02.service.impl;

import java.sql.SQLException;
import java.util.List;

import vn.edu.hcmute.exam02.dao.RatingDAO_24162124;
import vn.edu.hcmute.exam02.dao.impl.RatingDAOImpl_24162124;
import vn.edu.hcmute.exam02.model.Rating_24162124;
import vn.edu.hcmute.exam02.service.RatingService_24162124;

public class RatingServiceImpl_24162124 implements RatingService_24162124 {
    private static final int MAX_REVIEW_LENGTH = 2000;
    private final RatingDAO_24162124 ratingDAO;

    public RatingServiceImpl_24162124() {
        this(new RatingDAOImpl_24162124());
    }

    public RatingServiceImpl_24162124(RatingDAO_24162124 ratingDAO) {
        this.ratingDAO = ratingDAO;
    }

    @Override
    public List<Rating_24162124> getReviewsByBookId(int bookId) throws SQLException {
        if (bookId < 1) {
            throw new IllegalArgumentException("Book ID không hợp lệ.");
        }
        return ratingDAO.findByBookId(bookId);
    }

    @Override
    public void addReview(int userId, int bookId, short rating, String reviewText)
            throws SQLException {
        if (userId < 1 || bookId < 1) {
            throw new IllegalArgumentException("Thông tin người dùng hoặc sách không hợp lệ.");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Điểm đánh giá phải từ 1 đến 5.");
        }

        String normalizedReview = reviewText == null ? "" : reviewText.trim();
        if (normalizedReview.isEmpty()) {
            throw new IllegalArgumentException("Nội dung review không được để trống.");
        }
        if (normalizedReview.length() > MAX_REVIEW_LENGTH) {
            throw new IllegalArgumentException("Nội dung review không được vượt quá 2000 ký tự.");
        }
        if (ratingDAO.existsByUserIdAndBookId(userId, bookId)) {
            throw new IllegalStateException("Bạn đã review cuốn sách này.");
        }

        Rating_24162124 review = new Rating_24162124();
        review.setUserId(userId);
        review.setBookId(bookId);
        review.setRating(rating);
        review.setReviewText(normalizedReview);

        try {
            if (ratingDAO.insert(review) != 1) {
                throw new SQLException("Không thể thêm review vào database.");
            }
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 2601 || exception.getErrorCode() == 2627) {
                throw new IllegalStateException("Bạn đã review cuốn sách này.");
            }
            throw exception;
        }
    }
}
