package com.qlcvht.view.panel;

import com.qlcvht.dao.AuditDAO;
import com.qlcvht.model.AuditLog;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.model.UserSession;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

public class QuanLyAuditPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final AuditDAO auditDAO = new AuditDAO();

    // Stats Cards
    private JLabel lblOnlineCount;
    private JLabel lblLoginsToday;
    private JLabel lblLastLogout;
    private JLabel lblTotalAuditRecords;

    // Tab 1: Active Sessions
    private JTable tblSessions;
    private DefaultTableModel modelSessions;
    private List<UserSession> listSessions;
    private Timer refreshTimer;

    // Tab 2: Audit Logs
    private JTable tblAuditLogs;
    private DefaultTableModel modelAuditLogs;
    private JTextField txtSearchKeyword;
    private JComboBox<String> cbActionFilter;
    private List<AuditLog> listLogs;

    public QuanLyAuditPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadAllData();

        // Timer tự động làm mới mỗi 5 giây
        refreshTimer = new Timer(5000, e -> {
            if (isShowing()) {
                refreshStatsAndSessions();
            }
        });
        refreshTimer.start();
    }

    private void initUI() {
        // 1. TOP HEADER & STATS
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);

        // Header Title Box
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("🛡️ HỆ THỐNG AUDIT DỮ LIỆU & GIÁM SÁT TRUY CẬP THỜI GIAN THỰC");
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(UITheme.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Theo dõi lịch sử đăng nhập, đăng xuất, các tài khoản đang trực tuyến và toàn bộ thao tác thay đổi dữ liệu");
        lblSub.setFont(UITheme.fontPlain(12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel titleText = new JPanel(new GridLayout(2, 1, 0, 3));
        titleText.setOpaque(false);
        titleText.add(lblTitle);
        titleText.add(lblSub);
        titlePanel.add(titleText, BorderLayout.WEST);

        JButton btnManualRefresh = UITheme.createButton("🔄 Làm Mới Dữ Liệu", UITheme.PRIMARY, Color.WHITE);
        btnManualRefresh.setFont(UITheme.fontBold(12));
        btnManualRefresh.addActionListener(e -> loadAllData());
        titlePanel.add(btnManualRefresh, BorderLayout.EAST);

        topPanel.add(titlePanel, BorderLayout.NORTH);

        // 4 KPI Cards
        JPanel statRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statRow.setOpaque(false);

        lblOnlineCount = addStatCard(statRow, "ĐANG TRỰC TUYẾN (ONLINE)", "0", new Color(34, 197, 94), "🟢 Người dùng đang tương tác");
        lblLoginsToday = addStatCard(statRow, "ĐĂNG NHẬP HÔM NAY", "0", UITheme.PRIMARY, "🔵 Lượt truy cập trong ngày");
        lblLastLogout = addStatCard(statRow, "ĐĂNG XUẤT GẦN NHẤT", "N/A", new Color(249, 115, 22), "⚪ Phiên vừa kết thúc");
        lblTotalAuditRecords = addStatCard(statRow, "TỔNG BẢN GHI AUDIT", "0", new Color(139, 92, 246), "📜 Lịch sử thao tác hệ thống");

        topPanel.add(statRow, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 2. MAIN TABBED PANE
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.fontBold(13));

        tabbedPane.addTab("🟢 Giám Sát Tài Khoản Đang Hoạt Động (Sessions Monitor)", buildTabSessions());
        tabbedPane.addTab("📜 Lịch Sử Hoạt Động & Audit Dữ Liệu (Full Audit Trail)", buildTabAuditLogs());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // TAB 1: GIÁM SÁT TÀI KHOẢN ĐANG HOẠT ĐỘNG
    // =========================================================================
    private JPanel buildTabSessions() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        toolbar.setOpaque(false);

        JLabel lblHint = new JLabel("Danh sách các phiên làm việc và trạng thái kết nối trực tuyến của toàn bộ tài khoản:");
        lblHint.setFont(UITheme.fontPlain(13));
        lblHint.setForeground(UITheme.TEXT_SECONDARY);
        toolbar.add(lblHint);

        JButton btnForceLogout = UITheme.createButton("⚡ Buộc Đăng Xuất Tài Khoản", UITheme.DANGER, Color.WHITE);
        btnForceLogout.setFont(UITheme.fontBold(12));
        btnForceLogout.addActionListener(e -> onForceLogout());
        toolbar.add(btnForceLogout);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"STT", "Tên Đăng Nhập", "Họ và Tên", "Vai Trò", "Trạng Thái", "Thời Gian Đăng Nhập", "Thời Gian Đăng Xuất", "Thời Lượng", "Địa Chỉ IP", "Thiết Bị"};
        modelSessions = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblSessions = new JTable(modelSessions);
        UITheme.styleTable(tblSessions);
        tblSessions.setRowHeight(34);

        tblSessions.getColumnModel().getColumn(0).setMaxWidth(45);
        tblSessions.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblSessions.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblSessions.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblSessions.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblSessions.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblSessions.getColumnModel().getColumn(6).setPreferredWidth(140);
        tblSessions.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblSessions.getColumnModel().getColumn(8).setPreferredWidth(120);
        tblSessions.getColumnModel().getColumn(9).setPreferredWidth(150);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tblSessions.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tblSessions.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        tblSessions.getColumnModel().getColumn(3).setCellRenderer(centerRender);
        tblSessions.getColumnModel().getColumn(5).setCellRenderer(centerRender);
        tblSessions.getColumnModel().getColumn(6).setCellRenderer(centerRender);
        tblSessions.getColumnModel().getColumn(7).setCellRenderer(centerRender);
        tblSessions.getColumnModel().getColumn(8).setCellRenderer(centerRender);

        // Custom Renderer cho Trạng Thái Online/Offline
        tblSessions.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String val = (value != null) ? value.toString() : "";
                if (val.contains("ONLINE") || val.contains("Trực tuyến")) {
                    lbl.setForeground(new Color(22, 163, 74));
                    lbl.setText("🟢 Đang Online");
                } else {
                    lbl.setForeground(new Color(148, 163, 184));
                    lbl.setText("⚪ Đã Thoát");
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tblSessions);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // TAB 2: LỊCH SỬ HOẠT ĐỘNG & AUDIT DỮ LIỆU
    // =========================================================================
    private JPanel buildTabAuditLogs() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Filter Toolbar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBar.setOpaque(false);

        filterBar.add(new JLabel("Tìm kiếm:"));
        txtSearchKeyword = new JTextField(15);
        txtSearchKeyword.setPreferredSize(new Dimension(160, 32));
        txtSearchKeyword.addActionListener(e -> loadAuditLogs());
        filterBar.add(txtSearchKeyword);

        filterBar.add(new JLabel("Hành động:"));
        String[] actions = {"ALL - Tất cả hành động", "DANG_NHAP - Đăng nhập", "DANG_XUAT - Đăng xuất", "THEM_MOI - Thêm mới dữ liệu", "CAP_NHAT - Cập nhật dữ liệu", "XOA - Xóa dữ liệu", "XUAT_FILE - Xuất báo cáo", "NHAP_FILE - Nhập dữ liệu", "DIEM_DANH - Điểm danh", "CANH_BAO - Cảnh báo học vụ"};
        cbActionFilter = new JComboBox<>(actions);
        cbActionFilter.setPreferredSize(new Dimension(210, 32));
        cbActionFilter.addActionListener(e -> loadAuditLogs());
        filterBar.add(cbActionFilter);

        JButton btnFilter = UITheme.createButton("Lọc Nhật Ký", UITheme.PRIMARY, Color.WHITE);
        btnFilter.setFont(UITheme.fontBold(12));
        btnFilter.addActionListener(e -> loadAuditLogs());
        filterBar.add(btnFilter);

        JButton btnExport = UITheme.createButton("📥 Xuất File CSV / Excel", new Color(22, 101, 52), Color.WHITE);
        btnExport.setFont(UITheme.fontBold(12));
        btnExport.addActionListener(e -> exportAuditLogsToCsv());
        filterBar.add(btnExport);

        panel.add(filterBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Thời Gian", "Tên Đăng Nhập", "Họ và Tên", "Vai Trò", "Hành Động", "Mô Tả Thao Tác Chi Tiết", "Địa Chỉ IP", "Thiết Bị"};
        modelAuditLogs = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblAuditLogs = new JTable(modelAuditLogs);
        UITheme.styleTable(tblAuditLogs);
        tblAuditLogs.setRowHeight(32);

        tblAuditLogs.getColumnModel().getColumn(0).setMaxWidth(50);
        tblAuditLogs.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblAuditLogs.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblAuditLogs.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblAuditLogs.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblAuditLogs.getColumnModel().getColumn(5).setPreferredWidth(130);
        tblAuditLogs.getColumnModel().getColumn(6).setPreferredWidth(320);
        tblAuditLogs.getColumnModel().getColumn(7).setPreferredWidth(110);
        tblAuditLogs.getColumnModel().getColumn(8).setPreferredWidth(140);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tblAuditLogs.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tblAuditLogs.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        tblAuditLogs.getColumnModel().getColumn(2).setCellRenderer(centerRender);
        tblAuditLogs.getColumnModel().getColumn(4).setCellRenderer(centerRender);
        tblAuditLogs.getColumnModel().getColumn(7).setCellRenderer(centerRender);

        // Custom Renderer cho Loại Hành Động (Action Badges)
        tblAuditLogs.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(11));
                String v = (value != null) ? value.toString() : "";
                if (v.contains("ĐĂNG NHẬP") || v.contains("DANG_NHAP")) {
                    lbl.setForeground(new Color(22, 163, 74));
                } else if (v.contains("ĐĂNG XUẤT") || v.contains("DANG_XUAT")) {
                    lbl.setForeground(new Color(100, 116, 139));
                } else if (v.contains("XÓA") || v.contains("XOA")) {
                    lbl.setForeground(new Color(220, 38, 38));
                } else if (v.contains("CẬP NHẬT") || v.contains("CAP_NHAT")) {
                    lbl.setForeground(new Color(147, 51, 234));
                } else if (v.contains("THÊM MỚI") || v.contains("THEM_MOI")) {
                    lbl.setForeground(new Color(2, 132, 199));
                } else if (v.contains("CẢNH BÁO") || v.contains("CANH_BAO")) {
                    lbl.setForeground(new Color(217, 119, 6));
                } else {
                    lbl.setForeground(new Color(15, 23, 42));
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tblAuditLogs);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // DATA LOADING & REFRESH LOGIC
    // =========================================================================
    public void loadAllData() {
        refreshStatsAndSessions();
        loadAuditLogs();
    }

    private void refreshStatsAndSessions() {
        // 1. Load Sessions
        listSessions = auditDAO.getAllSessions();
        modelSessions.setRowCount(0);

        int stt = 1;
        int onlineCount = 0;
        String lastLogoutTime = "N/A";

        for (UserSession s : listSessions) {
            if (s.isOnline()) {
                onlineCount++;
            } else if ("N/A".equals(lastLogoutTime) && s.getThoiGianDangXuat() != null) {
                lastLogoutTime = s.getFormattedLogoutTime();
            }

            modelSessions.addRow(new Object[]{
                stt++,
                s.getTenDangNhap(),
                s.getHoTen() != null ? s.getHoTen() : s.getTenDangNhap(),
                s.getVaiTro(),
                s.getTrangThai(),
                s.getFormattedLoginTime(),
                s.getFormattedLogoutTime(),
                s.getSessionDuration(),
                s.getDiaChiIp(),
                s.getThietBi()
            });
        }

        // 2. Update KPI Cards
        lblOnlineCount.setText(String.valueOf(onlineCount));
        lblLoginsToday.setText(String.valueOf(auditDAO.countLoginsToday()));
        lblLastLogout.setText(lastLogoutTime);
    }

    private void loadAuditLogs() {
        String kw = (txtSearchKeyword != null) ? txtSearchKeyword.getText().trim() : "";
        String actSel = (cbActionFilter != null) ? (String) cbActionFilter.getSelectedItem() : "ALL";
        String action = null;
        if (actSel != null && !actSel.startsWith("ALL")) {
            action = actSel.split(" - ")[0].trim();
        }

        listLogs = auditDAO.getLogsByFilter(kw, action, null, null);
        modelAuditLogs.setRowCount(0);

        for (AuditLog l : listLogs) {
            modelAuditLogs.addRow(new Object[]{
                l.getId(),
                l.getFormattedTime(),
                l.getTenDangNhap(),
                l.getHoTen(),
                l.getVaiTro(),
                l.getLoaiHanhDongHienThi(),
                l.getMoTaChiTiet(),
                l.getDiaChiIp(),
                l.getThietBi()
            });
        }

        lblTotalAuditRecords.setText(String.valueOf(listLogs.size()));
    }

    private void onForceLogout() {
        int row = tblSessions.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản trong danh sách để buộc đăng xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) modelSessions.getValueAt(row, 1);
        String hoTen = (String) modelSessions.getValueAt(row, 2);
        String status = (String) modelSessions.getValueAt(row, 4);

        if (!"ONLINE".equalsIgnoreCase(status) && !status.contains("Online")) {
            JOptionPane.showMessageDialog(this, "Tài khoản " + username + " hiện đã ở trạng thái OFFLINE!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn BUỘC ĐĂNG XUẤT tài khoản [" + username + " - " + hoTen + "] khỏi hệ thống?",
            "Xác nhận Buộc Đăng Xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            auditDAO.forceLogout(username);
            JOptionPane.showMessageDialog(this, "Đã buộc đăng xuất tài khoản " + username + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadAllData();
        }
    }

    private void exportAuditLogsToCsv() {
        if (listLogs == null || listLogs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu audit log để xuất file!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Báo Cáo Audit Log (CSV / Excel)");
        fileChooser.setSelectedFile(new File("BaoCao_AuditLog_EAUT_" + LocalDate.now() + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(fileToSave, java.nio.charset.StandardCharsets.UTF_8))) {
                // UTF-8 BOM để Excel hiển thị đúng tiếng Việt có dấu
                pw.write('\ufeff');
                pw.println("ID,Thời Gian,Tên Đăng Nhập,Họ và Tên,Vai Trò,Hành Động,Mô Tả Chi Tiết,Địa Chỉ IP,Thiết Bị");
                for (AuditLog l : listLogs) {
                    String desc = l.getMoTaChiTiet() != null ? l.getMoTaChiTiet().replace("\"", "\"\"") : "";
                    pw.println(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        l.getId(),
                        l.getFormattedTime(),
                        l.getTenDangNhap(),
                        l.getHoTen(),
                        l.getVaiTro(),
                        l.getLoaiHanhDongHienThi(),
                        desc,
                        l.getDiaChiIp(),
                        l.getThietBi()
                    ));
                }
                JOptionPane.showMessageDialog(this, "Đã xuất báo cáo Audit Log thành công:\n" + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel addStatCard(JPanel parent, String title, String val, Color color, String subText) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UITheme.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.fontBold(11));
        lblTitle.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(UITheme.fontBold(22));
        lblVal.setForeground(color);

        JLabel lblSub = new JLabel(subText);
        lblSub.setFont(UITheme.fontPlain(11));
        lblSub.setForeground(new Color(148, 163, 184));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);

        parent.add(card);
        return lblVal;
    }
}
