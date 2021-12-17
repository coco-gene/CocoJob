package com.yunqiic.cocojob.worker.autoconfigure;

import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.worker.CocoJobWorker;
import com.yunqiic.cocojob.worker.common.CocoJobWorkerConfig;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Auto configuration class for CocoJob-worker.
 *
 * @author zhangchunsheng
 * @since 2021-12-02 16:37
 */
@Configuration
@EnableConfigurationProperties(CocoJobProperties.class)
@Conditional(CocoJobAutoConfiguration.CocoJobWorkerCondition.class)
public class CocoJobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CocoJobWorker initCocoJob(CocoJobProperties properties) {

        CocoJobProperties.Worker worker = properties.getWorker();

        /*
         * Address of CocoJob-server node(s). Do not mistake for ActorSystem port. Do not add
         * any prefix, i.e. http://.
         */
        CommonUtils.requireNonNull(worker.getServerAddress(), "serverAddress can't be empty!");
        List<String> serverAddress = Arrays.asList(worker.getServerAddress().split(","));

        /*
         * Create OhMyConfig object for setting properties.
         */
        CocoJobWorkerConfig config = new CocoJobWorkerConfig();
        /*
         * Configuration of worker port. Random port is enabled when port is set with non-positive number.
         */
        int port = worker.getAkkaPort();
        if (port <= 0) {
            port = NetUtils.getRandomPort();
        }
        config.setPort(port);
        /*
         * appName, name of the application. Applications should be registered in advance to prevent
         * error. This property should be the same with what you entered for appName when getting
         * registered.
         */
        config.setAppName(worker.getAppName());
        config.setServerAddress(serverAddress);
        /*
         * For non-Map/MapReduce tasks, {@code memory} is recommended for speeding up calculation.
         * Map/MapReduce tasks may produce batches of subtasks, which could lead to OutOfMemory
         * exception or error, {@code disk} should be applied.
         */
        config.setStoreStrategy(worker.getStoreStrategy());
        /*
         * When enabledTestMode is set as true, CocoJob-worker no longer connects to CocoJob-server
         * or validate appName.
         */
        config.setEnableTestMode(worker.isEnableTestMode());
        /*
         * Max length of appended workflow context . Appended workflow context value that is longer than the value will be ignore.
         */
        config.setMaxAppendedWfContextLength(worker.getMaxAppendedWfContextLength());
        /*
         * Create OhMyWorker object and set properties.
         */
        CocoJobWorker ohMyWorker = new CocoJobWorker();
        ohMyWorker.setConfig(config);
        return ohMyWorker;
    }

    static class CocoJobWorkerCondition extends AnyNestedCondition {

        public CocoJobWorkerCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @Deprecated
        @ConditionalOnProperty(prefix = "cocojob", name = "server-address")
        static class CocoJobProperty {

        }

        @ConditionalOnProperty(prefix = "cocojob.worker", name = "server-address")
        static class CocoJobWorkerProperty {

        }
    }
}
