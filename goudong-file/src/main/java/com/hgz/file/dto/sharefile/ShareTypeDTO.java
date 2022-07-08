package com.hgz.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/13 11:16
 */

@Data
@Schema(name = "分享类型DTO",required = true)
public class ShareTypeDTO {

    @Schema(description="批次号")
    private String shareBatchNum;
}