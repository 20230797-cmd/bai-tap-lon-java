# 🎓 BÀI TẬP LỚN MÔN CÔNG NGHỆ JAVA (CNJ09)
## Ứng dụng Desktop Quản lý Cố vấn Học tập và Cảnh báo Học vụ
**Chủ đề 1: Quản lý Tuyển sinh và Sinh viên**  
**Công nghệ:** Java 21 LTS, Java Swing, FlatLaf, JDBC, MySQL & SQLite Dual-Engine, Apache POI

---

## 📌 Giới thiệu Đề tài
Ứng dụng Desktop chuyên nghiệp phục vụ công tác Cố vấn học tập, Ban Quản lý Khoa và Phòng Đào tạo trong việc:
- Theo dõi toàn diện hồ sơ và kết quả học tập (GPA/CPA, tín chỉ nợ) của sinh viên qua các học kỳ.
- **Tự động quét & phát hiện cảnh báo học vụ** (Mức 1, Mức 2, Buộc thôi học) theo đúng Quy chế đào tạo tín chỉ chuẩn của Bộ Giáo dục & Đào tạo.
- **Phân tầng rủi ro sinh viên 3 Tier** (Tier 1: Học bổng/Khá Giỏi, Tier 2: Trung bình/Duy trì, Tier 3: Nguy cơ cao/Cần can thiệp khẩn cấp).
- Lập biên bản, ghi chép nhật ký tư vấn, theo dõi lộ trình và cam kết cải thiện của sinh viên.
- Gửi thông báo phân loại theo nhóm đối tượng, lớp hoặc phân tầng rủi ro.
- Thống kê, trực quan hóa dữ liệu qua biểu đồ Java2D (Bar Chart & Donut Chart) và xuất báo cáo chuẩn file Excel `.xlsx`.

---

## 🛠️ Ngăn xếp Công nghệ (Tech Stack)
- **Ngôn ngữ:** Java 21 LTS (Hỗ trợ switch pattern, records, virtual threads sẵn sàng).
- **Giao diện người dùng (GUI):** Java Swing + **FlatLaf Modern Look & Feel** (Giao diện phẳng, thanh thoát, màu sắc trực quan, icon đẹp mắt).
- **Cơ sở dữ liệu (Database):**
  - **MySQL 8.x** (Hỗ trợ cấu hình đa dạng `ql_canhbao_hocvu` / `ql_covan_hocvu`).
  - **SQLite Engine Tích Hợp (Zero-Config Fallback):** Tự động phát hiện và chuyển đổi sang SQLite cục bộ nếu máy chấm thi chưa cài đặt MySQL, nạp sẵn 100% dữ liệu mẫu để chạy trơn tru mà không bị lỗi kết nối DB!
- **Thao tác dữ liệu:** JDBC chuẩn với `PreparedStatement` chống SQL Injection, kiến trúc phân tầng 3 lớp (Model - DAO - Service - View).
- **Quản lý dự án & Build:** Apache Maven.
- **Xuất / Nhập Excel:** Apache POI (Hỗ trợ Import & Export bảng điểm, danh sách sinh viên ra `.xlsx`).

---

## 🏛️ Kiến trúc Ứng dụng (3-Tier MVC Architecture)

```
com.qlcvht
├── config
│   └── DatabaseConnection.java       # Quản lý kết nối MySQL & SQLite Fallback thông minh
├── model
│   ├── SinhVien.java                 # Thực thể Sinh viên & Hồ sơ
│   ├── CanhBaoHocVu.java             # Thực thể Quyết định Cảnh báo học vụ
│   ├── KetQuaHocTap.java             # Bảng điểm & Kết quả học kỳ
│   ├── NhatKyTuVan.java              # Biên bản & Nhật ký tư vấn CVHT
│   ├── LopHoc.java                   # Lớp học quản lý
│   ├── CoVanHocTap.java              # Cố vấn học tập phụ trách
│   ├── TaiKhoan.java                 # Tài khoản & Phân quyền người dùng
│   ├── SinhVienTier.java             # Phân tầng rủi ro học vụ Tier 1/2/3
│   └── ThongBao.java                 # Thông báo gửi sinh viên
├── dao
│   ├── SinhVienDAO.java              # CRUD Sinh viên & Lọc nâng cao
│   ├── CanhBaoDAO.java               # CRUD & Quản lý Cảnh báo học vụ
│   ├── KetQuaHocTapDAO.java          # Quản lý GPA/CPA từng kỳ
│   ├── NhatKyTuVanDAO.java           # Quản lý Biên bản tư vấn
│   ├── CoVanDAO.java                 # Quản lý Lớp học & Cố vấn
│   ├── TaiKhoanDAO.java              # Xác thực & Đổi mật khẩu
│   └── ThongBaoDAO.java              # Lưu trữ lịch sử thông báo
├── service
│   ├── CanhBaoService.java           # Thuật toán quét cảnh báo học vụ chuẩn tín chỉ
│   ├── ThongKeService.java           # Thống kê KPI, cơ cấu học lực và Tier
│   └── ThongBaoService.java          # Điều phối thông báo & Phân hạng Tier
├── util
│   ├── UITheme.java                  # Design system: màu sắc, font, table styling, badges
│   ├── ExcelExporter.java            # Xuất/Nhập dữ liệu Excel (.xlsx) với Apache POI
│   └── PasswordUtil.java             # Mã hóa mật khẩu SHA-256 an toàn
└── view
    ├── MainFrame.java                # Khung giao diện chính với sidebar điều hướng
    ├── LoginFrame.java               # Màn hình đăng nhập & chọn tài khoản mẫu nhanh
    ├── dialog
    │   ├── ChiTietSinhVienDialog.java# Hồ sơ học vụ 360° đa tab
    │   ├── ThemSuaSinhVienDialog.java# Hộp thoại thêm/sửa sinh viên
    │   ├── LapNhatKyDialog.java      # Hộp thoại lập biên bản tư vấn CVHT
    │   ├── ThemSuaDiemDialog.java    # Hộp thoại nhập/sửa GPA học kỳ
    │   ├── ThemSuaLopDialog.java     # Hộp thoại thêm/sửa lớp & gán CVHT
    │   └── DoiMatKhauDialog.java     # Hộp thoại đổi mật khẩu tài khoản
    └── panel
        ├── DashboardPanel.java       # Màn hình tổng quan KPI & Phân tầng Tier
        ├── QuanLySinhVienPanel.java  # Quản lý danh sách sinh viên, import/export
        ├── QuanLyKetQuaHocTapPanel.java # Quản lý bảng điểm & GPA
        ├── QuanLyCanhBaoPanel.java   # Quét tự động cảnh báo & Lập quyết định
        ├── NhatKyTuVanPanel.java     # Quản lý biên bản nhật ký tư vấn
        ├── QuanLyLopHocPanel.java    # Quản lý lớp & phân công CVHT
        ├── QuanLyThongBaoPanel.java  # Gửi thông báo phân tầng rủi ro
        └── BaoCaoThongKePanel.java   # Biểu đồ Java2D (Bar & Donut) & Xuất Excel
```

---

## ⚡ Các Tính năng Nổi bật

### 1. Quét Cảnh Báo Học Vụ Tự Động (Quy chế Tín chỉ Chuẩn)
- **Mức 1:** Điểm CPA tích lũy < 2.0 hoặc GPA học kỳ < 1.0 (hoặc nợ ≥ 6 tín chỉ).
- **Mức 2:** Điểm CPA tích lũy < 1.5 hoặc GPA học kỳ < 0.8 (hoặc nợ ≥ 14 tín chỉ).
- **Buộc thôi học / Đình chỉ:** CPA tích lũy < 1.0 hoặc nợ ≥ 24 tín chỉ vượt quá giới hạn đào tạo.
- Hỗ trợ quét tự động theo Học kỳ & Năm học chỉ định với 1 nút bấm.

### 2. Hồ Sơ Sinh Viên 360° (Student 360 Profile)
- Xem thông tin cá nhân đầy đủ và phân tầng rủi ro.
- Lịch sử kết quả học tập qua từng học kỳ và xếp loại học lực.
- Lịch sử các lần bị cảnh báo học vụ và lý do.
- Lịch sử các buổi gặp mặt tư vấn của Cố vấn học tập cùng cam kết của sinh viên.

### 3. Hệ thống Thông báo & Can thiệp Sớm (Early Warning System)
- Phân nhóm sinh viên: Tier 1 (Học bổng), Tier 2 (Duy trì), Tier 3 (Nguy cơ).
- Gửi tin nhắn thông báo theo nhóm rủi ro, theo lớp hoặc từng cá nhân.
- Mẫu thông báo dựng sẵn thuận tiện cho giảng viên và CVHT.

### 4. Báo cáo & Trực quan hóa Dữ liệu (Visual Analytics)
- Biểu đồ cột (Bar Chart) tình trạng học vụ và các mức cảnh báo.
- Biểu đồ tròn dạng Donut (Donut Chart) phân bổ cơ cấu Tier rủi ro.
- Xuất báo cáo thống kê tổng hợp ra file Excel `.xlsx` chuyên nghiệp.

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### Yêu cầu Môi trường:
- JDK 21 trở lên (`java -version`).
- Apache Maven 3.8+ (`mvn -version`).
- *(Tùy chọn)* MySQL 8.x (Nếu không có MySQL, hệ thống tự động chạy với SQLite Engine tích hợp).

### Cách 1: Chạy trực tiếp với Maven
```bash
# 1. Biên dịch dự án
mvn clean compile

# 2. Khởi chạy ứng dụng
mvn exec:java
```

### Cách 2: Đóng gói thành file JAR chạy độc lập
```bash
# Đóng gói Uber-JAR
mvn clean package

# Chạy file JAR đã đóng gói
java -jar target/ql-canhbao-hocvu-1.0.0.jar
```

---

## 🔑 Tài Khoản Đăng Nhập Mặc Định (Mật khẩu: `123456`)

| Tên đăng nhập | Vai trò | Người dùng | Quyền hạn |
| :--- | :--- | :--- | :--- |
| `admin` | **Quản trị viên** | Quản trị hệ thống | Toàn quyền quản lý, quản lý lớp, tài khoản, sinh viên |
| `cv_nguynvanan` | **Cố vấn học tập** | TS. Nguyễn Văn An | Quản lý sinh viên lớp 68IT1, quét cảnh báo, lập nhật ký tư vấn |
| `cv_tranthibinh` | **Cố vấn học tập** | ThS. Trần Thị Bình | Quản lý sinh viên lớp 68IT2, quét cảnh báo, lập nhật ký tư vấn |
| `quanly` | **Quản lý Khoa** | Trưởng khoa CNTT | Xem toàn bộ khoa, duyệt cảnh báo, xem báo cáo thống kê |
