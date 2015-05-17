package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Consts.UserConstant;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class Messages {
    private class UsersTo {
        private User userTo;
        private boolean itNewMessage;
        private boolean itDeleted;

        public UsersTo(User userTo, boolean itNewMessage, boolean itDeleted) {
            this.userTo = userTo;
            this.itNewMessage = itNewMessage;
            this.itDeleted = itDeleted;
        }

        public User getUserTo() {
            return userTo;
        }

        public void setUserTo(User userTo) {
            this.userTo = userTo;
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

            return !(userTo != null ? !userTo.equals(usersTo.userTo) : usersTo.userTo != null);

        }

        @Override
        public int hashCode() {
            return userTo != null ? userTo.hashCode() : 0;
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
    private List<User> usersWhichDontRead; //For Web. every time new
    private String UUIDFromBrowser;
    private final String type = "Messages"; //for json

    public Messages() {
    }

    public Messages(User userFrom, int chatID, int id, String message, boolean newMessage, Date dateMessage, String UUIDFromBrowser) {
        this.userFrom = userFrom;
        this.chatID = chatID;
        this.message = message;
        this.idMessage = id;
        this.newMessage = newMessage;
        this.dateMessage = dateMessage;
        this.UUIDFromBrowser = UUIDFromBrowser;
    }

    public Messages(User userFrom, int chatID, String message, boolean newMessage, Date dateMessage, String UUIDFromBrowser) {
        this.userFrom = userFrom;
        this.chatID = chatID;
        this.message = message;
        this.newMessage = newMessage;
        this.dateMessage = dateMessage;
        this.UUIDFromBrowser = UUIDFromBrowser;
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

    public String getUUIDFromBrowser() {
        return UUIDFromBrowser;
    }

    public void setUUIDFromBrowser(String UUIDFromBrowser) {
        this.UUIDFromBrowser = UUIDFromBrowser;
    }

    @JsonIgnore
    public Set<String> getUsersTo() {
        if (usersTo == null) {
            usersTo = new HashSet<>();
        }
        Set<String> usersUUIDTo = new HashSet<>();
        for (UsersTo userTo : usersTo) {
            usersUUIDTo.add(userTo.getUserTo().getUuid());
        }
        return usersUUIDTo;
    }

    @JsonIgnore
    public boolean isNewMessageForUserUUID(String userUUID) {
        boolean newMes = false;
        for (UsersTo us : usersTo) {
            if (us.getUserTo().getUuid().equals(userUUID)) {
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
            if (us.getUserTo().getUuid().equals(userUUID)) {
                deletedMes = us.isItDeleted();
                break;
            }

        }
        return deletedMes;
    }

    public List<User> getUsersWhichDontRead() {
        List<User> usersWhichDontReadNew = new ArrayList<>();
        if (usersTo != null) {
            for (UsersTo userTo : usersTo) {
                if (userTo.isItNewMessage()) {
                    usersWhichDontReadNew.add(userTo.getUserTo());
                }
            }
        }
        return usersWhichDontReadNew;
    }

    public void addUserTo(User user, boolean isNewMassage, boolean isDeleted) {
        if (usersTo == null) {
            usersTo = new HashSet<>();
        }
        if (!user.getUuid().equals(userFrom.getUuid())) {
            usersTo.add(new UsersTo(user, isNewMassage, isDeleted));
        }
    }

    public void addAllUserTo(List<User> allUsersTo, boolean isNewMassage, boolean isDeleted) {
        if (usersTo == null) {
            usersTo = new HashSet<>();
        }
        for (User user : allUsersTo) {
            if (!user.getUuid().equals(userFrom.getUuid())) {
                usersTo.add(new UsersTo(user, isNewMassage, isDeleted));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Messages messages = (Messages) o;

        if (idMessage != messages.idMessage) return false;
        return !(UUIDFromBrowser != null ? !UUIDFromBrowser.equals(messages.UUIDFromBrowser) : messages.UUIDFromBrowser != null);

    }

    @Override
    public int hashCode() {
        int result = idMessage;
        result = 31 * result + (UUIDFromBrowser != null ? UUIDFromBrowser.hashCode() : 0);
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
