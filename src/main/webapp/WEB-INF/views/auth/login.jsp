<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng nhập</title>
</head>
<body>
<section class="login-shell">
    <div class="login-intro">
        <span class="eyebrow">TÀI KHOẢN</span>
        <h1>Đăng nhập hệ thống</h1>
        <p>Phiên đăng nhập được quản lý bằng HttpSession. Quyền quản trị được kiểm tra ở Filter phía máy chủ.</p>
        <div class="demo-account">
            <strong>Tài khoản mẫu</strong>
            <span>Admin: admin@bookstore.vn / admin123</span>
            <span>User: user@bookstore.vn / user123</span>
        </div>
    </div>
    <form class="login-form" method="post" action="${pageContext.request.contextPath}/login">
        <h2>Thông tin đăng nhập</h2>
        <c:if test="${param.registered eq '1'}">
            <div class="alert alert-success">Đăng ký và xác nhận email thành công. Bạn có thể đăng nhập.</div>
        </c:if>
        <c:if test="${param.logout eq '1'}">
            <div class="alert alert-success">Bạn đã đăng xuất khỏi hệ thống.</div>
        </c:if>
        <c:if test="${param.required eq 'admin'}">
            <div class="alert alert-warning">Vui lòng đăng nhập bằng tài khoản Admin.</div>
        </c:if>
        <c:if test="${param.required eq 'review'}">
            <div class="alert alert-warning">Vui lòng đăng nhập để thêm review.</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error"><c:out value="${error}"/></div>
        </c:if>
        <label for="email">Email</label>
        <input id="email" name="email" type="email" maxlength="50" required
               value="<c:out value='${email}'/>">
        <label for="password">Mật khẩu</label>
        <input id="password" name="password" type="password" maxlength="32" required>
        <button class="button button-full" type="submit">Đăng nhập</button>
        <p class="form-switch">Chưa có tài khoản?
            <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
        </p>
    </form>
</section>
</body>
</html>
