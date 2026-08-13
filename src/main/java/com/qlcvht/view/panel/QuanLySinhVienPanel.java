package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.ChiTietSinhVienDialog;
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
    private List<SinhVien> currentList;

    private static final String[] COLUMNS = {"Ma SV", "Ho va Ten", "Ngay sinh", "Gioi tinh", "Email", "So dien thoai", "Lop", "Trang thai Hoc vu"};

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
        JLabel title = new JLabel("Quan ly Sinh vien");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    private void initToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(6, 10, 6, 10)
        ));

        // Search
        bar.add(new JLabel("Tim kiem:"));
        txtSearch = new JTextField(15);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        // Filter by Lop
        bar.add(new JLabel("Lop:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- Tat ca ---");
        List<LopHoc> listLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : listLop) cbFilterLop.addItem(l);
        cbFilterLop.addActionListener(e -> filterData());
        bar.add(cbFilterLop);

        // Buttons
        JButton btnSearch = createBtn("Tim kiem", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = createBtn("Lam moi", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { txtSearch.setText(""); cbFilterLop.setSelectedIndex(0); loadData(); });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // CRUD buttons
        boolean isAdmin = "ADMIN".equals(currentUser != null ? currentUser.getVaiTro() : "");
        boolean isQL    = "QUAN_LY".equals(currentUser != null ? currentUser.getVaiTro() : "");

        if (isAdmin || isQL) {
            JButton btnThem = createBtn("+ Them SV", UITheme.SUCCESS, Color.WHITE);
            btnThem.addActionListener(e -> onThemSinhVien());
            bar.add(btnThem);

            JButton btnSua = createBtn("Sua SV", UITheme.WARNING, Color.WHITE);
            btnSua.addActionListener(e -> onSuaSinhVien());
            bar.add(btnSua);

            JButton btnXoa = createBtn("Xoa SV", UITheme.DANGER, Color.WHITE);
            btnXoa.addActionListener(e -> onXoaSinhVien());
            bar.add(btnXoa);
        }

        JButton btnDetail = createBtn("Xem chi tiet & GPA", UITheme.INFO, Color.WHITE);
        btnDetail.addActionListener(e -> showChiTiet());
        bar.add(btnDetail);

        JButton btnExport = createBtn("Xuat Excel", new Color(60, 140, 60), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableSinhVien, "Danh_Sach_Sinh_Vien"));
        bar.add(btnExport);

        add(bar, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableSinhVien = new JTable(tableModel);
        UITheme.styleTable(tableSinhVien);
        tableSinhVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color renderer for "Trang thai"
        tableSinhVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Muc 1"))      { comp.setForeground(UITheme.WARNING);     setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("Muc 2")) { comp.setForeground(UITheme.DANGER);      setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("thoi"))  { comp.setForeground(UITheme.DANGER_DARK); setFont(getFont().deriveFont(Font.BOLD)); }
                    else                          { comp.setForeground(UITheme.SUCCESS); }
                }
                return comp;
            }
        });

        // Striped renderer
        tableSinhVien.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : UITheme.BG_TABLE_STRIPE);
                return comp;
            }
        });
        // Re-apply trang thai renderer over striped one
        tableSinhVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : UITheme.BG_TABLE_STRIPE);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Muc 1"))       { comp.setForeground(UITheme.WARNING);     setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("Muc 2"))  { comp.setForeground(UITheme.DANGER);      setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("thoi"))   { comp.setForeground(UITheme.DANGER_DARK); setFont(getFont().deriveFont(Font.BOLD)); }
                    else                           { comp.setForeground(UITheme.SUCCESS); }
                }
                return comp;
            }
        });

        // Col widths
        int[] widths = {90, 160, 90, 80, 180, 110, 120, 160};
        for (int i = 0; i < widths.length; i++) tableSinhVien.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(tableSinhVien);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null) {
            currentList = sinhVienDAO.getSinhVienByCoVan(currentUser.getMaRef());
        } else {
            currentList = sinhVienDAO.getAllSinhVien();
        }
        renderTable(currentList);
    }

    private void filterData() {
        String kw = txtSearch.getText().trim();
        String maLop = "ALL";
        Object sel = cbFilterLop.getSelectedItem();
        if (sel instanceof LopHoc) maLop = ((LopHoc) sel).getMaLop();
        currentList = sinhVienDAO.searchSinhVien(kw, maLop);
        renderTable(currentList);
    }

    private void renderTable(List<SinhVien> list) {
        tableModel.setRowCount(0);
        for (SinhVien sv : list) {
            tableModel.addRow(new Object[]{
                sv.getMaSv(), sv.getHoTen(), sv.getNgaySinh(),
                sv.getGioiTinh(), sv.getEmail(), sv.getSoDienThoai(),
                sv.getTenLop() != null ? sv.getTenLop() : sv.getMaLop(),
                sv.getTrangThaiHienThi()
            });
        }
    }

    private void showChiTiet() {
        int row = tableSinhVien.getSelectedRow();
        if (row < 0) { warn("Vui long chon 1 sinh vien trong bang de xem chi tiet!"); return; }
        String maSv = (String) tableModel.getValueAt(row, 0);
        SinhVien sv = sinhVienDAO.getSinhVienById(maSv);
        if (sv != null) {
            new ChiTietSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv).setVisible(true);
        }
    }

    private void onThemSinhVien() {
        ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onSuaSinhVien() {
        int row = tableSinhVien.getSelectedRow();
        if (row < 0) { warn("Vui long chon 1 sinh vien de sua!"); return; }
        String maSv = (String) tableModel.getValueAt(row, 0);
        SinhVien sv = sinhVienDAO.getSinhVienById(maSv);
        if (sv != null) {
            ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadData();
        }
    }

    private void onXoaSinhVien() {
        int row = tableSinhVien.getSelectedRow();
        if (row < 0) { warn("Vui long chon 1 sinh vien de xoa!"); return; }
        String maSv = (String) tableModel.getValueAt(row, 0);
        String tenSv = (String) tableModel.getValueAt(row, 1);
        int choice = JOptionPane.showConfirmDialog(this,
            "Ban co chac muon xoa sinh vien:\n" + maSv + " - " + tenSv + "?\nHanh dong nay khong the hoan tac!",
            "Xac nhan xoa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (sinhVienDAO.deleteSinhVien(maSv)) {
                JOptionPane.showMessageDialog(this, "Xoa sinh vien thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Khong the xoa. Sinh vien co the da co du lieu lien quan (GPA, canh bao,...)!", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        }
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

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thong bao", JOptionPane.WARNING_MESSAGE);
    }
}