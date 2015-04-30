package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class Chats {
    private int idChat;
    private String nameChat;
    private Set<String> userUUIDList;
    private List<Messages> messagesList;
    private final String type = "Chat"; //for json

    public Chats() {
    }

    public Chats(int idChat, String nameChat, Set<String> userUUIDList, List<Messages> messagesList) {
        this.idChat = idChat;
        this.nameChat = nameChat;
        this.userUUIDList = userUUIDList;
        this.messagesList = messagesList;
    }

    public int getIdChat() {
        return idChat;
    }

    public String getNameChat() {
        return nameChat;
    }

    public void setNameChat(String nameChat) {
        this.nameChat = nameChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public Set<String> getUserUUIDList() {
        return userUUIDList;
    }

    public void setUserUUIDList(Set<String> userUUIDList) {
        this.userUUIDList = userUUIDList;
    }

    public List<Messages> getMessagesList() {
        return messagesList;
    }

    public void setMessagesList(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    public void addUser(String userUUID) {
        if (userUUIDList == null) {
            userUUIDList = new HashSet<>();
        }
        userUUIDList.add(userUUID);
    }

    public void addUser(User user) {
        if (userUUIDList == null) {
            userUUIDList = new HashSet<>();
        }
        userUUIDList.add(user.getUuid());
    }

    public boolean containsUser(String userUUID) {
        return userUUIDList.contains(userUUID);
    }

    public boolean containsUser(User user) {
        return userUUIDList.contains(user.getUuid());
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
