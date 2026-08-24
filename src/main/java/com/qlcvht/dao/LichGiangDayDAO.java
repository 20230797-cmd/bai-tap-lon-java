package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.LichGiangDay;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LichGiangDayDAO {

    public List<LichGiangDay> getAll() {
        List<LichGiangDay> list = new ArrayList<>();
        String sql = "SELECT * FROM lich_giang_day ORDER BY ngay ASC, gio_bat_dau ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<LichGiangDay> getByFilter(String maLop, String maCvht, LocalDate tuNgay, LocalDate denNgay, String trangThai) {
        List<LichGiangDay> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM lich_giang_day WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (maLop != null && !maLop.isEmpty() && !"ALL".equals(maLop)) {
            sql.append("AND ma_lop = ? ");
            params.add(maLop);
        }
        if (maCvht != null && !maCvht.isEmpty() && !"ALL".equals(maCvht)) {
            sql.append("AND ma_cvht = ? ");
            params.add(maCvht);
        }
        if (tuNgay != null) {
            sql.append("AND ngay >= ? ");
            params.add(tuNgay.toString());
        }
        if (denNgay != null) {
            sql.append("AND ngay <= ? ");
            params.add(denNgay.toString());
        }
        if (trangThai != null && !trangThai.isEmpty() && !"ALL".equals(trangThai)) {
            sql.append("AND trang_thai = ? ");
            params.add(trangThai);
        }

        sql.append("ORDER BY ngay ASC, gio_bat_dau ASC");

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

    public boolean insert(LichGiangDay lich) {
        String sql = "INSERT INTO lich_giang_day (ma_cvht, ten_cvht, ma_lop, ten_lop, tieu_de, ngay, gio_bat_dau, gio_ket_thuc, dia_diem, hinh_thuc, loai_buoi, trang_thai, ghi_chu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lich.getMaCvht());
            ps.setString(2, lich.getTenCvht());
            ps.setString(3, lich.getMaLop());
            ps.setString(4, lich.getTenLop());
            ps.setString(5, lich.getTieuDe());
            ps.setString(6, lich.getNgay() != null ? lich.getNgay().toString() : LocalDate.now().toString());
            ps.setString(7, lich.getGioBatDau());
            ps.setString(8, lich.getGioKetThuc());
            ps.setString(9, lich.getDiaDiem());
            ps.setString(10, lich.getHinhThuc() != null ? lich.getHinhThuc() : "TRUC_TIEP");
            ps.setString(11, lich.getLoaiBuoi() != null ? lich.getLoaiBuoi() : "GIANG_DAY");
            ps.setString(12, lich.getTrangThai() != null ? lich.getTrangThai() : "SCHEDULED");
            ps.setString(13, lich.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(LichGiangDay lich) {
        String sql = "UPDATE lich_giang_day SET ma_cvht = ?, ten_cvht = ?, ma_lop = ?, ten_lop = ?, tieu_de = ?, ngay = ?, " +
                     "gio_bat_dau = ?, gio_ket_thuc = ?, dia_diem = ?, hinh_thuc = ?, loai_buoi = ?, trang_thai = ?, ghi_chu = ? " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lich.getMaCvht());
            ps.setString(2, lich.getTenCvht());
            ps.setString(3, lich.getMaLop());
            ps.setString(4, lich.getTenLop());
            ps.setString(5, lich.getTieuDe());
            ps.setString(6, lich.getNgay() != null ? lich.getNgay().toString() : LocalDate.now().toString());
            ps.setString(7, lich.getGioBatDau());
            ps.setString(8, lich.getGioKetThuc());
            ps.setString(9, lich.getDiaDiem());
            ps.setString(10, lich.getHinhThuc());
            ps.setString(11, lich.getLoaiBuoi());
            ps.setString(12, lich.getTrangThai());
            ps.setString(13, lich.getGhiChu());
            ps.setInt(14, lich.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM lich_giang_day WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkConflict(String maCvht, LocalDate ngay, String gioBatDau, String gioKetThuc, int excludeId) {
        String sql = "SELECT COUNT(*) FROM lich_giang_day WHERE ma_cvht = ? AND ngay = ? AND id != ? " +
                     "AND ((gio_bat_dau <= ? AND gio_ket_thuc > ?) OR (gio_bat_dau < ? AND gio_ket_thuc >= ?) OR (gio_bat_dau >= ? AND gio_ket_thuc <= ?))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCvht);
            ps.setString(2, ngay.toString());
            ps.setInt(3, excludeId);
            ps.setString(4, gioBatDau);
            ps.setString(5, gioBatDau);
            ps.setString(6, gioKetThuc);
            ps.setString(7, gioKetThuc);
            ps.setString(8, gioBatDau);
            ps.setString(9, gioKetThuc);
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

    private LichGiangDay mapResultSet(ResultSet rs) throws SQLException {
        LichGiangDay l = new LichGiangDay();
        l.setId(rs.getInt("id"));
        l.setMaCvht(rs.getString("ma_cvht"));
        l.setTenCvht(rs.getString("ten_cvht"));
        l.setMaLop(rs.getString("ma_lop"));
        l.setTenLop(rs.getString("ten_lop"));
        l.setTieuDe(rs.getString("tieu_de"));
        Date d = rs.getDate("ngay");
        if (d != null) {
            l.setNgay(d.toLocalDate());
        }
        l.setGioBatDau(rs.getString("gio_bat_dau"));
        l.setGioKetThuc(rs.getString("gio_ket_thuc"));
        l.setDiaDiem(rs.getString("dia_diem"));
        l.setHinhThuc(rs.getString("hinh_thuc"));
        l.setLoaiBuoi(rs.getString("loai_buoi"));
        l.setTrangThai(rs.getString("trang_thai"));
        l.setGhiChu(rs.getString("ghi_chu"));
        return l;
    }
}
