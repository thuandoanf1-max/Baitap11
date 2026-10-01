<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Edit Book</title>
</head>
<body>
<section class="admin-form-card">
    <div class="admin-form-heading">
        <span class="eyebrow">UPDATE • CÂU 6</span>
        <h1>Edit Book #${book.bookId}</h1>
        <p>Cập nhật thông tin sách và lưu thay đổi về SQL Server.</p>
    </div>

    <c:if test="${not empty formError}">
        <div class="alert alert-error"><c:out value="${formError}"/></div>
    </c:if>

    <form class="admin-book-form" method="post"
          action="${pageContext.request.contextPath}/admin/books">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="${book.bookId}">
        <input type="hidden" name="page" value="${returnPage}">
        <%@ include file="form-fields.jspf" %>

        <div class="form-actions">
            <a class="button button-secondary" href="${pageContext.request.contextPath}/admin/books?page=${returnPage}">Hủy</a>
            <button class="button" type="submit">Lưu thay đổi</button>
        </div>
    </form>
</section>
</body>
</html>
