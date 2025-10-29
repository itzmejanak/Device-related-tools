package com.brezze.share.utils.common.date;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


/**
 * 时间工具类 jdk8
 */
public class DateUtil {
    public final static String FORMAT_PATTERN1 = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_PATTERN2 = "yyyyMMddHHmmss";
    public final static String FORMAT_PATTERN3 = "yyyy-MM-dd";
    public final static String FORMAT_PATTERN4 = "yyyyMMdd";
    public final static String FORMAT_PATTERN5 = "HH:mm:ss";
    public final static String FORMAT_PATTERN6 = "yyyy-MM";
    public final static String FORMAT_PATTERN7 = "MMM";
    public final static String FORMAT_PATTERN8 = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String FORMAT_PATTERN9 = "yyyyMMddHH";
    public final static String FORMAT_PATTERN10 = "yyyyMM";
    public final static String FORMAT_PATTERN11 = "yyyy";

    /**
     * 日期格式yyyy-MM-dd
     */
    public static String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     */
    public static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期时间格式yyyy/MM/dd HH:mm:ss SSS
     */
    public static String DATE_MSEL_TIME_PATTERN = "yyyy/MM/dd HH:mm:ss SSS";
    /**
     * rfc3339标准格式时间yyyy-MM-dd'T'HH:mm:ssXXX
     */
    public static String RFC3339_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    /**
     * rfc3339标准格式时间yyyy-MM-dd'T'HH:mm:ssXXX
     */
    public static String RFC3339_TIME_PATTERN1 = "yyyy-MM-dd'T'HH:mm:ss";
    /**
     * rfc3339标准格式时间yyyy-MM-dd'T'HH:mm:ssXXX
     */
    public static String RFC3339_TIME_PATTERN2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";



    /**
     * long转LocalDateTime
     *
     * @param value
     * @return
     */
    public static LocalDateTime longToLocalDateTime(long value) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault());
    }

    /**
     * long转LocalDate
     *
     * @param value
     * @return
     */
    public static LocalDate longToLocalDate(long value) {
        return longToLocalDateTime(value).toLocalDate();
    }

    /**
     * 获取两个日期之间的日期数组
     *
     * @param rangeTime
     * @return
     */
    public static List<String> getDates(Long[] rangeTime, String... format) {
        List<String> list = new ArrayList<>();
        LocalDate start = longToLocalDateTime(rangeTime[0]).toLocalDate();
        LocalDate end = longToLocalDateTime(rangeTime[1]).toLocalDate();
        long between = ChronoUnit.DAYS.between(start, end);
        String pattern = DATE_PATTERN;
        if (format != null && format.length > 0) {
            pattern = format[0];
        }
        for (long i = 0; i < between + 1; i++) {
            LocalDate localDate = start.plusDays(i);
            list.add(format(localDate, pattern));
        }
        return list;
    }

    /**
     * 获取两个日期之间的日期数组
     *
     * @return
     */
    public static List<String> getDates(LocalDateTime time1, LocalDateTime time2) {
        List<String> list = new ArrayList<>();
        long diffDay = getAbsTimeDiffDay(time1, time2);
        for (int i = 0; i < diffDay; i++) {
            LocalDateTime dateTime = addTime(time1, ChronoUnit.DAYS, i + 1);
            list.add(format(dateTime, DATE_PATTERN));
        }
        return list;
    }

    /**
     * 格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime LocalDateTime对象
     * @return
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DATE_TIME_PATTERN);
    }

    /**
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime LocalDateTime对象
     * @param pattern  要格式化的字符串
     * @return
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime LocalDateTime对象
     * @param pattern  要格式化的字符串
     * @return
     */
    public static String formatEn(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    /**
     * 格式化时间-默认yyyy-MM-dd格式
     *
     * @param dateTime LocalDateTime对象
     * @return
     */
    public static String format(LocalDate dateTime) {
        return format(dateTime, DATE_TIME_PATTERN);
    }


    /**
     * 格式化当前时间-默认yyyy-MM格式
     *
     * @return
     */
    public static String format() {
        return format(LocalDateTime.now(), FORMAT_PATTERN6);
    }

    /**
     * 按pattern格式化时间-默认yyyy-MM-dd格式
     *
     * @param dateTime LocalDateTime对象
     * @param pattern  要格式化的字符串
     * @return
     */
    public static String format(LocalDate dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 按pattern格式化时间-默认yyyy-MM-dd 格式
     *
     * @param value   日期时间字符串
     * @param pattern 要格式化的字符串
     * @return
     */
    public static LocalDate formatToLocalDate(String value, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(value, formatter);
    }

    /**
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     *
     * @param value   日期时间字符串
     * @param pattern 要格式化的字符串
     * @return
     */
    public static LocalDateTime formatToLocalDateTime(String value, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(value, formatter);
    }

    /**
     * 按pattern格式化时间-默认HH:mm:ss格式
     *
     * @param value   日期时间字符串
     * @param pattern 要格式化的字符串
     * @return
     */
    public static LocalTime formatToLocalTime(String value, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(value, formatter);
    }

    /**
     * 根据时间获取当月有多少天数
     *
     * @param date
     * @return
     */
    public static int getActualMaximum(Date date) {

        return dateToLocalDateTime(date).getMonth().length(dateToLocalDate(date).isLeapYear());
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return 1:星期一；2:星期二；3:星期三；4:星期四；5:星期五；6:星期六；7:星期日；
     */
    public static int getWeekOfDate(Date date) {
        return dateToLocalDateTime(date).getDayOfWeek().getValue();
    }


    /**
     * 计算两个日期LocalDate相差的天数，不考虑日期前后，返回结果>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static int getAbsDateDiffDay(LocalDate before, LocalDate after) {

        return Math.abs(Period.between(before, after).getDays());
    }

    /**
     * 计算两个时间相隔的天数，不考虑日期前后，返回结果>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static long getAbsTimeDiffDay(LocalDateTime before, LocalDateTime after) {

        return Math.abs(Duration.between(before, after).toDays());
    }

    /**
     * 计算两个时间相隔的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static long getTimeDiffDay(LocalDateTime before, LocalDateTime after) {

        return Duration.between(before, after).toDays();
    }

    /**
     * 计算两个时间LocalDateTime相差的月数，不考虑日期前后，返回结果>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static int getAbsTimeDiffMonth(LocalDateTime before, LocalDateTime after) {

        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getMonths());
    }

    /**
     * 计算两个时间LocalDateTime相差的年数，不考虑日期前后，返回结果>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static int getAbsTimeDiffYear(LocalDateTime before, LocalDateTime after) {

        return Math.abs(Period.between(before.toLocalDate(), after.toLocalDate()).getYears());
    }


    /**
     * 根据传入日期返回星期几
     *
     * @param date 日期
     * @return 1-7 1：星期天,2:星期一,3:星期二,4:星期三,5:星期四,6:星期五,7:星期六
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }


    /**
     * 获取指定日期的当月的月份数
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        return dateToLocalDateTime(date).getMonth().getValue();

    }

    /**
     * 获取指定日期的当月第一天
     *
     * @param date
     * @return
     */
    public static LocalDate getFirstDayOfMonth(Date date) {
        LocalDate localDate = dateToLocalDate(date);
        return LocalDate.of(localDate.getYear(), localDate.getMonth(), 1);
    }

    /**
     * 特定日期的当月最后一天
     *
     * @param date
     * @return
     */
    public static LocalDate getLastDayOfMonth(Date date) {
        int lastDay = getActualMaximum(date);
        LocalDate localDate = dateToLocalDate(date);
        return LocalDate.of(localDate.getYear(), localDate.getMonth(), lastDay);
    }

    /**
     * 特定日期的当年第一天
     *
     * @param date
     * @return
     */
    public static LocalDate getFirstDayOfYear(Date date) {
        LocalDate localDate = dateToLocalDate(date);
        return LocalDate.of(localDate.getYear(), 1, 1);
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static Timestamp getCurrentDateTime() {
        return new Timestamp(Instant.now().toEpochMilli());
    }

    /**
     * 获取当前时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 把日期后的时间归0 变成(yyyy-MM-dd 00:00:00:000)
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime zerolizedTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 0, 0, 0, 0);
    }

    /**
     * 把时间变成(yyyy-MM-dd 00:00:00:000)
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime zerolizedTime(Timestamp date) {
        LocalDateTime localDateTime = timestampToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 0, 0, 0, 0);
    }

    /**
     * 把日期的时间变成(yyyy-MM-dd 23:59:59:999)
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime getEndTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59, 999 * 1000000);
    }

    /**
     * 把时间变成(yyyy-MM-dd 23:59:59:999)
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime getEndTime(Timestamp date) {
        LocalDateTime localDateTime = timestampToLocalDateTime(date);
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59, 999 * 1000000);
    }

    /**
     * 计算特定时间到 当天 23.59.59.999 的秒数
     *
     * @return
     */
    public static int calculateToEndTime(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime end = getEndTime(date);
        return (int) (end.toEpochSecond(ZoneOffset.UTC) - localDateTime.toEpochSecond(ZoneOffset.UTC));
    }


    /**
     * 增加或减少年/月/周/天/小时/分/秒数
     *
     * @param localDateTime 例：ChronoUnit.DAYS
     * @param chronoUnit
     * @param num
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(LocalDateTime localDateTime, ChronoUnit chronoUnit, int num) {
        return localDateTime.plus(num, chronoUnit);
    }

    public static LocalDateTime addTime(LocalDateTime localDateTime, ChronoUnit chronoUnit, long num) {
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * 增加或减少年/月/周/天/小时/分/秒数
     *
     * @param chronoUnit 例：ChronoUnit.DAYS
     * @param num
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(Date date, ChronoUnit chronoUnit, int num) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * 增加或减少年/月/周/天/小时/分/秒数
     *
     * @param chronoUnit 例：ChronoUnit.DAYS
     * @param num
     * @return LocalDateTime
     */
    public static LocalDateTime addTime(Timestamp date, ChronoUnit chronoUnit, int num) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        long nanoOfSecond = (date.getTime() % 1000) * 1000000;
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, (int) nanoOfSecond, ZoneOffset.of("+8"));

        return localDateTime;
    }

    /**
     * Timestamp 转 LocalDateTime
     *
     * @param date
     * @return LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp date) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(date.getTime() / 1000, date.getNanos(), ZoneOffset.of("+8"));

        return localDateTime;
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date
     * @return LocalDate
     */
    public static LocalDate dateToLocalDate(Date date) {

        return dateToLocalDateTime(date).toLocalDate();
    }

    /**
     * String 类型时间 (2020-9)转LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String date) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_PATTERN6);
        Date dateTime = null;
        try {
            dateTime = df.parse(date);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime.getTime()), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * timestamp 转 LocalDateTime
     *
     * @param date
     * @return LocalDate
     */
    public static LocalDate timestampToLocalDate(Timestamp date) {

        return timestampToLocalDateTime(date).toLocalDate();
    }

    /**
     * 比较两个LocalDateTime是否同一天
     *
     * @param begin
     * @param end
     * @return
     */
    public static boolean isTheSameDay(LocalDateTime begin, LocalDateTime end) {
        return begin.toLocalDate().equals(end.toLocalDate());
    }


    /**
     * 比较两个时间(yyyy-MM)大小
     *
     * @param time1
     * @param time2
     * @return 1:第一个比第二个大；0：第一个与第二个相同；-1：第一个比第二个小
     */
    public static Integer compareTwoTime(String time1, String time2) {
        LocalDateTime localDateTime1 = stringToLocalDateTime(time1);
        LocalDateTime localDateTime2 = stringToLocalDateTime(time2);
        if (null == localDateTime1 || null == localDateTime2) {
            return null;
        }
        return compareTwoTime(localDateTime1, localDateTime2);
    }


    /**
     * 比较两个时间LocalDateTime大小
     *
     * @param time1
     * @param time2
     * @return 1:第一个比第二个大；0：第一个与第二个相同；-1：第一个比第二个小
     */
    public static int compareTwoTime(LocalDateTime time1, LocalDateTime time2) {
        if (time1.isAfter(time2)) {
            return 1;
        } else if (time1.isBefore(time2)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 比较两个时间相差的毫秒数，如果endTime<=startTime,返回0
     *
     * @param startTime
     * @param endTime
     */
    public static long getTwoTimeDiffMillisSecond(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        long diff = duration.toMillis();
        if (diff > 0) {
            return diff;
        } else {
            return 0;
        }
    }

    /**
     * 比较两个时间相差的秒数，如果endTime<=startTime,返回0
     *
     * @param startTime
     * @param endTime
     */
    public static long getTwoTimeDiffSecond(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        long diff = duration.toMillis() / 1000;
        if (diff > 0) {
            return diff;
        } else {
            return 0;
        }
    }

    public static long getTwoTimeDiffSeconds(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMillis() / 1000;
    }

    /**
     * 比较两个时间相差的分钟数，如果endTime<=startTime,返回0
     *
     * @param startTime
     * @param endTime
     */
    public static long getTwoTimeDiffMin(LocalDateTime startTime, LocalDateTime endTime) {
        long diff = getTwoTimeDiffSecond(startTime, endTime) / 60;
        if (diff > 0) {
            return diff;
        } else {
            return 0;
        }
    }

    /**
     * 比较两个时间相差的小时数，如果endTime<=startTime,返回0
     *
     * @param startTime
     * @param endTime
     */
    public static long getTwoTimeDiffHour(LocalDateTime startTime, LocalDateTime endTime) {
        long diff = getTwoTimeDiffSecond(startTime, endTime) / 3600;
        if (diff > 0) {
            return diff;
        } else {
            return 0;
        }
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isTimeInRange(Date startTime, Date endTime) throws Exception {
        LocalDateTime now = getCurrentLocalDateTime();
        LocalDateTime start = dateToLocalDateTime(startTime);
        LocalDateTime end = dateToLocalDateTime(endTime);
        return (start.isBefore(now) && end.isAfter(now)) || start.isEqual(now) || end.isEqual(now);
    }

    /**
     * 今天和最后一天相差多少天
     *
     * @return eg :1
     */
    public static long todayDurationToDays() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDay = now.with(TemporalAdjusters.lastDayOfMonth());
        Duration duration = Duration.between(now, lastDay);
        return duration.toDays();
    }

    /**
     * 判断月份上市否是本月
     *
     * @param year  年
     * @param month 月
     * @return
     */
    public static boolean judgeYearAndMonth(String year, String month) {
        LocalDateTime now = LocalDateTime.now();
        String date = format(now, FORMAT_PATTERN6);
        String s = "0" + month;
        return date.equals(year + "-" + s.substring(s.length() - 2));
    }

    /**
     * 获得特殊格式的时间
     *
     * @return eg  CET HH:MM:SS,12 May,2019
     */
    public static String now() {
        String timezone = Calendar.getInstance().getTimeZone().getDisplayName(false, TimeZone.SHORT);
        LocalDateTime now = LocalDateTime.now();
        return timezone + " " + format(now, FORMAT_PATTERN5) +
                "," + now.getDayOfMonth() +
                " " + now.getMonth() + "," + now.getYear();
    }

    /**
     * 获得特殊格式的时间
     *
     * @return eg  CET HH:MM:SS,12 May,2019
     */
    public static String now(LocalDateTime now) {
        String timezone = Calendar.getInstance().getTimeZone().getDisplayName(false, TimeZone.SHORT);
        return timezone + " " + format(now, FORMAT_PATTERN5) +
                "," + now.getDayOfMonth() +
                " " + now.getMonth() + "," + now.getYear();
    }

    /**
     * 获得特殊格式的时间
     *
     * @return eg  CET HH:MM:SS,12 May,2019
     */
    public static String dateFormat(LocalDateTime now) {
        String timezone = Calendar.getInstance().getTimeZone().getDisplayName(false, TimeZone.SHORT);
        return timezone +
                "," + now.getDayOfMonth() +
                " " + now.getMonth() + "," + now.getYear();
    }

    /**
     * 获取指定时间毫秒值
     *
     * @param time
     * @return
     */
    public static long getMilli(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取当前时间毫秒值
     *
     * @param
     * @return
     */
    public static long getMilli() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取当前时间秒值
     *
     * @param
     * @return
     */
    public static long getSecond() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
    }


    /**
     * 判断此时是否在两个时间之间，,只判断时分
     *
     * @param startTime 起始时间 eg 7:20
     * @param endTime   结束时间 eg 17:20
     * @return boolean
     */
    public static boolean nowBetweenTwoTime(String startTime, String endTime) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String[] time1 = startTime.split(":");
        String[] time2 = endTime.split(":");
        LocalDateTime start;
        LocalDateTime end;
        boolean timeTag = Integer.parseInt(time1[0]) > Integer.parseInt(time2[0]) ||
                (Integer.parseInt(time1[0]) == Integer.parseInt(time2[0])
                        && Integer.parseInt(time1[1]) > Integer.parseInt(time2[1]));
        //有跨天
        if (timeTag) {
            start = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time2[0]), Integer.parseInt(time2[1]));
            end = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time1[0]), Integer.parseInt(time1[1]));
            return !(time.isAfter(start) && time.isBefore(end));
        }
        //没有跨天
        start = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time1[0]), Integer.parseInt(time1[1]));
        end = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time2[0]), Integer.parseInt(time2[1]));
        return time.isAfter(start) && time.isBefore(end);
    }

    /**
     * 计算两个LocalDateTime 之间的毫秒数
     *
     * @param time1
     * @param time2
     * @return
     */
    public static Long minusToMillsLocalDateTime(LocalDateTime time1, LocalDateTime time2) {
        return Duration.between(time1, time2).toMillis();
    }

    /**
     * 获取指定日期的全部天数
     *
     * @param date 日期
     * @return
     */
    public static List<String> getDates(Date date) {
        if (date == null) {
            date = new Date();
        }
        return getDates(new Long[]{getFirstDayOfMonth(date).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                getLastDayOfMonth(date).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()});
    }

    /**
     * 获取
     *
     * @param days
     * @return
     */
    public static List<String> getDates(LocalDateTime localDateTime, int days) {
        if (localDateTime == null) {
            localDateTime = getCurrentLocalDateTime();
        }
        LocalDateTime localDateTimeBefore = addTime(localDateTime, ChronoUnit.DAYS, days);
        return getDates(new Long[]{localDateTimeBefore.toInstant(ZoneOffset.of("+8")).toEpochMilli(), localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()});
    }

    /**
     * 24小时模板
     *
     * @return
     */
    public static List<String> homHourTemplateList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String hour = "";
            if (i < 10) {
                hour = "0" + i;
            } else {
                hour = i + "";
            }
            list.add(hour);
        }
        return list;
    }

    /**
     * 获取指定时间几天前后的开始时间
     *
     * @param target
     * @param day
     * @return
     */
    public static Date getCurrentStartDay(Date target, Integer day) {
        if (target == null) {
            return null;
        }
        if (day == null) {
            day = 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(target);
        cal.add(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得当天结束时间
     */
    public static Date getCurrentDay24() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 获取一天的开始时间
     *
     * @param dateTime 日期
     * @return
     */
    public static LocalDateTime getDayStartTime(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), 0, 0, 0);
    }

    /**
     * 获取一天的结束时间
     *
     * @param dateTime 日期
     * @return
     */
    public static LocalDateTime getDayEndTime(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), 23, 59, 59);
    }

    /**
     * 计算时长(秒)
     *
     * @param begin 起止时间
     * @param end   结束时间
     * @return
     */
    public static int durationSecond(Date begin, Date end) {
        return (int) ((end.getTime() - begin.getTime()) / (1000));
    }

    /**
     * 获取当天剩余时间，单位：秒
     *
     * @return
     */
    public static int getCurrentDayRemainSecond() {
        Date endTime = getCurrentDay24();
        return durationSecond(new Date(), endTime);
    }

    public static boolean isBetweenTwoTime(String startTime, String endTime, LocalDateTime time) {
        String[] time1 = startTime.split(":");
        String[] time2 = endTime.split(":");
        LocalDateTime start;
        LocalDateTime end;
        boolean timeTag = Integer.parseInt(time1[0]) > Integer.parseInt(time2[0]) ||
                (Integer.parseInt(time1[0]) == Integer.parseInt(time2[0])
                        && Integer.parseInt(time1[1]) > Integer.parseInt(time2[1]));
        //有跨天
        if (timeTag) {
            start = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time2[0]), Integer.parseInt(time2[1]));
            end = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time1[0]), Integer.parseInt(time1[1]));
            return !(time.isAfter(start) && time.isBefore(end));
        }
        //没有跨天
        start = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time1[0]), Integer.parseInt(time1[1]));
        end = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), Integer.parseInt(time2[0]), Integer.parseInt(time2[1]));
        return time.isAfter(start) && time.isBefore(end);
    }

    /**
     * 服务器时间转换到对应时区时间
     *
     * @param timeZoneId 时区名 例：Europe/Moscow
     * @return
     */
    public static LocalDateTime conversionTimezone(String timeZoneId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId oldZone = ZoneId.systemDefault();
        ZoneId newZone = ZoneId.of(timeZoneId);
        return localDateTime.atZone(oldZone)
                .withZoneSameInstant(newZone)
                .toLocalDateTime();
    }

    public static LocalDateTime conversionTimezone(LocalDateTime localDateTime, String timeZoneId) {
        ZoneId oldZone = ZoneId.systemDefault();
        ZoneId newZone = ZoneId.of(timeZoneId);
        return localDateTime.atZone(oldZone)
                .withZoneSameInstant(newZone)
                .toLocalDateTime();
    }

    public static List<String> getDateTimeLine(LocalDateTime dateTime, int num) {
        List<String> list = new ArrayList<>();
        for (int i = Math.abs(num); i >= 0; i--) {
            LocalDateTime date = addTime(dateTime, ChronoUnit.MONTHS, -i);
            list.add(format(date, FORMAT_PATTERN6));
        }
        return list;
    }

    public static List<String> getDateTimeLine(LocalDateTime dateTime, ChronoUnit chronoUnit, int num) {
        List<String> list = new ArrayList<>();
        for (int i = Math.abs(num); i >= 0; i--) {
            LocalDateTime date = addTime(dateTime, chronoUnit, -i);
            list.add(format(date, FORMAT_PATTERN6));
        }
        return list;
    }

    public static List<LocalDateTime> getLocalDateTimeLine(LocalDateTime dateTime, ChronoUnit chronoUnit, int num) {
        List<LocalDateTime> list = new ArrayList<>();
        for (int i = Math.abs(num); i >= 0; i--) {
            LocalDateTime date = addTime(dateTime, chronoUnit, -i);
            list.add(date);
        }
        return list;
    }

    public static List<LocalDateTime> getLocalDateTimeIncrease(LocalDateTime dateTime, ChronoUnit chronoUnit, int num) {
        List<LocalDateTime> list = new ArrayList<>();
        for (int i = 0; i <= Math.abs(num); i++) {
            LocalDateTime date = addTime(dateTime, chronoUnit, i);
            list.add(date);
        }
        return list;
    }

    public static List<String> getEnDateTimeLine(LocalDateTime dateTime, int num) {
        List<String> list = new ArrayList<>();
        for (int i = Math.abs(num); i >= 0; i--) {
            LocalDateTime date = addTime(dateTime, ChronoUnit.MONTHS, -i);
            list.add(formatEn(date, FORMAT_PATTERN7));
        }
        return list;
    }

    /**
     * 获取当前时间的前一分钟
     *
     * @return
     */
    public static String getBeforeDateOne(String second) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -1);//前一分钟
        Date beforeD = beforeTime.getTime();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:" + second).format(beforeD);
    }

    public static long getTimeDiffDayCeil(LocalDateTime before, LocalDateTime after) {
        long seconds = Duration.between(before, after).toMillis() / 1000;
        long days = Duration.between(before, after).toDays();
        long remainSeconds;
        if (days == 0) {
            remainSeconds = seconds;
        } else {
            remainSeconds = seconds % (days * 24 * 3600);
        }
        if (remainSeconds > 0) {
            return days + 1;
        } else {
            return days;
        }
    }
}
