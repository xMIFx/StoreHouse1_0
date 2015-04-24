package com.gist.github.xMIFx.StoreHouse.aspects;

import com.gist.github.xMIFx.StoreHouse.Entity.Directories.User;
import org.aspectj.lang.ProceedingJoinPoint;


/**
 * Created by bukatinvv on 24.04.2015.
 */

public class LogInLogOutListeners {
    public Object ListenUsersOnline(ProceedingJoinPoint call) throws Throwable {
        System.out.println("YYY");
        System.out.println(call.getTarget().getClass().getName());
        try {
            User user = (User) call.getTarget();
            System.out.println(user.getUuid());
        } catch (Exception e) {
           /*NOP*/
        }

        return call.proceed();

    }
}
