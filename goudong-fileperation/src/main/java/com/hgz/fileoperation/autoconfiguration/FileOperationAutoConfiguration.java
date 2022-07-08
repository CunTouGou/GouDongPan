package com.hgz.fileoperation.autoconfiguration;

import com.github.tobato.fastdfs.FdfsClientConfig;
import com.hgz.fileoperation.factory.FileOperationFactory;
import com.hgz.fileoperation.operation.copy.product.FastDFSCopier;
import com.hgz.fileoperation.operation.delete.product.FastDFSDeleter;
import com.hgz.fileoperation.operation.download.product.FastDFSDownloader;
import com.hgz.fileoperation.operation.preview.product.FastDFSPreviewer;
import com.hgz.fileoperation.operation.read.product.FastDFSReader;
import com.hgz.fileoperation.operation.upload.product.AliyunOSSUploader;
import com.hgz.fileoperation.operation.upload.product.FastDFSUploader;
import com.hgz.fileoperation.operation.write.product.FastDFSWriter;
import com.hgz.fileoperation.util.FileOperationUtils;
import com.hgz.fileoperation.util.RedisUtil;
import com.hgz.fileoperation.util.concurrent.locks.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * @author CunTouGou
 * @date 2022/5/11 5:10
 */

@Slf4j
@Configuration
@EnableConfigurationProperties({FileOperationProperties.class})
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FileOperationAutoConfiguration {
    @Autowired
    private FileOperationProperties fileOperationProperties;

    @Bean
    public FileOperationFactory fileOperationFactory() {
        FileOperationUtils.LOCAL_STORAGE_PATH = fileOperationProperties.getLocalStoragePath();
        String bucketName = fileOperationProperties.getBucketName();
        if (StringUtils.isNotEmpty(bucketName)) {
            FileOperationUtils.ROOT_PATH = fileOperationProperties.getBucketName();
        } else {
            FileOperationUtils.ROOT_PATH = "upload";
        }
        return new FileOperationFactory(fileOperationProperties);
    }

    @Bean
    public FastDFSCopier fastDFSCreater() {
        return new FastDFSCopier();
    }

    @Bean
    public FastDFSUploader fastDFSUploader() {
        return new FastDFSUploader();
    }

    @Bean
    public FastDFSDownloader fastDFSDownloader() {
        return new FastDFSDownloader();
    }

    @Bean
    public FastDFSDeleter fastDFSDeleter() {
        return new FastDFSDeleter();
    }

    @Bean
    public FastDFSReader fastDFSReader() {
        return new FastDFSReader();
    }

    @Bean
    public FastDFSWriter fastDFSWriter() {
        return new FastDFSWriter();
    }

    @Bean
    public FastDFSPreviewer fastDFSPreviewer() {
        return new FastDFSPreviewer(fileOperationProperties.getThumbImage());
    }

    @Bean
    public AliyunOSSUploader aliyunOSSUploader() {
        return new AliyunOSSUploader(fileOperationProperties.getAliYun());
    }

    @Bean
    public RedisLock redisLock() {
        return new RedisLock();
    }

    @Bean
    public RedisUtil redisUtil() {
        return new RedisUtil();
    }

}
