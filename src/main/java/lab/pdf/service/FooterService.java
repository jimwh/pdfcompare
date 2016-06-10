package lab.pdf.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FooterService {

    public static String getPageLine(final int page, final int totalPages) {
        return String.format("Page %d of %d", page, totalPages);
    }

    public static String getNumberLine(final String consentNum, final String fromNum) {
        return StringUtils.isBlank(fromNum) ?
                String.format("Consent Form #: %s", consentNum) :
                String.format("Consent Form #: %s  Copied From #: %s", consentNum, fromNum);
    }


    public ByteArrayOutputStream addFooterInfo(final String fstLine,
                                               final String numberLine,
                                               final InputStream inputStream) throws IOException, DocumentException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PdfReader pdfReader = new PdfReader(inputStream);
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        final int totalPages = pdfReader.getNumberOfPages();
        for (int page = 1; page <= totalPages; page++) {
            final PdfContentByte pdfContentByte = pdfStamper.getOverContent(page);
            final String pageLine = getPageLine(page, totalPages);
            updatePageFooterTable(fstLine, numberLine, pageLine, pdfContentByte);
        }
        pdfStamper.close();
        pdfReader.close();
        return outputStream;
    }

    private void updatePageFooterTable(final String fstLine,
                                       final String numberLine,
                                       final String pageLine,
                                       final PdfContentByte contentByte) {
        final PdfPTable pdfTable = getFooterTable(fstLine, numberLine, pageLine);
        pdfTable.writeSelectedRows(0, -1, 18, 50, contentByte);
    }

    public PdfPTable getFooterTable(
            final String fstLine,
            final String numberLine,
            final String pageLine) {

        final PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(350);
        final String space=String.format("%-36s", ' ');
        final String extraSpacePageLine = String.format("%s%s", space, pageLine);
        final String printedOnLine = getPrintedOnLine();
        final String text=String.format("%s%n%s%n%s%s", fstLine, numberLine, printedOnLine, extraSpacePageLine);
        final Font font = FontFactory.getFont(Font.FontFamily.HELVETICA.name(), 9f, Font.NORMAL);
        final PdfPCell cell =  getCell(text, font);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        return table;
    }

    /*
    public PdfPTable getFooterTable(
            final String fstLine,
            final String numberLine,
            final String pageLine) {

        final PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(330);
        final String space=String.format("%-36s", ' ');
        final String page = String.format("%s%s", space, pageLine);
        final String printedOnLine = getPrintedOnLine();
        final String lstLine = String.format("%s%s", printedOnLine, page);
        final Font font = FontFactory.getFont(Font.FontFamily.HELVETICA.name(), 9f, Font.NORMAL);
        table.addCell( getCell(fstLine, font) );
        table.addCell( getCell(numberLine, font) );
        table.addCell( getCell(lstLine, font) );
        return table;
    }
    */
    private PdfPCell getCell(final String text, final Font font) {
        final PdfPCell cell =  new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public PdfPTable getStampTable(final String text) {
        final PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(110);
        final Font font = FontFactory.getFont(FontFamily.HELVETICA.name(), 16f, Font.BOLD, BaseColor.GRAY.darker());
        final Paragraph paragraph=new Paragraph(text, font);
        final PdfPCell cell =  new PdfPCell(paragraph);
        cell.setBorder(Rectangle.BOX | Rectangle.CCITTG3_2D);

        cell.setBorderWidth(3f);
        cell.setPaddingBottom(6f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY.darker());

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        return table;
    }

    private String getPrintedOnLine() {
        final DateTime now = DateTime.now();
        return String.format("Printed on: %s at %s", now.toString("MM/dd/yyyy"), now.toString("HH:mm"));
    }

}
