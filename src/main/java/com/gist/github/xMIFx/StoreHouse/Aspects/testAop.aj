package com.gist.github.xMIFx.StoreHouse.Aspects;

/**
 * Created by Vlad on 25.04.2015.
 */

public aspect testAop {
   /* pointcut setterUser(): set(!static boolean com.gist.github.xMIFx.StoreHouse.Entity.Directories.User.online)&&args(newWal);*/

 /*   after():execution(public void *(..)){
        System.out.println("XXX");
    }*/
    after(): set(!static boolean com.gist.github.xMIFx.StoreHouse.Entity.Directories.User.online)
            {
                System.out.println("YYY");
            }
    /*after(): setterUser()
            {
                System.out.println("YYY");
            }*/
}
