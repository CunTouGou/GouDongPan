package com.hgz.file.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/30 22:55
 */

@Data
@Schema(description="分享文件VO")
public class ShareFileVO {
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description = "提取编码")
    private String extractionCode;
}
