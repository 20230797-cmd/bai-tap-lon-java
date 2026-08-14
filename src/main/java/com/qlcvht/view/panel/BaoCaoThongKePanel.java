package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.model.AIRiskPrediction;
import com.qlcvht.model.CounselingProgressItem;
import com.qlcvht.model.LopHoc;
import com.qlcvht.service.AIPredictionService;
import com.qlcvht.service.ThongKeService;
import com.qlcvht.util.ReportExporter;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel Bao cao & Thong ke nang cap voi 3 Tab:
 * 1. Thong ke bieu do & Tien do tu van
 * 2. Du bao nguy co hoc vu AI / Machine Learning
 * 3. Xuat Bao cao Hanh chinh (Word .docx / PDF .pdf)
 */
public class BaoCaoThongKePanel extends JPanel {

    private final ThongKeService thongKeService = new ThongKeService();
    private final AIPredictionService aiService = new AIPredictionService();

    private JTabbedPane tabbedPane;

    // === TAB 1: THỐNG KÊ & TIẾN ĐỘ TƯ VẤN ===
    private Map<String, Integer> stats;
    private Map<String, Object> counselingStats;
    private JComboBox<String> cbFilterLopTab1;
    private JLabel lblTongSv, lblBinhThuong, lblMuc1, lblMuc2, lblBuoc;
    private JLabel lblPercentDaTuVan, lblPercentCaiThien;

    // === TAB 2: DỰ BÁO AI / ML ===
    private JComboBox<String> cbFilterLopAi;
    private JComboBox<String> cbFilterRiskLevel;
    private JTable tblAiPrediction;
    private DefaultTableModel modelAiTable;

    // === TAB 3: XUẤT BÁO CÁO HÀNH CHÍNH ===
    private JComboBox<String> cbFormMau;
    private JComboBox<String> cbDinhDang;
    private JComboBox<String> cbExportLop;

    public BaoCaoThongKePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_MAIN);
        initUI();
    }

    private void initUI() {
        // Header Main Title
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_MAIN);
        header.setBorder(new EmptyBorder(14, 16, 8, 16));

        JLabel title = new JLabel("HỆ THỐNG BÁO CÁO, THỐNG KÊ & DỰ BÁO NGUY CƠ HỌC VỤ AI");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Main Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.fontBold(13));
        tabbedPane.setBackground(UITheme.BG_MAIN);

        tabbedPane.addTab("  Thống kê & Tiến độ tư vấn  ", createTabThongKeTongQuan());
        tabbedPane.addTab("  Dự báo nguy cơ Học vụ (AI/ML)  ", createTabAiPrediction());
        tabbedPane.addTab("  Xuất Báo cáo Hành chính (Word/PDF)  ", createTabExportReport());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // TAB 1: THỐNG KÊ TỔNG QUAN & TIẾN ĐỘ TƯ VẤN
    // =========================================================================

    private JPanel createTabThongKeTongQuan() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(UITheme.BG_MAIN);
        pnl.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Sub Header Filter
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterRow.setOpaque(false);

        cbFilterLopTab1 = new JComboBox<>();
        cbFilterLopTab1.addItem("Tat ca cac lop");
        for (LopHoc lh : new CoVanDAO().getAllLopHoc()) {
            cbFilterLopTab1.addItem(lh.getMaLop() + " - " + lh.getTenLop());
        }
        cbFilterLopTab1.addActionListener(e -> loadStatsTab1());

        JButton btnRefresh1 = createBtn("Cập nhật dữ liệu", UITheme.PRIMARY, Color.WHITE);
        btnRefresh1.addActionListener(e -> loadStatsTab1());

        filterRow.add(new JLabel("Lọc theo lớp: "));
        filterRow.add(cbFilterLopTab1);
        filterRow.add(btnRefresh1);
        pnl.add(filterRow, BorderLayout.NORTH);

        // Center Content: Cards (top) + Chart (center)
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);

        // 7 Stat Cards Row (Grid 2 rows x 4 cols)
        JPanel cards = new JPanel(new GridLayout(2, 4, 10, 10));
        cards.setOpaque(false);

        lblTongSv          = addCard(cards, "TỔNG SINH VIÊN",       "0", new Color(30, 80, 170), new Color(45, 110, 210));
        lblBinhThuong      = addCard(cards, "ĐANG HỌC BÌNH THƯỜNG", "0", new Color(27, 110, 55), new Color(46, 150, 78));
        lblMuc1            = addCard(cards, "CẢNH BÁO MỨC 1",       "0", new Color(190, 100, 0), new Color(220, 135, 0));
        lblMuc2            = addCard(cards, "CẢNH BÁO MỨC 2",       "0", new Color(170, 50, 50), new Color(200, 75, 75));
        lblBuoc            = addCard(cards, "BUỘC THÔI HỌC",        "0", new Color(110, 0, 0),   new Color(150, 20, 20));

        // 2 Thẻ mới: Tiến độ tư vấn & Cải thiện điểm
        lblPercentDaTuVan   = addCard(cards, "% SV CẢNH BÁO ĐÃ TƯ VẤN", "0%", new Color(0, 120, 150), new Color(2, 160, 200));
        lblPercentCaiThien  = addCard(cards, "% SV CẢI THIỆN ĐIỂM SỐ",   "0%", new Color(110, 40, 140), new Color(150, 60, 180));

        // Empty card placeholder
        JPanel ph = new JPanel(); ph.setOpaque(false); cards.add(ph);

        center.add(cards, BorderLayout.NORTH);

        // Chart Panel
        ChartPanel chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(800, 280));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(10, 12, 10, 12)
        ));
        center.add(chartPanel, BorderLayout.CENTER);

        pnl.add(center, BorderLayout.CENTER);

        loadStatsTab1();
        return pnl;
    }

    private void loadStatsTab1() {
        String maLop = getSelectedLop(cbFilterLopTab1);
        stats = thongKeService.getThongKeTongQuan(maLop);
        counselingStats = thongKeService.getThongKeTienDoTuVan(maLop);

        lblTongSv.setText(String.valueOf(stats.getOrDefault("tong_sv", 0)));
        lblBinhThuong.setText(String.valueOf(stats.getOrDefault("sv_binh_thuong", 0)));
        lblMuc1.setText(String.valueOf(stats.getOrDefault("cb_muc_1", 0)));
        lblMuc2.setText(String.valueOf(stats.getOrDefault("cb_muc_2", 0)));
        lblBuoc.setText(String.valueOf(stats.getOrDefault("buoc_thoi_hoc", 0)));

        double pTuVan = (Double) counselingStats.getOrDefault("percentDaTuVan", 0.0);
        double pCaiThien = (Double) counselingStats.getOrDefault("percentCaiThien", 0.0);

        lblPercentDaTuVan.setText(String.format("%.1f%%", pTuVan));
        lblPercentCaiThien.setText(String.format("%.1f%%", pCaiThien));

        repaint();
    }

    // =========================================================================
    // TAB 2: DỰ BÁO NGUY CƠ HỌC VỤ AI / MACHINE LEARNING
    // =========================================================================

    private JPanel createTabAiPrediction() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(UITheme.BG_MAIN);
        pnl.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Sub Header Banner + Filters
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setOpaque(false);

        // Banner Giai thich Mo hinh AI
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(235, 243, 255));
        banner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 255)),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblBannerTitle = new JLabel("🤖 Thuật toán AI Dự báo sớm Nguy cơ Học vụ (Linear Regression & Risk Matrix)");
        lblBannerTitle.setFont(UITheme.fontBold(13));
        lblBannerTitle.setForeground(new Color(20, 70, 160));

        JLabel lblBannerDesc = new JLabel("<html>Hệ thống tự động phân tích tốc độ sụt giảm GPA qua từng học kỳ (ΔGPA) và số tín chỉ nợ tích lũy để cảnh báo sớm <b>Nguy cơ Cao / Nguy cơ Trung bình</b> trước khi chính thức rơi vào danh sách Cảnh báo Mức 1 hoặc Mức 2.</html>");
        lblBannerDesc.setFont(UITheme.fontPlain(11));
        lblBannerDesc.setForeground(UITheme.TEXT_SECONDARY);

        banner.add(lblBannerTitle, BorderLayout.NORTH);
        banner.add(lblBannerDesc, BorderLayout.CENTER);

        topBar.add(banner, BorderLayout.NORTH);

        // Filter Controls
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        filterRow.setOpaque(false);

        cbFilterLopAi = new JComboBox<>();
        cbFilterLopAi.addItem("Tất cả các lớp");
        for (LopHoc lh : new CoVanDAO().getAllLopHoc()) {
            cbFilterLopAi.addItem(lh.getMaLop() + " - " + lh.getTenLop());
        }
        cbFilterLopAi.addActionListener(e -> loadAiTableData());

        cbFilterRiskLevel = new JComboBox<>(new String[]{
            "Tất cả các mức nguy cơ", "HIGH_RISK - Nguy cơ Cao", "MEDIUM_RISK - Nguy cơ Trung bình", "LOW_RISK - An toàn"
        });
        cbFilterRiskLevel.addActionListener(e -> loadAiTableData());

        JButton btnReloadAi = createBtn("Phân tích lại AI", UITheme.PRIMARY, Color.WHITE);
        btnReloadAi.addActionListener(e -> loadAiTableData());

        filterRow.add(new JLabel("Lớp: "));
        filterRow.add(cbFilterLopAi);
        filterRow.add(new JLabel("Mức nguy cơ AI: "));
        filterRow.add(cbFilterRiskLevel);
        filterRow.add(btnReloadAi);

        topBar.add(filterRow, BorderLayout.SOUTH);
        pnl.add(topBar, BorderLayout.NORTH);

        // AI Prediction Table
        String[] cols = {
            "STT", "Mã SV", "Họ và Tên", "Lớp", "GPA Hiện tại", "GPA Dự báo (Kỳ tới)",
            "Xu hướng ΔGPA", "TC Nợ", "Mức Nguy cơ AI", "Trạng thái", "Lý do & Khuyến nghị Cố vấn"
        };
        modelAiTable = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAiPrediction = new JTable(modelAiTable);
        tblAiPrediction.setRowHeight(32);
        tblAiPrediction.setFont(UITheme.FONT_BODY);
        tblAiPrediction.getTableHeader().setFont(UITheme.fontBold(11));

        // Custom Cell Renderer highlight Risk Levels
        tblAiPrediction.getColumnModel().getColumn(8).setCellRenderer(new RiskLevelCellRenderer());

        JScrollPane scroll = new JScrollPane(tblAiPrediction);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        pnl.add(scroll, BorderLayout.CENTER);

        loadAiTableData();
        return pnl;
    }

    private void loadAiTableData() {
        modelAiTable.setRowCount(0);
        String maLop = getSelectedLop(cbFilterLopAi);
        String riskSel = (String) cbFilterRiskLevel.getSelectedItem();
        String riskFilter = "ALL";
        if (riskSel != null && riskSel.contains("HIGH_RISK")) riskFilter = "HIGH_RISK";
        else if (riskSel != null && riskSel.contains("MEDIUM_RISK")) riskFilter = "MEDIUM_RISK";
        else if (riskSel != null && riskSel.contains("LOW_RISK")) riskFilter = "LOW_RISK";

        List<AIRiskPrediction> list = aiService.predictAllStudents(maLop, riskFilter);

        int stt = 1;
        for (AIRiskPrediction ai : list) {
            String trendDisplay;
            switch (ai.getTrend()) {
                case "GIAM_MANH": trendDisplay = "🔻 Giảm mạnh"; break;
                case "GIAM_NHE":  trendDisplay = "📉 Giảm nhẹ"; break;
                case "TANG":      trendDisplay = "📈 Cải thiện"; break;
                default:          trendDisplay = "➡️ Ổn định"; break;
            }

            modelAiTable.addRow(new Object[]{
                stt++,
                ai.getMaSv(),
                ai.getHoTen(),
                ai.getMaLop(),
                ai.getGpaMoiNhat(),
                ai.getGpaDuBao(),
                trendDisplay,
                ai.getSoTinChiNo(),
                ai.getMucRuiRoDisplay(),
                ai.getTrangThaiHienTai(),
                ai.getKhuyenNghi()
            });
        }
    }

    // Custom renderer de to mau muc nguy co AI
    static class RiskLevelCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String val = value != null ? value.toString() : "";

            if (!isSelected) {
                if (val.contains("Cao")) {
                    c.setBackground(new Color(255, 230, 230));
                    c.setForeground(new Color(180, 20, 20));
                    setFont(UITheme.fontBold(11));
                } else if (val.contains("Trung bình")) {
                    c.setBackground(new Color(255, 245, 220));
                    c.setForeground(new Color(180, 90, 0));
                    setFont(UITheme.fontBold(11));
                } else {
                    c.setBackground(new Color(230, 250, 235));
                    c.setForeground(new Color(20, 120, 40));
                    setFont(UITheme.fontPlain(11));
                }
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }

    // =========================================================================
    // TAB 3: XUẤT BÁO CÁO HÀNH CHÍNH (WORD / PDF)
    // =========================================================================

    private JPanel createTabExportReport() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(UITheme.BG_MAIN);
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(24, 30, 24, 30)
        ));
        formCard.setPreferredSize(new Dimension(650, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("XUẤT BÁO CÁO CHUẨN ĐỊNH DẠNG HÀNH CHÍNH (HUCE)", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.fontBold(15));
        lblTitle.setForeground(UITheme.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formCard.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Xuất Biên bản họp lớp & Báo cáo tổng hợp học vụ để in ấn trình Ban Giám hiệu", SwingConstants.CENTER);
        lblSub.setFont(UITheme.fontPlain(11));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 1;
        formCard.add(lblSub, gbc);

        gbc.gridwidth = 1;

        // Form Mau
        gbc.gridy = 2; gbc.gridx = 0;
        formCard.add(new JLabel("1. Chọn mẫu biểu báo cáo:"), gbc);
        cbFormMau = new JComboBox<>(new String[]{
            "Biên bản họp lớp Cố vấn học tập (V/v Cảnh báo học vụ & AI Prediction)",
            "Báo cáo tổng hợp tình hình học vụ Khoa/Lớp (Trình Ban Giám hiệu)"
        });
        cbFormMau.setFont(UITheme.FONT_BODY);
        gbc.gridx = 1;
        formCard.add(cbFormMau, gbc);

        // Dinh dang export (Word / PDF)
        gbc.gridy = 3; gbc.gridx = 0;
        formCard.add(new JLabel("2. Chọn định dạng xuất:"), gbc);
        cbDinhDang = new JComboBox<>(new String[]{
            "Microsoft Word Document (*.docx)",
            "Adobe PDF Document (*.pdf)"
        });
        cbDinhDang.setFont(UITheme.FONT_BODY);
        gbc.gridx = 1;
        formCard.add(cbDinhDang, gbc);

        // Pham vi Lop
        gbc.gridy = 4; gbc.gridx = 0;
        formCard.add(new JLabel("3. Phạm vi Lớp / Khoa:"), gbc);
        cbExportLop = new JComboBox<>();
        cbExportLop.addItem("Tất cả các lớp (Toàn Khoa)");
        for (LopHoc lh : new CoVanDAO().getAllLopHoc()) {
            cbExportLop.addItem(lh.getMaLop() + " - " + lh.getTenLop());
        }
        cbExportLop.setFont(UITheme.FONT_BODY);
        gbc.gridx = 1;
        formCard.add(cbExportLop, gbc);

        // Export Button
        JButton btnExport = new JButton("📄 THỰC HIỆN XUẤT BÁO CÁO");
        btnExport.setFont(UITheme.fontBold(14));
        btnExport.setBackground(UITheme.PRIMARY);
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.setPreferredSize(new Dimension(200, 44));
        btnExport.addActionListener(e -> executeExportReport());

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        formCard.add(btnExport, gbc);

        pnl.add(formCard);
        return pnl;
    }

    private void executeExportReport() {
        int formIdx = cbFormMau.getSelectedIndex();
        int formatIdx = cbDinhDang.getSelectedIndex();
        String maLop = getSelectedLop(cbExportLop);

        if (formIdx == 0) {
            // Bien ban hop lop
            if (formatIdx == 0) {
                ReportExporter.exportBienBanHopLopWord(maLop);
            } else {
                ReportExporter.exportBienBanHopLopPDF(maLop);
            }
        } else {
            // Bao cao tong hop Khoa / Lop
            if (formatIdx == 0) {
                ReportExporter.exportBaoCaoTongHopWord(maLop);
            } else {
                ReportExporter.exportBaoCaoTongHopPDF(maLop);
            }
        }
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    private String getSelectedLop(JComboBox<String> combo) {
        if (combo != null && combo.getSelectedIndex() > 0) {
            String sel = (String) combo.getSelectedItem();
            return sel.split(" - ")[0].trim();
        }
        return "ALL";
    }

    private JLabel addCard(JPanel parent, String title, String val, Color from, Color to) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, from, getWidth(), getHeight(), to);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(UITheme.fontBold(10));
        lTitle.setForeground(new Color(220, 235, 255));

        JLabel lVal = new JLabel(val, SwingConstants.RIGHT);
        lVal.setFont(UITheme.fontBold(26));
        lVal.setForeground(Color.WHITE);

        card.add(lTitle, BorderLayout.NORTH);
        card.add(lVal, BorderLayout.SOUTH);
        parent.add(card);
        return lVal;
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
    class ChartPanel extends JPanel {
        ChartPanel() { setBackground(Color.WHITE); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (stats == null) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int half = w / 2;

            drawBarChart(g2, 0, 0, half - 10, h);
            drawPieChart(g2, half + 10, 0, w - half - 20, h);
        }

        private void drawBarChart(Graphics2D g2, int x, int y, int w, int h) {
            g2.setFont(UITheme.fontBold(12));
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.drawString("Biểu đồ cột - Trạng thái sinh viên", x + 10, y + 20);

            String[] labels = {"Bình thường", "Cảnh báo 1", "Cảnh báo 2", "Buộc thôi học"};
            int[] values = {
                stats.getOrDefault("sv_binh_thuong", 0),
                stats.getOrDefault("cb_muc_1", 0),
                stats.getOrDefault("cb_muc_2", 0),
                stats.getOrDefault("buoc_thoi_hoc", 0)
            };
            Color[] colors = {UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER, UITheme.DANGER_DARK};

            int maxVal = 1;
            for (int v : values) if (v > maxVal) maxVal = v;

            int chartX = x + 40, chartY = y + 35;
            int chartW = w - 60, chartH = h - 65;

            g2.setColor(UITheme.BORDER_MEDIUM);
            g2.drawLine(chartX, chartY, chartX, chartY + chartH);
            g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);

            int steps = 4;
            for (int i = 0; i <= steps; i++) {
                int lineY = chartY + chartH - (i * chartH / steps);
                g2.setColor(UITheme.BORDER_LIGHT);
                g2.drawLine(chartX, lineY, chartX + chartW, lineY);
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.setFont(UITheme.fontPlain(10));
                g2.drawString(String.valueOf(maxVal * i / steps), x + 5, lineY + 4);
            }

            int barGroupW = chartW / labels.length;
            for (int i = 0; i < labels.length; i++) {
                int barH = (maxVal == 0) ? 0 : values[i] * chartH / maxVal;
                int barX = chartX + i * barGroupW + barGroupW / 4;
                int barW = barGroupW / 2;
                int barY = chartY + chartH - barH;

                g2.setPaint(new GradientPaint(barX, barY, colors[i].brighter(), barX, barY + barH, colors[i]));
                g2.fillRoundRect(barX, barY, barW, barH, 6, 6);

                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.fontBold(10));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(String.valueOf(values[i]), barX + (barW - fm.stringWidth(String.valueOf(values[i]))) / 2, barY - 3);

                g2.setFont(UITheme.fontPlain(10));
                g2.setColor(UITheme.TEXT_SECONDARY);
                fm = g2.getFontMetrics();
                g2.drawString(labels[i], barX + (barW - fm.stringWidth(labels[i])) / 2, chartY + chartH + 14);
            }
        }

        private void drawPieChart(Graphics2D g2, int x, int y, int w, int h) {
            g2.setFont(UITheme.fontBold(12));
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.drawString("Phân loại sinh viên (Donut Chart)", x + 10, y + 20);

            int binhThuong = stats.getOrDefault("sv_binh_thuong", 0);
            int canhBao1   = stats.getOrDefault("cb_muc_1", 0);
            int canhBao2   = stats.getOrDefault("cb_muc_2", 0);
            int buocThoi   = stats.getOrDefault("buoc_thoi_hoc", 0);

            int total = binhThuong + canhBao1 + canhBao2 + buocThoi;
            if (total == 0) return;

            String[] sliceLabels = {"Bình thường", "Cảnh báo 1", "Cảnh báo 2", "Buộc thôi học"};
            int[] sliceValues    = {binhThuong, canhBao1, canhBao2, buocThoi};
            Color[] sliceColors  = {UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER, UITheme.DANGER_DARK};

            int pieSize = Math.min(w - 180, h - 50);
            int pieX = x + 5;
            int pieY = y + 35;

            int startAngle = 90;
            int sumAngle = 0;

            for (int i = 0; i < sliceValues.length; i++) {
                if (sliceValues[i] == 0) continue;
                int arcAngle = (int) Math.round(360.0 * sliceValues[i] / total);
                sumAngle += arcAngle;
                g2.setColor(sliceColors[i]);
                g2.fillArc(pieX, pieY, pieSize, pieSize, startAngle, -arcAngle);
                startAngle -= arcAngle;
            }

            int innerSize = pieSize * 60 / 100;
            g2.setColor(Color.WHITE);
            g2.fillOval(pieX + (pieSize - innerSize)/2, pieY + (pieSize - innerSize)/2, innerSize, innerSize);

            int legX = pieX + pieSize + 15;
            int legY = pieY + 10;
            for (int i = 0; i < sliceLabels.length; i++) {
                g2.setColor(sliceColors[i]);
                g2.fillRoundRect(legX, legY + i * 24, 10, 10, 4, 4);

                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.setFont(UITheme.fontPlain(10));
                g2.drawString(sliceLabels[i], legX + 16, legY + i * 24 + 9);

                g2.setFont(UITheme.fontBold(10));
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.drawString(String.valueOf(sliceValues[i]), legX + 110, legY + i * 24 + 9);
            }
        }
    }
}