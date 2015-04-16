package com.gist.github.xMIFx.StoreHouse.dao.Impl;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Messages;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.MessageDao;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by bukatinvv on 16.04.2015.
 */
public class MessagesDaoJdbc implements MessageDao {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Messages> selectAllMessageToUser(String userUUID) {
        return null;
    }

    @Override
    public List<Messages> selectAllMessageFromUser(String userUUID) {
        return null;
    }

    @Override
    public List<Messages> selectAllMessageBetweenUsers(String userToUUID, String userFromUUID) {
        return null;
    }

    @Override
    public List<Messages> selectLastMessageBetweenUsers(String userToUUID, String userFromUUID) {
        return null;
    }

    @Override
    public int saveMessages(Messages message) {
        return 0;
    }


}
