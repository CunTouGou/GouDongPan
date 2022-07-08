package com.hgz.file.dto.user;

import com.hgz.common.constant.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author CunTouGou
 * @date 2022/4/11 13:53
 */
@Data
@Schema(name = "用户注册DTO",required = true)
public class RegisterDTO {

    @Schema(description = "用户名", required = true, example = "狗洞同学")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 20, message = "用户名最少1位，最多20位")
    private String username;

    @Schema(description = "手机号", required = true, example = "13215825621")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegexConstant.PHONE_REGEX, message = "手机号码输入有误")
    private String telephone;

    @Schema(description = "密码", required = true, example = "pwdtest01")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = RegexConstant.PASSWORD_REGEX, message = "密码长度6-20位,不允许中文")
    private String password;
}
