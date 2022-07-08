package com.hgz.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author CunTouGou
 * @date 2022/5/10 23:33
 */

public class DateUtil {
    /**
     * 获取系统当前时间
     *
     * @return 系统当前时间
     */
    public static String getCurrentTime() {
        Date date = new Date();
        return String.format("%tF %<tT", date);
    }

    /**
     * 按格式获取日期字符串
     * @param stringDate   日期字符串，如"2000-03-19"
     * @param formatString 格式，如"yyyy-MM-dd"
     * @return 日期
     * @throws ParseException 解析异常
     */
    public static Date getDateByFormatString(String stringDate, String formatString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(formatString);
        return dateFormat.parse(stringDate);
    }

    /**
     * 根据时间戳转换成日期
     * @param timestamp 时间戳
     * @param formatString 格式，如"yyyy-MM-dd"
     * @return 日期
     */
    public static Date getDateByByTimestamp(String timestamp, String formatString) throws ParseException {
        long l = Long.parseLong(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
        return dateFormat.parse(new Date(l).toString());
    }
}
