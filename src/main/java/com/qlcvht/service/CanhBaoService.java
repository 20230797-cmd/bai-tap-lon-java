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
     * Tá»± Ä‘á»™ng quÃ©t toÃ n bá»™ sinh viÃªn vÃ  láº­p quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o há»c vá»¥ theo quy cháº¿ tÃ­n chá»‰ chuáº©n
     * vÃ  tiáº¿n Ä‘á»™ 150 tÃ­n chá»‰ trong chu ká»³ Ä‘Ã o táº¡o 5 nÄƒm.
     * @param hocKy Há»c ká»³ xÃ©t duyá»‡t (1, 2, 3)
     * @param namHoc NÄƒm há»c xÃ©t duyá»‡t (VD: "2025-2026")
     * @return Sá»‘ lÆ°á»£ng cáº£nh bÃ¡o má»›i Ä‘Æ°á»£c láº­p
     */
    public int quetCanhBaoHocVu(int hocKy, String namHoc) {
        List<SinhVien> listSV = sinhVienDAO.getAllSinhVien();
        int countNewWarnings = 0;

        for (SinhVien sv : listSV) {
            // KhÃ´ng xÃ©t láº¡i cÃ¡c sinh viÃªn Ä‘Ã£ tá»‘t nghiá»‡p hoáº·c Ä‘Ã£ bá»‹ buá»™c thÃ´i há»c trÆ°á»›c Ä‘Ã³
            if ("DA_TOT_NGHIEP".equals(sv.getTrangThai()) || "BUOC_THOI_HOC".equals(sv.getTrangThai())) {
                continue;
            }

            // Láº¥y káº¿t quáº£ há»c táº­p theo há»c ká»³ chá»‰ Ä‘á»‹nh hoáº·c má»›i nháº¥t
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

            // Äá»“ng bá»™ ngÆ°á»£c láº¡i vÃ o SinhVien náº¿u cáº§n
            if (sv.getTongTinChiTichLuy() != tongTinChi || sv.getNamThu() != namThu) {
                sv.setTongTinChiTichLuy(tongTinChi);
                sv.setNamThu(namThu);
                sinhVienDAO.updateTongTinChiTichLuy(sv.getMaSv(), tongTinChi, namThu);
            }

            // Kiá»ƒm tra chuyÃªn cáº§n (váº¯ng há»c)
            List<ChuyenCanMonHoc> chuyenCanList = chuyenCanDAO.getChuyenCanBySinhVien(sv.getMaSv());
            List<String> attendanceIssues = new ArrayList<>();
            for (ChuyenCanMonHoc cc : chuyenCanList) {
                if ("CAM_THI".equalsIgnoreCase(cc.getTrangThaiDuThi())) {
                    attendanceIssues.add(String.format("Bá»‹ cáº¥m thi mÃ´n %s (váº¯ng %.0f/%d buá»•i, vÆ°á»£t 20%%)", cc.getTenMon(), cc.getTongBuoiVangQuyDoi(), cc.getTongSoBuoi()));
                } else if (cc.getTongBuoiVangQuyDoi() >= 2.0) {
                    attendanceIssues.add(String.format("Nghá»‰ %.0f buá»•i mÃ´n %s (nguy cÆ¡ cáº¥m thi)", cc.getTongBuoiVangQuyDoi(), cc.getTenMon()));
                }
            }

            String mucCanhBao = null;
            StringBuilder lyDoBuilder = new StringBuilder();
            String trangThaiSinhVienMoi = sv.getTrangThai();

            // 1. Kiá»ƒm tra tiÃªu chÃ­ Buá»™c thÃ´i há»c (Má»©c 3)
            if (gpaTichLuy < 1.0 || noTinChi >= 24 || (namThu >= 5 && tongTinChi < 120)) {
                mucCanhBao = "BUOC_THOI_HOC";
                lyDoBuilder.append(String.format("Nguy cÆ¡ buá»™c thÃ´i há»c: CPA tÃ­ch lÅ©y ráº¥t tháº¥p (%.2f < 1.0) hoáº·c ná»£ quÃ¡ %d tÃ­n chá»‰. Tiáº¿n Ä‘á»™: %d/150 tÃ­n chá»‰ (NÄƒm %d).",
                        gpaTichLuy, noTinChi, tongTinChi, namThu));
                trangThaiSinhVienMoi = "BUOC_THOI_HOC";
            }
            // 2. Kiá»ƒm tra Cáº£nh bÃ¡o Má»©c 2 (NghiÃªm trá»ng - Báº¯t buá»™c tÆ° váº¥n CVHT)
            else if (gpaTichLuy < 1.2 || (namThu == 4 && tongTinChi < 100) || (namThu == 3 && tongTinChi < 50) || noTinChi >= 20) {
                mucCanhBao = "MUC_3";
                StringBuilder muc3 = new StringBuilder();
                muc3.append("CANH BAO HOC VU MUC 3 - NGHIEM TRONG: ");
                if (gpaTichLuy < 1.2) {
                    muc3.append(String.format("CPA tich luy rat thap (%.2f, yeu cau >= 1.2). ", gpaTichLuy));
                }
                if (namThu == 4 && tongTinChi < 100) {
                    muc3.append(String.format("Sinh vien Nam thu 4 moi tich luy duoc %d/150 tin chi (can >= 100 tin o nam 4). ", tongTinChi));
                }
                if (namThu == 3 && tongTinChi < 50) {
                    muc3.append(String.format("Sinh vien Nam thu 3 moi tich luy duoc %d/150 tin chi (can >= 50 tin o nam 3). ", tongTinChi));
                }
                if (noTinChi >= 20) {
                    muc3.append(String.format("No qua nhieu tin chi: %d tin. ", noTinChi));
                }
                if (!attendanceIssues.isEmpty()) {
                    muc3.append("Chuyen can: ").append(String.join("; ", attendanceIssues)).append(". ");
                }
                muc3.append("Sinh vien BAT BUOC lien he Co van hoc tap trong vong 7 ngay de duoc tu van va lap ke hoach hoc tap. Neu khong cai thien trong hoc ky tiep theo co nguy co bi Buoc thoi hoc.");
                lyDoBuilder.append(muc3.toString());
                trangThaiSinhVienMoi = "CANH_BAO_3";
            }
            // 3. Kiá»ƒm tra Cáº£nh bÃ¡o Má»©c 1 (Nháº¯c nhá»Ÿ & theo dÃµi)
            else if (gpaTichLuy < 1.5 || gpaHocKy < 0.8 || noTinChi >= 14 || (namThu == 4 && tongTinChi <= 120) || (namThu == 3 && tongTinChi < 68)) {
                mucCanhBao = "MUC_2";
                if (namThu == 4 && tongTinChi <= 120) {
                    lyDoBuilder.append(String.format("Cháº­m tiáº¿n Ä‘á»™ tá»‘t nghiá»‡p: Äang lÃ  sinh viÃªn NÄƒm thá»© 4 nhÆ°ng má»›i tÃ­ch lÅ©y Ä‘Æ°á»£c %d/150 tÃ­n chá»‰ (chuáº©n yÃªu cáº§u >= 125 tÃ­n), ná»£ %d tÃ­n chá»‰. CPA: %.2f. ",
                            tongTinChi, noTinChi, gpaTichLuy));
                } else {
                    lyDoBuilder.append(String.format("Cáº£nh bÃ¡o há»c vá»¥ Má»©c 2: CPA tÃ­ch lÅ©y tháº¥p (%.2f < 1.5), GPA ká»³: %.2f, ná»£ %d tÃ­n chá»‰. Tiáº¿n Ä‘á»™: %d/150 tÃ­n (NÄƒm %d). ",
                            gpaTichLuy, gpaHocKy, noTinChi, tongTinChi, namThu));
                }
                trangThaiSinhVienMoi = "CANH_BAO_2";
            }
            // 3. Kiem tra Canh bao Muc 3 (Nghiem trong - nguy co buoc thoi hoc)
            else if (gpaTichLuy < 2.0 || gpaHocKy < 1.0 || noTinChi >= 6 || (namThu == 1 && tongTinChi < 20) || (namThu == 2 && tongTinChi < 45) || !attendanceIssues.isEmpty()) {
                mucCanhBao = "MUC_1";
                if (!attendanceIssues.isEmpty() && gpaTichLuy >= 2.0) {
                    lyDoBuilder.append("Cáº£nh bÃ¡o chuyÃªn cáº§n: ").append(String.join("; ", attendanceIssues)).append(". ");
                } else {
                    lyDoBuilder.append(String.format("Cáº£nh bÃ¡o há»c vá»¥ Má»©c 1: CPA tÃ­ch lÅ©y dÆ°á»›i chuáº©n (%.2f < 2.0) hoáº·c GPA ká»³ < 1.0, ná»£ %d tÃ­n chá»‰. Tiáº¿n Ä‘á»™: %d/150 tÃ­n (NÄƒm %d). ",
                            gpaTichLuy, noTinChi, tongTinChi, namThu));
                }
                trangThaiSinhVienMoi = "CANH_BAO_1";
            } else {
                // Äáº¡t yÃªu cáº§u há»c vá»¥ -> Chuyá»ƒn vá» tráº¡ng thÃ¡i Äang há»c
                if ("CANH_BAO_1".equals(sv.getTrangThai()) || "CANH_BAO_2".equals(sv.getTrangThai())) {
                    sinhVienDAO.updateTrangThaiSinhVien(sv.getMaSv(), "DANG_HOC");
                }
                continue;
            }

            // Gáº¯n thÃªm ghi chÃº chuyÃªn cáº§n náº¿u cÃ³
            if (!attendanceIssues.isEmpty() && !lyDoBuilder.toString().contains("chuyÃªn cáº§n")) {
                lyDoBuilder.append(" ChuyÃªn cáº§n: ").append(String.join("; ", attendanceIssues));
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
     * Táº¡o thÃ´ng Ä‘iá»‡p cáº£nh bÃ¡o cÃ¡ nhÃ¢n hÃ³a chi tiáº¿t gá»­i cho sinh viÃªn
     */
    public String taoThongDiepCanhBaoChiTiet(SinhVien sv, KetQuaHocTap kq, List<ChuyenCanMonHoc> chuyenCanList) {
        StringBuilder sb = new StringBuilder();
        int namThu = (sv.getNamThu() > 0) ? sv.getNamThu() : tinhNamThu(sv.getMaSv());
        int tongTin = sv.getTongTinChiTichLuy();
        double tiLe = sv.getTiLeTichLuy150Tin();

        sb.append(String.format("ChÃ o em %s (MSSV: %s - Lá»›p: %s),\n\n", sv.getHoTen(), sv.getMaSv(), sv.getTenLop()));
        sb.append(String.format("â— Tiáº¿n Ä‘á»™ Ä‘Ã o táº¡o 5 nÄƒm: Sinh viÃªn NÄƒm thá»© %d\n", namThu));
        sb.append(String.format("â— TÃ­ch lÅ©y tÃ­n chá»‰: %d / 150 TÃ­n chá»‰ (Äáº¡t %.1f%% káº¿ hoáº¡ch Ä‘Ã o táº¡o)\n", tongTin, tiLe));

        if (kq != null) {
            sb.append(String.format("â— Káº¿t quáº£ há»c táº­p: GPA Ká»³: %.2f | CPA TÃ­ch lÅ©y: %.2f | Sá»‘ tÃ­n chá»‰ ná»£: %d tÃ­n\n",
                    kq.getGpaHocKy(), kq.getGpaTichLuy(), kq.getSoTinChiNo()));
        }

        if (chuyenCanList != null && !chuyenCanList.isEmpty()) {
            sb.append("\nâ— TÃ¬nh tráº¡ng chuyÃªn cáº§n há»c pháº§n:\n");
            for (ChuyenCanMonHoc cc : chuyenCanList) {
                if (cc.getTongBuoiVangQuyDoi() > 0) {
                    sb.append(String.format("  - MÃ´n %s (%s): ÄÃ£ nghá»‰ %.0f/%d buá»•i (Tá»· lá»‡ váº¯ng %.1f%%) - %s\n",
                            cc.getTenMon(), cc.getMaMon(), cc.getTongBuoiVangQuyDoi(), cc.getTongSoBuoi(), cc.getTyLeVang(),
                            cc.getTrangThaiDuThi().equals("CAM_THI") ? "ðŸš« Bá»Š Cáº¤M THI" : "âš ï¸ Cáº¢NH BÃO NGUY CÆ "));
                }
            }
        }

        if (namThu == 4 && tongTin <= 120) {
            sb.append("\nâš ï¸ LÆ¯U Ã Äáº¶C BIá»†T: Em Ä‘ang lÃ  sinh viÃªn NÄƒm thá»© 4 nhÆ°ng má»›i tÃ­ch lÅ©y Ä‘Æ°á»£c <= 120 tÃ­n chá»‰. Em cáº§n tá»‘i thiá»ƒu 125 tÃ­n chá»‰ Ä‘á»ƒ Ä‘á»§ Ä‘iá»u kiá»‡n lÃ m Äá»“ Ã¡n tá»‘t nghiá»‡p Ä‘Ãºng háº¡n. HÃ£y liÃªn há»‡ Cá»‘ váº¥n há»c táº­p Ä‘á»ƒ Ä‘Äƒng kÃ½ há»c tráº£ ná»£ mÃ´n!");
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
