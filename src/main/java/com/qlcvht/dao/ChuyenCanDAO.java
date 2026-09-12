package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.ChuyenCanMonHoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChuyenCanDAO {

    public List<ChuyenCanMonHoc> getAllChuyenCan(String maLop, String maMon, String trangThaiFilter, String keyword) {
        List<ChuyenCanMonHoc> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, s.ho_ten, m.ten_mon, m.so_tin_chi, m.so_buoi_toi_da_vang " +
            "FROM chuyen_can_mon_hoc c " +
            "JOIN sinh_vien s ON c.ma_sv = s.ma_sv " +
            "JOIN mon_hoc m ON c.ma_mon = m.ma_mon " +
            "WHERE 1=1 "
        );

        if (maLop != null && !maLop.isEmpty() && !"ALL".equalsIgnoreCase(maLop)) {
            sql.append("AND c.ma_lop = ? ");
        }
        if (maMon != null && !maMon.isEmpty() && !"ALL".equalsIgnoreCase(maMon)) {
            sql.append("AND c.ma_mon = ? ");
        }
        if (trangThaiFilter != null && !trangThaiFilter.isEmpty() && !"ALL".equalsIgnoreCase(trangThaiFilter)) {
            sql.append("AND c.trang_thai_du_thi = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (c.ma_sv LIKE ? OR s.ho_ten LIKE ?) ");
        }
        sql.append("ORDER BY c.trang_thai_du_thi DESC, c.ma_lop, c.ma_sv");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIdx = 1;
            if (maLop != null && !maLop.isEmpty() && !"ALL".equalsIgnoreCase(maLop)) {
                ps.setString(paramIdx++, maLop);
            }
            if (maMon != null && !maMon.isEmpty() && !"ALL".equalsIgnoreCase(maMon)) {
                ps.setString(paramIdx++, maMon);
            }
            if (trangThaiFilter != null && !trangThaiFilter.isEmpty() && !"ALL".equalsIgnoreCase(trangThaiFilter)) {
                ps.setString(paramIdx++, trangThaiFilter);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(paramIdx++, kw);
                ps.setString(paramIdx++, kw);
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

    public List<ChuyenCanMonHoc> getAll() {
        return getAllChuyenCan(null, null, "ALL", null);
    }

    public List<ChuyenCanMonHoc> getByMaSv(String maSv) {
        return getChuyenCanBySinhVien(maSv);
    }

    public List<ChuyenCanMonHoc> getByAdvisor(String maCvht) {
        List<ChuyenCanMonHoc> list = new ArrayList<>();
        String sql = "SELECT c.*, s.ho_ten, m.ten_mon, m.so_tin_chi, m.so_buoi_toi_da_vang " +
                     "FROM chuyen_can_mon_hoc c " +
                     "JOIN sinh_vien s ON c.ma_sv = s.ma_sv " +
                     "JOIN mon_hoc m ON c.ma_mon = m.ma_mon " +
                     "JOIN lop_hoc l ON c.ma_lop = l.ma_lop " +
                     "WHERE l.ma_cvht = ? " +
                     "ORDER BY c.trang_thai_du_thi DESC, c.ma_lop, c.ma_sv";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCvht);
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

    public List<ChuyenCanMonHoc> getChuyenCanBySinhVien(String maSv) {
        List<ChuyenCanMonHoc> list = new ArrayList<>();
        String sql = "SELECT c.*, s.ho_ten, m.ten_mon, m.so_tin_chi, m.so_buoi_toi_da_vang " +
                     "FROM chuyen_can_mon_hoc c " +
                     "JOIN sinh_vien s ON c.ma_sv = s.ma_sv " +
                     "JOIN mon_hoc m ON c.ma_mon = m.ma_mon " +
                     "WHERE c.ma_sv = ? " +
                     "ORDER BY c.hoc_ky DESC, c.ma_mon ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
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

    public List<ChuyenCanMonHoc> getDanhSachCamThi(String maLop, String maMon) {
        return getAllChuyenCan(maLop, maMon, "CAM_THI", null);
    }

    public List<ChuyenCanMonHoc> getDanhSachCamThi() {
        return getAllChuyenCan(null, null, "CAM_THI", null);
    }

    public boolean updateChuyenCan(ChuyenCanMonHoc cc) {
        String sql = "UPDATE chuyen_can_mon_hoc SET " +
                     "so_buoi_co_mat = ?, so_buoi_muon = ?, so_buoi_vang_co_phep = ?, so_buoi_vang_khong_phep = ?, " +
                     "tong_buoi_vang_quy_doi = ?, ty_le_vang = ?, diem_chuyen_can = ?, trang_thai_du_thi = ?, ly_do_cam_thi = ? " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cc.getSoBuoiCoMat());
            ps.setInt(2, cc.getSoBuoiMuon());
            ps.setInt(3, cc.getSoBuoiVangCoPhep());
            ps.setInt(4, cc.getSoBuoiVangKhongPhep());
            ps.setDouble(5, cc.getTongBuoiVangQuyDoi());
            ps.setDouble(6, cc.getTyLeVang());
            ps.setDouble(7, cc.getDiemChuyenCan());
            ps.setString(8, cc.getTrangThaiDuThi());
            ps.setString(9, cc.getLyDoCamThi());
            ps.setInt(10, cc.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ChuyenCanMonHoc mapResultSet(ResultSet rs) throws SQLException {
        ChuyenCanMonHoc c = new ChuyenCanMonHoc();
        c.setId(rs.getInt("id"));
        c.setMaSv(rs.getString("ma_sv"));
        c.setHoTen(rs.getString("ho_ten"));
        c.setMaLop(rs.getString("ma_lop"));
        c.setMaMon(rs.getString("ma_mon"));
        c.setTenMon(rs.getString("ten_mon"));
        c.setSoTinChi(rs.getInt("so_tin_chi"));
        c.setHocKy(rs.getInt("hoc_ky"));
        c.setNamHoc(rs.getString("nam_hoc"));
        c.setTongSoBuoi(rs.getInt("tong_so_buoi"));
        c.setSoBuoiToiDaChoPhepVang(rs.getInt("so_buoi_toi_da_vang"));
        c.setSoBuoiCoMat(rs.getInt("so_buoi_co_mat"));
        c.setSoBuoiMuon(rs.getInt("so_buoi_muon"));
        c.setSoBuoiVangCoPhep(rs.getInt("so_buoi_vang_co_phep"));
        c.setSoBuoiVangKhongPhep(rs.getInt("so_buoi_vang_khong_phep"));
        c.setTongBuoiVangQuyDoi(rs.getDouble("tong_buoi_vang_quy_doi"));
        c.setTyLeVang(rs.getDouble("ty_le_vang"));
        c.setDiemChuyenCan(rs.getDouble("diem_chuyen_can"));
        c.setTrangThaiDuThi(rs.getString("trang_thai_du_thi"));
        c.setLyDoCamThi(rs.getString("ly_do_cam_thi"));
        return c;
    }
}
