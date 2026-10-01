package vn.edu.hcmute.exam02.service;

import java.sql.SQLException;
import java.util.List;

import vn.edu.hcmute.exam02.model.CartItem_24162124;
import vn.edu.hcmute.exam02.model.Order_24162124;

/**
 * Service interface cho chức năng đặt hàng và lịch sử đơn hàng.
 */
public interface OrderService_24162124 {

    /**
     * Đặt hàng COD: validate, tính tổng tiền, gọi DAO trong transaction.
     */
    int placeOrder(int userId,
                   String shippingName,
                   String shippingPhone,
                   String shippingAddress,
                   String note,
                   List<CartItem_24162124> cartItems) throws SQLException;

    /**
     * Lấy tất cả đơn hàng của user (kèm items), mới nhất trước.
     */
    List<Order_24162124> getOrdersByUser(int userId) throws SQLException;

    /**
     * Lấy đơn hàng của user theo trạng thái (kèm items).
     * Nếu status null hoặc rỗng → trả về tất cả đơn hàng.
     */
    List<Order_24162124> getOrdersByUserAndStatus(int userId, String status) throws SQLException;
}
