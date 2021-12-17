package com.yunqiic.cocojob.server.persistence.local;

import com.yunqiic.cocojob.common.enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 本地的运行时日志
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "local_instance_log", indexes = {@Index(columnList = "instanceId")})
public class LocalInstanceLogDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long instanceId;
    /**
     * 日志时间
     */
    private Long logTime;
    /**
     * 日志级别 {@link LogLevel}
     */
    private Integer logLevel;
    /**
     * 日志内容
     */
    @Lob
    @Column(columnDefinition="TEXT")
    private String logContent;

    /**
     * 机器地址
     */
    private String workerAddress;
}
