package com.yunqiic.cocojob.server.extension.defaultimpl;

import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.extension.LockService;
import com.yunqiic.cocojob.server.persistence.remote.model.OmsLockDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.OmsLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * 基于数据库实现的分布式锁
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Service
public class DatabaseLockService implements LockService {

    private final String ownerIp;
    private final OmsLockRepository omsLockRepository;

    @Autowired
    public DatabaseLockService(OmsLockRepository omsLockRepository) {

        this.ownerIp = NetUtils.getLocalHost();
        this.omsLockRepository = omsLockRepository;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            int num = omsLockRepository.deleteByOwnerIP(ownerIp);
            log.info("[DatabaseLockService] execute shutdown hook, release all lock(owner={},num={})", ownerIp, num);
        }));
    }

    @Override
    public boolean tryLock(String name, long maxLockTime) {

        OmsLockDO newLock = new OmsLockDO(name, ownerIp, maxLockTime);
        try {
            omsLockRepository.saveAndFlush(newLock);
            return true;
        } catch (DataIntegrityViolationException ignore) {
        } catch (Exception e) {
            log.warn("[DatabaseLockService] write lock to database failed, lockName = {}.", name, e);
        }

        OmsLockDO omsLockDO = omsLockRepository.findByLockName(name);
        long lockedMillions = System.currentTimeMillis() - omsLockDO.getGmtCreate().getTime();

        // 锁超时，强制释放锁并重新尝试获取
        if (lockedMillions > omsLockDO.getMaxLockTime()) {

            log.warn("[DatabaseLockService] The lock[{}] already timeout, will be unlocked now.", omsLockDO);
            unlock(name);
            return tryLock(name, maxLockTime);
        }
        return false;
    }

    @Override
    public void unlock(String name) {

        try {
            CommonUtils.executeWithRetry0(() -> omsLockRepository.deleteByLockName(name));
        }catch (Exception e) {
            log.error("[DatabaseLockService] unlock {} failed.", name, e);
        }
    }

}
