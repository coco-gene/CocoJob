package com.yunqiic.cocojob.server.test;

import com.yunqiic.cocojob.common.OmsConstant;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.TimeZone;

/**
 * 时区问题测试
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class TimezoneTest {

    @Test
    public void testTimeZone() {
        Date now = new Date();
        System.out.println(now.toString());

        System.out.println("timestamp before GMT: " + System.currentTimeMillis());

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        TimeZone timeZone = TimeZone.getDefault();
        System.out.println(timeZone.getDisplayName());
        System.out.println(new Date());
        System.out.println(DateFormatUtils.format(new Date(), OmsConstant.TIME_PATTERN));

        System.out.println("timestamp after GMT: " + System.currentTimeMillis());
    }

}
