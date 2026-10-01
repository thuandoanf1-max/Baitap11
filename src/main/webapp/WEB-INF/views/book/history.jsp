<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Lịch sử đơn hàng</title>
</head>
<body>
<div class="container mb-5">
    
    <!-- Header -->
    <div class="row align-items-center mb-4">
        <div class="col-md-6">
            <h2 class="fw-bold mb-0 text-dark">
                <i class="bi bi-clock-history text-primary me-2"></i> Lịch sử Đơn Hàng
            </h2>
            <p class="text-muted mb-0 mt-1">Quản lý và theo dõi các đơn hàng của <strong><c:out value="${sessionScope.currentUser.fullname}"/></strong></p>
        </div>
        <div class="col-md-6 text-md-end mt-3 mt-md-0">
            <c:if test="${not empty orders}">
                <span class="badge bg-dark text-white fs-6 px-4 py-2 rounded-pill shadow-sm">
                    Tổng cộng: ${orders.size()} đơn hàng
                </span>
            </c:if>
        </div>
    </div>

    <!-- Filter Tabs (Ngang có scroll nếu màn nhỏ) -->
    <div class="card border-0 shadow-sm rounded-4 mb-4 overflow-hidden">
        <div class="card-body p-1 bg-white">
            <c:url var="baseUrl" value="/orders"/>
            <div class="d-flex overflow-auto hide-scrollbar p-2" style="white-space: nowrap; -webkit-overflow-scrolling: touch;">
                <!-- Tất cả -->
                <a href="${baseUrl}" class="btn rounded-pill px-4 me-2 ${empty selectedStatus ? 'btn-dark fw-bold shadow' : 'btn-light border text-muted'}">
                    Tất cả
                </a>
                <!-- Lặp các trạng thái -->
                <c:forEach var="tab" items="${['đơn hàng mới','đã xác nhận','chuẩn bị hàng','vận chuyển','giao hàng','đã giao','đơn hàng hủy','đơn hàng hoàn']}">
                    <a href="${baseUrl}?status=${tab}" class="btn rounded-pill px-4 me-2 ${selectedStatus eq tab ? 'btn-primary fw-bold shadow' : 'btn-light border text-muted'}">
                        <c:out value="${tab}"/>
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>

    <c:if test="${not empty historyError}">
        <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-octagon-fill"></i> <c:out value="${historyError}"/></div>
    </c:if>

    <!-- Content -->
    <c:choose>
        <c:when test="${empty orders and empty historyError}">
            <!-- Empty state -->
            <div class="text-center py-5 bg-white rounded-4 shadow-sm border border-light mt-2">
                <img src="https://cdn-icons-png.flaticon.com/512/7486/7486747.png" width="150" alt="No orders" class="opacity-75 mb-4">
                <h3 class="fw-bold text-dark">Không tìm thấy đơn hàng</h3>
                <p class="text-muted fs-5 mb-4">
                    <c:choose>
                        <c:when test="${not empty selectedStatus}">
                            Bạn không có đơn hàng nào ở trạng thái "<strong class="text-dark"><c:out value="${selectedStatus}"/></strong>".
                        </c:when>
                        <c:otherwise>Bạn chưa từng mua cuốn sách nào tại cửa hàng.</c:otherwise>
                    </c:choose>
                </p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary btn-lg rounded-pill px-5">
                    <i class="bi bi-shop"></i> Bắt đầu mua sắm ngay
                </a>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row g-4">
                <c:forEach items="${orders}" var="order">
                    
                    <!-- Xác định màu badge theo status -->
                    <c:set var="badgeClass" value="bg-secondary"/>
                    <c:set var="iconClass" value="bi-info-circle"/>
                    <c:choose>
                        <c:when test="${order.status eq 'đơn hàng mới'}"><c:set var="badgeClass" value="bg-primary"/><c:set var="iconClass" value="bi-stars"/></c:when>
                        <c:when test="${order.status eq 'đã xác nhận'}"><c:set var="badgeClass" value="bg-info text-dark"/><c:set var="iconClass" value="bi-check-all"/></c:when>
                        <c:when test="${order.status eq 'chuẩn bị hàng'}"><c:set var="badgeClass" value="bg-warning text-dark"/><c:set var="iconClass" value="bi-box-seam"/></c:when>
                        <c:when test="${order.status eq 'vận chuyển'}"><c:set var="badgeClass" value="bg-warning text-dark"/><c:set var="iconClass" value="bi-truck"/></c:when>
                        <c:when test="${order.status eq 'giao hàng'}"><c:set var="badgeClass" value="bg-info text-dark"/><c:set var="iconClass" value="bi-bicycle"/></c:when>
                        <c:when test="${order.status eq 'đã giao'}"><c:set var="badgeClass" value="bg-success"/><c:set var="iconClass" value="bi-bag-check-fill"/></c:when>
                        <c:when test="${order.status eq 'đơn hàng hủy'}"><c:set var="badgeClass" value="bg-danger"/><c:set var="iconClass" value="bi-x-circle-fill"/></c:when>
                        <c:when test="${order.status eq 'đơn hàng hoàn'}"><c:set var="badgeClass" value="bg-secondary"/><c:set var="iconClass" value="bi-arrow-return-left"/></c:when>
                    </c:choose>

                    <div class="col-12">
                        <div class="card shadow-sm border-0 rounded-4 order-card hover-lift">
                            
                            <!-- Card Header: Thông tin chung đơn hàng -->
                            <div class="card-header bg-white border-bottom py-3 px-4 d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2">
                                <div>
                                    <h5 class="mb-1 fw-bold text-dark">
                                        Đơn hàng <span class="text-primary">#<c:out value="${order.id}"/></span>
                                    </h5>
                                    <div class="text-muted small">
                                        <i class="bi bi-calendar-event"></i> Ngày đặt: 
                                        <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" type="both" timeZone="Asia/Ho_Chi_Minh"/>
                                    </div>
                                </div>
                                <div class="text-md-end d-flex flex-row flex-md-column justify-content-between align-items-center align-items-md-end">
                                    <span class="badge rounded-pill ${badgeClass} fs-6 px-3 py-2 shadow-sm mb-0 mb-md-2">
                                        <i class="bi ${iconClass} me-1"></i> <c:out value="${order.status}"/>
                                    </span>
                                    <div class="fs-5 fw-bold text-danger ms-3 ms-md-0">
                                        <fmt:formatNumber value="${order.totalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </div>
                                </div>
                            </div>

                            <!-- Card Body: Sản phẩm -->
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-borderless table-hover align-middle mb-0 m-0">
                                        <thead class="bg-light text-muted small">
                                            <tr>
                                                <th class="ps-4">Sản phẩm</th>
                                                <th class="text-center" style="width: 100px;">SL</th>
                                                <th class="text-end pe-4" style="width: 150px;">Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${empty order.items}">
                                                    <tr><td colspan="3" class="text-center py-4 text-muted">Lỗi: Đơn hàng không có chi tiết sản phẩm.</td></tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach items="${order.items}" var="item">
                                                        <tr class="border-bottom border-light">
                                                            <td class="ps-4 py-3">
                                                                <div class="d-flex align-items-center">
                                                                    <div class="bg-secondary text-white rounded d-flex align-items-center justify-content-center me-3 shadow-sm" style="width: 40px; height: 50px;">
                                                                        <i class="bi bi-book"></i>
                                                                    </div>
                                                                    <div class="fw-semibold text-dark">
                                                                        <c:out value="${item.bookName}" default="Sách #${item.bookId}"/>
                                                                        <div class="text-muted fw-normal small d-block d-md-none">
                                                                            Đơn giá: <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </td>
                                                            <td class="text-center py-3">
                                                                <span class="badge bg-light text-dark border px-3 py-2 rounded-pill fs-6">x${item.quantity}</span>
                                                            </td>
                                                            <td class="text-end pe-4 py-3 fw-bold text-dark">
                                                                <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <!-- Card Footer: Giao hàng -->
                            <div class="card-footer bg-light border-top-0 py-3 px-4 rounded-bottom-4">
                                <div class="row align-items-center">
                                    <div class="col-md-9 text-muted small">
                                        <div class="mb-1"><i class="bi bi-geo-alt-fill text-danger"></i> <strong>Giao tới:</strong> <span class="text-dark"><c:out value="${order.shippingName}"/> - <c:out value="${order.shippingPhone}"/></span></div>
                                        <div class="ms-3 ps-1"><c:out value="${order.shippingAddress}"/></div>
                                        <c:if test="${not empty order.note}">
                                            <div class="mt-2 text-warning-emphasis bg-warning bg-opacity-10 border border-warning rounded p-2 d-inline-block">
                                                <i class="bi bi-chat-left-text"></i> Ghi chú: <c:out value="${order.note}"/>
                                            </div>
                                        </c:if>
                                    </div>
                                    <div class="col-md-3 text-md-end mt-3 mt-md-0">
                                        <a href="#" class="btn btn-sm btn-outline-primary rounded-pill px-3 disabled">Chi tiết <i class="bi bi-chevron-right"></i></a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<style>
    /* Ẩn thanh cuộn của thẻ div nằm ngang */
    .hide-scrollbar::-webkit-scrollbar { display: none; }
    .hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
    
    .hover-lift { transition: transform 0.2s, box-shadow 0.2s; }
    .hover-lift:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(0,0,0,0.08) !important; z-index: 10; }
</style>
</body>
</html>
