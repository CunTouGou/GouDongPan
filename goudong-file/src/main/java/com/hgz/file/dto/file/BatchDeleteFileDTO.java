package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/1/12 14:03
 */

@Data
@Schema(name = "批量删除文件DTO",required = true)
public class BatchDeleteFileDTO {
    @Schema(description="文件集合", required = true)
    private String files;

}
