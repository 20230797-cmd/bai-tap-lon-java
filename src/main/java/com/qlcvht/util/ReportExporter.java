package com.qlcvht.util;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.CounselingProgressItem;
import com.qlcvht.model.SinhVien;
import com.qlcvht.service.ThongKeService;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Utility xu?t Bi?u m?u B?o c?o H?nh ch?nh (Bi?n b?n h?p l?p, B?o c?o t?ng h?p Khoa/L?p)
 * sang ??nh d?ng Word (.docx) v? PDF (.pdf).
 */
public class ReportExporter {

    private static final ThongKeService thongKeService = new ThongKeService();
    private static final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private static final SinhVienDAO sinhVienDAO = new SinhVienDAO();

    // =========================================================================
    // 1. XU?T BI?N B?N H?P L?P C? V?N H?C T?P
    // =========================================================================

    public static void exportBienBanHopLopWord(String maLop) {
        File fileToSave = chooseSaveFile("Bien_Ban_Hop_Lop_" + maLop, "docx", "Word Document (*.docx)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        List<CanhBaoHocVu> cbList = "ALL".equals(maLop) ? canhBaoDAO.getAllCanhBao() : canhBaoDAO.getCanhBaoByLop(maLop);
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        try (XWPFDocument doc = new XWPFDocument()) {
            addParagraph(doc, "B? GI?O D?C V? ??O T?O", 10, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "TR??NG ??I H?C X?Y D?NG H? N?I (HUCE)", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "C?NG H?A X? H?I CH? NGH?A VI?T NAM", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "??c l?p - T? do - H?nh ph?c", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "------------------------", 10, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "BI?N B?N H?P L?P C? V?N H?C T?P", 16, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "V/v T?nh h?nh h?c t?p, C?nh b?o h?c v? & T? v?n sinh vi?n", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "Th?i gian th?c hi?n: Ng?y " + currentDateStr, 11, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "I. TH?NG TIN CHUNG", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "? L?p sinh ho?t: " + ("ALL".equals(maLop) ? "T?t c? c?c l?p" : maLop), 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "? T?ng s? sinh vi?n: " + stats.getOrDefault("tong_sv", 0) + " sinh vi?n", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "? Ch? tr? cu?c h?p: C? v?n h?c t?p ph? tr?ch l?p", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "? Th? k?: L?p tr??ng / ??i di?n l?p", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "II. TH?NG K? T?NH H?NH C?NH B?O H?C V?", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "1. S? l??ng sinh vi?n ?ang h?c b?nh th??ng: " + stats.getOrDefault("sv_binh_thuong", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "2. S? l??ng sinh vi?n b? C?nh b?o h?c v? M?c 1: " + stats.getOrDefault("cb_muc_1", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "3. S? l??ng sinh vi?n b? C?nh b?o h?c v? M?c 2: " + stats.getOrDefault("cb_muc_2", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "4. S? l??ng sinh vi?n b? Bu?c th?i h?c: " + stats.getOrDefault("buoc_thoi_hoc", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "III. DANH S?CH SINH VI?N B? C?NH B?O H?C V? C?N T? V?N", 13, true, ParagraphAlignment.LEFT);

            XWPFTable table = doc.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            setHeaderCell(headerRow, 0, "STT");
            setHeaderCell(headerRow, 1, "M? SV");
            setHeaderCell(headerRow, 2, "H? v? T?n");
            setHeaderCell(headerRow, 3, "L?p");
            setHeaderCell(headerRow, 4, "M?c C?nh B?o");
            setHeaderCell(headerRow, 5, "GPA X?t Duy?t");
            setHeaderCell(headerRow, 6, "L? Do");
            setHeaderCell(headerRow, 7, "Tr?ng Th?i T? V?n");

            int idx = 1;
            for (CanhBaoHocVu cb : cbList) {
                XWPFTableRow r = table.createRow();
                r.getCell(0).setText(String.valueOf(idx++));
                r.getCell(1).setText(cb.getMaSv());
                r.getCell(2).setText(cb.getHoTenSv() != null ? cb.getHoTenSv() : "");
                r.getCell(3).setText(cb.getMaLop() != null ? cb.getMaLop() : "");
                r.getCell(4).setText(UITheme.formatMucCanhBao(cb.getMucCanhBao()));
                r.getCell(5).setText(String.format("%.2f", cb.getGpaXetDuyet()));
                r.getCell(6).setText(cb.getLyDo() != null ? cb.getLyDo() : "");
                r.getCell(7).setText(UITheme.formatTrangThaiTuVan(cb.getTrangThaiTuVan()));
            }

            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "IV. K?T LU?N & CAM K?T", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "? CVHT y?u c?u t?t c? sinh vi?n thu?c di?n C?nh b?o h?c v? li?n h? t? v?n tr?c ti?p.", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "? Ban c?n s? l?p ph?i h?p theo d?i s? s? v? nh?c nh? l?ch ??ng k? m?n h?c c?i thi?n.", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "       L?P TR??NG                                             C? V?N H?C T?P", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "     (K? v? ghi r? h? t?n)                                   (K? v? ghi r? h? t?n)", 10, false, ParagraphAlignment.CENTER);

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                doc.write(out);
            }
            showSuccess("Xu?t Bi?n b?n h?p l?p (Word) th?nh c?ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L?i xu?t file Word: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportBienBanHopLopPDF(String maLop) {
        File fileToSave = chooseSaveFile("Bien_Ban_Hop_Lop_" + maLop, "pdf", "PDF Document (*.pdf)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        List<CanhBaoHocVu> cbList = "ALL".equals(maLop) ? canhBaoDAO.getAllCanhBao() : canhBaoDAO.getCanhBaoByLop(maLop);
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try (FileOutputStream out = new FileOutputStream(fileToSave)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fTitle = getPdfFont(15, Font.BOLD);
            Font fHeader = getPdfFont(11, Font.BOLD);
            Font fBold = getPdfFont(11, Font.BOLD);
            Font fNormal = getPdfFont(10, Font.NORMAL);
            Font fSmall = getPdfFont(9, Font.NORMAL);

            Paragraph pHeader = new Paragraph("TR??NG ??I H?C X?Y D?NG H? N?I (HUCE)\nC?NG H?A X? H?I CH? NGH?A VI?T NAM\n??c l?p - T? do - H?nh ph?c\n------------------------", fHeader);
            pHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeader);

            document.add(new Paragraph("\n"));
            Paragraph pTitle = new Paragraph("BI?N B?N H?P L?P C? V?N H?C T?P\nV/v C?nh B?o H?c V? & T? V?n H?c T?p", fTitle);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            document.add(new Paragraph("Th?i gian: Ng?y " + currentDateStr + "   |   L?p: " + ("ALL".equals(maLop) ? "T?t c? c?c l?p" : maLop) + "\n\n", fNormal));

            document.add(new Paragraph("I. TH?NG K? T?NH H?NH H?C T?P", fBold));
            document.add(new Paragraph(String.format("? T?ng s? SV: %d | B?nh th??ng: %d | C?nh b?o M1: %d | C?nh b?o M2: %d | Bu?c th?i h?c: %d\n\n",
                stats.getOrDefault("tong_sv", 0), stats.getOrDefault("sv_binh_thuong", 0),
                stats.getOrDefault("cb_muc_1", 0), stats.getOrDefault("cb_muc_2", 0), stats.getOrDefault("buoc_thoi_hoc", 0)), fNormal));

            document.add(new Paragraph("II. DANH S?CH SINH VI?N B? C?NH B?O H?C V?", fBold));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3.5f, 2, 2.5f, 1.5f, 2.5f});

            addPdfHeaderCell(table, "STT", fBold);
            addPdfHeaderCell(table, "M? SV", fBold);
            addPdfHeaderCell(table, "H? T?n", fBold);
            addPdfHeaderCell(table, "L?p", fBold);
            addPdfHeaderCell(table, "M?c CB", fBold);
            addPdfHeaderCell(table, "GPA X?t", fBold);
            addPdfHeaderCell(table, "Tr?ng Th?i", fBold);

            int idx = 1;
            for (CanhBaoHocVu cb : cbList) {
                table.addCell(new Phrase(String.valueOf(idx++), fSmall));
                table.addCell(new Phrase(cb.getMaSv(), fSmall));
                table.addCell(new Phrase(cb.getHoTenSv() != null ? cb.getHoTenSv() : "", fSmall));
                table.addCell(new Phrase(cb.getMaLop() != null ? cb.getMaLop() : "", fSmall));
                table.addCell(new Phrase(UITheme.formatMucCanhBao(cb.getMucCanhBao()), fSmall));
                table.addCell(new Phrase(String.format("%.2f", cb.getGpaXetDuyet()), fSmall));
                table.addCell(new Phrase(UITheme.formatTrangThaiTuVan(cb.getTrangThaiTuVan()), fSmall));
            }
            document.add(table);

            document.add(new Paragraph("\n\n"));
            Paragraph pSig = new Paragraph("         L?P TR??NG                                                  C? V?N H?C T?P\n    (K? v? ghi r? h? t?n)                                      (K? v? ghi r? h? t?n)", fBold);
            pSig.setAlignment(Element.ALIGN_CENTER);
            document.add(pSig);

            document.close();
            showSuccess("Xu?t Bi?n b?n h?p l?p (PDF) th?nh c?ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L?i xu?t file PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportBaoCaoTongHopWord(String maLop) {
        File fileToSave = chooseSaveFile("Bao_Cao_Tong_Hop_Hoc_Vu_" + maLop, "docx", "Word Document (*.docx)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        Map<String, Object> progressStats = thongKeService.getThongKeTienDoTuVan(maLop);
        List<CounselingProgressItem> counselingItems = (List<CounselingProgressItem>) progressStats.get("items");
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        try (XWPFDocument doc = new XWPFDocument()) {
            addParagraph(doc, "B? GI?O D?C V? ??O T?O", 10, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "TR??NG ??I H?C X?Y D?NG H? N?I (HUCE)", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "C?NG H?A X? H?I CH? NGH?A VI?T NAM", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "??c l?p - T? do - H?nh ph?c", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "------------------------", 10, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "B?O C?O T?NG H?P T?NH H?NH H?C V? & TI?N ?? T? V?N", 16, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "K?nh g?i: Ban Gi?m Hi?u & Ph?ng ??o T?o", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "Th?i ?i?m b?o c?o: Ng?y " + currentDateStr + "  |  Ph?m vi: " + maLop, 11, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "I. T? L? TI?N ?? T? V?N & C?I THI?N ?I?M S?", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, String.format("? T? l? sinh vi?n b? c?nh b?o ?? ???c t? v?n: %.1f%% (%s / %s SV)\n" +
                                             "? T? l? sinh vi?n c?i thi?n ?i?m s? sau t? v?n: %.1f%% (%s / %s SV ?? t? v?n)\n",
                progressStats.get("percentDaTuVan"), progressStats.get("svDaTuVan"), progressStats.get("tongSvCanhBao"),
                progressStats.get("percentCaiThien"), progressStats.get("svCaiThienDiem"), progressStats.get("svDaTuVan")), 11, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "II. CHI TI?T TI?N ?? V? K?T QU? T? V?N", 13, true, ParagraphAlignment.LEFT);

            XWPFTable table = doc.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            setHeaderCell(headerRow, 0, "STT");
            setHeaderCell(headerRow, 1, "M? SV");
            setHeaderCell(headerRow, 2, "H? v? T?n");
            setHeaderCell(headerRow, 3, "L?p");
            setHeaderCell(headerRow, 4, "Ng?y T? V?n");
            setHeaderCell(headerRow, 5, "GPA Tr??c");
            setHeaderCell(headerRow, 6, "GPA Sau");
            setHeaderCell(headerRow, 7, "K?t Qu? C?i Thi?n");

            int idx = 1;
            for (CounselingProgressItem item : counselingItems) {
                XWPFTableRow r = table.createRow();
                r.getCell(0).setText(String.valueOf(idx++));
                r.getCell(1).setText(item.getMaSv());
                r.getCell(2).setText(item.getHoTen());
                r.getCell(3).setText(item.getMaLop());
                r.getCell(4).setText(item.getNgayTuVan() != null ? item.getNgayTuVan().toString() : "---");
                r.getCell(5).setText(String.valueOf(item.getGpaTruocTuVan()));
                r.getCell(6).setText(String.valueOf(item.getGpaSauTuVan()));
                r.getCell(7).setText(item.getTrangThaiCaiThien());
            }

            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "        C? V?N H?C T?P / TR??NG KHOA                               TR??NG PH?NG ??O T?O", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "            (K? v? ghi r? h? t?n)                                      (K? v? ghi r? h? t?n)", 10, false, ParagraphAlignment.CENTER);

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                doc.write(out);
            }
            showSuccess("Xu?t B?o c?o t?ng h?p (Word) th?nh c?ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L?i xu?t file Word: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportBaoCaoTongHopPDF(String maLop) {
        File fileToSave = chooseSaveFile("Bao_Cao_Tong_Hop_Hoc_Vu_" + maLop, "pdf", "PDF Document (*.pdf)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        Map<String, Object> progressStats = thongKeService.getThongKeTienDoTuVan(maLop);
        List<CounselingProgressItem> counselingItems = (List<CounselingProgressItem>) progressStats.get("items");
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try (FileOutputStream out = new FileOutputStream(fileToSave)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fTitle = getPdfFont(15, Font.BOLD);
            Font fHeader = getPdfFont(11, Font.BOLD);
            Font fBold = getPdfFont(11, Font.BOLD);
            Font fNormal = getPdfFont(10, Font.NORMAL);
            Font fSmall = getPdfFont(9, Font.NORMAL);

            Paragraph pHeader = new Paragraph("TR??NG ??I H?C X?Y D?NG H? N?I (HUCE)\nC?NG H?A X? H?I CH? NGH?A VI?T NAM\n------------------------", fHeader);
            pHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeader);

            document.add(new Paragraph("\n"));
            Paragraph pTitle = new Paragraph("B?O C?O T?NG H?P T?NH H?NH H?C V? & TI?N ?? T? V?N\nK?NH G?I: BAN GI?M HI?U", fTitle);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            document.add(new Paragraph("Th?i ?i?m b?o c?o: " + currentDateStr + "   |   Ph?m vi: " + maLop + "\n\n", fNormal));

            document.add(new Paragraph("I. T? L? TI?N ?? T? V?N & C?I THI?N ?I?M S?", fBold));
            document.add(new Paragraph(String.format("? T? l? sinh vi?n b? c?nh b?o ?? ???c t? v?n: %.1f%% (%s / %s SV)\n" +
                                                     "? T? l? sinh vi?n c?i thi?n ?i?m s? sau t? v?n: %.1f%% (%s / %s SV ?? t? v?n)\n\n",
                progressStats.get("percentDaTuVan"), progressStats.get("svDaTuVan"), progressStats.get("tongSvCanhBao"),
                progressStats.get("percentCaiThien"), progressStats.get("svCaiThienDiem"), progressStats.get("svDaTuVan")), fNormal));

            document.add(new Paragraph("II. CHI TI?T K?T QU? T? V?N V? C?I THI?N ?I?M S?", fBold));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3, 1.5f, 2, 1.5f, 1.5f, 3.5f});

            addPdfHeaderCell(table, "STT", fBold);
            addPdfHeaderCell(table, "M? SV", fBold);
            addPdfHeaderCell(table, "H? T?n", fBold);
            addPdfHeaderCell(table, "L?p", fBold);
            addPdfHeaderCell(table, "Ng?y TV", fBold);
            addPdfHeaderCell(table, "GPA Tr??c", fBold);
            addPdfHeaderCell(table, "GPA Sau", fBold);
            addPdfHeaderCell(table, "??nh gi? C?i thi?n", fBold);

            int idx = 1;
            for (CounselingProgressItem item : counselingItems) {
                table.addCell(new Phrase(String.valueOf(idx++), fSmall));
                table.addCell(new Phrase(item.getMaSv(), fSmall));
                table.addCell(new Phrase(item.getHoTen(), fSmall));
                table.addCell(new Phrase(item.getMaLop(), fSmall));
                table.addCell(new Phrase(item.getNgayTuVan() != null ? item.getNgayTuVan().toString() : "", fSmall));
                table.addCell(new Phrase(String.valueOf(item.getGpaTruocTuVan()), fSmall));
                table.addCell(new Phrase(String.valueOf(item.getGpaSauTuVan()), fSmall));
                table.addCell(new Phrase(item.getTrangThaiCaiThien(), fSmall));
            }
            document.add(table);

            document.add(new Paragraph("\n\n"));
            Paragraph pSig = new Paragraph(" C? V?N H?C T?P / TR??NG KHOA                               TR??NG PH?NG ??O T?O\n    (K? v? ghi r? h? t?n)                                      (K? v? ghi r? h? t?n)", fBold);
            pSig.setAlignment(Element.ALIGN_CENTER);
            document.add(pSig);

            document.close();
            showSuccess("Xu?t B?o c?o t?ng h?p (PDF) th?nh c?ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L?i xu?t file PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static File chooseSaveFile(String defaultName, String ext, String extDescription) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ch?n v? tr? l?u file b?o c?o");
        fileChooser.setFileFilter(new FileNameExtensionFilter(extDescription, ext));
        fileChooser.setSelectedFile(new File(defaultName + "." + ext));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) return null;

        File file = fileChooser.getSelectedFile();
        if (!file.getAbsolutePath().endsWith("." + ext)) {
            file = new File(file.getAbsolutePath() + "." + ext);
        }
        return file;
    }

    private static void addParagraph(XWPFDocument doc, String text, int fontSize, boolean isBold, ParagraphAlignment align) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(align);
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setFontSize(fontSize);
        r.setBold(isBold);
        r.setFontFamily("Times New Roman");
    }

    private static void setHeaderCell(XWPFTableRow row, int index, String text) {
        XWPFTableCell cell = row.getCell(index) != null ? row.getCell(index) : row.createCell();
        cell.setText(text);
        cell.setColor("1E40AF");
    }

    private static Font getPdfFont(int size, int style) {
        try {
            String fontPath = "C:/Windows/Fonts/arial.ttf";
            if (!new File(fontPath).exists()) {
                fontPath = "C:/Windows/Fonts/times.ttf";
            }
            if (new File(fontPath).exists()) {
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new Font(bf, size, style, Color.BLACK);
            }
        } catch (Exception ignored) {}
        return new Font(Font.HELVETICA, size, style, Color.BLACK);
    }

    private static void addPdfHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(30, 64, 175));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void showSuccess(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Xu?t B?o C?o Th?nh C?ng", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "L?i Xu?t File", JOptionPane.ERROR_MESSAGE);
    }
}
