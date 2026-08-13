package com.qlcvht.model;

public class LopHoc {
    private String maLop;
    private String tenLop;
    private String khoa;
    private int khoaHoc;
    private String maCvht;
    private String tenCvht;

    public LopHoc() {}

    public LopHoc(String maLop, String tenLop, String khoa, int khoaHoc, String maCvht) {
        this.maLop = maLop; this.tenLop = tenLop; this.khoa = khoa;
        this.khoaHoc = khoaHoc; this.maCvht = maCvht;
    }

    public String getMaLop()              { return maLop; }
    public void   setMaLop(String s)      { this.maLop = s; }
    public String getTenLop()             { return tenLop; }
    public void   setTenLop(String s)     { this.tenLop = s; }
    public String getKhoa()               { return khoa; }
    public void   setKhoa(String s)       { this.khoa = s; }
    public int    getKhoaHoc()            { return khoaHoc; }
    public void   setKhoaHoc(int i)       { this.khoaHoc = i; }
    public String getMaCvht()             { return maCvht; }
    public void   setMaCvht(String s)     { this.maCvht = s; }
    public String getTenCvht()            { return tenCvht; }
    public void   setTenCvht(String s)    { this.tenCvht = s; }

    @Override public String toString()    { return tenLop; }
}