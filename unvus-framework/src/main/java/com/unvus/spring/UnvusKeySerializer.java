package com.unvus.spring;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

public class UnvusKeySerializer implements RedisSerializer<Object> {
    private final Charset charset;

    public UnvusKeySerializer() {
        this(Charset.forName("UTF8"));
    }

    public UnvusKeySerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
//        DigestUtils
//            .md5Hex("aa");
//        return toStringRecursive(o, new StringBuffer()).getBytes(charset);
        return SimpleObjectToStringUtil.convert(o).getBytes(charset);
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }
}
