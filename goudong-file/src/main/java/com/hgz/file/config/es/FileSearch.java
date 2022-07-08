package com.hgz.file.config.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Elasticsearch字段配置
 *
 * @author CunTouGou
 * @date 2022/4/22 18:07
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileSearch {
    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 用户文件Id
     */
    private String userFileId;

    /**
     * 文件Id
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 内容
     */
    private String content;

    /**
     * 文件的URL
     */
    private String fileUrl;

    /**
     * 文件的大小
     */
    private Long fileSize;

    /**
     * 存储方式
     */
    private Integer storageType;

    /**
     * 文件的md5
     */
    private String identifier;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 文件的路径
     */
    private String filePath;

    /**
     * 文件后缀名
     */
    private String extendName;

    /**
     * 是否目录
     */
    private Integer isDir;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String uploadTime;

    /**
     * 删除的标识符
     */
    private Integer deleteFlag;

    /**
     * 删除时间
     */
    private String deleteTime;

    /**
     * 删除批次号
     */
    private String deleteBatchNum;
}
