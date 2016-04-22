package lab.pdf.service;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.ConfigurationFactory;
import com.inet.pdfc.config.IConfiguration;
import com.inet.pdfc.config.PDFCProperty;
import com.inet.pdfc.results.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.regex.Pattern;

@Component
public class FooBar {

    static final Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
    static final Logger log = LoggerFactory.getLogger(FooBar.class);

    @Resource
    private Environment env;

    public String getDownloadDir() {
        final String foo = "abcegf@yahafbcoo.com";
        log.info("matches={}", pattern.matcher(foo).matches());
        return env.getProperty("downloadDirectory");
    }


    public void testCompare(final String f1Name, final  String f2Name) {
        File f1 = new File(f1Name);
        File f2 = new File(f2Name);
        IConfiguration configuration = ConfigurationFactory.getConfiguration();
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE, Boolean.TRUE);
        PDFComparer pdfComparer = new PDFComparer();
        pdfComparer.setConfiguration(configuration);
        ResultModel resultModel = pdfComparer.compare(f1, f2);
        log.info("result={}", resultModel.isEmpty());
    }
}