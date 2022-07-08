package com.hgz.file.service.Impl;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.common.constant.FileConstant;
import com.hgz.common.util.DateUtil;
import com.hgz.common.util.security.JwtUser;
import com.hgz.common.util.security.SessionUtil;
import com.hgz.file.component.FileDealComp;
import com.hgz.file.model.file.RecoveryFile;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.io.GouDongFile;
import com.hgz.file.mapper.FileMapper;
import com.hgz.file.mapper.FileTypeMapper;
import com.hgz.file.mapper.RecoveryFileMapper;
import com.hgz.file.mapper.UserFileMapper;
import com.hgz.file.service.UserFileService;
import com.hgz.file.vo.file.FileListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author CunTouGou
 * @date 2022/4/29 20:20
 */

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    FileTypeMapper fileTypeMapper;
    @Resource
    FileDealComp fileDealComp;

    // public static Executor executor = Executors.newFixedThreadPool(20);

    public static Executor executor = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getExtendName, extendName)
                .eq(UserFile::getDeleteFlag, "0");
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public IPage<FileListVo> userFileList(Long userId, String filePath, Long currentPage, Long pageCount) {
        Page<FileListVo> page = new Page<>(currentPage, pageCount);
        UserFile userFile = new UserFile();
        JwtUser sessionUserBean = SessionUtil.getSession();
        if (userId == null) {
            userFile.setUserId(sessionUserBean.getUserId());
        } else {
            userFile.setUserId(userId);
        }

        userFile.setFilePath(URLDecoder.decodeForPath(filePath, StandardCharsets.UTF_8));

        return userFileMapper.selectPageVo(page, userFile, null);
    }

    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, long userId) {

        QueryWrapper<UserFile> queryWrapper = new QueryWrapper<UserFile>()
                .eq("userId", userId)
                .eq("filePath", oldfilePath).eq("fileName", fileName);
        if (extendName == null) {
            queryWrapper.eq("isDir", 1);
        } else {
            queryWrapper.eq("extendName", extendName);
        }
        queryWrapper.eq("deleteFlag", 0);
        List<UserFile> userFileList = userFileMapper.selectList(queryWrapper);
        for (UserFile userFile : userFileList) {
            userFile.setFilePath(newfilePath);
            if (userFile.getIsDir() == 0) {
                String repeatFileName = fileDealComp.getRepeatFileName(userFile, userFile.getFilePath());
                userFile.setFileName(repeatFileName);
            }
            userFileMapper.updateById(userFile);
        }

        //移动子目录
        oldfilePath = new GouDongFile(oldfilePath, fileName, true).getPath();
        newfilePath = new GouDongFile(newfilePath, fileName, true).getPath();

        if (StringUtils.isEmpty(extendName)) { //为空说明是目录，则需要移动子目录
            List<UserFile> list = selectUserFileByLikeRightFilePath(oldfilePath, userId);

            for (UserFile newUserFile : list) {
                newUserFile.setFilePath(newUserFile.getFilePath().replaceFirst(oldfilePath, newfilePath));
                if (newUserFile.getIsDir() == 0) {
                    String repeatFileName = fileDealComp.getRepeatFileName(newUserFile, newUserFile.getFilePath());
                    newUserFile.setFileName(repeatFileName);
                }
                userFileMapper.updateById(newUserFile);
            }
        }

    }

    @Override
    public void userFileCopy(String oldfilePath, String newfilePath, String fileName, String extendName, long userId) {

        QueryWrapper<UserFile> queryWrapper = new QueryWrapper<UserFile>()
                .eq("userId", userId)
                .eq("filePath", oldfilePath).eq("fileName", fileName);
        if (extendName == null) {
            queryWrapper.eq("isDir", 1);
        } else {
            queryWrapper.eq("extendName", extendName);
        }
        queryWrapper.eq("deleteFlag", 0);
        List<UserFile> userFileList = userFileMapper.selectList(queryWrapper);
        for (UserFile userFile : userFileList) {
            userFile.setFilePath(newfilePath);
            userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
            if (userFile.getIsDir() == 0) {
                String repeatFileName = fileDealComp.getRepeatFileName(userFile, userFile.getFilePath());
                userFile.setFileName(repeatFileName);
            }
            userFileMapper.insert(userFile);
        }

        oldfilePath = new GouDongFile(oldfilePath, fileName, true).getPath();
        newfilePath = new GouDongFile(newfilePath, fileName, true).getPath();


        if (extendName == null) { //为null说明是目录，则需要移动子目录
            List<UserFile> subUserFileList = userFileMapper.selectUserFileByLikeRightFilePath(oldfilePath, userId);

            for (UserFile userFile : subUserFileList) {
                userFile.setFilePath(userFile.getFilePath().replaceFirst(oldfilePath, newfilePath));
                userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
                if (userFile.getIsDir() == 0) {
                    String repeatFileName = fileDealComp.getRepeatFileName(userFile, userFile.getFilePath());
                    userFile.setFileName(repeatFileName);
                }
                userFileMapper.insert(userFile);
            }
        }

    }

    @Override
    public IPage<FileListVo> getFileByFileType(Integer fileTypeId, Long currentPage, Long pageCount, long userId) {
        Page<FileListVo> page = new Page<>(currentPage, pageCount);

        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        return userFileMapper.selectPageVo(page, userFile, fileTypeId);
    }

    @Override
    public List<UserFile> selectUserFileListByPath(String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public void deleteUserFile(String userFileId, Long sessionUserId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        String uuid = UUID.randomUUID().toString();
        if (userFile.getIsDir() == 1) {
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<UserFile>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, userFileId);
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

            String filePath = new GouDongFile(userFile.getFilePath(), userFile.getFileName(), true).getPath();
            updateFileDeleteStateByFilePath(filePath, uuid, sessionUserId);

        } else {
            UserFile userFileTemp = userFileMapper.selectById(userFileId);
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(1, FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .eq(UserFile::getUserFileId, userFileTemp.getUserFileId());
            userFileMapper.update(null, userFileLambdaUpdateWrapper);
        }

        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(userFileId);
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFileMapper.insert(recoveryFile);


    }

    @Override
    public List<UserFile> selectUserFileByLikeRightFilePath(String filePath, long userId) {
        return userFileMapper.selectUserFileByLikeRightFilePath(filePath, userId);
    }

    private void updateFileDeleteStateByFilePath(String filePath, String deleteBatchNum, Long userId) {
        executor.execute(() -> {
            List<UserFile> fileList = selectUserFileByLikeRightFilePath(filePath, userId);
            for (int i = 0; i < fileList.size(); i++) {
                UserFile userFileTemp = fileList.get(i);
                //标记删除标志
                LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                userFileLambdaUpdateWrapper1.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                        .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                        .set(UserFile::getDeleteBatchNum, deleteBatchNum)
                        .eq(UserFile::getUserFileId, userFileTemp.getUserFileId())
                        .eq(UserFile::getDeleteFlag, 0);
                userFileMapper.update(null, userFileLambdaUpdateWrapper1);

            }
        });
    }


}