package com.brezze.share.communication.aop;

import com.brezze.share.utils.common.constant.HeaderCst;
import com.brezze.share.utils.common.date.DateUtil;
import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.exception.BrezException;
import com.brezze.share.utils.common.http.HttpUtil;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.dto.ApiLogDTO;
import com.brezze.share.utils.common.result.Rest;
import com.brezze.share.utils.common.string.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class AspectApiLog {

    /**
     * 忽略url
     */
    private static final String[] IGNORE_URL = {
            // knife4j文档接口
            "/v2/api-docs",
            "/api/v2/api-docs",
    };


    /**
     * 切点-定义一个切入点，可以是一个规则表达式，比如下例中某个 package 下的所有函数，也可以是一个注解等
     * <p>
     * execution() 为表达式主体
     * 第一个 * 号的位置：表示返回值类型，* 表示所有类型
     * 包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包的所有子包，com.itcodai.course09.controller 包、子包下所有类的方法
     * 第二个 * 号的位置：表示类名，* 表示所有类
     * *(..) ：这个星号表示方法名，* 表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数
     */
    @Pointcut("execution(* com.brezze.share.communication.controller..*.*(..))")
    public void aspect() {
    }

    /**
     * 环绕-在切入点前后切入内容，并自己控制何时执行切入点自身的内容(不与@AfterThrowing混用)
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // log.info("aspect around start...");
        // 请求开始时间
        LocalDateTime start = LocalDateTime.now();
        // 获取当前的HttpServletRequest对象
        // 注意：请求中的 body 内容仅能调用 request.getInputStream()， request.getReader()和request.getParameter("key") 方法读取一次
        // 重复读取会报 java.io.IOException: Stream closed 异常。
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Rest rest;
        try {
            // proceed
            rest = (Rest) point.proceed();
        } catch (BrezException e) {
            log.error("brezze exception: code-[{}], msg-[{}]", e.getCode(), e.getMsg());
            rest = Rest.failure(e);
        } catch (Exception e) {
            log.error("aspect exception - ", e);
            rest = handlingException(e);
        }

        // 语言转换
        rest = languageConvert(rest, request);

        // 记录接口日志
        recordApiLog(rest, start, request, point);

        // log.info("aspect around end...");
        return rest;
    }

    /**
     * 记录接口日志
     *
     * @param rest
     * @param start
     * @param request
     * @param point
     */
    private void recordApiLog(Object rest, LocalDateTime start, HttpServletRequest request, ProceedingJoinPoint point) {
        if (isIgnoreUrl(request.getRequestURI())) {
            return;
        }
        LocalDateTime end = LocalDateTime.now();
        ApiLogDTO apiLogDTO = new ApiLogDTO();
        apiLogDTO.setMethod(request.getMethod());
        apiLogDTO.setUri(request.getRequestURI());
        apiLogDTO.setIp(HttpUtil.getIpAddress(request));
        apiLogDTO.setHeaders(HttpUtil.getRequestHeaders(request));
        apiLogDTO.setQueryString(request.getQueryString() != null ? request.getQueryString() : "");
        apiLogDTO.setBody(getParameter(point));
        apiLogDTO.setResponse(GsonUtil.toJson(rest));
        apiLogDTO.setBeginDate(DateUtil.format(start, DateUtil.DATE_MSEL_TIME_PATTERN));
        apiLogDTO.setEndDate(DateUtil.format(end, DateUtil.DATE_MSEL_TIME_PATTERN));
        apiLogDTO.setElapsedTime((DateUtil.getTwoTimeDiffMillisSecond(start, end)) + "ms");
        // 输出日志
        log.info(apiLogDTO.format());
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private String getParameter(ProceedingJoinPoint point) {
        List<Object> argsList = new ArrayList<>();

        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argsList.add(point.getArgs()[i]);
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();
                if (StringUtil.isNotEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                if (point.getArgs()[i] instanceof MultipartFile
                        || point.getArgs()[i] instanceof MultipartFile[]) {
                    map.put(key, point.getArgs()[i].toString());
                } else {
                    map.put(key, point.getArgs()[i]);
                }

                argsList.add(map);
            }
        }

        if (argsList.size() == 0) {
            return "";
        } else if (argsList.size() == 1) {
            return GsonUtil.toJson(argsList.get(0));
        } else {
            return GsonUtil.toJson(argsList);
        }
    }

    /**
     * 语言转换
     *
     * @param rest
     * @param request
     */
    private Rest languageConvert(Rest rest, HttpServletRequest request) {
        // 如果rest为null（void方法），直接返回null
        if (rest == null) {
            return null;
        }

        // 获取语言
        String language = request.getHeader(HeaderCst.LANGUAGE);
        // 不是默认英文，则转换语言
        if (StringUtil.isNotEmpty(language)) {
            for (Hint hint : Hint.values()) {
                if (hint.getCode().equals(rest.getCode())) {
                    rest.setMessage(hint.getMsg(language));
                    break;
                }
            }
        }

        return rest;
    }

    /**
     * 是否忽略url
     *
     * @return true-忽略 false-不忽略
     */
    private static boolean isIgnoreUrl(String path) {
        for (int i = 0; i < IGNORE_URL.length; i++) {
            if (path.startsWith(IGNORE_URL[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * 处理异常
     *
     * @param e
     * @return
     */
    private Rest handlingException(Exception e) {
        Rest rest = Rest.failure(Hint.FAILURE);

        // 上传文件超出最大限制异常
        if (e instanceof MaxUploadSizeExceededException) {
            rest = Rest.failure(Hint.BAD_PARAMETER);
        }
        // 请求参数缺少异常
        if (e instanceof MissingServletRequestParameterException) {
            rest = Rest.failure(Hint.BAD_PARAMETER);
        }
        // 业务异常
        if (e instanceof BrezException) {
            rest = Rest.failure(((BrezException) e));
        }
        // 方法参数校验异常
        if (e instanceof MethodArgumentNotValidException) {
            rest = Rest.failure(Hint.BAD_PARAMETER);
            FieldError fieldError = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError();
            if (fieldError != null) {
                log.error(fieldError.getField() + ":" + fieldError.getDefaultMessage());
                switch (fieldError.getField()) {
                    default: {
                        rest = Rest.failure(Hint.BAD_PARAMETER);
                        break;
                    }


                }
            }
        }
        return rest;
    }
}
