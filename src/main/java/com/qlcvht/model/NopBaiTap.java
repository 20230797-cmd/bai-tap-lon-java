package com.qlcvht.model;

import java.time.LocalDateTime;

/**
 * Model đại diện cho Bài nộp và Điểm đánh giá quá trình của sinh viên.
 */
public class NopBaiTap {
    private int id;
    private int idBaiTap;
    private String maSv;
    private String hoTen;
    private String maLop;
    private LocalDateTime ngayNop;
    private String fileDinhKem;
    private Double diemSo; // null nếu chưa chấm
    private String nhanXet;
    private String trangThai; // SUBMITTED, LATE, NOT_SUBMITTED, GRADED

    public NopBaiTap() {}

    public NopBaiTap(int id, int idBaiTap, String maSv, String hoTen, String maLop,
                     LocalDateTime ngayNop, String fileDinhKem, Double diemSo, String nhanXet, String trangThai) {
        this.id = id;
        this.idBaiTap = idBaiTap;
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.maLop = maLop;
        this.ngayNop = ngayNop;
        this.fileDinhKem = fileDinhKem;
        this.diemSo = diemSo;
        this.nhanXet = nhanXet;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdBaiTap() { return idBaiTap; }
    public void setIdBaiTap(int idBaiTap) { this.idBaiTap = idBaiTap; }

    public String getMaSv() { return maSv; }
    public void setMaSv(String maSv) { this.maSv = maSv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public LocalDateTime getNgayNop() { return ngayNop; }
    public void setNgayNop(LocalDateTime ngayNop) { this.ngayNop = ngayNop; }

    public String getFileDinhKem() { return fileDinhKem; }
    public void setFileDinhKem(String fileDinhKem) { this.fileDinhKem = fileDinhKem; }

    public Double getDiemSo() { return diemSo; }
    public void setDiemSo(Double diemSo) { this.diemSo = diemSo; }

    public String getNhanXet() { return nhanXet; }
    public void setNhanXet(String nhanXet) { this.nhanXet = nhanXet; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
