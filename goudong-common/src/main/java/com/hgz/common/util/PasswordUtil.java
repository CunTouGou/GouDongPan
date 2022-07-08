package com.hgz.common.util;

import java.util.Random;

/**
 * @author CunTouGou
 * @date 2022/5/11 4:00
 */

public class PasswordUtil {
    public static String getSaltValue() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        return sb.toString();
    }
}
