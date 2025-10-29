package com.brezze.share.utils.common.number;

import java.math.BigDecimal;

/**
 * NumericUtils
 *
 * @author penf
 * @decription 数值运算相关工具类
 * @date 2019/03/01
 * <p>
 * //TODO 方法名divideToCeil（实际是向下取整操作）的计算与Ceil（向上取整）的作用严重不符合
 */
public class NumericUtil {

    /**
     * 默认精度
     */
    private static final int DEFAULT_SCALE = 2;

    /**
     * 我的精度
     */
    private static final int USER_SCALE = 4;


    /**
     * 加法运算。
     *
     * @param v1 被加数
     */
    public static BigDecimal add(BigDecimal... v1) {
        BigDecimal result = new BigDecimal("0");
        for (BigDecimal b : v1) {
            result = calculate(result, b, "+");
        }
        return result;
    }

    /**
     * 加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {

        return calculate(v1, v2, "+");
    }

    /**
     * 加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(float v1, float v2) {
        BigDecimal value1 = new BigDecimal(Float.toString(v1));
        BigDecimal value2 = new BigDecimal(Float.toString(v2));

        return calculate(value1, value2, "+");
    }

    /**
     * 加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(double v1, double v2) {
        BigDecimal value1 = new BigDecimal(v1 + "");
        BigDecimal value2 = new BigDecimal(v2 + "");

        return calculate(value1, value2, "+");
    }

    /**
     * 减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {

        return calculate(v1, v2, "-");
    }

    /**
     * 减法运算
     *
     * @param v1    被减数
     * @param v2    减数
     * @param scale 精确到多少位
     * @return 两个参数的差
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2, int scale) {
        return calculate(v1, v2, "-", scale);
    }

    /**
     * 减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal... v2) {
        if (v1 == null) {
            v1 = BigDecimal.ZERO;
        }
        BigDecimal result = v1;
        for (BigDecimal d : v2) {
            result = calculate(result, d, "-", DEFAULT_SCALE);
        }
        return result;
    }

    /**
     * 减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal subtract(float v1, float v2) {
        BigDecimal value1 = new BigDecimal(Float.toString(v1));
        BigDecimal value2 = new BigDecimal(Float.toString(v2));

        return calculate(value1, value2, "-");
    }

    /**
     * 减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal subtract(double v1, double v2) {
        BigDecimal value1 = new BigDecimal(v1 + "");
        BigDecimal value2 = new BigDecimal(v2 + "");
        return calculate(value1, value2, "-");
    }

    /**
     * 减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double subtract(double v1, double... v2) {
        BigDecimal result = new BigDecimal(v1 + "");
        for (double d : v2) {
            result = calculate(result, new BigDecimal(d + ""), "-", DEFAULT_SCALE);
        }
        return result.doubleValue();
    }

    /**
     * 提供乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {

        return calculate(v1, v2, "*");
    }

    /**
     * 提供乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2, int scale) {
        return calculate(v1, v2, "*", scale);
    }

    /**
     * 提供乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal multiply(float v1, float v2) {
        BigDecimal value1 = new BigDecimal(Float.toString(v1));
        BigDecimal value2 = new BigDecimal(Float.toString(v2));

        return calculate(value1, value2, "*");
    }

    /**
     * 提供乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal multiply(double v1, double v2) {
        BigDecimal value1 = new BigDecimal(v1 + "");
        BigDecimal value2 = new BigDecimal(v2 + "");

        return calculate(value1, value2, "*");
    }

    /**
     * 乘以100 ，一般用户钱 元转分时
     *
     * @param v1 被乘数
     * @return 两个参数的积
     */
    public static int multiply100ToInt(double v1) {
        BigDecimal value1 = new BigDecimal(v1 + "");
        BigDecimal value2 = new BigDecimal("100");
        return calculate(value1, value2, "*").intValue();
    }

    /**
     * 乘以100
     *
     * @param v1 被乘数
     * @return 两个参数的积
     */
    public static int multiply100ToInt(BigDecimal v1) {
        BigDecimal value2 = new BigDecimal("100");
        return calculate(v1, value2, "*").intValue();
    }

    /**
     * 乘以100
     *
     * @param v1 被乘数
     * @return 两个参数的积
     */
    public static long multiply100ToLong(BigDecimal v1) {
        BigDecimal value2 = new BigDecimal("100");
        return calculate(v1, value2, "*").longValue();
    }

    /**
     * 元转分
     *
     * @param v1 被乘数
     * @return 两个参数的积
     */
    public static int convertY2F(BigDecimal v1) {
        if (v1 == null) return 0;
        BigDecimal value2 = new BigDecimal("100");
        return calculate(v1, value2, "*").intValue();
    }

    /**
     * 分转元
     *
     * @param v1 被乘数
     * @return 两个参数的积
     */
    public static BigDecimal convertF2Y(Number v1) {
        if (v1 == null) return BigDecimal.ZERO;
        BigDecimal value2 = new BigDecimal("100");
        return calculate(new BigDecimal(String.valueOf(v1)), value2, "/");
    }


    /**
     * 提供（相对）精确的除法运算,默认精度小数点后两位
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {

        return divide(v1, v2, DEFAULT_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale) {

        return calculate(v1, v2, "/", scale);
    }

    /**
     * 提供（相对）精确的除法运算,默认精度小数点后两位
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal divide(float v1, float v2) {

        return divide(v1, v2, DEFAULT_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal divide(float v1, float v2, int scale) {
        BigDecimal value1 = new BigDecimal(Float.toString(v1));
        BigDecimal value2 = new BigDecimal(Float.toString(v2));

        return calculate(value1, value2, "/", scale);
    }

    /**
     * 提供（相对）精确的除法运算,默认精度小数点后两位
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal divide(double v1, double v2) {

        return divide(v1, v2, DEFAULT_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算,默认精度小数点后两位
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal divide(String v1, String v2) {
        return divide(new BigDecimal(v1), new BigDecimal(v2), DEFAULT_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal divide(double v1, double v2, int scale) {
        BigDecimal value1 = new BigDecimal(Double.toString(v1));
        BigDecimal value2 = new BigDecimal(Double.toString(v2));
        return calculate(value1, value2, "/", scale);
    }

    /**
     * 提供（相对）精确的除法运算,默认精度小数点后两位
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal divide(int v1, int v2) {

        return divide(v1, v2, DEFAULT_SCALE);
    }

    public static long divideToDown(long v1, long v2) {
        return divide(v1, v2, BigDecimal.ROUND_DOWN).longValue();
    }

    /**
     * 提供（相对）精确的除法运算,默认精度小数点后两位
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static int divideToCeil(int v1, int v2) {
        BigDecimal n1 = new BigDecimal(v1 + "");
        BigDecimal n2 = new BigDecimal(v2 + "");
        return n1.divide(n2, BigDecimal.ROUND_UP).intValue();
    }

    /**
     * 提供（相对）精确的除法运算
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static long divideToCeil(double v1, double v2) {
        BigDecimal n1 = new BigDecimal(v1 + "");
        BigDecimal n2 = new BigDecimal(v2 + "");
        return n1.divide(n2, BigDecimal.ROUND_UP).longValue();
    }

    /**
     * 提供（相对）精确的除法运算,默认精度小数点后两位
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static long divideToCeil(long v1, long v2) {
        BigDecimal n1 = new BigDecimal(v1 + "");
        BigDecimal n2 = new BigDecimal(v2 + "");
        return n1.divide(n2, BigDecimal.ROUND_UP).longValue();
    }

    public static BigDecimal divideToCeil(BigDecimal v1, BigDecimal v2) {
        BigDecimal n1 = new BigDecimal(v1 + "");
        BigDecimal n2 = new BigDecimal(v2 + "");
        return n1.divide(n2, BigDecimal.ROUND_UP);
    }

    /**
     * 提供（相对）精确的除法运算,向下取整
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal divideToFloor(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2, 0, BigDecimal.ROUND_DOWN);
    }

    /**
     * 提供（相对）精确的除法运算,向下取整
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static long divideToFloor(long v1, long v2) {
        BigDecimal n1 = new BigDecimal(v1 + "");
        BigDecimal n2 = new BigDecimal(v2 + "");
        return n1.divide(n2, 0, BigDecimal.ROUND_DOWN).longValue();
    }

    /**
     * 提供（相对）精确的除法运算
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal divide(int v1, int v2, int scale) {
        BigDecimal value1 = new BigDecimal(Integer.toString(v1));
        BigDecimal value2 = new BigDecimal(Integer.toString(v2));

        return calculate(value1, value2, "/", scale);
    }


    /**
     * 运算
     *
     * @param v1       运算值1
     * @param v2       运算值2
     * @param operator 操作符
     */
    private static BigDecimal calculate(BigDecimal v1, BigDecimal v2, String operator) {
        if (null == v1) {
            v1 = BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ZERO;
        }
        return calculate(v1, v2, operator, DEFAULT_SCALE);
    }


    /**
     * 运算
     *
     * @param v1       运算值1
     * @param v2       运算值2
     * @param operator 操作符
     * @param scale    精度
     */
    private static BigDecimal calculate(BigDecimal v1, BigDecimal v2, String operator, int scale) {
        if (null == v1) {
            v1 = BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ZERO;
        }
        if (null == operator || "".equals(operator.trim())) {
            return BigDecimal.ZERO;
        }
        if (scale < 0) {
            scale = DEFAULT_SCALE;
        }
        BigDecimal result = BigDecimal.ZERO;
        switch (operator) {
            case "+":
                result = v1.add(v2);
                break;
            case "-":
                result = v1.subtract(v2);
                break;
            case "*":
                result = v1.multiply(v2);
                break;
            case "/":
                result = v1.divide(v2, scale, BigDecimal.ROUND_HALF_UP);
                break;
            default:
                break;
        }

        return result.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

}
