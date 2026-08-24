package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.BaiTap;
import com.qlcvht.model.NopBaiTap;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BaiTapDAO {

    public List<BaiTap> getAll(String maLopFilter) {
        List<BaiTap> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT b.*, l.ten_lop, " +
            "(SELECT COUNT(*) FROM nop_bai_tap WHERE id_bai_tap = b.id) AS so_bai_da_nop, " +
            "(SELECT COUNT(*) FROM sinh_vien WHERE ma_lop = b.ma_lop) AS tong_so_sv " +
            "FROM bai_tap b LEFT JOIN lop_hoc l ON b.ma_lop = l.ma_lop WHERE 1=1 "
        );

        if (maLopFilter != null && !maLopFilter.isEmpty() && !"ALL".equals(maLopFilter)) {
            sql.append("AND b.ma_lop = ? ");
        }
        sql.append("ORDER BY b.han_nop ASC, b.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (maLopFilter != null && !maLopFilter.isEmpty() && !"ALL".equals(maLopFilter)) {
                ps.setString(1, maLopFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BaiTap b = new BaiTap();
                    b.setId(rs.getInt("id"));
                    b.setMaLop(rs.getString("ma_lop"));
                    b.setTenLop(rs.getString("ten_lop"));
                    b.setTieuDe(rs.getString("tieu_de"));
                    b.setLoaiDanhGia(rs.getString("loai_danh_gia"));
                    b.setTrongSo(rs.getDouble("trong_so"));
                    Date d = rs.getDate("han_nop");
                    if (d != null) {
                        b.setHanNop(d.toLocalDate());
                    }
                    b.setMoTa(rs.getString("mo_ta"));
                    b.setDinhDangChoPhep(rs.getString("dinh_dang_cho_phep"));
                    b.setTrangThai(rs.getString("trang_thai"));
                    b.setSoBaiDaNop(rs.getInt("so_bai_da_nop"));
                    b.setTongSoSinhVien(rs.getInt("tong_so_sv"));
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(BaiTap b) {
        String sql = "INSERT INTO bai_tap (ma_lop, tieu_de, loai_danh_gia, trong_so, han_nop, mo_ta, dinh_dang_cho_phep, trang_thai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getMaLop());
            ps.setString(2, b.getTieuDe());
            ps.setString(3, b.getLoaiDanhGia() != null ? b.getLoaiDanhGia() : "TMA");
            ps.setDouble(4, b.getTrongSo());
            ps.setString(5, b.getHanNop() != null ? b.getHanNop().toString() : LocalDate.now().toString());
            ps.setString(6, b.getMoTa());
            ps.setString(7, b.getDinhDangChoPhep() != null ? b.getDinhDangChoPhep() : "pdf, docx");
            ps.setString(8, b.getTrangThai() != null ? b.getTrangThai() : "OPEN");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(BaiTap b) {
        String sql = "UPDATE bai_tap SET ma_lop = ?, tieu_de = ?, loai_danh_gia = ?, trong_so = ?, han_nop = ?, mo_ta = ?, dinh_dang_cho_phep = ?, trang_thai = ? " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getMaLop());
            ps.setString(2, b.getTieuDe());
            ps.setString(3, b.getLoaiDanhGia());
            ps.setDouble(4, b.getTrongSo());
            ps.setString(5, b.getHanNop() != null ? b.getHanNop().toString() : LocalDate.now().toString());
            ps.setString(6, b.getMoTa());
            ps.setString(7, b.getDinhDangChoPhep());
            ps.setString(8, b.getTrangThai());
            ps.setInt(9, b.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM bai_tap WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<NopBaiTap> getSubmissionsByAssignment(int idBaiTap) {
        List<NopBaiTap> list = new ArrayList<>();
        String sql = "SELECT n.*, s.ho_ten, s.ma_lop " +
                     "FROM nop_bai_tap n JOIN sinh_vien s ON n.ma_sv = s.ma_sv " +
                     "WHERE n.id_bai_tap = ? ORDER BY n.ngay_nop DESC, s.ma_sv ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBaiTap);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NopBaiTap n = new NopBaiTap();
                    n.setId(rs.getInt("id"));
                    n.setIdBaiTap(rs.getInt("id_bai_tap"));
                    n.setMaSv(rs.getString("ma_sv"));
                    n.setHoTen(rs.getString("ho_ten"));
                    n.setMaLop(rs.getString("ma_lop"));
                    String ngayStr = rs.getString("ngay_nop");
                    if (ngayStr != null) {
                        try {
                            n.setNgayNop(LocalDateTime.parse(ngayStr.replace(" ", "T")));
                        } catch (Exception ex) {
                            n.setNgayNop(LocalDateTime.now());
                        }
                    }
                    n.setFileDinhKem(rs.getString("file_dinh_kem"));
                    double diem = rs.getDouble("diem_so");
                    if (!rs.wasNull()) {
                        n.setDiemSo(diem);
                    }
                    n.setNhanXet(rs.getString("nhan_xet"));
                    n.setTrangThai(rs.getString("trang_thai"));
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NopBaiTap> getSubmissionsByStudent(String maSv) {
        List<NopBaiTap> list = new ArrayList<>();
        String sql = "SELECT n.*, b.tieu_de, b.loai_danh_gia, b.trong_so " +
                     "FROM nop_bai_tap n JOIN bai_tap b ON n.id_bai_tap = b.id " +
                     "WHERE n.ma_sv = ? ORDER BY n.ngay_nop DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NopBaiTap n = new NopBaiTap();
                    n.setId(rs.getInt("id"));
                    n.setIdBaiTap(rs.getInt("id_bai_tap"));
                    n.setMaSv(rs.getString("ma_sv"));
                    n.setFileDinhKem(rs.getString("file_dinh_kem"));
                    double diem = rs.getDouble("diem_so");
                    if (!rs.wasNull()) {
                        n.setDiemSo(diem);
                    }
                    n.setNhanXet(rs.getString("nhan_xet"));
                    n.setTrangThai(rs.getString("trang_thai"));
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean gradeSubmission(int submissionId, double score, String feedback) {
        String sql = "UPDATE nop_bai_tap SET diem_so = ?, nhan_xet = ?, trang_thai = 'GRADED' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, score);
            ps.setString(2, feedback);
            ps.setInt(3, submissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSubmission(int idBaiTap, String maSv, String fileName) {
        String sql = "INSERT INTO nop_bai_tap (id_bai_tap, ma_sv, ngay_nop, file_dinh_kem, trang_thai) " +
                     "VALUES (?, ?, datetime('now'), ?, 'SUBMITTED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBaiTap);
            ps.setString(2, maSv);
            ps.setString(3, fileName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
