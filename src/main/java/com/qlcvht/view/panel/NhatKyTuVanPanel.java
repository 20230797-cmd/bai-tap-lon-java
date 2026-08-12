package com.qlcvht.view.panel;

import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.ExcelExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NhatKyTuVanPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final NhatKyTuVanDAO nhatKyDAO = new NhatKyTuVanDAO();

    private JTable tableNhatKy;
    private DefaultTableModel tableModel;

    public NhatKyTuVanPanel(TaiKhoan currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initTopToolbar();
        initTable();
        loadData();
    }

    private void initTopToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblTitle = new JLabel("📋 NHẬT KÝ TƯ VẤN CỦA CỐ VẤN HỌC TẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        toolbar.add(lblTitle);

        JButton btnReload = new JButton("Tải lại");
        btnReload.addActionListener(e -> loadData());
        toolbar.add(btnReload);

        JButton btnExport = new JButton("📊 Xuất Excel");
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableNhatKy, "Nhat_Ky_Tu_Van_Sinh_Vien"));
        toolbar.add(btnExport);

        add(toolbar, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"ID", "Ngày Tư Vấn", "Mã SV", "Tên Sinh Viên", "Cố Vấn Học Tập", "Hình Thức", "Nội Dung Trao Đổi", "Giải Pháp Khắc Phục", "Cam Kết Sinh Viên"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableNhatKy = new JTable(tableModel);
        tableNhatKy.setRowHeight(32);
        tableNhatKy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableNhatKy.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableNhatKy.getTableHeader().setBackground(new Color(235, 238, 245));

        add(new JScrollPane(tableNhatKy), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NhatKyTuVan> list;
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null) {
            list = nhatKyDAO.getNhatKyByCoVan(currentUser.getMaRef());
        } else {
            list = nhatKyDAO.getAllNhatKy();
        }

        for (NhatKyTuVan nk : list) {
            tableModel.addRow(new Object[]{
                nk.getId(),
                nk.getNgayTuVan(),
                nk.getMaSv(),
                nk.getHoTenSv(),
                nk.getHoTenCvht(),
                nk.getHinhThuc(),
                nk.getNoiDung(),
                nk.getGiaiPhap(),
                nk.getCamKetSinhVien()
            });
        }
    }
}
