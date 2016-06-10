package lab.pdf.service;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Component
public class PipelineService {

    @Resource
    private FooterService footerService;
    @Resource
    private PdfStampService stampService;
    @Resource
    private WatermarkService watermarkService;

    public ByteArrayOutputStream approvalPipeline(final String fstLine,
                                                  final String consentNumber,
                                                  final String fromNumber,
                                                  final Date approvalDate,
                                                  final Date expiryDate,
                                                  final InputStream inputStream) throws IOException, DocumentException {
        final String numberLine = FooterService.getNumberLine(consentNumber, fromNumber);
        final ByteArrayOutputStream footerOutput = footerService.addFooterInfo(fstLine, numberLine, inputStream);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(footerOutput.toByteArray());

        final ByteArrayOutputStream outputStream = stampService.approvalStamper(
                approvalDate, expiryDate,
                byteArrayInputStream);
        return outputStream;
    }

    public ByteArrayOutputStream expiryPipeline(final String fstLine,
                                                final String consentNumber,
                                                final String fromNumber,
                                                final Date approvalDate,
                                                final Date expiryDate,
                                                final InputStream inputStream) throws IOException, DocumentException {
        final ByteArrayOutputStream approvalOutput = approvalPipeline(fstLine,
                consentNumber,
                fromNumber,
                approvalDate,
                expiryDate,
                inputStream);
        return watermarkService.waterMark(WatermarkService.Status.Expired, approvalOutput);
    }

    public ByteArrayOutputStream closedEnrollmentWithApprovalInfo(final String fstLine,
                                                                  final String consentNumber,
                                                                  final String fromNumber,
                                                                  final Date approvalDate,
                                                                  final Date expiryDate,
                                                                  final InputStream inputStream) throws IOException, DocumentException {
        final ByteArrayOutputStream approvalOutput = approvalPipeline(fstLine,
                consentNumber,
                fromNumber,
                approvalDate,
                expiryDate,
                inputStream);
        return watermarkService.waterMark(WatermarkService.Status.ClosedEnrollment, approvalOutput);
    }

    public ByteArrayOutputStream inactiveWithApprovalInfo(final String fstLine,
                                                          final String consentNumber,
                                                          final String fromNumber,
                                                          final Date approvalDate,
                                                          final Date expiryDate,
                                                          final InputStream inputStream) throws IOException, DocumentException {
        final ByteArrayOutputStream approvalOutput = approvalPipeline(fstLine,
                consentNumber,
                fromNumber,
                approvalDate,
                expiryDate,
                inputStream);
        return watermarkService.waterMark(WatermarkService.Status.Inactive, approvalOutput);
    }

    public ByteArrayOutputStream inactiveNoApprovalStamp(final String fstLine,
                                                         final String consentNumber,
                                                         final String fromNumber,
                                                         final InputStream inputStream) throws IOException, DocumentException {

        final String numberLine = FooterService.getNumberLine(consentNumber, fromNumber);
        final ByteArrayOutputStream footerOutput = footerService.addFooterInfo(fstLine, numberLine, inputStream);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(footerOutput.toByteArray());
        final ByteArrayOutputStream outputStream = stampService.tableStamp(
                WatermarkService.Status.Inactive.toString(), byteArrayInputStream);
        return watermarkService.waterMark(WatermarkService.Status.Inactive.toString(), outputStream);
    }

    public ByteArrayOutputStream supersededWithApprovalInfo(final String fstLine,
                                                            final String consentNumber,
                                                            final String fromNumber,
                                                            final Date approvalDate,
                                                            final Date expiryDate,
                                                            final InputStream inputStream) throws IOException, DocumentException {
        final ByteArrayOutputStream approvalOutput = approvalPipeline(fstLine,
                consentNumber,
                fromNumber,
                approvalDate,
                expiryDate,
                inputStream);
        return watermarkService.waterMark(WatermarkService.Status.Superseded, approvalOutput);
    }

    public ByteArrayOutputStream supersededNoApprovalStamp(final String fstLine,
                                                           final String consentNumber,
                                                           final String fromNumber,
                                                           final InputStream inputStream) throws IOException, DocumentException {
        final String numberLine = FooterService.getNumberLine(consentNumber, fromNumber);
        final ByteArrayOutputStream footerOutput = footerService.addFooterInfo(fstLine, numberLine, inputStream);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(footerOutput.toByteArray());
        final ByteArrayOutputStream outputStream = stampService.tableStamp(
                WatermarkService.Status.Superseded.toString(), byteArrayInputStream);
        return watermarkService.waterMark(WatermarkService.Status.Superseded.toString(), outputStream);
    }

    public ByteArrayOutputStream ngsRuleFailStamp(final String fstLine,
                                                  final String consentNumber,
                                                  final String fromNumber,
                                                  final InputStream inputStream) throws IOException, DocumentException {
        final String numberLine = FooterService.getNumberLine(consentNumber, fromNumber);
        final ByteArrayOutputStream footerOutput = footerService.addFooterInfo(fstLine, numberLine, inputStream);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(footerOutput.toByteArray());
        final ByteArrayOutputStream outputStream = stampService.ngsRuleFailStamp(byteArrayInputStream);
        return watermarkService.waterMark(outputStream);
    }

}
