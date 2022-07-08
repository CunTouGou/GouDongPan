package com.hgz.fileoperation.operation.write;

import com.hgz.fileoperation.operation.write.domain.WriteFile;

import java.io.InputStream;

public abstract class Writer {
    public abstract void write(InputStream inputStream, WriteFile writeFile);
}
