<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><sitemesh:write property="title"/> | BookStore 24162124</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <sitemesh:write property="head"/>
    <style>
        body { 
            background-color: #f4f6f9; 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
        }
        .navbar-brand { font-weight: 800; font-size: 1.5rem; letter-spacing: -0.5px; }
        .navbar-brand .highlight { color: #f39c12; }
        .navbar { box-shadow: 0 4px 12px rgba(0,0,0,0.08); padding-top: 15px; padding-bottom: 15px; }
        .nav-link { font-weight: 600; text-transform: uppercase; font-size: 0.9rem; letter-spacing: 0.5px; }
        .page-content { min-height: 80vh; padding: 40px 0 60px 0; }
        .footer { background-color: #2c3e50; color: #bdc3c7; padding: 30px 0; margin-top: auto; border-top: 5px solid #f39c12; }
        .btn { font-weight: 600; border-radius: 8px; padding: 10px 20px; transition: all 0.3s ease; }
        .btn-primary { background-color: #3498db; border-color: #3498db; }
        .btn-primary:hover { background-color: #2980b9; transform: translateY(-2px); box-shadow: 0 4px 8px rgba(52, 152, 219, 0.3); }
        .btn-danger { background-color: #e74c3c; border-color: #e74c3c; }
        .btn-danger:hover { background-color: #c0392b; transform: translateY(-2px); box-shadow: 0 4px 8px rgba(231, 76, 60, 0.3); }
        .card { border-radius: 12px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.05); border: none; }
    </style>
</head>
<body class="d-flex flex-column min-vh-100">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
                <i class="bi bi-journal-bookmark-fill text-warning"></i> BookStore <span class="highlight">02</span>
            </a>
            <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-4">
                    <li class="nav-item"><a class="nav-link text-white" href="${pageContext.request.contextPath}/home">Trang Chủ</a></li>
                    <li class="nav-item"><a class="nav-link text-white" href="${pageContext.request.contextPath}/products">Cửa hàng</a></li>
                </ul>
                <ul class="navbar-nav align-items-lg-center">
                    <c:choose>
                        <c:when test="${empty sessionScope.currentUser}">
                            <li class="nav-item"><a class="nav-link text-light" href="${pageContext.request.contextPath}/register">Đăng ký</a></li>
                            <li class="nav-item ms-lg-2"><a class="btn btn-warning text-dark px-4" href="${pageContext.request.contextPath}/login">Đăng nhập</a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item d-none d-lg-block me-3">
                                <span class="text-light">Xin chào, <strong><c:out value="${sessionScope.currentUser.fullname}"/></strong></span>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link text-white position-relative px-3" href="${pageContext.request.contextPath}/cart">
                                    <i class="bi bi-cart3 fs-5"></i>
                                    <span class="d-lg-none ms-2">Giỏ hàng</span>
                                    <c:if test="${not empty sessionScope.cart and sessionScope.cart.size() > 0}">
                                        <span class="position-absolute top-25 start-75 translate-middle badge rounded-pill bg-danger shadow-sm" style="font-size: 0.75rem;">
                                            ${sessionScope.cart.size()}
                                        </span>
                                    </c:if>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link text-white px-3" href="${pageContext.request.contextPath}/orders">
                                    <i class="bi bi-box-seam fs-5"></i><span class="d-lg-none ms-2">Lịch sử đơn hàng</span>
                                </a>
                            </li>
                            <li class="nav-item ms-lg-2">
                                <a class="btn btn-outline-light btn-sm mt-2 mt-lg-0" href="${pageContext.request.contextPath}/logout">Đăng xuất <i class="bi bi-box-arrow-right"></i></a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${not empty sessionScope.currentUser and sessionScope.currentUser.admin}">
                        <li class="nav-item ms-lg-3 mt-2 mt-lg-0">
                            <a class="btn btn-danger btn-sm px-3" href="${pageContext.request.contextPath}/admin/dashboard">
                                <i class="bi bi-shield-lock"></i> Quản trị
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>

    <main class="container page-content flex-grow-1">
        <sitemesh:write property="body"/>
    </main>

    <footer class="footer mt-auto">
        <div class="container text-center">
            <h5 class="text-white mb-3"><i class="bi bi-mortarboard"></i> Đồ án Lập trình Web</h5>
            <p class="mb-1"><strong>Họ tên:</strong> Đoàn Nguyễn Gia Thuận &nbsp;|&nbsp; <strong>MSSV:</strong> 24162124 &nbsp;|&nbsp; <strong>Mã đề:</strong> 02</p>
            <small class="text-muted mt-3 d-block">&copy; 2024 BookStore Project. All rights reserved.</small>
        </div>
    </footer>
    
    <!-- Bootstrap 5 JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
