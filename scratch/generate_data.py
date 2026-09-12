# -*- coding: utf-8 -*-
import os

ho_list = ["Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Đinh", "Đoàn", "Lâm", "Trịnh", "Mai", "Đào", "Cao", "Hà"]
ten_nam = ["Văn Nam", "Đức Hải", "Gia Bảo", "Hữu Quân", "Đức Hiếu", "Trọng Tú", "Quang Phúc", "Hoàng Tuấn", "Thanh Hưng", "Đình Khánh", "Gia Cường", "Hữu Bách", "Đức Thịnh", "Trọng Hải", "Quang Khởi", "Hoàng Duy", "Thanh Trí", "Đình Đạt", "Gia Nam", "Hữu Bảo", "Đức Kiệt", "Minh Tuấn", "Quốc Bảo", "Tấn Phát", "Thành Long", "Bảo Long", "Văn Hùng", "Tiến Dũng", "Tuấn Anh", "Minh Khôi"]
ten_nu = ["Thùy Vân", "Diệu Linh", "Ngọc Yến", "Thị Giang", "Diệu Nhi", "Ngọc Vy", "Thùy Thảo", "Thị Mai", "Diệu Quyên", "Ngọc Lan", "Phương Thảo", "Thu Hà", "Hồng Nhung", "Thanh Trúc", "Minh Châu", "Bảo Trâm", "Hải Yến", "Khánh Linh", "Mỹ Linh", "Thu Trang"]

advisors = [
    ("CV001", "TS. Đinh Văn Phong", "phong.dv@eaut.edu.vn", "0912345678", "Công nghệ thông tin"),
    ("CV002", "PGS.TS. Nguyễn Thanh Hải", "hai.nt@eaut.edu.vn", "0987654321", "Công nghệ kỹ thuật Ô tô"),
    ("CV003", "ThS. Hoàng Thị Mai", "mai.ht@eaut.edu.vn", "0934567890", "Quản trị kinh doanh"),
    ("CV004", "TS. Vũ Trường Sơn", "son.vt@eaut.edu.vn", "0945678901", "Công nghệ kỹ thuật Điện - Điện tử"),
]

classes = [
    ("DCCTPM14A", "DCCTPM14A - Công nghệ phần mềm K14", "Công nghệ thông tin", 2023, "CV001", "202300", 1),
    ("DCCNTT14B", "DCCNTT14B - Công nghệ thông tin K14B", "Công nghệ thông tin", 2023, "CV001", "202300", 16),
    ("DCOTO14A", "DCOTO14A - Công nghệ kỹ thuật Ô tô 14A", "Công nghệ kỹ thuật Ô tô", 2023, "CV002", "202301", 1),
    ("DCOTO14B", "DCOTO14B - Công nghệ kỹ thuật Ô tô 14B", "Công nghệ kỹ thuật Ô tô", 2023, "CV002", "202301", 16),
    ("DCQTKD14A", "DCQTKD14A - Quản trị kinh doanh 14A", "Quản trị kinh doanh", 2023, "CV003", "202302", 1),
    ("DCQTKD14B", "DCQTKD14B - Quản trị kinh doanh 14B", "Quản trị kinh doanh", 2023, "CV003", "202302", 16),
    ("DCDDT14A", "DCDDT14A - Kỹ thuật Điện - Điện tử 14A", "Công nghệ kỹ thuật Điện - Điện tử", 2023, "CV004", "202303", 1),
    ("DCDDT14B", "DCDDT14B - Tự động hóa K14B", "Công nghệ kỹ thuật Điện - Điện tử", 2023, "CV004", "202303", 16),
]

# Danh mục môn học theo Khoa
# (ma_mon, ten_mon, so_tc, so_tiet, so_buoi_toi_da_vang, khoa)
# Quy chế: 2TC = 10 buổi (tối đa vắng 2); 3TC = 15 buổi (tối đa vắng 3); 4TC = 20 buổi (tối đa vắng 4)
courses = [
    # Khoa CNTT
    ("IT201", "Lập trình Java nâng cao", 3, 45, 3, "Công nghệ thông tin"),
    ("IT202", "Cấu trúc dữ liệu và giải thuật", 3, 45, 3, "Công nghệ thông tin"),
    ("IT203", "Hệ quản trị cơ sở dữ liệu", 4, 60, 4, "Công nghệ thông tin"),
    ("IT204", "Mạng máy tính và An toàn thông tin", 2, 30, 2, "Công nghệ thông tin"),
    
    # Khoa Ô tô
    ("AUTO201", "Lý thuyết động cơ đốt trong", 3, 45, 3, "Công nghệ kỹ thuật Ô tô"),
    ("AUTO202", "Thực hành bảo dưỡng và sửa chữa Ô tô", 4, 60, 4, "Công nghệ kỹ thuật Ô tô"),
    ("AUTO203", "Hệ thống điện và điện tử Ô tô", 3, 45, 3, "Công nghệ kỹ thuật Ô tô"),
    ("AUTO204", "An toàn lao động và kỹ thuật xưởng", 2, 30, 2, "Công nghệ kỹ thuật Ô tô"),
    
    # Khoa QTKD
    ("BA201", "Quản trị học đại cương", 3, 45, 3, "Quản trị kinh doanh"),
    ("BA202", "Marketing căn bản và nghiên cứu thị trường", 3, 45, 3, "Quản trị kinh doanh"),
    ("BA203", "Nguyên lý kế toán và Tài chính doanh nghiệp", 4, 60, 4, "Quản trị kinh doanh"),
    ("BA204", "Kỹ năng giao tiếp và đàm phán kinh doanh", 2, 30, 2, "Quản trị kinh doanh"),
    
    # Khoa Điện - Điện tử
    ("EE201", "Kỹ thuật mạch điện tử", 3, 45, 3, "Công nghệ kỹ thuật Điện - Điện tử"),
    ("EE202", "Kỹ thuật lập trình Vi điều khiển & PLC", 4, 60, 4, "Công nghệ kỹ thuật Điện - Điện tử"),
    ("EE203", "Lý thuyết điều khiển tự động", 3, 45, 3, "Công nghệ kỹ thuật Điện - Điện tử"),
    ("EE204", "Khí cụ điện và An toàn điện", 2, 30, 2, "Công nghệ kỹ thuật Điện - Điện tử"),
]

counseling_samples = [
    ("DCCTPM14A", "20230022", "CV001", "2026-08-20", "Trực tiếp tại văn phòng Khoa", "Tư vấn kế hoạch cải thiện điểm học phần Lập trình Java và Cấu trúc dữ liệu", "Làm thêm ca tối nhiều dẫn đến mệt mỏi, thiếu thời gian tự học", "Giảm giờ làm thêm xuống dưới 15h/tuần, tham gia nhóm học tập kèm cặp", "Cam kết đạt GPA >= 2.5 kỳ này và thi đạt 2 môn nợ"),
    ("DCCTPM14A", "20230023", "CV001", "2026-08-22", "Trực tiếp tại văn phòng Khoa", "Trao đổi về tình trạng nợ môn Toán cao cấp và Lập trình C", "Chưa quen phương pháp tự học đại học, lúng túng làm bài tập lớn", "Tham gia lớp trợ giảng phụ đạo của Khoa CNTT vào thứ 7", "Cam kết đi học đầy đủ 100% các buổi học"),
    ("DCCNTT14B", "20230026", "CV001", "2026-08-24", "Trực tiếp tại văn phòng Khoa", "Tư vấn cảnh báo Mức 2 và lập biên bản cam kết tiến độ tốt nghiệp", "Mất phương hướng học tập, gặp khó khăn môn Mạng máy tính", "Chỉ đăng ký 14 tín chỉ trong kỳ tới, tập trung trả nợ 2 môn tiên quyết", "Ký biên bản cam kết cải thiện GPA, phụ huynh đã nắm thông tin"),
    ("DCCNTT14B", "20230028", "CV001", "2026-08-26", "Gọi điện thoại trao đổi phụ huynh", "Thông báo tình hình vắng nhiều buổi và kết quả thi học kỳ", "Gia đình có chuyện riêng, sinh viên phải đi làm phụ giúp kinh tế", "Nhà trường hướng dẫn làm đơn xin giãn tiến độ và học bổng hỗ trợ", "Sinh viên sắp xếp lại thời gian biểu và tham gia đầy đủ"),
    ("DCOTO14A", "20230122", "CV002", "2026-08-22", "Trực tiếp tại xưởng Ô tô", "Tư vấn môn Thực hành động cơ và An toàn xưởng", "Nghỉ 2 buổi thực hành do ốm, chưa hoàn thành bài tập xưởng", "Đăng ký học bù vào sáng thứ 7 với kỹ thuật viên xưởng", "Cam kết hoàn thành 100% bài thực hành trước kỳ thi"),
    ("DCOTO14A", "20230126", "CV002", "2026-08-25", "Trực tiếp tại văn phòng Khoa", "Tư vấn cảnh báo Mức 2 chuyên ngành Kỹ thuật Ô tô", "Nợ môn Cơ khí đại cương và Sức bền vật liệu", "Được phân công bạn giỏi kèm cặp, CVHT kiểm tra bài tập 2 tuần/lần", "Cam kết không nghỉ học và trả hết 2 môn trong học kỳ"),
    ("DCQTKD14A", "20230222", "CV003", "2026-08-23", "Trực tiếp tại VP Khoa", "Tư vấn môn Nguyên lý kế toán và Kinh tế vi mô", "Hổng kiến thức nền tảng toán kinh tế", "Tham gia câu lạc bộ học thuật QTKD để được hướng dẫn ôn tập", "Cam kết làm đủ bài tập nhóm và nộp đúng hạn"),
    ("DCQTKD14B", "20230226", "CV003", "2026-08-27", "Trực tuyến qua MS Teams", "Tư vấn xử lý học vụ cảnh báo Mức 2", "Đi làm thêm kinh doanh online quá nhiều", "Tạm hoãn việc kinh doanh, tập trung ôn thi học kỳ", "Cam kết đạt điểm B các môn chuyên ngành"),
    ("DCDDT14A", "20230322", "CV004", "2026-08-24", "Trực tiếp tại phòng Lab", "Tư vấn môn Mạch điện tử và Kỹ thuật số", "Khó khăn trong việc nạp code và làm mạch in", "GV hướng dẫn kèm thêm tại phòng thí nghiệm thứ 5 hàng tuần", "Cam kết hoàn thành sản phẩm mạch đúng tiến độ"),
    ("DCDDT14B", "20230326", "CV004", "2026-08-28", "Trực tiếp tại VP Khoa", "Tư vấn sinh viên cảnh báo Mức 2 ngành Tự động hóa", "Nợ môn Vi điều khiển và Điều khiển tự động", "Rút bớt môn học tự chọn, tập trung tối đa cho 2 môn cốt lõi", "Cam kết cải thiện GPA trên 2.2 ở kỳ tới")
]

students = []
for c_idx, (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht, prefix, start_num) in enumerate(classes):
    for i in range(15):
        num = start_num + i
        ma_sv = f"{prefix}{num:02d}"
        
        is_female = (num % 3 == 0) or (num % 7 == 0)
        gioi_tinh = "Nữ" if is_female else "Nam"
        ho = ho_list[(num * 3 + c_idx) % len(ho_list)]
        ten = ten_nu[(num * 2) % len(ten_nu)] if is_female else ten_nam[(num * 2) % len(ten_nam)]
        ho_ten = f"{ho} {ten}"
        
        day = (num * 5) % 28 + 1
        month = (num * 3) % 12 + 1
        ngay_sinh = f"2005-{month:02d}-{day:02d}"
        email = f"sv{ma_sv}@eaut.edu.vn"
        sdt = f"091{c_idx+1}{num:06d}"
        
        if i < 2:
            tier = 1
            gpa1 = round(3.50 + (num % 5) * 0.08, 2)
            gpa2 = round(3.60 + (num % 4) * 0.09, 2)
            gpa3 = round(3.75 + (num % 3) * 0.07, 2)
            no_tc = 0
            trang_thai = "DANG_HOC"
        elif i < 5:
            tier = 1
            gpa1 = round(3.15 + (num % 4) * 0.08, 2)
            gpa2 = round(3.25 + (num % 5) * 0.06, 2)
            gpa3 = round(3.38 + (num % 4) * 0.05, 2)
            no_tc = 0
            trang_thai = "DANG_HOC"
        elif i < 9:
            tier = 2
            gpa1 = round(2.55 + (num % 5) * 0.12, 2)
            gpa2 = round(2.65 + (num % 4) * 0.11, 2)
            gpa3 = round(2.80 + (num % 3) * 0.10, 2)
            no_tc = 0 if num % 2 == 0 else 3
            trang_thai = "DANG_HOC"
        elif i < 11:
            tier = 2
            gpa1 = round(2.10 + (num % 4) * 0.08, 2)
            gpa2 = round(2.20 + (num % 3) * 0.07, 2)
            gpa3 = round(2.35 + (num % 2) * 0.05, 2)
            no_tc = 4 + (num % 3)
            trang_thai = "DANG_HOC"
        elif i < 13:
            tier = 3
            gpa1 = round(1.85 + (num % 3) * 0.04, 2)
            gpa2 = round(1.72 - (num % 2) * 0.05, 2)
            gpa3 = round(1.68 + (num % 3) * 0.06, 2)
            no_tc = 8 + (num % 4)
            trang_thai = "CANH_BAO_1"
        elif i == 13:
            tier = 3
            gpa1 = round(1.58, 2)
            gpa2 = round(1.36, 2)
            gpa3 = round(1.24, 2)
            no_tc = 15
            trang_thai = "CANH_BAO_2"
        else:
            tier = 3
            gpa1 = round(1.15, 2)
            gpa2 = round(0.88, 2)
            gpa3 = round(0.72, 2)
            no_tc = 24
            trang_thai = "BUOC_THOI_HOC"
            
        cpa = round((gpa1 + gpa2 + gpa3) / 3, 2)
        
        students.append({
            "ma_sv": ma_sv,
            "ho_ten": ho_ten,
            "ngay_sinh": ngay_sinh,
            "gioi_tinh": gioi_tinh,
            "email": email,
            "sdt": sdt,
            "ma_lop": ma_lop,
            "khoa": khoa,
            "trang_thai": trang_thai,
            "tier": tier,
            "gpa1": gpa1,
            "gpa2": gpa2,
            "gpa3": gpa3,
            "cpa": cpa,
            "no_tc": no_tc,
            "ma_cvht": ma_cvht,
            "class_idx": c_idx,
            "sv_in_class_idx": i
        })

# Tạo dữ liệu chuyên cần môn học cho 120 sinh viên
# Mỗi khoa có 4 môn. Sinh viên học các môn thuộc khoa của mình trong học kỳ hiện tại (HK1 2024-2025).
course_attendance_list = []
ca_id = 1

for sv in students:
    khoa_sv = sv["khoa"]
    sv_courses = [c for c in courses if c[5] == khoa_sv]
    idx_in_class = sv["sv_in_class_idx"]
    
    for c_item in sv_courses:
        ma_mon, ten_mon, so_tc, so_tiet, max_vang, _ = c_item
        tong_buoi = so_tc * 5
        
        # Thiết lập các trường hợp chuyên cần:
        # 1. Sinh viên Giỏi/Xuất sắc (idx 0-4): Đi đủ 100% hoặc vắng có phép 1 buổi -> ĐỦ ĐIỀU KIỆN
        # 2. Sinh viên Khá (idx 5-8): Đi đủ hoặc muộn 1-2 lần -> ĐỦ ĐIỀU KIỆN
        # 3. Sinh viên Trung bình (idx 9-10): Muộn 2 lần, vắng 1-2 buổi -> ĐỦ ĐIỀU KIỆN hoặc CẢNH BÁO NGUY CƠ
        # 4. Sinh viên Cảnh báo 1 (idx 11-12): Vắng 3-4 buổi -> Môn 2TC/3TC bị CẤM THI, môn 4TC NGUY CƠ
        # 5. Sinh viên Cảnh báo 2 (idx 13): Vắng 4-6 buổi -> CẤM THI 2-3 môn
        # 6. Sinh viên Buộc thôi học (idx 14): Vắng 5-8 buổi -> CẤM THI toàn bộ các môn
        
        if idx_in_class < 2:
            co_mat = tong_buoi
            muon = 0
            vang_cp = 0
            vang_kp = 0
        elif idx_in_class < 5:
            co_mat = tong_buoi - 1
            muon = 1 if (int(sv["ma_sv"][-2:]) % 2 == 0) else 0
            vang_cp = 1
            vang_kp = 0
        elif idx_in_class < 9:
            co_mat = tong_buoi - 2
            muon = 2
            vang_cp = 1
            vang_kp = 0
        elif idx_in_class < 11:
            co_mat = tong_buoi - 3
            muon = 2
            vang_cp = 1
            vang_kp = 1
        elif idx_in_class < 13:
            # Vắng 3-4 buổi
            vang_kp = 3 if so_tc <= 3 else 4
            vang_cp = 1
            muon = 2
            co_mat = tong_buoi - (vang_kp + vang_cp)
        elif idx_in_class == 13:
            # Cảnh báo 2: vắng nhiều
            vang_kp = 4 if so_tc == 2 else (5 if so_tc == 3 else 6)
            vang_cp = 1
            muon = 3
            co_mat = tong_buoi - (vang_kp + vang_cp)
        else:
            # Buộc thôi học: vắng quá nhiều
            vang_kp = 5 if so_tc == 2 else (6 if so_tc == 3 else 7)
            vang_cp = 2
            muon = 4
            co_mat = max(0, tong_buoi - (vang_kp + vang_cp))
            
        vang_qd = vang_kp + vang_cp + (muon // 2)
        ty_le_vang = round((vang_qd / float(tong_buoi)) * 100.0, 1)
        
        if vang_qd > max_vang or ty_le_vang >= 20.0:
            tt_thi = "CAM_THI"
            diem_cc = 0.0
            ly_do = f"Vắng {vang_qd}/{tong_buoi} buổi ({ty_le_vang}% > 20% quy định môn {so_tc} TC)"
        elif vang_qd == max_vang:
            tt_thi = "CANH_BAO_NGUY_CO"
            diem_cc = round(max(3.0, 10.0 - vang_qd * 2.0), 1)
            ly_do = f"Đã vắng {vang_qd}/{tong_buoi} buổi (ngưỡng tối đa cho phép)"
        else:
            tt_thi = "DU_DIEU_KIEN"
            diem_cc = round(max(5.0, 10.0 - vang_qd * 1.5 - muon * 0.5), 1)
            ly_do = ""
            
        course_attendance_list.append({
            "id": ca_id,
            "ma_sv": sv["ma_sv"],
            "ma_lop": sv["ma_lop"],
            "ma_mon": ma_mon,
            "hoc_ky": 1,
            "nam_hoc": "2024-2025",
            "tong_so_buoi": tong_buoi,
            "so_buoi_co_mat": co_mat,
            "so_buoi_muon": muon,
            "so_buoi_vang_co_phep": vang_cp,
            "so_buoi_vang_khong_phep": vang_kp,
            "tong_buoi_vang_quy_doi": vang_qd,
            "ty_le_vang": ty_le_vang,
            "diem_chuyen_can": diem_cc,
            "trang_thai_du_thi": tt_thi,
            "ly_do_cam_thi": ly_do
        })
        ca_id += 1

print(f"Total Course Attendance records generated: {len(course_attendance_list)}")

# Tạo SQL Server Script
def build_sql_server():
    sql = []
    sql.append("-- ============================================================")
    sql.append("-- DATABASE SCHEMA & DỮ LIỆU ĐỒNG BỘ 100% - ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)")
    sql.append("-- 4 Khoa chuyên ngành | 8 Lớp học | 16 Môn học | 120 Sinh viên | Quản lý Cấm thi Tín chỉ")
    sql.append("-- Mật khẩu đăng nhập mặc định: 123456")
    sql.append("-- ============================================================")
    sql.append("")
    sql.append("IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ql_canhbao_hocvu')")
    sql.append("BEGIN")
    sql.append("    CREATE DATABASE ql_canhbao_hocvu COLLATE Vietnamese_CI_AS;")
    sql.append("END")
    sql.append("GO")
    sql.append("")
    sql.append("USE ql_canhbao_hocvu;")
    sql.append("GO")
    sql.append("")
    sql.append("IF OBJECT_ID('dbo.chuyen_can_mon_hoc', 'U') IS NOT NULL DROP TABLE dbo.chuyen_can_mon_hoc;")
    sql.append("IF OBJECT_ID('dbo.diem_danh', 'U') IS NOT NULL DROP TABLE dbo.diem_danh;")
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
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE co_van_hoc_tap (")
    sql.append("    ma_cvht VARCHAR(20) PRIMARY KEY,")
    sql.append("    ho_ten NVARCHAR(100) NOT NULL,")
    sql.append("    email VARCHAR(100) NOT NULL,")
    sql.append("    so_dien_thoai VARCHAR(20),")
    sql.append("    khoa NVARCHAR(100) NOT NULL")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE mon_hoc (")
    sql.append("    ma_mon VARCHAR(20) PRIMARY KEY,")
    sql.append("    ten_mon NVARCHAR(100) NOT NULL,")
    sql.append("    so_tin_chi INT NOT NULL,")
    sql.append("    so_tiet INT NOT NULL,")
    sql.append("    so_buoi_toi_da_vang INT NOT NULL,")
    sql.append("    khoa NVARCHAR(100) NOT NULL")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE lop_hoc (")
    sql.append("    ma_lop VARCHAR(20) PRIMARY KEY,")
    sql.append("    ten_lop NVARCHAR(100) NOT NULL,")
    sql.append("    khoa NVARCHAR(100) NOT NULL,")
    sql.append("    khoa_hoc INT NOT NULL,")
    sql.append("    ma_cvht VARCHAR(20),")
    sql.append("    CONSTRAINT fk_lop_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht) ON DELETE SET NULL")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE sinh_vien (")
    sql.append("    ma_sv VARCHAR(20) PRIMARY KEY,")
    sql.append("    ho_ten NVARCHAR(100) NOT NULL,")
    sql.append("    ngay_sinh DATE,")
    sql.append("    gioi_tinh NVARCHAR(10),")
    sql.append("    email VARCHAR(100),")
    sql.append("    so_dien_thoai VARCHAR(20),")
    sql.append("    ma_lop VARCHAR(20) NOT NULL,")
    sql.append("    trang_thai VARCHAR(30) DEFAULT 'DANG_HOC',")
    sql.append("    CONSTRAINT fk_sinhvien_lop FOREIGN KEY (ma_lop) REFERENCES lop_hoc(ma_lop) ON DELETE CASCADE")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE ket_qua_hoc_tap (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    ma_sv VARCHAR(20) NOT NULL,")
    sql.append("    hoc_ky INT NOT NULL,")
    sql.append("    nam_hoc VARCHAR(20) NOT NULL,")
    sql.append("    gpa_hoc_ky FLOAT NOT NULL,")
    sql.append("    gpa_tich_luy FLOAT NOT NULL,")
    sql.append("    so_tin_chi_no INT DEFAULT 0,")
    sql.append("    CONSTRAINT fk_kq_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE canh_bao_hoc_vu (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    ma_canh_bao VARCHAR(50) UNIQUE NOT NULL,")
    sql.append("    ma_sv VARCHAR(20) NOT NULL,")
    sql.append("    hoc_ky INT NOT NULL,")
    sql.append("    nam_hoc VARCHAR(20) NOT NULL,")
    sql.append("    muc_canh_bao VARCHAR(30) NOT NULL,")
    sql.append("    gpa_xet_duyet FLOAT NOT NULL,")
    sql.append("    ly_do NVARCHAR(255),")
    sql.append("    ngay_quyet_dinh DATE,")
    sql.append("    trang_thai_tu_van VARCHAR(30) DEFAULT 'CHUA_TU_VAN',")
    sql.append("    CONSTRAINT fk_canhbao_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE nhat_ky_tu_van (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    ma_sv VARCHAR(20) NOT NULL,")
    sql.append("    ma_cvht VARCHAR(20) NOT NULL,")
    sql.append("    id_canh_bao INT,")
    sql.append("    ngay_tu_van DATE NOT NULL,")
    sql.append("    hinh_thuc NVARCHAR(50) NOT NULL,")
    sql.append("    noi_dung NVARCHAR(MAX) NOT NULL,")
    sql.append("    nguyen_nhan NVARCHAR(MAX),")
    sql.append("    giai_phap NVARCHAR(MAX),")
    sql.append("    cam_ket_sinh_vien NVARCHAR(MAX),")
    sql.append("    CONSTRAINT fk_nhatky_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv),")
    sql.append("    CONSTRAINT fk_nhatky_covan FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht),")
    sql.append("    CONSTRAINT fk_nhatky_canhbao FOREIGN KEY (id_canh_bao) REFERENCES canh_bao_hoc_vu(id) ON DELETE SET NULL")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE tai_khoan (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,")
    sql.append("    mat_khau VARCHAR(255) NOT NULL,")
    sql.append("    ho_ten NVARCHAR(100) NOT NULL,")
    sql.append("    email VARCHAR(100),")
    sql.append("    vai_tro VARCHAR(20) NOT NULL,")
    sql.append("    ma_ref VARCHAR(20),")
    sql.append("    trang_thai INT DEFAULT 1,")
    sql.append("    ngay_tao DATETIME DEFAULT GETDATE()")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE thong_bao (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    ma_thong_bao VARCHAR(50) UNIQUE NOT NULL,")
    sql.append("    tieu_de NVARCHAR(255) NOT NULL,")
    sql.append("    noi_dung NVARCHAR(MAX) NOT NULL,")
    sql.append("    nhom_rui_ro VARCHAR(30) DEFAULT 'ALL',")
    sql.append("    ma_lop VARCHAR(20) DEFAULT 'ALL',")
    sql.append("    ma_sv VARCHAR(20),")
    sql.append("    ngay_gui DATETIME DEFAULT GETDATE(),")
    sql.append("    nguoi_gui NVARCHAR(100),")
    sql.append("    so_luong_nhan INT DEFAULT 0,")
    sql.append("    trang_thai VARCHAR(30) DEFAULT 'DA_GUI'")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE lich_giang_day (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    ma_cvht VARCHAR(20) NOT NULL,")
    sql.append("    ten_cvht NVARCHAR(100) NOT NULL,")
    sql.append("    ma_lop VARCHAR(20) NOT NULL,")
    sql.append("    ten_lop NVARCHAR(100) NOT NULL,")
    sql.append("    tieu_de NVARCHAR(200) NOT NULL,")
    sql.append("    ngay DATE NOT NULL,")
    sql.append("    gio_bat_dau VARCHAR(10) NOT NULL,")
    sql.append("    gio_ket_thuc VARCHAR(10) NOT NULL,")
    sql.append("    dia_diem NVARCHAR(100) NOT NULL,")
    sql.append("    hinh_thuc NVARCHAR(50) NOT NULL,")
    sql.append("    loai_buoi NVARCHAR(50) NOT NULL,")
    sql.append("    trang_thai NVARCHAR(50) NOT NULL,")
    sql.append("    ghi_chu NVARCHAR(MAX)")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE diem_danh (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    id_lich INT NOT NULL,")
    sql.append("    ma_sv VARCHAR(20) NOT NULL,")
    sql.append("    ngay_diem_danh DATE NOT NULL,")
    sql.append("    trang_thai VARCHAR(30) NOT NULL,")
    sql.append("    ghi_chu NVARCHAR(255),")
    sql.append("    CONSTRAINT fk_diemdanh_lich FOREIGN KEY (id_lich) REFERENCES lich_giang_day(id) ON DELETE CASCADE,")
    sql.append("    CONSTRAINT fk_diemdanh_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    sql.append("CREATE TABLE chuyen_can_mon_hoc (")
    sql.append("    id INT IDENTITY(1,1) PRIMARY KEY,")
    sql.append("    ma_sv VARCHAR(20) NOT NULL,")
    sql.append("    ma_lop VARCHAR(20) NOT NULL,")
    sql.append("    ma_mon VARCHAR(20) NOT NULL,")
    sql.append("    hoc_ky INT NOT NULL,")
    sql.append("    nam_hoc VARCHAR(20) NOT NULL,")
    sql.append("    tong_so_buoi INT NOT NULL,")
    sql.append("    so_buoi_co_mat INT NOT NULL,")
    sql.append("    so_buoi_muon INT NOT NULL,")
    sql.append("    so_buoi_vang_co_phep INT NOT NULL,")
    sql.append("    so_buoi_vang_khong_phep INT NOT NULL,")
    sql.append("    tong_buoi_vang_quy_doi FLOAT NOT NULL,")
    sql.append("    ty_le_vang FLOAT NOT NULL,")
    sql.append("    diem_chuyen_can FLOAT NOT NULL,")
    sql.append("    trang_thai_du_thi VARCHAR(30) NOT NULL,")
    sql.append("    ly_do_cam_thi NVARCHAR(255),")
    sql.append("    CONSTRAINT fk_cc_sinhvien FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE,")
    sql.append("    CONSTRAINT fk_cc_monhoc FOREIGN KEY (ma_mon) REFERENCES mon_hoc(ma_mon) ON DELETE CASCADE")
    sql.append(");")
    sql.append("GO")
    sql.append("")
    
    # 1. Cố vấn
    sql.append("-- 1. CỐ VẤN HỌC TẬP")
    for adv in advisors:
        sql.append(f"INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES ('{adv[0]}', N'{adv[1]}', '{adv[2]}', '{adv[3]}', N'{adv[4]}');")
    sql.append("GO")
    sql.append("")
    
    # 2. Môn học
    sql.append("-- 2. MÔN HỌC / HỌC PHẦN (QUY CHẾ TÍN CHỈ)")
    for cr in courses:
        sql.append(f"INSERT INTO mon_hoc (ma_mon, ten_mon, so_tin_chi, so_tiet, so_buoi_toi_da_vang, khoa) VALUES ('{cr[0]}', N'{cr[1]}', {cr[2]}, {cr[3]}, {cr[4]}, N'{cr[5]}');")
    sql.append("GO")
    sql.append("")
    
    # 3. Lớp học
    sql.append("-- 3. LỚP HỌC")
    for cl in classes:
        sql.append(f"INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('{cl[0]}', N'{cl[1]}', N'{cl[2]}', {cl[3]}, '{cl[4]}');")
    sql.append("GO")
    sql.append("")
    
    # 4. Sinh viên
    sql.append("-- 4. SINH VIÊN (120 SINH VIÊN ĐỒNG BỘ)")
    for sv in students:
        sql.append(f"INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('{sv['ma_sv']}', N'{sv['ho_ten']}', '{sv['ngay_sinh']}', N'{sv['gioi_tinh']}', '{sv['email']}', '{sv['sdt']}', '{sv['ma_lop']}', '{sv['trang_thai']}');")
    sql.append("GO")
    sql.append("")
    
    # 5. Kết quả học tập
    sql.append("-- 5. KẾT QUẢ HỌC TẬP TỪNG HỌC KỲ")
    for sv in students:
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('{sv['ma_sv']}', 1, '2023-2024', {sv['gpa1']}, {sv['gpa1']}, {0 if sv['tier'] <= 2 else max(0, sv['no_tc']-6)});")
        cpa2 = round((sv['gpa1'] + sv['gpa2']) / 2, 2)
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('{sv['ma_sv']}', 2, '2023-2024', {sv['gpa2']}, {cpa2}, {0 if sv['tier'] <= 2 else max(0, sv['no_tc']-3)});")
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('{sv['ma_sv']}', 1, '2024-2025', {sv['gpa3']}, {sv['cpa']}, {sv['no_tc']});")
    sql.append("GO")
    sql.append("")
    
    # 6. Chuyên cần môn học & Quyết định cấm thi
    sql.append("-- 6. CHUYÊN CẦN MÔN HỌC & ĐIỀU KIỆN DỰ THI (TỰ ĐỘNG CẤM THI VẮNG >= 20%)")
    for ca in course_attendance_list:
        sql.append(f"INSERT INTO chuyen_can_mon_hoc (ma_sv, ma_lop, ma_mon, hoc_ky, nam_hoc, tong_so_buoi, so_buoi_co_mat, so_buoi_muon, so_buoi_vang_co_phep, so_buoi_vang_khong_phep, tong_buoi_vang_quy_doi, ty_le_vang, diem_chuyen_can, trang_thai_du_thi, ly_do_cam_thi) VALUES ('{ca['ma_sv']}', '{ca['ma_lop']}', '{ca['ma_mon']}', {ca['hoc_ky']}, '{ca['nam_hoc']}', {ca['tong_so_buoi']}, {ca['so_buoi_co_mat']}, {ca['so_buoi_muon']}, {ca['so_buoi_vang_co_phep']}, {ca['so_buoi_vang_khong_phep']}, {ca['tong_buoi_vang_quy_doi']}, {ca['ty_le_vang']}, {ca['diem_chuyen_can']}, '{ca['trang_thai_du_thi']}', N'{ca['ly_do_cam_thi']}');")
    sql.append("GO")
    sql.append("")
    
    # 7. Cảnh báo học vụ
    sql.append("-- 7. CẢNH BÁO HỌC VỤ")
    cb_count = 1
    for sv in students:
        if sv["trang_thai"] == "CANH_BAO_1":
            status_tu_van = "DA_TU_VAN" if (cb_count % 3 == 0) else ("DANG_THEO_DOI" if cb_count % 3 == 1 else "CHUA_TU_VAN")
            sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('CB1-2024-{cb_count:03d}', '{sv['ma_sv']}', 1, '2024-2025', 'CANH_BAO_1', {sv['gpa3']}, N'GPA học kỳ < 2.0 hoặc nợ {sv['no_tc']} tín chỉ', '2026-08-15', '{status_tu_van}');")
            cb_count += 1
        elif sv["trang_thai"] == "CANH_BAO_2":
            status_tu_van = "DA_TU_VAN" if (cb_count % 2 == 0) else "DANG_THEO_DOI"
            sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('CB2-2024-{cb_count:03d}', '{sv['ma_sv']}', 1, '2024-2025', 'CANH_BAO_2', {sv['gpa3']}, N'Bị cảnh báo Mức 1 liên tiếp 2 học kỳ, nợ {sv['no_tc']} TC', '2026-08-15', '{status_tu_van}');")
            cb_count += 1
        elif sv["trang_thai"] == "BUOC_THOI_HOC":
            status_tu_van = "CHUA_TU_VAN" if (cb_count % 2 == 0) else "DANG_THEO_DOI"
            sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('BTH-2024-{cb_count:03d}', '{sv['ma_sv']}', 1, '2024-2025', 'BUOC_THOI_HOC', {sv['gpa3']}, N'GPA học kỳ < 1.0 và nợ {sv['no_tc']} tín chỉ vượt ngưỡng', '2026-08-15', '{status_tu_van}');")
            cb_count += 1
    sql.append("GO")
    sql.append("")
    
    # 8. Nhật ký tư vấn
    sql.append("-- 8. NHẬT KÝ TƯ VẤN CỦA CỐ VẤN HỌC TẬP")
    for nk in counseling_samples:
        sql.append(f"INSERT INTO nhat_ky_tu_van (ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES ('{nk[1]}', '{nk[2]}', NULL, '{nk[3]}', N'{nk[4]}', N'{nk[5]}', N'{nk[6]}', N'{nk[7]}', N'{nk[8]}');")
    sql.append("GO")
    sql.append("")
    
    # 9. Tài khoản
    sql.append("-- 9. TÀI KHOẢN NGƯỜI DÙNG (MẬT KHẨU MẶC ĐỊNH: 123456)")
    hash_pass = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('admin', '{hash_pass}', N'Quản trị viên Hệ thống EAUT', 'admin@eaut.edu.vn', 'ADMIN', NULL);")
    
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_phongdv', '{hash_pass}', N'TS. Đinh Văn Phong', 'phong.dv@eaut.edu.vn', 'CO_VAN', 'CV001');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv001', '{hash_pass}', N'TS. Đinh Văn Phong', 'phong.dv@eaut.edu.vn', 'CO_VAN', 'CV001');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_haint', '{hash_pass}', N'PGS.TS. Nguyễn Thanh Hải', 'hai.nt@eaut.edu.vn', 'CO_VAN', 'CV002');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv002', '{hash_pass}', N'PGS.TS. Nguyễn Thanh Hải', 'hai.nt@eaut.edu.vn', 'CO_VAN', 'CV002');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_maiht', '{hash_pass}', N'ThS. Hoàng Thị Mai', 'mai.ht@eaut.edu.vn', 'CO_VAN', 'CV003');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv003', '{hash_pass}', N'ThS. Hoàng Thị Mai', 'mai.ht@eaut.edu.vn', 'CO_VAN', 'CV003');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_sonvt', '{hash_pass}', N'TS. Vũ Trường Sơn', 'son.vt@eaut.edu.vn', 'CO_VAN', 'CV004');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv004', '{hash_pass}', N'TS. Vũ Trường Sơn', 'son.vt@eaut.edu.vn', 'CO_VAN', 'CV004');")
    
    for sv in students:
        sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('{sv['ma_sv']}', '{hash_pass}', N'{sv['ho_ten']}', '{sv['email']}', 'SINH_VIEN', '{sv['ma_sv']}');")
    sql.append("GO")
    sql.append("")
    
    # 10. Thông báo & tin nhắn
    sql.append("-- 10. THÔNG BÁO & TIN NHẮN")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-01', N'[EAUT] THÔNG BÁO XÉT HỌC BỔNG KHUYẾN KHÍCH HỌC TẬP K14', N'Chúc mừng các sinh viên đạt GPA >= 3.2 trong học kỳ 2 năm học 2023-2024. Đề nghị sinh viên nộp hồ sơ xét học bổng tại VP Đoàn trường trước ngày 20/09/2026.', 'TIER_1', 'ALL', NULL, GETDATE(), N'Phòng Đào Tạo EAUT', 32, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-02', N'[EAUT] KẾ HOẠCH ĐĂNG KÝ HỌC PHẦN HỌC KỲ 1 NĂM HỌC 2024-2025', N'Hệ thống cổng đào tạo eaut.edu.vn mở cổng đăng ký tín chỉ từ 8h00 ngày 25/08/2026. Sinh viên chú ý các môn tiên quyết.', 'TIER_2', 'ALL', NULL, GETDATE(), N'Phòng Đào Tạo EAUT', 64, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-03', N'[EAUT] YÊU CẦU TƯ VẤN HỌC VỤ BẮT BUỘC ĐỐI VỚI SINH VIÊN BỊ CẢNH BÁO', N'Các sinh viên có tên trong danh sách cảnh báo Mức 1, Mức 2 và Buộc thôi học phải liên hệ ngay Cố vấn học tập trước ngày 15/08/2026.', 'TIER_3', 'ALL', NULL, GETDATE(), N'Ban CVHT EAUT', 24, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-04', N'[KHOA CNTT] HỘI THẢO CÔNG NGHỆ & ĐỊNH HƯỚNG NGHỀ NGHIỆP AI & CLOUD', N'Khoa CNTT phối hợp cùng doanh nghiệp đối tác tổ chức workshop chia sẻ công nghệ vào 9h00 sáng thứ Bảy tại Hội trường A.', 'ALL', 'DCCTPM14A', NULL, GETDATE(), N'TS. Đinh Văn Phong', 15, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-05', N'[KHOA Ô TÔ] LỊCH THỰC TẬP TỐT NGHIỆP VÀ AN TOÀN XƯỞNG THỰC HÀNH', N'Yêu cầu 100% sinh viên lớp DCOTO14A trang bị đồ bảo hộ lao động đầy đủ trước khi vào xưởng Ô tô.', 'ALL', 'DCOTO14A', NULL, GETDATE(), N'PGS.TS. Nguyễn Thanh Hải', 15, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-01', N'Chúc mừng kết quả học tập xuất sắc ngành Công nghệ thông tin', N'Thầy chúc mừng em Nam đã đạt GPA 3.75 đứng đầu lớp DCCTPM14A kỳ vừa qua. Tiếp tục giữ vững phong độ nhé em!', 'CA_NHAN', 'DCCTPM14A', '20230001', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-02', N'📬 [SV PHẢN HỒI] Em cảm ơn thầy và muốn hỏi về học bổng', N'Dạ em chào thầy Phong, em cảm ơn thầy ạ! Cho em hỏi hồ sơ xét học bổng kỳ này cần nộp bản sao bảng điểm có xác nhận không ạ?', 'PHAN_HOI_SV', 'ALL', '20230001', GETDATE(), N'Vũ Đình Anh (20230001)', 1, 'DA_DOC');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-03', N'💬 [CVHT TRẢ LỜI] Hướng dẫn thủ tục học bổng', N'Chào em, bản điểm thầy sẽ trực tiếp ký xác nhận và gửi VP Đoàn cho em nhé. Em chỉ cần nộp đơn xin xét theo mẫu thôi.', 'CVHT_TRA_LOI', 'CA_NHAN', '20230001', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_DOC');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-04', N'Lịch hẹn gặp mặt tư vấn học tập và kế hoạch học lại', N'Chào em Duy, học kỳ vừa qua em bị cảnh báo Mức 1. Chiều thứ Ba tuần tới 14h00 em đến văn phòng Khoa gặp thầy để trao đổi kế hoạch học lại nhé.', 'CA_NHAN', 'DCCTPM14A', '20230022', GETDATE(), N'TS. Đinh Văn Phong', 1, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-05', N'📬 [SV PHẢN HỒI] Em xác nhận lịch hẹn tư vấn', N'Dạ em chào thầy, thứ Ba tuần tới 14h00 em sẽ có mặt đúng giờ tại VP Khoa ạ. Em cảm ơn thầy đã nhắc nhở em!', 'PHAN_HOI_SV', 'ALL', '20230022', GETDATE(), N'Bùi Hoàng Duy (20230022)', 1, 'SV_CHUA_DOC');")
    sql.append("GO")
    sql.append("")
    
    sql.append("PRINT N'Khởi tạo Cơ sở Dữ liệu Microsoft SQL Server EAUT ql_canhbao_hocvu (16 Môn học + 480 Bản ghi Chuyên cần) thành công 100%!';")
    return "\n".join(sql)

# Tạo SQLite Script
def build_sqlite():
    sql = []
    sql.append("-- ============================================================")
    sql.append("-- CƠ SỞ DỮ LIỆU SQLITE TÍCH HỢP CỤC BỘ (ZERO-CONFIG FALLBACK)")
    sql.append("-- 100% ĐỒNG BỘ DỮ LIỆU ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)")
    sql.append("-- ============================================================")
    sql.append("")
    sql.append("DROP TABLE IF EXISTS chuyen_can_mon_hoc;")
    sql.append("DROP TABLE IF EXISTS diem_danh;")
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
    sql.append("")
    sql.append("CREATE TABLE co_van_hoc_tap (")
    sql.append("    ma_cvht TEXT PRIMARY KEY,")
    sql.append("    ho_ten TEXT NOT NULL,")
    sql.append("    email TEXT NOT NULL,")
    sql.append("    so_dien_thoai TEXT,")
    sql.append("    khoa TEXT NOT NULL")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE mon_hoc (")
    sql.append("    ma_mon TEXT PRIMARY KEY,")
    sql.append("    ten_mon TEXT NOT NULL,")
    sql.append("    so_tin_chi INTEGER NOT NULL,")
    sql.append("    so_tiet INTEGER NOT NULL,")
    sql.append("    so_buoi_toi_da_vang INTEGER NOT NULL,")
    sql.append("    khoa TEXT NOT NULL")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE lop_hoc (")
    sql.append("    ma_lop TEXT PRIMARY KEY,")
    sql.append("    ten_lop TEXT NOT NULL,")
    sql.append("    khoa TEXT NOT NULL,")
    sql.append("    khoa_hoc INTEGER NOT NULL,")
    sql.append("    ma_cvht TEXT,")
    sql.append("    FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht) ON DELETE SET NULL")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE sinh_vien (")
    sql.append("    ma_sv TEXT PRIMARY KEY,")
    sql.append("    ho_ten TEXT NOT NULL,")
    sql.append("    ngay_sinh TEXT,")
    sql.append("    gioi_tinh TEXT,")
    sql.append("    email TEXT,")
    sql.append("    so_dien_thoai TEXT,")
    sql.append("    ma_lop TEXT NOT NULL,")
    sql.append("    trang_thai TEXT DEFAULT 'DANG_HOC',")
    sql.append("    FOREIGN KEY (ma_lop) REFERENCES lop_hoc(ma_lop) ON DELETE CASCADE")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE ket_qua_hoc_tap (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    ma_sv TEXT NOT NULL,")
    sql.append("    hoc_ky INTEGER NOT NULL,")
    sql.append("    nam_hoc TEXT NOT NULL,")
    sql.append("    gpa_hoc_ky REAL NOT NULL,")
    sql.append("    gpa_tich_luy REAL NOT NULL,")
    sql.append("    so_tin_chi_no INTEGER DEFAULT 0,")
    sql.append("    FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE canh_bao_hoc_vu (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    ma_canh_bao TEXT UNIQUE NOT NULL,")
    sql.append("    ma_sv TEXT NOT NULL,")
    sql.append("    hoc_ky INTEGER NOT NULL,")
    sql.append("    nam_hoc TEXT NOT NULL,")
    sql.append("    muc_canh_bao TEXT NOT NULL,")
    sql.append("    gpa_xet_duyet REAL NOT NULL,")
    sql.append("    ly_do TEXT,")
    sql.append("    ngay_quyet_dinh TEXT,")
    sql.append("    trang_thai_tu_van TEXT DEFAULT 'CHUA_TU_VAN',")
    sql.append("    FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE nhat_ky_tu_van (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    ma_sv TEXT NOT NULL,")
    sql.append("    ma_cvht TEXT NOT NULL,")
    sql.append("    id_canh_bao INTEGER,")
    sql.append("    ngay_tu_van TEXT NOT NULL,")
    sql.append("    hinh_thuc TEXT NOT NULL,")
    sql.append("    noi_dung TEXT NOT NULL,")
    sql.append("    nguyen_nhan TEXT,")
    sql.append("    giai_phap TEXT,")
    sql.append("    cam_ket_sinh_vien TEXT,")
    sql.append("    FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv),")
    sql.append("    FOREIGN KEY (ma_cvht) REFERENCES co_van_hoc_tap(ma_cvht),")
    sql.append("    FOREIGN KEY (id_canh_bao) REFERENCES canh_bao_hoc_vu(id) ON DELETE SET NULL")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE tai_khoan (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    ten_dang_nhap TEXT UNIQUE NOT NULL,")
    sql.append("    mat_khau TEXT NOT NULL,")
    sql.append("    ho_ten TEXT NOT NULL,")
    sql.append("    email TEXT,")
    sql.append("    vai_tro TEXT NOT NULL,")
    sql.append("    ma_ref TEXT,")
    sql.append("    trang_thai INTEGER DEFAULT 1,")
    sql.append("    ngay_tao TEXT DEFAULT (datetime('now', 'localtime'))")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE thong_bao (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    ma_thong_bao TEXT UNIQUE NOT NULL,")
    sql.append("    tieu_de TEXT NOT NULL,")
    sql.append("    noi_dung TEXT NOT NULL,")
    sql.append("    nhom_rui_ro TEXT DEFAULT 'ALL',")
    sql.append("    ma_lop TEXT DEFAULT 'ALL',")
    sql.append("    ma_sv TEXT,")
    sql.append("    ngay_gui TEXT DEFAULT (datetime('now', 'localtime')),")
    sql.append("    nguoi_gui TEXT,")
    sql.append("    so_luong_nhan INTEGER DEFAULT 0,")
    sql.append("    trang_thai TEXT DEFAULT 'DA_GUI'")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE lich_giang_day (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    ma_cvht TEXT NOT NULL,")
    sql.append("    ten_cvht TEXT NOT NULL,")
    sql.append("    ma_lop TEXT NOT NULL,")
    sql.append("    ten_lop TEXT NOT NULL,")
    sql.append("    tieu_de TEXT NOT NULL,")
    sql.append("    ngay TEXT NOT NULL,")
    sql.append("    gio_bat_dau TEXT NOT NULL,")
    sql.append("    gio_ket_thuc TEXT NOT NULL,")
    sql.append("    dia_diem TEXT NOT NULL,")
    sql.append("    hinh_thuc TEXT NOT NULL,")
    sql.append("    loai_buoi TEXT NOT NULL,")
    sql.append("    trang_thai TEXT NOT NULL,")
    sql.append("    ghi_chu TEXT")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE diem_danh (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    id_lich INTEGER NOT NULL,")
    sql.append("    ma_sv TEXT NOT NULL,")
    sql.append("    ngay_diem_danh TEXT NOT NULL,")
    sql.append("    trang_thai TEXT NOT NULL,")
    sql.append("    ghi_chu TEXT,")
    sql.append("    FOREIGN KEY (id_lich) REFERENCES lich_giang_day(id) ON DELETE CASCADE,")
    sql.append("    FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE")
    sql.append(");")
    sql.append("")
    sql.append("CREATE TABLE chuyen_can_mon_hoc (")
    sql.append("    id INTEGER PRIMARY KEY AUTOINCREMENT,")
    sql.append("    ma_sv TEXT NOT NULL,")
    sql.append("    ma_lop TEXT NOT NULL,")
    sql.append("    ma_mon TEXT NOT NULL,")
    sql.append("    hoc_ky INTEGER NOT NULL,")
    sql.append("    nam_hoc TEXT NOT NULL,")
    sql.append("    tong_so_buoi INTEGER NOT NULL,")
    sql.append("    so_buoi_co_mat INTEGER NOT NULL,")
    sql.append("    so_buoi_muon INTEGER NOT NULL,")
    sql.append("    so_buoi_vang_co_phep INTEGER NOT NULL,")
    sql.append("    so_buoi_vang_khong_phep INTEGER NOT NULL,")
    sql.append("    tong_buoi_vang_quy_doi REAL NOT NULL,")
    sql.append("    ty_le_vang REAL NOT NULL,")
    sql.append("    diem_chuyen_can REAL NOT NULL,")
    sql.append("    trang_thai_du_thi TEXT NOT NULL,")
    sql.append("    ly_do_cam_thi TEXT,")
    sql.append("    FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv) ON DELETE CASCADE,")
    sql.append("    FOREIGN KEY (ma_mon) REFERENCES mon_hoc(ma_mon) ON DELETE CASCADE")
    sql.append(");")
    sql.append("")
    
    # 1. Cố vấn
    sql.append("-- 1. CỐ VẤN HỌC TẬP")
    for adv in advisors:
        sql.append(f"INSERT INTO co_van_hoc_tap (ma_cvht, ho_ten, email, so_dien_thoai, khoa) VALUES ('{adv[0]}', '{adv[1]}', '{adv[2]}', '{adv[3]}', '{adv[4]}');")
    sql.append("")
    
    # 2. Môn học
    sql.append("-- 2. MÔN HỌC / HỌC PHẦN")
    for cr in courses:
        sql.append(f"INSERT INTO mon_hoc (ma_mon, ten_mon, so_tin_chi, so_tiet, so_buoi_toi_da_vang, khoa) VALUES ('{cr[0]}', '{cr[1]}', {cr[2]}, {cr[3]}, {cr[4]}, '{cr[5]}');")
    sql.append("")
    
    # 3. Lớp học
    sql.append("-- 3. LỚP HỌC")
    for cl in classes:
        sql.append(f"INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES ('{cl[0]}', '{cl[1]}', '{cl[2]}', {cl[3]}, '{cl[4]}');")
    sql.append("")
    
    # 4. Sinh viên
    sql.append("-- 4. SINH VIÊN (120 SINH VIÊN ĐỒNG BỘ)")
    for sv in students:
        sql.append(f"INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES ('{sv['ma_sv']}', '{sv['ho_ten']}', '{sv['ngay_sinh']}', '{sv['gioi_tinh']}', '{sv['email']}', '{sv['sdt']}', '{sv['ma_lop']}', '{sv['trang_thai']}');")
    sql.append("")
    
    # 5. Kết quả học tập
    sql.append("-- 5. KẾT QUẢ HỌC TẬP")
    for sv in students:
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('{sv['ma_sv']}', 1, '2023-2024', {sv['gpa1']}, {sv['gpa1']}, {0 if sv['tier'] <= 2 else max(0, sv['no_tc']-6)});")
        cpa2 = round((sv['gpa1'] + sv['gpa2']) / 2, 2)
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('{sv['ma_sv']}', 2, '2023-2024', {sv['gpa2']}, {cpa2}, {0 if sv['tier'] <= 2 else max(0, sv['no_tc']-3)});")
        sql.append(f"INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) VALUES ('{sv['ma_sv']}', 1, '2024-2025', {sv['gpa3']}, {sv['cpa']}, {sv['no_tc']});")
    sql.append("")
    
    # 6. Chuyên cần môn học
    sql.append("-- 6. CHUYÊN CẦN MÔN HỌC & ĐIỀU KIỆN DỰ THI")
    for ca in course_attendance_list:
        sql.append(f"INSERT INTO chuyen_can_mon_hoc (ma_sv, ma_lop, ma_mon, hoc_ky, nam_hoc, tong_so_buoi, so_buoi_co_mat, so_buoi_muon, so_buoi_vang_co_phep, so_buoi_vang_khong_phep, tong_buoi_vang_quy_doi, ty_le_vang, diem_chuyen_can, trang_thai_du_thi, ly_do_cam_thi) VALUES ('{ca['ma_sv']}', '{ca['ma_lop']}', '{ca['ma_mon']}', {ca['hoc_ky']}, '{ca['nam_hoc']}', {ca['tong_so_buoi']}, {ca['so_buoi_co_mat']}, {ca['so_buoi_muon']}, {ca['so_buoi_vang_co_phep']}, {ca['so_buoi_vang_khong_phep']}, {ca['tong_buoi_vang_quy_doi']}, {ca['ty_le_vang']}, {ca['diem_chuyen_can']}, '{ca['trang_thai_du_thi']}', '{ca['ly_do_cam_thi']}');")
    sql.append("")
    
    # 7. Cảnh báo học vụ
    sql.append("-- 7. CẢNH BÁO HỌC VỤ")
    cb_count = 1
    for sv in students:
        if sv["trang_thai"] == "CANH_BAO_1":
            status_tu_van = "DA_TU_VAN" if (cb_count % 3 == 0) else ("DANG_THEO_DOI" if cb_count % 3 == 1 else "CHUA_TU_VAN")
            sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('CB1-2024-{cb_count:03d}', '{sv['ma_sv']}', 1, '2024-2025', 'CANH_BAO_1', {sv['gpa3']}, 'GPA học kỳ < 2.0 hoặc nợ {sv['no_tc']} tín chỉ', '2026-08-15', '{status_tu_van}');")
            cb_count += 1
        elif sv["trang_thai"] == "CANH_BAO_2":
            status_tu_van = "DA_TU_VAN" if (cb_count % 2 == 0) else "DANG_THEO_DOI"
            sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('CB2-2024-{cb_count:03d}', '{sv['ma_sv']}', 1, '2024-2025', 'CANH_BAO_2', {sv['gpa3']}, 'Bị cảnh báo Mức 1 liên tiếp 2 học kỳ, nợ {sv['no_tc']} TC', '2026-08-15', '{status_tu_van}');")
            cb_count += 1
        elif sv["trang_thai"] == "BUOC_THOI_HOC":
            status_tu_van = "CHUA_TU_VAN" if (cb_count % 2 == 0) else "DANG_THEO_DOI"
            sql.append(f"INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) VALUES ('BTH-2024-{cb_count:03d}', '{sv['ma_sv']}', 1, '2024-2025', 'BUOC_THOI_HOC', {sv['gpa3']}, 'GPA học kỳ < 1.0 và nợ {sv['no_tc']} tín chỉ vượt ngưỡng', '2026-08-15', '{status_tu_van}');")
            cb_count += 1
    sql.append("")
    
    # 8. Nhật ký tư vấn
    sql.append("-- 8. NHẬT KÝ TƯ VẤN")
    for nk in counseling_samples:
        sql.append(f"INSERT INTO nhat_ky_tu_van (ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) VALUES ('{nk[1]}', '{nk[2]}', NULL, '{nk[3]}', '{nk[4]}', '{nk[5]}', '{nk[6]}', '{nk[7]}', '{nk[8]}');")
    sql.append("")
    
    # 9. Tài khoản
    sql.append("-- 9. TÀI KHOẢN NGƯỜI DÙNG")
    hash_pass = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('admin', '{hash_pass}', 'Quản trị viên Hệ thống EAUT', 'admin@eaut.edu.vn', 'ADMIN', NULL);")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_phongdv', '{hash_pass}', 'TS. Đinh Văn Phong', 'phong.dv@eaut.edu.vn', 'CO_VAN', 'CV001');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv001', '{hash_pass}', 'TS. Đinh Văn Phong', 'phong.dv@eaut.edu.vn', 'CO_VAN', 'CV001');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_haint', '{hash_pass}', 'PGS.TS. Nguyễn Thanh Hải', 'hai.nt@eaut.edu.vn', 'CO_VAN', 'CV002');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv002', '{hash_pass}', 'PGS.TS. Nguyễn Thanh Hải', 'hai.nt@eaut.edu.vn', 'CO_VAN', 'CV002');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_maiht', '{hash_pass}', 'ThS. Hoàng Thị Mai', 'mai.ht@eaut.edu.vn', 'CO_VAN', 'CV003');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv003', '{hash_pass}', 'ThS. Hoàng Thị Mai', 'mai.ht@eaut.edu.vn', 'CO_VAN', 'CV003');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv_sonvt', '{hash_pass}', 'TS. Vũ Trường Sơn', 'son.vt@eaut.edu.vn', 'CO_VAN', 'CV004');")
    sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('cv004', '{hash_pass}', 'TS. Vũ Trường Sơn', 'son.vt@eaut.edu.vn', 'CO_VAN', 'CV004');")
    
    for sv in students:
        sql.append(f"INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES ('{sv['ma_sv']}', '{hash_pass}', '{sv['ho_ten']}', '{sv['email']}', 'SINH_VIEN', '{sv['ma_sv']}');")
    sql.append("")
    
    # 10. Thông báo
    sql.append("-- 10. THÔNG BÁO")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-01', '[EAUT] THÔNG BÁO XÉT HỌC BỔNG KHUYẾN KHÍCH HỌC TẬP K14', 'Chúc mừng các sinh viên đạt GPA >= 3.2 trong học kỳ 2 năm học 2023-2024. Đề nghị sinh viên nộp hồ sơ xét học bổng tại VP Đoàn trường trước ngày 20/09/2026.', 'TIER_1', 'ALL', NULL, datetime('now', 'localtime'), 'Phòng Đào Tạo EAUT', 32, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-02', '[EAUT] KẾ HOẠCH ĐĂNG KÝ HỌC PHẦN HỌC KỲ 1 NĂM HỌC 2024-2025', 'Hệ thống cổng đào tạo eaut.edu.vn mở cổng đăng ký tín chỉ từ 8h00 ngày 25/08/2026. Sinh viên chú ý các môn tiên quyết.', 'TIER_2', 'ALL', NULL, datetime('now', 'localtime'), 'Phòng Đào Tạo EAUT', 64, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-03', '[EAUT] YÊU CẦU TƯ VẤN HỌC VỤ BẮT BUỘC ĐỐI VỚI SINH VIÊN BỊ CẢNH BÁO', 'Các sinh viên có tên trong danh sách cảnh báo Mức 1, Mức 2 và Buộc thôi học phải liên hệ ngay Cố vấn học tập trước ngày 15/08/2026.', 'TIER_3', 'ALL', NULL, datetime('now', 'localtime'), 'Ban CVHT EAUT', 24, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-04', '[KHOA CNTT] HỘI THẢO CÔNG NGHỆ & ĐỊNH HƯỚNG NGHỀ NGHIỆP AI & CLOUD', 'Khoa CNTT phối hợp cùng doanh nghiệp đối tác tổ chức workshop chia sẻ công nghệ vào 9h00 sáng thứ Bảy tại Hội trường A.', 'ALL', 'DCCTPM14A', NULL, datetime('now', 'localtime'), 'TS. Đinh Văn Phong', 15, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-EAUT-05', '[KHOA Ô TÔ] LỊCH THỰC TẬP TỐT NGHIỆP VÀ AN TOÀN XƯỞNG THỰC HÀNH', 'Yêu cầu 100% sinh viên lớp DCOTO14A trang bị đồ bảo hộ lao động đầy đủ trước khi vào xưởng Ô tô.', 'ALL', 'DCOTO14A', NULL, datetime('now', 'localtime'), 'PGS.TS. Nguyễn Thanh Hải', 15, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-01', 'Chúc mừng kết quả học tập xuất sắc ngành Công nghệ thông tin', 'Thầy chúc mừng em Nam đã đạt GPA 3.75 đứng đầu lớp DCCTPM14A kỳ vừa qua. Tiếp tục giữ vững phong độ nhé em!', 'CA_NHAN', 'DCCTPM14A', '20230001', datetime('now', 'localtime'), 'TS. Đinh Văn Phong', 1, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-02', '📬 [SV PHẢN HỒI] Em cảm ơn thầy và muốn hỏi về học bổng', 'Dạ em chào thầy Phong, em cảm ơn thầy ạ! Cho em hỏi hồ sơ xét học bổng kỳ này cần nộp bản sao bảng điểm có xác nhận không ạ?', 'PHAN_HOI_SV', 'ALL', '20230001', datetime('now', 'localtime'), 'Vũ Đình Anh (20230001)', 1, 'DA_DOC');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-03', '💬 [CVHT TRẢ LỜI] Hướng dẫn thủ tục học bổng', 'Chào em, bản điểm thầy sẽ trực tiếp ký xác nhận và gửi VP Đoàn cho em nhé. Em chỉ cần nộp đơn xin xét theo mẫu thôi.', 'CVHT_TRA_LOI', 'CA_NHAN', '20230001', datetime('now', 'localtime'), 'TS. Đinh Văn Phong', 1, 'DA_DOC');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-04', 'Lịch hẹn gặp mặt tư vấn học tập và kế hoạch học lại', 'Chào em Duy, học kỳ vừa qua em bị cảnh báo Mức 1. Chiều thứ Ba tuần tới 14h00 em đến văn phòng Khoa gặp thầy để trao đổi kế hoạch học lại nhé.', 'CA_NHAN', 'DCCTPM14A', '20230022', datetime('now', 'localtime'), 'TS. Đinh Văn Phong', 1, 'DA_GUI');")
    sql.append("INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) VALUES ('TB-CHAT-05', '📬 [SV PHẢN HỒI] Em xác nhận lịch hẹn tư vấn', 'Dạ em chào thầy, thứ Ba tuần tới 14h00 em sẽ có mặt đúng giờ tại VP Khoa ạ. Em cảm ơn thầy đã nhắc nhở em!', 'PHAN_HOI_SV', 'ALL', '20230022', datetime('now', 'localtime'), 'Bùi Hoàng Duy (20230022)', 1, 'SV_CHUA_DOC');")
    sql.append("")
    
    return "\n".join(sql)

# Ghi ra các file
sqlserver_content = build_sql_server()
sqlite_content = build_sqlite()

with open("sqlserver_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlserver_content)
with open("src/main/resources/sqlserver_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlserver_content)

with open("sqlite_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)
with open("src/main/resources/sqlite_schema.sql", "w", encoding="utf-8") as f:
    f.write(sqlite_content)

print(f"Generated sqlserver_schema.sql ({len(sqlserver_content)} bytes)")
print(f"Generated sqlite_schema.sql ({len(sqlite_content)} bytes)")
