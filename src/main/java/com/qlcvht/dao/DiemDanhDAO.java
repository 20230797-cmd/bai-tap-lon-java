package com.qlcvht.dao;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.DiemDanh;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiemDanhDAO {

    private LocalDate parseLocalDate(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            String s = str.trim();
            if (s.length() >= 10) {
                return LocalDate.parse(s.substring(0, 10));
            }
        } catch (Exception ignored) {}
        return null;
    }

    public List<DiemDanh> getBySession(int idLich) {
        List<DiemDanh> list = new ArrayList<>();
        String sql = "SELECT d.*, s.ho_ten, s.ma_lop " +
                     "FROM diem_danh d JOIN sinh_vien s ON d.ma_sv = s.ma_sv " +
                     "WHERE d.id_lich = ? ORDER BY s.ma_sv ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLich);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemDanh d = new DiemDanh();
                    d.setId(rs.getInt("id"));
                    d.setIdLich(rs.getInt("id_lich"));
                    d.setMaSv(rs.getString("ma_sv"));
                    d.setHoTen(rs.getString("ho_ten"));
                    d.setMaLop(rs.getString("ma_lop"));
                    d.setNgayDiemDanh(parseLocalDate(rs.getString("ngay_diem_danh")));
                    d.setTrangThai(rs.getString("trang_thai"));
                    d.setGhiChu(rs.getString("ghi_chu"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DiemDanh> getByStudent(String maSv) {
        List<DiemDanh> list = new ArrayList<>();
        String sql = "SELECT d.*, l.tieu_de, l.ngay AS ngay_lich, l.gio_bat_dau, l.gio_ket_thuc, l.dia_diem, l.hinh_thuc, l.loai_buoi, l.ten_cvht, l.ma_cvht " +
                     "FROM diem_danh d JOIN lich_giang_day l ON d.id_lich = l.id " +
                     "WHERE d.ma_sv = ? ORDER BY d.ngay_diem_danh DESC, l.gio_bat_dau DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemDanh d = new DiemDanh();
                    d.setId(rs.getInt("id"));
                    d.setIdLich(rs.getInt("id_lich"));
                    d.setMaSv(rs.getString("ma_sv"));
                    d.setNgayDiemDanh(parseLocalDate(rs.getString("ngay_diem_danh")));
                    d.setTrangThai(rs.getString("trang_thai"));
                    d.setGhiChu(rs.getString("ghi_chu"));
                    d.setTieuDeBuoiHoc(rs.getString("tieu_de"));
                    d.setTenCvht(rs.getString("ten_cvht"));
                    d.setMaCvht(rs.getString("ma_cvht"));
                    d.setGioBatDau(rs.getString("gio_bat_dau"));
                    d.setGioKetThuc(rs.getString("gio_ket_thuc"));
                    d.setDiaDiem(rs.getString("dia_diem"));
                    d.setHinhThuc(rs.getString("hinh_thuc"));
                    d.setLoaiBuoi(rs.getString("loai_buoi"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy toàn bộ lịch học của sinh viên gắn liền với giảng viên phụ trách và trạng thái điểm danh từng buổi.
     * Kể cả những buổi chưa điểm danh cũng sẽ xuất hiện với trạng thái "CHUA_DIEM_DANH".
     */
    public List<DiemDanh> getLichHocVaDiemDanhBySinhVien(String maSv) {
        List<DiemDanh> list = new ArrayList<>();
        String sql = "SELECT l.id AS id_lich, l.tieu_de, l.ngay AS ngay_lich, l.gio_bat_dau, l.gio_ket_thuc, " +
                     "l.ma_cvht, l.ten_cvht, l.dia_diem, l.hinh_thuc, l.loai_buoi, l.ma_lop, l.trang_thai AS trang_thai_lich, " +
                     "s.ho_ten, s.ma_sv, " +
                     "d.id AS id_diemdanh, d.ngay_diem_danh, d.trang_thai AS trang_thai_diemdanh, d.ghi_chu " +
                     "FROM sinh_vien s " +
                     "JOIN lich_giang_day l ON s.ma_lop = l.ma_lop " +
                     "LEFT JOIN diem_danh d ON (d.id_lich = l.id AND d.ma_sv = s.ma_sv) " +
                     "WHERE s.ma_sv = ? " +
                     "ORDER BY l.ngay ASC, l.gio_bat_dau ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemDanh d = new DiemDanh();
                    d.setId(rs.getInt("id_diemdanh"));
                    d.setIdLich(rs.getInt("id_lich"));
                    d.setMaSv(rs.getString("ma_sv"));
                    d.setHoTen(rs.getString("ho_ten"));
                    d.setMaLop(rs.getString("ma_lop"));

                    LocalDate dt = parseLocalDate(rs.getString("ngay_diem_danh"));
                    if (dt == null) {
                        dt = parseLocalDate(rs.getString("ngay_lich"));
                    }
                    d.setNgayDiemDanh(dt);

                    String tt = rs.getString("trang_thai_diemdanh");
                    if (tt == null || tt.trim().isEmpty()) {
                        tt = "CHUA_DIEM_DANH";
                    }
                    d.setTrangThai(tt);
                    d.setGhiChu(rs.getString("ghi_chu"));

                    d.setTieuDeBuoiHoc(rs.getString("tieu_de"));
                    d.setTenCvht(rs.getString("ten_cvht"));
                    d.setMaCvht(rs.getString("ma_cvht"));
                    d.setGioBatDau(rs.getString("gio_bat_dau"));
                    d.setGioKetThuc(rs.getString("gio_ket_thuc"));
                    d.setDiaDiem(rs.getString("dia_diem"));
                    d.setHinhThuc(rs.getString("hinh_thuc"));
                    d.setLoaiBuoi(rs.getString("loai_buoi"));

                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean saveOrUpdateAttendance(int idLich, String maSv, LocalDate ngay, String trangThai, String ghiChu) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return saveOrUpdateAttendance(conn, idLich, maSv, ngay, trangThai, ghiChu);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveOrUpdateAttendance(Connection conn, int idLich, String maSv, LocalDate ngay, String trangThai, String ghiChu) {
        String checkSql = "SELECT id FROM diem_danh WHERE id_lich = ? AND ma_sv = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, idLich);
            ps.setString(2, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                String ngayStr = ngay != null ? ngay.toString() : LocalDate.now().toString();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String updateSql = "UPDATE diem_danh SET trang_thai = ?, ghi_chu = ?, ngay_diem_danh = ? WHERE id = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, trangThai);
                        updatePs.setString(2, ghiChu);
                        updatePs.setString(3, ngayStr);
                        updatePs.setInt(4, id);
                        return updatePs.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO diem_danh (id_lich, ma_sv, ngay_diem_danh, trang_thai, ghi_chu) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, idLich);
                        insertPs.setString(2, maSv);
                        insertPs.setString(3, ngayStr);
                        insertPs.setString(4, trangThai);
                        insertPs.setString(5, ghiChu);
                        return insertPs.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getAttendanceStats(String maLop) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("ON_TIME", 0);
        stats.put("LATE", 0);
        stats.put("EXCUSED", 0);
        stats.put("ABSENT", 0);

        StringBuilder sql = new StringBuilder(
            "SELECT d.trang_thai, COUNT(*) AS cnt " +
            "FROM diem_danh d JOIN sinh_vien s ON d.ma_sv = s.ma_sv " +
            "WHERE 1=1 "
        );
        if (maLop != null && !maLop.isEmpty() && !"ALL".equals(maLop)) {
            sql.append("AND s.ma_lop = ? ");
        }
        sql.append("GROUP BY d.trang_thai");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (maLop != null && !maLop.isEmpty() && !"ALL".equals(maLop)) {
                ps.setString(1, maLop);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tt = rs.getString("trang_thai");
                    int cnt = rs.getInt("cnt");
                    if (tt != null && stats.containsKey(tt)) {
                        stats.put(tt, cnt);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Tự động sinh dữ liệu ảo điểm danh thực tế cho sinh viên đi học trên lớp.
     * @param idLich Nếu truyền id cụ thể: sinh cho buổi đó. Nếu null: sinh cho tất cả các buổi học.
     * @return Số lượng bản ghi điểm danh đã được tạo / cập nhật.
     */
    public int generateMockAttendance(Integer idLich) {
        int count = 0;
        String[] onTimeNotes = {
            "Có mặt đúng giờ, hăng hái phát biểu",
            "Đi học đầy đủ, chuẩn bị bài tốt",
            "Tích cực tham gia thảo luận nhóm",
            "Có mặt đúng giờ, tiếp thu bài tốt",
            "Nắm vững kiến thức bài học",
            "Đầy đủ dụng cụ học tập và tài liệu"
        };
        String[] lateNotes = {
            "Đi muộn 10 phút",
            "Đi muộn 15 phút do tắc đường",
            "Vào lớp muộn sau giờ giải lao",
            "Đến muộn 20 phút - đã nhắc nhở",
            "Đi muộn có báo trước cho lớp trưởng"
        };
        String[] excusedNotes = {
            "Nghỉ ốm có giấy phép bác sĩ",
            "Xin phép nghỉ vì lý do gia đình",
            "Tham gia hoạt động Đoàn / Hội sinh viên",
            "Có đơn xin phép gửi cố vấn học tập"
        };
        String[] absentNotes = {
            "Vắng không phép",
            "Bỏ tiết không có lý do",
            "Không liên lạc được - cần theo dõi",
            "Vắng mặt, không nộp bài tập đầu giờ"
        };

        java.util.Random rand = new java.util.Random();

        String lichSql = "SELECT id, ma_lop, ngay FROM lich_giang_day WHERE 1=1 ";
        if (idLich != null) {
            lichSql += "AND id = " + idLich;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Object[]> sessions = new ArrayList<>();
            try (Statement st = conn.createStatement();
                 ResultSet rsLich = st.executeQuery(lichSql)) {
                while (rsLich.next()) {
                    int sId = rsLich.getInt("id");
                    String maLop = rsLich.getString("ma_lop");
                    LocalDate ngay = parseLocalDate(rsLich.getString("ngay"));
                    if (ngay == null) ngay = LocalDate.now();
                    sessions.add(new Object[]{sId, maLop, ngay});
                }
            }

            // Tải danh sách sinh viên theo lớp vào bộ nhớ
            Map<String, List<String[]>> classStudentsMap = new HashMap<>();
            String svSql = "SELECT ma_sv, ma_lop, trang_thai FROM sinh_vien";
            try (Statement stSv = conn.createStatement();
                 ResultSet rsSv = stSv.executeQuery(svSql)) {
                while (rsSv.next()) {
                    String mSv = rsSv.getString("ma_sv");
                    String mLop = rsSv.getString("ma_lop");
                    String ttSv = rsSv.getString("trang_thai");
                    classStudentsMap.computeIfAbsent(mLop, k -> new ArrayList<>()).add(new String[]{mSv, ttSv});
                }
            }

            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            for (Object[] sess : sessions) {
                int sId = (int) sess[0];
                String maLop = (String) sess[1];
                LocalDate ngay = (LocalDate) sess[2];

                List<String[]> svList = classStudentsMap.getOrDefault(maLop, new ArrayList<>());
                for (String[] svInfo : svList) {
                    String maSv = svInfo[0];
                    String trangThaiSv = svInfo[1];

                    int roll = rand.nextInt(100);
                    String status;
                    String note;

                    if (trangThaiSv != null && (trangThaiSv.contains("CANH_BAO_2") || trangThaiSv.contains("BUOC_THOI_HOC"))) {
                        if (roll < 45) {
                            status = "ON_TIME";
                            note = onTimeNotes[rand.nextInt(onTimeNotes.length)];
                        } else if (roll < 70) {
                            status = "LATE";
                            note = lateNotes[rand.nextInt(lateNotes.length)];
                        } else if (roll < 85) {
                            status = "ABSENT";
                            note = absentNotes[rand.nextInt(absentNotes.length)];
                        } else {
                            status = "EXCUSED";
                            note = excusedNotes[rand.nextInt(excusedNotes.length)];
                        }
                    } else if (trangThaiSv != null && trangThaiSv.contains("CANH_BAO_1")) {
                        if (roll < 65) {
                            status = "ON_TIME";
                            note = onTimeNotes[rand.nextInt(onTimeNotes.length)];
                        } else if (roll < 85) {
                            status = "LATE";
                            note = lateNotes[rand.nextInt(lateNotes.length)];
                        } else {
                            status = rand.nextBoolean() ? "ABSENT" : "EXCUSED";
                            note = "ABSENT".equals(status) ? absentNotes[rand.nextInt(absentNotes.length)] : excusedNotes[rand.nextInt(excusedNotes.length)];
                        }
                    } else {
                        // Sinh viên bình thường
                        if (roll < 82) {
                            status = "ON_TIME";
                            note = onTimeNotes[rand.nextInt(onTimeNotes.length)];
                        } else if (roll < 93) {
                            status = "LATE";
                            note = lateNotes[rand.nextInt(lateNotes.length)];
                        } else if (roll < 97) {
                            status = "EXCUSED";
                            note = excusedNotes[rand.nextInt(excusedNotes.length)];
                        } else {
                            status = "ABSENT";
                            note = absentNotes[rand.nextInt(absentNotes.length)];
                        }
                    }

                    if (saveOrUpdateAttendance(conn, sId, maSv, ngay, status, note)) {
                        count++;
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
