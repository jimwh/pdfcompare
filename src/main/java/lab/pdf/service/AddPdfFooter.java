package lab.pdf.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AddPdfFooter {

    private static final Logger log = LoggerFactory.getLogger(AddPdfFooter.class);



    public void addFooter(final String fileName) throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(fileName);
        final PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(new File("./foobar.pdf")));
        final int total = reader.getNumberOfPages();
        for(int i = 1; i<=total; i++) {
            log.info("page {}", i);
            getFooterTable(i, total).writeSelectedRows(0, -1, 24, 30, pdfStamper.getOverContent(i));
        }
        pdfStamper.close();
    }

    public static PdfPTable getFooterTable(final int x, final int y) {
        final PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(527);
        table.setLockedWidth(true);
        table.getDefaultCell().setFixedHeight(20);
        table.getDefaultCell().setBorder(Rectangle.TOP);
        table.addCell("FOOBAR FILM FESTIVAL");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(String.format("Page %d of %d", x, y));
        return table;
    }

    public PdfPTable getFooterTable(
            final String fstLine,
            final String consentNumber,
            final String fromNumber,
            final int x, final int y) {
        final PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(350);
        table.setLockedWidth(true);
        final String space=String.format("%-60s", ' ');
        final String page = String.format("%sPage %d of %d", space, x, y);
        final String line2 = getLine2(consentNumber, fromNumber);
        final String line3 = printedOn();
        final String paragraphString=String.format("%s%n%s%n%s%s", fstLine, line2, line3, page);
        final Font font = FontFactory.getFont(Font.FontFamily.HELVETICA.name(), 9);
        final Paragraph paragraph=new Paragraph(paragraphString, font);
        final PdfPCell cell =  new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        return table;
    }


    private String printedOn() {
        final DateTime now = DateTime.now();
        return String.format("Printed on: %s at %s", now.toString("MM/dd/yyyy"), now.toString("HH:mm"));
    }

    private String getLine2(final String consentNum, final String fromNum) {
        return StringUtils.isBlank(fromNum) ?
                String.format("Consent Form #: %s", consentNum) :
                String.format("Consent Form #: %s  Copied From #: %s", consentNum, fromNum);
    }

    public PdfPTable getNgsRuleFailFooterTable(final String fstLine,
                                            final String consentNumber, final String fromNumber,
                                            final int x, final int y) {
        final PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(350);
        table.setLockedWidth(true);
        final String space=String.format("%-60s", ' ');
        final String page = String.format("%sPage %d of %d", space, x, y);
        final String line2 = getLine2(consentNumber, fromNumber);
        final String line3 = printedOn();
        final String paragraphString=String.format("%s%n%s%n%s%s", fstLine, line2, line3, page);
        final Font font = FontFactory.getFont(Font.FontFamily.HELVETICA.name(), 9);
        final Paragraph paragraph=new Paragraph(paragraphString, font);
        final PdfPCell cell =  new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        return table;
    }

}
