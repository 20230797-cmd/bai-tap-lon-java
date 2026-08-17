package com.qlcvht.view.panel;

import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.ChiTietSinhVienDialog;
import com.qlcvht.view.dialog.LapNhatKyDialog;

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

public class NhatKyTuVanPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final NhatKyTuVanDAO nhatKyDAO = new NhatKyTuVanDAO();
    private final SinhVienDAO svDAO = new SinhVienDAO();

    private JTable tableNhatKy;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblTotal;
    private List<NhatKyTuVan> currentList = new ArrayList<>();

    private static final String[] COLUMNS = {
        "STT", "Ngày Tư Vấn", "Mã SV", "Họ Tên Sinh Viên", "Cố Vấn Học Tập", "Hình Thức", "Nội Dung Trao Đổi", "Nguyên Nhân", "Giải Pháp", "Cam Kết SV"
    };

    public NhatKyTuVanPanel(TaiKhoan user) {
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

        JLabel title = new JLabel("📝  NHẬT KÝ & BIÊN BẢN TƯ VẤN CỦA CỐ VẤN HỌC TẬP");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        lblTotal = new JLabel("Tổng số: 0 biên bản");
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

        // Nút thêm nhật ký
        JButton btnAdd = UITheme.createButton("➕ Lập Biên Bản Mới", UITheme.SUCCESS, Color.WHITE);
        btnAdd.addActionListener(e -> onLapMoi());
        bar.add(btnAdd);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        bar.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(14);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "MSSV, tên SV, CVHT...");
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        JButton btnSearch = UITheme.createButton("🔍 Tìm", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = UITheme.createButton("🔄 Làm Mới", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { txtSearch.setText(""); loadData(); });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        JButton btnDelete = UITheme.createButton("🗑️ Xóa Biên Bản", UITheme.DANGER, Color.WHITE);
        btnDelete.addActionListener(e -> onDelete());
        bar.add(btnDelete);

        JButton btnExport = UITheme.createButton("📊 Xuất Excel", new Color(46, 125, 50), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableNhatKy, "Nhat_Ky_Tu_Van_Hoc_Tap"));
        bar.add(btnExport);

        topContainer.add(bar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableNhatKy = new JTable(tableModel);
        UITheme.styleTable(tableNhatKy);
        tableNhatKy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {45, 95, 85, 150, 150, 120, 220, 160, 160, 160};
        for (int i = 0; i < widths.length && i < tableNhatKy.getColumnCount(); i++) {
            tableNhatKy.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        tableNhatKy.getColumnModel().getColumn(0).setCellRenderer(center);
        tableNhatKy.getColumnModel().getColumn(1).setCellRenderer(center);
        tableNhatKy.getColumnModel().getColumn(2).setCellRenderer(center);
        tableNhatKy.getColumnModel().getColumn(5).setCellRenderer(center);

        tableNhatKy.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    NhatKyTuVan nk = getSelectedNhatKy();
                    if (nk != null) {
                        SinhVien sv = svDAO.getSinhVienById(nk.getMaSv());
                        if (sv != null) {
                            new ChiTietSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(NhatKyTuVanPanel.this), sv).setVisible(true);
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tableNhatKy);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null) {
            currentList = nhatKyDAO.getNhatKyByCoVan(currentUser.getMaRef());
        } else {
            currentList = nhatKyDAO.getAllNhatKy();
        }
        renderTable(currentList);
    }

    private void filterData() {
        String kw = txtSearch.getText().trim().toLowerCase();
        List<NhatKyTuVan> all;
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null) {
            all = nhatKyDAO.getNhatKyByCoVan(currentUser.getMaRef());
        } else {
            all = nhatKyDAO.getAllNhatKy();
        }

        List<NhatKyTuVan> filtered = new ArrayList<>();
        for (NhatKyTuVan nk : all) {
            boolean match = kw.isEmpty()
                || nk.getMaSv().toLowerCase().contains(kw)
                || (nk.getHoTenSv() != null && nk.getHoTenSv().toLowerCase().contains(kw))
                || (nk.getHoTenCvht() != null && nk.getHoTenCvht().toLowerCase().contains(kw));
            if (match) filtered.add(nk);
        }
        currentList = filtered;
        renderTable(currentList);
    }

    private void renderTable(List<NhatKyTuVan> list) {
        tableModel.setRowCount(0);
        int stt = 1;
        for (NhatKyTuVan nk : list) {
            tableModel.addRow(new Object[]{
                stt++,
                nk.getNgayTuVan() != null ? nk.getNgayTuVan().toString() : "---",
                nk.getMaSv(),
                nk.getHoTenSv() != null ? nk.getHoTenSv() : nk.getMaSv(),
                nk.getHoTenCvht() != null ? nk.getHoTenCvht() : nk.getMaCvht(),
                nk.getHinhThuc(),
                nk.getNoiDung(),
                nk.getNguyenNhan() != null ? nk.getNguyenNhan() : "---",
                nk.getGiaiPhap() != null ? nk.getGiaiPhap() : "---",
                nk.getCamKetSinhVien() != null ? nk.getCamKetSinhVien() : "---"
            });
        }
        lblTotal.setText("Tổng số: " + list.size() + " biên bản tư vấn");
    }

    private NhatKyTuVan getSelectedNhatKy() {
        int row = tableNhatKy.getSelectedRow();
        if (row < 0 || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void onLapMoi() {
        LapNhatKyDialog dlg = new LapNhatKyDialog((Frame) SwingUtilities.getWindowAncestor(this), null, currentUser);
        dlg.setVisible(true);
        if (dlg.isSavedSuccess()) loadData();
    }

    private void onDelete() {
        NhatKyTuVan nk = getSelectedNhatKy();
        if (nk == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một biên bản tư vấn cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa biên bản tư vấn của sinh viên " + nk.getHoTenSv() + " ngày " + nk.getNgayTuVan() + "?",
            "Xác nhận xóa biên bản", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = nhatKyDAO.deleteNhatKy(nk.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa biên bản tư vấn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa biên bản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}