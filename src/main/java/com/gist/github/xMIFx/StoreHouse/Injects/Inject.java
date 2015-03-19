package com.gist.github.xMIFx.StoreHouse.Injects;

import jdk.nashorn.internal.ir.annotations.Reference;

import java.lang.annotation.*;

/**
 * Created by Vlad on 17.02.2015.
 */
@Documented //write in javaDoc
@Target(ElementType.FIELD) //We can use annotation on field
@Retention(RetentionPolicy.RUNTIME)// annotation was in JVM, we can use reflection
public @interface Inject {
    String value();
}
