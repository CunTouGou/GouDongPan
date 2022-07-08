package com.hgz.fileoperation.operation.delete.product;

import com.aliyun.oss.OSS;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.operation.delete.Deleter;
import com.hgz.fileoperation.operation.delete.domain.DeleteFile;
import com.hgz.fileoperation.util.AliYunUtils;
import com.hgz.fileoperation.util.FileOperationUtils;


public class AliyunOSSDeleter extends Deleter {
    private AliYunConfig aliyunConfig;

    public AliyunOSSDeleter(){

    }

    public AliyunOSSDeleter(AliYunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }
    @Override
    public void delete(DeleteFile deleteFile) {
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        try {
            ossClient.deleteObject(aliyunConfig.getOss().getBucketName(), FileOperationUtils.getAliyunObjectNameByFileUrl(deleteFile.getFileUrl()));
        } finally {
            ossClient.shutdown();
        }
        deleteCacheFile(deleteFile);
    }
}
