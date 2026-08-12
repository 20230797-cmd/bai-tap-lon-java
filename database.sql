-- ============================================================
-- DATABASE SCHEMA: QUẢN LÝ CỐ VẤN HỌC TẬP VÀ CẢNH BÁO HỌC VỤ
-- Đề tài Bài tập lớn môn Công nghệ Java (Java Swing + JDBC + MySQL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ql_canhbao_hocvu` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ql_canhbao_hocvu`;

-- 1. Bảng Cố vấn học tập (CVHT)
DROP TABLE IF EXISTS `nhat_ky_tu_van`;
DROP TABLE IF EXISTS `canh_bao_hoc_vu`;
DROP TABLE IF EXISTS `ket_qua_hoc_tap`;
DROP TABLE IF EXISTS `sinh_vien`;
DROP TABLE IF EXISTS `lop_hoc`;
DROP TABLE IF EXISTS `co_van_hoc_tap`;
DROP TABLE IF EXISTS `tai_khoan`;

CREATE TABLE `co_van_hoc_tap` (
    `ma_cvht` VARCHAR(20) NOT NULL,
    `ho_ten` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `so_dien_thoai` VARCHAR(15),
    `khoa` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`ma_cvht`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bảng Lớp học
CREATE TABLE `lop_hoc` (
    `ma_lop` VARCHAR(20) NOT NULL,
    `ten_lop` VARCHAR(100) NOT NULL,
    `khoa` VARCHAR(100) NOT NULL,
    `khoa_hoc` INT DEFAULT 2023,
    `ma_cvht` VARCHAR(20),
    PRIMARY KEY (`ma_lop`),
    CONSTRAINT `fk_lophoc_cvht` FOREIGN KEY (`ma_cvht`) REFERENCES `co_van_hoc_tap` (`ma_cvht`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng Sinh viên
CREATE TABLE `sinh_vien` (
    `ma_sv` VARCHAR(20) NOT NULL,
    `ho_ten` VARCHAR(100) NOT NULL,
    `ngay_sinh` DATE,
    `gioi_tinh` VARCHAR(10) DEFAULT 'Nam',
    `email` VARCHAR(100),
    `so_dien_thoai` VARCHAR(15),
    `ma_lop` VARCHAR(20) NOT NULL,
    `trang_thai` ENUM('DANG_HOC', 'CANH_BAO_1', 'CANH_BAO_2', 'BUOC_THOI_HOC', 'DA_TOT_NGHIEP') DEFAULT 'DANG_HOC',
    PRIMARY KEY (`ma_sv`),
    CONSTRAINT `fk_sinhvien_lop` FOREIGN KEY (`ma_lop`) REFERENCES `lop_hoc` (`ma_lop`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng Kết quả học tập
CREATE TABLE `ket_qua_hoc_tap` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_sv` VARCHAR(20) NOT NULL,
    `hoc_ky` INT NOT NULL,
    `nam_hoc` VARCHAR(20) NOT NULL,
    `gpa_hoc_ky` DOUBLE NOT NULL DEFAULT 0.0,
    `gpa_tich_luy` DOUBLE NOT NULL DEFAULT 0.0,
    `so_tin_chi_no` INT DEFAULT 0,
    UNIQUE KEY `uk_sv_hk_nh` (`ma_sv`, `hoc_ky`, `nam_hoc`),
    CONSTRAINT `fk_kqht_sv` FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bảng Cảnh báo học vụ
CREATE TABLE `canh_bao_hoc_vu` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_canh_bao` VARCHAR(30) UNIQUE NOT NULL,
    `ma_sv` VARCHAR(20) NOT NULL,
    `hoc_ky` INT NOT NULL,
    `nam_hoc` VARCHAR(20) NOT NULL,
    `muc_canh_bao` ENUM('MUC_1', 'MUC_2', 'BUOC_THOI_HOC') NOT NULL,
    `gpa_xet_duyet` DOUBLE NOT NULL,
    `ly_do` TEXT,
    `ngay_quyet_dinh` DATE DEFAULT (CURRENT_DATE),
    `trang_thai_tu_van` ENUM('CHUA_TU_VAN', 'DA_TU_VAN', 'DANG_THEO_DOI') DEFAULT 'CHUA_TU_VAN',
    CONSTRAINT `fk_cbhv_sv` FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Bảng Nhật ký tư vấn
CREATE TABLE `nhat_ky_tu_van` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `ma_sv` VARCHAR(20) NOT NULL,
    `ma_cvht` VARCHAR(20) NOT NULL,
    `id_canh_bao` INT,
    `ngay_tu_van` DATE NOT NULL,
    `hinh_thuc` VARCHAR(50) DEFAULT 'Trực tiếp',
    `noi_dung` TEXT NOT NULL,
    `nguyen_nhan` TEXT,
    `giai_phap` TEXT,
    `cam_ket_sinh_vien` TEXT,
    CONSTRAINT `fk_nktv_sv` FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_nktv_cvht` FOREIGN KEY (`ma_cvht`) REFERENCES `co_van_hoc_tap` (`ma_cvht`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_nktv_cbhv` FOREIGN KEY (`id_canh_bao`) REFERENCES `canh_bao_hoc_vu` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Bảng Tài khoản hệ thống
CREATE TABLE `tai_khoan` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `ten_dang_nhap` VARCHAR(50) UNIQUE NOT NULL,
    `mat_khau` VARCHAR(255) NOT NULL,
    `ho_ten` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100),
    `vai_tro` ENUM('ADMIN', 'CO_VAN', 'QUAN_LY') DEFAULT 'CO_VAN',
    `ma_ref` VARCHAR(20),
    `ngay_tao` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- DỮ LIỆU MẪU ĐẦY ĐỦ (15 SINH VIÊN + CẢNH BÁO + NHẬT KÝ TƯ VẤN)
-- Mật khẩu mặc định: 123456
-- ============================================================

INSERT INTO `co_van_hoc_tap` (`ma_cvht`, `ho_ten`, `email`, `so_dien_thoai`, `khoa`) VALUES
('CV001', 'TS. Nguyễn Văn An', 'an.nv@huce.edu.vn', '0912345678', 'Công nghệ thông tin'),
('CV002', 'ThS. Trần Thị Bình', 'binh.tt@huce.edu.vn', '0987654321', 'Công nghệ thông tin'),
('CV003', 'PGS.TS. Lê Hoàng Cường', 'cuong.lh@huce.edu.vn', '0905112233', 'Kinh tế xây dựng');

INSERT INTO `lop_hoc` (`ma_lop`, `ten_lop`, `khoa`, `khoa_hoc`, `ma_cvht`) VALUES
('68IT1', '68IT1 - Công nghệ thông tin 1', 'Công nghệ thông tin', 2023, 'CV001'),
('68IT2', '68IT2 - Công nghệ thông tin 2', 'Công nghệ thông tin', 2023, 'CV002'),
('68KX1', '68KX1 - Kinh tế xây dựng 1', 'Kinh tế xây dựng', 2023, 'CV003');

INSERT INTO `sinh_vien` (`ma_sv`, `ho_ten`, `ngay_sinh`, `gioi_tinh`, `email`, `so_dien_thoai`, `ma_lop`, `trang_thai`) VALUES
('20230001', 'Phạm Minh Đức', '2005-03-15', 'Nam', 'duc.20230001@huce.edu.vn', '0971112233', '68IT1', 'DANG_HOC'),
('20230002', 'Nguyễn Thị Hoa', '2005-08-20', 'Nữ', 'hoa.20230002@huce.edu.vn', '0972223344', '68IT1', 'CANH_BAO_1'),
('20230003', 'Lê Tuấn Anh', '2005-11-05', 'Nam', 'anh.20230003@huce.edu.vn', '0973334455', '68IT1', 'CANH_BAO_2'),
('20230004', 'Vũ Hoàng Nam', '2005-01-12', 'Nam', 'nam.20230004@huce.edu.vn', '0974445566', '68IT2', 'BUOC_THOI_HOC'),
('20230005', 'Hoàng Bảo Ngọc', '2005-06-25', 'Nữ', 'ngoc.20230005@huce.edu.vn', '0975556677', '68IT2', 'DANG_HOC'),
('20230006', 'Trần Quang Hải', '2005-09-18', 'Nam', 'hai.20230006@huce.edu.vn', '0976667788', '68IT2', 'CANH_BAO_1'),
('20230007', 'Đỗ Thùy Trang', '2005-04-30', 'Nữ', 'trang.20230007@huce.edu.vn', '0977778899', '68IT2', 'DANG_HOC'),
('20230008', 'Bùi Anh Dũng', '2005-12-10', 'Nam', 'dung.20230008@huce.edu.vn', '0978889900', '68IT1', 'CANH_BAO_2'),
('20230009', 'Đặng Khánh Linh', '2005-02-14', 'Nữ', 'linh.20230009@huce.edu.vn', '0979990011', '68KX1', 'DANG_HOC'),
('20230010', 'Ngô Gia Huy', '2005-07-08', 'Nam', 'huy.20230010@huce.edu.vn', '0981112244', '68KX1', 'CANH_BAO_1'),
('20230011', 'Lý Thanh Hà', '2005-10-22', 'Nữ', 'ha.20230011@huce.edu.vn', '0982223355', '68KX1', 'DANG_HOC'),
('20230012', 'Dương Văn Khoa', '2005-05-01', 'Nam', 'khoa.20230012@huce.edu.vn', '0983334466', '68IT1', 'BUOC_THOI_HOC'),
('20230013', 'Trịnh Mai Phương', '2005-08-16', 'Nữ', 'phuong.20230013@huce.edu.vn', '0984445577', '68IT2', 'DANG_HOC'),
('20230014', 'Phan Văn Khánh', '2005-01-29', 'Nam', 'khanh.20230014@huce.edu.vn', '0985556688', '68IT1', 'CANH_BAO_1'),
('20230015', 'Hồ Thị Tuyết', '2005-11-19', 'Nữ', 'tuyet.20230015@huce.edu.vn', '0986667799', '68KX1', 'DANG_HOC');

INSERT INTO `ket_qua_hoc_tap` (`ma_sv`, `hoc_ky`, `nam_hoc`, `gpa_hoc_ky`, `gpa_tich_luy`, `so_tin_chi_no`) VALUES
('20230001', 1, '2023-2024', 3.45, 3.45, 0),
('20230001', 2, '2023-2024', 3.60, 3.52, 0),
('20230002', 1, '2023-2024', 2.10, 2.10, 3),
('20230002', 2, '2023-2024', 1.80, 1.95, 7),
('20230003', 1, '2023-2024', 1.40, 1.40, 9),
('20230003', 2, '2023-2024', 1.35, 1.38, 14),
('20230004', 1, '2023-2024', 0.90, 0.90, 16),
('20230004', 2, '2023-2024', 0.85, 0.88, 22),
('20230005', 1, '2023-2024', 3.20, 3.20, 0),
('20230006', 2, '2023-2024', 1.82, 1.85, 6),
('20230008', 2, '2023-2024', 1.40, 1.42, 12),
('20230010', 2, '2023-2024', 1.90, 1.92, 4),
('20230012', 2, '2023-2024', 0.70, 0.75, 25),
('20230014', 2, '2023-2024', 1.85, 1.88, 5);

INSERT INTO `canh_bao_hoc_vu` (`ma_canh_bao`, `ma_sv`, `hoc_ky`, `nam_hoc`, `muc_canh_bao`, `gpa_xet_duyet`, `ly_do`, `ngay_quyet_dinh`, `trang_thai_tu_van`) VALUES
('CB-20232-20230002', '20230002', 2, '2023-2024', 'MUC_1', 1.95, 'GPA tích lũy dưới 2.0 (1.95) và nợ 7 tín chỉ', '2024-07-01', 'DA_TU_VAN'),
('CB-20232-20230003', '20230003', 2, '2023-2024', 'MUC_2', 1.38, 'GPA tích lũy dưới 1.5 (1.38) trong 2 học kỳ liên tiếp', '2024-07-01', 'CHUA_TU_VAN'),
('CB-20232-20230004', '20230004', 2, '2023-2024', 'BUOC_THOI_HOC', 0.88, 'GPA tích lũy dưới 1.0 (0.88) quá thời gian quy định', '2024-07-01', 'CHUA_TU_VAN'),
('CB-20232-20230006', '20230006', 2, '2023-2024', 'MUC_1', 1.85, 'GPA tích lũy dưới 2.0 (1.85) và nợ 6 tín chỉ', '2024-07-01', 'CHUA_TU_VAN'),
('CB-20232-20230008', '20230008', 2, '2023-2024', 'MUC_2', 1.42, 'GPA tích lũy dưới 1.5 (1.42) và nợ 12 tín chỉ', '2024-07-01', 'DA_TU_VAN');

INSERT INTO `nhat_ky_tu_van` (`ma_sv`, `ma_cvht`, `id_canh_bao`, `ngay_tu_van`, `hinh_thuc`, `noi_dung`, `nguyen_nhan`, `giai_phap`, `cam_ket_sinh_vien`) VALUES
('20230002', 'CV001', 1, '2024-07-10', 'Trực tiếp', 'Gặp mặt tư vấn sinh viên bị cảnh báo học vụ Mức 1 học kỳ 2 năm 2023-2024', 'Nghỉ học nhiều môn Giải tích do ốm kéo dài', 'Đăng ký học cải thiện vào học kỳ hè', 'Cam kết đạt GPA >= 2.5'),
('20230008', 'CV001', 5, '2024-07-12', 'Online (Teams/Zoom)', 'Tư vấn sinh viên bị cảnh báo học vụ Mức 2', 'Đi làm thêm quá sức dẫn tới bỏ tiết', 'Giảm giờ làm thêm, đăng ký học lại các môn nợ', 'Cam kết đi học đúng giờ và nộp bài bài tập');

INSERT INTO `tai_khoan` (`ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `vai_tro`, `ma_ref`) VALUES
('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Quản trị hệ thống', 'admin@huce.edu.vn', 'ADMIN', NULL),
('cv_nguynvanan', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'TS. Nguyễn Văn An', 'an.nv@huce.edu.vn', 'CO_VAN', 'CV001'),
('cv_tranthibinh', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ThS. Trần Thị Bình', 'binh.tt@huce.edu.vn', 'CO_VAN', 'CV002'),
('quanly', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Trưởng khoa CNTT', 'khoacntt@huce.edu.vn', 'QUAN_LY', NULL);
