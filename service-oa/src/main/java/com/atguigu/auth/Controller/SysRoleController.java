package com.atguigu.auth.Controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.response.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cjh
 * @date 2023/9/28
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/sysrole/")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;
    //查询所有角色
    @ApiOperation(value = "获取全部角色列表")
    @GetMapping("findAll")
    public Result list(){
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }
    //条件分页查询
    //page 当前页  limit 每页显示记录数
    //SysRoleQueryVo 条件对象
    @ApiOperation("条件分页查询")
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page, @PathVariable Long limit, SysRoleQueryVo sysRoleQueryVo) {
        //调用service方法实现
        //1 创建Page对象，传递分页相关参数
        Page<SysRole> Page=new Page(page,limit);
        //page 当前页  limit 每页显示记录数
        //2 封装条件，判断条件是否为空，不为空进行封装
        LambdaQueryWrapper<SysRole> wrapper=new LambdaQueryWrapper<>();
        String name = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isBlank(name))
        {
            //封装
            wrapper.like(SysRole::getRoleName,name);
        }
        IPage<SysRole> pageModel=sysRoleService.page(Page,wrapper);
        return Result.ok(pageModel);
    }
    @ApiOperation("获取")
    @GetMapping("get/{id}")
    public Result GetSysRole(@PathVariable Long id)
    {
        return Result.ok(sysRoleService.getById(id));
    }
    //新增角色
    @ApiOperation("新增角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @PostMapping("save")
    public Result SaveSysRole(@RequestBody SysRole sysRole)
    {
        if(sysRoleService.save(sysRole))
        {
            return Result.ok();
        }
        else
        {
            return Result.fail();
        }
    }
    @ApiOperation(value = "修改角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @PutMapping("update")
    public Result UpdateSysRole(@RequestBody SysRole sysRole)
    {
        return Result.ok(sysRoleService.updateById(sysRole));
    }
    @ApiOperation(value = "删除角色")
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @DeleteMapping("remove/{id}")
    public Result RemoveSysRole(@PathVariable Long id)
    {
        return Result.ok(sysRoleService.removeById(id));
    }
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result RemoveBatch(@RequestBody List<Long> list){
        return Result.ok(sysRoleService.removeByIds(list));
    }

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> roleMap = sysRoleService.findRoleByUserId(userId);
        return Result.ok(roleMap);
    }

    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }
}
