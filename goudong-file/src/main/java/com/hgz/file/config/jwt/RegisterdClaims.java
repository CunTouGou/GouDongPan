package com.hgz.file.config.jwt;

import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/22 18:03
 */
@Data
public class RegisterdClaims {
    private String iss;
    private String exp;
    private String sub;
    private String aud;
}
