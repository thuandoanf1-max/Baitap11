package vn.edu.hcmute.exam02.dao;

import java.sql.SQLException;
import java.util.List;

import vn.edu.hcmute.exam02.model.Rating_24162124;

public interface RatingDAO_24162124 {
    List<Rating_24162124> findByBookId(int bookId) throws SQLException;

    boolean existsByUserIdAndBookId(int userId, int bookId) throws SQLException;

    int insert(Rating_24162124 rating) throws SQLException;
}
