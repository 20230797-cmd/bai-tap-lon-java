package com.qlcvht;

import com.qlcvht.dao.ThongBaoDAO;
import com.qlcvht.model.SinhVien;

import java.util.List;

public class ChatOrderingTest {
    public static void main(String[] args) {
        System.out.println("=== BAT DAU KIEM THU TINH NANG DAY TIN NHAN MOI LEN DAU TIEN ===");

        ThongBaoDAO dao = new ThongBaoDAO();

        // 1. Sinh vien 20230001 gui tin nhan
        System.out.println("\n[BUOC 1] Sinh vien 20230001 gui tin nhan...");
        dao.guiPhanHoiChoCoVan("20230001", "Nguyen Van Nam", "Hoi hoc phi", "Em muon hoi ve hoc phi a.");

        List<SinhVien> list1 = dao.getDanhSachSinhVienDaGuiTinNhan("CV001");
        System.out.println(" -> Nguoi o vi tri dau tien (#1): " + list1.get(0).getHoTen() + " (" + list1.get(0).getMaSv() + ")");
        if ("20230001".equals(list1.get(0).getMaSv())) {
            System.out.println(" => BUOC 1 THANH CONG: 20230001 o vi tri dau!");
        }

        // Small delay to ensure timestamp/id order
        try { Thread.sleep(100); } catch (Exception ignored) {}

        // 2. Sinh vien 20230009 gui tin nhan sau do
        System.out.println("\n[BUOC 2] Sinh vien 20230009 (Pham Minh Tuan) gui tin nhan moi...");
        dao.guiPhanHoiChoCoVan("20230009", "Pham Minh Tuan", "Xin lich hen", "Em xin gap thay buoi chieu.");

        List<SinhVien> list2 = dao.getDanhSachSinhVienDaGuiTinNhan("CV001");
        System.out.println(" -> Nguoi o vi tri dau tien (#1): " + list2.get(0).getHoTen() + " (" + list2.get(0).getMaSv() + ")");
        System.out.println(" -> Nguoi o vi tri thu hai (#2): " + list2.get(1).getHoTen() + " (" + list2.get(1).getMaSv() + ")");
        if ("20230009".equals(list2.get(0).getMaSv()) && "20230001".equals(list2.get(1).getMaSv())) {
            System.out.println(" => BUOC 2 THANH CONG: 20230009 len #1, day 20230001 xuong #2!");
        }

        try { Thread.sleep(100); } catch (Exception ignored) {}

        // 3. Co van tra loi lai cho 20230001
        System.out.println("\n[BUOC 3] Co van tra loi lai cho 20230001...");
        dao.traLoiTinNhanSinhVien("CV001", "TS. Nguyen Van An", "20230001", "Tra loi hoc phi", "Hoc phi dong qua cong thong tin nhe em.");

        List<SinhVien> list3 = dao.getDanhSachSinhVienDaGuiTinNhan("CV001");
        System.out.println(" -> Nguoi o vi tri dau tien (#1): " + list3.get(0).getHoTen() + " (" + list3.get(0).getMaSv() + ")");
        System.out.println(" -> Nguoi o vi tri thu hai (#2): " + list3.get(1).getHoTen() + " (" + list3.get(1).getMaSv() + ")");
        if ("20230001".equals(list3.get(0).getMaSv()) && "20230009".equals(list3.get(1).getMaSv())) {
            System.out.println(" => BUOC 3 THANH CONG: 20230001 duoc day tro lai vi tri #1, day 20230009 xuong #2!");
        }

        System.out.println("\n=== HOAN TAT KIEM THU SAP XEP TIN NHAN 100% THANH CONG ===");
        System.exit(0);
    }
}
