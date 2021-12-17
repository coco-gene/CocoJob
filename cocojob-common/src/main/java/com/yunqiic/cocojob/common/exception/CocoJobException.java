package com.yunqiic.cocojob.common.exception;

/**
 * CocoJob 运行时异常
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class CocoJobException extends RuntimeException {

    public CocoJobException() {
    }

    public CocoJobException(String message) {
        super(message);
    }

    public CocoJobException(String message, Throwable cause) {
        super(message, cause);
    }

    public CocoJobException(Throwable cause) {
        super(cause);
    }

    public CocoJobException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
