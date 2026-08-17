package com.qlcvht.view.panel;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.CanhBaoService;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.ChiTietSinhVienDialog;
import com.qlcvht.view.dialog.LapNhatKyDialog;

import com.qlcvht.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class QuanLyCanhBaoPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final CanhBaoService canhBaoService = new CanhBaoService();

    private JTable tableCanhBao;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbFilterMuc;
    private JComboBox<String> cbFilterTuVan;
    private JTextField txtSearch;
    private JLabel lblTotal;
    private List<CanhBaoHocVu> currentList = new ArrayList<>();

    private static final String[] COLUMNS = {
        "STT", "Mã Quyết Định", "Mã SV", "Họ và Tên", "Lớp", "Học Kỳ", "Năm Học", "Mức Cảnh Báo", "GPA Xét", "Trạng Thái Tư Vấn", "Ngày Quyết Định"
    };

    public QuanLyCanhBaoPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        initTopPanel();
        initTable();
        loadData();
    }

    private void initTopPanel() {
        JPanel topContainer = new JPanel(new BorderLayout(0, 8));
        topContainer.setOpaque(false);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("⚠️  QUẢN LÝ QUYẾT ĐỊNH & CẢNH BÁO HỌC VỤ");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        lblTotal = new JLabel("Tổng số: 0 quyết định cảnh báo");
        lblTotal.setFont(UITheme.fontBold(13));
        lblTotal.setForeground(UITheme.DANGER);
        lblTotal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.DANGER, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        header.add(title, BorderLayout.WEST);
        header.add(lblTotal, BorderLayout.EAST);
        topContainer.add(header, BorderLayout.NORTH);

        // Toolbar with WrapLayout
        JPanel bar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        // Nút Quét tự động
        JButton btnScan = UITheme.createButton("⚡ Quét Tự Động", UITheme.DANGER, Color.WHITE);
        btnScan.setToolTipText("Quét hệ thống và tự động phát hiện sinh viên đạt điều kiện cảnh báo học vụ");
        btnScan.addActionListener(e -> onScanCanhBao());
        bar.add(btnScan);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // Bộ lọc Mức cảnh báo
        bar.add(new JLabel("Mức:"));
        cbFilterMuc = new JComboBox<>(new String[]{
            "--- Tất cả mức ---", 
            "Mức 1 (GPA < 2.0)", 
            "Mức 2 (GPA < 1.5)", 
            "Buộc thôi học (GPA < 1.0)"
        });
        cbFilterMuc.addActionListener(e -> filterData());
        bar.add(cbFilterMuc);

        // Bộ lọc Trạng thái tư vấn
        bar.add(new JLabel("Tư vấn:"));
        cbFilterTuVan = new JComboBox<>(new String[]{
            "--- Tất cả trạng thái ---", 
            "Chưa tư vấn", 
            "Đang theo dõi", 
            "Đã tư vấn"
        });
        cbFilterTuVan.addActionListener(e -> filterData());
        bar.add(cbFilterTuVan);

        // Tìm kiếm
        bar.add(new JLabel("Tìm:"));
        txtSearch = new JTextField(11);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "MSSV, họ tên, mã QĐ...");
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        JButton btnSearch = UITheme.createButton("🔍 Tìm", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = UITheme.createButton("🔄 Làm Mới", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { 
            cbFilterMuc.setSelectedIndex(0); 
            cbFilterTuVan.setSelectedIndex(0); 
            txtSearch.setText(""); 
            loadData(); 
        });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        JButton btnNhatKy = UITheme.createButton("📝 Lập Nhật Ký", UITheme.INFO, Color.WHITE);
        btnNhatKy.setToolTipText("Lập biên bản tư vấn CVHT cho sinh viên được chọn");
        btnNhatKy.addActionListener(e -> onLapNhatKy());
        bar.add(btnNhatKy);

        JButton btnDelete = UITheme.createButton("🗑️ Gỡ QĐ", new Color(170, 70, 70), Color.WHITE);
        btnDelete.setToolTipText("Hủy bỏ/Gỡ quyết định cảnh báo học vụ đã chọn");
        btnDelete.addActionListener(e -> onDeleteCanhBao());
        bar.add(btnDelete);

        JButton btnExport = UITheme.createButton("📊 Xuất Excel", new Color(46, 125, 50), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(tableCanhBao, "Danh_Sach_Canh_Bao_Hoc_Vu"));
        bar.add(btnExport);

        topContainer.add(bar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableCanhBao = new JTable(tableModel);
        UITheme.styleTable(tableCanhBao);
        tableCanhBao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {45, 140, 95, 160, 90, 70, 90, 160, 75, 125, 95};
        for (int i = 0; i < widths.length && i < tableCanhBao.getColumnCount(); i++) {
            tableCanhBao.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        tableCanhBao.getColumnModel().getColumn(0).setCellRenderer(center);
        tableCanhBao.getColumnModel().getColumn(1).setCellRenderer(center);
        tableCanhBao.getColumnModel().getColumn(2).setCellRenderer(center);
        tableCanhBao.getColumnModel().getColumn(4).setCellRenderer(center);
        tableCanhBao.getColumnModel().getColumn(5).setCellRenderer(center);
        tableCanhBao.getColumnModel().getColumn(6).setCellRenderer(center);
        tableCanhBao.getColumnModel().getColumn(8).setCellRenderer(center);
        tableCanhBao.getColumnModel().getColumn(10).setCellRenderer(center);

        // Mức cảnh báo renderer
        tableCanhBao.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Mức 1")) {
                        comp.setForeground(new Color(210, 100, 0));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (s.contains("Mức 2")) {
                        comp.setForeground(new Color(200, 30, 30));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (s.contains("Buộc thôi học")) {
                        comp.setForeground(new Color(140, 0, 0));
                        setFont(UITheme.FONT_BODY_BOLD);
                    }
                }
                return comp;
            }
        });

        // Trạng thái tư vấn renderer
        tableCanhBao.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Đã tư vấn")) {
                        comp.setForeground(new Color(40, 130, 50));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (s.contains("Đang theo dõi")) {
                        comp.setForeground(new Color(25, 118, 210));
                        setFont(UITheme.FONT_BODY);
                    } else {
                        comp.setForeground(new Color(180, 80, 0));
                        setFont(UITheme.FONT_BODY);
                    }
                }
                return comp;
            }
        });

        tableCanhBao.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CanhBaoHocVu cb = getSelectedCanhBao();
                    if (cb != null) {
                        SinhVien sv = sinhVienDAO.getSinhVienById(cb.getMaSv());
                        if (sv != null) {
                            new ChiTietSinhVienDialog((Frame) SwingUtilities.getWindowAncestor(QuanLyCanhBaoPanel.this), sv).setVisible(true);
                        }
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(tableCanhBao);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
    }

    public void loadData() {
        if ("CO_VAN".equals(currentUser != null ? currentUser.getVaiTro() : "") && currentUser.getMaRef() != null) {
            currentList = canhBaoDAO.getCanhBaoByCoVan(currentUser.getMaRef());
        } else {
            currentList = canhBaoDAO.getAllCanhBao();
        }
        renderTable(currentList);
    }

    private void filterData() {
        String kw = txtSearch.getText().trim().toLowerCase();
        String selMuc = (String) cbFilterMuc.getSelectedItem();
        String selTv = (String) cbFilterTuVan.getSelectedItem();

        List<CanhBaoHocVu> all;
        if ("CO_VAN".equals(currentUser != null ? currentUser.getVaiTro() : "") && currentUser.getMaRef() != null) {
            all = canhBaoDAO.getCanhBaoByCoVan(currentUser.getMaRef());
        } else {
            all = canhBaoDAO.getAllCanhBao();
        }

        List<CanhBaoHocVu> filtered = new ArrayList<>();
        for (CanhBaoHocVu cb : all) {
            boolean matchKw = kw.isEmpty()
                || cb.getMaCanhBao().toLowerCase().contains(kw)
                || cb.getMaSv().toLowerCase().contains(kw)
                || (cb.getHoTenSv() != null && cb.getHoTenSv().toLowerCase().contains(kw));

            boolean matchMuc = true;
            if (selMuc != null && !selMuc.startsWith("---")) {
                String formatted = UITheme.formatMucCanhBao(cb.getMucCanhBao());
                matchMuc = formatted.equalsIgnoreCase(selMuc);
            }

            boolean matchTv = true;
            if (selTv != null && !selTv.startsWith("---")) {
                String formatted = UITheme.formatTrangThaiTuVan(cb.getTrangThaiTuVan());
                matchTv = formatted.equalsIgnoreCase(selTv);
            }

            if (matchKw && matchMuc && matchTv) {
                filtered.add(cb);
            }
        }

        currentList = filtered;
        renderTable(currentList);
    }

    private void renderTable(List<CanhBaoHocVu> list) {
        tableModel.setRowCount(0);
        int stt = 1;
        for (CanhBaoHocVu cb : list) {
            tableModel.addRow(new Object[]{
                stt++,
                cb.getMaCanhBao(),
                cb.getMaSv(),
                cb.getHoTenSv() != null ? cb.getHoTenSv() : cb.getMaSv(),
                cb.getMaLop() != null ? cb.getMaLop() : "---",
                "Học kỳ " + cb.getHocKy(),
                cb.getNamHoc(),
                UITheme.formatMucCanhBao(cb.getMucCanhBao()),
                String.format("%.2f", cb.getGpaXetDuyet()),
                UITheme.formatTrangThaiTuVan(cb.getTrangThaiTuVan()),
                cb.getNgayQuyetDinh() != null ? cb.getNgayQuyetDinh().toString() : "---"
            });
        }
        lblTotal.setText("Tổng số: " + list.size() + " quyết định cảnh báo");
    }

    private CanhBaoHocVu getSelectedCanhBao() {
        int row = tableCanhBao.getSelectedRow();
        if (row < 0 || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void onScanCanhBao() {
        JPanel scanPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        JComboBox<Integer> cbHk = new JComboBox<>(new Integer[]{1, 2, 3});
        cbHk.setSelectedItem(2);
        JComboBox<String> cbNh = new JComboBox<>(new String[]{"2022-2023", "2023-2024", "2024-2025", "2025-2026"});
        cbNh.setSelectedItem("2023-2024");

        scanPanel.add(new JLabel("Học kỳ xét duyệt cảnh báo:"));
        scanPanel.add(cbHk);
        scanPanel.add(new JLabel("Năm học xét duyệt:"));
        scanPanel.add(cbNh);

        int opt = JOptionPane.showConfirmDialog(this, scanPanel, "Cấu hình Quét Cảnh báo Học vụ Tự động", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            int hk = (Integer) cbHk.getSelectedItem();
            String nh = (String) cbNh.getSelectedItem();

            int newCount = canhBaoService.quetCanhBaoHocVu(hk, nh);
            if (newCount > 0) {
                JOptionPane.showMessageDialog(this,
                    "Quét hoàn tất thành công!\nPhát hiện và lập mới: " + newCount + " quyết định cảnh báo học vụ cho Học kỳ " + hk + " (" + nh + ").",
                    "Kết quả quét cảnh báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không phát sinh cảnh báo học vụ mới cho Học kỳ " + hk + " (" + nh + ").",
                    "Kết quả quét cảnh báo", JOptionPane.INFORMATION_MESSAGE);
            }
            loadData();
        }
    }

    private void onLapNhatKy() {
        CanhBaoHocVu cb = getSelectedCanhBao();
        LapNhatKyDialog dlg = new LapNhatKyDialog((Frame) SwingUtilities.getWindowAncestor(this), cb, currentUser);
        dlg.setVisible(true);
        if (dlg.isSavedSuccess()) loadData();
    }

    private void onDeleteCanhBao() {
        CanhBaoHocVu cb = getSelectedCanhBao();
        if (cb == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một quyết định cảnh báo cần gỡ bỏ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn gỡ quyết định cảnh báo " + cb.getMaCanhBao() + " của sinh viên " + cb.getHoTenSv() + "?",
            "Xác nhận gỡ quyết định", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = canhBaoDAO.deleteCanhBao(cb.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã gỡ quyết định cảnh báo học vụ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gỡ quyết định thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}