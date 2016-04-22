package lab.pdf.service;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.ConfigurationFactory;
import com.inet.pdfc.config.IConfiguration;
import com.inet.pdfc.config.PDFCProperty;
import com.inet.pdfc.generator.model.DiffGroup;
import com.inet.pdfc.generator.model.Modification;
import com.inet.pdfc.results.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
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

    public void testCompare(final String f1Name, final String f2Name) {
        try {
            doCompare(f1Name, f2Name);
        } catch (Exception e) {
            log.error("caught:", e);
        }
    }


    void doCompare(final String f1Name, final String f2Name) {
        final File f1 = new File(f1Name);
        final File f2 = new File(f2Name);
        final IConfiguration configuration = ConfigurationFactory.getConfiguration();
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE, Boolean.TRUE);
        configuration.putObject(PDFCProperty.COMPARE_TEXT_CASE_SENSITIVE, Boolean.TRUE);
        configuration.putObject(PDFCProperty.COMPARE_TEXT_STYLES, Boolean.FALSE);
        //configuration.putObject(PDFCProperty.TOLERANCE_TEXT_SIZE, Boolean.TRUE);
        final PDFComparer pdfComparer = new PDFComparer();
        pdfComparer.setConfiguration(configuration);

        final ResultModel resultModel = pdfComparer.compare(f1, f2);
        log.info("result={}", resultModel.isEmpty());

        final List<DiffGroup> diffGroupList = resultModel.getDifferences(true);
        log.info("diffGroupList={}", diffGroupList.size());
        for (final DiffGroup dg : diffGroupList) {
            final List<Modification> mlist = dg.getModifications();
            DiffGroup.GroupType gt=dg.getType();
            log.info("gt.toString=", gt.toString());
            if(mlist == null) continue;
            log.info("mlist={}", mlist.size());
            for (final Modification m : mlist) {
                log.info("m={}, m.toString=", m.getMessage(), m.toString());
            }
        }
    }
}