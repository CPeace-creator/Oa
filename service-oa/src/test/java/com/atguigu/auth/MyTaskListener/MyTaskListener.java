package com.atguigu.auth.MyTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author cjh
 * @date 2023/11/1
 */
public class MyTaskListener  implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if(delegateTask.getName().equals("经理审批")){
            //这里指定任务负责人
            delegateTask.setAssignee("zhangsan");
        } else if(delegateTask.getName().equals("人事审批")){
            //这里指定任务负责人
            delegateTask.setAssignee("cjh");
        }
    }
}
