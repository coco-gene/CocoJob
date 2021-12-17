package com.yunqiic.cocojob.server.test;


import com.yunqiic.cocojob.server.common.utils.CronExpression;
import org.junit.Test;

import java.util.Date;

/**
 * CRON 测试
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class CronTest {

    private static final String FIXED_CRON = "0 0 13 8 10 ? 2020-2020";

    @Test
    public void testFixedTimeCron() throws Exception {
        CronExpression cronExpression = new CronExpression(FIXED_CRON);
        System.out.println(cronExpression.getCronExpression());
        System.out.println(cronExpression.getNextValidTimeAfter(new Date()));
    }

}
