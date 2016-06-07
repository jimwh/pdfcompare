package lab.pdf.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

}