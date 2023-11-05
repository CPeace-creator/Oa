package com.atguigu.auth.mapper;

import com.atguigu.model.process.ProcessType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author cjh
 * @since 2023-11-04
 */
@Mapper
public interface OaProcessTypeMapper extends BaseMapper<ProcessType> {

}
