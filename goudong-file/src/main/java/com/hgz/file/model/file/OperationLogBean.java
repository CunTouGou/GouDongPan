package com.hgz.file.model.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author CunTouGou
 * @date 2022/4/30 16:13
 */

@Data
@Table(name = "operationlog")
@Entity
@TableName("operationlog")
public class OperationLogBean {
    /**
     * 操作日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long operationLogId;

    /**
     * 用户id
     */
    @Column(columnDefinition="bigint(20) comment '用户id'")
    private Long userId;

    /**
     * 操作
     */
    @Column(columnDefinition="varchar(50) comment '操作'")
    private String operation;

    /**
     * 操作对象
     */
    private String operationObj;

    /**
     * 终端IP
     */
    @Column(columnDefinition="varchar(20) comment '终端ip地址'")
    private String terminal;

    /**
     * 操作结果
     */
    @Column(columnDefinition="varchar(20) comment '操作结果'")
    private String result;

    /**
     * 操作详情
     */
    @Column(columnDefinition="varchar(100) comment '操作详情'")
    private String detail;

    /**
     * 操作源
     */
    private String source;

    /**
     * 时间
     */
    @Column(columnDefinition="varchar(25) comment '操作时间'")
    private String time;

    /**
     * 日志级别 1-正常 2-警告 3-错误
     */
    private Integer logLevel;

    private String requestURI;

    private String requestMethod;

}
