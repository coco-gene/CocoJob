package com.yunqiic.cocojob.common.request.http;

import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.model.PEWorkflowDAG;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建/修改 Workflow 请求
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class SaveWorkflowRequest implements Serializable {

    private Long id;

    /**
     * 工作流名称
     */
    private String wfName;
    /**
     * 工作流描述
     */
    private String wfDescription;

    /**
     * 所属应用ID（OpenClient不需要用户填写，自动填充）
     */
    private Long appId;


    /* ************************** 定时参数 ************************** */
    /**
     * 时间表达式类型，仅支持 CRON 和 API
     */
    private TimeExpressionType timeExpressionType;
    /**
     * 时间表达式，CRON/NULL/LONG/LONG
     */
    private String timeExpression;

    /**
     * 最大同时运行的工作流个数，默认 1
     */
    private Integer maxWfInstanceNum = 1;

    /**
     * ENABLE / DISABLE
     */
    private boolean enable = true;

    /**
     * 工作流整体失败的报警
     */
    private List<Long> notifyUserIds = Lists.newLinkedList();

    /** 点线表示法*/
    private PEWorkflowDAG dag;

    public void valid() {
        CommonUtils.requireNonNull(wfName, "workflow name can't be empty");
        CommonUtils.requireNonNull(appId, "appId can't be empty");
        CommonUtils.requireNonNull(timeExpressionType, "timeExpressionType can't be empty");
    }
}
