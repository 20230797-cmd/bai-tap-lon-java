package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.CanhBaoHocVu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CanhBaoDAO {

    public List<CanhBaoHocVu> getAllCanhBao() {
        List<CanhBaoHocVu> list = new ArrayList<>();
        String sql = "SELECT cb.*, s.ho_ten AS ho_ten_sv, s.ma_lop " +
                     "FROM canh_bao_hoc_vu cb " +
                     "JOIN sinh_vien s ON cb.ma_sv = s.ma_sv " +
                     "ORDER BY cb.ngay_quyet_dinh DESC, cb.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToCanhBao(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CanhBaoHocVu> getCanhBaoByCoVan(String maCvht) {
        List<CanhBaoHocVu> list = new ArrayList<>();
        String sql = "SELECT cb.*, s.ho_ten AS ho_ten_sv, s.ma_lop " +
                     "FROM canh_bao_hoc_vu cb " +
                     "JOIN sinh_vien s ON cb.ma_sv = s.ma_sv " +
                     "JOIN lop_hoc l ON s.ma_lop = l.ma_lop " +
                     "WHERE l.ma_cvht = ? " +
                     "ORDER BY cb.ngay_quyet_dinh DESC, cb.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCvht);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCanhBao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CanhBaoHocVu> filterCanhBao(String mucFilter, String trangThaiTuVanFilter, String maLopFilter, String keyword) {
        List<CanhBaoHocVu> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT cb.*, s.ho_ten AS ho_ten_sv, s.ma_lop " +
            "FROM canh_bao_hoc_vu cb " +
            "JOIN sinh_vien s ON cb.ma_sv = s.ma_sv WHERE 1=1 "
        );

        if (mucFilter != null && !mucFilter.equals("ALL")) {
            sql.append("AND cb.muc_canh_bao = ? ");
        }
        if (trangThaiTuVanFilter != null && !trangThaiTuVanFilter.equals("ALL")) {
            sql.append("AND cb.trang_thai_tu_van = ? ");
        }
        if (maLopFilter != null && !maLopFilter.equals("ALL")) {
            sql.append("AND s.ma_lop = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (cb.ma_sv LIKE ? OR s.ho_ten LIKE ? OR cb.ma_canh_bao LIKE ?) ");
        }
        sql.append("ORDER BY cb.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (mucFilter != null && !mucFilter.equals("ALL")) ps.setString(idx++, mucFilter);
            if (trangThaiTuVanFilter != null && !trangThaiTuVanFilter.equals("ALL")) ps.setString(idx++, trangThaiTuVanFilter);
            if (maLopFilter != null && !maLopFilter.equals("ALL")) ps.setString(idx++, maLopFilter);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String k = "%" + keyword.trim() + "%";
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCanhBao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean existsCanhBao(String maSv, int hocKy, String namHoc) {
        String sql = "SELECT COUNT(*) FROM canh_bao_hoc_vu WHERE ma_sv = ? AND hoc_ky = ? AND nam_hoc = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            ps.setInt(2, hocKy);
            ps.setString(3, namHoc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addCanhBao(CanhBaoHocVu cb) {
        String sql = "INSERT INTO canh_bao_hoc_vu (ma_canh_bao, ma_sv, hoc_ky, nam_hoc, muc_canh_bao, gpa_xet_duyet, ly_do, ngay_quyet_dinh, trang_thai_tu_van) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cb.getMaCanhBao());
            ps.setString(2, cb.getMaSv());
            ps.setInt(3, cb.getHocKy());
            ps.setString(4, cb.getNamHoc());
            ps.setString(5, cb.getMucCanhBao());
            ps.setDouble(6, cb.getGpaXetDuyet());
            ps.setString(7, cb.getLyDo());
            ps.setDate(8, cb.getNgayQuyetDinh());
            ps.setString(9, cb.getTrangThaiTuVan() != null ? cb.getTrangThaiTuVan() : "CHUA_TU_VAN");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThaiTuVan(int idCanhBao, String trangThaiMoi) {
        String sql = "UPDATE canh_bao_hoc_vu SET trang_thai_tu_van = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setInt(2, idCanhBao);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private CanhBaoHocVu mapResultSetToCanhBao(ResultSet rs) throws SQLException {
        Date ngayQd = parseDateSafely(rs.getString("ngay_quyet_dinh"));
        CanhBaoHocVu cb = new CanhBaoHocVu(
            rs.getInt("id"),
            rs.getString("ma_canh_bao"),
            rs.getString("ma_sv"),
            rs.getInt("hoc_ky"),
            rs.getString("nam_hoc"),
            rs.getString("muc_canh_bao"),
            rs.getDouble("gpa_xet_duyet"),
            rs.getString("ly_do"),
            ngayQd,
            rs.getString("trang_thai_tu_van")
        );
        cb.setHoTenSv(rs.getString("ho_ten_sv"));
        cb.setMaLop(rs.getString("ma_lop"));
        return cb;
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
