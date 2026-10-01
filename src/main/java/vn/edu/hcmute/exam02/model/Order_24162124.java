package vn.edu.hcmute.exam02.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model đại diện cho một đơn hàng trong bảng `orders`.
 * Field `items` chứa danh sách OrderItem (JOIN từ order_items + books) —
 * được populate khi query lịch sử đơn hàng.
 */
public class Order_24162124 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private double totalPrice;
    private String status;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItem_24162124> items = new ArrayList<>();

    public Order_24162124() {
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingName() { return shippingName; }
    public void setShippingName(String shippingName) { this.shippingName = shippingName; }

    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItem_24162124> getItems() { return items; }
    public void setItems(List<OrderItem_24162124> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
