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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Component
public class PdfStampService {

    private static final String PROTOCOL_NUMBER_TEXT = "IRB-";
    private static final String APPROVAL_DATE_TEXT 	= "IRB Approval Date: ";
    private static final String EXPIRED_DATE_TEXT 	= "     for use until: ";
    private static final String EXEMPTION_DATE_TEXT = "IRB Exemption Date: ";

    @Resource
    private CustomerResourceLoader resourceLoader;
    private static final Logger log = LoggerFactory.getLogger(PdfStampService.class);

    public ByteArrayOutputStream stamp(final String fileName) throws IOException, DocumentException {
        final ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        final InputStream inputStream = getInputStream(fileName);
        final PdfReader pdfReader = new PdfReader(inputStream);
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);

        final Image image =  Image.getInstance( getResource("approvalConsent.png") );
        log.info("image={}", image!=null);
        final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        final int totalPages = pdfReader.getNumberOfPages();
        for(int i=1; i<=totalPages; i++) {
            Rectangle pageSize = pdfReader.getCropBox(i);
            // Ensure all the images will have a height of one inch.
            image.scaleToFit(1000f, 35.5f);

            //Stamp should be always on the right-bottom corner regardless of the original rotation.
            int rotation = pdfReader.getPageRotation(i);
            boolean isPortraitMode = rotation == 0 || rotation ==180;
            if (isPortraitMode) {
                image.setAbsolutePosition(pageSize.getRight(10f)-image.getScaledWidth(), pageSize.getBottom(20f));
            } else {
                image.setAbsolutePosition(pageSize.getTop(20f)-image.getScaledWidth(), pageSize.getLeft(5f));
            }

            //put content over (not under)
            final PdfContentByte content = pdfStamper.getOverContent(i);
            final PdfGState gs1 = new PdfGState();
            gs1.setFillOpacity(0.5f);
            content.setGState(gs1);
            content.saveState();
            content.addImage(image);

            final ApprovalDateStamp dateStamp = new ApprovalDateStamp(content,
                    pageSize,
                    baseFont,
                    new Date(),
                    isPortraitMode);

            dateStamp.stampText();
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
        final InputStream inputStream=resourceLoader.getInputStream(name);
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
                    // APPROVAL_DATE_TEXT + new DateTime(date).toString("MM/dd/yyyy"),
                    new DateTime(date).toString("MM/dd/yyyy"),
                    6f,
                    161f,
                    30f,
                    174f,
                    16f);
        }

    }
    private abstract class TextStamp {
        protected final float textRotation = 0f;

        protected final PdfContentByte content;
        protected final Rectangle cropBox;
        protected final boolean isPortraitMode;
        protected final String textToPrint;

        protected final BaseFont baseFont;
        protected final float fontSize;

        protected final float rightMargin;
        protected final float bottomMargin;
        protected final float topMargin;
        protected final float leftMargin;

        protected TextStamp (PdfContentByte content,
                             Rectangle cropBox,
                             BaseFont baseFont,
                             boolean isPortraitMode,
                             String textToPrint,
                             float fontSize,
                             float rightMargin,
                             float bottomMargin,
                             float topMargin,
                             float leftMargin) {
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
            if(!this.isPortraitMode) {
                x = this.cropBox.getTop(topMargin);
                y = this.cropBox.getLeft(leftMargin);
            }
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, textToPrint, x, y, textRotation);

            this.content.endText();
        }
    }

}
