package com.hgz.file.util;

import com.hgz.common.util.CollectUtil;
import com.hgz.common.util.DateUtil;
import com.hgz.file.model.file.OperationLogBean;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CunTouGou
 * @date 2022/4/28 5:11
 */

public class OperationLogUtil {

    /**
     * 构造操作日志参数
     *
     * @param request   请求
     * @param isSuccess 操作是否成功（成功/失败）
     * @param source    操作源模块
     * @param operation 执行操作
     * @param detail    详细信息
     * @return 操作日志参数
     */
    public static OperationLogBean getOperationLogObj(HttpServletRequest request, Long userId, String isSuccess, String source, String operation, String detail) {

        //用户需要登录才能进行的操作，需要记录操作日志
        OperationLogBean operationLogBean = new OperationLogBean();
        operationLogBean.setUserId(userId);
        operationLogBean.setTime(DateUtil.getCurrentTime());
        operationLogBean.setTerminal(new CollectUtil().getClientIpAddress(request));
        operationLogBean.setSource(source);
        operationLogBean.setResult(isSuccess);
        operationLogBean.setOperation(operation);
        operationLogBean.setDetail(detail);
        operationLogBean.setRequestURI(request.getRequestURI());
        operationLogBean.setRequestMethod(request.getMethod());

        return operationLogBean;
    }

}
