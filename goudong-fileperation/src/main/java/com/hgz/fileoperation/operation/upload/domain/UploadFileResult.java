package com.hgz.fileoperation.operation.upload.domain;

import com.hgz.fileoperation.constant.StorageTypeEnum;
import com.hgz.fileoperation.constant.UploadFileStatusEnum;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class UploadFileResult {
    private String fileName;
    private String extendName;
    private long fileSize;
    private String fileUrl;
    private String identifier;
    private StorageTypeEnum storageType;
    private UploadFileStatusEnum status;
    private BufferedImage bufferedImage;

}
