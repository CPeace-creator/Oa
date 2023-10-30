package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.response.Utils.JwtUtils;
import com.atguigu.model.system.SysRole;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author cjh
 * @date 2023/9/27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {
    @Autowired
    private SysRoleService sysRoleMapper;
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
    public void delopyProcess(){
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("process/qingjia.xml").addClasspathResource("process/qingjia.png")
                .name("请假审批流程").deploy();
        System.out.println("Github测试用例3");
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
}
