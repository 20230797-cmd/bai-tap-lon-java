package com.qlcvht.view.dialog;

import com.qlcvht.dao.DiemDanhDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.DiemDanh;
import com.qlcvht.model.SinhVien;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dialog Tra Cứu Lịch Học & Điểm Danh Sinh Viên Thông Qua Giảng Viên Khi Điểm Danh Trên Lớp.
 */
public class LichHocDiemDanhSinhVienDialog extends JDialog {

    private final SinhVien sinhVien;
    private final DiemDanhDAO diemDanhDAO = new DiemDanhDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();

    private JTable tableLichHoc;
    private DefaultTableModel tableModel;
    private List<DiemDanh> fullList = new ArrayList<>();
    private List<DiemDanh> displayedList = new ArrayList<>();

    private JComboBox<String> cbFilterGiangVien;
    private JComboBox<String> cbFilterTrangThai;

    private JLabel lblTongBuoi;
    private JLabel lblTyLeChuyenCan;
    private JLabel lblDungGio;
    private JLabel lblDiMuon;
    private JLabel lblVang;

    public LichHocDiemDanhSinhVienDialog(Window parent, SinhVien sv) {
        super(parent, "Lịch Học & Điểm Danh Chuyên Cần: " + sv.getHoTen() + " (" + sv.getMaSv() + ")", ModalityType.APPLICATION_MODAL);
        this.sinhVien = sv;
        setSize(1100, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(parent);
        initUI();
        loadData();
    }

    public LichHocDiemDanhSinhVienDialog(Window parent, String maSv) {
        this(parent, new SinhVienDAO().getSinhVienById(maSv) != null ? new SinhVienDAO().getSinhVienById(maSv) : new SinhVien(maSv, "Sinh viên " + maSv, null, "Nam", "", "", "", "DANG_HOC"));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        getContentPane().setBackground(UITheme.BG_MAIN);

        // === TOP BANNER & STATS ===
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(UITheme.BG_MAIN);

        // Header Panel with Gradient/Theme
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel("📅  LỊCH HỌC & THEO DÕI ĐIỂM DANH SINH VIÊN");
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Sinh viên: " + sinhVien.getHoTen() + "  |  MSSV: " + sinhVien.getMaSv()
            + "  |  Lớp: " + (sinhVien.getTenLop() != null ? sinhVien.getTenLop() : sinhVien.getMaLop())
            + "  |  Trạng thái học vụ: " + UITheme.formatTrangThaiSinhVien(sinhVien.getTrangThai()));
        lblSub.setFont(UITheme.fontPlain(13));
        lblSub.setForeground(new Color(215, 235, 255));

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        topPanel.add(header);

        // Stats KPI Row
        JPanel statRow = new JPanel(new GridLayout(1, 5, 10, 0));
        statRow.setBackground(UITheme.BG_MAIN);
        statRow.setBorder(new EmptyBorder(10, 16, 6, 16));

        lblTongBuoi      = addStatCard(statRow, "TỔNG BUỔI HỌC", "0", UITheme.PRIMARY);
        lblTyLeChuyenCan = addStatCard(statRow, "TỶ LỆ ĐI HỌC", "100%", new Color(46, 125, 50));
        lblDungGio       = addStatCard(statRow, "ĐÚNG GIỜ (ON TIME)", "0", new Color(46, 125, 50));
        lblDiMuon        = addStatCard(statRow, "ĐI MUỘN (LATE)", "0", new Color(230, 119, 0));
        lblVang          = addStatCard(statRow, "VẮNG MẶT (ABSENT)", "0", new Color(198, 40, 40));

        topPanel.add(statRow);

        // Filter Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        toolbar.setBackground(UITheme.BG_WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, UITheme.BORDER_LIGHT),
            new EmptyBorder(4, 16, 4, 16)
        ));

        toolbar.add(new JLabel("Giảng viên / CVHT:"));
        cbFilterGiangVien = new JComboBox<>();
        cbFilterGiangVien.setPreferredSize(new Dimension(220, 32));
        cbFilterGiangVien.addItem("--- Tất cả Giảng viên ---");
        cbFilterGiangVien.addActionListener(e -> applyFilter());
        toolbar.add(cbFilterGiangVien);

        toolbar.add(new JLabel("Trạng thái:"));
        cbFilterTrangThai = new JComboBox<>(new String[]{
            "--- Tất cả trạng thái ---",
            "ON_TIME (Có mặt đúng giờ)",
            "LATE (Đi muộn)",
            "EXCUSED (Vắng có phép)",
            "ABSENT (Vắng không phép)",
            "CHUA_DIEM_DANH (Chưa điểm danh)"
        });
        cbFilterTrangThai.setPreferredSize(new Dimension(210, 32));
        cbFilterTrangThai.addActionListener(e -> applyFilter());
        toolbar.add(cbFilterTrangThai);

        JButton btnCapNhat = UITheme.createButton("✏ Sửa Điểm Danh Buổi Này", UITheme.PRIMARY, Color.WHITE);
        btnCapNhat.addActionListener(e -> capNhatDiemDanhBuoiChon());
        toolbar.add(btnCapNhat);

        JButton btnRefresh = UITheme.createButton("🔄 Làm Mới", new Color(70, 90, 120), Color.WHITE);
        btnRefresh.addActionListener(e -> loadData());
        toolbar.add(btnRefresh);

        topPanel.add(toolbar);
        add(topPanel, BorderLayout.NORTH);

        // === TABLE ===
        String[] headers = {
            "ID Buổi", "Ngày Học", "Thời Gian", "Môn Học / Buổi Học", "Giảng Viên / CVHT",
            "Địa Điểm / Link", "Hình Thức", "Loại Buổi", "Trạng Thái Điểm Danh", "Ghi Chú Đánh Giá Của GV"
        };

        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableLichHoc = new JTable(tableModel);
        UITheme.styleTable(tableLichHoc);
        tableLichHoc.setRowHeight(34);

        tableLichHoc.getColumnModel().getColumn(0).setMaxWidth(60);
        tableLichHoc.getColumnModel().getColumn(1).setPreferredWidth(95);
        tableLichHoc.getColumnModel().getColumn(2).setPreferredWidth(95);
        tableLichHoc.getColumnModel().getColumn(3).setPreferredWidth(210);
        tableLichHoc.getColumnModel().getColumn(4).setPreferredWidth(170);
        tableLichHoc.getColumnModel().getColumn(5).setPreferredWidth(140);
        tableLichHoc.getColumnModel().getColumn(6).setPreferredWidth(85);
        tableLichHoc.getColumnModel().getColumn(7).setPreferredWidth(130);
        tableLichHoc.getColumnModel().getColumn(8).setPreferredWidth(160);
        tableLichHoc.getColumnModel().getColumn(9).setPreferredWidth(220);

        // Custom Renderer cho Trạng thái điểm danh
        tableLichHoc.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String v = value != null ? value.toString() : "";
                if (v.contains("ON_TIME") || v.contains("đúng giờ")) {
                    lbl.setForeground(new Color(46, 125, 50));
                    lbl.setText("✔ Đúng giờ");
                } else if (v.contains("LATE") || v.contains("muộn")) {
                    lbl.setForeground(new Color(230, 119, 0));
                    lbl.setText("⏳ Đi muộn");
                } else if (v.contains("EXCUSED") || v.contains("có phép")) {
                    lbl.setForeground(new Color(25, 118, 210));
                    lbl.setText("✉ Có phép");
                } else if (v.contains("ABSENT") || v.contains("không phép")) {
                    lbl.setForeground(new Color(198, 40, 40));
                    lbl.setText("✖ Vắng mặt");
                } else {
                    lbl.setForeground(new Color(120, 130, 140));
                    lbl.setText("⚪ Chưa điểm danh");
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tableLichHoc);
        scroll.setBorder(new EmptyBorder(0, 16, 0, 16));
        add(scroll, BorderLayout.CENTER);

        // === BOTTOM ACTIONS ===
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        bottom.setBackground(UITheme.BG_WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnClose = UITheme.createButton("Đóng Lịch Học", UITheme.PRIMARY, Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);
    }

    private JLabel addStatCard(JPanel parent, String title, String val, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 3));
        card.setBackground(UITheme.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JLabel lblT = new JLabel(title);
        lblT.setFont(UITheme.fontBold(10));
        lblT.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblV = new JLabel(val);
        lblV.setFont(UITheme.fontBold(18));
        lblV.setForeground(color);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        parent.add(card);
        return lblV;
    }

    public void loadData() {
        fullList = diemDanhDAO.getLichHocVaDiemDanhBySinhVien(sinhVien.getMaSv());

        // Cập nhật danh sách Giảng viên trong ComboBox Filter
        Set<String> setGv = new HashSet<>();
        for (DiemDanh d : fullList) {
            String gv = d.getTenCvht() != null && !d.getTenCvht().isEmpty() ? d.getTenCvht() : d.getMaCvht();
            if (gv != null && !gv.isEmpty()) {
                setGv.add(gv);
            }
        }
        cbFilterGiangVien.removeAllItems();
        cbFilterGiangVien.addItem("--- Tất cả Giảng viên ---");
        for (String gv : setGv) {
            cbFilterGiangVien.addItem(gv);
        }

        applyFilter();
    }

    private void applyFilter() {
        String selectedGv = (String) cbFilterGiangVien.getSelectedItem();
        int ttIdx = cbFilterTrangThai.getSelectedIndex();
        String selectedTt = null;
        if (ttIdx == 1) selectedTt = "ON_TIME";
        else if (ttIdx == 2) selectedTt = "LATE";
        else if (ttIdx == 3) selectedTt = "EXCUSED";
        else if (ttIdx == 4) selectedTt = "ABSENT";
        else if (ttIdx == 5) selectedTt = "CHUA_DIEM_DANH";

        displayedList.clear();
        int cntOnTime = 0;
        int cntLate = 0;
        int cntAbsent = 0;
        int cntExcused = 0;
        int cntTotal = 0;

        for (DiemDanh d : fullList) {
            String gv = d.getTenCvht() != null && !d.getTenCvht().isEmpty() ? d.getTenCvht() : d.getMaCvht();
            if (selectedGv != null && !selectedGv.startsWith("---") && !selectedGv.equals(gv)) {
                continue;
            }
            if (selectedTt != null && !selectedTt.equals(d.getTrangThai())) {
                continue;
            }

            displayedList.add(d);
            cntTotal++;
            if ("ON_TIME".equals(d.getTrangThai())) cntOnTime++;
            else if ("LATE".equals(d.getTrangThai())) cntLate++;
            else if ("ABSENT".equals(d.getTrangThai())) cntAbsent++;
            else if ("EXCUSED".equals(d.getTrangThai())) cntExcused++;
        }

        tableModel.setRowCount(0);
        for (DiemDanh d : displayedList) {
            String hinhThuc = "ONLINE".equals(d.getHinhThuc()) ? "🌐 Online" : ("HYBRID".equals(d.getHinhThuc()) ? "⚡ Kết hợp" : "🏫 Trực tiếp");
            String loaiHienThi;
            switch (d.getLoaiBuoi() != null ? d.getLoaiBuoi() : "") {
                case "TU_VAN_DINH_KY": loaiHienThi = "Tư vấn Định kỳ"; break;
                case "TU_VAN_CANH_BAO": loaiHienThi = "Tư vấn Cảnh báo (T3)"; break;
                case "HOC_BU": loaiHienThi = "Học / Tư Vấn Bù"; break;
                default: loaiHienThi = "Giảng Dạy Chính Khóa"; break;
            }

            String gvHienThi = d.getTenCvht() != null ? d.getTenCvht() : (d.getMaCvht() != null ? d.getMaCvht() : "---");

            tableModel.addRow(new Object[]{
                d.getIdLich(),
                d.getNgayDiemDanh() != null ? d.getNgayDiemDanh().toString() : "---",
                (d.getGioBatDau() != null ? d.getGioBatDau() : "") + " - " + (d.getGioKetThuc() != null ? d.getGioKetThuc() : ""),
                d.getTieuDeBuoiHoc() != null ? d.getTieuDeBuoiHoc() : "Buổi học",
                gvHienThi,
                d.getDiaDiem() != null ? d.getDiaDiem() : "---",
                hinhThuc,
                loaiHienThi,
                d.getTrangThai(),
                d.getGhiChu() != null ? d.getGhiChu() : ""
            });
        }

        // Cập nhật thống kê KPI
        lblTongBuoi.setText(cntTotal + " Buổi");
        lblDungGio.setText(cntOnTime + " Buổi");
        lblDiMuon.setText(cntLate + " Buổi");
        lblVang.setText((cntAbsent + cntExcused) + " Buổi");

        if (cntTotal > 0) {
            double rate = (cntOnTime * 100.0) / cntTotal;
            lblTyLeChuyenCan.setText(String.format("%.1f%%", rate));
        } else {
            lblTyLeChuyenCan.setText("---");
        }
    }

    private void capNhatDiemDanhBuoiChon() {
        int row = tableLichHoc.getSelectedRow();
        if (row < 0 || row >= displayedList.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một buổi học trong bảng để sửa trạng thái điểm danh!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DiemDanh item = displayedList.get(row);

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{
            "ON_TIME (Có mặt đúng giờ)",
            "LATE (Đi muộn)",
            "EXCUSED (Vắng có phép)",
            "ABSENT (Vắng không phép)"
        });
        if ("LATE".equals(item.getTrangThai())) cbStatus.setSelectedIndex(1);
        else if ("EXCUSED".equals(item.getTrangThai())) cbStatus.setSelectedIndex(2);
        else if ("ABSENT".equals(item.getTrangThai())) cbStatus.setSelectedIndex(3);
        else cbStatus.setSelectedIndex(0);

        JTextField txtNote = new JTextField(item.getGhiChu() != null ? item.getGhiChu() : "");

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Trạng thái điểm danh:"));
        form.add(cbStatus);
        form.add(new JLabel("Ghi chú của Giảng viên:"));
        form.add(txtNote);

        int res = JOptionPane.showConfirmDialog(this, form, "Cập Nhật Điểm Danh: Buổi #" + item.getIdLich(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String rawStatus = (String) cbStatus.getSelectedItem();
            String status = "ON_TIME";
            if (rawStatus != null) {
                if (rawStatus.contains("LATE")) status = "LATE";
                else if (rawStatus.contains("EXCUSED")) status = "EXCUSED";
                else if (rawStatus.contains("ABSENT")) status = "ABSENT";
            }
            String note = txtNote.getText().trim();

            if (diemDanhDAO.saveOrUpdateAttendance(item.getIdLich(), sinhVien.getMaSv(), item.getNgayDiemDanh(), status, note)) {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu điểm danh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
