package lab.pdf.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FooBar {

    @Resource
    private Environment env;

    public String getDownloadDir() {
        return env.getProperty("downloadDirectory");
    }
}