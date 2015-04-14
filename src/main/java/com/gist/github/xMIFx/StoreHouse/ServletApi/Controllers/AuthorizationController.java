package com.gist.github.xMIFx.StoreHouse.ServletApi.Controllers;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.ImplSQL.UnitOfWork;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Impl.InterfaceMenuDaoJdbc;
import com.gist.github.xMIFx.StoreHouse.dao.Impl.UserDaoJdbc;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Vlad on 17.02.2015.
 */
@WebServlet("/authorization.do")
public class AuthorizationController extends DependenceInjectionServlet {
    private static final String COOKIE_NAME = "user";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String PAGE_OK_FIRST = "pages/login.jsp";
    private static final String PAGE_OK_LOGIN = "/index.do";
    private static final String PAGE_ERROR = "pages/errorPage.jsp";
    @Inject("txManager")
    private TransactionManager txManager;

    @Inject("userDao")
    private UserDao userDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher(PAGE_OK_FIRST).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String enteredUserName = req.getParameter(LOGIN);
        String enteredPassword = req.getParameter(PASSWORD);
        try {
            User user = txManager.doInTransaction(() -> userDao.selectByLoginPassword(enteredUserName, enteredPassword));
            user = UserDao.getAllUser().get(user.getUuid());
            user.setOnline(true);
            Cookie userCookie = new Cookie(COOKIE_NAME, user.getUuid());
            userCookie.setMaxAge(3600);
            resp.addCookie(userCookie);
            req.getSession().setAttribute(COOKIE_NAME, user);
            resp.sendRedirect(PAGE_OK_LOGIN);

        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect(PAGE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(PAGE_ERROR);
        }

    }
}
