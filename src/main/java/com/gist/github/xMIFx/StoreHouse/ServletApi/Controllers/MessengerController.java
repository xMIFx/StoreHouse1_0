package com.gist.github.xMIFx.StoreHouse.ServletApi.Controllers;

import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bukatinvv on 03.04.2015.
 */
@WebServlet("/messenger.do")
public class MessengerController  extends DependenceInjectionServlet{
    private static final String COOKIE_NAME = "user";
    private static final String ATTRIBUTE_MENU_TO_VIEW = "UserList";
    private static final String PAGE_OK = "pages/messenger.jsp";
    private static final String PAGE_ERROR = "pages/errorPage.jsp";
    @Inject("txManager")
    private TransactionManager txManager;

    @Inject("userDao")
    private UserDao userDao;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       req.getRequestDispatcher(PAGE_OK).forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
