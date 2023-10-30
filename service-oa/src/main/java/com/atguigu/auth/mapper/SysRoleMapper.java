package com.atguigu.auth.mapper;

import com.atguigu.model.system.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author cjh
 * @date 2023/9/27
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
