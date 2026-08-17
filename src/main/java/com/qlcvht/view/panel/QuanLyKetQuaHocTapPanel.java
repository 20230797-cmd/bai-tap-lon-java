package com.qlcvht.view.panel;

import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.CanhBaoService;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.ThemSuaDiemDialog;

import com.qlcvht.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class QuanLyKetQuaHocTapPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final CanhBaoService canhBaoService = new CanhBaoService();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearchSv;
    private JComboBox<String> cbNamHoc;
    private JComboBox<String> cbHocKy;
    private JLabel lblTotal;
    private List<KetQuaHocTap> currentList = new ArrayList<>();

    private static final String[] COLUMNS = {
        "STT", "Mã SV", "Họ và Tên SV", "Học Kỳ", "Năm Học", "GPA Học Kỳ", "GPA Tích Lũy (CPA)", "Tín Chỉ Nợ", "Xếp Loại Học Lực"
    };

    public QuanLyKetQuaHocTapPanel(TaiKhoan user) {
        this.currentUser = user;
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

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("📑  QUẢN LÝ BẢNG ĐIỂM & KẾT QUẢ HỌC TẬP (GPA / CPA)");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        lblTotal = new JLabel("Tổng số: 0 kết quả");
        lblTotal.setFont(UITheme.fontBold(13));
        lblTotal.setForeground(UITheme.PRIMARY);
        lblTotal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        header.add(title, BorderLayout.WEST);
        header.add(lblTotal, BorderLayout.EAST);
        topContainer.add(header, BorderLayout.NORTH);

        // Toolbar with WrapLayout
        JPanel bar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        bar.add(new JLabel("Tìm kiếm:"));
        txtSearchSv = new JTextField(11);
        txtSearchSv.setFont(UITheme.FONT_BODY);
        txtSearchSv.putClientProperty("JTextField.placeholderText", "MSSV, họ tên...");
        txtSearchSv.addActionListener(e -> filterData());
        bar.add(txtSearchSv);

        bar.add(new JLabel("Năm học:"));
        cbNamHoc = new JComboBox<>(new String[]{"--- Tất cả năm học ---", "2022-2023", "2023-2024", "2024-2025", "2025-2026"});
        cbNamHoc.addActionListener(e -> filterData());
        bar.add(cbNamHoc);

        bar.add(new JLabel("Học kỳ:"));
        cbHocKy = new JComboBox<>(new String[]{"--- Tất cả kỳ ---", "Học kỳ 1", "Học kỳ 2", "Học kỳ 3 (Hè)"});
        cbHocKy.addActionListener(e -> filterData());
        bar.add(cbHocKy);

        JButton btnSearch = UITheme.createButton("🔍 Tìm", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = UITheme.createButton("🔄 Làm Mới", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { 
            txtSearchSv.setText(""); 
            cbNamHoc.setSelectedIndex(0); 
            cbHocKy.setSelectedIndex(0); 
            loadData(); 
        });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // Nút Nhập GPA
        JButton btnNhap = UITheme.createButton("➕ Nhập Điểm", UITheme.SUCCESS, Color.WHITE);
        btnNhap.addActionListener(e -> onNhapGPA());
        bar.add(btnNhap);

        // Nút Sửa GPA
        JButton btnSua = UITheme.createButton("✏️ Sửa Điểm", UITheme.WARNING, Color.WHITE);
        btnSua.addActionListener(e -> onSuaGPA());
        bar.add(btnSua);

        // Nút Xóa
        boolean isAdmin = "ADMIN".equals(currentUser != null ? currentUser.getVaiTro() : "");
        if (isAdmin) {
            JButton btnXoa = UITheme.createButton("🗑️ Xóa", UITheme.DANGER, Color.WHITE);
            btnXoa.addActionListener(e -> onXoaGPA());
            bar.add(btnXoa);
        }

        // Xuất Excel
        JButton btnExport = UITheme.createButton("📊 Xuất Excel", new Color(46, 125, 50), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(table, "Bang_Diem_Ket_Qua_Hoc_Tap"));
        bar.add(btnExport);

        topContainer.add(bar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {45, 95, 170, 80, 100, 95, 120, 90, 120};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);
        table.getColumnModel().getColumn(6).setCellRenderer(center);
        table.getColumnModel().getColumn(7).setCellRenderer(center);
        table.getColumnModel().getColumn(8).setCellRenderer(center);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) onSuaGPA();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
    }

    public void loadData() {
        currentList = ketQuaDAO.getAllKetQua();
        renderTable(currentList);
    }

    private void filterData() {
        String kw = txtSearchSv.getText().trim().toLowerCase();
        String selNh = (String) cbNamHoc.getSelectedItem();
        String selHk = (String) cbHocKy.getSelectedItem();

        List<KetQuaHocTap> all = ketQuaDAO.getAllKetQua();
        List<KetQuaHocTap> filtered = new ArrayList<>();

        for (KetQuaHocTap kq : all) {
            boolean matchKw = kw.isEmpty()
                || kq.getMaSv().toLowerCase().contains(kw)
                || (kq.getHoTenSv() != null && kq.getHoTenSv().toLowerCase().contains(kw));

            boolean matchNh = true;
            if (selNh != null && !selNh.startsWith("---")) {
                matchNh = kq.getNamHoc() != null && kq.getNamHoc().equals(selNh);
            }

            boolean matchHk = true;
            if (selHk != null && !selHk.startsWith("---")) {
                int hk = selHk.contains("1") ? 1 : (selHk.contains("2") ? 2 : 3);
                matchHk = kq.getHocKy() == hk;
            }

            if (matchKw && matchNh && matchHk) {
                filtered.add(kq);
            }
        }

        currentList = filtered;
        renderTable(currentList);
    }

    private void renderTable(List<KetQuaHocTap> list) {
        tableModel.setRowCount(0);
        int stt = 1;
        for (KetQuaHocTap kq : list) {
            String xepLoai = "Xuất sắc";
            if (kq.getGpaTichLuy() < 2.0) xepLoai = "Yếu / Cảnh báo";
            else if (kq.getGpaTichLuy() < 2.5) xepLoai = "Trung bình";
            else if (kq.getGpaTichLuy() < 3.2) xepLoai = "Khá";
            else if (kq.getGpaTichLuy() < 3.6) xepLoai = "Giỏi";

            tableModel.addRow(new Object[]{
                stt++,
                kq.getMaSv(),
                kq.getHoTenSv() != null ? kq.getHoTenSv() : kq.getMaSv(),
                "Học kỳ " + kq.getHocKy(),
                kq.getNamHoc(),
                String.format("%.2f", kq.getGpaHocKy()),
                String.format("%.2f", kq.getGpaTichLuy()),
                kq.getSoTinChiNo() + " TC",
                xepLoai
            });
        }
        lblTotal.setText("Tổng số: " + list.size() + " kết quả học tập");
    }

    private KetQuaHocTap getSelectedKetQua() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void onNhapGPA() {
        ThemSuaDiemDialog dlg = new ThemSuaDiemDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onSuaGPA() {
        KetQuaHocTap kq = getSelectedKetQua();
        if (kq == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng kết quả cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ThemSuaDiemDialog dlg = new ThemSuaDiemDialog((Frame) SwingUtilities.getWindowAncestor(this), kq);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onXoaGPA() {
        KetQuaHocTap kq = getSelectedKetQua();
        if (kq == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng kết quả cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa kết quả học tập của sinh viên " + kq.getHoTenSv() + " (Kỳ " + kq.getHocKy() + " - " + kq.getNamHoc() + ")?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = ketQuaDAO.delete(kq.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa kết quả học tập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}