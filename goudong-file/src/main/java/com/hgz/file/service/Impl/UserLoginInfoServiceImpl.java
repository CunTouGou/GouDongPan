package com.hgz.file.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgz.file.service.UserLoginInfoService;
import com.hgz.file.model.user.UserLoginInfo;
import com.hgz.file.mapper.UserLoginInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CunTouGou
 * @date 2022/4/18 20:24
 */

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserLoginInfoServiceImpl extends ServiceImpl<UserLoginInfoMapper, UserLoginInfo> implements UserLoginInfoService {


}