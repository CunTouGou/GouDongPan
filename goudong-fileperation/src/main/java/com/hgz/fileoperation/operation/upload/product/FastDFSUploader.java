package com.hgz.fileoperation.operation.upload.product;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.exception.FdfsServerException;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.hgz.fileoperation.constant.StorageTypeEnum;
import com.hgz.fileoperation.constant.UploadFileStatusEnum;
import com.hgz.fileoperation.exception.operation.UploadException;
import com.hgz.fileoperation.operation.upload.Uploader;
import com.hgz.fileoperation.operation.upload.domain.UploadFile;
import com.hgz.fileoperation.operation.upload.domain.UploadFileResult;
import com.hgz.fileoperation.operation.upload.request.GouDongMultipartFile;
import com.hgz.fileoperation.util.RedisUtil;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class FastDFSUploader extends Uploader {

    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    RedisUtil redisUtil;

    @Override
    public void doUploadFileChunk(GouDongMultipartFile gouDongMultipartFile, UploadFile uploadFile) throws IOException {
        StorePath storePath = null;

        if (uploadFile.getChunkNumber() <= 1) {
            log.info("上传第一块");

            storePath = defaultAppendFileStorageClient.uploadAppenderFile("group1", gouDongMultipartFile.getUploadInputStream(),
                    gouDongMultipartFile.getSize(), gouDongMultipartFile.getExtendName());
            // 记录第一个分片上传的大小
            redisUtil.set("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size", gouDongMultipartFile.getSize(), 1000 * 60 * 60);

            log.info("第一块上传完成");
            if (storePath == null) {
                redisUtil.set("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number", uploadFile.getChunkNumber(), 1000 * 60 * 60);

                log.info("获取远程文件路径出错");
                throw new UploadException("获取远程文件路径出错");
            }

            redisUtil.set("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path", storePath.getPath(), 1000 * 60 * 60);

            log.info("上传文件 result = {}", storePath.getPath());
        } else {
            log.info("正在上传第{}块：" , uploadFile.getChunkNumber());

            String path = redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");

            if (path == null) {
                log.error("无法获取已上传服务器文件地址");
                throw new UploadException("无法获取已上传服务器文件地址");
            }

            String uploadedSizeStr = redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size");
            Long alreadySize = Long.parseLong(uploadedSizeStr);

            // 追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
            defaultAppendFileStorageClient.modifyFile("group1", path, gouDongMultipartFile.getUploadInputStream(),
                    gouDongMultipartFile.getSize(), alreadySize);
            // 记录分片上传的大小
            redisUtil.set("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size", alreadySize + gouDongMultipartFile.getSize(), 1000 * 60 * 60);

        }
    }

    @Override
    protected UploadFileResult organizationalResults(GouDongMultipartFile gouDongMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();

        String path = redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
        uploadFileResult.setFileUrl(path);
        uploadFileResult.setFileName(gouDongMultipartFile.getFileName());
        uploadFileResult.setExtendName(gouDongMultipartFile.getExtendName());
        uploadFileResult.setFileSize(uploadFile.getTotalSize());
        if (uploadFile.getTotalChunks() == 1) {
            uploadFileResult.setFileSize(gouDongMultipartFile.getSize());
        }
        uploadFileResult.setStorageType(StorageTypeEnum.FAST_DFS);
        uploadFileResult.setIdentifier(uploadFile.getIdentifier());

        if (uploadFile.getChunkNumber() == uploadFile.getTotalChunks()) {
            log.info("分片上传完成");
            redisUtil.deleteKey("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":current_upload_chunk_number");
            redisUtil.deleteKey("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
            redisUtil.deleteKey("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":uploaded_size");
            if (FileOperationUtils.isImageFile(uploadFileResult.getExtendName())) {
                String group = "group1";
                String path1 = uploadFileResult.getFileUrl().substring(uploadFileResult.getFileUrl().indexOf("/") + 1);
                DownloadByteArray downloadByteArray = new DownloadByteArray();
                byte[] bytes = defaultAppendFileStorageClient.downloadFile(group, path1, downloadByteArray);
                InputStream is = new ByteArrayInputStream(bytes);

                BufferedImage src = null;
                try {
                    src = ImageIO.read(is);
                    uploadFileResult.setBufferedImage(src);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }

            }
            uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
        } else {
            uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);
        }
        return uploadFileResult;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
        String path = redisUtil.getObject("GouDongUploader:Identifier:" + uploadFile.getIdentifier() + ":storage_path");
        try {
            fastFileStorageClient.deleteFile(path.replace("M00", "group1"));
        } catch (FdfsServerException e) {
            log.error(e.getMessage());
        }
    }
}
