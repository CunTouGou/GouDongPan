package com.hgz.file.model.file;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hgz.common.util.DateUtil;
import com.hgz.file.io.GouDongFile;
import lombok.Data;

import javax.persistence.*;
/**
 * @author CunTouGou
 * @date 2022/4/8 23:25
 */

@Data
@Table(name = "userfile", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = { "userId", "filePath", "fileName", "extendName", "deleteFlag"})}
)
@Entity
@TableName("userfile")
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition = "varchar(20)")
    private String userFileId;

    @Column(columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;

    @Column(columnDefinition="varchar(20) comment '文件id'")
    private String fileId;

    @Column(columnDefinition="varchar(100) comment '文件名'")
    private String fileName;

    @Column(columnDefinition="varchar(500) comment '文件路径'")
    private String filePath;

    @Column(columnDefinition="varchar(100) comment '扩展名'")
    private String extendName;

    @Column(columnDefinition="int(1) comment '是否是目录(0-否,1-是)'")
    private Integer isDir;

    @Column(columnDefinition="varchar(25) comment '上传时间'")
    private String uploadTime;

    @Column(columnDefinition="int(11) comment '删除标识(0-未删除，1-已删除)'")
    private Integer deleteFlag;

    @Column(columnDefinition="varchar(25) comment '删除时间'")
    private String deleteTime;

    @Column(columnDefinition = "varchar(50) comment '删除批次号'")
    private String deleteBatchNum;

    public UserFile() {};
    public UserFile(GouDongFile gouDongFile, long userId, String fileId) {
        this.userFileId = IdUtil.getSnowflakeNextIdStr();
        this.userId = userId;
        this.fileId = fileId;
        this.filePath = gouDongFile.getParent();
        this.fileName = gouDongFile.getNameNotExtend();
        this.extendName = gouDongFile.getExtendName();
        this.isDir = gouDongFile.isDirectory() ? 1 : 0;
        this.uploadTime = DateUtil.getCurrentTime();
        this.deleteFlag = 0;
    }

}
