package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 09.03.2015.
 */
public class InterfaceMenu {

    private int iD;
    private String name;
    private List<MenuButton> buttonList;

    public void setButtonList(List<MenuButton> buttonList) {
        this.buttonList = buttonList;
    }

    public String getName() {
        return name;
    }

    public List<MenuButton> getButtonList() {
        if (buttonList == null) {
            buttonList = new ArrayList<>();
        }
        return buttonList;
    }

    public int getiD() {
        return iD;
    }

    public InterfaceMenu(int iD, String name) {
        this.iD = iD;
        this.name = name;
    }

    public InterfaceMenu(int iD, String name, List<MenuButton> buttonList) {
        this.iD = iD;
        this.name = name;
        this.buttonList = buttonList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterfaceMenu that = (InterfaceMenu) o;

        if (iD != that.iD) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return iD;
    }
}
