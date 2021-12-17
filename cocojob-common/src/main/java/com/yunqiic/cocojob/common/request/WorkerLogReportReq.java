package com.yunqiic.cocojob.common.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.model.InstanceLogContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 日志上报请求
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerLogReportReq implements PowerSerializable {
    private String workerAddress;
    private List<InstanceLogContent> instanceLogContents;
}
