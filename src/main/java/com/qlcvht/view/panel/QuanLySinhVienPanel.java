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

import com.qlcvht.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
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
    private JLabel lblTotal;
    private List<SinhVien> currentList = new ArrayList<>();

    private static final String[] COLUMNS = {
        "STT", "Mã Sinh Viên", "Họ và Tên", "Ngày Sinh", "Giới Tính", "Email", "Số Điện Thoại", "Lớp Quản Lý", "Trạng Thái Học Vụ"
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

        // Header Row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("👥  QUẢN LÝ DANH SÁCH & HỒ SƠ SINH VIÊN");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        lblTotal = new JLabel("Tổng số: 0 sinh viên");
        lblTotal.setFont(UITheme.fontBold(13));
        lblTotal.setForeground(UITheme.PRIMARY);
        lblTotal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        header.add(title, BorderLayout.WEST);
        header.add(lblTotal, BorderLayout.EAST);
        topContainer.add(header, BorderLayout.NORTH);

        // Toolbar with WrapLayout to prevent clipping
        JPanel bar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        // Search box
        bar.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(11);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "MSSV, họ tên, email...");
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        // Filter by Lớp
        bar.add(new JLabel("Lớp:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- Tất cả lớp ---");
        List<LopHoc> listLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : listLop) cbFilterLop.addItem(l);
        cbFilterLop.addActionListener(e -> filterData());
        bar.add(cbFilterLop);

        // Filter by Trạng thái
        bar.add(new JLabel("Trạng thái:"));
        cbFilterTrangThai = new JComboBox<>(new String[]{
            "--- Tất cả trạng thái ---",
            "Đang học",
            "Cảnh báo mức 1",
            "Cảnh báo mức 2",
            "Buộc thôi học",
            "Đã tốt nghiệp"
        });
        cbFilterTrangThai.addActionListener(e -> filterData());
        bar.add(cbFilterTrangThai);

        // Nút lọc & làm mới
        JButton btnSearch = UITheme.createButton("🔍 Tìm", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = UITheme.createButton("🔄 Làm Mới", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { 
            txtSearch.setText(""); 
            cbFilterLop.setSelectedIndex(0); 
            cbFilterTrangThai.setSelectedIndex(0); 
            loadData(); 
        });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // Nút Thêm / Sửa / Xóa (Admin / QL)
        boolean isAdminOrQL = "ADMIN".equals(currentUser != null ? currentUser.getVaiTro() : "") 
                           || "QUAN_LY".equals(currentUser != null ? currentUser.getVaiTro() : "");

        if (isAdminOrQL) {
            JButton btnThem = UITheme.createButton("➕ Thêm SV", UITheme.SUCCESS, Color.WHITE);
            btnThem.addActionListener(e -> onThemSinhVien());
            bar.add(btnThem);

            JButton btnImport = UITheme.createButton("📥 Nhập Excel", new Color(0, 120, 90), Color.WHITE);
            btnImport.addActionListener(e -> onImportExcel());
            bar.add(btnImport);

            JButton btnSua = UITheme.createButton("✏️ Sửa", UITheme.WARNING, Color.WHITE);
            btnSua.addActionListener(e -> onSuaSinhVien());
            bar.add(btnSua);

            JButton btnXoa = UITheme.createButton("🗑️ Xóa", UITheme.DANGER, Color.WHITE);
            btnXoa.addActionListener(e -> onXoaSinhVien());
            bar.add(btnXoa);
        }

        JButton btnDetail = UITheme.createButton("🔍 Xem Hồ Sơ 360°", UITheme.INFO, Color.WHITE);
        btnDetail.addActionListener(e -> showChiTiet());
        bar.add(btnDetail);

        JButton btnExport = UITheme.createButton("📊 Xuất Excel", new Color(46, 125, 50), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableSinhVien, "Danh_Sach_Sinh_Vien"));
        bar.add(btnExport);

        topContainer.add(bar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableSinhVien = new JTable(tableModel);
        UITheme.styleTable(tableSinhVien);

        // Widths
        int[] widths = {45, 95, 160, 90, 70, 160, 100, 110, 140};
        for (int i = 0; i < widths.length && i < tableSinhVien.getColumnCount(); i++) {
            tableSinhVien.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Center alignments
        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        tableSinhVien.getColumnModel().getColumn(0).setCellRenderer(center);
        tableSinhVien.getColumnModel().getColumn(1).setCellRenderer(center);
        tableSinhVien.getColumnModel().getColumn(3).setCellRenderer(center);
        tableSinhVien.getColumnModel().getColumn(4).setCellRenderer(center);
        tableSinhVien.getColumnModel().getColumn(6).setCellRenderer(center);
        tableSinhVien.getColumnModel().getColumn(7).setCellRenderer(center);

        // Status badge color renderer
        tableSinhVien.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (value != null && !isSelected) {
                    String str = value.toString();
                    if (str.contains("Cảnh báo mức 2")) {
                        c.setForeground(new Color(200, 30, 30));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (str.contains("Buộc thôi học")) {
                        c.setForeground(new Color(140, 0, 0));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (str.contains("Cảnh báo mức 1")) {
                        c.setForeground(new Color(210, 100, 0));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (str.contains("Đang học")) {
                        c.setForeground(new Color(40, 130, 50));
                        setFont(UITheme.FONT_BODY);
                    } else {
                        c.setForeground(UITheme.TEXT_PRIMARY);
                    }
                }
                return c;
            }
        });

        // Double click -> View 360 Detail
        tableSinhVien.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) showChiTiet();
            }
        });

        JScrollPane sp = new JScrollPane(tableSinhVien);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
    }

    public void loadData() {
        if ("CO_VAN".equals(currentUser != null ? currentUser.getVaiTro() : "") && currentUser.getMaRef() != null) {
            currentList = sinhVienDAO.getSinhVienByCoVan(currentUser.getMaRef());
        } else {
            currentList = sinhVienDAO.getAllSinhVien();
        }
        renderTable(currentList);
    }

    private void filterData() {
        String kw = txtSearch.getText().trim().toLowerCase();
        
        String maLopFilter = "ALL";
        Object selLop = cbFilterLop.getSelectedItem();
        if (selLop instanceof LopHoc) maLopFilter = ((LopHoc) selLop).getMaLop();

        String ttFilter = (String) cbFilterTrangThai.getSelectedItem();

        List<SinhVien> all;
        if ("CO_VAN".equals(currentUser != null ? currentUser.getVaiTro() : "") && currentUser.getMaRef() != null) {
            all = sinhVienDAO.getSinhVienByCoVan(currentUser.getMaRef());
        } else {
            all = sinhVienDAO.getAllSinhVien();
        }

        List<SinhVien> filtered = new ArrayList<>();
        for (SinhVien sv : all) {
            boolean matchKw = kw.isEmpty() 
                || sv.getMaSv().toLowerCase().contains(kw) 
                || sv.getHoTen().toLowerCase().contains(kw)
                || (sv.getEmail() != null && sv.getEmail().toLowerCase().contains(kw));

            boolean matchLop = maLopFilter.equals("ALL") || (sv.getMaLop() != null && sv.getMaLop().equals(maLopFilter));

            boolean matchTt = true;
            if (ttFilter != null && !ttFilter.startsWith("---")) {
                String disp = UITheme.formatTrangThaiSinhVien(sv.getTrangThai());
                matchTt = disp.equalsIgnoreCase(ttFilter);
            }

            if (matchKw && matchLop && matchTt) {
                filtered.add(sv);
            }
        }

        currentList = filtered;
        renderTable(currentList);
    }

    private void renderTable(List<SinhVien> list) {
        tableModel.setRowCount(0);
        int stt = 1;
        for (SinhVien sv : list) {
            tableModel.addRow(new Object[]{
                stt++,
                sv.getMaSv(),
                sv.getHoTen(),
                sv.getNgaySinh() != null ? sv.getNgaySinh().toString() : "---",
                sv.getGioiTinh() != null ? sv.getGioiTinh() : "Nam",
                sv.getEmail() != null ? sv.getEmail() : "---",
                sv.getSoDienThoai() != null ? sv.getSoDienThoai() : "---",
                sv.getTenLop() != null ? sv.getTenLop() : sv.getMaLop(),
                UITheme.formatTrangThaiSinhVien(sv.getTrangThai())
            });
        }
        lblTotal.setText("Tổng số: " + list.size() + " sinh viên");
    }

    private SinhVien getSelectedSinhVien() {
        int row = tableSinhVien.getSelectedRow();
        if (row < 0 || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void showChiTiet() {
        SinhVien sv = getSelectedSinhVien();
        if (sv == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên từ danh sách!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ChiTietSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv).setVisible(true);
    }

    private void onThemSinhVien() {
        ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onSuaSinhVien() {
        SinhVien sv = getSelectedSinhVien();
        if (sv == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên cần sửa thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ThemSuaSinhVienDialog dlg = new ThemSuaSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onXoaSinhVien() {
        SinhVien sv = getSelectedSinhVien();
        if (sv == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa sinh viên: " + sv.getHoTen() + " (" + sv.getMaSv() + ")?\nThao tác này sẽ xóa toàn bộ điểm số, cảnh báo và nhật ký tư vấn liên quan!",
            "Xác nhận xóa sinh viên", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = sinhVienDAO.deleteSinhVien(sv.getMaSv());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onImportExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file Excel danh sách sinh viên (.xlsx)");
        fc.setFileFilter(new FileNameExtensionFilter("Excel File (*.xlsx)", "xlsx"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                List<SinhVien> imported = ExcelExporter.importSinhVienFromExcel(file);
                int count = 0;
                for (SinhVien sv : imported) {
                    if (sinhVienDAO.addSinhVien(sv)) count++;
                }
                JOptionPane.showMessageDialog(this, "Đã nhập thành công " + count + " / " + imported.size() + " sinh viên từ file Excel!", "Kết quả nhập", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi đọc file Excel: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}