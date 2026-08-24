package com.qlcvht.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {

    public static final Color PRIMARY          = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK     = new Color(29, 78, 216);
    public static final Color PRIMARY_LIGHT    = new Color(239, 246, 255);
    public static final Color PRIMARY_HOVER    = new Color(59, 130, 246);

    public static final Color SUCCESS          = new Color(16, 185, 129);
    public static final Color SUCCESS_DARK     = new Color(5, 150, 105);
    public static final Color SUCCESS_LIGHT    = new Color(236, 253, 245);

    public static final Color WARNING          = new Color(245, 158, 11);
    public static final Color WARNING_DARK     = new Color(217, 119, 6);
    public static final Color WARNING_LIGHT    = new Color(254, 243, 199);

    public static final Color DANGER           = new Color(239, 68, 68);
    public static final Color DANGER_DARK      = new Color(185, 28, 28);
    public static final Color DANGER_LIGHT     = new Color(254, 242, 242);

    public static final Color INFO             = new Color(14, 165, 233);
    public static final Color INFO_LIGHT       = new Color(240, 249, 255);

    public static final Color PURPLE           = new Color(139, 92, 246);
    public static final Color PURPLE_LIGHT     = new Color(245, 243, 255);

    public static final Color BG_MAIN          = new Color(248, 250, 252);
    public static final Color BG_WHITE         = Color.WHITE;
    public static final Color BG_HEADER        = new Color(15, 23, 42);
    public static final Color BG_SIDEBAR       = new Color(15, 23, 42);
    public static final Color BG_SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color BG_SIDEBAR_ACTIVE= new Color(37, 99, 235);
    public static final Color BG_TABLE_HEADER  = new Color(241, 245, 249);
    public static final Color BG_TABLE_STRIPE  = new Color(250, 251, 253);

    public static final Color TEXT_PRIMARY     = new Color(15, 23, 42);
    public static final Color TEXT_SECONDARY   = new Color(100, 116, 139);
    public static final Color TEXT_WHITE       = Color.WHITE;
    public static final Color TEXT_SIDEBAR     = new Color(203, 213, 225);

    public static final Color BORDER_LIGHT     = new Color(226, 232, 240);
    public static final Color BORDER_MEDIUM    = new Color(203, 213, 225);

    public static final String FONT_FAMILY     = "Segoe UI";

    public static Font font(int style, int size) { return new Font(FONT_FAMILY, style, size); }
    public static Font fontPlain(int size) { return font(Font.PLAIN, size); }
    public static Font fontBold(int size)  { return font(Font.BOLD, size); }

    public static final Font FONT_HEADER       = fontBold(18);
    public static final Font FONT_SUBHEADER    = fontBold(14);
    public static final Font FONT_BODY         = fontPlain(13);
    public static final Font FONT_BODY_BOLD    = fontBold(13);
    public static final Font FONT_SMALL        = fontPlain(11);
    public static final Font FONT_TABLE        = fontPlain(13);
    public static final Font FONT_TABLE_HEADER = fontBold(13);
    public static final Font FONT_BTN          = fontBold(12);
    public static final Font FONT_BTN_LARGE    = fontBold(13);

    public static void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(FONT_TABLE);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(new Color(30, 41, 59));
        table.setGridColor(BORDER_LIGHT);
        table.setShowGrid(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(BG_TABLE_HEADER);
        header.setForeground(new Color(51, 65, 85));
        header.setPreferredSize(new Dimension(100, 38));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_MEDIUM));
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = bg;
                if (!isEnabled()) c = new Color(203, 213, 225);
                else if (getModel().isPressed()) c = bg.darker();
                else if (getModel().isRollover()) c = new Color(Math.min(255, bg.getRed()+15), Math.min(255, bg.getGreen()+15), Math.min(255, bg.getBlue()+15));
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(fontBold(11));
        lbl.setForeground(fg);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(4, 10, 4, 10));
        return lbl;
    }

    public static DefaultTableCellRenderer createCenterRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        return renderer;
    }

    public static String formatTrangThaiSinhVien(String raw) {
        if (raw == null) return "?ang h?c";
        switch (raw) {
            case "CANH_BAO_1": return "C?nh b?o m?c 1";
            case "CANH_BAO_2": return "C?nh b?o m?c 2";
            case "BUOC_THOI_HOC": return "Bu?c th?i h?c";
            case "DA_TOT_NGHIEP": return "?? t?t nghi?p";
            default: return "?ang h?c";
        }
    }

    public static String formatMucCanhBao(String raw) {
        if (raw == null) return "-";
        switch (raw) {
            case "MUC_1": return "M?c 1 (GPA < 2.0)";
            case "MUC_2": return "M?c 2 (GPA < 1.5)";
            case "BUOC_THOI_HOC": return "Bu?c th?i h?c (GPA < 1.0)";
            default: return raw;
        }
    }

    public static String formatTrangThaiTuVan(String raw) {
        if (raw == null) return "Ch?a t? v?n";
        switch (raw) {
            case "DA_TU_VAN": return "?? t? v?n";
            case "DANG_THEO_DOI": return "?ang theo d?i";
            default: return "Ch?a t? v?n";
        }
    }
}
