package com.hgz.fileoperation.operation.write.product;

import com.aliyun.oss.OSS;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.operation.write.Writer;
import com.hgz.fileoperation.operation.write.domain.WriteFile;
import com.hgz.fileoperation.util.AliYunUtils;
import com.hgz.fileoperation.util.FileOperationUtils;

import java.io.InputStream;

public class AliyunOSSWriter extends Writer {

    private AliYunConfig aliyunConfig;

    public AliyunOSSWriter(){

    }

    public AliyunOSSWriter(AliYunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);

        ossClient.putObject(aliyunConfig.getOss().getBucketName(), FileOperationUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream);
        ossClient.shutdown();
    }



}
