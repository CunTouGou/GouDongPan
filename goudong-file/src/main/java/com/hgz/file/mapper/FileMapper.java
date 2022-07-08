package com.hgz.file.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hgz.file.config.es.FileSearch;
import com.hgz.file.model.file.FileBean;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/8 23:35
 */

public interface FileMapper extends BaseMapper<FileBean> {
    /**
     * 批量插入文件
     * @param fileBeanList 文件列表
     */
    void batchInsertFile(List<FileBean> fileBeanList);

    /**
     * 更新文件列表
     * @return 更新结果
     */
    List<FileSearch> selectFileListToElasticSearch();

}
