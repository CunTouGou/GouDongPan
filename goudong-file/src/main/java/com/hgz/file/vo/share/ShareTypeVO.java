package com.hgz.file.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/5/1 0:09
 */
@Data
@Schema(description="分享类型VO")
public class ShareTypeVO {
    @Schema(description="0公共，1私密，2好友")
    private Integer shareType;
}
