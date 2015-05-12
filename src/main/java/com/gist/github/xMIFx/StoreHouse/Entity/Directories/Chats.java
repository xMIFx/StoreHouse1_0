package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class Chats {
    private int idChat;
    private String nameChat;
    private Set<User> userList;
    private Set<Messages> messagesList;
    private boolean isThereSomeMoreMessages;
    private final String type = "Chat"; //for json

    public Chats() {
    }

    public Chats(int idChat, String nameChat) {
        this.idChat = idChat;
        this.nameChat = nameChat;
    }

    public Chats(int idChat, String nameChat, Set<User> userList, Set<Messages> messagesList) {
        this.idChat = idChat;
        this.nameChat = nameChat;
        this.userList = userList;
        if (this.messagesList == null) {
            this.messagesList = Collections.newSetFromMap(new ConcurrentHashMap());
        }
        this.messagesList.addAll(messagesList);
    }

    public int getIdChat() {
        return idChat;
    }

    public String getNameChat() {
        return nameChat;
    }

    public String getType() {
        return type;
    }

    public void setNameChat(String nameChat) {
        this.nameChat = nameChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public boolean isThereSomeMoreMessages() {
        return isThereSomeMoreMessages;
    }

    public void setIsThereSomeMoreMessages(boolean isThereSomeMoreMessages) {
        this.isThereSomeMoreMessages = isThereSomeMoreMessages;
    }

    public Set<User> getUserList() {
        if (userList == null) {
            userList = new HashSet<>();
        }
        return userList;
    }

    public Set<Messages> getMessagesList() {
        if (messagesList == null) {
            messagesList = Collections.newSetFromMap(new ConcurrentHashMap());
        }
        return messagesList;
    }

    public void addMessage(Messages mes) {
        if (messagesList == null) {
            messagesList = Collections.newSetFromMap(new ConcurrentHashMap());
        }
        messagesList.add(mes);

    }

    public void addUser(User user) {
        if (userList == null) {
            userList = new HashSet<>();
        }
        userList.add(user);
    }


    public boolean containsUser(User user) {
        if (userList == null) {
            return false;
        }
        return userList.contains(user.getUuid());
    }

    public boolean containsMessage(Messages message) {
        if (messagesList == null) {
            return false;
        }
        return messagesList.contains(message);
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
                ", userList=" + userList +
                ", messagesList=" + messagesList +
                ", type=" + type +
                '}';
    }
}
