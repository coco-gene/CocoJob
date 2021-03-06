package com.yunqiic.cocojob.server.persistence.local;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;

/**
 * 本地运行时日志数据操作层
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface LocalInstanceLogRepository extends JpaRepository<LocalInstanceLogDO, Long> {

    // 流式查询
    Stream<LocalInstanceLogDO> findByInstanceIdOrderByLogTime(Long instanceId);

    // 删除数据
    @Modifying
    @Transactional
    long deleteByInstanceId(Long instanceId);

    @Modifying
    @Transactional
    @CanIgnoreReturnValue
    long deleteByInstanceIdInAndLogTimeLessThan(List<Long> instanceIds, Long t);

    long countByInstanceId(Long instanceId);
}
