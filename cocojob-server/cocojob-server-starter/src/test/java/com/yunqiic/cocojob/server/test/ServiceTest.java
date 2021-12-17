package com.yunqiic.cocojob.server.test;

import org.junit.jupiter.api.Assertions;
import com.yunqiic.cocojob.server.core.uid.IdGenerateService;
import com.yunqiic.cocojob.server.extension.LockService;
import com.yunqiic.cocojob.server.core.scheduler.CleanService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 服务测试
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
//@ActiveProfiles("daily")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceTest {

    @Resource
    private LockService lockService;
    @Resource
    private IdGenerateService idGenerateService;
    @Resource
    private CleanService cleanService;

    @Test
    public void testLockService() {
        String lockName = "myLock";

        lockService.tryLock(lockName, 10000);
        lockService.tryLock(lockName, 10000);

        Assertions.assertDoesNotThrow(() -> lockService.unlock(lockName));
    }

    @Test
    public void testIdGenerator() {
        Assertions.assertDoesNotThrow(() -> idGenerateService.allocate());
    }

    @Test
    public void testCleanInstanceInfo() {
        Assertions.assertDoesNotThrow(() -> cleanService.cleanInstanceLog());
    }

    @Test
    public void testCleanWorkflowNodeInfo() {
        Assertions.assertDoesNotThrow(() -> cleanService.cleanWorkflowNodeInfo());
    }

}
