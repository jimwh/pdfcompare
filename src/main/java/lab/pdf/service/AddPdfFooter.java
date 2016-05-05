package lab.pdf.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jh3389 on 5/4/16.
 */
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
}
