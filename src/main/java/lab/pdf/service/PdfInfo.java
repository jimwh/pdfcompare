package lab.pdf.service;

import com.itextpdf.text.pdf.PdfReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class PdfInfo {

    private static final Logger log = LoggerFactory.getLogger(PdfInfo.class);

    public void printPdfInfo(final String fileName) throws IOException {
        log.info("fileName={}", fileName);
        final InputStream inputStream = inputStream(fileName);
        final Map<String, String> map = printPdfInfo(inputStream);
        inputStream.close();
        for(final Map.Entry<String,String>e: map.entrySet()) {
            log.info("key={}, value={}", e.getKey(), e.getValue());
        }

    }

    public Map<String,String> printPdfInfo(final InputStream is) throws IOException {
        final PdfReader pdfReader = new PdfReader(is);
        return pdfReader.getInfo();
    }

    public boolean isLegacy(final InputStream is) throws IOException {
        final Map<String,String> map = printPdfInfo(is);
        final String value = map.get("Producer");
        return value.contains("big.faceless.org");
    }

    public boolean isLegacy(final String fileName) throws IOException {
        final InputStream inputStream=inputStream(fileName);
        final boolean bool = isLegacy(inputStream);
        inputStream.close();
        return bool;
    }

    private InputStream inputStream(final String fileName) throws FileNotFoundException {
        return new FileInputStream(new File(fileName));
    }
}
