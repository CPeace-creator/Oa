package com.atguigu.auth.APiCtrl;

import com.atguigu.auth.service.OaProcessService;
import com.atguigu.auth.service.OaProcessTemplateService;
import com.atguigu.auth.service.OaProcessTypeService;
import com.atguigu.common.response.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.vo.process.ApprovalVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author cjh
 * @date 2023/11/5
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin  //跨域
public class ProcessApiController {
    @Autowired
    private OaProcessTypeService processTypeService;
    @Autowired
    private OaProcessTemplateService processTemplateService;
    @Autowired
    private OaProcessService processService;
    @ApiOperation(value = "获取全部审批分类及模板")
    @GetMapping("findProcessType")
    public Result findProcessType() {
        return Result.ok(processTypeService.findProcessType());
    }
    @ApiOperation(value = "获取审批模板")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result get(@PathVariable Long processTemplateId) {
        ProcessTemplate processTemplate = processTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }
    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findPending(pageParam));
    }
    //查询审批详情信息
    @ApiOperation(value = "获取审批详情")
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id) {
        return Result.ok(processService.show(id));
    }
    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) {
        processService.approve(approvalVo);
        return Result.ok();
    }
}
