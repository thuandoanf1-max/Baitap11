-- ============================================================
-- SQL MẪU để kiểm thử lịch sử đơn hàng theo trạng thái
-- Database: BookStore_24162124
-- Chạy từng khối trong SQL Server Management Studio (SSMS)
-- ============================================================

-- ─────────────────────────────────────────────────────────────
-- 1. XEM tất cả đơn hàng hiện có (để lấy id đơn cần đổi)
-- ─────────────────────────────────────────────────────────────
SELECT id, user_id, status, total_price, shipping_name, created_at
FROM orders
ORDER BY created_at DESC;


-- ─────────────────────────────────────────────────────────────
-- 2. XEM chi tiết sản phẩm của một đơn (thay ? bằng order_id)
-- ─────────────────────────────────────────────────────────────
SELECT oi.id, oi.order_id, b.title AS book_name, oi.quantity, oi.price
FROM order_items oi
JOIN books b ON b.bookid = oi.book_id
WHERE oi.order_id = 1;           -- ← đổi 1 thành id đơn hàng cần xem


-- ─────────────────────────────────────────────────────────────
-- 3. ĐỔI TRẠNG THÁI từng đơn hàng để test lọc tab
--    (thay số id và giá trị status phù hợp)
-- ─────────────────────────────────────────────────────────────

-- → Tab "đơn hàng mới" (mặc định khi vừa đặt)
UPDATE orders SET status = N'đơn hàng mới'   WHERE id = 1;

-- → Tab "đã xác nhận"
UPDATE orders SET status = N'đã xác nhận'    WHERE id = 2;

-- → Tab "chuẩn bị hàng"
UPDATE orders SET status = N'chuẩn bị hàng'  WHERE id = 3;

-- → Tab "vận chuyển"
UPDATE orders SET status = N'vận chuyển'     WHERE id = 4;

-- → Tab "giao hàng"
UPDATE orders SET status = N'giao hàng'      WHERE id = 5;

-- → Tab "đã giao"
UPDATE orders SET status = N'đã giao'        WHERE id = 6;

-- → Tab "đơn hàng hủy"
UPDATE orders SET status = N'đơn hàng hủy'   WHERE id = 7;

-- → Tab "đơn hàng hoàn"
UPDATE orders SET status = N'đơn hàng hoàn'  WHERE id = 8;


-- ─────────────────────────────────────────────────────────────
-- 4. CẬP NHẬT NHIỀU ĐƠN CÙNG LÚC (ví dụ: đổi tất cả sang "đã giao")
-- ─────────────────────────────────────────────────────────────
UPDATE orders
SET status = N'đã giao'
WHERE user_id = 1;               -- ← thay bằng user_id của tài khoản đang test


-- ─────────────────────────────────────────────────────────────
-- 5. INSERT ĐƠN HÀNG MẪU (nếu chưa có đơn nào để test)
--    Giả sử user_id=1, book_id=1 đã tồn tại trong DB
-- ─────────────────────────────────────────────────────────────

-- Đơn 1: đơn hàng mới
INSERT INTO orders (user_id, total_price, status, shipping_name, shipping_phone, shipping_address, note, created_at)
VALUES (1, 150000, N'đơn hàng mới', N'Nguyễn Văn A', '0901234567', N'123 Lê Lợi, Q1, TP.HCM', NULL, GETDATE());

-- Lấy id vừa insert để thêm order_items
DECLARE @oid1 INT = SCOPE_IDENTITY();
INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (@oid1, 1, 2, 75000);


-- Đơn 2: đã xác nhận
INSERT INTO orders (user_id, total_price, status, shipping_name, shipping_phone, shipping_address, note, created_at)
VALUES (1, 200000, N'đã xác nhận', N'Nguyễn Văn A', '0901234567', N'123 Lê Lợi, Q1, TP.HCM', N'Giao giờ hành chính', DATEADD(MINUTE,-30,GETDATE()));

DECLARE @oid2 INT = SCOPE_IDENTITY();
INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (@oid2, 1, 1, 200000);


-- Đơn 3: đã giao
INSERT INTO orders (user_id, total_price, status, shipping_name, shipping_phone, shipping_address, note, created_at)
VALUES (1, 300000, N'đã giao', N'Nguyễn Văn A', '0901234567', N'456 Trần Hưng Đạo, Q5', NULL, DATEADD(HOUR,-2,GETDATE()));

DECLARE @oid3 INT = SCOPE_IDENTITY();
INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (@oid3, 1, 1, 150000);
INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (@oid3, 1, 1, 150000);


-- Đơn 4: đơn hàng hủy
INSERT INTO orders (user_id, total_price, status, shipping_name, shipping_phone, shipping_address, note, created_at)
VALUES (1, 50000, N'đơn hàng hủy', N'Nguyễn Văn A', '0901234567', N'789 Nguyễn Trãi, Q5', N'Khách hủy', DATEADD(DAY,-1,GETDATE()));

DECLARE @oid4 INT = SCOPE_IDENTITY();
INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (@oid4, 1, 1, 50000);


-- ─────────────────────────────────────────────────────────────
-- 6. KIỂM TRA kết quả JOIN (giống query mà DAO dùng)
-- ─────────────────────────────────────────────────────────────
SELECT
    o.id            AS order_id,
    o.status,
    o.total_price,
    o.created_at,
    oi.id           AS item_id,
    b.title         AS book_name,
    oi.quantity,
    oi.price
FROM orders o
LEFT JOIN order_items oi ON oi.order_id = o.id
LEFT JOIN books b        ON b.bookid    = oi.book_id
WHERE o.user_id = 1               -- ← thay bằng user_id đang test
ORDER BY o.created_at DESC, o.id DESC, oi.id ASC;


-- ─────────────────────────────────────────────────────────────
-- 7. KIỂM TRA lọc theo trạng thái cụ thể (như DAO làm)
-- ─────────────────────────────────────────────────────────────
SELECT
    o.id, o.status, o.total_price,
    b.title AS book_name, oi.quantity, oi.price
FROM orders o
LEFT JOIN order_items oi ON oi.order_id = o.id
LEFT JOIN books b        ON b.bookid    = oi.book_id
WHERE o.user_id = 1               -- ← user_id đang test
  AND o.status  = N'đã giao'      -- ← đổi thành trạng thái muốn kiểm tra
ORDER BY o.created_at DESC, o.id DESC;


-- ─────────────────────────────────────────────────────────────
-- 8. RESET: xóa đơn hàng test (cẩn thận!)
-- ─────────────────────────────────────────────────────────────
-- order_items sẽ bị xóa theo cascade (ON DELETE CASCADE đã khai báo)
-- DELETE FROM orders WHERE user_id = 1;  -- ← bỏ comment nếu muốn xóa sạch
