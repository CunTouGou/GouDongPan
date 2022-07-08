package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.service.ShareService;
import com.hgz.file.model.file.RecoveryFile;
import com.hgz.file.model.file.Share;
import com.hgz.file.model.file.ShareFile;
import com.hgz.file.dto.sharefile.ShareListDTO;
import com.hgz.file.mapper.RecoveryFileMapper;
import com.hgz.file.mapper.ShareMapper;
import com.hgz.file.vo.share.ShareFileListVO;
import com.hgz.file.vo.share.ShareListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 18:12
 */


@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Resource
    ShareMapper shareMapper;

    @Override
    public List<ShareListVO> selectShareList(ShareListDTO shareListDTO, Long userId) {
        Long beginCount = (shareListDTO.getCurrentPage() - 1) * shareListDTO.getPageCount();
        return shareMapper.selectShareList(shareListDTO.getShareFilePath(),
                shareListDTO.getShareBatchNum(),
                beginCount, shareListDTO.getPageCount(), userId);
    }

    @Override
    public int selectShareListTotalCount(ShareListDTO shareListDTO, Long userId) {
        return shareMapper.selectShareListTotalCount(shareListDTO.getShareFilePath(), shareListDTO.getShareBatchNum(), userId);
    }
}