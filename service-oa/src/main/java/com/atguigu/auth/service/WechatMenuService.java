package com.atguigu.auth.service;

import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.wechat.MenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author cjh
 * @since 2023-11-08
 */
public interface WechatMenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();
    void syncMenu();
}
