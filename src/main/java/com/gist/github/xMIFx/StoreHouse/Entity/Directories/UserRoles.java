package com.gist.github.xMIFx.StoreHouse.Entity.Directories;


/**
 * Created by Vlad on 15.03.2015.
 */
public class UserRoles {
    public int iD;
    public String name;
    public Boolean fullRole;
    public Boolean consumeVisible;

    public UserRoles(int iD, String name, Boolean fullRole, Boolean consumeVisible) {
        this.iD = iD;
        this.name = name;
        this.fullRole = fullRole;
        this.consumeVisible = consumeVisible;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullRole(Boolean fullRole) {
        this.fullRole = fullRole;
    }

    public void setConsumeVisible(Boolean consumeVisible) {
        this.consumeVisible = consumeVisible;
    }

    public String getName() {

        return name;
    }

    public Boolean getFullRole() {
        return fullRole;
    }

    public Boolean getConsumeVisible() {
        return consumeVisible;
    }

    public int getiD() {
        return iD;
    }

    public void setiD(int iD) {
        this.iD = iD;
    }


}
