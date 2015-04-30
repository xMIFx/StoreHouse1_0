package com.gist.github.xMIFx.StoreHouse.Entity.Interfaces;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.UUID;

/**
 * Created by Vlad on 15.02.2015.
 */
public abstract class Directory {
    public boolean markForDelete = false;
    protected int id;
    final String uuid;


    public Directory(String uuid) {
        this.uuid = uuid;
    }

    @JsonIgnore
    public String getUuid() {
        return uuid;
    }

    public Directory(String uuid, int id, boolean markForDelete) {
        this.uuid = uuid;
        this.id = id;
        this.markForDelete = markForDelete;

    }

    public void markToDelete(boolean mark) {
        markForDelete = mark;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected static String createGuid() {
        return UUID.randomUUID().toString();
    }


    public static String getEmptyUUID() {
        return "00000000-0000-0000-0000-000000000000";
    }

}
