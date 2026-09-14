package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.AuditLog;
import com.qlcvht.model.UserSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

    public boolean logAction(AuditLog log) {
        String sql = "INSERT INTO nhat_ky_he_thong (ten_dang_nhap, ho_ten, vai_tro, loai_hanh_dong, mo_ta_chi_tiet, dia_chi_ip, thiet_bi, thoi_gian, trang_thai_phien) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getTenDangNhap());
            ps.setString(2, log.getHoTen());
            ps.setString(3, log.getVaiTro());
            ps.setString(4, log.getLoaiHanhDong());
            ps.setString(5, log.getMoTaChiTiet());
            ps.setString(6, log.getDiaChiIp() != null ? log.getDiaChiIp() : "127.0.0.1 (Localhost)");
            ps.setString(7, log.getThietBi() != null ? log.getThietBi() : "Java Swing Desktop");
            ps.setTimestamp(8, log.getThoiGian() != null ? log.getThoiGian() : new Timestamp(System.currentTimeMillis()));
            ps.setString(9, log.getTrangThaiPhien() != null ? log.getTrangThaiPhien() : "ONLINE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean recordLogin(String tenDangNhap, String hoTen, String vaiTro, String ip, String thietBi) {
        // 1. Cập nhật phiên đăng nhập (nếu đã có thì chuyển thành ONLINE và cập nhật thời gian đăng nhập mới nhất, nếu chưa thì INSERT)
        String sessionCheckSql = "SELECT id FROM phien_dang_nhap WHERE ten_dang_nhap = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement psCheck = conn.prepareStatement(sessionCheckSql)) {
                psCheck.setString(1, tenDangNhap);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) exists = true;
                }
            }

            if (exists) {
                String updateSql = "UPDATE phien_dang_nhap SET ho_ten = ?, vai_tro = ?, thoi_gian_dang_nhap = CURRENT_TIMESTAMP, " +
                                   "thoi_gian_dang_xuat = NULL, trang_thai = 'ONLINE', dia_chi_ip = ?, thiet_bi = ? WHERE ten_dang_nhap = ?";
                try (PreparedStatement psUp = conn.prepareStatement(updateSql)) {
                    psUp.setString(1, hoTen);
                    psUp.setString(2, vaiTro);
                    psUp.setString(3, ip != null ? ip : "127.0.0.1");
                    psUp.setString(4, thietBi != null ? thietBi : "Java Swing Desktop");
                    psUp.setString(5, tenDangNhap);
                    psUp.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO phien_dang_nhap (ten_dang_nhap, ho_ten, vai_tro, thoi_gian_dang_nhap, trang_thai, dia_chi_ip, thiet_bi) " +
                                   "VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'ONLINE', ?, ?)";
                try (PreparedStatement psIn = conn.prepareStatement(insertSql)) {
                    psIn.setString(1, tenDangNhap);
                    psIn.setString(2, hoTen);
                    psIn.setString(3, vaiTro);
                    psIn.setString(4, ip != null ? ip : "127.0.0.1");
                    psIn.setString(5, thietBi != null ? thietBi : "Java Swing Desktop");
                    psIn.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Ghi nhật ký Audit Log
        AuditLog log = new AuditLog(tenDangNhap, hoTen, vaiTro, "DANG_NHAP", "Đăng nhập thành công vào hệ thống");
        log.setDiaChiIp(ip);
        log.setThietBi(thietBi);
        return logAction(log);
    }

    public boolean recordLogout(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isBlank()) return false;

        String hoTen = tenDangNhap;
        String vaiTro = "UNKNOWN";

        // Lấy thông tin phiên trước khi update
        String getSql = "SELECT ho_ten, vai_tro FROM phien_dang_nhap WHERE ten_dang_nhap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(getSql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hoTen = rs.getString("ho_ten");
                    vaiTro = rs.getString("vai_tro");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 1. Cập nhật phiên thành OFFLINE
        String updateSql = "UPDATE phien_dang_nhap SET trang_thai = 'OFFLINE', thoi_gian_dang_xuat = CURRENT_TIMESTAMP WHERE ten_dang_nhap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, tenDangNhap);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Ghi nhật ký Audit Log
        AuditLog log = new AuditLog(tenDangNhap, hoTen, vaiTro, "DANG_XUAT", "Đăng xuất khỏi hệ thống thành công");
        log.setTrangThaiPhien("OFFLINE");
        return logAction(log);
    }

    public List<AuditLog> getAllLogs() {
        return getLogsByFilter(null, null, null, null);
    }

    public List<AuditLog> getLogsByFilter(String keyword, String loaiHanhDong, String tuNgay, String denNgay) {
        List<AuditLog> list = new ArrayList<>();
        boolean isSqlServer = DatabaseConnection.isUsingSQLServer();
        boolean isSqlite = DatabaseConnection.isUsingSQLite();

        StringBuilder sql = new StringBuilder();
        if (isSqlServer) {
            sql.append("SELECT TOP 500 * FROM nhat_ky_he_thong WHERE 1=1 ");
        } else {
            sql.append("SELECT * FROM nhat_ky_he_thong WHERE 1=1 ");
        }

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (ten_dang_nhap LIKE ? OR ho_ten LIKE ? OR mo_ta_chi_tiet LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (loaiHanhDong != null && !loaiHanhDong.isEmpty() && !"ALL".equalsIgnoreCase(loaiHanhDong)) {
            sql.append("AND loai_hanh_dong = ? ");
            params.add(loaiHanhDong);
        }

        if (tuNgay != null && !tuNgay.trim().isEmpty()) {
            if (isSqlServer) {
                sql.append("AND CAST(thoi_gian AS DATE) >= ? ");
            } else {
                sql.append("AND DATE(thoi_gian) >= ? ");
            }
            params.add(tuNgay.trim());
        }

        if (denNgay != null && !denNgay.trim().isEmpty()) {
            if (isSqlServer) {
                sql.append("AND CAST(thoi_gian AS DATE) <= ? ");
            } else {
                sql.append("AND DATE(thoi_gian) <= ? ");
            }
            params.add(denNgay.trim());
        }

        if (isSqlServer) {
            sql.append("ORDER BY id DESC");
        } else {
            sql.append("ORDER BY id DESC LIMIT 500");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setId(rs.getInt("id"));
                    log.setTenDangNhap(rs.getString("ten_dang_nhap"));
                    log.setHoTen(rs.getString("ho_ten"));
                    log.setVaiTro(rs.getString("vai_tro"));
                    log.setLoaiHanhDong(rs.getString("loai_hanh_dong"));
                    log.setMoTaChiTiet(rs.getString("mo_ta_chi_tiet"));
                    log.setDiaChiIp(rs.getString("dia_chi_ip"));
                    log.setThietBi(rs.getString("thiet_bi"));
                    log.setThoiGian(parseTimestampSafely(rs.getString("thoi_gian")));
                    log.setTrangThaiPhien(rs.getString("trang_thai_phien"));
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR AuditDAO.getLogsByFilter] " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<UserSession> getAllSessions() {
        List<UserSession> list = new ArrayList<>();
        String sql = "SELECT * FROM phien_dang_nhap ORDER BY CASE WHEN trang_thai = 'ONLINE' THEN 0 ELSE 1 END, thoi_gian_dang_nhap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserSession s = new UserSession();
                s.setId(rs.getInt("id"));
                s.setTenDangNhap(rs.getString("ten_dang_nhap"));
                s.setHoTen(rs.getString("ho_ten"));
                s.setVaiTro(rs.getString("vai_tro"));
                s.setThoiGianDangNhap(parseTimestampSafely(rs.getString("thoi_gian_dang_nhap")));
                s.setThoiGianDangXuat(parseTimestampSafely(rs.getString("thoi_gian_dang_xuat")));
                s.setTrangThai(rs.getString("trang_thai"));
                s.setDiaChiIp(rs.getString("dia_chi_ip"));
                s.setThietBi(rs.getString("thiet_bi"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR AuditDAO.getAllSessions] " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<UserSession> getActiveSessions() {
        List<UserSession> list = new ArrayList<>();
        String sql = "SELECT * FROM phien_dang_nhap WHERE trang_thai = 'ONLINE' ORDER BY thoi_gian_dang_nhap DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserSession s = new UserSession();
                s.setId(rs.getInt("id"));
                s.setTenDangNhap(rs.getString("ten_dang_nhap"));
                s.setHoTen(rs.getString("ho_ten"));
                s.setVaiTro(rs.getString("vai_tro"));
                s.setThoiGianDangNhap(parseTimestampSafely(rs.getString("thoi_gian_dang_nhap")));
                s.setThoiGianDangXuat(parseTimestampSafely(rs.getString("thoi_gian_dang_xuat")));
                s.setTrangThai(rs.getString("trang_thai"));
                s.setDiaChiIp(rs.getString("dia_chi_ip"));
                s.setThietBi(rs.getString("thiet_bi"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR AuditDAO.getActiveSessions] " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public int countOnlineUsers() {
        String sql = "SELECT COUNT(*) FROM phien_dang_nhap WHERE trang_thai = 'ONLINE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ERROR AuditDAO.countOnlineUsers] " + e.getMessage());
        }
        return 0;
    }

    public int countLoginsToday() {
        boolean isSqlServer = DatabaseConnection.isUsingSQLServer();
        String sql;
        if (isSqlServer) {
            sql = "SELECT COUNT(*) FROM nhat_ky_he_thong WHERE loai_hanh_dong = 'DANG_NHAP' AND CAST(thoi_gian AS DATE) = CAST(GETDATE() AS DATE)";
        } else {
            sql = "SELECT COUNT(*) FROM nhat_ky_he_thong WHERE loai_hanh_dong = 'DANG_NHAP' AND DATE(thoi_gian) = DATE('now')";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            // Dự phòng nếu lỗi hàm date
            try (Connection conn2 = DatabaseConnection.getConnection();
                 PreparedStatement ps2 = conn2.prepareStatement("SELECT COUNT(*) FROM nhat_ky_he_thong WHERE loai_hanh_dong = 'DANG_NHAP'");
                 ResultSet rs2 = ps2.executeQuery()) {
                if (rs2.next()) return rs2.getInt(1);
            } catch (SQLException ignored) {}
        }
        return 0;
    }

    public boolean forceLogout(String tenDangNhap) {
        recordLogout(tenDangNhap);
        AuditLog log = new AuditLog("ADMIN", "Quản trị viên", "ADMIN", "DANG_XUAT", "Quản trị viên đã buộc đăng xuất phiên làm việc của tài khoản " + tenDangNhap);
        logAction(log);
        return true;
    }

    public boolean clearOldLogs(int days) {
        boolean isSqlServer = DatabaseConnection.isUsingSQLServer();
        String sql;
        if (isSqlServer) {
            sql = "DELETE FROM nhat_ky_he_thong WHERE thoi_gian < DATEADD(day, -?, GETDATE())";
        } else {
            sql = "DELETE FROM nhat_ky_he_thong WHERE DATE(thoi_gian) < DATE('now', '-' || ? || ' days')";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Timestamp parseTimestampSafely(String str) {
        if (str == null || str.isBlank()) return null;
        try {
            return Timestamp.valueOf(str);
        } catch (Exception e) {
            try {
                if (str.length() >= 19) {
                    return Timestamp.valueOf(str.substring(0, 19).replace('T', ' '));
                }
            } catch (Exception ex) {}
            return new Timestamp(System.currentTimeMillis());
        }
    }
}
