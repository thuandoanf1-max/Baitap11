-- ============================================================
-- FILE: test_data_insert.sql
-- Mục đích: Chèn dữ liệu mẫu để kiểm thử 3 chức năng:
--           Giỏ hàng / Thanh toán COD / Lịch sử đơn hàng
-- Database:  BookStore_24162124 (GIATHUAN\SQLEXPRESS)
-- Chạy:      Mở SSMS → New Query → chọn DB → F5
-- ============================================================

USE BookStore_24162124;
GO

-- ─────────────────────────────────────────────────────────────
-- BƯỚC 0: Kiểm tra đã có bảng chưa
-- ─────────────────────────────────────────────────────────────
SELECT 'users'       AS tbl, COUNT(*) AS rows FROM users       UNION ALL
SELECT 'books',               COUNT(*)        FROM books       UNION ALL
SELECT 'orders',              COUNT(*)        FROM orders      UNION ALL
SELECT 'order_items',         COUNT(*)        FROM order_items;
GO

-- ─────────────────────────────────────────────────────────────
-- BƯỚC 1: Tạo User test
--   Email:    test@bookstore.com
--   Password: test123          (plain-text vì app không hash)
--   Không phải admin
-- CHẠY CHỈ NẾU chưa có user này
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'test@bookstore.com')
BEGIN
    INSERT INTO users (email, fullname, phone, passwd, signup_date, last_login, is_admin)
    VALUES (
        'test@bookstore.com',
        N'Người Dùng Test',
        900000001,
        'test123',
        GETDATE(),
        NULL,
        0
    );
    PRINT 'Da them user test@bookstore.com';
END
ELSE
    PRINT 'User test@bookstore.com da ton tai, bo qua.';
GO

-- Xem id của user vừa tạo (dùng cho các bước sau)
SELECT id, email, fullname, is_admin FROM users WHERE email = 'test@bookstore.com';
GO

-- ─────────────────────────────────────────────────────────────
-- BƯỚC 2: Tạo Author mẫu (nếu chưa có)
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM author WHERE author_name = N'Tác Giả Test')
BEGIN
    INSERT INTO author (author_name) VALUES (N'Tác Giả Test');
    PRINT 'Da them author.';
END
GO

DECLARE @authorId INT = (SELECT TOP 1 author_id FROM author WHERE author_name = N'Tác Giả Test');

-- ─────────────────────────────────────────────────────────────
-- BƯỚC 3: Chèn 3 Sách test với stock rõ ràng
--
--   📗 Sách Alpha — stock =  3  (dùng để test vượt quá tồn kho)
--   📘 Sách Beta  — stock =  5  (dùng để test mua bình thường)
--   📕 Sách Gamma — stock = 10  (dùng để test mua nhiều)
-- ─────────────────────────────────────────────────────────────

-- Xóa sách test cũ nếu tồn tại (tránh duplicate khi chạy lại)
DELETE FROM book_author WHERE bookid IN (
    SELECT bookid FROM books WHERE title IN (N'Sách Alpha [TEST]', N'Sách Beta [TEST]', N'Sách Gamma [TEST]')
);
DELETE FROM rating WHERE bookid IN (
    SELECT bookid FROM books WHERE title IN (N'Sách Alpha [TEST]', N'Sách Beta [TEST]', N'Sách Gamma [TEST]')
);
DELETE FROM books WHERE title IN (N'Sách Alpha [TEST]', N'Sách Beta [TEST]', N'Sách Gamma [TEST]');

-- Chèn Sách Alpha — stock = 3 (test vượt kho)
INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
VALUES (1001, N'Sách Alpha [TEST]', N'NXB Test', 75000.00,
        N'Sách Alpha dùng để kiểm thử tính năng giỏ hàng. Chỉ có 3 cuốn trong kho.',
        '2024-01-01', NULL, 3);

DECLARE @bookAlpha INT = SCOPE_IDENTITY();
INSERT INTO book_author (bookid, author_id) VALUES (@bookAlpha, @authorId);
PRINT CONCAT('Da tao Sach Alpha — bookid = ', @bookAlpha, ', stock = 3');

-- Chèn Sách Beta — stock = 5 (test mua bình thường)
INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
VALUES (1002, N'Sách Beta [TEST]', N'NXB Test', 120000.00,
        N'Sách Beta dùng để kiểm thử thanh toán COD. Có 5 cuốn trong kho.',
        '2024-03-15', NULL, 5);

DECLARE @bookBeta INT = SCOPE_IDENTITY();
INSERT INTO book_author (bookid, author_id) VALUES (@bookBeta, @authorId);
PRINT CONCAT('Da tao Sach Beta — bookid = ', @bookBeta, ', stock = 5');

-- Chèn Sách Gamma — stock = 10 (test mua nhiều)
INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
VALUES (1003, N'Sách Gamma [TEST]', N'NXB Test', 200000.00,
        N'Sách Gamma dùng để kiểm thử lịch sử đơn hàng. Có 10 cuốn trong kho.',
        '2024-06-01', NULL, 10);

DECLARE @bookGamma INT = SCOPE_IDENTITY();
INSERT INTO book_author (bookid, author_id) VALUES (@bookGamma, @authorId);
PRINT CONCAT('Da tao Sach Gamma — bookid = ', @bookGamma, ', stock = 10');
GO

-- ─────────────────────────────────────────────────────────────
-- BƯỚC 4: Xác nhận dữ liệu đã chèn
-- ─────────────────────────────────────────────────────────────
SELECT
    b.bookid,
    b.title,
    b.price,
    b.quantity AS stock,
    a.author_name
FROM books b
LEFT JOIN book_author ba ON ba.bookid  = b.bookid
LEFT JOIN author a       ON a.author_id = ba.author_id
WHERE b.title LIKE '%[TEST]%'
ORDER BY b.bookid;
GO

PRINT '=== Seed data da san sang. Bat dau test! ===';
GO
