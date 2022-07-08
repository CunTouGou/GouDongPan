package com.hgz.file.dto.recoveryfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/15 12:46
 */

@Data
@Schema(name = "回收文件列表DTO",required = true)
public class RecoveryFileListDTO {
    @Schema(description = "当前页码")
    private Long currentPage;
    @Schema(description = "一页显示数量")
    private Long pageCount;
}
