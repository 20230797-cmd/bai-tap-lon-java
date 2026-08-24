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
}
