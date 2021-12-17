package com.yunqiic.cocojob.common.model;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Log instance model.
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceLogContent implements PowerSerializable {

    /**
     * Id of instance.
     */
    private long instanceId;
    /**
     * Submitted time of the log.
     */
    private long logTime;
    /**
     * Level of the log.
     */
    private int logLevel;
    /**
     * Content of the log.
     */
    private String logContent;
}

