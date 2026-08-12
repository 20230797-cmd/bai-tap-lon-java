package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.view.dialog.ChiTietSinhVienDialog;

import javax.swing.*;
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

    public QuanLySinhVienPanel(TaiKhoan currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initTopToolbar();
        initTable();
        loadData();
    }

    private void initTopToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        toolbar.add(new JLabel("🔍 Tìm kiếm:"));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toolbar.add(txtSearch);

        toolbar.add(new JLabel(" 🏫 Lớp học:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- Tất cả Lớp ---");
        List<LopHoc> listLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : listLop) {
            cbFilterLop.addItem(l);
        }
        toolbar.add(cbFilterLop);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(new Color(24, 119, 242));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        toolbar.add(btnSearch);

        JButton btnReset = new JButton("Tải lại");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterLop.setSelectedIndex(0);
            loadData();
        });
        toolbar.add(btnReset);

        JButton btnDetail = new JButton("👁️ Xem Hồ sơ & GPA");
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDetail.setBackground(new Color(40, 167, 69));
        btnDetail.setForeground(Color.WHITE);
        btnDetail.addActionListener(e -> showChiTiet());
        toolbar.add(btnDetail);

        JButton btnExport = new JButton("📊 Xuất Excel");
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableSinhVien, "Danh_Sach_Sinh_Vien"));
        toolbar.add(btnExport);

        add(toolbar, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"Mã SV", "Họ và Tên", "Ngày sinh", "Giới tính", "Email", "Số điện thoại", "Lớp", "Trạng thái Học vụ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableSinhVien = new JTable(tableModel);
        tableSinhVien.setRowHeight(30);
        tableSinhVien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableSinhVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableSinhVien.getTableHeader().setBackground(new Color(235, 238, 245));

        // Format màu trạng thái học vụ
        tableSinhVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String str = value.toString();
                    if (str.contains("Cảnh báo Mức 1")) {
                        c.setForeground(new Color(230, 124, 11)); // Cam
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (str.contains("Cảnh báo Mức 2")) {
                        c.setForeground(new Color(217, 83, 79)); // Đỏ nhạt
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (str.contains("Buộc thôi học")) {
                        c.setForeground(new Color(180, 0, 0)); // Đỏ đậm
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(40, 167, 69)); // Xanh lá
                    }
                }
                return c;
            }
        });

        add(new JScrollPane(tableSinhVien), BorderLayout.CENTER);
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
        Object selLop = cbFilterLop.getSelectedItem();
        if (selLop instanceof LopHoc) {
            maLop = ((LopHoc) selLop).getMaLop();
        }

        currentList = sinhVienDAO.searchSinhVien(kw, maLop);
        renderTable(currentList);
    }

    private void renderTable(List<SinhVien> list) {
        tableModel.setRowCount(0);
        for (SinhVien sv : list) {
            tableModel.addRow(new Object[]{
                sv.getMaSv(),
                sv.getHoTen(),
                sv.getNgaySinh(),
                sv.getGioiTinh(),
                sv.getEmail(),
                sv.getSoDienThoai(),
                sv.getTenLop() != null ? sv.getTenLop() : sv.getMaLop(),
                sv.getTrangThaiHienThi()
            });
        }
    }

    private void showChiTiet() {
        int selectedRow = tableSinhVien.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 sinh viên trong bảng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maSv = (String) tableModel.getValueAt(selectedRow, 0);
        SinhVien sv = sinhVienDAO.getSinhVienById(maSv);
        if (sv != null) {
            ChiTietSinhVienDialog dialog = new ChiTietSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(this), sv);
            dialog.setVisible(true);
        }
    }
}
