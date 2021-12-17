package com.yunqiic.cocojob.server.persistence.remote.repository;

import com.yunqiic.cocojob.server.persistence.remote.model.ContainerInfoDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 容器信息 数据操作层
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface ContainerInfoRepository extends JpaRepository<ContainerInfoDO, Long> {

    List<ContainerInfoDO> findByAppIdAndStatusNot(Long appId, Integer status);
}
