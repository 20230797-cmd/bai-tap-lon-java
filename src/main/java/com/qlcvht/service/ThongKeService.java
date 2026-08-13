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
        return getThongKeTongQuan("ALL");
    }

    public Map<String, Integer> getThongKeTongQuan(String maLop) {
        Map<String, Integer> stats = new HashMap<>();
        
        String condSv = "";
        String condCb = "";
        
        if (maLop != null && !maLop.isEmpty() && !maLop.equals("ALL")) {
            // Note: simple concatenation since maLop is controlled via combobox
            condSv = " WHERE ma_lop = '" + maLop.replace("'", "''") + "'";
            condCb = " AND ma_sv IN (SELECT ma_sv FROM sinh_vien WHERE ma_lop = '" + maLop.replace("'", "''") + "')";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            query(stmt, stats, "tong_sv",        "SELECT COUNT(*) FROM sinh_vien" + condSv);
            
            String whereSv = condSv.isEmpty() ? " WHERE " : condSv + " AND ";
            query(stmt, stats, "sv_binh_thuong", "SELECT COUNT(*) FROM sinh_vien" + whereSv + "trang_thai='DANG_HOC'");
            
            String whereCb = condCb.isEmpty() ? " WHERE " : " WHERE 1=1 " + condCb + " AND ";
            query(stmt, stats, "cb_muc_1",       "SELECT COUNT(*) FROM canh_bao_hoc_vu" + whereCb + "muc_canh_bao='MUC_1'");
            query(stmt, stats, "cb_muc_2",       "SELECT COUNT(*) FROM canh_bao_hoc_vu" + whereCb + "muc_canh_bao='MUC_2'");
            query(stmt, stats, "buoc_thoi_hoc",  "SELECT COUNT(*) FROM canh_bao_hoc_vu" + whereCb + "muc_canh_bao='BUOC_THOI_HOC'");
            query(stmt, stats, "da_tu_van",      "SELECT COUNT(*) FROM canh_bao_hoc_vu" + whereCb + "trang_thai_tu_van='DA_TU_VAN'");
            query(stmt, stats, "chua_tu_van",    "SELECT COUNT(*) FROM canh_bao_hoc_vu" + whereCb + "trang_thai_tu_van='CHUA_TU_VAN'");
            query(stmt, stats, "dang_theo_doi",  "SELECT COUNT(*) FROM canh_bao_hoc_vu" + whereCb + "trang_thai_tu_van='DANG_THEO_DOI'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    private void query(Statement stmt, Map<String, Integer> map, String key, String sql) {
        try (ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) map.put(key, rs.getInt(1));
        } catch (SQLException e) {
            map.put(key, 0);
        }
    }
}