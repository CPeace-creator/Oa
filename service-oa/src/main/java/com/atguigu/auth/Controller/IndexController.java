package com.atguigu.auth.Controller;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.response.Result;
import com.atguigu.common.response.Utils.JwtUtils;
import com.atguigu.common.response.Utils.MD5;
import com.atguigu.config.Exception.CjhException;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author cjh
 * @date 2023/10/1
 */
@RestController
@RequestMapping("/admin/system/index/")
@Api(tags = "登录管理接口")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
//    //登录中的Login
//    @PostMapping("login")
//    public Result login()
//    {
//        Map<String, Object> map=new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);
//    }
    @PostMapping("login")
    @ApiOperation("登录")
    public Result login(@RequestBody LoginVo login)
    {

        //{"code":200,"data":{"token":"admin-token"}}
//        Map<String,Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);
        //1 获取输入用户名和密码
        //2 根据用户名查询数据库
        String username = login.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(wrapper);

        //3 用户信息是否存在
        if(sysUser == null) {
            throw new CjhException(201,"用户不存在");
        }

        //4 判断密码
        //数据库存密码（MD5）
        String password_db = sysUser.getPassword();
        //获取输入的密码
        String password_input = MD5.encrypt(login.getPassword());
        if(!password_db.equals(password_input)) {
            throw new CjhException(201,"密码错误");
        }

        //5 判断用户是否被禁用  1 可用 0 禁用
        if(sysUser.getStatus().intValue()==0) {
            throw new CjhException(201,"用户已经被禁用");
        }

        //6 使用jwt根据用户id和用户名称生成token字符串
        String token = JwtUtils.createToken(sysUser.getId(), sysUser.getUsername());
        //7 返回
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("info")
    @ApiOperation("获取用户信息")
    public Result info(HttpServletRequest request) {
        try {
            //1 从请求头获取用户信息（获取请求头token字符串）
            String token = request.getHeader("token");
            System.out.println("token值为"+token);
            //2 从token字符串获取用户id 或者 用户名称
            Long userId = JwtUtils.getUserId(token);

            //3 根据用户id查询数据库，把用户信息获取出来
            SysUser sysUser = sysUserService.getById(userId);

            //4 根据用户id获取用户可以操作菜单列表
            //查询数据库动态构建路由结构，进行显示
            List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);

            //5 根据用户id获取用户可以操作按钮列表
            List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

            //6 返回相应的数据
            Map<String, Object> map = new HashMap<>();
            map.put("name",sysUser.getName());
            map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            //返回用户可以操作菜单
            map.put("routers",routerList);
            //返回用户可以操作按钮
            map.put("buttons",permsList);
            return Result.ok(map);

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
       return null;
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    @ApiOperation("退出")
    public Result logout(){
        return Result.ok();
    }

}
