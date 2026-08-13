package com.qlcvht.view.panel;

import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NhatKyTuVanPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final NhatKyTuVanDAO nhatKyDAO = new NhatKyTuVanDAO();

    private JTable tableNhatKy;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    private static final String[] COLUMNS = {"ID", "Ngay Tu Van", "Ma SV", "Ten Sinh Vien", "Co Van Hoc Tap", "Hinh Thuc", "Noi Dung Trao Doi", "Giai Phap Khac Phuc", "Cam Ket SV"};

    public NhatKyTuVanPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 14, 12, 14));
        initHeader();
        initToolbar();
        initTable();
        loadData();
    }

    private void initHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        JLabel title = new JLabel("Nhat ky Tu van - Co van Hoc tap");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        h.add(title, BorderLayout.WEST);
        add(h, BorderLayout.NORTH);
    }

    private void initToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(6, 10, 6, 10)
        ));

        bar.add(new JLabel("Tim kiem (Ma SV, Ten SV, Ten CVHT):"));
        txtSearch = new JTextField(18);
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        JButton btnSearch = createBtn("Tim kiem", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = createBtn("Lam moi", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { txtSearch.setText(""); loadData(); });
        bar.add(btnReset);

        JButton btnExport = createBtn("Xuat Excel", new Color(60, 140, 60), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableNhatKy, "Nhat_Ky_Tu_Van"));
        bar.add(btnExport);

        add(bar, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableNhatKy = new JTable(tableModel);
        UITheme.styleTable(tableNhatKy);
        tableNhatKy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {40, 100, 80, 150, 150, 100, 200, 180, 180};
        for (int i = 0; i < widths.length; i++) tableNhatKy.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(tableNhatKy);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NhatKyTuVan> list;
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null) {
            list = nhatKyDAO.getNhatKyByCoVan(currentUser.getMaRef());
        } else {
            list = nhatKyDAO.getAllNhatKy();
        }
        renderTable(list);
    }

    private void filterData() {
        String kw = txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        List<NhatKyTuVan> all;
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null) {
            all = nhatKyDAO.getNhatKyByCoVan(currentUser.getMaRef());
        } else {
            all = nhatKyDAO.getAllNhatKy();
        }
        for (NhatKyTuVan nk : all) {
            boolean match = kw.isEmpty()
                || nk.getMaSv().toLowerCase().contains(kw)
                || (nk.getHoTenSv() != null && nk.getHoTenSv().toLowerCase().contains(kw))
                || (nk.getHoTenCvht() != null && nk.getHoTenCvht().toLowerCase().contains(kw));
            if (match) addRow(nk);
        }
    }

    private void renderTable(List<NhatKyTuVan> list) {
        tableModel.setRowCount(0);
        for (NhatKyTuVan nk : list) addRow(nk);
    }

    private void addRow(NhatKyTuVan nk) {
        tableModel.addRow(new Object[]{
            nk.getId(), nk.getNgayTuVan(), nk.getMaSv(),
            nk.getHoTenSv(), nk.getHoTenCvht(), nk.getHinhThuc(),
            nk.getNoiDung(), nk.getGiaiPhap(), nk.getCamKetSinhVien()
        });
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
}