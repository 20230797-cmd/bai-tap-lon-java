package com.qlcvht.view.panel;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.CanhBaoService;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.LapNhatKyDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLyCanhBaoPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private final CanhBaoService canhBaoService = new CanhBaoService();

    private JTable tableCanhBao;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbFilterMuc;
    private JComboBox<String> cbFilterTuVan;
    private JTextField txtSearch;
    private List<CanhBaoHocVu> currentList;

    private static final String[] COLUMNS = {"ID", "Ma Canh Bao", "Ma SV", "Ho va Ten", "Lop", "Hoc Ky", "Nam Hoc", "Muc Canh Bao", "GPA Xet", "Trang Thai Tu Van", "Ngay QD"};

    public QuanLyCanhBaoPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 14, 12, 14));
        initHeader();
        initToolbar();
        initTable();
        loadData();
    }

    private void initHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        JLabel title = new JLabel("Quan ly Canh bao Hoc vu");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        h.add(title, BorderLayout.WEST);
        add(h, BorderLayout.NORTH);
    }

    private void initToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(6, 10, 6, 10)
        ));

        // Quet tu dong
        JButton btnScan = createBtn("Quet tu dong Canh bao", UITheme.DANGER, Color.WHITE);
        btnScan.addActionListener(e -> onScanCanhBao());
        bar.add(btnScan);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        bar.add(new JLabel("Muc:"));
        cbFilterMuc = new JComboBox<>(new String[]{"--- Tat ca ---", "MUC_1", "MUC_2", "BUOC_THOI_HOC"});
        cbFilterMuc.addActionListener(e -> filterData());
        bar.add(cbFilterMuc);

        bar.add(new JLabel("Tu van:"));
        cbFilterTuVan = new JComboBox<>(new String[]{"--- Tat ca ---", "CHUA_TU_VAN", "DA_TU_VAN", "DANG_THEO_DOI"});
        cbFilterTuVan.addActionListener(e -> filterData());
        bar.add(cbFilterTuVan);

        bar.add(new JLabel("Tim:"));
        txtSearch = new JTextField(12);
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        JButton btnSearch = createBtn("Tim", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = createBtn("Lam moi", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { cbFilterMuc.setSelectedIndex(0); cbFilterTuVan.setSelectedIndex(0); txtSearch.setText(""); loadData(); });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        JButton btnNhatKy = createBtn("Lap Nhat ky Tu van", UITheme.INFO, Color.WHITE);
        btnNhatKy.addActionListener(e -> onLapNhatKy());
        bar.add(btnNhatKy);

        JButton btnExport = createBtn("Xuat Excel", new Color(60, 140, 60), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableCanhBao, "Danh_Sach_Canh_Bao_Hoc_Vu"));
        bar.add(btnExport);

        add(bar, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableCanhBao = new JTable(tableModel);
        UITheme.styleTable(tableCanhBao);
        tableCanhBao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Muc canh bao renderer (col 7)
        tableCanhBao.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Muc 1"))      { comp.setForeground(UITheme.WARNING);     setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("Muc 2")) { comp.setForeground(UITheme.DANGER);      setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("thoi"))  { comp.setForeground(UITheme.DANGER_DARK); setFont(getFont().deriveFont(Font.BOLD)); }
                }
                return comp;
            }
        });

        // Trang thai tu van renderer (col 9)
        tableCanhBao.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Da tu van"))    comp.setForeground(UITheme.SUCCESS);
                    else if (s.contains("theo doi")) comp.setForeground(UITheme.WARNING);
                    else                             comp.setForeground(UITheme.DANGER);
                }
                return comp;
            }
        });

        int[] widths = {40, 130, 80, 150, 70, 70, 90, 130, 70, 120, 100};
        for (int i = 0; i < widths.length; i++) tableCanhBao.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(tableCanhBao);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null) {
            currentList = canhBaoDAO.getCanhBaoByCoVan(currentUser.getMaRef());
        } else {
            currentList = canhBaoDAO.getAllCanhBao();
        }
        renderTable(currentList);
    }

    private void filterData() {
        String muc = (String) cbFilterMuc.getSelectedItem();
        if ("--- Tat ca ---".equals(muc)) muc = "ALL";
        String tv = (String) cbFilterTuVan.getSelectedItem();
        if ("--- Tat ca ---".equals(tv)) tv = "ALL";
        String kw = txtSearch.getText().trim();
        currentList = canhBaoDAO.filterCanhBao(muc, tv, "ALL", kw);
        renderTable(currentList);
    }

    private void renderTable(List<CanhBaoHocVu> list) {
        tableModel.setRowCount(0);
        for (CanhBaoHocVu cb : list) {
            tableModel.addRow(new Object[]{
                cb.getId(), cb.getMaCanhBao(), cb.getMaSv(), cb.getHoTenSv(),
                cb.getMaLop(), "HK " + cb.getHocKy(), cb.getNamHoc(),
                cb.getMucCanhBaoHienThi(),
                String.format("%.2f", cb.getGpaXetDuyet()),
                cb.getTrangThaiTuVanHienThi(),
                cb.getNgayQuyetDinh()
            });
        }
    }

    private void onScanCanhBao() {
        String namHoc = JOptionPane.showInputDialog(this, "Nhap nam hoc can quet (VD: 2023-2024):", "2023-2024");
        if (namHoc == null || namHoc.trim().isEmpty()) return;
        String hocKyStr = JOptionPane.showInputDialog(this, "Nhap hoc ky (1, 2 hoac 3):", "2");
        if (hocKyStr == null) return;
        int hocKy;
        try { hocKy = Integer.parseInt(hocKyStr.trim()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Hoc ky phai la so!", "Loi", JOptionPane.ERROR_MESSAGE); return; }

        int choice = JOptionPane.showConfirmDialog(this,
            "Quet tu dong Canh bao Hoc vu cho Hoc ky " + hocKy + " - " + namHoc + "?",
            "Xac nhan quet", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            int count = canhBaoService.quetCanhBaoHocVu(hocKy, namHoc.trim());
            JOptionPane.showMessageDialog(this,
                "Hoan thanh quet!\nPhat hien va tao: " + count + " ban ghi canh bao moi.",
                "Ket qua quet", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }

    private void onLapNhatKy() {
        int row = tableCanhBao.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui long chon 1 ban ghi canh bao!", "Thong bao", JOptionPane.WARNING_MESSAGE); return; }
        CanhBaoHocVu cb = currentList.get(row);
        LapNhatKyDialog dlg = new LapNhatKyDialog((Frame) SwingUtilities.getWindowAncestor(this), cb, currentUser);
        dlg.setVisible(true);
        if (dlg.isSavedSuccess()) loadData();
    }

    private JButton createBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}