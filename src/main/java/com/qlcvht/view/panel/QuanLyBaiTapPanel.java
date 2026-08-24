package com.qlcvht.view.panel;

import com.qlcvht.dao.BaiTapDAO;
import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.model.BaiTap;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.NopBaiTap;
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
 * Panel Quản lý Bài tập, Đồ án & Đánh giá Quá trình (Assignments & Assessments).
 */
public class QuanLyBaiTapPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final BaiTapDAO baiTapDAO = new BaiTapDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();

    private JComboBox<String> cbFilterLop;
    private JTable tableBaiTap;
    private DefaultTableModel modelBaiTap;
    private JTable tableNopBai;
    private DefaultTableModel modelNopBai;

    private List<BaiTap> currentBaiTapList;
    private List<NopBaiTap> currentNopList;
    private BaiTap selectedBaiTap = null;

    private JLabel lblTongBaiTap;
    private JLabel lblTyLeNop;
    private JLabel lblDiemTB;

    public QuanLyBaiTapPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));
        initUI();
        loadBaiTap();
    }

    private void initUI() {
        // === HEADER ===
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("📝  QUẢN LÝ BÀI TẬP, ĐỒ ÁN & ĐÁNH GIÁ QUÁ TRÌNH");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Quản lý bài tập (TMA), trắc nghiệm/giữa kỳ (CMA), đồ án (Project), theo dõi nộp bài & chấm điểm quá trình");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Stats Banner
        JPanel statRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statRow.setOpaque(false);
        lblTongBaiTap = addStatCard(statRow, "TỔNG SỐ BÀI TẬP / ĐỒ ÁN", "0 Bài", UITheme.PRIMARY);
        lblTyLeNop = addStatCard(statRow, "TỶ LỆ NỘP BÀI TRUNG BÌNH", "0%", new Color(46, 125, 50));
        lblDiemTB = addStatCard(statRow, "ĐIỂM TRUNG BÌNH QUÁ TRÌNH", "0.0 / 10", new Color(230, 119, 0));
        topPanel.add(statRow, BorderLayout.CENTER);

        // Toolbar
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
        for (LopHoc l : dsLop) cbFilterLop.addItem(l.getMaLop());
        cbFilterLop.addActionListener(e -> loadBaiTap());
        toolbar.add(cbFilterLop);

        JButton btnThem = UITheme.createButton("➕ Thêm Bài Tập Mới", UITheme.PRIMARY, Color.WHITE);
        btnThem.addActionListener(e -> showThemBaiTapDialog());
        toolbar.add(btnThem);

        JButton btnXoa = UITheme.createButton("🗑 Xóa Bài Tập", new Color(198, 40, 40), Color.WHITE);
        btnXoa.addActionListener(e -> xoaBaiTap());
        toolbar.add(btnXoa);

        JButton btnChamDiem = UITheme.createButton("✏️ Chấm Điểm & Nhận Xét", new Color(46, 125, 50), Color.WHITE);
        btnChamDiem.addActionListener(e -> showChamDiemDialog());
        toolbar.add(btnChamDiem);

        JButton btnRefresh = UITheme.createButton("🔄 Làm Mới", UITheme.BG_MAIN, UITheme.TEXT_PRIMARY);
        btnRefresh.addActionListener(e -> loadBaiTap());
        toolbar.add(btnRefresh);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // === SPLIT PANE: BAI TAP (LEFT/TOP) & NOP BAI (RIGHT/BOTTOM) ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        // Panel Bài Tập
        JPanel pnlBaiTap = new JPanel(new BorderLayout(0, 6));
        pnlBaiTap.setBackground(UITheme.BG_WHITE);
        pnlBaiTap.setBorder(BorderFactory.createTitledBorder("Danh sách Bài tập & Đồ án"));

        String[] headersBT = {"ID", "Lớp", "Tiêu Đề Bài Tập", "Loại Đánh Giá", "Trọng Số", "Hạn Nộp", "Đã Nộp / Sĩ Số", "Trạng Thái"};
        modelBaiTap = new DefaultTableModel(headersBT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableBaiTap = new JTable(modelBaiTap);
        UITheme.styleTable(tableBaiTap);
        tableBaiTap.setRowHeight(32);
        tableBaiTap.getColumnModel().getColumn(0).setMaxWidth(45);
        tableBaiTap.getColumnModel().getColumn(1).setPreferredWidth(70);
        tableBaiTap.getColumnModel().getColumn(2).setPreferredWidth(280);
        tableBaiTap.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableBaiTap.getColumnModel().getColumn(4).setPreferredWidth(70);
        tableBaiTap.getColumnModel().getColumn(5).setPreferredWidth(90);
        tableBaiTap.getColumnModel().getColumn(6).setPreferredWidth(110);
        tableBaiTap.getColumnModel().getColumn(7).setPreferredWidth(80);

        tableBaiTap.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableBaiTap.getSelectedRow();
                if (row >= 0 && currentBaiTapList != null && row < currentBaiTapList.size()) {
                    selectedBaiTap = currentBaiTapList.get(row);
                    loadSubmissions(selectedBaiTap.getId());
                }
            }
        });

        pnlBaiTap.add(new JScrollPane(tableBaiTap), BorderLayout.CENTER);
        splitPane.setTopComponent(pnlBaiTap);

        // Panel Nộp Bài
        JPanel pnlNopBai = new JPanel(new BorderLayout(0, 6));
        pnlNopBai.setBackground(UITheme.BG_WHITE);
        pnlNopBai.setBorder(BorderFactory.createTitledBorder("Danh sách Bài nộp của Sinh viên & Điểm quá trình"));

        String[] headersNop = {"ID", "Mã SV", "Họ và Tên", "Lớp", "Ngày Nộp", "File Đính Kèm", "Điểm Số", "Nhận Xét Đánh Giá", "Tình Trạng"};
        modelNopBai = new DefaultTableModel(headersNop, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableNopBai = new JTable(modelNopBai);
        UITheme.styleTable(tableNopBai);
        tableNopBai.setRowHeight(32);
        tableNopBai.getColumnModel().getColumn(0).setMaxWidth(45);
        tableNopBai.getColumnModel().getColumn(1).setPreferredWidth(85);
        tableNopBai.getColumnModel().getColumn(2).setPreferredWidth(160);
        tableNopBai.getColumnModel().getColumn(3).setPreferredWidth(70);
        tableNopBai.getColumnModel().getColumn(4).setPreferredWidth(130);
        tableNopBai.getColumnModel().getColumn(5).setPreferredWidth(140);
        tableNopBai.getColumnModel().getColumn(6).setPreferredWidth(70);
        tableNopBai.getColumnModel().getColumn(7).setPreferredWidth(250);
        tableNopBai.getColumnModel().getColumn(8).setPreferredWidth(90);

        // Highlight điểm
        tableNopBai.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                if (value != null && !value.toString().isEmpty()) {
                    try {
                        double d = Double.parseDouble(value.toString());
                        if (d >= 8.5) lbl.setForeground(new Color(46, 125, 50));
                        else if (d >= 5.0) lbl.setForeground(new Color(25, 118, 210));
                        else lbl.setForeground(new Color(198, 40, 40));
                    } catch (Exception ignored) {}
                }
                return lbl;
            }
        });

        pnlNopBai.add(new JScrollPane(tableNopBai), BorderLayout.CENTER);
        splitPane.setBottomComponent(pnlNopBai);

        add(splitPane, BorderLayout.CENTER);
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

    public void loadBaiTap() {
        modelBaiTap.setRowCount(0);
        modelNopBai.setRowCount(0);

        String maLop = cbFilterLop.getSelectedIndex() > 0 ? (String) cbFilterLop.getSelectedItem() : null;
        currentBaiTapList = baiTapDAO.getAll(maLop);

        int totalSubmissions = 0;
        int totalExpected = 0;

        for (BaiTap b : currentBaiTapList) {
            String loai = "TMA".equals(b.getLoaiDanhGia()) ? "Bài tập (TMA)" : ("CMA".equals(b.getLoaiDanhGia()) ? "Kiểm tra (CMA)" : "Đồ án (Project)");
            modelBaiTap.addRow(new Object[]{
                b.getId(),
                b.getMaLop(),
                b.getTieuDe(),
                loai,
                b.getTrongSo() + "%",
                b.getHanNop() != null ? b.getHanNop().toString() : "",
                b.getSoBaiDaNop() + " / " + b.getTongSoSinhVien() + " SV",
                b.getTrangThai()
            });

            totalSubmissions += b.getSoBaiDaNop();
            totalExpected += b.getTongSoSinhVien();
        }

        lblTongBaiTap.setText(currentBaiTapList.size() + " Bài tập");
        if (totalExpected > 0) {
            double rate = (totalSubmissions * 100.0) / totalExpected;
            lblTyLeNop.setText(String.format("%.1f%%", rate));
        } else {
            lblTyLeNop.setText("0%");
        }

        if (!currentBaiTapList.isEmpty()) {
            tableBaiTap.setRowSelectionInterval(0, 0);
        }
    }

    private void loadSubmissions(int idBaiTap) {
        modelNopBai.setRowCount(0);
        currentNopList = baiTapDAO.getSubmissionsByAssignment(idBaiTap);

        double sumScores = 0;
        int countGraded = 0;

        for (NopBaiTap n : currentNopList) {
            String diem = n.getDiemSo() != null ? String.format("%.1f", n.getDiemSo()) : "---";
            if (n.getDiemSo() != null) {
                sumScores += n.getDiemSo();
                countGraded++;
            }

            String tt = "GRADED".equals(n.getTrangThai()) ? "✔ Đã chấm" : ("LATE".equals(n.getTrangThai()) ? "⚠️ Nộp muộn" : "● Đã nộp");

            modelNopBai.addRow(new Object[]{
                n.getId(),
                n.getMaSv(),
                n.getHoTen(),
                n.getMaLop(),
                n.getNgayNop() != null ? n.getNgayNop().toString().replace("T", " ") : "",
                n.getFileDinhKem() != null ? n.getFileDinhKem() : "",
                diem,
                n.getNhanXet() != null ? n.getNhanXet() : "",
                tt
            });
        }

        if (countGraded > 0) {
            lblDiemTB.setText(String.format("%.2f / 10", sumScores / countGraded));
        } else {
            lblDiemTB.setText("Chưa chấm");
        }
    }

    private void showThemBaiTapDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Bài Tập / Đồ Án Mới", true);
        dialog.setSize(500, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 14));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(new EmptyBorder(16, 20, 16, 20));

        JComboBox<String> cbLop = new JComboBox<>();
        List<LopHoc> dsLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : dsLop) cbLop.addItem(l.getMaLop());

        JTextField txtTieuDe = new JTextField();
        JComboBox<String> cbLoai = new JComboBox<>(new String[]{"TMA", "CMA", "PROJECT"});
        JTextField txtTrongSo = new JTextField("20.0");
        JTextField txtHanNop = new JTextField(LocalDate.now().plusWeeks(2).toString());
        JTextField txtDinhDang = new JTextField("pdf, docx, zip");
        JTextArea txtMoTa = new JTextArea(3, 20);

        form.add(new JLabel("Lớp học (*):"));
        form.add(cbLop);
        form.add(new JLabel("Tiêu đề bài tập (*):"));
        form.add(txtTieuDe);
        form.add(new JLabel("Loại đánh giá:"));
        form.add(cbLoai);
        form.add(new JLabel("Trọng số % (*):"));
        form.add(txtTrongSo);
        form.add(new JLabel("Hạn nộp (YYYY-MM-DD) (*):"));
        form.add(txtHanNop);
        form.add(new JLabel("Định dạng file cho phép:"));
        form.add(txtDinhDang);
        form.add(new JLabel("Mô tả yêu cầu:"));
        form.add(new JScrollPane(txtMoTa));

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnLuu = UITheme.createButton("Lưu Bài Tập", UITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = UITheme.createButton("Hủy", UITheme.BG_MAIN, UITheme.TEXT_PRIMARY);

        btnHuy.addActionListener(e -> dialog.dispose());
        btnLuu.addActionListener(e -> {
            String tieuDe = txtTieuDe.getText().trim();
            if (tieuDe.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tiêu đề bài tập!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double trongSo = 20.0;
            try {
                trongSo = Double.parseDouble(txtTrongSo.getText().trim());
            } catch (Exception ignored) {}

            LocalDate hanNop;
            try {
                hanNop = LocalDate.parse(txtHanNop.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Hạn nộp sai định dạng (YYYY-MM-DD)!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BaiTap b = new BaiTap();
            b.setMaLop((String) cbLop.getSelectedItem());
            b.setTieuDe(tieuDe);
            b.setLoaiDanhGia((String) cbLoai.getSelectedItem());
            b.setTrongSo(trongSo);
            b.setHanNop(hanNop);
            b.setDinhDangChoPhep(txtDinhDang.getText().trim());
            b.setMoTa(txtMoTa.getText().trim());
            b.setTrangThai("OPEN");

            if (baiTapDAO.insert(b)) {
                JOptionPane.showMessageDialog(dialog, "Thêm bài tập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBaiTap();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm bài tập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnHuy);
        btnPanel.add(btnLuu);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showChamDiemDialog() {
        int row = tableNopBai.getSelectedRow();
        if (row < 0 || currentNopList == null || row >= currentNopList.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bài nộp của sinh viên ở bảng dưới để chấm điểm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NopBaiTap n = currentNopList.get(row);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chấm Điểm: " + n.getHoTen() + " (" + n.getMaSv() + ")", true);
        dialog.setSize(440, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 14));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(new EmptyBorder(16, 20, 16, 20));

        JTextField txtDiem = new JTextField(n.getDiemSo() != null ? String.valueOf(n.getDiemSo()) : "8.0");
        JTextArea txtNhanXet = new JTextArea(n.getNhanXet() != null ? n.getNhanXet() : "Bài làm tốt, đúng yêu cầu", 3, 20);

        form.add(new JLabel("Sinh viên:"));
        form.add(new JLabel(n.getHoTen() + " (" + n.getMaSv() + ")"));
        form.add(new JLabel("File nộp:"));
        form.add(new JLabel(n.getFileDinhKem() != null ? n.getFileDinhKem() : "---"));
        form.add(new JLabel("Điểm số (0.0 - 10.0) (*):"));
        form.add(txtDiem);
        form.add(new JLabel("Nhận xét / Feedback:"));
        form.add(new JScrollPane(txtNhanXet));

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnLuu = UITheme.createButton("Lưu Điểm & Đánh Giá", UITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = UITheme.createButton("Hủy", UITheme.BG_MAIN, UITheme.TEXT_PRIMARY);

        btnHuy.addActionListener(e -> dialog.dispose());
        btnLuu.addActionListener(e -> {
            try {
                double diem = Double.parseDouble(txtDiem.getText().trim());
                if (diem < 0 || diem > 10) {
                    JOptionPane.showMessageDialog(dialog, "Điểm phải từ 0 đến 10!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nhanXet = txtNhanXet.getText().trim();
                if (baiTapDAO.gradeSubmission(n.getId(), diem, nhanXet)) {
                    JOptionPane.showMessageDialog(dialog, "Đã lưu điểm và nhận xét thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    if (selectedBaiTap != null) {
                        loadSubmissions(selectedBaiTap.getId());
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi lưu điểm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Điểm số không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnPanel.add(btnHuy);
        btnPanel.add(btnLuu);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void xoaBaiTap() {
        int row = tableBaiTap.getSelectedRow();
        if (row < 0 || currentBaiTapList == null || row >= currentBaiTapList.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài tập cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BaiTap b = currentBaiTapList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bài tập: \"" + b.getTieuDe() + "\" và toàn bộ bài nộp liên quan?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (baiTapDAO.delete(b.getId())) {
                JOptionPane.showMessageDialog(this, "Đã xóa bài tập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadBaiTap();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa bài tập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
