package com.hgz.file.dto.file;

import com.hgz.common.constant.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author CunTouGou
 * @date 2022/1/12 11:25
 */

@Data
@Schema(name = "创建文件DTO",required = true)
public class CreateFileDTO {
    @Schema(description="文件名", required=true)
    @NotBlank(message = "文件名不能为空")
    @Pattern(regexp = RegexConstant.FILE_NAME_REGEX, message = "文件名不合法！")
    private String fileName;
    @Schema(description="文件路径", required=true)
    private String filePath;

}
