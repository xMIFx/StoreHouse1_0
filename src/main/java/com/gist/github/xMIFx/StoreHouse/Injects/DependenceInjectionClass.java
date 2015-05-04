package com.gist.github.xMIFx.StoreHouse.Injects;

import org.springframework.context.ApplicationContext;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Vlad on 04.05.2015.
 */
public class DependenceInjectionClass {
    private static final String APP_CTX_PATH = "/appContext-dao-aop-schema-jdbc.xml";

    public final void initialize() throws IllegalAccessException {
        try {
            ApplicationContext appCtx = ApplicationContextHolder.getClassPathXmlApplicationContext(APP_CTX_PATH);//new ClassPathXmlApplicationContext(APP_CTX_PATH);
            List<Field> allFields = FieldReflector.collectUpTo(this.getClass(), DependenceInjectionClass.class);
            List<Field> injectField = FieldReflector.filterInject(allFields);
            allFields = null;
            /*
                1. Iterate on all fields with annotation @Inject
                2. Get this annotation from field(there can be a lot of annotations)
                3. Read value from my annotation
                4. Get bean
                5. Set bean in my field
             */
            for (Field field : injectField) {
                field.setAccessible(true);
                Inject annotation = field.getAnnotation(Inject.class);
                String beanName = annotation.value();
                Object bean = appCtx.getBean(beanName);
                if (bean == null) throw new IllegalAccessException("There isn't bean with name " + beanName);
                field.set(this, bean);
            }
        } catch (IllegalAccessException e) {
            throw e;
        }

    }


}
