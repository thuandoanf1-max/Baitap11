<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Danh sách Sản phẩm</title>
</head>
<body>
<div class="container">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom border-2 border-primary">
        <h2 class="fw-bold mb-0 text-dark"><i class="bi bi-book-half text-primary me-2"></i> Khám phá Sách</h2>
        <span class="badge bg-primary rounded-pill fs-6 px-3 py-2 shadow-sm">${books.size()} cuốn sách</span>
    </div>

    <c:if test="${not empty databaseError}">
        <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${databaseError}"/></div>
    </c:if>

    <!-- Book Grid -->
    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
        <c:forEach items="${books}" var="book">
            <c:url var="coverUrl" value="/assets/images/${book.coverImage}"/>
            <c:url var="fallbackUrl" value="/assets/images/book-placeholder.svg"/>
            <div class="col">
                <div class="card h-100 product-card">
                    <!-- Image Box -->
                    <div class="position-relative bg-light text-center" style="height: 260px; overflow: hidden;">
                        <c:choose>
                            <c:when test="${not empty book.coverImage}">
                                <img src="${coverUrl}" class="card-img-top w-100 h-100" style="object-fit: cover;" alt="Bìa sách" onerror="this.onerror=null;this.src='${fallbackUrl}';">
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex h-100 align-items-center justify-content-center text-muted">
                                    <i class="bi bi-image fs-1"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        
                        <!-- Stock Badge -->
                        <div class="position-absolute top-0 end-0 p-2">
                            <c:choose>
                                <c:when test="${book.quantity <= 0}">
                                    <span class="badge bg-danger shadow">Hết hàng</span>
                                </c:when>
                                <c:when test="${book.quantity <= 5}">
                                    <span class="badge bg-warning text-dark shadow">Sắp hết (${book.quantity})</span>
                                </c:when>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Info Box -->
                    <div class="card-body d-flex flex-column">
                        <p class="text-muted small mb-1 fw-semibold"><i class="bi bi-pen"></i> <c:out value="${book.authorNames}" default="Nhiều tác giả"/></p>
                        <h5 class="card-title fw-bold text-dark text-truncate" title="<c:out value="${book.title}"/>">
                            <a href="${pageContext.request.contextPath}/book/detail?id=${book.bookId}" class="text-decoration-none text-dark">
                                <c:out value="${book.title}"/>
                            </a>
                        </h5>
                        
                        <div class="mt-auto pt-3 d-flex justify-content-between align-items-center border-top mt-3">
                            <span class="fs-4 fw-bold text-danger">
                                <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </span>
                            <c:if test="${book.quantity > 0}">
                                <span class="text-success small fw-bold"><i class="bi bi-check-circle"></i> Sẵn hàng</span>
                            </c:if>
                        </div>
                    </div>

                    <!-- Action Box -->
                    <div class="card-footer bg-white border-0 pb-3 pt-0">
                        <c:choose>
                            <c:when test="${not empty sessionScope.currentUser and (empty book.quantity or book.quantity > 0)}">
                                <form method="post" action="${pageContext.request.contextPath}/cart">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="bookId" value="${book.bookId}">
                                    <input type="hidden" name="quantity" value="1">
                                    <button type="submit" class="btn btn-primary w-100 shadow-sm d-flex justify-content-center align-items-center gap-2">
                                        <i class="bi bi-cart-plus fs-5"></i> Thêm vào giỏ
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/book/detail?id=${book.bookId}" class="btn btn-outline-secondary w-100">
                                    <i class="bi bi-eye"></i> Xem chi tiết
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <!-- Empty state -->
    <c:if test="${empty books and empty databaseError}">
        <div class="text-center py-5 bg-white rounded-4 shadow-sm border border-light mt-4">
            <i class="bi bi-emoji-frown text-muted" style="font-size: 5rem;"></i>
            <h3 class="mt-4 text-dark fw-bold">Chưa có sản phẩm nào!</h3>
            <p class="text-muted fs-5">Hệ thống đang cập nhật sách mới, vui lòng quay lại sau.</p>
        </div>
    </c:if>
</div>

<style>
    .product-card {
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    .product-card:hover {
        transform: translateY(-8px);
        box-shadow: 0 15px 30px rgba(0,0,0,0.1) !important;
    }
    .product-card img {
        transition: transform 0.5s ease;
    }
    .product-card:hover img {
        transform: scale(1.05);
    }
</style>
</body>
</html>
