package com.hgz.file.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author CunTouGou
 * @date 2022/4/19 10:35
 */
@Data
@Table(name = "role_permission")
@Entity
@TableName("role_permission")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Column(columnDefinition="bigint(20) comment '角色id'")
    private Long roleId;

    @Column(columnDefinition="bigint(20) comment '权限id'")
    private Long permissionId;
}
