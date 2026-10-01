package vn.edu.hcmute.exam02.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import vn.edu.hcmute.exam02.dao.UserDAO_24162124;
import vn.edu.hcmute.exam02.model.OtpChallenge_24162124;
import vn.edu.hcmute.exam02.model.PendingRegistration_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;
import vn.edu.hcmute.exam02.service.impl.OtpServiceImpl_24162124;
import vn.edu.hcmute.exam02.service.impl.UserServiceImpl_24162124;

public final class Question2LogicTest_24162124 {
    private Question2LogicTest_24162124() {
    }

    public static void main(String[] args) throws SQLException {
        FakeUserDAO_24162124 fakeUserDAO = new FakeUserDAO_24162124();
        UserService_24162124 userService = new UserServiceImpl_24162124(fakeUserDAO);
        OtpService_24162124 otpService = new OtpServiceImpl_24162124();

        PendingRegistration_24162124 pending = userService.prepareRegistration(
                "new.user@example.com", "New User", "0901234567", "secret1", "secret1");
        User_24162124 registeredUser = userService.completeRegistration(pending);
        assertTrue(registeredUser.getId() > 0, "Register hợp lệ");

        assertThrows(() -> userService.prepareRegistration(
                "invalid-email", "Invalid User", "0901234567", "secret1", "secret1"),
                "Email không hợp lệ");

        OtpChallenge_24162124 wrongChallenge = otpService.createChallenge();
        assertTrue(!otpService.verify(wrongChallenge, "WRONG1").isSuccess(), "OTP sai");

        OtpChallenge_24162124 correctChallenge = otpService.createChallenge();
        assertTrue(otpService.verify(correctChallenge, correctChallenge.getCode()).isSuccess(),
                "OTP đúng");

        assertTrue(userService.login("user@example.com", "user123").isPresent(),
                "Login đúng");
        assertTrue(!userService.login("user@example.com", "wrong-password").isPresent(),
                "Login sai");
        assertTrue(!userService.login("user@example.com", "user123").get().isAdmin(),
                "User role");
        assertTrue(userService.login("admin@example.com", "admin123").get().isAdmin(),
                "Admin role");

        System.out.println("QUESTION_2_LOGIC_TESTS=PASS");
    }

    private static void assertTrue(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError(testName + " failed");
        }
        System.out.println("[PASS] " + testName);
    }

    private static void assertThrows(ThrowingAction_24162124 action, String testName) {
        try {
            action.run();
            throw new AssertionError(testName + " failed");
        } catch (IllegalArgumentException expected) {
            System.out.println("[PASS] " + testName);
        } catch (SQLException exception) {
            throw new AssertionError(testName + " failed", exception);
        }
    }

    @FunctionalInterface
    private interface ThrowingAction_24162124 {
        void run() throws SQLException;
    }

    private static final class FakeUserDAO_24162124 implements UserDAO_24162124 {
        private final Map<String, User_24162124> users = new HashMap<>();
        private int nextId = 3;

        private FakeUserDAO_24162124() {
            users.put("user@example.com", createUser(1, "user@example.com", "user123", false));
            users.put("admin@example.com", createUser(2, "admin@example.com", "admin123", true));
        }

        @Override
        public Optional<User_24162124> findByEmailAndPassword(String email, String password) {
            User_24162124 user = users.get(email);
            if (user != null && password.equals(user.getPasswd())) {
                return Optional.of(user);
            }
            return Optional.empty();
        }

        @Override
        public boolean existsByEmail(String email) {
            return users.containsKey(email);
        }

        @Override
        public int insert(User_24162124 user) {
            int generatedId = nextId++;
            user.setId(generatedId);
            users.put(user.getEmail(), user);
            return generatedId;
        }

        @Override
        public void updateLastLogin(int userId) {
            // No database update is needed in this isolated business-layer test.
        }

        private User_24162124 createUser(
                int id, String email, String password, boolean admin) {
            User_24162124 user = new User_24162124();
            user.setId(id);
            user.setEmail(email);
            user.setFullname(admin ? "Admin Test" : "User Test");
            user.setPasswd(password);
            user.setAdmin(admin);
            return user;
        }
    }
}
