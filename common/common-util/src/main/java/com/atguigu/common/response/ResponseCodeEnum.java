package com.atguigu.common.response;

import lombok.Getter;

/**
 * @author cjh
 * @date 2023/9/28
 */
@Getter
public enum ResponseCodeEnum {
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    LOGIN_MOBLE_ERROR(204,"权限失败"),
    LOGIN_ERROR(203, "登录失败");

    private Integer code;

    private String message;

    private ResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
