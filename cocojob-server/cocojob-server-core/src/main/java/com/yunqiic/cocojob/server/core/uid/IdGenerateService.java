package com.yunqiic.cocojob.server.core.uid;

import com.yunqiic.cocojob.server.remote.server.ServerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 唯一ID生成服务，使用 Twitter snowflake 算法
 * 机房ID：固定为0，占用2位
 * 机器ID：由 ServerIdProvider 提供
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Service
public class IdGenerateService {

    private final SnowFlakeIdGenerator snowFlakeIdGenerator;
    private static final int DATA_CENTER_ID = 0;

    @Autowired
    public IdGenerateService(ServerInfoService serverInfoService) {

        long id = serverInfoService.getServerId();
        snowFlakeIdGenerator = new SnowFlakeIdGenerator(DATA_CENTER_ID, id);
        log.info("[IdGenerateService] initialize IdGenerateService successfully, ID:{}", id);
    }

    /**
     * 分配分布式唯一ID
     * @return 分布式唯一ID
     */
    public long allocate() {
        return snowFlakeIdGenerator.nextId();
    }

}
