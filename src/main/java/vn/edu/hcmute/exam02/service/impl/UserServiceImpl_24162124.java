package vn.edu.hcmute.exam02.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import vn.edu.hcmute.exam02.dao.UserDAO_24162124;
import vn.edu.hcmute.exam02.dao.impl.UserDAOImpl_24162124;
import vn.edu.hcmute.exam02.model.PendingRegistration_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;
import vn.edu.hcmute.exam02.service.UserService_24162124;

public class UserServiceImpl_24162124 implements UserService_24162124 {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final UserDAO_24162124 userDAO;

    public UserServiceImpl_24162124() {
        this(new UserDAOImpl_24162124());
    }

    public UserServiceImpl_24162124(UserDAO_24162124 userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User_24162124> login(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return Optional.empty();
        }

        Optional<User_24162124> user =
                userDAO.findByEmailAndPassword(email.trim(), password);
        if (user.isPresent()) {
            userDAO.updateLastLogin(user.get().getId());
        }
        return user;
    }

    @Override
    public PendingRegistration_24162124 prepareRegistration(
            String email, String fullname, String phone,
            String password, String confirmPassword) throws SQLException {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String normalizedName = fullname == null ? "" : fullname.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches() || normalizedEmail.length() > 50) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
        if (normalizedName.isEmpty() || normalizedName.length() > 50) {
            throw new IllegalArgumentException("Họ tên phải có từ 1 đến 50 ký tự.");
        }
        if (password == null || password.length() < 6 || password.length() > 32) {
            throw new IllegalArgumentException("Mật khẩu phải có từ 6 đến 32 ký tự.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }
        if (!normalizedPhone.isEmpty() && !normalizedPhone.matches("\\d{9,10}")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 9 hoặc 10 chữ số.");
        }

        Integer phoneNumber = null;
        if (!normalizedPhone.isEmpty()) {
            try {
                phoneNumber = Integer.valueOf(normalizedPhone);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Số điện thoại vượt quá giới hạn của đề bài.");
            }
        }

        if (userDAO.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email đã được đăng ký.");
        }

        return new PendingRegistration_24162124(
                normalizedEmail, normalizedName, phoneNumber, password);
    }

    @Override
    public User_24162124 completeRegistration(
            PendingRegistration_24162124 pendingRegistration) throws SQLException {
        if (pendingRegistration == null) {
            throw new IllegalArgumentException("Không có thông tin đăng ký chờ xác nhận.");
        }
        if (userDAO.existsByEmail(pendingRegistration.getEmail())) {
            throw new IllegalArgumentException("Email đã được đăng ký.");
        }

        User_24162124 user = new User_24162124();
        user.setEmail(pendingRegistration.getEmail());
        user.setFullname(pendingRegistration.getFullname());
        user.setPhone(pendingRegistration.getPhone());
        user.setPasswd(pendingRegistration.getPassword());
        user.setSignupDate(LocalDateTime.now());
        user.setAdmin(false);
        user.setId(userDAO.insert(user));
        user.setPasswd(null);
        return user;
    }
}
