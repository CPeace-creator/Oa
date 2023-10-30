package com.atguigu.auth.service;

import com.atguigu.common.response.Result;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author cjh
 * @since 2023-10-03
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    Result login(LoginVo login);
    SysUser getByUsername(String userName);
}
