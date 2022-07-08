package com.hgz.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.vo.file.FileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/19 13:25
 */
public interface UserFileService extends IService<UserFile> {
    /**
     * 查找用户文件的名称和路径
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param userId 用户Id
     * @return 用户文件
     */
    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);

    /**
     * 查找相同的用户文件
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param extendName 文件后缀
     * @param userId 用户Id
     * @return 用户文件
     */
    List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId);

    /**
     * 用户文件列表
     * @param userId 用户Id
     * @param filePath 文件路径
     * @param beginCount 开始数量
     * @param pageCount 每页数量
     * @return 文件列表
     */
    IPage<FileListVo> userFileList(Long userId, String filePath, Long beginCount, Long pageCount);

    /**
     * 根据文件路径修改路径
     * @param oldFilePath 旧路径
     * @param newFilePath 新路径
     * @param fileName 文件名
     * @param extendName 文件后缀
     * @param userId 用户Id
     */
    void updateFilepathByFilepath(String oldFilePath, String newFilePath, String fileName, String extendName, long userId);

    /**
     * 用户文件复制 todo
     * @param oldFilePath 旧路径
     * @param newFilePath 新路径
     * @param fileName 文件名
     * @param extendName 文件后缀
     * @param userId 用户Id
     */
    void userFileCopy(String oldFilePath, String newFilePath, String fileName, String extendName, long userId);

    /**
     * 按文件类型获取文件
     * @param fileTypeId 文件类型Id
     * @param currentPage 当前页
     * @param pageCount 每页数量
     * @param userId 用户Id
     * @return 文件列表
     */
    IPage<FileListVo> getFileByFileType(Integer fileTypeId, Long currentPage, Long pageCount, long userId);

    /**
     * 按路径查找用户文件列表
     * @param filePath 文件路径
     * @param userId 用户id
     * @return 用户文件列表
     */
    List<UserFile> selectUserFileListByPath(String filePath, Long userId);

    /**
     * 根据用户Id查找文件目录树
     * @param userId 用户Id
     * @return 文件目录树
     */
    List<UserFile> selectFilePathTreeByUserId(Long userId);

    /**
     * 删除用户文件
     * @param userFileId 用户文件Id
     * @param sessionUserId 用户Id
     */
    void deleteUserFile(String userFileId, Long sessionUserId);

    /**
     * 根据目录查找用户文件
     * @param filePath 文件路径
     * @param userId 用户Id
     * @return 用户文件
     */
    List<UserFile> selectUserFileByLikeRightFilePath(@Param("filePath") String filePath, @Param("userId") long userId);

}
