package com.hgz.common.exception;

/**
 * @author CunTouGou
 * @date 2022/5/10 2022/5/10
 */

public class NotLoginException extends RuntimeException{
    public NotLoginException() {
        super("未登录");
    }
    public NotLoginException(Throwable cause) {
        super("未登录", cause);
    }

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}