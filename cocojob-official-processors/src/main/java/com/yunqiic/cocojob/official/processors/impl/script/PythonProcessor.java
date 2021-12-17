package com.yunqiic.cocojob.official.processors.impl.script;

/**
 * python processor
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class PythonProcessor extends AbstractScriptProcessor {

    @Override
    protected String getScriptName(Long instanceId) {
        return String.format("python_%d.py", instanceId);
    }

    @Override
    protected String getRunCommand() {
        return "python";
    }
}
