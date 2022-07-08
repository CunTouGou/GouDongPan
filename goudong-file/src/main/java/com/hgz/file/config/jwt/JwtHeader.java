package com.hgz.file.config.jwt;

import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/22 18:01
 */
@Data
public class JwtHeader {

    private String alg;
    private String typ;
}
