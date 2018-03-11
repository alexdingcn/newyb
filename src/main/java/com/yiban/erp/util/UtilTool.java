package com.yiban.erp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class UtilTool {

    private static final Logger logger = LoggerFactory.getLogger(UtilTool.class);


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

}
