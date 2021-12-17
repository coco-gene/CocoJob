package com.yunqiic.cocojob.worker.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 打印启动 Banner
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public final class PowerBannerPrinter {

    private static final String BANNER = "" +
            "\n" +
            " ███████                                          ██          ██\n" +
            "░██░░░░██                                        ░██         ░██\n" +
            "░██   ░██  ██████  ███     ██  █████  ██████     ░██  ██████ ░██\n" +
            "░███████  ██░░░░██░░██  █ ░██ ██░░░██░░██░░█     ░██ ██░░░░██░██████\n" +
            "░██░░░░  ░██   ░██ ░██ ███░██░███████ ░██ ░      ░██░██   ░██░██░░░██\n" +
            "░██      ░██   ░██ ░████░████░██░░░░  ░██    ██  ░██░██   ░██░██  ░██\n" +
            "░██      ░░██████  ███░ ░░░██░░██████░███   ░░█████ ░░██████ ░██████\n" +
            "░░        ░░░░░░  ░░░    ░░░  ░░░░░░ ░░░     ░░░░░   ░░░░░░  ░░░░░\n" +
            "\n" +
            "* Maintainer: zhangchunsheng423@gmail.com & CocoJob-Team\n" +
            "* OfficialWebsite: https://www.yunqiic.com/\n" +
            "* SourceCode: https://github.com/coco-gene/CocoJob\n" +
            "\n";

    public static void print() {
        log.info(BANNER);

        String version = CocoJobWorkerVersion.getVersion();
        version = (version != null) ? " (v" + version + ")" : "";
        log.info(":: CocoJob Worker :: {}", version);
    }

}
