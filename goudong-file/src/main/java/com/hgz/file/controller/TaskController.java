package com.hgz.file.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.hgz.file.config.es.FileSearch;
import com.hgz.file.service.Impl.FileServiceImpl;
import com.hgz.file.service.Impl.UserFileServiceImpl;
import com.hgz.file.service.ShareFileService;
import com.hgz.file.component.FileDealComp;
import com.hgz.file.model.file.ShareFile;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.io.GouDongFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务
 *
 * @author CunTouGou
 * @date 2022/4/22 18:01
 */

@Slf4j
@Controller
public class TaskController {

    @Resource
    private UserFileServiceImpl userFileServiceImpl;

    @Resource
    private FileServiceImpl fileService;

    @Resource
    private FileDealComp fileDealComp;
    @Resource
    private ShareFileService shareFileService;
    @Autowired
    private ElasticsearchClient elasticsearchClient;


    /**
     * 二十四小时更新一次
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateElasticSearch() {
        List<FileSearch> fileList = fileService.selectFileListToElasticSearch();
        for (int i = 0; i < fileList.size(); i++) {
            try {
                GouDongFile operationFile = new GouDongFile(fileList.get(i).getFilePath(), fileList.get(i).getFileName(), fileList.get(i).getIsDir() == 1);
                fileDealComp.restoreParentFilePath(operationFile, fileList.get(i).getUserId());
                if (i % 1000 == 0 || i == fileList.size() - 1) {
                    log.info("目录健康检查进度：" + (i + 1) + "/" + fileList.size());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        fileList = fileService.selectFileListToElasticSearch();
        for (FileSearch  file : fileList) {
            fileDealComp.uploadESByUserFileId(file.getUserFileId());
        }

    }

    /**
     * 只执行一次
     */
    @Scheduled(fixedRate = Long.MAX_VALUE)
    public void updateFilePath() {
        List<UserFile> list = userFileServiceImpl.list();
        for (UserFile userFile : list) {
            try {
                String path = GouDongFile.formatPath(userFile.getFilePath());
                if (!userFile.getFilePath().equals(path)) {
                    userFile.setFilePath(path);
                    userFileServiceImpl.updateById(userFile);
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * 只执行一次
     */
    @Scheduled(fixedRate = Long.MAX_VALUE)
    public void updateShareFilePath() {
        List<ShareFile> list = shareFileService.list();
        for (ShareFile shareFile : list) {
            try {
                String path = GouDongFile.formatPath(shareFile.getShareFilePath());
                shareFile.setShareFilePath(path);
                shareFileService.updateById(shareFile);
            } catch (Exception e) {
                //ignore
            }
        }
    }
}
