package com.gist.github.xMIFx.StoreHouse.Entity.Directories;


/**
 * Created by Vlad on 15.03.2015.
 */
public class UserRoles {
    public int iD;
    public String name;
    public Boolean fullRole;


    public UserRoles(int iD, String name, Boolean fullRole) {
        this.iD = iD;
        this.name = name;
        this.fullRole = fullRole;
     }



    public void setName(String name) {
        this.name = name;
    }

    public void setFullRole(Boolean fullRole) {
        this.fullRole = fullRole;
    }


    public String getName() {

        return name;
    }

    public Boolean getFullRole() {
        return fullRole;
    }


    public int getiD() {
        return iD;
    }

    public void setiD(int iD) {
        this.iD = iD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRoles userRoles = (UserRoles) o;

        return iD == userRoles.iD;

    }

    @Override
    public int hashCode() {
        return iD;
    }
}
