package com.hgz.file.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author CunTouGou
 * @date 2022/4/18 20:23
 */
@Data
@Table(name = "userlogininfo")
@Entity
@TableName("userlogininfo")
public class UserLoginInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long userLoginId;
    @Column(columnDefinition = "varchar(30) comment '用户登录日期'")
    private String userloginDate;
    @Column(columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;
}
