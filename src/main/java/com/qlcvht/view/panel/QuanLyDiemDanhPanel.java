package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.ChuyenCanDAO;
import com.qlcvht.dao.DiemDanhDAO;
import com.qlcvht.dao.LichGiangDayDAO;
import com.qlcvht.dao.MonHocDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.ChuyenCanMonHoc;
import com.qlcvht.model.DiemDanh;
import com.qlcvht.model.LichGiangDay;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.MonHoc;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.ChuyenCanService;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel Quản lý Điểm danh, Theo dõi Chuyên cần & Xét Điều kiện Dự thi (Cấm thi theo tín chỉ).
 * Chuẩn hóa theo Quy chế Đào tạo Tín chỉ Đại học (Bộ GD&ĐT / EAUT).
 */
public class QuanLyDiemDanhPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final DiemDanhDAO diemDanhDAO = new DiemDanhDAO();
    private final LichGiangDayDAO lichDAO = new LichGiangDayDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();
    private final MonHocDAO monHocDAO = new MonHocDAO();
    private final ChuyenCanDAO chuyenCanDAO = new ChuyenCanDAO();
    private final ChuyenCanService chuyenCanService = new ChuyenCanService();

    // Tabbed Pane
    private JTabbedPane mainTabbedPane;

    // --- Tab 1: Diem danh buoi hoc ---
    private JComboBox<String> cbLichHoc;
    private JTable tableDiemDanh;
    private DefaultTableModel modelDiemDanh;
    private List<LichGiangDay> dsLich;
    private List<SinhVien> dsSinhVien;
    private Map<String, DiemDanh> attendanceMap = new HashMap<>();
    private JLabel lblTyLeChuyenCan;
    private JLabel lblDungGio;
    private JLabel lblDiMuon;
    private JLabel lblVang;

    // --- Tab 2: Theo doi chuyen can tin chi & Cam thi ---
    private JComboBox<String> cbFilterLopCC;
    private JComboBox<String> cbFilterMonHoc;
    private JComboBox<String> cbFilterTrangThaiCC;
    private JTextField txtSearchCC;
    private JTable tableChuyenCan;
    private DefaultTableModel modelChuyenCan;
    private List<ChuyenCanMonHoc> dsChuyenCan = new ArrayList<>();

    private JLabel lblTongDangKy;
    private JLabel lblDuDieuKien;
    private JLabel lblCanhBaoNguyCo;
    private JLabel lblBiCamThi;

    public QuanLyDiemDanhPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        loadLichHoc();
        loadChuyenCanMonHocData();
    }

    private void initUI() {
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(UITheme.fontBold(13));

        // Tab 1: Theo Buổi học
        mainTabbedPane.addTab("  📅 Điểm Danh Theo Buổi & Lịch Học  ", buildTabDiemDanhBuoiHoc());

        // Tab 2: Chuyên cần Môn học & Cấm thi
        mainTabbedPane.addTab("  📋 Danh Sách Cấm Thi & Chuyên Cần Tín Chỉ  ", buildTabChuyenCanMonHoc());

        add(mainTabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // TAB 1: ĐIỂM DANH THEO BUỔI HỌC
    // =========================================================================
    private JPanel buildTabDiemDanhBuoiHoc() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 8, 10, 8));

        // Header & Stats
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐIỂM DANH BUỔI HỌC & HOẠT ĐỘNG HỌC VỤ");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Điểm danh từng buổi học/tư vấn theo thời khóa biểu giảng dạy và kiểm tra tỷ lệ chuyên cần tức thì");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Stats Cards Row
        JPanel statRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statRow.setOpaque(false);
        lblTyLeChuyenCan = addStatCard(statRow, "TỶ LỆ CHUYÊN CẦN BUỔI", "100%", UITheme.PRIMARY);
        lblDungGio = addStatCard(statRow, "ĐÚNG GIỜ (ON TIME)", "0 SV", new Color(46, 125, 50));
        lblDiMuon = addStatCard(statRow, "ĐI MUỘN (LATE)", "0 SV", new Color(230, 119, 0));
        lblVang = addStatCard(statRow, "VẮNG MẶT (ABSENT)", "0 SV", new Color(198, 40, 40));
        topPanel.add(statRow, BorderLayout.CENTER);

        // Toolbar
        JPanel toolbar = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 6));
        toolbar.setBackground(UITheme.BG_WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));

        toolbar.add(new JLabel("Chọn Buổi Lịch:"));
        cbLichHoc = new JComboBox<>();
        cbLichHoc.setPreferredSize(new Dimension(320, 32));
        cbLichHoc.addActionListener(e -> loadDanhSachSinhVien());
        toolbar.add(cbLichHoc);

        JButton btnTatCaCoMat = UITheme.createButton("✔ Tất Cả Có Mặt", new Color(46, 125, 50), Color.WHITE);
        btnTatCaCoMat.addActionListener(e -> setAllStatus("ON_TIME"));
        toolbar.add(btnTatCaCoMat);

        JButton btnLuu = UITheme.createButton("💾 Lưu Điểm Danh", UITheme.PRIMARY, Color.WHITE);
        btnLuu.addActionListener(e -> luuDiemDanh());
        toolbar.add(btnLuu);

        JButton btnLocVang = UITheme.createButton("⚠️ Cảnh Báo Vắng Buổi", new Color(198, 40, 40), Color.WHITE);
        btnLocVang.addActionListener(e -> canhBaoVangNhieu());
        toolbar.add(btnLocVang);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] headers = {"Mã SV", "Họ và Tên", "Lớp", "Giới Tính", "Trạng Thái Điểm Danh", "Ghi Chú Đánh Giá"};
        modelDiemDanh = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4 || col == 5;
            }
        };

        tableDiemDanh = new JTable(modelDiemDanh);
        UITheme.styleTable(tableDiemDanh);
        tableDiemDanh.setRowHeight(34);

        tableDiemDanh.getColumnModel().getColumn(0).setPreferredWidth(90);
        tableDiemDanh.getColumnModel().getColumn(1).setPreferredWidth(180);
        tableDiemDanh.getColumnModel().getColumn(2).setPreferredWidth(75);
        tableDiemDanh.getColumnModel().getColumn(3).setPreferredWidth(70);
        tableDiemDanh.getColumnModel().getColumn(4).setPreferredWidth(170);
        tableDiemDanh.getColumnModel().getColumn(5).setPreferredWidth(250);

        JComboBox<String> cbStatusEditor = new JComboBox<>(new String[]{
            "ON_TIME (Có mặt đúng giờ)",
            "LATE (Đi muộn)",
            "EXCUSED (Vắng có phép)",
            "ABSENT (Vắng không phép)"
        });
        tableDiemDanh.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cbStatusEditor));

        tableDiemDanh.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String v = value != null ? value.toString() : "";
                if (v.contains("ON_TIME") || v.contains("đúng giờ")) {
                    lbl.setForeground(new Color(46, 125, 50));
                    lbl.setText("● Có mặt đúng giờ");
                } else if (v.contains("LATE") || v.contains("muộn")) {
                    lbl.setForeground(new Color(230, 119, 0));
                    lbl.setText("● Đi muộn");
                } else if (v.contains("EXCUSED") || v.contains("có phép")) {
                    lbl.setForeground(new Color(25, 118, 210));
                    lbl.setText("● Vắng có phép");
                } else if (v.contains("ABSENT") || v.contains("không phép")) {
                    lbl.setForeground(new Color(198, 40, 40));
                    lbl.setText("● Vắng không phép");
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tableDiemDanh);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // TAB 2: QUẢN LÝ CHUYÊN CẦN TÍN CHỈ & DANH SÁCH CẤM THI
    // =========================================================================
    private JPanel buildTabChuyenCanMonHoc() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 8, 10, 8));

        // Header & KPI Stat Cards
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("XÉT ĐIỀU KIỆN DỰ THI THEO TÍN CHỈ (QUY CHẾ VẮNG > 20% CẤM THI)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(new Color(185, 28, 28));

        JLabel lblSub = new JLabel("Quy định: 2TC (10 buổi) vắng >= 3 buổi cấm thi | 3TC (15 buổi) vắng >= 4 buổi cấm thi | 4TC (20 buổi) vắng >= 5 buổi cấm thi");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // 4 KPI Cards
        JPanel statRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statRow.setOpaque(false);
        lblTongDangKy = addStatCard(statRow, "TỔNG SỐ LƯỢT HỌC PHẦN", "0", UITheme.PRIMARY);
        lblDuDieuKien = addStatCard(statRow, "ĐỦ ĐIỀU KIỆN DỰ THI", "0", new Color(46, 125, 50));
        lblCanhBaoNguyCo = addStatCard(statRow, "CẢNH BÁO NGUY CƠ", "0", new Color(230, 119, 0));
        lblBiCamThi = addStatCard(statRow, "CẤM THI CHÍNH THỨC", "0", new Color(198, 40, 40));
        topPanel.add(statRow, BorderLayout.CENTER);

        // Filter Bar & Action Buttons
        JPanel toolbar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(UITheme.BG_WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        // Lọc Lớp
        toolbar.add(new JLabel("Lớp:"));
        cbFilterLopCC = new JComboBox<>();
        cbFilterLopCC.setPreferredSize(new Dimension(130, 30));
        loadLopFilter();
        cbFilterLopCC.addActionListener(e -> filterChuyenCanTable());
        toolbar.add(cbFilterLopCC);

        // Lọc Môn
        toolbar.add(new JLabel("Môn học:"));
        cbFilterMonHoc = new JComboBox<>();
        cbFilterMonHoc.setPreferredSize(new Dimension(180, 30));
        loadMonHocFilter();
        cbFilterMonHoc.addActionListener(e -> filterChuyenCanTable());
        toolbar.add(cbFilterMonHoc);

        // Lọc Trạng thái
        toolbar.add(new JLabel("Trạng thái:"));
        cbFilterTrangThaiCC = new JComboBox<>(new String[]{
            "--- Tất cả ---",
            "ĐỦ ĐIỀU KIỆN DỰ THI",
            "CẢNH BÁO NGUY CƠ",
            "CẤM THI CHÍNH THỨC"
        });
        cbFilterTrangThaiCC.setPreferredSize(new Dimension(160, 30));
        cbFilterTrangThaiCC.addActionListener(e -> filterChuyenCanTable());
        toolbar.add(cbFilterTrangThaiCC);

        // Tìm kiếm
        toolbar.add(new JLabel("Tìm MSSV/Tên:"));
        txtSearchCC = new JTextField(10);
        txtSearchCC.setPreferredSize(new Dimension(110, 30));
        txtSearchCC.addActionListener(e -> filterChuyenCanTable());
        toolbar.add(txtSearchCC);

        JButton btnSearch = UITheme.createButton("Tìm", UITheme.PRIMARY, Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(65, 30));
        btnSearch.addActionListener(e -> filterChuyenCanTable());
        toolbar.add(btnSearch);

        // Actions
        JButton btnScan = UITheme.createButton("🔄 Quét Cấm Thi Tự Động", new Color(2, 132, 199), Color.WHITE);
        btnScan.setPreferredSize(new Dimension(175, 30));
        btnScan.addActionListener(e -> handleAutoScanCamThi());
        toolbar.add(btnScan);

        JButton btnSendNotice = UITheme.createButton("📧 Gửi Thông Báo Cấm Thi", new Color(220, 38, 38), Color.WHITE);
        btnSendNotice.setPreferredSize(new Dimension(175, 30));
        btnSendNotice.addActionListener(e -> handleSendWarningNotice());
        toolbar.add(btnSendNotice);

        JButton btnExport = UITheme.createButton("📊 Xuất Excel", new Color(16, 185, 129), Color.WHITE);
        btnExport.setPreferredSize(new Dimension(105, 30));
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableChuyenCan, "Danh_Sach_Cam_Thi_EAUT"));
        toolbar.add(btnExport);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table Model
        String[] headers = {
            "Mã SV", "Họ và Tên", "Lớp", "Mã Môn", "Tên Môn Học", "Số TC", 
            "Tổng Buổi", "Vắng TĐ (20%)", "Vắng Nghỉ", "Đi Muộn", "Điểm CC", "Điều Kiện Dự Thi", "Lý Do / Căn Cứ"
        };
        modelChuyenCan = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableChuyenCan = new JTable(modelChuyenCan);
        UITheme.styleTable(tableChuyenCan);
        tableChuyenCan.setRowHeight(34);

        tableChuyenCan.getColumnModel().getColumn(0).setPreferredWidth(85);   // Ma SV
        tableChuyenCan.getColumnModel().getColumn(1).setPreferredWidth(150);  // Ho Ten
        tableChuyenCan.getColumnModel().getColumn(2).setPreferredWidth(75);   // Lop
        tableChuyenCan.getColumnModel().getColumn(3).setPreferredWidth(65);   // Ma Mon
        tableChuyenCan.getColumnModel().getColumn(4).setPreferredWidth(160);  // Ten Mon
        tableChuyenCan.getColumnModel().getColumn(5).setPreferredWidth(50);   // So TC
        tableChuyenCan.getColumnModel().getColumn(6).setPreferredWidth(65);   // Tong Buoi
        tableChuyenCan.getColumnModel().getColumn(7).setPreferredWidth(75);   // Vang Toi Da
        tableChuyenCan.getColumnModel().getColumn(8).setPreferredWidth(65);   // Vang Nghi
        tableChuyenCan.getColumnModel().getColumn(9).setPreferredWidth(65);   // Di Muon
        tableChuyenCan.getColumnModel().getColumn(10).setPreferredWidth(60);  // Diem CC
        tableChuyenCan.getColumnModel().getColumn(11).setPreferredWidth(140); // Trang Thai Du Thi
        tableChuyenCan.getColumnModel().getColumn(12).setPreferredWidth(220); // Ly Do

        // Align center for numeric columns
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tableChuyenCan.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(2).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(3).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(5).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(6).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(7).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(8).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(9).setCellRenderer(centerRender);
        tableChuyenCan.getColumnModel().getColumn(10).setCellRenderer(centerRender);

        // Custom Renderer for Exam Status (Col 11)
        tableChuyenCan.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String v = value != null ? value.toString() : "";
                if (v.contains("CẤM THI") || v.contains("CAM_THI")) {
                    lbl.setForeground(new Color(185, 28, 28));
                    lbl.setText("⛔ CẤM THI");
                } else if (v.contains("NGUY CƠ") || v.contains("CANH_BAO")) {
                    lbl.setForeground(new Color(217, 119, 6));
                    lbl.setText("⚠️ NGUY CƠ CẤM THI");
                } else {
                    lbl.setForeground(new Color(22, 101, 52));
                    lbl.setText("✔ ĐỦ ĐIỀU KIỆN");
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tableChuyenCan);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JLabel addStatCard(JPanel parent, String title, String val, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UITheme.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 14, 10, 14)
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

    // =========================================================================
    // LOGIC TAB 1: ĐIỂM DANH BUỔI HỌC
    // =========================================================================
    public void loadLichHoc() {
        cbLichHoc.removeAllItems();
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null && !currentUser.getMaRef().isBlank()) {
            dsLich = lichDAO.getByFilter(null, currentUser.getMaRef(), null, null, null);
        } else {
            dsLich = lichDAO.getAll();
        }
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
        if (idx < 0 || dsLich == null || idx >= dsLich.size()) return;

        LichGiangDay lich = dsLich.get(idx);
        String maLop = lich.getMaLop();

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

    // =========================================================================
    // LOGIC TAB 2: QUẢN LÝ CHUYÊN CẦN MÔN HỌC & CẤM THI
    // =========================================================================
    private void loadLopFilter() {
        cbFilterLopCC.removeAllItems();
        cbFilterLopCC.addItem("--- Tất cả Lớp ---");
        List<LopHoc> lops = coVanDAO.getAllLopHoc();
        for (LopHoc l : lops) {
            cbFilterLopCC.addItem(l.getMaLop());
        }
    }

    private void loadMonHocFilter() {
        cbFilterMonHoc.removeAllItems();
        cbFilterMonHoc.addItem("--- Tất cả Môn ---");
        List<MonHoc> mons = monHocDAO.getAll();
        for (MonHoc m : mons) {
            cbFilterMonHoc.addItem(m.getMaMon() + " - " + m.getTenMon());
        }
    }

    public void loadChuyenCanMonHocData() {
        if (currentUser != null && "CO_VAN".equals(currentUser.getVaiTro()) && currentUser.getMaRef() != null && !currentUser.getMaRef().isBlank()) {
            dsChuyenCan = chuyenCanDAO.getByAdvisor(currentUser.getMaRef());
        } else {
            dsChuyenCan = chuyenCanDAO.getAll();
        }
        filterChuyenCanTable();
    }

    private void filterChuyenCanTable() {
        String selLop = (String) cbFilterLopCC.getSelectedItem();
        String selMon = (String) cbFilterMonHoc.getSelectedItem();
        String selTT = (String) cbFilterTrangThaiCC.getSelectedItem();
        String keyword = txtSearchCC.getText().trim().toLowerCase();

        String maMonFilter = null;
        if (selMon != null && !selMon.contains("Tất cả")) {
            maMonFilter = selMon.split(" - ")[0].trim();
        }

        String maLopFilter = null;
        if (selLop != null && !selLop.contains("Tất cả")) {
            maLopFilter = selLop.trim();
        }

        modelChuyenCan.setRowCount(0);
        int cntTotal = 0;
        int cntDu = 0;
        int cntNguyCo = 0;
        int cntCam = 0;

        for (ChuyenCanMonHoc cc : dsChuyenCan) {
            // Lọc lớp
            if (maLopFilter != null && !maLopFilter.equalsIgnoreCase(cc.getMaLop())) continue;
            // Lọc môn
            if (maMonFilter != null && !maMonFilter.equalsIgnoreCase(cc.getMaMon())) continue;
            // Lọc trạng thái
            if (selTT != null && !selTT.contains("Tất cả")) {
                if (selTT.contains("CẤM THI") && !"CAM_THI".equals(cc.getTrangThaiDuThi())) continue;
                if (selTT.contains("NGUY CƠ") && !"CANH_BAO_NGUY_CO".equals(cc.getTrangThaiDuThi())) continue;
                if (selTT.contains("ĐỦ ĐIỀU KIỆN") && !"DU_DIEU_KIEN".equals(cc.getTrangThaiDuThi())) continue;
            }
            // Lọc từ khóa
            if (!keyword.isEmpty()) {
                boolean matchMa = cc.getMaSv() != null && cc.getMaSv().toLowerCase().contains(keyword);
                boolean matchTen = cc.getHoTenSv() != null && cc.getHoTenSv().toLowerCase().contains(keyword);
                if (!matchMa && !matchTen) continue;
            }

            cntTotal++;
            if ("CAM_THI".equals(cc.getTrangThaiDuThi())) cntCam++;
            else if ("CANH_BAO_NGUY_CO".equals(cc.getTrangThaiDuThi())) cntNguyCo++;
            else cntDu++;

            int maxVang = (int) Math.floor(cc.getTongSoBuoi() * 0.20);
            int vangNghi = cc.getSoBuoiVangKhongPhep() + cc.getSoBuoiVangCoPhep();

            modelChuyenCan.addRow(new Object[]{
                cc.getMaSv(),
                cc.getHoTenSv() != null ? cc.getHoTenSv() : "N/A",
                cc.getMaLop(),
                cc.getMaMon(),
                cc.getTenMon() != null ? cc.getTenMon() : cc.getMaMon(),
                cc.getSoTinChi(),
                cc.getTongSoBuoi(),
                maxVang + " buổi",
                vangNghi + " buổi",
                cc.getSoBuoiMuon(),
                String.format("%.1f", cc.getDiemChuyenCan()),
                cc.getTrangThaiDuThiHienThi(),
                cc.getLyDoCamThi() != null && !cc.getLyDoCamThi().isBlank() ? cc.getLyDoCamThi() : "Đủ điều kiện dự thi kết thúc học phần"
            });
        }

        lblTongDangKy.setText(cntTotal + " LƯỢT");
        lblDuDieuKien.setText(cntDu + " ĐỦ");
        lblCanhBaoNguyCo.setText(cntNguyCo + " NGUY CƠ");
        lblBiCamThi.setText(cntCam + " CẤM THI");
    }

    private void handleAutoScanCamThi() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Hệ thống sẽ tiến hành rà soát tự động 100% dữ liệu điểm danh theo quy chế tín chỉ:\n" +
            "• Môn 2 Tín chỉ: Vắng >= 3 buổi -> CẤM THI\n" +
            "• Môn 3 Tín chỉ: Vắng >= 4 buổi -> CẤM THI\n" +
            "• Môn 4 Tín chỉ: Vắng >= 5 buổi -> CẤM THI\n\n" +
            "Bạn có chắc chắn muốn chạy quét và cập nhật trạng thái cấm thi?",
            "Xác Nhận Quét Cấm Thi Tự Động",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int updated = chuyenCanService.quetVaCapNhatCamThiTuDong();
            JOptionPane.showMessageDialog(
                this,
                "Quét hoàn tất! Đã kiểm tra và cập nhật trạng thái chuyên cần cho " + updated + " học phần.",
                "Hoàn Tất Quét Cấm Thi",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadChuyenCanMonHocData();
        }
    }

    private void handleSendWarningNotice() {
        List<ChuyenCanMonHoc> listCam = chuyenCanDAO.getDanhSachCamThi();
        if (listCam.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hiện không có sinh viên nào bị Cấm thi trong danh sách!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Tìm thấy " + listCam.size() + " trường hợp sinh viên bị CẤM THI.\n" +
            "Bạn có muốn phát lệnh gửi thông báo cảnh báo chính thức đến cổng thông tin của toàn bộ sinh viên này?",
            "Xác Nhận Gửi Thông Báo Cấm Thi",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String sender = currentUser != null ? currentUser.getHoTen() : "Phòng Đào Tạo & Quản Lý Sinh Viên EAUT";
            int sent = chuyenCanService.guiThongBaoCanhBaoCamThi(sender);
            JOptionPane.showMessageDialog(
                this,
                "Đã phát đi thông báo cấm thi thành công tới " + sent + " sinh viên vi phạm quy chế chuyên cần!",
                "Thành Công",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
