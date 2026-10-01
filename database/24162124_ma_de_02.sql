/* DE THI SO 02 - MSSV 24162124
   Chay script bang tai khoan co quyen CREATE DATABASE tren SQL Server.
*/

USE master;
GO

IF DB_ID(N'BookStore_24162124') IS NULL
BEGIN
    CREATE DATABASE BookStore_24162124;
END;
GO

USE BookStore_24162124;
GO

IF OBJECT_ID(N'dbo.rating', N'U') IS NOT NULL DROP TABLE dbo.rating;
IF OBJECT_ID(N'dbo.book_author', N'U') IS NOT NULL DROP TABLE dbo.book_author;
IF OBJECT_ID(N'dbo.author', N'U') IS NOT NULL DROP TABLE dbo.author;
IF OBJECT_ID(N'dbo.books', N'U') IS NOT NULL DROP TABLE dbo.books;
IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL DROP TABLE dbo.users;
GO

CREATE TABLE dbo.users (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    email       VARCHAR(50) NOT NULL UNIQUE,
    fullname    NVARCHAR(50),
    phone       INT,
    passwd      VARCHAR(32) NOT NULL,
    signup_date DATETIME DEFAULT GETDATE(),
    last_login  DATETIME,
    is_admin    BIT NOT NULL DEFAULT 0
);

CREATE TABLE dbo.books (
    bookid       INT IDENTITY(1,1) PRIMARY KEY,
    isbn         INT,
    title        VARCHAR(200),
    publisher    VARCHAR(100),
    price        DECIMAL(6,2),
    description  TEXT,
    publish_date DATE,
    cover_image  VARCHAR(100),
    quantity     INT
);

CREATE TABLE dbo.author (
    author_id      INT IDENTITY(1,1) PRIMARY KEY,
    author_name    VARCHAR(100),
    date_of_birth  DATE
);

CREATE TABLE dbo.book_author (
    bookid   INT NOT NULL,
    author_id INT NOT NULL,
    CONSTRAINT PK_book_author PRIMARY KEY (bookid, author_id),
    CONSTRAINT FK_book_author_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid),
    CONSTRAINT FK_book_author_author FOREIGN KEY (author_id) REFERENCES dbo.author(author_id)
);

CREATE TABLE dbo.rating (
    userid      INT NOT NULL,
    bookid      INT NOT NULL,
    rating      TINYINT,
    review_text TEXT,
    CONSTRAINT PK_rating PRIMARY KEY (userid, bookid),
    CONSTRAINT FK_rating_users FOREIGN KEY (userid) REFERENCES dbo.users(id),
    CONSTRAINT FK_rating_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid),
    CONSTRAINT CK_rating_value CHECK (rating BETWEEN 1 AND 5)
);
GO

INSERT INTO dbo.users (email, fullname, phone, passwd, signup_date, last_login, is_admin) VALUES
('admin@bookstore.vn', N'Quan tri vien', 901112223, 'admin123', GETDATE(), NULL, 1),
('user@bookstore.vn', N'Nguyen Van User', 908887776, 'user123', GETDATE(), NULL, 0);

INSERT INTO dbo.author (author_name, date_of_birth) VALUES
('Nguyen Nhat Anh', '1955-05-07'),
('Nam Cao', '1915-10-29'),
('To Hoai', '1920-09-27'),
('J. K. Rowling', '1965-07-31');

INSERT INTO dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity) VALUES
(100001, 'Cho toi xin mot ve di tuoi tho', 'Tre', 8.50, 'Tac pham ve ky uc tuoi tho.', '2008-01-01', 'book-01.jpg', 20),
(100002, 'Mat biec', 'Tre', 7.90, 'Cau chuyen tinh yeu trong sang.', '1990-01-01', 'book-02.jpg', 15),
(100003, 'Toi thay hoa vang tren co xanh', 'Tre', 9.50, 'Cau chuyen tuoi tho o lang que.', '2010-01-01', 'book-03.jpg', 18),
(100004, 'Chi Pheo', 'Van Hoc', 5.20, 'Tac pham hien thuc Viet Nam.', '1941-01-01', 'book-04.jpg', 12),
(100005, 'Lao Hac', 'Van Hoc', 4.80, 'Truyen ngan cua Nam Cao.', '1943-01-01', 'book-05.jpg', 10),
(100006, 'De Men phieu luu ky', 'Kim Dong', 6.75, 'Cuoc phieu luu cua De Men.', '1941-01-01', 'book-06.jpg', 25),
(100007, 'Vo chong A Phu', 'Giao Duc', 5.90, 'Tac pham van hoc Viet Nam.', '1952-01-01', 'book-07.jpg', 14),
(100008, 'Harry Potter 1', 'Bloomsbury', 12.50, 'Harry Potter va hon da phu thuy.', '1997-06-26', 'book-08.jpg', 30),
(100009, 'Harry Potter 2', 'Bloomsbury', 13.00, 'Harry Potter va phong chua bi mat.', '1998-07-02', 'book-09.jpg', 22),
(100010, 'Harry Potter 3', 'Bloomsbury', 13.50, 'Harry Potter va tu nhan nguc Azkaban.', '1999-07-08', 'book-10.jpg', 16),
(100011, 'Kinh van hoa', 'Kim Dong', 8.25, 'Bo truyen dai tap danh cho tuoi moi lon.', '1995-01-01', 'book-11.jpg', 19),
(100012, 'Dat rung phuong Nam', 'Kim Dong', 7.50, 'Hanh trinh tai mien Tay Nam Bo.', '1957-01-01', 'book-12.jpg', 13);

INSERT INTO dbo.book_author (bookid, author_id) VALUES
(1,1), (2,1), (3,1), (4,2), (5,2), (6,3), (7,3),
(8,4), (9,4), (10,4), (11,1), (12,3),
-- Sach bookid = 1 co hai tac gia de kiem tra JOIN khong lam duplicate sach.
(1,3);

INSERT INTO dbo.rating (userid, bookid, rating, review_text) VALUES
(1,1,5,'Noi dung gan gui va nhieu cam xuc.'),
(1,6,4,'Tac pham thieu nhi rat hay.'),
(2,1,5,'Mot cuon sach dang doc.'),
(2,2,4,'Cau chuyen cam dong.'),
(2,8,5,'The gioi phep thuat hap dan.');
GO

SELECT COUNT(*) AS total_users FROM dbo.users;
SELECT COUNT(*) AS total_authors FROM dbo.author;
SELECT COUNT(*) AS total_books FROM dbo.books;
SELECT COUNT(*) AS total_ratings FROM dbo.rating;
GO
