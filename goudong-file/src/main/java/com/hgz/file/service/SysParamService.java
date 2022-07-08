package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.model.file.SysParam;

/**
 * @author CunTouGou
 * @date 2022/5/11 2:22
 */

public interface SysParamService extends IService<SysParam> {
    String getValue(String key);
}
