package com.hgz.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hgz.file.model.user.Role;
import com.hgz.file.model.user.UserBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/8 23:37
 */
public interface UserMapper extends BaseMapper<UserBean> {

    /**
     * 新增用户
     * @param userBean 用户
     * @return
     */
    int insertUser(UserBean userBean);

    /**
     * 新增用户角色
     * @param userId 用户id
     * @param roleId 角色id
     * @return 新增结果
     */
    int insertUserRole(long userId, long roleId);

    /**
     * 按用户 ID 查找角色列表
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<Role>  selectRoleListByUserId(@Param("userId") long userId);

    /**
     * 通过电话查找盐
     * @param telephone 电话号码
     * @return 盐
     */
    String selectSaltByTelephone(@Param("telephone") String telephone);

    /**
     * 通过电话和密码查找用户
     * @param telephone 电话号码
     * @param password 密码
     * @return 用户
     */
    UserBean selectUserByTelephoneAndPassword(@Param("telephone") String telephone, @Param("password") String password);
}
