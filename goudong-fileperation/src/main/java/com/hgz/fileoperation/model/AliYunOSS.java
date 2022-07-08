package com.hgz.fileoperation.model;

import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/5/10 23:54
 */

@Data
public class AliYunOSS {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
}
