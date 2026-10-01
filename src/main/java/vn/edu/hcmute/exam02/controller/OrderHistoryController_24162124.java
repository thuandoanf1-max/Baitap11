package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.hcmute.exam02.model.Order_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;
import vn.edu.hcmute.exam02.service.OrderService_24162124;
import vn.edu.hcmute.exam02.service.impl.OrderServiceImpl_24162124;

/**
 * OrderHistoryController_24162124 — Hiển thị lịch sử đơn hàng của User.
 *
 * URL mapping: /orders
 *   GET ?status=<trang_thai>  → lọc theo trạng thái
 *   GET (không có status)     → hiển thị tất cả đơn hàng
 *
 * Ràng buộc: chỉ cho phép user đã đăng nhập.
 */
public class OrderHistoryController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String HISTORY_VIEW = "/WEB-INF/views/book/history.jsp";
    private static final String SESSION_USER = "currentUser";

    private final OrderService_24162124 orderService = new OrderServiceImpl_24162124();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Kiểm tra đăng nhập ──────────────────────────────────────────────
        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login?required=orders");
            return;
        }

        int userId = getCurrentUser(request).getId();

        // ── Đọc tham số lọc từ request ──────────────────────────────────────
        String status = request.getParameter("status");
        // Trim và chuẩn hóa — null hoặc rỗng → lấy tất cả
        if (status != null) {
            status = status.trim();
            if (status.isEmpty()) status = null;
        }

        try {
            List<Order_24162124> orders = orderService.getOrdersByUserAndStatus(userId, status);

            request.setAttribute("orders",        orders);
            request.setAttribute("selectedStatus", status); // null = "Tất cả"
            request.getRequestDispatcher(HISTORY_VIEW).forward(request, response);

        } catch (SQLException ex) {
            getServletContext().log("OrderHistoryController: Loi truy van lich su don hang", ex);
            request.setAttribute("historyError",
                    "Không thể tải lịch sử đơn hàng từ SQL Server. Vui lòng thử lại.");
            request.setAttribute("selectedStatus", status);
            request.getRequestDispatcher(HISTORY_VIEW).forward(request, response);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER) != null;
    }

    private User_24162124 getCurrentUser(HttpServletRequest request) {
        return (User_24162124) request.getSession(false).getAttribute(SESSION_USER);
    }
}
