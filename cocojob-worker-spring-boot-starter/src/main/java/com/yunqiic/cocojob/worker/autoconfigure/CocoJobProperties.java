package com.yunqiic.cocojob.worker.autoconfigure;

import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.worker.common.constants.StoreStrategy;
import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.WorkflowContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * CocoJob properties configuration class.
 *
 * @author zhangchunsheng
 * @since 2021-12-02 16:37
 */
@ConfigurationProperties(prefix = "cocojob")
public class CocoJobProperties {

    private final Worker worker = new Worker();

    public Worker getWorker() {
        return worker;
    }

    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "cocojob.worker.app-name")
    public String getAppName() {
        return getWorker().appName;
    }

    @Deprecated
    public void setAppName(String appName) {
        getWorker().setAppName(appName);
    }

    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "cocojob.worker.akka-port")
    public int getAkkaPort() {
        return getWorker().akkaPort;
    }

    @Deprecated
    public void setAkkaPort(int akkaPort) {
        getWorker().setAkkaPort(akkaPort);
    }

    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "cocojob.worker.server-address")
    public String getServerAddress() {
        return getWorker().serverAddress;
    }

    @Deprecated
    public void setServerAddress(String serverAddress) {
        getWorker().setServerAddress(serverAddress);
    }

    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "cocojob.worker.store-strategy")
    public StoreStrategy getStoreStrategy() {
        return getWorker().storeStrategy;
    }

    @Deprecated
    public void setStoreStrategy(StoreStrategy storeStrategy) {
        getWorker().setStoreStrategy(storeStrategy);
    }

    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "cocojob.worker.max-result-length")
    public int getMaxResultLength() {
        return getWorker().maxResultLength;
    }

    @Deprecated
    public void setMaxResultLength(int maxResultLength) {
        getWorker().setMaxResultLength(maxResultLength);
    }

    @Deprecated
    @DeprecatedConfigurationProperty(replacement = "cocojob.worker.enable-test-mode")
    public boolean isEnableTestMode() {
        return getWorker().enableTestMode;
    }

    @Deprecated
    public void setEnableTestMode(boolean enableTestMode) {
        getWorker().setEnableTestMode(enableTestMode);
    }


    /**
     * Cocojob worker configuration properties.
     */
    @Setter
    @Getter
    public static class Worker {
        /**
         * Name of application, String type. Total length of this property should be no more than 255
         * characters. This is one of the required properties when registering a new application. This
         * property should be assigned with the same value as what you entered for the appName.
         */
        private String appName;
        /**
         * Akka port of Cocojob-worker, optional value. Default value of this property is 27777.
         * If multiple CocoJob-worker nodes were deployed, different, unique ports should be assigned.
         */
        private int akkaPort = RemoteConstant.DEFAULT_WORKER_PORT;
        /**
         * Address(es) of Cocojob-server node(s). Ip:port or domain.
         * Example of single Cocojob-server node:
         * <p>
         * 127.0.0.1:7700
         * </p>
         * Example of Cocojob-server cluster:
         * <p>
         * 192.168.0.10:7700,192.168.0.11:7700,192.168.0.12:7700
         * </p>
         */
        private String serverAddress;
        /**
         * Local store strategy for H2 database. {@code disk} or {@code memory}.
         */
        private StoreStrategy storeStrategy = StoreStrategy.DISK;
        /**
         * Max length of response result. Result that is longer than the value will be truncated.
         * {@link ProcessResult} max length for #msg
         */
        private int maxResultLength = 8192;
        /**
         * If test mode is set as true, Cocojob-worker no longer connects to the server or validates appName.
         * Test mode is used for conditions that your have no cocojob-server in your develop env so you can't startup the application
         */
        private boolean enableTestMode = false;
        /**
         * Max length of appended workflow context value length. Appended workflow context value that is longer than the value will be ignore.
         * {@link WorkflowContext} max length for #appendedContextData
         */
        private int maxAppendedWfContextLength = 8192;

    }
}
