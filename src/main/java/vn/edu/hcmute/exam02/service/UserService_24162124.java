package vn.edu.hcmute.exam02.service;

import java.sql.SQLException;
import java.util.Optional;

import vn.edu.hcmute.exam02.model.PendingRegistration_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;

public interface UserService_24162124 {
    Optional<User_24162124> login(String email, String password) throws SQLException;

    PendingRegistration_24162124 prepareRegistration(
            String email, String fullname, String phone,
            String password, String confirmPassword) throws SQLException;

    User_24162124 completeRegistration(PendingRegistration_24162124 pendingRegistration)
            throws SQLException;
}
