package com.qlcvht.model;

import java.time.LocalDate;

/**
 * Model đại diện cho Bài tập, Bài kiểm tra, Đồ án môn học & Đánh giá quá trình.
 */
public class BaiTap {
    private int id;
    private String maLop;
    private String tenLop;
    private String tieuDe;
    private String loaiDanhGia; // TMA (Assignment), CMA (Quiz/Midterm), PROJECT (Đồ án)
    private double trongSo; // Phần trăm trọng số (vd: 10, 20, 30%)
    private LocalDate hanNop;
    private String moTa;
    private String dinhDangChoPhep; // pdf, docx, zip, etc.
    private String trangThai; // OPEN, CLOSED, GRADED
    private int soBaiDaNop;
    private int tongSoSinhVien;

    public BaiTap() {}

    public BaiTap(int id, String maLop, String tenLop, String tieuDe, String loaiDanhGia,
                  double trongSo, LocalDate hanNop, String moTa, String dinhDangChoPhep, String trangThai) {
        this.id = id;
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.tieuDe = tieuDe;
        this.loaiDanhGia = loaiDanhGia;
        this.trongSo = trongSo;
        this.hanNop = hanNop;
        this.moTa = moTa;
        this.dinhDangChoPhep = dinhDangChoPhep;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getLoaiDanhGia() { return loaiDanhGia; }
    public void setLoaiDanhGia(String loaiDanhGia) { this.loaiDanhGia = loaiDanhGia; }

    public double getTrongSo() { return trongSo; }
    public void setTrongSo(double trongSo) { this.trongSo = trongSo; }

    public LocalDate getHanNop() { return hanNop; }
    public void setHanNop(LocalDate hanNop) { this.hanNop = hanNop; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getDinhDangChoPhep() { return dinhDangChoPhep; }
    public void setDinhDangChoPhep(String dinhDangChoPhep) { this.dinhDangChoPhep = dinhDangChoPhep; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getSoBaiDaNop() { return soBaiDaNop; }
    public void setSoBaiDaNop(int soBaiDaNop) { this.soBaiDaNop = soBaiDaNop; }

    public int getTongSoSinhVien() { return tongSoSinhVien; }
    public void setTongSoSinhVien(int tongSoSinhVien) { this.tongSoSinhVien = tongSoSinhVien; }
}
