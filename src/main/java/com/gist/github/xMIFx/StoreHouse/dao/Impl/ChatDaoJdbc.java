package com.gist.github.xMIFx.StoreHouse.dao.Impl;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Chats;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Messages;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.ChatDao;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
    public Chats getChatByID(int chatID) {
        return null;
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
        try (Statement st = con.createStatement();
             ResultSet resultSet = st.executeQuery("select\n" +
                     "selectedChat.idChat\n" +
                     ",chats.name nameChat\n" +
                     ",messages.UserFrom\n" +
                     ",iFNull(messages.id,0) idMessage\n" +
                     ",messages.new\n" +
                     ",messages.message\n" +
                     ",messages.dateMessage\n" +
                     "from\n" +
                     "(SELECT \n" +
                     "chatsusers.idChat\n" +
                     ",count(distinct chatsusers.user) countUser \n" +
                     "FROM storehouse.chatsusers chatsusers\n" +
                     "Where chatsusers.user = '" + user1 + "'\n" +
                     "or chatsusers.user = '" + user2 + "'\n" +
                     "group by chatsusers.idChat\n" +
                     "having count(distinct chatsusers.user)=2) as selectedChat\n" +
                     "inner join storehouse.chats chats\n" +
                     "on selectedChat.idChat = chats.id\n" +
                     "left join storehouse.messages messages\n" +
                     "on selectedChat.idChat =  messages.idChat\n" +
                     "order by messages.dateMessage\n" +
                     "LIMIT 15;")) {


            while (resultSet.next()) {

                  if (chat == null) {
                    chat = new Chats();
                    chat.addUser(UserDao.getAllUser().get(user1));
                    chat.addUser(UserDao.getAllUser().get(user2));
                    chat.setNameChat(resultSet.getString("nameChat"));
                    chat.setIdChat(resultSet.getInt("idChat"));
                }
                if (resultSet.getInt("idMessage") != 0) {
                    Messages message = new Messages(UserDao.getAllUser().get(resultSet.getString("UserFrom"))
                            , resultSet.getInt("idChat")
                            , resultSet.getInt("idMessage")
                            , resultSet.getString("message")
                            , resultSet.getBoolean("new")
                            , resultSet.getDate("dateMessage"));
                    chat.addMessage(message);
                }
            }
            if (chat == null) {
                chat = new Chats();
                chat.addUser(UserDao.getAllUser().get(user1));
                chat.addUser(UserDao.getAllUser().get(user2));
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
        int autoIncKeyId = -1;
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("INSERT INTO `storehouse`.`messages` (`idChat`, `UserFrom`, `new`, `message`, `dateMessage`) " +
                    "VALUES ('"+mes.getChatID()+"', '"+mes.getUserFrom().getUuid()+"', '"+mes.isNewMessage()+"'', '"+mes.getMessage()+"', '"+mes.getDateMessage()+"');"
                    , Statement.RETURN_GENERATED_KEYS);

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    autoIncKeyId = rs.getInt(1);
                }
            }
            if (autoIncKeyId == -1) {
                throw new SQLException("can't get iD");
            }

        }
        return autoIncKeyId;
    }

}
