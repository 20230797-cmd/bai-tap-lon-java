package com.qlcvht.view.dialog;

import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.NhatKyTuVanDAO;
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

public class ChiTietSinhVienDialog extends JDialog {

    private final SinhVien sinhVien;

    public ChiTietSinhVienDialog(Frame parent, SinhVien sv) {
        super(parent, "Chi tiet Ho so Sinh vien: " + sv.getHoTen(), true);
        this.sinhVien = sv;
        initUI();
        setSize(740, 560);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblName = new JLabel(sinhVien.getHoTen() + "  (" + sinhVien.getMaSv() + ")");
        lblName.setFont(UITheme.fontBold(20));
        lblName.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Lop: " + (sinhVien.getTenLop() != null ? sinhVien.getTenLop() : sinhVien.getMaLop())
            + "    |    Trang thai: " + sinhVien.getTrangThaiHienThi());
        lblSub.setFont(UITheme.fontPlain(13));
        lblSub.setForeground(new Color(200, 220, 255));

        header.add(lblName, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Info panel (basic info row)
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        infoRow.setBackground(UITheme.PRIMARY_LIGHT);
        infoRow.setBorder(new EmptyBorder(2, 14, 2, 14));
        addInfoChip(infoRow, "Gioi tinh", sinhVien.getGioiTinh());
        addInfoChip(infoRow, "Ngay sinh", sinhVien.getNgaySinh() != null ? sinhVien.getNgaySinh().toString() : "---");
        addInfoChip(infoRow, "Email", sinhVien.getEmail() != null ? sinhVien.getEmail() : "---");
        addInfoChip(infoRow, "SDT", sinhVien.getSoDienThoai() != null ? sinhVien.getSoDienThoai() : "---");
        add(infoRow, BorderLayout.NORTH);

        // TabPane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY_BOLD);
        tabs.addTab("  Ket qua Hoc tap (GPA)  ", createKetQuaPanel());
        tabs.addTab("  Lich su Tu van CVHT  ", createTuVanPanel());
        add(tabs, BorderLayout.CENTER);

        // Close button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));
        JButton btnClose = new JButton("Dong");
        btnClose.setFont(UITheme.FONT_BTN);
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addInfoChip(JPanel parent, String label, String value) {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.fontPlain(10));
        lbl.setForeground(UITheme.PRIMARY);
        JLabel val = new JLabel(value);
        val.setFont(UITheme.fontBold(12));
        val.setForeground(UITheme.TEXT_PRIMARY);
        chip.add(lbl);
        chip.add(val);
        parent.add(chip);
    }

    private JPanel createKetQuaPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBorder(new EmptyBorder(10, 12, 10, 12));
        p.setBackground(Color.WHITE);

        String[] cols = {"Hoc ky", "Nam hoc", "GPA Hoc ky", "GPA Tich luy", "Tin chi No"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<KetQuaHocTap> list = new KetQuaHocTapDAO().getKetQuaBySinhVien(sinhVien.getMaSv());
        for (KetQuaHocTap kq : list) {
            model.addRow(new Object[]{
                "Hoc ky " + kq.getHocKy(), kq.getNamHoc(),
                String.format("%.2f", kq.getGpaHocKy()),
                String.format("%.2f", kq.getGpaTichLuy()),
                kq.getSoTinChiNo()
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);

        // GPA color renderer
        DefaultTableCellRenderer gpaR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null && !sel) {
                    try {
                        double g = Double.parseDouble(v.toString());
                        if (g < 1.0) comp.setForeground(UITheme.DANGER_DARK);
                        else if (g < 1.5) comp.setForeground(UITheme.DANGER);
                        else if (g < 2.0) comp.setForeground(UITheme.WARNING);
                        else comp.setForeground(UITheme.SUCCESS);
                    } catch (NumberFormatException ignored) {}
                }
                return comp;
            }
        };
        tbl.getColumnModel().getColumn(2).setCellRenderer(gpaR);
        tbl.getColumnModel().getColumn(3).setCellRenderer(gpaR);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);

        if (!list.isEmpty()) {
            KetQuaHocTap latest = list.get(0);
            JLabel summary = new JLabel("  GPA tich luy moi nhat: " + String.format("%.2f", latest.getGpaTichLuy())
                + "   |   Tin chi no: " + latest.getSoTinChiNo());
            summary.setFont(UITheme.FONT_BODY_BOLD);
            summary.setForeground(latest.getGpaTichLuy() >= 2.0 ? UITheme.SUCCESS : UITheme.DANGER);
            summary.setBorder(new EmptyBorder(8, 0, 0, 0));
            p.add(summary, BorderLayout.SOUTH);
        }

        return p;
    }

    private JPanel createTuVanPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBorder(new EmptyBorder(10, 12, 10, 12));
        p.setBackground(Color.WHITE);

        String[] cols = {"Ngay tu van", "Hinh thuc", "Co van", "Noi dung trao doi", "Cam ket sinh vien"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<NhatKyTuVan> list = new NhatKyTuVanDAO().getNhatKyBySinhVien(sinhVien.getMaSv());
        for (NhatKyTuVan nk : list) {
            model.addRow(new Object[]{
                nk.getNgayTuVan(), nk.getHinhThuc(), nk.getHoTenCvht(),
                nk.getNoiDung(), nk.getCamKetSinhVien()
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);
        int[] widths = {100, 130, 150, 250, 200};
        for (int i = 0; i < widths.length; i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JLabel total = new JLabel("  Tong so lan tu van: " + list.size());
        total.setFont(UITheme.FONT_BODY_BOLD);
        total.setForeground(UITheme.INFO);
        total.setBorder(new EmptyBorder(8, 0, 0, 0));
        p.add(total, BorderLayout.SOUTH);

        return p;
    }
}