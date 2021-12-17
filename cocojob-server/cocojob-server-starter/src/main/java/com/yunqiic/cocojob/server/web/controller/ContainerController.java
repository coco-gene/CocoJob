package com.yunqiic.cocojob.server.web.controller;

import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.yunqiic.cocojob.server.remote.transport.starter.AkkaStarter;
import com.yunqiic.cocojob.server.common.constants.ContainerSourceType;
import com.yunqiic.cocojob.server.common.constants.SwitchableStatus;
import com.yunqiic.cocojob.server.core.container.ContainerTemplateGenerator;
import com.yunqiic.cocojob.server.common.utils.OmsFileUtils;
import com.yunqiic.cocojob.server.persistence.remote.model.AppInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.model.ContainerInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.AppInfoRepository;
import com.yunqiic.cocojob.server.persistence.remote.repository.ContainerInfoRepository;
import com.yunqiic.cocojob.server.core.container.ContainerService;
import com.yunqiic.cocojob.server.web.request.GenerateContainerTemplateRequest;
import com.yunqiic.cocojob.server.web.request.SaveContainerInfoRequest;
import com.yunqiic.cocojob.server.web.response.ContainerInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 容器信息控制层
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@RestController
@RequestMapping("/container")
public class ContainerController {

    @Value("${server.port}")
    private int port;

    @Resource
    private ContainerService containerService;
    @Resource
    private AppInfoRepository appInfoRepository;
    @Resource
    private ContainerInfoRepository containerInfoRepository;

    @GetMapping("/downloadJar")
    public void downloadJar(String version, HttpServletResponse response) throws IOException {
        File file = containerService.fetchContainerJarFile(version);
        if (file.exists()) {
            OmsFileUtils.file2HttpResponse(file, response);
        }
    }

    @PostMapping("/downloadContainerTemplate")
    public void downloadContainerTemplate(@RequestBody GenerateContainerTemplateRequest req, HttpServletResponse response) throws IOException {
        File zipFile = ContainerTemplateGenerator.generate(req.getGroup(), req.getArtifact(), req.getName(), req.getPackageName(), req.getJavaVersion());
        OmsFileUtils.file2HttpResponse(zipFile, response);
    }

    @PostMapping("/jarUpload")
    public ResultDTO<String> fileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResultDTO.failed("empty file");
        }
        return ResultDTO.success(containerService.uploadContainerJarFile(file));
    }

    @PostMapping("/save")
    public ResultDTO<Void> saveContainer(@RequestBody SaveContainerInfoRequest request) {
        request.valid();

        ContainerInfoDO container = new ContainerInfoDO();
        BeanUtils.copyProperties(request, container);
        container.setSourceType(request.getSourceType().getV());
        container.setStatus(request.getStatus().getV());

        containerService.save(container);
        return ResultDTO.success(null);
    }

    @GetMapping("/delete")
    public ResultDTO<Void> deleteContainer(Long appId, Long containerId) {
        containerService.delete(appId, containerId);
        return ResultDTO.success(null);
    }

    @GetMapping("/list")
    public ResultDTO<List<ContainerInfoVO>> listContainers(Long appId) {
        List<ContainerInfoVO> res = containerInfoRepository.findByAppIdAndStatusNot(appId, SwitchableStatus.DELETED.getV())
                .stream().map(ContainerController::convert).collect(Collectors.toList());
        return ResultDTO.success(res);
    }

    @GetMapping("/listDeployedWorker")
    public ResultDTO<String> listDeployedWorker(Long appId, Long containerId, HttpServletResponse response) {
        AppInfoDO appInfoDO = appInfoRepository.findById(appId).orElseThrow(() -> new IllegalArgumentException("can't find app by id:" + appId));
        String targetServer = appInfoDO.getCurrentServer();

        if (StringUtils.isEmpty(targetServer)) {
            return ResultDTO.failed("No workers have even registered！");
        }

        // 转发 HTTP 请求
        if (!AkkaStarter.getActorSystemAddress().equals(targetServer)) {
            String targetIp = targetServer.split(":")[0];
            String url = String.format("http://%s:%d/container/listDeployedWorker?appId=%d&containerId=%d", targetIp, port, appId, containerId);
            try {
                response.sendRedirect(url);
                return ResultDTO.success(null);
            }catch (Exception e) {
                return ResultDTO.failed(e);
            }
        }
        return ResultDTO.success(containerService.fetchDeployedInfo(appId, containerId));
    }

    private static ContainerInfoVO convert(ContainerInfoDO containerInfoDO) {
        ContainerInfoVO vo = new ContainerInfoVO();
        BeanUtils.copyProperties(containerInfoDO, vo);
        if (containerInfoDO.getLastDeployTime() == null) {
            vo.setLastDeployTime("N/A");
        }else {
            vo.setLastDeployTime(DateFormatUtils.format(containerInfoDO.getLastDeployTime(), OmsConstant.TIME_PATTERN));
        }
        SwitchableStatus status = SwitchableStatus.of(containerInfoDO.getStatus());
        vo.setStatus(status.name());
        ContainerSourceType sourceType = ContainerSourceType.of(containerInfoDO.getSourceType());
        vo.setSourceType(sourceType.name());
        return vo;
    }
}
