package com.gist.github.xMIFx.StoreHouse.WebSockets;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

/**
 * Created by bukatinvv on 24.04.2015.
 */
public class EndpointConfiguratorChat extends ServerEndpointConfig.Configurator {
    private static final String COOKIE_NAME = "user";
    private static final String COOKIE_FOR_WEBSOCKET = "curentUser";

    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest request,
                                HandshakeResponse response) {
        List<String> cookiesList = request.getHeaders().get("cookie");
        String currentUserUUID = null;
        for (String cookies : cookiesList) {
            String[] cookiesArray = cookies.split(";");
            for (String cook : cookiesArray) {
                cook=cook.trim();
                if (cook.startsWith(COOKIE_NAME)) {
                    currentUserUUID = cook.substring(cook.lastIndexOf("=")+1, cook.length());
                }
            }
        }
        if (currentUserUUID == null) currentUserUUID = User.getEmptyUUID();
        config.getUserProperties().put(COOKIE_FOR_WEBSOCKET, currentUserUUID);
    }
}
