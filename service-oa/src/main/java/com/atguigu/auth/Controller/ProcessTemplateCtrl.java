package com.atguigu.auth.Controller;

import com.atguigu.auth.service.OaProcessTemplateService;
import com.atguigu.common.response.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author cjh
 * @date 2023/11/4
 */
@Api(value = "审批模板管理", tags = "审批模板管理")
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProcessTemplateCtrl {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessTemplate> pageParam = new Page<>(page, limit);
        IPage<ProcessTemplate> pageModel = processTemplateService.selectPage(pageParam);
        return Result.ok(pageModel);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }
}
