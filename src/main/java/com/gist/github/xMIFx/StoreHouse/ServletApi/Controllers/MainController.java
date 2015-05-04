package com.gist.github.xMIFx.StoreHouse.ServletApi.Controllers;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.InterfaceMenu;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.UserInterface;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Consts.UserConstant;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Impl.InterfaceMenuDaoJdbc;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Vlad on 07.03.2015.
 */
@WebServlet("/index.do")
public class MainController extends DependenceInjectionServlet {
    private static final String COOKIE_NAME = "user";
    private static final String ATTRIBUTE_MENU_TO_VIEW = "menuList";
    private static final String PAGE_OK = "pages/main.jsp";
    private static final String PAGE_ERROR = "pages/errorPage.jsp";

    @Inject("txManager")
    private TransactionManager txManager;

    @Inject("userDao")
    private UserDao userDao;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute(COOKIE_NAME);
        if (user == null) {
            final Cookie cookieFromClientFinal;
            Cookie cookieFromClient = null;
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cook : cookies) {
                    if (cook.getName().equals(COOKIE_NAME)) {
                        cookieFromClient = cook;
                        break;
                    }
                }
            }
            cookieFromClientFinal = cookieFromClient;
            try {

                if (cookieFromClientFinal != null) {
                    user = UserConstant.getUserConst().getAllUser().get(cookieFromClientFinal.getValue());

                           /*user = txManager.doInTransaction(() -> userDao.selectByUUID(cookieFromClientFinal.getValue()));*/
                } else {
                    /*user = txManager.doInTransaction(() -> userDao.createSimpleUser());*/
                    user = UserConstant.getUserConst().getAllUser().get(User.getEmptyUUID());
                }
                user.setOnline(true); // mark online
                req.getSession().setAttribute(COOKIE_NAME, user);
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendRedirect(PAGE_ERROR);
            }
        }
        if (user != null) {
            /*UserDao.getAllUser().get(user.getUuid()).setOnline(true); // mark online*/
            req.setAttribute(ATTRIBUTE_MENU_TO_VIEW, user.getUserInterface().getMenuList());
            req.getRequestDispatcher(PAGE_OK).forward(req, resp);
        }
    }
}


