package com.gist.github.xMIFx.StoreHouse.WebSockets;

/**
 * Created by bukatinvv on 02.04.2015.
 */

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Chats;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Crypting.AesException;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.ChatDao;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.InterfaceMenuDao;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/messenger.do/chat", configurator = EndpointConfiguratorChat.class)
public class MessengerSocket extends DependenceInjectionServlet {
    private static final String COOKIE_FOR_WEBSOCKET = "curentUser";

    private static Map<String, Session> usersWebSocketSession = Collections.synchronizedMap(new HashMap<String, Session>());

    private EndpointConfig config;

    @Inject("txManager")
    private TransactionManager txManager;

    @Inject("chatsDao")
    private ChatDao chatsDao;


    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.config = config;
        try {
            init();
        } catch (ServletException e) {
            createSendMessageAboutException(session);
        }
        usersWebSocketSession.put((String) config.getUserProperties().get(COOKIE_FOR_WEBSOCKET), session);
    }

    @OnClose
    public void onClose(Session session) {
        usersWebSocketSession.remove(this.config.getUserProperties().get(COOKIE_FOR_WEBSOCKET));
    }

    @OnMessage
    public void echoTextMessage(Session session, String msg) {
        parseMessageFromJson(session, msg);

    }

    private void parseMessageFromJson(Session session, String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> myMap = null;
        try {
            myMap = objectMapper.readValue(msg, new TypeReference<HashMap<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (myMap != null && myMap.get("type").equals("Messages")) {
            sendMessage(session, msg);
        } else if (myMap != null && myMap.get("type").equals("Chat")) {
            try {
                String userToUUID = User.decryptUUID(myMap.get("userTo"));
                Chats chat = txManager.doInTransaction(() -> chatsDao.getChatBetweenUsers((String) config.getUserProperties().get(COOKIE_FOR_WEBSOCKET), userToUUID));
                sendMessageAboutChat(session,chat);
            } catch (AesException e) {
                createSendMessageAboutException(session);
                e.printStackTrace();
            } catch (SQLException e) {
                createSendMessageAboutException(session);
                e.printStackTrace();
            }

        }
    }

    private void sendMessageAboutChat(Session session, Chats chat) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonStr = mapper.writeValueAsString(chat);
           System.out.println(jsonStr);
            session.getBasicRemote().sendText(jsonStr);
        } catch (IOException e) {
            createSendMessageAboutException(session);
            e.printStackTrace();
        }

    }

    private void createSendMessageAboutException(Session session) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "Exception");
        node.put("value", "sorry! try later");
        String jsonStr = node.toString();
        try {
            session.getBasicRemote().sendText(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(Session session, String msg) {
        for (Map.Entry<String, Session> pair : usersWebSocketSession.entrySet()) {

            try {
                if (pair.getValue().isOpen() && pair.getValue() != session) {
                    pair.getValue().getBasicRemote().sendText(msg, true);
                }
            } catch (IOException e) {
                try {
                    pair.getValue().close();
                    e.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public static void informAboutOnlineStatusOfUser(User user) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "User");
        node.put("id", user.getId());
        node.put("online", user.isOnline());
        String jsonStr = node.toString();
        System.out.println(jsonStr);
        for (Map.Entry<String, Session> pair : usersWebSocketSession.entrySet()) {
            System.out.println("beginning sending");
            // if consume changed online status, we need inform only for user with consumeVisible
            if (user.getRole().getiD() == UserDao.getAllUser().get(User.getEmptyUUID()).getRole().getiD()
                    && !UserDao.getAllUser().get(pair.getKey()).isConsumeVisible()) {
                System.out.println("message not sending");
                continue;
            }
            // if user changed online status with out consumeVisible, we don't need inform consumers
            if (!user.isConsumeVisible()
                    && UserDao.getAllUser().get(User.getEmptyUUID()).getRole().getiD()
                    == UserDao.getAllUser().get(pair.getKey()).getRole().getiD()) {
                System.out.println("message not sending");
                continue;
            }

            try {
                System.out.println("message sending");
                pair.getValue().getBasicRemote().sendText(jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("end sending");

    }
}
