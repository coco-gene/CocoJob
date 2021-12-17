package com.yunqiic.cocojob.worker.autoconfigure;

import com.yunqiic.cocojob.worker.CocoJobWorker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
class CocoJobAutoConfigurationTest {

    @Test
    void testAutoConfiguration() {
        ConfigurableApplicationContext run = SpringApplication.run(CocoJobAutoConfigurationTest.class);
        CocoJobWorker worker = run.getBean(CocoJobWorker.class);
        Assertions.assertNotNull(worker);
    }

}
