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
    public Chats getChatByID(int chatID) throws SQLException {
        Chats chat = null;
        Connection con = dataSource.getConnection();
        Map<Integer, Messages> messagesMap = new HashMap<>();
        Map<String, User> userMap = new HashMap<>();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("select\n" +
                     "chats.id idChat\n" +
                     ",chats.name nameChat\n" +
                     ",messages.UserFrom\n" +
                     ",iFNull(messages.id,0) idMessage\n" +
                     ",messages.message\n" +
                     ",messages.dateMessage\n" +
                     ",messagestouser.userUUID userToUUID\n" +
                     ",iFNull(messagestouser.newMes,0) newMessage\n" +
                     ",iFNull(messagestouser.markToDelete,1) markToDelete\n" +
                     "from\n" +
                     "storehouse.chats chats\n" +
                     "inner join storehouse.messages messages\n" +
                     "on chats.id =  messages.idChat\n" +
                     "and messages.idChat = " + chatID + "\n" +
                     "inner join storehouse.messagestouser messagestouser\n" +
                     "on messages.id = messagestouser.idmessage\n" +
                     "and messages.idChat=" + chatID + "\n" +
                     "where iFNull(messagestouser.markToDelete,1)=0\n" +
                     "and not messagestouser.userUUID is null\n" +
                     "order by messages.dateMessage\n" +
                     "LIMIT 15;")) {


            while (resultSet.next()) {

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
                        messagesMap.put(message.getIdMessage(), message);
                        itNewMes = true;
                    }
                    message.addUserTo(resultSet.getString("userToUUID"), resultSet.getBoolean("newMessage"), resultSet.getBoolean("markToDelete"));
                    if (itNewMes) {
                        chat.addMessage(message);
                    }
                }
                if (userMap.containsKey(resultSet.getString("userToUUID"))) {/*NOP*/} else {
                    User user = UserConstant.getUserConst().getAllUser().get(resultSet.getString("userToUUID"));
                    userMap.put(user.getUuid(), user);
                    chat.addUser(user);
                }
                if (userMap.containsKey(resultSet.getString("UserFrom"))) {/*NOP*/} else {
                    User user = UserConstant.getUserConst().getAllUser().get(resultSet.getString("UserFrom"));
                    userMap.put(user.getUuid(), user);
                    chat.addUser(user);
                }
            }
        }

        return chat;
    }

    @Override
    public List<String> getUsersFromChat(int chatID) throws SQLException {
        List<String> usersUUIDList = new ArrayList<>();
        Connection con = dataSource.getConnection();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("SELECT \n" +
                     "chatsusers.idChat\n" +
                     ", chatsusers.user userUUID\n" +
                     " FROM storehouse.chatsusers\n" +
                     " where chatsusers.idChat =" + chatID + ";")) {
            while (resultSet.next()) {
                usersUUIDList.add(resultSet.getString("userUUID"));
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
    public Chats getChatBetweenUsers(String user1, String user2) throws SQLException {
        Chats chat = null;
        Connection con = dataSource.getConnection();
        Map<Integer, Messages> messagesMap = new HashMap<>();
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("select\n" +
                     "selectedChat.idChat\n" +
                     ",chats.name nameChat\n" +
                     ",messages.UserFrom\n" +
                     ",iFNull(messages.id,0) idMessage\n" +
                     ",messages.message\n" +
                     ",messages.dateMessage\n" +
                     ",messagestouser.userUUID userToUUID\n" +
                     ",iFNull(messagestouser.newMes,0) newMessage\n" +
                     ",iFNull(messagestouser.markToDelete,1) markToDelete\n" +
                     "from\n" +
                     "(SELECT \n" +
                     "chatsusers.idChat\n" +
                     ",count(distinct chatsusers.user) countUser \n" +
                     "FROM storehouse.chatsusers chatsusers\n" +
                     "where chatsusers.idChat in (\n" +
                     "SELECT \n" +
                     "chatsusers.idChat\n" +
                     "FROM storehouse.chatsusers chatsusers\n" +
                     "Where chatsusers.user = '" + user1 + "'\n" +
                     "  or chatsusers.user = '" + user2 + "'\n" +
                     "  group by chatsusers.idChat\n" +
                     "having count(distinct chatsusers.user)=2)\n" +
                     "group by chatsusers.idChat\n" +
                     "having count(distinct chatsusers.user)=2) as selectedChat\n" +
                     "inner join storehouse.chats chats\n" +
                     "on selectedChat.idChat = chats.id\n" +
                     "left join storehouse.messages messages\n" +
                     "on selectedChat.idChat =  messages.idChat\n" +
                     "left join storehouse.messagestouser messagestouser\n" +
                     "on messages.id = messagestouser.idmessage\n" +
                     "where iFNull(messagestouser.markToDelete,1)=0\n" +
                     "and not messagestouser.userUUID is null\n" +
                     "order by messages.dateMessage\n" +
                     "LIMIT 15;")) {


            while (resultSet.next()) {

                if (chat == null) {
                    chat = new Chats();
                    chat.addUser(UserConstant.getUserConst().getAllUser().get(user1));
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
                    message.addUserTo(resultSet.getString("userToUUID"), resultSet.getBoolean("newMessage"), resultSet.getBoolean("markToDelete"));
                    if (itNewMes) {
                        chat.addMessage(message);
                    }
                }

            }
            if (chat == null) {
                chat = new Chats();
                chat.addUser(UserConstant.getUserConst().getAllUser().get(user1));
                chat.addUser(UserConstant.getUserConst().getAllUser().get(user2));
                chat.setNameChat("BetweenUsers");
                chat.setIdChat(saveNewChat(chat));
            }
            ;


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
                     "order by dateMessage\n" +
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
                    chatMap.put(chat.getIdChat(),chat);
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
