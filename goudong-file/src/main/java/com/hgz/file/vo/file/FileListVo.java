package com.hgz.file.vo.file;

import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/27 11:54
 */

@Data
public class FileListVo {
    private String fileId;

    private String timeStampName;

    private String fileUrl;

    private Long fileSize;

    private Integer storageType;

    private Integer pointCount;

    private String identifier;

    private String userFileId;

    private Long userId;

    private String fileName;

    private String filePath;

    private String extendName;

    private Integer isDir;

    private String createTime;

    private String uploadTime;

    private Integer deleteFlag;

    private String deleteTime;

    private String deleteBatchNum;

    private Integer imageWidth;

    private Integer imageHeight;

}