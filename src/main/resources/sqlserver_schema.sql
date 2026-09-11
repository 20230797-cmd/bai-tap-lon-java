-- ============================================================
-- DATABASE SCHEMA & DỮ LIỆU ĐỒNG BỘ CHUẨN CHO MICROSOFT SQL SERVER (SSMS)
-- Hệ thống Quản lý Cố vấn Học tập & Cảnh báo Học vụ
-- ============================================================

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

CREATE TABLE co_van_hoc_tap (
    ma_cvht VARCHAR(20) PRIMARY KEY,
    ho_ten NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(20),
    khoa NVARCHAR(100) NOT NULL
);

CREATE TABLE lop_hoc (
    ma_lop VARCHAR(20) PRIMARY KEY,
    ten_lop NVARCHAR(100) NOT NULL,
    khoa NVARCHAR(100) NOT NULL,
    khoa_hoc INT NOT NULL,
    ma_cvht VARCHAR(20),
    CONSTRAINT fk_lop_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht) ON DELETE SET NULL
);

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
    CONSTRAINT fk_nhatky_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE,
    CONSTRAINT fk_nhatky_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht) ON DELETE NO ACTION,
    CONSTRAINT fk_nhatky_canhbao FOREIGN KEY (id_canh_bao) REFERENCES canh_bao_hoc_vu(id) ON DELETE SET NULL
);

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

INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES
('CV001', N'TS. Nguyễn Văn An', 'an.nv@huce.edu.vn', '0912345678', N'Công nghệ thông tin'),
('CV002', N'ThS. Trần Thị Bình', 'binh.tt@huce.edu.vn', '0987654321', N'Kinh tế xây dựng');

INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES
('68IT1', N'68IT1 - Công nghệ thông tin 1', N'Công nghệ thông tin', 2023, 'CV001'),
('68IT2', N'68IT2 - Công nghệ thông tin 2', N'Công nghệ thông tin', 2023, 'CV001'),
('68KX1', N'68KX1 - Kinh tế xây dựng 1', N'Kinh tế xây dựng', 2023, 'CV002'),
('68XD1', N'68XD1 - Xây dựng dân dụng 1', N'Xây dựng dân dụng', 2023, 'CV002');

INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES
('20230001', N'Nguyễn Văn Nam', '2005-03-15', N'Nam', 'sv20230001@huce.edu.vn', '0911000001', '68IT1', 'DANG_HOC'),
('20230002', N'Trần Thị Lan', '2005-07-22', N'Nữ', 'sv20230002@huce.edu.vn', '0911000002', '68IT1', 'DANG_HOC'),
('20230003', N'Lê Hoàng Long', '2005-11-05', N'Nam', 'sv20230003@huce.edu.vn', '0911000003', '68IT1', 'DANG_HOC'),
('20230004', N'Đặng Thùy Dương', '2005-04-12', N'Nữ', 'sv20230004@huce.edu.vn', '0911000004', '68IT2', 'DANG_HOC'),
('20230005', N'Bùi Quang Huy', '2005-08-25', N'Nam', 'sv20230005@huce.edu.vn', '0911000005', '68IT2', 'DANG_HOC'),
('20230006', N'Mai Phương Thảo', '2005-01-08', N'Nữ', 'sv20230006@huce.edu.vn', '0911000006', '68KX1', 'DANG_HOC'),
('20230007', N'Trịnh Quốc Anh', '2005-10-10', N'Nam', 'sv20230007@huce.edu.vn', '0911000007', '68KX1', 'DANG_HOC'),
('20230008', N'Phan Bảo Châu', '2005-09-17', N'Nữ', 'sv20230008@huce.edu.vn', '0911000008', '68XD1', 'DANG_HOC'),
('20230009', N'Phạm Minh Tuấn', '2005-02-18', N'Nam', 'sv20230009@huce.edu.vn', '0911000009', '68IT1', 'CANH_BAO_1'),
('20230010', N'Vũ Đức Hải', '2005-09-30', N'Nam', 'sv20230010@huce.edu.vn', '0911000010', '68IT1', 'CANH_BAO_2'),
('20230011', N'Hoàng Quốc Bảo', '2005-12-14', N'Nam', 'sv20230011@huce.edu.vn', '0911000011', '68IT1', 'BUOC_THOI_HOC'),
('20230012', N'Đỗ Gia Hưng', '2005-06-19', N'Nam', 'sv20230012@huce.edu.vn', '0911000012', '68IT2', 'CANH_BAO_1'),
('20230013', N'Ngô Đình Khởi', '2005-05-03', N'Nam', 'sv20230013@huce.edu.vn', '0911000013', '68KX1', 'CANH_BAO_2'),
('20230014', N'Dương Tuấn Kiệt', '2005-03-29', N'Nam', 'sv20230014@huce.edu.vn', '0911000014', '68KX1', 'BUOC_THOI_HOC'),
('20230015', N'Cao Thanh Tùng', '2005-11-21', N'Nam', 'sv20230015@huce.edu.vn', '0911000015', '68XD1', 'CANH_BAO_1');

INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES
('20230001', 1, '2023-2024', 3.40, 3.40, 0),
('20230001', 2, '2023-2024', 3.65, 3.52, 0),
('20230002', 1, '2023-2024', 3.20, 3.20, 0),
('20230002', 2, '2023-2024', 3.45, 3.32, 0),
('20230003', 1, '2023-2024', 3.10, 3.10, 0),
('20230003', 2, '2023-2024', 3.25, 3.18, 0),
('20230004', 1, '2023-2024', 2.75, 2.75, 0),
('20230004', 2, '2023-2024', 2.90, 2.82, 0),
('20230005', 1, '2023-2024', 2.60, 2.60, 0),
('20230005', 2, '2023-2024', 2.70, 2.65, 0),
('20230006', 1, '2023-2024', 3.50, 3.50, 0),
('20230006', 2, '2023-2024', 3.70, 3.60, 0),
('20230007', 1, '2023-2024', 2.85, 2.85, 0),
('20230007', 2, '2023-2024', 2.95, 2.90, 0),
('20230008', 1, '2023-2024', 3.00, 3.00, 0),
('20230008', 2, '2023-2024', 3.15, 3.08, 0),
('20230009', 1, '2023-2024', 2.10, 2.10, 0),
('20230009', 2, '2023-2024', 1.65, 1.88, 6),
('20230010', 1, '2023-2024', 1.80, 1.80, 4),
('20230010', 2, '2023-2024', 1.30, 1.55, 9),
('20230011', 1, '2023-2024', 1.20, 1.20, 8),
('20230011', 2, '2023-2024', 0.80, 1.00, 15),
('20230012', 1, '2023-2024', 2.20, 2.20, 0),
('20230012', 2, '2023-2024', 1.75, 1.98, 4),
('20230013', 1, '2023-2024', 1.70, 1.70, 3),
('20230013', 2, '2023-2024', 1.40, 1.55, 8),
('20230014', 1, '2023-2024', 1.10, 1.10, 9),
('20230014', 2, '2023-2024', 0.75, 0.92, 16),
('20230015', 1, '2023-2024', 2.05, 2.05, 0),
('20230015', 2, '2023-2024', 1.80, 1.92, 3);

SET IDENTITY_INSERT canh_bao_hoc_vu ON;
INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES
(1, 'CB-20232-20230009', '20230009', 2, '2023-2024', 'MUC_1', 1.65, N'GPA học kỳ < 2.0 (1.65/4.0)', '2024-07-05', 'DA_TU_VAN'),
(2, 'CB-20232-20230010', '20230010', 2, '2023-2024', 'MUC_2', 1.30, N'GPA học kỳ < 1.5 liền tiếp', '2024-07-05', 'DA_TU_VAN'),
(3, 'CB-20232-20230011', '20230011', 2, '2023-2024', 'BUOC_THOI_HOC', 0.80, N'GPA học kỳ < 1.0 và nợ > 12 tín chỉ', '2024-07-05', 'DA_TU_VAN'),
(4, 'CB-20232-20230012', '20230012', 2, '2023-2024', 'MUC_1', 1.75, N'GPA học kỳ < 2.0 (1.75/4.0)', '2024-07-05', 'DANG_THEO_DOI'),
(5, 'CB-20232-20230013', '20230013', 2, '2023-2024', 'MUC_2', 1.40, N'GPA học kỳ < 1.5 (1.40/4.0)', '2024-07-05', 'DANG_THEO_DOI'),
(6, 'CB-20232-20230014', '20230014', 2, '2023-2024', 'BUOC_THOI_HOC', 0.75, N'GPA < 1.0 và nợ tín chỉ quá quy định', '2024-07-05', 'CHUA_TU_VAN'),
(7, 'CB-20232-20230015', '20230015', 2, '2023-2024', 'MUC_1', 1.80, N'GPA học kỳ < 2.0 (1.80/4.0)', '2024-07-05', 'CHUA_TU_VAN');
SET IDENTITY_INSERT canh_bao_hoc_vu OFF;

SET IDENTITY_INSERT nhat_ky_tu_van ON;
INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES
(1, '20230009', 'CV001', 1, '2024-07-10', N'Trực tiếp', N'Tư vấn cải thiện điểm môn Lập trình mạng và Toán rời rạc', N'Học lệch, nghỉ học nhiều buổi', N'Lập thời gian biểu tự học 2h/ngày, tham gia nhóm học tập', N'Cam kết đạt GPA >= 2.5 kỳ tới'),
(2, '20230010', 'CV001', 2, '2024-07-12', N'Trực tiếp', N'Phân tích nguyên nhân nợ môn và định hướng đăng ký học lại', N'Đi làm thêm quá giờ, không nộp bài tập', N'Giảm giờ làm thêm, đăng ký học lại 2 môn điểm F', N'Cam kết đi học chuyên cần và trả hết 2 môn nợ'),
(3, '20230011', 'CV001', 3, '2024-07-15', N'Trực tiếp có PH', N'Gặp mặt phụ huynh và sinh viên về nguy cơ buộc thôi học', N'Không theo kịp chương trình, bỏ thi', N'Làm đơn xin hoãn buộc thôi học theo diện xem xét, học lại tập trung', N'Phụ huynh cam kết giám sát chặt chẽ'),
(4, '20230012', 'CV001', 4, '2024-07-18', N'Trực tuyến (Zoom)', N'Hướng dẫn phương pháp học môn Cơ sở dữ liệu', N'Chưa nắm vững cú pháp SQL và mô hình ER', N'Được phân công bạn khá trong lớp kèm cặp', N'Cam kết hoàn thành đầy đủ bài tập thực hành'),
(5, '20230013', 'CV002', 5, '2024-07-20', N'Trực tiếp', N'Tư vấn phương pháp học các môn chuyên ngành Kinh tế xây dựng', N'Khó khăn trong việc hiểu bài giảng đồ án', N'Tham gia các buổi phụ đạo của Khoa', N'Cam kết nộp đồ án đúng tiến độ');
SET IDENTITY_INSERT nhat_ky_tu_van OFF;

INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES
('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Quản trị viên Hệ thống', 'admin@huce.edu.vn', 'ADMIN', NULL),
('cv_nguynvanan', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'TS. Nguyễn Văn An', 'an.nv@huce.edu.vn', 'CO_VAN', 'CV001'),
('cv_tranthibinh', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'ThS. Trần Thị Bình', 'binh.tt@huce.edu.vn', 'CO_VAN', 'CV002'),
('quanly', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Trưởng khoa CNTT', 'quanly.cntt@huce.edu.vn', 'QUAN_LY', NULL),
('20230001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nguyễn Văn Nam', 'sv20230001@huce.edu.vn', 'SINH_VIEN', '20230001'),
('20230009', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Phạm Minh Tuấn', 'sv20230009@huce.edu.vn', 'SINH_VIEN', '20230009'),
('20230010', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Vũ Đức Hải', 'sv20230010@huce.edu.vn', 'SINH_VIEN', '20230010');

INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES
('TB-T1-1001', N'THÔNG BÁO BIỂU DƯƠNG HỌC TẬP XUẤT SẮC (TIER 1)', N'Tuyên dương các sinh viên thuộc nhóm Tier 1 có GPA >= 3.2. Đủ điều kiện xét cấp học bổng khuyến khích học tập kỳ tới.', 'TIER_1', 'ALL', NULL, GETDATE(), N'TS. Nguyễn Văn An', 4, 'DA_GUI'),
('TB-T2-1002', N'NHẮC NHỞ DUY TRÌ TIẾN ĐỘ HỌC TẬP VÀ ĐĂNG KÝ TÍN CHỈ (TIER 2)', N'Nhắc nhở các sinh viên nhóm Tier 2 (2.0 <= GPA < 3.2) chủ động đăng ký môn học và hoàn thành các môn tiên quyết.', 'TIER_2', 'ALL', NULL, GETDATE(), N'TS. Nguyễn Văn An', 4, 'DA_GUI'),
('TB-T3-1003', N'CẢNH BÁO HỌC VỤ & LỊCH TƯ VẤN BẮT BUỘC (TIER 3)', N'Yêu cầu các sinh viên có GPA < 2.0 hoặc nợ tín chỉ liên hệ ngay Cố vấn học tập để làm kế hoạch học tập cải thiện.', 'TIER_3', 'ALL', NULL, GETDATE(), N'Phòng Đào Tạo', 7, 'DA_GUI'),
('TB-SV-20230001', N'Biểu dương thành tích học tập xuất sắc học kỳ 2', N'Thầy chúc mừng em Nguyễn Văn Nam đã đạt GPA 3.65 trong học kỳ vừa rồi. Em tiếp tục phát huy để nhận học bổng khuyến khích học tập của Trường nhé!', 'CA_NHAN', '68IT1', '20230001', GETDATE(), N'TS. Nguyễn Văn An', 1, 'DA_GUI'),
('TB-SV-20230009', N'Lịch hẹn tư vấn riêng và kế hoạch cải thiện học tập', N'Chào em Tuấn, do kết quả học kỳ 2 bị cảnh báo học vụ mức 1, thầy hẹn em 14h00 chiều thứ Tư tới phòng CVHT để trao đổi và lập kế hoạch học lại các môn nợ nhé.', 'CA_NHAN', '68IT1', '20230009', GETDATE(), N'TS. Nguyễn Văn An', 1, 'DA_GUI'),
('TB-SV-20230010', N'Thông báo cảnh báo học vụ Mức 2 và cảnh báo nguy cơ thôi học', N'Em Hải liên hệ ngay với thầy trong tuần này để nộp bản cam kết học tập và giảm giờ làm thêm theo đúng quy chế học vụ.', 'CA_NHAN', '68IT1', '20230010', GETDATE(), N'TS. Nguyễn Văn An', 1, 'DA_GUI');

SET IDENTITY_INSERT lich_giang_day ON;
INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES
(1, 'CV001', N'TS. Nguyễn Văn An', '68IT1', N'68IT1 - Công nghệ thông tin 1', N'Sinh hoạt lớp định kỳ đầu học kỳ 1', '2026-08-25', '08:00', '10:00', N'Phòng 302-H1', N'Trực tiếp', N'SINH_HOAT_LOP', N'HOAN_THANH', N'Triển khai kế hoạch năm học mới và rà soát kết quả học tập'),
(2, 'CV001', N'TS. Nguyễn Văn An', '68IT1', N'68IT1 - Công nghệ thông tin 1', N'Tư vấn học tập sinh viên cảnh báo học vụ', '2026-09-02', '14:00', '16:00', N'Phòng CVHT Khoa CNTT', N'Trực tiếp', N'TU_VAN_HOC_TAP', N'SAP_DIEN_RA', N'Gặp mặt tư vấn riêng các sinh viên Mức 1 và Mức 2'),
(3, 'CV002', N'ThS. Trần Thị Bình', '68KX1', N'68KX1 - Kinh tế xây dựng 1', N'Sinh hoạt lớp và phổ biến quy chế học vụ', '2026-09-05', '09:00', '11:00', N'Phòng 205-A1', N'Trực tiếp', N'SINH_HOAT_LOP', N'SAP_DIEN_RA', N'Phổ biến quy chế cảnh báo học vụ mới nhất');
SET IDENTITY_INSERT lich_giang_day OFF;

SET IDENTITY_INSERT diem_danh ON;
INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES
(1, 1, '20230001', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ, tích cực tham gia'),
(2, 1, '20230002', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ'),
(3, 1, '20230003', '2026-08-25', 'ON_TIME', N'Có mặt đúng giờ'),
(4, 1, '20230009', '2026-08-25', 'LATE', N'Đi muộn 15 phút, đã nhắc nhở'),
(5, 1, '20230010', '2026-08-25', 'ABSENT', N'Vắng mặt không phép - đã ghi nhận cảnh báo'),
(6, 1, '20230011', '2026-08-25', 'ABSENT', N'Vắng mặt không phép');
SET IDENTITY_INSERT diem_danh OFF;
