package com.qlcvht.model;

import java.util.List;

/**
 * Model du bao nguy co hoc vu bang AI/ML cho Sinh Vien.
 */
public class AIRiskPrediction {

    private String maSv;
    private String hoTen;
    private String maLop;
    private double gpaMoiNhat;
    private double gpaDuBao;
    private String trend; // "GIAM_MANH", "GIAM_NHE", "ON_DINH", "TANG"
    private int soTinChiNo;
    private String mucRuiRo; // "HIGH_RISK", "MEDIUM_RISK", "LOW_RISK"
    private String trangThaiHienTai;
    private String lyDoCanhBao;
    private String khuyenNghi;
    private List<Double> gpaHistory;

    public AIRiskPrediction() {}

    public AIRiskPrediction(String maSv, String hoTen, String maLop, double gpaMoiNhat, double gpaDuBao,
                            String trend, int soTinChiNo, String mucRuiRo, String trangThaiHienTai,
                            String lyDoCanhBao, String khuyenNghi, List<Double> gpaHistory) {
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.maLop = maLop;
        this.gpaMoiNhat = gpaMoiNhat;
        this.gpaDuBao = gpaDuBao;
        this.trend = trend;
        this.soTinChiNo = soTinChiNo;
        this.mucRuiRo = mucRuiRo;
        this.trangThaiHienTai = trangThaiHienTai;
        this.lyDoCanhBao = lyDoCanhBao;
        this.khuyenNghi = khuyenNghi;
        this.gpaHistory = gpaHistory;
    }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public double getGpaMoiNhat() { return gpaMoiNhat; }
    public void setGpaMoiNhat(double gpaMoiNhat) { this.gpaMoiNhat = gpaMoiNhat; }

    public double getGpaDuBao() { return gpaDuBao; }
    public void setGpaDuBao(double gpaDuBao) { this.gpaDuBao = gpaDuBao; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public int getSoTinChiNo() { return soTinChiNo; }
    public void setSoTinChiNo(int soTinChiNo) { this.soTinChiNo = soTinChiNo; }

    public String getMucRuiRo() { return mucRuiRo; }
    public void setMucRuiRo(String mucRuiRo) { this.mucRuiRo = mucRuiRo; }

    public String getTrangThaiHienTai() { return trangThaiHienTai; }
    public void setTrangThaiHienTai(String trangThaiHienTai) { this.trangThaiHienTai = trangThaiHienTai; }

    public String getLyDoCanhBao() { return lyDoCanhBao; }
    public void setLyDoCanhBao(String lyDoCanhBao) { this.lyDoCanhBao = lyDoCanhBao; }

    public String getKhuyenNghi() { return khuyenNghi; }
    public void setKhuyenNghi(String khuyenNghi) { this.khuyenNghi = khuyenNghi; }

    public List<Double> getGpaHistory() { return gpaHistory; }
    public void setGpaHistory(List<Double> gpaHistory) { this.gpaHistory = gpaHistory; }

    public String getMucRuiRoDisplay() {
        if ("HIGH_RISK".equals(mucRuiRo)) return "Nguy cơ Cao (Tiềm ẩn CB2/Thôi học)";
        if ("MEDIUM_RISK".equals(mucRuiRo)) return "Nguy cơ Trung bình (Cần cảnh báo 1)";
        return "An toàn (GPA ổn định)";
    }
}
