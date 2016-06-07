package lab.pdf.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Component
public class PdfStampService {

    @Resource
    private CustomerResourceLoader resourceLoader;
    private static final Logger log = LoggerFactory.getLogger(PdfStampService.class);

    public ByteArrayOutputStream stamp(final String fileName) throws IOException, DocumentException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final InputStream inputStream = getInputStream(fileName);
        final PdfReader pdfReader = new PdfReader(inputStream);
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);

        final Image image = Image.getInstance(getResource("approvalConsent.png"));
        log.info("image={}", image != null);
        final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        final int totalPages = pdfReader.getNumberOfPages();
        for (int i = 1; i <= totalPages; i++) {
            final Rectangle pageSize = pdfReader.getCropBox(i);
            // Ensure all the images will have a height of one inch.
            image.scaleToFit(1000f, 35.5f);

            //Stamp should be always on the right-bottom corner regardless of the original rotation.
            final int rotation = pdfReader.getPageRotation(i);
            final boolean isPortraitMode = rotation == 0 || rotation == 180;
            if (isPortraitMode) {
                image.setAbsolutePosition(pageSize.getRight(10f) - image.getScaledWidth(), pageSize.getBottom(20f));
            } else {
                image.setAbsolutePosition(pageSize.getTop(20f) - image.getScaledWidth(), pageSize.getLeft(5f));
            }

            //put content over (not under)
            final PdfContentByte content = pdfStamper.getOverContent(i);
            final PdfGState gs1 = new PdfGState();
            gs1.setFillOpacity(0.5f);
            content.setGState(gs1);
            content.saveState();
            content.addImage(image);

            final ApprovalDateStamp approvalDateStamp = new ApprovalDateStamp(content,
                    pageSize,
                    baseFont,
                    new Date(),
                    isPortraitMode);
            approvalDateStamp.stampText();

            final DateTime until = DateTime.now().plusYears(1);
            final ExpiredDateStamp expiredDateStamp = new ExpiredDateStamp(content,
                    pageSize,
                    baseFont,
                    until.toDate(),
                    isPortraitMode);
            expiredDateStamp.stampText();
            content.restoreState();
        }
        pdfStamper.close();
        pdfReader.close();
        inputStream.close();
        return byteArrayOutputStream;
    }


    private InputStream getInputStream(final String fileName) throws FileNotFoundException {
        log.info("fileName={}", fileName);
        return new FileInputStream(new File(fileName));
    }

    private byte[] getResource(final String name) throws IOException {
        final InputStream inputStream = resourceLoader.getInputStream(name);
        final byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return bytes;
    }

    private class ApprovalDateStamp extends TextStamp {
        public ApprovalDateStamp(final PdfContentByte contentByte,
                                 final Rectangle cropBox,
                                 final BaseFont baseFont,
                                 final Date date,
                                 final boolean isPortrait) {

            super(contentByte,
                    cropBox,
                    baseFont,
                    isPortrait,
                    new DateTime(date).toString("MM/dd/yyyy"),
                    6f + 2f,     // fontSize
                    161f - 55f,  // rightMargin
                    30f + 5f,    // bottomMargin
                    174f - 5f,   // topMargin
                    16f + 55f);  // leftMargin
        }
    }

    private class ExpiredDateStamp extends TextStamp {
        public ExpiredDateStamp(final PdfContentByte content,
                                final Rectangle cropBox,
                                final BaseFont baseFont,
                                final Date date,
                                final boolean isPortraitMode) {
            super(content,
                    cropBox,
                    baseFont,
                    isPortraitMode,
                    new DateTime(date).toString("MM/dd/yyyy"),
                    6f + 2f, // fontSize
                    151f - 45f, // rightMargin
                    23  + 2f,   // bottomMargin
                    164 - 2f,  // topMargin
                    10f + 45f); // leftMargin
        }
    }


    private class TextStamp {
        private static final float textRotation = 0f;

        private final PdfContentByte content;
        private final Rectangle cropBox;
        private final boolean isPortraitMode;
        private final String textToPrint;

        private final BaseFont baseFont;
        private final float fontSize;

        private final float rightMargin;
        private final float bottomMargin;
        private final float topMargin;
        private final float leftMargin;

        protected TextStamp(final PdfContentByte content,
                            final Rectangle cropBox,
                            final BaseFont baseFont,
                            final boolean isPortraitMode,
                            final String textToPrint,
                            final float fontSize,
                            final float rightMargin,
                            final float bottomMargin,
                            final float topMargin,
                            final float leftMargin) {
            this.content = content;
            this.cropBox = cropBox;
            this.baseFont = baseFont;
            this.isPortraitMode = isPortraitMode;
            this.textToPrint = textToPrint;

            this.fontSize = fontSize;
            this.rightMargin = rightMargin;
            this.bottomMargin = bottomMargin;
            this.topMargin = topMargin;
            this.leftMargin = leftMargin;
        }

        public void stampText() {
            this.content.beginText();
            this.content.setFontAndSize(this.baseFont, fontSize);

            float x = this.cropBox.getRight(rightMargin);
            float y = this.cropBox.getBottom(bottomMargin);
            if (!this.isPortraitMode) {
                x = this.cropBox.getTop(topMargin);
                y = this.cropBox.getLeft(leftMargin);
            }
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, textToPrint, x, y, textRotation);

            this.content.endText();
        }
    }

}
