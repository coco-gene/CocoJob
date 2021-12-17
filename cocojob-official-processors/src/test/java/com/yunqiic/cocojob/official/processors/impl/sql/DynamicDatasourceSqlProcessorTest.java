package com.yunqiic.cocojob.official.processors.impl.sql;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import com.yunqiic.cocojob.official.processors.TestUtils;
import com.yunqiic.cocojob.official.processors.util.SecurityUtils;
import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.TaskContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zhangchunsheng
 * @since 2021-12-02
 */
class DynamicDatasourceSqlProcessorTest {

    @Test
    void testSecurity() throws Exception {
        ProcessResult ps = new DynamicDatasourceSqlProcessor().process(genDynamicSqlCtx());
        assertFalse(ps.isSuccess());
    }

    @Test
    void testProcess() throws Exception {
        System.setProperty(SecurityUtils.ENABLE_DYNAMIC_SQL_PROCESSOR, Boolean.TRUE.toString());
        ProcessResult ps = new DynamicDatasourceSqlProcessor().process(genDynamicSqlCtx());
        assertTrue(ps.isSuccess());
    }

    private static TaskContext genDynamicSqlCtx() {
        JSONObject params = new JSONObject();

        // connection info
        params.put("jdbcUrl", "jdbc:mysql://localhost:3306/cocojob-daily?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
        params.put("user", "root");
        params.put("password", "No1Bug2Please3!");

        params.put("sql", "select * from job_info");
        params.put("showResult", true);

        String jobParams = params.toJSONString();

        System.out.println(jobParams);

        return TestUtils.genTaskContext(jobParams);
    }
}