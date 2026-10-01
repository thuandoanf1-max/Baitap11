package vn.edu.hcmute.exam02.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.edu.hcmute.exam02.model.Book_24162124;
import vn.edu.hcmute.exam02.model.CartItem_24162124;
import vn.edu.hcmute.exam02.model.User_24162124;
import vn.edu.hcmute.exam02.service.BookService_24162124;
import vn.edu.hcmute.exam02.service.impl.BookServiceImpl_24162124;

/**
 * CartController_24162124 — Servlet xử lý giỏ hàng (Session-based, không DB).
 *
 * URL mapping: /cart
 *   GET           → hiển thị giỏ hàng
 *   POST action=add    → thêm sách
 *   POST action=update → cập nhật số lượng
 *   POST action=remove → xóa một cuốn khỏi giỏ
 *
 * Key session: "cart" → List<CartItem_24162124>
 */
public class CartController_24162124 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String CART_VIEW    = "/WEB-INF/views/book/cart.jsp";
    private static final String SESSION_CART = "cart";
    private static final String SESSION_USER = "currentUser";

    private final BookService_24162124 bookService = new BookServiceImpl_24162124();

    // ══════════════════════════════════════════════════════════════════════════
    // GET — hiển thị giỏ hàng
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login?required=cart");
            return;
        }

        moveFlashToRequest(request);
        request.getRequestDispatcher(CART_VIEW).forward(request, response);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // POST — điều phối theo action
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login?required=cart");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add"    -> handleAdd(request, response);
            case "update" -> handleUpdate(request, response);
            case "remove" -> handleRemove(request, response);
            default       -> response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Xử lý: Thêm sản phẩm vào giỏ
    // ══════════════════════════════════════════════════════════════════════════

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer bookId = parsePositiveInt(request.getParameter("bookId"));
        Integer qty    = parsePositiveInt(request.getParameter("quantity"));

        if (bookId == null || qty == null) {
            setFlashError(request, "Dữ liệu thêm vào giỏ không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            Optional<Book_24162124> bookOpt = bookService.getBookById(bookId);
            if (bookOpt.isEmpty()) {
                setFlashError(request, "Không tìm thấy sách trong hệ thống.");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            Book_24162124 book  = bookOpt.get();
            int stock           = book.getQuantity() != null ? book.getQuantity() : 0;
            List<CartItem_24162124> cart = getOrCreateCart(request);

            // Kiểm tra sách đã có trong giỏ chưa
            CartItem_24162124 existing = findItemById(cart, bookId);

            if (existing != null) {
                // Cộng thêm số lượng, không vượt tồn kho
                int newQty = existing.getQuantity() + qty;
                if (newQty > stock) {
                    setFlashError(request,
                            "Số lượng vượt quá tồn kho. Tối đa còn " + stock + " cuốn.");
                    response.sendRedirect(request.getContextPath() + "/cart");
                    return;
                }
                existing.setQuantity(newQty);
                existing.setStock(stock); // cập nhật stock phòng khi thay đổi
            } else {
                // Sách chưa có trong giỏ — kiểm tra qty ban đầu
                if (qty > stock) {
                    setFlashError(request,
                            "Số lượng yêu cầu vượt quá tồn kho. Tối đa còn " + stock + " cuốn.");
                    response.sendRedirect(request.getContextPath() + "/cart");
                    return;
                }
                if (stock == 0) {
                    setFlashError(request, "Sách \"" + book.getTitle() + "\" đã hết hàng.");
                    response.sendRedirect(request.getContextPath() + "/cart");
                    return;
                }
                CartItem_24162124 item = new CartItem_24162124(
                        bookId,
                        book.getTitle(),
                        book.getPrice() != null ? book.getPrice().doubleValue() : 0.0,
                        qty,
                        book.getCoverImage(),
                        stock
                );
                cart.add(item);
            }

            setFlashSuccess(request, "Đã thêm \"" + book.getTitle() + "\" vào giỏ hàng.");

        } catch (SQLException e) {
            getServletContext().log("CartController: Loi them vao gio hang", e);
            setFlashError(request, "Không thể truy xuất thông tin sách từ SQL Server.");
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Xử lý: Cập nhật số lượng
    // ══════════════════════════════════════════════════════════════════════════

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer bookId = parsePositiveInt(request.getParameter("bookId"));
        String qtyStr  = request.getParameter("quantity");

        if (bookId == null || qtyStr == null) {
            setFlashError(request, "Dữ liệu cập nhật không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr.trim());
        } catch (NumberFormatException e) {
            setFlashError(request, "Số lượng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        List<CartItem_24162124> cart = getOrCreateCart(request);
        CartItem_24162124 item       = findItemById(cart, bookId);

        if (item == null) {
            setFlashError(request, "Không tìm thấy sản phẩm trong giỏ hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Ràng buộc: qty >= 1 và qty <= stock
        if (qty < 1) {
            setFlashError(request, "Số lượng tối thiểu là 1. Dùng nút Xóa nếu muốn bỏ sản phẩm.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        if (qty > item.getStock()) {
            setFlashError(request,
                    "Số lượng vượt quá tồn kho. Tối đa còn " + item.getStock() + " cuốn.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        item.setQuantity(qty);
        setFlashSuccess(request, "Đã cập nhật số lượng.");
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Xử lý: Xóa một sản phẩm khỏi giỏ
    // ══════════════════════════════════════════════════════════════════════════

    private void handleRemove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer bookId = parsePositiveInt(request.getParameter("bookId"));

        if (bookId == null) {
            setFlashError(request, "Mã sách không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        List<CartItem_24162124> cart = getOrCreateCart(request);
        boolean removed = cart.removeIf(item -> item.getBookId() == bookId);

        if (removed) {
            setFlashSuccess(request, "Đã xóa sản phẩm khỏi giỏ hàng.");
        } else {
            setFlashError(request, "Không tìm thấy sản phẩm cần xóa trong giỏ hàng.");
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════════════════════

    /** Lấy giỏ hàng từ session; nếu chưa có thì tạo mới và gán vào session. */
    @SuppressWarnings("unchecked")
    private List<CartItem_24162124> getOrCreateCart(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        List<CartItem_24162124> cart =
                (List<CartItem_24162124>) session.getAttribute(SESSION_CART);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    /** Tìm CartItem theo bookId trong danh sách; trả null nếu không tìm thấy. */
    private CartItem_24162124 findItemById(List<CartItem_24162124> cart, int bookId) {
        return cart.stream()
                .filter(item -> item.getBookId() == bookId)
                .findFirst()
                .orElse(null);
    }

    /** Kiểm tra user đã đăng nhập chưa. */
    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER) != null;
    }

    /** Parse chuỗi thành int > 0; trả null nếu không hợp lệ. */
    private Integer parsePositiveInt(String value) {
        try {
            int v = Integer.parseInt(value == null ? "" : value.trim());
            return v > 0 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ── Flash messages (lưu vào session, đọc + xóa ở doGet) ─────────────────

    private void setFlashSuccess(HttpServletRequest request, String msg) {
        request.getSession(true).setAttribute("cartFlashSuccess", msg);
    }

    private void setFlashError(HttpServletRequest request, String msg) {
        request.getSession(true).setAttribute("cartFlashError", msg);
    }

    private void moveFlashToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return;
        moveFlash(session, request, "cartFlashSuccess", "cartSuccess");
        moveFlash(session, request, "cartFlashError",   "cartError");
    }

    private void moveFlash(HttpSession session, HttpServletRequest request,
                           String sessionKey, String requestKey) {
        Object value = session.getAttribute(sessionKey);
        if (value != null) {
            request.setAttribute(requestKey, value);
            session.removeAttribute(sessionKey);
        }
    }
}
