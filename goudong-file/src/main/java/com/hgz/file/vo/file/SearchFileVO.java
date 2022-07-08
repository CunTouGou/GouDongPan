package com.hgz.file.vo.file;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author CunTouGou
 * @date 2022/5/10 2:34
 */
@Data
public class SearchFileVO {
    private String userFileId;
    private String fileName;
    private String filePath;
    private String extendName;
    private Long fileSize;
    private String fileUrl;
    private Map<String, List<String>> highLight;
    private Integer isDir;
}
