package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.ThongBao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongBaoDAO {

    public List<ThongBao> getAllThongBao() {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT * FROM thong_bao ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToThongBao(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ThongBao> getThongBaoByNhomRuiRo(String nhomRuiRo) {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT * FROM thong_bao WHERE nhom_rui_ro = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nhomRuiRo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToThongBao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addThongBao(ThongBao tb) {
        String sql = "INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tb.getMaThongBao());
            ps.setString(2, tb.getTieuDe());
            ps.setString(3, tb.getNoiDung());
            ps.setString(4, tb.getNhomRuiRo());
            ps.setString(5, tb.getMaLop());
            ps.setString(6, tb.getMaSv());
            ps.setString(7, tb.getNgayGui());
            ps.setString(8, tb.getNguoiGui());
            ps.setInt(9, tb.getSoLuongNhan());
            ps.setString(10, tb.getTrangThai() != null ? tb.getTrangThai() : "DA_GUI");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteThongBao(int id) {
        String sql = "DELETE FROM thong_bao WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ThongBao mapResultSetToThongBao(ResultSet rs) throws SQLException {
        return new ThongBao(
            rs.getInt("id"),
            rs.getString("ma_thong_bao"),
            rs.getString("tieu_de"),
            rs.getString("noi_dung"),
            rs.getString("nhom_rui_ro"),
            rs.getString("ma_lop"),
            rs.getString("ma_sv"),
            rs.getString("ngay_gui"),
            rs.getString("nguoi_gui"),
            rs.getInt("so_luong_nhan"),
            rs.getString("trang_thai")
        );
    }
}
