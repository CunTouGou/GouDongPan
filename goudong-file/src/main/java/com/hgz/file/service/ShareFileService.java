package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.model.file.ShareFile;
import com.hgz.file.vo.share.ShareFileListVO;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 22:11
 */

public interface ShareFileService extends IService<ShareFile> {

    /**
     * 批量新增分享文件
     * @param shareFiles 分享文件列表
     */
    void batchInsertShareFile(List<ShareFile> shareFiles);

    /**
     * 分享文件列表
     * @param shareBatchNum 分享批次号
     * @param filePath 文件路径
     * @return 分享文件列表
     */
    List<ShareFileListVO> selectShareFileList(String shareBatchNum, String filePath);
}
