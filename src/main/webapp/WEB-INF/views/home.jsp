<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang chủ BookStore</title>
</head>
<body>

<!-- Hero Banner -->
<div class="p-5 mb-5 rounded-4 shadow" style="background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); color: white;">
    <div class="row align-items-center">
        <div class="col-lg-8">
            <span class="badge bg-warning text-dark mb-3 px-3 py-2 fs-6 rounded-pill">ĐỀ THI 02 • CÂU 3</span>
            <h1 class="display-4 fw-bold">Khám Phá Sách Theo Tác Giả</h1>
            <p class="lead mt-3 opacity-75">Hệ thống phân loại thông minh giúp bạn dễ dàng tìm kiếm những tác phẩm kinh điển từ các tác giả yêu thích.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-warning btn-lg rounded-pill px-4 mt-3 shadow-sm fw-bold">
                <i class="bi bi-cart-fill"></i> Mua sắm ngay
            </a>
        </div>
        <div class="col-lg-4 d-none d-lg-block text-center">
            <i class="bi bi-book text-white opacity-25" style="font-size: 12rem;"></i>
        </div>
    </div>
</div>

<div class="container">
    <c:if test="${not empty databaseError}">
        <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-triangle-fill"></i> <c:out value="${databaseError}"/></div>
    </c:if>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold text-dark mb-0"><i class="bi bi-collection-fill text-primary"></i> Tác phẩm nổi bật</h3>
        <span class="text-muted"><i class="bi bi-file-earmark-text"></i> Trang ${currentPage}/${totalPages}</span>
    </div>

    <!-- Group theo tác giả -->
    <c:forEach items="${booksByAuthor}" var="authorGroup">
        <div class="card border-0 shadow-sm rounded-4 mb-5 overflow-hidden">
            <div class="card-header bg-dark text-white py-3 px-4">
                <h4 class="mb-0 fw-bold"><i class="bi bi-pen"></i> Tác giả: <span class="text-warning"><c:out value="${authorGroup.key}"/></span></h4>
            </div>
            <div class="card-body p-4 bg-light">
                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    
                    <c:forEach items="${authorGroup.value}" var="book">
                        <c:url var="coverUrl" value="/assets/images/${book.coverImage}"/>
                        <c:url var="fallbackUrl" value="/assets/images/book-placeholder.svg"/>
                        
                        <div class="col">
                            <div class="card h-100 border-0 shadow-sm author-book-card">
                                <div class="row g-0 h-100">
                                    <div class="col-4 bg-white text-center position-relative border-end">
                                        <c:choose>
                                            <c:when test="${not empty book.coverImage}">
                                                <img src="${coverUrl}" class="img-fluid rounded-start h-100 w-100" style="object-fit: cover;" alt="Bìa" onerror="this.onerror=null;this.src='${fallbackUrl}';">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="d-flex h-100 align-items-center justify-content-center text-muted p-2">
                                                    <i class="bi bi-image fs-1"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="col-8">
                                        <div class="card-body p-3 d-flex flex-column h-100">
                                            <h6 class="card-title fw-bold text-dark text-truncate mb-1" title="<c:out value="${book.title}"/>">
                                                <a href="${pageContext.request.contextPath}/book/detail?id=${book.bookId}" class="text-decoration-none text-dark">
                                                    <c:out value="${book.title}"/>
                                                </a>
                                            </h6>
                                            <div class="text-muted small mb-2"><c:out value="${book.publisher}" default="NXB Chưa Rõ"/></div>
                                            
                                            <ul class="list-unstyled small mb-3">
                                                <li><i class="bi bi-upc-scan"></i> ISBN: <c:out value="${book.isbn}" default="---"/></li>
                                                <li><i class="bi bi-archive"></i> Kho: 
                                                    <c:choose>
                                                        <c:when test="${book.quantity > 0}"><span class="text-success fw-bold">${book.quantity} cuốn</span></c:when>
                                                        <c:otherwise><span class="badge bg-danger">Hết hàng</span></c:otherwise>
                                                    </c:choose>
                                                </li>
                                            </ul>
                                            
                                            <div class="mt-auto d-flex justify-content-between align-items-center">
                                                <span class="fw-bold text-danger">
                                                    <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                                <a href="${pageContext.request.contextPath}/book/detail?id=${book.bookId}" class="btn btn-sm btn-outline-primary rounded-circle" title="Xem chi tiết">
                                                    <i class="bi bi-arrow-right"></i>
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    
                </div>
            </div>
        </div>
    </c:forEach>

    <c:if test="${empty booksByAuthor and empty databaseError}">
        <div class="text-center py-5 bg-white rounded-4 shadow-sm border border-light">
            <i class="bi bi-journals text-muted" style="font-size: 4rem;"></i>
            <h4 class="mt-3 text-dark fw-bold">Chưa có sách trong hệ thống</h4>
            <p class="text-muted">Vui lòng thêm sách vào CSDL để hiển thị.</p>
        </div>
    </c:if>

    <!-- Pagination -->
    <c:if test="${empty databaseError and totalPages > 1}">
        <nav aria-label="Phân trang" class="mt-4 mb-5">
            <ul class="pagination justify-content-center shadow-sm">
                <!-- Nút Trang trước -->
                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                    <a class="page-link px-3" href="${pageContext.request.contextPath}/home?page=${currentPage - 1}" tabindex="-1">
                        <i class="bi bi-chevron-left"></i>
                    </a>
                </li>
                
                <!-- Số trang -->
                <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                    <li class="page-item ${pageNumber == currentPage ? 'active' : ''}">
                        <a class="page-link fw-bold px-3" href="${pageContext.request.contextPath}/home?page=${pageNumber}">${pageNumber}</a>
                    </li>
                </c:forEach>
                
                <!-- Nút Trang sau -->
                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                    <a class="page-link px-3" href="${pageContext.request.contextPath}/home?page=${currentPage + 1}">
                        <i class="bi bi-chevron-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<style>
    .author-book-card { transition: transform 0.2s, box-shadow 0.2s; cursor: pointer; }
    .author-book-card:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important; }
</style>
</body>
</html>
