package com.gist.github.xMIFx.StoreHouse.dao.Interfaces;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Messages;

import java.util.List;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public interface MessageDao {
    List<Messages> selectAllMessageToUser(String userUUID);

    List<Messages> selectAllMessageFromUser(String userUUID);

    List<Messages> selectAllMessageBetweenUsers(String userToUUID, String userFromUUID);

    List<Messages> selectLastMessageBetweenUsers(String userToUUID, String userFromUUID);

    int saveMessages(Messages message);
}
