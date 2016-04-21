package lab.pdf;

import lab.pdf.conf.DataSourceConfig;

import lab.pdf.service.FooBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("lab.pdf")
@SpringApplicationConfiguration(classes = {DataSourceConfig.class})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        log.info("start...");

        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        FooBar fooBar=ctx.getBean(FooBar.class);
        log.info("foobar.dir={}", fooBar.getDownloadDir());

        SpringApplication.exit(ctx);

        log.info("done...");
    }

}