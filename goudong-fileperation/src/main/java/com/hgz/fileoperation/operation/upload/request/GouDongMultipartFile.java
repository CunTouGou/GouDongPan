package com.hgz.fileoperation.operation.upload.request;

import com.hgz.fileoperation.util.FileOperationUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class GouDongMultipartFile {

    MultipartFile multipartFile = null;

    private GouDongMultipartFile() {
    }

    public GouDongMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getFileName() {
        String originalName = getMultipartFile().getOriginalFilename();
        assert originalName != null;
        if (!originalName.contains(".")) {
            return originalName;
        }
        return originalName.substring(0, originalName.lastIndexOf("."));
    }

    public String getExtendName() {
        String originalName = getMultipartFile().getOriginalFilename();
        return FilenameUtils.getExtension(originalName);
    }

    public String getFileUrl() {
        String uuid = UUID.randomUUID().toString();
        return FileOperationUtils.getUploadFileUrl(uuid, getExtendName());
    }

    public String getFileUrl(String identify) {
        return FileOperationUtils.getUploadFileUrl(identify, getExtendName());
    }

    public InputStream getUploadInputStream() throws IOException {
        return getMultipartFile().getInputStream();
    }

    public byte[] getUploadBytes() throws IOException {
        return getMultipartFile().getBytes();
    }

    public long getSize() {
        return getMultipartFile().getSize();
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

}
