package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import java.util.Date;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class Messages {


    private User userFrom;
    private int idMessage;
    private int chatID;
    private String message;
    private boolean currentUserMessage;
    private boolean newMessage;
    private Date dateMessage;
    private final String type = "Messages"; //for json

    public Messages() {
    }

    public Messages(User userFrom, int chatID, int id, String message, boolean newMessage, Date dateMessage) {
        this.userFrom = userFrom;
        this.chatID = chatID;
        this.message = message;
        this.idMessage = id;
        this.newMessage = newMessage;
        this.dateMessage = dateMessage;
    }

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public boolean isCurrentUserMessage() {
        return currentUserMessage;
    }

    public void setCurrentUserMessage(boolean currentUserMessage) {
        this.currentUserMessage = currentUserMessage;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    public Date getDateMessage() {
        return dateMessage;
    }

    public String getType() {
        return type;
    }

    public void setDateMessage(Date dateMessage) {
        this.dateMessage = dateMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Messages messages = (Messages) o;

        if (idMessage != messages.idMessage) return false;
        if (chatID != messages.chatID) return false;
        if (userFrom != null ? !userFrom.equals(messages.userFrom) : messages.userFrom != null)
            return false;
        if (message != null ? !message.equals(messages.message) : messages.message != null) return false;
        return !(dateMessage != null ? !dateMessage.equals(messages.dateMessage) : messages.dateMessage != null);

    }

    @Override
    public int hashCode() {
        int result = userFrom != null ? userFrom.hashCode() : 0;
        result = 31 * result + idMessage;
        result = 31 * result + chatID;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (dateMessage != null ? dateMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "userFromUUID='" + userFrom + '\'' +
                ", chatID=" + chatID +
                ", message='" + message + '\'' +
                ", currentUserMessage=" + currentUserMessage +
                ", newMessage=" + newMessage +
                ", type=" + type +
                '}';
    }
}
