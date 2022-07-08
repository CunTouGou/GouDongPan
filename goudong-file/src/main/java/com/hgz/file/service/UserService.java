package com.hgz.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.common.result.RestResult;
import com.hgz.file.model.user.Role;
import com.hgz.file.model.user.UserBean;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/9 0:13
 */
public interface UserService extends IService<UserBean> {

    /**
     * 通过token获取用户 ID
     * @param token 用户 token
     * @return 返回结果
     */
    Long getUserIdByToken(String token);


    /**
     * 用户注册
     * @param userBean 用户信息
     * @return 返回结果
     */
    RestResult<String> registerUser(UserBean userBean);

    /**
     * 通过电话获取用户信息
     * @param telephone 用户电话
     * @return 返回结果
     */
    UserBean findUserInfoByTelephone(String telephone);

    /**
     * 按用户 ID 查找角色列表
     * @param userId 用户 ID
     * @return 返回结果
     */
    List<Role> selectRoleListByUserId(long userId);

    /**
     * 通过电话获取盐
     * @param telephone 用户电话
     * @return 返回结果
     */
    String getSaltByTelephone(String telephone);

    /**
     * 通过电话和密码查找用户
     * @param telephone 用户电话
     * @param password 用户密码
     * @return 返回结果
     */
    UserBean selectUserByTelephoneAndPassword(String username, String password);
}
