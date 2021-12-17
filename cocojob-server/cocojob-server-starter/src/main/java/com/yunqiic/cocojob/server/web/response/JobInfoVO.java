package com.yunqiic.cocojob.server.web.response;

import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.enums.ProcessorType;
import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.server.common.SJ;
import com.yunqiic.cocojob.common.enums.DispatchStrategy;
import com.yunqiic.cocojob.server.common.constants.SwitchableStatus;
import com.yunqiic.cocojob.server.persistence.remote.model.JobInfoDO;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * JobInfo 对外展示对象
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class JobInfoVO {

    private Long id;

    /* ************************** 任务基本信息 ************************** */
    // 任务名称
    private String jobName;
    // 任务描述
    private String jobDescription;
    // 任务所属的应用ID
    private Long appId;
    // 任务自带的参数
    private String jobParams;

    /* ************************** 定时参数 ************************** */
    // 时间表达式类型（CRON/API/FIX_RATE/FIX_DELAY）
    private String timeExpressionType;
    // 时间表达式，CRON/NULL/LONG/LONG
    private String timeExpression;

    /* ************************** 执行方式 ************************** */
    // 执行类型，单机/广播/MR
    private String executeType;
    // 执行器类型，Java/Shell
    private String processorType;
    // 执行器信息
    private String processorInfo;

    /* ************************** 运行时配置 ************************** */
    // 最大同时运行任务数，默认 1
    private Integer maxInstanceNum;
    // 并发度，同时执行某个任务的最大线程数量
    private Integer concurrency;
    // 任务整体超时时间
    private Long instanceTimeLimit;

    /* ************************** 重试配置 ************************** */
    private Integer instanceRetryNum;
    private Integer taskRetryNum;

    // 1 正常运行，2 停止（不再调度）
    private boolean enable;
    // 下一次调度时间
    private Long nextTriggerTime;
    // 下一次调度时间（文字版）
    private String nextTriggerTimeStr;

    /* ************************** 繁忙机器配置 ************************** */
    // 最低CPU核心数量，0代表不限
    private double minCpuCores;
    // 最低内存空间，单位 GB，0代表不限
    private double minMemorySpace;
    // 最低磁盘空间，单位 GB，0代表不限
    private double minDiskSpace;

    private Date gmtCreate;
    private Date gmtModified;

    /* ************************** 集群配置 ************************** */
    // 指定机器运行，空代表不限，非空则只会使用其中的机器运行（多值逗号分割）
    private String designatedWorkers;
    // 最大机器数量
    private Integer maxWorkerCount;

    // 报警用户ID列表
    private List<String> notifyUserIds;

    private String extra;

    private String dispatchStrategy;

    private String lifecycle;

    public static JobInfoVO from(JobInfoDO jobInfoDO) {
        JobInfoVO jobInfoVO = new JobInfoVO();
        BeanUtils.copyProperties(jobInfoDO, jobInfoVO);

        TimeExpressionType timeExpressionType = TimeExpressionType.of(jobInfoDO.getTimeExpressionType());
        ExecuteType executeType = ExecuteType.of(jobInfoDO.getExecuteType());
        ProcessorType processorType = ProcessorType.of(jobInfoDO.getProcessorType());
        DispatchStrategy dispatchStrategy = DispatchStrategy.of(jobInfoDO.getDispatchStrategy());

        jobInfoVO.setTimeExpressionType(timeExpressionType.name());
        jobInfoVO.setExecuteType(executeType.name());
        jobInfoVO.setProcessorType(processorType.name());
        jobInfoVO.setEnable(jobInfoDO.getStatus() == SwitchableStatus.ENABLE.getV());
        jobInfoVO.setDispatchStrategy(dispatchStrategy.name());

        if (!StringUtils.isEmpty(jobInfoDO.getNotifyUserIds())) {
            jobInfoVO.setNotifyUserIds(SJ.COMMA_SPLITTER.splitToList(jobInfoDO.getNotifyUserIds()));
        }else {
            jobInfoVO.setNotifyUserIds(Lists.newLinkedList());
        }
        jobInfoVO.setNextTriggerTimeStr(CommonUtils.formatTime(jobInfoDO.getNextTriggerTime()));

        return jobInfoVO;
    }
}
