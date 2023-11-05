package com.atguigu.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.auth.mapper.OaProcessMapper;
import com.atguigu.auth.service.OaProcessRecordService;
import com.atguigu.auth.service.OaProcessService;
import com.atguigu.auth.service.OaProcessTemplateService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.system.SysUser;
import com.atguigu.security.custom.LoginUserInfoHelper;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author cjh
 * @since 2023-11-05
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper,Process> implements OaProcessService {

    @Autowired
    private OaProcessMapper processMapper;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private OaProcessRecordService processRecordService;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> page = processMapper.selectPage(pageParam, processQueryVo);
        return page;
    }

    //部署流程定义
    @Override
    public void deployByZip(String deployPath) {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream=new ZipInputStream(input);
        //部署
        Deployment deploy = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
        log.debug(deploy.getId());
        log.debug(deploy.getName());
    }



    public void startUp(ProcessFormVo processFormVo) {
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        processMapper.insert(process);
        //绑定业务id
        String businessKey = String.valueOf(process.getId());
        //流程参数
        Map<String, Object> variables = new HashMap<>();
        //将表单数据放入流程实例中
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String, Object> map = new HashMap<>();
        //循环转换
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        variables.put("data", map);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processTemplate.getProcessDefinitionKey(), businessKey, variables);
        //业务表关联当前流程实例id
        String processInstanceId = processInstance.getId();
        process.setProcessInstanceId(processInstanceId);
        //计算下一个审批人，可能有多个（并行审批）
        List<Task> taskList = this.getCurrentTaskList(processInstanceId);
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for(Task task : taskList) {
                SysUser user = sysUserService.getByUsername(task.getAssignee());//获取下一位审批人
                assigneeList.add(user.getName());
                //推送消息给下一个审批人，后续完善

            }
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ',') + "审批");
        }
        processMapper.updateById(process);
        //记录操作行为
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    private List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return tasks;
    }
}
