package com.gist.github.xMIFx.StoreHouse.Aspects;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;

import com.gist.github.xMIFx.StoreHouse.WebSockets.MessengerSocket;
import com.gist.github.xMIFx.StoreHouse.dao.Interfaces.UserDao;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.node.ObjectNode;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;


/**
 * Created by Vlad on 25.04.2015.
 */

public aspect UserLogInLogOutListener {
    pointcut setterUser(User us): target(us) && set(!static boolean com.gist.github.xMIFx.StoreHouse.Entity.Directories.User.online);
    after(User us): setterUser(us)
            {
                //System.out.println(us.getName());

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("type", "User");
                node.put("id", us.getId());
                node.put("online", us.isOnline());
                String jsonStr = node.toString();
                System.out.println(jsonStr);
                     for (Map.Entry<String, Session> pair : MessengerSocket.getUsersWebSocketSession().entrySet()) {
                    System.out.println("beginning sending");
                    // if consume changed online status, we need inform only for user with consumeVisible
                    if (us.getRole().getiD() == UserDao.getAllUser().get(User.getEmptyUUID()).getRole().getiD()
                            && !UserDao.getAllUser().get(pair.getKey()).isConsumeVisible()) {
                        System.out.println("message not sending");
                        continue;
                    }
                    // if user changed online status with out consumeVisible, we don't need inform consumers
                    if (!us.isConsumeVisible()
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

               /* try {
                    System.out.println(mapper.writeValueAsString(us));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
}
