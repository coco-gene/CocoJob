package com.yunqiic.cocojob.server.web.controller;

import com.yunqiic.cocojob.common.request.http.SaveJobInfoRequest;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.yunqiic.cocojob.server.common.constants.SwitchableStatus;
import com.yunqiic.cocojob.server.persistence.PageResult;
import com.yunqiic.cocojob.server.persistence.remote.model.JobInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.JobInfoRepository;
import com.yunqiic.cocojob.server.core.service.JobService;
import com.yunqiic.cocojob.server.web.request.QueryJobInfoRequest;
import com.yunqiic.cocojob.server.web.response.JobInfoVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务信息管理 Controller
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {

    @Resource
    private JobService jobService;
    @Resource
    private JobInfoRepository jobInfoRepository;

    @PostMapping("/save")
    public ResultDTO<Void> saveJobInfo(@RequestBody SaveJobInfoRequest request) throws Exception {
        jobService.saveJob(request);
        return ResultDTO.success(null);
    }

    @PostMapping("/copy")
    public ResultDTO<JobInfoVO> copyJob(String jobId) {
        return ResultDTO.success(JobInfoVO.from(jobService.copyJob(Long.valueOf(jobId))));
    }


    @GetMapping("/disable")
    public ResultDTO<Void> disableJob(String jobId) {
        jobService.disableJob(Long.valueOf(jobId));
        return ResultDTO.success(null);
    }

    @GetMapping("/delete")
    public ResultDTO<Void> deleteJob(String jobId) {
        jobService.deleteJob(Long.valueOf(jobId));
        return ResultDTO.success(null);
    }

    @GetMapping("/run")
    public ResultDTO<Long> runImmediately(String appId, String jobId) {
        return ResultDTO.success(jobService.runJob(Long.valueOf(appId), Long.valueOf(jobId), null, 0L));
    }

    @PostMapping("/list")
    public ResultDTO<PageResult<JobInfoVO>> listJobs(@RequestBody QueryJobInfoRequest request) {

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(request.getIndex(), request.getPageSize(), sort);
        Page<JobInfoDO> jobInfoPage;

        // 无查询条件，查询全部
        if (request.getJobId() == null && StringUtils.isEmpty(request.getKeyword())) {
            jobInfoPage = jobInfoRepository.findByAppIdAndStatusNot(request.getAppId(), SwitchableStatus.DELETED.getV(), pageRequest);
            return ResultDTO.success(convertPage(jobInfoPage));
        }

        // 有 jobId，直接精确查询
        if (request.getJobId() != null) {

            Optional<JobInfoDO> jobInfoOpt = jobInfoRepository.findById(request.getJobId());
            PageResult<JobInfoVO> result = new PageResult<>();
            result.setIndex(0);
            result.setPageSize(request.getPageSize());

            if (jobInfoOpt.isPresent()) {
                result.setTotalItems(1);
                result.setTotalPages(1);
                result.setData(Lists.newArrayList(JobInfoVO.from(jobInfoOpt.get())));
            } else {
                result.setTotalPages(0);
                result.setTotalItems(0);
                result.setData(Lists.newLinkedList());
            }

            return ResultDTO.success(result);
        }

        // 模糊查询
        String condition = "%" + request.getKeyword() + "%";
        jobInfoPage = jobInfoRepository.findByAppIdAndJobNameLikeAndStatusNot(request.getAppId(), condition, SwitchableStatus.DELETED.getV(), pageRequest);
        return ResultDTO.success(convertPage(jobInfoPage));
    }


    private static PageResult<JobInfoVO> convertPage(Page<JobInfoDO> jobInfoPage) {
        List<JobInfoVO> jobInfoVOList = jobInfoPage.getContent().stream().map(JobInfoVO::from).collect(Collectors.toList());

        PageResult<JobInfoVO> pageResult = new PageResult<>(jobInfoPage);
        pageResult.setData(jobInfoVOList);
        return pageResult;
    }

}
