package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.service.ThongKeService;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Map;

/**
 * Panel bao cao thong ke voi bieu do Java2D.
 */
public class BaoCaoThongKePanel extends JPanel {

    private final ThongKeService thongKeService = new ThongKeService();
    private Map<String, Integer> stats;
    private JComboBox<String> cbFilterLop;

    // Stat labels
    private JLabel lblTongSv, lblBinhThuong, lblMuc1, lblMuc2, lblBuoc, lblDaTuVan, lblChuaTuVan;

    public BaoCaoThongKePanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(14, 16, 14, 16));
        initUI();
        loadStats();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Bao cao & Thong ke Canh bao Hoc vu");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setOpaque(false);
        
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("Tat ca cac lop");
        CoVanDAO cvDao = new CoVanDAO();
        for (LopHoc lh : cvDao.getAllLopHoc()) {
            cbFilterLop.addItem(lh.getMaLop() + " - " + lh.getTenLop());
        }
        cbFilterLop.addActionListener(e -> loadStats());
        
        JButton btnRefresh = createBtn("Cap nhat", UITheme.PRIMARY, Color.WHITE);
        btnRefresh.addActionListener(e -> loadStats());
        
        pnlAction.add(new JLabel("Loc theo lop: "));
        pnlAction.add(cbFilterLop);
        pnlAction.add(btnRefresh);

        header.add(title, BorderLayout.WEST);
        header.add(pnlAction, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center: stat cards (top) + chart (bottom)
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);

        // Stat cards row (2 rows x 4)
        JPanel cards = new JPanel(new GridLayout(2, 4, 12, 12));
        cards.setOpaque(false);
        lblTongSv    = addCard(cards, "TONG SINH VIEN",        "0", new Color(30,80,170), new Color(45,110,210));
        lblBinhThuong= addCard(cards, "DANG HOC BINH THUONG",  "0", new Color(27,110,55), new Color(46,150,78));
        lblMuc1      = addCard(cards, "CANH BAO MUC 1",        "0", new Color(190,100,0), new Color(220,135,0));
        lblMuc2      = addCard(cards, "CANH BAO MUC 2",        "0", new Color(170,50,50), new Color(200,75,75));
        lblBuoc      = addCard(cards, "BUOC THOI HOC",          "0", new Color(110,0,0),   new Color(150,20,20));
        lblDaTuVan   = addCard(cards, "DA DUOC TU VAN",        "0", new Color(0,100,160),  new Color(2,130,200));
        lblChuaTuVan = addCard(cards, "CHUA DUOC TU VAN",      "0", new Color(100,60,0),   new Color(140,90,0));
        // Placeholder card (empty)
        JPanel ph = new JPanel(); ph.setOpaque(false); cards.add(ph);

        center.add(cards, BorderLayout.NORTH);

        // Chart panel
        ChartPanel chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(800, 320));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(14, 16, 14, 16)
        ));
        center.add(chartPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JLabel addCard(JPanel parent, String title, String val, Color from, Color to) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, from, getWidth(), getHeight(), to);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel lTitle = new JLabel(title);
        lTitle.setFont(UITheme.fontBold(11));
        lTitle.setForeground(new Color(210,225,250));
        JLabel lVal = new JLabel(val, SwingConstants.RIGHT);
        lVal.setFont(UITheme.fontBold(34));
        lVal.setForeground(Color.WHITE);
        card.add(lTitle, BorderLayout.NORTH);
        card.add(lVal, BorderLayout.SOUTH);
        parent.add(card);
        return lVal;
    }

    private void loadStats() {
        String maLop = "ALL";
        if (cbFilterLop != null && cbFilterLop.getSelectedIndex() > 0) {
            String selected = (String) cbFilterLop.getSelectedItem();
            maLop = selected.split(" - ")[0]; // "68IT1"
        }
        stats = thongKeService.getThongKeTongQuan(maLop);
        lblTongSv.setText(String.valueOf(stats.getOrDefault("tong_sv", 0)));
        lblBinhThuong.setText(String.valueOf(stats.getOrDefault("sv_binh_thuong", 0)));
        lblMuc1.setText(String.valueOf(stats.getOrDefault("cb_muc_1", 0)));
        lblMuc2.setText(String.valueOf(stats.getOrDefault("cb_muc_2", 0)));
        lblBuoc.setText(String.valueOf(stats.getOrDefault("buoc_thoi_hoc", 0)));
        lblDaTuVan.setText(String.valueOf(stats.getOrDefault("da_tu_van", 0)));
        lblChuaTuVan.setText(String.valueOf(stats.getOrDefault("chua_tu_van", 0)));
        repaint();
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

    // ====================== INNER CHART PANEL ==========================
    /**
     * Ve bieu do cot (Bar Chart) va bieu do tron (Pie Chart) bang Java2D.
     */
    class ChartPanel extends JPanel {

        ChartPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (stats == null) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int half = w / 2;

            // === BAR CHART (left half) ===
            drawBarChart(g2, 0, 0, half - 10, h);

            // === PIE CHART (right half) ===
            drawPieChart(g2, half + 10, 0, w - half - 20, h);
        }

        private void drawBarChart(Graphics2D g2, int x, int y, int w, int h) {
            // Title
            g2.setFont(UITheme.fontBold(13));
            g2.setColor(UITheme.TEXT_PRIMARY);
            String chartTitle = "Bieu do cot - Trang thai sinh vien";
            g2.drawString(chartTitle, x + 10, y + 22);

            String[] labels = {"Binh thuong", "Canh bao 1", "Canh bao 2", "Buoc thoi hoc"};
            int[] values = {
                stats.getOrDefault("sv_binh_thuong", 0),
                stats.getOrDefault("cb_muc_1", 0),
                stats.getOrDefault("cb_muc_2", 0),
                stats.getOrDefault("buoc_thoi_hoc", 0)
            };
            Color[] colors = {UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER, UITheme.DANGER_DARK};

            int maxVal = 1;
            for (int v : values) if (v > maxVal) maxVal = v;

            int chartX = x + 50, chartY = y + 40;
            int chartW = w - 70, chartH = h - 80;

            // Axes
            g2.setColor(UITheme.BORDER_MEDIUM);
            g2.drawLine(chartX, chartY, chartX, chartY + chartH);
            g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);

            // Grid lines & Y labels
            g2.setFont(UITheme.fontPlain(10));
            int steps = 5;
            for (int i = 0; i <= steps; i++) {
                int lineY = chartY + chartH - (i * chartH / steps);
                g2.setColor(UITheme.BORDER_LIGHT);
                g2.drawLine(chartX, lineY, chartX + chartW, lineY);
                g2.setColor(UITheme.TEXT_SECONDARY);
                int yVal = maxVal * i / steps;
                g2.drawString(String.valueOf(yVal), x + 5, lineY + 4);
            }

            // Bars
            int barGroupW = chartW / labels.length;
            for (int i = 0; i < labels.length; i++) {
                int barH = (maxVal == 0) ? 0 : values[i] * chartH / maxVal;
                int barX = chartX + i * barGroupW + barGroupW / 4;
                int barW = barGroupW / 2;
                int barY = chartY + chartH - barH;

                // Bar gradient
                g2.setPaint(new GradientPaint(barX, barY, colors[i].brighter(), barX, barY + barH, colors[i]));
                g2.fillRoundRect(barX, barY, barW, barH, 6, 6);
                g2.setColor(colors[i].darker());
                g2.drawRoundRect(barX, barY, barW, barH, 6, 6);

                // Value on top
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.fontBold(11));
                String valStr = String.valueOf(values[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valStr, barX + (barW - fm.stringWidth(valStr)) / 2, barY - 4);

                // Label below
                g2.setFont(UITheme.fontPlain(10));
                g2.setColor(UITheme.TEXT_SECONDARY);
                fm = g2.getFontMetrics();
                g2.drawString(labels[i], barX + (barW - fm.stringWidth(labels[i])) / 2, chartY + chartH + 16);
            }
        }

        private void drawPieChart(Graphics2D g2, int x, int y, int w, int h) {
            g2.setFont(UITheme.fontBold(13));
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.drawString("Phan loai sinh vien (Donut Chart)", x + 10, y + 22);

            int binhThuong = stats.getOrDefault("sv_binh_thuong", 0);
            int canhBao1   = stats.getOrDefault("cb_muc_1", 0);
            int canhBao2   = stats.getOrDefault("cb_muc_2", 0);
            int buocThoi   = stats.getOrDefault("buoc_thoi_hoc", 0);

            int total = binhThuong + canhBao1 + canhBao2 + buocThoi;
            if (total == 0) {
                g2.setFont(UITheme.FONT_BODY);
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.drawString("Chua co du lieu", x + w/2 - 50, y + h/2);
                return;
            }

            String[] sliceLabels = {"Binh thuong", "Canh bao muc 1", "Canh bao muc 2", "Buoc thoi hoc"};
            int[] sliceValues    = {binhThuong, canhBao1, canhBao2, buocThoi};
            Color[] sliceColors  = {UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER, UITheme.DANGER_DARK};

            int pieSize = Math.min(w - 200, h - 70); // Adjust to make more room for legend
            int pieX = x + 5;
            int pieY = y + 40;

            int startAngle = 90; // Start from top
            int sumAngle = 0;
            
            // First count non-zero slices
            int nonZeroCount = 0;
            int lastNonZeroIndex = -1;
            for (int i = 0; i < sliceValues.length; i++) {
                if (sliceValues[i] > 0) {
                    nonZeroCount++;
                    lastNonZeroIndex = i;
                }
            }

            for (int i = 0; i < sliceValues.length; i++) {
                if (sliceValues[i] == 0) continue;
                
                int arcAngle = (int) Math.round(360.0 * sliceValues[i] / total);
                // Fix rounding error on the last non-zero slice
                if (i == lastNonZeroIndex) {
                    arcAngle = 360 - sumAngle;
                }
                sumAngle += arcAngle;
                
                g2.setColor(sliceColors[i]);
                g2.fillArc(pieX, pieY, pieSize, pieSize, startAngle, -arcAngle); // Negative to draw clockwise
                
                // Draw gap between slices if there are multiple slices
                if (nonZeroCount > 1) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(pieX, pieY, pieSize, pieSize, startAngle, -arcAngle);
                    g2.drawLine(pieX + pieSize/2, pieY + pieSize/2, 
                                pieX + pieSize/2 + (int)(Math.cos(Math.toRadians(startAngle)) * pieSize/2), 
                                pieY + pieSize/2 - (int)(Math.sin(Math.toRadians(startAngle)) * pieSize/2));
                }

                startAngle -= arcAngle;
            }

            // Draw inner circle for Donut effect
            int innerSize = pieSize * 65 / 100;
            int innerX = pieX + (pieSize - innerSize) / 2;
            int innerY = pieY + (pieSize - innerSize) / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(innerX, innerY, innerSize, innerSize);

            // Legend
            int legX = pieX + pieSize + 15;
            int legY = pieY + 15;
            for (int i = 0; i < sliceLabels.length; i++) {
                g2.setColor(sliceColors[i]);
                g2.fillRoundRect(legX, legY + i * 28, 12, 12, 6, 6);
                
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.setFont(UITheme.fontPlain(11));
                g2.drawString(sliceLabels[i], legX + 20, legY + i * 28 + 11);
                
                g2.setFont(UITheme.fontBold(12));
                g2.setColor(UITheme.TEXT_PRIMARY);
                String valStr = String.valueOf(sliceValues[i]);
                String pctStr = total > 0 ? String.format("%d%%", Math.round(100.0 * sliceValues[i] / total)) : "0%";
                
                g2.drawString(valStr, legX + 115, legY + i * 28 + 11);
                
                g2.setFont(UITheme.fontPlain(11));
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.drawString(pctStr, legX + 140, legY + i * 28 + 11);
            }

            // Total
            int totalY = legY + sliceLabels.length * 28 + 10;
            g2.setColor(UITheme.BORDER_LIGHT);
            g2.drawLine(legX, totalY - 15, legX + 160, totalY - 15);
            
            g2.setFont(UITheme.fontPlain(12));
            g2.setColor(UITheme.TEXT_SECONDARY);
            g2.drawString("Tong cong", legX, totalY);
            
            g2.setFont(UITheme.fontBold(13));
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.drawString(total + " sv", legX + 115, totalY);
        }
    }
}