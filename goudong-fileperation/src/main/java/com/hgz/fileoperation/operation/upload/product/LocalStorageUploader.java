package com.hgz.fileoperation.operation.upload.product;

import com.hgz.fileoperation.operation.upload.domain.UploadFile;
import com.hgz.fileoperation.constant.StorageTypeEnum;
import com.hgz.fileoperation.constant.UploadFileStatusEnum;
import com.hgz.fileoperation.exception.operation.UploadException;
import com.hgz.fileoperation.operation.upload.Uploader;
import com.hgz.fileoperation.operation.upload.domain.UploadFile;
import com.hgz.fileoperation.operation.upload.domain.UploadFileResult;
import com.hgz.fileoperation.operation.upload.request.GouDongMultipartFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocalStorageUploader extends Uploader {

    /**
     * URL映射文件
     */
    public static Map<String, String> FILE_URL_MAP = new HashMap<>();

    @Override
    protected UploadFileResult doUploadFlow(GouDongMultipartFile gouDongMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        try {
            String fileUrl = FileOperationUtils.getUploadFileUrl(uploadFile.getIdentifier(), gouDongMultipartFile.getExtendName());
            if (StringUtils.isNotEmpty(FILE_URL_MAP.get(uploadFile.getIdentifier()))) {
                fileUrl = FILE_URL_MAP.get(uploadFile.getIdentifier());
            } else {
                FILE_URL_MAP.put(uploadFile.getIdentifier(), fileUrl);
            }
            String tempFileUrl = fileUrl + "_tmp";
            String confFileUrl = fileUrl.replace("." + gouDongMultipartFile.getExtendName(), ".conf");

            File file = new File(FileOperationUtils.getStaticPath() + fileUrl);
            File tempFile = new File(FileOperationUtils.getStaticPath() + tempFileUrl);
            File confFile = new File(FileOperationUtils.getStaticPath() + confFileUrl);

            //第一步 打开将要写入的文件
            RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
            //第二步 打开通道
            try {
                FileChannel fileChannel = raf.getChannel();
                //第三步 计算偏移量
                long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
                //第四步 获取分片数据
                byte[] fileData = gouDongMultipartFile.getUploadBytes();
                //第五步 写入数据
                fileChannel.position(position);
                fileChannel.write(ByteBuffer.wrap(fileData));
                fileChannel.force(true);
                fileChannel.close();
            } finally {
                IOUtils.closeQuietly(raf);
            }

            //判断是否完成文件的传输并进行校验与重命名
            boolean isComplete = checkUploadStatus(uploadFile, confFile);
            uploadFileResult.setFileUrl(fileUrl);
            uploadFileResult.setFileName(gouDongMultipartFile.getFileName());
            uploadFileResult.setExtendName(gouDongMultipartFile.getExtendName());
            uploadFileResult.setFileSize(uploadFile.getTotalSize());
            uploadFileResult.setStorageType(StorageTypeEnum.LOCAL);

            if (uploadFile.getTotalChunks() == 1) {
                uploadFileResult.setFileSize(gouDongMultipartFile.getSize());
            }
            uploadFileResult.setIdentifier(uploadFile.getIdentifier());
            if (isComplete) {
                tempFile.renameTo(file);
                FILE_URL_MAP.remove(uploadFile.getIdentifier());

                if (FileOperationUtils.isImageFile(uploadFileResult.getExtendName())) {

                    InputStream is = null;
                    try {
                        is = new FileInputStream(FileOperationUtils.getLocalSaveFile(fileUrl));

                        BufferedImage src = ImageIO.read(is);
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
        } catch (IOException e) {
            throw new UploadException(e);
        }


        return uploadFileResult;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
        String fileUrl = FILE_URL_MAP.get(uploadFile.getIdentifier());
        String tempFileUrl = fileUrl + "_tmp";
        String confFileUrl = fileUrl.replace("." + FilenameUtils.getExtension(fileUrl), ".conf");
        File tempFile = new File(tempFileUrl);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        File confFile = new File(confFileUrl);
        if (confFile.exists()) {
            confFile.delete();
        }
    }

    @Override
    protected void doUploadFileChunk(GouDongMultipartFile gouDongMultipartFile, UploadFile uploadFile) throws IOException {

    }

    @Override
    protected UploadFileResult organizationalResults(GouDongMultipartFile gouDongMultipartFile, UploadFile uploadFile) {
        return null;
    }

}
