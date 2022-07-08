package com.hgz.fileoperation.autoconfiguration;

import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.model.ThumbImage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author CunTouGou
 * @date 2022/5/10 23:51
 */

@Data
@ConfigurationProperties(prefix = "file-operation")
public class FileOperationProperties {
    private String bucketName;
    private String storageType;
    private String localStoragePath;
    private AliYunConfig aliYun = new AliYunConfig();
    private ThumbImage thumbImage = new ThumbImage();
}
