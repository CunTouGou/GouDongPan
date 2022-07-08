package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.model.file.RecoveryFile;
import com.hgz.file.vo.file.RecoveryFileListVo;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/30 20:59
 */

public interface RecoveryFileService extends IService<RecoveryFile> {
    /**
     * 根据删除批次号删除文件
     * @param deleteBatchNum 删除批次号
     */
    void deleteUserFileByDeleteBatchNum(String deleteBatchNum);

    /**
     * 恢复文件
     * @param deleteBatchNum 删除批次号
     * @param filePath 文件路径
     * @param sessionUserId 用户id
     */
    void restorefile(String deleteBatchNum, String filePath, Long sessionUserId);

    /**
     * 查询回收站文件列表
     * @param userId 用户id
     * @return 回收站文件列表
     */
    List<RecoveryFileListVo> selectRecoveryFileList(Long userId);
}