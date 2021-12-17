package com.yunqiic.cocojob.worker.common;

import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.worker.common.constants.StoreStrategy;
import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.WorkflowContext;
import com.yunqiic.cocojob.worker.extension.SystemMetricsCollector;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The cocojob-worker's configuration
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@Setter
public class CocoJobWorkerConfig {
    /**
     * AppName, recommend to use the name of this project
     * Applications should be registered by cocojob-console in advance to prevent error.
     */
    private String appName;
    /**
     * Worker port
     * Random port is enabled when port is set with non-positive number.
     */
    private int port = RemoteConstant.DEFAULT_WORKER_PORT;
    /**
     * Address of cocojob-server node(s)
     * Do not mistake for ActorSystem port. Do not add any prefix, i.e. http://.
     */
    private List<String> serverAddress = Lists.newArrayList();
    /**
     * Max length of response result. Result that is longer than the value will be truncated.
     * {@link ProcessResult} max length for #msg
     */
    private int maxResultLength = 8096;
    /**
     * User-defined context object, which is passed through to the TaskContext#userContext property
     * Usage Scenarios: The container Java processor needs to use the Spring bean of the host application, where you can pass in the ApplicationContext and get the bean in the Processor
     */
    private Object userContext;
    /**
     * Internal persistence method, DISK or MEMORY
     * Normally you don't need to care about this configuration
     */
    private StoreStrategy storeStrategy = StoreStrategy.DISK;
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


    private SystemMetricsCollector systemMetricsCollector;

    private String tag;

}
