package com.hgz.fileoperation.util;

import com.hgz.fileoperation.exception.FileOperationException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CharsetUtils {

    public static byte[] convertTxtCharsetToGBK(byte[] bytes, String extendName) {

        if(Arrays.asList(FileOperationUtils.TXT_FILE).contains(extendName)) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            try {
                String str = new String(bytes, getFileCharsetName(byteArrayInputStream));
                return str.getBytes("GBK");
            } catch (IOException e) {
                throw new FileOperationException(e);
            }
        }
        return bytes;
    }

    public static byte[] convertTxtCharsetToUTF8(byte[] bytes, String extendName) {

        if(Arrays.asList(FileOperationUtils.TXT_FILE).contains(extendName)) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            try {
                String str = new String(bytes, getFileCharsetName(byteArrayInputStream));
                return str.getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new FileOperationException(e);
            }
        }
        return bytes;
    }




    public static String getFileCharsetName(InputStream inputStream) throws IOException {

        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                bis.close();
                // 文件编码为 ANSI
                return charset;
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                // 文件编码为 Unicode
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                // 文件编码为 Unicode big endian
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                // 文件编码为 UTF-8
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    // 单独出现BF以下的，也算是GBK
                    if (0x80 <= read && read <= 0xBF) {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();

                        // 双字节 (0xC0 - 0xDF)
                        if (0x80 <= read && read <= 0xBF){
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        }
                        else {
                            break;
                        }
                        // 也有可能出错，但是几率较小
                    } else if (0xE0 <= read) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return charset;

    }

}
