package com.hgz.fileoperation.operation.preview.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.model.ThumbImage;
import com.hgz.fileoperation.operation.preview.Previewer;
import com.hgz.fileoperation.operation.preview.domain.PreviewFile;
import com.hgz.fileoperation.util.AliYunUtils;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Data
@Slf4j
public class AliyunOSSPreviewer extends Previewer {


    private AliYunConfig aliyunConfig;

    public AliyunOSSPreviewer(){

    }

    public AliyunOSSPreviewer(AliYunConfig aliyunConfig, ThumbImage thumbImage) {
        this.aliyunConfig = aliyunConfig;
        setThumbImage(thumbImage);
    }


    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                FileOperationUtils.getAliyunObjectNameByFileUrl(previewFile.getFileUrl()));
        InputStream inputStream = ossObject.getObjectContent();
        previewFile.setOssClient(ossClient);
        return inputStream;
    }

}
