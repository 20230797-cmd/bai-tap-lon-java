package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.DiemDanhDAO;
import com.qlcvht.dao.LichGiangDayDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.DiemDanh;
import com.qlcvht.model.LichGiangDay;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;

import com.qlcvht.view.dialog.LichHocDiemDanhSinhVienDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel Quản lý Điểm danh & Theo dõi Chuyên cần (Attendance & Engagement Tracking).
 */
public class QuanLyDiemDanhPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final DiemDanhDAO diemDanhDAO = new DiemDanhDAO();
    private final LichGiangDayDAO lichDAO = new LichGiangDayDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();

    private JComboBox<String> cbLichHoc;
    private JLabel lblGvInfo;
    private JTable tableDiemDanh;
    private DefaultTableModel modelDiemDanh;

    private List<LichGiangDay> dsLich;
    private List<SinhVien> dsSinhVien;
    private Map<String, DiemDanh> attendanceMap = new HashMap<>();

    private JLabel lblTyLeChuyenCan;
    private JLabel lblDungGio;
    private JLabel lblDiMuon;
    private JLabel lblVang;

    public QuanLyDiemDanhPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));
        initUI();
        loadLichHoc();
    }

    private void initUI() {
        // === HEADER & STATS ===
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("✅  QUẢN LÝ ĐIỂM DANH & THEO DÕI CHUYÊN CẦN");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Điểm danh buổi học / buổi tư vấn học vụ, tra cứu lịch học & điểm danh của từng sinh viên qua giảng viên");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Stats Cards Row
        JPanel statRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statRow.setOpaque(false);
        lblTyLeChuyenCan = addStatCard(statRow, "TỶ LỆ CHUYÊN CẦN", "100%", UITheme.PRIMARY);
        lblDungGio = addStatCard(statRow, "ĐÚNG GIỜ (ON TIME)", "0 SV", new Color(46, 125, 50));
        lblDiMuon = addStatCard(statRow, "ĐI MUỘN (LATE)", "0 SV", new Color(230, 119, 0));
        lblVang = addStatCard(statRow, "VẮNG MẶT (ABSENT)", "0 SV", new Color(198, 40, 40));
        topPanel.add(statRow, BorderLayout.CENTER);

        // Toolbar Container (2 rows: Row 1 Chọn lịch & GV, Row 2 Các nút thao tác)
        JPanel toolbarBox = new JPanel(new GridLayout(2, 1, 0, 6));
        toolbarBox.setBackground(UITheme.BG_WHITE);
        toolbarBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Toolbar Row 1: Session Selector & Lecturer Info
        JPanel toolbarRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbarRow1.setOpaque(false);
        toolbarRow1.add(new JLabel("Chọn Buổi Lịch:"));
        cbLichHoc = new JComboBox<>();
        cbLichHoc.setPreferredSize(new Dimension(380, 32));
        cbLichHoc.addActionListener(e -> loadDanhSachSinhVien());
        toolbarRow1.add(cbLichHoc);

        lblGvInfo = new JLabel("👨‍🏫 Giảng viên: ---");
        lblGvInfo.setFont(UITheme.fontBold(12));
        lblGvInfo.setForeground(UITheme.PRIMARY);
        lblGvInfo.setBorder(new EmptyBorder(0, 10, 0, 0));
        toolbarRow1.add(lblGvInfo);

        toolbarBox.add(toolbarRow1);

        // Toolbar Row 2: Action Buttons
        JPanel toolbarRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbarRow2.setOpaque(false);

        JButton btnXemLichSv = UITheme.createButton("🔍 Xem Lịch Học & Điểm Danh SV", new Color(25, 118, 210), Color.WHITE);
        btnXemLichSv.setToolTipText("Tra cứu toàn bộ lịch học và lịch sử điểm danh của sinh viên được chọn qua các giảng viên");
        btnXemLichSv.addActionListener(e -> xemLichHocSinhVien());
        toolbarRow2.add(btnXemLichSv);

        JButton btnTatCaCoMat = UITheme.createButton("⚡ Tất Cả Có Mặt", new Color(46, 125, 50), Color.WHITE);
        btnTatCaCoMat.addActionListener(e -> setAllStatus("ON_TIME"));
        toolbarRow2.add(btnTatCaCoMat);

        JButton btnTaoDuLieuAo = UITheme.createButton("🎲 Tạo Dữ Liệu Ảo Đi Học", new Color(123, 31, 162), Color.WHITE);
        btnTaoDuLieuAo.setToolTipText("Tự động sinh dữ liệu ảo điểm danh thực tế (đúng giờ, đi muộn, vắng) cho sinh viên đi học");
        btnTaoDuLieuAo.addActionListener(e -> taoDuLieuAoDiemDanh());
        toolbarRow2.add(btnTaoDuLieuAo);

        JButton btnLuu = UITheme.createButton("💾 Lưu Điểm Danh", UITheme.PRIMARY, Color.WHITE);
        btnLuu.addActionListener(e -> luuDiemDanh());
        toolbarRow2.add(btnLuu);

        JButton btnLocVang = UITheme.createButton("⚠️ Cảnh Báo Vắng Nhiều", new Color(198, 40, 40), Color.WHITE);
        btnLocVang.addActionListener(e -> canhBaoVangNhieu());
        toolbarRow2.add(btnLocVang);

        toolbarBox.add(toolbarRow2);

        topPanel.add(toolbarBox, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // === TABLE ===
        String[] headers = {"Mã SV", "Họ và Tên", "Lớp", "Giới Tính", "Trạng Thái Điểm Danh", "Ghi Chú Đánh Giá"};
        modelDiemDanh = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4 || col == 5; // Cho phép sửa trạng thái và ghi chú
            }
        };

        tableDiemDanh = new JTable(modelDiemDanh);
        UITheme.styleTable(tableDiemDanh);
        tableDiemDanh.setRowHeight(34);

        tableDiemDanh.getColumnModel().getColumn(0).setPreferredWidth(90);
        tableDiemDanh.getColumnModel().getColumn(1).setPreferredWidth(180);
        tableDiemDanh.getColumnModel().getColumn(2).setPreferredWidth(70);
        tableDiemDanh.getColumnModel().getColumn(3).setPreferredWidth(70);
        tableDiemDanh.getColumnModel().getColumn(4).setPreferredWidth(170);
        tableDiemDanh.getColumnModel().getColumn(5).setPreferredWidth(250);

        // Double click mở Lịch Học & Điểm Danh Sinh Viên
        tableDiemDanh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tableDiemDanh.getSelectedRow() >= 0) {
                    xemLichHocSinhVien();
                }
            }
        });

        // Editor ComboBox cho cột Trạng Thái
        JComboBox<String> cbStatusEditor = new JComboBox<>(new String[]{
            "ON_TIME (Có mặt đúng giờ)",
            "LATE (Đi muộn)",
            "EXCUSED (Vắng có phép)",
            "ABSENT (Vắng không phép)"
        });
        tableDiemDanh.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cbStatusEditor));

        // Renderer cho Trạng thái
        tableDiemDanh.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String v = value != null ? value.toString() : "";
                if (v.contains("ON_TIME") || v.contains("đúng giờ")) {
                    lbl.setForeground(new Color(46, 125, 50));
                    lbl.setText("✔ Có mặt đúng giờ");
                } else if (v.contains("LATE") || v.contains("muộn")) {
                    lbl.setForeground(new Color(230, 119, 0));
                    lbl.setText("⏳ Đi muộn");
                } else if (v.contains("EXCUSED") || v.contains("có phép")) {
                    lbl.setForeground(new Color(25, 118, 210));
                    lbl.setText("✉ Vắng có phép");
                } else if (v.contains("ABSENT") || v.contains("không phép")) {
                    lbl.setForeground(new Color(198, 40, 40));
                    lbl.setText("✖ Vắng không phép");
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tableDiemDanh);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel addStatCard(JPanel parent, String title, String val, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UITheme.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 16, 10, 16)
        ));
        JLabel lblT = new JLabel(title);
        lblT.setFont(UITheme.fontBold(11));
        lblT.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblV = new JLabel(val);
        lblV.setFont(UITheme.fontBold(20));
        lblV.setForeground(color);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        parent.add(card);
        return lblV;
    }

    public void loadLichHoc() {
        cbLichHoc.removeAllItems();
        dsLich = lichDAO.getAll();
        for (LichGiangDay l : dsLich) {
            String item = "[" + l.getId() + "] " + l.getNgay() + " - " + l.getTieuDe() + " (" + l.getMaLop() + ")";
            cbLichHoc.addItem(item);
        }
        if (!dsLich.isEmpty()) {
            cbLichHoc.setSelectedIndex(0);
        }
    }

    private void loadDanhSachSinhVien() {
        int idx = cbLichHoc.getSelectedIndex();
        if (idx < 0 || dsLich == null || idx >= dsLich.size()) {
            lblGvInfo.setText("👨‍🏫 Giảng viên: ---");
            return;
        }

        LichGiangDay lich = dsLich.get(idx);
        String maLop = lich.getMaLop();
        String gv = lich.getTenCvht() != null && !lich.getTenCvht().isEmpty() ? lich.getTenCvht() : (lich.getMaCvht() != null ? lich.getMaCvht() : "---");
        String phong = lich.getDiaDiem() != null && !lich.getDiaDiem().isEmpty() ? lich.getDiaDiem() : "---";
        String hinhThuc = "ONLINE".equals(lich.getHinhThuc()) ? "🌐 Online" : "🏫 Trực tiếp";
        lblGvInfo.setText("👨‍🏫 GV: " + gv + "  |  " + hinhThuc + "  |  Địa điểm: " + phong + "  |  " + (lich.getGioBatDau() != null ? lich.getGioBatDau() : "") + " - " + (lich.getGioKetThuc() != null ? lich.getGioKetThuc() : ""));

        dsSinhVien = sinhVienDAO.getSinhVienByLop(maLop);
        List<DiemDanh> daDiemDanh = diemDanhDAO.getBySession(lich.getId());
        attendanceMap.clear();
        for (DiemDanh d : daDiemDanh) {
            attendanceMap.put(d.getMaSv(), d);
        }

        modelDiemDanh.setRowCount(0);
        int cntOnTime = 0;
        int cntLate = 0;
        int cntAbsent = 0;

        for (SinhVien sv : dsSinhVien) {
            DiemDanh dd = attendanceMap.get(sv.getMaSv());
            String trangThai = dd != null ? dd.getTrangThai() : "ON_TIME";
            String ghiChu = dd != null ? dd.getGhiChu() : "";

            if ("ON_TIME".equals(trangThai)) cntOnTime++;
            else if ("LATE".equals(trangThai)) cntLate++;
            else if ("ABSENT".equals(trangThai) || "EXCUSED".equals(trangThai)) cntAbsent++;

            modelDiemDanh.addRow(new Object[]{
                sv.getMaSv(),
                sv.getHoTen(),
                sv.getMaLop(),
                sv.getGioiTinh(),
                trangThai,
                ghiChu
            });
        }

        lblDungGio.setText(cntOnTime + " SV");
        lblDiMuon.setText(cntLate + " SV");
        lblVang.setText(cntAbsent + " SV");
        if (!dsSinhVien.isEmpty()) {
            double rate = (cntOnTime * 100.0) / dsSinhVien.size();
            lblTyLeChuyenCan.setText(String.format("%.1f%%", rate));
        }
    }

    private void xemLichHocSinhVien() {
        int row = tableDiemDanh.getSelectedRow();
        if (row < 0 || dsSinhVien == null || row >= dsSinhVien.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên trong bảng để xem lịch học & điểm danh!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SinhVien sv = dsSinhVien.get(row);
        LichHocDiemDanhSinhVienDialog dialog = new LichHocDiemDanhSinhVienDialog(SwingUtilities.getWindowAncestor(this), sv);
        dialog.setVisible(true);
        loadDanhSachSinhVien(); // reload sau khi đóng dialog phòng khi có sửa
    }

    private void taoDuLieuAoDiemDanh() {
        String[] options = {"Chỉ buổi học hiện tại", "Tất cả các buổi học (Toàn bộ lớp)", "Hủy bỏ"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Bạn muốn tạo dữ liệu ảo điểm danh thực tế (Có mặt đúng giờ, Đi muộn, Vắng có phép/không phép) cho sinh viên đi học ở phạm vi nào?",
            "🎲 Tạo Dữ Liệu Ảo Đi Học",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) { // Buổi hiện tại
            int idx = cbLichHoc.getSelectedIndex();
            if (idx < 0 || dsLich == null || idx >= dsLich.size()) return;
            LichGiangDay lich = dsLich.get(idx);
            int count = diemDanhDAO.generateMockAttendance(lich.getId());
            JOptionPane.showMessageDialog(this, "Đã tạo thành công " + count + " bản ghi điểm danh ảo cho buổi học #" + lich.getId() + " (" + lich.getTieuDe() + ")!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDanhSachSinhVien();
        } else if (choice == 1) { // Tất cả các buổi học
            int count = diemDanhDAO.generateMockAttendance(null);
            JOptionPane.showMessageDialog(this, "Đã tạo thành công " + count + " bản ghi điểm danh ảo cho TẤT CẢ các buổi học trong hệ thống!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDanhSachSinhVien();
        }
    }

    private void setAllStatus(String status) {
        for (int i = 0; i < modelDiemDanh.getRowCount(); i++) {
            modelDiemDanh.setValueAt(status, i, 4);
        }
        recalculateStats();
    }

    private void recalculateStats() {
        int cntOnTime = 0, cntLate = 0, cntAbsent = 0;
        for (int i = 0; i < modelDiemDanh.getRowCount(); i++) {
            String tt = (String) modelDiemDanh.getValueAt(i, 4);
            if (tt.contains("ON_TIME")) cntOnTime++;
            else if (tt.contains("LATE")) cntLate++;
            else cntAbsent++;
        }
        lblDungGio.setText(cntOnTime + " SV");
        lblDiMuon.setText(cntLate + " SV");
        lblVang.setText(cntAbsent + " SV");
        if (modelDiemDanh.getRowCount() > 0) {
            double rate = (cntOnTime * 100.0) / modelDiemDanh.getRowCount();
            lblTyLeChuyenCan.setText(String.format("%.1f%%", rate));
        }
    }

    private void luuDiemDanh() {
        int idx = cbLichHoc.getSelectedIndex();
        if (idx < 0 || dsLich == null || idx >= dsLich.size()) return;
        LichGiangDay lich = dsLich.get(idx);

        int savedCount = 0;
        for (int i = 0; i < modelDiemDanh.getRowCount(); i++) {
            String maSv = (String) modelDiemDanh.getValueAt(i, 0);
            String rawStatus = (String) modelDiemDanh.getValueAt(i, 4);
            String status = "ON_TIME";
            if (rawStatus != null) {
                if (rawStatus.contains("LATE")) status = "LATE";
                else if (rawStatus.contains("EXCUSED")) status = "EXCUSED";
                else if (rawStatus.contains("ABSENT")) status = "ABSENT";
            }
            String ghiChu = (String) modelDiemDanh.getValueAt(i, 5);

            if (diemDanhDAO.saveOrUpdateAttendance(lich.getId(), maSv, lich.getNgay(), status, ghiChu)) {
                savedCount++;
            }
        }

        JOptionPane.showMessageDialog(this, "Đã lưu thành công dữ liệu điểm danh cho " + savedCount + " sinh viên!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadDanhSachSinhVien();
    }

    private void canhBaoVangNhieu() {
        int idx = cbLichHoc.getSelectedIndex();
        if (idx < 0 || dsLich == null || idx >= dsLich.size()) return;
        LichGiangDay lich = dsLich.get(idx);

        List<String> listVang = new ArrayList<>();
        for (int i = 0; i < modelDiemDanh.getRowCount(); i++) {
            String rawStatus = (String) modelDiemDanh.getValueAt(i, 4);
            if (rawStatus != null && (rawStatus.contains("ABSENT") || rawStatus.contains("EXCUSED"))) {
                listVang.add(modelDiemDanh.getValueAt(i, 0) + " - " + modelDiemDanh.getValueAt(i, 1));
            }
        }

        if (listVang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có sinh viên nào vắng mặt trong buổi học này!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Danh sách sinh viên vắng mặt cần chú ý theo dõi:\n\n");
            for (String sv : listVang) {
                sb.append("• ").append(sv).append("\n");
            }
            sb.append("\nKhuyến nghị: Cố vấn học tập liên hệ trực tiếp hoặc gửi thông báo nhắc nhở!");
            JOptionPane.showMessageDialog(this, sb.toString(), "Cảnh Báo Vắng Mặt", JOptionPane.WARNING_MESSAGE);
        }
    }
}
