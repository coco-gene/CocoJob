package com.yunqiic.cocojob.server.web;

import com.yunqiic.cocojob.common.exception.CocoJobException;
import com.yunqiic.cocojob.common.response.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一处理 web 层异常信息
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultDTO<Void> exceptionHandler(Exception e) {

        // 不是所有异常都需要打印完整堆栈，后续可以定义内部的Exception，便于判断
        if (e instanceof IllegalArgumentException || e instanceof CocoJobException) {
            log.warn("[ControllerException] http request failed, message is {}.", e.getMessage());
        } else if (e instanceof HttpMessageNotReadableException || e instanceof MethodArgumentTypeMismatchException) {
            log.warn("[ControllerException] invalid http request params, exception is {}.", e.getMessage());
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            log.warn("[ControllerException] invalid http request method, exception is {}.", e.getMessage());
        } else {
            log.error("[ControllerException] http request failed.", e);
        }
        return ResultDTO.failed(ExceptionUtils.getMessage(e));
    }
}
