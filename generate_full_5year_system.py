# -*- coding: utf-8 -*-
"""
HỆ THỐNG MÔ PHỎNG DỮ LIỆU ĐỒ SỘ 5 NĂM HOẠT ĐỘNG (2021 - 2026)
TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)
Chuẩn chương trình đào tạo kỹ sư / cử nhân 150 tín chỉ.
Bao gồm:
- 30 Lớp học thuộc 4 Khoa chuyên ngành (Khóa K11 -> K15)
- 350+ Sinh viên và 350+ Tài khoản đăng nhập
- 1,600+ Bản ghi Kết quả học tập (GPA kỳ, GPA tích lũy, số tín nợ, tổng tích lũy 150 tín chỉ)
- 120+ Quyết định Cảnh báo học vụ (Mức 1, 2, 3 & Buộc thôi học)
- 80+ Nhật ký tư vấn Cố vấn học tập (CVHT)
- 60+ Lịch giảng dạy & Cố vấn
- 500+ Lượt điểm danh chuyên cần
- 400+ Bản ghi Chuyên cần môn học & Cấm thi (vắng >= 20%)
- 50+ Thông báo & Hội thoại chat trực tuyến 2 chiều
- 100+ Bản ghi Nhật ký hệ thống (Audit Log) & 35+ Phiên làm việc (Active Sessions)
"""

import os
import sys
import sqlite3
import random
from datetime import datetime, timedelta

try:
    sys.stdout.reconfigure(encoding='utf-8')
except Exception:
    pass

random.seed(2026)

# 1. DANH MỤC VAI TRÒ
vai_tro_list = [
    ("ADMIN", "Quản trị viên Hệ thống", "Toàn quyền quản trị tài khoản, cấu hình và giám sát hệ thống"),
    ("CO_VAN", "Cố vấn Học tập", "Quản lý lớp phụ trách, theo dõi học vụ 150 tín chỉ, điểm danh, tư vấn sinh viên, chat trực tuyến"),
    ("SINH_VIEN", "Sinh viên", "Xem tiến độ 150 tín chỉ, điểm danh chuyên cần, nhận cảnh báo học vụ, chat với Cố vấn"),
    ("QUAN_LY", "Quản lý Đào tạo / Khoa", "Giám sát tổng thể học vụ toàn trường, thống kê phân tầng rủi ro, phê duyệt quyết định")
]

# 2. DANH MỤC CỐ VẤN HỌC TẬP & KHOA
khoa_list = [
    {
        "ma_khoa": "CNTT",
        "ten_khoa": "Công nghệ thông tin",
        "cvht": ("CV001", "TS. Đinh Văn Phong", "phong.dv@eaut.edu.vn", "0912345678", "cv_phongdv"),
        "mon_codes": ["IT101", "IT201", "IT202", "IT203", "IT301", "IT302", "IT303", "IT401", "IT402", "IT501"]
    },
    {
        "ma_khoa": "OTO",
        "ten_khoa": "Công nghệ kỹ thuật Ô tô",
        "cvht": ("CV002", "PGS.TS. Nguyễn Thanh Hải", "hai.nt@eaut.edu.vn", "0987654321", "cv_haint"),
        "mon_codes": ["AUTO101", "AUTO201", "AUTO301", "AUTO401", "AUTO501"]
    },
    {
        "ma_khoa": "QTKD",
        "ten_khoa": "Quản trị kinh doanh",
        "cvht": ("CV003", "ThS. Hoàng Thị Mai", "mai.ht@eaut.edu.vn", "0934567890", "cv_maiht"),
        "mon_codes": ["BA101", "BA201", "BA301", "BA401", "BA501"]
    },
    {
        "ma_khoa": "DDT",
        "ten_khoa": "Công nghệ kỹ thuật Điện - Điện tử",
        "cvht": ("CV004", "TS. Vũ Trường Sơn", "son.vt@eaut.edu.vn", "0945678901", "cv_sonvt"),
        "mon_codes": ["EE101", "EE201", "EE301", "EE401", "EE501"]
    }
]

# 3. DANH MỤC 25 MÔN HỌC
mon_hoc_list = [
    ("IT101", "Nhập môn Lập trình C/C++", 3, 45, 3, "Công nghệ thông tin"),
    ("IT201", "Lập trình Hướng đối tượng Java", 3, 45, 3, "Công nghệ thông tin"),
    ("IT202", "Cấu trúc Dữ liệu & Giải thuật", 3, 45, 3, "Công nghệ thông tin"),
    ("IT203", "Hệ quản trị Cơ sở Dữ liệu & SQL", 4, 60, 4, "Công nghệ thông tin"),
    ("IT301", "Kỹ thuật Phần mềm Nâng cao", 3, 45, 3, "Công nghệ thông tin"),
    ("IT302", "Mạng Máy tính & An ninh mạng", 3, 45, 3, "Công nghệ thông tin"),
    ("IT303", "Phát triển Ứng dụng Doanh nghiệp Java/Spring", 4, 60, 4, "Công nghệ thông tin"),
    ("IT401", "Phân tích Thiết kế Hệ thống Thông tin", 3, 45, 3, "Công nghệ thông tin"),
    ("IT402", "Trí tuệ Nhân tạo & Khai phá Dữ liệu", 4, 60, 4, "Công nghệ thông tin"),
    ("IT501", "Đồ án Tốt nghiệp Kỹ sư CNTT (150 Tín chỉ)", 10, 150, 3, "Công nghệ thông tin"),

    ("AUTO101", "Vẽ Kỹ thuật & CAD Cơ khí Ô tô", 3, 45, 3, "Công nghệ kỹ thuật Ô tô"),
    ("AUTO201", "Lý thuyết Động cơ Đốt trong", 3, 45, 3, "Công nghệ kỹ thuật Ô tô"),
    ("AUTO301", "Hệ thống Điện - Điện tử Ô tô", 4, 60, 4, "Công nghệ kỹ thuật Ô tô"),
    ("AUTO401", "Thực hành Chẩn đoán & Sửa chữa Ô tô", 4, 60, 4, "Công nghệ kỹ thuật Ô tô"),
    ("AUTO501", "Đồ án Tốt nghiệp Kỹ sư Kỹ thuật Ô tô", 10, 150, 3, "Công nghệ kỹ thuật Ô tô"),

    ("BA101", "Kinh tế Vi mô & Vĩ mô Đại cương", 3, 45, 3, "Quản trị kinh doanh"),
    ("BA201", "Quản trị học & Văn hóa Doanh nghiệp", 3, 45, 3, "Quản trị kinh doanh"),
    ("BA301", "Nguyên lý Kế toán & Tài chính Doanh nghiệp", 4, 60, 4, "Quản trị kinh doanh"),
    ("BA401", "Quản trị Chiến lược & Marketing Số", 4, 60, 4, "Quản trị kinh doanh"),
    ("BA501", "Khóa luận Tốt nghiệp Quản trị Kinh doanh", 10, 150, 3, "Quản trị kinh doanh"),

    ("EE101", "Mạch Điện & Đo lường Điện tử", 3, 45, 3, "Công nghệ kỹ thuật Điện - Điện tử"),
    ("EE201", "Kỹ thuật Lập trình Vi điều khiển & IoT", 4, 60, 4, "Công nghệ kỹ thuật Điện - Điện tử"),
    ("EE301", "Lý thuyết Điều khiển Tự động & PLC", 4, 60, 4, "Công nghệ kỹ thuật Điện - Điện tử"),
    ("EE401", "Hệ thống Cung cấp Điện & Robot Công nghiệp", 4, 60, 4, "Công nghệ kỹ thuật Điện - Điện tử"),
    ("EE501", "Đồ án Tốt nghiệp Kỹ sư Điện - Điện tử", 10, 150, 3, "Công nghệ kỹ thuật Điện - Điện tử")
]

# 4. DANH MỤC 30 LỚP HỌC MÔ PHỎNG 5 NĂM (K11 -> K15)
lop_hoc_list = [
    # K15: Năm 1 (2025)
    ("DCCTPM15A", "DCCTPM15A - Kỹ thuật Phần mềm K15A (Năm 1)", "Công nghệ thông tin", 2025, "CV001"),
    ("DCCTPM15B", "DCCTPM15B - Kỹ thuật Phần mềm K15B (Năm 1)", "Công nghệ thông tin", 2025, "CV001"),
    ("DCCNTT15A", "DCCNTT15A - Công nghệ Thông tin K15A (Năm 1)", "Công nghệ thông tin", 2025, "CV001"),
    ("DCOTO15A", "DCOTO15A - Kỹ thuật Ô tô 15A (Năm 1)", "Công nghệ kỹ thuật Ô tô", 2025, "CV002"),
    ("DCOTO15B", "DCOTO15B - Kỹ thuật Ô tô 15B (Năm 1)", "Công nghệ kỹ thuật Ô tô", 2025, "CV002"),
    ("DCQTKD15A", "DCQTKD15A - Quản trị Kinh doanh 15A (Năm 1)", "Quản trị kinh doanh", 2025, "CV003"),
    ("DCQTKD15B", "DCQTKD15B - Quản trị Kinh doanh 15B (Năm 1)", "Quản trị kinh doanh", 2025, "CV003"),
    ("DCDDT15A", "DCDDT15A - Điện - Điện tử 15A (Năm 1)", "Công nghệ kỹ thuật Điện - Điện tử", 2025, "CV004"),

    # K14: Năm 2 (2024)
    ("DCCTPM14A", "DCCTPM14A - Kỹ thuật Phần mềm K14A (Năm 2)", "Công nghệ thông tin", 2024, "CV001"),
    ("DCCTPM14B", "DCCTPM14B - Kỹ thuật Phần mềm K14B (Năm 2)", "Công nghệ thông tin", 2024, "CV001"),
    ("DCCNTT14A", "DCCNTT14A - Công nghệ Thông tin K14A (Năm 2)", "Công nghệ thông tin", 2024, "CV001"),
    ("DCOTO14A", "DCOTO14A - Kỹ thuật Ô tô 14A (Năm 2)", "Công nghệ kỹ thuật Ô tô", 2024, "CV002"),
    ("DCOTO14B", "DCOTO14B - Kỹ thuật Ô tô 14B (Năm 2)", "Công nghệ kỹ thuật Ô tô", 2024, "CV002"),
    ("DCQTKD14A", "DCQTKD14A - Quản trị Kinh doanh 14A (Năm 2)", "Quản trị kinh doanh", 2024, "CV003"),
    ("DCDDT14A", "DCDDT14A - Điện - Điện tử 14A (Năm 2)", "Công nghệ kỹ thuật Điện - Điện tử", 2024, "CV004"),

    # K13: Năm 3 (2023)
    ("DCCTPM13A", "DCCTPM13A - Kỹ thuật Phần mềm K13A (Năm 3)", "Công nghệ thông tin", 2023, "CV001"),
    ("DCCNTT13A", "DCCNTT13A - Công nghệ Thông tin K13A (Năm 3)", "Công nghệ thông tin", 2023, "CV001"),
    ("DCOTO13A", "DCOTO13A - Kỹ thuật Ô tô 13A (Năm 3)", "Công nghệ kỹ thuật Ô tô", 2023, "CV002"),
    ("DCQTKD13A", "DCQTKD13A - Quản trị Kinh doanh 13A (Năm 3)", "Quản trị kinh doanh", 2023, "CV003"),
    ("DCDDT13A", "DCDDT13A - Điện - Điện tử 13A (Năm 3)", "Công nghệ kỹ thuật Điện - Điện tử", 2023, "CV004"),

    # K12: Năm 4 (2022) - Chu kỳ cảnh báo tiến độ 150 tín chỉ
    ("DCCTPM12A", "DCCTPM12A - Kỹ thuật Phần mềm K12A (Năm 4)", "Công nghệ thông tin", 2022, "CV001"),
    ("DCCNTT12A", "DCCNTT12A - Công nghệ Thông tin K12A (Năm 4)", "Công nghệ thông tin", 2022, "CV001"),
    ("DCOTO12A", "DCOTO12A - Kỹ thuật Ô tô 12A (Năm 4)", "Công nghệ kỹ thuật Ô tô", 2022, "CV002"),
    ("DCQTKD12A", "DCQTKD12A - Quản trị Kinh doanh 12A (Năm 4)", "Quản trị kinh doanh", 2022, "CV003"),
    ("DCDDT12A", "DCDDT12A - Điện - Điện tử 12A (Năm 4)", "Công nghệ kỹ thuật Điện - Điện tử", 2022, "CV004"),

    # K11: Năm 5 (2021) - Tốt nghiệp 150 tín chỉ & ĐATN
    ("DCCNTT11A", "DCCNTT11A - Khoa học Máy tính K11A (Năm 5)", "Công nghệ thông tin", 2021, "CV001"),
    ("DCCTPM11A", "DCCTPM11A - Kỹ thuật Phần mềm K11A (Năm 5)", "Công nghệ thông tin", 2021, "CV001"),
    ("DCOTO11A", "DCOTO11A - Kỹ thuật Ô tô K11A (Năm 5)", "Công nghệ kỹ thuật Ô tô", 2021, "CV002"),
    ("DCQTKD11A", "DCQTKD11A - Quản trị Kinh doanh 11A (Năm 5)", "Quản trị kinh doanh", 2021, "CV003"),
    ("DCDDT11A", "DCDDT11A - Điện - Điện tử 11A (Năm 5)", "Công nghệ kỹ thuật Điện - Điện tử", 2021, "CV004")
]

# HỌ TÊN TIẾNG VIỆT
ho_list = ["Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Đinh", "Đoàn", "Trịnh", "Mai", "Trương", "Lâm"]
dem_nam = ["Văn", "Đức", "Hoàng", "Minh", "Quang", "Đình", "Tuấn", "Thanh", "Hữu", "Quốc", "Gia", "Trọng", "Tiến", "Bảo", "Xuân", "Mạnh"]
dem_nu = ["Thị", "Thu", "Phương", "Thùy", "Bảo", "Khánh", "Ngọc", "Thanh", "Hải", "Diệu", "Minh", "Hồng", "Mai", "Tuyết", "Quỳnh", "Ánh"]
ten_nam = ["Nam", "Long", "Huy", "Anh", "Tuấn", "Hải", "Bảo", "Hưng", "Khởi", "Kiệt", "Tùng", "Đức", "Quân", "Khánh", "Duy", "Hiếu", "Cường", "Trí", "Thắng", "Phong", "Đạt", "Phúc", "Thịnh", "Khoa", "Vinh", "Bách", "Triết", "Lộc", "Khang"]
ten_nu = ["Lan", "Dương", "Thảo", "Châu", "Giang", "Trang", "Linh", "Hoa", "Mai", "Nhi", "Huyền", "Yến", "Ngân", "Quyên", "Tuyết", "Vy", "Hà", "Vân", "Hiền", "Trâm", "Ngọc", "Nhung", "Chi", "Hương", "Anh"]

all_tk = []
all_sv = []
all_kq = []
all_cb = []
all_nk = []
all_tb = []
all_lich = []
all_diemdanh = []
all_chuyencan = []

PW_HASH = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92" # 123456

# 1. TÀI KHOẢN ADMIN & QUẢN LÝ
all_tk.append(("admin", PW_HASH, "Quản trị viên Hệ thống EAUT", "admin@eaut.edu.vn", "ADMIN", None))
all_tk.append(("quanly", PW_HASH, "Trưởng phòng Đào tạo EAUT", "daotao@eaut.edu.vn", "QUAN_LY", None))

# 2. TÀI KHOẢN CỐ VẤN
for k in khoa_list:
    cv_ma, cv_ten, cv_email, cv_sdt, cv_user = k["cvht"]
    all_tk.append((cv_user, PW_HASH, cv_ten, cv_email, "CO_VAN", cv_ma))
    all_tk.append((cv_ma.lower(), PW_HASH, cv_ten, cv_email, "CO_VAN", cv_ma))

# 3. TẠO SINH VIÊN 5 NĂM (K11 - K15)
semesters = [
    (1, "2021-2022"), (2, "2021-2022"),
    (1, "2022-2023"), (2, "2022-2023"),
    (1, "2023-2024"), (2, "2023-2024"),
    (1, "2024-2025"), (2, "2024-2025"),
    (1, "2025-2026")
]

cb_id_counter = 1
nk_id_counter = 1
kq_id_counter = 1

# Mỗi lớp sinh 12-14 sinh viên -> 30 lớp = ~380 sinh viên
sv_global_id = 1

for lop in lop_hoc_list:
    ma_lop = lop[0]
    ten_lop = lop[1]
    khoa_ten = lop[2]
    khoa_hoc = lop[3]
    ma_cvht = lop[4]
    nam_thu = 2026 - khoa_hoc
    if nam_thu < 1: nam_thu = 1
    if nam_thu > 5: nam_thu = 5

    # Số lượng sinh viên mỗi lớp
    num_sv = 12 if nam_thu >= 4 else 14
    sem_count = nam_thu * 2 - 1
    if sem_count > 9: sem_count = 9
    cohort_sems = semesters[9 - sem_count:]

    for i in range(1, num_sv + 1):
        sv_idx = sv_global_id
        sv_global_id += 1
        ma_sv = f"{khoa_hoc}{sv_idx:04d}"

        is_nu = (sv_idx % 3 == 0)
        gt = "Nữ" if is_nu else "Nam"
        ho = ho_list[(sv_idx * 7 + i) % len(ho_list)]
        dem = dem_nu[(sv_idx * 3 + i) % len(dem_nu)] if is_nu else dem_nam[(sv_idx * 5 + i) % len(dem_nam)]
        ten = ten_nu[(sv_idx * 2 + i) % len(ten_nu)] if is_nu else ten_nam[(sv_idx * 3 + i) % len(ten_nam)]
        ho_ten = f"{ho} {dem} {ten}"
        email = f"sv{ma_sv}@eaut.edu.vn"
        sdt = f"091{nam_thu}{sv_idx:06d}"
        birth_year = (2007 - nam_thu)
        ngay_sinh = f"{birth_year}-{((sv_idx*3)%12)+1:02d}-{((sv_idx*5)%28)+1:02d}"

        # Phân tầng học lực 5 năm
        tier_mod = (i % 6)
        if tier_mod in [1, 2]:
            # Nhóm Giỏi / Xuất sắc
            base_gpa = round(random.uniform(3.4, 3.85), 2)
            tc_per_sem = random.randint(18, 22)
            warning_level = None
        elif tier_mod in [3, 4]:
            # Nhóm Khá / Trung bình khá
            base_gpa = round(random.uniform(2.6, 3.1), 2)
            tc_per_sem = random.randint(16, 19)
            warning_level = None
        elif tier_mod == 5:
            # Nhóm Cảnh báo Mức 1 / Mức 2 (Nguy cơ nợ tín chỉ / tiến độ)
            base_gpa = round(random.uniform(1.3, 1.85), 2)
            tc_per_sem = random.randint(10, 14)
            warning_level = "MUC_2" if (nam_thu >= 3 and i % 2 == 0) else "MUC_1"
        else: # tier_mod == 0
            # Nhóm Cảnh báo Nghiêm trọng (Mức 3 hoặc Buộc thôi học)
            base_gpa = round(random.uniform(0.7, 1.35), 2)
            tc_per_sem = random.randint(6, 11)
            warning_level = "BUOC_THOI_HOC" if (nam_thu >= 4 and i % 4 == 0) else "MUC_3"

        # Tính toán tích lũy theo năm thứ
        standard_credits = min(150, nam_thu * 30)
        if warning_level in ["MUC_3", "BUOC_THOI_HOC"]:
            accum_credits = max(10, int(standard_credits * 0.55))
            debt_credits = int(standard_credits * 0.45)
            status = "BUOC_THOI_HOC" if warning_level == "BUOC_THOI_HOC" else "CANH_BAO"
        elif warning_level in ["MUC_1", "MUC_2"]:
            # Cảnh báo tiến độ 150 tín chỉ (đặc biệt năm 4 < 120 tín)
            if nam_thu == 4:
                accum_credits = random.randint(110, 119) # < 120 tín -> Kích hoạt cảnh báo 150 tín!
            else:
                accum_credits = int(standard_credits * 0.75)
            debt_credits = standard_credits - accum_credits
            status = "DANG_HOC"
        else:
            if nam_thu == 5:
                accum_credits = random.randint(145, 150)
            elif nam_thu == 4:
                accum_credits = random.randint(126, 136) # Đạt chuẩn giao ĐATN
            else:
                accum_credits = int(standard_credits * random.uniform(0.95, 1.05))
            debt_credits = max(0, random.randint(0, 3))
            status = "DANG_HOC"

        all_sv.append((ma_sv, ho_ten, ngay_sinh, gt, email, sdt, ma_lop, status, accum_credits, nam_thu))
        all_tk.append((ma_sv, PW_HASH, ho_ten, email, "SINH_VIEN", ma_sv))

        # 4. TẠO LỊCH SỬ KẾT QUẢ HỌC TẬP QUA CÁC HỌC KỲ (KET_QUA_HOC_TAP)
        running_tc = 0
        for s_idx, sem in enumerate(cohort_sems, 1):
            hk_num = sem[0]
            nh_str = sem[1]
            cur_year_study = (s_idx + 1) // 2
            if cur_year_study < 1: cur_year_study = 1

            if warning_level in ["MUC_3", "BUOC_THOI_HOC"]:
                hk_gpa = round(max(0.5, base_gpa + random.uniform(-0.3, 0.3)), 2)
                cur_debt = random.randint(6, 14)
                add_tc = random.randint(8, 12)
            elif warning_level in ["MUC_1", "MUC_2"]:
                hk_gpa = round(max(1.1, base_gpa + random.uniform(-0.25, 0.4)), 2)
                cur_debt = random.randint(3, 8)
                add_tc = random.randint(12, 16)
            else:
                hk_gpa = round(min(4.0, base_gpa + random.uniform(-0.2, 0.2)), 2)
                cur_debt = 0 if base_gpa >= 3.2 else random.randint(0, 3)
                add_tc = random.randint(17, 21)

            running_tc += add_tc
            if running_tc > accum_credits and s_idx == len(cohort_sems):
                running_tc = accum_credits

            cpa_val = round((base_gpa * (s_idx - 1) + hk_gpa) / s_idx, 2)
            all_kq.append((ma_sv, hk_num, nh_str, hk_gpa, cpa_val, cur_debt, running_tc, cur_year_study))

        # 5. TẠO CẢNH BÁO HỌC VỤ & NHẬT KÝ TƯ VẤN NẾU CÓ NGUY CƠ
        if warning_level is not None:
            last_sem = cohort_sems[-1]
            ma_cb = f"CB-{last_sem[1]}-HK{last_sem[0]}-{cb_id_counter:04d}"
            cb_id_counter += 1

            if nam_thu == 4 and accum_credits < 120:
                ly_do = f"Tiến độ tích lũy Năm 4 chỉ đạt {accum_credits}/150 tín chỉ (dưới ngưỡng chuẩn 120 tín), nợ {debt_credits} tín chỉ. Nguy cơ chậm tốt nghiệp Đồ án Kỹ sư."
            elif warning_level == "MUC_1":
                ly_do = f"GPA học kỳ {last_sem[0]} ({nh_str}) đạt {base_gpa} (< 1.6), nợ {debt_credits} tín chỉ. Đề nghị liên hệ CVHT lập kế hoạch học tập."
            elif warning_level == "MUC_2":
                ly_do = f"GPA tích lũy đạt {base_gpa} (< 1.8) liên tiếp 2 học kỳ, nợ đọng {debt_credits} tín chỉ học phần tiên quyết."
            elif warning_level == "MUC_3":
                ly_do = f"GPA kỳ dưới 1.2, tổng số tín chỉ không đạt vượt quá 16 tín chỉ. Thuộc diện phân tầng rủi ro cao (Tier 3)."
            else: # BUOC_THOI_HOC
                ly_do = f"Cảnh báo học vụ Mức 3 liên tiếp 3 học kỳ, CPA < 0.90. Hội đồng Kỷ luật & Đào tạo xem xét buộc thôi học."

            quyet_dinh_date = "2026-08-15" if last_sem[0] == 1 else "2026-01-20"
            trang_thai_tv = "DA_TU_VAN" if (sv_idx % 2 == 0) else "CHUA_TU_VAN"
            all_cb.append((cb_id_counter, ma_cb, ma_sv, last_sem[0], last_sem[1], warning_level, base_gpa, ly_do, quyet_dinh_date, trang_thai_tv))

            # Nhật ký tư vấn tương ứng
            if trang_thai_tv == "DA_TU_VAN":
                hinh_thuc = "Gặp trực tiếp tại Văn phòng Khoa" if sv_idx % 3 == 0 else ("Họp trực tuyến qua Zoom/Meet" if sv_idx % 3 == 1 else "Gọi điện trao đổi với phụ huynh & sinh viên")
                noi_dung = f"Cố vấn học tập đã làm việc với sinh viên {ho_ten} về kết quả học tập và cảnh báo học vụ {warning_level}. Rà soát bảng điểm các học phần bị nợ ({debt_credits} tín chỉ)."
                nguyen_nhan = "Sinh viên đi làm thêm nhiều ca đêm, phân bổ thời gian tự học chưa hợp lý, vắng một số buổi lý thuyết trọng tâm."
                giai_phap = "Rút bớt 2 môn khó học kỳ tới để tập trung trả nợ 2 môn tiên quyết. Đăng ký tham gia nhóm hỗ trợ học tập của Khoa. Giảm giờ làm thêm."
                cam_ket = f"Sinh viên cam kết tham gia đầy đủ >= 90% các buổi học trên lớp, đạt GPA học kỳ tới >= 2.3 và tích lũy thêm tối thiểu 14 tín chỉ."
                all_nk.append((nk_id_counter, ma_sv, ma_cvht, cb_id_counter, "2026-08-25", hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket))
                nk_id_counter += 1

# 6. TẠO LỊCH GIẢNG DẠY & CỐ VẤN HỌC TẬP (60+ BUỔI 5 NĂM)
lich_id_counter = 1
sample_dates = [
    ("2026-09-05", "07:30", "09:30", "Phòng 402-A2 (EAUT)", "TRUC_TIEP", "GIANG_DAY", "Giảng dạy Chuyên đề Lập trình Java Hướng đối tượng & Design Patterns"),
    ("2026-09-08", "13:30", "15:30", "Phòng 505-A1 (EAUT)", "TRUC_TIEP", "CO_VAN", "Sinh hoạt lớp định kỳ đầu học kỳ: Phổ biến tiến độ tích lũy 150 tín chỉ & Cảnh báo học vụ"),
    ("2026-09-10", "09:45", "11:45", "Xưởng Cơ khí Ô tô EAUT", "TRUC_TIEP", "GIANG_DAY", "Thực hành Chẩn đoán Động cơ & Hệ thống Điện Ô tô"),
    ("2026-09-12", "14:00", "16:00", "Phòng Họp Trực Tuyến Zoom (ID: 889 2406 05)", "ONLINE", "CO_VAN", "Tư vấn cá nhân hóa cho nhóm sinh viên có nguy cơ cảnh báo học vụ Mức 2 & 3"),
    ("2026-09-15", "08:00", "11:00", "Hội trường Lớn Tòa nhà EAUT", "TRUC_TIEP", "GIANG_DAY", "Hội thảo Định hướng Đồ án Tốt nghiệp 10 Tín chỉ & Cơ hội Việc làm Doanh nghiệp"),
    ("2026-09-18", "15:00", "17:00", "Phòng 303-A2 (EAUT)", "TRUC_TIEP", "CO_VAN", "Gặp gỡ sinh viên Năm 4 có số tín chỉ tích lũy < 120 tín để làm kế hoạch trả nợ môn")
]

for lop in lop_hoc_list:
    ma_lop = lop[0]
    ten_lop = lop[1]
    ma_cv = lop[4]
    cv_info = next((k["cvht"] for k in khoa_list if k["cvht"][0] == ma_cv), None)
    ten_cv = cv_info[1] if cv_info else "TS. Đinh Văn Phong"

    # Mỗi lớp có 2-3 lịch giảng dạy/cố vấn
    for s_date, g_start, g_end, room, h_thuc, l_buoi, note in sample_dates[:2]:
        tieu_de = f"Lịch {l_buoi} - {ten_lop} ({s_date})"
        all_lich.append((lich_id_counter, ma_cv, ten_cv, ma_lop, ten_lop, tieu_de, s_date, g_start, g_end, room, h_thuc, l_buoi, "SCHEDULED", note))
        
        # Tạo điểm danh mẫu cho các sinh viên trong lớp này
        sv_in_lop = [s for s in all_sv if s[6] == ma_lop]
        for sv in sv_in_lop[:8]: # 8 sv điểm danh
            ma_sv = sv[0]
            tt_dd = "CO_MAT" if (int(ma_sv[-2:]) % 5 != 0) else ("VANG_CO_PHEP" if int(ma_sv[-2:]) % 2 == 0 else "VANG_KHONG_PHEP")
            ghi_chu_dd = "Có mặt đúng giờ" if tt_dd == "CO_MAT" else ("Có đơn xin phép ốm" if tt_dd == "VANG_CO_PHEP" else "Vắng không lý do")
            all_diemdanh.append((lich_id_counter, ma_sv, s_date, tt_dd, ghi_chu_dd))

        lich_id_counter += 1

# 7. TẠO CHUYÊN CẦN MÔN HỌC & CẤM THI CHO TẤT CẢ SINH VIÊN (400+ BẢN GHI)
for sv in all_sv:
    ma_sv = sv[0]
    ma_lop = sv[6]
    nam_thu = sv[9]
    # Lấy 1-2 môn học đại diện của khoa
    k_found = None
    for k in khoa_list:
        if k["ten_khoa"] == next(l[2] for l in lop_hoc_list if l[0] == ma_lop):
            k_found = k
            break
    mon_list = k_found["mon_codes"] if k_found else ["IT201", "IT203"]
    mon_xet = mon_list[min(len(mon_list)-1, nam_thu - 1)]

    # Sinh tỷ lệ chuyên cần
    sv_num = int(ma_sv[-3:]) if len(ma_sv) >= 3 else 1
    if sv_num % 12 == 0:
        # CẤM THI: Vắng 4 buổi / 15 buổi (26.7% >= 20%)
        tong_buoi = 15
        co_mat = 11
        muon = 1
        v_phep = 1
        v_kphep = 3
        tong_vang = 3.5
        ty_le = 23.3
        diem_cc = 0.0
        tt_duthi = "CAM_THI"
        ly_do_cam = "Nghỉ học vượt quá 20% tổng số tiết học phần (Vắng 4 buổi / 15 buổi chuẩn). Không đủ điều kiện dự thi kết thúc học phần."
    elif sv_num % 6 == 0:
        # CẢNH BÁO NGHỈ 2 BUỔI (Gần chạm ngưỡng cấm thi)
        tong_buoi = 15
        co_mat = 13
        muon = 1
        v_phep = 1
        v_kphep = 1
        tong_vang = 1.5
        ty_le = 10.0
        diem_cc = 6.5
        tt_duthi = "DU_DIEU_KIEN"
        ly_do_cam = None
    else:
        # Chuyên cần tốt
        tong_buoi = 15
        co_mat = 15
        muon = 0
        v_phep = 0
        v_kphep = 0
        tong_vang = 0.0
        ty_le = 0.0
        diem_cc = 10.0
        tt_duthi = "DU_DIEU_KIEN"
        ly_do_cam = None

    all_chuyencan.append((ma_sv, mon_xet, ma_lop, 1, "2025-2026", tong_buoi, co_mat, muon, v_phep, v_kphep, tong_vang, ty_le, diem_cc, tt_duthi, ly_do_cam, "Theo dõi chuyên cần tự động"))

# 8. THÔNG BÁO & TIN NHẮN CHAT TRỰC TUYẾN 2 CHIỀU
all_tb.append(("TB-BC-001", "📢 Thông báo Đăng ký Học phần & Kế hoạch Khung Đào tạo 150 Tín chỉ",
               "Kính gửi toàn thể sinh viên Trường Đại học Công nghệ Đông Á (EAUT),\nNhà trường thông báo kế hoạch đăng ký học phần học kỳ mới theo chuẩn khung 150 tín chỉ. Đề nghị sinh viên các khóa K11 - K15 kiểm tra tiến độ tích lũy và liên hệ CVHT nếu gặp vướng mắc.",
               "ALL", "ALL", None, "2026-08-15 08:00:00", "Phòng Quản lý Đào tạo EAUT", len(all_sv), "DA_GUI"))

all_tb.append(("TB-BC-002", "⚠️ Thông báo Quy chế Điểm danh & Ngưỡng Cấm thi Học phần (Vắng >= 20%)",
               "Căn cứ Quy chế đào tạo đại học tín chỉ: Sinh viên nghỉ học quá 20% tổng số tiết (tương đương từ 3-4 buổi tùy học phần) sẽ bị CẤM THI kết thúc học phần. Hệ thống sẽ tự động gửi cảnh báo khi sinh viên nghỉ 2 buổi.",
               "ALL", "ALL", None, "2026-08-20 09:00:00", "Phòng Đào tạo & Khảo thí", len(all_sv), "DA_GUI"))

all_tb.append(("TB-BC-003", "🎓 Thông báo Xét Điều kiện Giao Đề tài Đồ án Tốt nghiệp Kỹ sư (Khóa K12 & K11)",
               "Điều kiện nhận ĐATN 10 tín chỉ: Sinh viên Năm 4/Năm 5 tích lũy tối thiểu 125/150 tín chỉ, CPA >= 2.0 và không vi phạm quy chế đào tạo. Các trường hợp đạt <= 120 tín chỉ phải đăng ký học trả nợ trước.",
               "TIER_3", "ALL", None, "2026-08-25 10:00:00", "Khoa Công nghệ Thông tin", 45, "DA_GUI"))

# Hội thoại Chat 2 chiều
chat_samples = [
    (all_sv[0][0], [
        ("SV", "Em chào thầy Phong ạ! Thầy ơi cho em hỏi về lịch học bổ sung học phần Lập trình Java với ạ.", "2026-08-26 14:15:00"),
        ("CV", "Chào em! Lịch học bổ sung sẽ bắt đầu vào tuần thứ 3 của học kỳ, em nhớ kiểm tra thời khóa biểu trên hệ thống nhé!", "2026-08-26 14:18:00"),
        ("SV", "Dạ vâng em cảm ơn thầy nhiều ạ!", "2026-08-26 14:20:00")
    ]),
    (all_sv[20][0], [
        ("SV", "Thầy ơi em nhận được thông báo cảnh báo đã nghỉ 2 buổi môn Cấu trúc dữ liệu. Em lo quá thầy ạ!", "2026-08-28 09:10:00"),
        ("CV", "Chào em! Môn này có tổng 15 buổi, ngưỡng vắng tối đa là 3 buổi (20%). Em đã nghỉ 2 buổi rồi nên tuyệt đối không được nghỉ thêm buổi nào nữa nhé!", "2026-08-28 09:15:00"),
        ("SV", "Dạ em hiểu rồi, em sẽ đi học đầy đủ các buổi còn lại ạ.", "2026-08-28 09:18:00")
    ])
]

chat_cnt = 1
for m_sv, msgs in chat_samples:
    sv_info = next((s for s in all_sv if s[0] == m_sv), None)
    ten_sv = sv_info[1] if sv_info else "Sinh viên"
    ma_lop = sv_info[6] if sv_info else "ALL"
    for sender_t, content, m_time in msgs:
        code = f"CHAT-{chat_cnt:04d}"
        s_name = f"{ten_sv} ({m_sv})" if sender_t == "SV" else "TS. Đinh Văn Phong (Cố vấn học tập)"
        t_title = f"Tin nhắn từ {ten_sv}" if sender_t == "SV" else "Phản hồi từ Cố vấn học tập"
        all_tb.append((code, t_title, content, "CHAT", ma_lop, m_sv, m_time, s_name, 1, "DA_GUI"))
        chat_cnt += 1

# 9. TẠO AUDIT LOG & PHIÊN HOẠT ĐỘNG (80+ AUDIT LOGS, 30+ SESSIONS)
all_audit_logs = []
all_user_sessions = []

# Mẫu 30 phiên làm việc
session_samples = [
    ("admin", "Quản trị viên Hệ thống EAUT", "ADMIN", "2026-09-14 08:00:00", None, "ONLINE", "127.0.0.1", "Java Swing Desktop (Admin Console)"),
    ("quanly", "Trưởng phòng Đào tạo EAUT", "QUAN_LY", "2026-09-14 08:05:00", None, "ONLINE", "192.168.1.12", "Java Swing Desktop (P.Đào Tạo)"),
    ("cv_phongdv", "TS. Đinh Văn Phong", "CO_VAN", "2026-09-14 08:15:00", None, "ONLINE", "192.168.1.105", "Java Swing Desktop (Khoa CNTT)"),
    ("cv_haint", "PGS.TS. Nguyễn Thanh Hải", "CO_VAN", "2026-09-14 08:20:00", None, "ONLINE", "192.168.1.108", "Java Swing Desktop (Khoa Ô tô)"),
    ("cv_maiht", "ThS. Hoàng Thị Mai", "CO_VAN", "2026-09-14 08:25:00", None, "ONLINE", "192.168.1.112", "Java Swing Desktop (Khoa QTKD)"),
    ("cv_sonvt", "TS. Vũ Trường Sơn", "CO_VAN", "2026-09-14 08:30:00", None, "ONLINE", "192.168.1.115", "Java Swing Desktop (Khoa Điện)"),
]

for idx in range(1, 25):
    sv = all_sv[idx]
    is_on = (idx % 3 == 0)
    tt = "ONLINE" if is_on else "OFFLINE"
    out_t = None if is_on else "2026-09-14 09:30:00"
    session_samples.append((sv[0], sv[1], "SINH_VIEN", f"2026-09-14 08:{10+idx:02d}:00", out_t, tt, f"192.168.1.{100+idx}", "EAUT Student Portal"))

for s in session_samples:
    all_user_sessions.append(s)

# 80+ Audit Logs
audit_actions = [
    ("admin", "Quản trị viên Hệ thống EAUT", "ADMIN", "DANG_NHAP", "Đăng nhập thành công vào hệ thống quản trị", "127.0.0.1", "Java Swing Desktop", "2026-09-14 08:00:00", "ONLINE"),
    ("admin", "Quản trị viên Hệ thống EAUT", "ADMIN", "THEM_MOI", "Khởi tạo dữ liệu phân quyền 5 năm cho 30 lớp học và 350+ sinh viên", "127.0.0.1", "Java Swing Desktop", "2026-09-14 08:02:00", "ONLINE"),
    ("quanly", "Trưởng phòng Đào tạo EAUT", "QUAN_LY", "DANG_NHAP", "Đăng nhập thành công vào hệ thống quản lý đào tạo", "192.168.1.12", "Java Swing Desktop", "2026-09-14 08:05:00", "ONLINE"),
    ("quanly", "Trưởng phòng Đào tạo EAUT", "QUAN_LY", "XUAT_FILE", "Xuất báo cáo thống kê phân tầng rủi ro 3-Tier toàn trường (Excel)", "192.168.1.12", "Java Swing Desktop", "2026-09-14 08:10:00", "ONLINE"),
    ("cv_phongdv", "TS. Đinh Văn Phong", "CO_VAN", "DANG_NHAP", "Cố vấn Khoa CNTT đăng nhập vào hệ thống", "192.168.1.105", "Java Swing Desktop", "2026-09-14 08:15:00", "ONLINE"),
    ("cv_phongdv", "TS. Đinh Văn Phong", "CO_VAN", "DIEM_DANH", "Lưu kết quả điểm danh Buổi 1 lớp DCCTPM15A (Môn Nhập môn Lập trình C)", "192.168.1.105", "Java Swing Desktop", "2026-09-14 08:20:00", "ONLINE"),
    ("cv_phongdv", "TS. Đinh Văn Phong", "CO_VAN", "CANH_BAO", "Gửi cảnh báo tiến độ 150 tín chỉ cho nhóm sinh viên Năm 4 có số tín <= 120", "192.168.1.105", "Java Swing Desktop", "2026-09-14 08:25:00", "ONLINE"),
]

for i in range(1, 75):
    sv = all_sv[i]
    act = "DANG_NHAP" if i % 2 == 0 else "TRUY_CAP"
    mota = "Sinh viên đăng nhập tra cứu tiến độ 150 tín chỉ và bảng điểm cá nhân" if act == "DANG_NHAP" else "Xem thông báo học vụ và chuyên cần môn học"
    audit_actions.append((sv[0], sv[1], "SINH_VIEN", act, mota, f"192.168.1.{100+i}", "Java Swing Desktop", f"2026-09-14 08:{15+(i%40):02d}:00", "ONLINE" if i%3==0 else "OFFLINE"))

for a in audit_actions:
    all_audit_logs.append(a)

print(f"=== TỔNG KẾT DỮ LIỆU ĐÃ TẠO ===")
print(f"  - Lớp học: {len(lop_hoc_list)} lớp")
print(f"  - Sinh viên: {len(all_sv)} sinh viên")
print(f"  - Tài khoản: {len(all_tk)} tài khoản")
print(f"  - Kết quả học tập: {len(all_kq)} bản ghi")
print(f"  - Cảnh báo học vụ: {len(all_cb)} quyết định")
print(f"  - Nhật ký tư vấn: {len(all_nk)} biên bản")
print(f"  - Lịch giảng dạy: {len(all_lich)} buổi")
print(f"  - Điểm danh: {len(all_diemdanh)} lượt")
print(f"  - Chuyên cần môn học: {len(all_chuyencan)} bản ghi")
print(f"  - Thông báo: {len(all_tb)} thông báo & chat")
print(f"  - Audit Log: {len(all_audit_logs)} bản ghi")
print(f"  - User Sessions: {len(all_user_sessions)} phiên")

# ==============================================================================
# HÀM TẠO DDL & INSERT CHO SQL SERVER VÀ SQLITE
# ==============================================================================

def generate_sqlite_sql():
    sql = []
    sql.append("-- CSDL SQLITE QUẢN LÝ CỐ VẤN HỌC TẬP VÀ CẢNH BÁO HỌC VỤ (EAUT 5 NĂM ĐỒ SỘ)")
    sql.append("PRAGMA foreign_keys = OFF;")
    sql.append("DROP TABLE IF EXISTS phien_dang_nhap;")
    sql.append("DROP TABLE IF EXISTS nhat_ky_he_thong;")
    sql.append("DROP TABLE IF EXISTS diem_danh;")
    sql.append("DROP TABLE IF EXISTS chuyen_can_mon_hoc;")
    sql.append("DROP TABLE IF EXISTS lich_giang_day;")
    sql.append("DROP TABLE IF EXISTS thong_bao;")
    sql.append("DROP TABLE IF EXISTS nhat_ky_tu_van;")
    sql.append("DROP TABLE IF EXISTS canh_bao_hoc_vu;")
    sql.append("DROP TABLE IF EXISTS ket_qua_hoc_tap;")
    sql.append("DROP TABLE IF EXISTS sinh_vien;")
    sql.append("DROP TABLE IF EXISTS lop_hoc;")
    sql.append("DROP TABLE IF EXISTS mon_hoc;")
    sql.append("DROP TABLE IF EXISTS co_van_hoc_tap;")
    sql.append("DROP TABLE IF EXISTS tai_khoan;")
    sql.append("DROP TABLE IF EXISTS vai_tro;")
    sql.append("PRAGMA foreign_keys = ON;\n")

    sql.append("CREATE TABLE vai_tro (ma_vai_tro VARCHAR(20) PRIMARY KEY, ten_vai_tro TEXT NOT NULL, mo_ta TEXT);")
    sql.append("CREATE TABLE tai_khoan (id INTEGER PRIMARY KEY AUTOINCREMENT, ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL, mat_khau TEXT NOT NULL, ho_ten TEXT NOT NULL, email TEXT NOT NULL, vai_tro VARCHAR(20) NOT NULL, ma_ref VARCHAR(20), ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP);")
    sql.append("CREATE TABLE co_van_hoc_tap (ma_cvht VARCHAR(20) PRIMARY KEY, ho_ten TEXT NOT NULL, email TEXT NOT NULL, so_dien_thoai TEXT, khoa TEXT NOT NULL);")
    sql.append("CREATE TABLE mon_hoc (ma_mon VARCHAR(20) PRIMARY KEY, ten_mon TEXT NOT NULL, so_tin_chi INTEGER NOT NULL, so_tiet INTEGER NOT NULL, so_buoi_toi_da_vang INTEGER NOT NULL, khoa TEXT NOT NULL);")
    sql.append("CREATE TABLE lop_hoc (ma_lop VARCHAR(20) PRIMARY KEY, ten_lop TEXT NOT NULL, khoa TEXT NOT NULL, khoa_hoc INTEGER NOT NULL, ma_cvht VARCHAR(20));")
    sql.append("CREATE TABLE sinh_vien (ma_sv VARCHAR(20) PRIMARY KEY, ho_ten TEXT NOT NULL, ngay_sinh DATE NOT NULL, gioi_tinh TEXT NOT NULL, email TEXT NOT NULL, so_dien_thoai TEXT, ma_lop VARCHAR(20) NOT NULL, trang_thai VARCHAR(30) DEFAULT 'DANG_HOC', tong_tin_chi_tich_luy INTEGER DEFAULT 0, nam_thu INTEGER DEFAULT 1);")
    sql.append("CREATE TABLE ket_qua_hoc_tap (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_sv VARCHAR(20) NOT NULL, hoc_ky INTEGER NOT NULL, nam_hoc VARCHAR(20) NOT NULL, gpa_hoc_ky REAL NOT NULL, gpa_tich_luy REAL NOT NULL, so_tin_chi_no INTEGER DEFAULT 0, tong_tin_chi_tich_luy INTEGER DEFAULT 0, nam_thu INTEGER DEFAULT 1);")
    sql.append("CREATE TABLE canh_bao_hoc_vu (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_canh_bao VARCHAR(50) UNIQUE NOT NULL, ma_sv VARCHAR(20) NOT NULL, hoc_ky INTEGER NOT NULL, nam_hoc VARCHAR(20) NOT NULL, muc_canh_bao VARCHAR(30) NOT NULL, gpa_xet_duyet REAL NOT NULL, ly_do TEXT, ngay_quyet_dinh DATE NOT NULL, trang_thai_tu_van VARCHAR(30) DEFAULT 'CHUA_TU_VAN');")
    sql.append("CREATE TABLE nhat_ky_tu_van (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_sv VARCHAR(20) NOT NULL, ma_cvht VARCHAR(20) NOT NULL, id_canh_bao INTEGER, ngay_tu_van DATE NOT NULL, hinh_thuc TEXT NOT NULL, noi_dung TEXT NOT NULL, nguyen_nhan TEXT, giai_phap TEXT, cam_ket_sinh_vien TEXT);")
    sql.append("CREATE TABLE thong_bao (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_thong_bao VARCHAR(50) UNIQUE NOT NULL, tieu_de TEXT NOT NULL, noi_dung TEXT NOT NULL, nhom_rui_ro VARCHAR(30) DEFAULT 'ALL', ma_lop VARCHAR(20) DEFAULT 'ALL', ma_sv VARCHAR(20), ngay_gui DATETIME DEFAULT CURRENT_TIMESTAMP, nguoi_gui TEXT, so_luong_nhan INTEGER DEFAULT 0, trang_thai VARCHAR(30) DEFAULT 'DA_GUI');")
    sql.append("CREATE TABLE lich_giang_day (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_cvht VARCHAR(20) NOT NULL, ten_cvht TEXT NOT NULL, ma_lop VARCHAR(20) NOT NULL, ten_lop TEXT NOT NULL, tieu_de TEXT NOT NULL, ngay DATE NOT NULL, gio_bat_dau VARCHAR(20) NOT NULL, gio_ket_thuc VARCHAR(20) NOT NULL, dia_diem TEXT NOT NULL, hinh_thuc VARCHAR(50) DEFAULT 'TRUC_TIEP', loai_buoi VARCHAR(50) DEFAULT 'GIANG_DAY', trang_thai VARCHAR(30) DEFAULT 'SCHEDULED', ghi_chu TEXT);")
    sql.append("CREATE TABLE diem_danh (id INTEGER PRIMARY KEY AUTOINCREMENT, id_lich INTEGER NOT NULL, ma_sv VARCHAR(20) NOT NULL, ngay_diem_danh DATE NOT NULL, trang_thai VARCHAR(30) DEFAULT 'CO_MAT', ghi_chu TEXT);")
    sql.append("CREATE TABLE chuyen_can_mon_hoc (id INTEGER PRIMARY KEY AUTOINCREMENT, ma_sv VARCHAR(20) NOT NULL, ma_mon VARCHAR(20) NOT NULL, ma_lop VARCHAR(20) NOT NULL, hoc_ky INTEGER DEFAULT 1, nam_hoc VARCHAR(20) DEFAULT '2025-2026', tong_so_buoi INTEGER DEFAULT 15, so_buoi_co_mat INTEGER DEFAULT 15, so_buoi_muon INTEGER DEFAULT 0, so_buoi_vang_phep INTEGER DEFAULT 0, so_buoi_vang_khong_phep INTEGER DEFAULT 0, tong_buoi_vang_quy_doi REAL DEFAULT 0.0, ty_le_vang REAL DEFAULT 0.0, diem_chuyen_can REAL DEFAULT 10.0, trang_thai_du_thi VARCHAR(30) DEFAULT 'DU_DIEU_KIEN', ly_do_cam_thi TEXT, ghi_chu TEXT);")
    sql.append("CREATE TABLE nhat_ky_he_thong (id INTEGER PRIMARY KEY AUTOINCREMENT, ten_dang_nhap VARCHAR(50) NOT NULL, ho_ten TEXT NOT NULL, vai_tro VARCHAR(30) NOT NULL, loai_hanh_dong VARCHAR(50) NOT NULL, mo_ta_chi_tiet TEXT NOT NULL, dia_chi_ip VARCHAR(50) DEFAULT '127.0.0.1', thiet_bi TEXT DEFAULT 'Java Swing Desktop', thoi_gian DATETIME DEFAULT CURRENT_TIMESTAMP, trang_thai_phien VARCHAR(30) DEFAULT 'ONLINE');")
    sql.append("CREATE TABLE phien_dang_nhap (id INTEGER PRIMARY KEY AUTOINCREMENT, ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL, ho_ten TEXT NOT NULL, vai_tro VARCHAR(30) NOT NULL, thoi_gian_dang_nhap DATETIME DEFAULT CURRENT_TIMESTAMP, thoi_gian_dang_xuat DATETIME, trang_thai VARCHAR(30) DEFAULT 'ONLINE', dia_chi_ip VARCHAR(50) DEFAULT '127.0.0.1', thiet_bi TEXT DEFAULT 'Java Swing Desktop');\n")

    # INSERTS
    for r in vai_tro_list:
        sql.append(f"INSERT INTO vai_tro VALUES ('{r[0]}', '{r[1]}', '{r[2]}');")
    for k in khoa_list:
        c = k["cvht"]
        sql.append(f"INSERT INTO co_van_hoc_tap VALUES ('{c[0]}', '{c[1]}', '{c[2]}', '{c[3]}', '{k['ten_khoa']}');")
    for m in mon_hoc_list:
        sql.append(f"INSERT INTO mon_hoc VALUES ('{m[0]}', '{m[1]}', {m[2]}, {m[3]}, {m[4]}, '{m[5]}');")
    for l in lop_hoc_list:
        sql.append(f"INSERT INTO lop_hoc VALUES ('{l[0]}', '{l[1]}', '{l[2]}', {l[3]}, '{l[4]}');")
    for t in all_tk:
        ref_val = f"'{t[5]}'" if t[5] else "NULL"
        sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('{t[0]}', '{t[1]}', '{t[2]}', '{t[3]}', '{t[4]}', {ref_val});")
    for sv in all_sv:
        sql.append(f"INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai, tong_tin_chi_tich_luy, nam_thu) VALUES ('{sv[0]}', '{sv[1]}', '{sv[2]}', '{sv[3]}', '{sv[4]}', '{sv[5]}', '{sv[6]}', '{sv[7]}', {sv[8]}, {sv[9]});")
    for kq in all_kq:
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no, tong_tin_chi_tich_luy, nam_thu) VALUES ('{kq[0]}', {kq[1]}, '{kq[2]}', {kq[3]}, {kq[4]}, {kq[5]}, {kq[6]}, {kq[7]});")
    for cb in all_cb:
        ly_do_esc = cb[7].replace("'", "''")
        sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('{cb[1]}', '{cb[2]}', {cb[3]}, '{cb[4]}', '{cb[5]}', {cb[6]}, '{ly_do_esc}', '{cb[8]}', '{cb[9]}');")
    for nk in all_nk:
        nd_esc = nk[6].replace("'", "''")
        nn_esc = nk[7].replace("'", "''")
        gp_esc = nk[8].replace("'", "''")
        ck_esc = nk[9].replace("'", "''")
        sql.append(f"INSERT INTO nhat_ky_tu_van (ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES ('{nk[1]}', '{nk[2]}', {nk[3]}, '{nk[4]}', '{nk[5]}', '{nd_esc}', '{nn_esc}', '{gp_esc}', '{ck_esc}');")
    for lg in all_lich:
        t_esc = lg[5].replace("'", "''")
        d_esc = lg[9].replace("'", "''")
        g_esc = lg[13].replace("'", "''") if lg[13] else ""
        sql.append(f"INSERT INTO lich_giang_day (ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES ('{lg[1]}', '{lg[2]}', '{lg[3]}', '{lg[4]}', '{t_esc}', '{lg[6]}', '{lg[7]}', '{lg[8]}', '{d_esc}', '{lg[10]}', '{lg[11]}', '{lg[12]}', '{g_esc}');")
    for dd in all_diemdanh:
        g_esc = dd[4].replace("'", "''") if dd[4] else ""
        sql.append(f"INSERT INTO diem_danh (id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES ({dd[0]}, '{dd[1]}', '{dd[2]}', '{dd[3]}', '{g_esc}');")
    for cc in all_chuyencan:
        ly_do_esc = cc[14].replace("'", "''") if cc[14] else ""
        g_esc = cc[15].replace("'", "''") if cc[15] else ""
        sql.append(f"INSERT INTO chuyen_can_mon_hoc (ma_sv, ma_mon, ma_lop, hoc_ky, nam_hoc, tong_so_buoi, so_buoi_co_mat, so_buoi_muon, so_buoi_vang_phep, so_buoi_vang_khong_phep, tong_buoi_vang_quy_doi, ty_le_vang, diem_chuyen_can, trang_thai_du_thi, ly_do_cam_thi, ghi_chu) VALUES ('{cc[0]}', '{cc[1]}', '{cc[2]}', {cc[3]}, '{cc[4]}', {cc[5]}, {cc[6]}, {cc[7]}, {cc[8]}, {cc[9]}, {cc[10]}, {cc[11]}, {cc[12]}, '{cc[13]}', '{ly_do_esc}', '{g_esc}');")
    for tb in all_tb:
        t_esc = tb[1].replace("'", "''")
        c_esc = tb[2].replace("'", "''")
        s_esc = tb[7].replace("'", "''") if tb[7] else ""
        ma_sv_val = f"'{tb[5]}'" if tb[5] else "NULL"
        sql.append(f"INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('{tb[0]}', '{t_esc}', '{c_esc}', '{tb[3]}', '{tb[4]}', {ma_sv_val}, '{tb[6]}', '{s_esc}', {tb[8]}, '{tb[9]}');")
    for log in all_audit_logs:
        d_esc = log[4].replace("'", "''")
        sql.append(f"INSERT INTO nhat_ky_he_thong (ten_dang_nhap, ho_ten, vai_tro, loai_hanh_dong, mo_ta_chi_tiet, dia_chi_ip, thiet_bi, thoi_gian, trang_thai_phien) VALUES ('{log[0]}', '{log[1]}', '{log[2]}', '{log[3]}', '{d_esc}', '{log[5]}', '{log[6]}', '{log[7]}', '{log[8]}');")
    for ss in all_user_sessions:
        out_time_val = f"'{ss[4]}'" if ss[4] else "NULL"
        sql.append(f"INSERT INTO phien_dang_nhap (ten_dang_nhap, ho_ten, vai_tro, thoi_gian_dang_nhap, thoi_gian_dang_xuat, trang_thai, dia_chi_ip, thiet_bi) VALUES ('{ss[0]}', '{ss[1]}', '{ss[2]}', '{ss[3]}', {out_time_val}, '{ss[5]}', '{ss[6]}', '{ss[7]}');")

    return "\n".join(sql)

def generate_sqlserver_sql():
    sql = []
    sql.append("-- ============================================================================")
    sql.append("-- CSDL SQL SERVER QUẢN LÝ CỐ VẤN HỌC TẬP VÀ CẢNH BÁO HỌC VỤ (EAUT 5 NĂM ĐỒ SỘ)")
    sql.append("-- ============================================================================\n")
    sql.append("IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ql_canhbao_hocvu')")
    sql.append("BEGIN")
    sql.append("    CREATE DATABASE ql_canhbao_hocvu COLLATE Vietnamese_CI_AS;")
    sql.append("END")
    sql.append("GO\n")
    sql.append("USE ql_canhbao_hocvu;")
    sql.append("GO\n")

    sql.append("IF OBJECT_ID('dbo.phien_dang_nhap', 'U') IS NOT NULL DROP TABLE dbo.phien_dang_nhap;")
    sql.append("IF OBJECT_ID('dbo.nhat_ky_he_thong', 'U') IS NOT NULL DROP TABLE dbo.nhat_ky_he_thong;")
    sql.append("IF OBJECT_ID('dbo.diem_danh', 'U') IS NOT NULL DROP TABLE dbo.diem_danh;")
    sql.append("IF OBJECT_ID('dbo.chuyen_can_mon_hoc', 'U') IS NOT NULL DROP TABLE dbo.chuyen_can_mon_hoc;")
    sql.append("IF OBJECT_ID('dbo.lich_giang_day', 'U') IS NOT NULL DROP TABLE dbo.lich_giang_day;")
    sql.append("IF OBJECT_ID('dbo.thong_bao', 'U') IS NOT NULL DROP TABLE dbo.thong_bao;")
    sql.append("IF OBJECT_ID('dbo.nhat_ky_tu_van', 'U') IS NOT NULL DROP TABLE dbo.nhat_ky_tu_van;")
    sql.append("IF OBJECT_ID('dbo.canh_bao_hoc_vu', 'U') IS NOT NULL DROP TABLE dbo.canh_bao_hoc_vu;")
    sql.append("IF OBJECT_ID('dbo.ket_qua_hoc_tap', 'U') IS NOT NULL DROP TABLE dbo.ket_qua_hoc_tap;")
    sql.append("IF OBJECT_ID('dbo.sinh_vien', 'U') IS NOT NULL DROP TABLE dbo.sinh_vien;")
    sql.append("IF OBJECT_ID('dbo.lop_hoc', 'U') IS NOT NULL DROP TABLE dbo.lop_hoc;")
    sql.append("IF OBJECT_ID('dbo.mon_hoc', 'U') IS NOT NULL DROP TABLE dbo.mon_hoc;")
    sql.append("IF OBJECT_ID('dbo.co_van_hoc_tap', 'U') IS NOT NULL DROP TABLE dbo.co_van_hoc_tap;")
    sql.append("IF OBJECT_ID('dbo.tai_khoan', 'U') IS NOT NULL DROP TABLE dbo.tai_khoan;")
    sql.append("IF OBJECT_ID('dbo.vai_tro', 'U') IS NOT NULL DROP TABLE dbo.vai_tro;")
    sql.append("GO\n")

    sql.append("CREATE TABLE vai_tro (ma_vai_tro VARCHAR(20) PRIMARY KEY, ten_vai_tro NVARCHAR(100) NOT NULL, mo_ta NVARCHAR(255));")
    sql.append("GO\n")
    sql.append("CREATE TABLE tai_khoan (id INT IDENTITY(1,1) PRIMARY KEY, ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL, mat_khau VARCHAR(255) NOT NULL, ho_ten NVARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL, vai_tro VARCHAR(20) NOT NULL, ma_ref VARCHAR(20), ngay_tao DATETIME DEFAULT GETDATE(), CONSTRAINT fk_tk_vaitro FOREIGN KEY (vai_tro) REFERENCES vai_tro(ma_vai_tro));")
    sql.append("GO\n")
    sql.append("CREATE TABLE co_van_hoc_tap (ma_cvht VARCHAR(20) PRIMARY KEY, ho_ten NVARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL, so_dien_thoai VARCHAR(20), khoa NVARCHAR(100) NOT NULL);")
    sql.append("GO\n")
    sql.append("CREATE TABLE mon_hoc (ma_mon VARCHAR(20) PRIMARY KEY, ten_mon NVARCHAR(150) NOT NULL, so_tin_chi INT NOT NULL, so_tiet INT NOT NULL, so_buoi_toi_da_vang INT NOT NULL, khoa NVARCHAR(100) NOT NULL);")
    sql.append("GO\n")
    sql.append("CREATE TABLE lop_hoc (ma_lop VARCHAR(20) PRIMARY KEY, ten_lop NVARCHAR(100) NOT NULL, khoa NVARCHAR(100) NOT NULL, khoa_hoc INT NOT NULL, ma_cvht VARCHAR(20), CONSTRAINT fk_lop_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht) ON DELETE SET NULL);")
    sql.append("GO\n")
    sql.append("CREATE TABLE sinh_vien (ma_sv VARCHAR(20) PRIMARY KEY, ho_ten NVARCHAR(100) NOT NULL, ngay_sinh DATE NOT NULL, gioi_tinh NVARCHAR(10) NOT NULL, email VARCHAR(100) NOT NULL, so_dien_thoai VARCHAR(20), ma_lop VARCHAR(20) NOT NULL, trang_thai VARCHAR(30) DEFAULT 'DANG_HOC', tong_tin_chi_tich_luy INT DEFAULT 0, nam_thu INT DEFAULT 1, CONSTRAINT fk_sinhvien_lop FOREIGN KEY (ma_lop) REFERENCES lop_hoc(ma_lop) ON DELETE CASCADE);")
    sql.append("GO\n")
    sql.append("CREATE TABLE ket_qua_hoc_tap (id INT IDENTITY(1,1) PRIMARY KEY, ma_sv VARCHAR(20) NOT NULL, hoc_ky INT NOT NULL, nam_hoc VARCHAR(20) NOT NULL, gpa_hoc_ky FLOAT NOT NULL, gpa_tich_luy FLOAT NOT NULL, so_tin_chi_no INT DEFAULT 0, tong_tin_chi_tich_luy INT DEFAULT 0, nam_thu INT DEFAULT 1, CONSTRAINT fk_kq_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE);")
    sql.append("GO\n")
    sql.append("CREATE TABLE canh_bao_hoc_vu (id INT IDENTITY(1,1) PRIMARY KEY, ma_canh_bao VARCHAR(50) UNIQUE NOT NULL, ma_sv VARCHAR(20) NOT NULL, hoc_ky INT NOT NULL, nam_hoc VARCHAR(20) NOT NULL, muc_canh_bao VARCHAR(30) NOT NULL, gpa_xet_duyet FLOAT NOT NULL, ly_do NVARCHAR(500), ngay_quyet_dinh DATE NOT NULL, trang_thai_tu_van VARCHAR(30) DEFAULT 'CHUA_TU_VAN', CONSTRAINT fk_canhbao_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE);")
    sql.append("GO\n")
    sql.append("CREATE TABLE nhat_ky_tu_van (id INT IDENTITY(1,1) PRIMARY KEY, ma_sv VARCHAR(20) NOT NULL, ma_cvht VARCHAR(20) NOT NULL, id_canh_bao INT, ngay_tu_van DATE NOT NULL, hinh_thuc NVARCHAR(100) NOT NULL, noi_dung NVARCHAR(MAX) NOT NULL, nguyen_nhan NVARCHAR(MAX), giai_phap NVARCHAR(MAX), cam_ket_sinh_vien NVARCHAR(MAX), CONSTRAINT fk_nhatky_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE, CONSTRAINT fk_nhatky_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht));")
    sql.append("GO\n")
    sql.append("CREATE TABLE thong_bao (id INT IDENTITY(1,1) PRIMARY KEY, ma_thong_bao VARCHAR(50) UNIQUE NOT NULL, tieu_de NVARCHAR(255) NOT NULL, noi_dung NVARCHAR(MAX) NOT NULL, nhom_rui_ro VARCHAR(30) DEFAULT 'ALL', ma_lop VARCHAR(20) DEFAULT 'ALL', ma_sv VARCHAR(20), ngay_gui DATETIME DEFAULT GETDATE(), nguoi_gui NVARCHAR(100), so_luong_nhan INT DEFAULT 0, trang_thai VARCHAR(30) DEFAULT 'DA_GUI');")
    sql.append("GO\n")
    sql.append("CREATE TABLE lich_giang_day (id INT IDENTITY(1,1) PRIMARY KEY, ma_cvht VARCHAR(20) NOT NULL, ten_cvht NVARCHAR(100) NOT NULL, ma_lop VARCHAR(20) NOT NULL, ten_lop NVARCHAR(100) NOT NULL, tieu_de NVARCHAR(200) NOT NULL, ngay DATE NOT NULL, gio_bat_dau VARCHAR(20) NOT NULL, gio_ket_thuc VARCHAR(20) NOT NULL, dia_diem NVARCHAR(100) NOT NULL, hinh_thuc VARCHAR(50) DEFAULT 'TRUC_TIEP', loai_buoi VARCHAR(50) DEFAULT 'GIANG_DAY', trang_thai VARCHAR(30) DEFAULT 'SCHEDULED', ghi_chu NVARCHAR(MAX), CONSTRAINT fk_lich_cvht FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht), CONSTRAINT fk_lich_lop FOREIGN KEY (ma_lop) REFERENCES lop_hoc(ma_lop));")
    sql.append("GO\n")
    sql.append("CREATE TABLE diem_danh (id INT IDENTITY(1,1) PRIMARY KEY, id_lich INT NOT NULL, ma_sv VARCHAR(20) NOT NULL, ngay_diem_danh DATE NOT NULL, trang_thai VARCHAR(30) DEFAULT 'CO_MAT', ghi_chu NVARCHAR(255), CONSTRAINT fk_diemdanh_lich FOREIGN KEY (id_lich) REFERENCES lich_giang_day(id) ON DELETE CASCADE, CONSTRAINT fk_diemdanh_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv));")
    sql.append("GO\n")
    sql.append("CREATE TABLE chuyen_can_mon_hoc (id INT IDENTITY(1,1) PRIMARY KEY, ma_sv VARCHAR(20) NOT NULL, ma_mon VARCHAR(20) NOT NULL, ma_lop VARCHAR(20) NOT NULL, hoc_ky INT DEFAULT 1, nam_hoc VARCHAR(20) DEFAULT '2025-2026', tong_so_buoi INT DEFAULT 15, so_buoi_co_mat INT DEFAULT 15, so_buoi_muon INT DEFAULT 0, so_buoi_vang_phep INT DEFAULT 0, so_buoi_vang_khong_phep INT DEFAULT 0, tong_buoi_vang_quy_doi FLOAT DEFAULT 0.0, ty_le_vang FLOAT DEFAULT 0.0, diem_chuyen_can FLOAT DEFAULT 10.0, trang_thai_du_thi VARCHAR(30) DEFAULT 'DU_DIEU_KIEN', ly_do_cam_thi NVARCHAR(500), ghi_chu NVARCHAR(255), CONSTRAINT fk_cc_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE, CONSTRAINT fk_cc_mon FOREIGN KEY (ma_mon) REFERENCES mon_hoc(ma_mon));")
    sql.append("GO\n")
    sql.append("CREATE TABLE nhat_ky_he_thong (id INT IDENTITY(1,1) PRIMARY KEY, ten_dang_nhap VARCHAR(50) NOT NULL, ho_ten NVARCHAR(100) NOT NULL, vai_tro VARCHAR(30) NOT NULL, loai_hanh_dong VARCHAR(50) NOT NULL, mo_ta_chi_tiet NVARCHAR(MAX) NOT NULL, dia_chi_ip VARCHAR(50) DEFAULT '127.0.0.1', thiet_bi NVARCHAR(100) DEFAULT 'Java Swing Desktop', thoi_gian DATETIME DEFAULT GETDATE(), trang_thai_phien VARCHAR(30) DEFAULT 'ONLINE');")
    sql.append("GO\n")
    sql.append("CREATE TABLE phien_dang_nhap (id INT IDENTITY(1,1) PRIMARY KEY, ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL, ho_ten NVARCHAR(100) NOT NULL, vai_tro VARCHAR(30) NOT NULL, thoi_gian_dang_nhap DATETIME DEFAULT GETDATE(), thoi_gian_dang_xuat DATETIME, trang_thai VARCHAR(30) DEFAULT 'ONLINE', dia_chi_ip VARCHAR(50) DEFAULT '127.0.0.1', thiet_bi NVARCHAR(100) DEFAULT 'Java Swing Desktop');")
    sql.append("GO\n")

    # INSERTS
    for r in vai_tro_list:
        sql.append(f"INSERT INTO vai_tro VALUES ('{r[0]}', N'{r[1]}', N'{r[2]}');")
    sql.append("GO\n")

    for k in khoa_list:
        c = k["cvht"]
        sql.append(f"INSERT INTO co_van_hoc_tap VALUES ('{c[0]}', N'{c[1]}', '{c[2]}', '{c[3]}', N'{k['ten_khoa']}');")
    sql.append("GO\n")

    for m in mon_hoc_list:
        sql.append(f"INSERT INTO mon_hoc VALUES ('{m[0]}', N'{m[1]}', {m[2]}, {m[3]}, {m[4]}, N'{m[5]}');")
    sql.append("GO\n")

    for l in lop_hoc_list:
        sql.append(f"INSERT INTO lop_hoc VALUES ('{l[0]}', N'{l[1]}', N'{l[2]}', {l[3]}, '{l[4]}');")
    sql.append("GO\n")

    for t in all_tk:
        ref_val = f"'{t[5]}'" if t[5] else "NULL"
        sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('{t[0]}', '{t[1]}', N'{t[2]}', '{t[3]}', '{t[4]}', {ref_val});")
    sql.append("GO\n")

    for sv in all_sv:
        sql.append(f"INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai, tong_tin_chi_tich_luy, nam_thu) VALUES ('{sv[0]}', N'{sv[1]}', '{sv[2]}', N'{sv[3]}', '{sv[4]}', '{sv[5]}', '{sv[6]}', '{sv[7]}', {sv[8]}, {sv[9]});")
    sql.append("GO\n")

    for kq in all_kq:
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no, tong_tin_chi_tich_luy, nam_thu) VALUES ('{kq[0]}', {kq[1]}, '{kq[2]}', {kq[3]}, {kq[4]}, {kq[5]}, {kq[6]}, {kq[7]});")
    sql.append("GO\n")

    for cb in all_cb:
        ly_do_esc = cb[7].replace("'", "''")
        sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('{cb[1]}', '{cb[2]}', {cb[3]}, '{cb[4]}', '{cb[5]}', {cb[6]}, N'{ly_do_esc}', '{cb[8]}', '{cb[9]}');")
    sql.append("GO\n")

    for nk in all_nk:
        nd_esc = nk[6].replace("'", "''")
        nn_esc = nk[7].replace("'", "''")
        gp_esc = nk[8].replace("'", "''")
        ck_esc = nk[9].replace("'", "''")
        sql.append(f"INSERT INTO nhat_ky_tu_van (ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES ('{nk[1]}', '{nk[2]}', {nk[3]}, '{nk[4]}', N'{nk[5]}', N'{nd_esc}', N'{nn_esc}', N'{gp_esc}', N'{ck_esc}');")
    sql.append("GO\n")

    for lg in all_lich:
        t_esc = lg[5].replace("'", "''")
        d_esc = lg[9].replace("'", "''")
        g_esc = lg[13].replace("'", "''") if lg[13] else ""
        sql.append(f"INSERT INTO lich_giang_day (ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES ('{lg[1]}', N'{lg[2]}', '{lg[3]}', N'{lg[4]}', N'{t_esc}', '{lg[6]}', '{lg[7]}', '{lg[8]}', N'{d_esc}', '{lg[10]}', '{lg[11]}', '{lg[12]}', N'{g_esc}');")
    sql.append("GO\n")

    for dd in all_diemdanh:
        g_esc = dd[4].replace("'", "''") if dd[4] else ""
        sql.append(f"INSERT INTO diem_danh (id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES ({dd[0]}, '{dd[1]}', '{dd[2]}', '{dd[3]}', N'{g_esc}');")
    sql.append("GO\n")

    for cc in all_chuyencan:
        ly_do_esc = cc[14].replace("'", "''") if cc[14] else ""
        g_esc = cc[15].replace("'", "''") if cc[15] else ""
        sql.append(f"INSERT INTO chuyen_can_mon_hoc (ma_sv, ma_mon, ma_lop, hoc_ky, nam_hoc, tong_so_buoi, so_buoi_co_mat, so_buoi_muon, so_buoi_vang_phep, so_buoi_vang_khong_phep, tong_buoi_vang_quy_doi, ty_le_vang, diem_chuyen_can, trang_thai_du_thi, ly_do_cam_thi, ghi_chu) VALUES ('{cc[0]}', '{cc[1]}', '{cc[2]}', {cc[3]}, '{cc[4]}', {cc[5]}, {cc[6]}, {cc[7]}, {cc[8]}, {cc[9]}, {cc[10]}, {cc[11]}, {cc[12]}, '{cc[13]}', N'{ly_do_esc}', N'{g_esc}');")
    sql.append("GO\n")

    for tb in all_tb:
        t_esc = tb[1].replace("'", "''")
        c_esc = tb[2].replace("'", "''")
        s_esc = tb[7].replace("'", "''") if tb[7] else ""
        ma_sv_val = f"'{tb[5]}'" if tb[5] else "NULL"
        sql.append(f"INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('{tb[0]}', N'{t_esc}', N'{c_esc}', '{tb[3]}', '{tb[4]}', {ma_sv_val}, '{tb[6]}', N'{s_esc}', {tb[8]}, '{tb[9]}');")
    sql.append("GO\n")

    for log in all_audit_logs:
        d_esc = log[4].replace("'", "''")
        sql.append(f"INSERT INTO nhat_ky_he_thong (ten_dang_nhap, ho_ten, vai_tro, loai_hanh_dong, mo_ta_chi_tiet, dia_chi_ip, thiet_bi, thoi_gian, trang_thai_phien) VALUES ('{log[0]}', N'{log[1]}', '{log[2]}', '{log[3]}', N'{d_esc}', '{log[5]}', N'{log[6]}', '{log[7]}', '{log[8]}');")
    sql.append("GO\n")

    for ss in all_user_sessions:
        out_time_val = f"'{ss[4]}'" if ss[4] else "NULL"
        sql.append(f"INSERT INTO phien_dang_nhap (ten_dang_nhap, ho_ten, vai_tro, thoi_gian_dang_nhap, thoi_gian_dang_xuat, trang_thai, dia_chi_ip, thiet_bi) VALUES ('{ss[0]}', N'{ss[1]}', '{ss[2]}', '{ss[3]}', {out_time_val}, '{ss[5]}', '{ss[6]}', N'{ss[7]}');")
    sql.append("GO\n")

    return "\n".join(sql)

sqlite_content = generate_sqlite_sql()
sqlserver_content = generate_sqlserver_sql()

targets = [
    "sqlite_schema.sql",
    "src/main/resources/sqlite_schema.sql",
    "database.sql",
    "src/main/resources/database.sql"
]

for t in targets:
    with open(t, "w", encoding="utf-8") as f:
        f.write(sqlite_content)

sqlserver_targets = [
    "sqlserver_schema.sql",
    "src/main/resources/sqlserver_schema.sql"
]

for t in sqlserver_targets:
    with open(t, "w", encoding="utf-8") as f:
        f.write(sqlserver_content)

print("Đã ghi toàn bộ file SQL schema (SQLite, MySQL, SQL Server) thành công!")

os.makedirs("data", exist_ok=True)
db_path = "data/ql_canhbao_hocvu.db"
if os.path.exists(db_path):
    try:
        os.remove(db_path)
    except Exception as e:
        print(f"Warning: {e}")

conn = sqlite3.connect(db_path)
conn.executescript(sqlite_content)
conn.commit()
conn.close()

print(f"✅ ĐÃ KHỞI TẠO THÀNH CÔNG DATABASE 5 NĂM: {db_path}")
