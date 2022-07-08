package com.hgz.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hgz.file.model.file.ShareFile;
import com.hgz.file.vo.share.ShareFileListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 23:22
 */
public interface ShareFileMapper extends BaseMapper<ShareFile> {

    /**
     * 批量新增分享文件
     * @param shareFiles 分享文件
     */
    void batchInsertShareFile(List<ShareFile> shareFiles);

    /**
     * 查找分享文件列表
     * @param shareBatchNum 分享批次号
     * @param filePath 文件路径
     * @return 分享文件列表
     */
    List<ShareFileListVO> selectShareFileList(@Param("shareBatchNum") String shareBatchNum, @Param("shareFilePath") String filePath);
}
