package com.yunqiic.cocojob.server.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.persistence.remote.model.AppInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.AppInfoRepository;
import com.yunqiic.cocojob.server.remote.server.election.ServerElectionService;
import com.yunqiic.cocojob.server.remote.transport.TransportService;
import com.yunqiic.cocojob.server.remote.worker.WorkerClusterQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.TimeZone;

/**
 * 处理Worker请求的 Controller
 * Worker启动时，先请求assert验证appName的可用性，再根据得到的appId获取Server地址
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@RestController
@RequestMapping("/server")
public class ServerController {

    @Resource
    private TransportService transportService;
    @Resource
    private ServerElectionService serverElectionService;
    @Resource
    private AppInfoRepository appInfoRepository;
    @Resource
    private WorkerClusterQueryService workerClusterQueryService;

    @GetMapping("/assert")
    public ResultDTO<Long> assertAppName(String appName) {
        Optional<AppInfoDO> appInfoOpt = appInfoRepository.findByAppName(appName);
        return appInfoOpt.map(appInfoDO -> ResultDTO.success(appInfoDO.getId())).
                orElseGet(() -> ResultDTO.failed(String.format("app(%s) is not registered! Please register the app in oms-console first.", appName)));
    }

    @GetMapping("/acquire")
    public ResultDTO<String> acquireServer(Long appId, String protocol, String currentServer) {
        return ResultDTO.success(serverElectionService.elect(appId, protocol, currentServer));
    }

    @GetMapping("/hello")
    public ResultDTO<JSONObject> ping(@RequestParam(required = false) boolean debug) {
        JSONObject res = new JSONObject();
        res.put("localHost", NetUtils.getLocalHost());
        res.put("communicationSystemInfo", transportService.getProtocol2Transporter());
        res.put("serverTime", CommonUtils.formatTime(System.currentTimeMillis()));
        res.put("serverTimeZone", TimeZone.getDefault().getDisplayName());
        res.put("appIds", workerClusterQueryService.getAppId2ClusterStatus().keySet());
        if (debug) {
            res.put("appId2ClusterInfo", JSON.parseObject(JSON.toJSONString(workerClusterQueryService.getAppId2ClusterStatus())));

        }
        return ResultDTO.success(res);
    }

}
