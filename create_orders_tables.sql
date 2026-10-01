-- ============================================================
-- Script tạo bảng orders và order_items
-- Database: BookStore_24162124
-- Chạy trên SQL Server Express (GIATHUAN\SQLEXPRESS)
-- ============================================================

-- Bảng orders: lưu thông tin đơn hàng
IF NOT EXISTS (
    SELECT 1 FROM sys.tables WHERE name = 'orders' AND type = 'U'
)
BEGIN
    CREATE TABLE orders (
        id               INT            IDENTITY(1,1) PRIMARY KEY,
        user_id          INT            NOT NULL
                                        REFERENCES users(id) ON DELETE NO ACTION,
        total_price      DECIMAL(18,2)  NOT NULL,
        status           NVARCHAR(50)   NOT NULL DEFAULT N'đơn hàng mới',
        shipping_name    NVARCHAR(255)  NOT NULL,
        shipping_phone   NVARCHAR(20)   NOT NULL,
        shipping_address NVARCHAR(500)  NOT NULL,
        note             NVARCHAR(500)  NULL,
        created_at       DATETIME       NOT NULL DEFAULT GETDATE()
    );
    PRINT 'Da tao bang orders.';
END
ELSE
    PRINT 'Bang orders da ton tai, bo qua.';

-- Bảng order_items: lưu chi tiết từng dòng sản phẩm trong đơn hàng
IF NOT EXISTS (
    SELECT 1 FROM sys.tables WHERE name = 'order_items' AND type = 'U'
)
BEGIN
    CREATE TABLE order_items (
        id        INT            IDENTITY(1,1) PRIMARY KEY,
        order_id  INT            NOT NULL
                                 REFERENCES orders(id) ON DELETE CASCADE,
        book_id   INT            NOT NULL
                                 REFERENCES books(bookid) ON DELETE NO ACTION,
        quantity  INT            NOT NULL CHECK (quantity > 0),
        price     DECIMAL(18,2)  NOT NULL CHECK (price >= 0)
    );
    PRINT 'Da tao bang order_items.';
END
ELSE
    PRINT 'Bang order_items da ton tai, bo qua.';
