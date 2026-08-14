package com.qlcvht.model;

public class SinhVienTier {
    private String maSv;
    private String hoTen;
    private String maLop;
    private String tenLop;
    private String email;
    private String soDienThoai;
    private double gpaThucTe;
    private double gpaGiaLap; // Điểm giả lập (-1 nếu không dùng)
    private int soTinChiNo;
    private String trangThaiHocVu;

    public SinhVienTier() {
        this.gpaGiaLap = -1;
    }

    public SinhVienTier(String maSv, String hoTen, String maLop, String tenLop, String email,
                        String soDienThoai, double gpaThucTe, int soTinChiNo, String trangThaiHocVu) {
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.gpaThucTe = gpaThucTe;
        this.gpaGiaLap = -1;
        this.soTinChiNo = soTinChiNo;
        this.trangThaiHocVu = trangThaiHocVu;
    }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public double getGpaThucTe() { return gpaThucTe; }
    public void setGpaThucTe(double gpaThucTe) { this.gpaThucTe = gpaThucTe; }

    public double getGpaGiaLap() { return gpaGiaLap; }
    public void setGpaGiaLap(double gpaGiaLap) { this.gpaGiaLap = gpaGiaLap; }

    public int getSoTinChiNo() { return soTinChiNo; }
    public void setSoTinChiNo(int soTinChiNo) { this.soTinChiNo = soTinChiNo; }

    public String getTrangThaiHocVu() { return trangThaiHocVu; }
    public void setTrangThaiHocVu(String trangThaiHocVu) { this.trangThaiHocVu = trangThaiHocVu; }

    public double getGpaHienTai() {
        return (gpaGiaLap >= 0) ? gpaGiaLap : gpaThucTe;
    }

    /**
     * Phân hạng 3 Tier theo GPA hiện tại (hoặc giả lập):
     * Tier 1: GPA >= 3.2 (Tốt / Giỏi)
     * Tier 2: 2.0 <= GPA < 3.2 (Trung bình)
     * Tier 3: GPA < 2.0 (Yếu / Rủi ro cao)
     */
    public int getTierCode() {
        double gpa = getGpaHienTai();
        if (gpa >= 3.2) return 1;
        if (gpa >= 2.0) return 2;
        return 3;
    }

    public String getTierTen() {
        switch (getTierCode()) {
            case 1:  return "Tier 1 - Khá/Tốt";
            case 2:  return "Tier 2 - Trung bình";
            case 3:  
            default: return "Tier 3 - Yếu / Rủi ro";
        }
    }
}
