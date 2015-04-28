package com.gist.github.xMIFx.StoreHouse.Entity.Interfaces;

import com.gist.github.xMIFx.StoreHouse.Entity.StringCrypter;

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

    public static String getXorUUID(String uuid, String k) {
        byte[] txt = uuid.getBytes();
        byte[] key = k.getBytes();
        byte[] res = new byte[uuid.length()];

        for (int i = 0; i < txt.length; i++) {
            res[i] = (byte) (txt[i] ^ key[i % key.length]);
        }
        String result = new String(res);
        return changedSymbolsForWeb(result, true);
    }

    private static String changedSymbolsForWeb(String result,boolean toWeb) {

        char[] chars = result.toCharArray();
       StringBuilder builder = new StringBuilder();
       for (char ch:chars){
           if (toWeb)
           {

           }
           else {

           }
       }
        return builder.toString();
    }

    public static String getUnXorUUID(String xorUUID, String k) {
        byte[] txt = xorUUID.getBytes();
        byte[] key = k.getBytes();
        byte[] res = new byte[xorUUID.length()];

        for (int i = 0; i < txt.length; i++) {
            res[i] = (byte) (txt[i] ^ key[i % key.length]);
        }

        return changedSymbolsForWeb(new String(res),false);

    }
}
