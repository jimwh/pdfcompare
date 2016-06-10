package lab.pdf;

import com.itextpdf.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import lab.pdf.conf.DataSourceConfig;
import lab.pdf.service.CustomerResourceLoader;
import lab.pdf.service.PdfBodyCompare;
import lab.pdf.service.PdfInfo;
import lab.pdf.service.PdfTextCompare;
import lab.pdf.service.PipelineService;
import lab.pdf.service.TextExtractor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("lab.pdf")
@SpringApplicationConfiguration(classes = {DataSourceConfig.class})
public class Application {

    static final String FstLine = "Medical Center Institutional Review Board: 212-851-7040";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) throws IOException, DocumentException {
        if( args.length < 2 ) {
            log.info("lab.pdf.Application <file1> <file2>");
            return;
        }
        log.info("start...{}, {}", args[0], args[1]);

        final ApplicationContext ctx = SpringApplication.run(Application.class, args);

        // pdfInfo(ctx, args[0]);

        // testResourceLoader(ctx);

        /*
        if( args.length == 3 ) {
            AddPdfFooter addPdfFooter=ctx.getBean(AddPdfFooter.class);
            addPdfFooter.addFooterInfo(args[2]);
        } else {
            FooBar fooBar = ctx.getBean(FooBar.class);
            log.info("foobar.dir={}", fooBar.getDownloadDir());
        }
        */

        // bodyCompare(ctx, args);

        // testApprovalStamp(ctx, args[0]);
        // testApprovalStamp(ctx, "/tmp/foo.pdf");
        // testApprovalStamp(ctx, "./LegacyConsentForm.pdf");

        testExpiryPipeline(ctx, "./LegacyConsentForm.pdf");
        testInactivePipeline(ctx, "./LegacyConsentForm.pdf");
        testSupersededPipeline(ctx, "./LegacyConsentForm.pdf");
        testNgsPipeline(ctx, "./LegacyConsentForm.pdf");
        testInactiveStamp(ctx, "./LegacyConsentForm.pdf");
        testSupersededStamp(ctx, "./LegacyConsentForm.pdf");

        // testNgsRuleFailStamp(ctx, "./LegacyConsentForm.pdf");
        // testExpiredStamp(ctx, "./LegacyConsentForm.pdf");

        // testEnrollmentClosedStamp(ctx, "./LegacyConsentForm.pdf");

        SpringApplication.exit(ctx);
        log.info("done...");
    }

    static void pdfInfo(final ApplicationContext ctx, final String fileName) throws IOException {
        final PdfInfo pdfInfo = ctx.getBean(PdfInfo.class);
        pdfInfo.printPdfInfo(fileName);
        log.info("isLegacy={}", pdfInfo.isLegacy(fileName));
    }

    static void textComparison(final ApplicationContext ctx, final String f1, final String f2) throws IOException {
        final TextExtractor textExtractor=ctx.getBean(TextExtractor.class);
        textExtractor.extract(f1);
        //
        final PdfTextCompare pdfTextCompare = ctx.getBean(PdfTextCompare.class);
        boolean bool = pdfTextCompare.compare(f1, f2);
        log.info("bool = {}", bool);
        bool = pdfTextCompare.compareFromInputstream(f1, f2);
        log.info("bool = {}", bool);
    }

    static void testResourceLoader(final ApplicationContext ctx) throws IOException {
        final CustomerResourceLoader resourceLoader=ctx.getBean(CustomerResourceLoader.class);
        resourceLoader.foobar();
    }

    static void bodyCompare(final ApplicationContext ctx, String[] args) throws IOException {
        final PdfBodyCompare bodyComapre=ctx.getBean(PdfBodyCompare.class);
        final boolean bool = bodyComapre.compareContent(args[1], args[2]);
        log.info("Is the same body: {}", bool);
    }


    static FileOutputStream getFileOutputStream(final String name) throws FileNotFoundException {
        return new FileOutputStream(name);
    }

    static InputStream getInputStreamFromFile(final String name) throws FileNotFoundException {
        return new FileInputStream(name);
    }

    static void testInactiveStamp(final ApplicationContext ctx, final String fileName) throws IOException, DocumentException {
        log.info("input fileName={}", fileName);
        final String consentNumber="CF-ABCD5678";
        final String fromNumber="CF-ABCD1234";
        final InputStream inputStream = getInputStreamFromFile(fileName);

        final PipelineService pipelineService=ctx.getBean(PipelineService.class);

        ByteArrayOutputStream wout = pipelineService.inactiveNoApprovalStamp(
                FstLine, consentNumber, fromNumber, inputStream);

        log.info("output fileName=/tmp/stamped.pdf");
        FileOutputStream fileOutputStream=getFileOutputStream("/tmp/stamped.pdf");
        wout.writeTo(fileOutputStream);
        fileOutputStream.close();
        inputStream.close();
    }

    static void testSupersededStamp(final ApplicationContext ctx, final String fileName) throws IOException, DocumentException {
        log.info("input fileName={}", fileName);
        final String consentNumber="CF-ABCD5678";
        final String fromNumber="CF-ABCD1234";
        final InputStream inputStream = getInputStreamFromFile(fileName);

        final PipelineService pipelineService=ctx.getBean(PipelineService.class);
        ByteArrayOutputStream wout = pipelineService.supersededNoApprovalStamp(
                FstLine, consentNumber, fromNumber, inputStream);

        log.info("output fileName=/tmp/stamped.pdf");
        FileOutputStream fileOutputStream=getFileOutputStream("/tmp/stamped.pdf");
        wout.writeTo(fileOutputStream);
        fileOutputStream.close();
        inputStream.close();
    }


    static void testEnrollmentClosedStamp(final ApplicationContext ctx, final String fileName) throws IOException, DocumentException {
        log.info("input fileName={}", fileName);
        final String consentNumber="CF-ABCD5678";
        final String fromNumber="CF-ABCD1234";
        final Date approvalDate = DateTime.now().toDate();
        final Date expiryDate = DateTime.now().plusYears(1).toDate();
        final InputStream inputStream = getInputStreamFromFile(fileName);

        final PipelineService pipelineService=ctx.getBean(PipelineService.class);
        final ByteArrayOutputStream outputStream = pipelineService.closedEnrollmentWithApprovalInfo(
                FstLine, consentNumber, fromNumber, approvalDate, expiryDate, inputStream);
        //
        log.info("output fileName=/tmp/stamped.pdf");
        FileOutputStream fileOutputStream=getFileOutputStream("/tmp/stamped.pdf");
        outputStream.writeTo(fileOutputStream);
        fileOutputStream.close();
        inputStream.close();
    }

    static void testExpiryPipeline(final ApplicationContext ctx, final String fileName) throws IOException, DocumentException {
        log.info("input fileName={}", fileName);
        final String consentNumber="CF-ABCD5678";
        final String fromNumber="CF-ABCD1234";
        final Date approvalDate = DateTime.now().toDate();
        final Date expiryDate = DateTime.now().plusYears(1).toDate();

        final PipelineService pipelineService=ctx.getBean(PipelineService.class);
        final InputStream inputStream = getInputStreamFromFile(fileName);

        final ByteArrayOutputStream outputStream = pipelineService.expiryPipeline(
                FstLine, consentNumber, fromNumber, approvalDate, expiryDate, inputStream);

        log.info("output fileName=/tmp/stamped.pdf");
        FileOutputStream fileOutputStream=getFileOutputStream("/tmp/stamped.pdf");
        outputStream.writeTo(fileOutputStream);
        fileOutputStream.close();
        inputStream.close();
    }

    static void testInactivePipeline(final ApplicationContext ctx, final String fileName) throws IOException, DocumentException {
        log.info("input fileName={}", fileName);
        final String consentNumber="CF-ABCD5678";
        final String fromNumber="CF-ABCD1234";
        final Date approvalDate = DateTime.now().toDate();
        final Date expiryDate = DateTime.now().plusYears(1).toDate();

        final PipelineService pipelineService=ctx.getBean(PipelineService.class);
        final InputStream inputStream = getInputStreamFromFile(fileName);

        final ByteArrayOutputStream outputStream = pipelineService.inactiveWithApprovalInfo(
                FstLine, consentNumber, fromNumber, approvalDate, expiryDate, inputStream);

        log.info("output fileName=/tmp/stamped.pdf");
        FileOutputStream fileOutputStream=getFileOutputStream("/tmp/stamped.pdf");
        outputStream.writeTo(fileOutputStream);
        fileOutputStream.close();
        inputStream.close();
    }

    static void testSupersededPipeline(final ApplicationContext ctx, final String fileName) throws IOException, DocumentException {
        log.info("input fileName={}", fileName);
        final String consentNumber="CF-ABCD5678";
        final String fromNumber="CF-ABCD1234";
        final Date approvalDate = DateTime.now().toDate();
        final Date expiryDate = DateTime.now().plusYears(1).toDate();

        final PipelineService pipelineService=ctx.getBean(PipelineService.class);
        final InputStream inputStream = getInputStreamFromFile(fileName);

        final ByteArrayOutputStream outputStream = pipelineService.supersededWithApprovalInfo(
                FstLine, consentNumber, fromNumber, approvalDate, expiryDate, inputStream);

        log.info("output fileName=/tmp/stamped.pdf");
        FileOutputStream fileOutputStream=getFileOutputStream("/tmp/stamped.pdf");
        outputStream.writeTo(fileOutputStream);
        fileOutputStream.close();
        inputStream.close();
    }

    static void testNgsPipeline(final ApplicationContext ctx, final String fileName) throws IOException, DocumentException {
        log.info("input fileName={}", fileName);
        final String consentNumber="CF-ABCD5678";
        final String fromNumber="CF-ABCD1234";

        final InputStream inputStream = getInputStreamFromFile(fileName);

        final PipelineService pipelineService = ctx.getBean(PipelineService.class);
        ByteArrayOutputStream wout = pipelineService.ngsRuleFailStamp(FstLine, consentNumber, fromNumber, inputStream);

        log.info("output fileName=/tmp/stamped.pdf");
        FileOutputStream fileOutputStream=getFileOutputStream("/tmp/stamped.pdf");
        wout.writeTo(fileOutputStream);
        fileOutputStream.close();
        inputStream.close();
    }

}