package com.hgz.fileoperation.operation.copy.product;

import com.aliyun.oss.OSS;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.operation.copy.Copier;
import com.hgz.fileoperation.operation.copy.domain.CopyFile;
import com.hgz.fileoperation.util.AliYunUtils;
import com.hgz.fileoperation.util.FileOperationUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.UUID;

public class AliyunOSSCopier extends Copier {

    private AliYunConfig aliyunConfig;

    public AliyunOSSCopier(){

    }

    public AliyunOSSCopier(AliYunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = FileOperationUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        try {
            ossClient.putObject(aliyunConfig.getOss().getBucketName(), fileUrl, inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
            ossClient.shutdown();
        }
        return fileUrl;
    }

}
