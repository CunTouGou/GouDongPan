package com.hgz.file.service;

import com.hgz.file.dto.file.DownloadFileDTO;
import com.hgz.file.dto.file.PreviewDTO;
import com.hgz.file.dto.file.UploadFileDTO;
import com.hgz.file.model.file.FileBean;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.vo.file.UploadFileVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author CunTouGou
 * @date 2022/4/29 20:14
 */

public interface FileTransferService {
    /**
     * 极速上传
     * @param uploadFileDTO 上传文件信息
     * @return 上传文件信息
     */
    UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDTO);

    /**
     * 上传文件
     * @param request 请求
     * @param uploadFileDto 上传文件信息
     * @param userId 用户id
     */
    void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId);

    /**
     * 文件下载
     * @param httpServletResponse 响应
     * @param downloadFileDTO 下载文件信息
     */
    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO);

    /**
     * 下载用户文件列表
     * @param httpServletResponse 响应
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @param userFileList 用户文件列表
     */
    void downloadUserFileList(HttpServletResponse httpServletResponse, String filePath, String fileName, List<String> userFileList);

    /**
     * 预览文件
     * @param httpServletResponse 响应
     * @param previewDTO 预览文件信息
     */
    void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);

    /**
     * 预览图片文件
     * @param httpServletResponse 响应
     * @param previewDTO 预览文件信息
     */
    void previewPictureFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);

    /**
     * 文件删除
     * @param fileBean 删除文件信息
     */
    void deleteFile(FileBean fileBean);

    /**
     * 根据用户Id查找存储大小
     * @param userId 用户Id
     * @return 存储大小
     */
    Long selectStorageSizeByUserId(Long userId);
}
