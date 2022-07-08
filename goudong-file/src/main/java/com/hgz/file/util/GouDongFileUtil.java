package com.hgz.file.util;

import cn.hutool.core.util.IdUtil;
import com.hgz.common.util.DateUtil;
import com.hgz.file.model.file.UserFile;
import com.hgz.file.io.GouDongFile;

/**
 * @author CunTouGou
 * @date 2022/5/1 20:36
 */

public class GouDongFileUtil {
    public static UserFile getGouDongDir(long userId, String filePath, String fileName) {
        UserFile userFile = new UserFile();
        userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
        userFile.setUserId(userId);
        userFile.setFileId(null);
        userFile.setFileName(fileName);
        userFile.setFilePath(GouDongFile.formatPath(filePath));
        userFile.setExtendName(null);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

    public static UserFile getGouDongFile(long userId, String fileId, String filePath, String fileName, String extendName) {
        UserFile userFile = new UserFile();
        userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
        userFile.setUserId(userId);
        userFile.setFileId(fileId);
        userFile.setFileName(fileName);
        userFile.setFilePath(GouDongFile.formatPath(filePath));
        userFile.setExtendName(extendName);
        userFile.setIsDir(0);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

    public static UserFile searchGouDongFileParam(UserFile userFile) {
        UserFile param = new UserFile();
        param.setFilePath(GouDongFile.formatPath(userFile.getFilePath()));
        param.setFileName(userFile.getFileName());
        param.setExtendName(userFile.getExtendName());
        param.setDeleteFlag(0);
        param.setUserId(userFile.getUserId());
        param.setIsDir(0);
        return param;
    }

    public static String formatLikePath(String filePath) {
        String newFilePath = filePath.replace("'", "\\'");
        newFilePath = newFilePath.replace("%", "\\%");
        newFilePath = newFilePath.replace("_", "\\_");
        return newFilePath;
    }

}
