package com.atguigu.config.Exception;

import com.atguigu.common.response.ResponseCodeEnum;

/**
 * @author cjh
 * @date 2023/9/30
 */
public class CjhException extends RuntimeException{
    private Integer code;
    private String msg;

    public CjhException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    //接收枚举类异常
    public CjhException(ResponseCodeEnum responseCodeEnum)
    {
        super(responseCodeEnum.getMessage());
        this.code=responseCodeEnum.getCode();
        this.msg=responseCodeEnum.getMessage();
    }
    @Override
    public String toString() {
        return "CjhException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
