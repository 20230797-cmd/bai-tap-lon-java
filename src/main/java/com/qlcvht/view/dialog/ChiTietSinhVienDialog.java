package com.qlcvht.view.dialog;

import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.SinhVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ChiTietSinhVienDialog extends JDialog {

    private final SinhVien sinhVien;

    public ChiTietSinhVienDialog(Frame parent, SinhVien sv) {
        super(parent, "Chi tiết Hồ sơ Sinh viên: " + sv.getHoTen(), true);
        this.sinhVien = sv;

        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(700, 550);

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 53, 147));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblName = new JLabel(sinhVien.getHoTen() + " (" + sinhVien.getMaSv() + ")");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Lớp: " + (sinhVien.getTenLop() != null ? sinhVien.getTenLop() : sinhVien.getMaLop()) + " | Trạng thái: " + sinhVien.getTrangThaiHienThi());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(220, 225, 252));

        headerPanel.add(lblName, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for Academic Results & Advice History
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Kết quả học tập các học kỳ
        tabbedPane.addTab("📈 Kết quả Học tập (GPA)", createKetQuaHocTapPanel());

        // Tab 2: Lịch sử Tư vấn Cố vấn Học tập
        tabbedPane.addTab("📝 Lịch sử Tư vấn CVHT", createLichSuTuVanPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Close Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createKetQuaHocTapPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Học kỳ", "Năm học", "GPA Học kỳ", "GPA Tích lũy", "Số tín chỉ nợ"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<KetQuaHocTap> listKQ = new KetQuaHocTapDAO().getKetQuaBySinhVien(sinhVien.getMaSv());
        for (KetQuaHocTap kq : listKQ) {
            model.addRow(new Object[]{
                "Học kỳ " + kq.getHocKy(),
                kq.getNamHoc(),
                String.format("%.2f", kq.getGpaHocKy()),
                String.format("%.2f", kq.getGpaTichLuy()),
                kq.getSoTinChiNo()
            });
        }

        JTable tblKQ = new JTable(model);
        tblKQ.setRowHeight(26);
        tblKQ.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(new JScrollPane(tblKQ), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLichSuTuVanPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Ngày tư vấn", "Hình thức", "Cố vấn học tập", "Nội dung trao đổi", "Cam kết sinh viên"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<NhatKyTuVan> listNK = new NhatKyTuVanDAO().getNhatKyBySinhVien(sinhVien.getMaSv());
        for (NhatKyTuVan nk : listNK) {
            model.addRow(new Object[]{
                nk.getNgayTuVan(),
                nk.getHinhThuc(),
                nk.getHoTenCvht(),
                nk.getNoiDung(),
                nk.getCamKetSinhVien()
            });
        }

        JTable tblNK = new JTable(model);
        tblNK.setRowHeight(28);
        tblNK.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(new JScrollPane(tblNK), BorderLayout.CENTER);

        return panel;
    }
}
