package com.qlcvht.view.panel;

import com.qlcvht.service.ThongKeService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BaoCaoThongKePanel extends JPanel {

    private final ThongKeService thongKeService = new ThongKeService();

    private JLabel lblTongSv;
    private JLabel lblBinhThuong;
    private JLabel lblMuc1;
    private JLabel lblMuc2;
    private JLabel lblBuocThoiHoc;
    private JLabel lblDaTuVan;

    public BaoCaoThongKePanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initHeader();
        initCards();
        loadStats();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitle = new JLabel("📊 BÁO CÁO THỐNG KÊ CẢNH BÁO HỌC VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(lblTitle);

        JButton btnRefresh = new JButton("🔄 Cập nhật số liệu");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> loadStats());
        headerPanel.add(btnRefresh);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initCards() {
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 15, 15));

        lblTongSv = createCard(gridPanel, "TỔNG SINH VIÊN", "0", new Color(40, 53, 147));
        lblBinhThuong = createCard(gridPanel, "HỌC BÌNH THƯỜNG", "0", new Color(40, 167, 69));
        lblMuc1 = createCard(gridPanel, "CẢNH BÁO MỨC 1", "0", new Color(230, 124, 11));
        lblMuc2 = createCard(gridPanel, "CẢNH BÁO MỨC 2", "0", new Color(217, 83, 79));
        lblBuocThoiHoc = createCard(gridPanel, "BUỘC THÔI HỌC", "0", new Color(180, 0, 0));
        lblDaTuVan = createCard(gridPanel, "ĐÃ ĐƯỢC TƯ VẤN", "0", new Color(24, 119, 242));

        add(gridPanel, BorderLayout.CENTER);
    }

    private JLabel createCard(JPanel parent, String title, String initialVal, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(240, 240, 240));

        JLabel lblValue = new JLabel(initialVal, SwingConstants.RIGHT);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.SOUTH);

        parent.add(card);
        return lblValue;
    }

    private void loadStats() {
        Map<String, Integer> stats = thongKeService.getThongKeTongQuan();
        lblTongSv.setText(String.valueOf(stats.getOrDefault("tong_sv", 0)));
        lblBinhThuong.setText(String.valueOf(stats.getOrDefault("sv_binh_thuong", 0)));
        lblMuc1.setText(String.valueOf(stats.getOrDefault("cb_muc_1", 0)));
        lblMuc2.setText(String.valueOf(stats.getOrDefault("cb_muc_2", 0)));
        lblBuocThoiHoc.setText(String.valueOf(stats.getOrDefault("buoc_thoi_hoc", 0)));
        lblDaTuVan.setText(String.valueOf(stats.getOrDefault("da_tu_van", 0)));
    }
}
