package com.yiban.erp.util;

import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.RandomStringUtils;
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
     *
     * @return
     */
    public static String makeOrderNumber(Integer companyId, OrderNumberType numberType) {
        if (companyId == null || numberType == null) {
            throw new BizRuntimeException(ErrorCode.MAKE_ORDER_NUMBER_PARAMS);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(numberType.getCode());
        sb.append(companyId);
        sb.append("-");
        String timeStr = dateFormat(new Date(), "yyMMddHHmm");
        sb.append(timeStr);
        sb.append("-");
        sb.append(RandomStringUtils.randomNumeric(4));
        return sb.toString();
    }

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

    public static String dateFormat(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        String ft = StringUtils.isBlank(pattern) ? DEFAULT_DATE_TIME_PATTERN : pattern;
        SimpleDateFormat formatter = new SimpleDateFormat(ft);
        return formatter.format(date);
    }

    public static Date dateParse(String dateStr, String pattern) {
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
