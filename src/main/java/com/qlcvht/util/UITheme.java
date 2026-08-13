package com.qlcvht.util;

import java.awt.*;

/**
 * UITheme - Tap trung quan ly mau sac va font cho toan bo ung dung.
 */
public class UITheme {
    public static final Color PRIMARY          = new Color(25, 118, 210);
    public static final Color PRIMARY_DARK     = new Color(13, 71, 161);
    public static final Color PRIMARY_LIGHT    = new Color(227, 242, 253);
    public static final Color SECONDARY        = new Color(38, 50, 56);
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
    public static final Insets BTN_INSETS      = new Insets(8, 16, 8, 16);
    public static final int PAD_SMALL   = 5;
    public static final int PAD_MEDIUM  = 10;
    public static final int PAD_LARGE   = 15;
    public static final int PAD_XLARGE  = 20;

    public static void styleTable(javax.swing.JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_TABLE);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(BG_TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_LIGHT);
        table.setShowGrid(true);
        table.setIntercellSpacing(new java.awt.Dimension(1, 1));
    }
}