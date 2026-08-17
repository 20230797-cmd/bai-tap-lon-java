package com.qlcvht.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * UITheme - Quản lý màu sắc, font chữ và phong cách giao diện FlatLaf cho toàn bộ ứng dụng.
 */
public class UITheme {
    public static final Color PRIMARY          = new Color(25, 118, 210);
    public static final Color PRIMARY_DARK     = new Color(13, 71, 161);
    public static final Color PRIMARY_LIGHT    = new Color(227, 242, 253);
    public static final Color SECONDARY        = new Color(55, 71, 79);
    public static final Color SUCCESS          = new Color(46, 125, 50);
    public static final Color SUCCESS_LIGHT    = new Color(232, 245, 233);
    public static final Color WARNING          = new Color(230, 119, 0);
    public static final Color WARNING_LIGHT    = new Color(255, 243, 224);
    public static final Color DANGER           = new Color(198, 40, 40);
    public static final Color DANGER_DARK      = new Color(140, 0, 0);
    public static final Color DANGER_LIGHT     = new Color(255, 235, 238);
    public static final Color INFO             = new Color(2, 119, 189);
    public static final Color INFO_LIGHT       = new Color(225, 245, 254);
    public static final Color PURPLE           = new Color(106, 27, 154);
    public static final Color PURPLE_LIGHT     = new Color(243, 229, 245);
    public static final Color BG_MAIN          = new Color(245, 247, 250);
    public static final Color BG_WHITE         = Color.WHITE;
    public static final Color BG_HEADER        = new Color(25, 118, 210);
    public static final Color BG_SIDEBAR       = new Color(30, 40, 51);
    public static final Color BG_SIDEBAR_HOVER = new Color(42, 55, 70);
    public static final Color BG_SIDEBAR_ACTIVE= new Color(25, 118, 210);
    public static final Color BG_TABLE_HEADER  = new Color(232, 236, 243);
    public static final Color BG_TABLE_STRIPE  = new Color(250, 251, 253);
    public static final Color TEXT_PRIMARY     = new Color(30, 30, 30);
    public static final Color TEXT_SECONDARY   = new Color(100, 100, 100);
    public static final Color TEXT_WHITE       = Color.WHITE;
    public static final Color TEXT_SIDEBAR     = new Color(200, 210, 220);
    public static final Color BORDER_LIGHT     = new Color(220, 225, 235);
    public static final Color BORDER_MEDIUM    = new Color(200, 205, 215);
    public static final String FONT_FAMILY     = "Segoe UI";

    public static Font fontBold(int size)   { return new Font(FONT_FAMILY, Font.BOLD, size); }
    public static Font fontPlain(int size)  { return new Font(FONT_FAMILY, Font.PLAIN, size); }
    public static Font fontItalic(int size) { return new Font(FONT_FAMILY, Font.ITALIC, size); }

    public static final Font FONT_HEADER       = fontBold(18);
    public static final Font FONT_SUBHEADER    = fontBold(14);
    public static final Font FONT_BODY         = fontPlain(13);
    public static final Font FONT_BODY_BOLD    = fontBold(13);
    public static final Font FONT_SMALL        = fontPlain(11);
    public static final Font FONT_TABLE        = fontPlain(13);
    public static final Font FONT_TABLE_HEADER = fontBold(13);
    public static final Font FONT_BTN          = fontBold(12);
    public static final Font FONT_BTN_LARGE    = fontBold(14);
    public static final Insets BTN_INSETS      = new Insets(7, 14, 7, 14);
    public static final int PAD_SMALL   = 5;
    public static final int PAD_MEDIUM  = 10;
    public static final int PAD_LARGE   = 15;
    public static final int PAD_XLARGE  = 20;

    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_TABLE);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(BG_TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setPreferredSize(new Dimension(100, 36));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_LIGHT);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(BTN_INSETS);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return btn;
    }

    public static String formatTrangThaiSinhVien(String raw) {
        if (raw == null) return "Đang học";
        switch (raw) {
            case "CANH_BAO_1": return "Cảnh báo mức 1";
            case "CANH_BAO_2": return "Cảnh báo mức 2";
            case "BUOC_THOI_HOC": return "Buộc thôi học";
            case "DA_TOT_NGHIEP": return "Đã tốt nghiệp";
            case "DANG_HOC":
            default: return "Đang học";
        }
    }

    public static String formatMucCanhBao(String raw) {
        if (raw == null) return "-";
        switch (raw) {
            case "MUC_1": return "Mức 1 (GPA < 2.0)";
            case "MUC_2": return "Mức 2 (GPA < 1.5)";
            case "BUOC_THOI_HOC": return "Buộc thôi học (GPA < 1.0)";
            default: return raw;
        }
    }

    public static String formatTrangThaiTuVan(String raw) {
        if (raw == null) return "Chưa tư vấn";
        switch (raw) {
            case "DA_TU_VAN": return "Đã tư vấn";
            case "DANG_THEO_DOI": return "Đang theo dõi";
            case "CHUA_TU_VAN":
            default: return "Chưa tư vấn";
        }
    }

    public static DefaultTableCellRenderer createCenterRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        return renderer;
    }

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontBold(12));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return lbl;
    }
}