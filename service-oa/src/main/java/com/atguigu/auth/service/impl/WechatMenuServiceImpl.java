package com.atguigu.auth.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.atguigu.auth.mapper.WechatMenuMapper;
import com.atguigu.auth.service.WechatMenuService;
import com.atguigu.auth.wechat.WechatAccountConfig;
import com.atguigu.auth.wechat.config.WeChatMpConfig;
import com.atguigu.config.Exception.CjhException;
import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.wechat.MenuVo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author cjh
 * @since 2023-11-08
 */
@Service
public class WechatMenuServiceImpl extends ServiceImpl<WechatMenuMapper, Menu> implements WechatMenuService {
    @Autowired
    private WxMpService wxMpService;

    @Override
    public List<MenuVo> findMenuInfo() {
        List<MenuVo> list = new ArrayList<>();
        List<Menu> menuList = baseMapper.selectList(null);
        List<Menu> oneMenuList = menuList.stream().filter(menu -> menu.getParentId().longValue() == 0).collect(Collectors.toList());
        for (Menu oneMenu : oneMenuList) {
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu, oneMenuVo);

            List<Menu> twoMenuList = menuList.stream()
                    .filter(menu -> menu.getParentId().longValue() == oneMenu.getId())
                    .sorted(Comparator.comparing(Menu::getSort))
                    .collect(Collectors.toList());
            List<MenuVo> children = new ArrayList<>();
            for (Menu twoMenu : twoMenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu, twoMenuVo);
                children.add(twoMenuVo);
            }
            oneMenuVo.setChildren(children);
            list.add(oneMenuVo);
        }
        return list;
    }
    @Override
    public void syncMenu() {
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for (MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if (CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://oa.atguigu.cn/#" + oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for (MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if (twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://oa.atguigu.cn#" + twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (CjhException e) {
            throw new RuntimeException(e);
        }
    }
}
