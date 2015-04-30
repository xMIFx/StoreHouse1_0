package com.gist.github.xMIFx.StoreHouse.dao.Interfaces;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Chats;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Messages;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public interface ChatDao {
    int saveNewChat(Chats chat) throws SQLException;
    Chats getChatByID(int chatID);
    List<Messages> getAllMessagesByChat(int ID);
    List<Messages> getLastMessagesByChat(int ID);
    Chats getChatBetweenUsers(String user1,String user2) throws SQLException;
}
