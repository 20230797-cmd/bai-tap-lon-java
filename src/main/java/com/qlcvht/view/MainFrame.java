package com.qlcvht.view;

import com.qlcvht.model.TaiKhoan;
import com.qlcvht.view.panel.BaoCaoThongKePanel;
import com.qlcvht.view.panel.NhatKyTuVanPanel;
import com.qlcvht.view.panel.QuanLyCanhBaoPanel;
import com.qlcvht.view.panel.QuanLySinhVienPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final TaiKhoan currentUser;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JButton btnNavSinhVien;
    private JButton btnNavCanhBao;
    private JButton btnNavNhatKy;
    private JButton btnNavThongKe;

    public MainFrame(TaiKhoan user) {
        this.currentUser = user;
        setTitle("Hệ thống Quản lý Cố vấn Học tập & Cảnh báo Học vụ - Bài tập lớn Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(24, 119, 242));
        topBar.setPreferredSize(new Dimension(1200, 55));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblLogo = new JLabel("🎓 HỆ THỐNG CỐ VẤN HỌC TẬP & CẢNH BÁO HỌC VỤ");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(Color.WHITE);
        topBar.add(lblLogo, BorderLayout.WEST);

        // Right side: User Info & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        String roleTitle = "ADMIN".equals(currentUser.getVaiTro()) ? "Quản trị viên" :
                           ("QUAN_LY".equals(currentUser.getVaiTro()) ? "Quản lý Khoa" : "Cố vấn Học tập");

        JLabel lblUser = new JLabel("Xin chào, " + currentUser.getHoTen() + " [" + roleTitle + "]");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(Color.WHITE);
        userPanel.add(lblUser);

        JButton btnLogout = new JButton("Đăng xuất 🚪");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        userPanel.add(btnLogout);

        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Left Navigation Sidebar
        JPanel sideBar = new JPanel(new GridLayout(8, 1, 0, 8));
        sideBar.setBackground(new Color(240, 242, 245));
        sideBar.setPreferredSize(new Dimension(230, 700));
        sideBar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        btnNavSinhVien = createNavButton("👨‍🎓 Quản lý Sinh viên", "SINH_VIEN");
        btnNavCanhBao = createNavButton("⚠️ Cảnh báo Học vụ", "CANH_BAO");
        btnNavNhatKy = createNavButton("📋 Nhật ký Tư vấn", "NHAT_KY");
        btnNavThongKe = createNavButton("📊 Báo cáo Thống kê", "THONG_KE");

        sideBar.add(btnNavSinhVien);
        sideBar.add(btnNavCanhBao);
        sideBar.add(btnNavNhatKy);
        sideBar.add(btnNavThongKe);

        add(sideBar, BorderLayout.WEST);

        // Main Center CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(new QuanLySinhVienPanel(currentUser), "SINH_VIEN");
        cardPanel.add(new QuanLyCanhBaoPanel(currentUser), "CANH_BAO");
        cardPanel.add(new NhatKyTuVanPanel(currentUser), "NHAT_KY");
        cardPanel.add(new BaoCaoThongKePanel(), "THONG_KE");

        add(cardPanel, BorderLayout.CENTER);

        // Mặc định chọn TAB Cảnh báo Học vụ
        switchCard("CANH_BAO", btnNavCanhBao);
    }

    private JButton createNavButton(String title, String cardName) {
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> switchCard(cardName, btn));
        return btn;
    }

    private void switchCard(String cardName, JButton activeBtn) {
        cardLayout.show(cardPanel, cardName);

        // Reset style
        JButton[] buttons = {btnNavSinhVien, btnNavCanhBao, btnNavNhatKy, btnNavThongKe};
        for (JButton b : buttons) {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.DARK_GRAY);
        }

        // Active style
        activeBtn.setBackground(new Color(24, 119, 242));
        activeBtn.setForeground(Color.WHITE);
    }
}
