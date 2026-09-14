package com.qlcvht.model;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserSession {
    private int id;
    private String tenDangNhap;
    private String hoTen;
    private String vaiTro;
    private Timestamp thoiGianDangNhap;
    private Timestamp thoiGianDangXuat;
    private String trangThai; // ONLINE, OFFLINE
    private String diaChiIp;
    private String thietBi;

    public UserSession() {
        this.thoiGianDangNhap = new Timestamp(System.currentTimeMillis());
        this.trangThai = "ONLINE";
        this.diaChiIp = "127.0.0.1 (Localhost)";
        this.thietBi = "Java Swing Desktop Client";
    }

    public UserSession(String tenDangNhap, String hoTen, String vaiTro) {
        this();
        this.tenDangNhap = tenDangNhap;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
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

    public Timestamp getThoiGianDangNhap() {
        return thoiGianDangNhap;
    }

    public void setThoiGianDangNhap(Timestamp thoiGianDangNhap) {
        this.thoiGianDangNhap = thoiGianDangNhap;
    }

    public Timestamp getThoiGianDangXuat() {
        return thoiGianDangXuat;
    }

    public void setThoiGianDangXuat(Timestamp thoiGianDangXuat) {
        this.thoiGianDangXuat = thoiGianDangXuat;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
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

    public boolean isOnline() {
        return "ONLINE".equalsIgnoreCase(trangThai);
    }

    public String getFormattedLoginTime() {
        if (thoiGianDangNhap == null) return "N/A";
        return thoiGianDangNhap.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getFormattedLogoutTime() {
        if (thoiGianDangXuat == null) return isOnline() ? "Đang trực tuyến" : "N/A";
        return thoiGianDangXuat.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getSessionDuration() {
        if (thoiGianDangNhap == null) return "0 phút";
        LocalDateTime start = thoiGianDangNhap.toLocalDateTime();
        LocalDateTime end = (thoiGianDangXuat != null) ? thoiGianDangXuat.toLocalDateTime() : LocalDateTime.now();
        Duration d = Duration.between(start, end);
        long minutes = Math.max(0, d.toMinutes());
        long hours = minutes / 60;
        long remainMin = minutes % 60;
        if (hours > 0) {
            return hours + " giờ " + remainMin + " phút";
        }
        return minutes + " phút";
    }
}
