package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.dto.sharefile.ShareListDTO;
import com.hgz.file.model.file.Share;
import com.hgz.file.vo.share.ShareListVO;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 21:45
 */

public interface ShareService extends IService<Share> {

    /**
     * 查询分享列表
     * @param shareListDTO 分享列表DTO
     * @param userId 用户id
     * @return 分享列表
     */
    List<ShareListVO> selectShareList(ShareListDTO shareListDTO, Long userId);

    /**
     * 查找分享列表总数
     * @param shareListDTO 分享列表DTO
     * @param userId 用户id
     * @return 分享列表总数
     */
    int selectShareListTotalCount(ShareListDTO shareListDTO, Long userId);
}