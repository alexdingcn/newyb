package com.yiban.erp.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class UtilTool {

    private static final Logger logger = LoggerFactory.getLogger(UtilTool.class);

    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss";


    /**
     * 对一个对象中的所有String类型的属性进行去空格，如果去除空格后只剩空串，设置为null
     *
     * @param body
     * @param <T>
     * @return
     */
    public static <T> T trimString(T body) {
        if (body == null) {
            return body;
        }
        Field[] fields = body.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(body);
                if (value instanceof String) {
                    String newValue = ((String) value).trim();
                    field.set(body, "".equals(newValue) ? null : newValue);
                }
            } catch (IllegalAccessException e) {
                logger.error("trim string field get an IllegalAccessException.", e);
            }
        }
        return body;
    }

    public static String DateFormat(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        String ft = StringUtils.isBlank(pattern) ? DEFAULT_DATE_TIME_PATTERN : pattern;
        SimpleDateFormat formatter = new SimpleDateFormat(ft);
        return formatter.format(date);
    }

    public static Date DateParse(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        String ft = StringUtils.isBlank(pattern) ? DEFAULT_DATE_TIME_PATTERN : pattern;
        SimpleDateFormat formatter = new SimpleDateFormat(ft);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            logger.error("parse date string field get an ParseException.", e);
            return null;
        }
    }

}
