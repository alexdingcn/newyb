package com.yiban.erp.exception;


/**
 * 访问权限异常
 */
public class PermissionException extends Exception {

    private ErrorCode errorCode;

    private Object extra;

    public PermissionException() {
        super();
    }

    public PermissionException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public PermissionException(ErrorCode errorCode, Object extra) {
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
