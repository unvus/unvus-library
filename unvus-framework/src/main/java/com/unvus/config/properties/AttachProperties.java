package com.unvus.config.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "attach")
public class AttachProperties {

//    private static String activeProfile;
//
//    @Value("${spring.profiles.active}")
//    public void setActiveProfile(String activeProfile) {
//        this.activeProfile = activeProfile;
//    }

    private Directory directory = new Directory();

    private List<Room> rooms = new ArrayList();

    private Map<String, Filter> filterMap = new HashMap<>();

    public AttachProperties() {
    }

    public static class Directory {
        private String tmpDir;
        private String uploadBaseDir;

        public String getTmpDir() {
            return tmpDir;
        }

        public void setTmpDir(String tmpDir) {
            this.tmpDir = tmpDir;
        }

//        public String getUploadBaseDir() {
//            if(uploadBaseDir != null) {
//                String subDir = "dev";
//
//                if(StringUtils.contains(AttachProperties.activeProfile, Constants.SPRING_PROFILE_STAGE) ||
//                    StringUtils.contains(AttachProperties.activeProfile, Constants.SPRING_PROFILE_PRODUCTION)) {
//                    subDir = Constants.SPRING_PROFILE_PRODUCTION;
//                }
//                return uploadBaseDir + "/" + subDir;
//            }
//            return uploadBaseDir;
//        }

        public String getUploadBaseDir() {
//            if(uploadBaseDir != null) {
//                String subDir = "dev";
//
//                if(StringUtils.contains(AttachProperties.activeProfile, Constants.SPRING_PROFILE_STAGE) ||
//                    StringUtils.contains(AttachProperties.activeProfile, Constants.SPRING_PROFILE_PRODUCTION)) {
//                    subDir = Constants.SPRING_PROFILE_PRODUCTION;
//                }
//                return uploadBaseDir + "/" + subDir;
//            }
            return uploadBaseDir;
        }

        public void setUploadBaseDir(String uploadBaseDir) {
            this.uploadBaseDir = uploadBaseDir;
        }
    }

    public static class Room {
        private String name;


        private List<String> cabs;

        public String getName() {

            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getCabs() {
            return cabs;
        }

        public void setCabs(List<String> cabs) {
            this.cabs = cabs;
        }
    }


    public static class Filter {
        private String name;
        private String className;
        private Map<String, Object> config;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Map<String, Object> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Map<String, Filter> getFilterMap() {
        return filterMap;
    }
}
