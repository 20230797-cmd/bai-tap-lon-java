-- ============================================================
-- DATABASE SCHEMA & DỮ LIỆU ĐỒNG BỘ 100% - ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)
-- 4 Khoa chuyên ngành | 8 Lớp học | 120 Sinh viên chuẩn hóa đầy đủ
-- Mật khẩu đăng nhập mặc định: 123456
-- ============================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ql_canhbao_hocvu')
BEGIN
    CREATE DATABASE ql_canhbao_hocvu COLLATE Vietnamese_CI_AS;
END
GO

USE ql_canhbao_hocvu;
GO

IF OBJECT_ID('dbo.diem_danh', 'U') IS NOT NULL DROP TABLE dbo.diem_danh;
IF OBJECT_ID('dbo.lich_giang_day', 'U') IS NOT NULL DROP TABLE dbo.lich_giang_day;
IF OBJECT_ID('dbo.thong_bao', 'U') IS NOT NULL DROP TABLE dbo.thong_bao;
IF OBJECT_ID('dbo.nhat_ky_tu_van', 'U') IS NOT NULL DROP TABLE dbo.nhat_ky_tu_van;
IF OBJECT_ID('dbo.canh_bao_hoc_vu', 'U') IS NOT NULL DROP TABLE dbo.canh_bao_hoc_vu;
IF OBJECT_ID('dbo.ket_qua_hoc_tap', 'U') IS NOT NULL DROP TABLE dbo.ket_qua_hoc_tap;
IF OBJECT_ID('dbo.sinh_vien', 'U') IS NOT NULL DROP TABLE dbo.sinh_vien;
IF OBJECT_ID('dbo.lop_hoc', 'U') IS NOT NULL DROP TABLE dbo.lop_hoc;
IF OBJECT_ID('dbo.co_van_hoc_tap', 'U') IS NOT NULL DROP TABLE dbo.co_van_hoc_tap;
IF OBJECT_ID('dbo.tai_khoan', 'U') IS NOT NULL DROP TABLE dbo.tai_khoan;
GO

CREATE TABLE co_van_hoc_tap (
    ma_cvht VARCHAR(20) PRIMARY KEY,
    ho_ten NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(20),
    khoa NVARCHAR(100) NOT NULL
);
GO

CREATE TABLE lop_hoc (
    ma_lop VARCHAR(20) PRIMARY KEY,
    ten_lop NVARCHAR(100) NOT NULL,
    khoa NVARCHAR(100) NOT NULL,
    khoa_hoc INT NOT NULL,
    ma_cvht VARCHAR(20),
    CONSTRAINT fk_lop_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht) ON DELETE SET NULL
);
GO

CREATE TABLE sinh_vien (
    ma_sv VARCHAR(20) PRIMARY KEY,
    ho_ten NVARCHAR(100) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh NVARCHAR(10),
    email VARCHAR(100),
    so_dien_thoai VARCHAR(20),
    ma_lop VARCHAR(20) NOT NULL,
    trang_thai VARCHAR(30) DEFAULT 'DANG_HOC',
    CONSTRAINT fk_sinhvien_lop FOREIGN KEY (ma_lop) REFERENCES lop_hoc(ma_lop) ON DELETE CASCADE
);
GO

CREATE TABLE ket_qua_hoc_tap (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_sv VARCHAR(20) NOT NULL,
    hoc_ky INT NOT NULL,
    nam_hoc VARCHAR(20) NOT NULL,
    gpa_hoc_ky FLOAT NOT NULL,
    gpa_tich_luy FLOAT NOT NULL,
    so_tin_chi_no INT DEFAULT 0,
    CONSTRAINT fk_kq_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE
);
GO

CREATE TABLE canh_bao_hoc_vu (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_canh_bao VARCHAR(50) UNIQUE NOT NULL,
    ma_sv VARCHAR(20) NOT NULL,
    hoc_ky INT NOT NULL,
    nam_hoc VARCHAR(20) NOT NULL,
    muc_canh_bao VARCHAR(30) NOT NULL,
    gpa_xet_duyet FLOAT NOT NULL,
    ly_do NVARCHAR(255),
    ngay_quyet_dinh DATE,
    trang_thai_tu_van VARCHAR(30) DEFAULT 'CHUA_TU_VAN',
    CONSTRAINT fk_canhbao_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE
);
GO

CREATE TABLE nhat_ky_tu_van (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_sv VARCHAR(20) NOT NULL,
    ma_cvht VARCHAR(20) NOT NULL,
    id_canh_bao INT,
    ngay_tu_van DATE NOT NULL,
    hinh_thuc NVARCHAR(50) NOT NULL,
    noi_dung NVARCHAR(MAX) NOT NULL,
    nguyen_nhan NVARCHAR(MAX),
    giai_phap NVARCHAR(MAX),
    cam_ket_sinh_vien NVARCHAR(MAX),
    CONSTRAINT fk_nhatky_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv),
    CONSTRAINT fk_nhatky_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht),
    CONSTRAINT fk_nhatky_canhbao FOREIGN KEY (id_canh_bao) REFERENCES canh_bao_hoc_vu(id) ON DELETE SET NULL
);
GO

CREATE TABLE tai_khoan (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    email VARCHAR(100),
    vai_tro VARCHAR(20) NOT NULL,
    ma_ref VARCHAR(20),
    trang_thai INT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE thong_bao (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_thong_bao VARCHAR(50) UNIQUE NOT NULL,
    tieu_de NVARCHAR(255) NOT NULL,
    noi_dung NVARCHAR(MAX) NOT NULL,
    nhom_rui_ro VARCHAR(30) DEFAULT 'ALL',
    ma_lop VARCHAR(20) DEFAULT 'ALL',
    ma_sv VARCHAR(20),
    ngay_gui DATETIME DEFAULT GETDATE(),
    nguoi_gui NVARCHAR(100),
    so_luong_nhan INT DEFAULT 0,
    trang_thai VARCHAR(30) DEFAULT 'DA_GUI'
);
GO

CREATE TABLE lich_giang_day (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_cvht VARCHAR(20) NOT NULL,
    ten_cvht NVARCHAR(100) NOT NULL,
    ma_lop VARCHAR(20) NOT NULL,
    ten_lop NVARCHAR(100) NOT NULL,
    tieu_de NVARCHAR(200) NOT NULL,
    ngay DATE NOT NULL,
    gio_bat_dau VARCHAR(10) NOT NULL,
    gio_ket_thuc VARCHAR(10) NOT NULL,
    dia_diem NVARCHAR(100) NOT NULL,
    hinh_thuc NVARCHAR(50) NOT NULL,
    loai_buoi NVARCHAR(50) NOT NULL,
    trang_thai NVARCHAR(50) NOT NULL,
    ghi_chu NVARCHAR(MAX)
);
GO

CREATE TABLE diem_danh (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_lich INT NOT NULL,
    ma_sv VARCHAR(20) NOT NULL,
    ngay_diem_danh DATE NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    ghi_chu NVARCHAR(255),
    CONSTRAINT fk_diemdanh_lich FOREIGN KEY (id_lich) REFERENCES lich_giang_day(id) ON DELETE CASCADE,
    CONSTRAINT fk_diemdanh_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE
);
GO

-- 1. CỐ VẤN HỌC TẬP
INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES ('CV001', N'TS. Đinh Văn Phong', 'phong.dv@eaut.edu.vn', '0912345678', N'Công nghệ thông tin');
INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES ('CV002', N'PGS.TS. Nguyễn Thanh Hải', 'hai.nt@eaut.edu.vn', '0987654321', N'Công nghệ kỹ thuật Ô tô');
INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES ('CV003', N'ThS. Hoàng Thị Mai', 'mai.ht@eaut.edu.vn', '0934567890', N'Quản trị kinh doanh');
INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES ('CV004', N'TS. Vũ Trường Sơn', 'son.vt@eaut.edu.vn', '0945678901', N'Công nghệ kỹ thuật Điện - Điện tử');
GO

-- 2. LỚP HỌC
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCCTPM14A', N'DCCTPM14A - Công nghệ phần mềm K14', N'Công nghệ thông tin', 2023, 'CV001');
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCCNTT14B', N'DCCNTT14B - Công nghệ thông tin K14B', N'Công nghệ thông tin', 2023, 'CV001');
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCOTO14A', N'DCOTO14A - Công nghệ kỹ thuật Ô tô 14A', N'Công nghệ kỹ thuật Ô tô', 2023, 'CV002');
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCOTO14B', N'DCOTO14B - Công nghệ kỹ thuật Ô tô 14B', N'Công nghệ kỹ thuật Ô tô', 2023, 'CV002');
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCQTKD14A', N'DCQTKD14A - Quản trị kinh doanh 14A', N'Quản trị kinh doanh', 2023, 'CV003');
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCQTKD14B', N'DCQTKD14B - Quản trị kinh doanh 14B', N'Quản trị kinh doanh', 2023, 'CV003');
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCDDT14A', N'DCDDT14A - Kỹ thuật Điện - Điện tử 14A', N'Công nghệ kỹ thuật Điện - Điện tử', 2023, 'CV004');
INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('DCDDT14B', N'DCDDT14B - Tự động hóa K14B', N'Công nghệ kỹ thuật Điện - Điện tử', 2023, 'CV004');
GO

-- 3. SINH VIÊN (120 SINH VIÊN ĐỒNG BỘ)
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230001', N'Vũ Đình Anh', '2005-04-06', N'Nam', 'sv20230001@eaut.edu.vn', '0911000001', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230002', N'Dương Gia Bảo', '2005-07-11', N'Nam', 'sv20230002@eaut.edu.vn', '0911000002', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230003', N'Huỳnh Diệu Linh', '2005-10-16', N'Nữ', 'sv20230003@eaut.edu.vn', '0911000003', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230004', N'Hồ Hữu Quân', '2005-01-21', N'Nam', 'sv20230004@eaut.edu.vn', '0911000004', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230005', N'Phạm Đức Hiếu', '2005-04-26', N'Nam', 'sv20230005@eaut.edu.vn', '0911000005', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230006', N'Bùi Ngọc Yến', '2005-07-03', N'Nữ', 'sv20230006@eaut.edu.vn', '0911000006', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230007', N'Trần Trọng Tú', '2005-10-08', N'Nam', 'sv20230007@eaut.edu.vn', '0911000007', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230008', N'Võ Quang Phúc', '2005-01-13', N'Nam', 'sv20230008@eaut.edu.vn', '0911000008', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230009', N'Lý Thùy Vân', '2005-04-18', N'Nữ', 'sv20230009@eaut.edu.vn', '0911000009', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230010', N'Phan Hoàng Tuấn', '2005-07-23', N'Nam', 'sv20230010@eaut.edu.vn', '0911000010', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230011', N'Ngô Thanh Hưng', '2005-10-28', N'Nam', 'sv20230011@eaut.edu.vn', '0911000011', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230012', N'Hoàng Thị Giang', '2005-01-05', N'Nữ', 'sv20230012@eaut.edu.vn', '0911000012', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230013', N'Đỗ Đình Khánh', '2005-04-10', N'Nam', 'sv20230013@eaut.edu.vn', '0911000013', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230014', N'Lê Gia Cường', '2005-07-15', N'Nam', 'sv20230014@eaut.edu.vn', '0911000014', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230015', N'Đặng Diệu Nhi', '2005-10-20', N'Nữ', 'sv20230015@eaut.edu.vn', '0911000015', 'DCCTPM14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230016', N'Nguyễn Hữu Bách', '2005-01-25', N'Nam', 'sv20230016@eaut.edu.vn', '0911000016', 'DCCNTT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230017', N'Vũ Đức Thịnh', '2005-04-02', N'Nam', 'sv20230017@eaut.edu.vn', '0911000017', 'DCCNTT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230018', N'Dương Ngọc Vy', '2005-07-07', N'Nữ', 'sv20230018@eaut.edu.vn', '0911000018', 'DCCNTT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230019', N'Huỳnh Trọng Hải', '2005-10-12', N'Nam', 'sv20230019@eaut.edu.vn', '0911000019', 'DCCNTT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230020', N'Hồ Quang Khởi', '2005-01-17', N'Nam', 'sv20230020@eaut.edu.vn', '0911000020', 'DCCNTT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230021', N'Phạm Thùy Thảo', '2005-04-22', N'Nữ', 'sv20230021@eaut.edu.vn', '0911000021', 'DCCNTT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230022', N'Bùi Hoàng Duy', '2005-07-27', N'Nam', 'sv20230022@eaut.edu.vn', '0911000022', 'DCCNTT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230023', N'Trần Thanh Trí', '2005-10-04', N'Nam', 'sv20230023@eaut.edu.vn', '0911000023', 'DCCNTT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230024', N'Võ Thị Mai', '2005-01-09', N'Nữ', 'sv20230024@eaut.edu.vn', '0911000024', 'DCCNTT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230025', N'Lý Đình Đạt', '2005-04-14', N'Nam', 'sv20230025@eaut.edu.vn', '0911000025', 'DCCNTT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230026', N'Phan Gia Nam', '2005-07-19', N'Nam', 'sv20230026@eaut.edu.vn', '0911000026', 'DCCNTT14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230027', N'Ngô Diệu Quyên', '2005-10-24', N'Nữ', 'sv20230027@eaut.edu.vn', '0911000027', 'DCCNTT14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230028', N'Hoàng Hữu Bảo', '2005-01-01', N'Nam', 'sv20230028@eaut.edu.vn', '0911000028', 'DCCNTT14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230029', N'Đỗ Đức Kiệt', '2005-04-06', N'Nam', 'sv20230029@eaut.edu.vn', '0911000029', 'DCCNTT14B', 'BUOC_THOI_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230030', N'Lê Ngọc Lan', '2005-07-11', N'Nữ', 'sv20230030@eaut.edu.vn', '0911000030', 'DCCNTT14B', 'BUOC_THOI_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230101', N'Vũ Đình Anh', '2005-04-06', N'Nam', 'sv20230101@eaut.edu.vn', '0911000101', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230102', N'Dương Gia Bảo', '2005-07-11', N'Nam', 'sv20230102@eaut.edu.vn', '0911000102', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230103', N'Huỳnh Diệu Linh', '2005-10-16', N'Nữ', 'sv20230103@eaut.edu.vn', '0911000103', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230104', N'Hồ Hữu Quân', '2005-01-21', N'Nam', 'sv20230104@eaut.edu.vn', '0911000104', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230105', N'Phạm Đức Hiếu', '2005-04-26', N'Nam', 'sv20230105@eaut.edu.vn', '0911000105', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230106', N'Bùi Ngọc Yến', '2005-07-03', N'Nữ', 'sv20230106@eaut.edu.vn', '0911000106', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230107', N'Trần Trọng Tú', '2005-10-08', N'Nam', 'sv20230107@eaut.edu.vn', '0911000107', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230108', N'Võ Quang Phúc', '2005-01-13', N'Nam', 'sv20230108@eaut.edu.vn', '0911000108', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230109', N'Lý Thùy Vân', '2005-04-18', N'Nữ', 'sv20230109@eaut.edu.vn', '0911000109', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230110', N'Phan Hoàng Tuấn', '2005-07-23', N'Nam', 'sv20230110@eaut.edu.vn', '0911000110', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230111', N'Ngô Thanh Hưng', '2005-10-28', N'Nam', 'sv20230111@eaut.edu.vn', '0911000111', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230112', N'Hoàng Thị Giang', '2005-01-05', N'Nữ', 'sv20230112@eaut.edu.vn', '0911000112', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230113', N'Đỗ Đình Khánh', '2005-04-10', N'Nam', 'sv20230113@eaut.edu.vn', '0911000113', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230114', N'Lê Gia Cường', '2005-07-15', N'Nam', 'sv20230114@eaut.edu.vn', '0911000114', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230115', N'Đặng Diệu Nhi', '2005-10-20', N'Nữ', 'sv20230115@eaut.edu.vn', '0911000115', 'DCOTO14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230116', N'Nguyễn Hữu Bách', '2005-01-25', N'Nam', 'sv20230116@eaut.edu.vn', '0911000116', 'DCOTO14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230117', N'Vũ Đức Thịnh', '2005-04-02', N'Nam', 'sv20230117@eaut.edu.vn', '0911000117', 'DCOTO14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230118', N'Dương Ngọc Vy', '2005-07-07', N'Nữ', 'sv20230118@eaut.edu.vn', '0911000118', 'DCOTO14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230119', N'Huỳnh Trọng Hải', '2005-10-12', N'Nam', 'sv20230119@eaut.edu.vn', '0911000119', 'DCOTO14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230120', N'Hồ Quang Khởi', '2005-01-17', N'Nam', 'sv20230120@eaut.edu.vn', '0911000120', 'DCOTO14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230121', N'Phạm Thùy Thảo', '2005-04-22', N'Nữ', 'sv20230121@eaut.edu.vn', '0911000121', 'DCOTO14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230122', N'Bùi Hoàng Duy', '2005-07-27', N'Nam', 'sv20230122@eaut.edu.vn', '0911000122', 'DCOTO14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230123', N'Trần Thanh Trí', '2005-10-04', N'Nam', 'sv20230123@eaut.edu.vn', '0911000123', 'DCOTO14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230124', N'Võ Thị Mai', '2005-01-09', N'Nữ', 'sv20230124@eaut.edu.vn', '0911000124', 'DCOTO14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230125', N'Lý Đình Đạt', '2005-04-14', N'Nam', 'sv20230125@eaut.edu.vn', '0911000125', 'DCOTO14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230126', N'Phan Gia Nam', '2005-07-19', N'Nam', 'sv20230126@eaut.edu.vn', '0911000126', 'DCOTO14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230127', N'Ngô Diệu Quyên', '2005-10-24', N'Nữ', 'sv20230127@eaut.edu.vn', '0911000127', 'DCOTO14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230128', N'Hoàng Hữu Bảo', '2005-01-01', N'Nam', 'sv20230128@eaut.edu.vn', '0911000128', 'DCOTO14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230129', N'Đỗ Đức Kiệt', '2005-04-06', N'Nam', 'sv20230129@eaut.edu.vn', '0911000129', 'DCOTO14B', 'BUOC_THOI_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230130', N'Lê Ngọc Lan', '2005-07-11', N'Nữ', 'sv20230130@eaut.edu.vn', '0911000130', 'DCOTO14B', 'BUOC_THOI_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230201', N'Vũ Đình Anh', '2005-04-06', N'Nam', 'sv20230201@eaut.edu.vn', '0911000201', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230202', N'Dương Gia Bảo', '2005-07-11', N'Nam', 'sv20230202@eaut.edu.vn', '0911000202', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230203', N'Huỳnh Diệu Linh', '2005-10-16', N'Nữ', 'sv20230203@eaut.edu.vn', '0911000203', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230204', N'Hồ Hữu Quân', '2005-01-21', N'Nam', 'sv20230204@eaut.edu.vn', '0911000204', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230205', N'Phạm Đức Hiếu', '2005-04-26', N'Nam', 'sv20230205@eaut.edu.vn', '0911000205', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230206', N'Bùi Ngọc Yến', '2005-07-03', N'Nữ', 'sv20230206@eaut.edu.vn', '0911000206', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230207', N'Trần Trọng Tú', '2005-10-08', N'Nam', 'sv20230207@eaut.edu.vn', '0911000207', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230208', N'Võ Quang Phúc', '2005-01-13', N'Nam', 'sv20230208@eaut.edu.vn', '0911000208', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230209', N'Lý Thùy Vân', '2005-04-18', N'Nữ', 'sv20230209@eaut.edu.vn', '0911000209', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230210', N'Phan Hoàng Tuấn', '2005-07-23', N'Nam', 'sv20230210@eaut.edu.vn', '0911000210', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230211', N'Ngô Thanh Hưng', '2005-10-28', N'Nam', 'sv20230211@eaut.edu.vn', '0911000211', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230212', N'Hoàng Thị Giang', '2005-01-05', N'Nữ', 'sv20230212@eaut.edu.vn', '0911000212', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230213', N'Đỗ Đình Khánh', '2005-04-10', N'Nam', 'sv20230213@eaut.edu.vn', '0911000213', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230214', N'Lê Gia Cường', '2005-07-15', N'Nam', 'sv20230214@eaut.edu.vn', '0911000214', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230215', N'Đặng Diệu Nhi', '2005-10-20', N'Nữ', 'sv20230215@eaut.edu.vn', '0911000215', 'DCQTKD14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230216', N'Nguyễn Hữu Bách', '2005-01-25', N'Nam', 'sv20230216@eaut.edu.vn', '0911000216', 'DCQTKD14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230217', N'Vũ Đức Thịnh', '2005-04-02', N'Nam', 'sv20230217@eaut.edu.vn', '0911000217', 'DCQTKD14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230218', N'Dương Ngọc Vy', '2005-07-07', N'Nữ', 'sv20230218@eaut.edu.vn', '0911000218', 'DCQTKD14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230219', N'Huỳnh Trọng Hải', '2005-10-12', N'Nam', 'sv20230219@eaut.edu.vn', '0911000219', 'DCQTKD14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230220', N'Hồ Quang Khởi', '2005-01-17', N'Nam', 'sv20230220@eaut.edu.vn', '0911000220', 'DCQTKD14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230221', N'Phạm Thùy Thảo', '2005-04-22', N'Nữ', 'sv20230221@eaut.edu.vn', '0911000221', 'DCQTKD14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230222', N'Bùi Hoàng Duy', '2005-07-27', N'Nam', 'sv20230222@eaut.edu.vn', '0911000222', 'DCQTKD14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230223', N'Trần Thanh Trí', '2005-10-04', N'Nam', 'sv20230223@eaut.edu.vn', '0911000223', 'DCQTKD14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230224', N'Võ Thị Mai', '2005-01-09', N'Nữ', 'sv20230224@eaut.edu.vn', '0911000224', 'DCQTKD14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230225', N'Lý Đình Đạt', '2005-04-14', N'Nam', 'sv20230225@eaut.edu.vn', '0911000225', 'DCQTKD14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230226', N'Phan Gia Nam', '2005-07-19', N'Nam', 'sv20230226@eaut.edu.vn', '0911000226', 'DCQTKD14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230227', N'Ngô Diệu Quyên', '2005-10-24', N'Nữ', 'sv20230227@eaut.edu.vn', '0911000227', 'DCQTKD14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230228', N'Hoàng Hữu Bảo', '2005-01-01', N'Nam', 'sv20230228@eaut.edu.vn', '0911000228', 'DCQTKD14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230229', N'Đỗ Đức Kiệt', '2005-04-06', N'Nam', 'sv20230229@eaut.edu.vn', '0911000229', 'DCQTKD14B', 'BUOC_THOI_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230230', N'Lê Ngọc Lan', '2005-07-11', N'Nữ', 'sv20230230@eaut.edu.vn', '0911000230', 'DCQTKD14B', 'BUOC_THOI_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230301', N'Vũ Đình Anh', '2005-04-06', N'Nam', 'sv20230301@eaut.edu.vn', '0911000301', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230302', N'Dương Gia Bảo', '2005-07-11', N'Nam', 'sv20230302@eaut.edu.vn', '0911000302', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230303', N'Huỳnh Diệu Linh', '2005-10-16', N'Nữ', 'sv20230303@eaut.edu.vn', '0911000303', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230304', N'Hồ Hữu Quân', '2005-01-21', N'Nam', 'sv20230304@eaut.edu.vn', '0911000304', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230305', N'Phạm Đức Hiếu', '2005-04-26', N'Nam', 'sv20230305@eaut.edu.vn', '0911000305', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230306', N'Bùi Ngọc Yến', '2005-07-03', N'Nữ', 'sv20230306@eaut.edu.vn', '0911000306', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230307', N'Trần Trọng Tú', '2005-10-08', N'Nam', 'sv20230307@eaut.edu.vn', '0911000307', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230308', N'Võ Quang Phúc', '2005-01-13', N'Nam', 'sv20230308@eaut.edu.vn', '0911000308', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230309', N'Lý Thùy Vân', '2005-04-18', N'Nữ', 'sv20230309@eaut.edu.vn', '0911000309', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230310', N'Phan Hoàng Tuấn', '2005-07-23', N'Nam', 'sv20230310@eaut.edu.vn', '0911000310', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230311', N'Ngô Thanh Hưng', '2005-10-28', N'Nam', 'sv20230311@eaut.edu.vn', '0911000311', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230312', N'Hoàng Thị Giang', '2005-01-05', N'Nữ', 'sv20230312@eaut.edu.vn', '0911000312', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230313', N'Đỗ Đình Khánh', '2005-04-10', N'Nam', 'sv20230313@eaut.edu.vn', '0911000313', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230314', N'Lê Gia Cường', '2005-07-15', N'Nam', 'sv20230314@eaut.edu.vn', '0911000314', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230315', N'Đặng Diệu Nhi', '2005-10-20', N'Nữ', 'sv20230315@eaut.edu.vn', '0911000315', 'DCDDT14A', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230316', N'Nguyễn Hữu Bách', '2005-01-25', N'Nam', 'sv20230316@eaut.edu.vn', '0911000316', 'DCDDT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230317', N'Vũ Đức Thịnh', '2005-04-02', N'Nam', 'sv20230317@eaut.edu.vn', '0911000317', 'DCDDT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230318', N'Dương Ngọc Vy', '2005-07-07', N'Nữ', 'sv20230318@eaut.edu.vn', '0911000318', 'DCDDT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230319', N'Huỳnh Trọng Hải', '2005-10-12', N'Nam', 'sv20230319@eaut.edu.vn', '0911000319', 'DCDDT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230320', N'Hồ Quang Khởi', '2005-01-17', N'Nam', 'sv20230320@eaut.edu.vn', '0911000320', 'DCDDT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230321', N'Phạm Thùy Thảo', '2005-04-22', N'Nữ', 'sv20230321@eaut.edu.vn', '0911000321', 'DCDDT14B', 'DANG_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230322', N'Bùi Hoàng Duy', '2005-07-27', N'Nam', 'sv20230322@eaut.edu.vn', '0911000322', 'DCDDT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230323', N'Trần Thanh Trí', '2005-10-04', N'Nam', 'sv20230323@eaut.edu.vn', '0911000323', 'DCDDT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230324', N'Võ Thị Mai', '2005-01-09', N'Nữ', 'sv20230324@eaut.edu.vn', '0911000324', 'DCDDT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230325', N'Lý Đình Đạt', '2005-04-14', N'Nam', 'sv20230325@eaut.edu.vn', '0911000325', 'DCDDT14B', 'CANH_BAO_1');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230326', N'Phan Gia Nam', '2005-07-19', N'Nam', 'sv20230326@eaut.edu.vn', '0911000326', 'DCDDT14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230327', N'Ngô Diệu Quyên', '2005-10-24', N'Nữ', 'sv20230327@eaut.edu.vn', '0911000327', 'DCDDT14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230328', N'Hoàng Hữu Bảo', '2005-01-01', N'Nam', 'sv20230328@eaut.edu.vn', '0911000328', 'DCDDT14B', 'CANH_BAO_2');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230329', N'Đỗ Đức Kiệt', '2005-04-06', N'Nam', 'sv20230329@eaut.edu.vn', '0911000329', 'DCDDT14B', 'BUOC_THOI_HOC');
INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('20230330', N'Lê Ngọc Lan', '2005-07-11', N'Nữ', 'sv20230330@eaut.edu.vn', '0911000330', 'DCDDT14B', 'BUOC_THOI_HOC');
GO

-- 4. KẾT QUẢ HỌC TẬP (240 BẢN GHI ĐIỂM ĐỒNG BỘ)
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230001', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230001', 2, '2023-2024', 3.51, 3.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230002', 1, '2023-2024', 3.44, 3.44, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230002', 2, '2023-2024', 3.62, 3.53, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230003', 1, '2023-2024', 3.56, 3.56, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230003', 2, '2023-2024', 3.73, 3.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230004', 1, '2023-2024', 3.68, 3.68, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230004', 2, '2023-2024', 3.4, 3.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230005', 1, '2023-2024', 3.2, 3.2, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230005', 2, '2023-2024', 3.51, 3.35, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230006', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230006', 2, '2023-2024', 3.62, 3.47, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230007', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230007', 2, '2023-2024', 2.51, 2.4, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230008', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230008', 2, '2023-2024', 2.62, 2.52, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230009', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230009', 2, '2023-2024', 2.73, 2.63, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230010', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230010', 2, '2023-2024', 2.84, 2.75, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230011', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230011', 2, '2023-2024', 2.95, 2.87, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230012', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230012', 2, '2023-2024', 2.4, 2.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230013', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230013', 2, '2023-2024', 2.51, 2.76, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230014', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230014', 2, '2023-2024', 2.62, 2.46, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230015', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230015', 2, '2023-2024', 2.73, 2.58, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230016', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230016', 2, '2023-2024', 2.84, 2.69, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230017', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230017', 2, '2023-2024', 2.95, 2.81, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230018', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230018', 2, '2023-2024', 2.4, 2.59, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230019', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230019', 2, '2023-2024', 2.51, 2.71, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230020', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230020', 2, '2023-2024', 2.62, 2.82, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230021', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230021', 2, '2023-2024', 2.73, 2.51, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230022', 1, '2023-2024', 2.05, 2.05, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230022', 2, '2023-2024', 1.75, 1.9, 4);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230023', 1, '2023-2024', 2.0, 2.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230023', 2, '2023-2024', 1.69, 1.84, 6);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230024', 1, '2023-2024', 1.95, 1.95, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230024', 2, '2023-2024', 1.63, 1.79, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230025', 1, '2023-2024', 1.9, 1.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230025', 2, '2023-2024', 1.57, 1.73, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230026', 1, '2023-2024', 1.7, 1.7, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230026', 2, '2023-2024', 1.35, 1.52, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230027', 1, '2023-2024', 1.62, 1.62, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230027', 2, '2023-2024', 1.28, 1.45, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230028', 1, '2023-2024', 1.54, 1.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230028', 2, '2023-2024', 1.21, 1.38, 12);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230029', 1, '2023-2024', 1.15, 1.15, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230029', 2, '2023-2024', 0.78, 0.96, 14);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230030', 1, '2023-2024', 1.0, 1.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230030', 2, '2023-2024', 0.68, 0.84, 17);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230101', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230101', 2, '2023-2024', 3.51, 3.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230102', 1, '2023-2024', 3.44, 3.44, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230102', 2, '2023-2024', 3.62, 3.53, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230103', 1, '2023-2024', 3.56, 3.56, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230103', 2, '2023-2024', 3.73, 3.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230104', 1, '2023-2024', 3.68, 3.68, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230104', 2, '2023-2024', 3.4, 3.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230105', 1, '2023-2024', 3.2, 3.2, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230105', 2, '2023-2024', 3.51, 3.35, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230106', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230106', 2, '2023-2024', 3.62, 3.47, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230107', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230107', 2, '2023-2024', 2.51, 2.4, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230108', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230108', 2, '2023-2024', 2.62, 2.52, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230109', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230109', 2, '2023-2024', 2.73, 2.63, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230110', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230110', 2, '2023-2024', 2.84, 2.75, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230111', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230111', 2, '2023-2024', 2.95, 2.87, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230112', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230112', 2, '2023-2024', 2.4, 2.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230113', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230113', 2, '2023-2024', 2.51, 2.76, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230114', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230114', 2, '2023-2024', 2.62, 2.46, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230115', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230115', 2, '2023-2024', 2.73, 2.58, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230116', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230116', 2, '2023-2024', 2.84, 2.69, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230117', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230117', 2, '2023-2024', 2.95, 2.81, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230118', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230118', 2, '2023-2024', 2.4, 2.59, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230119', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230119', 2, '2023-2024', 2.51, 2.71, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230120', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230120', 2, '2023-2024', 2.62, 2.82, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230121', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230121', 2, '2023-2024', 2.73, 2.51, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230122', 1, '2023-2024', 2.05, 2.05, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230122', 2, '2023-2024', 1.75, 1.9, 4);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230123', 1, '2023-2024', 2.0, 2.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230123', 2, '2023-2024', 1.69, 1.84, 6);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230124', 1, '2023-2024', 1.95, 1.95, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230124', 2, '2023-2024', 1.63, 1.79, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230125', 1, '2023-2024', 1.9, 1.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230125', 2, '2023-2024', 1.57, 1.73, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230126', 1, '2023-2024', 1.7, 1.7, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230126', 2, '2023-2024', 1.35, 1.52, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230127', 1, '2023-2024', 1.62, 1.62, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230127', 2, '2023-2024', 1.28, 1.45, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230128', 1, '2023-2024', 1.54, 1.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230128', 2, '2023-2024', 1.21, 1.38, 12);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230129', 1, '2023-2024', 1.15, 1.15, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230129', 2, '2023-2024', 0.78, 0.96, 14);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230130', 1, '2023-2024', 1.0, 1.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230130', 2, '2023-2024', 0.68, 0.84, 17);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230201', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230201', 2, '2023-2024', 3.51, 3.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230202', 1, '2023-2024', 3.44, 3.44, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230202', 2, '2023-2024', 3.62, 3.53, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230203', 1, '2023-2024', 3.56, 3.56, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230203', 2, '2023-2024', 3.73, 3.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230204', 1, '2023-2024', 3.68, 3.68, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230204', 2, '2023-2024', 3.4, 3.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230205', 1, '2023-2024', 3.2, 3.2, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230205', 2, '2023-2024', 3.51, 3.35, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230206', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230206', 2, '2023-2024', 3.62, 3.47, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230207', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230207', 2, '2023-2024', 2.51, 2.4, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230208', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230208', 2, '2023-2024', 2.62, 2.52, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230209', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230209', 2, '2023-2024', 2.73, 2.63, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230210', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230210', 2, '2023-2024', 2.84, 2.75, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230211', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230211', 2, '2023-2024', 2.95, 2.87, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230212', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230212', 2, '2023-2024', 2.4, 2.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230213', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230213', 2, '2023-2024', 2.51, 2.76, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230214', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230214', 2, '2023-2024', 2.62, 2.46, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230215', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230215', 2, '2023-2024', 2.73, 2.58, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230216', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230216', 2, '2023-2024', 2.84, 2.69, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230217', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230217', 2, '2023-2024', 2.95, 2.81, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230218', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230218', 2, '2023-2024', 2.4, 2.59, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230219', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230219', 2, '2023-2024', 2.51, 2.71, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230220', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230220', 2, '2023-2024', 2.62, 2.82, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230221', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230221', 2, '2023-2024', 2.73, 2.51, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230222', 1, '2023-2024', 2.05, 2.05, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230222', 2, '2023-2024', 1.75, 1.9, 4);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230223', 1, '2023-2024', 2.0, 2.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230223', 2, '2023-2024', 1.69, 1.84, 6);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230224', 1, '2023-2024', 1.95, 1.95, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230224', 2, '2023-2024', 1.63, 1.79, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230225', 1, '2023-2024', 1.9, 1.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230225', 2, '2023-2024', 1.57, 1.73, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230226', 1, '2023-2024', 1.7, 1.7, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230226', 2, '2023-2024', 1.35, 1.52, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230227', 1, '2023-2024', 1.62, 1.62, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230227', 2, '2023-2024', 1.28, 1.45, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230228', 1, '2023-2024', 1.54, 1.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230228', 2, '2023-2024', 1.21, 1.38, 12);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230229', 1, '2023-2024', 1.15, 1.15, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230229', 2, '2023-2024', 0.78, 0.96, 14);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230230', 1, '2023-2024', 1.0, 1.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230230', 2, '2023-2024', 0.68, 0.84, 17);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230301', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230301', 2, '2023-2024', 3.51, 3.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230302', 1, '2023-2024', 3.44, 3.44, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230302', 2, '2023-2024', 3.62, 3.53, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230303', 1, '2023-2024', 3.56, 3.56, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230303', 2, '2023-2024', 3.73, 3.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230304', 1, '2023-2024', 3.68, 3.68, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230304', 2, '2023-2024', 3.4, 3.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230305', 1, '2023-2024', 3.2, 3.2, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230305', 2, '2023-2024', 3.51, 3.35, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230306', 1, '2023-2024', 3.32, 3.32, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230306', 2, '2023-2024', 3.62, 3.47, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230307', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230307', 2, '2023-2024', 2.51, 2.4, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230308', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230308', 2, '2023-2024', 2.62, 2.52, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230309', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230309', 2, '2023-2024', 2.73, 2.63, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230310', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230310', 2, '2023-2024', 2.84, 2.75, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230311', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230311', 2, '2023-2024', 2.95, 2.87, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230312', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230312', 2, '2023-2024', 2.4, 2.65, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230313', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230313', 2, '2023-2024', 2.51, 2.76, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230314', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230314', 2, '2023-2024', 2.62, 2.46, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230315', 1, '2023-2024', 2.42, 2.42, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230315', 2, '2023-2024', 2.73, 2.58, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230316', 1, '2023-2024', 2.54, 2.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230316', 2, '2023-2024', 2.84, 2.69, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230317', 1, '2023-2024', 2.66, 2.66, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230317', 2, '2023-2024', 2.95, 2.81, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230318', 1, '2023-2024', 2.78, 2.78, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230318', 2, '2023-2024', 2.4, 2.59, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230319', 1, '2023-2024', 2.9, 2.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230319', 2, '2023-2024', 2.51, 2.71, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230320', 1, '2023-2024', 3.02, 3.02, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230320', 2, '2023-2024', 2.62, 2.82, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230321', 1, '2023-2024', 2.3, 2.3, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230321', 2, '2023-2024', 2.73, 2.51, 2);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230322', 1, '2023-2024', 2.05, 2.05, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230322', 2, '2023-2024', 1.75, 1.9, 4);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230323', 1, '2023-2024', 2.0, 2.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230323', 2, '2023-2024', 1.69, 1.84, 6);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230324', 1, '2023-2024', 1.95, 1.95, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230324', 2, '2023-2024', 1.63, 1.79, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230325', 1, '2023-2024', 1.9, 1.9, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230325', 2, '2023-2024', 1.57, 1.73, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230326', 1, '2023-2024', 1.7, 1.7, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230326', 2, '2023-2024', 1.35, 1.52, 8);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230327', 1, '2023-2024', 1.62, 1.62, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230327', 2, '2023-2024', 1.28, 1.45, 10);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230328', 1, '2023-2024', 1.54, 1.54, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230328', 2, '2023-2024', 1.21, 1.38, 12);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230329', 1, '2023-2024', 1.15, 1.15, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230329', 2, '2023-2024', 0.78, 0.96, 14);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230330', 1, '2023-2024', 1.0, 1.0, 0);
INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('20230330', 2, '2023-2024', 0.68, 0.84, 17);
GO

-- 5. CẢNH BÁO HỌC VỤ
SET IDENTITY_INSERT canh_bao_hoc_vu ON;
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (1, 'CB-CNTT-20230022-2324-HK2', '20230022', 2, '2023-2024', 'CANH_BAO_1', 1.75, N'GPA HK2 (1.75) < 2.0 và Nợ 4 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (2, 'CB-CNTT-20230023-2324-HK2', '20230023', 2, '2023-2024', 'CANH_BAO_1', 1.69, N'GPA HK2 (1.69) < 2.0 và Nợ 6 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (3, 'CB-CNTT-20230024-2324-HK2', '20230024', 2, '2023-2024', 'CANH_BAO_1', 1.63, N'GPA HK2 (1.63) < 2.0 và Nợ 8 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (4, 'CB-CNTT-20230025-2324-HK2', '20230025', 2, '2023-2024', 'CANH_BAO_1', 1.57, N'GPA HK2 (1.57) < 2.0 và Nợ 10 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (5, 'CB-CNTT-20230026-2324-HK2', '20230026', 2, '2023-2024', 'CANH_BAO_2', 1.35, N'GPA HK2 (1.35) < 2.0 và Nợ 8 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (6, 'CB-CNTT-20230027-2324-HK2', '20230027', 2, '2023-2024', 'CANH_BAO_2', 1.28, N'GPA HK2 (1.28) < 2.0 và Nợ 10 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (7, 'CB-CNTT-20230028-2324-HK2', '20230028', 2, '2023-2024', 'CANH_BAO_2', 1.21, N'GPA HK2 (1.21) < 2.0 và Nợ 12 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (8, 'CB-CNTT-20230029-2324-HK2', '20230029', 2, '2023-2024', 'BUOC_THOI_HOC', 0.78, N'GPA HK2 (0.78) < 2.0 và Nợ 14 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (9, 'CB-CNTT-20230030-2324-HK2', '20230030', 2, '2023-2024', 'BUOC_THOI_HOC', 0.68, N'GPA HK2 (0.68) < 2.0 và Nợ 17 tín chỉ chuyên ngành Công nghệ thông tin', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (10, 'CB-OTO-20230122-2324-HK2', '20230122', 2, '2023-2024', 'CANH_BAO_1', 1.75, N'GPA HK2 (1.75) < 2.0 và Nợ 4 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (11, 'CB-OTO-20230123-2324-HK2', '20230123', 2, '2023-2024', 'CANH_BAO_1', 1.69, N'GPA HK2 (1.69) < 2.0 và Nợ 6 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (12, 'CB-OTO-20230124-2324-HK2', '20230124', 2, '2023-2024', 'CANH_BAO_1', 1.63, N'GPA HK2 (1.63) < 2.0 và Nợ 8 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (13, 'CB-OTO-20230125-2324-HK2', '20230125', 2, '2023-2024', 'CANH_BAO_1', 1.57, N'GPA HK2 (1.57) < 2.0 và Nợ 10 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (14, 'CB-OTO-20230126-2324-HK2', '20230126', 2, '2023-2024', 'CANH_BAO_2', 1.35, N'GPA HK2 (1.35) < 2.0 và Nợ 8 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (15, 'CB-OTO-20230127-2324-HK2', '20230127', 2, '2023-2024', 'CANH_BAO_2', 1.28, N'GPA HK2 (1.28) < 2.0 và Nợ 10 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (16, 'CB-OTO-20230128-2324-HK2', '20230128', 2, '2023-2024', 'CANH_BAO_2', 1.21, N'GPA HK2 (1.21) < 2.0 và Nợ 12 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (17, 'CB-OTO-20230129-2324-HK2', '20230129', 2, '2023-2024', 'BUOC_THOI_HOC', 0.78, N'GPA HK2 (0.78) < 2.0 và Nợ 14 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (18, 'CB-OTO-20230130-2324-HK2', '20230130', 2, '2023-2024', 'BUOC_THOI_HOC', 0.68, N'GPA HK2 (0.68) < 2.0 và Nợ 17 tín chỉ chuyên ngành Công nghệ kỹ thuật Ô tô', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (19, 'CB-QTKD-20230222-2324-HK2', '20230222', 2, '2023-2024', 'CANH_BAO_1', 1.75, N'GPA HK2 (1.75) < 2.0 và Nợ 4 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (20, 'CB-QTKD-20230223-2324-HK2', '20230223', 2, '2023-2024', 'CANH_BAO_1', 1.69, N'GPA HK2 (1.69) < 2.0 và Nợ 6 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (21, 'CB-QTKD-20230224-2324-HK2', '20230224', 2, '2023-2024', 'CANH_BAO_1', 1.63, N'GPA HK2 (1.63) < 2.0 và Nợ 8 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (22, 'CB-QTKD-20230225-2324-HK2', '20230225', 2, '2023-2024', 'CANH_BAO_1', 1.57, N'GPA HK2 (1.57) < 2.0 và Nợ 10 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (23, 'CB-QTKD-20230226-2324-HK2', '20230226', 2, '2023-2024', 'CANH_BAO_2', 1.35, N'GPA HK2 (1.35) < 2.0 và Nợ 8 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (24, 'CB-QTKD-20230227-2324-HK2', '20230227', 2, '2023-2024', 'CANH_BAO_2', 1.28, N'GPA HK2 (1.28) < 2.0 và Nợ 10 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (25, 'CB-QTKD-20230228-2324-HK2', '20230228', 2, '2023-2024', 'CANH_BAO_2', 1.21, N'GPA HK2 (1.21) < 2.0 và Nợ 12 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (26, 'CB-QTKD-20230229-2324-HK2', '20230229', 2, '2023-2024', 'BUOC_THOI_HOC', 0.78, N'GPA HK2 (0.78) < 2.0 và Nợ 14 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (27, 'CB-QTKD-20230230-2324-HK2', '20230230', 2, '2023-2024', 'BUOC_THOI_HOC', 0.68, N'GPA HK2 (0.68) < 2.0 và Nợ 17 tín chỉ chuyên ngành Quản trị kinh doanh', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (28, 'CB-DDT-20230322-2324-HK2', '20230322', 2, '2023-2024', 'CANH_BAO_1', 1.75, N'GPA HK2 (1.75) < 2.0 và Nợ 4 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (29, 'CB-DDT-20230323-2324-HK2', '20230323', 2, '2023-2024', 'CANH_BAO_1', 1.69, N'GPA HK2 (1.69) < 2.0 và Nợ 6 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (30, 'CB-DDT-20230324-2324-HK2', '20230324', 2, '2023-2024', 'CANH_BAO_1', 1.63, N'GPA HK2 (1.63) < 2.0 và Nợ 8 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (31, 'CB-DDT-20230325-2324-HK2', '20230325', 2, '2023-2024', 'CANH_BAO_1', 1.57, N'GPA HK2 (1.57) < 2.0 và Nợ 10 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'CHUA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (32, 'CB-DDT-20230326-2324-HK2', '20230326', 2, '2023-2024', 'CANH_BAO_2', 1.35, N'GPA HK2 (1.35) < 2.0 và Nợ 8 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (33, 'CB-DDT-20230327-2324-HK2', '20230327', 2, '2023-2024', 'CANH_BAO_2', 1.28, N'GPA HK2 (1.28) < 2.0 và Nợ 10 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (34, 'CB-DDT-20230328-2324-HK2', '20230328', 2, '2023-2024', 'CANH_BAO_2', 1.21, N'GPA HK2 (1.21) < 2.0 và Nợ 12 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (35, 'CB-DDT-20230329-2324-HK2', '20230329', 2, '2023-2024', 'BUOC_THOI_HOC', 0.78, N'GPA HK2 (0.78) < 2.0 và Nợ 14 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'DA_TU_VAN');
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES (36, 'CB-DDT-20230330-2324-HK2', '20230330', 2, '2023-2024', 'BUOC_THOI_HOC', 0.68, N'GPA HK2 (0.68) < 2.0 và Nợ 17 tín chỉ chuyên ngành Công nghệ kỹ thuật Điện - Điện tử', '2024-07-01', 'DA_TU_VAN');
SET IDENTITY_INSERT canh_bao_hoc_vu OFF;
GO

-- 6. NHẬT KÝ TƯ VẤN
SET IDENTITY_INSERT nhat_ky_tu_van ON;
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (1, '20230022', 'CV001', 1, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 4 tín chỉ ngành Công nghệ thông tin', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (2, '20230024', 'CV001', 3, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Công nghệ thông tin', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (3, '20230026', 'CV001', 5, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Công nghệ thông tin', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (4, '20230027', 'CV001', 6, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 10 tín chỉ ngành Công nghệ thông tin', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (5, '20230028', 'CV001', 7, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 12 tín chỉ ngành Công nghệ thông tin', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (6, '20230029', 'CV001', 8, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 14 tín chỉ ngành Công nghệ thông tin', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (7, '20230030', 'CV001', 9, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 17 tín chỉ ngành Công nghệ thông tin', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (8, '20230122', 'CV002', 10, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 4 tín chỉ ngành Công nghệ kỹ thuật Ô tô', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (9, '20230124', 'CV002', 12, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Công nghệ kỹ thuật Ô tô', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (10, '20230126', 'CV002', 14, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Công nghệ kỹ thuật Ô tô', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (11, '20230127', 'CV002', 15, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 10 tín chỉ ngành Công nghệ kỹ thuật Ô tô', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (12, '20230128', 'CV002', 16, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 12 tín chỉ ngành Công nghệ kỹ thuật Ô tô', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (13, '20230129', 'CV002', 17, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 14 tín chỉ ngành Công nghệ kỹ thuật Ô tô', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (14, '20230130', 'CV002', 18, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 17 tín chỉ ngành Công nghệ kỹ thuật Ô tô', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (15, '20230222', 'CV003', 19, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 4 tín chỉ ngành Quản trị kinh doanh', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (16, '20230224', 'CV003', 21, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Quản trị kinh doanh', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (17, '20230226', 'CV003', 23, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Quản trị kinh doanh', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (18, '20230227', 'CV003', 24, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 10 tín chỉ ngành Quản trị kinh doanh', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (19, '20230228', 'CV003', 25, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 12 tín chỉ ngành Quản trị kinh doanh', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (20, '20230229', 'CV003', 26, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 14 tín chỉ ngành Quản trị kinh doanh', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (21, '20230230', 'CV003', 27, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 17 tín chỉ ngành Quản trị kinh doanh', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (22, '20230322', 'CV004', 28, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 4 tín chỉ ngành Công nghệ kỹ thuật Điện - Điện tử', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (23, '20230324', 'CV004', 30, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Công nghệ kỹ thuật Điện - Điện tử', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (24, '20230326', 'CV004', 32, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 8 tín chỉ ngành Công nghệ kỹ thuật Điện - Điện tử', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (25, '20230327', 'CV004', 33, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 10 tín chỉ ngành Công nghệ kỹ thuật Điện - Điện tử', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (26, '20230328', 'CV004', 34, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 12 tín chỉ ngành Công nghệ kỹ thuật Điện - Điện tử', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (27, '20230329', 'CV004', 35, '2024-07-15', N'Trực tuyến (MS Teams)', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 14 tín chỉ ngành Công nghệ kỹ thuật Điện - Điện tử', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES (28, '20230330', 'CV004', 36, '2024-07-15', N'Trực tiếp tại VP Khoa', N'Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ 17 tín chỉ ngành Công nghệ kỹ thuật Điện - Điện tử', N'Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ', N'Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm', N'Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo');
SET IDENTITY_INSERT nhat_ky_tu_van OFF;
GO

-- 7. TÀI KHOẢN NGƯỜI DÙNG
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Quản trị viên Hệ thống EAUT', 'admin@eaut.edu.vn', 'ADMIN', NULL);
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('quanly', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trưởng phòng Đào tạo EAUT', 'daotao@eaut.edu.vn', 'QUAN_LY', NULL);
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_phongdv', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'TS. Đinh Văn Phong', 'phong.dv@eaut.edu.vn', 'CO_VAN', 'CV001');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'TS. Đinh Văn Phong', 'phong.dv@eaut.edu.vn', 'CO_VAN', 'CV001');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đình Anh', 'sv20230001@eaut.edu.vn', 'SINH_VIEN', '20230001');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Gia Bảo', 'sv20230002@eaut.edu.vn', 'SINH_VIEN', '20230002');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Diệu Linh', 'sv20230003@eaut.edu.vn', 'SINH_VIEN', '20230003');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230004', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Hữu Quân', 'sv20230004@eaut.edu.vn', 'SINH_VIEN', '20230004');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230005', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Đức Hiếu', 'sv20230005@eaut.edu.vn', 'SINH_VIEN', '20230005');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230006', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Ngọc Yến', 'sv20230006@eaut.edu.vn', 'SINH_VIEN', '20230006');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230007', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Trọng Tú', 'sv20230007@eaut.edu.vn', 'SINH_VIEN', '20230007');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230008', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Quang Phúc', 'sv20230008@eaut.edu.vn', 'SINH_VIEN', '20230008');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230009', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Thùy Vân', 'sv20230009@eaut.edu.vn', 'SINH_VIEN', '20230009');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230010', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Hoàng Tuấn', 'sv20230010@eaut.edu.vn', 'SINH_VIEN', '20230010');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230011', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Thanh Hưng', 'sv20230011@eaut.edu.vn', 'SINH_VIEN', '20230011');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230012', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Thị Giang', 'sv20230012@eaut.edu.vn', 'SINH_VIEN', '20230012');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230013', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đình Khánh', 'sv20230013@eaut.edu.vn', 'SINH_VIEN', '20230013');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230014', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Gia Cường', 'sv20230014@eaut.edu.vn', 'SINH_VIEN', '20230014');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230015', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đặng Diệu Nhi', 'sv20230015@eaut.edu.vn', 'SINH_VIEN', '20230015');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230016', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nguyễn Hữu Bách', 'sv20230016@eaut.edu.vn', 'SINH_VIEN', '20230016');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230017', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đức Thịnh', 'sv20230017@eaut.edu.vn', 'SINH_VIEN', '20230017');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230018', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Ngọc Vy', 'sv20230018@eaut.edu.vn', 'SINH_VIEN', '20230018');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230019', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Trọng Hải', 'sv20230019@eaut.edu.vn', 'SINH_VIEN', '20230019');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230020', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Quang Khởi', 'sv20230020@eaut.edu.vn', 'SINH_VIEN', '20230020');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230021', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Thùy Thảo', 'sv20230021@eaut.edu.vn', 'SINH_VIEN', '20230021');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230022', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Hoàng Duy', 'sv20230022@eaut.edu.vn', 'SINH_VIEN', '20230022');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230023', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Thanh Trí', 'sv20230023@eaut.edu.vn', 'SINH_VIEN', '20230023');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230024', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Thị Mai', 'sv20230024@eaut.edu.vn', 'SINH_VIEN', '20230024');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230025', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Đình Đạt', 'sv20230025@eaut.edu.vn', 'SINH_VIEN', '20230025');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230026', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Gia Nam', 'sv20230026@eaut.edu.vn', 'SINH_VIEN', '20230026');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230027', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Diệu Quyên', 'sv20230027@eaut.edu.vn', 'SINH_VIEN', '20230027');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230028', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Hữu Bảo', 'sv20230028@eaut.edu.vn', 'SINH_VIEN', '20230028');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230029', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đức Kiệt', 'sv20230029@eaut.edu.vn', 'SINH_VIEN', '20230029');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230030', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Ngọc Lan', 'sv20230030@eaut.edu.vn', 'SINH_VIEN', '20230030');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_haint', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'PGS.TS. Nguyễn Thanh Hải', 'hai.nt@eaut.edu.vn', 'CO_VAN', 'CV002');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'PGS.TS. Nguyễn Thanh Hải', 'hai.nt@eaut.edu.vn', 'CO_VAN', 'CV002');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230101', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đình Anh', 'sv20230101@eaut.edu.vn', 'SINH_VIEN', '20230101');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230102', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Gia Bảo', 'sv20230102@eaut.edu.vn', 'SINH_VIEN', '20230102');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230103', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Diệu Linh', 'sv20230103@eaut.edu.vn', 'SINH_VIEN', '20230103');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230104', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Hữu Quân', 'sv20230104@eaut.edu.vn', 'SINH_VIEN', '20230104');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230105', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Đức Hiếu', 'sv20230105@eaut.edu.vn', 'SINH_VIEN', '20230105');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230106', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Ngọc Yến', 'sv20230106@eaut.edu.vn', 'SINH_VIEN', '20230106');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230107', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Trọng Tú', 'sv20230107@eaut.edu.vn', 'SINH_VIEN', '20230107');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230108', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Quang Phúc', 'sv20230108@eaut.edu.vn', 'SINH_VIEN', '20230108');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230109', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Thùy Vân', 'sv20230109@eaut.edu.vn', 'SINH_VIEN', '20230109');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230110', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Hoàng Tuấn', 'sv20230110@eaut.edu.vn', 'SINH_VIEN', '20230110');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230111', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Thanh Hưng', 'sv20230111@eaut.edu.vn', 'SINH_VIEN', '20230111');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230112', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Thị Giang', 'sv20230112@eaut.edu.vn', 'SINH_VIEN', '20230112');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230113', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đình Khánh', 'sv20230113@eaut.edu.vn', 'SINH_VIEN', '20230113');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230114', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Gia Cường', 'sv20230114@eaut.edu.vn', 'SINH_VIEN', '20230114');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230115', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đặng Diệu Nhi', 'sv20230115@eaut.edu.vn', 'SINH_VIEN', '20230115');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230116', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nguyễn Hữu Bách', 'sv20230116@eaut.edu.vn', 'SINH_VIEN', '20230116');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230117', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đức Thịnh', 'sv20230117@eaut.edu.vn', 'SINH_VIEN', '20230117');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230118', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Ngọc Vy', 'sv20230118@eaut.edu.vn', 'SINH_VIEN', '20230118');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230119', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Trọng Hải', 'sv20230119@eaut.edu.vn', 'SINH_VIEN', '20230119');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230120', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Quang Khởi', 'sv20230120@eaut.edu.vn', 'SINH_VIEN', '20230120');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230121', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Thùy Thảo', 'sv20230121@eaut.edu.vn', 'SINH_VIEN', '20230121');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230122', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Hoàng Duy', 'sv20230122@eaut.edu.vn', 'SINH_VIEN', '20230122');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230123', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Thanh Trí', 'sv20230123@eaut.edu.vn', 'SINH_VIEN', '20230123');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230124', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Thị Mai', 'sv20230124@eaut.edu.vn', 'SINH_VIEN', '20230124');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230125', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Đình Đạt', 'sv20230125@eaut.edu.vn', 'SINH_VIEN', '20230125');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230126', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Gia Nam', 'sv20230126@eaut.edu.vn', 'SINH_VIEN', '20230126');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230127', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Diệu Quyên', 'sv20230127@eaut.edu.vn', 'SINH_VIEN', '20230127');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230128', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Hữu Bảo', 'sv20230128@eaut.edu.vn', 'SINH_VIEN', '20230128');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230129', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đức Kiệt', 'sv20230129@eaut.edu.vn', 'SINH_VIEN', '20230129');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230130', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Ngọc Lan', 'sv20230130@eaut.edu.vn', 'SINH_VIEN', '20230130');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_maiht', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'ThS. Hoàng Thị Mai', 'mai.ht@eaut.edu.vn', 'CO_VAN', 'CV003');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'ThS. Hoàng Thị Mai', 'mai.ht@eaut.edu.vn', 'CO_VAN', 'CV003');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230201', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đình Anh', 'sv20230201@eaut.edu.vn', 'SINH_VIEN', '20230201');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230202', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Gia Bảo', 'sv20230202@eaut.edu.vn', 'SINH_VIEN', '20230202');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230203', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Diệu Linh', 'sv20230203@eaut.edu.vn', 'SINH_VIEN', '20230203');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230204', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Hữu Quân', 'sv20230204@eaut.edu.vn', 'SINH_VIEN', '20230204');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230205', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Đức Hiếu', 'sv20230205@eaut.edu.vn', 'SINH_VIEN', '20230205');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230206', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Ngọc Yến', 'sv20230206@eaut.edu.vn', 'SINH_VIEN', '20230206');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230207', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Trọng Tú', 'sv20230207@eaut.edu.vn', 'SINH_VIEN', '20230207');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230208', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Quang Phúc', 'sv20230208@eaut.edu.vn', 'SINH_VIEN', '20230208');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230209', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Thùy Vân', 'sv20230209@eaut.edu.vn', 'SINH_VIEN', '20230209');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230210', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Hoàng Tuấn', 'sv20230210@eaut.edu.vn', 'SINH_VIEN', '20230210');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230211', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Thanh Hưng', 'sv20230211@eaut.edu.vn', 'SINH_VIEN', '20230211');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230212', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Thị Giang', 'sv20230212@eaut.edu.vn', 'SINH_VIEN', '20230212');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230213', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đình Khánh', 'sv20230213@eaut.edu.vn', 'SINH_VIEN', '20230213');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230214', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Gia Cường', 'sv20230214@eaut.edu.vn', 'SINH_VIEN', '20230214');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230215', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đặng Diệu Nhi', 'sv20230215@eaut.edu.vn', 'SINH_VIEN', '20230215');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230216', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nguyễn Hữu Bách', 'sv20230216@eaut.edu.vn', 'SINH_VIEN', '20230216');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230217', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đức Thịnh', 'sv20230217@eaut.edu.vn', 'SINH_VIEN', '20230217');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230218', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Ngọc Vy', 'sv20230218@eaut.edu.vn', 'SINH_VIEN', '20230218');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230219', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Trọng Hải', 'sv20230219@eaut.edu.vn', 'SINH_VIEN', '20230219');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230220', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Quang Khởi', 'sv20230220@eaut.edu.vn', 'SINH_VIEN', '20230220');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230221', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Thùy Thảo', 'sv20230221@eaut.edu.vn', 'SINH_VIEN', '20230221');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230222', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Hoàng Duy', 'sv20230222@eaut.edu.vn', 'SINH_VIEN', '20230222');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230223', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Thanh Trí', 'sv20230223@eaut.edu.vn', 'SINH_VIEN', '20230223');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230224', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Thị Mai', 'sv20230224@eaut.edu.vn', 'SINH_VIEN', '20230224');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230225', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Đình Đạt', 'sv20230225@eaut.edu.vn', 'SINH_VIEN', '20230225');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230226', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Gia Nam', 'sv20230226@eaut.edu.vn', 'SINH_VIEN', '20230226');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230227', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Diệu Quyên', 'sv20230227@eaut.edu.vn', 'SINH_VIEN', '20230227');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230228', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Hữu Bảo', 'sv20230228@eaut.edu.vn', 'SINH_VIEN', '20230228');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230229', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đức Kiệt', 'sv20230229@eaut.edu.vn', 'SINH_VIEN', '20230229');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230230', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Ngọc Lan', 'sv20230230@eaut.edu.vn', 'SINH_VIEN', '20230230');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_sonvt', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'TS. Vũ Trường Sơn', 'son.vt@eaut.edu.vn', 'CO_VAN', 'CV004');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv004', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'TS. Vũ Trường Sơn', 'son.vt@eaut.edu.vn', 'CO_VAN', 'CV004');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230301', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đình Anh', 'sv20230301@eaut.edu.vn', 'SINH_VIEN', '20230301');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230302', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Gia Bảo', 'sv20230302@eaut.edu.vn', 'SINH_VIEN', '20230302');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230303', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Diệu Linh', 'sv20230303@eaut.edu.vn', 'SINH_VIEN', '20230303');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230304', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Hữu Quân', 'sv20230304@eaut.edu.vn', 'SINH_VIEN', '20230304');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230305', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Đức Hiếu', 'sv20230305@eaut.edu.vn', 'SINH_VIEN', '20230305');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230306', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Ngọc Yến', 'sv20230306@eaut.edu.vn', 'SINH_VIEN', '20230306');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230307', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Trọng Tú', 'sv20230307@eaut.edu.vn', 'SINH_VIEN', '20230307');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230308', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Quang Phúc', 'sv20230308@eaut.edu.vn', 'SINH_VIEN', '20230308');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230309', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Thùy Vân', 'sv20230309@eaut.edu.vn', 'SINH_VIEN', '20230309');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230310', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Hoàng Tuấn', 'sv20230310@eaut.edu.vn', 'SINH_VIEN', '20230310');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230311', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Thanh Hưng', 'sv20230311@eaut.edu.vn', 'SINH_VIEN', '20230311');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230312', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Thị Giang', 'sv20230312@eaut.edu.vn', 'SINH_VIEN', '20230312');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230313', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đình Khánh', 'sv20230313@eaut.edu.vn', 'SINH_VIEN', '20230313');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230314', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Gia Cường', 'sv20230314@eaut.edu.vn', 'SINH_VIEN', '20230314');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230315', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đặng Diệu Nhi', 'sv20230315@eaut.edu.vn', 'SINH_VIEN', '20230315');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230316', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nguyễn Hữu Bách', 'sv20230316@eaut.edu.vn', 'SINH_VIEN', '20230316');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230317', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đức Thịnh', 'sv20230317@eaut.edu.vn', 'SINH_VIEN', '20230317');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230318', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Dương Ngọc Vy', 'sv20230318@eaut.edu.vn', 'SINH_VIEN', '20230318');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230319', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Huỳnh Trọng Hải', 'sv20230319@eaut.edu.vn', 'SINH_VIEN', '20230319');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230320', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hồ Quang Khởi', 'sv20230320@eaut.edu.vn', 'SINH_VIEN', '20230320');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230321', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Thùy Thảo', 'sv20230321@eaut.edu.vn', 'SINH_VIEN', '20230321');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230322', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Bùi Hoàng Duy', 'sv20230322@eaut.edu.vn', 'SINH_VIEN', '20230322');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230323', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trần Thanh Trí', 'sv20230323@eaut.edu.vn', 'SINH_VIEN', '20230323');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230324', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Võ Thị Mai', 'sv20230324@eaut.edu.vn', 'SINH_VIEN', '20230324');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230325', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lý Đình Đạt', 'sv20230325@eaut.edu.vn', 'SINH_VIEN', '20230325');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230326', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phan Gia Nam', 'sv20230326@eaut.edu.vn', 'SINH_VIEN', '20230326');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230327', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Ngô Diệu Quyên', 'sv20230327@eaut.edu.vn', 'SINH_VIEN', '20230327');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230328', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Hoàng Hữu Bảo', 'sv20230328@eaut.edu.vn', 'SINH_VIEN', '20230328');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230329', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Đỗ Đức Kiệt', 'sv20230329@eaut.edu.vn', 'SINH_VIEN', '20230329');
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('20230330', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Lê Ngọc Lan', 'sv20230330@eaut.edu.vn', 'SINH_VIEN', '20230330');
GO

-- 8. THÔNG BÁO & TIN NHẮN
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-01', N'[EAUT] THÔNG BÁO XÉT HỌC BỔNG KHUYẾN KHÍCH HỌC TẬP K14', N'Chúc mừng các sinh viên đạt GPA >= 3.2 trong học kỳ 2 năm học 2023-2024. Đề nghị sinh viên nộp hồ sơ xét học bổng tại VP Đoàn trường trước ngày 20/09/2026.', 'TIER_1', 'ALL', NULL, GETDATE(), N'Phòng Đào Tạo EAUT', 24, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-02', N'[EAUT] KẾ HOẠCH ĐĂNG KÝ HỌC PHẦN HỌC KỲ 1 NĂM HỌC 2024-2025', N'Hệ thống cổng đào tạo eaut.edu.vn mở cổng đăng ký tín chỉ từ 8h00 ngày 25/08/2026. Sinh viên chú ý các môn tiên quyết.', 'TIER_2', 'ALL', NULL, GETDATE(), N'Phòng Đào Tạo EAUT', 60, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-03', N'[EAUT] YÊU CẦU TƯ VẤN HỌC VỤ BẮT BUỘC ĐỐI VỚI SINH VIÊN BỊ CẢNH BÁO', N'Các sinh viên có tên trong danh sách cảnh báo Mức 1, Mức 2 và Buộc thôi học phải liên hệ ngay Cố vấn học tập trước ngày 15/08/2026.', 'TIER_3', 'ALL', NULL, GETDATE(), N'Ban CVHT EAUT', 36, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-04', N'[KHOA CNTT] HỘI THẢO CÔNG NGHỆ & ĐỊNH HƯỚNG NGHỀ NGHIỆP AI & CLOUD', N'Khoa CNTT phối hợp cùng doanh nghiệp đối tác tổ chức workshop chia sẻ công nghệ vào 9h00 sáng thứ Bảy tại Hội trường A.', 'ALL', 'DCCTPM14A', NULL, GETDATE(), N'TS. Đinh Văn Phong', 15, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-05', N'[KHOA Ô TÔ] LỊCH THỰC TẬP TỐT NGHIỆP VÀ AN TOÀN XƯỞNG THỰC HÀNH', N'Yêu cầu 100% sinh viên lớp DCOTO14A trang bị đồ bảo hộ lao động đầy đủ trước khi vào xưởng Ô tô.', 'ALL', 'DCOTO14A', NULL, GETDATE(), N'PGS.TS. Nguyễn Thanh Hải', 15, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-01', N'Chúc mừng kết quả học tập xuất sắc ngành Công nghệ thông tin', N'Thầy chúc mừng em Nam đã đạt GPA 3.65 đứng đầu lớp DCCTPM14A kỳ vừa qua. Tiếp tục giữ vững phong độ nhé em!', 'CA_NHAN', 'DCCTPM14A', '20230001', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-02', N'📬 [SV PHẢN HỒI] Em cảm ơn thầy và muốn hỏi về học bổng', N'Dạ em chào thầy Phong, em cảm ơn thầy ạ! Cho em hỏi hồ sơ xét học bổng kỳ này cần nộp bản sao bảng điểm có xác nhận không ạ?', 'PHAN_HOI_SV', 'ALL', '20230001', GETDATE(), N'Vũ Đình Anh (20230001)', 1, 'DA_DOC');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-03', N'💬 [CVHT TRẢ LỜI] Hướng dẫn thủ tục học bổng', N'Chào em, bản điểm thầy sẽ trực tiếp ký xác nhận và gửi VP Đoàn cho em nhé. Em chỉ cần nộp đơn xin xét theo mẫu thôi.', 'CVHT_TRA_LOI', 'CA_NHAN', '20230001', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_DOC');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-04', N'Lịch hẹn gặp mặt tư vấn học tập và kế hoạch học lại', N'Chào em Duy, học kỳ vừa qua em bị cảnh báo Mức 1. Chiều thứ Ba tuần tới 14h00 em đến văn phòng Khoa gặp thầy để trao đổi kế hoạch học lại nhé.', 'CA_NHAN', 'DCCTPM14A', '20230022', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-05', N'📬 [SV PHẢN HỒI] Em xác nhận lịch hẹn tư vấn', N'Dạ em chào thầy, thứ Ba tuần tới 14h00 em sẽ có mặt đúng giờ tại VP Khoa ạ. Em cảm ơn thầy đã nhắc nhở em!', 'PHAN_HOI_SV', 'ALL', '20230022', GETDATE(), N'Bùi Hoàng Duy (20230022)', 1, 'SV_CHUA_DOC');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-06', N'Cảnh báo học vụ Mức 2 và yêu cầu cam kết tiến độ', N'Chào em Nam, em đang bị cảnh báo Mức 2 và nợ 8 tín chỉ. Em cần nộp bản cam kết cải thiện GPA trước ngày 15/09.', 'CA_NHAN', 'DCCNTT14B', '20230026', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-07', N'📬 [SV PHẢN HỒI] Em xin tư vấn môn học lại để gỡ cảnh báo', N'Thưa thầy, kỳ này em đã đăng ký học lại 2 môn Toán rời rạc và Cấu trúc dữ liệu. Thầy cho em xin lời khuyên để phân bổ thời gian hợp lý ạ.', 'PHAN_HOI_SV', 'ALL', '20230026', GETDATE(), N'Phan Gia Nam (20230026)', 1, 'SV_CHUA_DOC');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-08', N'Thông báo cảnh báo nguy cơ Buộc thôi học', N'Em Kiệt chú ý, tình trạng học tập của em đang ở mức báo động Buộc thôi học. Hãy liên hệ ngay với CVHT trong tuần này.', 'CA_NHAN', 'DCCNTT14B', '20230029', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-09', N'📬 [SV PHẢN HỒI] Đơn xin xem xét hoàn cảnh gia đình', N'Dạ em chào thầy, đợt vừa rồi gia đình em có biến cố nên em phải nghỉ nhiều buổi. Em mong thầy và nhà trường tạo điều kiện cho em được tiếp tục học tập ạ.', 'PHAN_HOI_SV', 'ALL', '20230029', GETDATE(), N'Đỗ Đức Kiệt (20230029)', 1, 'SV_CHUA_DOC');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-10', N'Tư vấn hướng nghiệp và chuyên ngành Ô tô', N'Chào em, kỳ tới Khoa mở chuyên ngành Chẩn đoán điện Ô tô, em chú ý đăng ký môn tiên quyết nhé.', 'CA_NHAN', 'DCOTO14A', '20230101', GETDATE(), N'PGS.TS. Nguyễn Thanh Hải', 1, 'DA_GUI');
INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-11', N'Nhắc nhở học tập và đồ án Quản trị kinh doanh', N'Chào em, đồ án môn Quản trị chiến lược hạn nộp là ngày 20/09, nhóm em hoàn thiện sớm nhé.', 'CA_NHAN', 'DCQTKD14A', '20230201', GETDATE(), N'ThS. Hoàng Thị Mai', 1, 'DA_GUI');
GO

-- 9. LỊCH GIẢNG DẠY
SET IDENTITY_INSERT lich_giang_day ON;
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (1, 'CV001', N'TS. Đinh Văn Phong', 'DCCTPM14A', N'DCCTPM14A - Công nghệ phần mềm K14', N'Sinh hoạt lớp định kỳ đầu học kỳ 1', '2026-08-25', '08:00', '10:00', N'Phòng 501-A (Tòa nhà EAUT)', 'Trực tiếp', 'SINH_HOAT_LOP', 'HOAN_THANH', N'Triển khai kế hoạch năm học mới và rà soát kết quả học tập K14');
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (2, 'CV001', N'TS. Đinh Văn Phong', 'DCCTPM14A', N'DCCTPM14A - Công nghệ phần mềm K14', N'Tư vấn học tập sinh viên cảnh báo học vụ Khoa CNTT', '2026-09-02', '14:00', '16:00', N'Văn phòng CVHT Khoa CNTT', 'Trực tiếp', 'TU_VAN_HOC_TAP', 'SAP_DIEN_RA', N'Gặp mặt tư vấn riêng các sinh viên Mức 1 và Mức 2');
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (3, 'CV001', N'TS. Đinh Văn Phong', 'DCCNTT14B', N'DCCNTT14B - Công nghệ thông tin K14B', N'Sinh hoạt lớp & phổ biến đăng ký tín chỉ kỳ mới', '2026-09-04', '08:00', '10:00', N'Phòng 402-A (Tòa nhà EAUT)', 'Trực tiếp', 'SINH_HOAT_LOP', 'SAP_DIEN_RA', N'Hướng dẫn đăng ký học phần và xử lý học vụ');
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (4, 'CV002', N'PGS.TS. Nguyễn Thanh Hải', 'DCOTO14A', N'DCOTO14A - Công nghệ kỹ thuật Ô tô 14A', N'Sinh hoạt lớp và phổ biến quy chế thực tập xưởng Ô tô', '2026-09-05', '09:00', '11:00', N'Xưởng thực hành Ô tô EAUT', 'Trực tiếp', 'SINH_HOAT_LOP', 'SAP_DIEN_RA', N'Phổ biến quy chế an toàn lao động và cảnh báo học vụ mới nhất');
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (5, 'CV002', N'PGS.TS. Nguyễn Thanh Hải', 'DCOTO14B', N'DCOTO14B - Công nghệ kỹ thuật Ô tô 14B', N'Tư vấn phương pháp học tập chuyên ngành Điện tử Ô tô', '2026-09-07', '14:00', '16:00', N'Phòng 303-B', 'Trực tuyến (MS Teams)', 'TU_VAN_HOC_TAP', 'SAP_DIEN_RA', N'Hỗ trợ sinh viên có nguy cơ nợ môn thực hành');
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (6, 'CV003', N'ThS. Hoàng Thị Mai', 'DCQTKD14A', N'DCQTKD14A - Quản trị kinh doanh 14A', N'Tư vấn phương pháp học và hướng nghiệp Marketing', '2026-09-08', '14:00', '16:00', N'Hội trường B - EAUT', 'Trực tiếp', 'SINH_HOAT_LOP', 'SAP_DIEN_RA', N'Gặp gỡ doanh nghiệp liên kết EAUT và sinh hoạt lớp');
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (7, 'CV003', N'ThS. Hoàng Thị Mai', 'DCQTKD14B', N'DCQTKD14B - Quản trị kinh doanh 14B', N'Tư vấn học tập và rà soát tín chỉ tốt nghiệp', '2026-09-10', '09:00', '11:00', N'Phòng 201-B', 'Trực tiếp', 'SINH_HOAT_LOP', 'SAP_DIEN_RA', N'Rà soát điều kiện chuẩn đầu ra tiếng Anh và Tin học');
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES (8, 'CV004', N'TS. Vũ Trường Sơn', 'DCDDT14A', N'DCDDT14A - Kỹ thuật Điện - Điện tử 14A', N'Sinh hoạt định kỳ và hướng dẫn nghiên cứu khoa học', '2026-09-12', '08:30', '10:30', N'Phòng Lab Tự động hóa', 'Trực tiếp', 'SINH_HOAT_LOP', 'SAP_DIEN_RA', N'Thành lập các nhóm NCKH và hỗ trợ sinh viên học tập');
SET IDENTITY_INSERT lich_giang_day OFF;
GO

-- 10. ĐIỂM DANH
SET IDENTITY_INSERT diem_danh ON;
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (1, 1, '20230001', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ, tích cực tham gia phát biểu');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (2, 1, '20230002', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (3, 1, '20230003', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (4, 1, '20230004', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (5, 1, '20230005', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (6, 1, '20230006', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (7, 1, '20230007', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (8, 1, '20230008', '2026-08-25', 'LATE', N'Đi muộn 10 phút');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (9, 1, '20230009', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (10, 1, '20230010', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (11, 1, '20230011', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (12, 1, '20230012', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (13, 1, '20230013', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (14, 1, '20230014', '2026-08-25', 'LATE', N'Đi muộn 15 phút do kẹt xe');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (15, 1, '20230015', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (16, 2, '20230022', '2026-09-02', 'ON_TIME', N'Có mặt đúng giờ, đã trao đổi kế hoạch học lại');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (17, 2, '20230023', '2026-09-02', 'ON_TIME', N'Có mặt đúng giờ, đã ký cam kết');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (18, 2, '20230024', '2026-09-02', 'LATE', N'Đi muộn 15 phút, đã nhắc nhở');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (19, 2, '20230026', '2026-09-02', 'ABSENT', N'Vắng mặt không phép - đã gửi email cảnh báo');
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (20, 2, '20230029', '2026-09-02', 'ABSENT', N'Vắng mặt - đã liên hệ phụ huynh');
SET IDENTITY_INSERT diem_danh OFF;
GO

PRINT N'Khởi tạo Cơ sở Dữ liệu Microsoft SQL Server EAUT ql_canhbao_hocvu (120 SV) thành công 100%!';