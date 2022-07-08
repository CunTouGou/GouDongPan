package com.hgz.file.advice;

import com.hgz.common.exception.NotLoginException;
import com.hgz.common.exception.GouDongException;
import com.hgz.common.result.RestResult;
import com.hgz.common.result.ResultCodeEnum;
import com.hgz.fileoperation.exception.operation.UploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * 该注解为统一异常处理的核心
 * 是一种作用于控制层的切面通知（Advice），该注解能够将通用的@ExceptionHandler、@InitBinder和@ModelAttributes方法收集到一个类型，并应用到所有控制器上
 *
 * @author CunTouGou
 * @date 2022/4/20 14:00
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerAdvice {

    /**
     * 通用异常处理方法
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult error(Exception e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);

        // 通用异常结果
        return RestResult.fail();
    }

    /**
     * 指定异常处理方法 空指针异常
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult error(NullPointerException e) {
        e.printStackTrace();
        log.error("全局异常捕获-->空指针异常：" + e);
        return RestResult.setResult(ResultCodeEnum.NULL_POINT);
    }

    /**
     * 下标越界处理方法
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult error(IndexOutOfBoundsException e) {
        e.printStackTrace();
        log.error("全局异常捕获-->下标越界：" + e);
        return RestResult.setResult(ResultCodeEnum.INDEX_OUT_OF_BOUNDS);
    }

    /**
     * 文件上传异常处理
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(UploadException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public RestResult error(UploadException e) {
        e.printStackTrace();
        log.error("全局异常捕获-->文件上传异常：" + e);
        return RestResult.setResult(ResultCodeEnum.REQUEST_TIMEOUT);
    }

    /**
     * 未登录异常
     * @param e
     * @return
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResult error(NotLoginException e) {
        e.printStackTrace();
        log.error("全局异常捕获-->未登录：" + e);
        return RestResult.setResult(ResultCodeEnum.NOT_LOGIN_ERROR);
    }

    /**
     * 未找到用户异常
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResult error(UsernameNotFoundException e) {
        e.printStackTrace();
        log.error("全局异常捕获-->未找到用户：" + e);
        return RestResult.setResult(ResultCodeEnum.NOT_LOGIN_ERROR);
    }

    /**
     * 方法参数校验
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return RestResult.setResult(ResultCodeEnum.PARAM_ERROR).message(e.getBindingResult().getFieldError().getDefaultMessage());
    }


    /**
     * 自定义异常处理方法
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(GouDongException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public RestResult error(GouDongException e) {
        e.printStackTrace();
        log.error("全局异常捕获-->自定义异常：" + e);
        return RestResult.fail().message(e.getMessage()).code(e.getCode());
    }
}