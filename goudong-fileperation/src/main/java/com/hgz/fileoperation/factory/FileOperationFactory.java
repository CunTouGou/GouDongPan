package com.hgz.fileoperation.factory;

import com.hgz.fileoperation.autoconfiguration.FileOperationProperties;
import com.hgz.fileoperation.config.AliYunConfig;
import com.hgz.fileoperation.constant.StorageTypeEnum;
import com.hgz.fileoperation.model.ThumbImage;
import com.hgz.fileoperation.operation.copy.Copier;
import com.hgz.fileoperation.operation.copy.product.*;
import com.hgz.fileoperation.operation.delete.Deleter;
import com.hgz.fileoperation.operation.delete.product.*;
import com.hgz.fileoperation.operation.download.Downloader;
import com.hgz.fileoperation.operation.download.product.*;
import com.hgz.fileoperation.operation.preview.Previewer;
import com.hgz.fileoperation.operation.preview.product.*;
import com.hgz.fileoperation.operation.read.Reader;
import com.hgz.fileoperation.operation.read.product.*;
import com.hgz.fileoperation.operation.upload.Uploader;
import com.hgz.fileoperation.operation.upload.product.*;
import com.hgz.fileoperation.operation.write.Writer;
import com.hgz.fileoperation.operation.write.product.*;

import javax.annotation.Resource;

/**
 * @author CunTouGou
 * @date 2022/5/10 23:48
 */

public class FileOperationFactory {
    private String storageType;
    private String localStoragePath;
    private AliYunConfig aliyunConfig;
    private ThumbImage thumbImage;
    @Resource
    private FastDFSCopier fastDFSCopier;
    @Resource
    private FastDFSUploader fastDFSUploader;
    @Resource
    private FastDFSDownloader fastDFSDownloader;
    @Resource
    private  FastDFSDeleter fastDFSDeleter;
    @Resource
    private FastDFSReader fastDFSReader;
    @Resource
    private FastDFSPreviewer fastDFSPreviewer;
    @Resource
    private FastDFSWriter fastDFSWriter;
    @Resource
    private AliyunOSSUploader aliyunOSSUploader;

    public FileOperationFactory() {
    }

    public FileOperationFactory(FileOperationProperties fileoperationProperties) {
        this.storageType = fileoperationProperties.getStorageType();
        this.localStoragePath = fileoperationProperties.getLocalStoragePath();
        this.aliyunConfig = fileoperationProperties.getAliYun();
        this.thumbImage = fileoperationProperties.getThumbImage();

    }

    public Uploader getUploader() {

        int type = Integer.parseInt(storageType);
        Uploader uploader = null;
        if (StorageTypeEnum.LOCAL.getCode() == type) {
            uploader = new LocalStorageUploader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            uploader = aliyunOSSUploader;
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            uploader = fastDFSUploader;
        }
        return uploader;
    }


    public Downloader getDownloader(int storageType) {
        Downloader downloader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            downloader = new LocalStorageDownloader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            downloader = new AliyunOSSDownloader(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            downloader = fastDFSDownloader;
        }
        return downloader;
    }


    public Deleter getDeleter(int storageType) {
        Deleter deleter = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            deleter = new LocalStorageDeleter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            deleter = new AliyunOSSDeleter(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            deleter = fastDFSDeleter;
        }
        return deleter;
    }

    public Reader getReader(int storageType) {
        Reader reader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            reader = new LocalStorageReader();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            reader = new AliyunOSSReader(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            reader = fastDFSReader;
        }
        return reader;
    }

    public Writer getWriter(int storageType) {
        Writer writer = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            writer = new LocalStorageWriter();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            writer = new AliyunOSSWriter(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            writer = fastDFSWriter;
        }
        return writer;
    }

    public Previewer getPreviewer(int storageType) {
        Previewer previewer = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            previewer = new LocalStoragePreviewer(thumbImage);
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
            previewer = new AliyunOSSPreviewer(aliyunConfig, thumbImage);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
            previewer = fastDFSPreviewer;
        }
        return previewer;
    }

    public Copier getCopier() {
        int type = Integer.parseInt(storageType);
        Copier copier = null;
        if (StorageTypeEnum.LOCAL.getCode() == type) {
            copier = new LocalStorageCopier();
        } else if (StorageTypeEnum.ALIYUN_OSS.getCode() == type) {
            copier = new AliyunOSSCopier(aliyunConfig);
        } else if (StorageTypeEnum.FAST_DFS.getCode() == type) {
            copier = fastDFSCopier;
        }
        return copier;
    }
}

