package com.hgz.fileoperation.operation.download.product;

import com.hgz.fileoperation.operation.download.Downloader;
import com.hgz.fileoperation.operation.download.domain.DownloadFile;
import com.hgz.fileoperation.util.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
@Component
public class LocalStorageDownloader extends Downloader {

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        //设置文件路径
        File file = new File(FileOperationUtils.getStaticPath() + downloadFile.getFileUrl());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;

    }
}
