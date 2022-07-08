package com.hgz.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "批量下载文件DTO",required = true)
public class BatchDownloadFileDTO {
    private String files;

    @Schema(description="文件集合", required = true)
    private String userFileIds;

}
