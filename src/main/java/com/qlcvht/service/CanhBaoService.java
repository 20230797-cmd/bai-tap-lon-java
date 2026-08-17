package com.qlcvht.service;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.SinhVien;

import java.sql.Date;
import java.util.List;

public class CanhBaoService {

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();

    /**
     * Tự động quét toàn bộ sinh viên và lập quyết định cảnh báo học vụ theo quy chế tín chỉ chuẩn.
     * @param hocKy Học kỳ xét duyệt (1, 2, 3)
     * @param namHoc Năm học xét duyệt (VD: "2023-2024")
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

            String mucCanhBao = null;
            String lyDo = "";
            String trangThaiSinhVienMoi = sv.getTrangThai();

            // Tiêu chuẩn cảnh báo học vụ theo quy chế tín chỉ chuẩn
            if (gpaTichLuy < 1.0 || noTinChi >= 24) {
                mucCanhBao = "BUOC_THOI_HOC";
                lyDo = String.format("CPA tích lũy rất thấp (%.2f < 1.0) hoặc nợ tín chỉ vượt mức cho phép (%d tín chỉ).", gpaTichLuy, noTinChi);
                trangThaiSinhVienMoi = "BUOC_THOI_HOC";
            } else if (gpaTichLuy < 1.5 || gpaHocKy < 0.8 || noTinChi >= 14) {
                mucCanhBao = "MUC_2";
                lyDo = String.format("CPA tích lũy thấp (%.2f < 1.5) hoặc GPA kỳ < 0.8 (%.2f), nợ %d tín chỉ.", gpaTichLuy, gpaHocKy, noTinChi);
                trangThaiSinhVienMoi = "CANH_BAO_2";
            } else if (gpaTichLuy < 2.0 || gpaHocKy < 1.0 || noTinChi >= 6) {
                mucCanhBao = "MUC_1";
                lyDo = String.format("CPA tích lũy dưới chuẩn (%.2f < 2.0) hoặc GPA kỳ < 1.0 (%.2f), nợ %d tín chỉ.", gpaTichLuy, gpaHocKy, noTinChi);
                trangThaiSinhVienMoi = "CANH_BAO_1";
            } else {
                // Đạt yêu cầu học vụ -> Duy trì hoặc chuyển về Đang học
                if ("CANH_BAO_1".equals(sv.getTrangThai()) || "CANH_BAO_2".equals(sv.getTrangThai())) {
                    sinhVienDAO.updateTrangThaiSinhVien(sv.getMaSv(), "DANG_HOC");
                }
                continue;
            }

            // Kiểm tra xem đã tồn tại cảnh báo cho sinh viên trong kỳ này chưa
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
                    lyDo,
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
}

