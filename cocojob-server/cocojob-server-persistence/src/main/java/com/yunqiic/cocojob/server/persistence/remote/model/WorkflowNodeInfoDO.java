package com.yunqiic.cocojob.server.persistence.remote.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.yunqiic.cocojob.common.enums.WorkflowNodeType;

import javax.persistence.*;
import java.util.Date;

/**
 * 工作流节点信息
 * 记录了工作流中的任务节点个性化的配置信息
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(columnList = "appId"), @Index(columnList = "workflowId")})
public class WorkflowNodeInfoDO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column(nullable = false)
    private Long appId;

    @Column
    private Long workflowId;
    /**
     * 节点类型 {@link WorkflowNodeType}
     */
    private Integer type;
    /**
     * 任务 ID
     */
    private Long jobId;
    /**
     * 节点名称，默认为对应的任务名称
     */
    private String nodeName;
    /**
     * 节点参数
     */
    @Lob
    private String nodeParams;
    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enable;
    /**
     * 是否允许失败跳过
     */
    @Column(nullable = false)
    private Boolean skipWhenFailed;

    @Lob
    private String extra;
    /**
     * 创建时间
     */
    @Column(nullable = false)
    private Date gmtCreate;
    /**
     * 更新时间
     */
    @Column(nullable = false)
    private Date gmtModified;


}
