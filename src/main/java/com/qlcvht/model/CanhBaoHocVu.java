package com.qlcvht.model;

import java.sql.Date;

public class CanhBaoHocVu {
    private int id;
    private String maCanhBao;
    private String maSv;
    private String hoTenSv;
    private String maLop;
    private int hocKy;
    private String namHoc;
    private String mucCanhBao; // MUC_1, MUC_2, BUOC_THOI_HOC
    private double gpaXetDuyet;
    private String lyDo;
    private Date ngayQuyetDinh;
    private String trangThaiTuVan; // CHUA_TU_VAN, DA_TU_VAN, DANG_THEO_DOI

    public CanhBaoHocVu() {}

    public CanhBaoHocVu(int id, String maCanhBao, String maSv, int hocKy, String namHoc,
                        String mucCanhBao, double gpaXetDuyet, String lyDo,
                        Date ngayQuyetDinh, String trangThaiTuVan) {
        this.id = id; this.maCanhBao = maCanhBao; this.maSv = maSv;
        this.hocKy = hocKy; this.namHoc = namHoc; this.mucCanhBao = mucCanhBao;
        this.gpaXetDuyet = gpaXetDuyet; this.lyDo = lyDo;
        this.ngayQuyetDinh = ngayQuyetDinh; this.trangThaiTuVan = trangThaiTuVan;
    }

    public int    getId()                           { return id; }
    public void   setId(int id)                     { this.id = id; }
    public String getMaCanhBao()                    { return maCanhBao; }
    public void   setMaCanhBao(String s)            { this.maCanhBao = s; }
    public String getMaSv()                         { return maSv; }
    public void   setMaSv(String s)                 { this.maSv = s; }
    public String getHoTenSv()                      { return hoTenSv; }
    public void   setHoTenSv(String s)              { this.hoTenSv = s; }
    public String getMaLop()                        { return maLop; }
    public void   setMaLop(String s)                { this.maLop = s; }
    public int    getHocKy()                        { return hocKy; }
    public void   setHocKy(int hocKy)               { this.hocKy = hocKy; }
    public String getNamHoc()                       { return namHoc; }
    public void   setNamHoc(String s)               { this.namHoc = s; }
    public String getMucCanhBao()                   { return mucCanhBao; }
    public void   setMucCanhBao(String s)           { this.mucCanhBao = s; }
    public double getGpaXetDuyet()                  { return gpaXetDuyet; }
    public void   setGpaXetDuyet(double d)          { this.gpaXetDuyet = d; }
    public String getLyDo()                         { return lyDo; }
    public void   setLyDo(String s)                 { this.lyDo = s; }
    public Date   getNgayQuyetDinh()                { return ngayQuyetDinh; }
    public void   setNgayQuyetDinh(Date d)          { this.ngayQuyetDinh = d; }
    public String getTrangThaiTuVan()               { return trangThaiTuVan; }
    public void   setTrangThaiTuVan(String s)       { this.trangThaiTuVan = s; }

    public String getMucCanhBaoHienThi() {
        if (mucCanhBao == null) return "";
        switch (mucCanhBao) {
            case "MUC_1":         return "Canh bao Muc 1";
            case "MUC_2":         return "Canh bao Muc 2";
            case "BUOC_THOI_HOC": return "Buoc thoi hoc";
            default: return mucCanhBao;
        }
    }

    public String getTrangThaiTuVanHienThi() {
        if (trangThaiTuVan == null) return "Chua tu van";
        switch (trangThaiTuVan) {
            case "DA_TU_VAN":    return "Da tu van";
            case "DANG_THEO_DOI":return "Dang theo doi";
            case "CHUA_TU_VAN":
            default: return "Chua tu van";
        }
    }
}