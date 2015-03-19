package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 09.03.2015.
 */
public class UserInterface {
    private int iD;
    private String name;
    private List<InterfaceMenu> menuList;

    public UserInterface(int iD, String name) {
        this.iD = iD;
        this.name = name;
    }

    public void setMenuList(List<InterfaceMenu> menuList) {
        this.menuList = menuList;
    }

    public List<InterfaceMenu> getMenuList() {
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        return menuList;
    }

    public UserInterface(int iD, String name, List<InterfaceMenu> menuList) {
        this.iD = iD;
        this.name = name;
        this.menuList = menuList;
    }
}
