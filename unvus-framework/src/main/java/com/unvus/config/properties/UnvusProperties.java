package com.unvus.config.properties;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Properties specific to UnvUS.
 *
 * <p> Properties are configured in the application.yml file. </p>
 * <p> This class also load properties in the Spring Environment from the git.properties and META-INF/build-info.properties
 * files if they are found in the classpath.</p>
 */
@Getter
@ConfigurationProperties(prefix = "unvus")
@PropertySources({
		@PropertySource(value = "classpath:unvus-git.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:META-INF/unvus-build-info.properties", ignoreResourceNotFound = true)
})
public class UnvusProperties {


	public UnvusProperties() {

	}


    private final UnvusProperties.Http http = new UnvusProperties.Http();

    private final UnvusProperties.Format format = new UnvusProperties.Format();

	private final UnvusProperties.App app = new UnvusProperties.App();

    private final UnvusProperties.Cache cache = new UnvusProperties.Cache();

    private final UnvusProperties.Security security = new UnvusProperties.Security();

    private final CorsConfiguration cors = new CorsConfiguration();

    private final UnvusProperties.Xss xss = new UnvusProperties.Xss();

    private final MailProperties mail = new MailProperties();

    private final Map<String, Site> sites = new HashMap<>();

    @Setter
    @Getter
    public static class Http {

        private boolean ssl = false;
        private List<String> allowedOrigins = new ArrayList();

        private final Cache cache = new Cache();

        @Getter
        @Setter
        public static class Cache {

            private int timeToLiveInDays = UnvusPropertiesDefaults.Http.Cache.timeToLiveInDays;

        }
    }

    @Setter
    @Getter
    public static class Site {

        private String scheme;
        private String domain;

        public String getFull() {
            return scheme + "://" + domain;
        }
    }

    @Getter
    @Setter
    public static class Format {
        private String datetimeFormat;
        private String dateFormat;
    }

	@Getter
	@Setter
	public static class App {
		private String name;
        private String env;
        private String profile;
	}

    @Getter
    @Setter
    public static class Cache {

        private final Ehcache ehcache = new Ehcache();


        public Ehcache getEhcache() {
            return ehcache;
        }

        public static class Ehcache {

            private int timeToLiveSeconds = 60*60;

            private long maxEntries = 1000;

            public int getTimeToLiveSeconds() {
                return timeToLiveSeconds;
            }

            public void setTimeToLiveSeconds(int timeToLiveSeconds) {
                this.timeToLiveSeconds = timeToLiveSeconds;
            }

            public long getMaxEntries() {
                return maxEntries;
            }

            public void setMaxEntries(long maxEntries) {
                this.maxEntries = maxEntries;
            }
        }

    }



    @Getter
    public static class Security {

        private final ClientAuthorization clientAuthorization = new ClientAuthorization();
        private final Authentication authentication = new Authentication();
        private final RememberMe rememberMe = new RememberMe();


        @Getter
        @Setter
        public static class RememberMe {
            @NotNull
            private String key = UnvusPropertiesDefaults.Security.RememberMe.key;

            private String rememberMeParameter = "rememberMe";
        }

        @Getter
        @Setter
        public static class Authentication {
            private final Authentication.Oauth oauth = new Authentication.Oauth();
            private final Authentication.Jwt jwt = new Authentication.Jwt();
            private final Authentication.Sso sso = new Authentication.Sso();

            private String base64Secret = "";
            private int validityInSeconds = 1800;
            private int validityInSecondsForRememberMe = 2592000;
            private String usernameParameter = "username";
            private String passwordParameter = "password";
            private String loginProcessingUrl = "/authentication";
            private String defaultFailureUrl = "/signin?error";
            private String targetUrlParameter = "_targetUrl";
            private String dummyUrlForCookie = "/dummyForCookie";

            @Getter
            @Setter
            public static class Jwt {

                private String secret = UnvusPropertiesDefaults.Security.Authentication.Jwt.secret;

                private String base64Secret = UnvusPropertiesDefaults.Security.Authentication.Jwt.base64Secret;

                private int tokenValidityInSeconds = UnvusPropertiesDefaults.Security.Authentication.Jwt
                    .tokenValidityInSeconds;

                private int tokenValidityInSecondsForRememberMe = UnvusPropertiesDefaults.Security.Authentication.Jwt
                    .tokenValidityInSecondsForRememberMe;
            }

            @Getter
            @Setter
            public static class Oauth {
                private String clientId;
                private String clientSecret;
                private int tokenValidityInSeconds = 1800;

            }

            @Getter
            @Setter
            public static class Sso {
                private boolean enable = false;
                private String signinUrl;
                private String signoutUrl;
                private String checkUrl;

            }
        }

        @Getter
        @Setter
        public static class ClientAuthorization {
            private String accessTokenUri;
            private String tokenServiceId;
            private String clientId;
            private String clientSecret;

        }
    }



    @Getter
    @Setter
    public static class Xss {
        private String policyResource;
    }


}
