package com.qlcvht.model;

import java.sql.Date;

public class SinhVien {
    private String maSv;
    private String hoTen;
    private Date ngaySinh;
    private String gioiTinh;
    private String email;
    private String soDienThoai;
    private String maLop;
    private String tenLop; // Transient for GUI
    private String trangThai; // DANG_HOC, CANH_BAO_1, CANH_BAO_2, BUOC_THOI_HOC, DA_TOT_NGHIEP

    public SinhVien() {}

    public SinhVien(String maSv, String hoTen, Date ngaySinh, String gioiTinh, String email, String soDienThoai, String maLop, String trangThai) {
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.maLop = maLop;
        this.trangThai = trangThai;
    }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTrangThaiHienThi() {
        if (trangThai == null) return "Bình thường";
        switch (trangThai) {
            case "CANH_BAO_1": return "Cảnh báo Mức 1";
            case "CANH_BAO_2": return "Cảnh báo Mức 2";
            case "BUOC_THOI_HOC": return "Buộc thôi học";
            case "DA_TOT_NGHIEP": return "Đã tốt nghiệp";
            case "DANG_HOC":
            default: return "Đang học (Bình thường)";
        }
    }
}
