package com.hgz.fileoperation.operation.upload.domain;

import lombok.Data;

@Data
    public class UploadFileInfo {
        private String bucketName;
        private String key;
        private String uploadId;
    }