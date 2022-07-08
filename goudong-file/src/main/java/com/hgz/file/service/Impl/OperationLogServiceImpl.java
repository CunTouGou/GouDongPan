package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.service.OperationLogService;
import com.hgz.file.model.file.OperationLogBean;
import com.hgz.file.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/5/1 22:53
 */

@Service
@Transactional(rollbackFor=Exception.class)
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogBean> implements OperationLogService {
    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public IPage<OperationLogBean> selectOperationLogPage(Integer current, Integer size) {
        IPage<OperationLogBean> page = new Page<>(current, size);
        IPage<OperationLogBean> list = operationLogMapper.selectPage(page, null);
        return list;
    }

    @Override
    public List<OperationLogBean> selectOperationLog() {
        List<OperationLogBean> result = operationLogMapper.selectList(null);
        return result;
    }

    @Override
    public void insertOperationLog(OperationLogBean operationlogBean) {
        operationLogMapper.insert(operationlogBean);

    }
}
