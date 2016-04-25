package lab.pdf.service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TextExtractor {
    private static final Logger log = LoggerFactory.getLogger(TextExtractor.class);

    public void extract(final String fileName) throws IOException {
        final PdfReader reader = new PdfReader(fileName);
        final int numberOfPages = reader.getNumberOfPages();
        for(int i = 1; i<=numberOfPages; i++) {
            //log.info("text page {}={}", i, PdfTextExtractor.getTextFromPage(reader, i));
            log.info("page {}", i);
            String[] arrayStr = PdfTextExtractor.getTextFromPage(reader, i).split("\n");
            for(String str: arrayStr) {
                log.info("str={}", str);
            }
        }
    }
}
