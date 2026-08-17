package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.NhatKyTuVan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhatKyTuVanDAO {

    public List<NhatKyTuVan> getAllNhatKy() {
        List<NhatKyTuVan> list = new ArrayList<>();
        String sql = "SELECT nk.*, s.ho_ten AS ho_ten_sv, c.ho_ten AS ho_ten_cvht " +
                     "FROM nhat_ky_tu_van nk " +
                     "JOIN sinh_vien s ON nk.ma_sv = s.ma_sv " +
                     "JOIN co_van_hoc_tap c ON nk.ma_cvht = c.ma_cvht " +
                     "ORDER BY nk.ngay_tu_van DESC, nk.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToNhatKy(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NhatKyTuVan> getNhatKyByCoVan(String maCvht) {
        List<NhatKyTuVan> list = new ArrayList<>();
        String sql = "SELECT nk.*, s.ho_ten AS ho_ten_sv, c.ho_ten AS ho_ten_cvht " +
                     "FROM nhat_ky_tu_van nk " +
                     "JOIN sinh_vien s ON nk.ma_sv = s.ma_sv " +
                     "JOIN co_van_hoc_tap c ON nk.ma_cvht = c.ma_cvht " +
                     "WHERE nk.ma_cvht = ? " +
                     "ORDER BY nk.ngay_tu_van DESC, nk.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCvht);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhatKy(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NhatKyTuVan> getNhatKyBySinhVien(String maSv) {
        List<NhatKyTuVan> list = new ArrayList<>();
        String sql = "SELECT nk.*, s.ho_ten AS ho_ten_sv, c.ho_ten AS ho_ten_cvht " +
                     "FROM nhat_ky_tu_van nk " +
                     "JOIN sinh_vien s ON nk.ma_sv = s.ma_sv " +
                     "JOIN co_van_hoc_tap c ON nk.ma_cvht = c.ma_cvht " +
                     "WHERE nk.ma_sv = ? " +
                     "ORDER BY nk.ngay_tu_van DESC, nk.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhatKy(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addNhatKy(NhatKyTuVan nk) {
        String sql = "INSERT INTO nhat_ky_tu_van (ma_sv, ma_cvht, id_canh_bao, ngay_tu_van, hinh_thuc, noi_dung, nguyen_nhan, giai_phap, cam_ket_sinh_vien) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nk.getMaSv());
            ps.setString(2, nk.getMaCvht());
            if (nk.getIdCanhBao() != null) ps.setInt(3, nk.getIdCanhBao());
            else ps.setNull(3, Types.INTEGER);
            ps.setDate(4, nk.getNgayTuVan());
            ps.setString(5, nk.getHinhThuc());
            ps.setString(6, nk.getNoiDung());
            ps.setString(7, nk.getNguyenNhan());
            ps.setString(8, nk.getGiaiPhap());
            ps.setString(9, nk.getCamKetSinhVien());

            boolean success = ps.executeUpdate() > 0;
            if (success && nk.getIdCanhBao() != null) {
                // Cập nhật trạng thái tư vấn của cảnh báo tương ứng thành "DA_TU_VAN"
                new CanhBaoDAO().updateTrangThaiTuVan(nk.getIdCanhBao(), "DA_TU_VAN");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNhatKy(NhatKyTuVan nk) {
        String sql = "UPDATE nhat_ky_tu_van SET ngay_tu_van = ?, hinh_thuc = ?, noi_dung = ?, nguyen_nhan = ?, giai_phap = ?, cam_ket_sinh_vien = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, nk.getNgayTuVan());
            ps.setString(2, nk.getHinhThuc());
            ps.setString(3, nk.getNoiDung());
            ps.setString(4, nk.getNguyenNhan());
            ps.setString(5, nk.getGiaiPhap());
            ps.setString(6, nk.getCamKetSinhVien());
            ps.setInt(7, nk.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteNhatKy(int id) {
        String sql = "DELETE FROM nhat_ky_tu_van WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private NhatKyTuVan mapResultSetToNhatKy(ResultSet rs) throws SQLException {
        Date ngayTv = parseDateSafely(rs.getString("ngay_tu_van"));
        NhatKyTuVan nk = new NhatKyTuVan(
            rs.getInt("id"),
            rs.getString("ma_sv"),
            rs.getString("ma_cvht"),
            (Integer) rs.getObject("id_canh_bao"),
            ngayTv,
            rs.getString("hinh_thuc"),
            rs.getString("noi_dung"),
            rs.getString("nguyen_nhan"),
            rs.getString("giai_phap"),
            rs.getString("cam_ket_sinh_vien")
        );
        nk.setHoTenSv(rs.getString("ho_ten_sv"));
        nk.setHoTenCvht(rs.getString("ho_ten_cvht"));
        return nk;
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
