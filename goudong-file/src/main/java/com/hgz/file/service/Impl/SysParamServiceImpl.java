package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.service.SysParamService;
import com.hgz.file.model.file.SysParam;
import com.hgz.file.mapper.SysParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/9 0:14
 */

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParam> implements SysParamService {

    @Resource
    SysParamMapper sysParamMapper;

    @Override
    public String getValue(String key) {
        SysParam sysParam = new SysParam();
        sysParam.setSysParamKey(key);
        List<SysParam> list = sysParamMapper.selectList(new QueryWrapper<>(sysParam));
        if (list != null && !list.isEmpty()) {
            return list.get(0).getSysParamValue();
        }
        return null;
    }
}