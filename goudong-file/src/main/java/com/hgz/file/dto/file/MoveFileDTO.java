package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author CunTouGou
 * @date 2022/1/12 13:25
 */

@Data
@Schema(name = "移动文件DTO",required = true)
public class MoveFileDTO {

    @Schema(description = "文件路径", required = true)
    private String filePath;

    @Schema(description = "文件名", required = true)
    private String fileName;

    @Schema(description = "旧文件名", required = true)
    private String oldFilePath;
    @Schema(description = "扩展名", required = true)
    private String extendName;

}