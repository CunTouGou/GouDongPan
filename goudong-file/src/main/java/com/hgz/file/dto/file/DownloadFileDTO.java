package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/1/12 19:33
 */
@Data
@Schema(name = "下载文件DTO",required = true)
public class DownloadFileDTO {
    private String userFileId;
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="提取码")
    private String extractionCode;
}
