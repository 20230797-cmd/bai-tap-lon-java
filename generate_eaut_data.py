import random

khoas = [
    {
        "ma_khoa": "CNTT",
        "ten_khoa": "Công nghệ thông tin",
        "cvht": ("CV001", "TS. Đinh Văn Phong", "phong.dv@eaut.edu.vn", "0912345678"),
        "lops": [
            ("DCCTPM14A", "DCCTPM14A - Công nghệ phần mềm K14"),
            ("DCCNTT14B", "DCCNTT14B - Công nghệ thông tin K14B")
        ],
        "prefix_sv": "202300"
    },
    {
        "ma_khoa": "OTO",
        "ten_khoa": "Công nghệ kỹ thuật Ô tô",
        "cvht": ("CV002", "PGS.TS. Nguyễn Thanh Hải", "hai.nt@eaut.edu.vn", "0987654321"),
        "lops": [
            ("DCOTO14A", "DCOTO14A - Công nghệ kỹ thuật Ô tô 14A"),
            ("DCOTO14B", "DCOTO14B - Công nghệ kỹ thuật Ô tô 14B")
        ],
        "prefix_sv": "202301"
    },
    {
        "ma_khoa": "QTKD",
        "ten_khoa": "Quản trị kinh doanh",
        "cvht": ("CV003", "ThS. Hoàng Thị Mai", "mai.ht@eaut.edu.vn", "0934567890"),
        "lops": [
            ("DCQTKD14A", "DCQTKD14A - Quản trị kinh doanh 14A"),
            ("DCQTKD14B", "DCQTKD14B - Quản trị kinh doanh 14B")
        ],
        "prefix_sv": "202302"
    },
    {
        "ma_khoa": "DDT",
        "ten_khoa": "Công nghệ kỹ thuật Điện - Điện tử",
        "cvht": ("CV004", "TS. Vũ Trường Sơn", "son.vt@eaut.edu.vn", "0945678901"),
        "lops": [
            ("DCDDT14A", "DCDDT14A - Kỹ thuật Điện - Điện tử 14A"),
            ("DCDDT14B", "DCDDT14B - Tự động hóa K14B")
        ],
        "prefix_sv": "202303"
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

all_tk.append(("admin", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", "Quản trị viên Hệ thống EAUT", "admin@eaut.edu.vn", "ADMIN", None))
all_tk.append(("quanly", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", "Trưởng phòng Đào tạo EAUT", "daotao@eaut.edu.vn", "QUAN_LY", None))

cb_counter = 1
nk_counter = 1
kq_counter = 1

for k in khoas:
    cv_ma, cv_ten, cv_email, cv_sdt = k["cvht"]
    email_prefix = cv_email.split('@')[0].replace('.', '')
    username_named = f"cv_{email_prefix}"
    all_tk.append((username_named, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", cv_ten, cv_email, "CO_VAN", cv_ma))
    all_tk.append((cv_ma.lower(), "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", cv_ten, cv_email, "CO_VAN", cv_ma))

    prefix = k["prefix_sv"]
    for i in range(1, 31):
        ma_sv = f"{prefix}{i:02d}"
        lop_ma = k["lops"][0][0] if i <= 15 else k["lops"][1][0]
        
        is_nu = (i % 3 == 0)
        gt = "Nữ" if is_nu else "Nam"
        ho = ho_list[(i * 7) % len(ho_list)]
        dem = dem_nu[(i * 3) % len(dem_nu)] if is_nu else dem_nam[(i * 5) % len(dem_nam)]
        ten = ten_nu[(i * 2) % len(ten_nu)] if is_nu else ten_nam[(i * 3) % len(ten_nam)]
        ho_ten = f"{ho} {dem} {ten}"
        email = f"sv{ma_sv}@eaut.edu.vn"
        sdt = f"091100{ma_sv[-4:]}"
        ngay_sinh = f"2005-{((i*3)%12)+1:02d}-{((i*5)%28)+1:02d}"

        if i <= 6:
            tt = "DANG_HOC"
            gpa1 = round(3.2 + (i % 5) * 0.12, 2)
            gpa2 = round(3.4 + (i % 4) * 0.11, 2)
            no_tc = 0
        elif i <= 21:
            tt = "DANG_HOC"
            gpa1 = round(2.3 + (i % 7) * 0.12, 2)
            gpa2 = round(2.4 + (i % 6) * 0.11, 2)
            no_tc = 0 if i <= 18 else 2
        elif i <= 25:
            tt = "CANH_BAO_1"
            gpa1 = round(2.05 - (i - 22) * 0.05, 2)
            gpa2 = round(1.75 - (i - 22) * 0.06, 2)
            no_tc = 4 + (i - 22) * 2
        elif i <= 28:
            tt = "CANH_BAO_2"
            gpa1 = round(1.70 - (i - 26) * 0.08, 2)
            gpa2 = round(1.35 - (i - 26) * 0.07, 2)
            no_tc = 8 + (i - 26) * 2
        else:
            tt = "BUOC_THOI_HOC"
            gpa1 = round(1.15 - (i - 29) * 0.15, 2)
            gpa2 = round(0.78 - (i - 29) * 0.10, 2)
            no_tc = 14 + (i - 29) * 3

        gpa_tl = round((gpa1 + gpa2) / 2.0, 2)

        all_sv.append((ma_sv, ho_ten, ngay_sinh, gt, email, sdt, lop_ma, tt))
        all_kq.append((ma_sv, 1, "2023-2024", gpa1, gpa1, 0))
        all_kq.append((ma_sv, 2, "2023-2024", gpa2, gpa_tl, no_tc))
        all_tk.append((ma_sv, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", ho_ten, email, "SINH_VIEN", ma_sv))

        if tt in ["CANH_BAO_1", "CANH_BAO_2", "BUOC_THOI_HOC"]:
            ma_cb = f"CB-{k['ma_khoa']}-{ma_sv}-2324-HK2"
            ly_do = f"GPA HK2 ({gpa2}) < 2.0 và Nợ {no_tc} tín chỉ chuyên ngành {k['ten_khoa']}"
            ngay_qd = "2024-07-01"
            trang_thai_tv = "DA_TU_VAN" if i in [22, 24, 26, 27, 28, 29, 30] else "CHUA_TU_VAN"
            all_cb.append((cb_counter, ma_cb, ma_sv, 2, "2023-2024", tt, gpa2, ly_do, ngay_qd, trang_thai_tv))

            if trang_thai_tv == "DA_TU_VAN":
                ht = "Trực tiếp tại VP Khoa" if i % 2 == 0 else "Trực tuyến (MS Teams)"
                nd = f"Tư vấn cải thiện điểm học kỳ và lộ trình đăng ký trả nợ {no_tc} tín chỉ ngành {k['ten_khoa']}"
                nn = "Thiếu tập trung, nghỉ học nhiều buổi thực hành, đi làm thêm quá giờ"
                gp = "Lập thời gian biểu học tập 2h/ngày, đăng ký lớp học lại trong kỳ hè, có bạn học kèm"
                ck = "Cam kết đi học chuyên cần và đạt GPA >= 2.0 trong học kỳ tiếp theo"
                all_nk.append((nk_counter, ma_sv, cv_ma, cb_counter, "2024-07-15", ht, nd, nn, gp, ck))
                nk_counter += 1

            cb_counter += 1

# SAMPLE NOTIFICATIONS & CHAT MESSAGES
thong_bao_samples = [
    ("TB-EAUT-01", "[EAUT] THÔNG BÁO XÉT HỌC BỔNG KHUYẾN KHÍCH HỌC TẬP K14", "Chúc mừng các sinh viên đạt GPA >= 3.2 trong học kỳ 2 năm học 2023-2024. Đề nghị sinh viên nộp hồ sơ xét học bổng tại VP Đoàn trường trước ngày 20/09/2026.", "TIER_1", "ALL", None, "Phòng Đào Tạo EAUT", 24, "DA_GUI"),
    ("TB-EAUT-02", "[EAUT] KẾ HOẠCH ĐĂNG KÝ HỌC PHẦN HỌC KỲ 1 NĂM HỌC 2024-2025", "Hệ thống cổng đào tạo eaut.edu.vn mở cổng đăng ký tín chỉ từ 8h00 ngày 25/08/2026. Sinh viên chú ý các môn tiên quyết.", "TIER_2", "ALL", None, "Phòng Đào Tạo EAUT", 60, "DA_GUI"),
    ("TB-EAUT-03", "[EAUT] YÊU CẦU TƯ VẤN HỌC VỤ BẮT BUỘC ĐỐI VỚI SINH VIÊN BỊ CẢNH BÁO", "Các sinh viên có tên trong danh sách cảnh báo Mức 1, Mức 2 và Buộc thôi học phải liên hệ ngay Cố vấn học tập trước ngày 15/08/2026.", "TIER_3", "ALL", None, "Ban CVHT EAUT", 36, "DA_GUI"),
    ("TB-EAUT-04", "[KHOA CNTT] HỘI THẢO CÔNG NGHỆ & ĐỊNH HƯỚNG NGHỀ NGHIỆP AI & CLOUD", "Khoa CNTT phối hợp cùng doanh nghiệp đối tác tổ chức workshop chia sẻ công nghệ vào 9h00 sáng thứ Bảy tại Hội trường A.", "ALL", "DCCTPM14A", None, "TS. Đinh Văn Phong", 15, "DA_GUI"),
    ("TB-EAUT-05", "[KHOA Ô TÔ] LỊCH THỰC TẬP TỐT NGHIỆP VÀ AN TOÀN XƯỞNG THỰC HÀNH", "Yêu cầu 100% sinh viên lớp DCOTO14A trang bị đồ bảo hộ lao động đầy đủ trước khi vào xưởng Ô tô.", "ALL", "DCOTO14A", None, "PGS.TS. Nguyễn Thanh Hải", 15, "DA_GUI"),
    
    # CHAT / PHẢN HỒI 2 CHIỀU GIỮA SV & CVHT
    ("TB-CHAT-01", "Chúc mừng kết quả học tập xuất sắc ngành Công nghệ thông tin", "Thầy chúc mừng em Nam đã đạt GPA 3.65 đứng đầu lớp DCCTPM14A kỳ vừa qua. Tiếp tục giữ vững phong độ nhé em!", "CA_NHAN", "DCCTPM14A", "20230001", "TS. Đinh Văn Phong", 1, "DA_GUI"),
    ("TB-CHAT-02", "📬 [SV PHẢN HỒI] Em cảm ơn thầy và muốn hỏi về học bổng", "Dạ em chào thầy Phong, em cảm ơn thầy ạ! Cho em hỏi hồ sơ xét học bổng kỳ này cần nộp bản sao bảng điểm có xác nhận không ạ?", "PHAN_HOI_SV", "ALL", "20230001", "Vũ Đình Anh (20230001)", 1, "DA_DOC"),
    ("TB-CHAT-03", "💬 [CVHT TRẢ LỜI] Hướng dẫn thủ tục học bổng", "Chào em, bản điểm thầy sẽ trực tiếp ký xác nhận và gửi VP Đoàn cho em nhé. Em chỉ cần nộp đơn xin xét theo mẫu thôi.", "CVHT_TRA_LOI", "CA_NHAN", "20230001", "TS. Đinh Văn Phong", 1, "DA_DOC"),
    
    ("TB-CHAT-04", "Lịch hẹn gặp mặt tư vấn học tập và kế hoạch học lại", "Chào em Duy, học kỳ vừa qua em bị cảnh báo Mức 1. Chiều thứ Ba tuần tới 14h00 em đến văn phòng Khoa gặp thầy để trao đổi kế hoạch học lại nhé.", "CA_NHAN", "DCCTPM14A", "20230022", "TS. Đinh Văn Phong", 1, "DA_GUI"),
    ("TB-CHAT-05", "📬 [SV PHẢN HỒI] Em xác nhận lịch hẹn tư vấn", "Dạ em chào thầy, thứ Ba tuần tới 14h00 em sẽ có mặt đúng giờ tại VP Khoa ạ. Em cảm ơn thầy đã nhắc nhở em!", "PHAN_HOI_SV", "ALL", "20230022", "Bùi Hoàng Duy (20230022)", 1, "SV_CHUA_DOC"),
    
    ("TB-CHAT-06", "Cảnh báo học vụ Mức 2 và yêu cầu cam kết tiến độ", "Chào em Nam, em đang bị cảnh báo Mức 2 và nợ 8 tín chỉ. Em cần nộp bản cam kết cải thiện GPA trước ngày 15/09.", "CA_NHAN", "DCCNTT14B", "20230026", "TS. Đinh Văn Phong", 1, "DA_GUI"),
    ("TB-CHAT-07", "📬 [SV PHẢN HỒI] Em xin tư vấn môn học lại để gỡ cảnh báo", "Thưa thầy, kỳ này em đã đăng ký học lại 2 môn Toán rời rạc và Cấu trúc dữ liệu. Thầy cho em xin lời khuyên để phân bổ thời gian hợp lý ạ.", "PHAN_HOI_SV", "ALL", "20230026", "Phan Gia Nam (20230026)", 1, "SV_CHUA_DOC"),

    ("TB-CHAT-08", "Thông báo cảnh báo nguy cơ Buộc thôi học", "Em Kiệt chú ý, tình trạng học tập của em đang ở mức báo động Buộc thôi học. Hãy liên hệ ngay với CVHT trong tuần này.", "CA_NHAN", "DCCNTT14B", "20230029", "TS. Đinh Văn Phong", 1, "DA_GUI"),
    ("TB-CHAT-09", "📬 [SV PHẢN HỒI] Đơn xin xem xét hoàn cảnh gia đình", "Dạ em chào thầy, đợt vừa rồi gia đình em có biến cố nên em phải nghỉ nhiều buổi. Em mong thầy và nhà trường tạo điều kiện cho em được tiếp tục học tập ạ.", "PHAN_HOI_SV", "ALL", "20230029", "Đỗ Đức Kiệt (20230029)", 1, "SV_CHUA_DOC"),

    ("TB-CHAT-10", "Tư vấn hướng nghiệp và chuyên ngành Ô tô", "Chào em, kỳ tới Khoa mở chuyên ngành Chẩn đoán điện Ô tô, em chú ý đăng ký môn tiên quyết nhé.", "CA_NHAN", "DCOTO14A", "20230101", "PGS.TS. Nguyễn Thanh Hải", 1, "DA_GUI"),
    ("TB-CHAT-11", "Nhắc nhở học tập và đồ án Quản trị kinh doanh", "Chào em, đồ án môn Quản trị chiến lược hạn nộp là ngày 20/09, nhóm em hoàn thiện sớm nhé.", "CA_NHAN", "DCQTKD14A", "20230201", "ThS. Hoàng Thị Mai", 1, "DA_GUI")
]

# SAMPLE SCHEDULES (8 SESSIONS)
lich_samples = [
    (1, "CV001", "TS. Đinh Văn Phong", "DCCTPM14A", "DCCTPM14A - Công nghệ phần mềm K14", "Sinh hoạt lớp định kỳ đầu học kỳ 1", "2026-08-25", "08:00", "10:00", "Phòng 501-A (Tòa nhà EAUT)", "Trực tiếp", "SINH_HOAT_LOP", "HOAN_THANH", "Triển khai kế hoạch năm học mới và rà soát kết quả học tập K14"),
    (2, "CV001", "TS. Đinh Văn Phong", "DCCTPM14A", "DCCTPM14A - Công nghệ phần mềm K14", "Tư vấn học tập sinh viên cảnh báo học vụ Khoa CNTT", "2026-09-02", "14:00", "16:00", "Văn phòng CVHT Khoa CNTT", "Trực tiếp", "TU_VAN_HOC_TAP", "SAP_DIEN_RA", "Gặp mặt tư vấn riêng các sinh viên Mức 1 và Mức 2"),
    (3, "CV001", "TS. Đinh Văn Phong", "DCCNTT14B", "DCCNTT14B - Công nghệ thông tin K14B", "Sinh hoạt lớp & phổ biến đăng ký tín chỉ kỳ mới", "2026-09-04", "08:00", "10:00", "Phòng 402-A (Tòa nhà EAUT)", "Trực tiếp", "SINH_HOAT_LOP", "SAP_DIEN_RA", "Hướng dẫn đăng ký học phần và xử lý học vụ"),
    (4, "CV002", "PGS.TS. Nguyễn Thanh Hải", "DCOTO14A", "DCOTO14A - Công nghệ kỹ thuật Ô tô 14A", "Sinh hoạt lớp và phổ biến quy chế thực tập xưởng Ô tô", "2026-09-05", "09:00", "11:00", "Xưởng thực hành Ô tô EAUT", "Trực tiếp", "SINH_HOAT_LOP", "SAP_DIEN_RA", "Phổ biến quy chế an toàn lao động và cảnh báo học vụ mới nhất"),
    (5, "CV002", "PGS.TS. Nguyễn Thanh Hải", "DCOTO14B", "DCOTO14B - Công nghệ kỹ thuật Ô tô 14B", "Tư vấn phương pháp học tập chuyên ngành Điện tử Ô tô", "2026-09-07", "14:00", "16:00", "Phòng 303-B", "Trực tuyến (MS Teams)", "TU_VAN_HOC_TAP", "SAP_DIEN_RA", "Hỗ trợ sinh viên có nguy cơ nợ môn thực hành"),
    (6, "CV003", "ThS. Hoàng Thị Mai", "DCQTKD14A", "DCQTKD14A - Quản trị kinh doanh 14A", "Tư vấn phương pháp học và hướng nghiệp Marketing", "2026-09-08", "14:00", "16:00", "Hội trường B - EAUT", "Trực tiếp", "SINH_HOAT_LOP", "SAP_DIEN_RA", "Gặp gỡ doanh nghiệp liên kết EAUT và sinh hoạt lớp"),
    (7, "CV003", "ThS. Hoàng Thị Mai", "DCQTKD14B", "DCQTKD14B - Quản trị kinh doanh 14B", "Tư vấn học tập và rà soát tín chỉ tốt nghiệp", "2026-09-10", "09:00", "11:00", "Phòng 201-B", "Trực tiếp", "SINH_HOAT_LOP", "SAP_DIEN_RA", "Rà soát điều kiện chuẩn đầu ra tiếng Anh và Tin học"),
    (8, "CV004", "TS. Vũ Trường Sơn", "DCDDT14A", "DCDDT14A - Kỹ thuật Điện - Điện tử 14A", "Sinh hoạt định kỳ và hướng dẫn nghiên cứu khoa học", "2026-09-12", "08:30", "10:30", "Phòng Lab Tự động hóa", "Trực tiếp", "SINH_HOAT_LOP", "SAP_DIEN_RA", "Thành lập các nhóm NCKH và hỗ trợ sinh viên học tập")
]

# SAMPLE ATTENDANCE (20 RECORDS)
dd_samples = [
    (1, 1, "20230001", "2026-08-25", "ON_TIME", "Có mặt đúng giờ, tích cực tham gia phát biểu"),
    (2, 1, "20230002", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (3, 1, "20230003", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (4, 1, "20230004", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (5, 1, "20230005", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (6, 1, "20230006", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (7, 1, "20230007", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (8, 1, "20230008", "2026-08-25", "LATE", "Đi muộn 10 phút"),
    (9, 1, "20230009", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (10, 1, "20230010", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (11, 1, "20230011", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (12, 1, "20230012", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (13, 1, "20230013", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (14, 1, "20230014", "2026-08-25", "LATE", "Đi muộn 15 phút do kẹt xe"),
    (15, 1, "20230015", "2026-08-25", "ON_TIME", "Có mặt đúng giờ"),
    (16, 2, "20230022", "2026-09-02", "ON_TIME", "Có mặt đúng giờ, đã trao đổi kế hoạch học lại"),
    (17, 2, "20230023", "2026-09-02", "ON_TIME", "Có mặt đúng giờ, đã ký cam kết"),
    (18, 2, "20230024", "2026-09-02", "LATE", "Đi muộn 15 phút, đã nhắc nhở"),
    (19, 2, "20230026", "2026-09-02", "ABSENT", "Vắng mặt không phép - đã gửi email cảnh báo"),
    (20, 2, "20230029", "2026-09-02", "ABSENT", "Vắng mặt - đã liên hệ phụ huynh")
]

def generate_sql_server():
    sql = []
    sql.append("-- ============================================================")
    sql.append("-- DATABASE SCHEMA & DỮ LIỆU ĐỒNG BỘ 100% - ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)")
    sql.append("-- 4 Khoa chuyên ngành | 8 Lớp học | 120 Sinh viên chuẩn hóa đầy đủ")
    sql.append("-- Mật khẩu đăng nhập mặc định: 123456")
    sql.append("-- ============================================================\n")
    sql.append("IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ql_canhbao_hocvu')")
    sql.append("BEGIN\n    CREATE DATABASE ql_canhbao_hocvu COLLATE Vietnamese_CI_AS;\nEND\nGO\n")
    sql.append("USE ql_canhbao_hocvu;\nGO\n")
    sql.append("IF OBJECT_ID('dbo.diem_danh', 'U') IS NOT NULL DROP TABLE dbo.diem_danh;")
    sql.append("IF OBJECT_ID('dbo.lich_giang_day', 'U') IS NOT NULL DROP TABLE dbo.lich_giang_day;")
    sql.append("IF OBJECT_ID('dbo.thong_bao', 'U') IS NOT NULL DROP TABLE dbo.thong_bao;")
    sql.append("IF OBJECT_ID('dbo.nhat_ky_tu_van', 'U') IS NOT NULL DROP TABLE dbo.nhat_ky_tu_van;")
    sql.append("IF OBJECT_ID('dbo.canh_bao_hoc_vu', 'U') IS NOT NULL DROP TABLE dbo.canh_bao_hoc_vu;")
    sql.append("IF OBJECT_ID('dbo.ket_qua_hoc_tap', 'U') IS NOT NULL DROP TABLE dbo.ket_qua_hoc_tap;")
    sql.append("IF OBJECT_ID('dbo.sinh_vien', 'U') IS NOT NULL DROP TABLE dbo.sinh_vien;")
    sql.append("IF OBJECT_ID('dbo.lop_hoc', 'U') IS NOT NULL DROP TABLE dbo.lop_hoc;")
    sql.append("IF OBJECT_ID('dbo.co_van_hoc_tap', 'U') IS NOT NULL DROP TABLE dbo.co_van_hoc_tap;")
    sql.append("IF OBJECT_ID('dbo.tai_khoan', 'U') IS NOT NULL DROP TABLE dbo.tai_khoan;\nGO\n")

    sql.append("""CREATE TABLE co_van_hoc_tap (
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
""")

    sql.append("-- 1. CỐ VẤN HỌC TẬP")
    for k in khoas:
        cv_ma, cv_ten, cv_email, cv_sdt = k["cvht"]
        sql.append(f"INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES ('{cv_ma}', N'{cv_ten}', '{cv_email}', '{cv_sdt}', N'{k['ten_khoa']}');")
    sql.append("GO\n")

    sql.append("-- 2. LỚP HỌC")
    for k in khoas:
        cv_ma = k["cvht"][0]
        for lop_ma, lop_ten in k["lops"]:
            sql.append(f"INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('{lop_ma}', N'{lop_ten}', N'{k['ten_khoa']}', 2023, '{cv_ma}');")
    sql.append("GO\n")

    sql.append("-- 3. SINH VIÊN (120 SINH VIÊN ĐỒNG BỘ)")
    for sv in all_sv:
        sql.append(f"INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('{sv[0]}', N'{sv[1]}', '{sv[2]}', N'{sv[3]}', '{sv[4]}', '{sv[5]}', '{sv[6]}', '{sv[7]}');")
    sql.append("GO\n")

    sql.append("-- 4. KẾT QUẢ HỌC TẬP (240 BẢN GHI ĐIỂM ĐỒNG BỘ)")
    for kq in all_kq:
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('{kq[0]}', {kq[1]}, '{kq[2]}', {kq[3]}, {kq[4]}, {kq[5]});")
    sql.append("GO\n")

    sql.append("-- 5. CẢNH BÁO HỌC VỤ")
    sql.append("SET IDENTITY_INSERT canh_bao_hoc_vu ON;")
    for cb in all_cb:
        sql.append(f"INSERT INTO canh_bao_hoc_vu (id, ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ({cb[0]}, '{cb[1]}', '{cb[2]}', {cb[3]}, '{cb[4]}', '{cb[5]}', {cb[6]}, N'{cb[7]}', '{cb[8]}', '{cb[9]}');")
    sql.append("SET IDENTITY_INSERT canh_bao_hoc_vu OFF;\nGO\n")

    sql.append("-- 6. NHẬT KÝ TƯ VẤN")
    sql.append("SET IDENTITY_INSERT nhat_ky_tu_van ON;")
    for nk in all_nk:
        sql.append(f"INSERT INTO nhat_ky_tu_van (id, ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES ({nk[0]}, '{nk[1]}', '{nk[2]}', {nk[3]}, '{nk[4]}', N'{nk[5]}', N'{nk[6]}', N'{nk[7]}', N'{nk[8]}', N'{nk[9]}');")
    sql.append("SET IDENTITY_INSERT nhat_ky_tu_van OFF;\nGO\n")

    sql.append("-- 7. TÀI KHOẢN NGƯỜI DÙNG")
    for tk in all_tk:
        ref_val = f"'{tk[5]}'" if tk[5] else "NULL"
        sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('{tk[0]}', '{tk[1]}', N'{tk[2]}', '{tk[3]}', '{tk[4]}', {ref_val});")
    sql.append("GO\n")

    sql.append("-- 8. THÔNG BÁO & TIN NHẮN")
    for tb in thong_bao_samples:
        sv_val = f"'{tb[5]}'" if tb[5] else "NULL"
        sql.append(f"INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('{tb[0]}', N'{tb[1]}', N'{tb[2]}', '{tb[3]}', '{tb[4]}', {sv_val}, GETDATE(), N'{tb[6]}', {tb[7]}, '{tb[8]}');")
    sql.append("GO\n")

    sql.append("-- 9. LỊCH GIẢNG DẠY")
    sql.append("SET IDENTITY_INSERT lich_giang_day ON;")
    for lg in lich_samples:
        sql.append(f"INSERT INTO lich_giang_day (id, ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) VALUES ({lg[0]}, '{lg[1]}', N'{lg[2]}', '{lg[3]}', N'{lg[4]}', N'{lg[5]}', '{lg[6]}', '{lg[7]}', '{lg[8]}', N'{lg[9]}', '{lg[10]}', '{lg[11]}', '{lg[12]}', N'{lg[13]}');")
    sql.append("SET IDENTITY_INSERT lich_giang_day OFF;\nGO\n")

    sql.append("-- 10. ĐIỂM DANH")
    sql.append("SET IDENTITY_INSERT diem_danh ON;")
    for dd in dd_samples:
        sql.append(f"INSERT INTO diem_danh (id, id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES ({dd[0]}, {dd[1]}, '{dd[2]}', '{dd[3]}', '{dd[4]}', N'{dd[5]}');")
    sql.append("SET IDENTITY_INSERT diem_danh OFF;\nGO\n")

    sql.append("PRINT N'Khởi tạo Cơ sở Dữ liệu Microsoft SQL Server EAUT ql_canhbao_hocvu (120 SV) thành công 100%!';")
    return "\n".join(sql)

def generate_sqlite():
    sql = []
    sql.append("-- ============================================================")
    sql.append("-- DATABASE SCHEMA & DỮ LIỆU ĐỒNG BỘ 100% - ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)")
    sql.append("-- 4 Khoa chuyên ngành | 8 Lớp học | 120 Sinh viên chuẩn hóa đầy đủ")
    sql.append("-- ============================================================\n")
    sql.append("DROP TABLE IF EXISTS `diem_danh`;")
    sql.append("DROP TABLE IF EXISTS `lich_giang_day`;")
    sql.append("DROP TABLE IF EXISTS `thong_bao`;")
    sql.append("DROP TABLE IF EXISTS `nhat_ky_tu_van`;")
    sql.append("DROP TABLE IF EXISTS `canh_bao_hoc_vu`;")
    sql.append("DROP TABLE IF EXISTS `ket_qua_hoc_tap`;")
    sql.append("DROP TABLE IF EXISTS `sinh_vien`;")
    sql.append("DROP TABLE IF EXISTS `lop_hoc`;")
    sql.append("DROP TABLE IF EXISTS `co_van_hoc_tap`;")
    sql.append("DROP TABLE IF EXISTS `tai_khoan`;\n")

    sql.append("""CREATE TABLE `co_van_hoc_tap` (
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
""")

    for k in khoas:
        cv_ma, cv_ten, cv_email, cv_sdt = k["cvht"]
        sql.append(f"INSERT OR REPLACE INTO `co_van_hoc_tap` (`ma_cvht`, `ho_ten`, `email`, `so_dien_thoai`, `khoa`) VALUES ('{cv_ma}', '{cv_ten}', '{cv_email}', '{cv_sdt}', '{k['ten_khoa']}');")

    for k in khoas:
        cv_ma = k["cvht"][0]
        for lop_ma, lop_ten in k["lops"]:
            sql.append(f"INSERT OR REPLACE INTO `lop_hoc` (`ma_lop`, `ten_lop`, `khoa`, `khoa_hoc`, `ma_cvht`) VALUES ('{lop_ma}', '{lop_ten}', '{k['ten_khoa']}', 2023, '{cv_ma}');")

    for sv in all_sv:
        sql.append(f"INSERT OR REPLACE INTO `sinh_vien` (`ma_sv`, `ho_ten`, `ngay_sinh`, `gioi_tinh`, `email`, `so_dien_thoai`, `ma_lop`, `trang_thai`) VALUES ('{sv[0]}', '{sv[1]}', '{sv[2]}', '{sv[3]}', '{sv[4]}', '{sv[5]}', '{sv[6]}', '{sv[7]}');")

    for kq in all_kq:
        sql.append(f"INSERT OR REPLACE INTO `ket_qua_hoc_tap` (`ma_sv`, `hoc_ky`, `nam_hoc`, `gpa_hoc_ky`, `gpa_tich_luy`, `so_tin_chi_no`) VALUES ('{kq[0]}', {kq[1]}, '{kq[2]}', {kq[3]}, {kq[4]}, {kq[5]});")

    for cb in all_cb:
        sql.append(f"INSERT OR REPLACE INTO `canh_bao_hoc_vu` (`id`, `ma_canh_bao`, `ma_sv`, `hoc_ky`, `nam_hoc`, `muc_canh_bao`, `gpa_xet_duyet`, `ly_do`, `ngay_quyet_dinh`, `trang_thai_tu_van`) VALUES ({cb[0]}, '{cb[1]}', '{cb[2]}', {cb[3]}, '{cb[4]}', '{cb[5]}', {cb[6]}, '{cb[7]}', '{cb[8]}', '{cb[9]}');")

    for nk in all_nk:
        sql.append(f"INSERT OR REPLACE INTO `nhat_ky_tu_van` (`id`, `ma_sv`, `ma_cvht`, `id_canh_bao`, `ngay_tu_van`, `hinh_thuc`, `noi_dung`, `nguyen_nhan`, `giai_phap`, `cam_ket_sinh_vien`) VALUES ({nk[0]}, '{nk[1]}', '{nk[2]}', {nk[3]}, '{nk[4]}', '{nk[5]}', '{nk[6]}', '{nk[7]}', '{nk[8]}', '{nk[9]}');")

    for tk in all_tk:
        ref_val = f"'{tk[5]}'" if tk[5] else "NULL"
        sql.append(f"INSERT OR REPLACE INTO `tai_khoan` (`ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `vai_tro`, `ma_ref`) VALUES ('{tk[0]}', '{tk[1]}', '{tk[2]}', '{tk[3]}', '{tk[4]}', {ref_val});")

    for tb in thong_bao_samples:
        sv_val = f"'{tb[5]}'" if tb[5] else "NULL"
        sql.append(f"INSERT INTO `thong_bao` (`ma_thong_bao`, `tieu_de`, `noi_dung`, `nhom_rui_ro`, `ma_lop`, `ma_sv`, `ngay_gui`, `nguoi_gui`, `so_luong_nhan`, `trang_thai`) VALUES ('{tb[0]}', '{tb[1]}', '{tb[2]}', '{tb[3]}', '{tb[4]}', {sv_val}, CURRENT_TIMESTAMP, '{tb[6]}', {tb[7]}, '{tb[8]}');")

    for lg in lich_samples:
        sql.append(f"INSERT INTO `lich_giang_day` (`id`, `ma_cvht`, `ten_cvht`, `ma_lop`, `ten_lop`, `tieu_de`, `ngay`, `gio_bat_dau`, `gio_ket_thuc`, `dia_diem`, `hinh_thuc`, `loai_buoi`, `trang_thai`, `ghi_chu`) VALUES ({lg[0]}, '{lg[1]}', '{lg[2]}', '{lg[3]}', '{lg[4]}', '{lg[5]}', '{lg[6]}', '{lg[7]}', '{lg[8]}', '{lg[9]}', '{lg[10]}', '{lg[11]}', '{lg[12]}', '{lg[13]}');")

    for dd in dd_samples:
        sql.append(f"INSERT INTO `diem_danh` (`id`, `id_lich`, `ma_sv`, `ngay_diem_danh`, `trang_thai`, `ghi_chu`) VALUES ({dd[0]}, {dd[1]}, '{dd[2]}', '{dd[3]}', '{dd[4]}', '{dd[5]}');")

    return "\n".join(sql)

sqlserver_content = generate_sql_server()
with open("sqlserver_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlserver_content)
with open("src/main/resources/sqlserver_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlserver_content)

sqlite_content = generate_sqlite()
with open("sqlite_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)
with open("src/main/resources/sqlite_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)

print("Successfully generated EAUT database schemas!")
