package com.qlcvht.model;

import java.sql.Date;

public class NhatKyTuVan {
    private int id;
    private String maSv;
    private String hoTenSv; // Transient
    private String maCvht;
    private String hoTenCvht; // Transient
    private Integer idCanhBao;
    private Date ngayTuVan;
    private String hinhThuc;
    private String noiDung;
    private String nguyenNhan;
    private String giaiPhap;
    private String camKetSinhVien;

    public NhatKyTuVan() {}

    public NhatKyTuVan(int id, String maSv, String maCvht, Integer idCanhBao, Date ngayTuVan, String hinhThuc, String noiDung, String nguyenNhan, String giaiPhap, String camKetSinhVien) {
        this.id = id;
        this.maSv = maSv;
        this.maCvht = maCvht;
        this.idCanhBao = idCanhBao;
        this.ngayTuVan = ngayTuVan;
        this.hinhThuc = hinhThuc;
        this.noiDung = noiDung;
        this.nguyenNhan = nguyenNhan;
        this.giaiPhap = giaiPhap;
        this.camKetSinhVien = camKetSinhVien;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTenSv() { return hoTenSv; }
    public void setHoTenSv(String hoTenSv) { this.hoTenSv = hoTenSv; }

    public String getMaCvht() { return maCvht; }
    public void setMaCvht(String maCvht) { this.maCvht = maCvht; }

    public String getHoTenCvht() { return hoTenCvht; }
    public void setHoTenCvht(String hoTenCvht) { this.hoTenCvht = hoTenCvht; }

    public Integer getIdCanhBao() { return idCanhBao; }
    public void setIdCanhBao(Integer idCanhBao) { this.idCanhBao = idCanhBao; }

    public Date getNgayTuVan() { return ngayTuVan; }
    public void setNgayTuVan(Date ngayTuVan) { this.ngayTuVan = ngayTuVan; }

    public String getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { this.hinhThuc = hinhThuc; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getNguyenNhan() { return nguyenNhan; }
    public void setNguyenNhan(String nguyenNhan) { this.nguyenNhan = nguyenNhan; }

    public String getGiaiPhap() { return giaiPhap; }
    public void setGiaiPhap(String giaiPhap) { this.giaiPhap = giaiPhap; }

    public String getCamKetSinhVien() { return camKetSinhVien; }
    public void setCamKetSinhVien(String camKetSinhVien) { this.camKetSinhVien = camKetSinhVien; }
}
