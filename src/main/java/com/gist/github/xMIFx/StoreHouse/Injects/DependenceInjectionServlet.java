package com.gist.github.xMIFx.StoreHouse.Injects;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Vlad on 17.02.2015.
 */
public class DependenceInjectionServlet extends HttpServlet {
    private static final String  APP_CTX_PATH ="/appContext-dao-aop-schema-jdbc.xml";

    @Override
    public final void init() throws ServletException {
        try{
            ApplicationContext appCtx = ApplicationContextHolder.getClassPathXmlApplicationContext(APP_CTX_PATH);//new ClassPathXmlApplicationContext(APP_CTX_PATH);
            List<Field> allFields = FieldReflector.collectUpTo(this.getClass(), DependenceInjectionServlet.class);
            List<Field> injectField = FieldReflector.filterInject(allFields);
            allFields = null;
            /*
                1. Iterate on all fields with annotation @Inject
                2. Get this annotation from field(there can be a lot of annotations)
                3. Read value from my annotation
                4. Get bean
                5. Set bean in my field
             */
            for (Field field: injectField){
                field.setAccessible(true);
                Inject annotation = field.getAnnotation(Inject.class);
                String beanName = annotation.value();
                Object bean = appCtx.getBean(beanName);
                if (bean==null) throw  new ServletException("There isn't bean with name " + beanName);
                field.set(this,bean);
            }
        } catch (IllegalAccessException e) {
            throw new ServletException("Can't inject from "+APP_CTX_PATH, e);
        }

    }
}
