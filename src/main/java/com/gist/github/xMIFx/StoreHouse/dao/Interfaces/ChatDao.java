package com.gist.github.xMIFx.StoreHouse.dao.Interfaces;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Chats;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Messages;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public interface ChatDao {
    int saveNewChat(Chats chat) throws SQLException;
    Chats getChatByID(int chatID) throws SQLException;
    List<String> getUsersFromChat(int chatID) throws SQLException;
    List<Messages> getAllMessagesByChat(int ID);
    List<Messages> getLastMessagesByChat(int ID);
    Chats getChatBetweenUsers(String user1,String user2) throws SQLException;
    int saveMessage(Messages mes) throws SQLException;
    List<Chats> getLastChats(String userUUID) throws SQLException;
    Map<Chats,Integer> getCountNewMassages(String userUUID) throws SQLException;
    List<Chats> getAllBigChatsByUser(String userUUID) throws SQLException;
}
