package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/12 10:46
 */

@Data
@Schema(name = "预览文件DTO",required = true)
public class PreviewDTO {
    private String userFileId;
    @Schema(description="批次号")

    private String shareBatchNum;
    @Schema(description="提取码")

    private String extractionCode;

    private String isMin;

    private String url;

    private String token;
}
