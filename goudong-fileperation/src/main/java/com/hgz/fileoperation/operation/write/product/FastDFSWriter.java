package com.hgz.fileoperation.operation.write.product;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.hgz.fileoperation.operation.write.Writer;
import com.hgz.fileoperation.operation.write.domain.WriteFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

@Component
@Slf4j
public class FastDFSWriter extends Writer {
    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;
    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        defaultAppendFileStorageClient.modifyFile("group1", writeFile.getFileUrl(), inputStream,
                writeFile.getFileSize(), 0);
    }
}
