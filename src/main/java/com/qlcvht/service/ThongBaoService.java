package com.qlcvht.service;

import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.dao.ThongBaoDAO;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.SinhVienTier;
import com.qlcvht.model.ThongBao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ThongBaoService {

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    /**
     * Lấy toàn bộ danh sách sinh viên kèm GPA mới nhất và phân hạng 3 Tier
     */
    public List<SinhVienTier> getDanhSachSinhVienTier() {
        List<SinhVien> listSV = sinhVienDAO.getAllSinhVien();
        List<SinhVienTier> listTier = new ArrayList<>();

        for (SinhVien sv : listSV) {
            KetQuaHocTap kq = ketQuaDAO.getKetQuaHocKyMoiNhat(sv.getMaSv());
            double gpa = (kq != null) ? kq.getGpaTichLuy() : 0.0;
            int tinChiNo = (kq != null) ? kq.getSoTinChiNo() : 0;

            SinhVienTier svt = new SinhVienTier(
                sv.getMaSv(),
                sv.getHoTen(),
                sv.getMaLop(),
                sv.getTenLop(),
                sv.getEmail(),
                sv.getSoDienThoai(),
                gpa,
                tinChiNo,
                sv.getTrangThaiHienThi()
            );
            listTier.add(svt);
        }
        return listTier;
    }

    /**
     * Lọc danh sách sinh viên theo dải điểm GPA, Tier, Lớp và từ khóa tìm kiếm
     */
    public List<SinhVienTier> locSinhVienTier(List<SinhVienTier> source, double minGpa, double maxGpa,
                                              String tierFilter, String maLopFilter, String keyword) {
        return source.stream().filter(sv -> {
            double gpa = sv.getGpaHienTai();
            if (gpa < minGpa || gpa > maxGpa) return false;

            if (tierFilter != null && !tierFilter.equals("ALL")) {
                int expectedTier = 0;
                if ("TIER_1".equals(tierFilter)) expectedTier = 1;
                else if ("TIER_2".equals(tierFilter)) expectedTier = 2;
                else if ("TIER_3".equals(tierFilter)) expectedTier = 3;

                if (expectedTier > 0 && sv.getTierCode() != expectedTier) return false;
            }

            if (maLopFilter != null && !maLopFilter.equals("ALL") && !maLopFilter.trim().isEmpty()) {
                if (!maLopFilter.equalsIgnoreCase(sv.getMaLop())) return false;
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim().toLowerCase();
                boolean matchMa = sv.getMaSv().toLowerCase().contains(kw);
                boolean matchTen = sv.getHoTen().toLowerCase().contains(kw);
                boolean matchEmail = sv.getEmail() != null && sv.getEmail().toLowerCase().contains(kw);
                if (!matchMa && !matchTen && !matchEmail) return false;
            }

            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Lấy danh sách sinh viên thuộc nhóm đối tượng gửi thông báo
     */
    public List<SinhVienTier> getSinhVienTheoNhom(List<SinhVienTier> source, String nhomTarget, String maLopTarget, String maSvTarget) {
        if ("TIER_1".equals(nhomTarget)) {
            return source.stream().filter(sv -> sv.getTierCode() == 1).collect(Collectors.toList());
        } else if ("TIER_2".equals(nhomTarget)) {
            return source.stream().filter(sv -> sv.getTierCode() == 2).collect(Collectors.toList());
        } else if ("TIER_3".equals(nhomTarget)) {
            return source.stream().filter(sv -> sv.getTierCode() == 3).collect(Collectors.toList());
        } else if ("LOP".equals(nhomTarget) && maLopTarget != null) {
            return source.stream().filter(sv -> maLopTarget.equalsIgnoreCase(sv.getMaLop())).collect(Collectors.toList());
        } else if ("CA_NHAN".equals(nhomTarget) && maSvTarget != null) {
            return source.stream().filter(sv -> maSvTarget.equalsIgnoreCase(sv.getMaSv())).collect(Collectors.toList());
        }
        return source; // Default: ALL
    }

    /**
     * Gửi thông báo và lưu vào cơ sở dữ liệu
     */
    public boolean guiThongBao(String tieuDe, String noiDung, String nhomRuiRo, String maLop, String maSv,
                               int soLuongNhan, String nguoiGui) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = sdf.format(new Date());

        String prefix;
        if ("TIER_1".equals(nhomRuiRo)) prefix = "TB-T1";
        else if ("TIER_2".equals(nhomRuiRo)) prefix = "TB-T2";
        else if ("TIER_3".equals(nhomRuiRo)) prefix = "TB-T3";
        else prefix = "TB-ALL";

        String maThongBao = prefix + "-" + System.currentTimeMillis() % 100000;

        ThongBao tb = new ThongBao(
            0, maThongBao, tieuDe, noiDung, nhomRuiRo, maLop, maSv, nowStr, nguoiGui, soLuongNhan, "DA_GUI"
        );
        return thongBaoDAO.addThongBao(tb);
    }

    public List<ThongBao> getAllThongBao() {
        return thongBaoDAO.getAllThongBao();
    }

    public boolean deleteThongBao(int id) {
        return thongBaoDAO.deleteThongBao(id);
    }

    /**
     * Các Mẫu tin nhắn soạn sẵn theo phân hạng 3 Tier
     */
    public String getMauNoiDung(String templateKey) {
        switch (templateKey) {
            case "MAU_TIER_1":
                return "THÔNG BÁO BIỂU DƯƠNG HỌC TẬP XUẤT SẮC (TIER 1)\n\n" +
                       "Kính gửi các bạn Sinh viên nhóm Học tập Khá / Giỏi,\n" +
                       "Ban Giám hiệu và Cố vấn học tập tuyên dương kết quả học tập xuất sắc của bạn trong học kỳ vừa qua. " +
                       "Hiện tại kết quả học tập của bạn nằm trong Nhóm an toàn (Tier 1). " +
                       "Nhà trường xin chúc mừng và khuyến khích bạn tiếp tục phát huy, đăng ký xét duyệt các suất Học bổng Khuyến khích học tập kỳ tới.\n\n" +
                       "Trân trọng!";

            case "MAU_TIER_2":
                return "THÔNG BÁO DUY TRÌ PHONG ĐỘ HỌC TẬP (TIER 2)\n\n" +
                       "Kính gửi bạn Sinh viên,\n" +
                       "Nhà trường xin thông báo kết quả học tập hiện tại của bạn đang duy trì ở mức Trung bình (Tier 2). " +
                       "Để nâng cao kết quả học tập và cải thiện GPA tích lũy, khuyến khích bạn đăng ký các học phần cải thiện điểm và sắp xếp thời gian tự học hợp lý. " +
                       "Nếu có vướng mắc môn học, bạn vui lòng liên hệ Cố vấn học tập để được hướng dẫn.\n\n" +
                       "Trân trọng!";

            case "MAU_TIER_3":
                return "CẢNH BÁO HỌC VỤ KHẨN CẤP & YÊU CẦU TƯ VẤN (TIER 3)\n\n" +
                       "CẢNH BÁO HỌC VỤ: Kính gửi bạn Sinh viên thuộc Nhóm rủi ro cao (Tier 3),\n" +
                       "Qua kiểm tra dữ liệu học tập, GPA tích lũy của bạn đang ở dưới mức an toàn (hoặc bị cảnh báo học vụ). " +
                       "YÊU CẦU BẮT BUỘC: Bạn cần liên hệ ngay với Cố vấn học tập của lớp trong tuần này để lập Kế hoạch học tập khắc phục và Đăng ký lịch tư vấn trực tiếp.\n" +
                       "Lưu ý: Nếu không cải thiện kết quả, sinh viên sẽ bị xử lý theo Quy chế học vụ hiện hành.\n\n" +
                       "Trân trọng!";

            case "MAU_KHUYEN_KHUYEN_HOC":
                return "THÔNG BÁO HỌC BỔNG VÀ TƯ VẤN CẢI THIỆN ĐIỂM SỐ\n\n" +
                       "Chào các bạn sinh viên,\n" +
                       "Phòng Đào tạo xin thông báo danh sách đăng ký học bổng và các lớp phụ đạo hỗ trợ sinh viên trả nợ môn học đã chính thức mở đăng ký. " +
                       "Các bạn vui lòng kiểm tra cổng thông tin sinh viên để biết thêm chi tiết.";

            default:
                return "";
        }
    }
}
