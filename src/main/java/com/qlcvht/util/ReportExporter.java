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
 * Utility xu\u1EA5t Bi\u1EC3u m\u1EABu B\u00E1o c\u00E1o H\u00E0nh ch\u00EDnh
 */
public class ReportExporter {

    private static final ThongKeService thongKeService = new ThongKeService();
    private static final CanhBaoDAO canhBaoDAO = new CanhBaoDAO();
    private static final SinhVienDAO sinhVienDAO = new SinhVienDAO();

    public static void exportBienBanHopLopWord(String maLop) {
        File fileToSave = chooseSaveFile("Bien_Ban_Hop_Lop_" + maLop, "docx", "Word Document (*.docx)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        List<CanhBaoHocVu> cbList = "ALL".equals(maLop) ? canhBaoDAO.getAllCanhBao() : canhBaoDAO.getCanhBaoByLop(maLop);
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        try (XWPFDocument doc = new XWPFDocument()) {
            addParagraph(doc, "BỘ GIÁO DỤC VÀ ĐÀO TẠO", 10, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "\u0110\u1ED9c l\u1EADp - T\u1EF1 do - H\u1EA1nh ph\u00FAc", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "------------------------", 10, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "BI\u00CAN B\u1EA2N H\u1ECCC L\u1EDAP C\u1ED0 V\u1EA4N H\u1ECCC T\u1EACP", 16, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "V/v T\u00ECnh h\u00ECnh h\u1ECDc t\u1EADp, C\u1EA3nh b\u00E1o h\u1ECDc v\u1EE5 & T\u01B0 v\u1EA5n sinh vi\u00EAn", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "Th\u1EDDi gian th\u1EF1c hi\u1EC7n: Ng\u00E0y " + currentDateStr, 11, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "I. TH\u00D4NG TIN CHUNG", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "\u2022 L\u1EDBp sinh ho\u1EA1t: " + ("ALL".equals(maLop) ? "T\u1EA5t c\u1EA3 c\u00E1c l\u1EDBp" : maLop), 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "\u2022 T\u1ED5ng s\u1ED1 sinh vi\u00EAn: " + stats.getOrDefault("tong_sv", 0) + " sinh vi\u00EAn", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "\u2022 Ch\u1EE7 tr\u00EC cu\u1ED9c h\u1ECDp: C\u1ED1 v\u1EA5n h\u1ECDc t\u1EADp ph\u1EE5 tr\u00E1ch l\u1EDBp", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "\u2022 Th\u01B0 k\u00FD: L\u1EDBp tr\u01B0\u1EDFng / \u0110\u1EA1i di\u1EC7n l\u1EDBp", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "II. TH\u1ED0NG K\u00CA T\u00CCNH H\u00CCNH C\u1EA2NH B\u00C1O H\u1ECCC V\u1EE4", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "1. S\u1ED1 l\u01B0\u1EE3ng SV \u0111ang h\u1ECDc b\u00ECnh th\u01B0\u1EDDng: " + stats.getOrDefault("sv_binh_thuong", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "2. S\u1ED1 l\u01B0\u1EE3ng SV b\u1ECB C\u1EA3nh b\u00E1o h\u1ECDc v\u1EE5 M\u1EE9c 1: " + stats.getOrDefault("cb_muc_1", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "3. S\u1ED1 l\u01B0\u1EE3ng SV b\u1ECB C\u1EA3nh b\u00E1o h\u1ECDc v\u1EE5 M\u1EE9c 2: " + stats.getOrDefault("cb_muc_2", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "4. S\u1ED1 l\u01B0\u1EE3ng SV b\u1ECB Bu\u1ED9c th\u00F4i h\u1ECDc: " + stats.getOrDefault("buoc_thoi_hoc", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "III. DANH S\u00C1CH SINH VI\u00CAN B\u1ECA C\u1EA2NH B\u00C1O H\u1ECCC V\u1EE4", 13, true, ParagraphAlignment.LEFT);

            XWPFTable table = doc.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            setHeaderCell(headerRow, 0, "STT");
            setHeaderCell(headerRow, 1, "M\u00E3 SV");
            setHeaderCell(headerRow, 2, "H\u1ECD v\u00E0 T\u00EAn");
            setHeaderCell(headerRow, 3, "L\u1EDBp");
            setHeaderCell(headerRow, 4, "M\u1EE9c C\u1EA3nh B\u00E1o");
            setHeaderCell(headerRow, 5, "GPA X\u00E9t");
            setHeaderCell(headerRow, 6, "L\u00FD Do");
            setHeaderCell(headerRow, 7, "Tr\u1EA1ng Th\u00E1i");

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
            addParagraph(doc, "IV. K\u1EBEt LU\u1EACN & CAM K\u1EBEt", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "\u2022 CVHT y\u00EAu c\u1EA7u t\u1EA5t c\u1EA3 sinh vi\u00EAn thu\u1ED9c di\u1EC7n C\u1EA3nh b\u00E1o h\u1ECDc v\u1EE5 li\u00EAn h\u1EC7 t\u01B0 v\u1EA5n tr\u1EF1c ti\u1EBFp.", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "       L\u1EDBp TR\u01AF\u1EDENG                                             C\u1ED0 V\u1EA4N H\u1ECCC T\u1EACP", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "     (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)                                   (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)", 10, false, ParagraphAlignment.CENTER);

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                doc.write(out);
            }
            showSuccess("Xu\u1EA5t Bi\u00EAn b\u1EA3n h\u1ECDp l\u1EDBp (Word) th\u00E0nh c\u00F4ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L\u1ED7i xu\u1EA5t file Word: " + e.getMessage());
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

            Paragraph pHeader = new Paragraph("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)\nCỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\nĐộc lập - Tự do - Hạnh phúc\n------------------------", fHeader);
            pHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeader);

            document.add(new Paragraph("\n"));
            Paragraph pTitle = new Paragraph("BI\u00CAN B\u1EA2N H\u1ECCC L\u1EDAP C\u1ED0 V\u1EA4N H\u1ECCC T\u1EACP\nV/v C\u1EA3nh B\u00E1o H\u1ECDc V\u1EE5 & T\u01B0 V\u1EA5n H\u1ECDc T\u1EADp", fTitle);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            document.add(new Paragraph("Th\u1EDDi gian: Ng\u00E0y " + currentDateStr + "   |   L\u1EDBp: " + ("ALL".equals(maLop) ? "T\u1EA5t c\u1EA3 c\u00E1c l\u1EDBp" : maLop) + "\n\n", fNormal));

            document.add(new Paragraph("I. TH\u1ED0NG K\u00CA T\u00CCNH H\u00CCNH H\u1ECCC T\u1EACP", fBold));
            document.add(new Paragraph(String.format("\u2022 T\u1ED5ng s\u1ED1 SV: %d | B\u00ECnh th\u01B0\u1EDDng: %d | C\u1EA3nh b\u00E1o M1: %d | C\u1EA3nh b\u00E1o M2: %d | Bu\u1ED9c th\u00F4i h\u1ECDc: %d\n\n",
                stats.getOrDefault("tong_sv", 0), stats.getOrDefault("sv_binh_thuong", 0),
                stats.getOrDefault("cb_muc_1", 0), stats.getOrDefault("cb_muc_2", 0), stats.getOrDefault("buoc_thoi_hoc", 0)), fNormal));

            document.add(new Paragraph("II. DANH S\u00C1CH SINH VI\u00CAN B\u1ECA C\u1EA2NH B\u00C1O H\u1ECCC V\u1EE4", fBold));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3.5f, 2, 2.5f, 1.5f, 2.5f});

            addPdfHeaderCell(table, "STT", fBold);
            addPdfHeaderCell(table, "M\u00E3 SV", fBold);
            addPdfHeaderCell(table, "H\u1ECD T\u00EAn", fBold);
            addPdfHeaderCell(table, "L\u1EDBp", fBold);
            addPdfHeaderCell(table, "M\u1EE9c CB", fBold);
            addPdfHeaderCell(table, "GPA X\u00E9t", fBold);
            addPdfHeaderCell(table, "Tr\u1EA1ng Th\u00E1i", fBold);

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
            Paragraph pSig = new Paragraph("         L\u1EDBp TR\u01AF\u1EDENG                                                  C\u1ED0 V\u1EA4N H\u1ECCC T\u1EACP\n    (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)                                      (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)", fBold);
            pSig.setAlignment(Element.ALIGN_CENTER);
            document.add(pSig);

            document.close();
            showSuccess("Xu\u1EA5t Bi\u00EAn b\u1EA3n h\u1ECDp l\u1EDBp (PDF) th\u00E0nh c\u00F4ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L\u1ED7i xu\u1EA5t file PDF: " + e.getMessage());
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
            addParagraph(doc, "BỘ GIÁO DỤC VÀ ĐÀO TẠO", 10, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "\u0110\u1ED9c l\u1EADp - T\u1EF1 do - H\u1EA1nh ph\u00FAc", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "------------------------", 10, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "B\u00C1O C\u00C1O T\u1ED4NG H\u1EE2P T\u00CCNH H\u00CCNH H\u1ECCC V\u1EE4 & TI\u1EBEt \u0110\u1ED8 T\u01AF V\u1EA4N", 16, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "K\u00EDnh g\u1EEDi: Ban Gi\u00E1m Hi\u1EC7u & Ph\u00F2ng \u0110\u00E0o T\u1EA1o", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "Th\u1EDDi \u0111i\u1EC3m b\u00E1o c\u00E1o: Ng\u00E0y " + currentDateStr + "  |  Ph\u1EA1m vi: " + maLop, 11, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "I. T\u1EC8 L\u1EC6 TI\u1EBEt \u0110\u1ED8 T\u01AF V\u1EA4N & C\u1EA2I THI\u1EC6N \u0110I\u1EC2M S\u1ED0", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, String.format("\u2022 T\u1EC9 l\u1EC7 SV b\u1ECB c\u1EA3nh b\u00E1o \u0111\u00E3 \u0111\u01B0\u1EE3c t\u01B0 v\u1EA5n: %.1f%% (%s / %s SV)\n" +
                                             "\u2022 T\u1EC9 l\u1EC7 SV c\u1EA3i thi\u1EC7n \u0111i\u1EC3m s\u1ED1 sau t\u01B0 v\u1EA5n: %.1f%% (%s / %s SV \u0111\u00E3 t\u01B0 v\u1EA5n)\n",
                progressStats.get("percentDaTuVan"), progressStats.get("svDaTuVan"), progressStats.get("tongSvCanhBao"),
                progressStats.get("percentCaiThien"), progressStats.get("svCaiThienDiem"), progressStats.get("svDaTuVan")), 11, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "II. CHI TI\u1EBEt TI\u1EBEt \u0110\u1ED8 V\u00C0 K\u1EBEt QU\u1EA2 T\u01AF V\u1EA4N", 13, true, ParagraphAlignment.LEFT);

            XWPFTable table = doc.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            setHeaderCell(headerRow, 0, "STT");
            setHeaderCell(headerRow, 1, "M\u00E3 SV");
            setHeaderCell(headerRow, 2, "H\u1ECD v\u00E0 T\u00EAn");
            setHeaderCell(headerRow, 3, "L\u1EDBp");
            setHeaderCell(headerRow, 4, "Ng\u00E0y T\u01B0 V\u1EA5n");
            setHeaderCell(headerRow, 5, "GPA Tr\u01B0\u1EDBc");
            setHeaderCell(headerRow, 6, "GPA Sau");
            setHeaderCell(headerRow, 7, "K\u1EBFt Qu\u1EA3 C\u1EA3i Thi\u1EC7n");

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
            addParagraph(doc, "        C\u1ED0 V\u1EA4N H\u1ECCC T\u1EACP / TR\u01AF\u1EDENG KHOA                               TR\u01AF\u1EDENG PH\u00D2NG \u0110\u00C0O T\u1EA0O", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "            (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)                                      (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)", 10, false, ParagraphAlignment.CENTER);

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                doc.write(out);
            }
            showSuccess("Xu\u1EA5t B\u00E1o c\u00E1o t\u1ED5ng h\u1EE3p (Word) th\u00E0nh c\u00F4ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L\u1ED7i xu\u1EA5t file Word: " + e.getMessage());
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

            Paragraph pHeader = new Paragraph("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á (EAUT)\nCỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\n------------------------", fHeader);
            pHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeader);

            document.add(new Paragraph("\n"));
            Paragraph pTitle = new Paragraph("B\u00C1O C\u00C1O T\u1ED4NG H\u1EE2P T\u00CCNH H\u00CCNH H\u1ECCC V\u1EE4 & TI\u1EBEt \u0110\u1ED8 T\u01AF V\u1EA4N\nK\u00CDNH G\u1EEDI: BAN GI\u00C1M HI\u1EC6U", fTitle);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            document.add(new Paragraph("Th\u1EDDi \u0111i\u1EC3m b\u00E1o c\u00E1o: " + currentDateStr + "   |   Ph\u1EA1m vi: " + maLop + "\n\n", fNormal));

            document.add(new Paragraph("I. T\u1EC8 L\u1EC6 TI\u1EBEt \u0110\u1ED8 T\u01AF V\u1EA4N & C\u1EA2I THI\u1EC6N \u0110I\u1EC2M S\u1ED0", fBold));
            document.add(new Paragraph(String.format("\u2022 T\u1EC9 l\u1EC7 SV b\u1ECB c\u1EA3nh b\u00E1o \u0111\u00E3 \u0111\u01B0\u1EE3c t\u01B0 v\u1EA5n: %.1f%% (%s / %s SV)\n" +
                                                     "\u2022 T\u1EC9 l\u1EC7 SV c\u1EA3i thi\u1EC7n \u0111i\u1EC3m s\u1ED1 sau t\u01B0 v\u1EA5n: %.1f%% (%s / %s SV \u0111\u00E3 t\u01B0 v\u1EA5n)\n\n",
                progressStats.get("percentDaTuVan"), progressStats.get("svDaTuVan"), progressStats.get("tongSvCanhBao"),
                progressStats.get("percentCaiThien"), progressStats.get("svCaiThienDiem"), progressStats.get("svDaTuVan")), fNormal));

            document.add(new Paragraph("II. CHI TI\u1EBEt K\u1EBEt QU\u1EA2 T\u01AF V\u1EA4N V\u00C0 C\u1EA2I THI\u1EC6N \u0110I\u1EC2M S\u1ED0", fBold));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3, 1.5f, 2, 1.5f, 1.5f, 3.5f});

            addPdfHeaderCell(table, "STT", fBold);
            addPdfHeaderCell(table, "M\u00E3 SV", fBold);
            addPdfHeaderCell(table, "H\u1ECD T\u00EAn", fBold);
            addPdfHeaderCell(table, "L\u1EDBp", fBold);
            addPdfHeaderCell(table, "Ng\u00E0y TV", fBold);
            addPdfHeaderCell(table, "GPA Tr\u01B0\u1EDBc", fBold);
            addPdfHeaderCell(table, "GPA Sau", fBold);
            addPdfHeaderCell(table, "K\u1EBFt Qu\u1EA3", fBold);

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
            Paragraph pSig = new Paragraph(" C\u1ED0 V\u1EA4N H\u1ECCC T\u1EACP / TR\u01AF\u1EDENG KHOA                               TR\u01AF\u1EDENG PH\u00D2NG \u0110\u00C0O T\u1EA0O\n    (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)                                      (K\u00FD v\u00E0 ghi r\u00F5 h\u1ECD t\u00EAn)", fBold);
            pSig.setAlignment(Element.ALIGN_CENTER);
            document.add(pSig);

            document.close();
            showSuccess("Xu\u1EA5t B\u00E1o c\u00E1o t\u1ED5ng h\u1EE3p (PDF) th\u00E0nh c\u00F4ng:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("L\u1ED7i xu\u1EA5t file PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static File chooseSaveFile(String defaultName, String ext, String extDescription) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ch\u1ECDn v\u1ECB tr\u00ED l\u01B0u file b\u00E1o c\u00E1o");
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
        JOptionPane.showMessageDialog(null, msg, "Xu\u1EA5t B\u00E1o C\u00E1o Th\u00E0nh C\u00F4ng", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "L\u1ED7i Xu\u1EA5t File", JOptionPane.ERROR_MESSAGE);
    }
}
