package com.yunqiic.cocojob.server;

import com.yunqiic.cocojob.server.common.utils.PropertyUtils;
import com.yunqiic.cocojob.server.remote.transport.starter.AkkaStarter;
import com.yunqiic.cocojob.server.remote.transport.starter.VertXStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * cocojob-server entry
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
public class CocoJobServerApplication {

    private static final String TIPS = "\n\n" +
            "******************* CocoJob Tips *******************\n" +
            "如果应用无法启动，我们建议您仔细阅读以下文档来解决:\n" +
            "if server can't startup, we recommend that you read the documentation to find a solution:\n" +
            "https://blog.yunqiic.com/cocojob/guidence/problem\n" +
            "******************* CocoJob Tips *******************\n\n";

    public static void main(String[] args) {

        pre();

        AkkaStarter.init();
        VertXStarter.init();

        // Start SpringBoot application.
        try {
            SpringApplication.run(CocoJobServerApplication.class, args);
        } catch (Throwable t) {
            log.error(TIPS);
            throw t;
        }
    }

    private static void pre() {
        log.info(TIPS);
        PropertyUtils.init();
    }

}
