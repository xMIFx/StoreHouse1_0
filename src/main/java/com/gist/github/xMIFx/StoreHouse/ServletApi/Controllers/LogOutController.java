package com.gist.github.xMIFx.StoreHouse.ServletApi.Controllers;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Vlad on 13.03.2015.
 */
@WebServlet("/exit.do")
public class LogOutController extends DependenceInjectionServlet {
    private static final String COOKIE_USER = "user";
    private static final String PAGE_OK = "/index.do";
    private static final String PAGE_ERROR = "pages/errorPage.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Cookie userCookie = null;
            Cookie[] cookies = req.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_USER)) {
                    userCookie = cookie;
                    break;
                }
            }
            if (userCookie != null) {
                userCookie.setValue("");
                userCookie.setMaxAge(0);
                resp.addCookie(userCookie);
            }
            User user = (User) req.getSession().getAttribute(COOKIE_USER);
            if (user != null) {
                if (!user.getUuid().equals(User.getEmptyUUID())) {
                    UserDao.getAllUser().get(user.getUuid()).setOnline(false);
                }
                req.getSession().setAttribute(COOKIE_USER, null);
            }
            resp.sendRedirect(PAGE_OK);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(PAGE_ERROR);
        }
    }
}
