package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.service.ThongKeService;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Panel Báo cáo & Thống kê Cảnh báo Học vụ với biểu đồ trực quan hóa Java2D hiện đại.
 */
public class BaoCaoThongKePanel extends JPanel {

    private final ThongKeService thongKeService = new ThongKeService();
    private Map<String, Integer> stats;
    private JComboBox<String> cbFilterLop;

    // Stat labels
    private JLabel lblTongSv, lblBinhThuong, lblMuc1, lblMuc2, lblBuoc, lblDaTuVan, lblChuaTuVan, lblTier1;

    public BaoCaoThongKePanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        initUI();
        loadStats();
    }

    private void initUI() {
        // Header container
        JPanel topContainer = new JPanel(new BorderLayout(0, 8));
        topContainer.setOpaque(false);

        // Header Title
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("📈  BÁO CÁO THỐNG KÊ & TRỰC QUAN HÓA DỮ LIỆU");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        header.add(title, BorderLayout.WEST);
        topContainer.add(header, BorderLayout.NORTH);

        // Action Toolbar with WrapLayout
        JPanel pnlAction = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        pnlAction.setBackground(UITheme.BG_WHITE);
        pnlAction.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        
        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- Tất cả các lớp ---");
        CoVanDAO cvDao = new CoVanDAO();
        for (LopHoc lh : cvDao.getAllLopHoc()) {
            cbFilterLop.addItem(lh.getMaLop() + " - " + lh.getTenLop());
        }
        cbFilterLop.addActionListener(e -> loadStats());
        
        JButton btnRefresh = UITheme.createButton("🔄 Cập Nhật Số Liệu", UITheme.PRIMARY, Color.WHITE);
        btnRefresh.setToolTipText("Tải lại số liệu thống kê mới nhất");
        btnRefresh.addActionListener(e -> loadStats());

        JButton btnExport = UITheme.createButton("📊 Xuất Báo Cáo Excel", new Color(46, 125, 50), Color.WHITE);
        btnExport.setToolTipText("Xuất toàn bộ chỉ tiêu thống kê ra file Excel .xlsx");
        btnExport.addActionListener(e -> exportReportToExcel());
        
        pnlAction.add(new JLabel("Phạm vi lớp:"));
        pnlAction.add(cbFilterLop);
        pnlAction.add(btnRefresh);
        pnlAction.add(btnExport);

        topContainer.add(pnlAction, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Center: scrollable content (cards + chart)
        JPanel contentPanel = new JPanel(new BorderLayout(0, 14));
        contentPanel.setOpaque(false);

        // Stat cards row (2 rows x 4)
        JPanel cards = new JPanel(new GridLayout(2, 4, 12, 12));
        cards.setOpaque(false);
        lblTongSv     = addCard(cards, "TỔNG SỐ SINH VIÊN",       "0", new Color(30, 80, 170), new Color(45, 110, 210));
        lblBinhThuong = addCard(cards, "ĐANG HỌC BÌNH THƯỜNG", "0", new Color(27, 110, 55), new Color(46, 150, 78));
        lblMuc1       = addCard(cards, "CẢNH BÁO MỨC 1",       "0", new Color(190, 100, 0), new Color(220, 135, 0));
        lblMuc2       = addCard(cards, "CẢNH BÁO MỨC 2",       "0", new Color(170, 50, 50), new Color(200, 75, 75));
        lblBuoc       = addCard(cards, "BUỘC THÔI HỌC",         "0", new Color(110, 0, 0),   new Color(150, 20, 20));
        lblDaTuVan    = addCard(cards, "ĐÃ ĐƯỢC TƯ VẤN",       "0", new Color(0, 100, 160),  new Color(2, 130, 200));
        lblChuaTuVan  = addCard(cards, "CHƯA ĐƯỢC TƯ VẤN",     "0", new Color(130, 70, 0),   new Color(160, 95, 0));
        lblTier1      = addCard(cards, "TIER 1 (GPA ≥ 3.2)",   "0", new Color(74, 20, 140),  new Color(106, 27, 154));

        contentPanel.add(cards, BorderLayout.NORTH);

        // Chart panel
        ChartPanel chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(800, 360));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(14, 16, 14, 16)
        ));
        contentPanel.add(chartPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel addCard(JPanel parent, String title, String val, Color from, Color to) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, from, getWidth(), getHeight(), to);
                g2.setPaint(gp); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(UITheme.fontBold(10));
        lTitle.setForeground(new Color(220, 235, 250));

        JLabel lVal = new JLabel(val, SwingConstants.RIGHT);
        lVal.setFont(UITheme.fontBold(32));
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
            maLop = selected.split(" - ")[0];
        }
        stats = thongKeService.getThongKeTongQuan(maLop);
        lblTongSv.setText(String.valueOf(stats.getOrDefault("tong_sv", 0)));
        lblBinhThuong.setText(String.valueOf(stats.getOrDefault("sv_binh_thuong", 0)));
        lblMuc1.setText(String.valueOf(stats.getOrDefault("cb_muc_1", 0)));
        lblMuc2.setText(String.valueOf(stats.getOrDefault("cb_muc_2", 0)));
        lblBuoc.setText(String.valueOf(stats.getOrDefault("buoc_thoi_hoc", 0)));
        lblDaTuVan.setText(String.valueOf(stats.getOrDefault("da_tu_van", 0)));
        lblChuaTuVan.setText(String.valueOf(stats.getOrDefault("chua_tu_van", 0)));
        lblTier1.setText(String.valueOf(stats.getOrDefault("tier_1", 0)));
        repaint();
    }

    private void exportReportToExcel() {
        if (stats == null) return;
        String[] cols = {"Chỉ Tiêu Thống Kê", "Số Lượng Sinh Viên", "Tỷ Lệ (%)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        int total = stats.getOrDefault("tong_sv", 1);
        if (total == 0) total = 1;

        int bt = stats.getOrDefault("sv_binh_thuong", 0);
        int m1 = stats.getOrDefault("cb_muc_1", 0);
        int m2 = stats.getOrDefault("cb_muc_2", 0);
        int buoc = stats.getOrDefault("buoc_thoi_hoc", 0);
        int dtv = stats.getOrDefault("da_tu_van", 0);
        int ctv = stats.getOrDefault("chua_tu_van", 0);
        int t1 = stats.getOrDefault("tier_1", 0);
        int t2 = stats.getOrDefault("tier_2", 0);
        int t3 = stats.getOrDefault("tier_3", 0);

        model.addRow(new Object[]{"Tổng số sinh viên", total, "100.0%"});
        model.addRow(new Object[]{"Sinh viên học bình thường", bt, String.format("%.1f%%", (bt * 100.0 / total))});
        model.addRow(new Object[]{"Cảnh báo học vụ Mức 1", m1, String.format("%.1f%%", (m1 * 100.0 / total))});
        model.addRow(new Object[]{"Cảnh báo học vụ Mức 2", m2, String.format("%.1f%%", (m2 * 100.0 / total))});
        model.addRow(new Object[]{"Buộc thôi học / Đình chỉ", buoc, String.format("%.1f%%", (buoc * 100.0 / total))});
        model.addRow(new Object[]{"Đã hoàn thành tư vấn", dtv, String.format("%.1f%%", (dtv * 100.0 / Math.max(1, m1 + m2 + buoc)))});
        model.addRow(new Object[]{"Chưa được tư vấn", ctv, String.format("%.1f%%", (ctv * 100.0 / Math.max(1, m1 + m2 + buoc)))});
        model.addRow(new Object[]{"Phân tầng Tier 1 (GPA ≥ 3.2)", t1, String.format("%.1f%%", (t1 * 100.0 / total))});
        model.addRow(new Object[]{"Phân tầng Tier 2 (2.0 ≤ GPA < 3.2)", t2, String.format("%.1f%%", (t2 * 100.0 / total))});
        model.addRow(new Object[]{"Phân tầng Tier 3 (Nguy cơ / GPA < 2.0)", t3, String.format("%.1f%%", (t3 * 100.0 / total))});

        JTable tempTable = new JTable(model);
        ExcelExporter.exportJTableToExcel(tempTable, "Bao_Cao_Thong_Ke_Canh_Bao_Hoc_Vu");
    }

    // ====================== INNER CHART PANEL ==========================
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

            // === DONUT PIE CHART (right half) ===
            drawDonutChart(g2, half + 10, 0, w - half - 20, h);
        }

        private void drawBarChart(Graphics2D g2, int x, int y, int w, int h) {
            g2.setFont(UITheme.fontBold(13));
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.drawString("📊 Phân Bố Sinh Viên Theo Tình Trạng Học Vụ", x + 15, y + 24);

            int[] vals = {
                stats.getOrDefault("sv_binh_thuong", 0),
                stats.getOrDefault("cb_muc_1", 0),
                stats.getOrDefault("cb_muc_2", 0),
                stats.getOrDefault("buoc_thoi_hoc", 0)
            };
            String[] labels = {"Bình thường", "Cảnh báo 1", "Cảnh báo 2", "Buộc thôi học"};
            Color[] colors = {
                new Color(46, 125, 50),
                new Color(230, 119, 0),
                new Color(211, 47, 47),
                new Color(120, 0, 0)
            };

            int maxVal = 1;
            for (int v : vals) if (v > maxVal) maxVal = v;

            int marginL = x + 30, marginB = y + h - 50;
            int chartH = h - 100;
            int barW = Math.max(24, (w - 100) / 4);

            // Grid line
            g2.setColor(UITheme.BORDER_LIGHT);
            g2.drawLine(marginL, marginB, marginL + 4 * (barW + 16), marginB);

            for (int i = 0; i < 4; i++) {
                int barH = (int) ((double) vals[i] / maxVal * (chartH - 20));
                int bx = marginL + i * (barW + 16);
                int by = marginB - barH;

                GradientPaint gp = new GradientPaint(bx, by, colors[i].brighter(), bx, marginB, colors[i]);
                g2.setPaint(gp);
                g2.fillRoundRect(bx, by, barW, barH, 8, 8);

                g2.setColor(colors[i].darker());
                g2.setFont(UITheme.fontBold(12));
                String vStr = String.valueOf(vals[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(vStr, bx + (barW - fm.stringWidth(vStr)) / 2, by - 6);

                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.setFont(UITheme.fontPlain(11));
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(labels[i], bx + (barW - fm2.stringWidth(labels[i])) / 2, marginB + 18);
            }
        }

        private void drawDonutChart(Graphics2D g2, int x, int y, int w, int h) {
            g2.setFont(UITheme.fontBold(13));
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.drawString("🍩 Cơ Cấu Phân Tầng Rủi Ro (Tier 1 - 2 - 3)", x + 15, y + 24);

            int t1 = stats.getOrDefault("tier_1", 0);
            int t2 = stats.getOrDefault("tier_2", 0);
            int t3 = stats.getOrDefault("tier_3", 0);
            int total = t1 + t2 + t3;
            if (total == 0) total = 1;

            int[] vals = {t1, t2, t3};
            String[] labels = {
                "Tier 1 (GPA ≥ 3.2 - Khá Giỏi)",
                "Tier 2 (2.0 ≤ GPA < 3.2 - TB)",
                "Tier 3 (Nguy cơ / GPA < 2.0)"
            };
            Color[] colors = {
                new Color(46, 125, 50),
                new Color(25, 118, 210),
                new Color(211, 47, 47)
            };

            int size = Math.min(w - 180, h - 80);
            size = Math.max(size, 120);
            int cx = x + 20;
            int cy = y + 45;

            double startAngle = 90;
            for (int i = 0; i < 3; i++) {
                double arc = (double) vals[i] / total * 360.0;
                g2.setColor(colors[i]);
                g2.fillArc(cx, cy, size, size, (int) startAngle, (int) Math.ceil(arc));
                startAngle += arc;
            }

            // Donut hole
            int holeSize = (int) (size * 0.55);
            int hx = cx + (size - holeSize) / 2;
            int hy = cy + (size - holeSize) / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(hx, hy, holeSize, holeSize);

            // Center text
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.setFont(UITheme.fontBold(16));
            String totStr = total + " SV";
            FontMetrics fmc = g2.getFontMetrics();
            g2.drawString(totStr, hx + (holeSize - fmc.stringWidth(totStr)) / 2, hy + (holeSize + fmc.getAscent()) / 2 - 3);

            // Legend on the right
            int legX = cx + size + 20;
            int legY = cy + 25;
            g2.setFont(UITheme.fontPlain(11));
            for (int i = 0; i < 3; i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(legX, legY + i * 28, 14, 14, 4, 4);

                g2.setColor(UITheme.TEXT_PRIMARY);
                int pct = (int) Math.round((double) vals[i] / total * 100.0);
                g2.drawString(labels[i] + ": " + vals[i] + " (" + pct + "%)", legX + 22, legY + i * 28 + 12);
            }
        }
    }
}