package lab.pdf.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class PdfStampService {

    @Resource
    private transient CustomerResourceLoader resourceLoader;
    @Resource
    private transient FooterService footerService;

    public ByteArrayOutputStream approvalStamp(final Date approvalDate,
                                               final Date expiryDate,
                                               final InputStream inputStream) throws IOException, DocumentException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final PdfReader pdfReader = new PdfReader(inputStream);
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);

        final Image image = Image.getInstance(getResource("approvalConsent.png"));
        final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        final int totalPages = pdfReader.getNumberOfPages();
        for (int i = 1; i <= totalPages; i++) {
            final Rectangle rectangle = pdfReader.getCropBox(i);
            // Ensure all the images will have a height of one inch.
            image.scaleToFit(1000f, 35.5f);

            //Stamp should be always on the right-bottom corner regardless of the original rotation.

            final int rotation = pdfReader.getPageRotation(i);
            final boolean isPortraitMode = rotation == 0 || rotation == 180;
            /*
            if (isPortraitMode) {
                image.setAbsolutePosition(rectangle.getRight(10f) - image.getScaledWidth(), rectangle.getBottom(20f));
            } else {
                image.setAbsolutePosition(rectangle.getTop(20f) - image.getScaledWidth(), rectangle.getLeft(5f));
            }
            */

            //put content over (not under)
            final PdfContentByte content = pdfStamper.getOverContent(i);
            final PdfGState pdfGState = new PdfGState();
            pdfGState.setFillOpacity(0.5f);
            content.setGState(pdfGState);
            content.saveState();
            // content.addImage(image);
            final Image waterMarkedImg = getWatermarkedImage(content, image, mmddyyyy(approvalDate), mmddyyyy(expiryDate));
            if (isPortraitMode) {
                waterMarkedImg.setAbsolutePosition(rectangle.getRight(10f) - image.getScaledWidth(), rectangle.getBottom(20f));
            } else {
                waterMarkedImg.setAbsolutePosition(rectangle.getTop(20f) - image.getScaledWidth(), rectangle.getLeft(5f));
            }

            content.addImage(waterMarkedImg);
            // getWatermarkedImage(content, image, mmddyyyy(approvalDate));

            /*
            final ApprovalDateStamp approvalDateStamp = new ApprovalDateStamp(content,
                    rectangle,
                    baseFont,
                    approvalDate,
                    isPortraitMode);
            approvalDateStamp.stampText();

            final ExpiryDateStamp expiryDateStamp = new ExpiryDateStamp(content,
                    rectangle,
                    baseFont,
                    expiryDate,
                    isPortraitMode);
            expiryDateStamp.stampText();
            */
            content.restoreState();
        }

        pdfStamper.close();
        pdfReader.close();
        return byteArrayOutputStream;
    }

    private void updateStampTable(final String fstLine,
                                  final PdfContentByte contentByte) {
        final PdfPTable pdfTable = footerService.getStampTable(fstLine);
        pdfTable.writeSelectedRows(0, -1, 460, 50, contentByte);
    }

    private byte[] getResource(final String name) throws IOException {
        final InputStream inputStream = resourceLoader.getInputStream(name);
        final byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return bytes;
    }

    public Image getWatermarkedImage(final PdfContentByte cb, final Image img,
                                     final String approval, final String expiry) throws DocumentException {
        final Font FONT = new Font(FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.GRAY.darker());

        float width = img.getScaledWidth();
        float height = img.getScaledHeight();
        PdfTemplate template = cb.createTemplate(width, height);
        template.addImage(img, width, 0, 0, height, 0, 0);

        ColumnText.showTextAligned(template, Element.ALIGN_CENTER,
                new Phrase(approval, FONT), (width / 2)+40, (height / 2)-5, 0);

        ColumnText.showTextAligned(template, Element.ALIGN_CENTER,
                new Phrase(expiry, FONT), (width / 2)+40, (height / 2)-14, 0);

        return Image.getInstance(template);
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
                    mmddyyyy(date),
                    6f + 2f,     // fontSize
                    161f - 55f,  // rightMargin
                    30f + 5f,    // bottomMargin
                    174f - 5f,   // topMargin
                    16f + 55f);  // leftMargin
        }
    }

    private class ExpiryDateStamp extends TextStamp {
        public ExpiryDateStamp(final PdfContentByte content,
                               final Rectangle cropBox,
                               final BaseFont baseFont,
                               final Date date,
                               final boolean isPortraitMode) {
            super(content,
                    cropBox,
                    baseFont,
                    isPortraitMode,
                    mmddyyyy(date),
                    6f + 2f, // fontSize
                    151f - 45f, // rightMargin
                    23 + 2f,   // bottomMargin
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

    public ByteArrayOutputStream ngsRuleFailStamp(final InputStream inputStream) throws IOException, DocumentException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final PdfReader pdfReader = new PdfReader(inputStream);
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);

        final Image image = Image.getInstance(getResource("inactiveConsentDevice.jpg"));
        final int totalPages = pdfReader.getNumberOfPages();
        for (int i = 1; i <= totalPages; i++) {
            final Rectangle rectangle = pdfReader.getCropBox(i);
            image.scaleToFit(1000f, 35.5f);
            final int rotation = pdfReader.getPageRotation(i);
            final boolean isPortraitMode = rotation == 0 || rotation == 180;
            if (isPortraitMode) {
                image.setAbsolutePosition(rectangle.getRight(10f) - image.getScaledWidth(), rectangle.getBottom(20f));
            } else {
                image.setAbsolutePosition(rectangle.getTop(20f) - image.getScaledWidth(), rectangle.getLeft(5f));
            }

            final PdfContentByte content = pdfStamper.getOverContent(i);
            final PdfGState pdfGState = new PdfGState();
            pdfGState.setFillOpacity(1f);

            content.setGState(pdfGState);
            content.saveState();
            content.addImage(image);

            content.restoreState();
        }
        pdfStamper.close();
        pdfReader.close();
        return byteArrayOutputStream;
    }


    public ByteArrayOutputStream tableStamp(final String stampText,
                                            final InputStream inputStream) throws IOException, DocumentException {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PdfReader pdfReader = new PdfReader(inputStream);
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);

        final int totalPages = pdfReader.getNumberOfPages();
        for (int page = 1; page <= totalPages; page++) {
            final PdfContentByte content = pdfStamper.getOverContent(page);
            final PdfGState pdfGState = new PdfGState();
            pdfGState.setFillOpacity(0.5f);
            content.setGState(pdfGState);
            content.saveState();
            updateStampTable(stampText, content);
            content.restoreState();
        }
        pdfStamper.close();
        pdfReader.close();
        return byteArrayOutputStream;
    }

    private String mmddyyyy(final Date date) {
        return date == null ? "" : new DateTime(date).toString("MM/dd/yyyy");
    }
}
