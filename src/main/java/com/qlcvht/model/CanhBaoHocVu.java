package com.qlcvht.model;

import java.sql.Date;

public class CanhBaoHocVu {
    private int id;
    private String maCanhBao;
    private String maSv;
    private String hoTenSv; // Transient
    private String maLop;   // Transient
    private int hocKy;
    private String namHoc;
    private String mucCanhBao; // MUC_1, MUC_2, BUOC_THOI_HOC
    private double gpaXetDuyet;
    private String lyDo;
    private Date ngayQuyetDinh;
    private String trangThaiTuVan; // CHUA_TU_VAN, DA_TU_VAN, DANG_THEO_DOI

    public CanhBaoHocVu() {}

    public CanhBaoHocVu(int id, String maCanhBao, String maSv, int hocKy, String namHoc, String mucCanhBao, double gpaXetDuyet, String lyDo, Date ngayQuyetDinh, String trangThaiTuVan) {
        this.id = id;
        this.maCanhBao = maCanhBao;
        this.maSv = maSv;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.mucCanhBao = mucCanhBao;
        this.gpaXetDuyet = gpaXetDuyet;
        this.lyDo = lyDo;
        this.ngayQuyetDinh = ngayQuyetDinh;
        this.trangThaiTuVan = trangThaiTuVan;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaCanhBao() { return maCanhBao; }
    public void setMaCanhBao(String maCanhBao) { this.maCanhBao = maCanhBao; }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTenSv() { return hoTenSv; }
    public void setHoTenSv(String hoTenSv) { this.hoTenSv = hoTenSv; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public int getHocKy() { return hocKy; }
    public void setHocKy(int hocKy) { this.hocKy = hocKy; }

    public String getNamHoc() { return namHoc; }
    public void setNamHoc(String namHoc) { this.namHoc = namHoc; }

    public String getMucCanhBao() { return mucCanhBao; }
    public void setMucCanhBao(String mucCanhBao) { this.mucCanhBao = mucCanhBao; }

    public double getGpaXetDuyet() { return gpaXetDuyet; }
    public void setGpaXetDuyet(double gpaXetDuyet) { this.gpaXetDuyet = gpaXetDuyet; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }

    public Date getNgayQuyetDinh() { return ngayQuyetDinh; }
    public void setNgayQuyetDinh(Date ngayQuyetDinh) { this.ngayQuyetDinh = ngayQuyetDinh; }

    public String getTrangThaiTuVan() { return trangThaiTuVan; }
    public void setTrangThaiTuVan(String trangThaiTuVan) { this.trangThaiTuVan = trangThaiTuVan; }

    public String getMucCanhBaoHienThi() {
        if (mucCanhBao == null) return "";
        switch (mucCanhBao) {
            case "MUC_1": return "Cảnh báo Mức 1";
            case "MUC_2": return "Cảnh báo Mức 2";
            case "BUOC_THOI_HOC": return "Buộc thôi học";
            default: return mucCanhBao;
        }
    }

    public String getTrangThaiTuVanHienThi() {
        if (trangThaiTuVan == null) return "Chưa tư vấn";
        switch (trangThaiTuVan) {
            case "DA_TU_VAN": return "Đã tư vấn";
            case "DANG_THEO_DOI": return "Đang theo dõi";
            case "CHUA_TU_VAN":
            default: return "Chưa tư vấn";
        }
    }
}
