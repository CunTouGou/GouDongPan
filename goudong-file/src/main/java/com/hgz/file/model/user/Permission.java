package com.hgz.file.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author CunTouGou
 * @date 2022/4/19 7:11
 * 权限实体类
 */

@Data
@Table(name = "permission")
@Entity
@TableName("permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long permissionId;

    @Column(columnDefinition="bigint(20) comment '父编号'")
    private Long parentId;

    @Column(columnDefinition="varchar(30) comment '权限名称'")
    private String permissionName;

    @Column(columnDefinition="int(2) comment '资源类型'")
    private Integer resourceType;

    @Column(columnDefinition="varchar(30) comment '权限标识码'")
    private String permissionCode;

    @Column(columnDefinition="int(2) comment '次序'")
    private Integer orderNum;

    @Column(columnDefinition="varchar(30) comment '创建时间'")
    private String createTime;

    @Column(columnDefinition="bigint(20) comment '创建用户id'")
    private Long createUserId;

    @Column(columnDefinition="varchar(30) comment '修改时间'")
    private String modifyTime;

    @Column(columnDefinition="bigint(20) comment '修改用户id'")
    private Long modifyUserId;



}
