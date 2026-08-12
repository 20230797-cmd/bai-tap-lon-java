package com.qlcvht.model;

import java.sql.Timestamp;

public class TaiKhoan {
    private int id;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String email;
    private String vaiTro; // ADMIN, CO_VAN, QUAN_LY
    private String maRef; // MaCVHT hoac MaSV
    private Timestamp ngayTao;

    public TaiKhoan() {}

    public TaiKhoan(int id, String tenDangNhap, String matKhau, String hoTen, String email, String vaiTro, String maRef, Timestamp ngayTao) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.vaiTro = vaiTro;
        this.maRef = maRef;
        this.ngayTao = ngayTao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public String getMaRef() { return maRef; }
    public void setMaRef(String maRef) { this.maRef = maRef; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
}
