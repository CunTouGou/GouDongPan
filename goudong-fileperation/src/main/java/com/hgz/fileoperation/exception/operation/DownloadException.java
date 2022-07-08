package com.hgz.fileoperation.exception.operation;

/**
 * @author CunTouGou
 * @date 2022/5/10 23:43
 */

public class DownloadException extends RuntimeException{
    public DownloadException(Throwable cause) {
        super("下载出现了异常", cause);
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }

}
