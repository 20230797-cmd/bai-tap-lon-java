package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.ExcelSmartImporter.ImportType;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.ChiTietSinhVienDialog;
import com.qlcvht.view.dialog.ExcelImportDialog;
import com.qlcvht.view.dialog.ThemSuaSinhVienDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLySinhVienPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();

    private JTable tableSinhVien;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<Object> cbFilterLop;
    private JComboBox<String> cbFilterTrangThai;
    private JComboBox<String> cbFilterGpa;

    private List<SinhVien> currentList;

    private static final String[] COLUMNS = {
        "M\u00E3 SV", "H\u1ECD v\u00E0 T\u00EAn", "Ng\u00E0y sinh", "Gi\u1EDBi t\u00EDnh",
        "Email", "S\u1ED1 \u0111i\u1EC7n tho\u1EA1i", "L\u1EDBp", "Tr\u1EA1ng th\u00E1i H\u1ECDc v\u1EE5"
    };

    public QuanLySinhVienPanel(TaiKhoan currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 14, 12, 14));
        initHeader();
        initToolbar();
        initTable();
        loadData();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("H\u1ED3 S\u01A1 Sinh Vi\u00EAn");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro())) {
            JLabel lblRoleInfo = new JLabel("  (Ph\u1EA1m vi ph\u00E2n quy\u1EC1n C\u1ED1 v\u1EA5n: L\u1EDBp ph\u1EE5 tr\u00E1ch)");
            lblRoleInfo.setFont(UITheme.fontPlain(12));
            lblRoleInfo.setForeground(UITheme.PRIMARY);
            header.add(lblRoleInfo, BorderLayout.EAST);
        } else if (currentUser != null && "QUAN_LY".equals(currentUser.getVaiTro())) {
            JLabel lblRoleInfo = new JLabel("  (Ch\u1EBF \u0111\u1ED9 Qu\u1EA3n l\u00FD Khoa: Gi\u00E1m s\u00E1t & B\u00E1o c\u00E1o)");
            lblRoleInfo.setFont(UITheme.fontPlain(12));
            lblRoleInfo.setForeground(UITheme.WARNING);
            header.add(lblRoleInfo, BorderLayout.EAST);
        }

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    private void initToolbar() {
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 6));
        container.setBackground(UITheme.BG_WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Row 1: Search & Multi-filter
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row1.setOpaque(false);

        row1.add(new JLabel("T\u00ECm ki\u1EBFm:"));
        txtSearch = new JTextField(12);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "M\u00E3 ho\u1EB7c T\u00EAn SV...");
        txtSearch.addActionListener(e -> filterData());
        row1.add(txtSearch);

        row1.add(new JLabel("L\u1EDBp:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- T\u1EA5t c\u1EA3 ---");
        List<LopHoc> listLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : listLop) cbFilterLop.addItem(l);
        cbFilterLop.addActionListener(e -> filterData());
        row1.add(cbFilterLop);

        row1.add(new JLabel("Tr\u1EA1ng th\u00E1i:"));
        cbFilterTrangThai = new JComboBox<>(new String[]{
            "--- T\u1EA5t c\u1EA3 ---", "DANG_HOC", "CANH_BAO_1", "CANH_BAO_2", "BUOC_THOI_HOC"
        });
        cbFilterTrangThai.addActionListener(e -> filterData());
        row1.add(cbFilterTrangThai);

        row1.add(new JLabel("GPA / T\u00EDn ch\u1EC9 n\u1EE3:"));
        cbFilterGpa = new JComboBox<>(new String[]{
            "--- T\u1EA5t c\u1EA3 ---", "GPA < 1.5", "1.5 <= GPA < 2.0", "GPA >= 2.0", "N\u1EE3 >= 8 TC"
        });
        cbFilterGpa.addActionListener(e -> filterData());
        row1.add(cbFilterGpa);

        JButton btnSearch = UITheme.createButton("\uD83D\uDD0D L\u1ECDc D\u1EEF Li\u1EC7u", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        row1.add(btnSearch);

        JButton btnReset = UITheme.createOutlineButton("\uD83D\uDD04 L\u00E0m M\u1EDBi", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterLop.setSelectedIndex(0);
            cbFilterTrangThai.setSelectedIndex(0);
            cbFilterGpa.setSelectedIndex(0);
            loadData();
        });
        row1.add(btnReset);

        // Row 2: Actions
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row2.setOpaque(false);

        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getVaiTro());
        boolean isCoVan = currentUser != null && "CO_VAN".equals(currentUser.getVaiTro());

        if (isAdmin || isCoVan) {
            JButton btnThem = UITheme.createButton("\u2795 Th\u00EAm Sinh Vi\u00EAn", UITheme.SUCCESS, Color.WHITE);
            btnThem.addActionListener(e -> onThemSinhVien());
            row2.add(btnThem);

            JButton btnSua = UITheme.createButton("\u270F\uFE0F S\u1EEDa Th\u00F4ng Tin", UITheme.WARNING, Color.WHITE);
            btnSua.addActionListener(e -> onSuaSinhVien());
            row2.add(btnSua);

            if (isAdmin) {
                JButton btnXoa = UITheme.createButton("\uD83D\uDDD1\uFE0F X\u00F3a SV", UITheme.DANGER, Color.WHITE);
                btnXoa.addActionListener(e -> onXoaSinhVien());
                row2.add(btnXoa);
            }

            JButton btnImport = UITheme.createButton("\uD83D\uDCE5 Import Excel", new Color(109, 40, 217), Color.WHITE);
            btnImport.addActionListener(e -> onImportExcel());
            row2.add(btnImport);
        }

        JButton btnDetail = UITheme.createButton("\uD83D\uDC41\uFE0F Xem H\u1ED3 S\u01A1 360\u00B0", UITheme.INFO, Color.WHITE);
        btnDetail.addActionListener(e -> showChiTiet());
        row2.add(btnDetail);

        JButton btnExport = UITheme.createButton("\uD83D\uDCCA Xu\u1EA5t File Excel", new Color(21, 128, 61), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableSinhVien, "Danh_Sach_Sinh_Vien"));
        row2.add(btnExport);

        container.add(row1);
        container.add(row2);
        add(container, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableSinhVien = new JTable(tableModel);
        UITheme.styleTable(tableSinhVien);
        tableSinhVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableSinhVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : UITheme.BG_TABLE_STRIPE);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("M\u1EE9c 1"))       { comp.setForeground(UITheme.WARNING);     setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("M\u1EE9c 2"))  { comp.setForeground(UITheme.DANGER);      setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("th\u00F4i"))   { comp.setForeground(UITheme.DANGER_DARK); setFont(getFont().deriveFont(Font.BOLD)); }
                    else                                 { comp.setForeground(UITheme.SUCCESS); }
                }
                return comp;
            }
        });

        int[] widths = {90, 160, 90, 80, 180, 110, 120, 160};
        for (int i = 0; i < widths.length; i++) tableSinhVien.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        tableSinhVien.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showChiTiet();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tableSinhVien);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        filterData();
    }

    private void filterData() {
        String kw = txtSearch.getText().trim();

        String maLop = "ALL";
        Object selLop = cbFilterLop.getSelectedItem();
        if (selLop instanceof LopHoc) maLop = ((LopHoc) selLop).getMaLop();

        String trangThai = (String) cbFilterTrangThai.getSelectedItem();
        if (trangThai == null || trangThai.startsWith("---")) trangThai = "ALL";

        String gpaSel = (String) cbFilterGpa.getSelectedItem();
        String gpaFilter = "ALL";
        if (gpaSel != null) {
            if (gpaSel.contains("< 1.5")) gpaFilter = "<1.5";
            else if (gpaSel.contains("1.5 <= GPA")) gpaFilter = "1.5-2.0";
            else if (gpaSel.contains(">= 2.0")) gpaFilter = ">=2.0";
            else if (gpaSel.contains("N\u1EE3 >= 8")) gpaFilter = "NO_TC_GE_8";
        }

        String maCvht = null;
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro())) {
            maCvht = currentUser.getMaRef();
        }

        currentList = sinhVienDAO.filterSinhVienMulti(maLop, trangThai, gpaFilter, kw, maCvht);
        renderTable(currentList);
    }

    private void renderTable(List<SinhVien> list) {
        tableModel.setRowCount(0);
        for (SinhVien sv : list) {
            tableModel.addRow(new Object[]{
                sv.getMaSv(),
                sv.getHoTen(),
                sv.getNgaySinh() != null ? sv.getNgaySinh().toString() : "---",
                sv.getGioiTinh() != null ? sv.getGioiTinh() : "---",
                sv.getEmail() != null ? sv.getEmail() : "---",
                sv.getSoDienThoai() != null ? sv.getSoDienThoai() : "---",
                sv.getTenLop() != null ? sv.getTenLop() : sv.getMaLop(),
                UITheme.formatTrangThaiSinhVien(sv.getTrangThai())
            });
        }
    }

    private SinhVien getSelectedSinhVien() {
        int row = tableSinhVien.getSelectedRow();
        if (row < 0 || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void onThemSinhVien() {
        ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onSuaSinhVien() {
        SinhVien sv = getSelectedSinhVien();
        if (sv == null) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng ch\u1ECDn m\u1ED9t sinh vi\u00EAn c\u1EA7n s\u1EEDa!", "Th\u00F4ng b\u00E1o", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onXoaSinhVien() {
        SinhVien sv = getSelectedSinhVien();
        if (sv == null) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng ch\u1ECDn sinh vi\u00EAn c\u1EA7n x\u00F3a!", "Th\u00F4ng b\u00E1o", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "B\u1EA1n c\u00F3 ch\u1EAFc ch\u1EAFn mu\u1ED1n x\u00F3a sinh vi\u00EAn " + sv.getHoTen() + " (" + sv.getMaSv() + ")?",
            "X\u00E1c nh\u1EADn x\u00F3a", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = sinhVienDAO.deleteSinhVien(sv.getMaSv());
            if (ok) {
                JOptionPane.showMessageDialog(this, "\u0110\u00E3 x\u00F3a sinh vi\u00EAn th\u00E0nh c\u00F4ng!", "Th\u00F4ng b\u00E1o", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "X\u00F3a th\u1EA5t b\u1EA1i!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showChiTiet() {
        SinhVien sv = getSelectedSinhVien();
        if (sv == null) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng ch\u1ECDn m\u1ED9t sinh vi\u00EAn \u0111\u1EC3 xem h\u1ED3 s\u01A1!", "Th\u00F4ng b\u00E1o", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ChiTietSinhVienDialog dlg = new ChiTietSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv);
        dlg.setVisible(true);
    }

    private void onImportExcel() {
        ExcelImportDialog dlg = new ExcelImportDialog((Frame) SwingUtilities.getWindowAncestor(this), ImportType.SINH_VIEN);
        dlg.setVisible(true);
        if (dlg.isImportedSuccessfully()) loadData();
    }
}
