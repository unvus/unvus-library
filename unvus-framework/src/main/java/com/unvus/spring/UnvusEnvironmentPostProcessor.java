package com.unvus.spring;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class UnvusEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(UnvusEnvironmentPostProcessor.class);

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        Collection<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        Resource path = new ClassPathResource("attach.yml");
        // ^^^ here you can create the resource however you want
        // construct the name from a user name, use FileSystemResource, anything
        // for example you can ask users to place a file in their home
        // directory named "my-application.properties" and load it like so

        // Resource path = new FileSystemResource(Paths.get(System.getProperty("user.home"),"my-application.properties").toString());

        PropertySource<?> propertySource = loadProps(path);
        if(propertySource != null) {
            environment.getPropertySources().addFirst(propertySource);
        }
        for(String activeProfile : activeProfiles) {
            path = new ClassPathResource("attach." + activeProfile +".yml");
            propertySource = loadProps(path);
            if(propertySource != null) {
                MutablePropertySources ps = environment.getPropertySources();
                if(ps.contains(propertySource.getName())) {
                    Map source = (Map)ps.get(propertySource.getName()).getSource();
                    Map currSource = (Map)propertySource.getSource();
                    Map propMap = new HashMap();
                    propMap.putAll(source);
                    propMap.putAll(currSource);
                    PropertySource<?> newSource = new OriginTrackedMapPropertySource(propertySource.getName(), propMap, true);
                    ps.replace(propertySource.getName(), newSource);
                }else {
                    ps.addLast(propertySource);
                }
            }
        }
    }

    private PropertySource<?> loadProps(Resource path) {
        if (!path.exists()) {
            log.info("Resource " + path + " does not exist");
            return null;
        }
        try {
            List<PropertySource<?>> sourceList = this.loader.load("attach", path);
            if(sourceList != null && sourceList.size() > 0) {
                return sourceList.get(0);
            }
            return null;
        }
        catch (IOException ex) {
            throw new IllegalStateException(
                "Failed to load props configuration from " + path, ex);
        }
    }

}
