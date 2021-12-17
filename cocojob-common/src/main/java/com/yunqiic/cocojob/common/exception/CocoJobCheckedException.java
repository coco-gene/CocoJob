package com.yunqiic.cocojob.common.exception;

/**
 * CocoJob 受检异常，需要开发者手动处理
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class CocoJobCheckedException extends Exception {

    public CocoJobCheckedException() {
    }

    public CocoJobCheckedException(String message) {
        super(message);
    }

    public CocoJobCheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CocoJobCheckedException(Throwable cause) {
        super(cause);
    }

    public CocoJobCheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
