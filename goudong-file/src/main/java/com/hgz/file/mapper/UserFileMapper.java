package com.hgz.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.vo.file.FileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/8 23:37
 */

public interface UserFileMapper extends BaseMapper<UserFile> {

    /**
     * 根据目录查找用户文件列表
     * @param filePath 目录
     * @param userId 用户id
     * @return 文件列表
     */
    List<UserFile> selectUserFileByLikeRightFilePath(@Param("filePath") String filePath, @Param("userId") long userId);

    /**
     * 查找文件列表
     * @param page 分页
     * @param userFile 文件
     * @param fileTypeId 文件类型id
     * @return 文件列表
     */
    IPage<FileListVo> selectPageVo(Page<?> page, @Param("userFile") UserFile userFile, @Param("fileTypeId") Integer fileTypeId);

    /**
     * 根据用户查找存储大小
     * @param userId 用户id
     * @return 存储大小
     */
    Long selectStorageSizeByUserId(@Param("userId") Long userId);
}
