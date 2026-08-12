package com.qlcvht.service;

import com.qlcvht.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ThongKeService {

    public Map<String, Integer> getThongKeTongQuan() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Tổng sinh viên
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM sinh_vien")) {
                if (rs.next()) stats.put("tong_sv", rs.getInt(1));
            }
            // Đang học bình thường
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM sinh_vien WHERE trang_thai = 'DANG_HOC'")) {
                if (rs.next()) stats.put("sv_binh_thuong", rs.getInt(1));
            }
            // Cảnh báo Mức 1
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM canh_bao_hoc_vu WHERE muc_canh_bao = 'MUC_1'")) {
                if (rs.next()) stats.put("cb_muc_1", rs.getInt(1));
            }
            // Cảnh báo Mức 2
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM canh_bao_hoc_vu WHERE muc_canh_bao = 'MUC_2'")) {
                if (rs.next()) stats.put("cb_muc_2", rs.getInt(1));
            }
            // Buộc thôi học
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM canh_bao_hoc_vu WHERE muc_canh_bao = 'BUOC_THOI_HOC'")) {
                if (rs.next()) stats.put("buoc_thoi_hoc", rs.getInt(1));
            }
            // Đã tư vấn
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM canh_bao_hoc_vu WHERE trang_thai_tu_van = 'DA_TU_VAN'")) {
                if (rs.next()) stats.put("da_tu_van", rs.getInt(1));
            }
            // Chưa tư vấn
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM canh_bao_hoc_vu WHERE trang_thai_tu_van = 'CHUA_TU_VAN'")) {
                if (rs.next()) stats.put("chua_tu_van", rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
