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
        for(int i = 1; i<=reader.getNumberOfPages(); i++) {
            log.info("text={}", PdfTextExtractor.getTextFromPage(reader, i));
        }
    }
}
