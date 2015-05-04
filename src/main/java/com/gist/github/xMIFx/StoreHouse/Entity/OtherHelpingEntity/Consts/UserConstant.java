package com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Consts;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionClass;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vlad on 04.05.2015.
 */
public final class UserConstant extends DependenceInjectionClass {

    private Map<String, User> allUser;
    private static final UserConstant userConstant = new UserConstant();
    @Inject("userDao")
    private UserDao userDao;

    private UserConstant() {
        try {
            initialize();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static UserConstant getUserConst() {
        return userConstant;
    }

    public Map<String, User> getAllUser() throws SQLException {

        if (allUser == null) {
            allUser = new ConcurrentHashMap<>();
        }
        if (allUser.isEmpty()) {
            try {
                userDao.setAllUser(allUser);
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return allUser;
    }

    public void setAllUser() throws SQLException {
        try {
            userDao.setAllUser(allUser);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
