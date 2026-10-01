package vn.edu.hcmute.exam02.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection_24162124 {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DBConnection_24162124.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("Khong tim thay database.properties");
            }
            PROPERTIES.load(input);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (IOException | ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private DBConnection_24162124() {
    }

    public static Connection getConnection() throws SQLException {
        String url = getRequiredProperty("db.url");
        String username = getRequiredProperty("db.username");
        String password = getRequiredProperty("db.password");
        if ("YOUR_SQL_SERVER_PASSWORD".equals(password)) {
            throw new SQLException(
                    "Chua cau hinh db.password trong database.properties");
        }
        return DriverManager.getConnection(url, username, password);
    }

    private static String getRequiredProperty(String propertyName) throws SQLException {
        String value = PROPERTIES.getProperty(propertyName);
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException(
                    "Thieu cau hinh " + propertyName + " trong database.properties");
        }
        return value.trim();
    }
}
