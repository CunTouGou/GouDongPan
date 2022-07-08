package com.hgz.file.component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hgz.file.model.file.FileBean;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.io.GouDongFile;
import com.hgz.file.mapper.FileMapper;
import com.hgz.file.mapper.UserFileMapper;
import com.hgz.file.service.FileTransferService;
import com.hgz.file.service.RecoveryFileService;
import com.hgz.file.service.UserFileService;
import com.hgz.fileoperation.factory.FileOperationFactory;
import com.hgz.fileoperation.operation.copy.domain.CopyFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * 异步任务业务类
 *
 * @author CunTouGou
 * @date 2022/4/21 13:01
 */
@Slf4j
@Component
@Async("asyncTaskExecutor")
public class AsyncTaskComp {
    @Resource
    private UserFileService userFileService;

    @Resource
    private RecoveryFileService recoveryFileService;
    @Resource
    private FileTransferService fileTransferService;
    @Resource
    private FileOperationFactory fileOperationFactory;
    @Resource
    private UserFileMapper userFileMapper;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private FileDealComp fileDealComp;

    @Value("${file-operation.storage-type}")
    private Integer storageType;

    /**
     * 获取文件分块数
     *
     * @param fileId 文件id
     * @return 文件点计数
     */
    public Long getFilePointCount(String fileId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileId, fileId);
        long count = userFileMapper.selectCount(lambdaQueryWrapper);
        return count;
    }

    /**
     * deleteUserFile
     *
     * @param userFileId 用户文件ID
     * @return Future
     */
    public Future<String> deleteUserFile(String userFileId) {
        UserFile userFile = userFileService.getById(userFileId);
        if (userFile.getIsDir() == 1) {
            LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, userFile.getDeleteBatchNum());
            List<UserFile> list = userFileService.list(userFileLambdaQueryWrapper);
            recoveryFileService.deleteUserFileByDeleteBatchNum(userFile.getDeleteBatchNum());
            for (UserFile userFileItem : list) {

                Long filePointCount = getFilePointCount(userFileItem.getFileId());

                if (filePointCount != null && filePointCount == 0 && userFileItem.getIsDir() == 0) {
                    FileBean fileBean = fileMapper.selectById(userFileItem.getFileId());
                    try {
                        fileTransferService.deleteFile(fileBean);
                        fileMapper.deleteById(fileBean.getFileId());
                    } catch (Exception e) {
                        log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
                    }
                }
            }
        } else {
            // 通过批量编号删除用户文件
            recoveryFileService.deleteUserFileByDeleteBatchNum(userFile.getDeleteBatchNum());
            Long filePointCount = getFilePointCount(userFile.getFileId());

            if (filePointCount != null && filePointCount == 0 && userFile.getIsDir() == 0) {
                FileBean fileBean = fileMapper.selectById(userFile.getFileId());
                try {
                    fileTransferService.deleteFile(fileBean);
                    fileMapper.deleteById(fileBean.getFileId());
                } catch (Exception e) {
                    log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
                }
            }
        }

        return new AsyncResult<String>("deleteUserFile");
    }


    /**
     * 检查Elasticsearch中的用户文件
     *
     * @param userFileId 用户文件ID
     * @return Future
     */
    public Future<String> checkESUserFileId(String userFileId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        if (userFile == null) {
            fileDealComp.deleteESByUserFileId(userFileId);
        }
        return new AsyncResult<String>("checkUserFileId");
    }


    /**
     * 压缩文件保存
     *
     * @param userFile  用户文件
     * @param fileBean  文件信息
     * @param unzipMode 解压模式
     * @param entryName 解压后文件名
     * @param filePath  解压后文件路径
     * @return Future
     */
    public Future<String> saveUnzipFile(UserFile userFile, FileBean fileBean, int unzipMode, String entryName, String filePath) {
        String unzipUrl = FileOperationUtils.getTempFile(fileBean.getFileUrl()).getAbsolutePath().replace("." + userFile.getExtendName(), "");
        String totalFileUrl = unzipUrl + entryName;
        File currentFile = new File(totalFileUrl);

        String fileId = null;
        if (!currentFile.isDirectory()) {

            FileInputStream fis = null;
            String md5Str = UUID.randomUUID().toString();
            try {
                fis = new FileInputStream(currentFile);
                md5Str = DigestUtils.md5Hex(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fis);
            }

            FileInputStream fileInputStream = null;
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("identifier", md5Str);
                List<FileBean> list = fileMapper.selectByMap(param);

                //文件已存在
                if (list != null && !list.isEmpty()) {
                    fileId = list.get(0).getFileId();
                } else { //文件不存在
                    fileInputStream = new FileInputStream(currentFile);
                    CopyFile createFile = new CopyFile();
                    createFile.setExtendName(FilenameUtils.getExtension(totalFileUrl));
                    String saveFileUrl = fileOperationFactory.getCopier().copy(fileInputStream, createFile);

                    FileBean tempFileBean = new FileBean(saveFileUrl, currentFile.length(), storageType, md5Str, userFile.getUserId());
                    ;
                    fileMapper.insert(tempFileBean);
                    fileId = tempFileBean.getFileId();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fileInputStream);
                System.gc();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentFile.delete();
            }

        }
        GouDongFile gouDongFile = null;
        if (unzipMode == 0) {
            gouDongFile = new GouDongFile(userFile.getFilePath(), entryName, currentFile.isDirectory());
        } else if (unzipMode == 1) {
            gouDongFile = new GouDongFile(userFile.getFilePath() + "/" + userFile.getFileName(), entryName, currentFile.isDirectory());
        } else if (unzipMode == 2) {
            gouDongFile = new GouDongFile(filePath, entryName, currentFile.isDirectory());
        }

        UserFile saveUserFile = new UserFile(gouDongFile, userFile.getUserId(), fileId);
        String fileName = fileDealComp.getRepeatFileName(saveUserFile, saveUserFile.getFilePath());

        if (saveUserFile.getIsDir() == 1 && !fileName.equals(saveUserFile.getFileName())) {
            //如果是目录，而且重复，什么也不做
        } else {
            saveUserFile.setFileName(fileName);
            userFileMapper.insert(saveUserFile);
        }

        return new AsyncResult<String>("saveUnzipFile");
    }
}