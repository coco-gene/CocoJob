package com.yunqiic.cocojob.common.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Worker部署Container请求
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerDeployContainerRequest implements PowerSerializable {

    /**
     * 容器ID
     */
    private Long containerId;
    /**
     * 容器名称
     */
    private String containerName;
    /**
     * 文件名（MD5值），用于做版本校验和文件下载
     */
    private String version;
    /**
     * 下载地址
     */
    private String downloadURL;
}
