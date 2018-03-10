package com.yiban.erp.exception;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.entities.SystemError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Integer DEFAULT_CODE = 9999;
    private static final String DEFAULT_MESSAGE = "系统异常，请稍后再试";
    private static final Integer DEFAULT_DISPLAY = ErrorDisplay.MESSAGE.getCode();

    @Autowired
    private ErrorMessageService errorMessageService;

    @ExceptionHandler(BizException.class)
    @ResponseBody
    public ResponseEntity<JSON> handleBizException(HttpServletRequest request, BizException e) {
        String url = request.getRequestURL().toString();
        logger.warn("Request action url:{} have an BizException:", url, e.getErrorCode());

        SystemError systemError = errorMessageService.getByErrorCode(e.getErrorCode());
        if (systemError == null) {
            return ResponseEntity.badRequest().body((JSON) JSON.toJSON(getDefaultErrorInfo(e.getMessage())));
        }else {
            return ResponseEntity.badRequest().body((JSON) JSON.toJSON(getBySystemError(systemError, e.getExtra())));
        }
    }

    @ExceptionHandler(BizRuntimeException.class)
    @ResponseBody
    public ResponseEntity<JSON> handleBizRuntimeException(HttpServletRequest request, BizRuntimeException e) {
        String url = request.getRequestURL().toString();
        logger.warn("Request action url:{} have an BizRuntimeException:", url, e.getErrorCode());
        SystemError systemError = errorMessageService.getByErrorCode(e.getErrorCode());
        if (systemError == null) {
            return ResponseEntity.badRequest().body((JSON) JSON.toJSON(getDefaultErrorInfo(e.getMessage())));
        }else {
            return ResponseEntity.badRequest().body((JSON) JSON.toJSON(getBySystemError(systemError, e.getExtra())));
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<JSON> handleDefaultException(HttpServletRequest request, Exception e) {
        String url = request.getRequestURL().toString();
        logger.warn("Request action url:{} have an Exception:", url, e.getMessage());
        return ResponseEntity.badRequest().body((JSON) JSON.toJSON(getDefaultErrorInfo(e.getMessage())));
    }

    private ErrorInfo<String> getBySystemError(SystemError systemError, Object data) {
        ErrorInfo<String> errorInfo = new ErrorInfo<>();
        errorInfo.setCode(systemError.getCode());
        errorInfo.setMessage(systemError.getMessage());
        errorInfo.setDisplay(systemError.getDisplay());
        errorInfo.setTimestamp(new Date());
        if (data == null){
            errorInfo.setData(systemError.getExtra());
        }else {
            errorInfo.setData(JSON.toJSONString(data));
        }
        return errorInfo;
    }

    private ErrorInfo getDefaultErrorInfo(String extra) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setCode(DEFAULT_CODE);
        errorInfo.setMessage(DEFAULT_MESSAGE);
        errorInfo.setDisplay(DEFAULT_DISPLAY);
        errorInfo.setTimestamp(new Date());
        errorInfo.setData(extra);
        return errorInfo;
    }


}
