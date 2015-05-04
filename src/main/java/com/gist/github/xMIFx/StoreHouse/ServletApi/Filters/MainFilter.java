package com.gist.github.xMIFx.StoreHouse.ServletApi.Filters;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Consts.UserConstant;
import com.gist.github.xMIFx.StoreHouse.Injects.ApplicationContextHolder;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.FieldReflector;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;
import org.springframework.context.ApplicationContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Vlad on 15.03.2015.
 */
@WebFilter("/*")
public class MainFilter extends DependenceInjectionServlet implements Filter {
    private FilterConfig filterConfig;
    private static final String COOKIE_NAME = "user";
    private static final String PAGE_ERROR = "pages/errorPage.jsp";


    @Inject("userDao")
    private UserDao userDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init();
        this.filterConfig = filterConfig;
     }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean ex = false;
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        req.getSession().setMaxInactiveInterval(900);//15 min
        Cookie cookieFromClient = null;
        final Cookie cookieFromClientFinal;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    cookieFromClient = cookie;
                    break;
                }

            }
        }
        User user = (User) req.getSession().getAttribute(COOKIE_NAME);
        if (cookieFromClient != null && user != null && !(user.getUuid().equals(cookieFromClient.getValue()))) {
            cookieFromClient.setMaxAge(0);
            cookieFromClient.setValue(null);
            resp.addCookie(cookieFromClient);
            user = null;
            req.getSession().setAttribute(COOKIE_NAME, user);
        }
        cookieFromClientFinal = cookieFromClient;
        // Session destroyed with attributes, so we need to refresh it
        if (user == null) {
            try {
                if (cookieFromClientFinal != null) {
                    user = UserConstant.getUserConst().getAllUser().get(cookieFromClient.getValue());
                    user.setOnline(true);
                    //  user = txManager.doInTransaction(() -> userDao.selectByUUID(cookieFromClientFinal.getValue()));
                } else {
                    //  user = txManager.doInTransaction(() -> userDao.createSimpleUser());
                    user = UserConstant.getUserConst().getAllUser().get(User.getEmptyUUID());
                }
                req.getSession().setAttribute(COOKIE_NAME, user);
                } catch (SQLException e) {
                e.printStackTrace();
                ex = true;

            }
        }
        if (ex) {
            resp.sendRedirect(PAGE_ERROR);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }


    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
