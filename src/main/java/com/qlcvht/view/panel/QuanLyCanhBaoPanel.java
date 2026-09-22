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
        "STT", "MÃ£ Quyáº¿t Äá»‹nh", "MÃ£ SV", "Há» vÃ  TÃªn", "Lá»›p", "Há»c Ká»³", "NÄƒm Há»c", "Má»©c Cáº£nh BÃ¡o", "GPA XÃ©t", "Tráº¡ng ThÃ¡i TÆ° Váº¥n", "NgÃ y Quyáº¿t Äá»‹nh"
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

        JLabel title = new JLabel("QUáº¢N LÃ QUYáº¾T Äá»ŠNH & Cáº¢NH BÃO Há»ŒC Vá»¤");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        lblTotal = new JLabel("Tá»•ng sá»‘: 0 quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o");
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

        // NÃºt QuÃ©t tá»± Ä‘á»™ng
        JButton btnScan = UITheme.createButton("QuÃ©t Tá»± Äá»™ng", UITheme.DANGER, Color.WHITE);
        btnScan.setToolTipText("QuÃ©t há»‡ thá»‘ng vÃ  tá»± Ä‘á»™ng phÃ¡t hiá»‡n sinh viÃªn Ä‘áº¡t Ä‘iá»u kiá»‡n cáº£nh bÃ¡o há»c vá»¥");
        btnScan.addActionListener(e -> onScanCanhBao());
        bar.add(btnScan);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // Bá»™ lá»c Má»©c cáº£nh bÃ¡o
        bar.add(new JLabel("Má»©c:"));
        cbFilterMuc = new JComboBox<>(new String[]{
            "--- Táº¥t cáº£ má»©c ---", 
            "Má»©c 1 (GPA < 2.0)", 
            "Má»©c 2 (GPA < 1.5)", 
            "Buá»™c thÃ´i há»c (GPA < 1.0)"
        });
        cbFilterMuc.addActionListener(e -> filterData());
        bar.add(cbFilterMuc);

        // Bá»™ lá»c Tráº¡ng thÃ¡i tÆ° váº¥n
        bar.add(new JLabel("TÆ° váº¥n:"));
        cbFilterTuVan = new JComboBox<>(new String[]{
            "--- Táº¥t cáº£ tráº¡ng thÃ¡i ---", 
            "ChÆ°a tÆ° váº¥n", 
            "Äang theo dÃµi", 
            "ÄÃ£ tÆ° váº¥n"
        });
        cbFilterTuVan.addActionListener(e -> filterData());
        bar.add(cbFilterTuVan);

        // TÃ¬m kiáº¿m
        bar.add(new JLabel("TÃ¬m:"));
        txtSearch = new JTextField(11);
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "MSSV, há» tÃªn, mÃ£ QÄ...");
        txtSearch.addActionListener(e -> filterData());
        bar.add(txtSearch);

        JButton btnSearch = UITheme.createButton("TÃ¬m Kiáº¿m", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterData());
        bar.add(btnSearch);

        JButton btnReset = UITheme.createButton("LÃ m Má»›i", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { 
            cbFilterMuc.setSelectedIndex(0); 
            cbFilterTuVan.setSelectedIndex(0); 
            txtSearch.setText(""); 
            loadData(); 
        });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        JButton btnNhatKy = UITheme.createButton("Láº­p Nháº­t KÃ½", UITheme.INFO, Color.WHITE);
        btnNhatKy.setToolTipText("Láº­p biÃªn báº£n tÆ° váº¥n CVHT cho sinh viÃªn Ä‘Æ°á»£c chá»n");
        btnNhatKy.addActionListener(e -> onLapNhatKy());
        bar.add(btnNhatKy);

        JButton btnDelete = UITheme.createButton("Gá»¡ QÄ", new Color(170, 70, 70), Color.WHITE);
        btnDelete.setToolTipText("Há»§y bá»/Gá»¡ quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o há»c vá»¥ Ä‘Ã£ chá»n");
        btnDelete.addActionListener(e -> onDeleteCanhBao());
        bar.add(btnDelete);

        JButton btnExport = UITheme.createButton("Xuáº¥t Excel", new Color(46, 125, 50), Color.WHITE);
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

        // Má»©c cáº£nh bÃ¡o renderer
        tableCanhBao.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("Má»©c 1")) {
                        comp.setForeground(new Color(210, 100, 0));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (s.contains("Má»©c 2")) {
                        comp.setForeground(new Color(200, 30, 30));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (s.contains("Muc 3")) {
                        comp.setForeground(new Color(160, 0, 0));
                        comp.setBackground(new Color(255, 230, 230));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (s.contains("Buá»™c thÃ´i há»c")) {
                        comp.setForeground(new Color(140, 0, 0));
                        setFont(UITheme.FONT_BODY_BOLD);
                    }
                }
                return comp;
            }
        });

        // Tráº¡ng thÃ¡i tÆ° váº¥n renderer
        tableCanhBao.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (v != null && !sel) {
                    String s = v.toString();
                    if (s.contains("ÄÃ£ tÆ° váº¥n")) {
                        comp.setForeground(new Color(40, 130, 50));
                        setFont(UITheme.FONT_BODY_BOLD);
                    } else if (s.contains("Äang theo dÃµi")) {
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
                "Há»c ká»³ " + cb.getHocKy(),
                cb.getNamHoc(),
                UITheme.formatMucCanhBao(cb.getMucCanhBao()),
                String.format("%.2f", cb.getGpaXetDuyet()),
                UITheme.formatTrangThaiTuVan(cb.getTrangThaiTuVan()),
                cb.getNgayQuyetDinh() != null ? cb.getNgayQuyetDinh().toString() : "---"
            });
        }
        lblTotal.setText("Tá»•ng sá»‘: " + list.size() + " quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o");
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

        scanPanel.add(new JLabel("Há»c ká»³ xÃ©t duyá»‡t cáº£nh bÃ¡o:"));
        scanPanel.add(cbHk);
        scanPanel.add(new JLabel("NÄƒm há»c xÃ©t duyá»‡t:"));
        scanPanel.add(cbNh);

        int opt = JOptionPane.showConfirmDialog(this, scanPanel, "Cáº¥u hÃ¬nh QuÃ©t Cáº£nh bÃ¡o Há»c vá»¥ Tá»± Ä‘á»™ng", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            int hk = (Integer) cbHk.getSelectedItem();
            String nh = (String) cbNh.getSelectedItem();

            int newCount = canhBaoService.quetCanhBaoHocVu(hk, nh);
            if (newCount > 0) {
                JOptionPane.showMessageDialog(this,
                    "QuÃ©t hoÃ n táº¥t thÃ nh cÃ´ng!\nPhÃ¡t hiá»‡n vÃ  láº­p má»›i: " + newCount + " quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o há»c vá»¥ cho Há»c ká»³ " + hk + " (" + nh + ").",
                    "Káº¿t quáº£ quÃ©t cáº£nh bÃ¡o", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "KhÃ´ng phÃ¡t sinh cáº£nh bÃ¡o há»c vá»¥ má»›i cho Há»c ká»³ " + hk + " (" + nh + ").",
                    "Káº¿t quáº£ quÃ©t cáº£nh bÃ¡o", JOptionPane.INFORMATION_MESSAGE);
            }
            loadData();
        }
    }

    private void onLapNhatKy() {
        CanhBaoHocVu cb = getSelectedCanhBao();
        LapNhatKyDialog dlg = new LapNhatKyDialog((Frame) SwingUtilities.getWindowAncestor(this), cb, currentUser);
        dlg.setVisible(true);
        if (dlg.isSavedSuccess()) {
            com.qlcvht.service.AuditService.getInstance().logAction(currentUser, "LAP_NHAT_KY", "Lap bien ban tu van canh bao: " + cb.getMaCanhBao());
            loadData();
        }
    }

    private void onDeleteCanhBao() {
        CanhBaoHocVu cb = getSelectedCanhBao();
        if (cb == null) {
            JOptionPane.showMessageDialog(this, "Vui lÃ²ng chá»n má»™t quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o cáº§n gá»¡ bá»!", "ThÃ´ng bÃ¡o", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Báº¡n cÃ³ cháº¯c muá»‘n gá»¡ quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o " + cb.getMaCanhBao() + " cá»§a sinh viÃªn " + cb.getHoTenSv() + "?",
            "XÃ¡c nháº­n gá»¡ quyáº¿t Ä‘á»‹nh", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = canhBaoDAO.deleteCanhBao(cb.getId());
            if (ok) {
                com.qlcvht.service.AuditService.getInstance().logAction(currentUser, "GO_CANH_BAO", "Go quyet dinh canh bao: " + cb.getMaCanhBao() + " cua sinh vien " + cb.getHoTenSv());
                JOptionPane.showMessageDialog(this, "ÄÃ£ gá»¡ quyáº¿t Ä‘á»‹nh cáº£nh bÃ¡o há»c vá»¥ thÃ nh cÃ´ng!", "ThÃ´ng bÃ¡o", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gá»¡ quyáº¿t Ä‘á»‹nh tháº¥t báº¡i!", "Lá»—i", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}