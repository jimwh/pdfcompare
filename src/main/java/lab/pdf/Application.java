package lab.pdf;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import lab.pdf.conf.DataSourceConfig;
import lab.pdf.service.CustomerResourceLoader;
import lab.pdf.service.PdfBodyCompare;
import lab.pdf.service.PdfInfo;
import lab.pdf.service.PdfTextCompare;
import lab.pdf.service.TextExtractor;
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

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) throws IOException, DocumentException {
        if( args.length < 2 ) {
            log.info("lab.pdf.Application <file1> <file2>");
            return;
        }
        log.info("start...{}, {}", args[0], args[1]);

        final ApplicationContext ctx = SpringApplication.run(Application.class, args);

        pdfInfo(ctx, args[0]);

        testResourceLoader(ctx);

        /*
        if( args.length == 3 ) {
            AddPdfFooter addPdfFooter=ctx.getBean(AddPdfFooter.class);
            addPdfFooter.addFooter(args[2]);
        } else {
            FooBar fooBar = ctx.getBean(FooBar.class);
            log.info("foobar.dir={}", fooBar.getDownloadDir());
        }
        */

        bodyCompare(ctx, args);

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

    /*
    static boolean test() {
        return size1()==size2() || size3();
    }
    static int size1() {
        log.info("size 1");
        return 1;
    }
    static int size2() {
        log.info("size 2");
        return 1;
    }
    static boolean size3() {
        log.info("size 3");
        return true;
    }
    */
}