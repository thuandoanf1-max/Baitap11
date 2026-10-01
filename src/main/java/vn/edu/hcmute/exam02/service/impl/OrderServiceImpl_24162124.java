package vn.edu.hcmute.exam02.service.impl;

import java.sql.SQLException;
import java.util.List;

import vn.edu.hcmute.exam02.dao.OrderDAO_24162124;
import vn.edu.hcmute.exam02.dao.impl.OrderDAOImpl_24162124;
import vn.edu.hcmute.exam02.model.CartItem_24162124;
import vn.edu.hcmute.exam02.model.Order_24162124;
import vn.edu.hcmute.exam02.service.OrderService_24162124;

/**
 * Implement OrderService — validate input, tính tổng, delegate xuống DAO.
 */
public class OrderServiceImpl_24162124 implements OrderService_24162124 {

    private final OrderDAO_24162124 orderDAO;

    public OrderServiceImpl_24162124() {
        this(new OrderDAOImpl_24162124());
    }

    public OrderServiceImpl_24162124(OrderDAO_24162124 orderDAO) {
        this.orderDAO = orderDAO;
    }

    // ── placeOrder ───────────────────────────────────────────────────────────

    @Override
    public int placeOrder(int userId,
                          String shippingName,
                          String shippingPhone,
                          String shippingAddress,
                          String note,
                          List<CartItem_24162124> cartItems) throws SQLException {

        if (userId < 1) {
            throw new IllegalArgumentException("Bạn cần đăng nhập trước khi đặt hàng.");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống, không thể đặt hàng.");
        }

        String normName    = normalize(shippingName,    "Họ tên người nhận",  200);
        String normPhone   = normalize(shippingPhone,   "Số điện thoại",      20);
        String normAddress = normalize(shippingAddress, "Địa chỉ giao hàng", 500);
        String normNote    = (note == null) ? null : note.trim();
        if (normNote != null && normNote.length() > 500) {
            throw new IllegalArgumentException("Ghi chú không được vượt quá 500 ký tự.");
        }
        if (normNote != null && normNote.isEmpty()) {
            normNote = null;
        }

        double totalPrice = cartItems.stream()
                .mapToDouble(CartItem_24162124::getSubtotal)
                .sum();

        Order_24162124 order = new Order_24162124();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("đơn hàng mới");
        order.setShippingName(normName);
        order.setShippingPhone(normPhone);
        order.setShippingAddress(normAddress);
        order.setNote(normNote);

        return orderDAO.placeOrder(order, cartItems);
    }

    // ── getOrdersByUser ──────────────────────────────────────────────────────

    @Override
    public List<Order_24162124> getOrdersByUser(int userId) throws SQLException {
        if (userId < 1) throw new IllegalArgumentException("User ID không hợp lệ.");
        return orderDAO.findByUserId(userId);
    }

    // ── getOrdersByUserAndStatus ─────────────────────────────────────────────

    @Override
    public List<Order_24162124> getOrdersByUserAndStatus(int userId, String status)
            throws SQLException {
        if (userId < 1) throw new IllegalArgumentException("User ID không hợp lệ.");
        // Nếu status rỗng hoặc null → lấy tất cả
        if (status == null || status.trim().isEmpty()) {
            return orderDAO.findByUserId(userId);
        }
        return orderDAO.findByUserIdAndStatus(userId, status.trim());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String normalize(String value, String fieldName, int maxLength) {
        String trimmed = (value == null) ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " không được vượt quá " + maxLength + " ký tự.");
        }
        return trimmed;
    }
}
