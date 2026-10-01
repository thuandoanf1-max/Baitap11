package vn.edu.hcmute.exam02.dao;

import java.sql.SQLException;
import java.util.Optional;

import vn.edu.hcmute.exam02.model.User_24162124;

public interface UserDAO_24162124 {
    Optional<User_24162124> findByEmailAndPassword(String email, String password) throws SQLException;

    boolean existsByEmail(String email) throws SQLException;

    int insert(User_24162124 user) throws SQLException;

    void updateLastLogin(int userId) throws SQLException;
}
