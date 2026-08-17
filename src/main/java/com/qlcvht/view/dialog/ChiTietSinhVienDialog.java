package com.qlcvht.view.dialog;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.SinhVien;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog Hồ sơ Học vụ 360 độ của sinh viên gồm 3 tab:
 * 1. Bảng điểm & Kết quả học tập từng học kỳ.
 * 2. Lịch sử cảnh báo học vụ.
 * 3. Lịch sử các buổi tư vấn của CVHT.
 */
public class ChiTietSinhVienDialog extends JDialog {

    private final SinhVien sinhVien;
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private final NhatKyTuVanDAO nhatKyDAO = new NhatKyTuVanDAO();

    public ChiTietSinhVienDialog(Frame parent, SinhVien sv) {
        super(parent, "Hồ sơ Học vụ 360°: " + sv.getHoTen() + " (" + sv.getMaSv() + ")", true);
        this.sinhVien = sv;
        initUI();
        setSize(900, 620);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // Top Banner
        JPanel topBanner = new JPanel();
        topBanner.setLayout(new BoxLayout(topBanner, BoxLayout.Y_AXIS));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblName = new JLabel("🎓 " + sinhVien.getHoTen() + "  -  MSSV: " + sinhVien.getMaSv());
        lblName.setFont(UITheme.fontBold(20));
        lblName.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Lớp: " + (sinhVien.getTenLop() != null ? sinhVien.getTenLop() : sinhVien.getMaLop())
            + "    |    Trạng thái: " + UITheme.formatTrangThaiSinhVien(sinhVien.getTrangThai()));
        lblSub.setFont(UITheme.fontPlain(13));
        lblSub.setForeground(new Color(210, 230, 255));

        header.add(lblName, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        topBanner.add(header);

        // Info chips row
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        infoRow.setBackground(UITheme.PRIMARY_LIGHT);
        infoRow.setBorder(new EmptyBorder(4, 16, 4, 16));
        addInfoChip(infoRow, "GIỚI TÍNH", sinhVien.getGioiTinh() != null ? sinhVien.getGioiTinh() : "Nam");
        addInfoChip(infoRow, "NGÀY SINH", sinhVien.getNgaySinh() != null ? sinhVien.getNgaySinh().toString() : "---");
        addInfoChip(infoRow, "EMAIL", sinhVien.getEmail() != null ? sinhVien.getEmail() : "---");
        addInfoChip(infoRow, "SỐ ĐIỆN THOẠI", sinhVien.getSoDienThoai() != null ? sinhVien.getSoDienThoai() : "---");
        
        // Tier badge
        KetQuaHocTap latestKq = ketQuaDAO.getKetQuaHocKyMoiNhat(sinhVien.getMaSv());
        double cpa = latestKq != null ? latestKq.getGpaTichLuy() : 0.0;
        String tierText = (cpa >= 3.2) ? "Tier 1 (Học bổng / Khá Giỏi)" : (cpa >= 2.0 ? "Tier 2 (Trung bình / An toàn)" : "Tier 3 (Nguy cơ / Cảnh báo)");
        addInfoChip(infoRow, "PHÂN TẦNG RỦI RO", tierText);

        topBanner.add(infoRow);
        add(topBanner, BorderLayout.NORTH);

        // Center Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY_BOLD);
        tabs.addTab("  📊 Kết quả Học tập & GPA  ", createKetQuaPanel());
        tabs.addTab("  ⚠️ Lịch sử Cảnh báo Học vụ  ", createCanhBaoPanel());
        tabs.addTab("  📝 Lịch sử Tư vấn CVHT  ", createTuVanPanel());
        add(tabs, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        bottom.setBackground(UITheme.BG_WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnClose = UITheme.createButton("Đóng Hồ Sơ", UITheme.PRIMARY, Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addInfoChip(JPanel parent, String label, String value) {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.fontBold(10));
        lbl.setForeground(UITheme.PRIMARY);
        JLabel val = new JLabel(value);
        val.setFont(UITheme.fontPlain(12));
        val.setForeground(UITheme.TEXT_PRIMARY);
        chip.add(lbl);
        chip.add(val);
        parent.add(chip);
    }

    private JPanel createKetQuaPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setBackground(Color.WHITE);

        String[] cols = {"Học kỳ", "Năm học", "GPA Học kỳ", "GPA Tích lũy (CPA)", "Số tín chỉ nợ", "Xếp loại học lực"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<KetQuaHocTap> list = ketQuaDAO.getKetQuaBySinhVien(sinhVien.getMaSv());
        for (KetQuaHocTap kq : list) {
            String xepLoai = "Xuất sắc";
            if (kq.getGpaTichLuy() < 2.0) xepLoai = "Yếu / Kém";
            else if (kq.getGpaTichLuy() < 2.5) xepLoai = "Trung bình";
            else if (kq.getGpaTichLuy() < 3.2) xepLoai = "Khá";
            else if (kq.getGpaTichLuy() < 3.6) xepLoai = "Giỏi";

            model.addRow(new Object[]{
                "Học kỳ " + kq.getHocKy(),
                kq.getNamHoc(),
                String.format("%.2f", kq.getGpaHocKy()),
                String.format("%.2f", kq.getGpaTichLuy()),
                kq.getSoTinChiNo() + " TC",
                xepLoai
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        for (int i = 0; i < tbl.getColumnCount(); i++) {
            tbl.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private JPanel createCanhBaoPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setBackground(Color.WHITE);

        String[] cols = {"Mã quyết định", "Học kỳ", "Năm học", "Mức cảnh báo", "GPA xét", "Lý do", "Ngày QĐ", "Trạng thái tư vấn"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<CanhBaoHocVu> list = canhBaoDAO.getCanhBaoByMaSv(sinhVien.getMaSv());
        for (CanhBaoHocVu cb : list) {
            model.addRow(new Object[]{
                cb.getMaCanhBao(),
                "Học kỳ " + cb.getHocKy(),
                cb.getNamHoc(),
                UITheme.formatMucCanhBao(cb.getMucCanhBao()),
                String.format("%.2f", cb.getGpaXetDuyet()),
                cb.getLyDo(),
                cb.getNgayQuyetDinh() != null ? cb.getNgayQuyetDinh().toString() : "---",
                UITheme.formatTrangThaiTuVan(cb.getTrangThaiTuVan())
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        tbl.getColumnModel().getColumn(0).setCellRenderer(center);
        tbl.getColumnModel().getColumn(1).setCellRenderer(center);
        tbl.getColumnModel().getColumn(2).setCellRenderer(center);
        tbl.getColumnModel().getColumn(4).setCellRenderer(center);
        tbl.getColumnModel().getColumn(6).setCellRenderer(center);
        tbl.getColumnModel().getColumn(7).setCellRenderer(center);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private JPanel createTuVanPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setBackground(Color.WHITE);

        String[] cols = {"Ngày tư vấn", "CVHT phụ trách", "Hình thức", "Nội dung trao đổi", "Nguyên nhân", "Giải pháp", "Cam kết sinh viên"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<NhatKyTuVan> list = nhatKyDAO.getNhatKyBySinhVien(sinhVien.getMaSv());
        for (NhatKyTuVan nk : list) {
            model.addRow(new Object[]{
                nk.getNgayTuVan() != null ? nk.getNgayTuVan().toString() : "---",
                nk.getHoTenCvht() != null ? nk.getHoTenCvht() : nk.getMaCvht(),
                nk.getHinhThuc(),
                nk.getNoiDung(),
                nk.getNguyenNhan() != null ? nk.getNguyenNhan() : "---",
                nk.getGiaiPhap() != null ? nk.getGiaiPhap() : "---",
                nk.getCamKetSinhVien() != null ? nk.getCamKetSinhVien() : "---"
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        tbl.getColumnModel().getColumn(0).setCellRenderer(center);
        tbl.getColumnModel().getColumn(2).setCellRenderer(center);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }
}