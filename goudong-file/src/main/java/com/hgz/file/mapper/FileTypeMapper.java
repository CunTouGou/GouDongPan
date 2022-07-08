package com.hgz.file.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hgz.file.model.file.FileType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/5/1 22:36
 */

public interface FileTypeMapper extends BaseMapper<FileType> {
    /**
     * 根据文件类型查找后缀名
     * @param fileTypeId 文件类型id
     * @return 后缀名
     */
    List<String> selectExtendNameByFileType(@Param("fileTypeId") Integer fileTypeId);

}
