package com.yunqiic.cocojob.common.request.query;

import com.yunqiic.cocojob.common.PowerQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Query JobInfo
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@Setter
@Accessors(chain = true)
public class JobInfoQuery extends PowerQuery {

    private Long idEq;
    private Long idLt;
    private Long idGt;

    private String jobNameEq;
    private String jobNameLike;

    private String jobDescriptionLike;

    private String jobParamsLike;

    private List<Integer> timeExpressionTypeIn;
    private List<Integer> executeTypeIn;
    private List<Integer> processorTypeIn;

    private String processorInfoEq;
    private String processorInfoLike;

    private List<Integer> statusIn;
    private Long nextTriggerTimeGt;
    private Long nextTriggerTimeLt;

    private String notifyUserIdsLike;

    private Date gmtCreateLt;
    private Date gmtCreateGt;

    private Date gmtModifiedLt;
    private Date gmtModifiedGt;

    private Integer dispatchStrategyEq;
}
