package com.yunqiic.cocojob.official.processors.impl.script;

/**
 * shell processor
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class ShellProcessor extends AbstractScriptProcessor {

    @Override
    protected String getScriptName(Long instanceId) {
        return String.format("shell_%d.sh", instanceId);
    }

    @Override
    protected String getRunCommand() {
        return SH_SHELL;
    }
}
