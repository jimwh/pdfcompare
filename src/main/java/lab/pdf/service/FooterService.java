package lab.pdf.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
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
            final String numberLine,
            final String pageLine) {

        final PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(350);
        final String space=String.format("%-60s", ' ');
        final String page = String.format("%s%s", space, pageLine);
        final String printedOnLine = getPrintedOnLine();
        final String paragraphString=String.format("%s%n%s%n%s%s", fstLine, numberLine, printedOnLine, page);
        final Font font = FontFactory.getFont(Font.FontFamily.HELVETICA.name(), 9);
        final Paragraph paragraph=new Paragraph(paragraphString, font);
        final PdfPCell cell =  new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        return table;
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
