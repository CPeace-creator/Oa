package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.OaProcessTypeMapper;
import com.atguigu.auth.service.OaProcessTemplateService;
import com.atguigu.auth.service.OaProcessTypeService;
import com.atguigu.common.response.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.io.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author cjh
 * @since 2023-11-04
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateService templateService;
    @Override
    public List<ProcessType> findProcessType() {
        //1 查询所有审批分类，返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);

        //2 遍历返回所有审批分类list集合
        for (ProcessType processType:processTypeList) {
            //3 得到每个审批分类，根据审批分类id查询对应审批模板
            //审批分类id
            Long typeId = processType.getId();
            //根据审批分类id查询对应审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,typeId);
            List<ProcessTemplate> processTemplateList = templateService.list(wrapper);

            //4 根据审批分类id查询对应审批模板数据（List）封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
