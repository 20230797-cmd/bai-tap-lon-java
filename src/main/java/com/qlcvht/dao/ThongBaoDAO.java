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

    public List<ThongBao> getThongBaoForSinhVien(String maSv, String maLop, String tierName) {
        List<ThongBao> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM thong_bao WHERE (ma_sv = ?) " +
            "OR (ma_lop = ? AND (nhom_rui_ro IS NULL OR nhom_rui_ro = 'ALL' OR nhom_rui_ro = 'LOP')) " +
            "OR (ma_lop = 'ALL' AND (ma_sv IS NULL OR ma_sv = '') AND (nhom_rui_ro IS NULL OR nhom_rui_ro = 'ALL')) "
        );
        if (tierName != null && !tierName.isEmpty() && !"ALL".equals(tierName)) {
            sql.append("OR (nhom_rui_ro = ? AND (ma_lop = 'ALL' OR ma_lop = ? OR ma_lop IS NULL)) ");
        }
        sql.append("ORDER BY id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, maSv);
            ps.setString(idx++, (maLop != null) ? maLop : "");
            if (tierName != null && !tierName.isEmpty() && !"ALL".equals(tierName)) {
                ps.setString(idx++, tierName);
                ps.setString(idx++, (maLop != null) ? maLop : "");
            }
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

    public boolean guiPhanHoiChoCoVan(String maSv, String tenSv, String tieuDe, String noiDung) {
        String maTb = "PH-" + System.currentTimeMillis();
        String sql = "INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) " +
                     "VALUES (?, ?, ?, 'PHAN_HOI_SV', 'ALL', ?, CURRENT_TIMESTAMP, ?, 1, 'SV_CHUA_DOC')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTb);
            ps.setString(2, "📬 [SV PHẢN HỒI] " + tieuDe);
            ps.setString(3, noiDung);
            ps.setString(4, maSv);
            ps.setString(5, tenSv + " (" + maSv + ")");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ThongBao> getChatHistory(String maSv) {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT * FROM thong_bao WHERE ma_sv = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
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

    public List<com.qlcvht.model.SinhVien> getDanhSachSinhVienDaGuiTinNhan(String maCvht) {
        List<com.qlcvht.model.SinhVien> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.ma_sv, s.ho_ten, s.ngay_sinh, s.gioi_tinh, s.email, s.so_dien_thoai, s.ma_lop, s.trang_thai, " +
            "MAX(COALESCE(tb.id, 0)) AS latest_msg_id " +
            "FROM sinh_vien s " +
            "LEFT JOIN lop_hoc l ON s.ma_lop = l.ma_lop " +
            "LEFT JOIN thong_bao tb ON s.ma_sv = tb.ma_sv " +
            "WHERE (s.ma_sv IN (SELECT DISTINCT ma_sv FROM thong_bao WHERE ma_sv IS NOT NULL AND ma_sv != '')) "
        );
        if (maCvht != null && !maCvht.trim().isEmpty() && !"ADMIN".equalsIgnoreCase(maCvht)) {
            sql.append("OR l.ma_cvht = ? ");
        }
        sql.append("GROUP BY s.ma_sv, s.ho_ten, s.ngay_sinh, s.gioi_tinh, s.email, s.so_dien_thoai, s.ma_lop, s.trang_thai ");
        sql.append("ORDER BY latest_msg_id DESC, s.ma_sv ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (maCvht != null && !maCvht.trim().isEmpty() && !"ADMIN".equalsIgnoreCase(maCvht)) {
                ps.setString(1, maCvht);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new com.qlcvht.model.SinhVien(
                        rs.getString("ma_sv"),
                        rs.getString("ho_ten"),
                        parseDateSafely(rs.getString("ngay_sinh")),
                        rs.getString("gioi_tinh"),
                        rs.getString("email"),
                        rs.getString("so_dien_thoai"),
                        rs.getString("ma_lop"),
                        rs.getString("trang_thai")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private java.sql.Date parseDateSafely(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            if (dateStr.length() >= 10) {
                return java.sql.Date.valueOf(dateStr.substring(0, 10));
            }
            return java.sql.Date.valueOf(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean traLoiTinNhanSinhVien(String maCvht, String tenCvht, String maSv, String tieuDe, String noiDung) {
        String maTb = "REP-" + System.currentTimeMillis();
        String sql = "INSERT INTO thong_bao (ma_thong_bao, tieu_de, noi_dung, nhom_rui_ro, ma_lop, ma_sv, ngay_gui, nguoi_gui, so_luong_nhan, trang_thai) " +
                     "VALUES (?, ?, ?, 'CVHT_TRA_LOI', 'CA_NHAN', ?, CURRENT_TIMESTAMP, ?, 1, 'CV_CHUA_DOC')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTb);
            ps.setString(2, "💬 [CVHT TRẢ LỜI] " + tieuDe);
            ps.setString(3, noiDung);
            ps.setString(4, maSv);
            ps.setString(5, tenCvht != null ? tenCvht : ("Cố vấn " + maCvht));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUnreadCountForAdvisor(String maSv) {
        String sql = "SELECT COUNT(*) FROM thong_bao WHERE ma_sv = ? AND trang_thai = 'SV_CHUA_DOC'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getUnreadCountForStudent(String maSv) {
        String sql = "SELECT COUNT(*) FROM thong_bao WHERE ma_sv = ? AND (trang_thai = 'CV_CHUA_DOC' OR trang_thai = 'DA_GUI')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void markMessagesAsReadByAdvisor(String maSv) {
        String sql = "UPDATE thong_bao SET trang_thai = 'DA_DOC' WHERE ma_sv = ? AND trang_thai = 'SV_CHUA_DOC'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markMessagesAsReadByStudent(String maSv) {
        String sql = "UPDATE thong_bao SET trang_thai = 'DA_DOC' WHERE ma_sv = ? AND trang_thai = 'CV_CHUA_DOC'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
