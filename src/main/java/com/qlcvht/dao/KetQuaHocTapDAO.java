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
                while (rs.next()) {
                    list.add(new KetQuaHocTap(
                        rs.getInt("id"),
                        rs.getString("ma_sv"),
                        rs.getInt("hoc_ky"),
                        rs.getString("nam_hoc"),
                        rs.getDouble("gpa_hoc_ky"),
                        rs.getDouble("gpa_tich_luy"),
                        rs.getInt("so_tin_chi_no")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public KetQuaHocTap getKetQuaHocKyMoiNhat(String maSv) {
        String sql = "SELECT * FROM ket_qua_hoc_tap WHERE ma_sv = ? ORDER BY nam_hoc DESC, hoc_ky DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new KetQuaHocTap(
                        rs.getInt("id"),
                        rs.getString("ma_sv"),
                        rs.getInt("hoc_ky"),
                        rs.getString("nam_hoc"),
                        rs.getDouble("gpa_hoc_ky"),
                        rs.getDouble("gpa_tich_luy"),
                        rs.getInt("so_tin_chi_no")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveOrUpdate(KetQuaHocTap kq) {
        String sql = "INSERT INTO ket_qua_hoc_tap (ma_sv, hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE gpa_hoc_ky = VALUES(gpa_hoc_ky), gpa_tich_luy = VALUES(gpa_tich_luy), so_tin_chi_no = VALUES(so_tin_chi_no)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kq.getMaSv());
            ps.setInt(2, kq.getHocKy());
            ps.setString(3, kq.getNamHoc());
            ps.setDouble(4, kq.getGpaHocKy());
            ps.setDouble(5, kq.getGpaTichLuy());
            ps.setInt(6, kq.getSoTinChiNo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
