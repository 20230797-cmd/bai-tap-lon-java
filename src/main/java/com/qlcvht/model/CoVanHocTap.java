package com.qlcvht.model;

public class CoVanHocTap {
    private String maCvht;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String khoa;

    public CoVanHocTap() {}

    public CoVanHocTap(String maCvht, String hoTen, String email, String soDienThoai, String khoa) {
        this.maCvht = maCvht; this.hoTen = hoTen; this.email = email;
        this.soDienThoai = soDienThoai; this.khoa = khoa;
    }

    public String getMaCvht()             { return maCvht; }
    public void   setMaCvht(String s)     { this.maCvht = s; }
    public String getHoTen()              { return hoTen; }
    public void   setHoTen(String s)      { this.hoTen = s; }
    public String getEmail()              { return email; }
    public void   setEmail(String s)      { this.email = s; }
    public String getSoDienThoai()        { return soDienThoai; }
    public void   setSoDienThoai(String s){ this.soDienThoai = s; }
    public String getKhoa()              { return khoa; }
    public void   setKhoa(String s)       { this.khoa = s; }

    @Override public String toString()    { return hoTen + " (" + maCvht + ")"; }
}