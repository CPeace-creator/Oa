package com.atguigu.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.auth.mapper.OaProcessMapper;
import com.atguigu.auth.service.*;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessRecord;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.system.SysUser;
import com.atguigu.security.custom.LoginUserInfoHelper;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
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
    private MessageService messageService;
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
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
        }
        processMapper.updateById(process);
        //记录操作行为
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    //查询待办列表
    @Override
    public IPage<ProcessVo> findPending(Page<java.lang.Process> pageParam) {
            // 根据当前人的ID查询
            TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername()).orderByTaskCreateTime().desc();
            List<Task> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
            long totalCount = query.count();

            List<ProcessVo> processList = new ArrayList<>();
            // 根据流程的业务ID查询实体并关联
            for (Task item : list) {
                String processInstanceId = item.getProcessInstanceId();
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                if (processInstance == null) {
                    continue;
                }
                // 业务key
                String businessKey = processInstance.getBusinessKey();
                if (businessKey == null) {
                    continue;
                }
                Process process = this.getById(Long.parseLong(businessKey));
                ProcessVo processVo = new ProcessVo();
                BeanUtils.copyProperties(process, processVo);
                processVo.setTaskId(item.getId());
                processList.add(processVo);
            }
            IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
            page.setRecords(processList);
            return page;
    }

    //查询审批详情
    @Override
    public Map<String, Object> show(Long id) {
        Process process = this.getById(id);
        List<ProcessRecord> processRecordList = processRecordService.list(new LambdaQueryWrapper<ProcessRecord>().eq(ProcessRecord::getProcessId, id));
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        //计算当前用户是否可以审批，能够查看详情的用户不是都能审批，审批后也不能重复审批
        boolean isApprove = false;
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            for(Task task : taskList) {
                //审批流程的人可能有多个人 看是否包含当前用户则可审批 否则不可审批 isApprove=false
                if(task.getAssignee().contains(LoginUserInfoHelper.getUsername())) {
                    isApprove = true;
                }
            }
        }
        map.put("isApprove", isApprove);
        return map;

    }

    //审批按钮
    @Override
    public void approve(ApprovalVo approvalVo) {
        Map<String, Object> variables1 = taskService.getVariables(approvalVo.getTaskId());
        for (Map.Entry<String, Object> entry : variables1.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        String taskId = approvalVo.getTaskId();
        if (approvalVo.getStatus() == 1) {
            //已通过
            Map<String, Object> variables = new HashMap<String, Object>();
            taskService.complete(taskId, variables);
        } else {
            //驳回
            this.endTask(taskId);
        }
        String description = approvalVo.getStatus().intValue() == 1 ? "已通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), description);

        //计算下一个审批人
        Process process = this.getById(approvalVo.getProcessId());
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for (Task task : taskList) {
                SysUser sysUser = sysUserService.getByUsername(task.getAssignee());
                assigneeList.add(sysUser.getName());
                process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
                process.setStatus(1);
                if (approvalVo.getStatus().intValue() == 1) {
                    process.setDescription("审批完成（同意）");
                    process.setStatus(2);
                } else {
                    process.setDescription("审批完成（拒绝）");
                    process.setStatus(-1);
                }
            }
            //推送消息给申请人
            messageService.pushProcessedMessage(process.getId(), process.getUserId(), approvalVo.getStatus());
            this.updateById(process);
        }
    }

        @Override
        public IPage<ProcessVo> findStarted (Page < ProcessVo > pageParam) {
            ProcessQueryVo processQueryVo = new ProcessQueryVo();
            processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
            IPage<ProcessVo> page = processMapper.selectPage(pageParam, processQueryVo);
            for (ProcessVo item : page.getRecords()) {
                item.setTaskId("0");
            }
            return page;
        }

        private void endTask (String taskId){
            //  当前任务
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
            // 并行任务可能为null
            if (CollectionUtils.isEmpty(endEventList)) {
                return;
            }
            FlowNode endFlowNode = (FlowNode) endEventList.get(0);
            FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

            //  临时保存当前活动的原始方向
            List originalSequenceFlowList = new ArrayList<>();
            originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
            //  清理活动方向
            currentFlowNode.getOutgoingFlows().clear();

            //  建立新方向
            SequenceFlow newSequenceFlow = new SequenceFlow();
            newSequenceFlow.setId("newSequenceFlowId");
            newSequenceFlow.setSourceFlowElement(currentFlowNode);
            newSequenceFlow.setTargetFlowElement(endFlowNode);
            List newSequenceFlowList = new ArrayList<>();
            newSequenceFlowList.add(newSequenceFlow);
            //  当前节点指向新的方向
            currentFlowNode.setOutgoingFlows(newSequenceFlowList);

            //  完成当前任务
            taskService.complete(task.getId());
        }

        //获取当前任务
        private List<Task> getCurrentTaskList (String processInstanceId){
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            return tasks;
        }
    }
