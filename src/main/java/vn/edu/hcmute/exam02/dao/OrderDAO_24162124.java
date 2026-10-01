package vn.edu.hcmute.exam02.dao;

import java.sql.SQLException;
import java.util.List;

import vn.edu.hcmute.exam02.model.CartItem_24162124;
import vn.edu.hcmute.exam02.model.Order_24162124;

/**
 * DAO interface cho thao tác đơn hàng.
 */
public interface OrderDAO_24162124 {

    /**
     * Đặt hàng trong một JDBC Transaction duy nhất:
     *  1. INSERT vào bảng orders → lấy generated order_id
     *  2. INSERT từng dòng vào order_items
     *  3. UPDATE books.quantity (trừ tồn kho) cho từng cuốn
     *  4. COMMIT hoặc ROLLBACK nếu có lỗi
     *
     * @param order đối tượng đơn hàng (chưa có id)
     * @param items danh sách CartItem từ Session
     * @return id của đơn hàng vừa tạo
     * @throws SQLException nếu lỗi DB hoặc tồn kho không đủ
     */
    int placeOrder(Order_24162124 order, List<CartItem_24162124> items) throws SQLException;

    /**
     * Lấy tất cả đơn hàng của user (kèm items), sắp xếp mới nhất trước.
     */
    List<Order_24162124> findByUserId(int userId) throws SQLException;

    /**
     * Lấy đơn hàng của user theo trạng thái (kèm items), sắp xếp mới nhất trước.
     * JOIN order_items + books để lấy tên sách.
     *
     * @param userId id người dùng
     * @param status trạng thái cần lọc (ví dụ: "đơn hàng mới", "đã giao"...)
     */
    List<Order_24162124> findByUserIdAndStatus(int userId, String status) throws SQLException;
}
