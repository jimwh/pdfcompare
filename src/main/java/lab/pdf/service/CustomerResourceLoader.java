package lab.pdf.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class CustomerResourceLoader implements ResourceLoaderAware {

    private transient ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public InputStream getInputStream(final String location) throws IOException {

        if(location==null) {
            throw new IOException("location cannot be null");
        }

        return location.contains(ResourceLoader.CLASSPATH_URL_PREFIX) ?
                getResourceStream(location) :
                getResourceStream(ResourceLoader.CLASSPATH_URL_PREFIX + location);
    }

    private InputStream getResourceStream(final String location) throws IOException {
        final Resource resource = resourceLoader.getResource(location);
        return resource==null ? null : resource.getInputStream();
    }

    public void foobar() throws IOException {
        InputStream inputStream=getInputStream("approvalConsent.png");
        IOUtils.copy(inputStream, new FileOutputStream(new File("/tmp/approvalConsent.png")));
    }
}