package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import java.util.List;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class Chats {
    private int idChat;
    private List<String> userUUIDList;
    private List<Messages> messagesList;
    private final String type = "Chat"; //for json

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public List<String> getUserUUIDList() {
        return userUUIDList;
    }

    public void setUserUUIDList(List<String> userUUIDList) {
        this.userUUIDList = userUUIDList;
    }

    public List<Messages> getMessagesList() {
        return messagesList;
    }

    public void setMessagesList(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chats chats = (Chats) o;

        return idChat == chats.idChat;

    }

    @Override
    public int hashCode() {
        return idChat;
    }

    @Override
    public String toString() {
        return "Chats{" +
                "idChat=" + idChat +
                ", userUUIDList=" + userUUIDList +
                ", messagesList=" + messagesList +
                ", type=" + type +
                '}';
    }
}
