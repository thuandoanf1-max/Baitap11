package vn.edu.hcmute.exam02.model;

import java.io.Serializable;

/**
 * Model đại diện cho một dòng sản phẩm trong bảng `order_items`.
 * Bao gồm cả bookName (JOIN từ bảng books) để hiển thị trong lịch sử đơn hàng.
 */
public class OrderItem_24162124 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int orderId;
    private int bookId;
    private int quantity;
    private double price;
    /** Tên sách — được điền qua JOIN với bảng books khi query lịch sử. */
    private String bookName;

    public OrderItem_24162124() {
    }

    public OrderItem_24162124(int bookId, int quantity, double price) {
        this.bookId   = bookId;
        this.quantity = quantity;
        this.price    = price;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    // ── Computed helper ──────────────────────────────────────────────────────

    public double getSubtotal() {
        return price * quantity;
    }
}
