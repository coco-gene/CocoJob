package com.yunqiic.cocojob.server.persistence.remote.repository;

import com.yunqiic.cocojob.server.persistence.remote.model.UserInfoDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 用户信息表数据库访问层
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface UserInfoRepository extends JpaRepository<UserInfoDO, Long> {

    List<UserInfoDO> findByUsernameLike(String username);

    List<UserInfoDO> findByIdIn(List<Long> userIds);
}
