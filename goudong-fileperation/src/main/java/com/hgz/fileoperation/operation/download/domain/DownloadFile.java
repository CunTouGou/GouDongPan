package com.hgz.fileoperation.operation.download.domain;

import com.aliyun.oss.OSS;
import lombok.Data;

@Data
public class DownloadFile {
    private String fileUrl;
    private OSS ossClient;
}
