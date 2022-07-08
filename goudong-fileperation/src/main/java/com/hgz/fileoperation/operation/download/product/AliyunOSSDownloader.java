package com.hgz.fileoperation.operation.download.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.operation.download.Downloader;
import com.hgz.fileoperation.operation.download.domain.DownloadFile;
import com.hgz.fileoperation.util.AliYunUtils;
import com.hgz.fileoperation.util.FileOperationUtils;

import java.io.InputStream;

public class AliyunOSSDownloader extends Downloader {

    private AliYunConfig aliyunConfig;

    public AliyunOSSDownloader(){

    }

    public AliyunOSSDownloader(AliYunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                FileOperationUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
        downloadFile.setOssClient(ossClient);
        return inputStream;
    }

}
