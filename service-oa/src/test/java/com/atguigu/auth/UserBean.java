package com.atguigu.auth;

import org.springframework.stereotype.Component;

/**
 * @author cjh
 * @date 2023/11/1
 */
@Component
public class UserBean {

    public String getUsername(int id) {
        if(id == 1) {
            return "cjh";
        }
        if(id == 2) {
            return "lisi";
        }
        return "admin";
    }
}
