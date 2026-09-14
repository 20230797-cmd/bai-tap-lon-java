package com.qlcvht.view;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.dao.*;
import com.qlcvht.model.*;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.NotificationPopup;
import com.qlcvht.util.ReportExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.DoiMatKhauDialog;
import com.qlcvht.websocket.ChatMessage;
import com.qlcvht.websocket.ChatWebSocketClient;
import com.qlcvht.websocket.WebSocketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class StudentMainFrame extends JFrame {

    private final TaiKhoan currentUser;
    private SinhVien currentStudent;
    private CoVanHocTap currentAdvisor;
    private LopHoc currentClass;

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private final NhatKyTuVanDAO nhatKyDAO = new NhatKyTuVanDAO();
    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();
    private final LichGiangDayDAO lichDAO = new LichGiangDayDAO();
    private final ChuyenCanDAO chuyenCanDAO = new ChuyenCanDAO();

    private JPanel sideBar;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton activeBtn;

    private JButton btnDashboard;
    private JButton btnKetQua;
    private JButton btnThongBao;
    private JButton btnChat;
    private JButton btnHoSo;

    // Components - Dashboard & 150 Tin Chi Progress
    private JLabel lblGpaTichLuy;
    private JLabel lblGpaHocKy;
    private JLabel lblTinChiNo;
    private JLabel lblTrangThaiHocVu;
    private JLabel lblTienDo150TinText;
    private JProgressBar progressBar150Tin;
    private JLabel lblCanhBaoTienDoBanner;
    private JLabel lblAdvisorName;
    private JLabel lblAdvisorEmail;
    private JLabel lblAdvisorPhone;
    private JLabel lblAdvisorKhoa;

    // Components - Ket Qua
    private JTable tblKetQua;
    private DefaultTableModel modelKetQua;
    private JTable tblCanhBao;
    private DefaultTableModel modelCanhBao;
    private JTable tblNhatKy;
    private DefaultTableModel modelNhatKy;
    private JTable tblChuyenCanMonHoc;
    private DefaultTableModel modelChuyenCanMonHoc;

    // Components - Thong Bao
    private JTable tblThongBao;
    private DefaultTableModel modelThongBao;
    private JTextArea txtNotificationDetail;

    // Components - Chat Truc Tuyen Realtime
    private JTextPane chatTextPane;
    private JTextField txtChatMessage;
    private JLabel lblChatWsStatus;
    private JButton btnSendChat;
    private ChatWebSocketClient wsClient;

    // Components - Lich
    private JTable tblLichHoc;
    private DefaultTableModel modelLichHoc;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public StudentMainFrame(TaiKhoan user) {
        this.currentUser = user;
        loadStudentData();
        initWebSocket();

        setTitle("CỔNG THÔNG TIN SINH VIÊN - KẾT QUẢ HỌC TẬP & CỐ VẤN HỌC VỤ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1040, 660));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Lắng nghe sự kiện đóng cửa sổ để ghi nhận đăng xuất
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                com.qlcvht.service.AuditService.getInstance().logLogout(currentUser);
            }
        });

        buildTopBar();
        buildSidebar();
        buildCardPanel();

        updateUnreadBadgeForStudent();
        switchCard("DASHBOARD", btnDashboard);
    }

    private void loadStudentData() {
        String maSv = (currentUser != null && currentUser.getMaRef() != null) ? currentUser.getMaRef() : "";
        if (maSv.isEmpty() && currentUser != null) {
            maSv = currentUser.getTenDangNhap();
        }
        currentStudent = sinhVienDAO.getSinhVienById(maSv);
        if (currentStudent != null) {
            currentAdvisor = coVanDAO.getCoVanByLop(currentStudent.getMaLop());
            List<LopHoc> lops = coVanDAO.getAllLopHoc();
            for (LopHoc l : lops) {
                if (l.getMaLop().equalsIgnoreCase(currentStudent.getMaLop())) {
                    currentClass = l;
                    break;
                }
            }
        }
    }

    private void buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(UITheme.BG_HEADER);
        topBar.setPreferredSize(new Dimension(0, 58));
        topBar.setBorder(new EmptyBorder(0, 18, 0, 16));

        // Left Brand
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("🎓");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        leftPanel.add(lblLogo);

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);

        JLabel lblUniv = new JLabel("ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)");
        lblUniv.setFont(UITheme.fontBold(14));
        lblUniv.setForeground(Color.WHITE);

        JLabel lblSystem = new JLabel("CỔNG THÔNG TIN SINH VIÊN & CỐ VẤN HỌC VỤ");
        lblSystem.setFont(UITheme.fontPlain(11));
        lblSystem.setForeground(new Color(147, 197, 253));

        brandText.add(lblUniv);
        brandText.add(lblSystem);
        leftPanel.add(brandText);

        topBar.add(leftPanel, BorderLayout.WEST);

        // Right User Controls
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 11));
        userPanel.setOpaque(false);

        String dbType = DatabaseConnection.getDatabaseDisplayStatus();
        Color dbColor = DatabaseConnection.isUsingSQLite() ? new Color(251, 191, 36) : new Color(52, 211, 153);
        JLabel lblDb = new JLabel(dbType);
        lblDb.setFont(UITheme.fontBold(11));
        lblDb.setForeground(dbColor);
        lblDb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true),
            new EmptyBorder(3, 8, 3, 8)
        ));
        userPanel.add(lblDb);

        String svShort = (currentStudent != null) 
            ? "🎓 " + currentStudent.getHoTen() + " (" + currentStudent.getMaSv() + " - " + currentStudent.getMaLop() + ")"
            : "🎓 Sinh viên: " + (currentUser != null ? currentUser.getHoTen() : "N/A");

        JLabel lblUser = new JLabel(svShort);
        lblUser.setFont(UITheme.fontBold(12));
        lblUser.setForeground(new Color(241, 245, 249));
        userPanel.add(lblUser);

        JButton btnDoiPass = UITheme.createButton("Đổi MK", new Color(30, 58, 138), Color.WHITE);
        btnDoiPass.setFont(UITheme.fontBold(11));
        btnDoiPass.setToolTipText("Thay đổi mật khẩu tài khoản");
        btnDoiPass.addActionListener(e -> new DoiMatKhauDialog(this, currentUser).setVisible(true));
        userPanel.add(btnDoiPass);

        JButton btnLogout = UITheme.createButton("Đăng Xuất", new Color(185, 28, 28), Color.WHITE);
        btnLogout.setFont(UITheme.fontBold(11));
        btnLogout.setToolTipText("Đăng xuất khỏi hệ thống");
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                com.qlcvht.service.AuditService.getInstance().logLogout(currentUser);
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        userPanel.add(btnLogout);

        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }

    private void buildSidebar() {
        sideBar = new JPanel();
        sideBar.setBackground(UITheme.BG_SIDEBAR);
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(new EmptyBorder(12, 0, 12, 0));

        addSidebarSection("DANH MỤC SINH VIÊN");
        btnDashboard = createNavBtn("  Tổng Quan Học Tập", "DASHBOARD");
        btnKetQua    = createNavBtn("  Bảng Điểm Cá Nhân", "KET_QUA");
        btnThongBao  = createNavBtn("  Hộp Thư Thông Báo", "THONG_BAO");
        btnChat      = createNavBtn("  Chat Trực Tuyến CVHT", "CHAT_TRUC_TUYEN");
        btnHoSo      = createNavBtn("  Hồ Sơ & Cố Vấn", "HO_SO");

        sideBar.add(btnDashboard);
        sideBar.add(btnKetQua);
        sideBar.add(btnThongBao);
        sideBar.add(btnChat);
        sideBar.add(btnHoSo);

        sideBar.add(Box.createVerticalGlue());

        JScrollPane sideBarScroll = new JScrollPane(sideBar);
        sideBarScroll.setPreferredSize(new Dimension(240, 720));
        sideBarScroll.setBorder(null);
        sideBarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sideBarScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(sideBarScroll, BorderLayout.WEST);
    }

    private void addSidebarSection(String title) {
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(UITheme.fontBold(10));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setBorder(new EmptyBorder(12, 12, 4, 12));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sideBar.add(lbl);
    }

    private JButton createNavBtn(String title, String cardName) {
        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeBtn) {
                    g2.setColor(UITheme.BG_SIDEBAR_ACTIVE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(147, 197, 253));
                    g2.fillRect(0, 0, 4, getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(UITheme.BG_SIDEBAR_HOVER);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g2.setColor(UITheme.BG_SIDEBAR);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.fontPlain(13));
        btn.setForeground(UITheme.TEXT_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(240, 42));
        btn.addActionListener(e -> switchCard(cardName, btn));
        return btn;
    }

    private void switchCard(String cardName, JButton btn) {
        activeBtn = btn;
        cardLayout.show(cardPanel, cardName);
        sideBar.repaint();

        JButton[] allBtns = { btnDashboard, btnKetQua, btnThongBao, btnChat, btnHoSo };
        for (JButton b : allBtns) {
            if (b != null) {
                b.setForeground(b == btn ? Color.WHITE : UITheme.TEXT_SIDEBAR);
                b.setFont(b == btn ? UITheme.fontBold(13) : UITheme.fontPlain(13));
            }
        }

        // Tải lại dữ liệu tương ứng khi chuyển tab
        if ("DASHBOARD".equals(cardName)) {
            refreshDashboardData();
        } else if ("KET_QUA".equals(cardName)) {
            refreshKetQuaData();
        } else if ("THONG_BAO".equals(cardName)) {
            if (currentStudent != null) {
                thongBaoDAO.markMessagesAsReadByStudent(currentStudent.getMaSv());
                updateUnreadBadgeForStudent();
            }
            refreshThongBaoData();
        } else if ("CHAT_TRUC_TUYEN".equals(cardName)) {
            loadChatConversation();
        }
    }

    private void buildCardPanel() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.BG_MAIN);

        cardPanel.add(buildDashboardPanel(), "DASHBOARD");
        cardPanel.add(buildKetQuaPanel(),    "KET_QUA");
        cardPanel.add(buildThongBaoPanel(),  "THONG_BAO");
        cardPanel.add(buildChatPanel(),      "CHAT_TRUC_TUYEN");
        cardPanel.add(buildHoSoPanel(),      "HO_SO");

        add(cardPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // 1. DASHBOARD PANEL
    // ==========================================
    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(18, 20, 20, 20));

        // Header Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel lblHeader = new JLabel("TỔNG QUAN TÌNH TRẠNG HỌC TẬP CỦA BẠN");
        lblHeader.setFont(UITheme.fontBold(18));
        lblHeader.setForeground(UITheme.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Theo dõi kết quả học tập, lộ trình 150 tín chỉ 5 năm và thông tin Cố vấn học tập trực tiếp");
        lblSub.setFont(UITheme.fontPlain(12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel headerBox = new JPanel(new GridLayout(2, 1, 0, 4));
        headerBox.setOpaque(false);
        headerBox.add(lblHeader);
        headerBox.add(lblSub);
        titlePanel.add(headerBox, BorderLayout.WEST);

        panel.add(titlePanel, BorderLayout.NORTH);

        // Center: Cards & Advisor Box
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // 150 Credits Progress Card
        JPanel progressCard = new JPanel(new BorderLayout(10, 8));
        progressCard.setBackground(Color.WHITE);
        progressCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));
        progressCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel progHeader = new JPanel(new BorderLayout());
        progHeader.setOpaque(false);
        JLabel lblProgTitle = new JLabel("🎓 TIẾN ĐỘ ĐÀO TẠO TOÀN KHÓA (CHUẨN 150 TÍN CHỈ - 5 NĂM)");
        lblProgTitle.setFont(UITheme.fontBold(13));
        lblProgTitle.setForeground(UITheme.PRIMARY_DARK);

        lblTienDo150TinText = new JLabel("Đã tích lũy: 0 / 150 Tín chỉ (0.0%) - Sinh viên Năm 1");
        lblTienDo150TinText.setFont(UITheme.fontBold(12));
        lblTienDo150TinText.setForeground(new Color(30, 64, 175));

        progHeader.add(lblProgTitle, BorderLayout.WEST);
        progHeader.add(lblTienDo150TinText, BorderLayout.EAST);
        progressCard.add(progHeader, BorderLayout.NORTH);

        progressBar150Tin = new JProgressBar(0, 150);
        progressBar150Tin.setValue(0);
        progressBar150Tin.setStringPainted(true);
        progressBar150Tin.setFont(UITheme.fontBold(12));
        progressBar150Tin.setPreferredSize(new Dimension(300, 24));
        progressBar150Tin.setForeground(new Color(37, 99, 235));
        progressCard.add(progressBar150Tin, BorderLayout.CENTER);

        lblCanhBaoTienDoBanner = new JLabel(" ");
        lblCanhBaoTienDoBanner.setFont(UITheme.fontBold(11));
        lblCanhBaoTienDoBanner.setForeground(new Color(220, 38, 38));
        progressCard.add(lblCanhBaoTienDoBanner, BorderLayout.SOUTH);

        centerPanel.add(progressCard);
        centerPanel.add(Box.createVerticalStrut(12));

        // 4 KPI Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        kpiGrid.setPreferredSize(new Dimension(1000, 110));

        lblGpaTichLuy = new JLabel("0.00 / 4.0", SwingConstants.CENTER);
        lblGpaHocKy = new JLabel("0.00 / 4.0", SwingConstants.CENTER);
        lblTinChiNo = new JLabel("0 Tín chỉ", SwingConstants.CENTER);
        lblTrangThaiHocVu = new JLabel("Đang học", SwingConstants.CENTER);

        kpiGrid.add(createKpiCard("GPA TÍCH LŨY", lblGpaTichLuy, new Color(37, 99, 235), "Điểm trung bình tích lũy toàn khóa"));
        kpiGrid.add(createKpiCard("GPA HỌC KỲ MỚI NHẤT", lblGpaHocKy, new Color(13, 148, 136), "Điểm trung bình học kỳ gần nhất"));
        kpiGrid.add(createKpiCard("TÍN CHỈ NỢ", lblTinChiNo, new Color(234, 88, 12), "Tổng số tín chỉ chưa hoàn thành"));
        kpiGrid.add(createKpiCard("TRẠNG THÁI HỌC VỤ", lblTrangThaiHocVu, new Color(124, 58, 237), "Tình trạng cảnh báo / Xếp hạng"));

        centerPanel.add(kpiGrid);
        centerPanel.add(Box.createVerticalStrut(15));

        // Middle Split: Advisor Info & Schedule
        JPanel middleSplit = new JPanel(new GridLayout(1, 2, 15, 0));
        middleSplit.setOpaque(false);

        // Advisor Card
        JPanel advisorPanel = new JPanel(new BorderLayout(10, 10));
        advisorPanel.setBackground(Color.WHITE);
        advisorPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblAdvTitle = new JLabel("👨‍🏫 CỐ VẤN HỌC TẬP PHỤ TRÁCH LỚP CỦA BẠN");
        lblAdvTitle.setFont(UITheme.fontBold(14));
        lblAdvTitle.setForeground(UITheme.PRIMARY_DARK);
        advisorPanel.add(lblAdvTitle, BorderLayout.NORTH);

        JPanel advInfoBox = new JPanel(new GridLayout(4, 1, 0, 8));
        advInfoBox.setOpaque(false);

        lblAdvisorName  = new JLabel("Họ tên: Đang tải...");
        lblAdvisorEmail = new JLabel("Email: Đang tải...");
        lblAdvisorPhone = new JLabel("Số điện thoại: Đang tải...");
        lblAdvisorKhoa  = new JLabel("Khoa: Đang tải...");

        lblAdvisorName.setFont(UITheme.fontBold(13));
        lblAdvisorEmail.setFont(UITheme.fontPlain(13));
        lblAdvisorPhone.setFont(UITheme.fontPlain(13));
        lblAdvisorKhoa.setFont(UITheme.fontPlain(13));

        advInfoBox.add(lblAdvisorName);
        advInfoBox.add(lblAdvisorEmail);
        advInfoBox.add(lblAdvisorPhone);
        advInfoBox.add(lblAdvisorKhoa);

        advisorPanel.add(advInfoBox, BorderLayout.CENTER);

        JButton btnContactAdv = UITheme.createButton("✉️ Gửi Tin Nhắn / Xin Tư Vấn", UITheme.PRIMARY, Color.WHITE);
        btnContactAdv.setFont(UITheme.fontBold(12));
        btnContactAdv.addActionListener(e -> switchCard("THONG_BAO", btnThongBao));
        advisorPanel.add(btnContactAdv, BorderLayout.SOUTH);

        middleSplit.add(advisorPanel);

        // Upcoming Schedule Card
        JPanel schedulePanel = new JPanel(new BorderLayout(10, 10));
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblSchTitle = new JLabel("📅 LỊCH SINH HOẠT & TƯ VẤN CỦA LỚP");
        lblSchTitle.setFont(UITheme.fontBold(14));
        lblSchTitle.setForeground(UITheme.PRIMARY_DARK);
        schedulePanel.add(lblSchTitle, BorderLayout.NORTH);

        String[] schCols = {"Ngày", "Thời Gian", "Tiêu Đề / Hoạt Động", "Địa Điểm", "Hình Thức"};
        modelLichHoc = new DefaultTableModel(schCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichHoc = new JTable(modelLichHoc);
        UITheme.styleTable(tblLichHoc);
        JScrollPane schScroll = new JScrollPane(tblLichHoc);
        schScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        schedulePanel.add(schScroll, BorderLayout.CENTER);

        middleSplit.add(schedulePanel);
        centerPanel.add(middleSplit);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor, String hint) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.fontBold(11));
        lblTitle.setForeground(accentColor);

        valueLabel.setFont(UITheme.fontBold(20));
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblHint = new JLabel(hint, SwingConstants.CENTER);
        lblHint.setFont(UITheme.fontPlain(11));
        lblHint.setForeground(UITheme.TEXT_SECONDARY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblHint, BorderLayout.SOUTH);
        return card;
    }

    private void refreshDashboardData() {
        if (currentStudent == null) return;

        List<KetQuaHocTap> listKQ = ketQuaDAO.getKetQuaBySinhVien(currentStudent.getMaSv());
        KetQuaHocTap latestKq = !listKQ.isEmpty() ? listKQ.get(0) : null;

        double gpaTl = (latestKq != null) ? latestKq.getGpaTichLuy() : 0.0;
        double gpaHk = (latestKq != null) ? latestKq.getGpaHocKy() : 0.0;
        int noTc = (latestKq != null) ? latestKq.getSoTinChiNo() : 0;

        lblGpaTichLuy.setText(df.format(gpaTl) + " / 4.0");
        lblGpaHocKy.setText(df.format(gpaHk) + " / 4.0");
        lblTinChiNo.setText(noTc + " Tín chỉ");

        String trangThai = currentStudent.getTrangThaiHienThi();
        if (gpaTl >= 3.2) {
            trangThai = "Tier 1: Xuất sắc / Giỏi";
            lblTrangThaiHocVu.setForeground(new Color(16, 185, 129));
        } else if (gpaTl >= 2.0 && noTc < 8) {
            trangThai = "Tier 2: Đạt chuẩn";
            lblTrangThaiHocVu.setForeground(new Color(37, 99, 235));
        } else {
            trangThai = "Tier 3: Cảnh báo học vụ";
            lblTrangThaiHocVu.setForeground(new Color(239, 68, 68));
        }
        lblTrangThaiHocVu.setText(trangThai);

        // Advisor info
        if (currentAdvisor != null) {
            lblAdvisorName.setText("Họ tên: " + currentAdvisor.getHoTen() + " (Mã CV: " + currentAdvisor.getMaCvht() + ")");
            lblAdvisorEmail.setText("Email: " + currentAdvisor.getEmail());
            lblAdvisorPhone.setText("Số điện thoại: " + (currentAdvisor.getSoDienThoai() != null ? currentAdvisor.getSoDienThoai() : "Chưa cập nhật"));
            lblAdvisorKhoa.setText("Khoa phụ trách: " + currentAdvisor.getKhoa());
        } else {
            lblAdvisorName.setText("Họ tên: Chưa phân công");
            lblAdvisorEmail.setText("Email: N/A");
            lblAdvisorPhone.setText("Số điện thoại: N/A");
            lblAdvisorKhoa.setText("Khoa: N/A");
        }

        // Upcoming schedule
        modelLichHoc.setRowCount(0);
        if (currentStudent.getMaLop() != null) {
            List<LichGiangDay> lichList = lichDAO.getByFilter(currentStudent.getMaLop(), null, null, null, null);
            for (LichGiangDay lg : lichList) {
                modelLichHoc.addRow(new Object[]{
                    lg.getNgay() != null ? lg.getNgay().toString() : "",
                    lg.getGioBatDau() + " - " + lg.getGioKetThuc(),
                    lg.getTieuDe(),
                    lg.getDiaDiem(),
                    lg.getHinhThuc()
                });
            }
        }
    }

    // ==========================================
    // 2. KET QUA HOC TAP & CANH BAO PANEL
    // ==========================================
    private JPanel buildKetQuaPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(18, 20, 20, 20));

        // Header Title & Actions
        JPanel topBox = new JPanel(new BorderLayout());
        topBox.setOpaque(false);

        JLabel lblTitle = new JLabel("BẢNG ĐIỂM & LỊCH SỬ HỌC VỤ CỦA BẠN");
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(UITheme.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Hệ thống chỉ hiển thị điểm số và quyết định học vụ của riêng tài khoản sinh viên này");
        lblSub.setFont(UITheme.fontPlain(12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel headerBox = new JPanel(new GridLayout(2, 1, 0, 4));
        headerBox.setOpaque(false);
        headerBox.add(lblTitle);
        headerBox.add(lblSub);
        topBox.add(headerBox, BorderLayout.WEST);

        JButton btnExport = UITheme.createButton("📊 Xuất Bảng Điểm Excel", UITheme.PRIMARY, Color.WHITE);
        btnExport.setFont(UITheme.fontBold(12));
        btnExport.addActionListener(e -> {
            if (currentStudent != null) {
                ExcelExporter.exportJTableToExcel(tblKetQua, "Bang_Diem_" + currentStudent.getMaSv());
            }
        });
        topBox.add(btnExport, BorderLayout.EAST);

        panel.add(topBox, BorderLayout.NORTH);

        // Center: JTabbedPane (Bảng điểm từng kỳ / Cảnh báo học vụ / Nhật ký tư vấn)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.fontBold(13));

        // Tab 2.1: Bảng điểm học kỳ
        JPanel pnlBangDiem = new JPanel(new BorderLayout(10, 10));
        pnlBangDiem.setBackground(Color.WHITE);
        pnlBangDiem.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] kqCols = {"STT", "Học Kỳ", "Năm Học", "GPA Học Kỳ", "GPA Tích Lũy", "Tín Chỉ Nợ", "Xếp Loại", "Đánh Giá Học Vụ"};
        modelKetQua = new DefaultTableModel(kqCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKetQua = new JTable(modelKetQua);
        UITheme.styleTable(tblKetQua);
        tblKetQua.getColumnModel().getColumn(0).setMaxWidth(50);
        pnlBangDiem.add(new JScrollPane(tblKetQua), BorderLayout.CENTER);

        tabbedPane.addTab("📊 Bảng Điểm Chi Tiết Từng Học Kỳ", pnlBangDiem);

        // Tab 2.2: Lịch sử Cảnh báo học vụ
        JPanel pnlCanhBao = new JPanel(new BorderLayout(10, 10));
        pnlCanhBao.setBackground(Color.WHITE);
        pnlCanhBao.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] cbCols = {"Mã Cảnh Báo", "Học Kỳ", "Năm Học", "Mức Cảnh Báo", "GPA Xét", "Lý Do", "Ngày Quyết Định", "Trạng Thái Tư Vấn"};
        modelCanhBao = new DefaultTableModel(cbCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCanhBao = new JTable(modelCanhBao);
        UITheme.styleTable(tblCanhBao);
        pnlCanhBao.add(new JScrollPane(tblCanhBao), BorderLayout.CENTER);

        tabbedPane.addTab("⚠️ Lịch Sử Cảnh Báo Học Vụ", pnlCanhBao);

        // Tab 2.3: Nhật ký các buổi tư vấn với Cố vấn
        JPanel pnlNhatKy = new JPanel(new BorderLayout(10, 10));
        pnlNhatKy.setBackground(Color.WHITE);
        pnlNhatKy.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] nkCols = {"Ngày Tư Vấn", "Cố Vấn Phụ Trách", "Hình Thức", "Nội Dung Tư Vấn", "Nguyên Nhân", "Giải Pháp Đề Xuất", "Cam Kết Của Bạn"};
        modelNhatKy = new DefaultTableModel(nkCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhatKy = new JTable(modelNhatKy);
        UITheme.styleTable(tblNhatKy);
        pnlNhatKy.add(new JScrollPane(tblNhatKy), BorderLayout.CENTER);

        tabbedPane.addTab("📝 Biên Bản / Nhật Ký Tư Vấn Của CVHT", pnlNhatKy);

        // Tab 2.4: Chuyên cần & Điều kiện dự thi (Quy định Tín chỉ)
        JPanel pnlChuyenCan = new JPanel(new BorderLayout(10, 10));
        pnlChuyenCan.setBackground(Color.WHITE);
        pnlChuyenCan.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Quy chế banner
        JPanel pnlRuleBanner = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlRuleBanner.setBackground(new Color(254, 242, 242));
        pnlRuleBanner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(252, 165, 165), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JLabel lblRuleTitle = new JLabel("📌 QUY CHẾ ĐÀO TẠO TÍN CHỈ VỀ ĐIỀU KIỆN DỰ THI (VẮNG > 20% CẤM THI)");
        lblRuleTitle.setFont(UITheme.fontBold(12));
        lblRuleTitle.setForeground(new Color(185, 28, 28));

        JLabel lblRuleDesc = new JLabel("• Môn 2 TC (10 buổi): Vắng tối đa 2 buổi (vắng >= 3 cấm thi) | Môn 3 TC (15 buổi): Vắng tối đa 3 buổi (vắng >= 4 cấm thi) | Môn 4 TC (20 buổi): Vắng tối đa 4 buổi (vắng >= 5 cấm thi)");
        lblRuleDesc.setFont(UITheme.fontPlain(11));
        lblRuleDesc.setForeground(new Color(127, 29, 29));
        pnlRuleBanner.add(lblRuleTitle);
        pnlRuleBanner.add(lblRuleDesc);
        pnlChuyenCan.add(pnlRuleBanner, BorderLayout.NORTH);

        String[] ccCols = {"STT", "Mã Môn", "Tên Môn Học", "Số TC", "Tổng Buổi", "Vắng TĐ (20%)", "Đã Vắng Nghỉ", "Đi Muộn", "Điểm CC", "Điều Kiện Dự Thi", "Lý Do / Căn Cứ"};
        modelChuyenCanMonHoc = new DefaultTableModel(ccCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChuyenCanMonHoc = new JTable(modelChuyenCanMonHoc);
        UITheme.styleTable(tblChuyenCanMonHoc);
        tblChuyenCanMonHoc.setRowHeight(32);
        tblChuyenCanMonHoc.getColumnModel().getColumn(0).setMaxWidth(45);
        tblChuyenCanMonHoc.getColumnModel().getColumn(1).setPreferredWidth(75);
        tblChuyenCanMonHoc.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblChuyenCanMonHoc.getColumnModel().getColumn(3).setPreferredWidth(55);
        tblChuyenCanMonHoc.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblChuyenCanMonHoc.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblChuyenCanMonHoc.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblChuyenCanMonHoc.getColumnModel().getColumn(7).setPreferredWidth(65);
        tblChuyenCanMonHoc.getColumnModel().getColumn(8).setPreferredWidth(70);
        tblChuyenCanMonHoc.getColumnModel().getColumn(9).setPreferredWidth(140);
        tblChuyenCanMonHoc.getColumnModel().getColumn(10).setPreferredWidth(220);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tblChuyenCanMonHoc.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tblChuyenCanMonHoc.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        tblChuyenCanMonHoc.getColumnModel().getColumn(3).setCellRenderer(centerRender);
        tblChuyenCanMonHoc.getColumnModel().getColumn(4).setCellRenderer(centerRender);
        tblChuyenCanMonHoc.getColumnModel().getColumn(5).setCellRenderer(centerRender);
        tblChuyenCanMonHoc.getColumnModel().getColumn(6).setCellRenderer(centerRender);
        tblChuyenCanMonHoc.getColumnModel().getColumn(7).setCellRenderer(centerRender);
        tblChuyenCanMonHoc.getColumnModel().getColumn(8).setCellRenderer(centerRender);

        tblChuyenCanMonHoc.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String v = value != null ? value.toString() : "";
                if (v.contains("CẤM THI") || v.contains("CAM_THI")) {
                    lbl.setForeground(new Color(185, 28, 28));
                    lbl.setText("⛔ CẤM THI");
                } else if (v.contains("NGUY CƠ") || v.contains("CANH_BAO")) {
                    lbl.setForeground(new Color(217, 119, 6));
                    lbl.setText("⚠️ NGUY CƠ");
                } else {
                    lbl.setForeground(new Color(22, 101, 52));
                    lbl.setText("✔ ĐỦ ĐIỀU KIỆN");
                }
                return lbl;
            }
        });

        pnlChuyenCan.add(new JScrollPane(tblChuyenCanMonHoc), BorderLayout.CENTER);
        tabbedPane.addTab("🎯 Chuyên Cần & Điều Kiện Dự Thi Từng Môn", pnlChuyenCan);

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void refreshKetQuaData() {
        if (currentStudent == null) return;
        String maSv = currentStudent.getMaSv();

        // 1. Load Ket Qua
        modelKetQua.setRowCount(0);
        List<KetQuaHocTap> listKQ = ketQuaDAO.getKetQuaBySinhVien(maSv);
        int stt = 1;
        for (KetQuaHocTap kq : listKQ) {
            String xepLoai = "";
            double gpa = kq.getGpaHocKy();
            if (gpa >= 3.6) xepLoai = "Xuất sắc";
            else if (gpa >= 3.2) xepLoai = "Giỏi";
            else if (gpa >= 2.5) xepLoai = "Khá";
            else if (gpa >= 2.0) xepLoai = "Trung bình";
            else xepLoai = "Yếu / Cảnh báo";

            String danhGia = (kq.getSoTinChiNo() == 0) ? "Đạt chuẩn tiến độ" : ("Còn nợ " + kq.getSoTinChiNo() + " TC");

            modelKetQua.addRow(new Object[]{
                stt++,
                "Học kỳ " + kq.getHocKy(),
                kq.getNamHoc(),
                df.format(kq.getGpaHocKy()),
                df.format(kq.getGpaTichLuy()),
                kq.getSoTinChiNo(),
                xepLoai,
                danhGia
            });
        }

        // 2. Load Canh Bao
        modelCanhBao.setRowCount(0);
        List<CanhBaoHocVu> listCB = canhBaoDAO.getCanhBaoByMaSv(maSv);
        for (CanhBaoHocVu cb : listCB) {
            modelCanhBao.addRow(new Object[]{
                cb.getMaCanhBao(),
                "Học kỳ " + cb.getHocKy(),
                cb.getNamHoc(),
                cb.getMucCanhBaoHienThi(),
                df.format(cb.getGpaXetDuyet()),
                cb.getLyDo(),
                cb.getNgayQuyetDinh() != null ? cb.getNgayQuyetDinh().toString() : "N/A",
                cb.getTrangThaiTuVanHienThi()
            });
        }

        // 3. Load Nhat Ky
        modelNhatKy.setRowCount(0);
        List<NhatKyTuVan> listNK = nhatKyDAO.getNhatKyBySinhVien(maSv);
        for (NhatKyTuVan nk : listNK) {
            modelNhatKy.addRow(new Object[]{
                nk.getNgayTuVan() != null ? nk.getNgayTuVan().toString() : "N/A",
                nk.getHoTenCvht() != null ? nk.getHoTenCvht() : nk.getMaCvht(),
                nk.getHinhThuc(),
                nk.getNoiDung(),
                nk.getNguyenNhan() != null ? nk.getNguyenNhan() : "",
                nk.getGiaiPhap() != null ? nk.getGiaiPhap() : "",
                nk.getCamKetSinhVien() != null ? nk.getCamKetSinhVien() : ""
            });
        }

        // 4. Load Chuyen Can Mon Hoc
        modelChuyenCanMonHoc.setRowCount(0);
        List<ChuyenCanMonHoc> listCC = chuyenCanDAO.getByMaSv(maSv);
        int sttCC = 1;
        for (ChuyenCanMonHoc cc : listCC) {
            int maxVang = (int) Math.floor(cc.getTongSoBuoi() * 0.20);
            int vangNghi = cc.getSoBuoiVangKhongPhep() + cc.getSoBuoiVangCoPhep();
            modelChuyenCanMonHoc.addRow(new Object[]{
                sttCC++,
                cc.getMaMon(),
                cc.getTenMon() != null ? cc.getTenMon() : cc.getMaMon(),
                cc.getSoTinChi(),
                cc.getTongSoBuoi(),
                maxVang + " buổi",
                vangNghi + " buổi",
                cc.getSoBuoiMuon(),
                String.format("%.1f", cc.getDiemChuyenCan()),
                cc.getTrangThaiDuThiHienThi(),
                cc.getLyDoCamThi() != null && !cc.getLyDoCamThi().isBlank() ? cc.getLyDoCamThi() : "Đủ điều kiện dự thi kết thúc học phần"
            });
        }
    }

    // ==========================================
    // 3. THONG BAO & TRAO DOI VOI CO VAN PANEL
    // ==========================================
    public void updateUnreadBadgeForStudent() {
        if (currentStudent == null || btnThongBao == null) return;
        int unread = thongBaoDAO.getUnreadCountForStudent(currentStudent.getMaSv());
        if (unread > 0) {
            btnThongBao.setText("<html>📢 Hộp Thư Thông Báo <span style='color:#ef4444; font-weight:bold;'>(" + unread + " mới)</span></html>");
        } else {
            btnThongBao.setText("  Hộp Thư Thông Báo");
        }
    }

    private void initWebSocket() {
        if (currentStudent == null) return;
        try {
            wsClient = WebSocketService.getInstance().getClient(currentStudent.getMaSv());
            if (wsClient != null) {
                wsClient.addListener(new ChatWebSocketClient.MessageListener() {
                    @Override
                    public void onMessageReceived(ChatMessage message) {
                        updateUnreadBadgeForStudent();
                        refreshThongBaoData();
                        loadChatConversation();

                        boolean isChat = "CHAT".equalsIgnoreCase(message.getType()) || (message.getContent() != null && !message.getContent().startsWith("📢") && !message.getContent().startsWith("⚠️"));
                        String popupTitle = isChat ? ("💬 Tin nhắn từ " + message.getFromName()) : ("📢 Thông báo: " + message.getTitle());

                        NotificationPopup.showPopup(
                            StudentMainFrame.this,
                            popupTitle,
                            message.getContent(),
                            () -> {
                                if (isChat) {
                                    switchCard("CHAT_TRUC_TUYEN", btnChat);
                                } else {
                                    switchCard("THONG_BAO", btnThongBao);
                                }
                            }
                        );
                    }

                    @Override
                    public void onStatusChanged(boolean connected, String statusText) {
                        if (lblChatWsStatus != null) {
                            lblChatWsStatus.setText(connected ? "🟢 WebSocket Online (Real-time)" : "🔴 WebSocket Offline");
                            lblChatWsStatus.setForeground(connected ? new Color(16, 185, 129) : new Color(239, 68, 68));
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel buildThongBaoPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(18, 20, 20, 20));

        // Header Title
        JPanel topBox = new JPanel(new BorderLayout());
        topBox.setOpaque(false);

        JLabel lblTitle = new JLabel("HỘP THƯ THÔNG BÁO HỌC VỤ & QUYẾT ĐỊNH CẢNH BÁO");
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(UITheme.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Xem toàn bộ thông báo chung của Nhà trường, thông báo của Khoa và các quyết định cảnh báo học vụ riêng cho bạn");
        lblSub.setFont(UITheme.fontPlain(12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel headerBox = new JPanel(new GridLayout(2, 1, 0, 4));
        headerBox.setOpaque(false);
        headerBox.add(lblTitle);
        headerBox.add(lblSub);
        topBox.add(headerBox, BorderLayout.WEST);

        JButton btnReloadTb = UITheme.createButton("Làm Mới Hộp Thư", UITheme.PRIMARY, Color.WHITE);
        btnReloadTb.setFont(UITheme.fontBold(12));
        btnReloadTb.addActionListener(e -> refreshThongBaoData());
        topBox.add(btnReloadTb, BorderLayout.EAST);

        panel.add(topBox, BorderLayout.NORTH);

        // Center Split: Left is Notification List, Right is Detail Viewer
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);

        String[] tbCols = {"Mã TB", "Phân Loại", "Tiêu Đề", "Người Gửi", "Ngày Gửi"};
        modelThongBao = new DefaultTableModel(tbCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblThongBao = new JTable(modelThongBao);
        UITheme.styleTable(tblThongBao);
        tblThongBao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblThongBao.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onSelectNotification();
            }
        });

        JScrollPane tbScroll = new JScrollPane(tblThongBao);
        tbScroll.setPreferredSize(new Dimension(500, 240));

        txtNotificationDetail = new JTextArea();
        txtNotificationDetail.setFont(UITheme.fontPlain(13));
        txtNotificationDetail.setEditable(false);
        txtNotificationDetail.setLineWrap(true);
        txtNotificationDetail.setWrapStyleWord(true);
        txtNotificationDetail.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtNotificationDetail.setText("Chọn một thông báo ở danh sách trên để xem chi tiết nội dung...");

        JScrollPane detailScroll = new JScrollPane(txtNotificationDetail);
        detailScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            "Nội dung chi tiết thông báo",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.fontBold(12),
            UITheme.PRIMARY_DARK
        ));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tbScroll, detailScroll);
        split.setResizeWeight(0.45);
        split.setBorder(null);
        centerPanel.add(split, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 12));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(18, 20, 20, 20));

        // Header
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setOpaque(false);

        String advName = (currentAdvisor != null) ? currentAdvisor.getHoTen() : "Cố vấn học tập";
        JLabel lblChatTitle = new JLabel("💬 TRAO ĐỔI TRỰC TUYẾN VỚI CỐ VẤN HỌC TẬP: " + advName.toUpperCase());
        lblChatTitle.setFont(UITheme.fontBold(16));
        lblChatTitle.setForeground(UITheme.PRIMARY_DARK);

        lblChatWsStatus = new JLabel("🟢 WebSocket Online (Real-time)");
        lblChatWsStatus.setFont(UITheme.fontBold(12));
        lblChatWsStatus.setForeground(new Color(16, 185, 129));

        chatHeader.add(lblChatTitle, BorderLayout.WEST);
        chatHeader.add(lblChatWsStatus, BorderLayout.EAST);
        panel.add(chatHeader, BorderLayout.NORTH);

        // Chat Container
        JPanel chatContainer = new JPanel(new BorderLayout(0, 10));
        chatContainer.setBackground(Color.WHITE);
        chatContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        // Chat Stream Text Pane
        chatTextPane = new JTextPane();
        chatTextPane.setContentType("text/html");
        chatTextPane.setEditable(false);
        chatTextPane.setFont(UITheme.fontPlain(13));
        JScrollPane chatScroll = new JScrollPane(chatTextPane);
        chatScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        chatContainer.add(chatScroll, BorderLayout.CENTER);

        // Bottom Input Box
        JPanel chatInputBox = new JPanel(new BorderLayout(8, 0));
        chatInputBox.setOpaque(false);
        chatInputBox.setBorder(new EmptyBorder(8, 0, 0, 0));

        txtChatMessage = new JTextField();
        txtChatMessage.setFont(UITheme.fontPlain(13));
        txtChatMessage.setPreferredSize(new Dimension(300, 40));
        txtChatMessage.putClientProperty("JTextField.placeholderText", "Nhập nội dung tin nhắn gửi Cố vấn học tập...");
        txtChatMessage.addActionListener(e -> onSendChatMessage());

        btnSendChat = UITheme.createButton("  🚀 Gửi Tin Nhắn (Realtime)  ", UITheme.PRIMARY, Color.WHITE);
        btnSendChat.setFont(UITheme.fontBold(12));
        btnSendChat.setPreferredSize(new Dimension(200, 40));
        btnSendChat.addActionListener(e -> onSendChatMessage());

        chatInputBox.add(txtChatMessage, BorderLayout.CENTER);
        chatInputBox.add(btnSendChat, BorderLayout.EAST);
        chatContainer.add(chatInputBox, BorderLayout.SOUTH);

        panel.add(chatContainer, BorderLayout.CENTER);

        // Load conversation
        SwingUtilities.invokeLater(this::loadChatConversation);

        return panel;
    }

    private List<ThongBao> currentStudentNotifications;

    private void refreshThongBaoData() {
        if (currentStudent == null) return;
        modelThongBao.setRowCount(0);
        txtNotificationDetail.setText("Chọn một thông báo ở danh sách trên để xem chi tiết nội dung...");

        // Xác định Tier của sinh viên
        KetQuaHocTap kq = ketQuaDAO.getKetQuaHocKyMoiNhat(currentStudent.getMaSv());
        String tierCode = "TIER_2";
        if (kq != null) {
            if (kq.getGpaTichLuy() >= 3.2) tierCode = "TIER_1";
            else if (kq.getGpaTichLuy() < 2.0 || kq.getSoTinChiNo() >= 8) tierCode = "TIER_3";
        }

        currentStudentNotifications = thongBaoDAO.getThongBaoForSinhVien(currentStudent.getMaSv(), currentStudent.getMaLop(), tierCode);
        for (ThongBao tb : currentStudentNotifications) {
            String loaiTb = "📢 Thông Báo Chung";
            if (currentStudent.getMaSv().equalsIgnoreCase(tb.getMaSv())) {
                loaiTb = "🔒 GỬI RIÊNG BẠN";
            } else if (currentStudent.getMaLop() != null && currentStudent.getMaLop().equalsIgnoreCase(tb.getMaLop())) {
                loaiTb = "👥 Thông Báo Lớp";
            } else if ("TIER_1".equals(tb.getNhomRuiRo())) {
                loaiTb = "🌟 Khen Thưởng (Tier 1)";
            } else if ("TIER_3".equals(tb.getNhomRuiRo())) {
                loaiTb = "⚠️ Cảnh Báo (Tier 3)";
            } else if ("PHAN_HOI_SV".equals(tb.getNhomRuiRo())) {
                loaiTb = "📬 Bạn Đã Gửi CVHT";
            }

            modelThongBao.addRow(new Object[]{
                tb.getMaThongBao(),
                loaiTb,
                tb.getTieuDe(),
                tb.getNguoiGui() != null ? tb.getNguoiGui() : "Cố vấn học tập",
                tb.getNgayGui() != null ? tb.getNgayGui() : "N/A"
            });
        }
    }

    private void loadChatConversation() {
        if (currentStudent == null || chatTextPane == null) return;
        List<ThongBao> history = thongBaoDAO.getChatHistory(currentStudent.getMaSv());
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Segoe UI, sans-serif; padding: 10px; background-color: #f8fafc; }");
        html.append(".msg-box { margin-bottom: 12px; }");
        html.append(".bubble-sv { background-color: #2563eb; color: #ffffff; padding: 8px 14px; border-radius: 12px; display: inline-block; max-width: 75%; text-align: left; }");
        html.append(".bubble-cv { background-color: #e2e8f0; color: #0f172a; padding: 8px 14px; border-radius: 12px; display: inline-block; max-width: 75%; }");
        html.append(".meta-sv { font-size: 11px; color: #93c5fd; margin-bottom: 3px; }");
        html.append(".meta-cv { font-size: 11px; color: #64748b; margin-bottom: 3px; }");
        html.append("</style></head><body>");

        if (history.isEmpty()) {
            html.append("<div style='text-align:center; color:#94a3b8; margin-top:20px;'>Chưa có tin nhắn nào trao đổi với Cố vấn học tập. Bạn có thể gửi câu hỏi hoặc đề nghị tư vấn ngay bên dưới!</div>");
        } else {
            for (ThongBao tb : history) {
                boolean isFromStudent = (tb.getNhomRuiRo() != null && tb.getNhomRuiRo().contains("SV"))
                                     || (tb.getTrangThai() != null && tb.getTrangThai().contains("SV"));

                String senderName = tb.getNguoiGui() != null ? tb.getNguoiGui() : (isFromStudent ? "Bạn" : "Cố vấn học tập");
                String time = tb.getNgayGui() != null ? tb.getNgayGui() : "";

                if (isFromStudent) {
                    html.append("<div class='msg-box' style='text-align:right;'>");
                    html.append("<div style='font-size:11px; color:#64748b; margin-bottom:3px;'><b>🎓 ").append(senderName).append("</b> • ").append(time).append("</div>");
                    html.append("<div class='bubble-sv'>");
                    if (tb.getTieuDe() != null && !tb.getTieuDe().isEmpty()) {
                        html.append("<div style='font-weight:bold; margin-bottom:3px;'>").append(escapeHtml(tb.getTieuDe())).append("</div>");
                    }
                    html.append(escapeHtml(tb.getNoiDung()).replace("\n", "<br/>"));
                    html.append("</div></div>");
                } else {
                    html.append("<div class='msg-box' style='text-align:left;'>");
                    html.append("<div class='meta-cv'><b>👨‍🏫 ").append(senderName).append("</b> • ").append(time).append("</div>");
                    html.append("<div class='bubble-cv'>");
                    if (tb.getTieuDe() != null && !tb.getTieuDe().isEmpty()) {
                        html.append("<div style='font-weight:bold; margin-bottom:3px;'>").append(escapeHtml(tb.getTieuDe())).append("</div>");
                    }
                    html.append(escapeHtml(tb.getNoiDung()).replace("\n", "<br/>"));
                    html.append("</div></div>");
                }
            }
        }

        html.append("</body></html>");
        chatTextPane.setText(html.toString());
        chatTextPane.setCaretPosition(chatTextPane.getDocument().getLength());
    }

    private void onSelectNotification() {
        int row = tblThongBao.getSelectedRow();
        if (row >= 0 && currentStudentNotifications != null && row < currentStudentNotifications.size()) {
            ThongBao tb = currentStudentNotifications.get(row);
            StringBuilder sb = new StringBuilder();
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("📌 TIÊU ĐỀ: ").append(tb.getTieuDe()).append("\n");
            sb.append("👤 NGƯỜI GỬI: ").append(tb.getNguoiGui()).append("\n");
            sb.append("🕒 THỜI GIAN: ").append(tb.getNgayGui()).append("\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            sb.append(tb.getNoiDung());
            txtNotificationDetail.setText(sb.toString());
            txtNotificationDetail.setCaretPosition(0);
        }
    }

    private void onSendChatMessage() {
        if (txtChatMessage == null || currentStudent == null) return;
        String content = txtChatMessage.getText().trim();
        if (content.isEmpty()) return;

        String toAdvisorId = (currentAdvisor != null) ? currentAdvisor.getMaCvht() : "ALL_ADVISORS";

        // 1. Save to Database
        thongBaoDAO.guiPhanHoiChoCoVan(
            currentStudent.getMaSv(),
            currentStudent.getHoTen(),
            "Câu hỏi tư vấn",
            content
        );

        // 2. Send via WebSocket Realtime
        if (wsClient != null && wsClient.isOpen()) {
            ChatMessage msg = new ChatMessage(
                currentStudent.getMaSv(),
                currentStudent.getHoTen(),
                "SINH_VIEN",
                toAdvisorId,
                "Câu hỏi tư vấn",
                content
            );
            wsClient.sendChatMessage(msg);
        }

        txtChatMessage.setText("");
        refreshThongBaoData();
        loadChatConversation();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }

    // ==========================================
    // 4. HO SO SINH VIEN & DOI MAT KHAU PANEL
    // ==========================================
    private JPanel buildHoSoPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(18, 20, 20, 20));

        JLabel lblTitle = new JLabel("HỒ SƠ CÁ NHÂN & CỐ VẤN HỌC TẬP");
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Card 1: Student Details
        JPanel pnlSv = new JPanel(new BorderLayout(10, 10));
        pnlSv.setBackground(Color.WHITE);
        pnlSv.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblSvHeader = new JLabel("👤 THÔNG TIN SINH VIÊN");
        lblSvHeader.setFont(UITheme.fontBold(15));
        lblSvHeader.setForeground(UITheme.PRIMARY_DARK);
        pnlSv.add(lblSvHeader, BorderLayout.NORTH);

        JPanel svGrid = new JPanel(new GridLayout(8, 1, 0, 10));
        svGrid.setOpaque(false);

        if (currentStudent != null) {
            svGrid.add(createDetailRow("Mã Sinh Viên (MSSV):", currentStudent.getMaSv()));
            svGrid.add(createDetailRow("Họ và Tên:", currentStudent.getHoTen()));
            svGrid.add(createDetailRow("Ngày Sinh:", currentStudent.getNgaySinh() != null ? currentStudent.getNgaySinh().toString() : "N/A"));
            svGrid.add(createDetailRow("Giới Tính:", currentStudent.getGioiTinh() != null ? currentStudent.getGioiTinh() : "N/A"));
            svGrid.add(createDetailRow("Lớp Học:", currentStudent.getMaLop() + (currentStudent.getTenLop() != null ? " - " + currentStudent.getTenLop() : "")));
            svGrid.add(createDetailRow("Email:", currentStudent.getEmail() != null ? currentStudent.getEmail() : "N/A"));
            svGrid.add(createDetailRow("Số Điện Thoại:", currentStudent.getSoDienThoai() != null ? currentStudent.getSoDienThoai() : "N/A"));
            svGrid.add(createDetailRow("Trạng Thái Đào Tạo:", currentStudent.getTrangThaiHienThi()));
        }
        pnlSv.add(svGrid, BorderLayout.CENTER);

        JButton btnChangePass = UITheme.createButton("🔑 Đổi Mật Khẩu Tài Khoản", UITheme.PRIMARY, Color.WHITE);
        btnChangePass.setFont(UITheme.fontBold(13));
        btnChangePass.setPreferredSize(new Dimension(200, 40));
        btnChangePass.addActionListener(e -> new DoiMatKhauDialog(this, currentUser).setVisible(true));
        pnlSv.add(btnChangePass, BorderLayout.SOUTH);

        centerPanel.add(pnlSv);

        // Card 2: Advisor & Class Details
        JPanel pnlAdv = new JPanel(new BorderLayout(10, 10));
        pnlAdv.setBackground(Color.WHITE);
        pnlAdv.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblAdvHeader = new JLabel("👨‍🏫 THÔNG TIN CỐ VẤN HỌC TẬP & LỚP");
        lblAdvHeader.setFont(UITheme.fontBold(15));
        lblAdvHeader.setForeground(UITheme.PRIMARY_DARK);
        pnlAdv.add(lblAdvHeader, BorderLayout.NORTH);

        JPanel advGrid = new JPanel(new GridLayout(8, 1, 0, 10));
        advGrid.setOpaque(false);

        if (currentAdvisor != null) {
            advGrid.add(createDetailRow("Mã Cố Vấn:", currentAdvisor.getMaCvht()));
            advGrid.add(createDetailRow("Họ và Tên CVHT:", currentAdvisor.getHoTen()));
            advGrid.add(createDetailRow("Email Liên Hệ:", currentAdvisor.getEmail()));
            advGrid.add(createDetailRow("Số Điện Thoại:", currentAdvisor.getSoDienThoai() != null ? currentAdvisor.getSoDienThoai() : "N/A"));
            advGrid.add(createDetailRow("Khoa Phụ Trách:", currentAdvisor.getKhoa()));
        }
        if (currentClass != null) {
            advGrid.add(createDetailRow("Khóa Học:", "Khóa " + currentClass.getKhoaHoc()));
            advGrid.add(createDetailRow("Khoa:", currentClass.getKhoa()));
        }
        pnlAdv.add(advGrid, BorderLayout.CENTER);

        centerPanel.add(pnlAdv);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel lblName = new JLabel(label);
        lblName.setFont(UITheme.fontBold(12));
        lblName.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(UITheme.fontPlain(13));
        lblVal.setForeground(UITheme.TEXT_PRIMARY);

        row.add(lblName, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.EAST);
        return row;
    }
}
