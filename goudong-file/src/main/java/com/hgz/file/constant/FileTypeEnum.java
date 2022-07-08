package com.hgz.file.constant;

/**
 * 文件分类枚举
 * @author CunTouGou
 * @date 2022/4/24 03:01
 */
public enum FileTypeEnum {
    /**
     * 全部
     */
    TOTAL(0, "全部"),

    /**
     * 图片
     */
    PICTURE(1, "图片"),

    /**
     * 文档
     */
    DOCUMENT(2, "文档"),

    /**
     * 视频
     */
    VIDEO(3, "视频"),

    /**
     * 音频
     */
    MUSIC(4, "音乐"),

    /**
     * 其他
     */
    OTHER(5, "其他"),

    /**
     * 分享
     */
    SHARE(6, "分享"),

    /**
     * 回收站
     */
    RECYCLE(7, "回收站");


    private int type;
    private String desc;
    FileTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
