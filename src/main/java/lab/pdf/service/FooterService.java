package lab.pdf.service;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class FooterService {

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
        final String numberLine = getNumberLine(consentNumber, fromNumber);
        final String printedOnLine = getPrintedOnLine();
        final String paragraphString=String.format("%s%n%s%n%s%s", fstLine, numberLine, printedOnLine, page);
        final Font font = FontFactory.getFont(Font.FontFamily.HELVETICA.name(), 9);
        final Paragraph paragraph=new Paragraph(paragraphString, font);
        final PdfPCell cell =  new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        return table;
    }

    private String getNumberLine(final String consentNum, final String fromNum) {
        return StringUtils.isBlank(fromNum) ?
                String.format("Consent Form #: %s", consentNum) :
                String.format("Consent Form #: %s  Copied From #: %s", consentNum, fromNum);
    }

    private String getPrintedOnLine() {
        final DateTime now = DateTime.now();
        return String.format("Printed on: %s at %s", now.toString("MM/dd/yyyy"), now.toString("HH:mm"));
    }


}
