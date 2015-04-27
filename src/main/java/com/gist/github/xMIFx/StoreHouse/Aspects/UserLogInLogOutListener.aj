package com.gist.github.xMIFx.StoreHouse.Aspects;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;

import com.gist.github.xMIFx.StoreHouse.WebSockets.MessengerSocket;
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
                node.put("type","User");
                node.put("id", us.getId());
                node.put("online", us.isOnline());
                String jsonStr = node.toString();
                System.out.println(jsonStr);
               for (Map.Entry<String, Session> pair : MessengerSocket.getUsersWebSocketSession().entrySet())
               {
                   try {
                       pair.getValue().getBasicRemote().sendText(jsonStr);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               /* try {
                    System.out.println(mapper.writeValueAsString(us));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
}
