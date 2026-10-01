package vn.edu.hcmute.exam02.model;

import java.io.Serializable;

/**
 * Đại diện một dòng sản phẩm trong giỏ hàng (lưu qua HttpSession).
 * Implements Serializable theo yêu cầu khi object nằm trong Session.
 */
public class CartItem_24162124 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookId;
    private String bookName;
    private double price;
    private int quantity;
    private String coverImage;
    private int stock;

    public CartItem_24162124() {
    }

    public CartItem_24162124(int bookId, String bookName, double price,
                             int quantity, String coverImage, int stock) {
        this.bookId    = bookId;
        this.bookName  = bookName;
        this.price     = price;
        this.quantity  = quantity;
        this.coverImage = coverImage;
        this.stock     = stock;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // ── Computed helper (dùng trong JSP qua EL: ${item.subtotal}) ───────────

    public double getSubtotal() {
        return price * quantity;
    }
}
