package com.yiban.erpcustomer.exception;


import com.alibaba.fastjson.JSON;

import java.util.Date;

public class ErrorInfo {

//    public static final Integer OK = 0;
//
//    public static final Integer ERROR = 100;

    private Integer code;

    private String message;

    private Integer display;

    private Date timestamp;

    private String url;

    private JSON data; //会序列化成JSON串

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSON getData() {
        return data;
    }

    public void setData(JSON data) {
        this.data = data;
    }
}