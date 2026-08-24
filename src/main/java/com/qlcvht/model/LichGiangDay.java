package com.qlcvht.model;

import java.time.LocalDate;

/**
 * Model đại diện cho Lịch giảng dạy, Thời khóa biểu và Lịch Cố vấn Học vụ.
 */
public class LichGiangDay {
    private int id;
    private String maCvht;
    private String tenCvht;
    private String maLop;
    private String tenLop;
    private String tieuDe;
    private LocalDate ngay;
    private String gioBatDau;
    private String gioKetThuc;
    private String diaDiem;
    private String hinhThuc; // TRUC_TIEP, ONLINE, HYBRID
    private String loaiBuoi; // GIANG_DAY, TU_VAN_DINH_KY, TU_VAN_CANH_BAO, HOC_BU
    private String trangThai; // SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED
    private String ghiChu;

    public LichGiangDay() {}

    public LichGiangDay(int id, String maCvht, String tenCvht, String maLop, String tenLop,
                        String tieuDe, LocalDate ngay, String gioBatDau, String gioKetThuc,
                        String diaDiem, String hinhThuc, String loaiBuoi, String trangThai, String ghiChu) {
        this.id = id;
        this.maCvht = maCvht;
        this.tenCvht = tenCvht;
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.tieuDe = tieuDe;
        this.ngay = ngay;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.diaDiem = diaDiem;
        this.hinhThuc = hinhThuc;
        this.loaiBuoi = loaiBuoi;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaCvht() { return maCvht; }
    public void setMaCvht(String maCvht) { this.maCvht = maCvht; }

    public String getTenCvht() { return tenCvht; }
    public void setTenCvht(String tenCvht) { this.tenCvht = tenCvht; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public LocalDate getNgay() { return ngay; }
    public void setNgay(LocalDate ngay) { this.ngay = ngay; }

    public String getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(String gioBatDau) { this.gioBatDau = gioBatDau; }

    public String getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(String gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }

    public String getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { this.hinhThuc = hinhThuc; }

    public String getLoaiBuoi() { return loaiBuoi; }
    public void setLoaiBuoi(String loaiBuoi) { this.loaiBuoi = loaiBuoi; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
