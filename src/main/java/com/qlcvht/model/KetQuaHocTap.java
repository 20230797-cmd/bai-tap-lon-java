package com.qlcvht.model;

public class KetQuaHocTap {
    private int id;
    private String maSv;
    private String hoTenSv; // Transient
    private int hocKy;
    private String namHoc;
    private double gpaHocKy;
    private double gpaTichLuy;
    private int soTinChiNo;

    public KetQuaHocTap() {}

    public KetQuaHocTap(int id, String maSv, int hocKy, String namHoc, double gpaHocKy, double gpaTichLuy, int soTinChiNo) {
        this.id = id;
        this.maSv = maSv;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.gpaHocKy = gpaHocKy;
        this.gpaTichLuy = gpaTichLuy;
        this.soTinChiNo = soTinChiNo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTenSv() { return hoTenSv; }
    public void setHoTenSv(String hoTenSv) { this.hoTenSv = hoTenSv; }

    public int getHocKy() { return hocKy; }
    public void setHocKy(int hocKy) { this.hocKy = hocKy; }

    public String getNamHoc() { return namHoc; }
    public void setNamHoc(String namHoc) { this.namHoc = namHoc; }

    public double getGpaHocKy() { return gpaHocKy; }
    public void setGpaHocKy(double gpaHocKy) { this.gpaHocKy = gpaHocKy; }

    public double getGpaTichLuy() { return gpaTichLuy; }
    public void setGpaTichLuy(double gpaTichLuy) { this.gpaTichLuy = gpaTichLuy; }

    public int getSoTinChiNo() { return soTinChiNo; }
    public void setSoTinChiNo(int soTinChiNo) { this.soTinChiNo = soTinChiNo; }
}
