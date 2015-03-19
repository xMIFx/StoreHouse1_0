package com.gist.github.xMIFx.StoreHouse.ServletApi.Filters;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Vlad on 15.03.2015.
 */
@WebFilter("/*")
public class MainFilter implements Filter {
    private FilterConfig filterConfig;
    private static final String COOKIE_NAME = "user";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        Cookie cookieFromClient = null;
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
            req.getSession().setAttribute("user", user);
        }
        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
