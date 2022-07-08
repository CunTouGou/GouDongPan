package com.hgz.fileoperation.operation.read.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.exception.operation.ReadException;
import com.hgz.fileoperation.operation.read.Reader;
import com.hgz.fileoperation.operation.read.domain.ReadFile;
import com.hgz.fileoperation.util.AliYunUtils;
import com.hgz.fileoperation.util.ReadFileUtils;
import com.hgz.fileoperation.util.FileOperationUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;

public class AliyunOSSReader extends Reader {

    private AliYunConfig aliyunConfig;

    public AliyunOSSReader(){

    }

    public AliyunOSSReader(AliYunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public String read(ReadFile readFile) {
        String fileUrl = readFile.getFileUrl();
        String fileType = FilenameUtils.getExtension(fileUrl);
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                FileOperationUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        try {
            return ReadFileUtils.getContentByInputStream(fileType, inputStream);
        } catch (IOException e) {
            throw new ReadException("读取文件失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    public InputStream getInputStream(String fileUrl) {
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                FileOperationUtils.getAliyunObjectNameByFileUrl(fileUrl));
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

}
