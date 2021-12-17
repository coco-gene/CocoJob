package com.yunqiic.cocojob.client.test;

import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.enums.ProcessorType;
import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.request.http.SaveJobInfoRequest;
import com.yunqiic.cocojob.common.response.ResultDTO;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;

/**
 * TestConcurrencyControl
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
class TestConcurrencyControl extends ClientInitializer {

    @Test
    void testRunJobConcurrencyControl() {

        SaveJobInfoRequest saveJobInfoRequest = new SaveJobInfoRequest();
        saveJobInfoRequest.setJobName("test concurrency control job");
        saveJobInfoRequest.setProcessorType(ProcessorType.SHELL);
        saveJobInfoRequest.setProcessorInfo("pwd");
        saveJobInfoRequest.setExecuteType(ExecuteType.STANDALONE);
        saveJobInfoRequest.setTimeExpressionType(TimeExpressionType.API);
        saveJobInfoRequest.setMaxInstanceNum(1);

        Long jobId = cocoJobClient.saveJob(saveJobInfoRequest).getData();

        System.out.println("jobId: " + jobId);

        ForkJoinPool pool = new ForkJoinPool(32);

        for (int i = 0; i < 100; i++) {
            String params = "index-" + i;
            pool.execute(() -> {
                ResultDTO<Long> res = cocoJobClient.runJob(jobId, params, 0);
                System.out.println(params + ": " + res);
            });
        }
    }
}
