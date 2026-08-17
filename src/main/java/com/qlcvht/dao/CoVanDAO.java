package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.CoVanHocTap;
import com.qlcvht.model.LopHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoVanDAO {

    public List<CoVanHocTap> getAllCoVan() {
        List<CoVanHocTap> list = new ArrayList<>();
        String sql = "SELECT * FROM co_van_hoc_tap ORDER BY ma_cvht";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CoVanHocTap(
                    rs.getString("ma_cvht"),
                    rs.getString("ho_ten"),
                    rs.getString("email"),
                    rs.getString("so_dien_thoai"),
                    rs.getString("khoa")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<LopHoc> getAllLopHoc() {
        List<LopHoc> list = new ArrayList<>();
        String sql = "SELECT l.*, c.ho_ten AS ten_cvht FROM lop_hoc l LEFT JOIN co_van_hoc_tap c ON l.ma_cvht = c.ma_cvht ORDER BY l.ma_lop";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LopHoc lh = new LopHoc(
                    rs.getString("ma_lop"),
                    rs.getString("ten_lop"),
                    rs.getString("khoa"),
                    rs.getInt("khoa_hoc"),
                    rs.getString("ma_cvht")
                );
                lh.setTenCvht(rs.getString("ten_cvht"));
                list.add(lh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<LopHoc> getLopHocByCoVan(String maCvht) {
        List<LopHoc> list = new ArrayList<>();
        String sql = "SELECT l.*, c.ho_ten AS ten_cvht FROM lop_hoc l LEFT JOIN co_van_hoc_tap c ON l.ma_cvht = c.ma_cvht WHERE l.ma_cvht = ? ORDER BY l.ma_lop";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCvht);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LopHoc lh = new LopHoc(
                        rs.getString("ma_lop"),
                        rs.getString("ten_lop"),
                        rs.getString("khoa"),
                        rs.getInt("khoa_hoc"),
                        rs.getString("ma_cvht")
                    );
                    lh.setTenCvht(rs.getString("ten_cvht"));
                    list.add(lh);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getSoLuongSinhVienTrongLop(String maLop) {
        String sql = "SELECT COUNT(*) FROM sinh_vien WHERE ma_lop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean addLopHoc(LopHoc lop) {
        String sql = "INSERT INTO lop_hoc (ma_lop, ten_lop, khoa, khoa_hoc, ma_cvht) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lop.getMaLop());
            ps.setString(2, lop.getTenLop());
            ps.setString(3, lop.getKhoa());
            ps.setInt(4, lop.getKhoaHoc());
            ps.setString(5, lop.getMaCvht());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLopHoc(LopHoc lop) {
        String sql = "UPDATE lop_hoc SET ten_lop = ?, khoa = ?, khoa_hoc = ?, ma_cvht = ? WHERE ma_lop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lop.getTenLop());
            ps.setString(2, lop.getKhoa());
            ps.setInt(3, lop.getKhoaHoc());
            ps.setString(4, lop.getMaCvht());
            ps.setString(5, lop.getMaLop());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLopHoc(String maLop) {
        String sql = "DELETE FROM lop_hoc WHERE ma_lop = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

