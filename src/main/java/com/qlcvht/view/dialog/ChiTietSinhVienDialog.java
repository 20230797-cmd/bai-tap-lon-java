package com.qlcvht.view.dialog;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.DiemDanhDAO;
import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.DiemDanh;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.SinhVien;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog Hồ sơ Học vụ 360 độ của sinh viên (Enhanced Student 360° Profile).
 * Tích hợp:
 * 1. Bảng điểm & Kết quả học tập từng học kỳ + Biểu đồ Quỹ đạo Điểm Java2D.
 * 2. Lịch sử cảnh báo học vụ.
 * 3. Lịch sử các buổi tư vấn của CVHT.
 * 4. Lịch sử chuyên cần & điểm danh.
 * 5. Lịch sử bài tập & đánh giá quá trình.
 * 6. Nút Tư Vấn AI Cố Vấn Nhanh.
 */
public class ChiTietSinhVienDialog extends JDialog {

    private final SinhVien sinhVien;
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private final NhatKyTuVanDAO nhatKyDAO = new NhatKyTuVanDAO();
    private final DiemDanhDAO diemDanhDAO = new DiemDanhDAO();
    
    public ChiTietSinhVienDialog(Frame parent, SinhVien sv) {
        super(parent, "Hồ sơ Học vụ 360°: " + sv.getHoTen() + " (" + sv.getMaSv() + ")", true);
        this.sinhVien = sv;
        initUI();
        setSize(980, 680);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // Top Banner
        JPanel topBanner = new JPanel();
        topBanner.setLayout(new BoxLayout(topBanner, BoxLayout.Y_AXIS));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblName = new JLabel("🎓 " + sinhVien.getHoTen() + "  -  MSSV: " + sinhVien.getMaSv());
        lblName.setFont(UITheme.fontBold(20));
        lblName.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Lớp: " + (sinhVien.getTenLop() != null ? sinhVien.getTenLop() : sinhVien.getMaLop())
            + "    |    Trạng thái: " + UITheme.formatTrangThaiSinhVien(sinhVien.getTrangThai()));
        lblSub.setFont(UITheme.fontPlain(13));
        lblSub.setForeground(new Color(210, 230, 255));

        header.add(lblName, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        topBanner.add(header);

        // Info chips row
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        infoRow.setBackground(UITheme.PRIMARY_LIGHT);
        infoRow.setBorder(new EmptyBorder(4, 16, 4, 16));
        addInfoChip(infoRow, "GIỚI TÍNH", sinhVien.getGioiTinh() != null ? sinhVien.getGioiTinh() : "Nam");
        addInfoChip(infoRow, "NGÀY SINH", sinhVien.getNgaySinh() != null ? sinhVien.getNgaySinh().toString() : "---");
        addInfoChip(infoRow, "EMAIL", sinhVien.getEmail() != null ? sinhVien.getEmail() : "---");
        addInfoChip(infoRow, "SỐ ĐIỆN THOẠI", sinhVien.getSoDienThoai() != null ? sinhVien.getSoDienThoai() : "---");

        // Tier badge
        KetQuaHocTap latestKq = ketQuaDAO.getKetQuaHocKyMoiNhat(sinhVien.getMaSv());
        double cpa = latestKq != null ? latestKq.getGpaTichLuy() : 0.0;
        String tierText = (cpa >= 3.2) ? "Tier 1 (Học bổng / Khá Giỏi)" : (cpa >= 2.0 ? "Tier 2 (Trung bình / An toàn)" : "Tier 3 (Nguy cơ / Cảnh báo)");
        addInfoChip(infoRow, "PHÂN TẦNG RỦI RO", tierText);

        topBanner.add(infoRow);
        add(topBanner, BorderLayout.NORTH);

        // Center Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY_BOLD);
        tabs.addTab("  📊 Kết quả & Quỹ đạo GPA  ", createKetQuaPanel());
        tabs.addTab("  ⚠️ Lịch sử Cảnh báo  ", createCanhBaoPanel());
        tabs.addTab("  📝 Nhật ký Tư vấn  ", createTuVanPanel());
        tabs.addTab("  ✅ Chuyên cần & Điểm danh  ", createDiemDanhPanel());
        add(tabs, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        bottom.setBackground(UITheme.BG_WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnAiAdvisor = UITheme.createButton("🤖 Tư Vấn AI Cải Thiện", new Color(123, 31, 162), Color.WHITE);
        btnAiAdvisor.addActionListener(e -> showAiAdvisorQuickDialog());
        bottom.add(btnAiAdvisor);

        JButton btnClose = UITheme.createButton("Đóng Hồ Sơ", UITheme.PRIMARY, Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addInfoChip(JPanel parent, String label, String value) {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.fontBold(10));
        lbl.setForeground(UITheme.PRIMARY);
        JLabel val = new JLabel(value);
        val.setFont(UITheme.fontPlain(12));
        val.setForeground(UITheme.TEXT_PRIMARY);
        chip.add(lbl);
        chip.add(val);
        parent.add(chip);
    }

    private JPanel createKetQuaPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setBackground(Color.WHITE);

        String[] cols = {"Học kỳ", "Năm học", "GPA Học kỳ", "GPA Tích lũy (CPA)", "Số tín chỉ nợ", "Xếp loại học lực"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<KetQuaHocTap> list = ketQuaDAO.getKetQuaBySinhVien(sinhVien.getMaSv());
        List<Double> gpaHkList = new ArrayList<>();
        List<Double> cpaList = new ArrayList<>();
        List<String> labelList = new ArrayList<>();

        for (KetQuaHocTap kq : list) {
            String xepLoai = "Xuất sắc";
            if (kq.getGpaTichLuy() < 2.0) xepLoai = "Yếu / Kém";
            else if (kq.getGpaTichLuy() < 2.5) xepLoai = "Trung bình";
            else if (kq.getGpaTichLuy() < 3.2) xepLoai = "Khá";
            else if (kq.getGpaTichLuy() < 3.6) xepLoai = "Giỏi";

            model.addRow(new Object[]{
                "Học kỳ " + kq.getHocKy(),
                kq.getNamHoc(),
                String.format("%.2f", kq.getGpaHocKy()),
                String.format("%.2f", kq.getGpaTichLuy()),
                kq.getSoTinChiNo() + " TC",
                xepLoai
            });

            gpaHkList.add(kq.getGpaHocKy());
            cpaList.add(kq.getGpaTichLuy());
            labelList.add("HK" + kq.getHocKy() + " (" + kq.getNamHoc() + ")");
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        for (int i = 0; i < tbl.getColumnCount(); i++) {
            tbl.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Java2D Trajectory Chart Panel (Bottom)
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gpaHkList.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int padLeft = 45, padRight = 30, padTop = 25, padBottom = 30;
                int chartW = w - padLeft - padRight;
                int chartH = h - padTop - padBottom;

                // Background
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(10, 5, w - 20, h - 10, 10, 10);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(10, 5, w - 20, h - 10, 10, 10);

                // Grid lines (0.0 to 4.0)
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                for (int score = 0; score <= 4; score++) {
                    int y = padTop + chartH - (int)((score / 4.0) * chartH);
                    g2.setColor(new Color(220, 225, 230));
                    g2.drawLine(padLeft, y, padLeft + chartW, y);
                    g2.setColor(Color.GRAY);
                    g2.drawString(score + ".0", padLeft - 28, y + 4);
                }

                // Danger Threshold line (2.0 CPA)
                int yDanger = padTop + chartH - (int)((2.0 / 4.0) * chartH);
                g2.setColor(new Color(239, 68, 68, 120));
                Stroke orig = g2.getStroke();
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
                g2.drawLine(padLeft, yDanger, padLeft + chartW, yDanger);
                g2.drawString("Ngưỡng cảnh báo (2.0)", padLeft + chartW - 120, yDanger - 4);
                g2.setStroke(orig);

                // Draw Line & Points for GPA Học kỳ
                int n = gpaHkList.size();
                int[] xPoints = new int[n];
                int[] yGpa = new int[n];
                int[] yCpa = new int[n];

                for (int i = 0; i < n; i++) {
                    xPoints[i] = padLeft + (n > 1 ? (i * chartW / (n - 1)) : (chartW / 2));
                    yGpa[i] = padTop + chartH - (int)((Math.min(4.0, gpaHkList.get(i)) / 4.0) * chartH);
                    yCpa[i] = padTop + chartH - (int)((Math.min(4.0, cpaList.get(i)) / 4.0) * chartH);

                    // X-axis label
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(labelList.get(i), xPoints[i] - 30, padTop + chartH + 18);
                }

                // Draw CPA Line (Blue)
                g2.setColor(new Color(21, 101, 192));
                g2.setStroke(new BasicStroke(2.5f));
                for (int i = 0; i < n - 1; i++) {
                    g2.drawLine(xPoints[i], yCpa[i], xPoints[i+1], yCpa[i+1]);
                }
                for (int i = 0; i < n; i++) {
                    g2.setColor(new Color(21, 101, 192));
                    g2.fillOval(xPoints[i] - 5, yCpa[i] - 5, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(xPoints[i] - 2, yCpa[i] - 2, 4, 4);
                    g2.setColor(new Color(21, 101, 192));
                    g2.drawString(String.format("%.2f", cpaList.get(i)), xPoints[i] - 12, yCpa[i] - 8);
                }

                // Legend
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(new Color(21, 101, 192));
                g2.drawString("● Quỹ đạo CPA tích lũy", padLeft + 10, padTop - 8);
            }
        };
        chartPanel.setPreferredSize(new Dimension(800, 170));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tbl), chartPanel);
        split.setResizeWeight(0.6);
        split.setBorder(null);

        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel createCanhBaoPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setBackground(Color.WHITE);

        String[] cols = {"Mã quyết định", "Học kỳ", "Năm học", "Mức cảnh báo", "GPA xét", "Lý do", "Ngày QĐ", "Trạng thái tư vấn"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<CanhBaoHocVu> list = canhBaoDAO.getCanhBaoByMaSv(sinhVien.getMaSv());
        for (CanhBaoHocVu cb : list) {
            model.addRow(new Object[]{
                cb.getMaCanhBao(),
                "Học kỳ " + cb.getHocKy(),
                cb.getNamHoc(),
                UITheme.formatMucCanhBao(cb.getMucCanhBao()),
                String.format("%.2f", cb.getGpaXetDuyet()),
                cb.getLyDo(),
                cb.getNgayQuyetDinh() != null ? cb.getNgayQuyetDinh().toString() : "---",
                UITheme.formatTrangThaiTuVan(cb.getTrangThaiTuVan())
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        tbl.getColumnModel().getColumn(0).setCellRenderer(center);
        tbl.getColumnModel().getColumn(1).setCellRenderer(center);
        tbl.getColumnModel().getColumn(2).setCellRenderer(center);
        tbl.getColumnModel().getColumn(4).setCellRenderer(center);
        tbl.getColumnModel().getColumn(6).setCellRenderer(center);
        tbl.getColumnModel().getColumn(7).setCellRenderer(center);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private JPanel createTuVanPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setBackground(Color.WHITE);

        String[] cols = {"Ngày tư vấn", "CVHT phụ trách", "Hình thức", "Nội dung trao đổi", "Nguyên nhân", "Giải pháp", "Cam kết sinh viên"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<NhatKyTuVan> list = nhatKyDAO.getNhatKyBySinhVien(sinhVien.getMaSv());
        for (NhatKyTuVan nk : list) {
            model.addRow(new Object[]{
                nk.getNgayTuVan() != null ? nk.getNgayTuVan().toString() : "---",
                nk.getHoTenCvht() != null ? nk.getHoTenCvht() : nk.getMaCvht(),
                nk.getHinhThuc(),
                nk.getNoiDung(),
                nk.getNguyenNhan() != null ? nk.getNguyenNhan() : "---",
                nk.getGiaiPhap() != null ? nk.getGiaiPhap() : "---",
                nk.getCamKetSinhVien() != null ? nk.getCamKetSinhVien() : "---"
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        tbl.getColumnModel().getColumn(0).setCellRenderer(center);
        tbl.getColumnModel().getColumn(2).setCellRenderer(center);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private JPanel createDiemDanhPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setBackground(Color.WHITE);

        String[] cols = {"ID Buổi", "Ngày Điểm Danh", "Trạng Thái", "Ghi Chú Đánh Giá"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<DiemDanh> list = diemDanhDAO.getByStudent(sinhVien.getMaSv());
        for (DiemDanh d : list) {
            String tt = "ON_TIME".equals(d.getTrangThai()) ? "✔ Có mặt đúng giờ" : ("LATE".equals(d.getTrangThai()) ? "⏳ Đi muộn" : "✖ Vắng mặt");
            model.addRow(new Object[]{
                d.getIdLich(),
                d.getNgayDiemDanh() != null ? d.getNgayDiemDanh().toString() : "---",
                tt,
                d.getGhiChu() != null ? d.getGhiChu() : ""
            });
        }

        JTable tbl = new JTable(model);
        UITheme.styleTable(tbl);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    

    private void showAiAdvisorQuickDialog() {
        JDialog d = new JDialog(this, "Trợ Lý AI: Lộ Trình Cải Thiện cho " + sinhVien.getHoTen(), true);
        d.setSize(650, 480);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout(0, 10));

        KetQuaHocTap latestKq = ketQuaDAO.getKetQuaHocKyMoiNhat(sinhVien.getMaSv());
        double cpa = latestKq != null ? latestKq.getGpaTichLuy() : 1.5;
        int tinNo = latestKq != null ? latestKq.getSoTinChiNo() : 6;

        JTextArea txt = new JTextArea();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setMargin(new Insets(12, 14, 12, 14));

        StringBuilder sb = new StringBuilder();
        sb.append("🎯 LỘ TRÌNH CẢI THIỆN HỌC TẬP TỨC THÌ CHO: ").append(sinhVien.getHoTen().toUpperCase()).append("\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append("• MSSV: ").append(sinhVien.getMaSv()).append("  |  Lớp: ").append(sinhVien.getMaLop()).append("\n");
        sb.append("• CPA hiện tại: ").append(String.format("%.2f", cpa)).append(" / 4.0  |  Tín chỉ nợ: ").append(tinNo).append(" TC\n\n");
        sb.append("1. GỢI Ý MỤC TIÊU HỌC KỲ TỚI:\n");
        sb.append("   - GPA mục tiêu: Đạt tối thiểu 2.50 trở lên.\n");
        sb.append("   - Kéo CPA tích lũy vượt mốc an toàn 2.00.\n\n");
        sb.append("2. PHÂN BỔ MÔN HỌC & THỜI KHÓA BIỂU:\n");
        sb.append("   - Giới hạn tổng số tín chỉ đăng ký không quá 15 tín chỉ.\n");
        sb.append("   - Đăng ký học lại ngay 1 môn điểm F có 3 tín chỉ trong kỳ này.\n");
        sb.append("   - Đảm bảo tham gia đầy đủ các buổi điểm danh và nộp bài tập đúng hạn.\n");

        txt.setText(sb.toString());

        d.add(new JScrollPane(txt), BorderLayout.CENTER);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnClose = UITheme.createButton("Đóng", UITheme.PRIMARY, Color.WHITE);
        btnClose.addActionListener(e -> d.dispose());
        pBtn.add(btnClose);
        d.add(pBtn, BorderLayout.SOUTH);

        d.setVisible(true);
    }
}