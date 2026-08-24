package com.qlcvht.model;

import java.time.LocalDate;

/**
 * Model đại diện cho Điểm danh & Theo dõi Chuyên cần lớp học / buổi tư vấn.
 */
public class DiemDanh {
    private int id;
    private int idLich;
    private String maSv;
    private String hoTen;
    private String maLop;
    private LocalDate ngayDiemDanh;
    private String trangThai; // ON_TIME (Đúng giờ), LATE (Đi muộn), EXCUSED (Vắng có phép), ABSENT (Vắng không phép)
    private String ghiChu;

    private String tieuDeBuoiHoc;
    private String tenCvht;
    private String maCvht;
    private String gioBatDau;
    private String gioKetThuc;
    private String diaDiem;
    private String hinhThuc;
    private String loaiBuoi;

    public DiemDanh() {}

    public DiemDanh(int id, int idLich, String maSv, String hoTen, String maLop,
                    LocalDate ngayDiemDanh, String trangThai, String ghiChu) {
        this.id = id;
        this.idLich = idLich;
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.maLop = maLop;
        this.ngayDiemDanh = ngayDiemDanh;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdLich() { return idLich; }
    public void setIdLich(int idLich) { this.idLich = idLich; }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public LocalDate getNgayDiemDanh() { return ngayDiemDanh; }
    public void setNgayDiemDanh(LocalDate ngayDiemDanh) { this.ngayDiemDanh = ngayDiemDanh; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTieuDeBuoiHoc() { return tieuDeBuoiHoc; }
    public void setTieuDeBuoiHoc(String tieuDeBuoiHoc) { this.tieuDeBuoiHoc = tieuDeBuoiHoc; }

    public String getTenCvht() { return tenCvht; }
    public void setTenCvht(String tenCvht) { this.tenCvht = tenCvht; }

    public String getMaCvht() { return maCvht; }
    public void setMaCvht(String maCvht) { this.maCvht = maCvht; }

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
}
