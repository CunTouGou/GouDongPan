package com.hgz.common.exception;

/**
 * @author CunTouGou
 * @date 2022/5/10 2022/5/10
 */

public class NotSameFileExpection extends Exception {
    public NotSameFileExpection() {
        super("File MD5 Different");
    }
}
