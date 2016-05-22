package lab.pdf.service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfTextCompare {
    private static final Logger log = LoggerFactory.getLogger(PdfTextCompare.class);

    public boolean compareFromInputstream(final String fileName1, final String fileName2) throws IOException {
        final InputStream in1 = new FileInputStream(fileName1);
        final InputStream in2 = new FileInputStream(fileName2);
        return compare(in1, in2);
    }

    public boolean compare(final String fileName1, final String fileName2) throws IOException {
        final PdfReader reader1 = new PdfReader(fileName1);
        final PdfReader reader2 = new PdfReader(fileName2);
        return isSameNumberOfPages(reader1, reader2) || doCompare(reader1, reader2);
    }

    public boolean compare(final ByteArrayInputStream b1, final ByteArrayInputStream b2) throws IOException {
        final PdfReader reader1 = new PdfReader(b1);
        final PdfReader reader2 = new PdfReader(b2);
        return isSameNumberOfPages(reader1, reader2) || doCompare(reader1, reader2);
    }

    public boolean compare(final InputStream in1, final InputStream in2) throws IOException {
        final PdfReader reader1 = new PdfReader(in1);
        final PdfReader reader2 = new PdfReader(in2);
        return !isSameNumberOfPages(reader1, reader2) || doCompare(reader1, reader2);
    }

    private boolean isSameNumberOfPages(final PdfReader reader1, final PdfReader reader2) {
        return  reader1.getNumberOfPages() == reader2.getNumberOfPages();
    }

    private boolean doCompare(final PdfReader reader1, final PdfReader reader2) throws IOException {
        log.info("doCompare...");
        final List<String> list1 = getLineData(reader1);
        final List<String> list2 = getLineData(reader2);
        return list1.size()==list2.size() && doCompare(list1, list2);
    }

    private boolean doCompare(final List<String> list1, final List<String>list2) {
        log.info("do compare list...");
        boolean isSame = true;
        for(int i = 0; i<list1.size(); i++) {
            final String str1 = list1.get(i);
            if(str1.contains("Printed On:")) {
                continue;
            }
            final String str2 = list2.get(i);
            if( !str1.equals(str2) ) {
                log.info("str1={}, str2={}", str1, str2);
                isSame = false;
                break;
            }
            log.info("str1={}, str2={}", str1, str2);
        }
        return isSame;
    }

    private List<String> getLineData(final PdfReader reader) throws IOException {
        final List<String> list = new ArrayList<String>();
        for(int i=1; i<=reader.getNumberOfPages(); i++) {
            final String pageText = PdfTextExtractor.getTextFromPage(reader, i);
            if( !StringUtils.isBlank(pageText) ) {
                final String[] lineArray = pageText.split("\n");
                for(final String lineText: lineArray) {
                    if( !StringUtils.isBlank(lineText) ) {
                        list.add(lineText);
                    }
                }
                // list.addAll(Arrays.asList(arrayStr));
            }
        }
        return list;
    }

}
