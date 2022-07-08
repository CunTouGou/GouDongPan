package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.model.file.UploadTaskDetail;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/5/11 2:08
 */

public interface UploadTaskDetailService extends IService<UploadTaskDetail> {
    /**
     * 获取文件上传块列表
     * @param identifier 文件唯一标识
     * @return 文件上传块列表
     */
    List<Integer> getUploadedChunkNumList(String identifier);
}
