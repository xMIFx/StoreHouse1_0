package com.gist.github.xMIFx.StoreHouse.WebSockets;

/**
 * Created by bukatinvv on 02.04.2015.
 */

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/messenger.do/chat", configurator = EndpointConfiguratorChat.class)
public class MessengerSocket {
    private static final String COOKIE_FOR_WEBSOCKET = "curentUser";

    public static Map<String, Session> getUsersWebSocketSession() {
        return usersWebSocketSession;
    }

    private static Map<String, Session> usersWebSocketSession = Collections.synchronizedMap(new HashMap<String, Session>());
    private EndpointConfig config;
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.config=config;
        usersWebSocketSession.put((String) config.getUserProperties().get(COOKIE_FOR_WEBSOCKET), session);
    }

    @OnClose
    public void onClose(Session session) {
        usersWebSocketSession.remove(this.config.getUserProperties().get(COOKIE_FOR_WEBSOCKET));
    }

    @OnMessage
    public void echoTextMessage(Session session, String msg) {
        //to json first time, latter it will be already json
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("type","Messages");
        node.put("message", msg);
        String jsonStr = node.toString();

        for (Map.Entry<String, Session> pair : usersWebSocketSession.entrySet()) {

            try {
                if (pair.getValue().isOpen() && pair.getValue()!=session) {
                    pair.getValue().getBasicRemote().sendText(jsonStr, true);
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
}
