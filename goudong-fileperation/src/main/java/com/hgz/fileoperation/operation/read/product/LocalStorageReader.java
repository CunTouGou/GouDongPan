package com.hgz.fileoperation.operation.read.product;

import com.hgz.fileoperation.exception.operation.ReadException;
import com.hgz.fileoperation.operation.read.Reader;
import com.hgz.fileoperation.operation.read.domain.ReadFile;
import com.hgz.fileoperation.util.ReadFileUtils;
import com.hgz.fileoperation.util.FileOperationUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class LocalStorageReader extends Reader {
    @Override
    public String read(ReadFile readFile) {

        String fileContent;
        try {
            String extendName = FilenameUtils.getExtension(readFile.getFileUrl());
            FileInputStream fileInputStream = new FileInputStream(FileOperationUtils.getStaticPath() + readFile.getFileUrl());
            fileContent = ReadFileUtils.getContentByInputStream(extendName, fileInputStream);
        } catch (IOException e) {
            throw new ReadException("文件读取出现异常", e);
        }
        return fileContent;
    }
}
