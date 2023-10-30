package com.atguigu.auth.Utils;

import com.atguigu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjh
 * @date 2023/10/11
 */
public class MenuHelper {
    //使用递归方法创建菜单
    public static List<SysMenu> buildTree(List<SysMenu> menuList) {
        //创建list集合，放最终数组
        List<SysMenu> trees=new ArrayList<>();
        for (SysMenu sysMenu : menuList) {
            //递归入口
            //parentId=0是入口
            if(sysMenu.getParentId()==0)
            {
                trees.add(getChildern(sysMenu,menuList));
            }
        }
        return trees;
    }

    private static SysMenu getChildern(SysMenu sysMenu, List<SysMenu> menuList) {
        //遍历所有菜单，判断id和parentId对应关系
        for (SysMenu menu : menuList) {
            if(sysMenu.getId().longValue()==menu.getParentId().longValue())
            {
                if(sysMenu.getChildren()==null)
                {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildern(menu,menuList));
            }
        }
        return sysMenu;
    }
}
