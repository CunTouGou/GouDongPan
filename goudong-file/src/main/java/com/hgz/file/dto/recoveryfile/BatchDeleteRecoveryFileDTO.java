package com.hgz.file.dto.recoveryfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/13 10:23
 */

@Data
@Schema(name = "批量删除回收文件DTO",required = true)
public class BatchDeleteRecoveryFileDTO {
    @Schema(description="恢复文件集合")
    private String recoveryFileIds;
}
