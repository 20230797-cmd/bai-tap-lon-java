package com.qlcvht.model;

/**
 * Entity Chuyên cần & Điều kiện dự thi học phần theo Quy chế Tín chỉ Đại học (Bộ GD&ĐT / EAUT).
 * Quy chế: Sinh viên vắng mặt >= 20% tổng số buổi học sẽ bị CẤM THI HỌC KỲ.
 */
public class ChuyenCanMonHoc {
    private int id;
    private String maSv;
    private String hoTen;
    private String maLop;
    private String maMon;
    private String tenMon;
    private int soTinChi;
    private int hocKy;
    private String namHoc;
    
    private int tongSoBuoi;            // Môn 2TC = 10, Môn 3TC = 15, Môn 4TC = 20
    private int soBuoiToiDaChoPhepVang; // 20% của tongSoBuoi (2TC: 2 buổi, 3TC: 3 buổi, 4TC: 4 buổi)
    
    private int soBuoiCoMat;           // ON_TIME
    private int soBuoiMuon;            // LATE (2 lần muộn = 1 buổi vắng quy đổi)
    private int soBuoiVangCoPhep;      // EXCUSED
    private int soBuoiVangKhongPhep;   // ABSENT
    
    private double tongBuoiVangQuyDoi; // = ABSENT + EXCUSED + (LATE / 2.0)
    private double tyLeVang;           // (%) = (tongBuoiVangQuyDoi / tongSoBuoi) * 100
    private double diemChuyenCan;      // Thang 10 (Nếu cấm thi = 0.0)
    
    // DU_DIEU_KIEN | CANH_BAO_NGUY_CO | CAM_THI
    private String trangThaiDuThi;
    private String lyDoCamThi;

    public ChuyenCanMonHoc() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaSv() {
        return maSv;
    }

    public void setMaSv(String maSv) {
        this.maSv = maSv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getHoTenSv() {
        return hoTen;
    }

    public void setHoTenSv(String hoTenSv) {
        this.hoTen = hoTenSv;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
        this.tongSoBuoi = soTinChi * 5;
        this.soBuoiToiDaChoPhepVang = (int) Math.floor(this.tongSoBuoi * 0.20);
    }

    public int getHocKy() {
        return hocKy;
    }

    public void setHocKy(int hocKy) {
        this.hocKy = hocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public int getTongSoBuoi() {
        return tongSoBuoi;
    }

    public void setTongSoBuoi(int tongSoBuoi) {
        this.tongSoBuoi = tongSoBuoi;
    }

    public int getSoBuoiToiDaChoPhepVang() {
        return soBuoiToiDaChoPhepVang;
    }

    public void setSoBuoiToiDaChoPhepVang(int soBuoiToiDaChoPhepVang) {
        this.soBuoiToiDaChoPhepVang = soBuoiToiDaChoPhepVang;
    }

    public int getSoBuoiCoMat() {
        return soBuoiCoMat;
    }

    public void setSoBuoiCoMat(int soBuoiCoMat) {
        this.soBuoiCoMat = soBuoiCoMat;
    }

    public int getSoBuoiMuon() {
        return soBuoiMuon;
    }

    public void setSoBuoiMuon(int soBuoiMuon) {
        this.soBuoiMuon = soBuoiMuon;
    }

    public int getSoBuoiVangCoPhep() {
        return soBuoiVangCoPhep;
    }

    public void setSoBuoiVangCoPhep(int soBuoiVangCoPhep) {
        this.soBuoiVangCoPhep = soBuoiVangCoPhep;
    }

    public int getSoBuoiVangKhongPhep() {
        return soBuoiVangKhongPhep;
    }

    public void setSoBuoiVangKhongPhep(int soBuoiVangKhongPhep) {
        this.soBuoiVangKhongPhep = soBuoiVangKhongPhep;
    }

    public double getTongBuoiVangQuyDoi() {
        return tongBuoiVangQuyDoi;
    }

    public void setTongBuoiVangQuyDoi(double tongBuoiVangQuyDoi) {
        this.tongBuoiVangQuyDoi = tongBuoiVangQuyDoi;
    }

    public double getTyLeVang() {
        return tyLeVang;
    }

    public void setTyLeVang(double tyLeVang) {
        this.tyLeVang = tyLeVang;
    }

    public double getDiemChuyenCan() {
        return diemChuyenCan;
    }

    public void setDiemChuyenCan(double diemChuyenCan) {
        this.diemChuyenCan = diemChuyenCan;
    }

    public String getTrangThaiDuThi() {
        return trangThaiDuThi;
    }

    public void setTrangThaiDuThi(String trangThaiDuThi) {
        this.trangThaiDuThi = trangThaiDuThi;
    }

    public String getLyDoCamThi() {
        return lyDoCamThi;
    }

    public void setLyDoCamThi(String lyDoCamThi) {
        this.lyDoCamThi = lyDoCamThi;
    }

    public String getTrangThaiHienThi() {
        if ("CAM_THI".equalsIgnoreCase(trangThaiDuThi)) {
            return "🚫 BỊ CẤM THI";
        } else if ("CANH_BAO_NGUY_CO".equalsIgnoreCase(trangThaiDuThi)) {
            return "⚠️ CẢNH BÁO NGUY CƠ";
        } else {
            return "🟢 ĐỦ ĐIỀU KIỆN DỰ THI";
        }
    }

    public String getTrangThaiDuThiHienThi() {
        return getTrangThaiHienThi();
    }

    /**
     * Tự động tính toán lại tỷ lệ vắng và trạng thái dự thi theo quy chế chuẩn
     */
    public void tinhToanQuyChe() {
        if (tongSoBuoi <= 0) {
            tongSoBuoi = Math.max(10, soTinChi * 5);
        }
        if (soBuoiToiDaChoPhepVang <= 0) {
            soBuoiToiDaChoPhepVang = (int) Math.floor(tongSoBuoi * 0.20);
        }

        // 2 lần đi muộn = 1 buổi vắng
        tongBuoiVangQuyDoi = soBuoiVangKhongPhep + soBuoiVangCoPhep + Math.floor(soBuoiMuon / 2.0);
        tyLeVang = (tongBuoiVangQuyDoi / (double) tongSoBuoi) * 100.0;

        if (tongBuoiVangQuyDoi > soBuoiToiDaChoPhepVang || tyLeVang >= 20.0) {
            trangThaiDuThi = "CAM_THI";
            diemChuyenCan = 0.0;
            lyDoCamThi = String.format("Vắng %.1f/%d buổi (%.1f%% > 20%% quy định môn %d TC)", 
                tongBuoiVangQuyDoi, tongSoBuoi, tyLeVang, soTinChi);
        } else if (tongBuoiVangQuyDoi == soBuoiToiDaChoPhepVang) {
            trangThaiDuThi = "CANH_BAO_NGUY_CO";
            diemChuyenCan = Math.max(3.0, 10.0 - tongBuoiVangQuyDoi * 2.0);
            lyDoCamThi = String.format("Đã vắng %.1f/%d buổi (ngưỡng tối đa cho phép)", tongBuoiVangQuyDoi, soBuoiToiDaChoPhepVang);
        } else {
            trangThaiDuThi = "DU_DIEU_KIEN";
            diemChuyenCan = Math.max(5.0, 10.0 - tongBuoiVangQuyDoi * 1.5 - soBuoiMuon * 0.5);
            lyDoCamThi = "";
        }
    }
}
