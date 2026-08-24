package com.qlcvht.view;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.DoiMatKhauDialog;
import com.qlcvht.view.panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final TaiKhoan currentUser;
    private final String role;

    private JPanel sideBar;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton activeBtn;

    private JButton btnDashboard;
    private JButton btnLichGiangDay;
    private JButton btnSinhVien;
    private JButton btnKetQua;
    private JButton btnDiemDanh;
    private JButton btnCanhBao;
    private JButton btnNhatKy;
    private JButton btnThongBao;
    private JButton btnThongKe;
    private JButton btnLopHoc;

    public MainFrame(TaiKhoan user) {
        this.currentUser = user;
        this.role = (user != null && user.getVaiTro() != null) ? user.getVaiTro() : "CO_VAN";

        setTitle("H? th?ng Qu?n l? C? v?n H?c t?p & C?nh b?o H?c v?");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildTopBar();
        buildSidebar();
        buildCardPanel();

        switchCard("DASHBOARD", btnDashboard);
    }

    private void buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.BG_HEADER);
        topBar.setPreferredSize(new Dimension(0, 56));
        topBar.setBorder(new EmptyBorder(0, 20, 0, 16));

        JLabel lblTitle = new JLabel("??  H? TH?NG C? V?N H?C T?P & C?NH B?O H?C V?");
        lblTitle.setFont(UITheme.fontBold(15));
        lblTitle.setForeground(Color.WHITE);
        topBar.add(lblTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        userPanel.setOpaque(false);

        String dbType = DatabaseConnection.isUsingSQLite() ? "? SQLite (Offline)" : "? MySQL (Online)";
        Color dbColor = DatabaseConnection.isUsingSQLite() ? new Color(251, 191, 36) : new Color(52, 211, 153);
        JLabel lblDb = new JLabel(dbType);
        lblDb.setFont(UITheme.fontBold(11));
        lblDb.setForeground(dbColor);
        lblDb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true),
            new EmptyBorder(3, 8, 3, 8)
        ));
        userPanel.add(lblDb);

        String roleTitle = switch (role) {
            case "ADMIN"   -> "?? Qu?n tr? vi?n";
            case "QUAN_LY" -> "??? Qu?n l? Khoa";
            case "CO_VAN"  -> "?? C? v?n H?c t?p";
            default        -> role;
        };

        JLabel lblUser = new JLabel(roleTitle + " ? " + (currentUser != null ? currentUser.getHoTen() : "User"));
        lblUser.setFont(UITheme.fontPlain(13));
        lblUser.setForeground(new Color(226, 232, 240));
        userPanel.add(lblUser);

        JButton btnDoiPass = UITheme.createButton("??i MK", new Color(30, 58, 138), Color.WHITE);
        btnDoiPass.setFont(UITheme.fontBold(11));
        btnDoiPass.setToolTipText("Thay ??i m?t kh?u t?i kho?n");
        btnDoiPass.addActionListener(e -> new DoiMatKhauDialog(this, currentUser).setVisible(true));
        userPanel.add(btnDoiPass);

        JButton btnLogout = UITheme.createButton("??ng Xu?t", new Color(185, 28, 28), Color.WHITE);
        btnLogout.setFont(UITheme.fontBold(11));
        btnLogout.setToolTipText("??ng xu?t kh?i h? th?ng");
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "B?n c? ch?c ch?n mu?n ??ng xu?t kh?i h? th?ng?",
                "X?c nh?n ??ng xu?t",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
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
        sideBar.setBorder(new EmptyBorder(8, 0, 8, 0));

        addSidebarSection("T?NG QUAN & L?CH TR?NH");
        btnDashboard    = createNavBtn("  ??  T?ng Quan (Dashboard)", "DASHBOARD");
        btnLichGiangDay = createNavBtn("  ??  L?ch Gi?ng D?y & CVHT", "LICH_GIANG_DAY");
        sideBar.add(btnDashboard);
        sideBar.add(btnLichGiangDay);

        addSidebarSection("QU?N L? H?C V? & SINH VI?N");
        btnSinhVien = createNavBtn("  ??  H? S? Sinh Vi?n", "SINH_VIEN");
        btnKetQua   = createNavBtn("  ??  B?ng ?i?m & K?t Qu? HT", "KET_QUA");
        btnDiemDanh = createNavBtn("  ?  ?i?m Danh & Chuy?n C?n", "DIEM_DANH");
        sideBar.add(btnSinhVien);
        sideBar.add(btnKetQua);
        if (!"QUAN_LY".equals(role)) {
            sideBar.add(btnDiemDanh);
        }

        addSidebarSection("C? V?N & C?NH B?O H?C V?");
        btnCanhBao  = createNavBtn("  ??  C?nh B?o H?c V?", "CANH_BAO");
        btnNhatKy   = createNavBtn("  ??  Nh?t K? T? V?n CVHT", "NHAT_KY");
        btnThongBao = createNavBtn("  ??  Th?ng B?o Sinh Vi?n", "THONG_BAO");
        sideBar.add(btnCanhBao);
        sideBar.add(btnNhatKy);
        sideBar.add(btnThongBao);

        addSidebarSection("B?O C?O & TH?NG K?");
        btnThongKe = createNavBtn("  ??  B?o C?o & Th?ng K?", "THONG_KE");
        sideBar.add(btnThongKe);

        boolean isAdminOrQL = "ADMIN".equals(role) || "QUAN_LY".equals(role);
        if (isAdminOrQL) {
            addSidebarSection("QU?N TR? H? TH?NG");
            btnLopHoc = createNavBtn("  ??  Qu?n L? L?p & CVHT", "LOP_HOC");
            sideBar.add(btnLopHoc);
        }

        sideBar.add(Box.createVerticalGlue());

        JScrollPane sideBarScroll = new JScrollPane(sideBar);
        sideBarScroll.setPreferredSize(new Dimension(250, 720));
        sideBarScroll.setBorder(null);
        sideBarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sideBarScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(sideBarScroll, BorderLayout.WEST);
    }

    private void buildCardPanel() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.BG_MAIN);

        cardPanel.add(new DashboardPanel(currentUser),          "DASHBOARD");
        cardPanel.add(new LichGiangDayPanel(currentUser),       "LICH_GIANG_DAY");
        cardPanel.add(new QuanLySinhVienPanel(currentUser),     "SINH_VIEN");
        cardPanel.add(new QuanLyKetQuaHocTapPanel(currentUser), "KET_QUA");
        cardPanel.add(new QuanLyDiemDanhPanel(currentUser),     "DIEM_DANH");
        cardPanel.add(new QuanLyCanhBaoPanel(currentUser),      "CANH_BAO");
        cardPanel.add(new NhatKyTuVanPanel(currentUser),        "NHAT_KY");
        cardPanel.add(new QuanLyThongBaoPanel(currentUser),     "THONG_BAO");
        cardPanel.add(new BaoCaoThongKePanel(),                 "THONG_KE");

        if ("ADMIN".equals(role) || "QUAN_LY".equals(role)) {
            cardPanel.add(new QuanLyLopHocPanel(), "LOP_HOC");
        }

        add(cardPanel, BorderLayout.CENTER);
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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(250, 40));
        btn.addActionListener(e -> switchCard(cardName, btn));
        return btn;
    }

    private void switchCard(String cardName, JButton btn) {
        activeBtn = btn;
        cardLayout.show(cardPanel, cardName);
        sideBar.repaint();

        JButton[] allBtns = {
            btnDashboard, btnLichGiangDay, btnSinhVien, btnKetQua,
            btnDiemDanh, btnCanhBao, btnNhatKy, btnThongBao, btnThongKe, btnLopHoc
        };
        for (JButton b : allBtns) {
            if (b != null) {
                b.setForeground(b == btn ? Color.WHITE : UITheme.TEXT_SIDEBAR);
                b.setFont(b == btn ? UITheme.fontBold(13) : UITheme.fontPlain(13));
            }
        }
    }
}
