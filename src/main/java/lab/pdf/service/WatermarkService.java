package lab.pdf.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class WatermarkService {

    private static final int ROTATION = 45;

    @Resource
    private CustomerResourceLoader resourceLoader;

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

    public ByteArrayOutputStream waterMarkOnStamper(final String text, final ByteArrayOutputStream source) throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(source.toByteArray());
        final ByteArrayOutputStream out = waterMarkOnStamper(text, reader);
        reader.close();
        return out;
    }

    public ByteArrayOutputStream waterMark(final ByteArrayOutputStream source) throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(source.toByteArray());
        final ByteArrayOutputStream out = waterMark(reader);
        reader.close();
        return out;
    }

    private ByteArrayOutputStream waterMark(final String text, final PdfReader reader) throws IOException, DocumentException {
        if (reader.isEncrypted() || reader.isMetadataEncrypted()) {
            throw new IOException("Cannot modify encrypted pdf");
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int totalPages = reader.getNumberOfPages();
        final PdfStamper stamper = new PdfStamper(reader, output);
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
        return output;
    }

    private ByteArrayOutputStream waterMark(final PdfReader reader) throws IOException, DocumentException {
        if (reader.isEncrypted() || reader.isMetadataEncrypted()) {
            throw new IOException("Cannot modify encrypted pdf");
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int totalPages = reader.getNumberOfPages();
        final PdfStamper stamper = new PdfStamper(reader, output);
        final PdfGState pdfGState = new PdfGState();
        pdfGState.setFillOpacity(0.2f);
        final Image image = Image.getInstance(getResource("inactiveConsentDeviceNew.jpg"));
        image.setAbsolutePosition(10, 100);

        for (int page = 1; page <= totalPages; page++) {
            final PdfContentByte under = stamper.getUnderContent(page);
            under.setGState(pdfGState);
            under.addImage(image);
        }
        stamper.close();
        return output;
    }

    private Phrase getWaterMarkPhrase(final String text) {
        return new Phrase(text,
                new Font(FontFamily.HELVETICA, 110, Font.BOLD,
                        BaseColor.GRAY.darker()));
    }

    private Phrase getWaterMarkPhrase(final String text, final int fontSize) {
        return new Phrase(text,
                new Font(FontFamily.HELVETICA, fontSize, Font.BOLD,
                        BaseColor.BLACK.darker()));
    }

    private byte[] getResource(final String name) throws IOException {
        final InputStream inputStream = resourceLoader.getInputStream(name);
        final byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return bytes;
    }

    private ByteArrayOutputStream waterMarkOnStamper(final String text, final PdfReader reader) throws IOException, DocumentException {
        if (reader.isEncrypted() || reader.isMetadataEncrypted()) {
            throw new IOException("Cannot modify encrypted pdf");
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int totalPages = reader.getNumberOfPages();
        final PdfStamper stamper = new PdfStamper(reader, output);
        final PdfGState pdfGState = new PdfGState();
        pdfGState.setFillOpacity(0.2f);

        for (int page = 1; page <= totalPages; page++) {
            //final PdfContentByte under = stamper.getUnderContent(page);
            final PdfContentByte under = stamper.getOverContent(page);
            under.setGState(pdfGState);
            ColumnText.showTextAligned(under,
                    Element.ALIGN_CENTER,
                    getWaterMarkPhrase(text, 24),
                    440, 30, 0);
        }

        stamper.close();
        return output;
    }

}