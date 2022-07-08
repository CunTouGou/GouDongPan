package com.hgz.file.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author CunTouGou
 * @date 2022/4/30 16:19
 */

@Data
@Table(name = "user_role")
@Entity
@TableName("user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long userRoleId;

    private Long userId;

    private Long roleId;
}
