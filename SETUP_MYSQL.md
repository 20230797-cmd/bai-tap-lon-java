# Hướng dẫn thiết lập Cơ sở dữ liệu MySQL

Dự án này sử dụng JDBC để kết nối với CSDL MySQL. Nếu máy bạn chưa cài đặt MySQL, hệ thống sẽ tự động chuyển (fallback) sang dùng SQLite tạm thời. Tuy nhiên, để đáp ứng đúng yêu cầu của môn học (Java Swing + JDBC + MySQL), bạn cần chạy MySQL thực sự.

Dưới đây là 2 cách để bạn thiết lập môi trường CSDL:

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
