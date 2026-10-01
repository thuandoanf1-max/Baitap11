<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Giỏ hàng của tôi</title>
</head>
<body>
<div class="container mb-5">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-4 bg-white px-3 py-2 rounded shadow-sm">
        <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none"><i class="bi bi-house-door"></i> Trang chủ</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products" class="text-decoration-none">Cửa hàng</a></li>
            <li class="breadcrumb-item active fw-bold text-primary" aria-current="page">Giỏ hàng của tôi</li>
        </ol>
    </nav>

    <!-- Header -->
    <div class="d-flex align-items-center mb-4">
        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3 shadow" style="width: 50px; height: 50px;">
            <i class="bi bi-cart3 fs-3"></i>
        </div>
        <h2 class="fw-bold mb-0 text-dark">Giỏ hàng của <span class="text-primary"><c:out value="${sessionScope.currentUser.fullname}"/></span></h2>
    </div>

    <!-- Alerts -->
    <c:if test="${not empty cartSuccess}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm border-0 border-start border-success border-5" role="alert">
            <i class="bi bi-check-circle-fill me-2 fs-5 text-success"></i> <span class="fs-6"><c:out value="${cartSuccess}"/></span>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty cartError}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm border-0 border-start border-danger border-5" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2 fs-5 text-danger"></i> <span class="fs-6"><c:out value="${cartError}"/></span>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Content -->
    <c:choose>
        <c:when test="${empty sessionScope.cart}">
            <!-- Giỏ hàng trống -->
            <div class="text-center py-5 bg-white rounded-4 shadow-sm border border-light mt-2">
                <img src="https://cdn-icons-png.flaticon.com/512/11329/11329060.png" alt="Empty Cart" style="width: 150px; opacity: 0.7;" class="mb-4">
                <h3 class="fw-bold text-dark">Giỏ hàng của bạn đang trống</h3>
                <p class="text-muted fs-5 mb-4">Chưa có sản phẩm nào được chọn. Hãy tiếp tục mua sắm nhé!</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg px-5 rounded-pill shadow">
                    <i class="bi bi-shop me-2"></i> Khám phá sản phẩm
                </a>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row g-4">
                <!-- Cột trái: Danh sách item -->
                <div class="col-lg-8">
                    <div class="card shadow-sm border-0 rounded-4 overflow-hidden">
                        <div class="card-header bg-white border-bottom py-3 px-4 d-flex justify-content-between align-items-center">
                            <h5 class="mb-0 fw-bold text-dark">Sản phẩm đã chọn (${sessionScope.cart.size()})</h5>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light text-muted">
                                        <tr>
                                            <th class="ps-4 py-3 border-0">Sản phẩm</th>
                                            <th class="text-center py-3 border-0">Đơn giá</th>
                                            <th class="text-center py-3 border-0" style="width: 140px;">Số lượng</th>
                                            <th class="text-end py-3 border-0">Thành tiền</th>
                                            <th class="text-center pe-4 py-3 border-0">Xóa</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:set var="grandTotal" value="${0}"/>
                                        <c:forEach items="${sessionScope.cart}" var="item">
                                            <c:set var="grandTotal" value="${grandTotal + item.subtotal}"/>
                                            <c:url var="coverUrl" value="/assets/images/${item.coverImage}"/>
                                            
                                            <tr>
                                                <td class="ps-4 py-4">
                                                    <div class="d-flex align-items-center">
                                                        <div class="bg-light rounded p-1 border shadow-sm me-3" style="width: 70px; height: 95px; flex-shrink: 0;">
                                                            <img src="${coverUrl}" class="w-100 h-100 object-fit-cover rounded" alt="Bìa" onerror="this.onerror=null;this.src='https://placehold.co/60x85/e9ecef/6c757d?text=Book';">
                                                        </div>
                                                        <div>
                                                            <a href="${pageContext.request.contextPath}/book/detail?id=${item.bookId}" class="text-decoration-none text-dark fw-bold fs-6 mb-1 d-block cart-title-hover">
                                                                <c:out value="${item.bookName}"/>
                                                            </a>
                                                            <span class="badge ${item.stock <= 5 ? 'bg-warning text-dark' : 'bg-success bg-opacity-10 text-success border border-success'}">
                                                                <i class="bi ${item.stock <= 5 ? 'bi-exclamation-triangle' : 'bi-check2-circle'}"></i> Tồn kho: ${item.stock}
                                                            </span>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td class="text-center fw-semibold text-dark">
                                                    <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </td>
                                                <td class="text-center">
                                                    <form method="post" action="${pageContext.request.contextPath}/cart" class="m-0">
                                                        <input type="hidden" name="action" value="update">
                                                        <input type="hidden" name="bookId" value="${item.bookId}">
                                                        <div class="input-group input-group-sm shadow-sm rounded">
                                                            <input type="number" name="quantity" class="form-control text-center fw-bold text-primary" value="${item.quantity}" min="1" max="${item.stock}">
                                                            <button class="btn btn-primary px-2" type="submit" title="Cập nhật"><i class="bi bi-arrow-repeat fs-6"></i></button>
                                                        </div>
                                                    </form>
                                                </td>
                                                <td class="text-end fw-bold text-danger fs-6">
                                                    <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </td>
                                                <td class="text-center pe-4">
                                                    <form method="post" action="${pageContext.request.contextPath}/cart" class="m-0">
                                                        <input type="hidden" name="action" value="remove">
                                                        <input type="hidden" name="bookId" value="${item.bookId}">
                                                        <button type="submit" class="btn btn-outline-danger btn-sm rounded-circle shadow-sm" style="width: 32px; height: 32px; padding: 0;" onclick="return confirm('Bạn có chắc muốn xóa cuốn này khỏi giỏ?')" title="Xóa">
                                                            <i class="bi bi-trash3"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="card-footer bg-light border-top py-3 px-4">
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary rounded-pill px-4">
                                <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
                            </a>
                        </div>
                    </div>
                </div>
                
                <!-- Cột phải: Thanh toán -->
                <div class="col-lg-4">
                    <div class="card shadow-sm border-0 rounded-4 sticky-top" style="top: 100px;">
                        <div class="card-header bg-white border-bottom py-3 px-4">
                            <h5 class="card-title mb-0 fw-bold text-dark">Tóm tắt đơn hàng</h5>
                        </div>
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between mb-3 text-muted fs-6">
                                <span>Tổng số lượng:</span>
                                <span class="fw-semibold text-dark">${sessionScope.cart.size()} sản phẩm</span>
                            </div>
                            <div class="d-flex justify-content-between mb-3 text-muted fs-6">
                                <span>Phí vận chuyển:</span>
                                <span class="fw-semibold text-success">Miễn phí</span>
                            </div>
                            
                            <hr class="my-4 border-secondary opacity-25">
                            
                            <div class="d-flex justify-content-between align-items-end mb-4">
                                <span class="fw-bold fs-5 text-dark">Tổng tiền:</span>
                                <span class="fw-bold text-danger" style="font-size: 1.8rem; line-height: 1;">
                                    <fmt:formatNumber value="${grandTotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </span>
                            </div>
                            
                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-danger w-100 py-3 fs-5 fw-bold rounded-pill shadow btn-checkout">
                                TIẾN HÀNH THANH TOÁN
                            </a>
                            
                            <div class="mt-4 text-center">
                                <p class="small text-muted mb-2">Chấp nhận thanh toán:</p>
                                <div class="d-flex justify-content-center gap-2">
                                    <span class="badge bg-light text-dark border p-2"><i class="bi bi-cash-stack fs-5 text-success"></i> COD</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<style>
    .cart-title-hover:hover { color: #0d6efd !important; text-decoration: underline !important; }
    .btn-checkout { transition: all 0.3s ease; }
    .btn-checkout:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(220,53,69,0.3) !important; }
</style>
</body>
</html>
