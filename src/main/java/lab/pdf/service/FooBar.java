package lab.pdf.service;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.CompareType;
import com.inet.pdfc.config.ConfigurationFactory;
import com.inet.pdfc.config.FilterType;
import com.inet.pdfc.config.IConfiguration;
import com.inet.pdfc.config.PDFCProperty;
import com.inet.pdfc.generator.message.HighlightData;
import com.inet.pdfc.generator.message.InfoData;
import com.inet.pdfc.generator.model.DiffGroup;
import com.inet.pdfc.generator.model.IDiffGroupBounds;
import com.inet.pdfc.generator.model.Modification;
import com.inet.pdfc.normalizers.NormalizerType;
import com.inet.pdfc.results.ResultModel;
import inet.elements.PagedElement;
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
        configuration.putObject(PDFCProperty.CREATE_DIFFIMAGES, Boolean.FALSE);
        configuration.putObject(PDFCProperty.CONTINUOUS_COMPARE, Boolean.FALSE);
        //
        configuration.putObject(PDFCProperty.NORMALIZERS, NormalizerType.HEADER_FOOTER);
        configuration.putObject(PDFCProperty.FIXED_FOOTER_SIZE, 60);
        configuration.putObject(PDFCProperty.LOG_LEVEL, "ERROR");
        configuration.putObject(PDFCProperty.COMPARE_TEXT_STYLES, Boolean.FALSE);
        configuration.putObject( PDFCProperty.CONTINUOUS_COMPARE_TYPES, "" + CompareType.TEXT + "");


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

        /*
        final InfoData infoData = resultModel.getComparisonParameters();
        int firstTotalPageNumber = infoData.getFirstTotalPageNumber();
        int secondTotalPageNumber = infoData.getSecondTotalPageNumber();
        log.info("{}, {}", firstTotalPageNumber, secondTotalPageNumber);
        //
        // resultModel.setHighlightVisibile(FilterType.TYPE.REGEXP, true);
        resultModel.setHighlightVisibile(FilterType.HEADERFOOTER, true);
        // HighlightData highlightData = resultModel.getHighlightData(FilterType.TYPE.REGEXP);
        HighlightData highlightData = resultModel.getHighlightData(FilterType.HEADERFOOTER);
        if(highlightData != null) {
            log.info("highlightData={}", highlightData.toString());
        }
        //
        final List<DiffGroup> diffGroupList = resultModel.getDifferences(true);
        if( diffGroupList.isEmpty() ) {
            log.info("empty diff groups");
            return;
        } else {
            log.info("diffGroupList={}", diffGroupList.size());
        }

        for (final DiffGroup group : diffGroupList) {
            if(group.getType() == DiffGroup.GroupType.Sync) {
                continue;
            }
            log.info("groupType={}", group.getType());
            final List<Modification> modificationList = group.getModifications();
            if(modificationList == null) {
                continue;
            }
            for (final Modification m : modificationList) {
                Modification.ModificationType mt = m.getModificationType();
                log.info("mt={}", mt);
                // Modifications don't have intrinsic page numbers. We use a small method here to extract the
                // page numbers from the modification.
                Range leftPages = getPageNumbers( m, group, true );
                Range rightPages = getPageNumbers( m, group, false );

                // Each group has a human readable, localized message. You can as well generate a custom message on your
                // own or use an extended the ModificationFormatter.
                final String message = m.getMessage();
                String pages = leftPages.equals( rightPages ) ? "Page " + leftPages : "Page (first)" + leftPages + " and (second)" + rightPages;
                log.info("{} : {}", pages, message );

            }
        }
        */
    }

    /**
     * Returns the page numbers of a modification<BR>
     * <b>A short explanation:</b><br>
     * In the continuous comparison mode, the differences can span several pages or be on different pages in the two documents.
     * As a result, there is no simple 'getPage()' method for modifications since there is not one single page number in any case.
     * This method derives the page number from the modified elements. In case there are no elements in the document
     * (in case of add or remove), the matched synchronization elements before and after the modification point will
     * be used as a fall back.
     * @param m the modification to get the page numbers for
     * @param group the {@link DiffGroup} to which the modification belongs
     * @param first <code>true</code> to get the numbers for the first document, <code>false</code> for the second one
     * @return a Range with the start and end page number of the modification
     */
    private static Range getPageNumbers(Modification m, DiffGroup group, boolean first) {
        int lower = Integer.MAX_VALUE;
        int upper = 0;
        List<PagedElement> elements = m.getAffectedElements(first);
        if( elements == null || elements.isEmpty() ) {
            // if there are no affected elements on this side, e.G. in case of a remove there are no elements
            // in the second document, fall back to the position of the matched elements before and after the group
            IDiffGroupBounds bounds = group.getBoundingElements();
            PagedElement before = first ? bounds.getBeforeFirst() : bounds.getBeforeSecond();
            PagedElement after = first ? bounds.getAfterFirst() : bounds.getAfterSecond();;
            if( before != null ) {
                lower = before.getPageIndex();
            }
            if( after != null ) {
                upper = after.getPageIndex();
            }
        } else {
            // there are affected elements, use their page numbers
            for( PagedElement element : elements ) {
                lower = Math.min( lower, element.getPageIndex() );
                upper = Math.max( upper, element.getPageIndex() );
                log.info("element={}, label={}, type={}", element.toString(), element.getLabel(), element.getType().nameLocalized());
            }
        }
        upper = Math.min( lower, upper ); // required if there is no content ad all in a document
        return new Range( lower + 1, upper + 1 ); // +1 to get the page number instead of index
    }

    /**
     * A simple class to conveniently store an integer range. The range can be inverse, there is no guarantee
     * that the lower bound is smaller than the upper bound
     */
    public static class Range {

        private int lower;
        private int upper;

        /**
         * Creates a range
         * @param lower the lower bound value, included
         * @param upper the upper bound value, included
         */
        public Range( int lower, int upper ) {
            this.lower = lower;
            this.upper = upper;
        }

        /**
         * Returns the lower bound value, included
         * @return the lower bound value, included
         */
        public int getLowerBound() {
            return lower;
        }

        /**
         * Returns upper the upper bound value, included
         * @return upper the upper bound value, included
         */
        public int getUpperBound() {
            return upper;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if( lower == upper ) {
                return Integer.toString( lower );
            } else {
                return lower + " to " + upper;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals( Object obj ) {
            if( !(obj instanceof Range)) {
                return false;
            }
            Range other = (Range)obj;
            return lower == other.lower && upper == other.upper;
        }
    }
}