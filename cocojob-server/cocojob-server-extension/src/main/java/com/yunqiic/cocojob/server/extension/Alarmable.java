package com.yunqiic.cocojob.server.extension;

import com.yunqiic.cocojob.server.persistence.remote.model.UserInfoDO;
import com.yunqiic.cocojob.server.extension.defaultimpl.alram.module.Alarm;

import java.util.List;

/**
 * 报警接口
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface Alarmable {

    void onFailed(Alarm alarm, List<UserInfoDO> targetUserList);
}
