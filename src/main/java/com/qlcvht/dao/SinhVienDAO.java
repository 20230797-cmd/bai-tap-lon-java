package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.SinhVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {

    public List<SinhVien> getAllSinhVien() {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT s.*, l.ten_lop FROM sinh_vien s LEFT JOIN lop_hoc l ON s.ma_lop = l.ma_lop ORDER BY s.ma_sv";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SinhVien sv = mapResultSetToSinhVien(rs);
                list.add(sv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SinhVien> getSinhVienByLop(String maLop) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT s.*, l.ten_lop FROM sinh_vien s LEFT JOIN lop_hoc l ON s.ma_lop = l.ma_lop WHERE s.ma_lop = ? ORDER BY s.ma_sv";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSinhVien(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SinhVien> getSinhVienByCoVan(String maCvht) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT s.*, l.ten_lop FROM sinh_vien s " +
                     "JOIN lop_hoc l ON s.ma_lop = l.ma_lop " +
                     "WHERE l.ma_cvht = ? ORDER BY s.ma_sv";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCvht);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSinhVien(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SinhVien> searchSinhVien(String keyword, String maLopFilter) {
        List<SinhVien> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT s.*, l.ten_lop FROM sinh_vien s LEFT JOIN lop_hoc l ON s.ma_lop = l.ma_lop WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (s.ma_sv LIKE ? OR s.ho_ten LIKE ? OR s.email LIKE ?) ");
        }
        if (maLopFilter != null && !maLopFilter.trim().isEmpty() && !maLopFilter.equals("ALL")) {
            sql.append("AND s.ma_lop = ? ");
        }
        sql.append("ORDER BY s.ma_sv");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String k = "%" + keyword.trim() + "%";
                ps.setString(paramIdx++, k);
                ps.setString(paramIdx++, k);
                ps.setString(paramIdx++, k);
            }
            if (maLopFilter != null && !maLopFilter.trim().isEmpty() && !maLopFilter.equals("ALL")) {
                ps.setString(paramIdx++, maLopFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSinhVien(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public SinhVien getSinhVienById(String maSv) {
        String sql = "SELECT s.*, l.ten_lop FROM sinh_vien s LEFT JOIN lop_hoc l ON s.ma_lop = l.ma_lop WHERE s.ma_sv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSinhVien(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addSinhVien(SinhVien sv) {
        String sql = "INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sv.getMaSv());
            ps.setString(2, sv.getHoTen());
            ps.setDate(3, sv.getNgaySinh());
            ps.setString(4, sv.getGioiTinh());
            ps.setString(5, sv.getEmail());
            ps.setString(6, sv.getSoDienThoai());
            ps.setString(7, sv.getMaLop());
            ps.setString(8, sv.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSinhVien(SinhVien sv) {
        String sql = "UPDATE sinh_vien SET ho_ten = ?, ngay_sinh = ?, gioi_tinh = ?, email = ?, so_dien_thoai = ?, ma_lop = ?, trang_thai = ? WHERE ma_sv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sv.getHoTen());
            ps.setDate(2, sv.getNgaySinh());
            ps.setString(3, sv.getGioiTinh());
            ps.setString(4, sv.getEmail());
            ps.setString(5, sv.getSoDienThoai());
            ps.setString(6, sv.getMaLop());
            ps.setString(7, sv.getTrangThai());
            ps.setString(8, sv.getMaSv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThaiSinhVien(String maSv, String trangThaiMoi) {
        String sql = "UPDATE sinh_vien SET trang_thai = ? WHERE ma_sv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maSv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSinhVien(String maSv) {
        String sql = "DELETE FROM sinh_vien WHERE ma_sv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SinhVien> filterSinhVienMulti(String maLop, String trangThai, String gpaFilter, String keyword, String maCvht) {
        List<SinhVien> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.*, l.ten_lop FROM sinh_vien s " +
                "LEFT JOIN lop_hoc l ON s.ma_lop = l.ma_lop " +
                "LEFT JOIN ket_qua_hoc_tap k ON s.ma_sv = k.ma_sv " +
                "WHERE 1=1 ");
        if (maCvht != null && !maCvht.isBlank()) sql.append("AND l.ma_cvht = ? ");
        if (maLop != null && !maLop.isBlank() && !maLop.equals("ALL")) sql.append("AND s.ma_lop = ? ");
        if (trangThai != null && !trangThai.isBlank() && !trangThai.equals("ALL")) sql.append("AND s.trang_thai = ? ");
        if (keyword != null && !keyword.isBlank()) sql.append("AND (s.ma_sv LIKE ? OR s.ho_ten LIKE ? OR s.email LIKE ?) ");
        if (gpaFilter != null && !gpaFilter.isBlank()) {
            switch (gpaFilter) {
                case "<1.0"       -> sql.append("AND (SELECT gpa FROM ket_qua_hoc_tap WHERE ma_sv = s.ma_sv ORDER BY nam_hoc DESC, hoc_ky DESC LIMIT 1) < 1.0 ");
                case "1.0-1.5"    -> sql.append("AND (SELECT gpa FROM ket_qua_hoc_tap WHERE ma_sv = s.ma_sv ORDER BY nam_hoc DESC, hoc_ky DESC LIMIT 1) BETWEEN 1.0 AND 1.499 ");
                case "1.5-2.0"    -> sql.append("AND (SELECT gpa FROM ket_qua_hoc_tap WHERE ma_sv = s.ma_sv ORDER BY nam_hoc DESC, hoc_ky DESC LIMIT 1) BETWEEN 1.5 AND 1.999 ");
                case ">=2.0"      -> sql.append("AND (SELECT gpa FROM ket_qua_hoc_tap WHERE ma_sv = s.ma_sv ORDER BY nam_hoc DESC, hoc_ky DESC LIMIT 1) >= 2.0 ");
                case "NO_TC_GE_8" -> sql.append("AND (SELECT COALESCE(SUM(so_tin_chi_no), 0) FROM ket_qua_hoc_tap WHERE ma_sv = s.ma_sv) >= 8 ");
            }
        }
        sql.append("GROUP BY s.ma_sv ORDER BY s.ma_sv");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (maCvht != null && !maCvht.isBlank()) ps.setString(idx++, maCvht);
            if (maLop != null && !maLop.isBlank() && !maLop.equals("ALL")) ps.setString(idx++, maLop);
            if (trangThai != null && !trangThai.isBlank() && !trangThai.equals("ALL")) ps.setString(idx++, trangThai);
            if (keyword != null && !keyword.isBlank()) {
                String k = "%" + keyword.trim() + "%";
                ps.setString(idx++, k); ps.setString(idx++, k); ps.setString(idx++, k);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToSinhVien(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private SinhVien mapResultSetToSinhVien(ResultSet rs) throws SQLException {
        Date ngaySinh = parseDateSafely(rs.getString("ngay_sinh"));
        SinhVien sv = new SinhVien(
            rs.getString("ma_sv"),
            rs.getString("ho_ten"),
            ngaySinh,
            rs.getString("gioi_tinh"),
            rs.getString("email"),
            rs.getString("so_dien_thoai"),
            rs.getString("ma_lop"),
            rs.getString("trang_thai")
        );
        sv.setTenLop(rs.getString("ten_lop"));
        return sv;
    }

    private Date parseDateSafely(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            if (dateStr.length() >= 10) {
                return Date.valueOf(dateStr.substring(0, 10));
            }
            return Date.valueOf(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
