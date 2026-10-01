package vn.edu.hcmute.exam02.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DatabaseConnectionTest_24162124 {
    private static final String EXPECTED_DATABASE = "BookStore_24162124";

    private DatabaseConnectionTest_24162124() {
    }

    public static void main(String[] args) {
        String databaseName = null;

        try (Connection connection = DBConnection_24162124.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DB_NAME() AS connected_database");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                databaseName = resultSet.getString("connected_database");
            }

            if (!EXPECTED_DATABASE.equals(databaseName)) {
                throw new SQLException(
                        "Ket noi sai database. Expected: " + EXPECTED_DATABASE
                                + ", actual: " + databaseName);
            }

            System.out.println("Connected database: " + databaseName);
        } catch (SQLException | ExceptionInInitializerError exception) {
            System.err.println("Database connection failed: " + safeMessage(exception));
            System.exit(1);
        }
    }

    private static String safeMessage(Throwable throwable) {
        Throwable cause = throwable.getCause();
        String message = cause == null ? throwable.getMessage() : cause.getMessage();
        return message == null ? throwable.getClass().getSimpleName() : message;
    }
}
