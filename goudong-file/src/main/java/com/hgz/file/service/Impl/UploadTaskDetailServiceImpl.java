package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.service.UploadTaskDetailService;
import com.hgz.file.model.file.UploadTaskDetail;
import com.hgz.file.mapper.UploadTaskDetailMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author CunTouGou
 * @date 2022/5/1 14:30
 */

public class UploadTaskDetailServiceImpl extends ServiceImpl<UploadTaskDetailMapper, UploadTaskDetail> implements UploadTaskDetailService {

    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;

    @Override
    public List<Integer> getUploadedChunkNumList(String identifier) {
        return uploadTaskDetailMapper.selectUploadedChunkNumList(identifier);
    }
}