package com.hgz.file.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hgz.common.result.RestResult;
import com.hgz.file.service.SysParamService;
import com.hgz.file.model.file.SysParam;
import com.hgz.file.dto.param.QueryGroupParamDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统参数管理 用于配置存储空间大小
 * @author CunTouGou
 * @date 2022/4/26 10:02
 */

@Tag(name = "系统参数管理")
@RestController
@RequestMapping("/param")
public class SysParamController {
    @Resource
    private SysParamService sysParamService;

    @Operation(summary = "查询系统参数组", tags = {"系统参数管理"})
    @RequestMapping(value = "/grouplist", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<Map> groupList(
            @Parameter(description = "查询参数dto", required = false)
                    QueryGroupParamDTO queryGroupParamDTO
    ) {
        List<SysParam> list = sysParamService.list(new QueryWrapper<SysParam>().lambda().eq(SysParam::getGroupName, queryGroupParamDTO.getGroupName()));
        Map<String, Object> result = new HashMap<>();

        for (SysParam sysParam : list) {
            result.put(sysParam.getSysParamKey(), sysParam.getSysParamValue());
        }

        return RestResult.success().data(result);
    }


}
