package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    public TaiKhoan login(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || matKhau == null) return null;
        String trimmedUser = tenDangNhap.trim();
        String trimmedPass = matKhau.trim();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Tìm trong bảng tai_khoan theo ten_dang_nhap hoặc ma_ref
            String sql = "SELECT * FROM tai_khoan WHERE LOWER(ten_dang_nhap) = LOWER(?) OR (ma_ref IS NOT NULL AND LOWER(ma_ref) = LOWER(?))";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, trimmedUser);
                ps.setString(2, trimmedUser);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String storedHash = rs.getString("mat_khau");
                        if (PasswordUtil.verifyPassword(trimmedPass, storedHash)) {
                            return mapResultSet(rs);
                        }
                    }
                }
            }

            // 2. Fallback Admin nếu tài khoản admin đăng nhập bằng mật khẩu 123456 hoặc admin
            if ("admin".equalsIgnoreCase(trimmedUser) && ("123456".equals(trimmedPass) || "admin".equalsIgnoreCase(trimmedPass))) {
                String hash = PasswordUtil.hashPassword("123456");
                try (PreparedStatement psIns = conn.prepareStatement(
                        "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro) VALUES ('admin', ?, N'Quản trị viên Hệ thống EAUT', 'admin@eaut.edu.vn', 'ADMIN')")) {
                    psIns.setString(1, hash);
                    psIns.executeUpdate();
                } catch (SQLException ignored) {}
                return new TaiKhoan(1, "admin", hash, "Quản trị viên Hệ thống EAUT", "admin@eaut.edu.vn", "ADMIN", null, new Timestamp(System.currentTimeMillis()));
            }



            // 4. Fallback: Nếu là Cố vấn học tập trong bảng co_van_hoc_tap
            String cvSql = "SELECT * FROM co_van_hoc_tap WHERE LOWER(ma_cvht) = LOWER(?) OR LOWER(email) LIKE ? OR LOWER(?) LIKE '%' + LOWER(ma_cvht) + '%'";
            try (PreparedStatement psCv = conn.prepareStatement(cvSql)) {
                psCv.setString(1, trimmedUser);
                psCv.setString(2, "%" + trimmedUser.toLowerCase() + "%");
                psCv.setString(3, trimmedUser);
                try (ResultSet rsCv = psCv.executeQuery()) {
                    if (rsCv.next()) {
                        if ("123456".equals(trimmedPass) || PasswordUtil.verifyPassword(trimmedPass, PasswordUtil.hashPassword("123456"))) {
                            String maCv = rsCv.getString("ma_cvht");
                            String hoTen = rsCv.getString("ho_ten");
                            String email = rsCv.getString("email");
                            String hash = PasswordUtil.hashPassword("123456");
                            
                            try (PreparedStatement psIns = conn.prepareStatement(
                                    "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES (?, ?, ?, ?, 'CO_VAN', ?)")) {
                                psIns.setString(1, trimmedUser);
                                psIns.setString(2, hash);
                                psIns.setString(3, hoTen);
                                psIns.setString(4, email);
                                psIns.setString(5, maCv);
                                psIns.executeUpdate();
                            } catch (SQLException ignored) {}

                            return new TaiKhoan(0, trimmedUser, hash, hoTen, email, "CO_VAN", maCv, new Timestamp(System.currentTimeMillis()));
                        }
                    }
                }
            }

            // 5. Fallback: Nếu là Sinh viên trong bảng sinh_vien
            String svSql = "SELECT * FROM sinh_vien WHERE LOWER(ma_sv) = LOWER(?)";
            try (PreparedStatement psSv = conn.prepareStatement(svSql)) {
                psSv.setString(1, trimmedUser);
                try (ResultSet rsSv = psSv.executeQuery()) {
                    if (rsSv.next()) {
                        if ("123456".equals(trimmedPass) || PasswordUtil.verifyPassword(trimmedPass, PasswordUtil.hashPassword("123456"))) {
                            String maSv = rsSv.getString("ma_sv");
                            String hoTen = rsSv.getString("ho_ten");
                            String email = rsSv.getString("email");
                            String hash = PasswordUtil.hashPassword("123456");

                            try (PreparedStatement psIns = conn.prepareStatement(
                                    "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES (?, ?, ?, ?, 'SINH_VIEN', ?)")) {
                                psIns.setString(1, maSv);
                                psIns.setString(2, hash);
                                psIns.setString(3, hoTen);
                                psIns.setString(4, email);
                                psIns.setString(5, maSv);
                                psIns.executeUpdate();
                            } catch (SQLException ignored) {}

                            return new TaiKhoan(0, maSv, hash, hoTen, email, "SINH_VIEN", maSv, new Timestamp(System.currentTimeMillis()));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TaiKhoan> getAll(String keyword, String vaiTro) {
        List<TaiKhoan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tai_khoan WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (vaiTro != null && !vaiTro.isEmpty() && !"ALL".equalsIgnoreCase(vaiTro)) {
            sql.append("AND vai_tro = ? ");
            params.add(vaiTro);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(ten_dang_nhap) LIKE ? OR LOWER(ho_ten) LIKE ? OR LOWER(email) LIKE ? OR LOWER(ma_ref) LIKE ?) ");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        sql.append("ORDER BY id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean them(TaiKhoan tk) {
        String sql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, PasswordUtil.hashPassword(tk.getMatKhau() != null ? tk.getMatKhau() : "123456"));
            ps.setString(3, tk.getHoTen());
            ps.setString(4, tk.getEmail());
            ps.setString(5, tk.getVaiTro());
            ps.setString(6, tk.getMaRef());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        String sql = "UPDATE tai_khoan SET ho_ten = ?, email = ?, vai_tro = ?, ma_ref = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getHoTen());
            ps.setString(2, tk.getEmail());
            ps.setString(3, tk.getVaiTro());
            ps.setString(4, tk.getMaRef());
            ps.setInt(5, tk.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(int id) {
        String sql = "DELETE FROM tai_khoan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    public boolean resetMatKhau(int id, String matKhauMoi) {
        return doiMatKhau(id, matKhauMoi != null ? matKhauMoi : "123456");
    }

    private TaiKhoan mapResultSet(ResultSet rs) throws SQLException {
        return new TaiKhoan(
            rs.getInt("id"),
            rs.getString("ten_dang_nhap"),
            rs.getString("mat_khau"),
            rs.getString("ho_ten"),
            rs.getString("email"),
            rs.getString("vai_tro"),
            rs.getString("ma_ref"),
            rs.getTimestamp("ngay_tao")
        );
    }
}
