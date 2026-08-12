# 🎓 BÀI TẬP LỚN MÔN CÔNG NGHỆ JAVA
## Ứng dụng Desktop Quản lý Cố vấn Học tập và Cảnh báo Học vụ

Ứng dụng Desktop được phát triển bằng **Java 21, Java Swing, JDBC và MySQL** dành cho Cố vấn học tập, Ban quản lý Khoa và Trưởng bộ môn trong việc theo dõi kết quả học tập của sinh viên, quét tự động cảnh báo học vụ, lập nhật ký tư vấn và xuất báo cáo thống kê.

---

## 🛠️ Công nghệ sử dụng
- **Ngôn ngữ**: Java 21 LTS
- **Giao diện (GUI)**: Java Swing + [FlatLaf Look & Feel](https://www.formdev.com/flatlaf/) (Giao diện phẳng, hiện đại)
- **Cơ sở dữ liệu**: MySQL 8.x
- **Kết nối CSDL**: JDBC (`mysql-connector-j`)
- **Quản lý dự án & thư viện**: Apache Maven
- **Xuất báo cáo**: Apache POI (Xuất file Excel `.xlsx`)

---

## 📊 Các tính năng chính

1. **Quản lý Đăng nhập & Phân quyền**:
   - Phân quyền người dùng: `ADMIN` (Quản trị viên), `CO_VAN` (Cố vấn học tập), `QUAN_LY` (Quản lý Khoa/Trường).
   - Mã hóa mật khẩu SHA-256 an toàn.

2. **Quản lý Sinh viên & Lớp học**:
   - Quản lý danh sách lớp phụ trách theo từng Cố vấn học tập.
   - Tìm kiếm, thêm, sửa, xóa sinh viên, lọc theo lớp/khoa/trạng thái.

3. **Tự động quét & Quản lý Cảnh báo học vụ**:
   - **Mức 1**: GPA Tích lũy < 2.0.
   - **Mức 2**: GPA Tích lũy < 1.5.
   - **Buộc thôi học**: GPA Tích lũy < 1.0 hoặc vi phạm quy định nhiều kỳ liên tiếp.
   - Tính năng **"Quét tự động cảnh báo"** cho toàn bộ sinh viên trong học kỳ xét duyệt chỉ với 1 cú click.

4. **Quản lý Nhật ký tư vấn (Cố vấn học tập)**:
   - Ghi chép chi tiết các buổi gặp mặt tư vấn sinh viên bị cảnh báo.
   - Lưu trữ nguyên nhân, giải pháp, cam kết cải thiện điểm số của sinh viên.

5. **Thống kê & Xuất báo cáo Excel**:
   - Thống kê tỷ lệ sinh viên theo các mức cảnh báo học vụ.
   - Xuất danh sách sinh viên bị cảnh báo và nhật ký tư vấn ra file Excel `.xlsx`.

---

## 🚀 Hướng dẫn cài đặt & Chạy dự án

### 1. Chuẩn bị Cơ sở dữ liệu MySQL
1. Mở MySQL Workbench, phpMyAdmin hoặc Navicat.
2. Mở và thực thi file `database.sql` nằm ở thư mục gốc của dự án.
3. CSDL `ql_canhbao_hocvu` sẽ được tạo cùng các bảng và dữ liệu mẫu.

### 2. Cấu hình kết nối MySQL trong ứng dụng
Nếu mật khẩu MySQL của bạn khác mặc định (`root` / không mật khẩu hoặc `root`/`root`), bạn có thể chỉnh sửa trong file:
`src/main/java/com/qlcvht/config/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/ql_canhbao_hocvu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = ""; // Đổi thành mật khẩu MySQL của bạn
```

### 3. Biên dịch & Chạy ứng dụng
Mở terminal tại thư mục gốc dự án và chạy các lệnh sau:

- **Biên dịch mã nguồn**:
  ```bash
  mvn clean compile
  ```

- **Chạy ứng dụng**:
  ```bash
  mvn exec:java
  ```

---

## 🔑 Tài khoản đăng nhập mặc định (Mật khẩu: `123456`)

| Tên đăng nhập | Vai trò | Họ tên |
| :--- | :--- | :--- |
| `admin` | Quản trị viên | Quản trị hệ thống |
| `cv_nguynvanan` | Cố vấn học tập | TS. Nguyễn Văn An |
| `cv_tranthibinh` | Cố vấn học tập | ThS. Trần Thị Bình |
| `quanly` | Quản lý Khoa | Trưởng khoa CNTT |
