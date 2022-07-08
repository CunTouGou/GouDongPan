package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/1/12 11:22
 */
@Data
@Schema(name = "删除回收文件DTO",required = true)
public class DeleteRecoveryFileDTO {
    @Schema(description = "回收文件id")
    private Long recoveryFileId;

}
