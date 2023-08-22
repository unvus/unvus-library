package com.unvus.config.properties;

public interface UnvusPropertiesDefaults {

    interface Http {

        interface Cache {

            int timeToLiveInDays = 1461; // 4 years (including leap day)
        }
    }

    interface Cache {

        interface Hazelcast {

            int timeToLiveSeconds = 3600; // 1 hour
            int backupCount = 1;

            interface ManagementCenter {

                boolean enabled = false;
                int updateInterval = 3;
                String url = "";
            }
        }

        interface Caffeine {

            int timeToLiveSeconds = 3600; // 1 hour
            long maxEntries = 100;
        }

        interface Ehcache {

            int timeToLiveSeconds = 3600; // 1 hour
            long maxEntries = 100;
        }

        interface Infinispan {

            String configFile = "default-configs/default-jgroups-tcp.xml";
            boolean statsEnabled = false;

            interface Local {

                long timeToLiveSeconds = 60; // 1 minute
                long maxEntries = 100;
            }

            interface Distributed {

                long timeToLiveSeconds = 60; // 1 minute
                long maxEntries = 100;
                int instanceCount = 1;
            }

            interface Replicated {

                long timeToLiveSeconds = 60; // 1 minute
                long maxEntries = 100;
            }
        }

        interface Memcached {

            boolean enabled = false;
            String servers = "localhost:11211";
            int expiration = 300; // 5 minutes
            boolean useBinaryProtocol = true;

            interface Authentication {
                boolean enabled = false;
            }
        }

        interface Redis {
            String[] server = {"redis://localhost:6379"};
            int expiration = 300; // 5 minutes
            boolean cluster = false;
            int connectionPoolSize = 64; // default as in redisson
            int connectionMinimumIdleSize = 24; // default as in redisson
            int subscriptionConnectionPoolSize = 50; // default as in redisson
            int subscriptionConnectionMinimumIdleSize = 1; // default as in redisson
        }
    }

    interface Security {

        interface ClientAuthorization {

            String accessTokenUri = null;
            String tokenServiceId = null;
            String clientId = null;
            String clientSecret = null;
        }

        interface Authentication {

            interface Jwt {

                String secret = null;
                String base64Secret = null;
                int tokenValidityInSeconds = 1800; // 30 minutes
                int tokenValidityInSecondsForRememberMe = 2592000; // 30 days
            }
        }

        interface RememberMe {

            String key = null;
        }
    }
}
