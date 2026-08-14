package com.qlcvht.config;

import java.sql.*;

public class DatabaseConnection {

    // === MySQL config (chinh sua theo moi truong cua ban) ===
    private static final String MYSQL_URL      = "jdbc:mysql://localhost:3306/ql_covan_hocvu?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String MYSQL_USER     = "root";
    private static final String MYSQL_PASSWORD = "";

    // === SQLite fallback (tu dong tao neu khong co MySQL) ===
    private static final String SQLITE_URL     = "jdbc:sqlite:ql_covan_hocvu.db";

    private static boolean usingSQLite = false;

    public static boolean isUsingSQLite() { return usingSQLite; }

    public static Connection getConnection() throws SQLException {
        if (usingSQLite) {
            return DriverManager.getConnection(SQLITE_URL);
        }
        try {
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        } catch (SQLException e) {
            usingSQLite = true;
            return DriverManager.getConnection(SQLITE_URL);
        }
    }

    public static boolean testConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
                usingSQLite = false;
                initSQLite(); // van init de co fallback
                return true;
            }
        } catch (Exception e) {
            usingSQLite = true;
            initSQLite();
            return false;
        }
    }

    private static void initSQLite() {
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA foreign_keys=ON;");

            // === TAO BANG ===
            stmt.execute("CREATE TABLE IF NOT EXISTS co_van_hoc_tap (" +
                "ma_cvht TEXT PRIMARY KEY," +
                "ho_ten TEXT NOT NULL," +
                "email TEXT," +
                "so_dien_thoai TEXT," +
                "khoa TEXT" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS lop_hoc (" +
                "ma_lop TEXT PRIMARY KEY," +
                "ten_lop TEXT NOT NULL," +
                "khoa TEXT," +
                "khoa_hoc INTEGER," +
                "ma_cvht TEXT REFERENCES co_van_hoc_tap(ma_cvht)" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS sinh_vien (" +
                "ma_sv TEXT PRIMARY KEY," +
                "ho_ten TEXT NOT NULL," +
                "ngay_sinh TEXT," +
                "gioi_tinh TEXT," +
                "email TEXT," +
                "so_dien_thoai TEXT," +
                "ma_lop TEXT REFERENCES lop_hoc(ma_lop)," +
                "trang_thai TEXT DEFAULT 'DANG_HOC'" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS ket_qua_hoc_tap (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_sv TEXT REFERENCES sinh_vien(ma_sv)," +
                "hoc_ky INTEGER," +
                "nam_hoc TEXT," +
                "gpa_hoc_ky REAL," +
                "gpa_tich_luy REAL," +
                "so_tin_chi_no INTEGER DEFAULT 0," +
                "UNIQUE(ma_sv, hoc_ky, nam_hoc)" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS canh_bao_hoc_vu (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_canh_bao TEXT UNIQUE," +
                "ma_sv TEXT REFERENCES sinh_vien(ma_sv)," +
                "hoc_ky INTEGER," +
                "nam_hoc TEXT," +
                "muc_canh_bao TEXT," +
                "gpa_xet_duyet REAL," +
                "ly_do TEXT," +
                "ngay_quyet_dinh TEXT," +
                "trang_thai_tu_van TEXT DEFAULT 'CHUA_TU_VAN'" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS nhat_ky_tu_van (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_sv TEXT REFERENCES sinh_vien(ma_sv)," +
                "ma_cvht TEXT REFERENCES co_van_hoc_tap(ma_cvht)," +
                "id_canh_bao INTEGER REFERENCES canh_bao_hoc_vu(id)," +
                "ngay_tu_van TEXT," +
                "hinh_thuc TEXT," +
                "noi_dung TEXT," +
                "nguyen_nhan TEXT," +
                "giai_phap TEXT," +
                "cam_ket_sinh_vien TEXT" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS tai_khoan (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten_dang_nhap TEXT UNIQUE NOT NULL," +
                "mat_khau TEXT NOT NULL," +
                "ho_ten TEXT," +
                "email TEXT," +
                "vai_tro TEXT," +
                "ma_ref TEXT," +
                "ngay_tao TEXT" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS thong_bao (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_thong_bao TEXT UNIQUE NOT NULL," +
                "tieu_de TEXT NOT NULL," +
                "noi_dung TEXT NOT NULL," +
                "nhom_rui_ro TEXT DEFAULT 'ALL'," +
                "ma_lop TEXT," +
                "ma_sv TEXT," +
                "ngay_gui TEXT," +
                "nguoi_gui TEXT," +
                "so_luong_nhan INTEGER DEFAULT 0," +
                "trang_thai TEXT DEFAULT 'DA_GUI'" +
            ");");

            // === SEED DATA Co van ===
            stmt.execute("INSERT OR IGNORE INTO co_van_hoc_tap VALUES ('CV001','TS. Nguyen Van An','an.nv@huce.edu.vn','0912345678','Cong nghe thong tin');");
            stmt.execute("INSERT OR IGNORE INTO co_van_hoc_tap VALUES ('CV002','ThS. Tran Thi Binh','binh.tt@huce.edu.vn','0923456789','Cong nghe thong tin');");
            stmt.execute("INSERT OR IGNORE INTO co_van_hoc_tap VALUES ('CV003','TS. Le Minh Cuong','cuong.lm@huce.edu.vn','0934567890','Kinh te xay dung');");

            // === SEED DATA Lop hoc ===
            stmt.execute("INSERT OR IGNORE INTO lop_hoc VALUES ('68IT1','68IT1 - Cong nghe thong tin 1','Cong nghe thong tin',2023,'CV001');");
            stmt.execute("INSERT OR IGNORE INTO lop_hoc VALUES ('68IT2','68IT2 - Cong nghe thong tin 2','Cong nghe thong tin',2023,'CV002');");
            stmt.execute("INSERT OR IGNORE INTO lop_hoc VALUES ('68KX1','68KX1 - Kinh te xay dung 1','Kinh te xay dung',2023,'CV003');");

            // === SEED DATA Sinh vien (90 SV) ===
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230001', 'Võ Minh Linh', '2005-05-27', 'Nữ', 'sv20230001@huce.edu.vn', '0952808199', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230002', 'Lý Hữu Hoa', '2005-04-18', 'Nữ', 'sv20230002@huce.edu.vn', '0968962252', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230003', 'Bùi Quang Đức', '2005-09-08', 'Nam', 'sv20230003@huce.edu.vn', '0939887513', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230004', 'Trần Bảo Nam', '2005-10-07', 'Nam', 'sv20230004@huce.edu.vn', '0916069717', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230005', 'Ngô Thị Hoa', '2005-05-25', 'Nam', 'sv20230005@huce.edu.vn', '0966996708', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230006', 'Đặng Văn Dũng', '2005-09-03', 'Nam', 'sv20230006@huce.edu.vn', '0940942806', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230007', 'Võ Hữu Huy', '2005-09-05', 'Nữ', 'sv20230007@huce.edu.vn', '0953925683', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230008', 'Võ Văn Khánh', '2005-06-27', 'Nam', 'sv20230008@huce.edu.vn', '0927278770', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230009', 'Bùi Văn Dũng', '2005-11-21', 'Nam', 'sv20230009@huce.edu.vn', '0931437623', '68IT1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230010', 'Đỗ Văn Tuyệt', '2005-02-20', 'Nam', 'sv20230010@huce.edu.vn', '0983881386', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230011', 'Trần Hải Hoa', '2005-05-28', 'Nữ', 'sv20230011@huce.edu.vn', '0922439409', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230012', 'Ngô Quang Anh', '2005-08-22', 'Nữ', 'sv20230012@huce.edu.vn', '0924251057', '68IT1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230013', 'Lê Tuấn Nam', '2005-12-25', 'Nam', 'sv20230013@huce.edu.vn', '0983424783', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230014', 'Dương Hữu Anh', '2005-05-04', 'Nữ', 'sv20230014@huce.edu.vn', '0979217084', '68IT1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230015', 'Vũ Hữu Linh', '2005-02-16', 'Nữ', 'sv20230015@huce.edu.vn', '0988155893', '68IT1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230016', 'Hồ Quang Dũng', '2005-01-14', 'Nữ', 'sv20230016@huce.edu.vn', '0993323663', '68IT1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230017', 'Ngô Thanh Nam', '2005-11-08', 'Nữ', 'sv20230017@huce.edu.vn', '0968592399', '68IT1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230018', 'Dương Hữu Nam', '2005-08-02', 'Nữ', 'sv20230018@huce.edu.vn', '0955145586', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230019', 'Vũ Hữu Ngọc', '2005-06-15', 'Nam', 'sv20230019@huce.edu.vn', '0943726654', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230020', 'Dương Khánh Ngọc', '2005-09-22', 'Nữ', 'sv20230020@huce.edu.vn', '0968687491', '68IT1', 'BUOC_THOI_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230021', 'Hồ Gia Khoa', '2005-10-16', 'Nữ', 'sv20230021@huce.edu.vn', '0949699139', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230022', 'Nguyễn Minh Linh', '2005-06-01', 'Nam', 'sv20230022@huce.edu.vn', '0915233537', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230023', 'Lê Thùy Khoa', '2005-12-25', 'Nam', 'sv20230023@huce.edu.vn', '0939918643', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230024', 'Hồ Hữu Tuyệt', '2005-08-21', 'Nữ', 'sv20230024@huce.edu.vn', '0977230359', '68IT1', 'BUOC_THOI_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230025', 'Hoàng Thùy Đức', '2005-05-26', 'Nữ', 'sv20230025@huce.edu.vn', '0924208852', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230026', 'Vũ Bảo Hà', '2005-05-09', 'Nữ', 'sv20230026@huce.edu.vn', '0916983816', '68IT1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230027', 'Đặng Tuấn Huy', '2005-07-03', 'Nữ', 'sv20230027@huce.edu.vn', '0993556429', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230028', 'Nguyễn Bảo Ngọc', '2005-11-10', 'Nam', 'sv20230028@huce.edu.vn', '0993648883', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230029', 'Bùi Bảo Phương', '2005-02-06', 'Nữ', 'sv20230029@huce.edu.vn', '0958355394', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230030', 'Võ Bảo Linh', '2005-03-27', 'Nữ', 'sv20230030@huce.edu.vn', '0912207394', '68IT1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230031', 'Đỗ Thùy Hải', '2005-06-28', 'Nữ', 'sv20230031@huce.edu.vn', '0954724770', '68IT2', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230032', 'Đặng Tuấn Hoa', '2005-12-21', 'Nữ', 'sv20230032@huce.edu.vn', '0947729143', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230033', 'Bùi Gia Dũng', '2005-11-16', 'Nam', 'sv20230033@huce.edu.vn', '0993443241', '68IT2', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230034', 'Hoàng Gia Hoa', '2005-02-21', 'Nam', 'sv20230034@huce.edu.vn', '0950611795', '68IT2', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230035', 'Đỗ Văn Linh', '2005-07-15', 'Nam', 'sv20230035@huce.edu.vn', '0911844684', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230036', 'Hoàng Khánh Khoa', '2005-12-25', 'Nữ', 'sv20230036@huce.edu.vn', '0952917102', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230037', 'Hồ Quang Tuyệt', '2005-02-18', 'Nam', 'sv20230037@huce.edu.vn', '0940559443', '68IT2', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230038', 'Hồ Thanh Khánh', '2005-10-02', 'Nam', 'sv20230038@huce.edu.vn', '0937734095', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230039', 'Lý Khánh Ngọc', '2005-10-19', 'Nam', 'sv20230039@huce.edu.vn', '0927691140', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230040', 'Lê Thùy Khoa', '2005-04-14', 'Nữ', 'sv20230040@huce.edu.vn', '0930784861', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230041', 'Trần Gia Nam', '2005-07-10', 'Nữ', 'sv20230041@huce.edu.vn', '0928754566', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230042', 'Phạm Gia Hải', '2005-12-09', 'Nam', 'sv20230042@huce.edu.vn', '0925197068', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230043', 'Lê Hữu Nam', '2005-08-05', 'Nam', 'sv20230043@huce.edu.vn', '0983844674', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230044', 'Nguyễn Gia Dũng', '2005-05-04', 'Nữ', 'sv20230044@huce.edu.vn', '0991978369', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230045', 'Phạm Gia Linh', '2005-05-06', 'Nam', 'sv20230045@huce.edu.vn', '0948960956', '68IT2', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230046', 'Dương Văn Phương', '2005-07-25', 'Nam', 'sv20230046@huce.edu.vn', '0940889594', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230047', 'Lý Minh Hà', '2005-01-20', 'Nữ', 'sv20230047@huce.edu.vn', '0922782935', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230048', 'Bùi Quang Dũng', '2005-11-06', 'Nam', 'sv20230048@huce.edu.vn', '0951199683', '68IT2', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230049', 'Bùi Hải Trang', '2005-02-27', 'Nam', 'sv20230049@huce.edu.vn', '0939722585', '68IT2', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230050', 'Bùi Hữu Hải', '2005-02-07', 'Nam', 'sv20230050@huce.edu.vn', '0998625547', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230051', 'Ngô Bảo Khánh', '2005-08-08', 'Nam', 'sv20230051@huce.edu.vn', '0920867419', '68IT2', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230052', 'Bùi Thị Khoa', '2005-07-21', 'Nam', 'sv20230052@huce.edu.vn', '0996467133', '68IT2', 'BUOC_THOI_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230053', 'Hoàng Tuấn Khánh', '2005-09-15', 'Nữ', 'sv20230053@huce.edu.vn', '0955455648', '68IT2', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230054', 'Nguyễn Bảo Khoa', '2005-08-21', 'Nam', 'sv20230054@huce.edu.vn', '0931080635', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230055', 'Hồ Khánh Huy', '2005-07-22', 'Nữ', 'sv20230055@huce.edu.vn', '0920488876', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230056', 'Hồ Hữu Hải', '2005-04-25', 'Nữ', 'sv20230056@huce.edu.vn', '0925403628', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230057', 'Bùi Bảo Nam', '2005-07-24', 'Nam', 'sv20230057@huce.edu.vn', '0960458007', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230058', 'Đỗ Thùy Dũng', '2005-10-19', 'Nam', 'sv20230058@huce.edu.vn', '0971212859', '68IT2', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230059', 'Võ Bảo Khoa', '2005-05-28', 'Nam', 'sv20230059@huce.edu.vn', '0947620205', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230060', 'Ngô Thị Đức', '2005-09-08', 'Nữ', 'sv20230060@huce.edu.vn', '0963871790', '68IT2', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230061', 'Nguyễn Thanh Hoa', '2005-04-10', 'Nữ', 'sv20230061@huce.edu.vn', '0936928140', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230062', 'Vũ Bảo Nam', '2005-05-07', 'Nữ', 'sv20230062@huce.edu.vn', '0937866347', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230063', 'Đỗ Minh Linh', '2005-05-23', 'Nữ', 'sv20230063@huce.edu.vn', '0988380192', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230064', 'Nguyễn Hữu Trang', '2005-10-27', 'Nữ', 'sv20230064@huce.edu.vn', '0959644869', '68KX1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230065', 'Võ Minh Nam', '2005-11-25', 'Nữ', 'sv20230065@huce.edu.vn', '0968036458', '68KX1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230066', 'Đỗ Bảo Trang', '2005-08-18', 'Nữ', 'sv20230066@huce.edu.vn', '0923432064', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230067', 'Vũ Bảo Phương', '2005-03-23', 'Nữ', 'sv20230067@huce.edu.vn', '0910983417', '68KX1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230068', 'Bùi Minh Phương', '2005-04-03', 'Nam', 'sv20230068@huce.edu.vn', '0973266136', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230069', 'Lê Thùy Ngọc', '2005-10-03', 'Nam', 'sv20230069@huce.edu.vn', '0984256825', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230070', 'Võ Gia Phương', '2005-02-01', 'Nam', 'sv20230070@huce.edu.vn', '0946601209', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230071', 'Lý Khánh Linh', '2005-01-17', 'Nữ', 'sv20230071@huce.edu.vn', '0994190265', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230072', 'Đặng Tuấn Hoa', '2005-07-03', 'Nữ', 'sv20230072@huce.edu.vn', '0956621958', '68KX1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230073', 'Hoàng Tuấn Ngọc', '2005-07-23', 'Nam', 'sv20230073@huce.edu.vn', '0993436913', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230074', 'Hoàng Thị Khoa', '2005-11-27', 'Nam', 'sv20230074@huce.edu.vn', '0958417520', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230075', 'Hồ Bảo Huy', '2005-04-16', 'Nữ', 'sv20230075@huce.edu.vn', '0943232197', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230076', 'Trần Quang Nam', '2005-03-19', 'Nữ', 'sv20230076@huce.edu.vn', '0988651102', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230077', 'Ngô Quang Khoa', '2005-01-09', 'Nữ', 'sv20230077@huce.edu.vn', '0924139858', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230078', 'Đặng Thùy Hà', '2005-01-01', 'Nữ', 'sv20230078@huce.edu.vn', '0915728015', '68KX1', 'CANH_BAO_2');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230079', 'Hoàng Hữu Huy', '2005-07-25', 'Nữ', 'sv20230079@huce.edu.vn', '0932657743', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230080', 'Phạm Hải Nam', '2005-01-22', 'Nữ', 'sv20230080@huce.edu.vn', '0934946644', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230081', 'Dương Thị Khoa', '2005-03-19', 'Nữ', 'sv20230081@huce.edu.vn', '0959629777', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230082', 'Dương Thị Hoa', '2005-09-17', 'Nam', 'sv20230082@huce.edu.vn', '0935756296', '68KX1', 'BUOC_THOI_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230083', 'Đỗ Hữu Khoa', '2005-02-14', 'Nam', 'sv20230083@huce.edu.vn', '0932447325', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230084', 'Dương Minh Đức', '2005-11-11', 'Nam', 'sv20230084@huce.edu.vn', '0919449676', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230085', 'Ngô Minh Dũng', '2005-01-24', 'Nam', 'sv20230085@huce.edu.vn', '0917003150', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230086', 'Nguyễn Khánh Hải', '2005-05-25', 'Nam', 'sv20230086@huce.edu.vn', '0980193960', '68KX1', 'CANH_BAO_1');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230087', 'Lê Thanh Huy', '2005-04-17', 'Nữ', 'sv20230087@huce.edu.vn', '0970158994', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230088', 'Hồ Hải Khánh', '2005-04-21', 'Nam', 'sv20230088@huce.edu.vn', '0945016733', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230089', 'Lê Bảo Hà', '2005-04-27', 'Nam', 'sv20230089@huce.edu.vn', '0911977025', '68KX1', 'DANG_HOC');");
            stmt.execute("INSERT OR IGNORE INTO sinh_vien VALUES ('20230090', 'Ngô Khánh Tuyệt', '2005-05-03', 'Nam', 'sv20230090@huce.edu.vn', '0999874357', '68KX1', 'DANG_HOC');");

            // === SEED DATA Ket qua hoc tap ===
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230001', 2, '2023-2024', 3.23, 3.23, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230002', 2, '2023-2024', 3.31, 3.31, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230003', 2, '2023-2024', 3.42, 3.42, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230004', 2, '2023-2024', 2.9, 2.9, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230005', 2, '2023-2024', 3.38, 3.38, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230006', 2, '2023-2024', 3.89, 3.89, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230007', 2, '2023-2024', 2.68, 2.68, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230008', 2, '2023-2024', 3.6, 3.6, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230009', 2, '2023-2024', 1.61, 1.61, 8);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230010', 2, '2023-2024', 2.78, 2.78, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230011', 2, '2023-2024', 3.47, 3.47, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230012', 2, '2023-2024', 1.75, 1.75, 6);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230013', 2, '2023-2024', 3.3, 3.3, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230014', 2, '2023-2024', 1.74, 1.74, 9);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230015', 2, '2023-2024', 1.87, 1.87, 9);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230016', 2, '2023-2024', 1.62, 1.62, 9);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230017', 2, '2023-2024', 1.47, 1.47, 13);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230018', 2, '2023-2024', 3.85, 3.85, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230019', 2, '2023-2024', 2.57, 2.57, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230020', 2, '2023-2024', 0.92, 0.92, 21);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230021', 2, '2023-2024', 2.93, 2.93, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230022', 2, '2023-2024', 3.44, 3.44, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230023', 2, '2023-2024', 3.91, 3.91, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230024', 2, '2023-2024', 0.93, 0.93, 18);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230025', 2, '2023-2024', 3.31, 3.31, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230026', 2, '2023-2024', 1.25, 1.25, 14);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230027', 2, '2023-2024', 3.7, 3.7, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230028', 2, '2023-2024', 3.28, 3.28, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230029', 2, '2023-2024', 2.7, 2.7, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230030', 2, '2023-2024', 2.94, 2.94, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230031', 2, '2023-2024', 1.38, 1.38, 11);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230032', 2, '2023-2024', 2.65, 2.65, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230033', 2, '2023-2024', 1.29, 1.29, 13);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230034', 2, '2023-2024', 1.87, 1.87, 8);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230035', 2, '2023-2024', 3.11, 3.11, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230036', 2, '2023-2024', 2.77, 2.77, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230037', 2, '2023-2024', 1.7, 1.7, 10);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230038', 2, '2023-2024', 3.92, 3.92, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230039', 2, '2023-2024', 2.77, 2.77, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230040', 2, '2023-2024', 2.8, 2.8, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230041', 2, '2023-2024', 2.84, 2.84, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230042', 2, '2023-2024', 3.47, 3.47, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230043', 2, '2023-2024', 3.93, 3.93, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230044', 2, '2023-2024', 3.99, 3.99, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230045', 2, '2023-2024', 1.76, 1.76, 7);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230046', 2, '2023-2024', 3.87, 3.87, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230047', 2, '2023-2024', 2.67, 2.67, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230048', 2, '2023-2024', 1.62, 1.62, 5);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230049', 2, '2023-2024', 1.75, 1.75, 8);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230050', 2, '2023-2024', 3.34, 3.34, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230051', 2, '2023-2024', 1.27, 1.27, 15);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230052', 2, '2023-2024', 0.57, 0.57, 17);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230053', 2, '2023-2024', 1.77, 1.77, 7);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230054', 2, '2023-2024', 3.37, 3.37, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230055', 2, '2023-2024', 3.58, 3.58, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230056', 2, '2023-2024', 2.99, 2.99, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230057', 2, '2023-2024', 3.24, 3.24, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230058', 2, '2023-2024', 1.33, 1.33, 15);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230059', 2, '2023-2024', 3.14, 3.14, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230060', 2, '2023-2024', 3.79, 3.79, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230061', 2, '2023-2024', 3.61, 3.61, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230062', 2, '2023-2024', 1.64, 1.64, 7);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230063', 2, '2023-2024', 3.39, 3.39, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230064', 2, '2023-2024', 1.49, 1.49, 13);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230065', 2, '2023-2024', 1.5, 1.5, 15);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230066', 2, '2023-2024', 1.78, 1.78, 7);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230067', 2, '2023-2024', 1.22, 1.22, 11);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230068', 2, '2023-2024', 2.79, 2.79, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230069', 2, '2023-2024', 2.79, 2.79, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230070', 2, '2023-2024', 3.49, 3.49, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230071', 2, '2023-2024', 1.89, 1.89, 10);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230072', 2, '2023-2024', 1.33, 1.33, 13);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230073', 2, '2023-2024', 2.67, 2.67, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230074', 2, '2023-2024', 2.79, 2.79, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230075', 2, '2023-2024', 3.38, 3.38, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230076', 2, '2023-2024', 3.66, 3.66, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230077', 2, '2023-2024', 3.96, 3.96, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230078', 2, '2023-2024', 1.27, 1.27, 15);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230079', 2, '2023-2024', 1.74, 1.74, 10);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230080', 2, '2023-2024', 1.65, 1.65, 6);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230081', 2, '2023-2024', 3.73, 3.73, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230082', 2, '2023-2024', 0.73, 0.73, 22);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230083', 2, '2023-2024', 3.99, 3.99, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230084', 2, '2023-2024', 3.56, 3.56, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230085', 2, '2023-2024', 1.83, 1.83, 7);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230086', 2, '2023-2024', 1.87, 1.87, 5);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230087', 2, '2023-2024', 3.61, 3.61, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230088', 2, '2023-2024', 2.75, 2.75, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230089', 2, '2023-2024', 3.23, 3.23, 0);");
            stmt.execute("INSERT OR IGNORE INTO ket_qua_hoc_tap VALUES (NULL, '20230090', 2, '2023-2024', 3.97, 3.97, 0);");

            // === SEED DATA Canh bao hoc vu ===
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230009', '20230009', 2, '2023-2024', 'MUC_1', 1.61, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230012', '20230012', 2, '2023-2024', 'MUC_1', 1.75, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230014', '20230014', 2, '2023-2024', 'MUC_1', 1.74, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230015', '20230015', 2, '2023-2024', 'MUC_1', 1.87, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230016', '20230016', 2, '2023-2024', 'MUC_1', 1.62, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230017', '20230017', 2, '2023-2024', 'MUC_2', 1.47, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230020', '20230020', 2, '2023-2024', 'BUOC_THOI_HOC', 0.92, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230024', '20230024', 2, '2023-2024', 'BUOC_THOI_HOC', 0.93, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230026', '20230026', 2, '2023-2024', 'MUC_2', 1.25, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230031', '20230031', 2, '2023-2024', 'MUC_2', 1.38, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230033', '20230033', 2, '2023-2024', 'MUC_2', 1.29, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230034', '20230034', 2, '2023-2024', 'MUC_1', 1.87, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230037', '20230037', 2, '2023-2024', 'MUC_1', 1.7, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230045', '20230045', 2, '2023-2024', 'MUC_1', 1.76, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230048', '20230048', 2, '2023-2024', 'MUC_1', 1.62, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230049', '20230049', 2, '2023-2024', 'MUC_1', 1.75, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230051', '20230051', 2, '2023-2024', 'MUC_2', 1.27, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230052', '20230052', 2, '2023-2024', 'BUOC_THOI_HOC', 0.57, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230053', '20230053', 2, '2023-2024', 'MUC_1', 1.77, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230058', '20230058', 2, '2023-2024', 'MUC_2', 1.33, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230062', '20230062', 2, '2023-2024', 'MUC_1', 1.64, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230064', '20230064', 2, '2023-2024', 'MUC_2', 1.49, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230065', '20230065', 2, '2023-2024', 'MUC_2', 1.5, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230066', '20230066', 2, '2023-2024', 'MUC_1', 1.78, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230067', '20230067', 2, '2023-2024', 'MUC_2', 1.22, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230071', '20230071', 2, '2023-2024', 'MUC_1', 1.89, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230072', '20230072', 2, '2023-2024', 'MUC_2', 1.33, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230078', '20230078', 2, '2023-2024', 'MUC_2', 1.27, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230079', '20230079', 2, '2023-2024', 'MUC_1', 1.74, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230080', '20230080', 2, '2023-2024', 'MUC_1', 1.65, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230082', '20230082', 2, '2023-2024', 'BUOC_THOI_HOC', 0.73, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230085', '20230085', 2, '2023-2024', 'MUC_1', 1.83, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");
            stmt.execute("INSERT OR IGNORE INTO canh_bao_hoc_vu VALUES (NULL, 'CB-20232-20230086', '20230086', 2, '2023-2024', 'MUC_1', 1.87, 'Vi pham quy che hoc vu', '2024-07-01', 'CHUA_TU_VAN');");

            // === SEED DATA Nhat ky tu van ===
            stmt.execute("INSERT OR IGNORE INTO nhat_ky_tu_van VALUES (1,'20230002','CV001',1,'2024-07-10','Truc tiep','Gap mat tu van SV bi canh bao hoc vu Muc 1 HK2 2023-2024','Nghi hoc nhieu mon do om dai','Dang ky hoc cai thien vao HK toi','Cam ket dat GPA >= 2.5');");
            stmt.execute("INSERT OR IGNORE INTO nhat_ky_tu_van VALUES (2,'20230008','CV001',5,'2024-07-12','Online (Teams/Zoom)','Tu van SV bi canh bao hoc vu Muc 2','Di lam them qua suc dan toi bo tiet','Giam gio lam them, dang ky hoc lai cac mon no','Cam ket di hoc dung gio va nop bai tap');");

            // === SEED DATA Tai khoan ===
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (1,'admin','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','Quan tri vien','admin@huce.edu.vn','ADMIN',NULL,'2024-01-01 00:00:00');");
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (2,'cv_nguynvanan','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','TS. Nguyen Van An','an.nv@huce.edu.vn','CO_VAN','CV001','2024-01-01 00:00:00');");
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (3,'cv_tranthibinh','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','ThS. Tran Thi Binh','binh.tt@huce.edu.vn','CO_VAN','CV002','2024-01-01 00:00:00');");
            stmt.execute("INSERT OR IGNORE INTO tai_khoan VALUES (4,'quanly','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','Truong khoa CNTT','khoacntt@huce.edu.vn','QUAN_LY',NULL,'2024-01-01 00:00:00');");

            // === SEED DATA Thong bao ===
            stmt.execute("INSERT OR IGNORE INTO thong_bao VALUES (1,'TB-T1-1001','THONG BAO BIEU DUONG HOC TAP XUAT SAC (TIER 1)','Tuyen duong cac sinh vien thuoc nhom Tier 1 co GPA >= 3.2. Du dieu kien dang ky hoc bong hoc ky nay.','TIER_1',NULL,NULL,'2026-08-01 09:00:00','TS. Nguyen Van An',28,'DA_GUI');");
            stmt.execute("INSERT OR IGNORE INTO thong_bao VALUES (2,'TB-T2-1002','THONG BAO DUY TRI PHONG DO HOC TAP (TIER 2)','Nhac nho sinh vien Tier 2 dang ky bo sung cac mon cai thien diem so va theo doi lich thi.','TIER_2',NULL,NULL,'2026-08-05 10:30:00','TS. Nguyen Van An',35,'DA_GUI');");
            stmt.execute("INSERT OR IGNORE INTO thong_bao VALUES (3,'TB-T3-1003','CANH BAO HOC VU KHAN CAP & YEU CAU TU VAN (TIER 3)','Yeu cau tat ca sinh vien nhom Tier 3 (GPA < 2.0 / Canh bao hoc vu) lien he CVHT lap ke hoach tu van.','TIER_3',NULL,NULL,'2026-08-10 14:00:00','TS. Nguyen Van An',27,'DA_GUI');");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}