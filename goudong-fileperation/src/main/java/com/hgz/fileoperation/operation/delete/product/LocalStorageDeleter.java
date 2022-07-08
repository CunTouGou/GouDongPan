package com.hgz.fileoperation.operation.delete.product;

import com.hgz.fileoperation.exception.operation.DeleteException;
import com.hgz.fileoperation.operation.delete.Deleter;
import com.hgz.fileoperation.operation.delete.domain.DeleteFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File localSaveFile = FileOperationUtils.getLocalSaveFile(deleteFile.getFileUrl());
        if (localSaveFile.exists()) {
            boolean result = localSaveFile.delete();
            if (!result) {
                new DeleteException("删除本地文件失败");
            }
        }

        deleteCacheFile(deleteFile);
    }
}
