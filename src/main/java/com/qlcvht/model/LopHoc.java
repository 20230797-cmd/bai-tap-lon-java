package com.qlcvht.model;

public class LopHoc {
    private String maLop;
    private String tenLop;
    private String khoa;
    private int khoaHoc;
    private String maCvht;
    private String tenCvht; // Transient attribute for UI convenience

    public LopHoc() {}

    public LopHoc(String maLop, String tenLop, String khoa, int khoaHoc, String maCvht) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.khoa = khoa;
        this.khoaHoc = khoaHoc;
        this.maCvht = maCvht;
    }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getKhoa() { return khoa; }
    public void setKhoa(String khoa) { this.khoa = khoa; }

    public int getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(int khoaHoc) { this.khoaHoc = khoaHoc; }

    public String getMaCvht() { return maCvht; }
    public void setMaCvht(String maCvht) { this.maCvht = maCvht; }

    public String getTenCvht() { return tenCvht; }
    public void setTenCvht(String tenCvht) { this.tenCvht = tenCvht; }

    @Override
    public String toString() {
        return tenLop;
    }
}
