package com.hgz.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hgz.file.model.file.RecoveryFile;
import com.hgz.file.vo.file.RecoveryFileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author CunTouGou
 * @date 2022/4/29 10:23
 */
public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {
    /**
     * 查找回收站文件列表
     * @param userId 用户id
     * @return List<RecoveryFileListVo>
     */
    List<RecoveryFileListVo> selectRecoveryFileList(@Param("userId") Long userId);
}
