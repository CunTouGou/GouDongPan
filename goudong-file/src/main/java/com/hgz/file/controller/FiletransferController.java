package com.hgz.file.controller;

import com.alibaba.fastjson.JSON;
import com.hgz.common.anno.MyLog;
import com.hgz.common.result.RestResult;
import com.hgz.common.util.DateUtil;
import com.hgz.common.util.MimeUtils;
import com.hgz.common.util.security.JwtUser;
import com.hgz.common.util.security.SessionUtil;
import com.hgz.file.service.FileService;
import com.hgz.file.service.FileTransferService;
import com.hgz.file.service.Impl.StorageServiceImpl;
import com.hgz.file.service.UserFileService;
import com.hgz.file.component.FileDealComp;
import com.hgz.file.model.file.FileBean;
import com.hgz.file.model.file.StorageBean;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.dto.file.BatchDownloadFileDTO;
import com.hgz.file.dto.file.DownloadFileDTO;
import com.hgz.file.dto.file.PreviewDTO;
import com.hgz.file.dto.file.UploadFileDTO;
import com.hgz.file.io.GouDongFile;
import com.hgz.file.vo.file.UploadFileVo;
import com.hgz.fileoperation.factory.FileOperationFactory;
import com.hgz.fileoperation.operation.download.Downloader;
import com.hgz.fileoperation.operation.download.domain.DownloadFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件传输接口
 *
 * @author CunTouGou
 * @date 2022/4/26 12:30
 */

@Slf4j
@Tag(name = "fileTransfer", description = "该接口为文件传输接口，主要用来做文件的上传、下载和预览")
@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

    @Resource
    private FileTransferService filetransferService;

    @Resource
    private FileService fileService;
    @Resource
    private UserFileService userFileService;
    @Resource
    private FileDealComp fileDealComp;
    @Resource
    private StorageServiceImpl storageServiceImpl;
    @Resource
    private FileOperationFactory fileoperationFactory;


    public static final String CURRENT_MODULE = "文件传输接口";

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"fileTransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.GET)
    @MyLog(operation = "极速上传", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFileSpeed(UploadFileDTO uploadFileDto) {

        JwtUser sessionUserBean = SessionUtil.getSession();

        boolean isCheckSuccess = storageServiceImpl.checkStorage(sessionUserBean.getUserId(), uploadFileDto.getTotalSize());
        if (!isCheckSuccess) {
            return RestResult.fail().message("存储空间不足");
        }
        UploadFileVo uploadFileVo = filetransferService.uploadFileSpeed(uploadFileDto);
        return RestResult.success().data(uploadFileVo);

    }

    @Operation(summary = "上传文件", description = "真正的上传文件接口", tags = {"fileTransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @MyLog(operation = "上传文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto) {

        JwtUser sessionUserBean = SessionUtil.getSession();

        filetransferService.uploadFile(request, uploadFileDto, sessionUserBean.getUserId());

        UploadFileVo uploadFileVo = new UploadFileVo();
        return RestResult.success().data(uploadFileVo);

    }


    @Operation(summary = "下载文件", description = "下载文件接口", tags = {"fileTransfer"})
    @MyLog(operation = "下载文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        Cookie[] cookieArr = httpServletRequest.getCookies();
        String token = "";
        if (cookieArr != null) {
            for (Cookie cookie : cookieArr) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        boolean authResult = fileDealComp.checkAuthDownloadAndPreview(downloadFileDTO.getShareBatchNum(),
                downloadFileDTO.getExtractionCode(),
                token,
                downloadFileDTO.getUserFileId());
        if (!authResult) {
            log.error("没有权限下载！！！");
            return;
        }
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        UserFile userFile = userFileService.getById(downloadFileDTO.getUserFileId());
        String fileName = "";
        if (userFile.getIsDir() == 1) {
            fileName = userFile.getFileName() + ".zip";
        } else {
            fileName = userFile.getFileName() + "." + userFile.getExtendName();

        }
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名

        filetransferService.downloadFile(httpServletResponse, downloadFileDTO);
    }

    @Operation(summary = "批量下载文件", description = "批量下载文件", tags = {"fileTransfer"})
    @RequestMapping(value = "/batchDownloadFile", method = RequestMethod.GET)
    @MyLog(operation = "批量下载文件", module = CURRENT_MODULE)
    @ResponseBody
    public void batchDownloadFile(HttpServletResponse httpServletResponse, BatchDownloadFileDTO batchDownloadFileDTO) {
        String files = batchDownloadFileDTO.getUserFileIds();
        String[] userFileIdStrs = files.split(",");
        List<String> userFileIds = new ArrayList<>();
        for (String userFileId : userFileIdStrs) {
            UserFile userFile = userFileService.getById(userFileId);
            if (userFile.getIsDir() == 0) {
                userFileIds.add(userFileId);
            } else {
                GouDongFile gouDongFile = new GouDongFile(userFile.getFilePath(), userFile.getFileName(), true);
                List<UserFile> userFileList = userFileService.selectUserFileByLikeRightFilePath(gouDongFile.getPath(), userFile.getUserId());
                List<String> userFileIds1 = userFileList.stream().map(UserFile::getUserFileId).collect(Collectors.toList());
                userFileIds.add(userFile.getUserFileId());
                userFileIds.addAll(userFileIds1);
            }

        }
        UserFile userFile = userFileService.getById(userFileIdStrs[0]);
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        Date date = new Date();
        String fileName = String.valueOf(date.getTime());
        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName + ".zip");// 设置文件名
        filetransferService.downloadUserFileList(httpServletResponse, userFile.getFilePath(), fileName, userFileIds);
    }

    @Operation(summary = "预览文件", description = "用于文件预览", tags = {"fileTransfer"})
    @GetMapping("/preview")
    public void preview(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, PreviewDTO previewDTO) throws IOException {

        String token = "";
        if (StringUtils.isNotEmpty(previewDTO.getToken())) {
            token = previewDTO.getToken();
        } else {
            Cookie[] cookieArr = httpServletRequest.getCookies();
            if (cookieArr != null) {
                for (Cookie cookie : cookieArr) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
            }
        }

        UserFile userFile = userFileService.getById(previewDTO.getUserFileId());
        boolean authResult = fileDealComp.checkAuthDownloadAndPreview(previewDTO.getShareBatchNum(),
                previewDTO.getExtractionCode(),
                token,
                previewDTO.getUserFileId());

        if (!authResult) {
            log.error("没有权限预览！！！");
            return;
        }

        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名

        FileBean fileBean = fileService.getById(userFile.getFileId());
        if ((FileOperationUtils.isVideoFile(userFile.getExtendName()) || "mp3".equalsIgnoreCase(userFile.getExtendName()) || "flac".equalsIgnoreCase(userFile.getExtendName()))
                && !"true".equals(previewDTO.getIsMin())) {
            Downloader downloader = fileoperationFactory.getDownloader(fileBean.getStorageType());
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl(fileBean.getFileUrl());
            InputStream inputStream = downloader.getInputStream(downloadFile);

            String mime = MimeUtils.getMime(userFile.getExtendName());
            httpServletResponse.setHeader("Content-Type", mime);

            //获取从那个字节开始读取文件
            String rangeString = httpServletRequest.getHeader("Range");
            int range = 0;
            if (StringUtils.isNotBlank(rangeString)) {
                range = Integer.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
            }
            //获取响应的输出流
            OutputStream outputStream = httpServletResponse.getOutputStream();
            //返回码需要为206，代表只处理了部分请求，响应了部分数据
            httpServletResponse.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            // 每次请求只返回1MB的视频流
            byte[] bytes = new byte[1024 * 1024 * 5];
            inputStream.skip(range);
            int len = IOUtils.read(inputStream, bytes);
            //设置此次相应返回的数据长度
            httpServletResponse.setContentLength(len);
            httpServletResponse.setHeader("Accept-Ranges", "bytes");
            //设置此次相应返回的数据范围
            httpServletResponse.setHeader("Content-Range", "bytes " + range + "-" + (fileBean.getFileSize() - 1) + "/" + fileBean.getFileSize());
            // 将这1MB的视频流响应给客户端
            outputStream.write(bytes, 0, len);
            outputStream.close();
        } else {
            filetransferService.previewFile(httpServletResponse, previewDTO);
        }

    }

    @Operation(summary = "获取存储信息", description = "获取存储信息", tags = {"fileTransfer"})
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<StorageBean> getStorage() {

        JwtUser sessionUserBean = SessionUtil.getSession();
        StorageBean storageBean = new StorageBean();

        storageBean.setUserId(sessionUserBean.getUserId());


        Long storageSize = filetransferService.selectStorageSizeByUserId(sessionUserBean.getUserId());
        StorageBean storage = new StorageBean();
        storage.setUserId(sessionUserBean.getUserId());
        storage.setStorageSize(storageSize);
        Long totalStorageSize = storageServiceImpl.getTotalStorageSize(sessionUserBean.getUserId());
        storage.setTotalStorageSize(totalStorageSize);
        return RestResult.success().data(storage);

    }


}
