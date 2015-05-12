package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class Messages {
    private class UsersTo {
        private String userToUUID;
        private boolean itNewMessage;
        private boolean itDeleted;

        public UsersTo(String userToUUID, boolean itNewMessage, boolean itDeleted) {
            this.userToUUID = userToUUID;
            this.itNewMessage = itNewMessage;
            this.itDeleted = itDeleted;
        }

        public String getUserToUUID() {
            return userToUUID;
        }

        public void setUserToUUID(String userToUUID) {
            this.userToUUID = userToUUID;
        }

        public boolean isItNewMessage() {
            return itNewMessage;
        }

        public void setItNewMessage(boolean itNewMessage) {
            this.itNewMessage = itNewMessage;
        }

        public boolean isItDeleted() {
            return itDeleted;
        }

        public void setItDeleted(boolean itDeleted) {
            this.itDeleted = itDeleted;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UsersTo usersTo = (UsersTo) o;

            return !(userToUUID != null ? !userToUUID.equals(usersTo.userToUUID) : usersTo.userToUUID != null);

        }

        @Override
        public int hashCode() {
            return userToUUID != null ? userToUUID.hashCode() : 0;
        }
    }

    private User userFrom;
    private int idMessage;
    private int chatID;
    private String message;
    private boolean newMessage;
    private boolean markForDelete;
    private Date dateMessage;
    private Set<UsersTo> usersTo;
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

    public Messages(User userFrom, int chatID, String message, boolean newMessage, Date dateMessage) {
        this.userFrom = userFrom;
        this.chatID = chatID;
        this.message = message;
        this.newMessage = newMessage;
        this.dateMessage = dateMessage;
    }

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
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

    public boolean isMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(boolean markForDelete) {
        this.markForDelete = markForDelete;
    }

    @JsonIgnore
    public Set<String> getUsersTo() {
        if (usersTo == null) {
            usersTo = new HashSet<>();
        }
        Set<String> usersUUIDTo = new HashSet<>();
        for (UsersTo userTo : usersTo) {
            usersUUIDTo.add(userTo.getUserToUUID());
        }
        return usersUUIDTo;
    }

    @JsonIgnore
    public boolean isNewMessageForUserUUID(String userUUID) {
        boolean newMes = false;
        for (UsersTo us : usersTo) {
            if (us.getUserToUUID().equals(userUUID)) {
                newMes = us.isItNewMessage();
                break;
            }

        }
        return newMes;
    }

    @JsonIgnore
    public boolean isNewMessageForSomeOne() {
        boolean newMes = false;
        for (UsersTo us : usersTo) {
            if (us.isItNewMessage()) {
                newMes = us.isItNewMessage();
                break;
            }

        }
        return newMes;
    }

    @JsonIgnore
    public boolean isDeletedMessageForUserUUID(String userUUID) {
        boolean deletedMes = true;
        for (UsersTo us : usersTo) {
            if (us.getUserToUUID().equals(userUUID)) {
                deletedMes = us.isItDeleted();
                break;
            }

        }
        return deletedMes;
    }


    public void addUserTo(String userUUID, boolean isNewMassage, boolean isDeleted) {
        if (usersTo == null) {
            usersTo = new HashSet<>();
        }
        if (!userUUID.equals(userFrom.getUuid())) {
            usersTo.add(new UsersTo(userUUID, isNewMassage, isDeleted));
        }
    }

    public void addAllUserTo(List<String> usersToUUID, boolean isNewMassage, boolean isDeleted) {
        if (usersTo == null) {
            usersTo = new HashSet<>();
        }
        for (String userUUID : usersToUUID) {
            if (!userUUID.equals(userFrom.getUuid())) {
                usersTo.add(new UsersTo(userUUID, isNewMassage, isDeleted));
            }
        }
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
                "userFrom=" + userFrom +
                ", idMessage=" + idMessage +
                ", chatID=" + chatID +
                ", message='" + message + '\'' +
                ", newMessage=" + newMessage +
                ", dateMessage=" + dateMessage +
                ", type='" + type + '\'' +
                '}';
    }
}
