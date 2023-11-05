package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.OaProcessTemplateMapper;
import com.atguigu.auth.service.OaProcessService;
import com.atguigu.auth.service.OaProcessTemplateService;
import com.atguigu.auth.service.OaProcessTypeService;
import com.atguigu.common.response.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author cjh
 * @since 2023-11-04
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Resource
    private OaProcessTemplateMapper processTemplateMapper;

    @Resource
    private OaProcessTypeService processTypeService;
    @Resource
    private OaProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam) {
        LambdaQueryWrapper<ProcessTemplate> queryWrapper = new LambdaQueryWrapper<ProcessTemplate>();
        queryWrapper.orderByDesc(ProcessTemplate::getId);
        IPage<ProcessTemplate> page = processTemplateMapper.selectPage(pageParam, queryWrapper);
        List<ProcessTemplate> processTemplateList = page.getRecords();

        List<Long> processTypeIdList = processTemplateList.stream().map(processTemplate -> processTemplate.getProcessTypeId()).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(processTypeIdList)) {
            Map<Long, ProcessType> processTypeIdToProcessTypeMap = processTypeService.list(new LambdaQueryWrapper<ProcessType>().in(ProcessType::getId, processTypeIdList)).stream().collect(Collectors.toMap(ProcessType::getId, ProcessType -> ProcessType));
            for(ProcessTemplate processTemplate : processTemplateList) {
                ProcessType processType = processTypeIdToProcessTypeMap.get(processTemplate.getProcessTypeId());
                if(null == processType) continue;
                processTemplate.setProcessTypeName(processType.getName());
            }
        }
        return page;
    }
    /**
     * 上传zip文件
     * @param file
     * @return
     */
    @Override
    public Result upload(MultipartFile file) {
        try {
            File path=new File(ResourceUtils.getURL("classpath:").getPath()).getAbsoluteFile();
            String fileName = file.getOriginalFilename();
            //上传目录
            File upfile=new File(path+"/processes/");
            if(!upfile.exists())
            {
                upfile.mkdirs();
            }
            // 创建空文件用于写入文件
            File zipFile = new File(path + "/processes/" + fileName);
            // 保存文件流到本地
            file.transferTo(zipFile);
            Map<String, Object> map = new HashMap<>();
            //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
            map.put("processDefinitionPath", "processes/" + fileName);
            map.put("processDefinitionKey", fileName.substring(0, fileName.lastIndexOf(".")));
            return Result.ok(map);
        } catch (FileNotFoundException e) {
            return Result.fail("上传失败");
        } catch (IOException e) {
            return Result.fail("上传失败");
        }

    }

    //发布流程定义
    @Override
    public void publish(Long id) {
        //修改状态为已发布
        ProcessTemplate processTemplate = this.getById(id);
        processTemplate.setStatus(1);
        processTemplateMapper.updateById(processTemplate);
        //TODO 部署流程定义，后续完善
        if(StringUtils.isEmpty(processTemplate.getProcessDefinitionPath()))
        {
             log.error("流程未定义");
        }
        processService.deployByZip(processTemplate.getProcessDefinitionPath());
    }
}
