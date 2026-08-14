package com.qlcvht.model;

import java.sql.Timestamp;

public class ThongBao {
    private int id;
    private String maThongBao;
    private String tieuDe;
    private String noiDung;
    private String nhomRuiRo; // ALL, TIER_1, TIER_2, TIER_3, LOP, CA_NHAN
    private String maLop;
    private String maSv;
    private String ngayGui;
    private String nguoiGui;
    private int soLuongNhan;
    private String trangThai; // DA_GUI

    public ThongBao() {}

    public ThongBao(int id, String maThongBao, String tieuDe, String noiDung, String nhomRuiRo,
                    String maLop, String maSv, String ngayGui, String nguoiGui, int soLuongNhan, String trangThai) {
        this.id = id;
        this.maThongBao = maThongBao;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.nhomRuiRo = nhomRuiRo;
        this.maLop = maLop;
        this.maSv = maSv;
        this.ngayGui = ngayGui;
        this.nguoiGui = nguoiGui;
        this.soLuongNhan = soLuongNhan;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaThongBao() { return maThongBao; }
    public void setMaThongBao(String maThongBao) { this.maThongBao = maThongBao; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getNhomRuiRo() { return nhomRuiRo; }
    public void setNhomRuiRo(String nhomRuiRo) { this.nhomRuiRo = nhomRuiRo; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getNgayGui() { return ngayGui; }
    public void setNgayGui(String ngayGui) { this.ngayGui = ngayGui; }

    public String getNguoiGui() { return nguoiGui; }
    public void setNguoiGui(String nguoiGui) { this.nguoiGui = nguoiGui; }

    public int getSoLuongNhan() { return soLuongNhan; }
    public void setSoLuongNhan(int soLuongNhan) { this.soLuongNhan = soLuongNhan; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getNhomRuiRoHienThi() {
        if (nhomRuiRo == null) return "Tất cả sinh viên";
        switch (nhomRuiRo) {
            case "TIER_1": return "Tier 1 - Khá / Tốt (Nhóm an toàn)";
            case "TIER_2": return "Tier 2 - Trung bình (Nhóm theo dõi)";
            case "TIER_3": return "Tier 3 - Yếu (Nhóm rủi ro cao)";
            case "LOP":    return "Lớp " + (maLop != null ? maLop : "");
            case "CA_NHAN": return "Sinh viên " + (maSv != null ? maSv : "");
            case "ALL":
            default:       return "Tất cả sinh viên";
        }
    }
}
