package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaiKhoanDAO {

    public TaiKhoan login(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM tai_khoan WHERE ten_dang_nhap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("mat_khau");
                    if (PasswordUtil.verifyPassword(matKhau, storedHash)) {
                        String vaiTro = rs.getString("vai_tro");
                        String maRef = rs.getString("ma_ref");

                        // Nếu tên đăng nhập là mã sinh viên trong bảng sinh_vien nhưng vaiTro/maRef chưa chuẩn -> tự động đồng bộ
                        String checkSvSql = "SELECT * FROM sinh_vien WHERE ma_sv = ?";
                        try (PreparedStatement psSv = conn.prepareStatement(checkSvSql)) {
                            psSv.setString(1, tenDangNhap);
                            try (ResultSet rsSv = psSv.executeQuery()) {
                                if (rsSv.next()) {
                                    vaiTro = "SINH_VIEN";
                                    maRef = tenDangNhap;
                                    try (PreparedStatement fixPs = conn.prepareStatement("UPDATE tai_khoan SET vai_tro = 'SINH_VIEN', ma_ref = ? WHERE id = ?")) {
                                        fixPs.setString(1, maRef);
                                        fixPs.setInt(2, rs.getInt("id"));
                                        fixPs.executeUpdate();
                                    } catch (SQLException ignored) {}
                                }
                            }
                        }

                        return new TaiKhoan(
                            rs.getInt("id"),
                            rs.getString("ten_dang_nhap"),
                            storedHash,
                            rs.getString("ho_ten"),
                            rs.getString("email"),
                            vaiTro,
                            maRef,
                            rs.getTimestamp("ngay_tao")
                        );
                    }
                } else {
                    // Kiểm tra xem tenDangNhap có phải là Mã Sinh Viên trong bảng sinh_vien hay không
                    String checkSvSql = "SELECT * FROM sinh_vien WHERE ma_sv = ?";
                    try (PreparedStatement psSv = conn.prepareStatement(checkSvSql)) {
                        psSv.setString(1, tenDangNhap);
                        try (ResultSet rsSv = psSv.executeQuery()) {
                            if (rsSv.next()) {
                                // Mật khẩu mặc định cho sinh viên lần đầu đăng nhập là 123456
                                if ("123456".equals(matKhau) || PasswordUtil.verifyPassword(matKhau, PasswordUtil.hashPassword("123456"))) {
                                    String hoTen = rsSv.getString("ho_ten");
                                    String email = rsSv.getString("email");
                                    String hash = PasswordUtil.hashPassword("123456");
                                    String insertSql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES (?, ?, ?, ?, 'SINH_VIEN', ?)";
                                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                                        psIns.setString(1, tenDangNhap);
                                        psIns.setString(2, hash);
                                        psIns.setString(3, hoTen);
                                        psIns.setString(4, email);
                                        psIns.setString(5, tenDangNhap);
                                        psIns.executeUpdate();
                                    } catch (SQLException ignored) {}
                                    
                                    // Truy vấn lại để lấy ID vừa tạo
                                    return new TaiKhoan(
                                        0,
                                        tenDangNhap,
                                        hash,
                                        hoTen,
                                        email,
                                        "SINH_VIEN",
                                        tenDangNhap,
                                        new java.sql.Timestamp(System.currentTimeMillis())
                                    );
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean doiMatKhau(int id, String matKhauMoi) {
        String sql = "UPDATE tai_khoan SET mat_khau = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(matKhauMoi));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
