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
import com.gist.github.xMIFx.StoreHouse.Injects.Inject;
import com.gist.github.xMIFx.StoreHouse.SQLPack.TransactionManager;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.ChatDao;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            createSendMessageAboutException(session, "You can't whrite to this chat");
        }
        usersWebSocketSession.put((String) config.getUserProperties().get(COOKIE_FOR_WEBSOCKET), session);
        sendMessageAboutCountNewMessages(session);
    }

    @OnClose
    public void onClose(Session session) {
        usersWebSocketSession.remove(this.config.getUserProperties().get(COOKIE_FOR_WEBSOCKET));
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace(); //ignored for first time
    }

    @OnMessage
    public void echoTextMessage(Session session, String msg) {
        parseMessageFromJson(session, msg);

    }


    private void parseMessageFromJson(Session session, String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> myMap = null;
        try {
            myMap = objectMapper.readValue(msg, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(msg);
        if (myMap != null && myMap.get("type").equals("Messages")) {
            sendMessage(session, msg, myMap);
        } else if (myMap != null && myMap.get("type").equals("Chat")) {
            try {
                String userToUUID = User.decryptUUID((String)myMap.get("userTo"));
                String currentUser = User.decryptUUID((String)myMap.get("userFrom"));
                Chats chat = txManager.doInTransaction(() -> chatsDao.getChatBetweenUsers(currentUser, userToUUID));
                sendMessageAboutChat(session, chat);
            } catch (AesException e) {
                createSendMessageAboutException(session, "Sorry, try later");
                e.printStackTrace();
            } catch (SQLException e) {
                createSendMessageAboutException(session, "Sorry, try later");
                e.printStackTrace();
            }
        } else if (myMap != null && myMap.get("type").equals("bigChat")) {
            try {
                int chatID = (int)myMap.get("chatID");
                String currentUser = User.decryptUUID((String)myMap.get("userFrom"));
                Chats chat = txManager.doInTransaction(() -> chatsDao.getChatByID(chatID, currentUser));
                if (chat.getUserList().contains(UserConstant.getUserConst().getAllUser().get(currentUser))) {
                    sendMessageAboutChat(session, chat);
                } else {
                    createSendMessageAboutException(session, "You can't write to this chat");
                }
            } catch (SQLException e) {
                createSendMessageAboutException(session, "Sorry, try later");
                e.printStackTrace();
            } catch (AesException e) {
                e.printStackTrace();
            }
        } else if (myMap != null && myMap.get("type").equals("MoreMessages")) {
            try {
                int chatID = (int)myMap.get("chatID");
                int numberMessageAlreadyInChat = (int)myMap.get("numberMessagesAlreadyInChat");
                Date minDateInChat = new Date(Long.valueOf((String)myMap.get("minDateInChat")));
                int howMuchNeed =(int)myMap.get("howMuchWeNeed");
                String currentUser = User.decryptUUID((String)myMap.get("userFrom"));
                Chats chat = txManager.doInTransaction(() -> chatsDao.getMoreMessagesInChat(chatID, numberMessageAlreadyInChat, minDateInChat, howMuchNeed, currentUser));
                sendMessageAboutChat(session, chat);
            } catch (AesException e) {
                e.printStackTrace();
            } catch (SQLException e) {
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
            createSendMessageAboutException(session, "Sorry, try later");
            e.printStackTrace();
        }

    }

    private void createSendMessageAboutException(Session session, String exeption) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "Exception");
        node.put("value", exeption);
        String jsonStr = node.toString();
        try {
            session.getBasicRemote().sendText(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(Session session, String msg, Map<String, Object> myMap) {
        try {
            List<User> allUsersFromChat = txManager.doInTransaction(() -> chatsDao.getUsersFromChat(Integer.valueOf((String)myMap.get("chatID"))));

            if (checkUser(allUsersFromChat, User.decryptUUID((String)myMap.get("userFrom")))) {
                Messages newMessage = new Messages(
                        UserConstant.getUserConst().getAllUser().get(User.decryptUUID((String)myMap.get("userFrom"))),
                        Integer.valueOf((String)myMap.get("chatID")),
                        (String)myMap.get("message"),
                        Boolean.valueOf((String)myMap.get("newMessage")),
                        new Date(Long.valueOf((String)myMap.get("dateMessage"))),
                        (String)myMap.get("UUIDFromBrowser"));
                newMessage.addAllUserTo(allUsersFromChat, true, false);
                newMessage.setIdMessage(txManager.doInTransaction(() -> chatsDao.saveMessage(newMessage)));
                ObjectMapper mapper = new ObjectMapper();

                //session.getBasicRemote().sendText(jsonStr);

                for (User userFromChat : allUsersFromChat) {
                    if (usersWebSocketSession.containsKey(userFromChat.getUuid())) {
                        if (userFromChat.getUuid().equals(newMessage.getUserFrom().getUuid())) {
                            newMessage.setNewMessage(newMessage.isNewMessageForSomeOne());
                        } else {
                            newMessage.setNewMessage(newMessage.isNewMessageForUserUUID(userFromChat.getUuid()));
                        }
                        String jsonStr = mapper.writeValueAsString(newMessage);
                        usersWebSocketSession.get(userFromChat.getUuid()).getBasicRemote().sendText(jsonStr);
                    }
                }
            } else {
                createSendMessageAboutException(session, "You can't write to this chat");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            createSendMessageAboutException(session, "Sorry, try later");
        } catch (AesException e) {
            e.printStackTrace();
            createSendMessageAboutException(session, "Sorry, try later"); //need something about message
        } catch (IOException e) {
            e.printStackTrace();
            createSendMessageAboutException(session, "Sorry, try latert"); //need something about message
        }

    }

    private boolean checkUser(List<User> allUsersFromChat, String userFrom) {
        boolean checking = false;
        for (User user : allUsersFromChat) {
            if (user.getUuid().equals(userFrom)) {
                checking = true;
                break;
            }

        }
        return checking;
    }

    public void sendMessageAboutCountNewMessages(Session session) {
        try {
            Map<Chats, Integer> countMessageMap = txManager.doInTransaction(() -> chatsDao.getCountNewMassages((String) config.getUserProperties().get(COOKIE_FOR_WEBSOCKET)));
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode nodeBig = mapper.createObjectNode();
            nodeBig.put("type", "newMessages");
            ArrayNode chatArray = nodeBig.putArray("chats");
            for (Map.Entry<Chats, Integer> pair : countMessageMap.entrySet()) {
                ObjectNode chatNode = mapper.createObjectNode();
                chatNode.put("chatID", pair.getKey().getIdChat());
                chatNode.put("newMessagesCount", pair.getValue());
                ArrayNode userArray = chatNode.putArray("users");
                for (User user : pair.getKey().getUserList()) {
                    ObjectNode nodeUsers = mapper.createObjectNode();
                    nodeUsers.put("useriD", user.getId());
                    if (user.getUuid().equals((String) config.getUserProperties().get(COOKIE_FOR_WEBSOCKET))) {
                        nodeUsers.put("currentUser", true);
                    } else {
                        nodeUsers.put("currentUser", false);
                    }
                    userArray.add(nodeUsers);
                }
                chatArray.add(chatNode);
            }

            String jsonStr = nodeBig.toString();
            // String jsonStr = mapper.writeValueAsString(countMessageMap);
            System.out.println(jsonStr);
            session.getBasicRemote().sendText(jsonStr);
        } catch (SQLException e) {
            e.printStackTrace();
      /*  } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();*/
        } catch (IOException e) {
            e.printStackTrace();
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
