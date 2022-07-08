package com.hgz.fileoperation.config;

import com.hgz.fileoperation.model.AliYunOSS;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/5/10 23:52
 */

@Data
public class  AliYunConfig {
    private AliYunOSS oss = new AliYunOSS();
}
