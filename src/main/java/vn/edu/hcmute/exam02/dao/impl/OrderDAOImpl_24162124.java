package vn.edu.hcmute.exam02.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vn.edu.hcmute.exam02.config.DBConnection_24162124;
import vn.edu.hcmute.exam02.dao.OrderDAO_24162124;
import vn.edu.hcmute.exam02.model.CartItem_24162124;
import vn.edu.hcmute.exam02.model.Order_24162124;
import vn.edu.hcmute.exam02.model.OrderItem_24162124;

/**
 * Implement OrderDAO_24162124 bằng JDBC thuần.
 *
 * placeOrder() chạy trong một JDBC Transaction:
 *   ① INSERT orders  → lấy generated id
 *   ② INSERT order_items  (từng dòng giỏ hàng)
 *   ③ UPDATE books.quantity  (trừ tồn kho, kiểm tra không âm)
 *   ④ COMMIT — hoặc ROLLBACK nếu bất kỳ bước nào thất bại
 *
 * findByUserId / findByUserIdAndStatus thực hiện JOIN 3 bảng:
 *   orders → order_items → books
 * để trả về List<Order> với mỗi Order chứa đầy đủ List<OrderItem> kèm bookName.
 */
public class OrderDAOImpl_24162124 implements OrderDAO_24162124 {

    // ── SQL — placeOrder ─────────────────────────────────────────────────────

    private static final String INSERT_ORDER_SQL =
            "INSERT INTO orders (user_id, total_price, status, "
            + "shipping_name, shipping_phone, shipping_address, note, created_at) "
            + "VALUES (?, ?, N'đơn hàng mới', ?, ?, ?, ?, ?)";

    private static final String INSERT_ITEM_SQL =
            "INSERT INTO order_items (order_id, book_id, quantity, price) "
            + "VALUES (?, ?, ?, ?)";

    /**
     * Trừ tồn kho; ràng buộc quantity không âm bằng WHERE clause.
     * Nếu rowsAffected == 0 → quantity trong kho không đủ → rollback.
     */
    private static final String DECREASE_STOCK_SQL =
            "UPDATE books SET quantity = quantity - ? "
            + "WHERE bookid = ? AND quantity >= ?";

    // ── SQL — query lịch sử (JOIN 3 bảng) ───────────────────────────────────

    /**
     * Query lấy tất cả đơn hàng + items của user.
     * Dùng LEFT JOIN để vẫn trả về đơn hàng ngay cả khi order_items rỗng
     * (trường hợp dữ liệu bất thường).
     * Sắp xếp: đơn mới nhất trước, trong từng đơn sắp theo id item.
     */
    private static final String FIND_BY_USER_SQL =
            "SELECT o.id            AS order_id, "
            + "       o.user_id, "
            + "       o.total_price, "
            + "       o.status, "
            + "       o.shipping_name, "
            + "       o.shipping_phone, "
            + "       o.shipping_address, "
            + "       o.note, "
            + "       o.created_at, "
            + "       oi.id         AS item_id, "
            + "       oi.book_id, "
            + "       oi.quantity   AS item_qty, "
            + "       oi.price      AS item_price, "
            + "       b.title       AS book_name "
            + "FROM orders o "
            + "LEFT JOIN order_items oi ON oi.order_id = o.id "
            + "LEFT JOIN books b        ON b.bookid    = oi.book_id "
            + "WHERE o.user_id = ? "
            + "ORDER BY o.created_at DESC, o.id DESC, oi.id ASC";

    /** Phiên bản lọc theo status — thêm điều kiện WHERE o.status = ? */
    private static final String FIND_BY_USER_AND_STATUS_SQL =
            "SELECT o.id            AS order_id, "
            + "       o.user_id, "
            + "       o.total_price, "
            + "       o.status, "
            + "       o.shipping_name, "
            + "       o.shipping_phone, "
            + "       o.shipping_address, "
            + "       o.note, "
            + "       o.created_at, "
            + "       oi.id         AS item_id, "
            + "       oi.book_id, "
            + "       oi.quantity   AS item_qty, "
            + "       oi.price      AS item_price, "
            + "       b.title       AS book_name "
            + "FROM orders o "
            + "LEFT JOIN order_items oi ON oi.order_id = o.id "
            + "LEFT JOIN books b        ON b.bookid    = oi.book_id "
            + "WHERE o.user_id = ? AND o.status = ? "
            + "ORDER BY o.created_at DESC, o.id DESC, oi.id ASC";

    // ── placeOrder ───────────────────────────────────────────────────────────

    @Override
    public int placeOrder(Order_24162124 order, List<CartItem_24162124> items)
            throws SQLException {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống, không thể đặt hàng.");
        }

        Connection conn = null;
        try {
            conn = DBConnection_24162124.getConnection();
            conn.setAutoCommit(false);              // ── Bắt đầu Transaction ──

            // ① INSERT orders → lấy generated id
            int orderId = insertOrder(conn, order);

            // ② INSERT order_items + ③ UPDATE books.quantity
            for (CartItem_24162124 item : items) {
                insertOrderItem(conn, orderId, item);
                decreaseStock(conn, item.getBookId(), item.getQuantity());
            }

            conn.commit();                          // ── COMMIT ──
            return orderId;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();                // ── ROLLBACK khi có lỗi ──
                } catch (SQLException rollbackEx) {
                    rollbackEx.addSuppressed(ex);
                    throw rollbackEx;
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ── findByUserId ─────────────────────────────────────────────────────────

    @Override
    public List<Order_24162124> findByUserId(int userId) throws SQLException {
        try (Connection conn = DBConnection_24162124.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_USER_SQL)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapOrdersWithItems(rs);
            }
        }
    }

    // ── findByUserIdAndStatus ────────────────────────────────────────────────

    @Override
    public List<Order_24162124> findByUserIdAndStatus(int userId, String status)
            throws SQLException {
        try (Connection conn = DBConnection_24162124.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_USER_AND_STATUS_SQL)) {
            stmt.setInt(1, userId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapOrdersWithItems(rs);
            }
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Đọc ResultSet JOIN (orders + order_items + books) và nhóm thành
     * List<Order> với mỗi Order chứa List<OrderItem>.
     * Dùng LinkedHashMap để giữ thứ tự theo created_at DESC.
     */
    private List<Order_24162124> mapOrdersWithItems(ResultSet rs) throws SQLException {
        // orderId → Order object  (giữ thứ tự insert = thứ tự ResultSet)
        Map<Integer, Order_24162124> orderMap = new LinkedHashMap<>();

        while (rs.next()) {
            int orderId = rs.getInt("order_id");

            // Lấy hoặc tạo mới Order
            Order_24162124 order = orderMap.computeIfAbsent(orderId, id -> {
                try {
                    return mapOrderRow(rs);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            // Thêm OrderItem nếu có (LEFT JOIN có thể trả về NULL khi không có item)
            int itemId = rs.getInt("item_id");
            if (!rs.wasNull()) {
                OrderItem_24162124 item = new OrderItem_24162124();
                item.setId(itemId);
                item.setOrderId(orderId);
                item.setBookId(rs.getInt("book_id"));
                item.setQuantity(rs.getInt("item_qty"));
                item.setPrice(rs.getDouble("item_price"));
                item.setBookName(rs.getString("book_name"));
                order.getItems().add(item);
            }
        }

        return new ArrayList<>(orderMap.values());
    }

    /**
     * Map các cột của bảng orders từ ResultSet hiện tại thành Order_24162124.
     */
    private Order_24162124 mapOrderRow(ResultSet rs) throws SQLException {
        Order_24162124 order = new Order_24162124();
        order.setId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setStatus(rs.getString("status"));
        order.setShippingName(rs.getString("shipping_name"));
        order.setShippingPhone(rs.getString("shipping_phone"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setNote(rs.getString("note"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        order.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return order;
    }

    /**
     * Bước ①: Insert vào bảng orders và trả về id vừa sinh ra.
     */
    private int insertOrder(Connection conn, Order_24162124 order) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                INSERT_ORDER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserId());
            stmt.setDouble(2, order.getTotalPrice());
            stmt.setString(3, order.getShippingName());
            stmt.setString(4, order.getShippingPhone());
            stmt.setString(5, order.getShippingAddress());
            stmt.setString(6, order.getNote());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Không thể tạo đơn hàng trong bảng orders.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new SQLException("Không đọc được id đơn hàng vừa tạo.");
        }
    }

    /**
     * Bước ②: Insert một dòng vào order_items.
     */
    private void insertOrderItem(Connection conn, int orderId, CartItem_24162124 item)
            throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_ITEM_SQL)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, item.getBookId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            if (stmt.executeUpdate() != 1) {
                throw new SQLException(
                        "Không thể thêm sản phẩm bookId=" + item.getBookId()
                        + " vào order_items.");
            }
        }
    }

    /**
     * Bước ③: Trừ tồn kho — rollback nếu không đủ hàng.
     */
    private void decreaseStock(Connection conn, int bookId, int qty) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DECREASE_STOCK_SQL)) {
            stmt.setInt(1, qty);
            stmt.setInt(2, bookId);
            stmt.setInt(3, qty);            // WHERE quantity >= qty
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException(
                        "Sách bookId=" + bookId
                        + " không đủ tồn kho để đặt số lượng " + qty + ".");
            }
        }
    }
}
