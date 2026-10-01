<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Quản lý Books</title>
</head>
<body>
<div class="page-heading admin-books-heading">
    <div>
        <span class="eyebrow">ADMIN • CÂU 6</span>
        <h1>Quản lý Books</h1>
        <p>CRUD bảng <code>books</code> bằng Servlet, Service, DAO và JDBC.</p>
    </div>
    <a class="button" href="${pageContext.request.contextPath}/admin/books?action=add&page=${currentPage}">+ Add Book</a>
</div>

<c:if test="${param.status eq 'created'}">
    <div class="alert alert-success">Thêm Book ID ${param.id} thành công.</div>
</c:if>
<c:if test="${param.status eq 'updated'}">
    <div class="alert alert-success">Cập nhật Book thành công.</div>
</c:if>
<c:if test="${param.status eq 'deleted'}">
    <div class="alert alert-success">Xóa Book và dữ liệu liên quan thành công.</div>
</c:if>
<c:if test="${param.status eq 'notFound'}">
    <div class="alert alert-warning">Không tìm thấy Book được yêu cầu.</div>
</c:if>
<c:if test="${param.status eq 'deleteError'}">
    <div class="alert alert-error">Không thể xóa Book. Database đã rollback để bảo toàn dữ liệu.</div>
</c:if>
<c:if test="${param.status eq 'databaseError' or not empty databaseError}">
    <div class="alert alert-error"><c:out value="${empty databaseError ? 'Không thể truy cập SQL Server.' : databaseError}"/></div>
</c:if>

<div class="admin-list-summary">
    <span>Tổng số: <strong>${totalBooks}</strong> sách</span>
    <span>Trang <strong>${currentPage}/${totalPages}</strong></span>
</div>

<div class="admin-table-wrap">
    <table class="admin-book-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>ISBN</th>
            <th>Tiêu đề</th>
            <th>Publisher</th>
            <th>Giá</th>
            <th>Publish date</th>
            <th>Quantity</th>
            <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${books}" var="book">
            <tr>
                <td><strong>${book.bookId}</strong></td>
                <td><c:out value="${book.isbn}"/></td>
                <td class="admin-book-title"><c:out value="${book.title}"/></td>
                <td><c:out value="${book.publisher}"/></td>
                <td><fmt:formatNumber value="${book.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td><c:out value="${book.publishDate}"/></td>
                <td>${book.quantity}</td>
                <td>
                    <div class="admin-actions">
                        <a class="action-link edit-link"
                           href="${pageContext.request.contextPath}/admin/books?action=edit&id=${book.bookId}&page=${currentPage}">Edit</a>
                        <form method="post" action="${pageContext.request.contextPath}/admin/books"
                              onsubmit="return confirm('Bạn chắc chắn muốn xóa sách này? Review và quan hệ tác giả cũng sẽ bị xóa.');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${book.bookId}">
                            <input type="hidden" name="page" value="${currentPage}">
                            <button class="action-link delete-link" type="submit">Delete</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${empty books and empty databaseError}">
    <div class="empty-state">Chưa có Book trong database.</div>
</c:if>

<c:if test="${empty databaseError}">
    <nav class="pagination" aria-label="Phân trang quản lý sách">
        <c:choose>
            <c:when test="${currentPage > 1}">
                <a class="page-link page-nav" href="${pageContext.request.contextPath}/admin/books?page=${currentPage - 1}">Previous</a>
            </c:when>
            <c:otherwise>
                <span class="page-link page-nav disabled" aria-disabled="true">Previous</span>
            </c:otherwise>
        </c:choose>

        <c:forEach begin="1" end="${totalPages}" var="pageNumber">
            <c:choose>
                <c:when test="${pageNumber == currentPage}">
                    <span class="page-link active" aria-current="page">${pageNumber}</span>
                </c:when>
                <c:otherwise>
                    <a class="page-link" href="${pageContext.request.contextPath}/admin/books?page=${pageNumber}">${pageNumber}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:choose>
            <c:when test="${currentPage < totalPages}">
                <a class="page-link page-nav" href="${pageContext.request.contextPath}/admin/books?page=${currentPage + 1}">Next</a>
            </c:when>
            <c:otherwise>
                <span class="page-link page-nav disabled" aria-disabled="true">Next</span>
            </c:otherwise>
        </c:choose>
    </nav>
</c:if>
</body>
</html>
