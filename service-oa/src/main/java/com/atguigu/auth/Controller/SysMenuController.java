package com.atguigu.auth.Controller;


import com.atguigu.auth.service.SysMenuService;
import com.atguigu.common.response.Result;
import com.atguigu.model.system.SysMenu;
import com.atguigu.vo.system.AssginMenuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author cjh
 * @since 2023-10-11
 */
@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/sys-menu/")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;
    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu permission) {
        sysMenuService.save(permission);
        return Result.ok();
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu permission) {
        sysMenuService.updateById(permission);
        return Result.ok();
    }

//    @ApiOperation(value = "删除菜单")
//    @DeleteMapping("remove/{id}")
//    public Result remove(@PathVariable Long id) {
//        sysMenuService.removeByIdIsChildren(id);
//        return Result.ok();
//    }
    @ApiOperation(value = "获取菜单")
    @GetMapping("findNodes")
    public Result findNodes() {
        List<SysMenu> list = sysMenuService.findNodes();
        return Result.ok(list);
    }
    //查询所有菜单和角色分配的菜单
    @GetMapping("/toAssign/{roleId}")
    @ApiOperation("查询所有菜单和角色分配的菜单")
    public Result toAssign(@PathVariable Long roleId)
    {
        return Result.ok(sysMenuService.findMenuByRoleId(roleId));
    }
    @ApiOperation("角色分配菜单")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginMenuVo assginMenuVo)
    {
        sysMenuService.doAssign(assginMenuVo);
        return Result.ok();
    }
}

