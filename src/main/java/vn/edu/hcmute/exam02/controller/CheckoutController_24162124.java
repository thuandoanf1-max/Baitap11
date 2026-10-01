package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.hcmute.exam02.model.CartItem_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;
import vn.edu.hcmute.exam02.service.OrderService_24162124;
import vn.edu.hcmute.exam02.service.impl.OrderServiceImpl_24162124;

/**
 * CheckoutController_24162124 — Servlet xử lý thanh toán COD.
 *
 * URL mapping: /checkout
 *   GET  → hiển thị form nhập thông tin giao hàng (checkout.jsp)
 *   POST → nhận form, đặt hàng, xóa giỏ khỏi session, redirect thành công
 *
 * Ràng buộc: chỉ cho phép user đã đăng nhập.
 */
public class CheckoutController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String CHECKOUT_VIEW   = "/WEB-INF/views/book/checkout.jsp";
    private static final String SESSION_USER    = "currentUser";
    private static final String SESSION_CART    = "cart";

    private final OrderService_24162124 orderService = new OrderServiceImpl_24162124();

    // ══════════════════════════════════════════════════════════════════════════
    // GET — Hiển thị form thanh toán
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login?required=checkout");
            return;
        }

        List<CartItem_24162124> cart = getCart(request);
        if (cart == null || cart.isEmpty()) {
            // Giỏ rỗng → quay về giỏ hàng
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Điền sẵn họ tên & SĐT từ tài khoản để user tiện chỉnh sửa
        User_24162124 user = getCurrentUser(request);
        request.setAttribute("prefillName",  user.getFullname());
        request.setAttribute("prefillPhone",
                user.getPhone() != null ? String.valueOf(user.getPhone()) : "");

        request.getRequestDispatcher(CHECKOUT_VIEW).forward(request, response);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // POST — Xử lý đặt hàng
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login?required=checkout");
            return;
        }

        List<CartItem_24162124> cart = getCart(request);
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        String shippingName    = request.getParameter("shippingName");
        String shippingPhone   = request.getParameter("shippingPhone");
        String shippingAddress = request.getParameter("shippingAddress");
        String note            = request.getParameter("note");

        User_24162124 user = getCurrentUser(request);

        try {
            int orderId = orderService.placeOrder(
                    user.getId(),
                    shippingName,
                    shippingPhone,
                    shippingAddress,
                    note,
                    cart);

            // ── Đặt hàng thành công: xóa giỏ hàng khỏi session ──────────────
            request.getSession(true).removeAttribute(SESSION_CART);

            // Truyền orderId qua flash để trang success hiển thị
            request.getSession(true).setAttribute("orderSuccessId", orderId);
            response.sendRedirect(request.getContextPath() + "/checkout?success=1");

        } catch (IllegalArgumentException ex) {
            // Lỗi validate → hiển thị lại form kèm thông báo lỗi
            request.setAttribute("checkoutError", ex.getMessage());
            request.setAttribute("prefillName",    shippingName);
            request.setAttribute("prefillPhone",   shippingPhone);
            request.setAttribute("prefillAddress", shippingAddress);
            request.setAttribute("prefillNote",    note);
            request.getRequestDispatcher(CHECKOUT_VIEW).forward(request, response);

        } catch (SQLException ex) {
            getServletContext().log("CheckoutController: Loi dat hang", ex);
            // Lỗi DB (kể cả tồn kho không đủ sau khi đã qua validate)
            request.setAttribute("checkoutError",
                    "Không thể đặt hàng: " + ex.getMessage()
                    + " Vui lòng thử lại.");
            request.setAttribute("prefillName",    shippingName);
            request.setAttribute("prefillPhone",   shippingPhone);
            request.setAttribute("prefillAddress", shippingAddress);
            request.setAttribute("prefillNote",    note);
            request.getRequestDispatcher(CHECKOUT_VIEW).forward(request, response);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════════════════════

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER) != null;
    }

    private User_24162124 getCurrentUser(HttpServletRequest request) {
        return (User_24162124) request.getSession(false).getAttribute(SESSION_USER);
    }

    @SuppressWarnings("unchecked")
    private List<CartItem_24162124> getCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (List<CartItem_24162124>) session.getAttribute(SESSION_CART);
    }
}
