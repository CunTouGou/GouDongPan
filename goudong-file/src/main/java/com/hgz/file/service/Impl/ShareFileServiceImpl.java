package com.hgz.file.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.service.ShareFileService;
import com.hgz.file.service.ShareService;
import com.hgz.file.model.file.Share;
import com.hgz.file.model.file.ShareFile;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.mapper.ShareFileMapper;
import com.hgz.file.mapper.ShareMapper;
import com.hgz.file.mapper.UserFileMapper;
import com.hgz.file.vo.share.ShareFileListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 23:10
 */

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class ShareFileServiceImpl extends ServiceImpl<ShareFileMapper, ShareFile> implements ShareFileService {
    @Resource
    private ShareFileMapper shareFileMapper;
    @Resource
    private UserFileMapper userFileMapper;
    @Override
    public void batchInsertShareFile(List<ShareFile> shareFiles) {
        shareFileMapper.batchInsertShareFile(shareFiles);
    }

    @Override
    public List<ShareFileListVO> selectShareFileList(String shareBatchNum, String filePath) {
        return shareFileMapper.selectShareFileList(shareBatchNum, filePath);
    }

}
