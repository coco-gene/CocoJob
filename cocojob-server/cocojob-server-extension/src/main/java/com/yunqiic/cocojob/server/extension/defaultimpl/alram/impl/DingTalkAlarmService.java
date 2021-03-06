package com.yunqiic.cocojob.server.extension.defaultimpl.alram.impl;

import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.exception.CocoJobException;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.common.CocoJobServerConfigKey;
import com.yunqiic.cocojob.server.common.SJ;
import com.yunqiic.cocojob.server.persistence.remote.model.UserInfoDO;
import com.yunqiic.cocojob.server.extension.defaultimpl.alram.module.Alarm;
import com.yunqiic.cocojob.server.extension.Alarmable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 钉钉告警服务
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Service
public class DingTalkAlarmService implements Alarmable {

    @Resource
    private Environment environment;

    private Long agentId;
    private DingTalkUtils dingTalkUtils;
    private Cache<String, String> mobile2UserIdCache;

    private static final int CACHE_SIZE = 8192;
    // 防止缓存击穿
    private static final String EMPTY_TAG = "EMPTY";

    @Override
    public void onFailed(Alarm alarm, List<UserInfoDO> targetUserList) {
        if (dingTalkUtils == null) {
            return;
        }
        Set<String> userIds = Sets.newHashSet();
        targetUserList.forEach(user -> {
            String phone = user.getPhone();
            if (StringUtils.isEmpty(phone)) {
                return;
            }
            try {
                String userId = mobile2UserIdCache.get(phone, () -> {
                    try {
                        return dingTalkUtils.fetchUserIdByMobile(phone);
                    } catch (CocoJobException ignore) {
                        return EMPTY_TAG;
                    } catch (Exception ignore) {
                        return null;
                    }
                });
                if (!EMPTY_TAG.equals(userId)) {
                    userIds .add(userId);
                }
            }catch (Exception ignore) {
            }
        });
        userIds.remove(null);

        if (!userIds.isEmpty()) {
            String userListStr = SJ.COMMA_JOINER.skipNulls().join(userIds);
            List<DingTalkUtils.MarkdownEntity> markdownEntities = Lists.newLinkedList();
            markdownEntities.add(new DingTalkUtils.MarkdownEntity("server", NetUtils.getLocalHost()));
            String content = alarm.fetchContent().replaceAll(OmsConstant.LINE_SEPARATOR, OmsConstant.COMMA);
            markdownEntities.add(new DingTalkUtils.MarkdownEntity("content", content));

            try {
                dingTalkUtils.sendMarkdownAsync(alarm.fetchTitle(), markdownEntities, userListStr, agentId);
            }catch (Exception e) {
                log.error("[DingTalkAlarmService] send ding message failed, reason is {}", e.getMessage());
            }
        }
    }

    @PostConstruct
    public void init() {
        String agentId = environment.getProperty(CocoJobServerConfigKey.DING_AGENT_ID);
        String appKey = environment.getProperty(CocoJobServerConfigKey.DING_APP_KEY);
        String appSecret = environment.getProperty(CocoJobServerConfigKey.DING_APP_SECRET);

        log.info("[DingTalkAlarmService] init with appKey:{},appSecret:{},agentId:{}", appKey, appSecret, agentId);

        if (StringUtils.isAnyBlank(agentId, appKey, appSecret)) {
            log.warn("[DingTalkAlarmService] cannot get agentId, appKey, appSecret at the same time, this service is unavailable");
            return;
        }
        if (!StringUtils.isNumeric(agentId)) {
            log.warn("[DingTalkAlarmService] DingTalkAlarmService is unavailable due to invalid agentId: {}", agentId);
            return;
        }
        this.agentId = Long.valueOf(agentId);
        dingTalkUtils = new DingTalkUtils(appKey, appSecret);
        mobile2UserIdCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).build();
        log.info("[DingTalkAlarmService] init DingTalkAlarmService successfully!");
    }

}
