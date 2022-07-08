package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/1/12 13:31
 */

@Data
@Schema(name = "删除文件DTO",required = true)
public class DeleteFileDTO {
    @Schema(description = "用户文件id", required = true)
    private String userFileId;


}
