package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.service.FilePermissionService;
import com.hgz.file.model.file.FilePermission;
import com.hgz.file.mapper.FilePermissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CunTouGou
 * @date 2022/5/1 23:19
 */

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FilePermissionServiceImpl extends ServiceImpl<FilePermissionMapper, FilePermission> implements FilePermissionService {

}
