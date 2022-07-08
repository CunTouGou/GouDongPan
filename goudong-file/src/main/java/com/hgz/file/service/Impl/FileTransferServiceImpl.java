package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hgz.common.util.DateUtil;
import com.hgz.common.util.MimeUtils;
import com.hgz.common.util.security.JwtUser;
import com.hgz.common.util.security.SessionUtil;
import com.hgz.file.model.file.*;
import com.hgz.file.service.FileTransferService;
import com.hgz.file.component.FileDealComp;
import com.hgz.file.dto.file.DownloadFileDTO;
import com.hgz.file.dto.file.PreviewDTO;
import com.hgz.file.dto.file.UploadFileDTO;
import com.hgz.file.io.GouDongFile;
import com.hgz.file.mapper.*;
import com.hgz.file.util.GouDongFileUtil;
import com.hgz.file.vo.file.UploadFileVo;
import com.hgz.fileoperation.constant.StorageTypeEnum;
import com.hgz.fileoperation.constant.UploadFileStatusEnum;
import com.hgz.fileoperation.exception.operation.DownloadException;
import com.hgz.fileoperation.exception.operation.UploadException;
import com.hgz.fileoperation.factory.FileOperationFactory;
import com.hgz.fileoperation.operation.delete.Deleter;
import com.hgz.fileoperation.operation.delete.domain.DeleteFile;
import com.hgz.fileoperation.operation.download.Downloader;
import com.hgz.fileoperation.operation.download.domain.DownloadFile;
import com.hgz.fileoperation.operation.preview.Previewer;
import com.hgz.fileoperation.operation.preview.domain.PreviewFile;
import com.hgz.fileoperation.operation.upload.Uploader;
import com.hgz.fileoperation.operation.upload.domain.UploadFile;
import com.hgz.fileoperation.operation.upload.domain.UploadFileResult;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author CunTouGou
 * @date 2022/4/29 20:20
 */

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FileTransferServiceImpl implements FileTransferService {

    @Resource
    FileMapper fileMapper;

    @Resource
    UserFileMapper userFileMapper;

    @Resource
    FileOperationFactory fileoperationFactory;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;
    @Resource
    UploadTaskMapper uploadTaskMapper;
    @Resource
    ImageMapper imageMapper;
    @Resource
    MusicMapper musicMapper;

    @Resource
    PictureFileMapper pictureFileMapper;


    @Override
    public UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDTO) {
        UploadFileVo uploadFileVo = new UploadFileVo();
        JwtUser sessionUserBean = SessionUtil.getSession();
        Map<String, Object> param = new HashMap<>();
        param.put("identifier", uploadFileDTO.getIdentifier());
        List<FileBean> list = fileMapper.selectByMap(param);

        String filePath = uploadFileDTO.getFilePath();
        String relativePath = uploadFileDTO.getRelativePath();
        GouDongFile gouDongFile = null;
        if (relativePath.contains("/")) {
            gouDongFile = new GouDongFile(filePath, relativePath, false);
        } else {
            gouDongFile = new GouDongFile(filePath, uploadFileDTO.getFilename(), false);
        }

        if (list != null && !list.isEmpty()) {
            FileBean file = list.get(0);

            if (relativePath.contains("/")) {
                fileDealComp.restoreParentFilePath(gouDongFile, sessionUserBean.getUserId());
                fileDealComp.deleteRepeatSubDirFile(uploadFileDTO.getFilePath(), sessionUserBean.getUserId());
            }

            UserFile userFile = new UserFile(gouDongFile, sessionUserBean.getUserId(), file.getFileId());
            UserFile param1 = GouDongFileUtil.searchGouDongFileParam(userFile);
            List<UserFile> userFileList = userFileMapper.selectList(new QueryWrapper<>(param1));
            if (userFileList.size() <= 0) {
                userFileMapper.insert(userFile);
                fileDealComp.uploadESByUserFileId(userFile.getUserFileId());
            }

            uploadFileVo.setSkipUpload(true);
        } else {
            uploadFileVo.setSkipUpload(false);

            List<Integer> uploaded = uploadTaskDetailMapper.selectUploadedChunkNumList(uploadFileDTO.getIdentifier());
            if (uploaded != null && !uploaded.isEmpty()) {
                uploadFileVo.setUploaded(uploaded);
            } else {

                LambdaQueryWrapper<UploadTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTask::getIdentifier, uploadFileDTO.getIdentifier());
                List<UploadTask> rslist = uploadTaskMapper.selectList(lambdaQueryWrapper);
                if (rslist == null || rslist.isEmpty()) {
                    UploadTask uploadTask = new UploadTask();
                    uploadTask.setIdentifier(uploadFileDTO.getIdentifier());
                    uploadTask.setUploadTime(DateUtil.getCurrentTime());
                    uploadTask.setUploadStatus(UploadFileStatusEnum.UNCOMPLATE.getCode());
                    uploadTask.setFileName(gouDongFile.getNameNotExtend());
                    uploadTask.setFilePath(gouDongFile.getParent());
                    uploadTask.setExtendName(gouDongFile.getExtendName());
                    uploadTask.setUserId(sessionUserBean.getUserId());
                    uploadTaskMapper.insert(uploadTask);
                }
            }

        }
        return uploadFileVo;
    }

    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {

        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(uploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(uploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(uploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(uploadFileDto.getIdentifier());
        uploadFile.setTotalSize(uploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(uploadFileDto.getCurrentChunkSize());

        Uploader uploader = fileoperationFactory.getUploader();
        if (uploader == null) {
            log.error("上传失败，请检查storageType是否配置正确");
            throw new UploadException("上传失败");
        }
        List<UploadFileResult> uploadFileResultList;
        try {
            uploadFileResultList = uploader.upload(request, uploadFile);
        } catch (Exception e) {
            log.error("上传失败，请检查file-operation连接配置是否正确");
            throw new UploadException("上传失败");
        }
        for (int i = 0; i < uploadFileResultList.size(); i++){
            UploadFileResult uploadFileResult = uploadFileResultList.get(i);
            String relativePath = uploadFileDto.getRelativePath();
            GouDongFile gouDongFile = null;
            if (relativePath.contains("/")) {
                gouDongFile = new GouDongFile(uploadFileDto.getFilePath(), relativePath, false);
            } else {
                gouDongFile = new GouDongFile(uploadFileDto.getFilePath(), uploadFileDto.getFilename(), false);
            }

            if (UploadFileStatusEnum.SUCCESS.equals(uploadFileResult.getStatus())){
                FileBean fileBean = new FileBean(uploadFileResult);
                fileBean.setCreateUserId(userId);
                fileMapper.insert(fileBean);


                UserFile userFile = new UserFile(gouDongFile, userId, fileBean.getFileId());

                if (relativePath.contains("/")) {
                    fileDealComp.restoreParentFilePath(gouDongFile, userId);
                    fileDealComp.deleteRepeatSubDirFile(uploadFileDto.getFilePath(), userId);
                }

                UserFile param = GouDongFileUtil.searchGouDongFileParam(userFile);
                List<UserFile> userFileList = userFileMapper.selectList(new QueryWrapper<>(param));
                if (userFileList.size() > 0) {
                    String fileName = fileDealComp.getRepeatFileName(userFile, userFile.getFilePath());
                    userFile.setFileName(fileName);
                }
                userFileMapper.insert(userFile);

                fileDealComp.uploadESByUserFileId(userFile.getUserFileId());


                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.SUCCESS.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);


                try {
                    if (FileOperationUtils.isImageFile(uploadFileResult.getExtendName())) {
                        BufferedImage src = uploadFileResult.getBufferedImage();
                        Image image = new Image();
                        image.setImageWidth(src.getWidth());
                        image.setImageHeight(src.getHeight());
                        image.setFileId(fileBean.getFileId());
                        imageMapper.insert(image);
                    }
                } catch (Exception e) {
                    log.error("生成图片缩略图失败！", e);
                }

                fileDealComp.parseMusicFile(uploadFileResult.getExtendName(), uploadFileResult.getStorageType().getCode(), uploadFileResult.getFileUrl(), fileBean.getFileId());

            } else if (UploadFileStatusEnum.UNCOMPLATE.equals(uploadFileResult.getStatus())) {
                UploadTaskDetail uploadTaskDetail = new UploadTaskDetail();
                uploadTaskDetail.setFilePath(gouDongFile.getParent());
                uploadTaskDetail.setFilename(gouDongFile.getNameNotExtend());
                uploadTaskDetail.setChunkNumber(uploadFileDto.getChunkNumber());
                uploadTaskDetail.setChunkSize((int)uploadFileDto.getChunkSize());
                uploadTaskDetail.setRelativePath(uploadFileDto.getRelativePath());
                uploadTaskDetail.setTotalChunks(uploadFileDto.getTotalChunks());
                uploadTaskDetail.setTotalSize((int)uploadFileDto.getTotalSize());
                uploadTaskDetail.setIdentifier(uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.insert(uploadTaskDetail);

            } else if (UploadFileStatusEnum.FAIL.equals(uploadFileResult.getStatus())) {
                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.FAIL.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
            }
        }

    }


    private String formatChatset(String str) {
        if (str == null) {
            return "";
        }
        if (StandardCharsets.ISO_8859_1.newEncoder().canEncode(str)) {
            byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1);
            return new String(bytes, Charset.forName("GBK"));
        }
        return str;
    }

    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userFileMapper.selectById(downloadFileDTO.getUserFileId());

        if (userFile.getIsDir() == 0) {

            FileBean fileBean = fileMapper.selectById(userFile.getFileId());
            Downloader downloader = fileoperationFactory.getDownloader(fileBean.getStorageType());
            if (downloader == null) {
                log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                throw new DownloadException("下载失败");
            }
            DownloadFile downloadFile = new DownloadFile();

            downloadFile.setFileUrl(fileBean.getFileUrl());
            httpServletResponse.setContentLengthLong(fileBean.getFileSize());
            downloader.download(httpServletResponse, downloadFile);
        } else {

            List<UserFile> userFileList = userFileMapper.selectUserFileByLikeRightFilePath(userFile.getFilePath() + "/" + userFile.getFileName()
                    , userFile.getUserId());
            List<String> userFileIds = userFileList.stream().map(UserFile::getUserFileId).collect(Collectors.toList());

            downloadUserFileList(httpServletResponse, userFile.getFilePath(), userFile.getFileName(), userFileIds);
        }
    }

    @Override
    public void downloadUserFileList(HttpServletResponse httpServletResponse, String filePath, String fileName, List<String> userFileIds) {
        String staticPath = FileOperationUtils.getStaticPath();
        String tempPath = staticPath + "temp" + File.separator;
        File tempDirFile = new File(tempPath);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }

        FileOutputStream f = null;
        try {
            f = new FileOutputStream(tempPath + fileName + ".zip");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
        ZipOutputStream zos = new ZipOutputStream(csum);
        BufferedOutputStream out = new BufferedOutputStream(zos);

        try {
            for (String userFileId : userFileIds) {
                UserFile userFile1 = userFileMapper.selectById(userFileId);
                if (userFile1.getIsDir() == 0) {
                    FileBean fileBean = fileMapper.selectById(userFile1.getFileId());
                    Downloader downloader = fileoperationFactory.getDownloader(fileBean.getStorageType());
                    if (downloader == null) {
                        log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                        throw new UploadException("下载失败");
                    }
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    InputStream inputStream = downloader.getInputStream(downloadFile);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    try {
                        GouDongFile gouDongFile = new GouDongFile(userFile1.getFilePath().replaceFirst(filePath, ""), userFile1.getFileName() + "." + userFile1.getExtendName(), false);
                        zos.putNextEntry(new ZipEntry(gouDongFile.getPath()));

                        byte[] buffer = new byte[1024];
                        int i = bis.read(buffer);
                        while (i != -1) {
                            out.write(buffer, 0, i);
                            i = bis.read(buffer);
                        }
                    } catch (IOException e) {
                        log.error("" + e);
                        e.printStackTrace();
                    } finally {
                        IOUtils.closeQuietly(bis);
                        try {
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    GouDongFile gouDongFile = new GouDongFile(userFile1.getFilePath(), userFile1.getFileName(), true);
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(gouDongFile.getPath() + GouDongFile.separator));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }

        } catch (Exception e) {
            log.error("压缩过程中出现异常:"+ e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String zipPath = "";
        try {
            Downloader downloader = fileoperationFactory.getDownloader(StorageTypeEnum.LOCAL.getCode());
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl("temp" + File.separator + fileName + ".zip");
            File tempFile = new File(FileOperationUtils.getStaticPath() + downloadFile.getFileUrl());
            httpServletResponse.setContentLengthLong(tempFile.length());
            downloader.download(httpServletResponse, downloadFile);
            zipPath = FileOperationUtils.getStaticPath() + "temp" + File.separator + fileName + ".zip";
        } catch (Exception e) {
            //org.apache.catalina.connector.ClientAbortException: java.io.IOException: Connection reset by peer
            if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("下传zip文件出现异常：{}", e.getMessage());
            }

        } finally {
            File file = new File(zipPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    public void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        UserFile userFile = userFileMapper.selectById(previewDTO.getUserFileId());
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        Previewer previewer = fileoperationFactory.getPreviewer(fileBean.getStorageType());
        if (previewer == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", fileBean.getStorageType());
            throw new UploadException("预览失败");
        }
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileUrl(fileBean.getFileUrl());
        try {
            if ("true".equals(previewDTO.getIsMin())) {
                previewer.imageThumbnailPreview(httpServletResponse, previewFile);
            } else {
                previewer.imageOriginalPreview(httpServletResponse, previewFile);
            }
        } catch (Exception e){
            //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
            if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("预览文件出现异常：{}", e.getMessage());
            }

        }

    }

    @Override
    public void previewPictureFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        byte[] bytesUrl = Base64.getDecoder().decode(previewDTO.getUrl());
        PictureFile pictureFile = new PictureFile();
        pictureFile.setFileUrl(new String(bytesUrl));
        pictureFile = pictureFileMapper.selectOne(new QueryWrapper<>(pictureFile));
        Previewer previewer = fileoperationFactory.getPreviewer(pictureFile.getStorageType());
        if (previewer == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", pictureFile.getStorageType());
            throw new UploadException("预览失败");
        }
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileUrl(pictureFile.getFileUrl());
//        previewFile.setFileSize(pictureFile.getFileSize());
        try {

            String mime= MimeUtils.getMime(pictureFile.getExtendName());
            httpServletResponse.setHeader("Content-Type", mime);

            String fileName = pictureFile.getFileName() + "." + pictureFile.getExtendName();
            try {
                fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名

            previewer.imageOriginalPreview(httpServletResponse, previewFile);
        } catch (Exception e){
            //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
            if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("预览文件出现异常：{}", e.getMessage());
            }

        }
    }

    @Override
    public void deleteFile(FileBean fileBean) {
        Deleter deleter = null;

        deleter = fileoperationFactory.getDeleter(fileBean.getStorageType());
        DeleteFile deleteFile = new DeleteFile();
        deleteFile.setFileUrl(fileBean.getFileUrl());
        deleter.delete(deleteFile);
    }



    @Override
    public Long selectStorageSizeByUserId(Long userId){
        return userFileMapper.selectStorageSizeByUserId(userId);
    }
}
