package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.config.es.FileSearch;
import com.hgz.file.model.file.FileBean;
import com.hgz.file.vo.file.FileDetailVO;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 3:08
 */

public interface FileService extends IService<FileBean> {

    /**
     * 获取文件数量
     * @param fileId 文件id
     * @return 文件数量
     */
    Long getFilePointCount(String fileId);

    /**
     * 解压文件
     * @param userFileId 用户文件id
     * @param unzipMode 解压模式
     * @param filePath 解压文件路径
     */
    void unzipFile(String userFileId, int unzipMode, String filePath);

    /**
     * 修改文件的详细信息
     * @param userFileId 用户文件id
     * @param identifier 文件的标识符
     * @param fileSize 文件的大小
     * @param modifyUserId 修改人id
     */
    void updateFileDetail(String userFileId, String identifier, long fileSize, long modifyUserId);

    /**
     * 获取文件详细信息
     * @param userFileId 用户文件id
     * @return 文件详细信息
     */
    FileDetailVO getFileDetail(String userFileId);

    /**
     * 获取文件列表 提供 ElasticSearch
     * @return 文件列表
     */
    List<FileSearch> selectFileListToElasticSearch();

}