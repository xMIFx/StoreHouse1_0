package com.gist.github.xMIFx.StoreHouse.dao.Impl;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Chats;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Messages;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Consts.UserConstant;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.ChatDao;


import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class ChatDaoJdbc implements ChatDao {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int saveNewChat(Chats chat) throws SQLException {
        Connection connection = dataSource.getConnection();
        int autoIncKeyId = -1;
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("INSERT INTO `storehouse`.`chats` (`name`) VALUES ('" + chat.getNameChat() + "');"
                    , Statement.RETURN_GENERATED_KEYS);

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    autoIncKeyId = rs.getInt(1);
                }
            }
            if (autoIncKeyId == -1) {
                throw new SQLException("can't get iD");
            }
            for (User user : chat.getUserList()) {
                st.executeUpdate("INSERT INTO `storehouse`.`chatsusers` (`idChat`, `user`) " +
                        "VALUES ('" + autoIncKeyId + "', '" + user.getUuid() + "');");

            }

            for (Messages message : chat.getMessagesList()) {
                if (message.getIdMessage() == 0) {
                    message.setIdMessage(saveMessage(message));
                }
            }
        }
        return autoIncKeyId;
    }

    @Override
    public Chats getChatByID(int chatID, String currentUser) throws SQLException {
        Chats chat = null;
        int allMessagesCount = 0;
        int countMessage = 0;
        Connection con = dataSource.getConnection();
        Map<Integer, Messages> messagesMap = new HashMap<>();
        /*Map<String, User> userMap = new HashMap<>();*/
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("Select\n" +
                     "allMessagesInChat.idMessage\n" +
                     ",allMessagesInChat.idChat\n" +
                     ",chats.name nameChat\n" +
                     ",allMessagesInChat.message\n" +
                     ",allMessagesInChat.UserFrom\n" +
                     ",allMessagesInChat.dateMessage\n" +
                     ",messagestouser.userUUID userToUUID\n" +
                     ",messagestouser.newMes newMessage\n" +
                     ",messagestouser.markToDelete\n" +
                     ",allMessageCountTable.countAllMess\n" +
                     ",allMessagesInChat.markForDeleteUserFrom\n" +
                     "from\n" +
                     "(select\n" +
                     "messages.id idMessage\n" +
                     ",messages.idChat\n" +
                     ",messages.message\n" +
                     ",messages.UserFrom\n" +
                     ",messages.dateMessage\n" +
                     ",messagestouser.userUUID UserToUUID\n" +
                     ",messages.markForDeleteUserFrom\n" +
                     ",case When messagestouser.userUUID is null then\n" +
                     "messages.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end deletemark\n" +
                     "from\n" +
                     "storehouse.messages messages\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on messagestouser.userUUID = '"+currentUser+"'\n" +
                     "and messages.id = messagestouser.idmessage\n" +
                     "where idChat ="+chatID+" \n" +
                     "and case When messagestouser.userUUID is null then\n" +
                     "messages.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end =0\n" +
                     "order by messages.dateMessage DESC\n" +
                     "LIMIT 15) as allMessagesInChat\n" +
                     "inner join storehouse.chats chats\n" +
                     "on chats.id = allMessagesInChat.idChat\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on allMessagesInChat.idMessage = messagestouser.idmessage\n" +
                     "left join (select\n" +
                     "count(distinct m.id) countAllMess\n" +
                     ",m.idChat\n" +
                     ",case When messagestouser.userUUID is null then\n" +
                     "m.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end deletemark\n" +
                     "from\n" +
                     "storehouse.messages m\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on messagestouser.userUUID = '"+currentUser+"'\n" +
                     "and m.id = messagestouser.idmessage\n" +
                     "where m.idChat ="+chatID+"\n" +
                     "and case When messagestouser.userUUID is null then\n" +
                     "m.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end =0\n" +
                     "group by m.idChat) as allMessageCountTable\n" +
                     "on allMessagesInChat.idChat = allMessageCountTable.idChat;")) {


            while (resultSet.next()) {
                if (allMessagesCount == 0) {
                    allMessagesCount = resultSet.getInt("countAllMess");
                }
                if (chat == null) {
                    chat = new Chats();
                    chat.setNameChat(resultSet.getString("nameChat"));
                    chat.setIdChat(resultSet.getInt("idChat"));
                }
                if (resultSet.getInt("idMessage") != 0) {
                    boolean itNewMes = false;
                    Messages message = null;
                    if (messagesMap.containsKey(resultSet.getInt("idMessage"))) {
                        message = messagesMap.get(resultSet.getInt("idMessage"));

                    } else {
                        message = new Messages(UserConstant.getUserConst().getAllUser().get(resultSet.getString("UserFrom"))
                                , resultSet.getInt("idChat")
                                , resultSet.getInt("idMessage")
                                , resultSet.getString("message")
                                , resultSet.getBoolean("newMessage")
                                , new Date(resultSet.getTimestamp("dateMessage").getTime()));
                        message.setMarkForDelete(resultSet.getBoolean("markForDeleteUserFrom"));
                        messagesMap.put(message.getIdMessage(), message);
                        itNewMes = true;
                    }
                    message.addUserTo(UserConstant.getUserConst().getAllUser().get(resultSet.getString("userToUUID")), resultSet.getBoolean("newMessage"), resultSet.getBoolean("markToDelete"));
                    if (itNewMes) {
                        chat.addMessage(message);
                        countMessage++;
                    }
                }
               /* if (userMap.containsKey(resultSet.getString("userFromChat"))) {*//*NOP*//*} else {
                    User user = UserConstant.getUserConst().getAllUser().get(resultSet.getString("userFromChat"));
                    userMap.put(user.getUuid(), user);
                    chat.addUser(user);
                }
                if (userMap.containsKey(resultSet.getString("UserFrom"))) {*//*NOP*//*} else {
                    User user = UserConstant.getUserConst().getAllUser().get(resultSet.getString("UserFrom"));
                    userMap.put(user.getUuid(), user);
                    chat.addUser(user);
                }*/
            }
            chat.setIsThereSomeMoreMessages((allMessagesCount > countMessage) ? true : false);
            List<User> usersFromChat = getUsersFromChat(chat.getIdChat());
            for (User userFromChat: usersFromChat){
                chat.addUser(userFromChat);
            }
        }

        return chat;
    }

    @Override
    public List<User> getUsersFromChat(int chatID) throws SQLException {
        List<User> usersUUIDList = new ArrayList<>();
        Connection con = dataSource.getConnection();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("SELECT \n" +
                     "chatsusers.idChat\n" +
                     ", chatsusers.user userUUID\n" +
                     " FROM storehouse.chatsusers\n" +
                     " where chatsusers.idChat =" + chatID + ";")) {
            while (resultSet.next()) {
                usersUUIDList.add(UserConstant.getUserConst().getAllUser().get(resultSet.getString("userUUID")));
            }
        }
        return usersUUIDList;
    }

    @Override
    public List<Messages> getAllMessagesByChat(int ID) {
        return null;
    }

    @Override
    public List<Messages> getLastMessagesByChat(int ID) {
        return null;
    }

    @Override
    public Chats getMoreMessagesInChat(int chatID, int countMessageAlreadyInChat, Date minDateInChat, int howMuchNeed, String currentUser) throws SQLException {
        Chats chat = null;
        int allMessagesCount = 0;
        int countMessage = 0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection con = dataSource.getConnection();
        Map<Integer, Messages> messagesMap = new HashMap<>();
       /* Map<String, User> userMap = new HashMap<>();*/
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("Select\n" +
                     "allMessagesInChat.idMessage\n" +
                     ",allMessagesInChat.idChat\n" +
                     ",chats.name nameChat\n" +
                     ",allMessagesInChat.message\n" +
                     ",allMessagesInChat.UserFrom\n" +
                     ",allMessagesInChat.markForDeleteUserFrom\n" +
                     ",allMessagesInChat.dateMessage\n" +
                     ",messagestouser.userUUID userToUUID\n" +
                     ",ifNULL(messagestouser.newMes,0) newMessage\n" +
                     ",messagestouser.markToDelete\n" +
                     ",allMessageCountTable.countAllMess\n" +
                     "from\n" +
                     "(select\n" +
                     "messages.id idMessage\n" +
                     ",messages.idChat\n" +
                     ",messages.message\n" +
                     ",messages.UserFrom\n" +
                     ",messages.markForDeleteUserFrom\n" +
                     ",messages.dateMessage\n" +
                     ",messagestouser.userUUID UserToUUID\n" +
                     ",case When messagestouser.userUUID is null then\n" +
                     "messages.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end deletemark\n" +
                     "from\n" +
                     "storehouse.messages messages\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on messagestouser.userUUID = '" + currentUser + "'\n" +
                     "and messages.id = messagestouser.idmessage\n" +
                     "where idChat =" + chatID + " \n" +
                     "and messages.dateMessage <= '"+dateFormat.format(minDateInChat)+"' \n" +
                     "and case When messagestouser.userUUID is null then\n" +
                     "messages.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end =0\n" +
                     "order by messages.dateMessage DESC\n" +
                     "LIMIT "+howMuchNeed+") as allMessagesInChat\n" +
                     "inner join storehouse.chats chats\n" +
                     "on chats.id = allMessagesInChat.idChat\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on allMessagesInChat.idMessage = messagestouser.idmessage\n" +
                     "left join (\n" +
                     "select\n" +
                     "count(distinct m.id) countAllMess\n" +
                     ",m.idChat\n" +
                     ",case When messagestouser.userUUID is null then\n" +
                     "m.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end deletemark\n" +
                     "from\n" +
                     "storehouse.messages m\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on messagestouser.userUUID = '"+currentUser+"'\n" +
                     "and m.id = messagestouser.idmessage\n" +
                     "where m.idChat ="+chatID+" \n" +
                     "and case When messagestouser.userUUID is null then\n" +
                     "m.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end =0\n" +
                     "group by m.idChat) as allMessageCountTable\n" +
                     "on allMessagesInChat.idChat = allMessageCountTable.idChat;")) {


            while (resultSet.next()) {
                if (allMessagesCount == 0) {
                    allMessagesCount = resultSet.getInt("countAllMess");
                }
                if (chat == null) {
                    chat = new Chats();
                    chat.setNameChat(resultSet.getString("nameChat"));
                    chat.setIdChat(resultSet.getInt("idChat"));
                }
                if (resultSet.getInt("idMessage") != 0) {
                    boolean itNewMes = false;
                    Messages message = null;
                    if (messagesMap.containsKey(resultSet.getInt("idMessage"))) {
                        message = messagesMap.get(resultSet.getInt("idMessage"));

                    } else {
                        message = new Messages(UserConstant.getUserConst().getAllUser().get(resultSet.getString("UserFrom"))
                                , resultSet.getInt("idChat")
                                , resultSet.getInt("idMessage")
                                , resultSet.getString("message")
                                , resultSet.getBoolean("newMessage")
                                , new Date(resultSet.getTimestamp("dateMessage").getTime()));
                        message.setMarkForDelete(resultSet.getBoolean("markForDeleteUserFrom"));
                        messagesMap.put(message.getIdMessage(), message);
                        itNewMes = true;
                    }
                    message.addUserTo(UserConstant.getUserConst().getAllUser().get(resultSet.getString("userToUUID")), resultSet.getBoolean("newMessage"), resultSet.getBoolean("markToDelete"));
                    if (itNewMes) {
                        chat.addMessage(message);
                        countMessage++;
                    }
                }
                /*if (userMap.containsKey(resultSet.getString("userToUUID"))) {*//*NOP*//*} else {
                    User user = UserConstant.getUserConst().getAllUser().get(resultSet.getString("userToUUID"));
                    userMap.put(user.getUuid(), user);
                    chat.addUser(user);
                }
                if (userMap.containsKey(resultSet.getString("UserFrom"))) {*//*NOP*//*} else {
                    User user = UserConstant.getUserConst().getAllUser().get(resultSet.getString("UserFrom"));
                    userMap.put(user.getUuid(), user);
                    chat.addUser(user);
                }*/
            }
            chat.setIsThereSomeMoreMessages((allMessagesCount > (countMessage+countMessageAlreadyInChat)) ? true : false);
            List<User> usersFromChat = getUsersFromChat(chat.getIdChat());
            for (User userFromChat: usersFromChat){
                chat.addUser(userFromChat);
            }
        }

        return chat;
    }

    @Override
    public Chats getChatBetweenUsers(String currentUser, String user2) throws SQLException {
        Chats chat = null;
        int allMessagesCount = 0;
        int countMessage = 0;
        Connection con = dataSource.getConnection();
        Map<Integer, Messages> messagesMap = new HashMap<>();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("Select\n" +
                     "allMessagesInChat.idMessage\n" +
                     ",allMessagesInChat.idChat\n" +
                     ",chats.name nameChat\n" +
                     ",allMessagesInChat.message\n" +
                     ",allMessagesInChat.UserFrom\n" +
                     ",allMessagesInChat.dateMessage\n" +
                     ",messagestouser.userUUID userToUUID\n" +
                     ",ifNULL(messagestouser.newMes,0) newMessage\n" +
                     ",messagestouser.markToDelete\n" +
                     ",allMessageCountTable.countAllMess\n" +
                     "from\n" +
                     "(select\n" +
                     "messages.id idMessage\n" +
                     ",messages.idChat\n" +
                     ",messages.message\n" +
                     ",messages.UserFrom\n" +
                     ",messages.dateMessage\n" +
                     ",messagestouser.userUUID UserToUUID\n" +
                     ",case When messagestouser.userUUID is null then\n" +
                     "messages.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end deletemark\n" +
                     "from\n" +
                     "storehouse.messages messages\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on messagestouser.userUUID = '"+currentUser+"'\n" +
                     "and messages.id = messagestouser.idmessage\n" +
                     "where idChat in (SELECT \n" +
                     "chatsusers.idChat\n" +
                     "FROM storehouse.chatsusers chatsusers\n" +
                     "where chatsusers.idChat in (\n" +
                     "SELECT \n" +
                     "chatsusers.idChat\n" +
                     "FROM storehouse.chatsusers chatsusers\n" +
                     "Where chatsusers.user = '"+user2+"'\n" +
                     "or chatsusers.user = '"+currentUser+"'\n" +
                     "group by chatsusers.idChat\n" +
                     "having count(distinct chatsusers.user)=2)\n" +
                     "group by chatsusers.idChat\n" +
                     "having count(distinct chatsusers.user)=2) \n" +
                     "and case When messagestouser.userUUID is null then\n" +
                     "messages.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end =0\n" +
                     "order by messages.dateMessage DESC\n" +
                     "LIMIT 15) as allMessagesInChat\n" +
                     "inner join storehouse.chats chats\n" +
                     "on chats.id = allMessagesInChat.idChat\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on allMessagesInChat.idMessage = messagestouser.idmessage\n" +
                     "left join (select\n" +
                     "count(distinct m.id) countAllMess\n" +
                     ",m.idChat\n" +
                     ",case When messagestouser.userUUID is null then\n" +
                     "m.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end deletemark\n" +
                     "from\n" +
                     "storehouse.messages m\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on messagestouser.userUUID = '"+currentUser+"'\n" +
                     "and m.id = messagestouser.idmessage\n" +
                     "where m.idChat in (SELECT \n" +
                     "chatsusers.idChat\n" +
                     "FROM storehouse.chatsusers chatsusers\n" +
                     "where chatsusers.idChat in (\n" +
                     "SELECT \n" +
                     "chatsusers.idChat\n" +
                     "FROM storehouse.chatsusers chatsusers\n" +
                     "Where chatsusers.user = '"+currentUser+"'\n" +
                     "or chatsusers.user = '"+user2+"'\n" +
                     "group by chatsusers.idChat\n" +
                     "having count(distinct chatsusers.user)=2)\n" +
                     "group by chatsusers.idChat\n" +
                     "having count(distinct chatsusers.user)=2) \n" +
                     "and case When messagestouser.userUUID is null then\n" +
                     "m.markForDeleteUserFrom\n" +
                     "else\n" +
                     "messagestouser.markToDelete\n" +
                     "end =0\n" +
                     "group by m.idChat) as allMessageCountTable\n" +
                     "on allMessagesInChat.idChat = allMessageCountTable.idChat;")) {


            while (resultSet.next()) {
                if (allMessagesCount == 0) {
                    allMessagesCount = resultSet.getInt("countAllMess");
                }
                if (chat == null) {
                    chat = new Chats();
                    chat.addUser(UserConstant.getUserConst().getAllUser().get(currentUser));
                    chat.addUser(UserConstant.getUserConst().getAllUser().get(user2));
                    chat.setNameChat(resultSet.getString("nameChat"));
                    chat.setIdChat(resultSet.getInt("idChat"));
                }
                if (resultSet.getInt("idMessage") != 0) {
                    boolean itNewMes = false;
                    Messages message = null;
                    if (messagesMap.containsKey(resultSet.getInt("idMessage"))) {
                        message = messagesMap.get(resultSet.getInt("idMessage"));

                    } else {
                        message = new Messages(UserConstant.getUserConst().getAllUser().get(resultSet.getString("UserFrom"))
                                , resultSet.getInt("idChat")
                                , resultSet.getInt("idMessage")
                                , resultSet.getString("message")
                                , resultSet.getBoolean("newMessage")
                                , new Date(resultSet.getTimestamp("dateMessage").getTime()));
                        messagesMap.put(message.getIdMessage(), message);
                        itNewMes = true;
                    }
                    message.addUserTo(UserConstant.getUserConst().getAllUser().get(resultSet.getString("userToUUID")), resultSet.getBoolean("newMessage"), resultSet.getBoolean("markToDelete"));
                    if (itNewMes) {
                        chat.addMessage(message);
                        countMessage++;
                    }
                }

            }
            if (chat == null) {
                chat = new Chats();
                chat.addUser(UserConstant.getUserConst().getAllUser().get(currentUser));
                chat.addUser(UserConstant.getUserConst().getAllUser().get(user2));
                chat.setNameChat("BetweenUsers");
                chat.setIdChat(saveNewChat(chat));
            }
            chat.setIsThereSomeMoreMessages((allMessagesCount > countMessage) ? true : false);
        }

        return chat;
    }

    @Override
    public int saveMessage(Messages mes) throws SQLException {
        Connection connection = dataSource.getConnection();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int autoIncKeyId = -1;
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("INSERT INTO `storehouse`.`messages` (`idChat`, `UserFrom`, `message`, `dateMessage`) " +
                    "VALUES ('" + mes.getChatID() + "', '" + mes.getUserFrom().getUuid() + "', '" + mes.getMessage() + "', '" + dateFormat.format(mes.getDateMessage()) + "');"
                    , Statement.RETURN_GENERATED_KEYS);

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    autoIncKeyId = rs.getInt(1);
                }
            }
            if (autoIncKeyId == -1) {
                throw new SQLException("can't get iD");
            }
            for (String userToUUID : mes.getUsersTo()) {
                st.executeUpdate("INSERT INTO `storehouse`.`messagestouser` (`idmessage`, `userUUID`, `newMes`, `markToDelete`) " +
                        "VALUES ('" + autoIncKeyId + "', '" + userToUUID + "', '" + (mes.isNewMessageForUserUUID(userToUUID) ? 1 : 0) + "', '" + (mes.isDeletedMessageForUserUUID(userToUUID) ? 1 : 0) + "');");
            }

        }
        return autoIncKeyId;
    }

    @Override
    public List<Chats> getLastChats(String userUUID) throws SQLException {

        Connection con = dataSource.getConnection();
        Map<Integer, Chats> chatMap = new HashMap<>();
        List<Chats> chatsList = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("Select\n" +
                     "lastChats.idChat\n" +
                     ",lastChats.chatName\n" +
                     ",chatsusers.user user\n" +
                     "from\n" +
                     "(select \n" +
                     "messages.idChat\n" +
                     ",MAX(messages.dateMessage) dateMessage\n" +
                     ",chats.name as chatName\n" +
                     "from\n" +
                     "(SELECT \n" +
                     "mes.idmessage\n" +
                     "FROM storehouse.messagestouser mes\n" +
                     "where\n" +
                     "mes.markToDelete = 0\n" +
                     "and mes.userUUID = '" + userUUID + "'\n" +
                     "UNION ALL\n" +
                     "Select\n" +
                     "m.idChat\n" +
                     "from\n" +
                     "storehouse.messages m\n" +
                     "where m.UserFrom = '" + userUUID + "'\n" +
                     ") as selectedMessage\n" +
                     "inner join storehouse.messages messages\n" +
                     "on selectedMessage.idmessage = messages.id\n" +
                     "inner join storehouse.chats chats\n" +
                     "on messages.idChat = chats.id\n" +
                     "group by\n" +
                     "messages.idChat\n" +
                     ",chats.name\n" +
                     "order by dateMessage DESC\n" +
                     "limit 10) as lastChats\n" +
                     "inner join storehouse.chatsusers chatsusers\n" +
                     "on lastChats.idChat = chatsusers.idChat\n" +
                     "where chatsusers.user <> '" + userUUID + "'\n" +
                     "group by\n" +
                     "lastChats.idChat\n" +
                     ",chatsusers.user\n" +
                     ",lastChats.chatName;\n")) {

            while (resultSet.next()) {
                Chats chat = null;
                if (chatMap.containsKey(resultSet.getInt("idChat"))) {
                    chat = chatMap.get(resultSet.getInt("idChat"));
                } else {
                    chat = new Chats();
                    chat.setIdChat(resultSet.getInt("idChat"));
                    chat.setNameChat(resultSet.getString("chatName"));
                    chatMap.put(chat.getIdChat(), chat);
                }
                chat.addUser(UserConstant.getUserConst().getAllUser().get(resultSet.getString("user")));
            }
        }
        chatsList.addAll(chatMap.values());
        return chatsList;
    }

    @Override
    public Map<Chats, Integer> getCountNewMassages(String userUUID) throws SQLException {
        Connection con = dataSource.getConnection();
        Map<Integer, Chats> chatMap = new HashMap<>();
        Map<Chats, Integer> usersNewMessage = new HashMap<>();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("Select\n" +
                     "newMessInChat.countMes\n" +
                     ",newMessInChat.chatName\n" +
                     ",chatsusers.user userUUID\n" +
                     ",newMessInChat.idChat\n" +
                     "from\n" +
                     "(select \n" +
                     "messages.idChat\n" +
                     ",count(distinct selectedMessage.idmessage) as countMes\n" +
                     ",chats.name as chatName\n" +
                     "from\n" +
                     "(SELECT \n" +
                     "mes.idmessage\n" +
                     "FROM storehouse.messagestouser mes\n" +
                     "where\n" +
                     "mes.markToDelete = 0\n" +
                     "and mes.newMes = 1\n" +
                     "and mes.userUUID = '" + userUUID + "') as selectedMessage\n" +
                     "inner join storehouse.messages messages\n" +
                     "on selectedMessage.idmessage = messages.id\n" +
                     "inner join storehouse.chats chats\n" +
                     "on messages.idChat = chats.id\n" +
                     "group by\n" +
                     "messages.idChat\n" +
                     ",chats.name) as newMessInChat\n" +
                     "inner join storehouse.chatsusers chatsusers\n" +
                     "on newMessInChat.idChat = chatsusers.idChat\n" +
                     //"where chatsusers.user <> '" + userUUID + "'\n" +
                     "group by\n" +
                     "newMessInChat.countMes\n" +
                     ",newMessInChat.chatName\n" +
                     ",chatsusers.user\n" +
                     ",newMessInChat.idChat;\n")) {

            while (resultSet.next()) {
                Chats chat = null;
                if (chatMap.containsKey(resultSet.getInt("idChat"))) {
                    chat = chatMap.get(resultSet.getInt("idChat"));
                } else {

                    chat = new Chats();
                    chat.setIdChat(resultSet.getInt("idChat"));
                    chat.setNameChat(resultSet.getString("chatName"));
                    chatMap.put(chat.getIdChat(), chat);
                }
                chat.addUser(UserConstant.getUserConst().getAllUser().get(resultSet.getString("userUUID")));
                if (!usersNewMessage.containsKey(chat)) {
                    usersNewMessage.put(chat, resultSet.getInt("countMes"));
                }
            }
        }
        return usersNewMessage;
    }

    @Override
    public List<Chats> getAllBigChatsByUser(String userUUID) throws SQLException {
        Connection con = dataSource.getConnection();
        Map<Integer, Chats> chatMap = new HashMap<>();
        List<Chats> chatsList = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("select\n" +
                     "bigChats.idChat\n" +
                     ",chats.name chatName\n" +
                     ",chatsusers.user\n" +
                     "from\n" +
                     "(SELECT \n" +
                     "chatsusers.idChat\n" +
                     ",count(distinct chatsusers.user) userCount\n" +
                     " FROM storehouse.chatsusers chatsusers\n" +
                     " where chatsusers.idChat in \n" +
                     " (Select chatsusers.idChat \n" +
                     " from storehouse.chatsusers \n" +
                     " where chatsusers.user ='" + userUUID + "'\n" +
                     " group by chatsusers.idChat)\n" +
                     " group by\n" +
                     " chatsusers.idChat\n" +
                     " having count(distinct chatsusers.user)>2) as bigChats\n" +
                     " inner join storehouse.chatsusers chatsusers\n" +
                     " on bigChats.idChat = chatsusers.idChat\n" +
                     " inner join storehouse.chats chats \n" +
                     " on bigChats.idChat = chats.id;")) {

            while (resultSet.next()) {
                Chats chat = null;
                if (chatMap.containsKey(resultSet.getInt("idChat"))) {
                    chat = chatMap.get(resultSet.getInt("idChat"));
                } else {
                    chat = new Chats();
                    chat.setIdChat(resultSet.getInt("idChat"));
                    chat.setNameChat(resultSet.getString("chatName"));
                    chatMap.put(chat.getIdChat(), chat);
                }
                chat.addUser(UserConstant.getUserConst().getAllUser().get(resultSet.getString("user")));
            }
        }
        chatsList.addAll(chatMap.values());
        return chatsList;
    }

}
