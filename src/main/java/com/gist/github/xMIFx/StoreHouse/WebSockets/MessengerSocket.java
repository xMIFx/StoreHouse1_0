package com.gist.github.xMIFx.StoreHouse.WebSockets;

/**
 * Created by bukatinvv on 02.04.2015.
 */

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Chats;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.Messages;
import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Consts.UserConstant;
import com.gist.github.xMIFx.StoreHouse.Entity.OtherHelpingEntity.Crypting.AesException;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionClass;
import com.gist.github.xMIFx.StoreHouse.Injects.DependenceInjectionServlet;
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.ChatDao;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.InterfaceMenuDao;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import sun.misc.resources.Messages_es;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/messenger.do/chat", configurator = EndpointConfiguratorChat.class)
public class MessengerSocket extends DependenceInjectionClass {
    private static final String COOKIE_FOR_WEBSOCKET = "curentUser";
    private static Map<String, Session> usersWebSocketSession = new ConcurrentHashMap<String, Session>();

    private EndpointConfig config;

    @Inject("txManager")
    private TransactionManager txManager;

    @Inject("chatsDao")
    private ChatDao chatsDao;


    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.config = config;
        try {
            initialize();
        } catch (IllegalAccessException e) {
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
        System.out.println(msg);
        if (myMap != null && myMap.get("type").equals("Messages")) {
            sendMessage(session, msg, myMap);
        } else if (myMap != null && myMap.get("type").equals("Chat")) {
            try {
                String userToUUID = User.decryptUUID(myMap.get("userTo"));
                Chats chat = txManager.doInTransaction(() -> chatsDao.getChatBetweenUsers((String) config.getUserProperties().get(COOKIE_FOR_WEBSOCKET), userToUUID));
                sendMessageAboutChat(session, chat);
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

    public void sendMessage(Session session, String msg, Map<String, String> myMap) {
        try {
            Messages newMessage = new Messages(
                    UserConstant.getUserConst().getAllUser().get(User.decryptUUID(myMap.get("userFrom"))),
                    Integer.valueOf(myMap.get("chatID")),
                    myMap.get("message"),
                    Boolean.valueOf(myMap.get("newMessage")),
                    new Date(Long.valueOf(myMap.get("dateMessage"))));
            newMessage.setIdMessage(txManager.doInTransaction(() -> chatsDao.saveMessage(newMessage)));
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(newMessage);
            //session.getBasicRemote().sendText(jsonStr);
            List<String> allUsersFromChat = txManager.doInTransaction(() -> chatsDao.getUsersFromChat(newMessage.getChatID()));
            for (String userUUID : allUsersFromChat) {
                if (usersWebSocketSession.containsKey(userUUID)) {
                    usersWebSocketSession.get(userUUID).getBasicRemote().sendText(jsonStr);

                }
            }


        /*for (Map.Entry<String, Session> pair : usersWebSocketSession.entrySet()) {

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
        }*/
        } catch (SQLException e) {
            e.printStackTrace();
            createSendMessageAboutException(session);
        } catch (AesException e) {
            e.printStackTrace();
            createSendMessageAboutException(session); //need something about message
        } catch (IOException e) {
            e.printStackTrace();
            createSendMessageAboutException(session); //need something about message
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
            try {
                if (user.getRole().getiD() == UserConstant.getUserConst().getAllUser().get(User.getEmptyUUID()).getRole().getiD()
                        && !UserConstant.getUserConst().getAllUser().get(pair.getKey()).isConsumeVisible()) {
                    System.out.println("message not sending");
                    continue;
                }
                // if user changed online status with out consumeVisible, we don't need inform consumers
                if (!user.isConsumeVisible()
                        && UserConstant.getUserConst().getAllUser().get(User.getEmptyUUID()).getRole().getiD()
                        == UserConstant.getUserConst().getAllUser().get(pair.getKey()).getRole().getiD()) {
                    System.out.println("message not sending");
                    continue;
                }
                System.out.println("message sending");
                pair.getValue().getBasicRemote().sendText(jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("end sending");

    }
}
