package com.yunqiic.cocojob.server.web.controller;

import com.yunqiic.cocojob.common.enums.InstanceStatus;
import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.yunqiic.cocojob.server.common.constants.SwitchableStatus;
import com.yunqiic.cocojob.server.persistence.remote.repository.InstanceInfoRepository;
import com.yunqiic.cocojob.server.persistence.remote.repository.JobInfoRepository;
import com.yunqiic.cocojob.server.remote.server.ServerInfoService;
import com.yunqiic.cocojob.server.remote.worker.WorkerClusterQueryService;
import com.yunqiic.cocojob.server.common.module.WorkerInfo;
import com.yunqiic.cocojob.server.web.response.SystemOverviewVO;
import com.yunqiic.cocojob.server.web.response.WorkerStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * 系统信息控制器（服务于前端首页）
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@RestController
@RequestMapping("/system")
public class SystemInfoController {

    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private InstanceInfoRepository instanceInfoRepository;

    @Resource
    private ServerInfoService serverInfoService;
    @Resource
    private WorkerClusterQueryService workerClusterQueryService;

    @GetMapping("/listWorker")
    public ResultDTO<List<WorkerStatusVO>> listWorker(Long appId) {

        List<WorkerInfo> workerInfos = workerClusterQueryService.getAllWorkers(appId);
        return ResultDTO.success(workerInfos.stream().map(WorkerStatusVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/overview")
    public ResultDTO<SystemOverviewVO> getSystemOverview(Long appId) {

        SystemOverviewVO overview = new SystemOverviewVO();

        // 总任务数量
        overview.setJobCount(jobInfoRepository.countByAppIdAndStatusNot(appId, SwitchableStatus.DELETED.getV()));
        // 运行任务数
        overview.setRunningInstanceCount(instanceInfoRepository.countByAppIdAndStatus(appId, InstanceStatus.RUNNING.getV()));
        // 近期失败任务数（24H内）
        Date date = DateUtils.addDays(new Date(), -1);
        overview.setFailedInstanceCount(instanceInfoRepository.countByAppIdAndStatusAndGmtCreateAfter(appId, InstanceStatus.FAILED.getV(), date));

        // 服务器时区
        overview.setTimezone(TimeZone.getDefault().getDisplayName());
        // 服务器时间
        overview.setServerTime(DateFormatUtils.format(new Date(), OmsConstant.TIME_PATTERN));

        SystemOverviewVO.CurrentServerInfo info = new SystemOverviewVO.CurrentServerInfo(serverInfoService.getServerId(), serverInfoService.getServerIp(), serverInfoService.getServerVersion());
        overview.setCurrentServerInfo(info);

        return ResultDTO.success(overview);
    }

}
