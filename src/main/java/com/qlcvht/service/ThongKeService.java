package com.qlcvht.service;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.CounselingProgressItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

            // Thống kê Phân tầng Tier
            String whereSvBase = condSv.isEmpty() ? "" : condSv;
            query(stmt, stats, "tier_1", "SELECT COUNT(DISTINCT s.ma_sv) FROM sinh_vien s JOIN ket_qua_hoc_tap k ON s.ma_sv = k.ma_sv " +
                (whereSvBase.isEmpty() ? "WHERE" : whereSvBase + " AND") + " k.gpa_tich_luy >= 3.2");
            query(stmt, stats, "tier_2", "SELECT COUNT(DISTINCT s.ma_sv) FROM sinh_vien s JOIN ket_qua_hoc_tap k ON s.ma_sv = k.ma_sv " +
                (whereSvBase.isEmpty() ? "WHERE" : whereSvBase + " AND") + " k.gpa_tich_luy >= 2.0 AND k.gpa_tich_luy < 3.2");
            query(stmt, stats, "tier_3", "SELECT COUNT(DISTINCT s.ma_sv) FROM sinh_vien s JOIN ket_qua_hoc_tap k ON s.ma_sv = k.ma_sv " +
                (whereSvBase.isEmpty() ? "WHERE" : whereSvBase + " AND") + " (k.gpa_tich_luy < 2.0 OR s.trang_thai LIKE 'CANH_BAO%' OR s.trang_thai = 'BUOC_THOI_HOC')");

            // Thống kê Học lực
            query(stmt, stats, "hl_xuat_sac", "SELECT COUNT(DISTINCT ma_sv) FROM ket_qua_hoc_tap WHERE gpa_tich_luy >= 3.6" + condCb);
            query(stmt, stats, "hl_gioi",     "SELECT COUNT(DISTINCT ma_sv) FROM ket_qua_hoc_tap WHERE gpa_tich_luy >= 3.2 AND gpa_tich_luy < 3.6" + condCb);
            query(stmt, stats, "hl_kha",      "SELECT COUNT(DISTINCT ma_sv) FROM ket_qua_hoc_tap WHERE gpa_tich_luy >= 2.5 AND gpa_tich_luy < 3.2" + condCb);
            query(stmt, stats, "hl_trung_binh","SELECT COUNT(DISTINCT ma_sv) FROM ket_qua_hoc_tap WHERE gpa_tich_luy >= 2.0 AND gpa_tich_luy < 2.5" + condCb);
            query(stmt, stats, "hl_yeu_kem",  "SELECT COUNT(DISTINCT ma_sv) FROM ket_qua_hoc_tap WHERE gpa_tich_luy < 2.0" + condCb);

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

    /**
     * Lay thong ke tien do tu van va phan percent cai thien diem so.
     */
    public Map<String, Object> getThongKeTienDoTuVan(String maLop) {
        Map<String, Object> res = new HashMap<>();
        List<CounselingProgressItem> items = getChiTietTienDoTuVan(maLop);

        int tongSvCanhBao = 0;
        int svDaTuVan = 0;
        int svCaiThienDiem = 0;

        String sqlCanhBaoCount = "SELECT COUNT(DISTINCT cb.ma_sv) FROM canh_bao_hoc_vu cb " +
                                 "JOIN sinh_vien s ON cb.ma_sv = s.ma_sv ";
        if (maLop != null && !maLop.isEmpty() && !"ALL".equals(maLop)) {
            sqlCanhBaoCount += "WHERE s.ma_lop = '" + maLop.replace("'", "''") + "'";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCanhBaoCount)) {
            if (rs.next()) tongSvCanhBao = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        svDaTuVan = items.size();
        for (CounselingProgressItem item : items) {
            if (item.getTrangThaiCaiThien() != null && item.getTrangThaiCaiThien().contains("Đã cải thiện")) {
                svCaiThienDiem++;
            }
        }

        double percentDaTuVan = tongSvCanhBao > 0 ? (svDaTuVan * 100.0 / tongSvCanhBao) : 0.0;
        double percentCaiThien = svDaTuVan > 0 ? (svCaiThienDiem * 100.0 / svDaTuVan) : 0.0;

        res.put("tongSvCanhBao", tongSvCanhBao);
        res.put("svDaTuVan", svDaTuVan);
        res.put("svCaiThienDiem", svCaiThienDiem);
        res.put("percentDaTuVan", Math.round(percentDaTuVan * 10.0) / 10.0);
        res.put("percentCaiThien", Math.round(percentCaiThien * 10.0) / 10.0);
        res.put("items", items);

        return res;
    }

    /**
     * Lay danh sach chi tiet tien do tu van tung SV.
     */
    public List<CounselingProgressItem> getChiTietTienDoTuVan(String maLop) {
        List<CounselingProgressItem> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT nk.ma_sv, s.ho_ten, s.ma_lop, cb.muc_canh_bao, cb.gpa_xet_duyet, nk.ngay_tu_van " +
            "FROM nhat_ky_tu_van nk " +
            "JOIN sinh_vien s ON nk.ma_sv = s.ma_sv " +
            "LEFT JOIN canh_bao_hoc_vu cb ON nk.id_canh_bao = cb.id OR nk.ma_sv = cb.ma_sv " +
            "WHERE 1=1 "
        );

        if (maLop != null && !maLop.isEmpty() && !"ALL".equals(maLop)) {
            sql.append("AND s.ma_lop = '").append(maLop.replace("'", "''")).append("' ");
        }
        sql.append("GROUP BY nk.ma_sv ORDER BY nk.ngay_tu_van DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {

            while (rs.next()) {
                String maSv = rs.getString("ma_sv");
                String hoTen = rs.getString("ho_ten");
                String lop = rs.getString("ma_lop");
                String mucCb = rs.getString("muc_canh_bao");
                double gpaTruoc = rs.getDouble("gpa_xet_duyet");
                Date ngayTv = rs.getDate("ngay_tu_van");

                // Tim GPA hoc ky sau ngay tu van
                double gpaSau = getGpaSauTuVan(conn, maSv, ngayTv);
                String trangThaiCaiThien;

                if (gpaSau <= 0) {
                    trangThaiCaiThien = "Đang theo dõi (Chưa có GPA kỳ mới)";
                    gpaSau = gpaTruoc;
                } else if (gpaSau > gpaTruoc) {
                    double diff = Math.round((gpaSau - gpaTruoc) * 100.0) / 100.0;
                    trangThaiCaiThien = String.format("Đã cải thiện (+%.2f)", diff);
                } else {
                    double diff = Math.round((gpaSau - gpaTruoc) * 100.0) / 100.0;
                    trangThaiCaiThien = String.format("Chưa cải thiện (%.2f)", diff);
                }

                list.add(new CounselingProgressItem(maSv, hoTen, lop, mucCb != null ? mucCb : "MUC_1", ngayTv, gpaTruoc, gpaSau, trangThaiCaiThien));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private double getGpaSauTuVan(Connection conn, String maSv, Date ngayTuVan) {
        // Query GPA hoc ky moi nhat
        String sql = "SELECT gpa_hoc_ky FROM ket_qua_hoc_tap WHERE ma_sv = ? ORDER BY nam_hoc DESC, hoc_ky DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("gpa_hoc_ky");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}