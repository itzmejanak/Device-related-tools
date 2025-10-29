package com.brezze.share.utils.common.string;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
public class StringUtil {
    /**
     * ^    匹配输入字符串开始的位置
     * \d   匹配一个或多个数字，其中 \ 要转义，所以是 \\d
     * +    一次或多次匹配前面的字符或子表达式。例如，"zo+"与"zo"和"zoo"匹配，但与"z"不匹配。+ 等效于 {1,}
     * $    匹配输入字符串结尾的位置
     */
    private static final Pattern PATTERN_PHONE_HK = Pattern.compile("^(5|6|8|9)\\d{7}$");
    private static final Pattern PATTERN_PHONE_CHINA = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");
    private static final Pattern PATTERN_NUM = Pattern.compile("[0-9]+");
    private static final Pattern PATTERN_NUM_WORD = Pattern.compile("^[0-9a-zA-Z]*");



    /**
     * 字符串是否为null或空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        if (value == null) {
            return true;
        }

        return value.trim().isEmpty();
    }

    /**
     * 字符串是否不为null或空
     *
     * @param value
     * @return
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }


    /**
     * 是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return PATTERN_NUM.matcher(str).matches();
    }

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isPhoneLegal(String str) throws PatternSyntaxException {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    /**
     * 大陆手机号码11位数，
     * <pre>
     *     匹配格式：前三位固定格式+后8位任意数
     *     此方法中前三位格式有：
     *     13+任意数
     *     145,147,149
     *     15+除4的任意数(不要写^4，这样的话字母也会被认为是正确的)
     *     166
     *     17+3,5,6,7,8
     *     18+任意数
     *     198,199
     * </pre>
     *
     * @return true-手机号正确 false-手机号错误
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        Matcher m = PATTERN_PHONE_CHINA.matcher(str);
        return m.matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    public static boolean isHKPhoneLegal(String str) throws PatternSyntaxException {

        Matcher m = PATTERN_PHONE_HK.matcher(str);
        return m.matches();
    }

    /**
     * 字符串转数组
     *
     * @param value     字符串
     * @param separator 分隔符
     * @return
     */
    public static String[] convertStringArray(String value, String separator) {
        if (isNotEmpty(value)) {
            return value.split(separator);
        }
        return new String[0];
    }

    /**
     * 字符串转集合
     *
     * @param value     字符串
     * @param separator 分隔符
     * @return
     */
    public static List<String> convertList(String value, String separator) {
        String[] strings = convertStringArray(value, separator);
        if (strings != null) {
            List<String> list = new ArrayList<>(strings.length);
            Collections.addAll(list, strings);
            return list;
        }
        return new ArrayList<>();
    }

    public static String getSignalLevel(String signal) {
        int signalLevel = 0;
        if (signal.startsWith("4G") || signal.startsWith("CSQ")) {
            String[] split = signal.split(":");
            if (split.length > 1) {
                int signalInt = Integer.parseInt(split[1]);
                if (signalInt >= 0 && signalInt < 16) {
                    signalLevel = 0;
                } else if (signalInt >= 16 && signalInt < 21) {
                    signalLevel = 0;
                } else if (signalInt >= 21 && signalInt < 24) {
                    signalLevel = 1;
                } else if (signalInt >= 24 && signalInt < 27) {
                    signalLevel = 1;
                } else if (signalInt >= 27) {
                    signalLevel = 2;
                }
            }
        } else if (signal.startsWith("WIFI")) {
            String[] split = signal.split(":");
            if (split.length > 1) {
                int signalInt;
                if (split[1].length() >= 3) {
                    signalInt = Integer.parseInt(split[1].substring(0, 3));
                } else {
                    signalInt = Integer.parseInt(split[1]);
                }
                if (signalInt > -35 && signalInt <= 0) {
                    signalLevel = 2;
                } else if (signalInt > -50 && signalInt <= -35) {
                    signalLevel = 1;
                } else if (signalInt > -60 && signalInt <= -50) {
                    signalLevel = 1;
                } else if (signalInt > -70 && signalInt <= -60) {
                    signalLevel = 0;
                }
            }
        }
        return String.valueOf(signalLevel);
    }

    /**
     * 秒转换 时分秒格式
     *
     * @param seconds
     * @return
     */
    public static String formatSeconds(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secondsLeft = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secondsLeft);
    }

    public static String formatDuration(Integer userTime) {
        StringBuffer sb = new StringBuffer();
        Integer day = 0;
        Integer hour = 0;
        Integer minuter = 0;
        Integer second = 0;
        if (userTime > 0) {
            day = (int) (userTime / 86400);
            hour = (int) (userTime % 86400) / 3600;
            minuter = (int) (userTime % 3600) / 60;
            second = (int) (userTime % 3600) % 60;
        }
        if (day < 10) {
            sb.append("0").append(day).append(" Days:");
        } else {
            sb.append(day).append(" Days:");
        }
        if (hour < 10) {
            sb.append("0").append(hour).append(" Hours:");
        } else {
            sb.append(hour).append(" Hours:");
        }
        if (minuter < 10) {
            sb.append("0").append(minuter).append(" Mins:");
        } else {
            sb.append(minuter).append(" Mins:");
        }
        if (second < 10) {
            sb.append("0").append(second).append(" Secs");
        } else {
            sb.append(second).append(" Secs");
        }
        return sb.toString();
    }
}
