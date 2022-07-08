package com.hgz.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hgz.file.model.file.Share;
import com.hgz.file.vo.share.ShareListVO;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 23:12
 */

public interface ShareMapper  extends BaseMapper<Share> {

    /**
     * 查找分享列表
     * @param shareFilePath 分享文件路径
     * @param shareBatchNum 分享批次号
     * @param beginCount 开始条数
     * @param pageCount 每页条数
     * @param userId 用户id
     * @return 分享列表
     */
    List<ShareListVO> selectShareList(String shareFilePath, String shareBatchNum, Long beginCount, Long pageCount, Long userId);

    /**
     * 查找分享列表总数
     * @param shareFilePath 分享文件路径
     * @param shareBatchNum 分享批次号
     * @param userId 用户id
     * @return 分享列表总数
     */
    int selectShareListTotalCount(String shareFilePath,String shareBatchNum, Long userId);
}
