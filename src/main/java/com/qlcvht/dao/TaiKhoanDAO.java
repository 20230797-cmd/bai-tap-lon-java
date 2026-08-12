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
                        return new TaiKhoan(
                            rs.getInt("id"),
                            rs.getString("ten_dang_nhap"),
                            storedHash,
                            rs.getString("ho_ten"),
                            rs.getString("email"),
                            rs.getString("vai_tro"),
                            rs.getString("ma_ref"),
                            rs.getTimestamp("ngay_tao")
                        );
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
