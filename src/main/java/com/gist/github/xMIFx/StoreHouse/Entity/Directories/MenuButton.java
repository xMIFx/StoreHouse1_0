package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

/**
 * Created by Vlad on 09.03.2015.
 */
public class MenuButton {
    private int iD;
    private String name;
    private  String action;

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
}
