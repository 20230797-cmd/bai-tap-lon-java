package com.qlcvht.view.panel;

import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.ThongKeService;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Dashboard - Man hinh tong quan hien thi thong ke nhanh.
 */
public class DashboardPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final ThongKeService thongKeService = new ThongKeService();

    // Stat labels
    private JLabel valTongSv, valBinhThuong, valCB1, valCB2, valBuoc, valDaTuVan;

    public DashboardPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        // Welcome header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        String roleTitle;
        switch (currentUser != null && currentUser.getVaiTro() != null ? currentUser.getVaiTro() : "") {
            case "ADMIN":   roleTitle = "Quan tri vien"; break;
            case "QUAN_LY": roleTitle = "Quan ly Khoa"; break;
            case "CO_VAN":  roleTitle = "Co van Hoc tap"; break;
            default: roleTitle = "";
        }

        JLabel welcome = new JLabel("Chao mung, " + (currentUser != null ? currentUser.getHoTen() : "") + "!");
        welcome.setFont(UITheme.FONT_HEADER);
        welcome.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Vai tro: " + roleTitle + "   |   He thong Quan ly Co van Hoc tap & Canh bao Hoc vu");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        header.add(welcome, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Stats cards grid
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);

        valTongSv    = addStatCard(grid, "TONG SINH VIEN",    "0", new Color(30, 80, 170),   new Color(45, 100, 200));
        valBinhThuong= addStatCard(grid, "DANG HOC BINH THUONG","0",new Color(27, 110, 55),  new Color(46, 140, 78));
        valCB1       = addStatCard(grid, "CANH BAO MUC 1",    "0", new Color(190, 100, 0),   new Color(220, 130, 0));
        valCB2       = addStatCard(grid, "CANH BAO MUC 2",    "0", new Color(170, 50, 50),   new Color(200, 70, 70));
        valBuoc      = addStatCard(grid, "BUOC THOI HOC",      "0", new Color(110, 0, 0),     new Color(150, 20, 20));
        valDaTuVan   = addStatCard(grid, "DA DUOC TU VAN",    "0", new Color(0, 100, 160),   new Color(2, 130, 200));

        add(grid, BorderLayout.CENTER);

        // Bottom tip
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(16, 0, 0, 0));
        JLabel tip = new JLabel("  Chon muc tu sidebar de quan ly sinh vien, canh bao hoc vu, nhat ky tu van va bao cao thong ke.");
        tip.setFont(UITheme.FONT_SMALL);
        tip.setForeground(UITheme.TEXT_SECONDARY);
        bottom.add(tip);
        add(bottom, BorderLayout.SOUTH);
    }

    private JLabel addStatCard(JPanel parent, String title, String initVal, Color colorFrom, Color colorTo) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, colorFrom, getWidth(), getHeight(), colorTo);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.fontBold(12));
        lblTitle.setForeground(new Color(220, 230, 250));

        JLabel lblValue = new JLabel(initVal, SwingConstants.RIGHT);
        lblValue.setFont(UITheme.fontBold(40));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.SOUTH);
        parent.add(card);
        return lblValue;
    }

    private void loadData() {
        Map<String, Integer> stats = thongKeService.getThongKeTongQuan();
        valTongSv.setText(String.valueOf(stats.getOrDefault("tong_sv", 0)));
        valBinhThuong.setText(String.valueOf(stats.getOrDefault("sv_binh_thuong", 0)));
        valCB1.setText(String.valueOf(stats.getOrDefault("cb_muc_1", 0)));
        valCB2.setText(String.valueOf(stats.getOrDefault("cb_muc_2", 0)));
        valBuoc.setText(String.valueOf(stats.getOrDefault("buoc_thoi_hoc", 0)));
        valDaTuVan.setText(String.valueOf(stats.getOrDefault("da_tu_van", 0)));
    }
}