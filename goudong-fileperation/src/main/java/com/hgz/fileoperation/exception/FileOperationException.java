package com.hgz.fileoperation.exception;

/**
 * @author CunTouGou
 * @date 2022/5/11 0:24
 */

public class FileOperationException extends RuntimeException {
    public FileOperationException(Throwable cause) {
        super("统一文件操作平台（FileOperation）出现异常", cause);
    }

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}