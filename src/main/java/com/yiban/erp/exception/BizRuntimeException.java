package com.yiban.erp.exception;

/**
 * 业务异常，RuntimeException子类
 */
public class BizRuntimeException extends RuntimeException {

    private ErrorCode errorCode;

    private Object extra;

    public BizRuntimeException() {
        super();
    }

    public BizRuntimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BizRuntimeException(ErrorCode errorCode, Object extra) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.extra = extra;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
