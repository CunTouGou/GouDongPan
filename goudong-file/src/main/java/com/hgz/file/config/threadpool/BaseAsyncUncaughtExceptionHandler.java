package com.hgz.file.config.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author CunTouGou
 * @date 2022/4/24 04:42
 */
@Slf4j
public class BaseAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    /**
     * 处理未捕获的异常
     * @param throwable 异常
     * @param method 方法
     * @param objects 参数
     */
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        log.error("捕获线程异常method[{}] params{}", method, Arrays.toString(objects));
        log.error("线程异常");
    }
}
