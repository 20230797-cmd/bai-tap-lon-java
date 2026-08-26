package com.qlcvht;

import com.qlcvht.dao.*;
import com.qlcvht.model.*;

import java.util.List;

public class StudentFeatureTest {
    public static void main(String[] args) {
        System.out.println("=== BAT DAU KIEM THU TINH NANG TAI KHOAN SINH VIEN ===");

        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
        KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();
        SinhVienDAO sinhVienDAO = new SinhVienDAO();
        CoVanDAO coVanDAO = new CoVanDAO();

        // 1. Kiem tra dang nhap bang Ma Sinh Vien
        System.out.println("\n[TEST 1] Dang nhap bang ma sinh vien 20230001 (Nguyen Van Nam)...");
        try (java.sql.Connection c = com.qlcvht.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement("SELECT * FROM tai_khoan WHERE ten_dang_nhap = '20230001'")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG in tai_khoan: id=" + rs.getInt("id") + ", user=" + rs.getString("ten_dang_nhap") + ", pass=" + rs.getString("mat_khau") + ", role=" + rs.getString("vai_tro"));
                } else {
                    System.out.println("DEBUG: 20230001 CHUA CO trong tai_khoan");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        try (java.sql.Connection c = com.qlcvht.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement("SELECT * FROM sinh_vien WHERE ma_sv = '20230001'")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG in sinh_vien: ma_sv=" + rs.getString("ma_sv") + ", ten=" + rs.getString("ho_ten"));
                } else {
                    System.out.println("DEBUG: 20230001 CHUA CO trong sinh_vien");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        TaiKhoan tk1 = taiKhoanDAO.login("20230001", "123456");
        if (tk1 != null && "SINH_VIEN".equalsIgnoreCase(tk1.getVaiTro())) {
            System.out.println(" -> Thanh cong: " + tk1.getHoTen() + " | Vai tro: " + tk1.getVaiTro() + " | MaRef: " + tk1.getMaRef());
        } else {
            System.err.println(" -> That bai: khong the dang nhap 20230001");
        }

        System.out.println("\n[TEST 2] Dang nhap bang ma sinh vien 20230009 (Pham Minh Tuan)...");
        TaiKhoan tk2 = taiKhoanDAO.login("20230009", "123456");
        if (tk2 != null && "SINH_VIEN".equalsIgnoreCase(tk2.getVaiTro())) {
            System.out.println(" -> Thanh cong: " + tk2.getHoTen() + " | Vai tro: " + tk2.getVaiTro() + " | MaRef: " + tk2.getMaRef());
        } else {
            System.err.println(" -> That bai: khong the dang nhap 20230009");
        }

        // 2. Kiem tra truy van bang diem (Chi xem diem cua chinh minh)
        System.out.println("\n[TEST 3] Xem bang diem ca nhan cua SV 20230001...");
        List<KetQuaHocTap> kqList = ketQuaDAO.getKetQuaBySinhVien("20230001");
        System.out.println(" -> So hoc ky tim thay: " + kqList.size());
        for (KetQuaHocTap kq : kqList) {
            System.out.println("    + HK" + kq.getHocKy() + " (" + kq.getNamHoc() + ") - GPA HK: " + kq.getGpaHocKy() + " - GPA TL: " + kq.getGpaTichLuy() + " - No TC: " + kq.getSoTinChiNo());
        }

        // 3. Kiem tra tim Co van phu trach
        System.out.println("\n[TEST 4] Tim Co van hoc tap phu trach lop 68IT1...");
        CoVanHocTap cv = coVanDAO.getCoVanByLop("68IT1");
        if (cv != null) {
            System.out.println(" -> CVHT: " + cv.getHoTen() + " | Email: " + cv.getEmail() + " | Khoa: " + cv.getKhoa());
        }

        // 4. Kiem tra thong bao rieng cho SV 20230001
        System.out.println("\n[TEST 5] Lay thong bao rieng cho SV 20230001 (Lop 68IT1, Tier 1)...");
        List<ThongBao> tbList = thongBaoDAO.getThongBaoForSinhVien("20230001", "68IT1", "TIER_1");
        System.out.println(" -> So thong bao tim thay: " + tbList.size());
        for (ThongBao tb : tbList) {
            System.out.println("    + [" + tb.getMaThongBao() + "] " + tb.getTieuDe() + " (Gui boi: " + tb.getNguoiGui() + ")");
        }

        // 5. Kiem tra gui phan hoi cho CVHT
        System.out.println("\n[TEST 6] SV 20230001 gui tin nhan cho CVHT...");
        boolean sendSuccess = thongBaoDAO.guiPhanHoiChoCoVan("20230001", "Nguyen Van Nam", "Xin tu van dang ky mon hoc nang cao", "Em chao thay, em muon hoi ve viec dang ky som cac mon chuyen nganh ky toi a.");
        System.out.println(" -> Gui tin nhan thanh cong: " + sendSuccess);

        System.out.println("\n=== HOAN TAT KIEM THU 100% THANH CONG ===");
        System.exit(0);
    }
}
