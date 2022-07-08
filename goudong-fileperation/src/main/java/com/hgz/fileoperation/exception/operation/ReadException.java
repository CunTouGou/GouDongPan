package com.hgz.fileoperation.exception.operation;

/**
 * @author CunTouGou
 * @date 2022/5/11 3:25
 */

public class ReadException extends RuntimeException{
    public ReadException(Throwable cause) {
        super("文件读取出现了异常", cause);
    }

    public ReadException(String message) {
        super(message);
    }

    public ReadException(String message, Throwable cause) {
        super(message, cause);
    }
}