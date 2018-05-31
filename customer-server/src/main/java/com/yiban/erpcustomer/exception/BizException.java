package com.yiban.erpcustomer.exception;

/**
 * 业务异常
 */
public class BizException extends Exception {

    private ErrorCode errorCode;

    private Object extra;

    public BizException() {
        super();
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, Object extra) {
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
