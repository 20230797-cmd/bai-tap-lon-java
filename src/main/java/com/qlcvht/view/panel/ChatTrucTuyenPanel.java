package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.dao.ThongBaoDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.model.ThongBao;
import com.qlcvht.util.NotificationPopup;
import com.qlcvht.util.UITheme;
import com.qlcvht.websocket.ChatMessage;
import com.qlcvht.websocket.ChatWebSocketClient;
import com.qlcvht.websocket.WebSocketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatTrucTuyenPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();
    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    // UI Components
    private JComboBox<String> cbFilterLop;
    private JTextField txtSearchStudent;
    private DefaultListModel<SinhVien> listModelSinhVien;
    private JList<SinhVien> listSinhVien;

    private JLabel lblChatHeaderTitle;
    private JLabel lblChatHeaderSubtitle;
    private JLabel lblWsStatus;
    private JTextPane chatPane;
    private HTMLEditorKit htmlKit;
    private HTMLDocument htmlDoc;
    private JTextField txtMessageInput;
    private JButton btnSend;

    private SinhVien selectedStudent;
    private ChatWebSocketClient wsClient;
    private final List<ChatMessage> currentConversation = new ArrayList<>();

    public ChatTrucTuyenPanel(TaiKhoan currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        initHeader();
        initMainContent();
        initWebSocket();
        loadStudents();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("  GIAO TIẾP & CHAT TRỰC TUYẾN THỜI GIAN THỰC");
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(UITheme.PRIMARY_DARK);

        JLabel lblSub = new JLabel("Hệ thống nhắn tin 2 chiều Real-time qua WebSocket giữa Cố vấn học tập và Sinh viên");
        lblSub.setFont(UITheme.fontPlain(12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblSub);

        header.add(titleBox, BorderLayout.WEST);

        // WebSocket badge
        lblWsStatus = new JLabel("🟢 WebSocket: Đang kết nối...");
        lblWsStatus.setFont(UITheme.fontBold(12));
        lblWsStatus.setForeground(new Color(16, 185, 129));
        lblWsStatus.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(16, 185, 129), 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
        header.add(lblWsStatus, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void initMainContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftListPanel(), createRightChatPanel());
        splitPane.setDividerLocation(340);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createLeftListPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UITheme.BG_WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Filter box
        JPanel filterBox = new JPanel(new GridLayout(2, 1, 0, 6));
        filterBox.setOpaque(false);

        cbFilterLop = new JComboBox<>();
        cbFilterLop.addItem("--- Tất cả lớp phụ trách ---");
        String maCv = currentUser.getMaRef();
        List<LopHoc> lopList = (maCv != null && !maCv.isEmpty()) ? coVanDAO.getLopHocByCoVan(maCv) : coVanDAO.getAllLopHoc();
        for (LopHoc lh : lopList) {
            cbFilterLop.addItem(lh.getMaLop() + " - " + lh.getTenLop());
        }
        cbFilterLop.setFont(UITheme.fontPlain(12));
        cbFilterLop.addActionListener(e -> loadStudents());

        txtSearchStudent = new JTextField();
        txtSearchStudent.putClientProperty("JTextField.placeholderText", "Tìm tên hoặc MSSV...");
        txtSearchStudent.setFont(UITheme.fontPlain(12));
        txtSearchStudent.addActionListener(e -> loadStudents());

        filterBox.add(cbFilterLop);
        filterBox.add(txtSearchStudent);
        leftPanel.add(filterBox, BorderLayout.NORTH);

        // Student list
        listModelSinhVien = new DefaultListModel<>();
        listSinhVien = new JList<>(listModelSinhVien);
        listSinhVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSinhVien.setCellRenderer(new StudentChatCellRenderer());
        listSinhVien.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SinhVien sel = listSinhVien.getSelectedValue();
                if (sel != null && sel != selectedStudent) {
                    selectedStudent = sel;
                    onSelectStudent(sel);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(listSinhVien);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1));
        leftPanel.add(scroll, BorderLayout.CENTER);

        return leftPanel;
    }

    private JPanel createRightChatPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(UITheme.BG_WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        // Chat Header
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setOpaque(false);
        chatHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
            new EmptyBorder(0, 0, 8, 0)
        ));

        lblChatHeaderTitle = new JLabel("Chọn một sinh viên bên trái để bắt đầu cuộc trò chuyện");
        lblChatHeaderTitle.setFont(UITheme.fontBold(14));
        lblChatHeaderTitle.setForeground(UITheme.PRIMARY_DARK);

        lblChatHeaderSubtitle = new JLabel("Tin nhắn được mã hóa và đồng bộ thời gian thực");
        lblChatHeaderSubtitle.setFont(UITheme.fontPlain(11));
        lblChatHeaderSubtitle.setForeground(UITheme.TEXT_SECONDARY);

        JPanel headerText = new JPanel(new GridLayout(2, 1, 0, 2));
        headerText.setOpaque(false);
        headerText.add(lblChatHeaderTitle);
        headerText.add(lblChatHeaderSubtitle);

        chatHeader.add(headerText, BorderLayout.WEST);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // Chat messages pane (HTML based for rich bubbles)
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        htmlKit = new HTMLEditorKit();
        htmlDoc = new HTMLDocument();
        chatPane.setEditorKit(htmlKit);
        chatPane.setDocument(htmlDoc);
        chatPane.setBackground(new Color(248, 250, 252));

        JScrollPane chatScroll = new JScrollPane(chatPane);
        chatScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1));
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        // Chat Bottom (Quick actions + Input)
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 6));
        bottomContainer.setOpaque(false);

        // Quick template buttons
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        quickPanel.setOpaque(false);

        JButton btnQuick1 = createQuickBtn("⚠️ Cảnh báo vắng học");
        btnQuick1.addActionListener(e -> setQuickMessage("Chào em, thầy/cô nhận thấy em đã nghỉ 2 buổi học phần gần đây. Em chú ý đi học đầy đủ để tránh bị cấm thi nhé!"));

        JButton btnQuick2 = createQuickBtn("📅 Hẹn gặp tại VP Khoa");
        btnQuick2.addActionListener(e -> setQuickMessage("Chào em, mời em đến Văn phòng Khoa vào sáng thứ 4 tuần này lúc 9h00 để trao đổi về tiến độ học tập."));

        JButton btnQuick3 = createQuickBtn("🎓 Tiến độ 150 tín chỉ");
        btnQuick3.addActionListener(e -> setQuickMessage("Chào em, tiến độ tích lũy tín chỉ của em hiện tại đang chậm so với lộ trình 150 tín. Em cần đăng ký học trả nợ môn sớm."));

        quickPanel.add(new JLabel("Mẫu tin nhanh: "));
        quickPanel.add(btnQuick1);
        quickPanel.add(btnQuick2);
        quickPanel.add(btnQuick3);
        bottomContainer.add(quickPanel, BorderLayout.NORTH);

        // Input bar
        JPanel inputBar = new JPanel(new BorderLayout(8, 0));
        inputBar.setOpaque(false);

        txtMessageInput = new JTextField();
        txtMessageInput.setFont(UITheme.fontPlain(13));
        txtMessageInput.setPreferredSize(new Dimension(300, 38));
        txtMessageInput.putClientProperty("JTextField.placeholderText", "Nhập nội dung tin nhắn gửi sinh viên (Nhấn Enter để gửi)...");
        txtMessageInput.addActionListener(e -> sendMessage());

        btnSend = UITheme.createButton("  GỬI TIN NHẮN  ", UITheme.PRIMARY, Color.WHITE);
        btnSend.setFont(UITheme.fontBold(12));
        btnSend.addActionListener(e -> sendMessage());

        inputBar.add(txtMessageInput, BorderLayout.CENTER);
        inputBar.add(btnSend, BorderLayout.EAST);
        bottomContainer.add(inputBar, BorderLayout.SOUTH);

        rightPanel.add(bottomContainer, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JButton createQuickBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.fontPlain(11));
        btn.setBackground(new Color(241, 245, 249));
        btn.setForeground(new Color(51, 65, 85));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(3, 8, 3, 8)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setQuickMessage(String msg) {
        txtMessageInput.setText(msg);
        txtMessageInput.requestFocus();
    }

    private void loadStudents() {
        String selLop = (String) cbFilterLop.getSelectedItem();
        String maLop = null;
        if (selLop != null && !selLop.startsWith("---")) {
            maLop = selLop.split(" - ")[0].trim();
        }
        String kw = txtSearchStudent.getText().trim();
        String maCv = currentUser.getMaRef();

        List<SinhVien> list;
        if (maLop != null) {
            list = sinhVienDAO.getSinhVienByLop(maLop);
        } else if (maCv != null && !maCv.isEmpty()) {
            list = sinhVienDAO.getSinhVienByCoVan(maCv);
        } else {
            list = sinhVienDAO.getAllSinhVien();
        }

        listModelSinhVien.clear();
        for (SinhVien sv : list) {
            if (kw.isEmpty() || sv.getMaSv().toLowerCase().contains(kw.toLowerCase()) || sv.getHoTen().toLowerCase().contains(kw.toLowerCase())) {
                listModelSinhVien.addElement(sv);
            }
        }

        if (!listModelSinhVien.isEmpty() && selectedStudent == null) {
            listSinhVien.setSelectedIndex(0);
        }
    }

    private void onSelectStudent(SinhVien sv) {
        lblChatHeaderTitle.setText("💬 Cuộc trò chuyện với: " + sv.getHoTen() + " (MSSV: " + sv.getMaSv() + ")");
        lblChatHeaderSubtitle.setText("Lớp: " + sv.getMaLop() + " | Tiến độ: " + sv.getTongTinChiTichLuy() + "/150 Tín chỉ (Năm " + sv.getNamThu() + ") | Trạng thái: " + sv.getTrangThaiHienThi());
        loadConversation(sv.getMaSv());
    }

    private void loadConversation(String maSv) {
        currentConversation.clear();
        String advisorId = (currentUser.getMaRef() != null && !currentUser.getMaRef().isEmpty()) ? currentUser.getMaRef() : currentUser.getTenDangNhap();

        // Lấy lịch sử từ bảng thong_bao
        List<ThongBao> list = thongBaoDAO.getChatHistory(maSv);
        for (ThongBao tb : list) {
            ChatMessage msg = new ChatMessage(
                tb.getNguoiGui(),
                tb.getNguoiGui(),
                "CO_VAN",
                maSv,
                tb.getTieuDe(),
                tb.getNoiDung()
            );
            msg.setTimestamp(tb.getNgayGui());
            currentConversation.add(msg);
        }

        renderChatHtml();
    }

    private void renderChatHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body { font-family: 'Segoe UI', Tahoma, sans-serif; padding: 10px; background-color: #f8fafc; }")
            .append(".bubble-cv { background-color: #dbeafe; color: #1e3a8a; padding: 8px 12px; border-radius: 12px 12px 2px 12px; margin-bottom: 8px; margin-left: 50px; }")
            .append(".bubble-sv { background-color: #ffffff; color: #1e293b; padding: 8px 12px; border-radius: 12px 12px 12px 2px; margin-bottom: 8px; margin-right: 50px; border: 1px solid #e2e8f0; }")
            .append(".time { font-size: 9px; color: #64748b; margin-top: 4px; text-align: right; }")
            .append(".sender { font-weight: bold; font-size: 11px; margin-bottom: 2px; }")
            .append("</style></head><body>");

        if (currentConversation.isEmpty()) {
            html.append("<div style='text-align:center; color:#94a3b8; margin-top:40px;'>")
                .append("<p style='font-size:14px; font-weight:bold;'>Chưa có tin nhắn nào</p>")
                .append("<p style='font-size:11px;'>Hãy gửi tin nhắn đầu tiên để bắt đầu hỗ trợ sinh viên này.</p>")
                .append("</div>");
        } else {
            for (ChatMessage msg : currentConversation) {
                boolean isFromAdvisor = "CO_VAN".equalsIgnoreCase(msg.getFromRole()) || currentUser.getTenDangNhap().equalsIgnoreCase(msg.getFromId());
                String bubbleClass = isFromAdvisor ? "bubble-cv" : "bubble-sv";
                String senderName = isFromAdvisor ? ("Cố vấn: " + currentUser.getHoTen()) : ("Sinh viên: " + (selectedStudent != null ? selectedStudent.getHoTen() : msg.getFromId()));

                html.append("<div class='").append(bubbleClass).append("'>")
                    .append("<div class='sender'>").append(senderName).append("</div>")
                    .append("<div style='font-size:12px; line-height:1.4;'>").append(msg.getContent().replace("\n", "<br/>")).append("</div>")
                    .append("<div class='time'>").append(msg.getTimestamp() != null ? msg.getTimestamp() : "").append("</div>")
                    .append("</div>");
            }
        }

        html.append("</body></html>");
        chatPane.setText(html.toString());
        chatPane.setCaretPosition(chatPane.getDocument().getLength());
    }

    private void sendMessage() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên để gửi tin nhắn!", "Chưa chọn sinh viên", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String content = txtMessageInput.getText().trim();
        if (content.isEmpty()) return;

        String advisorId = (currentUser.getMaRef() != null && !currentUser.getMaRef().isEmpty()) ? currentUser.getMaRef() : currentUser.getTenDangNhap();
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ChatMessage msg = new ChatMessage(
            advisorId,
            currentUser.getHoTen(),
            "CO_VAN",
            selectedStudent.getMaSv(),
            "Tin nhắn từ Cố Vấn Học Tập",
            content
        );
        msg.setTimestamp(timeNow);

        // Gửi qua WebSocket
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.sendChatMessage(msg);
        }

        // Lưu vào CSDL
        ThongBao tb = new ThongBao();
        tb.setMaThongBao("CHAT-" + System.currentTimeMillis() % 100000);
        tb.setTieuDe("💬 Tin nhắn từ CVHT: " + currentUser.getHoTen());
        tb.setNoiDung(content);
        tb.setNhomRuiRo("ALL");
        tb.setMaLop(selectedStudent.getMaLop());
        tb.setMaSv(selectedStudent.getMaSv());
        tb.setNgayGui(timeNow);
        tb.setNguoiGui(currentUser.getHoTen());
        tb.setSoLuongNhan(1);
        tb.setTrangThai("DA_GUI");
        thongBaoDAO.addThongBao(tb);

        currentConversation.add(msg);
        renderChatHtml();
        txtMessageInput.setText("");
    }

    private void initWebSocket() {
        try {
            WebSocketService.getInstance().startServer();
            String userId = (currentUser.getMaRef() != null && !currentUser.getMaRef().isEmpty()) ? currentUser.getMaRef() : currentUser.getTenDangNhap();
            wsClient = WebSocketService.getInstance().getClient(userId);

            wsClient.addListener(new ChatWebSocketClient.MessageListener() {
                @Override
                public void onMessageReceived(ChatMessage message) {
                    SwingUtilities.invokeLater(() -> {
                        if (selectedStudent != null && (selectedStudent.getMaSv().equalsIgnoreCase(message.getFromId()) || selectedStudent.getMaSv().equalsIgnoreCase(message.getToId()))) {
                            currentConversation.add(message);
                            renderChatHtml();
                        } else {
                            NotificationPopup.showInfo("Tin nhắn mới từ sinh viên: " + message.getFromName(), message.getContent());
                        }
                    });
                }

                @Override
                public void onStatusChanged(boolean connected, String statusText) {
                    SwingUtilities.invokeLater(() -> {
                        if (lblWsStatus != null) {
                            lblWsStatus.setText(connected ? "🟢 WebSocket Online (Real-time)" : "🔴 WebSocket Offline");
                            lblWsStatus.setForeground(connected ? new Color(16, 185, 129) : new Color(239, 68, 68));
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Custom Cell Renderer for Student Chat List
    private static class StudentChatCellRenderer extends JPanel implements ListCellRenderer<SinhVien> {
        private final JLabel lblAvatar = new JLabel("🎓", SwingConstants.CENTER);
        private final JLabel lblName = new JLabel();
        private final JLabel lblSub = new JLabel();
        private final JLabel lblBadge = new JLabel();

        public StudentChatCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setOpaque(true);

            lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            lblAvatar.setPreferredSize(new Dimension(36, 36));

            JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
            textPanel.setOpaque(false);
            lblName.setFont(UITheme.fontBold(12));
            lblSub.setFont(UITheme.fontPlain(11));
            lblSub.setForeground(UITheme.TEXT_SECONDARY);
            textPanel.add(lblName);
            textPanel.add(lblSub);

            lblBadge.setFont(UITheme.fontBold(10));
            lblBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

            add(lblAvatar, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
            add(lblBadge, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SinhVien> list, SinhVien sv, int index, boolean isSelected, boolean cellHasFocus) {
            if (sv == null) return this;

            lblName.setText(sv.getHoTen());
            lblSub.setText(sv.getMaSv() + " | " + sv.getTongTinChiTichLuy() + "/150 TC (Năm " + sv.getNamThu() + ")");

            if ("CANH_BAO_2".equals(sv.getTrangThai()) || "BUOC_THOI_HOC".equals(sv.getTrangThai())) {
                lblBadge.setText("⚠️ Mức 2");
                lblBadge.setForeground(new Color(220, 38, 38));
            } else if ("CANH_BAO_1".equals(sv.getTrangThai())) {
                lblBadge.setText("⚠️ Mức 1");
                lblBadge.setForeground(new Color(217, 119, 6));
            } else {
                lblBadge.setText("OK");
                lblBadge.setForeground(new Color(16, 185, 129));
            }

            if (isSelected) {
                setBackground(new Color(219, 234, 254));
                lblName.setForeground(UITheme.PRIMARY_DARK);
            } else {
                setBackground(index % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                lblName.setForeground(UITheme.TEXT_PRIMARY);
            }

            return this;
        }
    }
}
