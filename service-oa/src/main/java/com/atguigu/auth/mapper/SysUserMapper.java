package com.atguigu.auth.mapper;

import com.atguigu.model.system.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author cjh
 * @since 2023-10-03
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
