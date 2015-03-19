package com.gist.github.xMIFx.StoreHouse.ServletApi.Controllers;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.InterfaceMenu;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.MenuButton;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Impl.InterfaceMenuDaoJdbc;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.InterfaceMenuDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by Vlad on 11.03.2015.
 */
@WebServlet("/menu.do")
public class MenuController extends DependenceInjectionServlet {
    private static final String COOKIE_NAME = "user";
    private static final String PARAM_ID = "id";
    private static final String ATTRIBUTE_BUTTON_TO_VIEW = "buttonList";
    private static final String PAGE_OK = "pages/main.jsp";
    private static final String PAGE_ERROR = "pages/errorPage.jsp";
    @Inject("txManager")
    private TransactionManager txManager;

    @Inject("userInterface")
    private InterfaceMenuDao interfaceMenu;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String stringIDMenu = req.getParameter(PARAM_ID);
        if (stringIDMenu != null) {
            try {
                int iDMenu = Integer.valueOf(stringIDMenu);
                User user = (User) req.getSession().getAttribute(COOKIE_NAME);
                if (user != null) {
                    List<MenuButton> buttonList = null;
                    for (InterfaceMenu menu : user.getUserInterface().getMenuList()) {
                        if (menu.getiD() == iDMenu) {
                            buttonList = menu.getButtonList();
                            break;
                        }
                    }
                    req.setAttribute(ATTRIBUTE_BUTTON_TO_VIEW, buttonList);
                    req.getRequestDispatcher(PAGE_OK).forward(req, resp);
                }
            } catch (Exception e) {
                resp.sendRedirect(PAGE_ERROR);
            }
        } else

        {
            resp.sendRedirect(PAGE_ERROR);
        }
    }

}
