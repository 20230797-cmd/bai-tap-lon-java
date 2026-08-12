package com.qlcvht.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/ql_canhbao_hocvu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&connectTimeout=1000&socketTimeout=1000";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "";

    private static final String SQLITE_URL = "jdbc:sqlite:ql_canhbao_hocvu.db";

    private static boolean useSQLiteFallback = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {}
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {}
    }

    public static Connection getConnection() throws SQLException {
        if (!useSQLiteFallback) {
            try {
                DriverManager.setLoginTimeout(1);
                Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
                if (conn != null && !conn.isClosed()) {
                    return conn;
                }
            } catch (Throwable e) {
                useSQLiteFallback = true;
                System.out.println("ℹ️ MySQL chưa bật trên localhost:3306. Tự động chuyển sang CSDL nhúng SQLite...");
            }
        }

        Connection sqliteConn = DriverManager.getConnection(SQLITE_URL);
        initSQLiteSchemaIfNeeded(sqliteConn);
        return sqliteConn;
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            return false;
        }
    }

    public static boolean isUsingSQLite() {
        return useSQLiteFallback;
    }

    private static synchronized void initSQLiteSchemaIfNeeded(Connection conn) {
        File dbFile = new File("ql_canhbao_hocvu.db");
        if (dbFile.exists() && dbFile.length() > 2048) return;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS co_van_hoc_tap (ma_cvht TEXT PRIMARY KEY, ho_ten TEXT NOT NULL, email TEXT, so_dien_thoai TEXT, khoa TEXT NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS lop_hoc (ma_lop TEXT PRIMARY KEY, ten_lop TEXT NOT NULL, khoa TEXT NOT NULL, khoa_hoc INT DEFAULT 2023, ma_cvht TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS sinh_vien (ma_sv TEXT PRIMARY KEY, ho_ten TEXT NOT NULL, ngay_sinh TEXT, gioi_tinh TEXT DEFAULT 'Nam', email TEXT, so_dien_thoai TEXT, ma_lop TEXT NOT NULL, trang_thai TEXT DEFAULT 'DANG_HOC');");
            stmt.execute("CREATE TABLE IF NOT EXISTS ket_qua_hoc_tap (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_sv TEXT NOT NULL, hoc_ky INT NOT NULL, nam_hoc TEXT NOT NULL, gpa_hoc_ky DOUBLE DEFAULT 0.0, gpa_tich_luy DOUBLE DEFAULT 0.0, so_tin_chi_no INT DEFAULT 0);");
            stmt.execute("CREATE TABLE IF NOT EXISTS canh_bao_hoc_vu (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_canh_bao TEXT UNIQUE NOT NULL, ma_sv TEXT NOT NULL, hoc_ky INT NOT NULL, nam_hoc TEXT NOT NULL, muc_canh_bao TEXT NOT NULL, gpa_xet_duyet DOUBLE NOT NULL, ly_do TEXT, ngay_quyet_dinh TEXT, trang_thai_tu_van TEXT DEFAULT 'CHUA_TU_VAN');");
            stmt.execute("CREATE TABLE IF NOT EXISTS nhat_ky_tu_van (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_sv TEXT NOT NULL, ma_cvht TEXT NOT NULL, id_canh_bao INT, ngay_tu_van TEXT NOT NULL, hinh_thuc TEXT DEFAULT 'Trực tiếp', noi_dung TEXT NOT NULL, nguyen_nhan TEXT, giai_phap TEXT, cam_ket_sinh_vien TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS tai_khoan (id INTEGER PRIMARY KEY AUTOINCREMENT, ten_dang_nhap TEXT UNIQUE NOT NULL, mat_khau TEXT NOT NULL, ho_ten TEXT NOT NULL, email TEXT, vai_tro TEXT DEFAULT 'CO_VAN', ma_ref TEXT, ngay_tao TEXT);");

            // Seed Data Cố vấn & Lớp học
            stmt.execute("INSERT OR IGNORE INTO co_van_hoc_tap VALUES ('CV001', 'TS. Nguyễn Văn An', 'an.nv@huce.edu.vn', '0912345678', 'Công nghệ thông tin');");
            stmt.execute("INSERT OR IGNORE INTO co_van_hoc_tap VALUES ('CV002', 'ThS. Trần Thị Bình', 'binh.tt@huce.edu.vn', '0987654321', 'Công nghệ thông tin');");
            stmt.execute("INSERT OR IGNORE INTO co_van_hoc_tap VALUES ('CV003', 'PGS.TS. Lê Hoàng Cường', 'cuong.lh@huce.edu.vn', '0905112233', 'Kinh tế xây dựng');");

            stmt.execute("INSERT OR IGNORE INTO lop_hoc VALUES ('68IT1', '68IT1 - Công nghệ thông tin 1', 'Công nghệ thông tin', 2023, 'CV001');");
            stmt.execute("INSERT OR IGNORE INTO lop_hoc VALUES ('68IT2', '68IT2 - Công nghệ thông tin 2', 'Công nghệ thông tin', 2023, 'CV002');");
            stmt.execute("INSERT OR IGNORE INTO lop_hoc VALUES ('68KX1', '68KX1 - Kinh tế xây dựng 1', 'Kinh tế xây dựng', 2023, 'CV003');");

            // Seed Data 15 Sinh viên
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230001', 'Phạm Minh Đức', '2005-03-15', 'Nam', 'duc.20230001@huce.edu.vn', '0971112233', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230002', 'Nguyễn Thị Hoa', '2005-08-20', 'Nữ', 'hoa.20230002@huce.edu.vn', '0972223344', '68IT1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230003', 'Lê Tuấn Anh', '2005-11-05', 'Nam', 'anh.20230003@huce.edu.vn', '0973334455', '68IT1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230004', 'Vũ Hoàng Nam', '2005-01-12', 'Nam', 'nam.20230004@huce.edu.vn', '0974445566', '68IT2', 'BUOC_THOI_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230005', 'Hoàng Bảo Ngọc', '2005-06-25', 'Nữ', 'ngoc.20230005@huce.edu.vn', '0975556677', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230006', 'Trần Quang Hải', '2005-09-18', 'Nam', 'hai.20230006@huce.edu.vn', '0976667788', '68IT2', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230007', 'Đỗ Thùy Trang', '2005-04-30', 'Nữ', 'trang.20230007@huce.edu.vn', '0977778899', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230008', 'Bùi Anh Dũng', '2005-12-10', 'Nam', 'dung.20230008@huce.edu.vn', '0978889900', '68IT1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230009', 'Đặng Khánh Linh', '2005-02-14', 'Nữ', 'linh.20230009@huce.edu.vn', '0979990011', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230010', 'Ngô Gia Huy', '2005-07-08', 'Nam', 'huy.20230010@huce.edu.vn', '0981112244', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230011', 'Lý Thanh Hà', '2005-10-22', 'Nữ', 'ha.20230011@huce.edu.vn', '0982223355', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230012', 'Dương Văn Khoa', '2005-05-01', 'Nam', 'khoa.20230012@huce.edu.vn', '0983334466', '68IT1', 'BUOC_THOI_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230013', 'Trịnh Mai Phương', '2005-08-16', 'Nữ', 'phuong.20230013@huce.edu.vn', '0984445577', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230014', 'Phan Văn Khánh', '2005-01-29', 'Nam', 'khanh.20230014@huce.edu.vn', '0985556688', '68IT1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230015', 'Hồ Thị Tuyết', '2005-11-19', 'Nữ', 'tuyet.20230015@huce.edu.vn', '0986667799', '68KX1', 'DANG_HOC');");

            // Seed Data GPA
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (1, '20230001', 1, '2023-2024', 3.45, 3.45, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (2, '20230001', 2, '2023-2024', 3.60, 3.52, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (3, '20230002', 1, '2023-2024', 2.10, 2.10, 3);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (4, '20230002', 2, '2023-2024', 1.80, 1.95, 7);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (5, '20230003', 1, '2023-2024', 1.40, 1.40, 9);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (6, '20230003', 2, '2023-2024', 1.35, 1.38, 14);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (7, '20230004', 1, '2023-2024', 0.90, 0.90, 16);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (8, '20230004', 2, '2023-2024', 0.85, 0.88, 22);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (9, '20230006', 2, '2023-2024', 1.82, 1.85, 6);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (10, '20230008', 2, '2023-2024', 1.40, 1.42, 12);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (11, '20230010', 2, '2023-2024', 1.90, 1.92, 4);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (12, '20230012', 2, '2023-2024', 0.70, 0.75, 25);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (13, '20230014', 2, '2023-2024', 1.85, 1.88, 5);");

            // Seed Data Cảnh báo học vụ
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (1, 'CB-20232-20230002', '20230002', 2, '2023-2024', 'MUC_1', 1.95, 'GPA tích lũy dưới 2.0 (1.95) và nợ 7 tín chỉ', '2024-07-01', 'DA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (2, 'CB-20232-20230003', '20230003', 2, '2023-2024', 'MUC_2', 1.38, 'GPA tích lũy dưới 1.5 (1.38) trong 2 học kỳ liên tiếp', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (3, 'CB-20232-20230004', '20230004', 2, '2023-2024', 'BUOC_THOI_HOC', 0.88, 'GPA tích lũy dưới 1.0 (0.88) quá thời gian quy định', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (4, 'CB-20232-20230006', '20230006', 2, '2023-2024', 'MUC_1', 1.85, 'GPA tích lũy dưới 2.0 (1.85) và nợ 6 tín chỉ', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (5, 'CB-20232-20230008', '20230008', 2, '2023-2024', 'MUC_2', 1.42, 'GPA tích lũy dưới 1.5 (1.42) và nợ 12 tín chỉ', '2024-07-01', 'DA_TU_VAN');");

            // Seed Data Nhật ký tư vấn
            stmt.execute("INSERT OR IGNORE INTO nhat_ky_tu_van VALUES (1, '20230002', 'CV001', 1, '2024-07-10', 'Trực tiếp', 'Gặp mặt tư vấn sinh viên bị cảnh báo học vụ Mức 1 học kỳ 2 năm 2023-2024', 'Nghỉ học nhiều môn Giải tích do ốm kéo dài', 'Đăng ký học cải thiện vào học kỳ hè', 'Cam kết đạt GPA >= 2.5');");
            stmt.execute("INSERT OR IGNORE INTO nhat_ky_tu_van VALUES (2, '20230008', 'CV001', 5, '2024-07-12', 'Online (Teams/Zoom)', 'Tư vấn sinh viên bị cảnh báo học vụ Mức 2', 'Đi làm thêm quá sức dẫn tới bỏ tiết', 'Giảm giờ làm thêm, đăng ký học lại các môn nợ', 'Cam kết đi học đúng giờ và nộp bài bài tập');");

            // Seed Data Tài khoản
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Quản trị hệ thống', 'admin@huce.edu.vn', 'ADMIN', NULL, '2024-01-01 00:00:00');");
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (2, 'cv_nguynvanan', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'TS. Nguyễn Văn An', 'an.nv@huce.edu.vn', 'CO_VAN', 'CV001', '2024-01-01 00:00:00');");
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (3, 'cv_tranthibinh', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ThS. Trần Thị Bình', 'binh.tt@huce.edu.vn', 'CO_VAN', 'CV002', '2024-01-01 00:00:00');");
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (4, 'quanly', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Trưởng khoa CNTT', 'khoacntt@huce.edu.vn', 'QUAN_LY', NULL, '2024-01-01 00:00:00');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
