package com.yunqiic.cocojob.server.web.controller;

import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.yunqiic.cocojob.server.core.service.ValidateService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 校验控制器
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@RestController
@RequestMapping("/validate")
public class ValidateController {

    @GetMapping("/timeExpression")
    public ResultDTO<List<String>> checkTimeExpression(TimeExpressionType timeExpressionType, String timeExpression) {
        try {
            return ResultDTO.success(ValidateService.calculateNextTriggerTime(timeExpressionType, timeExpression));
        } catch (Exception e) {
            return ResultDTO.success(Lists.newArrayList(ExceptionUtils.getMessage(e)));
        }
    }
}
