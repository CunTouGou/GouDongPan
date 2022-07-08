package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.model.file.StorageBean;

/**
 * @author CunTouGou
 * @date 2022/4/29 18:10
 */

public interface StorageService extends IService<StorageBean> {

    /**
     * 获取总存储大小
     * @param userId 用户id
     * @return 总存储大小
     */
    Long getTotalStorageSize(Long userId);

    /**
     * 检查存储
     * @param userId 用户id
     * @param fileSize 文件大小
     * @return 检查结果
     */
    boolean checkStorage(Long userId, Long fileSize);
}
