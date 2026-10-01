<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang quản trị</title>
</head>
<body>
<div class="page-heading">
    <div>
        <span class="eyebrow">ADMIN ONLY</span>
        <h1>Trang quản trị</h1>
        <p>Chào mừng <strong><c:out value="${sessionScope.currentUser.fullname}"/></strong>.</p>
    </div>
    <span class="role-badge">ADMIN</span>
</div>

<c:if test="${not empty databaseError}">
    <div class="alert alert-error"><c:out value="${databaseError}"/></div>
</c:if>

<section class="admin-grid">
    <article class="admin-card">
        <span>Tổng số sách</span>
        <strong><c:out value="${empty totalBooks ? '—' : totalBooks}"/></strong>
    </article>
    <article class="admin-card">
        <span>Vai trò hiện tại</span>
        <strong>ADMIN</strong>
    </article>
    <article class="admin-card">
        <span>MSSV</span>
        <strong>24162124</strong>
    </article>
</section>

<div class="admin-note">
    <h2>Kiểm tra phân quyền thành công</h2>
    <p>Trang này được bảo vệ bởi <code>AdminAuthorizationFilter_24162124</code>. User thường không thể truy cập trực tiếp.</p>
    <a class="button" href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a>
</div>
</body>
</html>
