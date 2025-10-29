package com.brezze.share.utils.common.number;

import com.brezze.share.utils.common.string.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * @Description: 数字工具类
 * @author: penf
 * @Date: 2019-10-25 14:10
 **/
@Slf4j
public class NumberUtil {

    private static DecimalFormat df_2 = new DecimalFormat("0.00");

    /**
     * 判断字符串是否是浮点型数字
     *
     * @param value
     * @return
     */
    public static boolean isDouble(Object value) {
        if (value == null) {
            return false;
        } else {
            String sValue = value.toString();
            int sz = sValue.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(sValue.charAt(i)) && sValue.charAt(i) != '.') {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * 是否是数字
     *
     * @param value
     * @return
     */
    public static boolean isNumeric(Object value) {
        if (value == null) return false;

        return StringUtil.isNumeric(value.toString());
    }

    /**
     * 随机数[整数]
     *
     * @param value
     * @return
     */
    public static double roundInt(String value) {
        return round(value, 0);
    }

    /**
     * 随机数[整数]
     *
     * @param value
     * @return
     */
    public static double roundInt(double value) {
        return round(value, 0);
    }

    /**
     * 数字格式化
     *
     * @param value
     * @param precious
     * @return
     */
    public static String format(double value, int precious) {
        return String.valueOf(round(value, precious));
    }

    /**
     * 随机数
     *
     * @param value
     * @param precious
     * @return
     */
    public static double round(String value, int precious) {
        try {
            if (value == null || "".equals(value.trim())) return 0;

            BigDecimal bg = new BigDecimal(Double.valueOf(value)).setScale(precious, RoundingMode.UP);

            return bg.doubleValue();
        } catch (Exception exc) {
            log.warn("值=" + value + ", 精度=" + precious, exc);

            return 0.0;
        }
    }

    /**
     * 随机数
     *
     * @param value
     * @param precious
     * @return
     */
    public static double round(double value, int precious) {
        try {
            BigDecimal bg = new BigDecimal(value).setScale(precious, RoundingMode.HALF_UP);

            return bg.doubleValue();
        } catch (Exception exc) {
            log.warn("值=" + value + ", 精度=" + precious, exc);

            return 0.0;
        }
    }

    /**
     * 拿小的数
     *
     * @return BigDecimal
     */
    public static BigDecimal min(BigDecimal v1, BigDecimal v2) {
        return v1.compareTo(v2) < 0 ? v1 : v2;
    }

    /**
     * 0到N 随机一个整数
     *
     * @param n 长度
     * @return 0<= random and random < n
     */
    public static int getRandom(int n) {
        Random ran = new Random();
        return ran.nextInt(n);
    }

    /**
     * 获取指定位数随机数
     *
     * @return String
     */
    public static String getRandomNumber(int digit) {
        String result = "";
        if (digit <= 0) {
            return result;
        }
        for (int i = 0; i < digit; i++) {
            result += getRandom(10);
        }
        return result;
    }

    /**
     * 乘法
     *
     * @param val1
     * @param val2
     * @return int
     */
    public static Integer multiplyDouToInt(double val1, double val2) {
        BigDecimal b1 = new BigDecimal(val1 + "");
        BigDecimal b2 = new BigDecimal(val2 + "");
        return b1.multiply(b2).intValue();
    }

    public static Double toDouble(String val) {
        BigDecimal b1 = new BigDecimal(val);
        return Double.parseDouble(df_2.format(b1));
    }

    public static Double toDouble(double val) {
        BigDecimal b1 = new BigDecimal(val + "");
        return Double.parseDouble(df_2.format(b1));
    }

    public static Double toDouble(BigDecimal b1) {
        return Double.parseDouble(df_2.format(b1));
    }

    public static String toString(double value) {
        return df_2.format(value);
    }

    /**
     * 比大小。
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean compare(BigDecimal v1, BigDecimal v2) {
        if (v1.compareTo(v2) == -1) {
            System.out.println("a小于b");
        }

        if (v1.compareTo(v2) == 0) {
            System.out.println("a等于b");
        }

        if (v1.compareTo(v2) == 1) {
            System.out.println("a大于b");
        }

        if (v1.compareTo(v2) > -1) {
            System.out.println("a大于等于b");
        }

        if (v1.compareTo(v2) < 1) {
            System.out.println("a小于等于b");
        }
        return true;
    }


    /**
     * 等于
     * @param v1
     * @param v2
     * @return
     */
    public static boolean eq(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) == 0;
    }

    /**
     * 大于
     * @param v1
     * @param v2
     * @return
     */
    public static boolean gt(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) > 0;
    }

    /**
     * 大于等于
     * @param v1
     * @param v2
     * @return
     */
    public static boolean gte(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) >= 0;
    }

    /**
     * 小于
     * @param v1
     * @param v2
     * @return
     */
    public static boolean lt(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) < 0;
    }

    /**
     * 小于等于
     * @param v1
     * @param v2
     * @return
     */
    public static boolean lte(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) <= 0;
    }
}
