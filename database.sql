-- ============================================================
-- DATABASE SCHEMA & D? LI?U ??NG B? CHU?N (SQLITE ZERO-CONFIG)
-- H? th?ng Qu?n l? C? v?n H?c t?p & C?nh b?o H?c v?
-- M?t kh?u t?i kho?n m?u: 123456
-- ============================================================

DROP TABLE IF EXISTS `diem_danh`;
DROP TABLE IF EXISTS `lich_giang_day`;
DROP TABLE IF EXISTS `thong_bao`;
DROP TABLE IF EXISTS `nhat_ky_tu_van`;
DROP TABLE IF EXISTS `canh_bao_hoc_vu`;
DROP TABLE IF EXISTS `ket_qua_hoc_tap`;
DROP TABLE IF EXISTS `sinh_vien`;
DROP TABLE IF EXISTS `lop_hoc`;
DROP TABLE IF EXISTS `co_van_hoc_tap`;
DROP TABLE IF EXISTS `tai_khoan`;

-- 1. B?NG C? V?N H?C T?P
CREATE TABLE `co_van_hoc_tap` (
  `ma_cvht` VARCHAR(20) PRIMARY KEY,
  `ho_ten` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `so_dien_thoai` VARCHAR(20),
  `khoa` VARCHAR(100) NOT NULL
);

-- 2. B?NG L?P H?C
CREATE TABLE `lop_hoc` (
  `ma_lop` VARCHAR(20) PRIMARY KEY,
  `ten_lop` VARCHAR(100) NOT NULL,
  `khoa` VARCHAR(100) NOT NULL,
  `khoa_hoc` INT NOT NULL,
  `ma_cvht` VARCHAR(20),
  CONSTRAINT `fk_lop_covan` FOREIGN KEY (`ma_cvht`) REFERENCES `co_van_hoc_tap` (`ma_cvht`) ON DELETE SET NULL
);


CREATE TABLE `sinh_vien` (
  `ma_sv` VARCHAR(20) PRIMARY KEY,
  `ho_ten` VARCHAR(100) NOT NULL,
  `ngay_sinh` DATE,
  `gioi_tinh` VARCHAR(10),
  `email` VARCHAR(100),
  `so_dien_thoai` VARCHAR(20),
  `ma_lop` VARCHAR(20) NOT NULL,
  `trang_thai` VARCHAR(30) DEFAULT 'DANG_HOC',
  CONSTRAINT `fk_sinhvien_lop` FOREIGN KEY (`ma_lop`) REFERENCES `lop_hoc` (`ma_lop`) ON DELETE CASCADE
);


CREATE TABLE `ket_qua_hoc_tap` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_sv` VARCHAR(20) NOT NULL,
  `hoc_ky` INT NOT NULL,
  `nam_hoc` VARCHAR(20) NOT NULL,
  `gpa_hoc_ky` DOUBLE NOT NULL,
  `gpa_tich_luy` DOUBLE NOT NULL,
  `so_tin_chi_no` INT DEFAULT 0,
  CONSTRAINT `fk_kq_sinhvien` FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`) ON DELETE CASCADE
);

-- 5. B?NG C?NH B?O H?C V?
CREATE TABLE `canh_bao_hoc_vu` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_canh_bao` VARCHAR(50) UNIQUE NOT NULL,
  `ma_sv` VARCHAR(20) NOT NULL,
  `hoc_ky` INT NOT NULL,
  `nam_hoc` VARCHAR(20) NOT NULL,
  `muc_canh_bao` VARCHAR(30) NOT NULL,
  `gpa_xet_duyet` DOUBLE NOT NULL,
  `ly_do` VARCHAR(255),
  `ngay_quyet_dinh` DATE,
  `trang_thai_tu_van` VARCHAR(30) DEFAULT 'CHUA_TU_VAN',
  CONSTRAINT `fk_canhbao_sinhvien` FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`) ON DELETE CASCADE
);

-- 6. B?NG NH?T K? T? V?N
CREATE TABLE `nhat_ky_tu_van` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_sv` VARCHAR(20) NOT NULL,
  `ma_cvht` VARCHAR(20) NOT NULL,
  `id_canh_bao` INT,
  `ngay_tu_van` DATE NOT NULL,
  `hinh_thuc` VARCHAR(50) NOT NULL,
  `noi_dung` TEXT NOT NULL,
  `nguyen_nhan` TEXT,
  `giai_phap` TEXT,
  `cam_ket_sinh_vien` TEXT,
  CONSTRAINT `fk_nhatky_sinhvien` FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`) ON DELETE CASCADE,
  CONSTRAINT `fk_nhatky_covan` FOREIGN KEY (`ma_cvht`) REFERENCES `co_van_hoc_tap` (`ma_cvht`) ON DELETE CASCADE,
  CONSTRAINT `fk_nhatky_canhbao` FOREIGN KEY (`id_canh_bao`) REFERENCES `canh_bao_hoc_vu` (`id`) ON DELETE SET NULL
);

-- 7. B?NG T?I KHO?N NG??I D?NG
CREATE TABLE `tai_khoan` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ten_dang_nhap` VARCHAR(50) UNIQUE NOT NULL,
  `mat_khau` VARCHAR(255) NOT NULL,
  `ho_ten` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100),
  `vai_tro` VARCHAR(20) NOT NULL,
  `ma_ref` VARCHAR(20),
  `trang_thai` INT DEFAULT 1,
  `ngay_tao` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. B?NG TH?NG B?O
CREATE TABLE `thong_bao` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_thong_bao` VARCHAR(50) UNIQUE NOT NULL,
  `tieu_de` VARCHAR(255) NOT NULL,
  `noi_dung` TEXT NOT NULL,
  `nhom_rui_ro` VARCHAR(30) DEFAULT 'ALL',
  `ma_lop` VARCHAR(20) DEFAULT 'ALL',
  `ma_sv` VARCHAR(20),
  `ngay_gui` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `nguoi_gui` VARCHAR(100),
  `so_luong_nhan` INT DEFAULT 0,
  `trang_thai` VARCHAR(30) DEFAULT 'DA_GUI'
);

-- 9. B?NG L?CH GI?NG D?Y & T? V?N
CREATE TABLE `lich_giang_day` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_cvht` VARCHAR(20) NOT NULL,
  `ten_cvht` VARCHAR(100) NOT NULL,
  `ma_lop` VARCHAR(20) NOT NULL,
  `ten_lop` VARCHAR(100) NOT NULL,
  `tieu_de` VARCHAR(200) NOT NULL,
  `ngay` DATE NOT NULL,
  `gio_bat_dau` VARCHAR(10) NOT NULL,
  `gio_ket_thuc` VARCHAR(10) NOT NULL,
  `dia_diem` VARCHAR(100) NOT NULL,
  `hinh_thuc` VARCHAR(50) NOT NULL,
  `loai_buoi` VARCHAR(50) NOT NULL,
  `trang_thai` VARCHAR(50) NOT NULL,
  `ghi_chu` TEXT
);

-- 10. B?NG ?I?M DANH
CREATE TABLE `diem_danh` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `id_lich` INT NOT NULL,
  `ma_sv` VARCHAR(20) NOT NULL,
  `ngay_diem_danh` DATE NOT NULL,
  `trang_thai` VARCHAR(30) NOT NULL,
  `ghi_chu` VARCHAR(255),
  CONSTRAINT `fk_diemdanh_lich` FOREIGN KEY (`id_lich`) REFERENCES `lich_giang_day` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_diemdanh_sv` FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`) ON DELETE CASCADE
);

-- ============================================================
-- D? LI?U M?U ??NG B? 100% (M?T KH?U: 123456)
-- ============================================================

INSERT OR REPLACE INTO `co_van_hoc_tap` (`ma_cvht`, `ho_ten`, `email`, `so_dien_thoai`, `khoa`) VALUES
('CV001', 'TS. Nguy?n V?n An', 'an.nv@huce.edu.vn', '0912345678', 'C?ng ngh? th?ng tin'),
('CV002', 'ThS. Tr?n Th? B?nh', 'binh.tt@huce.edu.vn', '0987654321', 'Kinh t? x?y d?ng');

INSERT OR REPLACE INTO `lop_hoc` (`ma_lop`, `ten_lop`, `khoa`, `khoa_hoc`, `ma_cvht`) VALUES
('68IT1', '68IT1 - C?ng ngh? th?ng tin 1', 'C?ng ngh? th?ng tin', 2023, 'CV001'),
('68IT2', '68IT2 - C?ng ngh? th?ng tin 2', 'C?ng ngh? th?ng tin', 2023, 'CV001'),
('68KX1', '68KX1 - Kinh t? x?y d?ng 1', 'Kinh t? x?y d?ng', 2023, 'CV002'),
('68XD1', '68XD1 - X?y d?ng d?n d?ng 1', 'X?y d?ng d?n d?ng', 2023, 'CV002');

INSERT OR REPLACE INTO `sinh_vien` (`ma_sv`, `ho_ten`, `ngay_sinh`, `gioi_tinh`, `email`, `so_dien_thoai`, `ma_lop`, `trang_thai`) VALUES
('20230001', 'Nguy?n V?n Nam', '2005-03-15', 'Nam', 'sv20230001@huce.edu.vn', '0911000001', '68IT1', 'DANG_HOC'),
('20230002', 'Tr?n Th? Lan', '2005-07-22', 'N?', 'sv20230002@huce.edu.vn', '0911000002', '68IT1', 'DANG_HOC'),
('20230003', 'L? Ho?ng Long', '2005-11-05', 'Nam', 'sv20230003@huce.edu.vn', '0911000003', '68IT1', 'DANG_HOC'),
('20230004', '??ng Th?y D??ng', '2005-04-12', 'N?', 'sv20230004@huce.edu.vn', '0911000004', '68IT2', 'DANG_HOC'),
('20230005', 'B?i Quang Huy', '2005-08-25', 'Nam', 'sv20230005@huce.edu.vn', '0911000005', '68IT2', 'DANG_HOC'),
('20230006', 'Mai Ph??ng Th?o', '2005-01-08', 'N?', 'sv20230006@huce.edu.vn', '0911000006', '68KX1', 'DANG_HOC'),
('20230007', 'Tr?nh Qu?c Anh', '2005-10-10', 'Nam', 'sv20230007@huce.edu.vn', '0911000007', '68KX1', 'DANG_HOC'),
('20230008', 'Phan B?o Ch?u', '2005-09-17', 'N?', 'sv20230008@huce.edu.vn', '0911000008', '68XD1', 'DANG_HOC'),
('20230009', 'Ph?m Minh Tu?n', '2005-02-18', 'Nam', 'sv20230009@huce.edu.vn', '0911000009', '68IT1', 'CANH_BAO_1'),
('20230010', 'V? ??c H?i', '2005-09-30', 'Nam', 'sv20230010@huce.edu.vn', '0911000010', '68IT1', 'CANH_BAO_2'),
('20230011', 'Ho?ng Qu?c B?o', '2005-12-14', 'Nam', 'sv20230011@huce.edu.vn', '0911000011', '68IT1', 'BUOC_THOI_HOC'),
('20230012', '?? Gia H?ng', '2005-06-19', 'Nam', 'sv20230012@huce.edu.vn', '0911000012', '68IT2', 'CANH_BAO_1'),
('20230013', 'Ng? ??nh Kh?i', '2005-05-03', 'Nam', 'sv20230013@huce.edu.vn', '0911000013', '68KX1', 'CANH_BAO_2'),
('20230014', 'D??ng Tu?n Ki?t', '2005-03-29', 'Nam', 'sv20230014@huce.edu.vn', '0911000014', '68KX1', 'BUOC_THOI_HOC'),
('20230015', 'Cao Thanh T?ng', '2005-11-21', 'Nam', 'sv20230015@huce.edu.vn', '0911000015', '68XD1', 'CANH_BAO_1');

INSERT OR REPLACE INTO `ket_qua_hoc_tap` (`ma_sv`, `hoc_ky`, `nam_hoc`, `gpa_hoc_ky`, `gpa_tich_luy`, `so_tin_chi_no`) VALUES
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

INSERT OR REPLACE INTO `canh_bao_hoc_vu` (`id`, `ma_canh_bao`, `ma_sv`, `hoc_ky`, `nam_hoc`, `muc_canh_bao`, `gpa_xet_duyet`, `ly_do`, `ngay_quyet_dinh`, `trang_thai_tu_van`) VALUES
(1, 'CB-20232-20230009', '20230009', 2, '2023-2024', 'MUC_1', 1.65, 'GPA h?c k? < 2.0 (1.65/4.0)', '2024-07-05', 'DA_TU_VAN'),
(2, 'CB-20232-20230010', '20230010', 2, '2023-2024', 'MUC_2', 1.30, 'GPA h?c k? < 1.5 li?n ti?p', '2024-07-05', 'DA_TU_VAN'),
(3, 'CB-20232-20230011', '20230011', 2, '2023-2024', 'BUOC_THOI_HOC', 0.80, 'GPA h?c k? < 1.0 v? n? > 12 t?n ch?', '2024-07-05', 'DA_TU_VAN'),
(4, 'CB-20232-20230012', '20230012', 2, '2023-2024', 'MUC_1', 1.75, 'GPA h?c k? < 2.0 (1.75/4.0)', '2024-07-05', 'DANG_THEO_DOI'),
(5, 'CB-20232-20230013', '20230013', 2, '2023-2024', 'MUC_2', 1.40, 'GPA h?c k? < 1.5 (1.40/4.0)', '2024-07-05', 'DANG_THEO_DOI'),
(6, 'CB-20232-20230014', '20230014', 2, '2023-2024', 'BUOC_THOI_HOC', 0.75, 'GPA < 1.0 v? n? t?n ch? qu? quy ??nh', '2024-07-05', 'CHUA_TU_VAN'),
(7, 'CB-20232-20230015', '20230015', 2, '2023-2024', 'MUC_1', 1.80, 'GPA h?c k? < 2.0 (1.80/4.0)', '2024-07-05', 'CHUA_TU_VAN');

INSERT OR REPLACE INTO `nhat_ky_tu_van` (`id`, `ma_sv`, `ma_cvht`, `id_canh_bao`, `ngay_tu_van`, `hinh_thuc`, `noi_dung`, `nguyen_nhan`, `giai_phap`, `cam_ket_sinh_vien`) VALUES
(1, '20230009', 'CV001', 1, '2024-07-10', 'Tr?c ti?p', 'T? v?n c?i thi?n ?i?m m?n L?p tr?nh m?ng v? To?n r?i r?c', 'H?c l?ch, ngh? h?c nhi?u bu?i', 'L?p th?i gian bi?u t? h?c 2h/ng?y, tham gia nh?m h?c t?p', 'Cam k?t ??t GPA >= 2.5 k? t?i'),
(2, '20230010', 'CV001', 2, '2024-07-12', 'Tr?c ti?p', 'Ph?n t?ch nguy?n nh?n n? m?n v? ??nh h??ng ??ng k? h?c l?i', '?i l?m th?m qu? gi?, kh?ng n?p b?i t?p', 'Gi?m gi? l?m th?m, ??ng k? h?c l?i 2 m?n ?i?m F', 'Cam k?t ?i h?c chuy?n c?n v? tr? h?t 2 m?n n?'),
(3, '20230011', 'CV001', 3, '2024-07-15', 'Tr?c ti?p c? PH', 'G?p m?t ph? huynh v? sinh vi?n v? nguy c? bu?c th?i h?c', 'Kh?ng theo k?p ch??ng tr?nh, b? thi', 'L?m ??n xin ho?n bu?c th?i h?c theo di?n xem x?t, h?c l?i t?p trung', 'Ph? huynh cam k?t gi?m s?t ch?t ch?'),
(4, '20230012', 'CV001', 4, '2024-07-18', 'Tr?c tuy?n (Zoom)', 'H??ng d?n ph??ng ph?p h?c m?n C? s? d? li?u', 'Ch?a n?m v?ng c? ph?p SQL v? m? h?nh ER', '???c ph?n c?ng b?n kh? trong l?p k?m c?p', 'Cam k?t ho?n th?nh ??y ?? b?i t?p th?c h?nh'),
(5, '20230013', 'CV002', 5, '2024-07-20', 'Tr?c ti?p', 'T? v?n ph??ng ph?p h?c c?c m?n chuy?n ng?nh Kinh t? x?y d?ng', 'Kh? kh?n trong vi?c hi?u b?i gi?ng ?? ?n', 'Tham gia c?c bu?i ph? ??o c?a Khoa', 'Cam k?t n?p ?? ?n ??ng ti?n ??');

INSERT OR REPLACE INTO `tai_khoan` (`ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `vai_tro`, `ma_ref`) VALUES
('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Quản trị viên Hệ thống', 'admin@huce.edu.vn', 'ADMIN', NULL),
('cv_nguynvanan', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'TS. Nguyễn Văn An', 'an.nv@huce.edu.vn', 'CO_VAN', 'CV001'),
('cv_tranthibinh', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ThS. Trần Thị Bình', 'binh.tt@huce.edu.vn', 'CO_VAN', 'CV002'),
('quanly', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Trưởng khoa CNTT', 'quanly.cntt@huce.edu.vn', 'QUAN_LY', NULL),
('20230001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nguyễn Văn Nam', 'sv20230001@huce.edu.vn', 'SINH_VIEN', '20230001'),
('20230009', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Phạm Minh Tuấn', 'sv20230009@huce.edu.vn', 'SINH_VIEN', '20230009'),
('20230010', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Vũ Đức Hải', 'sv20230010@huce.edu.vn', 'SINH_VIEN', '20230010');

INSERT OR REPLACE INTO `thong_bao` (`ma_thong_bao`, `tieu_de`, `noi_dung`, `nhom_rui_ro`, `ma_lop`, `ma_sv`, `ngay_gui`, `nguoi_gui`, `so_luong_nhan`, `trang_thai`) VALUES
('TB-T1-1001', 'THÔNG BÁO BIỂU DƯƠNG HỌC TẬP XUẤT SẮC (TIER 1)', 'Tuyên dương các sinh viên thuộc nhóm Tier 1 có GPA >= 3.2. Đủ điều kiện xét cấp học bổng khuyến khích học tập kỳ tới.', 'TIER_1', 'ALL', NULL, CURRENT_TIMESTAMP, 'TS. Nguyễn Văn An', 4, 'DA_GUI'),
('TB-T2-1002', 'NHẮC NHỞ DUY TRÌ TIẾN ĐỘ HỌC TẬP VÀ ĐĂNG KÝ TÍN CHỈ (TIER 2)', 'Nhắc nhở các sinh viên nhóm Tier 2 (2.0 <= GPA < 3.2) chủ động đăng ký môn học và hoàn thành các môn tiên quyết.', 'TIER_2', 'ALL', NULL, CURRENT_TIMESTAMP, 'TS. Nguyễn Văn An', 4, 'DA_GUI'),
('TB-T3-1003', 'CẢNH BÁO HỌC VỤ & LỊCH TƯ VẤN BẮT BUỘC (TIER 3)', 'Yêu cầu các sinh viên có GPA < 2.0 hoặc nợ tín chỉ liên hệ ngay Cố vấn học tập để làm kế hoạch học tập cải thiện.', 'TIER_3', 'ALL', NULL, CURRENT_TIMESTAMP, 'Phòng Đào Tạo', 7, 'DA_GUI'),
('TB-SV-20230001', 'Biểu dương thành tích học tập xuất sắc học kỳ 2', 'Thầy chúc mừng em Nguyễn Văn Nam đã đạt GPA 3.65 trong học kỳ vừa rồi. Em tiếp tục phát huy để nhận học bổng khuyến khích học tập của Trường nhé!', 'CA_NHAN', '68IT1', '20230001', CURRENT_TIMESTAMP, 'TS. Nguyễn Văn An', 1, 'DA_GUI'),
('TB-SV-20230009', 'Lịch hẹn tư vấn riêng và kế hoạch cải thiện học tập', 'Chào em Tuấn, do kết quả học kỳ 2 bị cảnh báo học vụ mức 1, thầy hẹn em 14h00 chiều thứ Tư tới phòng CVHT để trao đổi và lập kế hoạch học lại các môn nợ nhé.', 'CA_NHAN', '68IT1', '20230009', CURRENT_TIMESTAMP, 'TS. Nguyễn Văn An', 1, 'DA_GUI'),
('TB-SV-20230010', 'Thông báo cảnh báo học vụ Mức 2 và cảnh báo nguy cơ thôi học', 'Em Hải liên hệ ngay với thầy trong tuần này để nộp bản cam kết học tập và giảm giờ làm thêm theo đúng quy chế học vụ.', 'CA_NHAN', '68IT1', '20230010', CURRENT_TIMESTAMP, 'TS. Nguyễn Văn An', 1, 'DA_GUI');

INSERT OR REPLACE INTO `lich_giang_day` (`id`, `ma_cvht`, `ten_cvht`, `ma_lop`, `ten_lop`, `tieu_de`, `ngay`, `gio_bat_dau`, `gio_ket_thuc`, `dia_diem`, `hinh_thuc`, `loai_buoi`, `trang_thai`, `ghi_chu`) VALUES
(1, 'CV001', 'TS. Nguy?n V?n An', '68IT1', '68IT1 - C?ng ngh? th?ng tin 1', 'Sinh ho?t l?p ??nh k? ??u h?c k? 1', '2026-08-25', '08:00', '10:00', 'Ph?ng 302-H1', 'Tr?c ti?p', 'SINH_HOAT_LOP', 'HOAN_THANH', 'Tri?n khai k? ho?ch n?m h?c m?i v? r? so?t k?t qu? h?c t?p'),
(2, 'CV001', 'TS. Nguy?n V?n An', '68IT1', '68IT1 - C?ng ngh? th?ng tin 1', 'T? v?n h?c t?p sinh vi?n c?nh b?o h?c v?', '2026-09-02', '14:00', '16:00', 'Ph?ng CVHT Khoa CNTT', 'Tr?c ti?p', 'TU_VAN_HOC_TAP', 'SAP_DIEN_RA', 'G?p m?t t? v?n ri?ng c?c sinh vi?n M?c 1 v? M?c 2'),
(3, 'CV002', 'ThS. Tr?n Th? B?nh', '68KX1', '68KX1 - Kinh t? x?y d?ng 1', 'Sinh ho?t l?p v? ph? bi?n quy ch? h?c v?', '2026-09-05', '09:00', '11:00', 'Ph?ng 205-A1', 'Tr?c ti?p', 'SINH_HOAT_LOP', 'SAP_DIEN_RA', 'Ph? bi?n quy ch? c?nh b?o h?c v? m?i nh?t');

INSERT OR REPLACE INTO `diem_danh` (`id_lich`, `ma_sv`, `ngay_diem_danh`, `trang_thai`, `ghi_chu`) VALUES
(1, '20230001', '2026-08-25', 'ON_TIME', 'C? m?t ??ng gi?, t?ch c?c tham gia'),
(1, '20230002', '2026-08-25', 'ON_TIME', 'C? m?t ??ng gi?'),
(1, '20230003', '2026-08-25', 'ON_TIME', 'C? m?t ??ng gi?'),
(1, '20230009', '2026-08-25', 'LATE', '?i mu?n 15 ph?t, ?? nh?c nh?'),
(1, '20230010', '2026-08-25', 'ABSENT', 'V?ng m?t kh?ng ph?p - ?? ghi nh?n c?nh b?o'),
(1, '20230011', '2026-08-25', 'ABSENT', 'V?ng m?t kh?ng ph?p');
