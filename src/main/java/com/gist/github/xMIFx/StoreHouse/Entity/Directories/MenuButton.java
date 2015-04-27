package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

/**
 * Created by Vlad on 09.03.2015.
 */
public class MenuButton {
    private int iD;
    private String name;
    private String action;
    private final String type = "MenuButton"; //for json

    public void setiD(int iD) {
        this.iD = iD;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getiD() {

        return iD;
    }

    public String getName() {
        return name;
    }

    public String getAction() {
        return action;
    }

    public MenuButton(String name, String action) {
        this.name = name;
        this.action = action;
    }

    public MenuButton(int iD, String name, String action) {
        this.iD = iD;
        this.name = name;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuButton that = (MenuButton) o;

        return iD == that.iD;

    }

    @Override
    public int hashCode() {
        return iD;
    }

    @Override
    public String toString() {
        return "MenuButton{" +
                "iD=" + iD +
                ", name='" + name + '\'' +
                ", action='" + action + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
