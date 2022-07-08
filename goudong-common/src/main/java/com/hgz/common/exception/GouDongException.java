package com.hgz.common.exception;

import com.hgz.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @author CunTouGou
 * @date 2022/5/10 2022/5/10
 */

@Data
public class GouDongException extends RuntimeException {
    private Integer code;

    public GouDongException(String message) {
        super(message);
        this.code = ResultCodeEnum.UNKNOWN_ERROR.getCode();
    }

    public GouDongException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public GouDongException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "GouDongException{" + "code=" + code + ", message=" + this.getMessage() + '}';
    }
}