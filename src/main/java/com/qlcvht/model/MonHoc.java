package com.qlcvht.model;

/**
 * Entity đại diện cho Môn học / Học phần trong chương trình đào tạo.
 */
public class MonHoc {
    private String maMon;
    private String tenMon;
    private int soTinChi;
    private int soTiet;
    private int soBuoiToiDaVang; // 20% tổng số buổi học
    private String khoa;

    public MonHoc() {}

    public MonHoc(String maMon, String tenMon, int soTinChi, int soTiet, int soBuoiToiDaVang, String khoa) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        this.soTiet = soTiet;
        this.soBuoiToiDaVang = soBuoiToiDaVang;
        this.khoa = khoa;
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
        this.soTiet = soTinChi * 15;
        // Môn 2TC = 10 buổi (tối đa vắng 2); Môn 3TC = 15 buổi (tối đa vắng 3); Môn 4TC = 20 buổi (tối đa vắng 4)
        this.soBuoiToiDaVang = (int) Math.floor((soTinChi * 5) * 0.20);
    }

    public int getSoTiet() {
        return soTiet;
    }

    public void setSoTiet(int soTiet) {
        this.soTiet = soTiet;
    }

    public int getSoBuoiToiDaVang() {
        return soBuoiToiDaVang;
    }

    public void setSoBuoiToiDaVang(int soBuoiToiDaVang) {
        this.soBuoiToiDaVang = soBuoiToiDaVang;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public int getTongSoBuoiHoc() {
        return soTinChi * 5;
    }

    @Override
    public String toString() {
        return maMon + " - " + tenMon + " (" + soTinChi + " TC)";
    }
}
