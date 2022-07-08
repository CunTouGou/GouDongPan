package com.hgz.file.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.common.exception.GouDongException;
import com.hgz.common.operation.FileOperation;
import com.hgz.common.util.DateUtil;
import com.hgz.file.config.es.FileSearch;
import com.hgz.file.service.FileService;
import com.hgz.file.component.AsyncTaskComp;
import com.hgz.file.component.FileDealComp;
import com.hgz.file.model.file.FileBean;
import com.hgz.file.model.file.Image;
import com.hgz.file.model.file.Music;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.mapper.FileMapper;
import com.hgz.file.mapper.ImageMapper;
import com.hgz.file.mapper.MusicMapper;
import com.hgz.file.mapper.UserFileMapper;
import com.hgz.file.util.GouDongFileUtil;
import com.hgz.file.vo.file.FileDetailVO;
import com.hgz.fileoperation.factory.FileOperationFactory;
import com.hgz.fileoperation.operation.download.Downloader;
import com.hgz.fileoperation.operation.download.domain.DownloadFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author CunTouGou
 * @date 2022/4/29 3:14
 */

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FileServiceImpl extends ServiceImpl<FileMapper, FileBean> implements FileService {
    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileOperationFactory fileoperationFactory;

    @Value("${fileoperation.storage-type}")
    private Integer storageType;

    @Resource
    AsyncTaskComp asyncTaskComp;
    @Resource
    MusicMapper musicMapper;
    @Resource
    ImageMapper imageMapper;
    @Resource
    FileDealComp fileDealComp;

    @Override
    public Long getFilePointCount(String fileId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileId, fileId);
        long count = userFileMapper.selectCount(lambdaQueryWrapper);
        return count;
    }

    @Override
    public void unzipFile(String userFileId, int unzipMode, String filePath) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        File destFile = new File(FileOperationUtils.getStaticPath() + "temp" + File.separator + fileBean.getFileUrl());


        Downloader downloader = fileoperationFactory.getDownloader(fileBean.getStorageType());
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(fileBean.getFileUrl());
        InputStream inputStream = downloader.getInputStream(downloadFile);

        try {
            FileUtils.copyInputStreamToFile(inputStream, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String extendName = userFile.getExtendName();

        String unzipUrl = FileOperationUtils.getTempFile(fileBean.getFileUrl()).getAbsolutePath().replace("." + extendName, "");

        List<String> fileEntryNameList = new ArrayList<>();

        try {
            fileEntryNameList = FileOperation.unzip(destFile, unzipUrl);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解压失败" + e);
            throw new GouDongException(500001, "解压异常：" + e.getMessage());
        }

        if (destFile.exists()) {
            destFile.delete();
        }

        if (!fileEntryNameList.isEmpty() && unzipMode == 1) {
            UserFile gouDongDir = GouDongFileUtil.getGouDongDir(userFile.getUserId(), userFile.getFilePath(), userFile.getFileName());
            userFileMapper.insert(gouDongDir);
        }
        for (int i = 0; i < fileEntryNameList.size(); i++){
            String entryName = fileEntryNameList.get(i);
            asyncTaskComp.saveUnzipFile(userFile, fileBean, unzipMode, entryName, filePath);

        }
    }

    @Override
    public void updateFileDetail(String userFileId, String identifier, long fileSize, long modifyUserId) {
        UserFile userFile = userFileMapper.selectById(userFileId);

        FileBean fileBean = new FileBean();
        fileBean.setIdentifier(identifier);
        fileBean.setFileSize(fileSize);
        fileBean.setModifyTime(DateUtil.getCurrentTime());
        fileBean.setModifyUserId(modifyUserId);
        fileBean.setFileId(userFile.getFileId());
        fileMapper.updateById(fileBean);
    }

    @Override
    public FileDetailVO getFileDetail(String userFileId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        Music music = musicMapper.selectOne(new QueryWrapper<Music>().eq("fileId", userFile.getFileId()));
        Image image = imageMapper.selectOne(new QueryWrapper<Image>().eq("fileId", userFile.getFileId()));

        if ("mp3".equalsIgnoreCase(userFile.getExtendName()) || "flac".equalsIgnoreCase(userFile.getExtendName())) {
            if (music == null) {
                fileDealComp.parseMusicFile(userFile.getExtendName(), fileBean.getStorageType(), fileBean.getFileUrl(), fileBean.getFileId());
                music = musicMapper.selectOne(new QueryWrapper<Music>().eq("fileId", userFile.getFileId()));
            }
        }

        FileDetailVO fileDetailVO = new FileDetailVO();
        BeanUtil.copyProperties(userFile, fileDetailVO);
        BeanUtil.copyProperties(fileBean, fileDetailVO);
        fileDetailVO.setMusic(music);
        fileDetailVO.setImage(image);
        System.out.println(fileDetailVO);
        return fileDetailVO;
    }

    @Override
    public List<FileSearch> selectFileListToElasticSearch() {
        return fileMapper.selectFileListToElasticSearch();
    }

}
