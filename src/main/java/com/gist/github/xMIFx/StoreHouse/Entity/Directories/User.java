package com.gist.github.xMIFx.StoreHouse.Entity.Directories;

import com.gist.github.xMIFx.StoreHouse.Entity.Interfaces.Directory;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Crypting.AesException;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Crypting.StringCrypter;
import com.google.common.html.HtmlEscapers;

import java.util.Date;

/**
 * Created by Vlad on 17.02.2015.
 */

public class User extends Directory {
    private String password;
    private UserInterface userInterface;
    private UserRoles role;
    public String login;
    public String name;
    public String email;
    public String telephone;
    public Date birthDay;
    public boolean consumeVisible;
    public MessengerGroup messengerGroup;
    public boolean online;
    private final String type = "User"; //for json
    private static final String keyForCrypt = "IKnowNothing";
    private final static StringCrypter crypter = new StringCrypter(keyForCrypt.getBytes());
    private String cryptUUID; //need escape html symbols

    public User(String login, String password, String name) {
        super(Directory.createGuid());
        this.password = password;
        this.login = login;
        this.name = name;
    }

    public User(String login, String password, String name, String email) {
        super(Directory.createGuid());
        this.password = password;
        this.login = login;
        this.name = name;
        this.email = email;


    }

    public User(String uuid, int id, boolean markdel, String login, String password, String name, String email) {
        super(uuid, id, markdel);
        this.password = password;
        this.login = login;
        this.name = name;
        this.email = email;


    }

    public User(String uuid, int id, boolean markdel, String login, String password, String name, String email, String telephone, Date birthDay) {
        super(uuid, id, markdel);
        this.password = password;
        this.login = login;
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.birthDay = birthDay;
    }

    public User(String uuid, int id, boolean markdel, String login, String password, String name, String email, UserInterface userInterface, UserRoles role, String telephone, Date birthDay) {
        super(uuid, id, markdel);
        this.password = password;
        this.login = login;
        this.name = name;
        this.email = email;
        this.role = role;
        this.birthDay = birthDay;
        this.telephone = telephone;
        this.userInterface = userInterface;
    }

    public User(String uuid, int id, boolean markdel, String login, String password, String name, String email, UserInterface userInterface, UserRoles role, String telephone, Date birthDay, boolean consumeVisible) {
        super(uuid, id, markdel);
        this.password = password;
        this.login = login;
        this.name = name;
        this.email = email;
        this.role = role;
        this.birthDay = birthDay;
        this.telephone = telephone;
        this.userInterface = userInterface;
        this.consumeVisible = consumeVisible;
    }

    public User(String uuid, int id, boolean markdel, String login, String password, String name, String email, String telephone, Date birthDay, boolean consumeVisible) {
        super(uuid, id, markdel);
        this.password = password;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthDay = birthDay;
        this.telephone = telephone;
        this.consumeVisible = consumeVisible;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    public UserRoles getRole() {
        return role;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCryptUUID() {
        if (cryptUUID == null) {
            try {
                cryptUUID = HtmlEscapers.htmlEscaper().escape(crypter.encrypt(this.getUuid()));
            } catch (AesException e) {
                cryptUUID = HtmlEscapers.htmlEscaper().escape(this.getUuid());
                e.printStackTrace();
            }
        }
        return cryptUUID;
    }

    public boolean isConsumeVisible() {
        return consumeVisible;
    }

    public void setConsumeVisible(boolean consumeVisible) {
        this.consumeVisible = consumeVisible;
    }

    public MessengerGroup getMessengerGroup() {
        return messengerGroup;
    }

    public void setMessengerGroup(MessengerGroup messengerGroup) {
        this.messengerGroup = messengerGroup;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public static String decryptUUID(String cryptStr) throws AesException {
        String result = null;
        result = crypter.decrypt(cryptStr);
        return result;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = password != null ? password.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "markForDelete='" + markForDelete + '\'' +
                "uuid='" + this.getUuid() + '\'' +
                "password='" + password + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", birthDay=" + birthDay +
                ", consumeVisible=" + consumeVisible +
                ", online=" + online +
                ", userInterface=" + userInterface +
                ", role=" + role +
                ", messengerGroup=" + messengerGroup +
                ", type=" + type +
                '}';
    }
}
