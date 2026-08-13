package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.KetQuaHocTap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KetQuaHocTapDAO {

    public List<KetQuaHocTap> getKetQuaBySinhVien(String maSv) {
        List<KetQuaHocTap> list = new ArrayList<>();
        String sql = "SELECT * FROM ket_qua_hoc_tap WHERE ma_sv = ? ORDER BY nam_hoc DESC, hoc_ky DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public KetQuaHocTap getKetQuaHocKyMoiNhat(String maSv) {
        String sql = "SELECT * FROM ket_qua_hoc_tap WHERE ma_sv = ? ORDER BY nam_hoc DESC, hoc_ky DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<KetQuaHocTap> getAllKetQua() {
        List<KetQuaHocTap> list = new ArrayList<>();
        String sql = "SELECT kq.*, s.ho_ten AS ho_ten_sv FROM ket_qua_hoc_tap kq " +
                     "JOIN sinh_vien s ON kq.ma_sv = s.ma_sv ORDER BY kq.nam_hoc DESC, kq.hoc_ky DESC, kq.ma_sv";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                KetQuaHocTap kq = mapRow(rs);
                try { kq.setHoTenSv(rs.getString("ho_ten_sv")); } catch (Exception ignored) {}
                list.add(kq);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Them hoac cap nhat ket qua hoc tap (dung cho ca MySQL va SQLite).
     */
    public boolean saveOrUpdate(KetQuaHocTap kq) {
        // Kiem tra ton tai
        String checkSql = "SELECT id FROM ket_qua_hoc_tap WHERE ma_sv=? AND hoc_ky=? AND nam_hoc=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, kq.getMaSv());
            check.setInt(2, kq.getHocKy());
            check.setString(3, kq.getNamHoc());
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    // UPDATE
                    int existId = rs.getInt("id");
                    String upd = "UPDATE ket_qua_hoc_tap SET gpa_hoc_ky=?, gpa_tich_luy=?, so_tin_chi_no=? WHERE id=?";
                    try (PreparedStatement upStmt = conn.prepareStatement(upd)) {
                        upStmt.setDouble(1, kq.getGpaHocKy());
                        upStmt.setDouble(2, kq.getGpaTichLuy());
                        upStmt.setInt(3, kq.getSoTinChiNo());
                        upStmt.setInt(4, existId);
                        return upStmt.executeUpdate() > 0;
                    }
                } else {
                    // INSERT
                    String ins = "INSERT INTO ket_qua_hoc_tap (ma_sv,hoc_ky,nam_hoc,gpa_hoc_ky,gpa_tich_luy,so_tin_chi_no) VALUES (?,?,?,?,?,?)";
                    try (PreparedStatement inStmt = conn.prepareStatement(ins)) {
                        inStmt.setString(1, kq.getMaSv());
                        inStmt.setInt(2, kq.getHocKy());
                        inStmt.setString(3, kq.getNamHoc());
                        inStmt.setDouble(4, kq.getGpaHocKy());
                        inStmt.setDouble(5, kq.getGpaTichLuy());
                        inStmt.setInt(6, kq.getSoTinChiNo());
                        return inStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM ket_qua_hoc_tap WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private KetQuaHocTap mapRow(ResultSet rs) throws SQLException {
        KetQuaHocTap kq = new KetQuaHocTap(
            rs.getInt("id"), rs.getString("ma_sv"),
            rs.getInt("hoc_ky"), rs.getString("nam_hoc"),
            rs.getDouble("gpa_hoc_ky"), rs.getDouble("gpa_tich_luy"),
            rs.getInt("so_tin_chi_no")
        );
        return kq;
    }
}