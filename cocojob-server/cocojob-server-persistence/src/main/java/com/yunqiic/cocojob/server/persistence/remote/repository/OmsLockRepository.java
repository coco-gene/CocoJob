package com.yunqiic.cocojob.server.persistence.remote.repository;

import com.yunqiic.cocojob.server.persistence.remote.model.OmsLockDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * 利用唯一性约束作为数据库锁
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface OmsLockRepository extends JpaRepository<OmsLockDO, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from OmsLockDO where lockName = ?1")
    int deleteByLockName(String lockName);

    OmsLockDO findByLockName(String lockName);

    @Modifying
    @Transactional
    int deleteByOwnerIP(String ip);
}
