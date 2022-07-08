package com.hgz.fileoperation.operation.delete;

import com.hgz.fileoperation.operation.delete.domain.DeleteFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@Slf4j
public abstract class Deleter {
    public abstract void delete(DeleteFile deleteFile);

    protected void deleteCacheFile(DeleteFile deleteFile) {
        if (FileOperationUtils.isImageFile(FilenameUtils.getExtension(deleteFile.getFileUrl()))) {
            File cacheFile = FileOperationUtils.getCacheFile(deleteFile.getFileUrl());
            if (cacheFile.exists()) {
                boolean result = cacheFile.delete();
                if (!result) {
                    log.error("删除本地缓存文件失败！");
                }
            }
        }
    }
}
