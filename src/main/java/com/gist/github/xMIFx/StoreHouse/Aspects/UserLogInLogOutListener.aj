package com.gist.github.xMIFx.StoreHouse.Aspects;

/**
 * Created by Vlad on 25.04.2015.
 */

public aspect UserLogInLogOutListener {
    pointcut setterUser(): set(!static boolean com.gist.github.xMIFx.StoreHouse.Entity.Directories.User.online);
    after(): setterUser()
            {
                System.out.println("YYY");
            }
}
