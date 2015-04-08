package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import com.gist.github.xMIFx.StoreHouse.Entity.Interfaces.Directory;

/**
 * Created by bukatinvv on 08.04.2015.
 */
public class MessengerGroup {

    public int ID;
    public String name;

    public MessengerGroup(int id, String name) {
        this.ID = id;
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessengerGroup that = (MessengerGroup) o;

        if (ID != that.ID) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = ID;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
