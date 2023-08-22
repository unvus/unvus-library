package com.unvus.config.properties;

import java.util.Properties;


public class MailProperties {
    private String host;
 
    private int port;
 
    private String username;
 
    private String password;
    
    private String[] testReceivers;

    private String startTls;
    private String enableSSL;

    private String auth;

    private String protocol = "smtp";


    public String getHost() {
        return host;
    }


    public void setHost(String host) {
        this.host = host;
    }


    public int getPort() {
        return port;
    }


    public void setPort(int port) {
        this.port = port;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String[] getTestReceivers() {
        return testReceivers;
    }


    public void setTestReceivers(String[] testReceivers) {
        this.testReceivers = testReceivers;
    }

    public String getStartTls() {
        return startTls;
    }

    public void setStartTls(String startTls) {
        this.startTls = startTls;
    }

    public String getEnableSSL() {
        return enableSSL;
    }

    public void setEnableSSL(String enableSSL) {
        this.enableSSL = enableSSL;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", protocol);

        if(startTls != null) {
            properties.setProperty("mail.smtp.starttls.enable", startTls);
        }
        if(enableSSL != null) {
            properties.setProperty("mail.smtp.EnableSSL.enable", enableSSL);
        }
        if(auth != null) {
            properties.setProperty("mail.smtp.auth", auth);
        }

        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.port", String.valueOf(port));


        if("smtps".equals(protocol)) {
            properties.setProperty("mail.smtp.ssl.trust", host);
            properties.setProperty("mail.smtp.socketFactory.port", String.valueOf(port));
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        return properties;
    }
    
    
        
}
