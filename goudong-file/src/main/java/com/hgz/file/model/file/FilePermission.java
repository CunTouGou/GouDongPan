package com.hgz.file.model.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author CunTouGou
 * @date 2022/4/30 16:11
 */
@Data
@Table(name = "filepermission")
@Entity
@TableName("filepermission")
public class FilePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    public Long filePermissionId;

    @Column(columnDefinition="varchar(20)  comment '共享文件id'")
    public String commonFileId;

    @Column(columnDefinition="bigint(20) comment '用户id'")
    public Long userId;

    @Column(columnDefinition="int(2) comment '文件权限码'")
    public Integer filePermissionCode;

}
