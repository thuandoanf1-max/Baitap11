<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Xác nhận OTP</title>
</head>
<body>
<section class="otp-shell">
    <div class="otp-icon" aria-hidden="true">✉</div>
    <span class="eyebrow">XÁC NHẬN EMAIL</span>
    <h1>Nhập mã OTP</h1>
    <p>Mã OTP 6 số đã được gửi đến <strong><c:out value="${pendingEmail}"/></strong>.</p>
    <p class="otp-note">Mã có hiệu lực trong 5 phút và được nhập sai tối đa 5 lần.</p>

    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${registrationClosed}">
            <a class="button" href="${pageContext.request.contextPath}/register">Đăng ký lại</a>
        </c:when>
        <c:otherwise>
            <form class="otp-form" method="post" action="${pageContext.request.contextPath}/verify-otp">
                <label for="otp">Mã xác nhận</label>
                <input id="otp" name="otp" type="text" inputmode="numeric"
                       pattern="[0-9]{6}" minlength="6" maxlength="6"
                       placeholder="000000" autocomplete="one-time-code" required autofocus>
                <button class="button" type="submit">Xác nhận OTP</button>
            </form>
        </c:otherwise>
    </c:choose>
</section>
</body>
</html>
