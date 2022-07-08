package com.hgz.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.model.file.OperationLogBean;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/5/1 22:49
 */

public interface OperationLogService extends IService<OperationLogBean> {

    /**
     * 查找操作文件
     * @param current 当前页
     * @param size 每页数量
     * @return 操作日志
     */
    IPage<OperationLogBean> selectOperationLogPage(Integer current, Integer size);

    /**
     * 查找操作日记
     * @return 操作日记
     */
    List<OperationLogBean> selectOperationLog();

    /**
     * 新增操作日志
     * @param operationlogBean 操作日志
     */
    void insertOperationLog(OperationLogBean operationlogBean);
}
