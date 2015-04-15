package com.gist.github.xMIFx.StoreHouse.dao.Interfaces;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Entity.Interfaces.AllObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vlad on 17.02.2015.
 */
public interface UserDao extends AllObject {
    Map<String, User> allUser = new ConcurrentHashMap<>();

    List<User> selectAll() throws SQLException;

    User selectById(int id);

    User selectByLogin(String login);

    User selectByUUID(String uuid) throws SQLException;

    User selectByLoginPassword(String login, String pas) throws SQLException;

    User createSimpleUser() throws SQLException;

    List<User> selectByName(String name);

     void setAllUser() throws SQLException;

    static Map<String, User> getAllUser() {
        return allUser;
    }


}
