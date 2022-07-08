package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/1/12 12:23
 */

@Data
@Schema(name = "批量移动文件DTO",required = true)
public class BatchMoveFileDTO {
    @Schema(description="文件集合", required = true)
    private String files;
    @Schema(description="目的文件路径", required = true)
    private String filePath;


}
