package com.yiban.erpcustomer.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Integer DEFAULT_CODE = 9999;
    private static final String DEFAULT_MESSAGE = "系统异常，请稍后再试";
    private static final Integer DEFAULT_DISPLAY = ErrorDisplay.MESSAGE.getCode();


    @ExceptionHandler(BizException.class)
    @ResponseBody
    public ResponseEntity<JSON> handleBizException(HttpServletRequest request, BizException e) {
        String url = request.getRequestURL().toString();
        logger.warn("Request action url:{} have an BizException:", url, e.getErrorCode());
        ErrorInfo errorInfo = getErrorInfo(e.getErrorCode(), url, getExtraJson(e.getExtra()));
        return ResponseEntity.badRequest().body((JSON) JSON.toJSON(errorInfo));
    }

    @ExceptionHandler(BizRuntimeException.class)
    @ResponseBody
    public ResponseEntity<JSON> handleBizRuntimeException(HttpServletRequest request, BizRuntimeException e) {
        String url = request.getRequestURL().toString();
        logger.error("Request action url:{} have an BizRuntimeException:", url, e);
        ErrorInfo errorInfo = getErrorInfo(e.getErrorCode(), url, getExtraJson(e.getExtra()));
        return ResponseEntity.badRequest().body((JSON) JSON.toJSON(errorInfo));
    }

    /**
     * 访问权限异常
     * HttpStatus 403
     */
    @ExceptionHandler(PermissionException.class)
    @ResponseBody
    public ResponseEntity<JSON> handlePermissionException(HttpServletRequest request, PermissionException e) {
        String url = request.getRequestURL().toString();
        logger.warn("Request action url:{} have an BizRuntimeException:", url, e.getErrorCode());
        ErrorInfo errorInfo = getErrorInfo(e.getErrorCode(), url, getExtraJson(e.getExtra()));
        return new ResponseEntity((JSON) JSON.toJSON(errorInfo), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<JSON> handleMultipartException(HttpServletRequest request, MultipartException exception) {
        if (exception == null) {
            return ResponseEntity.badRequest().build();
        }
        String url = request.getRequestURL().toString();
        logger.error("update file fail. ", exception);
        if (exception.getCause() != null && exception.getCause().getCause() != null &&
                exception.getCause().getCause() instanceof FileUploadBase.SizeLimitExceededException) {
            ErrorInfo errorInfo = getErrorInfo(ErrorCode.FILE_UPLOAD_SIZE_ERROR, url, null);
            return ResponseEntity.badRequest().body((JSON) JSON.toJSON(errorInfo));
        }else {
            ErrorInfo errorInfo = getErrorInfo(ErrorCode.FILE_UPLOAD_FAIL, url, null);
            return ResponseEntity.badRequest().body((JSON) JSON.toJSON(errorInfo));
        }
    }

    /**
     * 未定义异常
     * HttpStatus 500
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<JSON> handleDefaultException(HttpServletRequest request, Exception e) {
        String url = request.getRequestURL().toString();
        logger.error("Request action url:{} have an Exception:", url, e);
        ErrorInfo errorInfo = getErrorInfo(null, url, getExtraJson(e.toString()));
        return new ResponseEntity((JSON) JSON.toJSON(errorInfo), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorInfo getErrorInfo(ErrorCode errorCode, String url, JSON extra) {
        return errorCode == null ? getDefaultErrorInfo(url, extra) :
                getByErrorCode(errorCode, url, extra);
    }

    private ErrorInfo getByErrorCode(ErrorCode errorCode, String url, JSON extra) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setCode(errorCode.getCode());
        errorInfo.setMessage(errorCode.getMessage());
        errorInfo.setDisplay(errorCode.getDisplay() == null ? DEFAULT_DISPLAY : errorCode.getDisplay().getCode());
        errorInfo.setUrl(url);
        errorInfo.setTimestamp(new Date());
        errorInfo.setData(extra);
        return errorInfo;
    }

    private ErrorInfo getDefaultErrorInfo(String url, JSON extra) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setCode(DEFAULT_CODE);
        errorInfo.setMessage(DEFAULT_MESSAGE);
        errorInfo.setDisplay(DEFAULT_DISPLAY);
        errorInfo.setTimestamp(new Date());
        errorInfo.setData(extra);
        errorInfo.setUrl(url);
        return errorInfo;
    }

    private JSON getExtraJson(Object extra) {
        if (extra == null) {
            return null;
        }
        JSON result = null;
        if (extra instanceof String) {
            JSONObject extraInfo = new JSONObject();
            extraInfo.put("message", extra);
            result = extraInfo;
        }else {
            try{
                result = (JSON) JSON.toJSON(extra);
            }catch (Exception e) {
                logger.error("extra parse to json fail, extra:{}", extra.toString());
                JSONObject extraInfo = new JSONObject();
                extraInfo.put("message", extra);
                result = extraInfo;
            }
        }
        return result;
    }

}
