package com.yunqiic.cocojob.agent;

import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.worker.CocoJobWorker;
import com.yunqiic.cocojob.worker.common.CocoJobWorkerConfig;
import com.yunqiic.cocojob.worker.common.constants.StoreStrategy;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * cocojob-worker-agent entry
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Command(name = "CocoJobAgent", mixinStandardHelpOptions = true, version = "4.0.0", description = "cocojob-worker agent")
public class MainApplication implements Runnable {

    @Option(names = {"-a", "--app"}, description = "worker-agent's name", required = true)
    private String appName;

    @Option(names = {"-p", "--port"}, description = "akka ActorSystem working port, not recommended to change")
    private Integer port = RemoteConstant.DEFAULT_WORKER_PORT;

    @Option(names = {"-e", "--persistence"}, description = "storage strategy, DISK or MEMORY")
    private String storeStrategy = "DISK";

    @Option(names = {"-s", "--server"}, description = "oms-server's address, IP:Port OR domain", required = true)
    private String server = "localhost:7700";

    @Option(names = {"-l", "--length"}, description = "ProcessResult#msg max length")
    private int length = 1024;

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new MainApplication());
        commandLine.execute(args);
    }

    @Override
    public void run() {

        CocoJobWorkerConfig cfg = new CocoJobWorkerConfig();
        try {

            cfg.setAppName(appName);
            cfg.setPort(port);
            cfg.setServerAddress(Splitter.on(",").splitToList(server));
            cfg.setStoreStrategy(StoreStrategy.MEMORY.name().equals(storeStrategy) ? StoreStrategy.MEMORY : StoreStrategy.DISK);
            cfg.setMaxResultLength(length);

            CocoJobWorker worker = new CocoJobWorker();
            worker.setConfig(cfg);

            worker.init();
        }catch (Exception e) {
            log.error("[CocoJobAgent] startup failed by config: {}.", cfg, e);
            ExceptionUtils.rethrow(e);
        }
    }
}
