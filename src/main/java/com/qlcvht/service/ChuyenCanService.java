package com.qlcvht.service;

import com.qlcvht.dao.ChuyenCanDAO;
import com.qlcvht.dao.ThongBaoDAO;
import com.qlcvht.model.ChuyenCanMonHoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChuyenCanService {

    private final ChuyenCanDAO chuyenCanDAO = new ChuyenCanDAO();
    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    public List<ChuyenCanMonHoc> getDanhSachTheoDoi(String maLop, String maMon, String trangThai, String keyword) {
        return chuyenCanDAO.getAllChuyenCan(maLop, maMon, trangThai, keyword);
    }

    public List<ChuyenCanMonHoc> getChuyenCanSinhVien(String maSv) {
        return chuyenCanDAO.getChuyenCanBySinhVien(maSv);
    }

    public Map<String, Integer> getThongKeChuyenCan(String maLop, String maMon) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("tong_sv", 0);
        stats.put("du_dieu_kien", 0);
        stats.put("nguy_co", 0);
        stats.put("cam_thi", 0);

        List<ChuyenCanMonHoc> list = chuyenCanDAO.getAllChuyenCan(maLop, maMon, "ALL", null);
        stats.put("tong_sv", list.size());

        for (ChuyenCanMonHoc c : list) {
            if ("CAM_THI".equalsIgnoreCase(c.getTrangThaiDuThi())) {
                stats.put("cam_thi", stats.get("cam_thi") + 1);
            } else if ("CANH_BAO_NGUY_CO".equalsIgnoreCase(c.getTrangThaiDuThi())) {
                stats.put("nguy_co", stats.get("nguy_co") + 1);
            } else {
                stats.put("du_dieu_kien", stats.get("du_dieu_kien") + 1);
            }
        }
        return stats;
    }

    /**
     * Tự động quét lại điều kiện thi theo quy chế 20% cho toàn bộ sinh viên
     */
    public int quetVaCapNhatDieuKienThi(String maLop, String maMon) {
        List<ChuyenCanMonHoc> list = chuyenCanDAO.getAllChuyenCan(maLop, maMon, "ALL", null);
        int updated = 0;
        for (ChuyenCanMonHoc c : list) {
            c.tinhToanQuyChe();
            if (chuyenCanDAO.updateChuyenCan(c)) {
                updated++;
            }
        }
        return updated;
    }

    public int quetVaCapNhatCamThiTuDong() {
        return quetVaCapNhatDieuKienThi(null, null);
    }

    /**
     * Gửi thông báo cảnh báo chuyên cần / thông báo cấm thi trực tiếp cho sinh viên
     */
    public boolean guiThongBaoChuyenCan(ChuyenCanMonHoc cc, String senderName) {
        String tieuDe;
        String noiDung;
        String nhom;

        if ("CAM_THI".equalsIgnoreCase(cc.getTrangThaiDuThi())) {
            tieuDe = String.format("🚫 [CẤM THI HỌC PHẦN] Môn %s (%s - %d TC)", cc.getTenMon(), cc.getMaMon(), cc.getSoTinChi());
            noiDung = String.format(
                "Chào em %s (%s),\n\n" +
                "Theo quy chế đào tạo tín chỉ của Nhà trường (vắng >= 20%% số buổi học phần), em đã vắng %.1f/%d buổi môn %s (tỷ lệ vắng %.1f%%).\n" +
                "Quyết định: Em KHÔNG ĐƯỢC DỰ THI KẾT THÚC HỌC PHẦN (CẤM THI) kỳ này, nhận điểm 0 chuyên cần và bắt buộc phải đăng ký học lại học phần này ở các kỳ tiếp theo.\n\n" +
                "Nếu có thắc mắc hoặc trường hợp đặc biệt, em liên hệ ngay Cố vấn học tập trước ngày thi.",
                cc.getHoTen(), cc.getMaSv(), cc.getTongBuoiVangQuyDoi(), cc.getTongSoBuoi(), cc.getTenMon(), cc.getTyLeVang()
            );
            nhom = "TIER_3";
        } else {
            tieuDe = String.format("⚠️ [CẢNH BÁO CHUYÊN CẦN] Môn %s (%s - %d TC)", cc.getTenMon(), cc.getMaMon(), cc.getSoTinChi());
            noiDung = String.format(
                "Chào em %s (%s),\n\n" +
                "Cảnh báo chuyên cần: Em đã vắng %.1f/%d buổi môn %s (ngưỡng tối đa cho phép là %d buổi).\n" +
                "Nếu em tiếp tục vắng thêm 1 buổi nữa, em sẽ BỊ CẤM THI học phần này.\n\n" +
                "Đề nghị em chú ý đi học đầy đủ và đúng giờ các buổi học tiếp theo!",
                cc.getHoTen(), cc.getMaSv(), cc.getTongBuoiVangQuyDoi(), cc.getTongSoBuoi(), cc.getTenMon(), cc.getSoBuoiToiDaChoPhepVang()
            );
            nhom = "TIER_2";
        }

        com.qlcvht.model.ThongBao tb = new com.qlcvht.model.ThongBao();
        tb.setMaThongBao("TB-CC-" + (System.currentTimeMillis() % 100000));
        tb.setTieuDe(tieuDe);
        tb.setNoiDung(noiDung);
        tb.setNhomRuiRo(nhom);
        tb.setMaLop(cc.getMaLop());
        tb.setMaSv(cc.getMaSv());
        tb.setNgayGui(java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 19));
        tb.setNguoiGui(senderName != null ? senderName : "Ban Cố Vấn Học Vụ & Phòng Đào Tạo EAUT");
        tb.setSoLuongNhan(1);
        tb.setTrangThai("DA_GUI");

        return thongBaoDAO.addThongBao(tb);
    }

    /**
     * Phát thông báo hàng loạt cho toàn bộ sinh viên bị cấm thi
     */
    public int guiThongBaoCanhBaoCamThi(String senderName) {
        List<ChuyenCanMonHoc> listCam = chuyenCanDAO.getDanhSachCamThi();
        int sent = 0;
        for (ChuyenCanMonHoc cc : listCam) {
            if (guiThongBaoChuyenCan(cc, senderName)) {
                sent++;
            }
        }
        return sent;
    }
}
