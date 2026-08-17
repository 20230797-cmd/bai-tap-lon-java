package com.qlcvht.view.panel;

import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.ThongKeService;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Dashboard - Màn hình Tổng quan hiển thị KPI, thống kê cảnh báo học vụ và phân tầng rủi ro.
 */
public class DashboardPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final ThongKeService thongKeService = new ThongKeService();

    // Stat labels
    private JLabel valTongSv, valBinhThuong, valCB1, valCB2, valBuoc, valDaTuVan;
    private JLabel valTier1, valTier2, valTier3, valChuaTuVan;

    public DashboardPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_MAIN);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setBackground(UITheme.BG_MAIN);
        mainContent.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Welcome header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        String roleTitle;
        switch (currentUser != null && currentUser.getVaiTro() != null ? currentUser.getVaiTro() : "") {
            case "ADMIN":   roleTitle = "Quản trị viên Hệ thống"; break;
            case "QUAN_LY": roleTitle = "Ban Quản lý Khoa / Đào tạo"; break;
            case "CO_VAN":  roleTitle = "Cố vấn Học tập"; break;
            default: roleTitle = "";
        }

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGrp.setOpaque(false);
        JLabel welcome = new JLabel("Chào mừng trở lại, " + (currentUser != null ? currentUser.getHoTen() : "") + "!");
        welcome.setFont(UITheme.fontBold(20));
        welcome.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Vai trò: " + roleTitle + "   |   Hệ thống Quản lý Cố vấn Học tập & Cảnh báo Học vụ (CNJ09)");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        titleGrp.add(welcome);
        titleGrp.add(sub);
        header.add(titleGrp, BorderLayout.WEST);

        JButton btnRefresh = UITheme.createButton("🔄 Làm Mới Dữ Liệu", UITheme.PRIMARY, Color.WHITE);
        btnRefresh.addActionListener(e -> loadData());
        header.add(btnRefresh, BorderLayout.EAST);

        mainContent.add(header, BorderLayout.NORTH);

        // Center: Stats grid & Tier banner
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);

        // Top 6 KPI cards
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);

        valTongSv     = addStatCard(grid, "TỔNG SỐ SINH VIÊN",      "0", new Color(30, 80, 170),   new Color(45, 100, 200));
        valBinhThuong = addStatCard(grid, "ĐANG HỌC BÌNH THƯỜNG",   "0", new Color(27, 110, 55),  new Color(46, 140, 78));
        valCB1        = addStatCard(grid, "CẢNH BÁO MỨC 1",         "0", new Color(190, 100, 0),   new Color(220, 130, 0));
        valCB2        = addStatCard(grid, "CẢNH BÁO MỨC 2",         "0", new Color(170, 50, 50),   new Color(200, 70, 70));
        valBuoc       = addStatCard(grid, "BUỘC THÔI HỌC",          "0", new Color(110, 0, 0),     new Color(150, 20, 20));
        valDaTuVan    = addStatCard(grid, "ĐÃ ĐƯỢC TƯ VẤN",         "0", new Color(0, 100, 160),   new Color(2, 130, 200));

        center.add(grid, BorderLayout.CENTER);

        // Tier Summary Box (Bottom)
        JPanel tierBox = new JPanel(new GridLayout(1, 4, 10, 0));
        tierBox.setOpaque(false);
        tierBox.setPreferredSize(new Dimension(800, 80));

        valTier1 = addMiniCard(tierBox, "TIER 1 (GPA ≥ 3.2)", "0 SV", new Color(46, 125, 50));
        valTier2 = addMiniCard(tierBox, "TIER 2 (2.0 ≤ GPA < 3.2)", "0 SV", new Color(25, 118, 210));
        valTier3 = addMiniCard(tierBox, "TIER 3 (NGUY CƠ / GPA < 2.0)", "0 SV", new Color(198, 40, 40));
        valChuaTuVan = addMiniCard(tierBox, "CẢNH BÁO CHƯA TƯ VẤN", "0 SV", new Color(230, 119, 0));

        center.add(tierBox, BorderLayout.SOUTH);
        mainContent.add(center, BorderLayout.CENTER);

        // Bottom instruction tip
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        JLabel tip = new JLabel("💡 Mẹo: Sử dụng thanh điều hướng bên trái để quét tự động cảnh báo học vụ, xem hồ sơ 360°, ghi nhật ký tư vấn hoặc xuất báo cáo Excel.");
        tip.setFont(UITheme.FONT_SMALL);
        tip.setForeground(UITheme.TEXT_SECONDARY);
        bottom.add(tip);
        mainContent.add(bottom, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel addStatCard(JPanel parent, String title, String initVal, Color colorFrom, Color colorTo) {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, colorFrom, getWidth(), getHeight(), colorTo);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.fontBold(11));
        lblTitle.setForeground(new Color(220, 230, 250));

        JLabel lblValue = new JLabel(initVal, SwingConstants.RIGHT);
        lblValue.setFont(UITheme.fontBold(32));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.SOUTH);
        parent.add(card);
        return lblValue;
    }

    private JLabel addMiniCard(JPanel parent, String title, String initVal, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.fontBold(10));
        lblTitle.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblVal = new JLabel(initVal);
        lblVal.setFont(UITheme.fontBold(18));
        lblVal.setForeground(borderColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        parent.add(card);
        return lblVal;
    }

    public void loadData() {
        Map<String, Integer> stats = thongKeService.getThongKeTongQuan();
        valTongSv.setText(String.valueOf(stats.getOrDefault("tong_sv", 0)));
        valBinhThuong.setText(String.valueOf(stats.getOrDefault("sv_binh_thuong", 0)));
        valCB1.setText(String.valueOf(stats.getOrDefault("cb_muc_1", 0)));
        valCB2.setText(String.valueOf(stats.getOrDefault("cb_muc_2", 0)));
        valBuoc.setText(String.valueOf(stats.getOrDefault("buoc_thoi_hoc", 0)));
        valDaTuVan.setText(String.valueOf(stats.getOrDefault("da_tu_van", 0)));

        valTier1.setText(stats.getOrDefault("tier_1", 0) + " SV");
        valTier2.setText(stats.getOrDefault("tier_2", 0) + " SV");
        valTier3.setText(stats.getOrDefault("tier_3", 0) + " SV");
        valChuaTuVan.setText(stats.getOrDefault("chua_tu_van", 0) + " SV");
    }
}