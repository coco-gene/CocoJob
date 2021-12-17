package com.yunqiic.cocojob.worker.pojo.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.worker.common.ThreadLocalStore;
import com.yunqiic.cocojob.common.serialize.SerializerUtils;
import com.yunqiic.cocojob.worker.persistence.TaskDO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * WorkerMapTaskRequest
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@NoArgsConstructor
public class ProcessorMapTaskRequest implements PowerSerializable {

    private Long instanceId;
    private Long subInstanceId;

    private String taskName;
    private List<SubTask> subTasks;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTask {
        private String taskId;
        private byte[] taskContent;
    }

    public ProcessorMapTaskRequest(TaskDO parentTask, List<?> subTaskList, String taskName) {

        this.instanceId = parentTask.getInstanceId();
        this.subInstanceId = parentTask.getSubInstanceId();
        this.taskName = taskName;
        this.subTasks = Lists.newLinkedList();

        subTaskList.forEach(subTask -> {
            // 同一个 Task 内部可能多次 Map，因此还是要确保线程级别的唯一
            String subTaskId = parentTask.getTaskId() + "." + ThreadLocalStore.getTaskIDAddr().getAndIncrement();
            // 写入类名，方便反序列化
            subTasks.add(new SubTask(subTaskId, SerializerUtils.serialize(subTask)));
        });
    }
}
