package lab.pdf.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class WatermarkService {

    private static final int ROTATION = 45;

    public ByteArrayOutputStream waterMark(final String text, final byte[] source) throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(source);
        final ByteArrayOutputStream out = waterMark(text, reader);
        reader.close();
        return out;
    }

    public ByteArrayOutputStream waterMark(final String text, final ByteArrayOutputStream source) throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(source.toByteArray());
        final ByteArrayOutputStream out = waterMark(text, reader);
        reader.close();
        return out;
    }

    private ByteArrayOutputStream waterMark(final String text, final PdfReader reader) throws IOException, DocumentException {
        // if read only, don't bother further, simply return null
        if (reader.isEncrypted() || reader.isMetadataEncrypted()) {
            throw new IOException("Cannot modify encrypted pdf");
        }
        final ByteArrayOutputStream dest = new ByteArrayOutputStream();
        final int totalPages = reader.getNumberOfPages();
        final PdfStamper stamper = new PdfStamper(reader, dest);
        final PdfGState pdfGState = new PdfGState();
        pdfGState.setFillOpacity(0.2f);

        for (int page = 1; page <= totalPages; page++) {
            final PdfContentByte under = stamper.getUnderContent(page);
            under.setGState(pdfGState);
            ColumnText.showTextAligned(under,
                    Element.ALIGN_CENTER,
                    getWaterMarkPhrase(text),
                    280, 390, ROTATION);
        }

        stamper.close();
        return dest;
    }

    private Phrase getWaterMarkPhrase(final String text) {
        return new Phrase(text,
                new Font(FontFamily.HELVETICA, 140, Font.BOLD,
                        BaseColor.GRAY.darker()));
    }
}