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

    public enum Status {
        Expired,
        Inactive,
        Superseded,
        ClosedEnrollment,
        NGS
    }

    @Resource
    private CustomerResourceLoader resourceLoader;

    public ByteArrayOutputStream waterMark(final String text, final ByteArrayOutputStream source) throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(source.toByteArray());
        final ByteArrayOutputStream out = waterMark(text, reader);
        reader.close();
        return out;
    }

    public ByteArrayOutputStream waterMark(final Status status, final ByteArrayOutputStream source) throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(source.toByteArray());
        final ByteArrayOutputStream out = waterMark(status, reader);
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
        pdfGState.setFillOpacity(0.5f);

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
                new Font(FontFamily.HELVETICA, 100, Font.BOLD,
                        BaseColor.GRAY.brighter()));
    }

    private Phrase getWaterMarkPhrase(final String text, final int fontSize) {
        return new Phrase(text,
                new Font(FontFamily.HELVETICA, fontSize, Font.BOLD,
                        BaseColor.RED.brighter()));
    }

    private Phrase getStudayClosedToEnrollmentPhrase() {
        return new Phrase("Study Closed to Enrollment",
                new Font(FontFamily.HELVETICA, 18, Font.BOLD,
                        BaseColor.RED.brighter()));
    }

    private byte[] getResource(final String name) throws IOException {
        final InputStream inputStream = resourceLoader.getInputStream(name);
        final byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return bytes;
    }

    // expired, inactive, superseded, study of closed enrollment
    private ByteArrayOutputStream waterMark(final Status status, final PdfReader reader) throws IOException, DocumentException {
        if (reader.isEncrypted() || reader.isMetadataEncrypted()) {
            throw new IOException("Cannot modify encrypted pdf");
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int totalPages = reader.getNumberOfPages();
        final PdfStamper stamper = new PdfStamper(reader, output);
        final PdfGState pdfGState = new PdfGState();
        pdfGState.setFillOpacity(0.5f);

        for (int page = 1; page <= totalPages; page++) {
            final PdfContentByte over = stamper.getOverContent(page);
            over.setGState(pdfGState);
            if (Status.Expired == status) {
                // on stamp
                ColumnText.showTextAligned(over,
                        Element.ALIGN_CENTER,
                        getWaterMarkPhrase(Status.Expired.toString(), 24),
                        460, 38, ROTATION);
                // in the middle of page
                ColumnText.showTextAligned(over,
                        Element.ALIGN_CENTER,
                        getWaterMarkPhrase(Status.Expired.toString()),
                        280, 390, ROTATION);
            } else if (status == Status.Inactive) {
                // on stamp
                ColumnText.showTextAligned(over,
                        Element.ALIGN_CENTER,
                        getWaterMarkPhrase(Status.Inactive.toString(), 24),
                        460, 38, ROTATION);
                // in the middle of page
                ColumnText.showTextAligned(over,
                        Element.ALIGN_CENTER,
                        getWaterMarkPhrase(Status.Inactive.toString()),
                        280, 390, ROTATION);
            } else if (status == Status.Superseded) {
                // on stamp
                ColumnText.showTextAligned(over,
                        Element.ALIGN_CENTER,
                        getWaterMarkPhrase(Status.Superseded.toString(), 24),
                        460, 38, 30);
                // in the middle of page
                ColumnText.showTextAligned(over,
                        Element.ALIGN_CENTER,
                        getWaterMarkPhrase(Status.Superseded.toString()),
                        300, 390, 30);
            } else if (status == Status.ClosedEnrollment) {
                ColumnText.showTextAligned(over,
                        Element.ALIGN_CENTER,
                        getStudayClosedToEnrollmentPhrase(),
                        460, 38, 0);
            }

        }

        stamper.close();
        return output;
    }

}