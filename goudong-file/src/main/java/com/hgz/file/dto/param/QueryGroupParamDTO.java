package com.hgz.file.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/4/12 10:46
 */

@Data
@Schema(name = "获取组参数列表DTO")
public class QueryGroupParamDTO {

    @Schema(description = "组名")
    private String groupName;

}
