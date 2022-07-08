package com.hgz.file.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hgz.common.constant.RegexConstant;
import com.hgz.file.model.user.UserBean;
import com.hgz.file.mapper.UserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户处理组件
 *
 * @author CunTouGou
 * @date 2022/4/21 14:07
 */
@Component
public class UserDealComp {
    @Resource
    UserMapper userMapper;


    /**
     * 检测用户名是否存在
     *
     * @param userBean 用户信息
     */
    public Boolean isUserNameExit(UserBean userBean) {
        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getUsername, userBean.getUsername());
        List<UserBean> list = userMapper.selectList(lambdaQueryWrapper);
        return list != null && !list.isEmpty();
    }

    /**
     * 检测手机号是否存在
     *
     * @param userBean 用户信息
     * @return 是否存在
     */
    public Boolean isPhoneExit(UserBean userBean) {

        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getTelephone, userBean.getTelephone());
        List<UserBean> list = userMapper.selectList(lambdaQueryWrapper);
        return list != null && !list.isEmpty();

    }

    /**
     * 电话格式正确吗?
     *
     * @param phone 电话号码
     * @return 是否正确
     */
    public Boolean isPhoneFormatRight(String phone) {
        return Pattern.matches(RegexConstant.PASSWORD_REGEX, phone);
    }
}
