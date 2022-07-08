package com.hgz.fileoperation.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.hgz.fileoperation.config.AliYunConfig;

public class AliYunUtils {

    public static OSS getOSSClient(AliYunConfig aliyunConfig) {
        OSS ossClient = new OSSClientBuilder().build(aliyunConfig.getOss().getEndpoint(),
                aliyunConfig.getOss().getAccessKeyId(),
                aliyunConfig.getOss().getAccessKeySecret());;
        return ossClient;
    }

}
