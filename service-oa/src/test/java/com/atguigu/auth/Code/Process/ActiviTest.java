package com.atguigu.auth.Code.Process;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cjh
 * @date 2023/11/1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ActiviTest {
     @Autowired
    private TaskService taskService;
     @Autowired
    private HistoryService historyService;
     @Autowired
     private RepositoryService repositoryService;

     @Autowired
    private RuntimeService runtimeService;
     @Test
    public void deploy(){
         Deployment deploy = repositoryService.createDeployment().addClasspathResource("process/jiaban.bpmn21.xml").name("加班审批流程").deploy();
         System.out.println(deploy.getId());
         System.out.println(deploy.getName());
     }
     ///启动流程实例
    @Test
    public void start(){
        Map<String,Object> map=new HashMap<>();
        map.put("assignee1","lucy");
        map.put("assignee2","mary");
        //设置任务人
        ProcessInstance process = runtimeService.startProcessInstanceByKey("jiaban",map);
        System.out.println(process.getId());
        System.out.println(process.getName());

    }
}
