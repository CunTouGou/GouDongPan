package com.hgz.fileoperation.operation.upload.product;


import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.hgz.fileoperation.util.RedisUtil;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.constant.StorageTypeEnum;
import com.hgz.fileoperation.constant.UploadFileStatusEnum;
import com.hgz.fileoperation.operation.upload.Uploader;
import com.hgz.fileoperation.operation.upload.domain.UploadFile;
import com.hgz.fileoperation.operation.upload.domain.UploadFileInfo;
import com.hgz.fileoperation.operation.upload.domain.UploadFileResult;
import com.hgz.fileoperation.operation.upload.request.GouDongMultipartFile;
import com.hgz.fileoperation.util.AliYunUtils;
import com.hgz.fileoperation.util.RedisUtil;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class AliyunOSSUploader extends Uploader {

    @Resource
    private RedisUtil redisUtil;

    private AliYunConfig aliyunConfig;

    public AliyunOSSUploader(){

    }

    public AliyunOSSUploader(AliYunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    protected void doUploadFileChunk(GouDongMultipartFile gouDongMultipartFile, UploadFile uploadFile) throws IOException {

        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        try {
            UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);
            String fileUrl = gouDongMultipartFile.getFileUrl();
            if (uploadFileInfo == null) {

                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliyunConfig.getOss().getBucketName(), fileUrl);
                InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
                String uploadId = upresult.getUploadId();

                uploadFileInfo = new UploadFileInfo();
                uploadFileInfo.setBucketName(aliyunConfig.getOss().getBucketName());
                uploadFileInfo.setKey(fileUrl);
                uploadFileInfo.setUploadId(uploadId);

                redisUtil.set("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest", JSON.toJSONString(uploadFileInfo));

            }

            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(uploadFileInfo.getBucketName());
            uploadPartRequest.setKey(uploadFileInfo.getKey());
            uploadPartRequest.setUploadId(uploadFileInfo.getUploadId());
            uploadPartRequest.setInputStream(gouDongMultipartFile.getUploadInputStream());
            uploadPartRequest.setPartSize(gouDongMultipartFile.getSize());
            uploadPartRequest.setPartNumber(uploadFile.getChunkNumber());
            log.debug(JSON.toJSONString(uploadPartRequest));

            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);

            log.debug("上传结果：" + JSON.toJSONString(uploadPartResult));

            if (redisUtil.hasKey("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags")) {
                List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"), PartETag.class);
                partETags.add(uploadPartResult.getPartETag());
                redisUtil.set("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags", JSON.toJSONString(partETags));
            } else {
                List<PartETag> partETags = new ArrayList<PartETag>();
                partETags.add(uploadPartResult.getPartETag());
                redisUtil.set("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags", JSON.toJSONString(partETags));
            }
        } finally {
            ossClient.shutdown();
        }


    }

    @Override
    protected UploadFileResult organizationalResults(GouDongMultipartFile gouDongMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        uploadFileResult.setFileUrl(uploadFileInfo.getKey());
        uploadFileResult.setFileName(gouDongMultipartFile.getFileName());
        uploadFileResult.setExtendName(gouDongMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(gouDongMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.ALIYUN_OSS);
        uploadFileResult.setIdentifier(uploadFile.getIdentifier());
        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
            log.info("分片上传完成");
            completeMultipartUpload(uploadFile);
            redisUtil.deleteKey("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            redisUtil.deleteKey("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags");
            redisUtil.deleteKey("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest");
            if (FileOperationUtils.isImageFile(uploadFileResult.getExtendName())) {

                OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
                OSSObject ossObject = ossClient.getObject(aliyunConfig.getOss().getBucketName(),
                        FileOperationUtils.getAliyunObjectNameByFileUrl(uploadFileResult.getFileUrl()));
                InputStream is = ossObject.getObjectContent();
                BufferedImage src = null;
                try {
                    src = ImageIO.read(is);
                    uploadFileResult.setBufferedImage(src);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }

            }
            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);

        }
        return uploadFileResult;
    }


    /**
     * 将文件分块进行升序排序并执行文件上传。
     * @param uploadFile 上传信息
     */
    private void completeMultipartUpload(UploadFile uploadFile) {

        List<PartETag> partETags = JSON.parseArray(redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":partETags"), PartETag.class);

        Collections.sort(partETags, Comparator.comparingInt(PartETag::getPartNumber));

        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId(),
                        partETags);
        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        // 完成上传。
        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        ossClient.shutdown();

    }

    /**
     * 取消上传
     */
    @Override
    public void cancelUpload(UploadFile uploadFile) {

        UploadFileInfo uploadFileInfo = JSON.parseObject(redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploadPartRequest"), UploadFileInfo.class);

        OSS ossClient = AliYunUtils.getOSSClient(aliyunConfig);
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(aliyunConfig.getOss().getBucketName(),
                        uploadFileInfo.getKey(),
                        uploadFileInfo.getUploadId());
        ossClient.abortMultipartUpload(abortMultipartUploadRequest);
        ossClient.shutdown();
    }


}
