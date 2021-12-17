package com.yunqiic.cocojob.server.test;

import com.yunqiic.cocojob.server.persistence.remote.model.ServerInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.ServerInfoRepository;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * test server info
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerInfoServiceTest {

    @Resource
    private ServerInfoRepository serverInfoRepository;

    @Test
    void generateInvalidRecord2Test() {

        List<ServerInfoDO> records = Lists.newLinkedList();
        for (int i = 0; i < 11111; i++) {

            // invalid ip to test
            String ip = "T-192.168.1." + i;

            Date gmtModified = DateUtils.addHours(new Date(), -ThreadLocalRandom.current().nextInt(1, 48));

            ServerInfoDO serverInfoDO = new ServerInfoDO(ip);
            serverInfoDO.setGmtModified(gmtModified);

            records.add(serverInfoDO);
        }

        serverInfoRepository.saveAll(records);
        serverInfoRepository.flush();
    }

}