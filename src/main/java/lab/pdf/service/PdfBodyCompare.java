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

@Component
public class PdfBodyCompare {

    static final Logger log = LoggerFactory.getLogger(PdfBodyCompare.class);

    public boolean compareContent(final String f1Name, final String f2Name) {

        final File f1 = new File(f1Name);
        final File f2 = new File(f2Name);
        final IConfiguration configuration = getConfiguration();

        final PDFComparer pdfComparer = new PDFComparer();
        pdfComparer.setConfiguration(configuration);

        final ResultModel resultModel = pdfComparer.compare(f1, f2);
        log.info("resultModel.isEmpty={}", resultModel.isEmpty());
        log.info("resultModel.getDifferencesCount(true)={}", resultModel.getDifferencesCount(true));
        log.info("resultModel.getDifferencesCount(false)={}", resultModel.getDifferencesCount(false));

        final List<DiffGroup> diffGroupList = resultModel.getDifferences(true);
        log.info("diffGroupList.isEmpty={}", diffGroupList.isEmpty());
        log.info("diffGroupList={}", diffGroupList.size());

        return resultModel.getDifferencesCount(true)==0;
    }

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

}
