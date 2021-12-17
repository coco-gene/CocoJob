package com.yunqiic.cocojob.worker.core.processor;

import lombok.*;

/**
 * processor执行结果
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult {

    private boolean success = false;
    private String msg;

    public ProcessResult(boolean success) {
        this.success = success;
    }
}
