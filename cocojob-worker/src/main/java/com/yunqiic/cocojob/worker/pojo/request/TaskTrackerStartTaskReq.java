package com.yunqiic.cocojob.worker.pojo.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.worker.persistence.TaskDO;
import com.yunqiic.cocojob.worker.pojo.model.InstanceInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * TaskTracker 派发 task 进行执行
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskTrackerStartTaskReq implements PowerSerializable {

    // TaskTracker 地址
    private String taskTrackerAddress;
    private InstanceInfo instanceInfo;

    private String taskId;
    private String taskName;
    private byte[] taskContent;
    // 子任务当前重试次数
    private int taskCurrentRetryNums;

    // 秒级任务专用
    private long subInstanceId;


    /**
     * 创建 TaskTrackerStartTaskReq，该构造方法必须在 TaskTracker 节点调用
     */
    public TaskTrackerStartTaskReq(InstanceInfo instanceInfo, TaskDO task, String taskTrackerAddress) {

        this.taskTrackerAddress = taskTrackerAddress;
        this.instanceInfo = instanceInfo;

        this.taskId = task.getTaskId();
        this.taskName = task.getTaskName();
        this.taskContent = task.getTaskContent();

        this.taskCurrentRetryNums = task.getFailedCnt();
        this.subInstanceId = task.getSubInstanceId();
    }
}
