<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Add Book</title>
</head>
<body>
<section class="admin-form-card">
    <div class="admin-form-heading">
        <span class="eyebrow">CREATE • CÂU 6</span>
        <h1>Thêm Book</h1>
        <p>Nhập đầy đủ thông tin để thêm sách mới vào SQL Server.</p>
    </div>

    <c:if test="${not empty formError}">
        <div class="alert alert-error"><c:out value="${formError}"/></div>
    </c:if>

    <form class="admin-book-form" method="post"
          action="${pageContext.request.contextPath}/admin/books">
        <input type="hidden" name="action" value="create">
        <input type="hidden" name="page" value="${returnPage}">
        <%@ include file="form-fields.jspf" %>

        <div class="form-actions">
            <a class="button button-secondary" href="${pageContext.request.contextPath}/admin/books?page=${returnPage}">Hủy</a>
            <button class="button" type="submit">Add Book</button>
        </div>
    </form>
</section>
</body>
</html>
