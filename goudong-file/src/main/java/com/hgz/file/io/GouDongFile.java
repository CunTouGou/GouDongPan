package com.hgz.file.io;

import com.hgz.common.exception.GouDongException;
import com.hgz.fileoperation.util.FileOperationUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author CunTouGou
 * @date 2022/4/21 12:08
 */
public class GouDongFile {

    private final String path;
    public static final String separator = "/";
    private boolean isDirectory;

    public GouDongFile(String pathname, boolean isDirectory) {
        if (StringUtils.isEmpty(pathname)) {
            throw new GouDongException("文件名格式错误，路径:" + pathname);
        }
        this.path = formatPath(pathname);
        this.isDirectory = isDirectory;
    }

    public GouDongFile(String parent, String child, boolean isDirectory) {
        if (StringUtils.isEmpty(child)) {
            throw new GouDongException("文件名格式错误，父:" + parent +", 子:" + child);
        }
        if (parent != null) {
            String parentPath = separator.equals(formatPath(parent)) ? "" : formatPath(parent);
            String childPath = formatPath(child);
            if (childPath.startsWith(separator)) {
                childPath = childPath.replaceFirst(separator, "");
            }
            this.path = parentPath + separator + childPath;
        } else {
            this.path = formatPath(child);
        }
        this.isDirectory = isDirectory;
    }

    public static String formatPath(String path) {
        path = FileOperationUtils.pathSplitFormat(path);
        if (separator.equals(path)) {
            return path;
        }
        if (path.endsWith(separator)) {
            int length = path.length();
            return path.substring(0, length - 1);
        }

        return path;
    }

    public String getParent() {
        if (separator.equals(this.path)) {
            return null;
        }
        if (!this.path.contains(separator)) {
            return null;
        }
        int index = path.lastIndexOf(separator);
        if (index == 0) {
            return separator;
        }
        return path.substring(0, index);
    }

    public GouDongFile getParentFile() {
        String parentPath = this.getParent();
        return new GouDongFile(parentPath, true);
    }

    public String getName() {
        int index = path.lastIndexOf(separator);
        if (!path.contains(separator)) {
            return path;
        }
        return path.substring(index + 1);
    }

    public String getExtendName() {
        return FilenameUtils.getExtension(getName());
    }

    public String getNameNotExtend() {
        return FilenameUtils.removeExtension(getName());
    }

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
       return isDirectory;
    }

    public boolean isFile() {
        return !isDirectory;
    }

    
}
