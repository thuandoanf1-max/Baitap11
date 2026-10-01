package vn.edu.hcmute.exam02.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import vn.edu.hcmute.exam02.config.DBConnection_24162124;
import vn.edu.hcmute.exam02.dao.UserDAO_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;

public class UserDAOImpl_24162124 implements UserDAO_24162124 {
    private static final String LOGIN_SQL =
            "SELECT id, email, fullname, phone, passwd, signup_date, last_login, is_admin "
                    + "FROM users WHERE email = ? AND passwd = ?";
    private static final String UPDATE_LAST_LOGIN_SQL =
            "UPDATE users SET last_login = ? WHERE id = ?";
    private static final String EXISTS_BY_EMAIL_SQL =
            "SELECT COUNT(*) FROM users WHERE email = ?";
    private static final String INSERT_SQL =
            "INSERT INTO users (email, fullname, phone, passwd, signup_date, last_login, is_admin) "
                    + "VALUES (?, ?, ?, ?, ?, NULL, 0)";

    @Override
    public Optional<User_24162124> findByEmailAndPassword(String email, String password)
            throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(LOGIN_SQL)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_BY_EMAIL_SQL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public int insert(User_24162124 user) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFullname());
            statement.setObject(3, user.getPhone());
            statement.setString(4, user.getPasswd());
            statement.setTimestamp(5, Timestamp.valueOf(user.getSignupDate()));

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Khong the tao tai khoan nguoi dung");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new SQLException("Khong doc duoc id tai khoan vua tao");
        }
    }

    @Override
    public void updateLastLogin(int userId) throws SQLException {
        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_LAST_LOGIN_SQL)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private User_24162124 mapUser(ResultSet resultSet) throws SQLException {
        User_24162124 user = new User_24162124();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setFullname(resultSet.getString("fullname"));
        user.setPhone((Integer) resultSet.getObject("phone"));
        user.setPasswd(resultSet.getString("passwd"));
        Timestamp signupDate = resultSet.getTimestamp("signup_date");
        Timestamp lastLogin = resultSet.getTimestamp("last_login");
        user.setSignupDate(signupDate == null ? null : signupDate.toLocalDateTime());
        user.setLastLogin(lastLogin == null ? null : lastLogin.toLocalDateTime());
        user.setAdmin(resultSet.getBoolean("is_admin"));
        return user;
    }
}
