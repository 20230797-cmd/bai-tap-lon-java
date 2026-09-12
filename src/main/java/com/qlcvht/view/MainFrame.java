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
    private JButton btnTaiKhoan;
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

        setTitle("Hệ thống Quản lý Cố vấn Học tập & Cảnh báo Học vụ - Đại học Công nghệ Đông Á (EAUT)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildTopBar();
        buildSidebar();
        buildCardPanel();

        // Default open tab
        if ("ADMIN".equals(role)) {
            switchCard("TAI_KHOAN", btnTaiKhoan != null ? btnTaiKhoan : btnDashboard);
        } else {
            switchCard("DASHBOARD", btnDashboard);
        }
    }

    private void buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(UITheme.BG_HEADER);
        topBar.setPreferredSize(new Dimension(0, 58));
        topBar.setBorder(new EmptyBorder(0, 18, 0, 16));

        // Brand on Left
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("🏛️");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        brandPanel.add(lblLogo);

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);

        JLabel lblUniv = new JLabel("ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)");
        lblUniv.setFont(UITheme.fontBold(14));
        lblUniv.setForeground(Color.WHITE);

        JLabel lblSystem = new JLabel("HỆ THỐNG CỐ VẤN HỌC TẬP & CẢNH BÁO HỌC VỤ");
        lblSystem.setFont(UITheme.fontPlain(11));
        lblSystem.setForeground(new Color(147, 197, 253));

        brandText.add(lblUniv);
        brandText.add(lblSystem);
        brandPanel.add(brandText);

        topBar.add(brandPanel, BorderLayout.WEST);

        // User Controls on Right
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

        String roleTitle = switch (role) {
            case "ADMIN"   -> "Quản trị viên";
            case "QUAN_LY" -> "Quản lý Đào tạo";
            case "CO_VAN"  -> "Cố vấn Học tập";
            default        -> role;
        };

        JLabel lblUser = new JLabel("👤 " + roleTitle + ": " + (currentUser != null ? currentUser.getHoTen() : "User"));
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

        if ("ADMIN".equals(role)) {
            // ADMIN: Chỉ quản trị người dùng & tài khoản, không can thiệp nghiệp vụ học tập
            addSidebarSection("QUẢN TRỊ HỆ THỐNG");
            btnTaiKhoan = createNavBtn("  Quản Lý Tài Khoản", "TAI_KHOAN");
            sideBar.add(btnTaiKhoan);
        } else if ("QUAN_LY".equals(role)) {
            // QUẢN LÝ (Ban Đào tạo / Trưởng khoa): Giám sát toàn bộ học vụ, lớp học, cảnh báo, thống kê
            addSidebarSection("TỔNG QUAN & LỊCH TRÌNH");
            btnDashboard    = createNavBtn("  Tổng Quan (Dashboard)", "DASHBOARD");
            btnLichGiangDay = createNavBtn("  Lịch Giảng Dạy & Họp", "LICH_GIANG_DAY");
            sideBar.add(btnDashboard);
            sideBar.add(btnLichGiangDay);

            addSidebarSection("QUẢN LÝ HỌC VỤ & SINH VIÊN");
            btnSinhVien = createNavBtn("  Hồ Sơ Toàn Bộ Sinh Viên", "SINH_VIEN");
            btnKetQua   = createNavBtn("  Bảng Điểm & Kết Quả HT", "KET_QUA");
            sideBar.add(btnSinhVien);
            sideBar.add(btnKetQua);

            addSidebarSection("CỐ VẤN & CẢNH BÁO HỌC VỤ");
            btnCanhBao  = createNavBtn("  Cảnh Báo Học Vụ", "CANH_BAO");
            btnThongBao = createNavBtn("  Thông Báo Học Vụ", "THONG_BAO");
            sideBar.add(btnCanhBao);
            sideBar.add(btnThongBao);

            addSidebarSection("BÁO CÁO & PHÂN CÔNG");
            btnThongKe = createNavBtn("  Báo Cáo & Thống Kê", "THONG_KE");
            btnLopHoc  = createNavBtn("  Quản Lý Lớp & CVHT", "LOP_HOC");
            sideBar.add(btnThongKe);
            sideBar.add(btnLopHoc);
        } else {
            // CỐ VẤN HỌC TẬP (CO_VAN): Trực tiếp quản lý sinh viên lớp, tư vấn, điểm danh, cảnh báo
            addSidebarSection("TỔNG QUAN & LỊCH TRÌNH");
            btnDashboard    = createNavBtn("  Tổng Quan (Dashboard)", "DASHBOARD");
            btnLichGiangDay = createNavBtn("  Lịch Giảng Dạy & CVHT", "LICH_GIANG_DAY");
            sideBar.add(btnDashboard);
            sideBar.add(btnLichGiangDay);

            addSidebarSection("QUẢN LÝ HỌC VỤ & SINH VIÊN");
            btnSinhVien = createNavBtn("  Hồ Sơ Sinh Viên Lớp", "SINH_VIEN");
            btnKetQua   = createNavBtn("  Bảng Điểm & Kết Quả HT", "KET_QUA");
            btnDiemDanh = createNavBtn("  Điểm Danh & Chuyên Cần", "DIEM_DANH");
            sideBar.add(btnSinhVien);
            sideBar.add(btnKetQua);
            sideBar.add(btnDiemDanh);

            addSidebarSection("CỐ VẤN & CẢNH BÁO HỌC VỤ");
            btnCanhBao  = createNavBtn("  Cảnh Báo Học Vụ", "CANH_BAO");
            btnNhatKy   = createNavBtn("  Nhật Ký Tư Vấn CVHT", "NHAT_KY");
            btnThongBao = createNavBtn("  Thông Báo & Chat Tư Vấn", "THONG_BAO");
            sideBar.add(btnCanhBao);
            sideBar.add(btnNhatKy);
            sideBar.add(btnThongBao);

            addSidebarSection("BÁO CÁO & THỐNG KÊ");
            btnThongKe = createNavBtn("  Báo Cáo & Thống Kê", "THONG_KE");
            sideBar.add(btnThongKe);
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

        if ("ADMIN".equals(role)) {
            // Admin Panels: Chỉ quản lý tài khoản
            cardPanel.add(new QuanLyTaiKhoanPanel(currentUser), "TAI_KHOAN");
        } else if ("QUAN_LY".equals(role)) {
            // Quan Ly Panels
            cardPanel.add(new DashboardPanel(currentUser),          "DASHBOARD");
            cardPanel.add(new LichGiangDayPanel(currentUser),       "LICH_GIANG_DAY");
            cardPanel.add(new QuanLySinhVienPanel(currentUser),     "SINH_VIEN");
            cardPanel.add(new QuanLyKetQuaHocTapPanel(currentUser), "KET_QUA");
            cardPanel.add(new QuanLyCanhBaoPanel(currentUser),      "CANH_BAO");
            cardPanel.add(new QuanLyThongBaoPanel(currentUser),     "THONG_BAO");
            cardPanel.add(new BaoCaoThongKePanel(),                 "THONG_KE");
            cardPanel.add(new QuanLyLopHocPanel(),                  "LOP_HOC");
        } else {
            // Co Van Panels
            cardPanel.add(new DashboardPanel(currentUser),          "DASHBOARD");
            cardPanel.add(new LichGiangDayPanel(currentUser),       "LICH_GIANG_DAY");
            cardPanel.add(new QuanLySinhVienPanel(currentUser),     "SINH_VIEN");
            cardPanel.add(new QuanLyKetQuaHocTapPanel(currentUser), "KET_QUA");
            cardPanel.add(new QuanLyDiemDanhPanel(currentUser),     "DIEM_DANH");
            cardPanel.add(new QuanLyCanhBaoPanel(currentUser),      "CANH_BAO");
            cardPanel.add(new NhatKyTuVanPanel(currentUser),        "NHAT_KY");
            cardPanel.add(new QuanLyThongBaoPanel(currentUser),     "THONG_BAO");
            cardPanel.add(new BaoCaoThongKePanel(),                 "THONG_KE");
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
            btnDashboard, btnTaiKhoan, btnLichGiangDay, btnSinhVien, btnKetQua,
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
