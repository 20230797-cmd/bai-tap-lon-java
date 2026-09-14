package com.qlcvht.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    private int id;
    private String tenDangNhap;
    private String hoTen;
    private String vaiTro;
    private String loaiHanhDong;   // DANG_NHAP, DANG_XUAT, THEM_MOI, CAP_NHAT, XOA, XUAT_FILE, NHAP_FILE, DIEM_DANH, CANH_BAO
    private String moTaChiTiet;
    private String diaChiIp;
    private String thietBi;
    private Timestamp thoiGian;
    private String trangThaiPhien;  // ONLINE, OFFLINE

    public AuditLog() {
        this.thoiGian = new Timestamp(System.currentTimeMillis());
        this.diaChiIp = "127.0.0.1 (Localhost)";
        this.thietBi = "Java Swing Desktop Client";
        this.trangThaiPhien = "ONLINE";
    }

    public AuditLog(String tenDangNhap, String hoTen, String vaiTro, String loaiHanhDong, String moTaChiTiet) {
        this();
        this.tenDangNhap = tenDangNhap;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.loaiHanhDong = loaiHanhDong;
        this.moTaChiTiet = moTaChiTiet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getLoaiHanhDong() {
        return loaiHanhDong;
    }

    public void setLoaiHanhDong(String loaiHanhDong) {
        this.loaiHanhDong = loaiHanhDong;
    }

    public String getMoTaChiTiet() {
        return moTaChiTiet;
    }

    public void setMoTaChiTiet(String moTaChiTiet) {
        this.moTaChiTiet = moTaChiTiet;
    }

    public String getDiaChiIp() {
        return diaChiIp;
    }

    public void setDiaChiIp(String diaChiIp) {
        this.diaChiIp = diaChiIp;
    }

    public String getThietBi() {
        return thietBi;
    }

    public void setThietBi(String thietBi) {
        this.thietBi = thietBi;
    }

    public Timestamp getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Timestamp thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getTrangThaiPhien() {
        return trangThaiPhien;
    }

    public void setTrangThaiPhien(String trangThaiPhien) {
        this.trangThaiPhien = trangThaiPhien;
    }

    public String getFormattedTime() {
        if (thoiGian == null) return "";
        LocalDateTime ldt = thoiGian.toLocalDateTime();
        return ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getLoaiHanhDongHienThi() {
        if (loaiHanhDong == null) return "KHÁC";
        switch (loaiHanhDong) {
            case "DANG_NHAP": return "🟢 ĐĂNG NHẬP";
            case "DANG_XUAT": return "⚪ ĐĂNG XUẤT";
            case "THEM_MOI":  return "➕ THÊM MỚI";
            case "CAP_NHAT":  return "✏️ CẬP NHẬT";
            case "XOA":       return "🗑️ XÓA";
            case "XUAT_FILE": return "📥 XUẤT BÁO CÁO";
            case "NHAP_FILE": return "📤 NHẬP DỮ LIỆU";
            case "DIEM_DANH": return "📋 ĐIỂM DANH";
            case "CANH_BAO":  return "⚠️ CẢNH BÁO";
            default: return loaiHanhDong;
        }
    }
}
