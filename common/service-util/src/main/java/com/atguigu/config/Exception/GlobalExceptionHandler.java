package com.atguigu.config.Exception;

import com.atguigu.common.response.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.AuthenticationException;

/**
 * @author cjh
 * @date 2023/9/30
 */
//全局处理异常
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    //全局异常
    @ExceptionHandler(Exception.class)
    public Result error(){
        return Result.fail().message("执行了全局异常处理");
    }
    //执行特定异常
    @ExceptionHandler(ArithmeticException.class)
    public Result ArithmeticExceptionError(){
        return Result.fail().message("执行了特定异常处理");
    }
    //执行自定义异常
    @ExceptionHandler(CjhException.class)
    public Result CjhExceptionError(){
        return Result.fail().message("执行了自定义异常处理");
    }
}
