package com.yunqiic.cocojob.server.test;

import com.yunqiic.cocojob.common.enums.InstanceStatus;
import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.enums.WorkflowInstanceStatus;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.common.constants.SwitchableStatus;
import com.yunqiic.cocojob.server.persistence.remote.model.InstanceInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.model.JobInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.model.OmsLockDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.InstanceInfoRepository;
import com.yunqiic.cocojob.server.persistence.remote.repository.JobInfoRepository;
import com.yunqiic.cocojob.server.persistence.remote.repository.OmsLockRepository;
import com.yunqiic.cocojob.server.persistence.remote.repository.WorkflowInstanceInfoRepository;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 数据库层测试
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
//@ActiveProfiles("daily")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryTest {

    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private OmsLockRepository omsLockRepository;
    @Resource
    private InstanceInfoRepository instanceInfoRepository;
    @Resource
    private WorkflowInstanceInfoRepository workflowInstanceInfoRepository;

    /**
     * 需要证明批量写入失败后会回滚
     */
    @Test
    @Transactional
    public void testBatchLock() {

        List<OmsLockDO> locks = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            OmsLockDO lockDO = new OmsLockDO("lock" + i, NetUtils.getLocalHost(), 10000L);
            locks.add(lockDO);
        }
        omsLockRepository.saveAll(locks);
        omsLockRepository.flush();
    }

    @Test
    public void testDeleteLock() {
        String lockName = "test-lock";
        OmsLockDO lockDO = new OmsLockDO(lockName, NetUtils.getLocalHost(), 10000L);
        omsLockRepository.save(lockDO);
        omsLockRepository.deleteByLockName(lockName);
    }

    @Test
    public void testSelectCronJobSQL() {
        List<JobInfoDO> result = jobInfoRepository.findByAppIdInAndStatusAndTimeExpressionTypeAndNextTriggerTimeLessThanEqual(Lists.newArrayList(1L), SwitchableStatus.ENABLE.getV(), TimeExpressionType.CRON.getV(), System.currentTimeMillis());
        System.out.println(result);
    }

    @Test
    @Transactional
    public void testUpdate() {
        InstanceInfoDO updateEntity = new InstanceInfoDO();
        updateEntity.setId(22L);
        updateEntity.setActualTriggerTime(System.currentTimeMillis());
        updateEntity.setResult("hahaha");
        instanceInfoRepository.saveAndFlush(updateEntity);
    }

    @Test
    @Transactional
    public void testExecuteLogUpdate() {
        instanceInfoRepository.update4TriggerFailed(1586310414570L, 2, System.currentTimeMillis(), System.currentTimeMillis(), "192.168.1.1", "NULL", new Date());
        instanceInfoRepository.update4FrequentJob(1586310419650L, 2, 200, new Date());
    }

    @Test
    public void testCheckQuery() {
        Date time = new Date();
        System.out.println(time);
        final List<InstanceInfoDO> res = instanceInfoRepository.findByAppIdInAndStatusAndGmtModifiedBefore(Lists.newArrayList(1L), 3, time);
        System.out.println(res);
    }

    @Test
    public void testFindByJobIdInAndStatusIn() {
        List<Long> res = instanceInfoRepository.findByJobIdInAndStatusIn(Lists.newArrayList(1L, 2L, 3L, 4L), Lists.newArrayList(1, 2, 3, 4, 5));
        System.out.println(res);
    }

    @Test
    public void testDeleteInstanceInfo() {
        instanceInfoRepository.deleteAllByGmtModifiedBeforeAndStatusIn(new Date(), InstanceStatus.FINISHED_STATUS);
    }

    @Test
    public void testDeleteWorkflowInstanceInfo() {
        workflowInstanceInfoRepository.deleteAllByGmtModifiedBeforeAndStatusIn(new Date(), WorkflowInstanceStatus.FINISHED_STATUS);
    }

}
