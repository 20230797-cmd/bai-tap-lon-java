package com.qlcvht.view.panel;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.CanhBaoService;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.view.dialog.LapNhatKyDialog;

import javax.swing.*;
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

    public QuanLyCanhBaoPanel(TaiKhoan currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initTopToolbar();
        initTable();
        loadData();
    }

    private void initTopToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        // Nút Quét tự động (Tính năng chính)
        JButton btnScan = new JButton("⚡ Quét tự động Cảnh báo");
        btnScan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnScan.setBackground(new Color(220, 53, 69)); // Đỏ nổi bật
        btnScan.setForeground(Color.WHITE);
        btnScan.addActionListener(e -> onScanCanhBao());
        toolbar.add(btnScan);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        // Lọc mức cảnh báo
        toolbar.add(new JLabel("Mức cảnh báo:"));
        cbFilterMuc = new JComboBox<>(new String[]{"--- Tất cả ---", "MUC_1", "MUC_2", "BUOC_THOI_HOC"});
        cbFilterMuc.addActionListener(e -> filterData());
        toolbar.add(cbFilterMuc);

        // Lọc trạng thái tư vấn
        toolbar.add(new JLabel("Tư vấn:"));
        cbFilterTuVan = new JComboBox<>(new String[]{"--- Tất cả ---", "CHUA_TU_VAN", "DA_TU_VAN", "DANG_THEO_DOI"});
        cbFilterTuVan.addActionListener(e -> filterData());
        toolbar.add(cbFilterTuVan);

        // Từ khóa
        txtSearch = new JTextField(12);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toolbar.add(txtSearch);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> filterData());
        toolbar.add(btnSearch);

        JButton btnLapNhatKy = new JButton("📝 Lập Nhật ký Tư vấn");
        btnLapNhatKy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLapNhatKy.setBackground(new Color(24, 119, 242));
        btnLapNhatKy.setForeground(Color.WHITE);
        btnLapNhatKy.addActionListener(e -> onLapNhatKy());
        toolbar.add(btnLapNhatKy);

        JButton btnExport = new JButton("📊 Xuất Excel");
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableCanhBao, "Danh_Sach_Canh_Bao_Hoc_Vu"));
        toolbar.add(btnExport);

        add(toolbar, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"ID", "Mã Cảnh Báo", "Mã SV", "Họ và Tên", "Lớp", "Học Kỳ", "Năm Học", "Mức Cảnh Báo", "GPA Xét", "Trạng Thái Tư Vấn", "Ngày QĐ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableCanhBao = new JTable(tableModel);
        tableCanhBao.setRowHeight(30);
        tableCanhBao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableCanhBao.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableCanhBao.getTableHeader().setBackground(new Color(235, 238, 245));

        // Format màu cho Mức Cảnh Báo
        tableCanhBao.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String str = value.toString();
                    if (str.contains("Mức 1")) {
                        c.setForeground(new Color(230, 124, 11));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (str.contains("Mức 2")) {
                        c.setForeground(new Color(217, 83, 79));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (str.contains("Buộc thôi học")) {
                        c.setForeground(new Color(180, 0, 0));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        });

        // Format màu cho Trạng thái tư vấn
        tableCanhBao.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String str = value.toString();
                    if ("Đã tư vấn".equals(str)) {
                        c.setForeground(new Color(40, 167, 69));
                    } else {
                        c.setForeground(new Color(220, 53, 69));
                    }
                }
                return c;
            }
        });

        add(new JScrollPane(tableCanhBao), BorderLayout.CENTER);
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
        if ("--- Tất cả ---".equals(muc)) muc = "ALL";

        String tv = (String) cbFilterTuVan.getSelectedItem();
        if ("--- Tất cả ---".equals(tv)) tv = "ALL";

        String kw = txtSearch.getText().trim();
        currentList = canhBaoDAO.filterCanhBao(muc, tv, "ALL", kw);
        renderTable(currentList);
    }

    private void renderTable(List<CanhBaoHocVu> list) {
        tableModel.setRowCount(0);
        for (CanhBaoHocVu cb : list) {
            tableModel.addRow(new Object[]{
                cb.getId(),
                cb.getMaCanhBao(),
                cb.getMaSv(),
                cb.getHoTenSv(),
                cb.getMaLop(),
                "Học kỳ " + cb.getHocKy(),
                cb.getNamHoc(),
                cb.getMucCanhBaoHienThi(),
                String.format("%.2f", cb.getGpaXetDuyet()),
                cb.getTrangThaiTuVanHienThi(),
                cb.getNgayQuyetDinh()
            });
        }
    }

    private void onScanCanhBao() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có muốn thực hiện quét tự động Cảnh báo Học vụ cho Học kỳ 2 Năm học 2023-2024 dựa trên dữ liệu GPA mới nhất không?",
            "Xác nhận quét tự động",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            int countNew = canhBaoService.quetCanhBaoHocVu(2, "2023-2024");
            JOptionPane.showMessageDialog(
                this,
                "Hoàn thành quét dữ liệu!\nPhát hiện và tự động tạo: " + countNew + " bản ghi cảnh báo mới.",
                "Kết quả quét Cảnh báo",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadData();
        }
    }

    private void onLapNhatKy() {
        int selectedRow = tableCanhBao.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bản ghi cảnh báo học vụ để lập nhật ký tư vấn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CanhBaoHocVu selectedCB = currentList.get(selectedRow);
        LapNhatKyDialog dialog = new LapNhatKyDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedCB, currentUser);
        dialog.setVisible(true);

        if (dialog.isSavedSuccess()) {
            loadData();
        }
    }
}
