package com.unvus.web.util;

import com.unvus.spring.UnvusCacheKeyPrefix;
import com.unvus.spring.UnvusRedisKeyGenerator;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix="spring.cache", name = "type", havingValue="redis")
@Component
public class CacheUtil {


    private static String prefix;

    private static UnvusCacheKeyPrefix cacheKeyPrefix;

    private static UnvusRedisKeyGenerator redisKeyGenerator;

    private static RedisTemplate<String, String> template;

    @Inject
    public void setTemplate(RedisTemplate<String, String> template) {
        this.template = template;
    }


//    @Value("${spring.profiles.active}")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
        cacheKeyPrefix = new UnvusCacheKeyPrefix(prefix);
        redisKeyGenerator = new UnvusRedisKeyGenerator();
    }

//    public static void evictStartsWith(String cacheNames, Object... keyArr) {
//        try {
//            byte[] key = makeKey(cacheNames, redisKeyGenerator.generate(null, null, keyArr));
//            String str = StringUtils.substringBeforeLast(new String(key), "]");
//            str = str.replaceAll("\\[", "?");
////            StringUtils.replaceAll(str, "\\[", "\\\\[");
//            key = str.getBytes("UTF-8");
//            byte[] append = new byte[1];
//            append[0] = '*';
//
//            byte[] destination = ArrayUtils.addAll(key, append);
//
//            Set<String> keys = template.keys(new String(destination, "UTF-8"));
//            for (String delKey : keys) {
//                template.delete(delKey);
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }


//    private static byte[] makeKey(String cacheNames, Object key) {
//        RedisCacheKey redisCacheKey = new RedisCacheKey(key).usePrefix(cacheKeyPrefix.prefix(cacheNames))
//            .withKeySerializer(new WonlabKeySerializer());
//        return redisCacheKey.getKeyBytes();
//    }

}
