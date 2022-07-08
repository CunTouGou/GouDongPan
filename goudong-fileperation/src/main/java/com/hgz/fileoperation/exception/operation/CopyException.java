package com.hgz.fileoperation.exception.operation;

import com.hgz.fileoperation.exception.FileOperationException;

public class CopyException extends FileOperationException {
    public CopyException(Throwable cause) {
        super("创建出现了异常", cause);
    }

    public CopyException(String message) {
        super(message);
    }

    public CopyException(String message, Throwable cause) {
        super(message, cause);
    }

}
