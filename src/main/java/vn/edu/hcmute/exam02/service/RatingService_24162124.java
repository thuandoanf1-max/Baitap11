package vn.edu.hcmute.exam02.service;

import java.sql.SQLException;
import java.util.List;

import vn.edu.hcmute.exam02.model.Rating_24162124;

public interface RatingService_24162124 {
    List<Rating_24162124> getReviewsByBookId(int bookId) throws SQLException;

    void addReview(int userId, int bookId, short rating, String reviewText)
            throws SQLException;
}
