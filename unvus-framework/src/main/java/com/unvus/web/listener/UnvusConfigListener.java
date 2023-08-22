package com.unvus.web.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class UnvusConfigListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {
        ServletContext context = contextEvent.getServletContext();

        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        Environment env = ctx.getEnvironment();
        Map<String, Object> map = new HashMap();
        for(Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext(); ) {
            PropertySource propertySource = (PropertySource) it.next();
            if (propertySource instanceof MapPropertySource) {
                if("systemProperties".equals(propertySource.getName()) || "systemEnvironment".equals(propertySource.getName())) {
                    continue;
                }
                try{
                    Map<String, Object> prop = ((MapPropertySource) propertySource).getSource();
                    for(Map.Entry<String, Object> entry: prop.entrySet()) {
                        if(entry.getValue() instanceof OriginTrackedValue) {
                            map.put(entry.getKey(), ((OriginTrackedValue)entry.getValue()).getValue());
                        }else {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        context.setAttribute("config", map);

        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(context);
        String[] profiles = applicationContext.getEnvironment().getActiveProfiles();
        List profileList = Arrays.asList(profiles);

        context.setAttribute("profiles", profileList);
    }


    public void contextDestroyed(ServletContextEvent contextEvent) {

    }

}
