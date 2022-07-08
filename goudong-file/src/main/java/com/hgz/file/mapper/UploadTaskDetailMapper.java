package com.hgz.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hgz.file.model.file.UploadTaskDetail;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/30 0:24
 */
public interface UploadTaskDetailMapper extends BaseMapper<UploadTaskDetail> {

    /**
     * 查找上传文科块列表
     * @param identifier 文件标识
     * @return 上传任务详情
     */
    List<Integer> selectUploadedChunkNumList(String identifier);
}
