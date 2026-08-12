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
     * Tự động quét toàn bộ danh sách sinh viên và tạo cảnh báo học vụ theo tiêu chí GPA
     * @return Số lượng sinh viên mới phát hiện bị cảnh báo
     */
    public int quetCanhBaoHocVu(int hocKy, String namHoc) {
        List<SinhVien> listSV = sinhVienDAO.getAllSinhVien();
        int countNewWarnings = 0;

        for (SinhVien sv : listSV) {
            // Lấy kết quả học tập mới nhất hoặc theo kỳ xét duyệt
            KetQuaHocTap kq = ketQuaDAO.getKetQuaHocKyMoiNhat(sv.getMaSv());
            if (kq == null) continue;

            double gpaTichLuy = kq.getGpaTichLuy();
            String mucCanhBao = null;
            String lyDo = "";
            String trangThaiSinhVienMoi = sv.getTrangThai();

            if (gpaTichLuy < 1.0) {
                mucCanhBao = "BUOC_THOI_HOC";
                lyDo = String.format("GPA tích lũy rất thấp (%.2f < 1.0) và nợ %d tín chỉ.", gpaTichLuy, kq.getSoTinChiNo());
                trangThaiSinhVienMoi = "BUOC_THOI_HOC";
            } else if (gpaTichLuy < 1.5) {
                mucCanhBao = "MUC_2";
                lyDo = String.format("GPA tích lũy thấp (%.2f < 1.5) và nợ %d tín chỉ.", gpaTichLuy, kq.getSoTinChiNo());
                trangThaiSinhVienMoi = "CANH_BAO_2";
            } else if (gpaTichLuy < 2.0) {
                mucCanhBao = "MUC_1";
                lyDo = String.format("GPA tích lũy dưới mức trung bình (%.2f < 2.0).", gpaTichLuy);
                trangThaiSinhVienMoi = "CANH_BAO_1";
            } else {
                // Đạt yêu cầu -> Cập nhật trạng thái về Đang học nếu trước đó không bị buộc thôi học
                if (!"BUOC_THOI_HOC".equals(sv.getTrangThai()) && !"DA_TOT_NGHIEP".equals(sv.getTrangThai())) {
                    sinhVienDAO.updateTrangThaiSinhVien(sv.getMaSv(), "DANG_HOC");
                }
                continue; // Không bị cảnh báo
            }

            // Nếu bị cảnh báo -> kiểm tra xem đã tồn tại cảnh báo cho học kỳ này chưa
            if (!canhBaoDAO.existsCanhBao(sv.getMaSv(), hocKy, namHoc)) {
                String maCanhBao = String.format("CB-%d%s-%s", hocKy, namHoc.replace("-", ""), sv.getMaSv());
                CanhBaoHocVu cb = new CanhBaoHocVu(
                    0,
                    maCanhBao,
                    sv.getMaSv(),
                    hocKy,
                    namHoc,
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
