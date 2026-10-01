<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng ký</title>
</head>
<body>
<section class="auth-card">
    <div class="auth-heading">
        <span class="eyebrow">TẠO TÀI KHOẢN</span>
        <h1>Đăng ký thành viên</h1>
        <p>Nhập thông tin của bạn. Hệ thống sẽ gửi mã OTP 6 số tới email để xác nhận.</p>
    </div>

    <form class="auth-form" method="post" action="${pageContext.request.contextPath}/register">
        <c:if test="${param.missing eq '1'}">
            <div class="alert alert-warning">Phiên xác nhận không tồn tại hoặc đã kết thúc. Vui lòng đăng ký lại.</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error"><c:out value="${error}"/></div>
        </c:if>

        <div class="form-grid">
            <div class="field field-full">
                <label for="fullname">Họ và tên</label>
                <input id="fullname" name="fullname" type="text" maxlength="50" required
                       autocomplete="name" value="<c:out value='${fullname}'/>">
            </div>
            <div class="field">
                <label for="email">Email</label>
                <input id="email" name="email" type="email" maxlength="50" required
                       autocomplete="email" value="<c:out value='${email}'/>">
            </div>
            <div class="field">
                <label for="phone">Số điện thoại</label>
                <input id="phone" name="phone" type="tel" maxlength="10"
                       pattern="[0-9]{9,10}" autocomplete="tel"
                       value="<c:out value='${phone}'/>">
            </div>
            <div class="field">
                <label for="password">Mật khẩu</label>
                <input id="password" name="password" type="password"
                       minlength="6" maxlength="32" required autocomplete="new-password">
            </div>
            <div class="field">
                <label for="confirmPassword">Xác nhận mật khẩu</label>
                <input id="confirmPassword" name="confirmPassword" type="password"
                       minlength="6" maxlength="32" required autocomplete="new-password">
            </div>
        </div>

        <button class="button button-full" type="submit">Gửi mã OTP</button>
        <p class="form-switch">Đã có tài khoản?
            <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        </p>
    </form>
</section>
</body>
</html>
