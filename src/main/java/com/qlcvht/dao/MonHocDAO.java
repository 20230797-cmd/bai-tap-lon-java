package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.MonHoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MonHocDAO {

    public List<MonHoc> getAllMonHoc() {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM mon_hoc ORDER BY khoa, ma_mon";
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

    public List<MonHoc> getAll() {
        return getAllMonHoc();
    }

    public List<MonHoc> getMonHocByKhoa(String khoa) {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM mon_hoc WHERE khoa = ? ORDER BY ma_mon";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, khoa);
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

    public MonHoc getMonHocById(String maMon) {
        String sql = "SELECT * FROM mon_hoc WHERE ma_mon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MonHoc mapResultSet(ResultSet rs) throws SQLException {
        MonHoc m = new MonHoc();
        m.setMaMon(rs.getString("ma_mon"));
        m.setTenMon(rs.getString("ten_mon"));
        m.setSoTinChi(rs.getInt("so_tin_chi"));
        m.setSoTiet(rs.getInt("so_tiet"));
        m.setSoBuoiToiDaVang(rs.getInt("so_buoi_toi_da_vang"));
        m.setKhoa(rs.getString("khoa"));
        return m;
    }
}
