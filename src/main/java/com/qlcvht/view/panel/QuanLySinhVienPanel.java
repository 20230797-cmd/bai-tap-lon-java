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

import com.qlcvht.util.WrapLayout;

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
    private JLabel lblTotal;

    private List<SinhVien> currentList;

    private static final String[] COLUMNS = {
        "Mã SV", "Họ và Tên", "Ngày sinh", "Giới tính",
        "Email", "Số điện thoại", "Lớp", "Trạng thái Học vụ"
    };

    public QuanLySinhVienPanel(TaiKhoan currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        initTopPanel();
        initTable();
        loadData();
    }

    private void initTopPanel() {
        JPanel topContainer = new JPanel(new BorderLayout(0, 8));
        topContainer.setOpaque(false);

        // Header Title & Info
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("HỒ SƠ VÀ THÔNG TIN SINH VIÊN");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);

        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro())) {
            JLabel lblRoleInfo = new JLabel("(Phạm vi Cố vấn: Lớp phụ trách)");
            lblRoleInfo.setFont(UITheme.fontPlain(12));
            lblRoleInfo.setForeground(UITheme.PRIMARY);
            rightHeader.add(lblRoleInfo);
        } else if (currentUser != null && "QUAN_LY".equals(currentUser.getVaiTro())) {
            JLabel lblRoleInfo = new JLabel("(Chế độ Quản lý Khoa: Giám sát & Báo cáo)");
            lblRoleInfo.setFont(UITheme.fontPlain(12));
            lblRoleInfo.setForeground(UITheme.WARNING);
            rightHeader.add(lblRoleInfo);
        }

        lblTotal = new JLabel("Tổng số: 0 sinh viên");
        lblTotal.setFont(UITheme.fontBold(13));
        lblTotal.setForeground(UITheme.PRIMARY);
        lblTotal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        rightHeader.add(lblTotal);

        header.add(title, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        topContainer.add(header, BorderLayout.NORTH);

        // Toolbar with WrapLayout to ensure NO buttons or filters are cut off
        JPanel toolbar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(UITheme.BG_WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        toolbar.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(11);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Mã hoặc Tên SV...");
        txtSearch.addActionListener(e -> filterData());
        toolbar.add(txtSearch);

        toolbar.add(new JLabel("Lớp:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- Tất cả ---");
        List<LopHoc> listLop;
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null && !currentUser.getMaRef().isBlank()) {
            listLop = coVanDAO.getLopHocByCoVan(currentUser.getMaRef());
        } else {
            listLop = coVanDAO.getAllLopHoc();
        }
        for (LopHoc l : listLop) cbFilterLop.addItem(l);
        cbFilterLop.addActionListener(e -> filterData());
        toolbar.add(cbFilterLop);

        toolbar.add(new JLabel("Trạng thái:"));
        cbFilterTrangThai = new JComboBox<>(new String[]{
            "--- Tất cả ---", "DANG_HOC", "CANH_BAO_1", "CANH_BAO_2", "BUOC_THOI_HOC"
        });
        cbFilterTrangThai.addActionListener(e -> filterData());
        toolbar.add(cbFilterTrangThai);

        toolbar.add(new JLabel("GPA / Nợ:"));
        cbFilterGpa = new JComboBox<>(new String[]{
            "--- Tất cả ---", "GPA < 1.5", "1.5 <= GPA < 2.0", "GPA >= 2.0", "Nợ >= 8 TC"
        });
        cbFilterGpa.addActionListener(e -> filterData());
        toolbar.add(cbFilterGpa);

        JButton btnSearch = UITheme.createButton("Lọc Dữ Liệu", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        toolbar.add(btnSearch);

        JButton btnReset = UITheme.createButton("Làm Mới", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterLop.setSelectedIndex(0);
            cbFilterTrangThai.setSelectedIndex(0);
            cbFilterGpa.setSelectedIndex(0);
            loadData();
        });
        toolbar.add(btnReset);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        boolean isQuanLy = currentUser != null && "QUAN_LY".equals(currentUser.getVaiTro());
        boolean isCoVan  = currentUser != null && "CO_VAN".equals(currentUser.getVaiTro());

        if (isQuanLy || isCoVan) {
            JButton btnThem = UITheme.createButton("+ Thêm Sinh Viên", UITheme.SUCCESS, Color.WHITE);
            btnThem.addActionListener(e -> onThemSinhVien());
            toolbar.add(btnThem);

            JButton btnSua = UITheme.createButton("Sửa Thông Tin", UITheme.WARNING, Color.WHITE);
            btnSua.addActionListener(e -> onSuaSinhVien());
            toolbar.add(btnSua);

            if (isQuanLy) {
                JButton btnXoa = UITheme.createButton("Xóa SV", UITheme.DANGER, Color.WHITE);
                btnXoa.addActionListener(e -> onXoaSinhVien());
                toolbar.add(btnXoa);
            }

            JButton btnImport = UITheme.createButton("Import Excel", new Color(109, 40, 217), Color.WHITE);
            btnImport.addActionListener(e -> onImportExcel());
            toolbar.add(btnImport);
        }

        JButton btnDetail = UITheme.createButton("Xem Hồ Sơ 360°", UITheme.INFO, Color.WHITE);
        btnDetail.addActionListener(e -> showChiTiet());
        toolbar.add(btnDetail);

        JButton btnExport = UITheme.createButton("Xuất File Excel", new Color(21, 128, 61), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableSinhVien, "Danh_Sach_Sinh_Vien"));
        toolbar.add(btnExport);

        topContainer.add(toolbar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);
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
        if (lblTotal != null) {
            lblTotal.setText("Tổng số: " + list.size() + " sinh viên");
        }
    }

    private SinhVien getSelectedSinhVien() {
        int row = tableSinhVien.getSelectedRow();
        if (row < 0 || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void onThemSinhVien() {
        ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), null, currentUser);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onSuaSinhVien() {
        SinhVien sv = getSelectedSinhVien();
        if (sv == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv, currentUser);
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
