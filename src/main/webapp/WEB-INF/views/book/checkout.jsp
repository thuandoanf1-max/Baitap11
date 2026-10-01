<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Thanh toán đơn hàng</title>
</head>
<body>
<div class="container mb-5">

    <!-- ═══════════════ TRANG THÀNH CÔNG ═══════════════ -->
    <c:if test="${param.success eq '1'}">
        <c:set var="successOrderId" value="${sessionScope.orderSuccessId}"/>
        <c:remove var="orderSuccessId" scope="session"/>
        
        <div class="row justify-content-center mt-5">
            <div class="col-md-8 col-lg-6">
                <div class="card border-0 shadow-lg text-center p-5 rounded-4 overflow-hidden position-relative">
                    <div class="position-absolute top-0 start-0 w-100 bg-success" style="height: 10px;"></div>
                    
                    <div class="mb-4 mt-2">
                        <div class="bg-success text-white rounded-circle d-inline-flex align-items-center justify-content-center shadow" style="width: 100px; height: 100px;">
                            <i class="bi bi-check-lg" style="font-size: 4rem;"></i>
                        </div>
                    </div>
                    
                    <h1 class="fw-bold text-dark mb-3">Đặt Hàng Thành Công!</h1>
                    <p class="text-muted fs-5 mb-4">Tuyệt vời! Đơn hàng của bạn đã được hệ thống ghi nhận và đang được xử lý.</p>
                    
                    <c:if test="${not empty successOrderId}">
                        <div class="bg-light p-4 rounded-4 mb-4 border border-2 border-dashed">
                            <span class="fs-5 text-muted d-block mb-1">Mã tra cứu đơn hàng</span>
                            <span class="fs-2 fw-bold text-primary">#<c:out value="${successOrderId}"/></span>
                        </div>
                    </c:if>
                    
                    <div class="d-inline-flex align-items-center bg-white border rounded-pill px-4 py-2 shadow-sm mb-5">
                        <i class="bi bi-cash-coin text-success fs-4 me-2"></i>
                        <span class="text-dark fw-semibold">Phương thức: Thanh toán khi nhận hàng (COD)</span>
                    </div>
                    
                    <div class="d-flex flex-column flex-sm-row justify-content-center gap-3">
                        <a href="${pageContext.request.contextPath}/orders" class="btn btn-outline-primary btn-lg rounded-pill px-4">
                            <i class="bi bi-list-check"></i> Xem lịch sử đơn
                        </a>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg rounded-pill px-5 shadow">
                            <i class="bi bi-bag-heart"></i> Mua sắm tiếp
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- ═══════════════ FORM THANH TOÁN ═══════════════ -->
    <c:if test="${param.success ne '1'}">
        <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb bg-white p-3 rounded shadow-sm">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/cart" class="text-decoration-none"><i class="bi bi-arrow-left"></i> Quay lại Giỏ hàng</a></li>
                <li class="breadcrumb-item active fw-bold text-danger" aria-current="page">Thanh toán</li>
            </ol>
        </nav>
        
        <h2 class="fw-bold mb-4 text-dark"><i class="bi bi-credit-card-2-front text-danger"></i> Thanh toán đơn hàng</h2>
        
        <c:if test="${not empty checkoutError}">
            <div class="alert alert-danger alert-dismissible fade show shadow-sm border-0 border-start border-danger border-5">
                <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i> <span class="fs-6"><c:out value="${checkoutError}"/></span>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="row g-4">
            <!-- Cột trái: Form thông tin -->
            <div class="col-lg-7">
                <div class="card shadow border-0 rounded-4 mb-4">
                    <div class="card-header bg-white border-bottom py-3 px-4">
                        <h5 class="card-title mb-0 fw-bold text-dark"><i class="bi bi-geo-alt-fill text-danger me-2"></i> Thông tin giao hàng</h5>
                    </div>
                    <div class="card-body p-4 p-md-5">
                        <form method="post" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">
                            <div class="row g-4">
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <input type="text" class="form-control bg-light" id="shippingName" name="shippingName" required value="<c:out value="${not empty prefillName ? prefillName : ''}"/>" placeholder="Họ và tên">
                                        <label for="shippingName" class="text-muted"><i class="bi bi-person"></i> Họ và tên người nhận <span class="text-danger">*</span></label>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <input type="tel" class="form-control bg-light" id="shippingPhone" name="shippingPhone" required value="<c:out value="${not empty prefillPhone ? prefillPhone : ''}"/>" placeholder="Số điện thoại">
                                        <label for="shippingPhone" class="text-muted"><i class="bi bi-telephone"></i> Số điện thoại <span class="text-danger">*</span></label>
                                    </div>
                                </div>
                                <div class="col-12">
                                    <div class="form-floating">
                                        <input type="text" class="form-control bg-light" id="shippingAddress" name="shippingAddress" required value="<c:out value="${not empty prefillAddress ? prefillAddress : ''}"/>" placeholder="Địa chỉ giao hàng">
                                        <label for="shippingAddress" class="text-muted"><i class="bi bi-house"></i> Địa chỉ giao hàng cụ thể <span class="text-danger">*</span></label>
                                    </div>
                                </div>
                                <div class="col-12">
                                    <div class="form-floating">
                                        <textarea class="form-control bg-light" id="note" name="note" style="height: 100px" placeholder="Ghi chú"><c:out value="${not empty prefillNote ? prefillNote : ''}"/></textarea>
                                        <label for="note" class="text-muted"><i class="bi bi-chat-text"></i> Ghi chú cho shipper (tùy chọn)</label>
                                    </div>
                                </div>
                            </div>
                        </form>
                        
                        <div class="mt-5">
                            <h5 class="fw-bold mb-3 text-dark">Phương thức thanh toán</h5>
                            <label class="w-100">
                                <div class="border border-2 border-primary rounded-4 p-3 bg-primary bg-opacity-10 d-flex align-items-center position-relative cursor-pointer">
                                    <div class="form-check fs-5 me-3 mb-0">
                                        <input class="form-check-input" type="radio" checked disabled>
                                    </div>
                                    <div class="bg-white p-2 rounded shadow-sm me-3">
                                        <img src="https://cdn-icons-png.flaticon.com/512/2897/2897851.png" width="32" alt="COD">
                                    </div>
                                    <div>
                                        <h6 class="mb-0 fw-bold text-primary">Thanh toán khi nhận hàng (COD)</h6>
                                        <small class="text-muted">Kiểm tra hàng trước khi thanh toán tiền mặt</small>
                                    </div>
                                    <div class="position-absolute top-0 end-0 p-2">
                                        <span class="badge bg-primary">Mặc định</span>
                                    </div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Cột phải: Hóa đơn -->
            <div class="col-lg-5">
                <div class="card shadow border-0 rounded-4 sticky-top" style="top: 100px;">
                    <div class="card-header bg-dark text-white py-3 px-4 border-0 rounded-top-4">
                        <h5 class="card-title mb-0 fw-bold"><i class="bi bi-receipt-cutoff me-2"></i> Hóa đơn của bạn</h5>
                    </div>
                    <div class="card-body p-0">
                        <div class="p-4 bg-light">
                            <ul class="list-unstyled mb-0">
                                <c:set var="grandTotal" value="${0}"/>
                                <c:forEach items="${sessionScope.cart}" var="item">
                                    <c:set var="grandTotal" value="${grandTotal + item.subtotal}"/>
                                    <li class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom border-secondary border-opacity-25">
                                        <div class="d-flex align-items-center overflow-hidden me-3">
                                            <span class="badge bg-secondary rounded-pill me-3 fs-6">${item.quantity}</span>
                                            <span class="text-dark fw-semibold text-truncate"><c:out value="${item.bookName}"/></span>
                                        </div>
                                        <span class="fw-bold text-dark fs-6 text-nowrap">
                                            <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                        
                        <div class="p-4">
                            <div class="d-flex justify-content-between mb-2 text-muted">
                                <span>Tạm tính</span>
                                <span><fmt:formatNumber value="${grandTotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                            </div>
                            <div class="d-flex justify-content-between mb-3 text-muted">
                                <span>Phí vận chuyển</span>
                                <span class="text-success fw-semibold">Miễn phí (Freeship)</span>
                            </div>
                            
                            <hr class="border-secondary border-2 opacity-25 my-3">
                            
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <span class="fw-bold fs-5 text-dark">Thành tiền</span>
                                <span class="fw-bold text-danger" style="font-size: 2rem;">
                                    <fmt:formatNumber value="${grandTotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </span>
                            </div>
                            
                            <button type="submit" form="checkoutForm" class="btn btn-danger w-100 py-3 fs-5 fw-bold rounded-pill shadow-lg text-uppercase letter-spacing-1 btn-submit-order">
                                Xác nhận Đặt Hàng
                            </button>
                            <p class="text-center text-muted small mt-3 mb-0">
                                <i class="bi bi-shield-lock-fill text-success"></i> Thông tin của bạn được bảo mật an toàn.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<style>
    .form-floating > .form-control:focus ~ label { color: #dc3545; }
    .form-control:focus { border-color: #dc3545; box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25); }
    .border-dashed { border-style: dashed !important; }
    .btn-submit-order { transition: transform 0.2s; }
    .btn-submit-order:hover { transform: scale(1.02); }
    .letter-spacing-1 { letter-spacing: 1px; }
</style>
</body>
</html>
