package com.qlcvht.view;

import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final TaiKhoan currentUser;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel sideBar;

    // Nav buttons
    private JButton btnDashboard;
    private JButton btnSinhVien;
    private JButton btnKetQua;
    private JButton btnCanhBao;
    private JButton btnNhatKy;
    private JButton btnThongKe;
    private JButton btnLopHoc;

    private JButton activeBtn;

    public MainFrame(TaiKhoan user) {
        this.currentUser = user;
        setTitle("He thong Quan ly Co van Hoc tap & Canh bao Hoc vu - HUCE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // === TOP HEADER ===
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(13, 71, 161), getWidth(), 0, UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setPreferredSize(new Dimension(1300, 58));
        topBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel lblLogo = new JLabel("  \u2665  HE THONG CO VAN HOC TAP & CANH BAO HOC VU - HUCE");
        lblLogo.setFont(UITheme.fontBold(15));
        lblLogo.setForeground(Color.WHITE);
        topBar.add(lblLogo, BorderLayout.WEST);

        // User info panel (right)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userPanel.setOpaque(false);

        String roleTitle;
        switch (currentUser.getVaiTro() != null ? currentUser.getVaiTro() : "") {
            case "ADMIN":    roleTitle = "Quan tri vien"; break;
            case "QUAN_LY":  roleTitle = "Quan ly Khoa";  break;
            case "CO_VAN":   roleTitle = "Co van Hoc tap"; break;
            default:         roleTitle = currentUser.getVaiTro();
        }

        JLabel lblUser = new JLabel("Xin chao, " + currentUser.getHoTen() + "   |   " + roleTitle);
        lblUser.setFont(UITheme.fontPlain(13));
        lblUser.setForeground(new Color(210, 225, 255));
        userPanel.add(lblUser);

        JButton btnLogout = new JButton("Dang xuat");
        btnLogout.setFont(UITheme.FONT_BTN);
        btnLogout.setBackground(new Color(200, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        userPanel.add(btnLogout);

        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // === LEFT SIDEBAR ===
        sideBar = new JPanel();
        sideBar.setBackground(UITheme.BG_SIDEBAR);
        sideBar.setPreferredSize(new Dimension(215, 800));
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(new EmptyBorder(12, 0, 12, 0));

        addSidebarSection("MENU CHINH");

        btnDashboard = createNavBtn("  Dashboard", "DASHBOARD");
        btnSinhVien  = createNavBtn("  Quan ly Sinh vien", "SINH_VIEN");
        btnKetQua    = createNavBtn("  Ket qua Hoc tap", "KET_QUA");
        btnCanhBao   = createNavBtn("  Canh bao Hoc vu", "CANH_BAO");
        btnNhatKy    = createNavBtn("  Nhat ky Tu van", "NHAT_KY");
        btnThongKe   = createNavBtn("  Bao cao & Thong ke", "THONG_KE");

        sideBar.add(btnDashboard);
        sideBar.add(btnSinhVien);
        sideBar.add(btnKetQua);
        sideBar.add(btnCanhBao);
        sideBar.add(btnNhatKy);
        sideBar.add(btnThongKe);

        // Admin-only panels
        boolean isAdminOrQL = "ADMIN".equals(currentUser.getVaiTro()) || "QUAN_LY".equals(currentUser.getVaiTro());
        if (isAdminOrQL) {
            addSidebarSection("QUAN TRI");
            btnLopHoc = createNavBtn("  Quan ly Lop hoc", "LOP_HOC");
            sideBar.add(btnLopHoc);
        }

        sideBar.add(Box.createVerticalGlue());
        add(sideBar, BorderLayout.WEST);

        // === CARD PANEL (CENTER) ===
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.BG_MAIN);

        cardPanel.add(new DashboardPanel(currentUser), "DASHBOARD");
        cardPanel.add(new QuanLySinhVienPanel(currentUser), "SINH_VIEN");
        cardPanel.add(new QuanLyKetQuaHocTapPanel(currentUser), "KET_QUA");
        cardPanel.add(new QuanLyCanhBaoPanel(currentUser), "CANH_BAO");
        cardPanel.add(new NhatKyTuVanPanel(currentUser), "NHAT_KY");
        cardPanel.add(new BaoCaoThongKePanel(), "THONG_KE");

        if (isAdminOrQL) {
            cardPanel.add(new QuanLyLopHocPanel(), "LOP_HOC");
        }

        add(cardPanel, BorderLayout.CENTER);

        // Default: Dashboard
        switchCard("DASHBOARD", btnDashboard);
    }

    private void addSidebarSection(String title) {
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(UITheme.fontBold(10));
        lbl.setForeground(new Color(120, 140, 160));
        lbl.setBorder(new EmptyBorder(16, 10, 6, 10));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sideBar.add(lbl);
    }

    private JButton createNavBtn(String title, String cardName) {
        JButton btn = new JButton(title) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeBtn) {
                    g2.setColor(UITheme.BG_SIDEBAR_ACTIVE);
                } else if (getModel().isRollover()) {
                    g2.setColor(UITheme.BG_SIDEBAR_HOVER);
                } else {
                    g2.setColor(UITheme.BG_SIDEBAR);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(215, 44));
        btn.addActionListener(e -> switchCard(cardName, btn));
        return btn;
    }

    private void switchCard(String cardName, JButton btn) {
        activeBtn = btn;
        cardLayout.show(cardPanel, cardName);
        sideBar.repaint();
        // Update text color
        JButton[] allBtns = {btnDashboard, btnSinhVien, btnKetQua, btnCanhBao, btnNhatKy, btnThongKe};
        for (JButton b : allBtns) {
            if (b == null) continue;
            b.setForeground(b == btn ? Color.WHITE : UITheme.TEXT_SIDEBAR);
        }
        if (btnLopHoc != null) {
            btnLopHoc.setForeground(btnLopHoc == btn ? Color.WHITE : UITheme.TEXT_SIDEBAR);
        }
    }
}