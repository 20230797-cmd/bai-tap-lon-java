package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.LichGiangDayDAO;
import com.qlcvht.model.CoVanHocTap;
import com.qlcvht.model.LichGiangDay;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel Thời khóa biểu & Lịch Cố vấn Học vụ (Teaching & Advising Schedule).
 */
public class LichGiangDayPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final LichGiangDayDAO lichDAO = new LichGiangDayDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();

    private JComboBox<String> cbFilterLop;
    private JComboBox<String> cbFilterTrangThai;
    private JComboBox<String> cbFilterThoiGian;
    private JTable tableLich;
    private DefaultTableModel tableModel;
    private List<LichGiangDay> currentList;

    public LichGiangDayPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));
        initUI();
        loadData();
    }

    private void initUI() {
        // === HEADER & FILTERS ===
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("📅  THỜI KHÓA BIỂU & LỊCH CỐ VẤN HỌC VỤ");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Quản lý lịch giảng dạy, lịch sinh hoạt cố vấn định kỳ, lịch gặp nhóm sinh viên cảnh báo & học bù");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Filter & Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        toolbar.setBackground(UITheme.BG_WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));

        toolbar.add(new JLabel("Lớp học:"));
        cbFilterLop = new JComboBox<>();
        cbFilterLop.setPreferredSize(new Dimension(140, 32));
        cbFilterLop.addItem("--- Tất cả ---");
        List<LopHoc> dsLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : dsLop) {
            cbFilterLop.addItem(l.getMaLop());
        }
        cbFilterLop.addActionListener(e -> loadData());
        toolbar.add(cbFilterLop);

        toolbar.add(new JLabel("Thời gian:"));
        cbFilterThoiGian = new JComboBox<>(new String[]{"Tất cả", "Hôm nay", "Tuần này", "Tháng này"});
        cbFilterThoiGian.setPreferredSize(new Dimension(120, 32));
        cbFilterThoiGian.addActionListener(e -> loadData());
        toolbar.add(cbFilterThoiGian);

        toolbar.add(new JLabel("Trạng thái:"));
        cbFilterTrangThai = new JComboBox<>(new String[]{"Tất cả", "SCHEDULED (Đã lên lịch)", "COMPLETED (Đã hoàn thành)", "CANCELLED (Đã hủy)"});
        cbFilterTrangThai.setPreferredSize(new Dimension(180, 32));
        cbFilterTrangThai.addActionListener(e -> loadData());
        toolbar.add(cbFilterTrangThai);

        JButton btnThemLich = UITheme.createButton("➕ Thêm Lịch Mới", UITheme.PRIMARY, Color.WHITE);
        btnThemLich.addActionListener(e -> showThemSuaDialog(null));
        toolbar.add(btnThemLich);

        JButton btnHoanThanh = UITheme.createButton("✓ Đánh dấu Hoàn thành", new Color(46, 125, 50), Color.WHITE);
        btnHoanThanh.addActionListener(e -> danhDauTrangThai("COMPLETED"));
        toolbar.add(btnHoanThanh);

        JButton btnHuyLich = UITheme.createButton("✕ Hủy Buổi", new Color(198, 40, 40), Color.WHITE);
        btnHuyLich.addActionListener(e -> danhDauTrangThai("CANCELLED"));
        toolbar.add(btnHuyLich);

        JButton btnXoa = UITheme.createButton("🗑 Xóa", new Color(150, 150, 150), Color.WHITE);
        btnXoa.addActionListener(e -> xoaLich());
        toolbar.add(btnXoa);

        topPanel.add(toolbar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // === TABLE ===
        String[] headers = {"ID", "Ngày", "Thời Gian", "Tiêu Đề Buổi / Môn", "Lớp", "Loại Buổi", "Hình Thức", "Địa Điểm / Link", "Cố Vấn / GV", "Trạng Thái"};
        tableModel = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableLich = new JTable(tableModel);
        UITheme.styleTable(tableLich);
        tableLich.setRowHeight(36);

        tableLich.getColumnModel().getColumn(0).setMaxWidth(50);
        tableLich.getColumnModel().getColumn(1).setPreferredWidth(95);
        tableLich.getColumnModel().getColumn(2).setPreferredWidth(95);
        tableLich.getColumnModel().getColumn(3).setPreferredWidth(230);
        tableLich.getColumnModel().getColumn(4).setPreferredWidth(70);
        tableLich.getColumnModel().getColumn(5).setPreferredWidth(140);
        tableLich.getColumnModel().getColumn(6).setPreferredWidth(90);
        tableLich.getColumnModel().getColumn(7).setPreferredWidth(180);
        tableLich.getColumnModel().getColumn(8).setPreferredWidth(140);
        tableLich.getColumnModel().getColumn(9).setPreferredWidth(120);

        // Custom Renderer cho Trạng thái & Loại buổi
        tableLich.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String v = value != null ? value.toString() : "";
                if ("SCHEDULED".equals(v) || v.contains("Đã lên lịch")) {
                    lbl.setForeground(new Color(21, 101, 192));
                    lbl.setText("● Đã lên lịch");
                } else if ("COMPLETED".equals(v) || v.contains("Hoàn thành")) {
                    lbl.setForeground(new Color(46, 125, 50));
                    lbl.setText("✔ Hoàn thành");
                } else if ("CANCELLED".equals(v) || v.contains("Hủy")) {
                    lbl.setForeground(new Color(198, 40, 40));
                    lbl.setText("✖ Đã hủy");
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tableLich);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);

        String maLop = cbFilterLop.getSelectedIndex() > 0 ? (String) cbFilterLop.getSelectedItem() : null;
        String trangThai = null;
        if (cbFilterTrangThai.getSelectedIndex() == 1) trangThai = "SCHEDULED";
        else if (cbFilterTrangThai.getSelectedIndex() == 2) trangThai = "COMPLETED";
        else if (cbFilterTrangThai.getSelectedIndex() == 3) trangThai = "CANCELLED";

        LocalDate tuNgay = null;
        LocalDate denNgay = null;
        int timeIdx = cbFilterThoiGian.getSelectedIndex();
        LocalDate today = LocalDate.now();
        if (timeIdx == 1) { // Hôm nay
            tuNgay = today;
            denNgay = today;
        } else if (timeIdx == 2) { // Tuần này
            tuNgay = today.minusDays(today.getDayOfWeek().getValue() - 1);
            denNgay = tuNgay.plusDays(6);
        } else if (timeIdx == 3) { // Tháng này
            tuNgay = today.withDayOfMonth(1);
            denNgay = today.withDayOfMonth(today.lengthOfMonth());
        }

        currentList = lichDAO.getByFilter(maLop, null, tuNgay, denNgay, trangThai);
        for (LichGiangDay l : currentList) {
            String loaiHienThi;
            switch (l.getLoaiBuoi() != null ? l.getLoaiBuoi() : "") {
                case "TU_VAN_DINH_KY": loaiHienThi = "Tư vấn Học vụ Định kỳ"; break;
                case "TU_VAN_CANH_BAO": loaiHienThi = "Tư vấn Nhóm Cảnh báo (T3)"; break;
                case "HOC_BU": loaiHienThi = "Buổi Dạy / Tư Vấn Bù"; break;
                default: loaiHienThi = "Giảng Dạy / Chuyên Đề"; break;
            }

            String hinhThuc = "ONLINE".equals(l.getHinhThuc()) ? "🌐 Online" : ("HYBRID".equals(l.getHinhThuc()) ? "⚡ Kết hợp" : "🏫 Trực tiếp");

            tableModel.addRow(new Object[]{
                l.getId(),
                l.getNgay() != null ? l.getNgay().toString() : "",
                (l.getGioBatDau() != null ? l.getGioBatDau() : "") + " - " + (l.getGioKetThuc() != null ? l.getGioKetThuc() : ""),
                l.getTieuDe(),
                l.getMaLop(),
                loaiHienThi,
                hinhThuc,
                l.getDiaDiem() != null ? l.getDiaDiem() : "",
                l.getTenCvht() != null ? l.getTenCvht() : l.getMaCvht(),
                l.getTrangThai()
            });
        }
    }

    private void danhDauTrangThai(String status) {
        int row = tableLich.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một buổi lịch trong bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LichGiangDay l = currentList.get(row);
        l.setTrangThai(status);
        if (lichDAO.update(l)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái buổi lịch thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLich() {
        int row = tableLich.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một buổi lịch cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LichGiangDay l = currentList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa buổi lịch: \"" + l.getTieuDe() + "\"?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (lichDAO.delete(l.getId())) {
                JOptionPane.showMessageDialog(this, "Đã xóa buổi lịch thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa buổi lịch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showThemSuaDialog(LichGiangDay lich) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Mới Lịch Giảng Dạy & Tư Vấn", true);
        dialog.setSize(520, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 14));

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setBorder(new EmptyBorder(16, 20, 16, 20));

        JTextField txtTieuDe = new JTextField();
        JComboBox<String> cbLop = new JComboBox<>();
        List<LopHoc> dsLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : dsLop) cbLop.addItem(l.getMaLop());

        JComboBox<String> cbCvht = new JComboBox<>();
        List<CoVanHocTap> dsCv = coVanDAO.getAllCoVan();
        for (CoVanHocTap cv : dsCv) cbCvht.addItem(cv.getMaCvht() + " - " + cv.getHoTen());

        JTextField txtNgay = new JTextField(LocalDate.now().toString());
        JTextField txtGioBD = new JTextField("08:00");
        JTextField txtGioKT = new JTextField("10:00");
        JTextField txtDiaDiem = new JTextField("Phòng H1-302");
        JComboBox<String> cbHinhThuc = new JComboBox<>(new String[]{"TRUC_TIEP", "ONLINE", "HYBRID"});
        JComboBox<String> cbLoaiBuoi = new JComboBox<>(new String[]{"GIANG_DAY", "TU_VAN_DINH_KY", "TU_VAN_CANH_BAO", "HOC_BU"});

        form.add(new JLabel("Tiêu đề buổi / môn (*):"));
        form.add(txtTieuDe);
        form.add(new JLabel("Lớp học (*):"));
        form.add(cbLop);
        form.add(new JLabel("Cố vấn / Giảng viên (*):"));
        form.add(cbCvht);
        form.add(new JLabel("Ngày (YYYY-MM-DD) (*):"));
        form.add(txtNgay);
        form.add(new JLabel("Giờ bắt đầu (HH:mm) (*):"));
        form.add(txtGioBD);
        form.add(new JLabel("Giờ kết thúc (HH:mm) (*):"));
        form.add(txtGioKT);
        form.add(new JLabel("Địa điểm / Link Online:"));
        form.add(txtDiaDiem);
        form.add(new JLabel("Hình thức:"));
        form.add(cbHinhThuc);
        form.add(new JLabel("Loại buổi:"));
        form.add(cbLoaiBuoi);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnLuu = UITheme.createButton("Lưu Lịch Trình", UITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = UITheme.createButton("Hủy Bỏ", UITheme.BG_MAIN, UITheme.TEXT_PRIMARY);

        btnHuy.addActionListener(e -> dialog.dispose());
        btnLuu.addActionListener(e -> {
            String tieuDe = txtTieuDe.getText().trim();
            if (tieuDe.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tiêu đề buổi lịch!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate ngay;
            try {
                ngay = LocalDate.parse(txtNgay.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ngày sai định dạng (YYYY-MM-DD)!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedCv = (String) cbCvht.getSelectedItem();
            String maCv = selectedCv != null ? selectedCv.split(" - ")[0] : "CV001";
            String tenCv = selectedCv != null && selectedCv.contains(" - ") ? selectedCv.split(" - ")[1] : "";

            // Kiểm tra trùng lịch
            boolean conflict = lichDAO.checkConflict(maCv, ngay, txtGioBD.getText().trim(), txtGioKT.getText().trim(), 0);
            if (conflict) {
                int c = JOptionPane.showConfirmDialog(dialog, "Cảnh báo: Đã có một buổi lịch khác của Cố vấn trùng khung giờ này!\nBạn có muốn tiếp tục lưu?", "Trùng Lịch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (c != JOptionPane.YES_OPTION) return;
            }

            LichGiangDay l = new LichGiangDay();
            l.setTieuDe(tieuDe);
            l.setMaLop((String) cbLop.getSelectedItem());
            l.setTenLop((String) cbLop.getSelectedItem());
            l.setMaCvht(maCv);
            l.setTenCvht(tenCv);
            l.setNgay(ngay);
            l.setGioBatDau(txtGioBD.getText().trim());
            l.setGioKetThuc(txtGioKT.getText().trim());
            l.setDiaDiem(txtDiaDiem.getText().trim());
            l.setHinhThuc((String) cbHinhThuc.getSelectedItem());
            l.setLoaiBuoi((String) cbLoaiBuoi.getSelectedItem());
            l.setTrangThai("SCHEDULED");

            if (lichDAO.insert(l)) {
                JOptionPane.showMessageDialog(dialog, "Thêm lịch mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi lưu lịch vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnHuy);
        btnPanel.add(btnLuu);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
