package com.hgz.file.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author CunTouGou
 * @date 2022/4/18 15:39
 */

@Data
@Schema(name = "用户登录Vo",required = true)
public class UserLoginVo {
    @Schema(description = "用户id", example = "1")
    private long userId;

    @Schema(description = "用户名", example = "狗洞同学")
    private String username;

    @Schema(description = "真实名", example = "狗洞同学")
    private String realname;

    private String qqImageUrl;

    @Schema(description = "手机号", example = "13215825622")
    private String telephone;

    @Schema(description = "邮箱", example = "1594872540@qq.com")
    private String email;

    @Schema(description = "性别", example = "男")
    private String sex;

    @Schema(description = "生日", example = "1999-02-08")
    private String birthday;

    @Schema(description = "省", example = "海南省")
    private String addrProvince;

    @Schema(description = "市", example = "万宁市")
    private String addrCity;

    @Schema(description = "区/镇", example = "和乐镇")
    private String addrArea;

    @Schema(description = "行业", example = "计算机行业")
    private String industry;

    @Schema(description = "职位", example = "java开发")
    private String position;

    @Schema(description = "个人介绍", example = "君子厚积而薄发")
    private String intro;

    @Schema(description = "用户头像地址", example = "\\upload\\20220405\\93811586079860974.png")
    private String imageUrl;

    @Schema(description = "注册时间", example = "2022-03-01 14:21:52")
    private String registerTime;

    @Schema(description = "最后登录时间", example = "2022-03-01 14:21:52")
    private String lastLoginTime;

    @Schema(description = "Token 接口访问凭证")
    private String token;

}
