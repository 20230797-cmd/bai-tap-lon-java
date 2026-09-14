import random
import os
import sqlite3

khoas = [
    {
        "ma_khoa": "CNTT",
        "ten_khoa": "Công nghệ thông tin",
        "cvht": ("CV001", "TS. Đinh Văn Phong", "phong.dv@eaut.edu.vn", "0912345678"),
        "lops": [
            ("DCCTPM14A", "DCCTPM14A - Công nghệ phần mềm K14"),
            ("DCCNTT14B", "DCCNTT14B - Công nghệ thông tin K14B"),
            ("DCCTPM12A", "DCCTPM12A - Kỹ thuật Phần mềm K12 (Năm 4)"),
            ("DCCNTT11A", "DCCNTT11A - Khoa học Máy tính K11 (Năm 5)")
        ]
    },
    {
        "ma_khoa": "OTO",
        "ten_khoa": "Công nghệ kỹ thuật Ô tô",
        "cvht": ("CV002", "PGS.TS. Nguyễn Thanh Hải", "hai.nt@eaut.edu.vn", "0987654321"),
        "lops": [
            ("DCOTO14A", "DCOTO14A - Công nghệ kỹ thuật Ô tô 14A"),
            ("DCOTO14B", "DCOTO14B - Công nghệ kỹ thuật Ô tô 14B"),
            ("DCOTO12A", "DCOTO12A - Cơ điện tử Ô tô K12 (Năm 4)")
        ]
    },
    {
        "ma_khoa": "QTKD",
        "ten_khoa": "Quản trị kinh doanh",
        "cvht": ("CV003", "ThS. Hoàng Thị Mai", "mai.ht@eaut.edu.vn", "0934567890"),
        "lops": [
            ("DCQTKD14A", "DCQTKD14A - Quản trị kinh doanh 14A"),
            ("DCQTKD14B", "DCQTKD14B - Quản trị kinh doanh 14B")
        ]
    },
    {
        "ma_khoa": "DDT",
        "ten_khoa": "Công nghệ kỹ thuật Điện - Điện tử",
        "cvht": ("CV004", "TS. Vũ Trường Sơn", "son.vt@eaut.edu.vn", "0945678901"),
        "lops": [
            ("DCDDT14A", "DCDDT14A - Kỹ thuật Điện - Điện tử 14A"),
            ("DCDDT14B", "DCDDT14B - Tự động hóa K14B")
        ]
    }
]

ho_list = ["Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý"]
dem_nam = ["Văn", "Đức", "Hoàng", "Minh", "Quang", "Đình", "Tuấn", "Thanh", "Hữu", "Quốc", "Gia", "Trọng"]
dem_nu = ["Thị", "Thu", "Phương", "Thùy", "Bảo", "Khánh", "Ngọc", "Thanh", "Hải", "Diệu", "Minh", "Hồng"]
ten_nam = ["Nam", "Long", "Huy", "Anh", "Tuấn", "Hải", "Bảo", "Hưng", "Khởi", "Kiệt", "Tùng", "Đức", "Quân", "Khánh", "Duy", "Hiếu", "Cường", "Trí", "Thắng", "Phong", "Hào", "Tú", "Bách", "Đạt", "Phúc", "Thịnh"]
ten_nu = ["Lan", "Dương", "Thảo", "Châu", "Giang", "Trang", "Linh", "Hoa", "Mai", "Trang", "Nhi", "Huyền", "Yến", "Ngân", "Quyên", "Tuyết", "Vy", "Hà", "Vân", "Hiền"]

random.seed(42)

all_sv = []
all_kq = []
all_cb = []
all_nk = []
all_tk = []

# Tài khoản mặc định hệ thống
all_tk.append(("admin", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", "Quản trị viên Hệ thống EAUT", "admin@eaut.edu.vn", "ADMIN", None))
all_tk.append(("quanly", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", "Trưởng phòng Đào tạo EAUT", "daotao@eaut.edu.vn", "QUAN_LY", None))

cb_counter = 1
nk_counter = 1
kq_counter = 1

# Tạo tài khoản Cố vấn
for k in khoas:
    cv_ma, cv_ten, cv_email, cv_sdt = k["cvht"]
    email_prefix = cv_email.split('@')[0].replace('.', '')
    all_tk.append((f"cv_{email_prefix}", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", cv_ten, cv_email, "CO_VAN", cv_ma))
    all_tk.append((cv_ma.lower(), "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", cv_ten, cv_email, "CO_VAN", cv_ma))

# Mô phỏng dữ liệu 5 năm (Năm 1 đến Năm 5: Khóa 2021 -> 2025)
# Chu kỳ 150 tín chỉ:
# Năm 1: 30 tín chuẩn
# Năm 2: 60 tín chuẩn
# Năm 3: 90 tín chuẩn
# Năm 4: 125-130 tín chuẩn (Nếu <= 120 tín -> Cảnh báo tiến độ!)
# Năm 5: 150 tín chuẩn (Tốt nghiệp / ĐATN)

cohorts = [
    {"nam_thu": 1, "khoa_hoc": 2025, "prefix": "202500", "lop": "DCCTPM14A", "cvht": "CV001", "bench_credits": 30},
    {"nam_thu": 2, "khoa_hoc": 2024, "prefix": "202400", "lop": "DCCNTT14B", "cvht": "CV001", "bench_credits": 60},
    {"nam_thu": 3, "khoa_hoc": 2023, "prefix": "202300", "lop": "DCOTO14A",  "cvht": "CV002", "bench_credits": 90},
    {"nam_thu": 4, "khoa_hoc": 2022, "prefix": "202200", "lop": "DCCTPM12A", "cvht": "CV001", "bench_credits": 125}, # Năm 4: Có SV chỉ được 120 tín
    {"nam_thu": 5, "khoa_hoc": 2021, "prefix": "202100", "lop": "DCCNTT11A", "cvht": "CV001", "bench_credits": 150}  # Năm 5: Tốt nghiệp hoặc ĐATN
]

for cohort in cohorts:
    nam_thu = cohort["nam_thu"]
    prefix = cohort["prefix"]
    lop_ma = cohort["lop"]
    cv_ma = cohort["cvht"]
    bench = cohort["bench_credits"]

    for i in range(1, 26):
        ma_sv = f"{prefix}{i:02d}"
        is_nu = (i % 3 == 0)
        gt = "Nữ" if is_nu else "Nam"
        ho = ho_list[(i * 7 + nam_thu) % len(ho_list)]
        dem = dem_nu[(i * 3) % len(dem_nu)] if is_nu else dem_nam[(i * 5) % len(dem_nam)]
        ten = ten_nu[(i * 2) % len(ten_nu)] if is_nu else ten_nam[(i * 3) % len(ten_nam)]
        ho_ten = f"{ho} {dem} {ten}"
        email = f"sv{ma_sv}@eaut.edu.vn"
        sdt = f"09110{nam_thu}{ma_sv[-4:]}"
        ngay_sinh = f"{2006 - nam_thu}-{((i*3)%12)+1:02d}-{((i*5)%28)+1:02d}"

        # Tính toán phân tầng học tập & tín chỉ tích lũy / 150 tín
        if nam_thu == 4:
            # Năm 4 đặc biệt: Có các sinh viên mới được 110-120 tín -> Bị cảnh báo tiến độ!
            if i <= 5:
                # SV Giỏi: 130 tín
                tt = "DANG_HOC"
                tong_tc = 132
                gpa_tl = 3.45
                gpa_hk = 3.60
                no_tc = 0
            elif i <= 15:
                # SV Khá: 125 tín (đạt chuẩn)
                tt = "DANG_HOC"
                tong_tc = 126
                gpa_tl = 2.80
                gpa_hk = 2.75
                no_tc = 3
            elif i <= 22:
                # SV Năm 4 chỉ được 115-120 tín -> CẢNH BÁO TIẾN ĐỘ 150 TÍN (Mức 2)
                tt = "CANH_BAO_2"
                tong_tc = 118
                gpa_tl = 1.85
                gpa_hk = 1.40
                no_tc = 9
                ly_do = f"Chậm tiến độ 150 tín chỉ: Đang học Năm thứ 4 nhưng mới đạt {tong_tc}/150 tín chỉ (chuẩn >= 125 tín). Nợ 9 tín chỉ, CPA: {gpa_tl}."
                all_cb.append((cb_counter, f"CB-4-{ma_sv}", ma_sv, 1, "2025-2026", "MUC_2", gpa_tl, ly_do, "2026-08-25", "CHUA_TU_VAN"))
                cb_counter += 1
                all_nk.append((nk_counter, ma_sv, cv_ma, cb_counter - 1, "2026-08-28", "Trực tiếp tại văn phòng Khoa",
                               "Tư vấn xử lý học vụ Năm 4 chậm tiến độ tốt nghiệp", "Nợ môn chuyên ngành và đi làm thêm nhiều",
                               "Đăng ký 12 tín chỉ trả nợ trong kỳ 1 và kỳ hè", "Cam kết đạt chuẩn 135 tín trước khi nhận ĐATN"))
                nk_counter += 1
            else:
                # Nguy cơ thôi học
                tt = "BUOC_THOI_HOC"
                tong_tc = 95
                gpa_tl = 0.95
                gpa_hk = 0.60
                no_tc = 26
                ly_do = f"CPA tích lũy rất thấp ({gpa_tl} < 1.0), nợ 26 tín chỉ vượt quá giới hạn đào tạo 5 năm."
                all_cb.append((cb_counter, f"CB-BTH-{ma_sv}", ma_sv, 1, "2025-2026", "BUOC_THOI_HOC", gpa_tl, ly_do, "2026-08-25", "CHUA_TU_VAN"))
                cb_counter += 1
        elif nam_thu == 5:
            # Năm 5: 140 - 150 tín
            if i <= 18:
                tt = "DA_TOT_NGHIEP" if i <= 10 else "DANG_HOC"
                tong_tc = 150 if i <= 10 else 145
                gpa_tl = 3.20
                gpa_hk = 3.50
                no_tc = 0
            else:
                tt = "CANH_BAO_1"
                tong_tc = 135
                gpa_tl = 2.10
                gpa_hk = 2.00
                no_tc = 6
                ly_do = f"Năm thứ 5 chưa hoàn thành 150 tín chỉ ({tong_tc}/150 tín), cần gia hạn thêm 1 học kỳ làm Đồ án tốt nghiệp."
                all_cb.append((cb_counter, f"CB-5-{ma_sv}", ma_sv, 1, "2025-2026", "MUC_1", gpa_tl, ly_do, "2026-08-25", "DA_TU_VAN"))
                cb_counter += 1
        else:
            # Năm 1, 2, 3
            if i <= 5:
                tt = "DANG_HOC"
                tong_tc = bench + 4
                gpa_tl = 3.30
                gpa_hk = 3.40
                no_tc = 0
            elif i <= 20:
                tt = "DANG_HOC"
                tong_tc = bench - 2
                gpa_tl = 2.65
                gpa_hk = 2.50
                no_tc = 2
            else:
                tt = "CANH_BAO_1"
                tong_tc = bench - 10
                gpa_tl = 1.75
                gpa_hk = 1.30
                no_tc = 8
                ly_do = f"Cảnh báo học vụ Mức 1: CPA tích lũy dưới 2.0 ({gpa_tl}), nợ {no_tc} tín chỉ. Tiến độ: {tong_tc}/150 tín (Năm {nam_thu})."
                all_cb.append((cb_counter, f"CB-{nam_thu}-{ma_sv}", ma_sv, 1, "2025-2026", "MUC_1", gpa_tl, ly_do, "2026-08-25", "CHUA_TU_VAN"))
                cb_counter += 1

        all_sv.append((ma_sv, ho_ten, ngay_sinh, gt, email, sdt, lop_ma, tt, tong_tc, nam_thu))
        all_kq.append((kq_counter, ma_sv, 1, "2025-2026", gpa_hk, gpa_tl, no_tc, tong_tc, nam_thu))
        kq_counter += 1

        # Tài khoản sinh viên
        all_tk.append((ma_sv, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", ho_ten, email, "SINH_VIEN", ma_sv))

print(f"Total students simulated for 5 years: {len(all_sv)}")
print(f"Total academic results: {len(all_kq)}")
print(f"Total warnings: {len(all_cb)}")

def generate_sqlite():
    sql = []
    sql.append("-- CSDL SQLite EAUT 5 Nam Mo Phong")
    sql.append("""
DROP TABLE IF EXISTS `diem_danh`;
DROP TABLE IF EXISTS `chuyen_can_mon_hoc`;
DROP TABLE IF EXISTS `mon_hoc`;
DROP TABLE IF EXISTS `thong_bao`;
DROP TABLE IF EXISTS `nhat_ky_tu_van`;
DROP TABLE IF EXISTS `canh_bao_hoc_vu`;
DROP TABLE IF EXISTS `ket_qua_hoc_tap`;
DROP TABLE IF EXISTS `sinh_vien`;
DROP TABLE IF EXISTS `lop_hoc`;
DROP TABLE IF EXISTS `co_van_hoc_tap`;
DROP TABLE IF EXISTS `tai_khoan`;
DROP TABLE IF EXISTS `vai_tro`;

CREATE TABLE `vai_tro` (
  `ma_vai_tro` VARCHAR(20) PRIMARY KEY,
  `ten_vai_tro` VARCHAR(100) NOT NULL,
  `mo_ta` VARCHAR(255)
);

INSERT INTO `vai_tro` VALUES 
('ADMIN', 'Quản trị viên Hệ thống', 'Toàn quyền quản trị tài khoản và cấu hình hệ thống'),
('CO_VAN', 'Cố vấn Học tập', 'Quản lý lớp, theo dõi học vụ, tư vấn sinh viên, điểm danh, báo cáo'),
('SINH_VIEN', 'Sinh viên', 'Xem kết quả học tập, chuyên cần, nhận cảnh báo, chat với CVHT'),
('QUAN_LY', 'Quản lý Đào tạo / Khoa', 'Giám sát toàn diện học vụ, báo cáo thống kê cấp Khoa/Trường');

CREATE TABLE `tai_khoan` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ten_dang_nhap` VARCHAR(50) UNIQUE NOT NULL,
  `mat_khau` VARCHAR(255) NOT NULL,
  `ho_ten` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `vai_tro` VARCHAR(20) NOT NULL,
  `ma_ref` VARCHAR(20),
  `ngay_tao` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`vai_tro`) REFERENCES `vai_tro` (`ma_vai_tro`)
);

CREATE TABLE `co_van_hoc_tap` (
  `ma_cvht` VARCHAR(20) PRIMARY KEY,
  `ho_ten` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `so_dien_thoai` VARCHAR(20),
  `khoa` VARCHAR(100) NOT NULL
);

CREATE TABLE `lop_hoc` (
  `ma_lop` VARCHAR(20) PRIMARY KEY,
  `ten_lop` VARCHAR(100) NOT NULL,
  `khoa` VARCHAR(100) NOT NULL,
  `khoa_hoc` INT NOT NULL,
  `ma_cvht` VARCHAR(20),
  FOREIGN KEY (`ma_cvht`) REFERENCES `co_van_hoc_tap` (`ma_cvht`)
);

CREATE TABLE `sinh_vien` (
  `ma_sv` VARCHAR(20) PRIMARY KEY,
  `ho_ten` VARCHAR(100) NOT NULL,
  `ngay_sinh` DATE NOT NULL,
  `gioi_tinh` VARCHAR(10) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `so_dien_thoai` VARCHAR(20),
  `ma_lop` VARCHAR(20) NOT NULL,
  `trang_thai` VARCHAR(30) DEFAULT 'DANG_HOC',
  `tong_tin_chi_tich_luy` INT DEFAULT 0,
  `nam_thu` INT DEFAULT 1,
  FOREIGN KEY (`ma_lop`) REFERENCES `lop_hoc` (`ma_lop`)
);

CREATE TABLE `ket_qua_hoc_tap` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_sv` VARCHAR(20) NOT NULL,
  `hoc_ky` INT NOT NULL,
  `nam_hoc` VARCHAR(20) NOT NULL,
  `gpa_hoc_ky` DOUBLE NOT NULL,
  `gpa_tich_luy` DOUBLE NOT NULL,
  `so_tin_chi_no` INT DEFAULT 0,
  `tong_tin_chi_tich_luy` INT DEFAULT 0,
  `nam_thu` INT DEFAULT 1,
  FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`)
);

CREATE TABLE `canh_bao_hoc_vu` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_canh_bao` VARCHAR(50) UNIQUE NOT NULL,
  `ma_sv` VARCHAR(20) NOT NULL,
  `hoc_ky` INT NOT NULL,
  `nam_hoc` VARCHAR(20) NOT NULL,
  `muc_canh_bao` VARCHAR(20) NOT NULL,
  `gpa_xet_duyet` DOUBLE NOT NULL,
  `ly_do` TEXT NOT NULL,
  `ngay_quyet_dinh` DATE NOT NULL,
  `trang_thai_tu_van` VARCHAR(30) DEFAULT 'CHUA_TU_VAN',
  FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`)
);

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
  FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`),
  FOREIGN KEY (`ma_cvht`) REFERENCES `co_van_hoc_tap` (`ma_cvht`)
);

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

CREATE TABLE `mon_hoc` (
  `ma_mon` VARCHAR(20) PRIMARY KEY,
  `ten_mon` VARCHAR(150) NOT NULL,
  `so_tin_chi` INT NOT NULL,
  `khoa` VARCHAR(100) NOT NULL,
  `tong_so_buoi` INT DEFAULT 15,
  `nguong_vang_cam_thi` INT DEFAULT 3
);

INSERT INTO `mon_hoc` VALUES
('IT101', 'Nhập môn Lập trình C/C++', 3, 'Công nghệ thông tin', 15, 3),
('IT204', 'Lập trình Hướng đối tượng Java', 3, 'Công nghệ thông tin', 15, 3),
('IT301', 'Cơ sở Dữ liệu & SQL', 3, 'Công nghệ thông tin', 15, 3),
('IT401', 'Kỹ thuật Phần mềm Nâng cao', 4, 'Công nghệ thông tin', 20, 4),
('IT501', 'Đồ án Tốt nghiệp Kỹ sư CNTT', 10, 'Công nghệ thông tin', 15, 3);

CREATE TABLE `chuyen_can_mon_hoc` (
  `id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `ma_sv` VARCHAR(20) NOT NULL,
  `ma_mon` VARCHAR(20) NOT NULL,
  `ma_lop` VARCHAR(20) NOT NULL,
  `so_buoi_co_mat` INT DEFAULT 15,
  `so_buoi_vang_phep` INT DEFAULT 0,
  `so_buoi_vang_khong_phep` INT DEFAULT 0,
  `trang_thai_du_thi` VARCHAR(30) DEFAULT 'DU_DIEU_KIEN',
  `ghi_chu` TEXT,
  FOREIGN KEY (`ma_sv`) REFERENCES `sinh_vien` (`ma_sv`),
  FOREIGN KEY (`ma_mon`) REFERENCES `mon_hoc` (`ma_mon`)
);
""")

    # Nạp dữ liệu Cố vấn
    for k in khoas:
        cv_ma, cv_ten, cv_email, cv_sdt = k["cvht"]
        sql.append(f"INSERT OR REPLACE INTO `co_van_hoc_tap` (`ma_cvht`, `ho_ten`, `email`, `so_dien_thoai`, `khoa`) VALUES ('{cv_ma}', '{cv_ten}', '{cv_email}', '{cv_sdt}', '{k['ten_khoa']}');")

    # Nạp lớp học
    for k in khoas:
        cv_ma = k["cvht"][0]
        for lop_ma, lop_ten in k["lops"]:
            sql.append(f"INSERT OR REPLACE INTO `lop_hoc` (`ma_lop`, `ten_lop`, `khoa`, `khoa_hoc`, `ma_cvht`) VALUES ('{lop_ma}', '{lop_ten}', '{k['ten_khoa']}', 2023, '{cv_ma}');")

    # Nạp sinh viên
    for sv in all_sv:
        sql.append(f"INSERT OR REPLACE INTO `sinh_vien` (`ma_sv`, `ho_ten`, `ngay_sinh`, `gioi_tinh`, `email`, `so_dien_thoai`, `ma_lop`, `trang_thai`, `tong_tin_chi_tich_luy`, `nam_thu`) VALUES ('{sv[0]}', '{sv[1]}', '{sv[2]}', '{sv[3]}', '{sv[4]}', '{sv[5]}', '{sv[6]}', '{sv[7]}', {sv[8]}, {sv[9]});")

    # Nạp kết quả học tập
    for kq in all_kq:
        sql.append(f"INSERT OR REPLACE INTO `ket_qua_hoc_tap` (`ma_sv`, `hoc_ky`, `nam_hoc`, `gpa_hoc_ky`, `gpa_tich_luy`, `so_tin_chi_no`, `tong_tin_chi_tich_luy`, `nam_thu`) VALUES ('{kq[1]}', {kq[2]}, '{kq[3]}', {kq[4]}, {kq[5]}, {kq[6]}, {kq[7]}, {kq[8]});")

    # Nạp cảnh báo
    for cb in all_cb:
        ly_do_esc = cb[7].replace("'", "''")
        sql.append(f"INSERT OR REPLACE INTO `canh_bao_hoc_vu` (`id`, `ma_canh_bao`, `ma_sv`, `hoc_ky`, `nam_hoc`, `muc_canh_bao`, `gpa_xet_duyet`, `ly_do`, `ngay_quyet_dinh`, `trang_thai_tu_van`) VALUES ({cb[0]}, '{cb[1]}', '{cb[2]}', {cb[3]}, '{cb[4]}', '{cb[5]}', {cb[6]}, '{ly_do_esc}', '{cb[8]}', '{cb[9]}');")

    # Nạp nhật ký tư vấn
    for nk in all_nk:
        nd_esc = nk[6].replace("'", "''")
        nn_esc = nk[7].replace("'", "''")
        gp_esc = nk[8].replace("'", "''")
        ck_esc = nk[9].replace("'", "''")
        sql.append(f"INSERT OR REPLACE INTO `nhat_ky_tu_van` (`id`, `ma_sv`, `ma_cvht`, `id_canh_bao`, `ngay_tu_van`, `hinh_thuc`, `noi_dung`, `nguyen_nhan`, `giai_phap`, `cam_ket_sinh_vien`) VALUES ({nk[0]}, '{nk[1]}', '{nk[2]}', {nk[3]}, '{nk[4]}', '{nk[5]}', '{nd_esc}', '{nn_esc}', '{gp_esc}', '{ck_esc}');")

    # Nạp tài khoản
    for tk in all_tk:
        ref_val = f"'{tk[5]}'" if tk[5] else "NULL"
        sql.append(f"INSERT OR REPLACE INTO `tai_khoan` (`ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `vai_tro`, `ma_ref`) VALUES ('{tk[0]}', '{tk[1]}', '{tk[2]}', '{tk[3]}', '{tk[4]}', {ref_val});")

    # Nạp chuyên cần mẫu môn học (Vắng 2 buổi -> cảnh báo; Vắng 4 buổi -> cấm thi)
    for sv in all_sv:
        ma_sv = sv[0]
        lop_ma = sv[6]
        # Môn IT204: Lập trình Java
        last_digits = int(ma_sv[-2:])
        if last_digits == 22 or last_digits == 23:
            # Vắng 2 buổi -> Cảnh báo chuyên cần
            sql.append(f"INSERT INTO `chuyen_can_mon_hoc` (`ma_sv`, `ma_mon`, `ma_lop`, `so_buoi_co_mat`, `so_buoi_vang_phep`, `so_buoi_vang_khong_phep`, `trang_thai_du_thi`, `ghi_chu`) VALUES ('{ma_sv}', 'IT204', '{lop_ma}', 13, 1, 1, 'CANH_BAO_NGUY_CO', 'Đã nghỉ 2 buổi môn Lập trình Java. Cần đi học đầy đủ!');")
        elif last_digits >= 24 and last_digits <= 25:
            # Vắng 4 buổi -> CẤM THI
            sql.append(f"INSERT INTO `chuyen_can_mon_hoc` (`ma_sv`, `ma_mon`, `ma_lop`, `so_buoi_co_mat`, `so_buoi_vang_phep`, `so_buoi_vang_khong_phep`, `trang_thai_du_thi`, `ghi_chu`) VALUES ('{ma_sv}', 'IT204', '{lop_ma}', 11, 0, 4, 'CAM_THI', 'Nghỉ 4/15 buổi (26.7% > 20%). CẤM THI HỌC PHẦN!');")
        else:
            sql.append(f"INSERT INTO `chuyen_can_mon_hoc` (`ma_sv`, `ma_mon`, `ma_lop`, `so_buoi_co_mat`, `so_buoi_vang_phep`, `so_buoi_vang_khong_phep`, `trang_thai_du_thi`, `ghi_chu`) VALUES ('{ma_sv}', 'IT204', '{lop_ma}', 15, 0, 0, 'DU_DIEU_KIEN', 'Đi học chuyên cần 100%');")

    # Nạp thông báo mẫu
    sql.append(f"""
INSERT INTO `thong_bao` (`ma_thong_bao`, `tieu_de`, `noi_dung`, `nhom_rui_ro`, `ma_lop`, `ma_sv`, `ngay_gui`, `nguoi_gui`, `so_luong_nhan`, `trang_thai`) VALUES 
('TB-001', '📢 Thông báo đăng ký môn học và xét tốt nghiệp 150 tín chỉ', 'Kính gửi toàn thể sinh viên, Nhà trường thông báo lịch đăng ký học phần học kỳ mới và xét điều kiện làm Đồ án tốt nghiệp 150 tín chỉ cho sinh viên Năm 4 và Năm 5.', 'ALL', 'ALL', NULL, CURRENT_TIMESTAMP, 'Phòng Quản lý Đào tạo', 125, 'DA_GUI'),
('TB-002', '⚠️ Cảnh báo chuyên cần: Bạn đã nghỉ 2 buổi học phần Java', 'Chào em, hệ thống ghi nhận em đã nghỉ 2 buổi môn Lập trình Java (IT204). Ngưỡng tối đa cho phép là 3 buổi (20%). Nếu em nghỉ thêm từ 1 buổi nữa sẽ bị CẤM THI học phần.', 'TIER_2', 'ALL', '20250022', CURRENT_TIMESTAMP, 'TS. Đinh Văn Phong', 1, 'DA_GUI'),
('TB-003', '🚫 Quyết định CẤM THI học phần do vắng quá 20% số buổi', 'Căn cứ quy chế đào tạo tín chỉ, sinh viên đã nghỉ 4/15 buổi môn Lập trình Java. Quyết định: CẤM THI KẾT THÚC HỌC PHẦN, nhận điểm 0 chuyên cần và bắt buộc học lại.', 'TIER_3', 'ALL', '20250024', CURRENT_TIMESTAMP, 'Phòng Đào tạo & Cố Vấn Học Vụ', 1, 'DA_GUI'),
('TB-004', '🎓 Cảnh báo tiến độ 150 tín chỉ: Năm thứ 4 tích lũy <= 120 tín', 'Chào em, hiện tại em đang học Năm thứ 4 nhưng mới tích lũy được 118/150 tín chỉ. Em có nguy cơ không đủ điều kiện làm Đồ án tốt nghiệp đúng hạn. Mời em gặp CVHT tại VP Khoa!', 'TIER_3', 'ALL', '20220022', CURRENT_TIMESTAMP, 'TS. Đinh Văn Phong', 1, 'DA_GUI');
""")

    return "\n".join(sql)

sqlite_content = generate_sqlite()
with open("sqlite_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)
with open("src/main/resources/sqlite_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)
with open("database.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)
with open("src/main/resources/database.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)

# Trực tiếp tạo CSDL SQLite data/ql_canhbao_hocvu.db
os.makedirs("data", exist_ok=True)
db_file = "data/ql_canhbao_hocvu.db"
if os.path.exists(db_file):
    try:
        os.remove(db_file)
    except Exception as e:
        print(f"Could not remove old db: {e}")

conn = sqlite3.connect(db_file)
conn.executescript(sqlite_content)
conn.commit()
conn.close()

print("Successfully regenerated 5-year simulation schemas and SQLite database!")
