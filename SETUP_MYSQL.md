# HƯỚNG DẪN CƠ SỞ DỮ LIỆU MYSQL CHO DỰ ÁN

Dự án sử dụng kiến trúc chuẩn Java Swing + JDBC kết nối trực tiếp với hệ quản trị CSDL **MySQL Server 8.4**.

### 1. Thông tin cấu hình kết nối (`src/main/resources/database.properties`):
- **Host:** `localhost`
- **Port:** `3306`
- **Database:** `ql_canhbao_hocvu`
- **User:** `root`
- **Password:** *(để trống theo mặc định hoặc điền mật khẩu của bạn)*

### 2. Khởi động MySQL Server:
- Nhấp đúp chuột vào file `start_mysql.cmd` ở thư mục gốc của dự án để khởi động MySQL Server bất kỳ lúc nào.
- Dữ liệu và bảng được lưu trữ trực tiếp bên trong MySQL Server.

## Cách 1: Sử dụng Docker (Khuyên dùng - Nhanh nhất)

Nếu máy bạn đã cài sẵn **Docker** và **Docker Desktop**, bạn chỉ cần mở terminal tại thư mục gốc của dự án và chạy lệnh:

```bash
docker-compose up -d
```

Docker sẽ tự động tải MySQL 8.0, cấu hình CSDL tên là `ql_canhbao_hocvu` (không cần password) và tự động Import toàn bộ bảng, dữ liệu mẫu từ file `database.sql`.
Sau đó bạn có thể mở dự án lên chạy ngay lập tức.

## Cách 2: Sử dụng XAMPP (Truyền thống)

Nếu bạn quen dùng XAMPP, hãy làm theo các bước sau:

1. Bật **XAMPP Control Panel**, nhấn `Start` cho module **MySQL**.
2. Mở trình duyệt, truy cập `http://localhost/phpmyadmin`.
3. Tạo một Database mới với tên: `ql_canhbao_hocvu` (Collation chọn `utf8mb4_unicode_ci`).
4. Bấm vào tab **Import** (Nhập), chọn file `database.sql` nằm ở thư mục dự án và bấm **Go** (Thực hiện).
5. (Tuỳ chọn) Nếu MySQL của bạn có mật khẩu (thường XAMPP để trống), hãy mở file `src/main/java/com/qlcvht/config/DatabaseConnection.java` và sửa lại dòng `MYSQL_PASSWORD`.
6. Chạy dự án. Hệ thống sẽ tự động nhận diện kết nối MySQL thành công.
