package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/2/8 19:23
 */
@Data
@Schema(name = "修改文件DTO",required = true)
public class UpdateFileDTO {
    @Schema(description = "用户文件id")
    private String userFileId;
    @Schema(description = "文件内容")
    private String fileContent;
}
