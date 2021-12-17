package com.yunqiic.cocojob.worker.pojo.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;


/**
 * worker 上报 task 执行情况
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessorReportTaskStatusReq implements PowerSerializable {

    public static final Integer BROADCAST = 1;

    private Long instanceId;

    private Long subInstanceId;

    private String taskId;

    private int status;
    /**
     * 执行完成时才有
     */
    private String result;
    /**
     * 上报时间
     */
    private long reportTime;
    /**
     * 特殊请求名称
     */
    private Integer cmd;
    /**
     * 追加的工作流下文数据
     * @since 2021-12-02
     */
    private Map<String,String> appendedWfContext;
}
