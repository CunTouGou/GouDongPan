package com.hgz.fileoperation.operation.copy.product;

import com.hgz.fileoperation.exception.operation.CopyException;
import com.hgz.fileoperation.operation.copy.Copier;
import com.hgz.fileoperation.operation.copy.domain.CopyFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
public class LocalStorageCopier extends Copier {
    @Override
    public String copy(InputStream inputStream, CopyFile copyFile) {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = FileOperationUtils.getUploadFileUrl(uuid, copyFile.getExtendName());
        File saveFile = new File(FileOperationUtils.getStaticPath() + fileUrl);
        try {
            FileUtils.copyInputStreamToFile(inputStream, saveFile);
        } catch (IOException e) {
            throw new CopyException("创建文件出现异常", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return fileUrl;
    }
}
