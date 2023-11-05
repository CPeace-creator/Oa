package com.atguigu.auth.mapper;

import com.atguigu.model.process.ProcessRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 审批记录 Mapper 接口
 * </p>
 *
 * @author cjh
 * @since 2023-11-05
 */
@Mapper
public interface OaProcessRecordMapper extends BaseMapper<ProcessRecord> {

}
