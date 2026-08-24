package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.DiemDanh;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiemDanhDAO {

    public List<DiemDanh> getBySession(int idLich) {
        List<DiemDanh> list = new ArrayList<>();
        String sql = "SELECT d.*, s.ho_ten, s.ma_lop " +
                     "FROM diem_danh d JOIN sinh_vien s ON d.ma_sv = s.ma_sv " +
                     "WHERE d.id_lich = ? ORDER BY s.ma_sv ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLich);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemDanh d = new DiemDanh();
                    d.setId(rs.getInt("id"));
                    d.setIdLich(rs.getInt("id_lich"));
                    d.setMaSv(rs.getString("ma_sv"));
                    d.setHoTen(rs.getString("ho_ten"));
                    d.setMaLop(rs.getString("ma_lop"));
                    Date dt = rs.getDate("ngay_diem_danh");
                    if (dt != null) {
                        d.setNgayDiemDanh(dt.toLocalDate());
                    }
                    d.setTrangThai(rs.getString("trang_thai"));
                    d.setGhiChu(rs.getString("ghi_chu"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DiemDanh> getByStudent(String maSv) {
        List<DiemDanh> list = new ArrayList<>();
        String sql = "SELECT d.*, l.tieu_de, l.ngay AS ngay_lich, l.gio_bat_dau " +
                     "FROM diem_danh d JOIN lich_giang_day l ON d.id_lich = l.id " +
                     "WHERE d.ma_sv = ? ORDER BY d.ngay_diem_danh DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemDanh d = new DiemDanh();
                    d.setId(rs.getInt("id"));
                    d.setIdLich(rs.getInt("id_lich"));
                    d.setMaSv(rs.getString("ma_sv"));
                    Date dt = rs.getDate("ngay_diem_danh");
                    if (dt != null) {
                        d.setNgayDiemDanh(dt.toLocalDate());
                    }
                    d.setTrangThai(rs.getString("trang_thai"));
                    d.setGhiChu(rs.getString("ghi_chu"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean saveOrUpdateAttendance(int idLich, String maSv, LocalDate ngay, String trangThai, String ghiChu) {
        // Kiểm tra xem đã có bản ghi chưa
        String checkSql = "SELECT id FROM diem_danh WHERE id_lich = ? AND ma_sv = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, idLich);
            ps.setString(2, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String updateSql = "UPDATE diem_danh SET trang_thai = ?, ghi_chu = ?, ngay_diem_danh = ? WHERE id = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, trangThai);
                        updatePs.setString(2, ghiChu);
                        updatePs.setString(3, ngay.toString());
                        updatePs.setInt(4, id);
                        return updatePs.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO diem_danh (id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, idLich);
                        insertPs.setString(2, maSv);
                        insertPs.setString(3, ngay.toString());
                        insertPs.setString(4, trangThai);
                        insertPs.setString(5, ghiChu);
                        return insertPs.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getAttendanceStats(String maLop) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("ON_TIME", 0);
        stats.put("LATE", 0);
        stats.put("EXCUSED", 0);
        stats.put("ABSENT", 0);

        StringBuilder sql = new StringBuilder(
            "SELECT d.trang_thai, COUNT(*) AS cnt " +
            "FROM diem_danh d JOIN sinh_vien s ON d.ma_sv = s.ma_sv " +
            "WHERE 1=1 "
        );
        if (maLop != null && !maLop.isEmpty() && !"ALL".equals(maLop)) {
            sql.append("AND s.ma_lop = ? ");
        }
        sql.append("GROUP BY d.trang_thai");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (maLop != null && !maLop.isEmpty() && !"ALL".equals(maLop)) {
                ps.setString(1, maLop);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tt = rs.getString("trang_thai");
                    int cnt = rs.getInt("cnt");
                    if (tt != null && stats.containsKey(tt)) {
                        stats.put(tt, cnt);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
