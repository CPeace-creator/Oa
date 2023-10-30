package com.atguigu.auth.service;

import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.Objects;

/**
 * @author cjh
 * @date 2023/9/28
 */
public interface SysRoleService extends IService<SysRole> {
    void doAssign(AssginRoleVo assginRoleVo);

    Map<String, Object> findRoleByUserId(Long userId);
}
