package com.gist.github.xMIFx.StoreHouse.ServletApi.Controllers;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.MessengerGroup;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Consts.UserConstant;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bukatinvv on 03.04.2015.
 */
@WebServlet("/messenger.do")
public class MessengerController extends DependenceInjectionServlet {
    private static final String COOKIE_NAME = "user";
    private static final String COOKIE_NAME_FOR_CHAT = "chat";
    private static final String ATTRIBUTE_GROUPS_TO_VIEW = "groupMap";
    private static final String PAGE_OK = "pages/messenger.jsp";
    private static final String PAGE_ERROR = "pages/errorPage.jsp";
    @Inject("txManager")
    private TransactionManager txManager;

    @Inject("userDao")
    private UserDao userDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User currentUser = (User) req.getSession().getAttribute(COOKIE_NAME);
            Cookie userCookie = new Cookie(COOKIE_NAME_FOR_CHAT, currentUser.getCryptUUID());
            userCookie.setMaxAge(3600);
            resp.addCookie(userCookie);
            List<User> userList;
            Map<MessengerGroup, List<User>> groupMap = new HashMap<>();
            userList = new ArrayList<>();
            userList.addAll(UserConstant.getUserConst().getAllUser().values());

            //if user not registered, he can't send messages. Redirect on page with question/answer in future!!!
            if (userList == null) {/*NOP*/} else {
                for (User user : userList) {
                    if (user.equals(currentUser) || user.getUuid().equals(User.getEmptyUUID()) || user.markForDelete) {
                        continue;
                    }
                    /*If current user is consume he can see only some people
                    * User.getEmptyUUID()).getRole().getiD() - it's role of consume*/
                    if ((currentUser.getUuid().equals(User.getEmptyUUID()) ||
                            currentUser.getRole().getiD() == UserConstant.getUserConst().getAllUser().get(User.getEmptyUUID()).getRole().getiD())
                            && !user.isConsumeVisible()) {
                        continue;
                    }
                    // Only user with consumeVisible = true can see consumers
                    if (!currentUser.isConsumeVisible() &&
                            user.getRole().getiD() == UserConstant.getUserConst().getAllUser().get(User.getEmptyUUID()).getRole().getiD()) {
                        continue;
                    }

                    if (groupMap.containsKey(user.getMessengerGroup())) {
                        groupMap.get(user.getMessengerGroup()).add(user);
                    } else {
                        ArrayList<User> ar = new ArrayList<>();
                        ar.add(user);
                        groupMap.put(user.getMessengerGroup(), ar);
                    }
                }
            }
            req.setAttribute(ATTRIBUTE_GROUPS_TO_VIEW, groupMap);
            req.getRequestDispatcher(PAGE_OK).forward(req, resp);
        } catch (SQLException e) {
            resp.sendRedirect(PAGE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(PAGE_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
