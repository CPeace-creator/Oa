package com.atguigu.auth.service;

import com.atguigu.common.response.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author cjh
 * @since 2023-11-04
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {

    IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam);

   Result upload(MultipartFile file);

    void publish(Long id);
}
