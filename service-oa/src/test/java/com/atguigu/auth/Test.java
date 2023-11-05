package com.atguigu.auth;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.response.Utils.JwtUtils;
import com.atguigu.model.system.SysRole;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cjh
 * @date 2023/9/27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {
    @Autowired
    private SysRoleService sysRoleMapper;
    @Autowired
    private RuntimeService runtimeService;
    @org.junit.Test
    public void testSelectList() {
        List<SysRole> sysRoles = sysRoleMapper.list();
        System.out.println(sysRoles);
    }
    @org.junit.Test
    public void main() {
        String token = JwtUtils.createToken(1L, "admin");
        System.out.println(token);
        System.out.println(JwtUtils.getUserId(token));
        System.out.println(JwtUtils.getUsername(token));
    }
    //流程定义部署
    @Autowired
    private RepositoryService repositoryService;
    @org.junit.Test
    //TODO 审批流程库重新创建并且重新尝试插入数据(插入数据失败)
    public void delopyProcess(){
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("process/qjsp.bpmn20.xml").addClasspathResource("process/qjsp.png")
                .name("请假申请流程").deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    @org.junit.Test
    public void startUpProcess() {
        //创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia");
        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());
    }
    @Autowired
    private TaskService taskService;

    /**
     * 查询当前个人待执行的任务
     */
    @org.junit.Test
    public void findPendingTaskList() {
        //任务负责人
        String assignee = "cjh";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)//只查询该任务负责人的任务
                .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }
    /**
     * 完成任务
     */
    @org.junit.Test
    public void completTask(){
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan")  //要查询的负责人
                .singleResult();//返回一条

        //完成任务,参数：任务id
        taskService.complete(task.getId());
    }
    @Autowired
    private HistoryService historyService;
    /**
     * 查询已处理历史任务
     */
    @org.junit.Test
    public void findProcessedTaskList() {
        //张三已处理过的历史任务
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee("zhangsan").finished().list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("流程实例id：" + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id：" + historicTaskInstance.getId());
            System.out.println("任务负责人：" + historicTaskInstance.getAssignee());
            System.out.println("任务名称：" + historicTaskInstance.getName());
        }
    }
    //部署流程
    @org.junit.Test
    public void deploy(){
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("process/jiaban02.bpmn20.xml").name("加班审批流程监听").deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    /**
     * 启动流程实例
     */
    @org.junit.Test
    public void startUpProcess03() {
        //创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban04");
        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }
    ///启动流程实例
    @org.junit.Test
    public void start(){
        Map<String,Object> map=new HashMap<>();
        map.put("assignee2","zhangsan");
        map.put("assignee1","mary1");
        //设置任务人
        ProcessInstance process = runtimeService.startProcessInstanceByKey("jiaban",map);
        System.out.println(process.getId());
        System.out.println(process.getName());

    }
    @org.junit.Test
    public void test() throws FileNotFoundException {
        File path=new File(ResourceUtils.getURL("resource/process").getPath()).getAbsoluteFile();
        System.out.println(path);
        String admin = JwtUtils.createToken(1L, "admin");
        System.out.println(admin);
    }

}
