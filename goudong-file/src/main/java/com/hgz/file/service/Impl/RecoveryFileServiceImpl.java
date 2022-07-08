package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.component.FileDealComp;
import com.hgz.file.model.file.RecoveryFile;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.io.GouDongFile;
import com.hgz.file.mapper.RecoveryFileMapper;
import com.hgz.file.mapper.UserFileMapper;
import com.hgz.file.service.RecoveryFileService;
import com.hgz.file.vo.file.RecoveryFileListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 23:22
 */

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class RecoveryFileServiceImpl extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements RecoveryFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    FileDealComp fileDealComp;


    @Override
    public void deleteUserFileByDeleteBatchNum(String deleteBatchNum) {


        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.delete(userFileLambdaQueryWrapper);



    }

    @Override
    public void restorefile(String deleteBatchNum, String filePath, Long sessionUserId) {

        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 0)
                .set(UserFile::getDeleteBatchNum, "")
                .eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.update(null, userFileLambdaUpdateWrapper);
        GouDongFile qiwenFile = new GouDongFile(filePath, true);
        fileDealComp.restoreParentFilePath(qiwenFile, sessionUserId);

        fileDealComp.deleteRepeatSubDirFile(filePath, sessionUserId);
        // TODO 如果被还原的文件已存在，暂未实现

        LambdaQueryWrapper<RecoveryFile> recoveryFileServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recoveryFileServiceLambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, deleteBatchNum);
        recoveryFileMapper.delete(recoveryFileServiceLambdaQueryWrapper);
    }

    @Override
    public List<RecoveryFileListVo> selectRecoveryFileList(Long userId) {
        return recoveryFileMapper.selectRecoveryFileList(userId);
    }
}