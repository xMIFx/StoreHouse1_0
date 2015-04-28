package com.gist.github.xMIFx.StoreHouse.Aspects;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;

import com.gist.github.xMIFx.StoreHouse.WebSockets.MessengerSocket;



/**
 * Created by Vlad on 25.04.2015.
 */

public aspect UserLogInLogOutListener {
    pointcut setterUser(User us): target(us) && set(!static boolean com.gist.github.xMIFx.StoreHouse.Entity.Directories.User.online);
    after(User us): setterUser(us)
            {
                //System.out.println(us.getName());
                MessengerSocket.informAboutOnlineStatusOfUser(us);
            }
}
