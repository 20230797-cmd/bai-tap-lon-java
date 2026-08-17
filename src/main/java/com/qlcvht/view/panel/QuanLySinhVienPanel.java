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

/**
 * Panel Quan ly Sinh vien ho tro Bo loc thong minh Multi-Filter, Phan quyen RBAC va Excel Smart Import.
 */
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

    private static final String[] COLUMNS = {"Mã SV", "Họ và Tên", "Ngày sinh", "Giới tính", "Email", "Số điện thoại", "Lớp", "Trạng thái Học vụ"};

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
        JLabel title = new JLabel("Quản lý Sinh viên");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro())) {
            JLabel lblRoleInfo = new JLabel("  (Phạm vi phân quyền Cố vấn Học tập: Lớp phụ trách)");
            lblRoleInfo.setFont(UITheme.fontPlain(12));
            lblRoleInfo.setForeground(UITheme.PRIMARY);
            header.add(lblRoleInfo, BorderLayout.EAST);
        } else if (currentUser != null && "QUAN_LY".equals(currentUser.getVaiTro())) {
            JLabel lblRoleInfo = new JLabel("  (Chế độ Trưởng Khoa / Ban Giám hiệu: Chỉ xem dữ liệu - Read-Only)");
            lblRoleInfo.setFont(UITheme.fontPlain(12));
            lblRoleInfo.setForeground(UITheme.WARNING);
            header.add(lblRoleInfo, BorderLayout.EAST);
        }

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

        // 1. Multi-filter Controls
        bar.add(new JLabel("Mã/Tên SV:"));
        txtSearch = new JTextField(10);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        bar.add(new JLabel("Lớp:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- Tất cả ---");
        List<LopHoc> listLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : listLop) cbFilterLop.addItem(l);
        cbFilterLop.addActionListener(e -> filterData());
        bar.add(cbFilterLop);

        bar.add(new JLabel("Trạng thái:"));
        cbFilterTrangThai = new JComboBox<>(new String[]{
            "--- Tất cả ---", "DANG_HOC", "CANH_BAO_1", "CANH_BAO_2", "BUOC_THOI_HOC"
        });
        cbFilterTrangThai.addActionListener(e -> filterData());
        bar.add(cbFilterTrangThai);

        bar.add(new JLabel("Khoảng GPA/TC:"));
        cbFilterGpa = new JComboBox<>(new String[]{
            "--- Tất cả ---", "GPA < 1.5", "1.5 <= GPA < 2.0", "GPA >= 2.0", "Nợ >= 8 TC"
        });
        cbFilterGpa.addActionListener(e -> filterData());
        bar.add(cbFilterGpa);

        JButton btnSearch = createBtn("Lọc", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = createBtn("Làm mới", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterLop.setSelectedIndex(0);
            cbFilterTrangThai.setSelectedIndex(0);
            cbFilterGpa.setSelectedIndex(0);
            loadData();
        });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // 2. RBAC Action Buttons
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getVaiTro());
        boolean isCoVan = currentUser != null && "CO_VAN".equals(currentUser.getVaiTro());

        if (isAdmin || isCoVan) {
            JButton btnThem = createBtn("+ Thêm SV", UITheme.SUCCESS, Color.WHITE);
            btnThem.addActionListener(e -> onThemSinhVien());
            bar.add(btnThem);

            JButton btnSua = createBtn("Sửa SV", UITheme.WARNING, Color.WHITE);
            btnSua.addActionListener(e -> onSuaSinhVien());
            bar.add(btnSua);

            if (isAdmin) {
                JButton btnXoa = createBtn("Xóa SV", UITheme.DANGER, Color.WHITE);
                btnXoa.addActionListener(e -> onXoaSinhVien());
                bar.add(btnXoa);
            }

            JButton btnImport = createBtn("📥 Import Excel", new Color(130, 60, 180), Color.WHITE);
            btnImport.addActionListener(e -> onImportExcel());
            bar.add(btnImport);
        }

        JButton btnDetail = createBtn("Xem chi tiết & GPA", UITheme.INFO, Color.WHITE);
        btnDetail.addActionListener(e -> showChiTiet());
        bar.add(btnDetail);

        JButton btnExport = createBtn("Xuất Excel", new Color(60, 140, 60), Color.WHITE);
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

        // Striped and Color renderer
        tableSinhVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : UITheme.BG_TABLE_STRIPE);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Mức 1"))       { comp.setForeground(UITheme.WARNING);     setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("Mức 2"))  { comp.setForeground(UITheme.DANGER);      setFont(getFont().deriveFont(Font.BOLD)); }
                    else if (s.contains("thôi"))   { comp.setForeground(UITheme.DANGER_DARK); setFont(getFont().deriveFont(Font.BOLD)); }
                    else                           { comp.setForeground(UITheme.SUCCESS); }
                }
                return comp;
            }
        });

        int[] widths = {90, 160, 90, 80, 180, 110, 120, 160};
        for (int i = 0; i < widths.length; i++) tableSinhVien.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

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
        if ("--- Tất cả ---".equals(trangThai)) trangThai = "ALL";

        String gpaSel = (String) cbFilterGpa.getSelectedItem();
        String gpaFilter = "ALL";
        if (gpaSel != null) {
            if (gpaSel.contains("< 1.5")) gpaFilter = "<1.5";
            else if (gpaSel.contains("1.5 <= GPA")) gpaFilter = "1.5-2.0";
            else if (gpaSel.contains(">= 2.0")) gpaFilter = ">=2.0";
            else if (gpaSel.contains("Nợ >= 8")) gpaFilter = "NO_TC_GE_8";
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
                sv.getMaSv(), sv.getHoTen(), sv.getNgaySinh(),
                sv.getGioiTinh(), sv.getEmail(), sv.getSoDienThoai(),
                sv.getTenLop() != null ? sv.getTenLop() : sv.getMaLop(),
                sv.getTrangThaiHienThi()
            });
        }
    }

    private void showChiTiet() {
        int row = tableSinhVien.getSelectedRow();
        if (row < 0) { warn("Vui lòng chọn 1 sinh viên trong bảng để xem chi tiết!"); return; }
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
        if (row < 0) { warn("Vui lòng chọn 1 sinh viên để sửa!"); return; }
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
        if (row < 0) { warn("Vui lòng chọn 1 sinh viên để xóa!"); return; }
        String maSv = (String) tableModel.getValueAt(row, 0);
        String tenSv = (String) tableModel.getValueAt(row, 1);
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa sinh viên:\n" + maSv + " - " + tenSv + "?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (sinhVienDAO.deleteSinhVien(maSv)) {
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa. Sinh viên có thể đã có dữ liệu liên quan (GPA, cảnh báo,...)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onImportExcel() {
        ExcelImportDialog dlg = new ExcelImportDialog((Frame) SwingUtilities.getWindowAncestor(this), ImportType.SINH_VIEN);
        dlg.setVisible(true);
        if (dlg.isImportedSuccessfully()) {
            loadData();
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
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}