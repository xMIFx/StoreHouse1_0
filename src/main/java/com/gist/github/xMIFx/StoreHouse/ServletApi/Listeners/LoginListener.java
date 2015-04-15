package com.gist.github.xMIFx.StoreHouse.ServletApi.Listeners;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.sql.SQLException;

/**
 * Created by bukatinvv on 03.04.2015.
 */
@WebListener
public class LoginListener implements HttpSessionListener {

    private static final String COOKIE_NAME = "user";

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        System.out.println("created");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        User disconnectUser = (User) httpSessionEvent.getSession().getAttribute(COOKIE_NAME);

        if (disconnectUser != null) {
            if (!UserDao.getAllUser().isEmpty()) {
                UserDao.getAllUser().get(disconnectUser.getUuid()).setOnline(false);//mark offline
            }
            System.out.println("destroyed " + disconnectUser.getName());
        }
    }
}
