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

        if ("SINH_VIEN".equalsIgnoreCase(this.role)) {
            SwingUtilities.invokeLater(() -> {
                dispose();
                new StudentMainFrame(currentUser).setVisible(true);
            });
            return;
        }

        setTitle("Hệ thống Quản lý Cố vấn Học tập & Cảnh báo Học vụ");
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

        JLabel lblTitle = new JLabel("HỆ THỐNG CỐ VẤN HỌC TẬP & CẢNH BÁO HỌC VỤ");
        lblTitle.setFont(UITheme.fontBold(15));
        lblTitle.setForeground(Color.WHITE);
        topBar.add(lblTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        userPanel.setOpaque(false);

        String dbType = DatabaseConnection.isUsingSQLite() ? "● SQLite (Offline)" : "● MySQL (Online)";
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
            case "ADMIN"   -> "Quản trị viên";
            case "QUAN_LY" -> "Quản lý Khoa";
            case "CO_VAN"  -> "Cố vấn Học tập";
            default        -> role;
        };

        JLabel lblUser = new JLabel(roleTitle + " – " + (currentUser != null ? currentUser.getHoTen() : "User"));
        lblUser.setFont(UITheme.fontPlain(13));
        lblUser.setForeground(new Color(226, 232, 240));
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

        addSidebarSection("TỔNG QUAN & LỊCH TRÌNH");
        btnDashboard    = createNavBtn("  Tổng Quan (Dashboard)", "DASHBOARD");
        btnLichGiangDay = createNavBtn("  Lịch Giảng Dạy & CVHT", "LICH_GIANG_DAY");
        sideBar.add(btnDashboard);
        sideBar.add(btnLichGiangDay);

        addSidebarSection("QUẢN LÝ HỌC VỤ & SINH VIÊN");
        btnSinhVien = createNavBtn("  Hồ Sơ Sinh Viên", "SINH_VIEN");
        btnKetQua   = createNavBtn("  Bảng Điểm & Kết Quả HT", "KET_QUA");
        btnDiemDanh = createNavBtn("  Điểm Danh & Chuyên Cần", "DIEM_DANH");
        sideBar.add(btnSinhVien);
        sideBar.add(btnKetQua);
        if (!"QUAN_LY".equals(role)) {
            sideBar.add(btnDiemDanh);
        }

        addSidebarSection("CỐ VẤN & CẢNH BÁO HỌC VỤ");
        btnCanhBao  = createNavBtn("  Cảnh Báo Học Vụ", "CANH_BAO");
        btnNhatKy   = createNavBtn("  Nhật Ký Tư Vấn CVHT", "NHAT_KY");
        btnThongBao = createNavBtn("  Thông Báo Sinh Viên", "THONG_BAO");
        sideBar.add(btnCanhBao);
        sideBar.add(btnNhatKy);
        sideBar.add(btnThongBao);

        addSidebarSection("BÁO CÁO & THỐNG KÊ");
        btnThongKe = createNavBtn("  Báo Cáo & Thống Kê", "THONG_KE");
        sideBar.add(btnThongKe);

        boolean isAdminOrQL = "ADMIN".equals(role) || "QUAN_LY".equals(role);
        if (isAdminOrQL) {
            addSidebarSection("QUẢN TRỊ HỆ THỐNG");
            btnLopHoc = createNavBtn("  Quản Lý Lớp & CVHT", "LOP_HOC");
            sideBar.add(btnLopHoc);
        }

        sideBar.add(Box.createVerticalGlue());

        JScrollPane sideBarScroll = new JScrollPane(sideBar);
        sideBarScroll.setPreferredSize(new Dimension(240, 720));
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
        btn.setPreferredSize(new Dimension(240, 40));
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
