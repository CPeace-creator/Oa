package com.atguigu.auth.service;

import com.atguigu.common.response.Result;
import com.atguigu.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author cjh
 * @since 2023-11-04
 */
public interface OaProcessTypeService extends IService<ProcessType> {
    //查询审批分类与模板
    List<ProcessType> findProcessType();
}
