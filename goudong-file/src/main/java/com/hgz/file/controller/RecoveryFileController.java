package com.hgz.file.controller;

import com.alibaba.fastjson.JSON;
import com.hgz.common.anno.MyLog;
import com.hgz.common.result.RestResult;
import com.hgz.common.util.security.JwtUser;
import com.hgz.common.util.security.SessionUtil;
import com.hgz.file.service.*;
import com.hgz.file.component.AsyncTaskComp;
import com.hgz.file.model.file.RecoveryFile;
import com.hgz.file.dto.file.DeleteRecoveryFileDTO;
import com.hgz.file.dto.recoveryfile.BatchDeleteRecoveryFileDTO;
import com.hgz.file.dto.recoveryfile.RestoreFileDTO;
import com.hgz.file.vo.file.RecoveryFileListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 回收站文件接口
 *
 * @author CunTouGou
 * @date 2022/4/26 16:02
 */
@Tag(name = "recoveryFile", description = "文件删除后会进入回收站，该接口主要是对回收站文件进行管理")
@RestController
@Slf4j
@RequestMapping("/recoveryfile")
public class RecoveryFileController {
    @Resource
    private RecoveryFileService recoveryFileService;
    @Resource
    private UserFileService userFileService;
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private FileTransferService fileTransferService;
    @Resource
    private AsyncTaskComp asyncTaskComp;


    public static final String CURRENT_MODULE = "回收站文件接口";

    @Operation(summary = "删除回收文件", description = "删除回收文件", tags = {"recoveryFile"})
    @MyLog(operation = "删除回收文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/deleterecoveryfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteRecoveryFile(@RequestBody DeleteRecoveryFileDTO deleteRecoveryFileDTO) {
        JwtUser sessionUserBean = SessionUtil.getSession();
        RecoveryFile recoveryFile = recoveryFileService.getById(deleteRecoveryFileDTO.getRecoveryFileId());

        asyncTaskComp.deleteUserFile(recoveryFile.getUserFileId());

        recoveryFileService.removeById(deleteRecoveryFileDTO.getRecoveryFileId());
        return RestResult.success().data("删除成功");
    }

    @Operation(summary = "批量删除回收文件", description = "批量删除回收文件", tags = {"recoveryFile"})
    @RequestMapping(value = "/batchdelete", method = RequestMethod.POST)
    @MyLog(operation = "批量删除回收文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> batchDeleteRecoveryFile(@RequestBody BatchDeleteRecoveryFileDTO batchDeleteRecoveryFileDTO) {
        JwtUser sessionUserBean = SessionUtil.getSession();
        List<RecoveryFile> recoveryFileList = JSON.parseArray(batchDeleteRecoveryFileDTO.getRecoveryFileIds(), RecoveryFile.class);
        for (RecoveryFile recoveryFile : recoveryFileList) {
            RecoveryFile recoveryFile1 = recoveryFileService.getById(recoveryFile.getRecoveryFileId());

            if (recoveryFile1 != null) {
                asyncTaskComp.deleteUserFile(recoveryFile1.getUserFileId());

                recoveryFileService.removeById(recoveryFile1.getRecoveryFileId());
            }

        }
        return RestResult.success().data("批量删除成功");
    }

    @Operation(summary = "回收文件列表", description = "回收文件列表", tags = {"recoveryFile"})
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<List<RecoveryFileListVo>> getRecoveryFileList() {
        JwtUser sessionUserBean = SessionUtil.getSession();
        RestResult<List<RecoveryFileListVo>> restResult = new RestResult<List<RecoveryFileListVo>>();
        List<RecoveryFileListVo> recoveryFileList = recoveryFileService.selectRecoveryFileList(sessionUserBean.getUserId());
        restResult.setData(recoveryFileList);
        restResult.setSuccess(true);

        return restResult;
    }

    @Operation(summary = "还原文件", description = "还原文件", tags = {"recoveryFile"})
    @RequestMapping(value = "/restorefile", method = RequestMethod.POST)
    @MyLog(operation = "还原文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult restoreFile(@RequestBody RestoreFileDTO restoreFileDto) {
        JwtUser sessionUserBean = SessionUtil.getSession();
        recoveryFileService.restorefile(restoreFileDto.getDeleteBatchNum(), restoreFileDto.getFilePath(), sessionUserBean.getUserId());
        return RestResult.success().message("还原成功！");
    }

}









