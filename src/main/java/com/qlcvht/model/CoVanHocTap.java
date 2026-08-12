package com.qlcvht.model;

public class CoVanHocTap {
    private String maCvht;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String khoa;

    public CoVanHocTap() {}

    public CoVanHocTap(String maCvht, String hoTen, String email, String soDienThoai, String khoa) {
        this.maCvht = maCvht;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.khoa = khoa;
    }

    public String getMaCvht() { return maCvht; }
    public void setMaCvht(String maCvht) { this.maCvht = maCvht; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getKhoa() { return khoa; }
    public void setKhoa(String khoa) { this.khoa = khoa; }

    @Override
    public String toString() {
        return hoTen + " (" + maCvht + ")";
    }
}
