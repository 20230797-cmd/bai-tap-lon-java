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
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel sideBar;

    // Nav buttons
    private JButton btnDashboard;
    private JButton btnSinhVien;
    private JButton btnKetQua;
    private JButton btnCanhBao;
    private JButton btnNhatKy;
    private JButton btnThongBao;
    private JButton btnThongKe;
    private JButton btnLopHoc;

    private JButton activeBtn;

    public MainFrame(TaiKhoan user) {
        this.currentUser = user;
        setTitle("Hệ thống Quản lý Cố vấn Học tập & Cảnh báo Học vụ - HUCE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 780);
        setMinimumSize(new Dimension(1080, 650));
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
        topBar.setBorder(new EmptyBorder(0, 18, 0, 18));

        // Logo & Title
        JLabel lblLogo = new JLabel("🎓  HỆ THỐNG CỐ VẤN HỌC TẬP & CẢNH BÁO HỌC VỤ");
        lblLogo.setFont(UITheme.fontBold(15));
        lblLogo.setForeground(Color.WHITE);
        topBar.add(lblLogo, BorderLayout.WEST);

        // User info panel (right)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setOpaque(false);

        // DB Status Pill
        String dbType = DatabaseConnection.getDatabaseType();
        JLabel lblDb = new JLabel("● " + dbType);
        lblDb.setFont(UITheme.fontBold(11));
        lblDb.setForeground(DatabaseConnection.isUsingSQLite() ? new Color(255, 235, 160) : new Color(170, 255, 190));
        lblDb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        userPanel.add(lblDb);

        String roleTitle;
        switch (currentUser.getVaiTro() != null ? currentUser.getVaiTro() : "") {
            case "ADMIN":    roleTitle = "Quản trị viên"; break;
            case "QUAN_LY":  roleTitle = "Quản lý Khoa";  break;
            case "CO_VAN":   roleTitle = "Cố vấn Học tập"; break;
            default:         roleTitle = currentUser.getVaiTro();
        }

        JLabel lblUser = new JLabel("Xin chào, " + currentUser.getHoTen() + " (" + roleTitle + ")");
        lblUser.setFont(UITheme.fontPlain(13));
        lblUser.setForeground(new Color(220, 235, 255));
        userPanel.add(lblUser);

        // Đổi mật khẩu button
        JButton btnDoiPass = UITheme.createButton("Đổi MK", new Color(40, 90, 160), Color.WHITE);
        btnDoiPass.setFont(UITheme.fontBold(11));
        btnDoiPass.setToolTipText("Thay đổi mật khẩu tài khoản");
        btnDoiPass.addActionListener(e -> new DoiMatKhauDialog(this, currentUser).setVisible(true));
        userPanel.add(btnDoiPass);

        // Đăng xuất button
        JButton btnLogout = UITheme.createButton("Đăng Xuất", new Color(190, 40, 40), Color.WHITE);
        btnLogout.setFont(UITheme.fontBold(11));
        btnLogout.setToolTipText("Đăng xuất khỏi hệ thống");
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        userPanel.add(btnLogout);

        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // === LEFT SIDEBAR WITH SMOOTH SCROLLING ===
        sideBar = new JPanel();
        sideBar.setBackground(UITheme.BG_SIDEBAR);
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(new EmptyBorder(8, 0, 8, 0));

        addSidebarSection("CHỨC NĂNG CHÍNH");

        btnDashboard = createNavBtn("  📊  Tổng Quan (Dashboard)", "DASHBOARD");
        btnSinhVien  = createNavBtn("  👥  Hồ Sơ Sinh Viên", "SINH_VIEN");
        btnKetQua    = createNavBtn("  📑  Bảng Điểm & Kết Quả", "KET_QUA");
        btnCanhBao   = createNavBtn("  ⚠️  Cảnh Báo Học Vụ", "CANH_BAO");
        btnNhatKy    = createNavBtn("  📝  Nhật Ký Tư Vấn CVHT", "NHAT_KY");
        btnThongBao  = createNavBtn("  🔔  Thông Báo & Tiering", "THONG_BAO");
        btnThongKe   = createNavBtn("  📈  Báo Cáo & Thống Kê", "THONG_KE");

        sideBar.add(btnDashboard);
        sideBar.add(btnSinhVien);
        sideBar.add(btnKetQua);
        sideBar.add(btnCanhBao);
        sideBar.add(btnNhatKy);
        sideBar.add(btnThongBao);
        sideBar.add(btnThongKe);

        // Admin-only panels
        boolean isAdminOrQL = "ADMIN".equals(currentUser.getVaiTro()) || "QUAN_LY".equals(currentUser.getVaiTro());
        if (isAdminOrQL) {
            addSidebarSection("QUẢN TRỊ HỆ THỐNG");
            btnLopHoc = createNavBtn("  🏫  Quản Lý Lớp & CVHT", "LOP_HOC");
            sideBar.add(btnLopHoc);
        }

        sideBar.add(Box.createVerticalGlue());

        JScrollPane sideBarScroll = new JScrollPane(sideBar);
        sideBarScroll.setPreferredSize(new Dimension(235, 700));
        sideBarScroll.setBorder(null);
        sideBarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sideBarScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(sideBarScroll, BorderLayout.WEST);

        // === CARD PANEL (CENTER) ===
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.BG_MAIN);

        cardPanel.add(new DashboardPanel(currentUser), "DASHBOARD");
        cardPanel.add(new QuanLySinhVienPanel(currentUser), "SINH_VIEN");
        cardPanel.add(new QuanLyKetQuaHocTapPanel(currentUser), "KET_QUA");
        cardPanel.add(new QuanLyCanhBaoPanel(currentUser), "CANH_BAO");
        cardPanel.add(new NhatKyTuVanPanel(currentUser), "NHAT_KY");
        cardPanel.add(new QuanLyThongBaoPanel(currentUser), "THONG_BAO");
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
        lbl.setForeground(new Color(130, 150, 175));
        lbl.setBorder(new EmptyBorder(12, 12, 4, 12));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(235, 42));
        btn.addActionListener(e -> switchCard(cardName, btn));
        return btn;
    }

    private void switchCard(String cardName, JButton btn) {
        activeBtn = btn;
        cardLayout.show(cardPanel, cardName);
        sideBar.repaint();
        // Update text color
        JButton[] allBtns = {btnDashboard, btnSinhVien, btnKetQua, btnCanhBao, btnNhatKy, btnThongBao, btnThongKe};
        for (JButton b : allBtns) {
            if (b == null) continue;
            b.setForeground(b == btn ? Color.WHITE : UITheme.TEXT_SIDEBAR);
        }
        if (btnLopHoc != null) {
            btnLopHoc.setForeground(btnLopHoc == btn ? Color.WHITE : UITheme.TEXT_SIDEBAR);
        }
    }
}