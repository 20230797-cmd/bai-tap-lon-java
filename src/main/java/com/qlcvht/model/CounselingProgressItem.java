package com.qlcvht.model;

import java.sql.Date;

/**
 * Model chi tiet tien do tu van va cai thien diem so cua Sinh Vien.
 */
public class CounselingProgressItem {

    private String maSv;
    private String hoTen;
    private String maLop;
    private String mucCanhBao;
    private Date ngayTuVan;
    private double gpaTruocTuVan;
    private double gpaSauTuVan;
    private String trangThaiCaiThien; // "Đã cải thiện (+0.45)", "Chưa cải thiện", "Chưa có điểm kỳ mới"

    public CounselingProgressItem() {}

    public CounselingProgressItem(String maSv, String hoTen, String maLop, String mucCanhBao,
                                  Date ngayTuVan, double gpaTruocTuVan, double gpaSauTuVan,
                                  String trangThaiCaiThien) {
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.maLop = maLop;
        this.mucCanhBao = mucCanhBao;
        this.ngayTuVan = ngayTuVan;
        this.gpaTruocTuVan = gpaTruocTuVan;
        this.gpaSauTuVan = gpaSauTuVan;
        this.trangThaiCaiThien = trangThaiCaiThien;
    }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getMucCanhBao() { return mucCanhBao; }
    public void setMucCanhBao(String mucCanhBao) { this.mucCanhBao = mucCanhBao; }

    public Date getNgayTuVan() { return ngayTuVan; }
    public void setNgayTuVan(Date ngayTuVan) { this.ngayTuVan = ngayTuVan; }

    public double getGpaTruocTuVan() { return gpaTruocTuVan; }
    public void setGpaTruocTuVan(double gpaTruocTuVan) { this.gpaTruocTuVan = gpaTruocTuVan; }

    public double getGpaSauTuVan() { return gpaSauTuVan; }
    public void setGpaSauTuVan(double gpaSauTuVan) { this.gpaSauTuVan = gpaSauTuVan; }

    public String getTrangThaiCaiThien() { return trangThaiCaiThien; }
    public void setTrangThaiCaiThien(String trangThaiCaiThien) { this.trangThaiCaiThien = trangThaiCaiThien; }
}
