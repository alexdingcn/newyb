package com.yiban.erp.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 时间帮助类
 * @version 1.0
 * @author nihaojia
 */
public class DateUtil {

    private Calendar calendar=Calendar.getInstance();


    /**
     * 日期格式化，自定义输出日期格式
     * @param date
     * @return
     */
    public String getFormatDate(Date date,String dateFormat){
        SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    /**
     * 日期格式化，将字符串转换更日期格式
     *
     */
    public Date revertString2Date(String strDate,String strFormat){

        SimpleDateFormat sdf=new SimpleDateFormat(strFormat);
        Date date;
        try {
            date = (Date) sdf.parseObject(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            date=null;
        }
        return date;
    }

    /**
     * 某一个日期的前一个日期
     * @param d,某一个日期
     * @param field 日历字段
     * y 年 M 月 d 日 H 时 m 分 s 秒
     * @param amount 数量
     * @return 一个日期
     */
    public Date getPreDate(Date d,String field,int amount){
        calendar.setTime(d);
        if(field!=null&&!field.equals("")){
            if(field.equals("y")){
                calendar.add(calendar.YEAR, amount);
            }else if(field.equals("M")){
                calendar.add(calendar.MONTH, amount);
            }else if(field.equals("d")){
                calendar.add(calendar.DAY_OF_MONTH, amount);
            }else if(field.equals("H")){
                calendar.add(calendar.HOUR, amount);
            }
        }else{
            return null;
        }
        return calendar.getTime();
    }


    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time){
        return isThisTime(time,"yyyy-MM");
    }

    public static boolean isThisTime(long time,String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if(param.equals(now)){
            return true;
        }
        return false;
    }

    //计算两个时间之差，返回一个数组里面包含间隔的：天，小时，分钟，秒
    public long[] getDuration(Date starttime ,Date endTime){
        long[] duration = new long[4];
        long time = starttime.getTime() - endTime.getTime();
        long day = time / (24 * 60 * 60 * 1000);
        long hour = (time / (60 * 60 * 1000) - day * 24);
        long min = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        duration[0] = day;
        duration[1] = hour;
        duration[2] = min;
        duration[3] = second;
        return duration;
    }
}
