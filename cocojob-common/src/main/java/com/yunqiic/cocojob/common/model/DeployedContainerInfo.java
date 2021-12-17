package com.yunqiic.cocojob.common.model;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Deployed Container Information
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeployedContainerInfo implements PowerSerializable {

    /**
     * Id of the container.
     */
    private Long containerId;
    /**
     * Version of the container.
     */
    private String version;
    /**
     * Deploy timestamp.
     */
    private long deployedTime;
    /**
     * No need to report to the server
     */
    private String workerAddress;
}
