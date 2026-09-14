package com.qlcvht.service;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.ChuyenCanDAO;
import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.ChuyenCanMonHoc;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.SinhVien;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CanhBaoService {

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private final ChuyenCanDAO chuyenCanDAO = new ChuyenCanDAO();

    /**
     * Tự động quét toàn bộ sinh viên và lập quyết định cảnh báo học vụ theo quy chế tín chỉ chuẩn
     * và tiến độ 150 tín chỉ trong chu kỳ đào tạo 5 năm.
     * @param hocKy Học kỳ xét duyệt (1, 2, 3)
     * @param namHoc Năm học xét duyệt (VD: "2025-2026")
     * @return Số lượng cảnh báo mới được lập
     */
    public int quetCanhBaoHocVu(int hocKy, String namHoc) {
        List<SinhVien> listSV = sinhVienDAO.getAllSinhVien();
        int countNewWarnings = 0;

        for (SinhVien sv : listSV) {
            // Không xét lại các sinh viên đã tốt nghiệp hoặc đã bị buộc thôi học trước đó
            if ("DA_TOT_NGHIEP".equals(sv.getTrangThai()) || "BUOC_THOI_HOC".equals(sv.getTrangThai())) {
                continue;
            }

            // Lấy kết quả học tập theo học kỳ chỉ định hoặc mới nhất
            KetQuaHocTap kq = null;
            if (hocKy > 0 && namHoc != null && !namHoc.trim().isEmpty()) {
                kq = ketQuaDAO.getKetQuaHocKy(sv.getMaSv(), hocKy, namHoc);
            }
            if (kq == null) {
                kq = ketQuaDAO.getKetQuaHocKyMoiNhat(sv.getMaSv());
            }
            if (kq == null) continue;

            double gpaTichLuy = kq.getGpaTichLuy();
            double gpaHocKy = kq.getGpaHocKy();
            int noTinChi = kq.getSoTinChiNo();
            int tongTinChi = (kq.getTongTinChiTichLuy() > 0) ? kq.getTongTinChiTichLuy() : sv.getTongTinChiTichLuy();
            int namThu = (kq.getNamThu() > 0) ? kq.getNamThu() : (sv.getNamThu() > 0 ? sv.getNamThu() : tinhNamThu(sv.getMaSv()));

            // Đồng bộ ngược lại vào SinhVien nếu cần
            if (sv.getTongTinChiTichLuy() != tongTinChi || sv.getNamThu() != namThu) {
                sv.setTongTinChiTichLuy(tongTinChi);
                sv.setNamThu(namThu);
                sinhVienDAO.updateTongTinChiTichLuy(sv.getMaSv(), tongTinChi, namThu);
            }

            // Kiểm tra chuyên cần (vắng học)
            List<ChuyenCanMonHoc> chuyenCanList = chuyenCanDAO.getChuyenCanBySinhVien(sv.getMaSv());
            List<String> attendanceIssues = new ArrayList<>();
            for (ChuyenCanMonHoc cc : chuyenCanList) {
                if ("CAM_THI".equalsIgnoreCase(cc.getTrangThaiDuThi())) {
                    attendanceIssues.add(String.format("Bị cấm thi môn %s (vắng %.0f/%d buổi, vượt 20%%)", cc.getTenMon(), cc.getTongBuoiVangQuyDoi(), cc.getTongSoBuoi()));
                } else if (cc.getTongBuoiVangQuyDoi() >= 2.0) {
                    attendanceIssues.add(String.format("Nghỉ %.0f buổi môn %s (nguy cơ cấm thi)", cc.getTongBuoiVangQuyDoi(), cc.getTenMon()));
                }
            }

            String mucCanhBao = null;
            StringBuilder lyDoBuilder = new StringBuilder();
            String trangThaiSinhVienMoi = sv.getTrangThai();

            // 1. Kiểm tra tiêu chí Buộc thôi học (Mức 3)
            if (gpaTichLuy < 1.0 || noTinChi >= 24 || (namThu >= 5 && tongTinChi < 120)) {
                mucCanhBao = "BUOC_THOI_HOC";
                lyDoBuilder.append(String.format("Nguy cơ buộc thôi học: CPA tích lũy rất thấp (%.2f < 1.0) hoặc nợ quá %d tín chỉ. Tiến độ: %d/150 tín chỉ (Năm %d).",
                        gpaTichLuy, noTinChi, tongTinChi, namThu));
                trangThaiSinhVienMoi = "BUOC_THOI_HOC";
            }
            // 2. Kiểm tra Cảnh báo Mức 2 (Nghiêm trọng - Bắt buộc tư vấn CVHT)
            else if (gpaTichLuy < 1.5 || gpaHocKy < 0.8 || noTinChi >= 14 || (namThu == 4 && tongTinChi <= 120) || (namThu == 3 && tongTinChi < 68)) {
                mucCanhBao = "MUC_2";
                if (namThu == 4 && tongTinChi <= 120) {
                    lyDoBuilder.append(String.format("Chậm tiến độ tốt nghiệp: Đang là sinh viên Năm thứ 4 nhưng mới tích lũy được %d/150 tín chỉ (chuẩn yêu cầu >= 125 tín), nợ %d tín chỉ. CPA: %.2f. ",
                            tongTinChi, noTinChi, gpaTichLuy));
                } else {
                    lyDoBuilder.append(String.format("Cảnh báo học vụ Mức 2: CPA tích lũy thấp (%.2f < 1.5), GPA kỳ: %.2f, nợ %d tín chỉ. Tiến độ: %d/150 tín (Năm %d). ",
                            gpaTichLuy, gpaHocKy, noTinChi, tongTinChi, namThu));
                }
                trangThaiSinhVienMoi = "CANH_BAO_2";
            }
            // 3. Kiểm tra Cảnh báo Mức 1 (Nhắc nhở & theo dõi)
            else if (gpaTichLuy < 2.0 || gpaHocKy < 1.0 || noTinChi >= 6 || (namThu == 1 && tongTinChi < 20) || (namThu == 2 && tongTinChi < 45) || !attendanceIssues.isEmpty()) {
                mucCanhBao = "MUC_1";
                if (!attendanceIssues.isEmpty() && gpaTichLuy >= 2.0) {
                    lyDoBuilder.append("Cảnh báo chuyên cần: ").append(String.join("; ", attendanceIssues)).append(". ");
                } else {
                    lyDoBuilder.append(String.format("Cảnh báo học vụ Mức 1: CPA tích lũy dưới chuẩn (%.2f < 2.0) hoặc GPA kỳ < 1.0, nợ %d tín chỉ. Tiến độ: %d/150 tín (Năm %d). ",
                            gpaTichLuy, noTinChi, tongTinChi, namThu));
                }
                trangThaiSinhVienMoi = "CANH_BAO_1";
            } else {
                // Đạt yêu cầu học vụ -> Chuyển về trạng thái Đang học
                if ("CANH_BAO_1".equals(sv.getTrangThai()) || "CANH_BAO_2".equals(sv.getTrangThai())) {
                    sinhVienDAO.updateTrangThaiSinhVien(sv.getMaSv(), "DANG_HOC");
                }
                continue;
            }

            // Gắn thêm ghi chú chuyên cần nếu có
            if (!attendanceIssues.isEmpty() && !lyDoBuilder.toString().contains("chuyên cần")) {
                lyDoBuilder.append(" Chuyên cần: ").append(String.join("; ", attendanceIssues));
            }

            int hkXet = (hocKy > 0) ? hocKy : kq.getHocKy();
            String nhXet = (namHoc != null && !namHoc.trim().isEmpty()) ? namHoc : kq.getNamHoc();

            if (!canhBaoDAO.existsCanhBao(sv.getMaSv(), hkXet, nhXet)) {
                String maCanhBao = String.format("CB-%d%s-%s", hkXet, nhXet.replace("-", ""), sv.getMaSv());
                CanhBaoHocVu cb = new CanhBaoHocVu(
                    0,
                    maCanhBao,
                    sv.getMaSv(),
                    hkXet,
                    nhXet,
                    mucCanhBao,
                    gpaTichLuy,
                    lyDoBuilder.toString().trim(),
                    new Date(System.currentTimeMillis()),
                    "CHUA_TU_VAN"
                );
                boolean saved = canhBaoDAO.addCanhBao(cb);
                if (saved) {
                    sinhVienDAO.updateTrangThaiSinhVien(sv.getMaSv(), trangThaiSinhVienMoi);
                    countNewWarnings++;
                }
            }
        }
        return countNewWarnings;
    }

    /**
     * Tạo thông điệp cảnh báo cá nhân hóa chi tiết gửi cho sinh viên
     */
    public String taoThongDiepCanhBaoChiTiet(SinhVien sv, KetQuaHocTap kq, List<ChuyenCanMonHoc> chuyenCanList) {
        StringBuilder sb = new StringBuilder();
        int namThu = (sv.getNamThu() > 0) ? sv.getNamThu() : tinhNamThu(sv.getMaSv());
        int tongTin = sv.getTongTinChiTichLuy();
        double tiLe = sv.getTiLeTichLuy150Tin();

        sb.append(String.format("Chào em %s (MSSV: %s - Lớp: %s),\n\n", sv.getHoTen(), sv.getMaSv(), sv.getTenLop()));
        sb.append(String.format("● Tiến độ đào tạo 5 năm: Sinh viên Năm thứ %d\n", namThu));
        sb.append(String.format("● Tích lũy tín chỉ: %d / 150 Tín chỉ (Đạt %.1f%% kế hoạch đào tạo)\n", tongTin, tiLe));

        if (kq != null) {
            sb.append(String.format("● Kết quả học tập: GPA Kỳ: %.2f | CPA Tích lũy: %.2f | Số tín chỉ nợ: %d tín\n",
                    kq.getGpaHocKy(), kq.getGpaTichLuy(), kq.getSoTinChiNo()));
        }

        if (chuyenCanList != null && !chuyenCanList.isEmpty()) {
            sb.append("\n● Tình trạng chuyên cần học phần:\n");
            for (ChuyenCanMonHoc cc : chuyenCanList) {
                if (cc.getTongBuoiVangQuyDoi() > 0) {
                    sb.append(String.format("  - Môn %s (%s): Đã nghỉ %.0f/%d buổi (Tỷ lệ vắng %.1f%%) - %s\n",
                            cc.getTenMon(), cc.getMaMon(), cc.getTongBuoiVangQuyDoi(), cc.getTongSoBuoi(), cc.getTyLeVang(),
                            cc.getTrangThaiDuThi().equals("CAM_THI") ? "🚫 BỊ CẤM THI" : "⚠️ CẢNH BÁO NGUY CƠ"));
                }
            }
        }

        if (namThu == 4 && tongTin <= 120) {
            sb.append("\n⚠️ LƯU Ý ĐẶC BIỆT: Em đang là sinh viên Năm thứ 4 nhưng mới tích lũy được <= 120 tín chỉ. Em cần tối thiểu 125 tín chỉ để đủ điều kiện làm Đồ án tốt nghiệp đúng hạn. Hãy liên hệ Cố vấn học tập để đăng ký học trả nợ môn!");
        }

        return sb.toString();
    }

    private int tinhNamThu(String maSv) {
        if (maSv == null || maSv.length() < 4) return 1;
        try {
            int year = Integer.parseInt(maSv.substring(0, 4));
            int currentYear = 2026;
            int diff = currentYear - year + 1;
            return Math.max(1, Math.min(5, diff));
        } catch (Exception e) {
            return 1;
        }
    }
}
