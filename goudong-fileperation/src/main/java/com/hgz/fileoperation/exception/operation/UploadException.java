package com.hgz.fileoperation.exception.operation;

/**
 * @author CunTouGou
 * @date 2022/5/10 23:10
 */

public class UploadException extends RuntimeException{
    public UploadException(Throwable cause) {
        super("上传出现了异常", cause);
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }

}