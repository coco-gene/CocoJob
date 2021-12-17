package com.yunqiic.cocojob.client.test;

import com.alibaba.fastjson.JSON;
import com.yunqiic.cocojob.common.request.query.JobInfoQuery;
import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.enums.ProcessorType;
import com.yunqiic.cocojob.common.response.JobInfoDTO;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

/**
 * Test the query method
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
class TestQuery extends ClientInitializer {

    @Test
    void testFetchAllJob() {
        ResultDTO<List<JobInfoDTO>> allJobRes = cocoJobClient.fetchAllJob();
        System.out.println(JSON.toJSONString(allJobRes));
    }

    @Test
    void testQueryJob() {
        JobInfoQuery jobInfoQuery = new JobInfoQuery()
                .setIdGt(-1L)
                .setIdLt(10086L)
                .setJobNameLike("DAG")
                .setGmtModifiedGt(DateUtils.addYears(new Date(), -10))
                .setGmtCreateLt(DateUtils.addDays(new Date(), 10))
                .setExecuteTypeIn(Lists.newArrayList(ExecuteType.STANDALONE.getV(), ExecuteType.BROADCAST.getV(), ExecuteType.MAP_REDUCE.getV()))
                .setProcessorTypeIn(Lists.newArrayList(ProcessorType.BUILT_IN.getV(), ProcessorType.SHELL.getV(), ProcessorType.EXTERNAL.getV()))
                .setProcessorInfoLike("com.yunqiic.cocojob");

        ResultDTO<List<JobInfoDTO>> jobQueryResult = cocoJobClient.queryJob(jobInfoQuery);
        System.out.println(JSON.toJSONString(jobQueryResult));
        System.out.println(jobQueryResult.getData().size());
    }
}
