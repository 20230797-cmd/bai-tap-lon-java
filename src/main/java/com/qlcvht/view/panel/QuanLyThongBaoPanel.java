package com.qlcvht.view.panel;

import com.qlcvht.model.SinhVienTier;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.model.ThongBao;
import com.qlcvht.service.ThongBaoService;
import com.qlcvht.util.UITheme;
import com.qlcvht.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuanLyThongBaoPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final ThongBaoService thongBaoService = new ThongBaoService();

    // Data lists
    private List<SinhVienTier> fullStudentList = new ArrayList<>();
    private List<SinhVienTier> displayedStudentList = new ArrayList<>();
    private List<ThongBao> notificationList = new ArrayList<>();

    // Top Summary KPI labels
    private JLabel lblTotalCount;
    private JLabel lblTier1Count;
    private JLabel lblTier2Count;
    private JLabel lblTier3Count;

    // Tab 1: Send Notification components
    private JComboBox<String> cbTargetGroup;
    private JComboBox<String> cbTargetClass;
    private JTextField txtTargetStudentId;
    private JComboBox<String> cbTemplate;
    private JTextField txtTieuDe;
    private JTextArea txtNoiDung;
    private JLabel lblRecipientCountBadge;
    private JButton btnSendNotification;

    // Tab 2: Filter & Simulation components
    private JTextField txtMinGpa;
    private JTextField txtMaxGpa;
    private JComboBox<String> cbFilterTier;
    private JComboBox<String> cbFilterClass;
    private JTextField txtSearchKeyword;
    private JTable tblStudents;
    private DefaultTableModel modelStudents;

    // Tab 3: History components
    private JTable tblHistory;
    private DefaultTableModel modelHistory;

    public QuanLyThongBaoPanel(TaiKhoan currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        initHeader();
        initKpiCards();
        initMainTabs();

        loadData();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("  GỬI THÔNG BÁO & PHÂN HẠNG 3 TIER SINH VIÊN");
        lblTitle.setFont(UITheme.fontBold(20));
        lblTitle.setForeground(UITheme.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Phân nhóm rủi ro (Tier 1: Tốt | Tier 2: Trung bình | Tier 3: Yếu/Cảnh báo) - Giả lập điểm & Gửi tin nhắn theo nhóm");
        lblSub.setFont(UITheme.fontPlain(12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblSub);

        headerPanel.add(titleBox, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initKpiCards() {
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setPreferredSize(new Dimension(1000, 75));

        lblTotalCount = new JLabel("0", SwingConstants.CENTER);
        lblTier1Count = new JLabel("0", SwingConstants.CENTER);
        lblTier2Count = new JLabel("0", SwingConstants.CENTER);
        lblTier3Count = new JLabel("0", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("TỔNG SINH VIÊN", lblTotalCount, UITheme.PRIMARY, "Tất cả sinh viên quản lý"));
        kpiPanel.add(createKpiCard("TIER 1: KHÁ / TỐT", lblTier1Count, UITheme.SUCCESS, "GPA >= 3.2 (Nhóm An Toàn)"));
        kpiPanel.add(createKpiCard("TIER 2: TRUNG BÌNH", lblTier2Count, UITheme.WARNING, "2.0 <= GPA < 3.2 (Cần Theo Dõi)"));
        kpiPanel.add(createKpiCard("TIER 3: YẾU / RỦI RO", lblTier3Count, UITheme.DANGER, "GPA < 2.0 (Nhóm Cảnh Báo)"));

        // Put KPI + Header together in a container
        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("  GỬI THÔNG BÁO & PHÂN HẠNG 3 TIER SINH VIÊN");
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(UITheme.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Phân loại nhóm rủi ro (Tier 1: Tốt | Tier 2: Trung bình | Tier 3: Yếu/Rủi ro cao) & Giả lập điểm GPA");
        lblSub.setFont(UITheme.fontPlain(12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblSub);

        headerPanel.add(titleBox, BorderLayout.WEST);

        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(kpiPanel, BorderLayout.SOUTH);

        // Replace Northern component
        add(topContainer, BorderLayout.NORTH);
    }

    private JPanel createKpiCard(String title, JLabel lblValue, Color headerColor, String subtext) {
        JPanel card = new JPanel(new BorderLayout(5, 5)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(UITheme.BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Color left border strip
                g2.setColor(headerColor);
                g2.fillRoundRect(0, 0, 6, getHeight(), 10, 10);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 15, 8, 10));

        JLabel lblHeader = new JLabel(title);
        lblHeader.setFont(UITheme.fontBold(11));
        lblHeader.setForeground(headerColor);

        lblValue.setFont(UITheme.fontBold(20));
        lblValue.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(UITheme.fontPlain(10));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(lblHeader, BorderLayout.NORTH);
        centerPanel.add(lblValue, BorderLayout.CENTER);
        centerPanel.add(lblSub, BorderLayout.SOUTH);

        card.add(centerPanel, BorderLayout.CENTER);
        return card;
    }

    private void initMainTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.fontBold(13));

        tabbedPane.addTab("  Gửi Thông Báo Mới  ", createTabSendNotification());
        tabbedPane.addTab("  Lọc & Giả Lập Điểm  ", createTabFilterAndSimulation());
        tabbedPane.addTab("  Lịch Sử Thông Báo Đã Gửi  ", createTabHistory());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // TAB 1: GỬI THÔNG BÁO MỚI
    // =========================================================================
    private JPanel createTabSendNotification() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Form fields container
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Target Group
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblGroup = new JLabel("Nhóm đối tượng nhận:");
        lblGroup.setFont(UITheme.fontBold(13));
        formPanel.add(lblGroup, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35;
        cbTargetGroup = new JComboBox<>(new String[]{
            "Tất cả sinh viên (ALL)",
            "Tier 1 - Khá / Tốt (GPA >= 3.2)",
            "Tier 2 - Trung bình (2.0 <= GPA < 3.2)",
            "Tier 3 - Yếu / Rủi ro cao (GPA < 2.0 / Cảnh báo)",
            "Lớp học cụ thể",
            "Sinh viên cá nhân"
        });
        cbTargetGroup.setFont(UITheme.fontPlain(13));
        cbTargetGroup.addActionListener(e -> onTargetGroupChanged());
        formPanel.add(cbTargetGroup, gbc);

        // Class & Student specific fields (Row 0 right side)
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblClassSv = new JLabel("Chi tiết (Lớp / Mã SV):");
        lblClassSv.setFont(UITheme.fontPlain(12));
        formPanel.add(lblClassSv, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.35;
        JPanel classSvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        classSvPanel.setOpaque(false);

        cbTargetClass = new JComboBox<>(new String[]{"68IT1", "68IT2", "68KX1"});
        cbTargetClass.setFont(UITheme.fontPlain(12));
        cbTargetClass.setEnabled(false);

        txtTargetStudentId = new JTextField(10);
        txtTargetStudentId.setFont(UITheme.fontPlain(12));
        txtTargetStudentId.setEnabled(false);

        classSvPanel.add(new JLabel("Lớp:"));
        classSvPanel.add(cbTargetClass);
        classSvPanel.add(new JLabel("Mã SV:"));
        classSvPanel.add(txtTargetStudentId);
        formPanel.add(classSvPanel, gbc);

        // Row 1: Templates selector
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lblTpl = new JLabel("Mẫu thông báo sẵn:");
        lblTpl.setFont(UITheme.fontBold(13));
        formPanel.add(lblTpl, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; gbc.weightx = 0.85;
        cbTemplate = new JComboBox<>(new String[]{
            "-- Chọn mẫu thông báo theo phân hạng Tier --",
            "Mẫu 1: Biểu dương sinh viên xuất sắc (Dành cho Tier 1)",
            "Mẫu 2: Duy trì phong độ & Đăng ký môn cải thiện (Dành cho Tier 2)",
            "Mẫu 3: CẢNH BÁO HỌC VỤ KHẨN CẤP & Yêu cầu gặp CVHT (Dành cho Tier 3)",
            "Mẫu 4: Thông báo mở đăng ký học bổng & phụ đạo trả nợ môn"
        });
        cbTemplate.setFont(UITheme.fontPlain(13));
        cbTemplate.addActionListener(e -> applyTemplate());
        formPanel.add(cbTemplate, gbc);

        // Row 2: Title
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.15;
        JLabel lblTieuDe = new JLabel("Tiêu đề thông báo:");
        lblTieuDe.setFont(UITheme.fontBold(13));
        formPanel.add(lblTieuDe, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 0.85;
        txtTieuDe = new JTextField();
        txtTieuDe.setFont(UITheme.fontPlain(13));
        formPanel.add(txtTieuDe, gbc);

        // Row 3: Content
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0.15;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblNoiDung = new JLabel("Nội dung tin nhắn:");
        lblNoiDung.setFont(UITheme.fontBold(13));
        formPanel.add(lblNoiDung, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; gbc.weightx = 0.85; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtNoiDung = new JTextArea(8, 50);
        txtNoiDung.setFont(UITheme.fontPlain(13));
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        JScrollPane scrollNoiDung = new JScrollPane(txtNoiDung);
        formPanel.add(scrollNoiDung, gbc);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblRecipientCountBadge = new JLabel("  Sẽ gửi cho: 0 sinh viên", SwingConstants.LEFT);
        lblRecipientCountBadge.setFont(UITheme.fontBold(13));
        lblRecipientCountBadge.setForeground(UITheme.PRIMARY_DARK);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnReset = new JButton("Làm mới");
        btnReset.setFont(UITheme.FONT_BTN);
        btnReset.setBackground(new Color(230, 235, 245));
        btnReset.addActionListener(e -> resetSendForm());

        btnSendNotification = new JButton("  GỬI THÔNG BÁO HÀNG LOẠT");
        btnSendNotification.setFont(UITheme.fontBold(13));
        btnSendNotification.setBackground(UITheme.PRIMARY);
        btnSendNotification.setForeground(Color.WHITE);
        btnSendNotification.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSendNotification.setFocusPainted(false);
        btnSendNotification.addActionListener(e -> handleSendNotification());

        btnPanel.add(btnReset);
        btnPanel.add(btnSendNotification);

        bottomBar.add(lblRecipientCountBadge, BorderLayout.WEST);
        bottomBar.add(btnPanel, BorderLayout.EAST);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================================
    // TAB 2: LỌC & GIẢ LẬP ĐIỂM
    // =========================================================================
    private JPanel createTabFilterAndSimulation() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_WHITE);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Filter Bar (North)
        JPanel filterPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 6));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            "Bộ Lọc Sinh Viên Theo GPA & Phân Hạng Tier",
            TitledBorder.LEFT, TitledBorder.TOP, UITheme.fontBold(12), UITheme.PRIMARY_DARK
        ));

        txtMinGpa = new JTextField("0.0", 4);
        txtMaxGpa = new JTextField("4.0", 4);
        txtMinGpa.setFont(UITheme.fontPlain(12));
        txtMaxGpa.setFont(UITheme.fontPlain(12));

        cbFilterTier = new JComboBox<>(new String[]{"Tất cả Tier", "Tier 1 - Khá / Tốt", "Tier 2 - Trung bình", "Tier 3 - Yếu / Rủi ro"});
        cbFilterTier.setFont(UITheme.fontPlain(12));

        cbFilterClass = new JComboBox<>(new String[]{"Tất cả Lớp", "68IT1", "68IT2", "68KX1"});
        cbFilterClass.setFont(UITheme.fontPlain(12));

        txtSearchKeyword = new JTextField(11);
        txtSearchKeyword.setFont(UITheme.fontPlain(12));

        JButton btnApplyFilter = UITheme.createButton("🔍 Lọc Danh Sách", UITheme.PRIMARY, Color.WHITE);
        btnApplyFilter.addActionListener(e -> applyStudentFilter());

        JButton btnResetFilter = UITheme.createButton("🔄 Khôi Phục", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnResetFilter.addActionListener(e -> resetStudentFilter());

        JButton btnSimulateGrade = UITheme.createButton("⚡ Giả Lập Điểm", UITheme.PURPLE, Color.WHITE);
        btnSimulateGrade.setToolTipText("Giả lập điểm thi học kỳ cho sinh viên được chọn");
        btnSimulateGrade.addActionListener(e -> handleGradeSimulation());

        JButton btnClearSimulation = UITheme.createButton("↩️ Điểm Gốc", new Color(245, 230, 230), UITheme.DANGER);
        btnClearSimulation.setToolTipText("Khôi phục lại điểm gốc ban đầu");
        btnClearSimulation.addActionListener(e -> clearSimulation());

        filterPanel.add(new JLabel("GPA:"));
        filterPanel.add(txtMinGpa);
        filterPanel.add(new JLabel("-"));
        filterPanel.add(txtMaxGpa);
        filterPanel.add(new JLabel("Tier:"));
        filterPanel.add(cbFilterTier);
        filterPanel.add(new JLabel("Lớp:"));
        filterPanel.add(cbFilterClass);
        filterPanel.add(new JLabel("Từ khóa:"));
        filterPanel.add(txtSearchKeyword);
        filterPanel.add(btnApplyFilter);
        filterPanel.add(btnResetFilter);
        filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
        filterPanel.add(btnSimulateGrade);
        filterPanel.add(btnClearSimulation);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Student Table (Center)
        String[] columns = {"STT", "Mã SV", "Họ và Tên", "Lớp", "GPA Thực Tế", "GPA Giả Lập", "Phân Hạng Tier", "Trạng Thái Học Vụ"};
        modelStudents = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblStudents = new JTable(modelStudents);
        UITheme.styleTable(tblStudents);
        tblStudents.setRowHeight(34);

        // Custom renderer for Tier & GPA
        tblStudents.getColumnModel().getColumn(6).setCellRenderer(new TierBadgeRenderer());
        tblStudents.getColumnModel().getColumn(5).setCellRenderer(new SimulatedGpaRenderer());

        tblStudents.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblStudents.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblStudents.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblStudents.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblStudents.getColumnModel().getColumn(4).setPreferredWidth(95);
        tblStudents.getColumnModel().getColumn(5).setPreferredWidth(105);
        tblStudents.getColumnModel().getColumn(6).setPreferredWidth(160);
        tblStudents.getColumnModel().getColumn(7).setPreferredWidth(160);

        JScrollPane scrollTable = new JScrollPane(tblStudents);
        panel.add(scrollTable, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // TAB 3: LỊCH SỬ THÔNG BÁO
    // =========================================================================
    private JPanel createTabHistory() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_WHITE);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Top ToolBar
        JPanel toolBar = new JPanel(new WrapLayout(FlowLayout.RIGHT, 10, 6));
        toolBar.setOpaque(false);

        JButton btnViewDetail = UITheme.createButton("🔍 Xem Chi Tiết Nội Dung", UITheme.PRIMARY, Color.WHITE);
        btnViewDetail.addActionListener(e -> viewNotificationDetail());

        JButton btnDeleteHistory = UITheme.createButton("🗑️ Xóa Thông Báo", UITheme.DANGER, Color.WHITE);
        btnDeleteHistory.addActionListener(e -> deleteNotificationHistory());

        JButton btnRefreshHistory = UITheme.createButton("🔄 Tải Lại Lịch Sử", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnRefreshHistory.addActionListener(e -> loadHistoryData());

        toolBar.add(btnViewDetail);
        toolBar.add(btnDeleteHistory);
        toolBar.add(btnRefreshHistory);

        panel.add(toolBar, BorderLayout.NORTH);

        // History Table
        String[] columns = {"ID", "Mã TB", "Tiêu Đề Thông Báo", "Nhóm Đối Tượng", "Số Người Nhận", "Ngày Gửi", "Người Gửi", "Trạng Thái"};
        modelHistory = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblHistory = new JTable(modelHistory);
        UITheme.styleTable(tblHistory);
        tblHistory.setRowHeight(32);

        tblHistory.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblHistory.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblHistory.getColumnModel().getColumn(2).setPreferredWidth(280);
        tblHistory.getColumnModel().getColumn(3).setPreferredWidth(160);
        tblHistory.getColumnModel().getColumn(4).setPreferredWidth(95);
        tblHistory.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblHistory.getColumnModel().getColumn(6).setPreferredWidth(130);

        JScrollPane scrollHistory = new JScrollPane(tblHistory);
        panel.add(scrollHistory, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // DATA LOADING & LOGIC HANDLERS
    // =========================================================================
    private void loadData() {
        fullStudentList = thongBaoService.getDanhSachSinhVienTier();
        displayedStudentList = new ArrayList<>(fullStudentList);

        updateKpiMetrics();
        renderStudentTable();
        loadHistoryData();
        updateRecipientCount();
    }

    private void updateKpiMetrics() {
        int total = fullStudentList.size();
        long tier1 = fullStudentList.stream().filter(s -> s.getTierCode() == 1).count();
        long tier2 = fullStudentList.stream().filter(s -> s.getTierCode() == 2).count();
        long tier3 = fullStudentList.stream().filter(s -> s.getTierCode() == 3).count();

        lblTotalCount.setText(String.valueOf(total));
        lblTier1Count.setText(String.valueOf(tier1));
        lblTier2Count.setText(String.valueOf(tier2));
        lblTier3Count.setText(String.valueOf(tier3));
    }

    private void renderStudentTable() {
        modelStudents.setRowCount(0);
        int stt = 1;
        for (SinhVienTier sv : displayedStudentList) {
            String giaLapStr = (sv.getGpaGiaLap() >= 0) ? String.format("%.2f ⚡", sv.getGpaGiaLap()) : "-";
            modelStudents.addRow(new Object[]{
                stt++,
                sv.getMaSv(),
                sv.getHoTen(),
                sv.getMaLop(),
                String.format("%.2f", sv.getGpaThucTe()),
                giaLapStr,
                sv.getTierTen(),
                sv.getTrangThaiHocVu()
            });
        }
    }

    private void loadHistoryData() {
        notificationList = thongBaoService.getAllThongBao();
        modelHistory.setRowCount(0);
        for (ThongBao tb : notificationList) {
            modelHistory.addRow(new Object[]{
                tb.getId(),
                tb.getMaThongBao(),
                tb.getTieuDe(),
                tb.getNhomRuiRoHienThi(),
                tb.getSoLuongNhan() + " SV",
                tb.getNgayGui(),
                tb.getNguoiGui(),
                "Đã gửi"
            });
        }
    }

    private void onTargetGroupChanged() {
        int idx = cbTargetGroup.getSelectedIndex();
        cbTargetClass.setEnabled(idx == 4);
        txtTargetStudentId.setEnabled(idx == 5);
        updateRecipientCount();
    }

    private void updateRecipientCount() {
        int idx = cbTargetGroup.getSelectedIndex();
        String targetNhom = "ALL";
        if (idx == 1) targetNhom = "TIER_1";
        else if (idx == 2) targetNhom = "TIER_2";
        else if (idx == 3) targetNhom = "TIER_3";
        else if (idx == 4) targetNhom = "LOP";
        else if (idx == 5) targetNhom = "CA_NHAN";

        String targetLop = (String) cbTargetClass.getSelectedItem();
        String targetSv = txtTargetStudentId.getText().trim();

        List<SinhVienTier> targetList = thongBaoService.getSinhVienTheoNhom(fullStudentList, targetNhom, targetLop, targetSv);
        lblRecipientCountBadge.setText(String.format("  Sẽ gửi cho: %d sinh viên thuộc đối tượng đã chọn", targetList.size()));
    }

    private void applyTemplate() {
        int idx = cbTemplate.getSelectedIndex();
        if (idx == 1) {
            cbTargetGroup.setSelectedIndex(1); // Tier 1
            txtTieuDe.setText("THÔNG BÁO BIỂU DƯƠNG SINH VIÊN HỌC TẬP XUẤT SẮC (TIER 1)");
            txtNoiDung.setText(thongBaoService.getMauNoiDung("MAU_TIER_1"));
        } else if (idx == 2) {
            cbTargetGroup.setSelectedIndex(2); // Tier 2
            txtTieuDe.setText("THÔNG BÁO DUY TRÌ PHONG ĐỘ & ĐĂNG KÝ HỌC PHẦN CẢI THIỆN (TIER 2)");
            txtNoiDung.setText(thongBaoService.getMauNoiDung("MAU_TIER_2"));
        } else if (idx == 3) {
            cbTargetGroup.setSelectedIndex(3); // Tier 3
            txtTieuDe.setText("CẢNH BÁO HỌC VỤ KHẨN CẤP & YÊU CẦU ĐĂNG KÝ LỊCH TƯ VẤN (TIER 3)");
            txtNoiDung.setText(thongBaoService.getMauNoiDung("MAU_TIER_3"));
        } else if (idx == 4) {
            cbTargetGroup.setSelectedIndex(0); // ALL
            txtTieuDe.setText("THÔNG BÁO VỀ HỌC BỔNG VÀ CÁC LỚP PHỤ ĐẠO HỌC KỲ MỚI");
            txtNoiDung.setText(thongBaoService.getMauNoiDung("MAU_KHUYEN_KHUYEN_HOC"));
        }
        updateRecipientCount();
    }

    private void handleSendNotification() {
        String tieuDe = txtTieuDe.getText().trim();
        String noiDung = txtNoiDung.getText().trim();

        if (tieuDe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề thông báo!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtTieuDe.requestFocus();
            return;
        }

        if (noiDung.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung thông báo!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtNoiDung.requestFocus();
            return;
        }

        int idx = cbTargetGroup.getSelectedIndex();
        String targetNhom = "ALL";
        if (idx == 1) targetNhom = "TIER_1";
        else if (idx == 2) targetNhom = "TIER_2";
        else if (idx == 3) targetNhom = "TIER_3";
        else if (idx == 4) targetNhom = "LOP";
        else if (idx == 5) targetNhom = "CA_NHAN";

        String targetLop = (String) cbTargetClass.getSelectedItem();
        String targetSv = txtTargetStudentId.getText().trim();

        List<SinhVienTier> recipientList = thongBaoService.getSinhVienTheoNhom(fullStudentList, targetNhom, targetLop, targetSv);
        if (recipientList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có sinh viên nào thuộc nhóm đối tượng được chọn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Xác nhận gửi thông báo tới %d sinh viên?\n\nTiêu đề: %s\nĐối tượng: %s",
                recipientList.size(), tieuDe, cbTargetGroup.getSelectedItem()),
            "Xác nhận gửi tin nhắn hàng loạt",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String senderName = (currentUser != null && currentUser.getHoTen() != null) ? currentUser.getHoTen() : "Cố vấn học tập";
            boolean success = thongBaoService.guiThongBao(tieuDe, noiDung, targetNhom, targetLop, targetSv, recipientList.size(), senderName);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    String.format("Đã gửi thông báo thành công cho %d sinh viên!", recipientList.size()),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE
                );
                resetSendForm();
                loadHistoryData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi thông báo vào CSDL!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetSendForm() {
        cbTargetGroup.setSelectedIndex(0);
        cbTemplate.setSelectedIndex(0);
        txtTieuDe.setText("");
        txtNoiDung.setText("");
        cbTargetClass.setEnabled(false);
        txtTargetStudentId.setEnabled(false);
        txtTargetStudentId.setText("");
        updateRecipientCount();
    }

    private void applyStudentFilter() {
        try {
            double minGpa = Double.parseDouble(txtMinGpa.getText().trim());
            double maxGpa = Double.parseDouble(txtMaxGpa.getText().trim());

            String tierFilter = "ALL";
            int tierIdx = cbFilterTier.getSelectedIndex();
            if (tierIdx == 1) tierFilter = "TIER_1";
            else if (tierIdx == 2) tierFilter = "TIER_2";
            else if (tierIdx == 3) tierFilter = "TIER_3";

            String classFilter = (String) cbFilterClass.getSelectedItem();
            if ("Tất cả Lớp".equals(classFilter)) classFilter = "ALL";

            String kw = txtSearchKeyword.getText().trim();

            displayedStudentList = thongBaoService.locSinhVienTier(fullStudentList, minGpa, maxGpa, tierFilter, classFilter, kw);
            renderStudentTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dải điểm GPA phải là số thực!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetStudentFilter() {
        txtMinGpa.setText("0.0");
        txtMaxGpa.setText("4.0");
        cbFilterTier.setSelectedIndex(0);
        cbFilterClass.setSelectedIndex(0);
        txtSearchKeyword.setText("");
        displayedStudentList = new ArrayList<>(fullStudentList);
        renderStudentTable();
    }

    private void handleGradeSimulation() {
        int selRow = tblStudents.getSelectedRow();
        if (selRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 sinh viên trong bảng để giả lập điểm!", "Chưa chọn sinh viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SinhVienTier targetSv = displayedStudentList.get(selRow);
        String input = JOptionPane.showInputDialog(this,
            String.format("Nhập điểm GPA giả lập cho sinh viên [%s - %s]:\n(GPA thực tế: %.2f)",
                targetSv.getMaSv(), targetSv.getHoTen(), targetSv.getGpaThucTe()),
            targetSv.getGpaGiaLap() >= 0 ? targetSv.getGpaGiaLap() : targetSv.getGpaThucTe()
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                double newGpa = Double.parseDouble(input.trim());
                if (newGpa < 0.0 || newGpa > 4.0) {
                    JOptionPane.showMessageDialog(this, "Điểm GPA giả lập phải từ 0.0 đến 4.0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int oldTier = targetSv.getTierCode();
                targetSv.setGpaGiaLap(newGpa);
                int newTier = targetSv.getTierCode();

                updateKpiMetrics();
                renderStudentTable();
                updateRecipientCount();

                String changeNote = (oldTier != newTier) ?
                    String.format("\n⚡ Phân hạng Tier thay đổi từ Tier %d sang TIER %d!", oldTier, newTier) : "";

                JOptionPane.showMessageDialog(this,
                    String.format("Đã cập nhật GPA giả lập: %.2f cho sinh viên %s!%s", newGpa, targetSv.getHoTen(), changeNote),
                    "Giả Lập Thành Công", JOptionPane.INFORMATION_MESSAGE
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm GPA phải là số thực!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearSimulation() {
        for (SinhVienTier sv : fullStudentList) {
            sv.setGpaGiaLap(-1);
        }
        updateKpiMetrics();
        renderStudentTable();
        updateRecipientCount();
        JOptionPane.showMessageDialog(this, "Đã khôi phục toàn bộ điểm GPA thực tế!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewNotificationDetail() {
        int selRow = tblHistory.getSelectedRow();
        if (selRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 thông báo trong bảng để xem chi tiết!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ThongBao tb = notificationList.get(selRow);
        JTextArea area = new JTextArea(12, 45);
        area.setText(String.format("MÃ THÔNG BÁO: %s\nNGÀY GỬI: %s\nNGƯỜI GỬI: %s\nĐỐI TƯỢNG: %s\nSỐ LƯỢNG NHẬN: %d sinh viên\n\nTIÊU ĐỀ: %s\n----------------------------------------\nNỘI DUNG:\n%s",
            tb.getMaThongBao(), tb.getNgayGui(), tb.getNguoiGui(), tb.getNhomRuiRoHienThi(), tb.getSoLuongNhan(), tb.getTieuDe(), tb.getNoiDung()
        ));
        area.setFont(UITheme.fontPlain(13));
        area.setEditable(false);
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        JOptionPane.showMessageDialog(this, scroll, "Chi Tiết Thông Báo - " + tb.getMaThongBao(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteNotificationHistory() {
        int selRow = tblHistory.getSelectedRow();
        if (selRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 thông báo để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ThongBao tb = notificationList.get(selRow);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận xóa lịch sử thông báo " + tb.getMaThongBao() + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = thongBaoService.deleteThongBao(tb.getId());
            if (ok) {
                loadHistoryData();
                JOptionPane.showMessageDialog(this, "Đã xóa lịch sử thông báo thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa thông báo!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // CUSTOM TABLE CELL RENDERERS (UI/UX ENHANCEMENTS)
    // =========================================================================
    private static class TierBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(UITheme.fontBold(12));

            String strVal = value != null ? value.toString() : "";
            if (strVal.contains("Tier 1")) {
                label.setBackground(UITheme.SUCCESS_LIGHT);
                label.setForeground(UITheme.SUCCESS);
            } else if (strVal.contains("Tier 2")) {
                label.setBackground(UITheme.WARNING_LIGHT);
                label.setForeground(UITheme.WARNING);
            } else if (strVal.contains("Tier 3")) {
                label.setBackground(UITheme.DANGER_LIGHT);
                label.setForeground(UITheme.DANGER);
            }
            if (isSelected) {
                label.setBackground(UITheme.PRIMARY_LIGHT);
            }
            return label;
        }
    }

    private static class SimulatedGpaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            String strVal = value != null ? value.toString() : "";

            if (strVal.contains("⚡")) {
                label.setFont(UITheme.fontBold(13));
                label.setForeground(UITheme.PURPLE);
                label.setBackground(UITheme.PURPLE_LIGHT);
            } else {
                label.setFont(UITheme.fontPlain(13));
                label.setForeground(UITheme.TEXT_SECONDARY);
                if (!isSelected) label.setBackground(Color.WHITE);
            }
            return label;
        }
    }
}
