package lab.pdf.service;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.CompareType;
import com.inet.pdfc.config.ConfigurationFactory;
import com.inet.pdfc.config.IConfiguration;
import com.inet.pdfc.config.PDFCProperty;
import com.inet.pdfc.generator.model.DiffGroup;
import com.inet.pdfc.normalizers.NormalizerType;
import com.inet.pdfc.results.ResultModel;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by jh3389 on 5/22/16.
 */
@Component
public class ConsentComapre {

    static final Logger log = LoggerFactory.getLogger(ConsentComapre.class);

    public void testCompare(final String f1Name, final String f2Name) {
        try {
            doCompare(f1Name, f2Name);
        } catch (Exception e) {
            log.error("caught:", e);
        }
    }

    /*
    private IConfiguration getConfiguration() {
        final IConfiguration configuration = ConfigurationFactory.getConfiguration();
        configuration.putObject(PDFCProperty.CREATE_DIFFIMAGES, Boolean.FALSE);
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE, Boolean.FALSE);
        //
        configuration.putObject(PDFCProperty.NORMALIZERS, NormalizerType.HEADER_FOOTER);
        configuration.putObject(PDFCProperty.FIXED_FOOTER_SIZE, 60);
        configuration.putObject(PDFCProperty.LOG_LEVEL, "ERROR");
        configuration.putObject(PDFCProperty.COMPARE_TEXT_STYLES, Boolean.FALSE);
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE_TYPES, "" + CompareType.TEXT + "");
        return configuration;
    }
    */

    private IConfiguration getConfiguration() {
        final IConfiguration configuration = ConfigurationFactory.getConfiguration();
        configuration.putObject(PDFCProperty.CREATE_DIFFIMAGES, Boolean.FALSE);
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE, Boolean.FALSE);
        configuration.putObject(PDFCProperty.NORMALIZERS, NormalizerType.HEADER_FOOTER);
        configuration.putObject(PDFCProperty.COMPARE_TEXT_STYLES, Boolean.FALSE);
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE_TYPES, CompareType.TEXT);
        // TO DO: footer size is subject to change!!!
        configuration.putObject(PDFCProperty.FIXED_FOOTER_SIZE, 60);
        configuration.putObject(PDFCProperty.LOG_LEVEL, "ERROR");
        return configuration;
    }

    void doCompare(final String f1Name, final String f2Name) {

        final File f1 = new File(f1Name);
        final File f2 = new File(f2Name);
        final IConfiguration configuration = getConfiguration();
        /*
        ConfigurationFactory.getConfiguration();
        configuration.putObject(PDFCProperty.CREATE_DIFFIMAGES, Boolean.FALSE);
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE, Boolean.FALSE);
        //
        configuration.putObject(PDFCProperty.NORMALIZERS, NormalizerType.HEADER_FOOTER);
        configuration.putObject(PDFCProperty.FIXED_FOOTER_SIZE, 60);
        configuration.putObject(PDFCProperty.LOG_LEVEL, "ERROR");
        configuration.putObject(PDFCProperty.COMPARE_TEXT_STYLES, Boolean.FALSE);
        configuration.putObject( PDFCProperty.CONTINUOUS_COMPARE_TYPES, "" + CompareType.TEXT + "");
        */

        /*
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE, Boolean.TRUE);
        configuration.putObject(PDFCProperty.COMPARE_TEXT_CASE_SENSITIVE, Boolean.TRUE);

        configuration.putObject(PDFCProperty.TOLERANCE_TEXT_SIZE, Boolean.FALSE);
        */
        //configuration.putObject(PDFCProperty.FIXED_HEADER_SIZE, Boolean.FALSE);
        //configuration.putObject(PDFCProperty.FIXED_FOOTER_SIZE, Boolean.FALSE);
        //
        // configuration.putObject( PDFCProperty.CONTINUOUS_COMPARE_TYPES, "" + CompareType.TEXT + ", " + CompareType.LINE + ", " + CompareType.IMAGE );
        // configuration.putObject( PDFCProperty.CONTINUOUS_COMPARE_TYPES, ""+CompareType.TEXT+"");
        // configuration.putObject( PDFCProperty.FILTER_PATTERNS, "Radiation risks");
        // configuration.putObject( PDFCProperty.FILTER_PATTERNS, "Printed On");
        //
        final PDFComparer pdfComparer = new PDFComparer();
        pdfComparer.setConfiguration(configuration);

        final ResultModel resultModel = pdfComparer.compare(f1, f2);
        log.info("resultModel.isEmpty={}", resultModel.isEmpty());
        log.info("resultModel.getDifferencesCount(true)={}", resultModel.getDifferencesCount(true));
        log.info("resultModel.getDifferencesCount(false)={}", resultModel.getDifferencesCount(false));
        final List<DiffGroup> diffGroupList = resultModel.getDifferences(true);
        log.info("diffGroupList.isEmpty={}", diffGroupList.isEmpty());
        log.info("diffGroupList={}", diffGroupList.size());

    }


}
