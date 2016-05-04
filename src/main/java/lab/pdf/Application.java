package lab.pdf;

import com.itextpdf.text.DocumentException;
import lab.pdf.conf.DataSourceConfig;

import lab.pdf.service.AddPdfFooter;
import lab.pdf.service.FooBar;
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

import java.io.IOException;

@Configuration
@EnableAutoConfiguration
@ComponentScan("lab.pdf")
@SpringApplicationConfiguration(classes = {DataSourceConfig.class})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, DocumentException {
        if( args.length < 2 ) {
            log.info("lab.pdf.Application <file1> <file2>");
            System.exit(1);
        }
        log.info("start...{}, {}", args[0], args[1]);

        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        if( args.length == 3 ) {
            AddPdfFooter addPdfFooter=ctx.getBean(AddPdfFooter.class);
            addPdfFooter.addFooter(args[2]);
            SpringApplication.exit(ctx);
            return;
        }

        FooBar fooBar=ctx.getBean(FooBar.class);
        log.info("foobar.dir={}", fooBar.getDownloadDir());
        fooBar.testCompare(args[0], args[1]);

        /*
        TextExtractor textExtractor=ctx.getBean(TextExtractor.class);
        textExtractor.extract(args[0]);
        PdfTextCompare pdfTextCompare = ctx.getBean(PdfTextCompare.class);
        boolean bool = pdfTextCompare.compare(args[0], args[1]);
        log.info("bool = {}", bool);

        bool = pdfTextCompare.compareFromInputstream(args[0], args[1]);
        log.info("bool = {}", bool);

        SpringApplication.exit(ctx);

        test();
        */

        log.info("done...");
    }

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
}