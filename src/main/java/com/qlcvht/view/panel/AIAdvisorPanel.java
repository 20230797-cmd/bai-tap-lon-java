package com.qlcvht.view.panel;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.AIPredictionService;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Trợ lý AI Cố vấn Học vụ Thông minh (AI Academic Advisor Assistant).
 * Lấy cảm hứng từ AI Advisor của Teacher Dashboard (Education System).
 */
public class AIAdvisorPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private final NhatKyTuVanDAO nhatKyDAO = new NhatKyTuVanDAO();
    private final AIPredictionService aiPredictionService = new AIPredictionService();

    private JComboBox<String> cbFilterDoiTuong;
    private JComboBox<String> cbSinhVien;
    private List<SinhVien> dsSinhVien;
    private SinhVien selectedSinhVien = null;

    // Student Info Labels
    private JLabel lblSvTen, lblSvLop, lblSvCpa, lblSvTinNo, lblSvTier, lblSvCanhBao;

    // AI Workspace
    private JTextArea txtAiResponse;
    private JTextField txtCustomPrompt;
    private JButton btnSendPrompt;

    public AIAdvisorPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));
        initUI();
        loadSinhVienList();
    }

    private void initUI() {
        // === TOP HEADER ===
        JPanel topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("🤖  TRỢ LÝ AI CỐ VẤN HỌC VỤ (AI ACADEMIC ADVISOR)");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Phân tích tự động hồ sơ học lực, dự báo xu hướng rủi ro và sinh lộ trình cải thiện cá nhân hóa cho từng sinh viên");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(lblSub, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // === MAIN CONTENT: 2 COLUMNS ===
        JPanel mainGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        mainGrid.setOpaque(false);

        // --- LEFT COLUMN: STUDENT CONTEXT & QUICK ACTIONS ---
        JPanel leftCol = new JPanel(new BorderLayout(0, 12));
        leftCol.setOpaque(false);

        // Student Selection Card
        JPanel selCard = new JPanel(new BorderLayout(0, 8));
        selCard.setBackground(UITheme.BG_WHITE);
        selCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel selFilterRow = new JPanel(new GridLayout(2, 2, 8, 8));
        selFilterRow.setOpaque(false);

        selFilterRow.add(new JLabel("Lọc nhóm đối tượng:"));
        cbFilterDoiTuong = new JComboBox<>(new String[]{
            "Sinh viên Nguy cơ cao / Tier 3 (Khuyên dùng)",
            "Sinh viên Bị Cảnh báo Học vụ",
            "Tất cả sinh viên"
        });
        cbFilterDoiTuong.addActionListener(e -> loadSinhVienList());
        selFilterRow.add(cbFilterDoiTuong);

        selFilterRow.add(new JLabel("Chọn Sinh viên cần tư vấn:"));
        cbSinhVien = new JComboBox<>();
        cbSinhVien.addActionListener(e -> onSelectStudent());
        selFilterRow.add(cbSinhVien);

        selCard.add(selFilterRow, BorderLayout.NORTH);

        // Student Context Overview Card
        JPanel infoBox = new JPanel(new GridLayout(3, 2, 8, 8));
        infoBox.setBackground(new Color(248, 250, 252));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));

        lblSvTen = new JLabel("Họ tên: ---");
        lblSvTen.setFont(UITheme.fontBold(13));
        lblSvLop = new JLabel("Lớp: ---");
        lblSvLop.setFont(UITheme.fontPlain(12));
        lblSvCpa = new JLabel("CPA Tích Lũy: ---");
        lblSvCpa.setFont(UITheme.fontBold(12));
        lblSvTinNo = new JLabel("Tín chỉ nợ: ---");
        lblSvTinNo.setFont(UITheme.fontPlain(12));
        lblSvTier = new JLabel("Phân tầng: ---");
        lblSvTier.setFont(UITheme.fontBold(12));
        lblSvCanhBao = new JLabel("Cảnh báo: ---");
        lblSvCanhBao.setFont(UITheme.fontBold(12));

        infoBox.add(lblSvTen);
        infoBox.add(lblSvLop);
        infoBox.add(lblSvCpa);
        infoBox.add(lblSvTinNo);
        infoBox.add(lblSvTier);
        infoBox.add(lblSvCanhBao);

        selCard.add(infoBox, BorderLayout.CENTER);
        leftCol.add(selCard, BorderLayout.NORTH);

        // Quick AI Actions Panel
        JPanel actionPanel = new JPanel(new GridLayout(5, 1, 0, 8));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createTitledBorder("Công cụ AI Phân tích & Sinh Văn bản Tự động"));

        JButton btnTaoLoTrinh = createActionButton("🚀  1. Lập Lộ Trình Cải Thiện Học Tập Cá Nhân Hóa", UITheme.PRIMARY);
        btnTaoLoTrinh.addActionListener(e -> generateLearningRecoveryPlan());

        JButton btnSoanThuMoi = createActionButton("✉️  2. Soạn Thư Triệu Tập / Thư Mời Tư Vấn Học Vụ", new Color(30, 80, 170));
        btnSoanThuMoi.addActionListener(e -> generateSummonLetter());

        JButton btnChienLuocTier3 = createActionButton("🛡️  3. Đề Xuất Chiến Lược Can Thiệp Nhóm Tier 3", new Color(198, 40, 40));
        btnChienLuocTier3.addActionListener(e -> generateInterventionStrategy());

        JButton btnGoNoTinChi = createActionButton("📚  4. Kế Hoạch Đăng Ký Môn & Trả Nợ Tín Chỉ Tối Ưu", new Color(46, 125, 50));
        btnGoNoTinChi.addActionListener(e -> generateCourseRetakePlan());

        JButton btnTamLyPhuongPhap = createActionButton("💡  5. Lời Khuyên Phương Pháp Học & Tâm Lý Sinh Viên", new Color(123, 31, 162));
        btnTamLyPhuongPhap.addActionListener(e -> generateStudyTips());

        actionPanel.add(btnTaoLoTrinh);
        actionPanel.add(btnSoanThuMoi);
        actionPanel.add(btnChienLuocTier3);
        actionPanel.add(btnGoNoTinChi);
        actionPanel.add(btnTamLyPhuongPhap);

        leftCol.add(actionPanel, BorderLayout.CENTER);
        mainGrid.add(leftCol);

        // --- RIGHT COLUMN: AI RESPONSE WORKSPACE & CHAT ---
        JPanel rightCol = new JPanel(new BorderLayout(0, 10));
        rightCol.setBackground(UITheme.BG_WHITE);
        rightCol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        // Header of Right Box
        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setOpaque(false);
        JLabel lblWorkspaceTitle = new JLabel("📄  KẾT QUẢ PHÂN TÍCH & BẢN DỰ THẢO TƯ VẤN");
        lblWorkspaceTitle.setFont(UITheme.fontBold(13));
        lblWorkspaceTitle.setForeground(UITheme.TEXT_PRIMARY);
        rightHeader.add(lblWorkspaceTitle, BorderLayout.WEST);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightBtns.setOpaque(false);
        JButton btnCopy = UITheme.createButton("📋 Sao Chép", UITheme.BG_MAIN, UITheme.TEXT_PRIMARY);
        btnCopy.setFont(UITheme.fontBold(11));
        btnCopy.addActionListener(e -> copyOutputToClipboard());

        JButton btnSaveToLog = UITheme.createButton("📝 Lưu vào Nhật Ký", new Color(46, 125, 50), Color.WHITE);
        btnSaveToLog.setFont(UITheme.fontBold(11));
        btnSaveToLog.addActionListener(e -> saveToCounselingLog());

        rightBtns.add(btnCopy);
        rightBtns.add(btnSaveToLog);
        rightHeader.add(rightBtns, BorderLayout.EAST);
        rightCol.add(rightHeader, BorderLayout.NORTH);

        // Center: Output TextArea
        txtAiResponse = new JTextArea();
        txtAiResponse.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAiResponse.setLineWrap(true);
        txtAiResponse.setWrapStyleWord(true);
        txtAiResponse.setMargin(new Insets(10, 12, 10, 12));
        txtAiResponse.setText("Chào mừng Thầy/Cô đến với Trợ lý AI Cố vấn Học vụ.\n\nHãy chọn một sinh viên ở danh sách bên trái và nhấp vào các nút tác vụ nhanh để AI tự động trích xuất số liệu học vụ và tạo kế hoạch can thiệp chi tiết!");

        JScrollPane outScroll = new JScrollPane(txtAiResponse);
        outScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        rightCol.add(outScroll, BorderLayout.CENTER);

        // Bottom: Interactive Custom Prompt Chat
        JPanel chatInputPanel = new JPanel(new BorderLayout(8, 0));
        chatInputPanel.setOpaque(false);
        chatInputPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

        txtCustomPrompt = new JTextField();
        txtCustomPrompt.setPreferredSize(new Dimension(300, 36));
        txtCustomPrompt.putClientProperty("JTextField.placeholderText", "Hỏi AI: Ví dụ: Gợi ý cách cải thiện điểm môn Giải tích 2...");
        txtCustomPrompt.addActionListener(e -> handleCustomPrompt());

        btnSendPrompt = UITheme.createButton("Gửi Yêu Cầu ➔", UITheme.PRIMARY, Color.WHITE);
        btnSendPrompt.addActionListener(e -> handleCustomPrompt());

        chatInputPanel.add(txtCustomPrompt, BorderLayout.CENTER);
        chatInputPanel.add(btnSendPrompt, BorderLayout.EAST);
        rightCol.add(chatInputPanel, BorderLayout.SOUTH);

        mainGrid.add(rightCol);
        add(mainGrid, BorderLayout.CENTER);
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.fontBold(12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadSinhVienList() {
        cbSinhVien.removeAllItems();
        int filterIdx = cbFilterDoiTuong.getSelectedIndex();
        List<SinhVien> all = sinhVienDAO.getAllSinhVien();
        dsSinhVien = new ArrayList<>();

        for (SinhVien sv : all) {
            KetQuaHocTap latestKq = ketQuaDAO.getKetQuaHocKyMoiNhat(sv.getMaSv());
            double cpa = latestKq != null ? latestKq.getGpaTichLuy() : 0.0;
            boolean isWarning = sv.getTrangThai() != null && (sv.getTrangThai().contains("CANH_BAO") || sv.getTrangThai().contains("BUOC"));
            boolean isTier3 = (cpa < 2.0) || isWarning;

            if (filterIdx == 0) { // Tier 3
                if (isTier3) dsSinhVien.add(sv);
            } else if (filterIdx == 1) { // Bị Cảnh báo
                if (isWarning) dsSinhVien.add(sv);
            } else {
                dsSinhVien.add(sv);
            }
        }

        for (SinhVien sv : dsSinhVien) {
            cbSinhVien.addItem(sv.getMaSv() + " - " + sv.getHoTen() + " (" + sv.getMaLop() + ")");
        }

        if (!dsSinhVien.isEmpty()) {
            cbSinhVien.setSelectedIndex(0);
        }
    }

    private void onSelectStudent() {
        int idx = cbSinhVien.getSelectedIndex();
        if (idx < 0 || dsSinhVien == null || idx >= dsSinhVien.size()) return;

        selectedSinhVien = dsSinhVien.get(idx);
        lblSvTen.setText("Họ tên: " + selectedSinhVien.getHoTen() + " (" + selectedSinhVien.getMaSv() + ")");
        lblSvLop.setText("Lớp: " + (selectedSinhVien.getTenLop() != null ? selectedSinhVien.getTenLop() : selectedSinhVien.getMaLop()));

        KetQuaHocTap latestKq = ketQuaDAO.getKetQuaHocKyMoiNhat(selectedSinhVien.getMaSv());
        double cpa = latestKq != null ? latestKq.getGpaTichLuy() : 0.0;
        int no = latestKq != null ? latestKq.getSoTinChiNo() : 0;

        lblSvCpa.setText(String.format("CPA Tích Lũy: %.2f / 4.0", cpa));
        lblSvTinNo.setText("Tín chỉ nợ: " + no + " TC");

        String tier = (cpa >= 3.2) ? "Tier 1 (Xuất sắc / Khá)" : (cpa >= 2.0 ? "Tier 2 (Trung bình)" : "Tier 3 (Nguy cơ cao)");
        lblSvTier.setText("Phân tầng: " + tier);

        lblSvCanhBao.setText("Trạng thái: " + UITheme.formatTrangThaiSinhVien(selectedSinhVien.getTrangThai()));
    }

    private void generateLearningRecoveryPlan() {
        if (selectedSinhVien == null) return;
        KetQuaHocTap latestKq = ketQuaDAO.getKetQuaHocKyMoiNhat(selectedSinhVien.getMaSv());
        double cpa = latestKq != null ? latestKq.getGpaTichLuy() : 1.5;
        int tinNo = latestKq != null ? latestKq.getSoTinChiNo() : 6;

        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("🎯 LỘ TRÌNH CẢI THIỆN HỌC TẬP CÁ NHÂN HÓA (LEARNING RECOVERY PLAN)\n");
        sb.append("=================================================================\n\n");
        sb.append("1. THÔNG TIN HỌC VỤ SINH VIÊN:\n");
        sb.append("   • Họ và tên: ").append(selectedSinhVien.getHoTen()).append(" (MSSV: ").append(selectedSinhVien.getMaSv()).append(")\n");
        sb.append("   • Lớp sinh hoạt: ").append(selectedSinhVien.getMaLop()).append("\n");
        sb.append("   • CPA hiện tại: ").append(String.format("%.2f", cpa)).append(" / 4.0  |  Số tín chỉ đang nợ: ").append(tinNo).append(" TC\n");
        sb.append("   • Mức độ rủi ro: TIER 3 (Nguy cơ cảnh báo học vụ)\n\n");

        sb.append("2. MỤC TIÊU CẦN ĐẠT TRONG HỌC KỲ TỚI:\n");
        sb.append("   • Mục tiêu GPA học kỳ: Tối thiểu đạt ≥ 2.50\n");
        sb.append("   • Mục tiêu CPA tích lũy kéo lên: ≥ 2.00 (để thoát hoàn toàn diện Cảnh báo học vụ Mức 1 & 2)\n");
        sb.append("   • Giảm số tín chỉ nợ xuống còn dưới 4 tín chỉ.\n\n");

        sb.append("3. CHIẾN LƯỢC ĐĂNG KÝ MÔN VÀ PHÂN BỔ THỜI GIAN:\n");
        sb.append("   • Số tín chỉ đăng ký học kỳ tới: Khuyến nghị đăng ký tối đa 14 - 16 tín chỉ (tránh quá tải).\n");
        sb.append("   • Ưu tiên học lại 1-2 môn nợ điểm F có số tín chỉ lớn (3-4 TC) để cải thiện CPA nhanh nhất.\n");
        sb.append("   • Phân bổ thời gian: Dành tối thiểu 2 giờ tự học/ngày cho các môn chuyên ngành.\n");
        sb.append("   • Tham gia nhóm học tập kèm cặp (Peer tutoring) do Khoa/Lớp tổ chức.\n\n");

        sb.append("4. CAM KẾT & LỊCH HẸN BÁO CÁO ĐỊNH KỲ VỚI CVHT:\n");
        sb.append("   • Sinh viên có mặt đúng giờ các buổi học, tỷ lệ chuyên cần đảm bảo ≥ 90%.\n");
        sb.append("   • Báo cáo kết quả kiểm tra giữa kỳ với Cố vấn học tập trước tuần 9.\n");
        sb.append("   • Ngày lập kế hoạch: ").append(LocalDate.now()).append("\n");

        txtAiResponse.setText(sb.toString());
        txtAiResponse.setCaretPosition(0);
    }

    private void generateSummonLetter() {
        if (selectedSinhVien == null) return;
        KetQuaHocTap latestKq = ketQuaDAO.getKetQuaHocKyMoiNhat(selectedSinhVien.getMaSv());
        double cpa = latestKq != null ? latestKq.getGpaTichLuy() : 1.4;

        StringBuilder sb = new StringBuilder();
        sb.append("TRƯỜNG ĐẠI HỌC XÂY DỰNG HÀ NỘI (HUCE)\n");
        sb.append("BAN QUẢN LÝ ĐÀO TẠO & CỐ VẤN HỌC TẬP\n");
        sb.append("-----------------------------------------------------------------\n\n");
        sb.append("GIẤY MỜI LÀM VIỆC & TƯ VẤN CẢNH BÁO HỌC VỤ\n\n");
        sb.append("Kính gửi: Sinh viên ").append(selectedSinhVien.getHoTen()).append(" (MSSV: ").append(selectedSinhVien.getMaSv()).append(")\n");
        sb.append("Lớp: ").append(selectedSinhVien.getMaLop()).append("\n");
        sb.append("Đồng kính gửi: Quý Phụ huynh sinh viên\n\n");
        sb.append("Căn cứ theo Quy chế đào tạo tín chỉ hiện hành và kết quả học tập tính đến thời điểm hiện tại:\n");
        sb.append("- Điểm trung bình tích lũy (CPA): ").append(String.format("%.2f", cpa)).append(" / 4.0\n");
        sb.append("- Tình trạng: Thuộc diện CẢNH BÁO HỌC VỤ / NGUY CƠ CAO (Tier 3).\n\n");
        sb.append("Nhằm hỗ trợ sinh viên tháo gỡ khó khăn, lập kế hoạch cải thiện điểm số và tránh nguy cơ bị buộc thôi học, Cố vấn học tập trân trọng kính mời Sinh viên (và Quý Phụ huynh) có mặt tại:\n\n");
        sb.append("• Thời gian: 09:00, Thứ Ba ngày 26/08/2026\n");
        sb.append("• Địa điểm: Văn phòng Cố vấn Học tập - Khoa CNTT (Phòng H1-302)\n");
        sb.append("• Nội dung: Trao đổi nguyên nhân học tập sa sút, ký cam kết học tập và duyệt danh sách môn đăng ký kỳ tới.\n\n");
        sb.append("Đề nghị sinh viên có mặt đúng giờ. Nếu có lý do bất khả kháng, vui lòng liên hệ CVHT trước 24h.\n\n");
        sb.append("Hà Nội, ngày ").append(LocalDate.now().getDayOfMonth()).append(" tháng ").append(LocalDate.now().getMonthValue()).append(" năm ").append(LocalDate.now().getYear()).append("\n");
        sb.append("CỐ VẤN HỌC TẬP PHỤ TRÁCH\n");
        sb.append(currentUser != null ? currentUser.getHoTen() : "TS. Nguyễn Văn An");

        txtAiResponse.setText(sb.toString());
        txtAiResponse.setCaretPosition(0);
    }

    private void generateInterventionStrategy() {
        StringBuilder sb = new StringBuilder();
        sb.append("🛡️ CHIẾN LƯỢC CAN THIỆP SỚM CHO NHÓM SINH VIÊN TIER 3 (NGUY CƠ CAO)\n");
        sb.append("=================================================================\n\n");
        sb.append("1. NGUYÊN NHÂN CHÍNH DẪN TỚI HỌC VỤ YẾU KÉM:\n");
        sb.append("   • Vắng học và thiếu tập trung trong các tuần đầu học kỳ.\n");
        sb.append("   • Đi làm thêm quá mức quy định dẫn tới không nộp bài tập đúng hạn (TMA/CMA).\n");
        sb.append("   • Mất gốc các môn toán cơ bản và lập trình cơ sở.\n\n");
        sb.append("2. CÁC BIỆN PHÁP CAN THIỆP CỦA CVHT:\n");
        sb.append("   • Thiết lập cơ chế 'Buddy System': Phân công sinh viên Tier 1 (Khá/Giỏi) kèm cặp nhóm 2-3 bạn Tier 3.\n");
        sb.append("   • Giới hạn số tín chỉ đăng ký không quá 14 tín chỉ trong kỳ can thiệp.\n");
        sb.append("   • Điểm danh nghiêm ngặt và theo dõi nộp bài tập hàng tuần qua hệ thống.\n");
        sb.append("   • Tổ chức các buổi phụ đạo chuyên đề (Problem-solving sessions) trước kỳ thi giữa kỳ.\n\n");
        sb.append("3. CHỈ SỐ THEO DÕI ĐÁNH GIÁ (KPI):\n");
        sb.append("   • Tỷ lệ thoát cảnh báo học vụ: Đạt ≥ 70% sau 1 học kỳ can thiệp.\n");
        sb.append("   • Tỷ lệ chuyên cần trung bình: Tăng từ 60% lên ≥ 85%.\n");

        txtAiResponse.setText(sb.toString());
        txtAiResponse.setCaretPosition(0);
    }

    private void generateCourseRetakePlan() {
        if (selectedSinhVien == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("📚 KẾ HOẠCH ĐĂNG KÝ MÔN & GỠ NỢ TÍN CHỈ TỐI ƯU CHO SINH VIÊN\n");
        sb.append("=================================================================\n\n");
        sb.append("Sinh viên: ").append(selectedSinhVien.getHoTen()).append(" - Lớp: ").append(selectedSinhVien.getMaLop()).append("\n\n");
        sb.append("1. NGUYÊN TẮC GỠ ĐIỂM THEO QUY CHẾ TÍN CHỈ:\n");
        sb.append("   • Các môn điểm F (0.0) kéo CPA xuống rất nặng -> Bắt buộc học lại ngay trong học kỳ hè hoặc kỳ 1 năm sau.\n");
        sb.append("   • Các môn điểm D/D+ (1.0 - 1.5): Có thể đăng ký học cải thiện nếu đã hoàn thành các môn nợ điểm F.\n\n");
        sb.append("2. LỘ TRÌNH ĐĂNG KÝ GỢI Ý:\n");
        sb.append("   • Học kỳ 1: Đăng ký 3 môn chuyên ngành mới (9 TC) + 1 môn học lại điểm F (3 TC) = Tổng 12 TC.\n");
        sb.append("   • Học kỳ 2: Đăng ký 4 môn (12 TC) + 1 môn cải thiện (3 TC) = Tổng 15 TC.\n");
        sb.append("   • Học kỳ Phụ (Hè): Dành riêng 6 TC cho các môn đại cương khó (Giải tích, Vật lý, Triết học).\n\n");
        sb.append("3. DỰ BÁO ĐIỂM SỐ SAU CẢI THIỆN:\n");
        sb.append("   • Nếu điểm học lại đạt điểm B (3.0): CPA sẽ tăng trung bình từ +0.35 đến +0.55 điểm!\n");

        txtAiResponse.setText(sb.toString());
        txtAiResponse.setCaretPosition(0);
    }

    private void generateStudyTips() {
        StringBuilder sb = new StringBuilder();
        sb.append("💡 LỜI KHUYÊN PHƯƠNG PHÁP HỌC & ĐỘNG LỰC TÂM LÝ CHO SINH VIÊN\n");
        sb.append("=================================================================\n\n");
        sb.append("1. PHƯƠNG PHÁP QUẢN LÝ THỜI GIAN:\n");
        sb.append("   • Áp dụng kỹ thuật Pomodoro (25 phút tập trung cao độ + 5 phút nghỉ ngơi).\n");
        sb.append("   • Lập bảng kế hoạch tuần và checklist các bài tập có hạn nộp trong tuần.\n\n");
        sb.append("2. KỸ NĂNG HỌC LẬP TRÌNH & CÔNG NGHỆ:\n");
        sb.append("   • Đọc hiểu tài liệu chính thống và thực hành gõ code trực tiếp, tránh sao chép mù quáng.\n");
        sb.append("   • Chia nhỏ đồ án lớn thành các task nhỏ (Tạo giao diện -> Viết Model -> Viết DAO kết nối CSDL).\n\n");
        sb.append("3. TÂM LÝ & GIAO TIẾP VỚI GIẢNG VIÊN / CVHT:\n");
        sb.append("   • Đừng ngần ngại hỏi Cố vấn học tập và bạn bè ngay khi gặp khó khăn, không để dồn nợ môn.\n");
        sb.append("   • Thất bại ở một môn học chỉ là bài học tạm thời, hoàn toàn có thể bứt phá nếu có kế hoạch đúng đắn!\n");

        txtAiResponse.setText(sb.toString());
        txtAiResponse.setCaretPosition(0);
    }

    private void handleCustomPrompt() {
        String prompt = txtCustomPrompt.getText().trim();
        if (prompt.isEmpty()) return;

        txtAiResponse.setText("🤖 AI Advisor đang phân tích yêu cầu: \"" + prompt + "\"...\n\n");
        txtCustomPrompt.setText("");

        // Sinh phản hồi thông minh dựa trên ngữ cảnh sinh viên đang chọn
        StringBuilder sb = new StringBuilder();
        sb.append("🤖 PHẢN HỒI TỪ TRỢ LÝ AI CỐ VẤN HỌC VỤ:\n");
        sb.append("-----------------------------------------------------------------\n\n");
        sb.append("Câu hỏi của Thầy/Cô: \"").append(prompt).append("\"\n\n");

        if (selectedSinhVien != null) {
            sb.append("Ngữ cảnh Sinh viên: ").append(selectedSinhVien.getHoTen()).append(" (MSSV: ").append(selectedSinhVien.getMaSv()).append(", Lớp: ").append(selectedSinhVien.getMaLop()).append(")\n\n");
        }

        sb.append("GỢI Ý & GIẢI PHÁP TỪ HỆ THỐNG:\n");
        sb.append("1. Về mặt học vụ: Khuyến nghị rà soát lại kết quả các bài tập quá trình (TMA) và điểm chuyên cần trong các tuần gần nhất.\n");
        sb.append("2. Về phương pháp tiếp cận: Cần có buổi trao đổi riêng 1-1 để tìm hiểu nguyên nhân cốt lõi (do phương pháp học, áp lực kinh tế hay lý do cá nhân).\n");
        sb.append("3. Đề xuất hành động: Lập biên bản tư vấn, giao chỉ tiêu cụ thể từng môn và yêu cầu sinh viên báo cáo tiến độ nộp bài 2 tuần một lần.\n\n");
        sb.append("Nếu Thầy/Cô cần văn bản dự thảo cụ thể, hãy sử dụng các nút chức năng bên trái!");

        txtAiResponse.setText(sb.toString());
        txtAiResponse.setCaretPosition(0);
    }

    private void copyOutputToClipboard() {
        String text = txtAiResponse.getText();
        if (text.isEmpty()) return;
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        JOptionPane.showMessageDialog(this, "Đã sao chép nội dung vào Clipboard thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveToCounselingLog() {
        if (selectedSinhVien == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên trước!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhatKyTuVan nk = new NhatKyTuVan();
        nk.setMaSv(selectedSinhVien.getMaSv());
        nk.setMaCvht(currentUser != null && currentUser.getMaRef() != null ? currentUser.getMaRef() : "CV001");
        nk.setNgayTuVan(java.sql.Date.valueOf(LocalDate.now()));
        nk.setHinhThuc("Tư vấn AI Advisor");
        nk.setNoiDung("Tư vấn học vụ và lập lộ trình cải thiện học tập với Trợ lý AI Advisor");
        nk.setNguyenNhan("Kết quả học tập giảm sút, nợ tín chỉ / diện rủi ro Tier 3");
        nk.setGiaiPhap("Lập lộ trình cải thiện cá nhân hóa, giới hạn tín chỉ đăng ký và theo dõi chuyên cần");
        nk.setCamKetSinhVien("Cam kết đi học chuyên cần ≥ 90%, nộp đầy đủ bài tập và đạt GPA ≥ 2.5 kỳ tới");

        if (nhatKyDAO.addNhatKy(nk)) {
            JOptionPane.showMessageDialog(this, "Đã tự động lưu kết quả tư vấn vào Nhật ký Cố vấn của sinh viên " + selectedSinhVien.getHoTen() + "!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào Nhật ký tư vấn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
