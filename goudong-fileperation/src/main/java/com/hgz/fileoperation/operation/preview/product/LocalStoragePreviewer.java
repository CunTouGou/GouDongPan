package com.hgz.fileoperation.operation.preview.product;

import com.hgz.fileoperation.model.ThumbImage;
import com.hgz.fileoperation.operation.preview.Previewer;
import com.hgz.fileoperation.operation.preview.domain.PreviewFile;
import com.hgz.fileoperation.util.FileOperationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LocalStoragePreviewer extends Previewer {

    public LocalStoragePreviewer(){

    }
    public LocalStoragePreviewer(ThumbImage thumbImage) {
        setThumbImage(thumbImage);
    }

    @Override
    protected InputStream getInputStream(PreviewFile previewFile) {
        //设置文件路径
        File file = FileOperationUtils.getLocalSaveFile(previewFile.getFileUrl());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return inputStream;

    }

}
