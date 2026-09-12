package com.qlcvht.view.panel;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.dao.TaiKhoanDAO;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel Quản lý Tài khoản người dùng dành riêng cho ADMIN.
 * Cung cấp chức năng tạo, sửa, xóa, tìm kiếm, lọc vai trò và đặt lại mật khẩu.
 */
public class QuanLyTaiKhoanPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbFilterRole;
    private JLabel lblTotalUsers;

    public QuanLyTaiKhoanPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadData();
    }

    private void initUI() {
        // 1. TOP HEADER & FILTER
        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN TRỊ TÀI KHOẢN NGƯỜI DÙNG");
        lblTitle.setFont(UITheme.fontBold(20));
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblTotalUsers = new JLabel("Tổng số: 0 tài khoản");
        lblTotalUsers.setFont(UITheme.fontBold(13));
        lblTotalUsers.setForeground(UITheme.PRIMARY);

        titlePanel.add(lblTitle, BorderLayout.WEST);
        titlePanel.add(lblTotalUsers, BorderLayout.EAST);
        topContainer.add(titlePanel, BorderLayout.NORTH);

        // Control & Filter Bar (2 organized rows to prevent any button clipping)
        JPanel controlContainer = new JPanel();
        controlContainer.setLayout(new BoxLayout(controlContainer, BoxLayout.Y_AXIS));
        controlContainer.setBackground(Color.WHITE);
        controlContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Row 1: Search and Filters
        JPanel rowFilter = new JPanel(new com.qlcvht.util.WrapLayout(FlowLayout.LEFT, 8, 4));
        rowFilter.setOpaque(false);

        rowFilter.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(15);
        txtSearch.setFont(UITheme.fontPlain(13));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tên đăng nhập, họ tên, email...");
        txtSearch.addActionListener(e -> loadData());
        rowFilter.add(txtSearch);

        rowFilter.add(new JLabel("Vai trò:"));
        cbFilterRole = new JComboBox<>(new String[]{
            "TẤT CẢ VAI TRÒ",
            "ADMIN",
            "QUAN_LY",
            "CO_VAN",
            "SINH_VIEN"
        });
        cbFilterRole.setFont(UITheme.fontPlain(13));
        cbFilterRole.addActionListener(e -> loadData());
        rowFilter.add(cbFilterRole);

        JButton btnSearch = UITheme.createButton("Tìm Kiếm", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> loadData());
        rowFilter.add(btnSearch);

        JButton btnRefresh = UITheme.createButton("Làm Mới", new Color(100, 116, 139), Color.WHITE);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterRole.setSelectedIndex(0);
            loadData();
        });
        rowFilter.add(btnRefresh);

        // Row 2: Action CRUD Buttons
        JPanel rowActions = new JPanel(new com.qlcvht.util.WrapLayout(FlowLayout.LEFT, 8, 4));
        rowActions.setOpaque(false);

        JButton btnAdd = UITheme.createButton("+ Thêm Tài Khoản", new Color(16, 185, 129), Color.WHITE);
        btnAdd.addActionListener(e -> showAddDialog());
        rowActions.add(btnAdd);

        JButton btnEdit = UITheme.createButton("Sửa Thông Tin", new Color(59, 130, 246), Color.WHITE);
        btnEdit.addActionListener(e -> showEditDialog());
        rowActions.add(btnEdit);

        JButton btnResetPass = UITheme.createButton("Reset Mật Khẩu", new Color(245, 158, 11), Color.WHITE);
        btnResetPass.addActionListener(e -> showResetPassDialog());
        rowActions.add(btnResetPass);

        JButton btnDelete = UITheme.createButton("Xóa Tài Khoản", new Color(239, 68, 68), Color.WHITE);
        btnDelete.addActionListener(e -> onDeleteUser());
        rowActions.add(btnDelete);

        JButton btnSeed = UITheme.createButton("🔄 Nạp Lại Dữ Liệu Mẫu EAUT", new Color(139, 92, 246), Color.WHITE);
        btnSeed.setToolTipText("Khởi tạo và nạp lại toàn bộ 100% dữ liệu mẫu EAUT vào CSDL");
        btnSeed.addActionListener(e -> {
            boolean confirm = com.qlcvht.util.MessageUtil.confirm(
                this,
                "Bạn có chắc chắn muốn làm mới toàn bộ Cơ sở Dữ liệu và nạp lại 100% dữ liệu mẫu EAUT?\n(Bao gồm: 4 Khoa, 8 Lớp, 120 Sinh viên, Điểm, Cảnh báo, Lịch giảng dạy, Điểm danh và Tin nhắn mẫu)"
            );
            if (confirm) {
                btnSeed.setEnabled(false);
                btnSeed.setText("Đang nạp dữ liệu...");
                com.qlcvht.util.AsyncWorker.execute(
                    () -> DatabaseConnection.resetAndSeedDatabase(),
                    success -> {
                        btnSeed.setEnabled(true);
                        btnSeed.setText("🔄 Nạp Lại Dữ Liệu Mẫu EAUT");
                        if (Boolean.TRUE.equals(success)) {
                            com.qlcvht.util.MessageUtil.showSuccess(this, "Khởi tạo và nạp 100% dữ liệu mẫu EAUT thành công!");
                            loadData();
                        } else {
                            com.qlcvht.util.MessageUtil.showError(this, "Có lỗi khi nạp dữ liệu vào CSDL!");
                        }
                    },
                    err -> {
                        btnSeed.setEnabled(true);
                        btnSeed.setText("🔄 Nạp Lại Dữ Liệu Mẫu EAUT");
                        com.qlcvht.util.MessageUtil.showError(this, "Lỗi: " + err.getMessage());
                    }
                );
            }
        });
        rowActions.add(btnSeed);

        controlContainer.add(rowFilter);
        controlContainer.add(Box.createVerticalStrut(4));
        controlContainer.add(new JSeparator(SwingConstants.HORIZONTAL));
        controlContainer.add(Box.createVerticalStrut(4));
        controlContainer.add(rowActions);

        topContainer.add(controlContainer, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 2. TABLE CENTER
        String[] columns = {"ID", "Tên Đăng Nhập", "Họ Và Tên", "Email", "Vai Trò", "Mã Liên Kết (Ref)", "Ngày Tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(UITheme.fontPlain(13));
        table.getTableHeader().setFont(UITheme.fontBold(13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(30, 41, 59));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);

        // Custom Cell Renderer for Role Tag
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.fontBold(12));
                String role = value != null ? value.toString() : "";
                if (!isSelected) {
                    switch (role) {
                        case "ADMIN" -> {
                            lbl.setForeground(new Color(185, 28, 28));
                            lbl.setText("👑 ADMIN");
                        }
                        case "QUAN_LY" -> {
                            lbl.setForeground(new Color(30, 64, 175));
                            lbl.setText("🏛️ QUẢN LÝ");
                        }
                        case "CO_VAN" -> {
                            lbl.setForeground(new Color(21, 128, 61));
                            lbl.setText("👨‍🏫 CỐ VẤN");
                        }
                        case "SINH_VIEN" -> {
                            lbl.setForeground(new Color(107, 114, 128));
                            lbl.setText("🎓 SINH VIÊN");
                        }
                        default -> lbl.setText(role);
                    }
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        String kw = txtSearch.getText().trim();
        String selectedRole = (String) cbFilterRole.getSelectedItem();
        String roleFilter = ("TẤT CẢ VAI TRÒ".equals(selectedRole)) ? "" : selectedRole;

        tableModel.setRowCount(0);
        List<TaiKhoan> list = taiKhoanDAO.getAll(kw, roleFilter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (TaiKhoan tk : list) {
            String dateStr = tk.getNgayTao() != null ? sdf.format(tk.getNgayTao()) : "N/A";
            tableModel.addRow(new Object[]{
                tk.getId(),
                tk.getTenDangNhap(),
                tk.getHoTen(),
                tk.getEmail() != null ? tk.getEmail() : "",
                tk.getVaiTro(),
                tk.getMaRef() != null ? tk.getMaRef() : "—",
                dateStr
            });
        }
        lblTotalUsers.setText("Tổng số: " + list.size() + " tài khoản");
    }

    private void showAddDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Tài Khoản Mới", true);
        dlg.setSize(440, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 16));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        JTextField txtUser = new JTextField();
        JTextField txtTen = new JTextField();
        JTextField txtMail = new JTextField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"SINH_VIEN", "CO_VAN", "QUAN_LY", "ADMIN"});
        JTextField txtRef = new JTextField();
        JPasswordField txtPass = new JPasswordField("123456");

        form.add(new JLabel("Tên đăng nhập (*):"));
        form.add(txtUser);
        form.add(new JLabel("Họ và tên (*):"));
        form.add(txtTen);
        form.add(new JLabel("Email:"));
        form.add(txtMail);
        form.add(new JLabel("Vai trò (*):"));
        form.add(cbRole);
        form.add(new JLabel("Mã Ref (MSSV / Mã CV):"));
        form.add(txtRef);
        form.add(new JLabel("Mật khẩu (mặc định 123456):"));
        form.add(txtPass);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        JButton btnSave = UITheme.createButton("Lưu Tài Khoản", UITheme.PRIMARY, Color.WHITE);
        btnSave.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String name = txtTen.getText().trim();
            String email = txtMail.getText().trim();
            String role = (String) cbRole.getSelectedItem();
            String ref = txtRef.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();

            if (u.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ Tên đăng nhập và Họ tên!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            TaiKhoan tk = new TaiKhoan(0, u, pass.isEmpty() ? "123456" : pass, name, email, role, ref.isEmpty() ? null : ref, null);
            if (taiKhoanDAO.them(tk)) {
                JOptionPane.showMessageDialog(dlg, "Thêm tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dlg, "Thêm tài khoản thất bại! Tên đăng nhập có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = UITheme.createButton("Hủy", new Color(148, 163, 184), Color.WHITE);
        btnCancel.addActionListener(e -> dlg.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dlg.add(btnPanel, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản cần sửa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String name = (String) tableModel.getValueAt(row, 2);
        String email = (String) tableModel.getValueAt(row, 3);
        String role = (String) tableModel.getValueAt(row, 4);
        String ref = (String) tableModel.getValueAt(row, 5);
        if ("—".equals(ref)) ref = "";

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Tài Khoản: " + username, true);
        dlg.setSize(440, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 16));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        JTextField txtTen = new JTextField(name);
        JTextField txtMail = new JTextField(email);
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"SINH_VIEN", "CO_VAN", "QUAN_LY", "ADMIN"});
        cbRole.setSelectedItem(role);
        JTextField txtRef = new JTextField(ref);

        form.add(new JLabel("Họ và tên (*):"));
        form.add(txtTen);
        form.add(new JLabel("Email:"));
        form.add(txtMail);
        form.add(new JLabel("Vai trò (*):"));
        form.add(cbRole);
        form.add(new JLabel("Mã Ref (MSSV / Mã CV):"));
        form.add(txtRef);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        JButton btnSave = UITheme.createButton("Lưu Thay Đổi", UITheme.PRIMARY, Color.WHITE);
        btnSave.addActionListener(e -> {
            String newName = txtTen.getText().trim();
            String newEmail = txtMail.getText().trim();
            String newRole = (String) cbRole.getSelectedItem();
            String newRef = txtRef.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Họ tên không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            TaiKhoan tk = new TaiKhoan(id, username, null, newName, newEmail, newRole, newRef.isEmpty() ? null : newRef, null);
            if (taiKhoanDAO.capNhat(tk)) {
                JOptionPane.showMessageDialog(dlg, "Cập nhật tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dlg, "Cập nhật tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = UITheme.createButton("Hủy", new Color(148, 163, 184), Color.WHITE);
        btnCancel.addActionListener(e -> dlg.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        dlg.add(btnPanel, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void showResetPassDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần đặt lại mật khẩu!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String name = (String) tableModel.getValueAt(row, 2);

        String newPass = JOptionPane.showInputDialog(
            this,
            "Nhập mật khẩu mới cho tài khoản [" + username + " - " + name + "]:",
            "123456"
        );

        if (newPass != null) {
            newPass = newPass.trim();
            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (taiKhoanDAO.resetMatKhau(id, newPass)) {
                JOptionPane.showMessageDialog(this, "Đã đặt lại mật khẩu thành công cho tài khoản [" + username + "]!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        if (currentUser != null && currentUser.getId() == id) {
            JOptionPane.showMessageDialog(this, "Bạn không thể tự xóa tài khoản đang đăng nhập của chính mình!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa tài khoản [" + username + "] không?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa tài khoản",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (taiKhoanDAO.xoa(id)) {
                JOptionPane.showMessageDialog(this, "Đã xóa tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
